package csbst.generators;


import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import csbst.analysis.String2Expression;
import csbst.generators.atomic.AbstractPrimitive;
import csbst.generators.atomic.ClassGenerator;
import csbst.generators.atomic.ObjectGenerator;
import csbst.generators.atomic.StringGenerator;
import csbst.generators.containers.ArrayGenerator;
import csbst.generators.containers.CollectionGenerator;
import csbst.generators.containers.MapGenerator;
import csbst.generators.dynamic.InstanceGenerator;
import csbst.testing.JTE;
import csbst.testing.fitness.TestingFitness;
import csbst.utils.FileEditor;
import csbst.utils.ClassLoaderUtil;


import ec.EvolutionState;
import ec.vector.VectorGene;



public abstract class AbsractGenerator extends VectorGene{
	protected IVariableBinding variableBinding=null;
	protected Class  clazz;
	protected Class  stub;
	protected TestingFitness fitness;
	protected Object object;
	protected long seed;
	protected Random random;
	protected AbsractGenerator parent;
	protected boolean isNullAccepted=true;
	
	public static final boolean SystematicallySurroundCall=true;
	public static final Class DefaultGenericType=Integer.class;
	public static final Class defaultClass=Integer.class;
	
	public static final int SEEDING_MAX_PROBABILITY=20;
	//Random modification
	public static final int SEEDING_MIN_PROBABILITY=5;
	//Random modification
	public static final int SEEDING_NULL_PROBABILITY=5;
	//Random modification
	public static final int SEEDING_MAX_NUMBER=10;
	public static final int SEEDING_MIN_NUMBER=0;
	
	private String variableName;
	
	private static  Block CATCH_BLOCK;
	static{
//		if(JTE.ExceptionsOriented){
//			String expression="ExceptionsFormatter.printException(exce,\""+JTE.projectPackagesPrefix+"\",\""+JTE.className+"\");";
//			if(JTE.ExceptionsAnalysis){
//				expression+="\n throw exce;";//" \n ExceptionsFormatter.ExceptionAnalyzer(exce);";
//			}
//			CATCH_BLOCK=String2Expression.getInfixExpression(expression);
//		}else
		{
			String expression=""; 
			//if(JTE.ExceptionsAnalysis){
				expression+="throw exce;";//" ExceptionsFormatter.ExceptionAnalyzer(exce);";
			//}
			CATCH_BLOCK=String2Expression.getInfixExpression(expression);
		}
	}
	
	
	public abstract void generateRandom();
	public abstract void mutate();
	public abstract List<Statement>getStatements(AST ast, String varName, String pName);
	//public abstract Object getInstance();
	
	public AbsractGenerator(AbsractGenerator p, Class clazz){ 
		//fitness=new ApproachLevel(0);
		random=new Random();
		parent=p;
		this.clazz=clazz;
		this.stub=clazz;
	}
	
	public AbsractGenerator getParent(){
		return parent;
	}
	
//	public Gene() {
//		// TODO Auto-generated constructor stub
//	}
	public Object getObject(){
		return object;
	}

	public void setObject(Object obj){
		 object=obj;
	}
	
	public Class getClazz(){
		return clazz;
	}
	
	public Class getStub(){
		return stub;
	}
	
	public TestingFitness getFitness(){
		return fitness;
	}

	
	public IVariableBinding getVariableBinding() {
		return variableBinding;
	}
	
	public void setVariableBinding(IVariableBinding variableBinding) {
		this.variableBinding = variableBinding;
	}
	
	@Override
	public void reset(EvolutionState state, int thread) {
		generateRandom();	
	}
	
	@Override
	public boolean equals(Object other) {
		boolean areEqual=object.equals(((AbsractGenerator)other).getObject());
		areEqual=areEqual && clazz.equals(((AbsractGenerator)other).getClazz());
		areEqual=areEqual && fitness.equals(((AbsractGenerator)other).getFitness());
		return areEqual;
	}

	protected AbsractGenerator createAdequateGene(AbsractGenerator parent, Class cls){
		return createAdequateGene(parent,cls, true);
	}
	
	protected AbsractGenerator createAdequateGene(AbsractGenerator parent, Class cls, Type type){
		return createAdequateGene( parent,  cls,  type,true);
	}
	
