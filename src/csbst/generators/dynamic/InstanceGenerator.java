package csbst.generators.dynamic;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import csbst.analysis.String2Expression;
import csbst.generators.AbsractGenerator;
import csbst.generators.containers.ArrayGenerator;
import csbst.generators.containers.ListGenerator;
import csbst.testing.JTE;
//import csbst.utils.MagicClassLoader;



import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class InstanceGenerator extends AbstractDynamicGenerator<Constructor>{	
	//private Vector<Constructor> recommandedConstractors;
	public static Map<Class,Vector<ExecutionWay>>class2InstantiationWays=new HashMap<Class,Vector<ExecutionWay>>();
	public static Map<Class,Vector<Class>>interface2Implementation=new HashMap<Class,Vector<Class>>();
	
	public static Reflections reflections;
	public static Map<String,Reflections> package2Reflections=new HashMap<String,Reflections>();
	public static Vector<Class> defaultClassesSet=new Vector<Class>();
	private boolean once=false;
	
	static{ 
		for(int i=0; i<10;i++){  
			InstanceGenerator.defaultClassesSet.add(JTE.currentClassUnderTest.getClazz());
			InstanceGenerator.defaultClassesSet.add(String.class);
			InstanceGenerator.defaultClassesSet.add(Integer.class);
			InstanceGenerator.defaultClassesSet.add(Long.class);
			InstanceGenerator.defaultClassesSet.add(Short.class); 
			InstanceGenerator.defaultClassesSet.add(Double.class); 
			InstanceGenerator.defaultClassesSet.add(Float.class); 
			InstanceGenerator.defaultClassesSet.add(Date.class);
			InstanceGenerator.defaultClassesSet.add(Boolean.class);
			InstanceGenerator.defaultClassesSet.add(Character.class);
			InstanceGenerator.defaultClassesSet.add(Byte.class);
			InstanceGenerator.defaultClassesSet.add(Integer.TYPE);
			InstanceGenerator.defaultClassesSet.add(Long.TYPE);
			InstanceGenerator.defaultClassesSet.add(Short.TYPE); 
			InstanceGenerator.defaultClassesSet.add(Double.TYPE); 
			InstanceGenerator.defaultClassesSet.add(Float.TYPE); 
			InstanceGenerator.defaultClassesSet.add(Boolean.TYPE);
			InstanceGenerator.defaultClassesSet.add(Character.TYPE);
			InstanceGenerator.defaultClassesSet.add(Byte.TYPE);
		}
		
		URL[] urls = new URL[JTE.classPath.length];
		for(int i =0;i<JTE.classPath.length;i++){
			try {
				urls[i]=(new File(JTE.classPath[i])).toURL();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		reflections = new Reflections(new ConfigurationBuilder()
		.setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new MethodParameterScanner()) //new ResourcesScanner(), 
		.setUrls(urls)
		);
	}
	
	public InstanceGenerator(AbsractGenerator parent, Class clazz, Vector<Constructor> recommandedConstractors,boolean withInstance){
		super(parent,clazz,recommandedConstractors,withInstance);
	}
	
	public InstanceGenerator(AbsractGenerator parent, Class clazz, boolean withInstance){	
		this( parent,  clazz,new Vector<Constructor>(),  withInstance);
		if(clazz.toString().equals("[Ljava.lang.String"))
			try {
				throw new Exception ("Erreur [Ljava.lang.String");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	
	public InstanceGenerator(AbsractGenerator p, Class clazz) {
		super( p,  clazz);
	}

	@Override
	public void generateRandom() {
		super.generateRandom();
		//if(withInstance)
			object=getInstance();
		//else
			if(object!=null && !defaultClassesSet.contains(clazz)&& !clazz.equals(Object.class) && isAccessible(clazz))
				defaultClassesSet.add(clazz);
	}
	protected boolean canGoOn(){
		Long stubCost=stub2Cost.get(stub);
		return (!isParent(stub,this)&&!isParent(clazz,this)&&!(deep(clazz)>20));//&&!(deep(clazz)>4)&&!(stubCost!=null && stubCost>MAX_COST)
	}

	private int deep(Class cls){
		int d=0;
		AbsractGenerator currentParent=this.parent;
		while (currentParent!=null){
			d++;
			currentParent=currentParent.getParent();
		}
		return d;
	}

	
//	
//	private boolean isSys(){
//		boolean sys=clazz.getCanonicalName().toString().startsWith("java.io");
//		
//		return sys;
//	}
	
	private boolean isInstantiable(Class cls){
		if(cls.isAnonymousClass()) return false;
		if (cls.isInterface()) return false;
		if(generateFactoryMethods(cls).size()>0) return true;
		if(getSingletons(cls).size()>0) return true;
		if (Modifier.isAbstract(cls.getModifiers())) return false;
		if(getConstructors(cls).size()>0) return true;
		if(cls.getDeclaredConstructors().length==0) return true; //no constructor is defined we can instantiate by using newInstance 

		return false;
	}

	private static Set<Class<?>> getStubs4Class(Class cls){
		Set<Class<?>> subTypeSet=null;		
		try{
			 subTypeSet = reflections.getSubTypesOf(cls);
		}
		catch (ReflectionsException exc){
			//exc.printStackTrace();
		}		
		return subTypeSet;
	}
	
	public static Vector<Class> generateStubs(Class clazz){
		if(clazz.equals(Object.class))
			return defaultClassesSet;
		
		 Vector<Class> tmpPossiblesStubs=new Vector<Class>();
		 tmpPossiblesStubs=interface2Implementation.get(clazz);
		if(tmpPossiblesStubs!=null)	{
			return tmpPossiblesStubs;
		}
		
		tmpPossiblesStubs=new Vector();
		
		Set<Class<?>> subTypeSet=getStubs4Class(clazz);		
		
		
		if(subTypeSet!=null)
		    for(Class clss:subTypeSet){
		    	//System.out.println("**check "+clss);
		    	try{	
		    		//check if a class is a part of classpath is good but decrease performances
		    		//if((isInClassPath(clss))&&inClasspath(clss))//(areInSameArchive(clss,clazz)||(clazz.equals(JTE.currentClassUnderTest.getClazz())&& inClassPath(clss))) ||isInJavapath(clss) 
		    		if(clazz.getName().contains("AbstractLoader")){
		    			
		    			System.out.println ("clss "+clss);
		    			System.out.println ("clss "+getResourcePath(clss));
		    			System.out.println ("clazz "+getResourcePath(clazz));
		    			System.out.println ("areInSameArchive(clss,clazz) "+areInSameArchive(clss,clazz));
		    			System.out.println ("(JTE.classPath.length<5 && isInClassPath(clss)) "+(JTE.classPath.length<5 && isInClassPath(clss)));
		    			System.out.println ("(clazz.equals(JTE.currentClassUnderTest.getClazz())&& isInClassPath(clss))) "+(clazz.equals(JTE.currentClassUnderTest.getClazz())&& isInClassPath(clss)));
		    			System.out.println ("isAccessible(clss) "+isAccessible(clss));
		    		}
		    		if(!clss.isAnonymousClass() 
			    			&& (( areInSameArchive(clss,clazz) ||(JTE.classPath.length<5 && isInClassPath(clss)) 
			    					|| (isInJavapath(clss))
			    							||(clazz.equals(JTE.currentClassUnderTest.getClazz())&& isInClassPath(clss))))
			    			//&& isInClassPath(clss)
			    			){
			    		if(isAccessible(clss))
			    		{
			    			tmpPossiblesStubs.add(clss);
			    		}
			    	}
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
		    	//System.out.println("**done "+clss);
		    }
    	interface2Implementation.put(clazz,new Vector(tmpPossiblesStubs));	
    	//System.out.println("**End "+tmpPossiblesStubs.size());
    	return tmpPossiblesStubs;	
	}
	
	private boolean areInSamePackage(Class cls1, Class cls2){
		return (cls1.getPackage().getName().equalsIgnoreCase(cls2.getPackage().getName()));
	}
	
	private static boolean areInSameArchive(Class cls1, Class cls2){
		return (getResourcePath(cls1).equalsIgnoreCase(getResourcePath(cls2)));
	}
	
	private int nbrOfDeclaringClasses(Class cls){
		if(cls.getDeclaringClass()==null)
			return 0;
		else
			return nbrOfDeclaringClasses(cls.getDeclaringClass())+1;
	}
	
	private Vector<Method> generateFactoryMethods(Class cls){
		Vector<Method> allFactoryMethods=new Vector<Method>();
		if (cls==null||cls.equals(Object.class)||cls.equals(Class.class))
			return allFactoryMethods;
		
		//System.out.println(cls.getCanonicalName());
		Method[] allMethods;
		//try{
		allMethods=cls.getDeclaredMethods();
		//}catch(Exception e){
		//	return allFactoryMethods;
		//}
		for(Method m:allMethods){
			if(cls.equals(m.getReturnType())
					&& !m.getName().startsWith("access")
					&& Modifier.isStatic(m.getModifiers())
					&& isAccessible(m)){
				allFactoryMethods.add(m);
			}
		}
		return allFactoryMethods;
	}

	private boolean isAccessible(Field field){
		Class declaringClass=field.getDeclaringClass();
		return (isAccessible(declaringClass)
				&&(!Modifier.isPrivate(field.getModifiers()))
				&&(!Modifier.isProtected(field.getModifiers()))
				&&((Modifier.isPublic(field.getModifiers()) ||JTE.currentClassUnderTest.getClazz().getPackage().equals(declaringClass.getPackage())))
				);
	}
	
	private Vector<Constructor> getConstructors(Class cls){
		if (cls==null||cls.equals(Object.class)||cls.equals(Class.class))
			return new Vector<Constructor>();

		Vector<Constructor> allConstructors=new Vector<Constructor>();
		Constructor[] allDeclaredConstructors=cls.getDeclaredConstructors();
		for(Constructor c:allDeclaredConstructors){
			if(isAccessible(c)) //&& !isParent(c)
				allConstructors.add(c);
		}
		return allConstructors;
	}
	
	private Vector<Field> getSingletons(Class cls){
		try{
			if (cls==null||cls.equals(Object.class)||cls.equals(Class.class))
				return new Vector<Field>();
	
			Vector<Field> allSingletons=new Vector<Field>();
			Field[] allFields=cls.getDeclaredFields();//.getFields();
			for(Field f:allFields){
				if((f.getType().isAssignableFrom(cls)||cls.isAssignableFrom(f.getType()))
						&& Modifier.isStatic(f.getModifiers())
						&& isAccessible(f)
						&& !f.getType().equals(Object.class)
						)
					allSingletons.add(f);
			}
	
			return allSingletons;
		}catch (Exception e){
			e.printStackTrace();
			return new Vector<Field>();
			
		}
	}

	private Vector<Method> generateExternalMethods(Class cls){ 
		Vector<Method> externalMethods;
		
		externalMethods=new Vector<Method>();
		
		//Random modification
		if(cls==null||cls.equals(Object.class)||cls.equals(Class.class))
			return externalMethods;
		
		Set<Method> returnTypeSet=null;
		Set<Class<?>> subTypeSet=null;
		try{
			returnTypeSet = reflections.getMethodsReturn(cls);
		}
		catch (ReflectionsException exc){
			//exc.printStackTrace();
		}catch(NoClassDefFoundError e){
			
		}
		
		//externalMethods=new Vector<Method>();
		if(returnTypeSet!=null)
		    for(Method md:returnTypeSet){
		    	if(isAccessible(md) //&& !isParent(md) 
		    			//&&Modifier.isStatic(md.getModifiers())
						&&!Modifier.isAbstract(md.getModifiers())
						&& !md.getName().startsWith("access$")
						&& md.getDeclaringClass()!=cls
						&& !md.getDeclaringClass().isAnonymousClass()
						&& (areInSameArchive(md.getDeclaringClass(),cls)||areInSameArchive(md.getDeclaringClass(),JTE.currentClassUnderTest.getClazz())))
		    		externalMethods.add(md);
		    }	
		return externalMethods;
	}
	
	protected Vector<ExecutionWay> generateInstantiationWays(Class cls){
		Vector<ExecutionWay> tmpPossibleWays;
		tmpPossibleWays=class2InstantiationWays.get(cls);
		
		if(tmpPossibleWays!=null && !cls.equals(Object.class)){	
			possibleWays=tmpPossibleWays;
			return possibleWays;
		}
		
		//if(cls.getSimpleName().toString().equals("ReadableInstant"))
		//	clazz=clazz;

		tmpPossibleWays=new Vector<ExecutionWay>();
		
		if(cls.isAnonymousClass()){
			if(cls.getEnclosingMethod()!=null){
				Method m=cls.getEnclosingMethod();
				if(m.getReturnType().isAssignableFrom(cls))
					tmpPossibleWays.add(new ExecutionWay(m,m.getDeclaringClass()));
			}
			possibleWays=tmpPossibleWays;
			return possibleWays;
		}
		
		//generate external methods
		for(Method em:generateExternalMethods(cls))
				tmpPossibleWays.add(new ExecutionWay(em,null));
			
		//("generating data members  ");		
		for(Field f:getSingletons(cls))
			tmpPossibleWays.add(new ExecutionWay(f));
		
		//("generating FactoryMethods ");
		//generate factory methods
		for(Method fm:generateFactoryMethods(cls))
			tmpPossibleWays.add(new ExecutionWay(fm));

		
		//("generating constructors ");
		//generate constructors ways
		if(!Modifier.isAbstract(cls.getModifiers()))
			for(Constructor c: getConstructors(cls))
				if(recommandedWays.contains(c))//cls.equals(clazz)&&
					tmpPossibleWays.add(new ExecutionWay(c,true));	
				else
					tmpPossibleWays.add(new ExecutionWay(c));
		
		//generate newinstance way
		if(cls.getDeclaredConstructors().length==0 
				&& !Modifier.isAbstract(cls.getModifiers())
				&& !Modifier.isInterface(cls.getModifiers()))
			tmpPossibleWays.add(new ExecutionWay(cls));
			
		//never change the source code please
		for(Class s:generateStubs(cls)){		
			if(!s.isInterface() 
					&& !Modifier.isAbstract(s.getModifiers()))
					tmpPossibleWays.addAll(generateInstantiationWays(s));
		}
		
		//Java Classes
		if(!cls.equals(Object.class) 
				&& (cls.getPackage()!=null && 
						isInJavapath (cls) ) //cls.getPackage().getName().toString().startsWith("java.")
				)
		{
			
			Reflections reflections1=package2Reflections.get(cls.getPackage().getName().toString());
			if(reflections1==null){
				File java_home = new File(System.getProperty("java.home"));
				File java_classes=new File(java_home.getParent()+"/Classes/classes.jar"); ///lib/rt.jar
				if(JAVA_VERSION>=1.7) 
					java_classes=new File(java_home.getParent()+"/jre/lib/rt.jar"); //This is not correct for java 8
				//System.out.println(java_classes);
				URL[] urls = new URL[1];
				for(int i =0;i<1;i++){
					try {
						urls[i]=java_classes.toURL();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//				System.err.println("+++++++++++++++++++++++++++++++");
//				System.err.println("cls.getPackage().getName()  : "+ cls.getPackage().getName());
//				System.err.println("+++++++++++++++++++++++++++++++");
				reflections1 = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(false /* don't exclude Object.class */))
				.filterInputsBy(new FilterBuilder().includePackage(cls)) // ; . .getPackage().getName().toString()
				.setUrls(urls));
				package2Reflections.put(cls.getPackage().getName().toString(), reflections1);
			}
			
			Set<Class<?>> subTypes = reflections1.getSubTypesOf(cls);// extends SomeClassOrInterface
			
			for(Class s:subTypes){
				if(!s.isInterface() && !Modifier.isAbstract(s.getModifiers()))
				if(!s.isAnonymousClass()&&isAccessible(s)){
					//System.out.println("*****"+s);
					tmpPossibleWays.addAll(generateInstantiationWays(s));
				}
			}
			
		}
		
		
		possibleWays=tmpPossibleWays;
		class2InstantiationWays.put(cls, possibleWays);
		if(possibleWays.size()<1)	stub2Cost.put(clazz, MAX_COST);
//		
//	    if(tmpPossibleWays.size()<1){ //&&(!withInstance)
//	    	System.err.println("");
//	    	System.err.println("**************************************************************************************");
//	    	System.err.println (" The Interface or abstruct class \'"+clazz+"\' requires a stub " + possibleWays.size());
//	    	for (ExecutionWay ew:possibleWays)
//	    		System.err.println (ew);
//	    	System.err.println("**************************************************************************************");
//	    		
//	    }
		if(clazz.getName().contains("AbstractLoader"))
			System.err.println (cls+" "+possibleWays);
		return possibleWays;
	}
	
	public static double JAVA_VERSION = getVersion ();

	static double getVersion () {
	    String version = System.getProperty("java.version");
	    
	    int pos = 0, count = 0;
	    for ( ; pos<version.length() && count < 2; pos ++) {
	        if (version.charAt(pos) == '.') count ++;
	    }
	    
	    return Double.parseDouble (version.substring (0, pos-1));
	}
	
	private boolean isProtectedOrAbstract (Class cls){
		if(cls.equals(Object.class))
			return true;
		if(cls.isInterface()
				||Modifier.isAbstract(cls.getModifiers())
				||(Modifier.isProtected(cls.getModifiers())&&cls.equals(JTE.currentClassUnderTest.getClazz())))
			return true;
		for(Constructor c: cls.getDeclaredConstructors())
			if(Modifier.isProtected(c.getModifiers())&&cls.equals(JTE.currentClassUnderTest.getClazz()))
				return true;
		for(Method m: cls.getDeclaredMethods())
			if(Modifier.isAbstract(m.getModifiers())
					||(Modifier.isProtected(cls.getModifiers())&&cls.equals(JTE.currentClassUnderTest.getClazz())))
				return true;
		return false;
	}
		
	public List<Statement>getStatements(AST ast, String varName,String pName){	
//		if(parent instanceof AbstractDynamicGenerator)
//			 SystematicallySurroundCall=((AbstractDynamicGenerator)parent).SystematicallySurroundCall;
		
		List<Statement>returnList=new ArrayList<Statement>();
		//create a variable declaration
		VariableDeclarationFragment varDec=ast.newVariableDeclarationFragment();
		varDec.setName(ast.newSimpleName(varName));
		
		switch(currentWay.source){
		case isNull:
			NullLiteral nullLiteral=ast.newNullLiteral();
			varDec.setInitializer(nullLiteral);
			//GeneConstructor.requiredClasses.add(clazz);
			break;
		//case isStatic:			
		case constructor:
			ClassInstanceCreation classInstance=null;
			if(parameters!=null){				
				classInstance= ast.newClassInstanceCreation();
				//classInstance.setExpression(x0);
				if(currentWay.constructor.getDeclaringClass().isMemberClass()){
					String newVarName="";
					int i=0;
					if( !Modifier.isStatic(currentWay.constructor.getDeclaringClass().getModifiers())){
						//dont change the following type, actually it requirds a simple type 
						//Type TypeName =getType2UseInJunitClass(currentWay.constructor.getDeclaringClass(),ast);
						classInstance.setType(ast.newSimpleType(ast.newSimpleName(currentWay.constructor.getDeclaringClass().getSimpleName())));											
//					if(!Modifier.isStatic(currentWay.constructor.getDeclaringClass().getModifiers())){
						newVarName=varName+pName+"P"+(1);
						returnList.addAll(parameters.get(0).getStatements(ast,newVarName,""));
						classInstance.setExpression(ast.newSimpleName(newVarName));
						i++;
					}else{
						//dont change the following type, actually it requirds a simple type 
						//classInstance.setType(ast.newSimpleType(ast.newSimpleName(currentWay.constructor.getDeclaringClass().getSimpleName())));
						Type TypeName =getType2UseInJunitClass(currentWay.constructor.getDeclaringClass(),ast);
						classInstance.setType(TypeName);
						
//						//I am not sure of the source of this problem (in some cases I have seen that the first parameter contains the class itself) i think clonage
//						if(parameters.get(0).getClazz().equals(currentWay.constructor.getDeclaringClass()))
//							i++;
					}
					
					for(;i<parameters.size();i++){
		    			newVarName=varName+pName+"P"+(i+1);
			    		returnList.addAll(parameters.get(i).getStatements(ast,newVarName,""));//classInstance.arguments().add(object);
			    		classInstance.arguments().add(ast.newSimpleName(newVarName));
		    		}		    		
		    		
				}else{
					Type TypeName =getType2UseInJunitClass(currentWay.constructor.getDeclaringClass(),ast); 
					classInstance.setType(TypeName);//ast.newSimpleType(ast.newSimpleName(currentWay.constructor.getDeclaringClass().getSimpleName());));
	
				    for(int i=0;i<parameters.size();i++){
				    	if(parameters.get(i).getObject()==null){
				    		
				    		NullLiteral nLiteral=ast.newNullLiteral();
				    		CastExpression ce=ast.newCastExpression();
				    		//System.out.println(currentWay.constructor);
				    		TypeName =getType2UseInJunitClass(currentWay.constructor.getParameterTypes()[i],ast);
				    		if(parameters.get(i) instanceof ListGenerator){
				    			ParameterizedType pt= ast.newParameterizedType(ast.newSimpleType(ast.newSimpleName("List")));
				    			pt.typeArguments().add(TypeName);//ast.newSimpleType(qNameClass));
				    			ce.setType(pt);
				    		}
				    		else 
				    			ce.setType(TypeName);
				    		
				    		ce.setExpression(nLiteral);
				    		classInstance.arguments().add(ce);
				    		
				    		//GeneConstructor.requiredClasses.add(parameters.get(i).getClazz());
				    	}
				    	else{
				    		String newVarName=varName+pName+"P"+(i+1);
				    		returnList.addAll(parameters.get(i).getStatements(ast,newVarName,""));//classInstance.arguments().add(object);
				    		classInstance.arguments().add(ast.newSimpleName(newVarName));
				    	}
				    }
				}
				varDec.setInitializer(classInstance);
			}
			else{
				MethodInvocation newInstanceInv = ast.newMethodInvocation();
				Name qNameClass=getName2UseInJunitClass(stub,ast);
				newInstanceInv.setExpression(qNameClass);//stub.getSimpleName()));			
				newInstanceInv.setName(ast.newSimpleName("newInstance"));				
			    varDec.setInitializer(newInstanceInv);
			}
			
						
			//GeneConstructor.requiredClasses.add(stub);
			break;
		case factoryMethod:
			MethodInvocation methodInv1 = ast.newMethodInvocation();
			Name qNamef=getName2UseInJunitClass(currentWay.method.getDeclaringClass(),ast);
			methodInv1.setExpression(qNamef);
			methodInv1.setName(ast.newSimpleName(currentWay.method.getName()));
			if(parameters!=null)
			    for(int i=0;i<parameters.size();i++){
			    	if(parameters.get(i).getObject()==null){
			    		NullLiteral nLiteral=ast.newNullLiteral();
			    		CastExpression ce=ast.newCastExpression();
			    		//System.out.println(currentWay.constructor);
			    		Type TypeName =getType2UseInJunitClass(currentWay.method.getParameterTypes()[i],ast); //getType2UseInJunitClass(parameters.get(i).getClazz(),ast);
			    		if(parameters.get(i) instanceof ListGenerator){
			    			ParameterizedType pt= ast.newParameterizedType(ast.newSimpleType(ast.newSimpleName("List")));
			    			pt.typeArguments().add(TypeName);//ast.newSimpleType(qNameClass));
			    			ce.setType(pt);
			    		}
			    		else 
			    			ce.setType(TypeName);
			    		
			    		ce.setExpression(nLiteral);
			    		methodInv1.arguments().add(ce);
			    		
			    		//methodInv1.arguments().add(ast.newNullLiteral());
			    	}else{
				    	String newVarName=varName+pName+"P"+(i+1);
				    	returnList.addAll(parameters.get(i).getStatements(ast,newVarName,""));//classInstance.arguments().add(object);
				    	methodInv1.arguments().add(ast.newSimpleName(newVarName));
			    	}
			    	//GeneConstructor.requiredClasses.add(parameters.get(i).getClazz());
			    }
		    
		    varDec.setInitializer(methodInv1);		    
		    //GeneConstructor.requiredClasses.add(currentWay.method.getDeclaringClass());
		    //GeneConstructor.requiredClasses.add(clazz);
		    break;			
		case externalMethod:
			String objectName;
			if(Modifier.isStatic(currentWay.method.getModifiers())){
				objectName=currentWay.method.getDeclaringClass().getSimpleName().toString();
				MethodInvocation methodInv = ast.newMethodInvocation();
				Name qNameEx=getName2UseInJunitClass(currentWay.method.getDeclaringClass(),ast);	
				methodInv.setExpression(qNameEx); 
				methodInv.setName(ast.newSimpleName(currentWay.method.getName()));
				if(parameters!=null)
			    for(int i=0;i<parameters.size();i++){
			    	if(parameters.get(i).getObject()==null){
			    		NullLiteral nLiteral=ast.newNullLiteral();
			    		CastExpression ce=ast.newCastExpression(); 
			    		//System.out.println(currentWay.constructor);
			    		Type TypeName =getType2UseInJunitClass(currentWay.method.getParameterTypes()[i],ast); //getType2UseInJunitClass(parameters.get(i).getClazz(),ast);
			    		if(parameters.get(i) instanceof ListGenerator){
			    			ParameterizedType pt= ast.newParameterizedType(ast.newSimpleType(ast.newSimpleName("List")));
			    			pt.typeArguments().add(TypeName);//ast.newSimpleType(qNameClass));
			    			ce.setType(pt);
			    		}
			    		else 
			    			ce.setType(TypeName);
			    		
			    		ce.setExpression(nLiteral);
			    		methodInv.arguments().add(ce);
			    	}else{
				    	String newVarName=varName+pName+"P"+(i+1);
				    	returnList.addAll(parameters.get(i).getStatements(ast,newVarName,""));//classInstance.arguments().add(object);
				    	methodInv.arguments().add(ast.newSimpleName(newVarName));
			    	//InstanceGenerator.requiredClasses.add(parameters.get(i).getClazz());
			    	}
			    }			    
			    varDec.setInitializer(methodInv);
			}else{
				objectName=varName+pName+"P0";
				if(externalConstructor.currentWay.source!=ExecutionSource.isNull){
					returnList.addAll(externalConstructor.getStatements(ast, objectName, ""));
					MethodInvocation methodInv = ast.newMethodInvocation();
					methodInv.setExpression(ast.newSimpleName(objectName));			
					methodInv.setName(ast.newSimpleName(currentWay.method.getName()));	
					if(parameters!=null)
				    for(int i=0;i<parameters.size();i++){
				    	if(parameters.get(i).getObject()==null){
				    		NullLiteral nLiteral=ast.newNullLiteral();
				    		CastExpression ce=ast.newCastExpression(); 
				    		//System.out.println(currentWay.constructor);
				    		Type TypeName =getType2UseInJunitClass(parameters.get(i).getClazz(),ast);
				    		if(parameters.get(i) instanceof ListGenerator){
				    			ParameterizedType pt= ast.newParameterizedType(ast.newSimpleType(ast.newSimpleName("List")));
				    			pt.typeArguments().add(TypeName);//ast.newSimpleType(qNameClass));
				    			ce.setType(pt);
				    		}
				    		else 
				    			ce.setType(TypeName);
				    		
				    		ce.setExpression(nLiteral);
				    		methodInv.arguments().add(ce);
				    	}else{
					    	String newVarName=varName+pName+"P"+(i+1);
					    	returnList.addAll(parameters.get(i).getStatements(ast,newVarName,""));//classInstance.arguments().add(object);
					    	methodInv.arguments().add(ast.newSimpleName(newVarName));
				    	}
				    	//InstanceGenerator.requiredClasses.add(parameters.get(i).getClazz());
				    }			    
				    varDec.setInitializer(methodInv);
				}else{
					NullLiteral nullLiteral1=ast.newNullLiteral();
					varDec.setInitializer(nullLiteral1);
				}
			}
			
		    
		    //GeneConstructor.requiredClasses.add(currentWay.method.getDeclaringClass());
		    //GeneConstructor.requiredClasses.add(clazz);
		    break;
		case dataMember:
			CastExpression cast=ast.newCastExpression();
			//Name qNameClass=getName2UseInJunitClass(clazz,ast);
			Type TypeName =getType2UseInJunitClass(clazz,ast);
			cast.setType(TypeName);//ast.newSimpleType(qNameClass));
			Name qNameClass1=getName2UseInJunitClass(currentWay.field.getDeclaringClass(),ast);
			QualifiedName qName=ast.newQualifiedName(qNameClass1, ast.newSimpleName(currentWay.field.getName()));
			cast.setExpression(qName);
			
			varDec.setInitializer(cast);
			//GeneConstructor.requiredClasses.add(currentWay.field.getDeclaringClass());
			//GeneConstructor.requiredClasses.add(clazz);
			break;			
		case newInstance:
			MethodInvocation newInstanceInv = ast.newMethodInvocation();
			Name qNameClass=getName2UseInJunitClass(stub,ast);
			newInstanceInv.setExpression(qNameClass);//stub.getSimpleName()));			
			newInstanceInv.setName(ast.newSimpleName("newInstance"));				
		    varDec.setInitializer(newInstanceInv);
		    //GeneConstructor.requiredClasses.add(stub);
		    //GeneConstructor.requiredClasses.add(clazz);
			break;
		default:
			System.out.println(currentWay);
		}
		
	    VariableDeclarationStatement varDecStat = ast.newVariableDeclarationStatement(varDec);
	    
	    //getName2UseInJunitClass(stub,ast);
	    //getName2UseInJunitClass(clazz,ast);
	    
	    Type TypeName =getType2UseInJunitClass(clazz,ast);
	    varDecStat.setType(TypeName);

	    if(!(varDec.getInitializer()instanceof NullLiteral)
	    		&&(exceptions.size()>0 || getUnexpectedException()!=null  || SystematicallySurroundCall)){ //||unexpectedException!=null)
	    	//AssignementStatement as =ast.new;
	    	Assignment Ass = ast.newAssignment();
	    	Ass.setLeftHandSide(ast.newSimpleName(varName));
	    	Expression leftHandExp =varDec.getInitializer();
	    	varDec.setInitializer(ast.newNullLiteral());
	    	returnList.add(varDecStat);
	    	
	    	Ass.setRightHandSide(leftHandExp);
	    	ExpressionStatement AssStatement=ast.newExpressionStatement(Ass);
	    	
	    	TryStatement tryStatement=ast.newTryStatement();

//	    	for(Class exce:exceptions)
//	    		if(!exce.equals(Throwable.class))
//	    			tryStatement.catchClauses().add(getCatchClause(exce,ast,true));

		    Class except=Exception.class;		    	
	    	tryStatement.catchClauses().add(getCatchClause(except,ast,false));

	    	Block b =ast.newBlock();
	    	b.statements().add(AssStatement);
	    	tryStatement.setBody(b);
	    	//if an exception add a signe like as System.out.println("Process exitValue: " + exitVal1);
//	    	if(exceptions.size()>0 || getUnexpectedException()!=null){
//	    		//Statement Sign=String2Expression.getInfixExpression("System.out.println(\"1\");");
//	    		MethodInvocation Sign = ast.newMethodInvocation();
//	    		Sign.setExpression(ast.newQualifiedName(ast.newSimpleName("System"), ast.newSimpleName("out"))); 
//	    		Sign.setName(ast.newSimpleName("println"));
//	    		Sign.arguments().add(ast.newNumberLiteral("1"));
//	    		Statement statement1=ast.newExpressionStatement(Sign); 
//	    		returnList.add(statement1);
//	    	}
	    			
	    	returnList.add(tryStatement);
	    }else
	    	returnList.add(varDecStat);
	    
	    //System.out.println(returnList);
		return returnList;
	}
	
	public Object getInstanceOnce(){
		once=true;
	    return getInstance();
	}
	
	public Object getInstance(){
		//if(true) return null;
		//System.out.println("generating instance for : "+clazz.getName());
		//System.out.println("using  : "+currentWay.source+" *** "+currentWay);
		exceptions=new HashSet(); 
		object=null;
		try{
			switch(currentWay.source){
			//case isStatic:
			case isNull:
				object=null;
				break;
			case constructor:
				final Constructor  tmpCon=currentWay.constructor;
				AccessController.doPrivileged(new PrivilegedExceptionAction() {
				     public Object run() throws Exception {
				         if(!tmpCon.isAccessible()) {
				        	 tmpCon.setAccessible(true);
				         }
				         //exceptions.clear();
				         exceptions=new HashSet();
				         //setUnexpectedException(null);
				         for(Class thro:tmpCon.getExceptionTypes())
				        	 addExceptionClass(thro);
					      try{
					    	  
								object=tmpCon.newInstance(getParameters());
							  
					     }catch (Throwable e){
					    	 setUnexpectedException(e);
				         }
				       return null;
				     }
				 });
				break;
			case factoryMethod:
			case externalMethod:
					final Method  tmpMethod1=currentWay.method;
					final Object tmpObject;
					if(Modifier.isStatic(tmpMethod1.getModifiers()))
						tmpObject=tmpMethod1.getDeclaringClass();
					else{
						//if(externalConstructor)
						if((externalConstructor.currentWay.source==ExecutionSource.isNull)
						|| (externalConstructor.object==null )
						){
							object=null;
							break;
						}
						tmpObject=externalConstructor.object; 
					}
					AccessController.doPrivileged(new PrivilegedExceptionAction() {
					     public Object run() throws Exception {
					         if(!tmpMethod1.isAccessible()) {
					        	 tmpMethod1.setAccessible(true);
					         }
					         //exceptions.clear();
					         exceptions=new HashSet();
					         //setUnexpectedException(null);
					         for(Class thro:tmpMethod1.getExceptionTypes())
					        	 addExceptionClass(thro);
						      try{
						    	  if(tmpObject!=null)
						    		  object=tmpMethod1.invoke(tmpObject, getParameters());
						     }catch (Throwable e){
						    	 setUnexpectedException(e);
					        }
					       return null;
					     }
					 });
					break;

			case newInstance:
				//exceptions.clear();
				exceptions=new HashSet();
				setUnexpectedException(null);
			     try{
			    	 object=stub.newInstance();
			     }catch (Throwable e){
			    	 setUnexpectedException(e);
			      }
				break;
			case dataMember:			
				final Field tmpField=currentWay.field;
				AccessController.doPrivileged(new PrivilegedExceptionAction() {
				     public Object run() throws Exception {
				         if(!tmpField.isAccessible()) {
				        	 tmpField.setAccessible(true);
				         }
				         //exceptions.clear();
				         exceptions=new HashSet();
				         setUnexpectedException(null);
					     try{
					    	 object=tmpField.get(null);
					     }catch (Throwable e){
					    	 setUnexpectedException(e);
					     }
				       return null;
				     }
				 });
				break;
			}
		}catch (Exception exc) {
			if (exc instanceof IllegalArgumentException) {
				exc.printStackTrace();
			}if (exc instanceof IllegalAccessException 
					|| exc instanceof InstantiationException
					|| exc instanceof InvocationTargetException) {
				//the class is not instantiable so remove the stub and try another one by calling a new stub choice and instantiation	
			//	restatrtGeneration();
				 exc.printStackTrace();
			}//else 
			if (!(exc instanceof InvocationTargetException)) 
			{
				exc.printStackTrace();
			}
		}
		
		if(once){
			return object;
		}
        
		if(object==null){
			//if(withInstance){			
			if(currentWay.source!=ExecutionSource.isNull){ //&& instantiationWay.source!=InstanceSource.isStatic
				generationNbr++;
				currentWay.cost+=PENALITY_COST;
				lastGenerationCost=currentWay.cost;
				if( generationNbr<=MAX_GENERATION_NBR){					
					selectInstantiationWay();
					generateRandom();
					//if(!withInstance)
					//	getInstance();
				}//else 
				//	instantiationWay=new InstantiationWay(InstanceSource.isNull);
			}
			//}
		}
		else {
			currentWay.cost+=ExecutionWay.getInitialCost(currentWay)+PENALITY_COST/10; //it s initial cost + INITIAL_COST
			lastGenerationCost=currentWay.cost;
			//if(currentWay.cost>=INITIAL_COST  && withInstance)
				
			if(object!=null && stub!=null && !defaultClassesSet.contains(stub)&& !stub.isAnonymousClass() && !stub.equals(Object.class)&& isAccessible(stub))
				defaultClassesSet.add(stub);
		}
		//setObject(object);
		return object;
	}
	
	@Override
	public Object clone() {
		InstanceGenerator newCon=new InstanceGenerator(parent,clazz);//parent,clazz,withInstance
		
		//newCon.clazz=this.clazz;
		//newCon.parent=this.parent;
		newCon.withInstance=this.withInstance;
		//newCon.variableBinding=this.variableBinding;
		newCon.fitness=this.fitness;
		newCon.currentWay=new ExecutionWay(currentWay);
		newCon.object=this.object;
		newCon.seed=this.seed;
		newCon.random=this.random;
		if(parameters!=null){
			newCon.parameters=new Vector<AbsractGenerator>();
			for(AbsractGenerator gene:parameters){
				newCon.parameters.add((AbsractGenerator)gene.clone());
			}
		}
		newCon.stub=this.stub;
		//if(possiblesStubs!=null)
		//	newCon.possiblesStubs=new Vector(this.possiblesStubs);

		if(recommandedWays!=null)
			newCon.recommandedWays=recommandedWays;
		newCon.exceptions=new HashSet();
		for(Class e:exceptions)
			newCon.addExceptionClass(e);
		newCon.unexpectedException=unexpectedException;
		newCon.generationNbr=generationNbr;
		newCon.parent=parent;
		newCon.possibleWays=possibleWays;
		
		if(externalConstructor!=null)
			newCon.externalConstructor=(InstanceGenerator)externalConstructor.clone();
		//newCon.
		return newCon;
	}
	
	@Override
	public String toString(){
		if(object!=null && object.getClass().equals(Object.class))
			object=object;
		
		String str=new String();
		switch (currentWay.source){
		case constructor:
			str=currentWay.constructor.getName();
			if(parameters!=null)
				str+=parameters.toString();
			break;
		case factoryMethod:
		case externalMethod:
			str=currentWay.method.getName();
			if(parameters!=null)
				str+=parameters.toString();
			break;
		case dataMember:
			str=currentWay.field.getName(); 
		//case isStatic:
		//	str="static";
		case isNull:
			str="null";
		}
	
		return str;
	}

	@Override
	public boolean isStatic() {
		// TODO Auto-generated method stub
		return false;
	}

}