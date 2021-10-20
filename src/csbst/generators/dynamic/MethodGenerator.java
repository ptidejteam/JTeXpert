package csbst.generators.dynamic;


import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
//import java.lang.reflect.ParameterizedType;










//import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import csbst.ga.ecj.TestCaseCandidate;
import csbst.generators.AbsractGenerator;
import csbst.generators.CopyGenerator;
import csbst.generators.atomic.ObjectGenerator;
import csbst.generators.containers.ArrayGenerator;
import csbst.generators.containers.ListGenerator;
import csbst.heuristic.RandomTesting;
import csbst.testing.JTE;
import csbst.utils.FileEditor;

public class MethodGenerator extends AbstractDynamicGenerator<Method>{
	Object classUTInstance;
	Object returnObject;
	String returnObjectName;
	boolean returnStable;
	boolean isAnonymousGenerator;
	
	private Vector<MethodGenerator> methodsOnReturnObject;
	
	protected static Map<Class,Vector<ExecutionWay>>class2MethodsWays=new HashMap<Class,Vector<ExecutionWay>>();
	
	public MethodGenerator(AbsractGenerator parent, Class clazz, Class stub, Vector<Method> recommandedMethodes) {
		super(parent,clazz, stub,recommandedMethodes,false);
	}

	public MethodGenerator(AbsractGenerator parent, Class clazz) { 
		super( parent,  clazz);
	}

	public MethodGenerator(TestCaseCandidate testCaseCandidate, Class clazz,
			Class stub, Vector<Method> methodsMayReach,boolean isAnonymousGenerator) {
		// TODO Auto-generated constructor stub
		this((AbsractGenerator)null,clazz, stub,methodsMayReach);
		this.testCaseCandidate=testCaseCandidate;
		this.isAnonymousGenerator=isAnonymousGenerator; 
		
	}

	public Object getReturnedObject(){
		return returnObject;
	}
	
	public String getReturnedObjectName(){
		return returnObjectName;
	}
	
	@Override
	public void generateRandom() {
		super.generateRandom();
		
	}
	
	private void generateSubSequence(){
			
			Vector<Method> methods=new Vector();
			Class cls=currentWay.method.getReturnType();//returnObject.getClass();
			for(Method m:cls.getMethods()){ // currentWay.method.getReturnType()
				if(m.getDeclaringClass().equals(cls) 
						&& !m.getDeclaringClass().equals(Object.class)
						//&& !isAccessible(cls) //may be replaced with, cls is declared in the class under test
						){
					
					boolean pass2Next=false;
					for(Class c:m.getParameterTypes()) //I got a lot of problem with submethods with Object as parameter.
						if(c.equals(Object.class))
							pass2Next=true; 
					if(!pass2Next)
						methods.add(m);
				}
			}
			
			
			methodsOnReturnObject=new Vector();
			if(methods.size()<1) 
				return;
			
			int mn=new Random().nextInt(3);
			for(int i=0;i<mn;i++){
				methodsOnReturnObject.add(new MethodGenerator(this, cls, cls,methods));//currentWay.method.getReturnType()
				methodsOnReturnObject.get(i).generateRandom();
			}
//		}		
	}
	
	
	private void executeSubSequence(){
		for(MethodGenerator m:methodsOnReturnObject)
			m.execute(returnObject, returnObject.getClass());
	}


	//csbst.generators.dynamic.AbstractDynamicGenerator.
	protected Vector<ExecutionWay> generateInstantiationWays(Class cls){
		//possibleWays=class2MethodsWays.get(cls);
		//if(possibleWays!=null)
		
		if(recommandedWays!=null && recommandedWays.size()>0)
			for(Method m: recommandedWays)
				possibleWays.add(new ExecutionWay(m,0));
		else
		{ 
			Vector<Method> allMethods=new Vector<Method>();
			
			//class under test methods
			for(Method m:cls.getDeclaredMethods()){
				if(isAccessible(m))
					if(!allMethods.contains(m)&& !m.isSynthetic() && !m.isBridge()){
						allMethods.add(m);
						possibleWays.add(new ExecutionWay(m,INITIAL_COST));
					}
			}

			//Random modification********************
			//stub under test methods
			//if(!stub.equals(cls))
			
			for(Method m:stub.getDeclaredMethods()){
				if(isAccessible(m)) //&& !isParent(c)
					if(!allMethods.contains(m)&& !m.isSynthetic() && !m.isBridge())
					{
						//if(m.getName().equalsIgnoreCase("compareTo"))
						//	System.out.println("At stub:"+m+" D:"+m.isSynthetic()+" D:"+m.isBridge()+" D:"+m.isAccessible());
						allMethods.add(m);
						possibleWays.add(new ExecutionWay(m,2*INITIAL_COST));
					}
			}

			//cls.get
			Class superCls=cls.getSuperclass();
			int currentCost=3*INITIAL_COST;
			int level=0;
			while(superCls!=null && !superCls.equals(Object.class)  
					&& level<3 && !superCls.isInterface()
					&& !superCls.getName().startsWith("java.lang")){
				currentCost+=INITIAL_COST;
				level++;
				for(Method m:superCls.getDeclaredMethods()){
					if(isAccessible(m) && !m.isSynthetic() && !m.isBridge()) //&& !isParent(c)
						if(!allMethods.contains(m)){
							//if(m.getName().equalsIgnoreCase("compareTo"))
							//	System.out.println("At super:"+m+" D:"+Modifier.isAbstract(m.getModifiers())+" D:"+m.isSynthetic()+" D:"+m.isBridge()+" D:"+m.isAccessible());
							allMethods.add(m);
							possibleWays.add(new ExecutionWay(m,currentCost));
						}
				}
				superCls=superCls.getSuperclass();
			}
		}
		return possibleWays;
	}