	protected AbsractGenerator createAdequateGene(AbsractGenerator parent, Class cls, Type type, boolean withInstance){
		AbsractGenerator gene=null;
		if(Collection.class.isAssignableFrom(cls)||Map.class.isAssignableFrom(cls)){//((cls.equals(List.class)||((cls.equals(Collection.class))&&!cls.isArray()))){			
			ParameterizedType type1=null;
			if(type instanceof ParameterizedType)
				type1=(ParameterizedType) type;
			
			Class clazz=null;
			Class clazzValue=null;
			if(type1==null || type1.getActualTypeArguments().length==0){
				clazz=Object.class;
				clazzValue=Object.class;
			}
			else{			
				
				String nclass=type1.getActualTypeArguments()[0].toString();
				String nclassValue=type1.getActualTypeArguments()[0].toString();
				//System.err.println("Map nclassKey= "+nclass);
				if(type1.getActualTypeArguments().length>1)
					nclassValue=type1.getActualTypeArguments()[1].toString();
				
				//System.err.println("Map nclassValue= "+nclassValue);
				//if(nclass.l)
				String []nclassHierarchy=nclass.split(" ");
				if(nclassHierarchy==null || nclassHierarchy.length<1)
					clazz=Object.class;
				else{
					if(nclassHierarchy.length==1)
						nclass=ClassLoaderUtil.toCanonicalName(nclass);
					else
						nclass=nclassHierarchy[1];
					try {
						clazz=JTE.magicClassLoader.loadClass(nclass);
					} catch (ClassNotFoundException e) {
						clazz=Object.class;
					}
				}
				
				if(Map.class.isAssignableFrom(cls))
					if(nclassValue.equals(nclass))
						clazzValue=clazz;
					else{
						nclassHierarchy=nclassValue.split(" ");
						if(nclassHierarchy==null || nclassHierarchy.length<1)
							clazzValue=Object.class;
						else{
							if(nclassHierarchy.length==1)
								nclassValue=ClassLoaderUtil.toCanonicalName(nclassValue);
							else
								nclassValue=nclassHierarchy[1];
							try {
								//System.err.println("Map nclassValue= "+nclassValue);
								clazzValue=JTE.magicClassLoader.loadClass(nclassValue); //Class.forName(nclassValue);
								
							} catch (ClassNotFoundException e) {
								clazzValue=Object.class;
							}
						}					
					}
				
				
			}
			//gene=new ListGenerator(parent,clazz); ContainerGenerator(AbsractGenerator parent, int l, Class containerType, Class elementType) 
			//System.out.println("Collection  :"+ cls +" class :"+type);
			//System.out.println("Collection  :"+ cls +" class :"+clazz);
			if(Collection.class.isAssignableFrom(cls))
				gene=new CollectionGenerator(parent,0,cls,clazz);
			else{
				//System.err.println("Map generated cKeyType = "+clazz+ " cValueType"+clazzValue);
				gene=new MapGenerator(parent,0,cls,clazz,clazzValue);
			}
		}
		else
			gene=createAdequateGene(parent, cls, withInstance);
		
		return gene;
	}
	
	protected AbsractGenerator createAdequateGene(AbsractGenerator parent, Class cls, boolean withInstance){
		try {				
			AbsractGenerator gene=null;
			Class geneCls=AbstractPrimitive.getGeneClass(cls); 
			if(geneCls!=null){
						Constructor constructor;
						constructor = geneCls.getConstructor(AbsractGenerator.class,Class.class);
						gene = (AbsractGenerator) constructor.newInstance(parent,cls);
						//if(!defaultClassesSet.contains(cls))
						//	defaultClassesSet.add(cls);
			}
//			else if(cls.equals(String.class))
//					gene=new StringGenerator(parent,0);
			else if(cls.isArray()) 
					 gene=new ArrayGenerator(parent,0,cls.getComponentType());
			else if(cls.equals(String.class))
					gene=new StringGenerator(parent,0); 
			else if(cls.equals(Object.class))
					return new ObjectGenerator(parent);
			else  if(cls.equals(Class.class))
				return new ClassGenerator(parent);
		else
					gene = new InstanceGenerator(parent, cls,withInstance);

			return gene;
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			//e.getStackTrace()[0].getFileName();
			//e.getStackTrace()[0].
		}
		return null;
	}
	
	public abstract boolean isSameFamillyAs(AbsractGenerator gene);
//	{
//		//System.out.println(" originr class "+this.getClass());
//		//System.out.println(" externa class "+gene.getClass());
//		
//		return (this.getClass().equals(gene.getClass()));
//	}
	
	public void defaultCrossover(AbsractGenerator gene) {
		AbsractGenerator tmp=(AbsractGenerator) this.clone();
		AbsractGenerator tmp1=(AbsractGenerator) gene.clone();
		this.object=tmp1.object;
		this.clazz=tmp1.clazz;
		this.fitness=tmp1.fitness;
		this.variableBinding=tmp1.variableBinding;
		gene=tmp;
	}
	
	public static org.eclipse.jdt.core.dom.Type getType2UseInJunitClass(Class cls, AST ast){
		if(cls.isPrimitive())
			return ast.newPrimitiveType(getPrimitiveCode(cls));
		
		if(cls.isArray())
			return ast.newArrayType(getType2UseInJunitClass( cls.getComponentType(),  ast));
		
		if(cls.isAnonymousClass()){
			//System.out.println(cls);
			return getType2UseInJunitClass(cls.getEnclosingMethod().getReturnType(),  ast);
		}
		
		Name qNameClass=getName2UseInJunitClass(cls,ast);
    	return ast.newSimpleType(qNameClass);
		//return null;
		
	}

