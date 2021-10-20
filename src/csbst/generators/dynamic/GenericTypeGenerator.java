package csbst.generators.dynamic;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import csbst.generators.AbsractGenerator;
import csbst.testing.JTE;
import csbst.utils.FileEditor;
import csbst.utils.ClassLoaderUtil;

public class GenericTypeGenerator  extends AbsractGenerator{	
	private Vector<AbsractGenerator> components;
	//private List<Vector<List<Vector<Map<Integer,Integer>>>>> x;
	private ParameterizedType componentsTypes;
	public GenericTypeGenerator(AbsractGenerator parent, ParameterizedType types) { 
		super(parent,null);
		componentsTypes=types;
		AbsractGenerator gene;
		
		if(componentsTypes.getActualTypeArguments().length==0){
			gene=createAdequateGene(this, Object.class);
			components.add(gene);
		}
		else{
			Class cls=null;
			for(Type t:componentsTypes.getActualTypeArguments()){
				if(t instanceof ParameterizedType){
					gene=new GenericTypeGenerator(this, (ParameterizedType)t);
					components.add(gene);
				}
				else{
					String nclass=t.toString(); //((ParameterizedType)
					String []nclassHierarchy=nclass.split(" ");
					if(nclassHierarchy==null || nclassHierarchy.length<1)
						gene=createAdequateGene(this, Object.class);
					else{
						if(nclassHierarchy.length==1)
							nclass=ClassLoaderUtil.toCanonicalName(nclass);
						else
							nclass=nclassHierarchy[1];
						try {
							cls=JTE.magicClassLoader.loadClass(nclass);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
					gene=createAdequateGene(this, cls);
					components.add(gene);
				}
			}
		}
	}
	@Override
	public void generateRandom() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mutate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<Statement> getStatements(AST ast, String varName, String pName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	
//	public void generateRandom(){
//		boolean anyGeneNotInstiantiable=false ;
//		components=new Vector<Gene>();	
//		
//		for(int i=0;i<componentsTypes.size();i++){
//				Class cls=componentsTypes.get(i);
//				Gene gene;
//				gene=createAdequateGene(this, cls); 
//				components.add(gene);
////				if(cls.equals(List.class)||cls.equals(Collection.class))
////					if(currentWay.source==ExecutionSource.constructor)
////						gene=createAdequateGene(this, cls, currentWay.constructor.getGenericParameterTypes()[i]);//
////					else
////						gene=createAdequateGene(this, cls, currentWay.method.getGenericParameterTypes()[i]);//
////				else
////					gene=createAdequateGene(this,cls);
////				if(gene.object==null && gene instanceof GeneConstructor && (((GeneConstructor)gene).currentWay.source!=ExecutionSource.isNull))
////					currentWay.cost+=PENALITY_COST;
////				if(gene instanceof GeneConstructor)
////						currentWay.cost+=((GeneConstructor)gene).lastGenerationCost;
//		}
//	}
	
	
//	@Override
//	public List<Statement> getStatements(AST ast, String varName, String pName) {
//		List<Statement>returnList=new ArrayList<Statement>();
//
//		VariableDeclarationFragment myVar=ast.newVariableDeclarationFragment();
//		myVar.setName(ast.newSimpleName(varName));
//
//		//new ArrayList<Integer>(Arrays.asList(new 
//		ClassInstanceCreation classInstanceArrayList= ast.newClassInstanceCreation();
//	    classInstanceArrayList.setType(ast.newSimpleType(ast.newSimpleName("ArrayList")));
//	    
//       
//	    MethodInvocation invokeAsList=ast.newMethodInvocation();
//	    invokeAsList.setExpression(ast.newSimpleName("Arrays"));
//	    invokeAsList.setName(ast.newSimpleName("asList"));
//	    
//	    classInstanceArrayList.arguments().add(invokeAsList);
//	    
//		VariableDeclarationStatement myVarDec = ast.newVariableDeclarationStatement(myVar);
//		myVarDec.setType(ast.newSimpleType(ast.newSimpleName("List")));
//        myVar.setInitializer(classInstanceArrayList);
//             
//        ArrayCreation aCreation= ast.newArrayCreation();
//        ArrayInitializer aInitializer=ast.newArrayInitializer();
//        {
//            aCreation.setType(ast.newArrayType(ast.newSimpleType(ast.newSimpleName(clazz.getSimpleName()))));
//	        for(int i=0;i<length;i++){
//	        	String newVarName=varName+pName+"P"+(i+1);
//		    	returnList.addAll(elements[i].getStatements(ast,newVarName,""));
//	        	aInitializer.expressions().add(ast.newSimpleName(newVarName));
//	        }
//        }
//        aCreation.setInitializer(aInitializer);
//        invokeAsList.arguments().add(aCreation);
//        
//		returnList.add(myVarDec);
//		
//		GeneConstructor.requiredClasses.add(List.class);
//		GeneConstructor.requiredClasses.add(Arrays.class);
//		GeneConstructor.requiredClasses.add(ArrayList.class);
//		return returnList;
//	}
	
//	@Override
//	public Object getObject(){
//		//Array.newInstance(clazz, length);
//		Object retVal=new ArrayList();//new Object[length];
//		//retVal
//		//Object list = field.getType().newInstance();
//
//		try {
//			Method add = List.class.getDeclaredMethod("add",Object.class);
//			//add.invoke(list, addToAddToList);
//			for(int i=0;i<length;i++){
//				try {
//					add.invoke(retVal, elements[i].getObject());
//				} catch (IllegalArgumentException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}//Array.set(retVal, i, elements[i].getObject());
//			}
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//		return retVal;		
//	}

//	@Override
//	public Object clone() {
//		GeneGenericType newList=new GeneGenericType(parent,clazz);		
//		newList.clazz=this.clazz;;
//		newList.variableBinding=this.variableBinding;
//		newList.fitness=this.fitness;
//		newList.object=this.object;
//		newList.seed=this.seed;
//		newList.random=this.random;
//		newList.length=this.length;
//		newList.isFixedSize=this.isFixedSize;
//		
//		if(length>0){
//			newList.elements=new Gene[length];
//			for(int i=0;i<length;i++)
//				newList.elements[i]=(Gene)this.elements[i].clone();
//		}
//		return newList;
//	}
}