	public List<Statement>getStatements(final AST ast, String varName, String pName){
		return getStatements(ast,  varName,  pName, true);
	}
	public List<Statement>getStatements(final AST ast, String varName, String pName, boolean generateAssertion){	
		final List<Statement>returnList=new ArrayList<Statement>();
		if(currentWay.method==null 
				|| (classUTInstance==null && !Modifier.isStatic(currentWay.method.getModifiers())))
			return returnList;
//		if(parent instanceof AbstractDynamicGenerator)
//			 SystematicallySurroundCall=((AbstractDynamicGenerator)parent).SystematicallySurroundCall;
		
		//create a method invocation		
		MethodInvocation methodInv = ast.newMethodInvocation();
		
		if(!Modifier.isStatic(currentWay.method.getModifiers())){
			//System.out.println("Variable Name: "+varName);
			if(!clazz.equals(currentWay.method.getDeclaringClass())
					&& stub.equals(currentWay.method.getDeclaringClass())){
				CastExpression ce=ast.newCastExpression();	
	    		Type TypeName =getType2UseInJunitClass(currentWay.method.getDeclaringClass(),ast);
	    		ce.setType(TypeName);

	    		ce.setExpression(ast.newSimpleName(varName));
	    		ParenthesizedExpression pe=ast.newParenthesizedExpression();
	    		pe.setExpression(ce);
	    		methodInv.setExpression(pe); //ast.newSimpleName(varName)
			}else
				methodInv.setExpression(ast.newSimpleName(varName)); 
		}
		else{
			Name qNamef=getName2UseInJunitClass(currentWay.method.getDeclaringClass(),ast);
			methodInv.setExpression(qNamef);
		}
		
		methodInv.setName(ast.newSimpleName(currentWay.method.getName().toString()));	
		Vector<Integer> parametersToCheck=new Vector<Integer>();
		
	    for(int i=0;i<parameters.size();i++){
	    	if((parameters.get(i).getObject()==null) && !(parameters.get(i) instanceof CopyGenerator)){	    		
	    		
	    		NullLiteral nLiteral=ast.newNullLiteral();
	    		CastExpression ce=ast.newCastExpression(); 	
	    			    		
	    		//Name qNameClass=null;//getName2UseInJunitClass(currentWay.method.getParameterTypes()[i],ast);//(parameters.get(i).getClazz(),ast);
	    		Type TypeName =getType2UseInJunitClass(currentWay.method.getParameterTypes()[i],ast);
	    		if(parameters.get(i) instanceof ListGenerator){
	    			ParameterizedType pt= ast.newParameterizedType(ast.newSimpleType(ast.newSimpleName("List")));
	    			pt.typeArguments().add(TypeName);
	    			ce.setType(pt);
	    		}else
	    			ce.setType(TypeName); 

	    		ce.setExpression(nLiteral);
	    		methodInv.arguments().add(ce);
	    		
	    		//GeneConstructor.requiredClasses.add(parameters.get(i).stub);
	    	}else{
	    		
	    		String newVarName;
	    		if(parameters.get(i) instanceof CopyGenerator)
	    			newVarName=varName;
	    		else {
	    			newVarName=varName+pName+"P"+(i+1);	 
	    			parameters.get(i).setVariableName(newVarName);
	    			returnList.addAll(parameters.get(i).getStatements(ast,newVarName,""));//classInstance.arguments().add(object); 
	    		}
	    		
	    		//System.out.println(parameters.get(i));
	    		//System.out.println(parameters.get(i).getClazz());

	    		if((parameters.get(i) instanceof ObjectGenerator)  
	    				||(!(parameters.get(i) instanceof CopyGenerator) && parameters.get(i).getClazz().equals(Object.class))
	    				
	    				){	  //parameters.get(i) instanceof ObjectGenerator || (parameters.get(i) instanceof ArrayGenerator &&
		    		CastExpression ce=ast.newCastExpression();	
		    		Type TypeName =getType2UseInJunitClass(currentWay.method.getParameterTypes()[i],ast);
		    		if(parameters.get(i) instanceof ListGenerator){
		    			ParameterizedType pt= ast.newParameterizedType(ast.newSimpleType(ast.newSimpleName("List")));
		    			pt.typeArguments().add(TypeName);
		    			ce.setType(pt);
		    		}else
		    			ce.setType(TypeName);

		    		ce.setExpression(ast.newSimpleName(newVarName));
		    		methodInv.arguments().add(ce);
	    		}else
	    			methodInv.arguments().add(ast.newSimpleName(newVarName));
	    		
	    		//after the method call
	    		if((parameters.get(i)!=null)
	    				&& !currentWay.method.getParameterTypes()[i].isPrimitive()){
	    			
	    			
	    			parametersToCheck.add(i);	
	    		}
	    	}
	    }
	    
	    Statement statement;//=ast.newExpressionStatement(methodInv);

	    
		if(!currentWay.method.getReturnType().equals(void.class) && isAccessible(currentWay.method.getReturnType())){
			VariableDeclarationFragment varDec=ast.newVariableDeclarationFragment();	
			returnObjectName=varName+pName+"R";
			varDec.setName(ast.newSimpleName(returnObjectName));
			varDec.setInitializer(methodInv);
			VariableDeclarationStatement varDecStat;
			varDecStat = ast.newVariableDeclarationStatement(varDec);

			if(currentWay.method.getReturnType().isPrimitive())
				varDecStat.setType(ast.newPrimitiveType(getPrimitiveCode(currentWay.method.getReturnType())));
			else{
				varDecStat.setType(getType2UseInJunitClass(currentWay.method.getReturnType(),ast));
			}
			statement=varDecStat;

			//*******************************************
			if(!(varDec.getInitializer()instanceof NullLiteral)&&(exceptions.size()>0|| (getUnexpectedException()!=null)  || (SystematicallySurroundCall ))){ //||unexpectedException!=null)
		    	//AssignementStatement as =ast.new;
		    	Assignment Ass = ast.newAssignment(); 
		    	Ass.setLeftHandSide(ast.newSimpleName(returnObjectName));
		    	Expression leftHandExp =varDec.getInitializer();
		    	//****************verifier 
		    	if(currentWay.method.getReturnType().isPrimitive()){
		    			if(currentWay.method.getReturnType().getSimpleName().equals("boolean"))
		    				varDec.setInitializer(ast.newBooleanLiteral(false));
		    			else if(currentWay.method.getReturnType().getSimpleName().equals("char")){
		    				varDec.setInitializer(ast.newCharacterLiteral());
		    			}else
		    				varDec.setInitializer(ast.newNumberLiteral(getPrimitiveInitialiser(currentWay.method.getReturnType())));
		    	}else
		    		varDec.setInitializer(ast.newNullLiteral());
		    	
		    	returnList.add(varDecStat);
		    	
		    	Ass.setRightHandSide(leftHandExp);
		    	ExpressionStatement AssStatement=ast.newExpressionStatement(Ass);
		    	
		    	TryStatement tryStatement=ast.newTryStatement(); 
		    	
	    		Class except=Exception.class;			    	
		    	tryStatement.catchClauses().add(getCatchClause(except,ast,false));
			    	
		    	Block b =ast.newBlock();
		    	b.statements().add(AssStatement);
		    	tryStatement.setBody(b);
		    	
		    	returnList.add(tryStatement);
		    }else
		    	returnList.add(varDecStat);
			//*******************************************

		}else{
			statement=ast.newExpressionStatement(methodInv);
		
		    if(exceptions.size()>0 || getUnexpectedException()!=null || (SystematicallySurroundCall)){
		    	TryStatement tryStatement=ast.newTryStatement();
		    	
	    		Class except=Exception.class;
	    		//if(generateAssertion)
	    		tryStatement.catchClauses().add(getCatchClause(except,ast,false));

		    	Block b =ast.newBlock();
		    	b.statements().add(statement); 
		    	tryStatement.setBody(b);

    			
		    	returnList.add(tryStatement);
		    }
		    else
				returnList.add(statement);
		}
		
		if(generateAssertion){
			//System.err.println("Generating Assertions On Parameters");
			for (int i : parametersToCheck){
				AbsractGenerator ag=parameters.get(i);
				final Class cls=currentWay.method.getParameterTypes()[i];
				final AbsractGenerator  agp=ag;
				if(ag.getObject()!=null &&  ag.getVariableName()!=null &&  !ag.getVariableName().equals("") ){
					Thread thread = new Thread(){  
					@Override public void run (){
						if(cls.isArray()){ //.getClazz().isArray()
							addArrayChecker(returnList,ast,cls,agp.getObject(),agp.getVariableName());//agp.getObject().getClass()
						}else{ 	
							if(cls.equals(String.class))
								addToStringChecker(returnList,ast,cls,agp.getObject(),agp.getVariableName(),agp.getObject());
							else{
								//System.err.println("++++++++++++++String Insert"+cls.getName());
								//if(cls.getName().equals(String.class)){
									addStringChecker(returnList,ast,cls,agp.getObject(),agp.getVariableName(),agp.getObject());//.getObject().getClass()
									//System.err.println("++++++++++++++String Insert");
								//}else
									addMethodsChecker(returnList,ast,cls,agp.getObject(),agp.getVariableName(),agp.getObject());//use another method
							}
						}
					}
					};
					
					thread.setDaemon(false);
					thread.start();
					try {
						thread.join(1000); //35s toute une class
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(thread.isAlive()){
						thread.interrupt();	
						if(thread.isAlive()){
							Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
							thread.setPriority(Thread.MIN_PRIORITY);
							RandomTesting.killerQueue.add(thread);
							//interrupted=true;
							if(!RandomTesting.killerThread.isAlive())
								RandomTesting.killerThread.start();
						}
					}
				}
			}
		}
		//System.err.println("*********End of Generating Assertions On Parameters");
		
		
		if(returnObject!=null 
				&& parent==null 
				&& !currentWay.method.getReturnType().isPrimitive()
				&& !currentWay.method.getReturnType().equals(void.class) 
				&& isAccessible(currentWay.method.getReturnType())
				&& methodsOnReturnObject!=null){
					int i=0;
					for(MethodGenerator m:methodsOnReturnObject) 
						returnList.addAll(m.getStatements(ast,returnObjectName,"P"+(i++)));
						//m.execute(returnObject, returnObject.getClass());
		}

		if(generateAssertion){
			if(returnObject==null ) { //traiter le cas static !!!!!!!!!!!!!!!! I dont know why, but this influences the couverage
				if(returnObjectName!=null && !returnObjectName.equals("")){
					MethodInvocation assertNull=ast.newMethodInvocation();
					assertNull.setName(ast.newSimpleName("assertNull"));
					assertNull.arguments().add(ast.newSimpleName(returnObjectName));
		        	Statement statement1=ast.newExpressionStatement(assertNull); 
		        	returnList.add(statement1);
				}
			}else{ 		
				MethodInvocation assertEquals=ast.newMethodInvocation();
				assertEquals.setName(ast.newSimpleName("assertEquals"));
				//write an assertion for primitive, string, and primitive classes  
				if(currentWay.method.getReturnType().isPrimitive()
						|| currentWay.method.getReturnType().equals(String.class)){	
					//if(!currentWay.method.getName().equalsIgnoreCase("hashCode"))
						addStringPrimitiveChecker(returnList,ast,currentWay.method.getReturnType(),returnObject, returnObjectName);
				}else{
	
					final Class cls=currentWay.method.getReturnType();
					final Object OLDobj=returnObject;
					final String nm=returnObjectName;
					
					//System.err.println("OLDobj: "+returnObject);
					//System.err.println("classUTInstance: "+classUTInstance);
					if(classUTInstance!=null)
						execute(classUTInstance,clazz); 
						
					final Object NEWobj=returnObject;
					if(NEWobj==null)
						return returnList;
					//System.err.println("====Method: "+currentWay.method.getName());
					
					if(cls!=null && OLDobj!=null && nm!=null  ){
						Thread thread = new Thread(){  
						@Override public void run (){
							if(cls.isArray()){
								addArrayChecker(returnList,ast,cls,OLDobj,nm);
							}else{ 	
								//System.err.println("= addMethodsChecker");
								//if(currentWay.method.getName().equalsIgnoreCase("withUTC"));
								addStringChecker(returnList,ast,cls,OLDobj,nm,NEWobj);
								addMethodsChecker(returnList,ast,cls,OLDobj,nm,NEWobj);//use another method
							}
						}
						};
						
						thread.setDaemon(false);
						thread.start();
						try {
							thread.join(750); //35s toute une class
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if(thread.isAlive()){
							thread.interrupt();	
							if(thread.isAlive()){
								Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
								thread.setPriority(Thread.MIN_PRIORITY);
								RandomTesting.killerQueue.add(thread);
								//interrupted=true;
								if(!RandomTesting.killerThread.isAlive())
									RandomTesting.killerThread.start();
							}
						}
					}
				}
			}
		}
		//System.err.println("*********End of Generating Assertions On Return Value");
		return returnList;
	}

	private static void addMethodsChecker(List<Statement>returnList, AST ast,final Class cClass,Object cObject,String cName, Object checkObj){	
		
		if(cObject==null)
			return;
		
		final Method classMethods[] =cClass.getMethods().clone();
		int i=0; 
		Vector<Method> checkerMethods=new Vector<Method>();
		while(i<classMethods.length){
			if((classMethods[i].getReturnType().isPrimitive() ) //|| classMethods[i].getReturnType().equals(String.class)
					&& !classMethods[i].getReturnType().equals(void.class)
					&& classMethods[i].getParameterTypes().length==0
					&& classMethods[i].getDeclaringClass().equals(cClass)
					&& !Modifier.isStatic(classMethods[i].getModifiers())
					) 
			{
				checkerMethods.add(classMethods[i]);
				
			}
			i++;
		}
		
		boolean transitiveToString = false;
		if(checkerMethods.size()==0){
			//System.err.println("++++++++++++++++++++++++++transitive string+++++++++++++");
			//System.err.println("++++++++++++++Method: "+cClass);
			transitiveToString=true;
			i=0;
			while(i<classMethods.length){
				Method subMethod=null;
				if(classMethods[i].getParameterTypes().length==0 && 
						!classMethods[i].getReturnType().isPrimitive()
						&& !classMethods[i].getReturnType().equals(void.class)
						&& classMethods[i].getDeclaringClass().equals(cClass)
						&& !Modifier.isStatic(classMethods[i].getModifiers())
						) {
					try {
						subMethod = classMethods[i].getReturnType().getMethod("toString",null);
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					
					if(subMethod!=null){
						checkerMethods.add( classMethods[i]);
						//System.err.println("*************Method: "+classMethods[i]);
					}
				}
				i++;
			}
		}
		
		if(checkerMethods.size()>0){
			
			Random rand1=new Random();
			int length = rand1.nextInt(checkerMethods.size())+1;
			if(length>3)
				 length=3;
			
			Vector<Method> visitedMethods=new Vector<Method>();
			for(int j=0;j< length;j++){
				Random rand2=new Random();
				int k = rand2.nextInt(checkerMethods.size()); 
				Method m=checkerMethods.get(k);
				while(visitedMethods.contains(m)){
					 rand2=new Random();
					 k = rand2.nextInt(checkerMethods.size()); 
					 m=checkerMethods.get(k);
				}
				visitedMethods.add(m);
				
				//System.err.println("m="+m);
				//checkerMethods.remove(k);
				
				if(m==null) // || m.getName().equalsIgnoreCase("hashCode")
					continue;
				
				Object returnValue=null; 
				Object returnValue1=null;
//				if(m!=null)
				{
					try {
						returnValue=m.invoke(cObject);
						returnValue1=m.invoke(checkObj);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} catch (InvocationTargetException e) { 
						// TODO Auto-generated catch block
						//e.printStackTrace(); 
					}
					if(returnValue==null) 
						continue;
					
					MethodInvocation assertEqualsString=ast.newMethodInvocation(); 
					assertEqualsString.setName(ast.newSimpleName("assertEquals"));
					
					if(m.getReturnType().equals(char.class)){
		        		CharacterLiteral charLiteral=ast.newCharacterLiteral();
		        		//UnicodeBlock a=(Character.UnicodeBlock)Array.get(returnObject, i);
		        		char a= (Character) returnValue;
		        		char b= (Character) returnValue1;
		        		if(a!=b) 
							continue;
		        		
		        		String unicode =""+a;
		        		if(unicode.equals(StringEscapeUtils.unescapeJava(unicode))){
		        			unicode="\'"+String.format("\\u%04x", (int) a)+"\'";//
		        		}
		        		charLiteral.setEscapedValue(unicode);
		        		
		        		assertEqualsString.arguments().add(charLiteral);
		        	}else if(m.getReturnType().equals(boolean.class)){
		        		boolean b=(Boolean) returnValue;
		        		boolean a= (Boolean) returnValue1;
		        		if(a!=b) 
							continue;
		        		
		        		BooleanLiteral boolLiteral=ast.newBooleanLiteral(b);
		        		assertEqualsString.arguments().add(boolLiteral);
		        	}else if(m.getReturnType().equals(float.class)){
		        		float f=(Float)returnValue;
		        		float f1= (Float) returnValue1;
		        		if(f!=f1) 
							continue;
		        		
		        		NumberLiteral numberLiteral=ast.newNumberLiteral(""+f+"F");
		        		assertEqualsString.arguments().add(numberLiteral);
		        	}else if(m.getReturnType().equals(double.class)){
		        		double d=(Double) returnValue;
		        		double d1= (Double) returnValue1;
		        		if(d!=d1) 
							continue;
		        		
		        		if ((""+d).equals("NaN")) 
		        			assertEqualsString.arguments().add(ast.newQualifiedName(ast.newSimpleName("Double"), ast.newSimpleName("NaN")));
		        		else if ((""+d).equals("NEGATIVE_INFINITY")) 
		        			assertEqualsString.arguments().add(ast.newQualifiedName(ast.newSimpleName("Double"), ast.newSimpleName("NEGATIVE_INFINITY")));
		        		else if ((""+d).equals("POSITIVE_INFINITY") ||(""+d).equalsIgnoreCase("INFINITY")) 
		        			assertEqualsString.arguments().add(ast.newQualifiedName(ast.newSimpleName("Double"), ast.newSimpleName("POSITIVE_INFINITY")));	
		        		else if ((""+d).equals("MIN_NORMAL")) 
		        			assertEqualsString.arguments().add(ast.newQualifiedName(ast.newSimpleName("Double"), ast.newSimpleName("MIN_NORMAL")));
		        		else if ((""+d).equals("MAX_VALUE")) 
		        			assertEqualsString.arguments().add(ast.newQualifiedName(ast.newSimpleName("Double"), ast.newSimpleName("MAX_VALUE")));
		        		else if ((""+d).equals("MIN_VALUE")) 
		        			assertEqualsString.arguments().add(ast.newQualifiedName(ast.newSimpleName("Double"), ast.newSimpleName("MIN_VALUE")));
		        		else{
		        			//if((""+d+"D").equals("NaND")){
		        				
		        			//}
		        			
		        			NumberLiteral numberLiteral=ast.newNumberLiteral(d+"D");
			        		assertEqualsString.arguments().add(numberLiteral);
		        		}
		        		
		        	}else if(m.getReturnType().equals(long.class)){
		        		long l=(Long)returnValue;
		        		long l1= (Long) returnValue1;
		        		if(l!=l1) 
							continue;
							
		        		NumberLiteral numberLiteral=ast.newNumberLiteral(""+l+"L");
		        		assertEqualsString.arguments().add(numberLiteral);
		        	}else if(m.getReturnType().equals(String.class)){
		        		String s1= (String) returnValue;
		        		String s2= (String) returnValue1;
		        		
		        		if(s1!=s2 ||  s1==null || s1.length()>1052 || s1.contains("@") ) 
							continue;
							
		        		StringLiteral stringLiteral=ast.newStringLiteral();
		        		stringLiteral.setLiteralValue(s1);
		        		assertEqualsString.arguments().add(stringLiteral);
		        		
		        	}else if(transitiveToString){ 
		        		
		        		Method subMethod=null;
		        		try {
							subMethod = m.getReturnType().getMethod("toString",null);
							returnValue1=subMethod.invoke(returnValue);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        		
		        		String st=(String) returnValue1;
		        		if(returnValue1==null  || st==null || st.length()>1052 || st.contains("@") )
		        		//if(returnValue1==null )
		        			continue;
		        		
		        		StringLiteral stringLiteral=ast.newStringLiteral();
		        		stringLiteral.setLiteralValue((String) returnValue1);
		        		assertEqualsString.arguments().add(stringLiteral);
		        		
		        		
		        	}else{ 
		        		int i1=(int)returnValue;
		        		int i2= (int) returnValue1;
		        		if(i1!=i1) 
							continue;
		        		
		        		NumberLiteral numberLiteral=ast.newNumberLiteral(""+returnValue);
		        		assertEqualsString.arguments().add(numberLiteral);
		        	}
					
			        MethodInvocation mm=ast.newMethodInvocation(); 
			        if(transitiveToString){
			        	MethodInvocation mm1=ast.newMethodInvocation();
			        	mm1.setName(ast.newSimpleName(m.getName()));
					    mm1.setExpression(ast.newSimpleName(cName));
					    mm.setName(ast.newSimpleName("toString"));
					    mm.setExpression(mm1);
					    //System.err.println("****createdMethod: "+mm.toString());
			        }else{
				        mm.setName(ast.newSimpleName(m.getName()));
				        mm.setExpression(ast.newSimpleName(cName));
			        }
			        
			        assertEqualsString.arguments().add(mm);
			        Statement statement1=ast.newExpressionStatement(assertEqualsString); 
			        
			        
			        //if(returnStringNew==returnString)
			        returnList.add(statement1);
			        break; //insert only one method
				}
			}
		}
	}
	
	private static void addToStringChecker(List<Statement>returnList, AST ast,Class cClass,Object cObject,String cName, Object checkObj){	
		Method classMethods[] =cClass.getMethods();
		Method toStringMeth=null;
		try {
			toStringMeth=cClass.getMethod("toString", null);
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		
//		int i=0; 
//		while((toStringMeth==null ) && i<classMethods.length){
//			if(classMethods[i].getName().equalsIgnoreCase("toString") && (classMethods[i].getParameterTypes().length==0))
//				toStringMeth=classMethods[i];
//			i++;
//		}
		
		if(toStringMeth==null || cObject==null)
			return;
		
		//get toString 
		String returnString=null;
		try {
				returnString = (String) toStringMeth.invoke(cObject);
				String returnString1 = (String) toStringMeth.invoke(checkObj);
				
				if(!returnString.equals(returnString1))
					return;
				
					
		}catch (IllegalAccessException e) {
			//e.printStackTrace();
			return;
		} catch (IllegalArgumentException e) {
			//e.printStackTrace();
			return;
		} catch (InvocationTargetException e) {
			//e.printStackTrace();
			return;
		}
		
			if((returnString!=null && returnString.length()<=1052) &&!returnString.contains("@") ){
				MethodInvocation assertEqualsString=ast.newMethodInvocation();
				assertEqualsString.setName(ast.newSimpleName("assertEquals"));
				
				StringLiteral strLiteral=ast.newStringLiteral();
				strLiteral.setLiteralValue(returnString);
		        assertEqualsString.arguments().add(strLiteral);  
		        
				
		        MethodInvocation toString=ast.newMethodInvocation(); 
		        toString.setName(ast.newSimpleName("toString"));
		        toString.setExpression(ast.newSimpleName(cName));
		        
		        assertEqualsString.arguments().add(toString);
		        Statement statement1=ast.newExpressionStatement(assertEqualsString); 
		        
		        //if(returnStringNew==returnString)
		        returnList.add(statement1);
			}
//		}
	}
		
	private static void addArrayChecker(List<Statement>returnList, AST ast,Class cClass,Object cObject,String cName){
//		Object cObject=returnObject;
//		String cName=returnObjectName;
//		Class cClass=currentWay.method.getReturnType();
		
		//currentWay.method.getReturnType().isArray() && 
		if(cClass.getComponentType().isPrimitive()||cClass.getComponentType().equals(String.class)){
			MethodInvocation assertArrayEquals=ast.newMethodInvocation();
			assertArrayEquals.setName(ast.newSimpleName("equals"));  //assertArrayEquals
			assertArrayEquals.setExpression(ast.newSimpleName("Arrays")); 
			
	        ArrayInitializer aInitializer=ast.newArrayInitializer();
	        //Object array[] =(Object[]) returnObject;
	        int length = Array.getLength(cObject);
	        for(int i=0;i<length;i++){ 
	        	if( cClass.getComponentType().equals(String.class)){
	        		String s= (String) Array.get(cObject, i);	
	        		
	        		if(s==null){
	        			aInitializer.expressions().add(ast.newNullLiteral());
	        		}else{
		        		StringLiteral stringLiteral=ast.newStringLiteral();
		        		stringLiteral.setLiteralValue(s);
		        		aInitializer.expressions().add(stringLiteral);
	        		}
	        	}else if( cClass.getComponentType().equals(char.class)){
	        		CharacterLiteral charLiteral=ast.newCharacterLiteral();
	        		//UnicodeBlock a=(Character.UnicodeBlock)Array.get(returnObject, i);
	        		char a= (Character) Array.get(cObject, i);	
	        		String unicode="\'"+String.format("\\u%04x", (int) a)+"\'";
	    			try{
	    				charLiteral.setEscapedValue(unicode); 
	    			}catch (IllegalArgumentException ie){
	    				return;
	    			}
	    			
	        		//charLiteral.setEscapedValue(unicode);
	        		
	        		aInitializer.expressions().add(charLiteral);
	        	}else if(cClass.getComponentType().equals(boolean.class)){
	        		BooleanLiteral boolLiteral=ast.newBooleanLiteral((Boolean)Array.get(cObject, i));
	        		aInitializer.expressions().add(boolLiteral);
	        	}else if(cClass.getComponentType().equals(float.class)){
	        		NumberLiteral numberLiteral=ast.newNumberLiteral(""+Array.get(cObject, i)+"F");
	        		aInitializer.expressions().add(numberLiteral);
	        	}else if(cClass.getComponentType().equals(double.class)){
	        		NumberLiteral numberLiteral=ast.newNumberLiteral(""+Array.get(cObject, i)+"D");
	        		aInitializer.expressions().add(numberLiteral);
	        	}else if(cClass.getComponentType().equals(long.class)){
	        		NumberLiteral numberLiteral=ast.newNumberLiteral(""+Array.get(cObject, i)+"L");
	        		aInitializer.expressions().add(numberLiteral);
	        	}else { 
	        		NumberLiteral numberLiteral=ast.newNumberLiteral(""+Array.get(cObject, i));
	        		aInitializer.expressions().add(numberLiteral);
	        	}
	        }  
	        
        	
	        ArrayCreation aCreation= ast.newArrayCreation();
	        Type TypeNameX1 =getType2UseInJunitClass(cClass,ast);
	        aCreation.setType((ArrayType) TypeNameX1);
	        aCreation.setInitializer(aInitializer);
	        assertArrayEquals.arguments().add(aCreation);
        	assertArrayEquals.arguments().add(ast.newSimpleName(cName));
        		
        	MethodInvocation assertTrue=ast.newMethodInvocation();
        	assertTrue.setName(ast.newSimpleName("assertTrue"));  //assertArrayEquals
        	assertTrue.arguments().add(assertArrayEquals);
			
        	Statement statement1=ast.newExpressionStatement(assertTrue); 
        	returnList.add(statement1);	
        	
        	Type TypeNameX =getType2UseInJunitClass(Arrays.class,ast);//add Arrays as required class
		}
		//TODO add an array checker for String
	}
	
	private static void addStringPrimitiveChecker(List<Statement>returnList, AST ast,Class cClass,Object cObject,String cName){
		//Object returnNew=null;
		boolean dontCreate=false;
		MethodInvocation assertEquals=ast.newMethodInvocation();
		assertEquals.setName(ast.newSimpleName("assertEquals"));

    	if(cClass.equals(char.class)){
    		{
    			CharacterLiteral charLiteral=ast.newCharacterLiteral();  
    			char a= (Character) cObject;		
        		String unicode="\'"+String.format("\\u%04x", (int) a)+"\'";
    			try{
    				charLiteral.setEscapedValue(unicode); 
    			}catch (IllegalArgumentException ie){
    				return;
    			}
    			assertEquals.arguments().add(charLiteral);
    			assertEquals.arguments().add(ast.newSimpleName(cName));
    		}
    		
    	}else if(cClass.equals(boolean.class)){
    		{
    			assertEquals.arguments().add(ast.newBooleanLiteral((Boolean) cObject));
    			assertEquals.arguments().add(ast.newSimpleName(cName));
    		}
    	}else {
    		if(cClass.equals(double.class)){
        		{
        			MethodInvocation compareFloat=ast.newMethodInvocation();
        			compareFloat.setName(ast.newSimpleName("compare"));  //assertArrayEquals
        			compareFloat.setExpression(ast.newSimpleName("Double"));
        			
        			compareFloat.arguments().add(ast.newNumberLiteral(""+ cObject.toString() +"D"));
        			compareFloat.arguments().add(ast.newSimpleName(cName));
        			
        			assertEquals.arguments().add(ast.newNumberLiteral("0"));
        			assertEquals.arguments().add(compareFloat);
        		}
        	}else if(cClass.equals(float.class)){
        		{
        			//Float.compare(f1, f2)
        			MethodInvocation compareFloat=ast.newMethodInvocation();
        			compareFloat.setName(ast.newSimpleName("compare"));  //assertArrayEquals
        			compareFloat.setExpression(ast.newSimpleName("Float"));
        			
        			compareFloat.arguments().add(ast.newNumberLiteral(""+ cObject.toString() +"F"));
        			compareFloat.arguments().add(ast.newSimpleName(cName));
        			
        			assertEquals.arguments().add(ast.newNumberLiteral("0"));
        			assertEquals.arguments().add(compareFloat);
        		}
        	}else if(cClass.equals(long.class)){
        		{
        			MethodInvocation compareLong=ast.newMethodInvocation();
        			compareLong.setName(ast.newSimpleName("compare"));  //assertArrayEquals
        			compareLong.setExpression(ast.newSimpleName("Long"));
        			
        			compareLong.arguments().add(ast.newNumberLiteral(""+ cObject.toString() +"L"));
        			compareLong.arguments().add(ast.newSimpleName(cName));
        			
        			assertEquals.arguments().add(ast.newNumberLiteral("0"));
        			assertEquals.arguments().add(compareLong); 
        		}
        	}else if(cClass.equals(int.class)){
        		{
        			assertEquals.arguments().add(ast.newNumberLiteral(""+ cObject.toString()));
        			assertEquals.arguments().add(ast.newSimpleName(cName));
        		}
        	}else if(cClass.equals(short.class)){
        		{
        			assertEquals.arguments().add(ast.newNumberLiteral(""+ cObject.toString()));
        			assertEquals.arguments().add(ast.newSimpleName(cName));
        		}
        	}else if(cClass.equals(byte.class)){
        		{
        			assertEquals.arguments().add(ast.newNumberLiteral(""+ cObject.toString()));
        			assertEquals.arguments().add(ast.newSimpleName(cName));
        		}
        	}else if(cClass.equals(String.class)){
        		String s1=(String)cObject.toString();
        		if(s1==null || s1.length()>1052 || s1.contains("@") )
        			dontCreate=true;
        		else{
	        		StringLiteral stringLiteral=ast.newStringLiteral();
	        		stringLiteral.setLiteralValue(s1);
	        		assertEquals.arguments().add(stringLiteral);
	        		assertEquals.arguments().add(ast.newSimpleName(cName));
        		}
        	}else
        		dontCreate=true;
    		
    		
    	}
		
    	if(!dontCreate){
        	Statement statement1=ast.newExpressionStatement(assertEquals); 
        	returnList.add(statement1);
    	}
	}

	
	private static void addStringChecker(List<Statement>returnList, AST ast,Class cClass,Object cObject,String cName, Object checkObj){			
		Method classMethods[] =cClass.getMethods();
		int i=0; 
		Vector<Method> checkerMethods=new Vector<Method>();
		Class clsString =String.class;
		while(i<classMethods.length){
			if( clsString.isAssignableFrom(classMethods[i].getReturnType())
					&& classMethods[i].getParameterTypes().length==0
					)//&& !classMethods[i].getName().equalsIgnoreCase("hashCode")
				checkerMethods.add( classMethods[i]);
			i++;
		}
		
		if(checkerMethods.size()<=0)
			return;
		
		Random rand1=new Random();
		int length = rand1.nextInt(checkerMethods.size())+1;
		if(length>3)
			 length=3;

		for(int j=0;j< checkerMethods.size() && length>0;j++, length--){
			Random rand2=new Random();
			int k = rand2.nextInt(checkerMethods.size()); 
			Method m=checkerMethods.get(k);
			checkerMethods.remove(k);
			
			if(m==null) // || m.getName().equalsIgnoreCase("hashCode")
				continue;

			String returnString;// = (String) m.invoke(cObject);
			String returnString1;// = (String) m.invoke(checkObj);
			
			//get toString ;
			try {
				 returnString = (String) m.invoke(cObject);
				 returnString1 = (String) m.invoke(checkObj);
						
			}catch (IllegalAccessException e) {
				//e.printStackTrace();
				return;
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
				return;
			} catch (InvocationTargetException e) {
				//e.printStackTrace();
				return;
			}
			
			if((returnString!=null && !returnString.equals(returnString1))||((returnString==null || returnString1==null)))
				return;
			
			if((returnString.length()<=1052) &&!returnString.contains("@") ){
				
				MethodInvocation assertEqualsString=ast.newMethodInvocation();
				assertEqualsString.setName(ast.newSimpleName("assertEquals"));
				
				StringLiteral strLiteral=ast.newStringLiteral();
				strLiteral.setLiteralValue(returnString);
		        assertEqualsString.arguments().add(strLiteral);  
		        
				
		        MethodInvocation toString=ast.newMethodInvocation(); 
		        toString.setName(ast.newSimpleName(m.getName()));
		        toString.setExpression(ast.newSimpleName(cName));
		        
		        assertEqualsString.arguments().add(toString);
		        Statement statement1=ast.newExpressionStatement(assertEqualsString); 
		        
		        //if(returnStringNew==returnString)
		        returnList.add(statement1);
			}
		}

	}

	
	@Override
	public Object clone() {
		MethodGenerator newCon=new MethodGenerator(parent,clazz, stub,recommandedWays);//(parent,clazz);isAnonymousGenerator
		
		newCon.clazz=this.clazz; //,stub,recommandedWays
		//newCon.method=this.method;
		//newCon.variableBinding=this.variableBinding;
		newCon.fitness=this.fitness;
		newCon.currentWay=currentWay;
		newCon.object=this.object;
		newCon.seed=this.seed;
		newCon.random=this.random;
		if(parameters!=null){
			newCon.parameters=new Vector<AbsractGenerator>();
			for(AbsractGenerator gene:parameters){ 
				if(gene!=null)
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
		newCon.returnObject=returnObject;
		newCon.returnObjectName=returnObjectName;
		newCon.returnStable=returnStable;
		newCon.classUTInstance=classUTInstance;
		
		if(externalConstructor!=null)
			newCon.externalConstructor=(InstanceGenerator)externalConstructor.clone();
		//newCon.
		return newCon;
	}
	
	public void execute( final Object obj,final Class clsUT){
		classUTInstance=obj;
		exceptions=new HashSet();
		if(this!=null)
			if(currentWay.method!=null){ 
				try {
					final Object nObj;
					if(obj.getClass().equals(clsUT) && clsUT.isAssignableFrom(obj.getClass()))
						nObj=clsUT.cast(obj);
					else
						nObj=obj;
					final Method methodUT=currentWay.method;//clsUT.getDeclaredMethod(method.getName(), method.getParameterTypes());
					try {
						AccessController.doPrivileged(new PrivilegedExceptionAction() {
						     public Object run() throws Exception {
						         if(!methodUT.isAccessible()) {
						        	 methodUT.setAccessible(true);
						         }
						         
						        	 //getExceptions().clear();
						        	 exceptions=new HashSet();
						        	 setUnexpectedException(null);
						        	 for(Class thro:methodUT.getExceptionTypes())
						        		 addExceptionClass(thro);
						        	 returnObject=null;
						        try{ 
						        	returnObject=methodUT.invoke(nObj, getParameters());
						        	//methodsOnReturnObject[0].
						         }catch (Throwable e){
						        	 setUnexpectedException(e);	
						         }
						       return null;
						     }
						 });
					} catch (PrivilegedActionException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}		
				} catch (IllegalArgumentException e) {
					//addException(e.getCause());
					//setUnexpectedException(IllegalArgumentException.class);
				} catch (SecurityException e) {
					//e.printStackTrace();
					//exception=e.getCause();
				} catch(Exception e){
					//setUnexpectedException(e.getCause().getClass());
					e.printStackTrace();
				}
			}
		if(returnObject!=null 
				&& parent==null 
				&& !currentWay.method.getReturnType().isPrimitive()
				&& !currentWay.method.getReturnType().equals(void.class) 
				&& isAccessible(currentWay.method.getReturnType())
				){
			
			generateSubSequence();
			executeSubSequence();
		}
	}

	
	public void execute(){
		if(this!=null)
			if(currentWay.method!=null){
				try {		
					final Class currentClass=currentWay.method.getDeclaringClass();
					final Method methodUT = currentClass.getDeclaredMethod(currentWay.method.getName(), currentWay.method.getParameterTypes());
					AccessController.doPrivileged(new PrivilegedExceptionAction() {
					     public Object run() throws Exception {
					         if(!methodUT.isAccessible()) {
					        	 methodUT.setAccessible(true);
					         }
					     
					    	  getExceptions().clear();
					        	 for(Class thro:methodUT.getExceptionTypes())
					        		 addExceptionClass(thro);
					        	 returnObject=null;
					      try{
					    	  returnObject= methodUT.invoke(currentClass, getParameters());
				         }catch (Throwable e){
				        	 setUnexpectedException(e);
					     }
					       return null;
					     }
					 });					
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}catch (PrivilegedActionException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}catch (IllegalArgumentException e) {
					//exceptions.add(e);
					//e.printStackTrace();
					//setUnexpectedException(IllegalArgumentException.class);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}catch(Exception e){
					//setUnexpectedException(e.getCause().getClass());
				}
			}
	}
	
	@Override
	public String toString(){
		String str=new String();
		if(currentWay.method==null)
			str="nothing";
		else
			str=currentWay.method.getName();
			if(parameters!=null)
				str+=parameters.toString();
			else
				str+="()";
		return str;
	}

	@Override
	public boolean isStatic() {
		// TODO Auto-generated method stub
		if(currentWay.method==null) return true;
		return Modifier.isStatic(currentWay.method.getModifiers());
	}

	
//	private static Object cloneObject(Object obj){
//        try{
//            Object clone = obj.getClass().newInstance();
//            for (Field field : obj.getClass().getDeclaredFields()) {
//                field.setAccessible(true);
//                if(field.get(obj) == null || Modifier.isFinal(field.getModifiers())){
//                    continue;
//                }
//                if(field.getType().isPrimitive() || field.getType().equals(String.class)
//                        || field.getType().getSuperclass().equals(Number.class)
//                        || field.getType().equals(Boolean.class)){
//                    field.set(clone, field.get(obj));
//                }else{
//                    Object childObj = field.get(obj);
//                    if(childObj == obj){
//                        field.set(clone, clone);
//                    }else{
//                        field.set(clone, cloneObject(field.get(obj)));
//                    }
//                }
//            }
//            return clone;
//        }catch(Exception e){
//            return null;
//        }
//    }
}