	public static String getPrimitiveInitialiser(Class t){
		if(t.equals(long.class)||t.getSimpleName().equals("long"))
			return "0L";
		if(t.equals(double.class)||t.getSimpleName().equals("double"))
			return "0d";
		if(t.equals(float.class)||t.getSimpleName().equals("float"))
			return "0f";
		
		return "0";
	}

    
	public static Code getPrimitiveCode(Class t){
		if(t.equals(int.class)||t.getSimpleName().equals("int"))
			return PrimitiveType.INT;
		if(t.equals(byte.class)||t.getSimpleName().equals("byte"))
			return PrimitiveType.BYTE;
		if(t.equals(short.class)||t.getSimpleName().equals("short"))
			return PrimitiveType.SHORT;
		if(t.equals(long.class)||t.getSimpleName().equals("long"))
			return PrimitiveType.LONG;
		if(t.equals(boolean.class)||t.getSimpleName().equals("boolean"))
			return PrimitiveType.BOOLEAN;
		if(t.equals(char.class)||t.getSimpleName().equals("char"))
			return PrimitiveType.CHAR;
		if(t.equals(double.class)||t.getSimpleName().equals("double"))
			return PrimitiveType.DOUBLE;
		if(t.equals(float.class)||t.getSimpleName().equals("float"))
			return PrimitiveType.FLOAT;
		
		return null;
	}
	
	public static QualifiedName generateQualifiedName(String aName, AST _ast){
		//System.out.println(aName);
		if(aName.lastIndexOf(".")<0) return _ast.newQualifiedName(null,_ast.newSimpleName(aName));  
		String[] names=aName.split("\\.");
		QualifiedName nQN;
		if(names.length==1) 
			nQN= _ast.newQualifiedName(null,_ast.newSimpleName(names[0]));
		else{
			if(names.length==2) 
				nQN= _ast.newQualifiedName(_ast.newSimpleName(names[0]),_ast.newSimpleName(names[1]));	
			else
				nQN= _ast.newQualifiedName(generateQualifiedName(aName.substring(0,aName.lastIndexOf(".")),_ast),_ast.newSimpleName(names[names.length-1]));
		}
		return nQN;	
	}
	
	public static Name getName2UseInJunitClass(Class cls, AST ast){
		Name qNameClass=null;

		if(cls.isLocalClass()||cls.isMemberClass()){
			if(cls.isLocalClass()){
				for(Class c:JTE.requiredClasses){
					if(!c.equals(cls.getEnclosingClass()) && c.getSimpleName().toString().equals(cls.getEnclosingClass().getSimpleName().toString())){
						qNameClass=generateQualifiedName(cls.getCanonicalName().toString(),ast);
						return qNameClass;
					}
				}
				Name qNameClass1=getName2UseInJunitClass(cls.getEnclosingClass(),ast);
				qNameClass=ast.newQualifiedName(qNameClass1,ast.newSimpleName(cls.getSimpleName()));
				//InstanceGenerator.requiredClasses.add(cls.getEnclosingClass());
			}
			else if(cls.isMemberClass()){
				for(Class c:JTE.requiredClasses){
					if(!c.equals(cls.getDeclaringClass()) && c.getSimpleName().toString().equals(cls.getDeclaringClass().getSimpleName().toString())){
						qNameClass=generateQualifiedName(cls.getCanonicalName().toString(),ast);
						return qNameClass;
					}
				}
				Name qNameClass1=getName2UseInJunitClass(cls.getDeclaringClass(),ast);
				qNameClass=ast.newQualifiedName(qNameClass1,ast.newSimpleName(cls.getSimpleName()));
				//InstanceGenerator.requiredClasses.add(cls.getDeclaringClass());
			}
		}else{
			//System.out.println(cls);
			for(Class c:JTE.requiredClasses){
				if(!c.equals(cls) && c.getSimpleName().toString().equals(cls.getSimpleName().toString())){
					qNameClass=generateQualifiedName(cls.getName().toString(),ast);
					return qNameClass;
				}
			}
			
			qNameClass=ast.newSimpleName(cls.getSimpleName());
			JTE.requiredClasses.add(cls);
		}
		
		return qNameClass;
	}
	
	public static CatchClause getCatchClause(Class exce, AST ast, boolean simple){
		Class except=exce;
		if(!simple)
			except=Throwable.class;	
		
		SingleVariableDeclaration excepVar=ast.newSingleVariableDeclaration();
		excepVar.setName(ast.newSimpleName("exce"));
		org.eclipse.jdt.core.dom.Type TypeName = getType2UseInJunitClass(except,ast);
		excepVar.setType(TypeName);
		
		CatchClause catchClause=ast.newCatchClause();
		catchClause.setException(excepVar);
		Block b=null;
		if(CATCH_BLOCK!=null && !simple){
			 b =(Block) ASTNode.copySubtree(ast,CATCH_BLOCK);
			catchClause.setBody(b);
		}
		
		return catchClause;
	}
//	public AbsractGenerator getParent() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	
}

