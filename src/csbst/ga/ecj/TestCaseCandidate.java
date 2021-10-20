package csbst.ga.ecj;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;






//import org.apache.commons.math3.exception.NotPositiveException;
//import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import csbst.analysis.String2Expression;
import csbst.generators.AbsractGenerator;
import csbst.generators.dynamic.AbstractDynamicGenerator;
import csbst.generators.dynamic.ExternalMethodGenerator;
import csbst.generators.dynamic.InstanceGenerator;
import csbst.generators.dynamic.MethodGenerator;
import csbst.heuristic.RandomTesting;
import csbst.testing.ClassUnderTest;
import csbst.testing.DataMember;
import csbst.testing.JTE;
import csbst.testing.JUnitExecutor;
import csbst.testing.fitness.ApproachLevel;
import csbst.testing.fitness.NumberCoveredBranches;
import csbst.testing.fitness.TestingFitness;
import csbst.utils.FileEditor;
import csbst.utils.ExceptionsFormatter;
import csbst.utils.MethodExecutorBasedJUnit;
import csbst.utils.ObjectGeneratorBasedJUnit;
import csbst.utils.TimeOutMethodExecutor;
import ec.EvolutionState;
import ec.util.Parameter;
import ec.vector.VectorIndividual;

public class TestCaseCandidate extends VectorIndividual{
	protected Vector<AbsractGenerator> genes;
	protected Set<Integer> coveredBranches;
	private TestCaseCandidate parent;
	protected Random random;
	private boolean isTestData;
	//private Class clazzUT;
	ClassUnderTest classUnderTest;
	Class AnonymousType; 
	private boolean isPrototype;
	private boolean interrupted=false;
	public boolean isGoodTestData=true;
	public String MethodCodeBody;
	public List<Statement> statements;
	
	public TestCaseCandidate(){
		genes=new Vector<AbsractGenerator>();
		coveredBranches=new HashSet<Integer>();
		random=new Random();
		isPrototype=false;
	}
	
	public TestCaseCandidate(ClassUnderTest clazzUT){
		fitness=new NumberCoveredBranches();
		genes=new Vector<AbsractGenerator>();
		coveredBranches=new HashSet();
		this.classUnderTest=clazzUT;
	}

	public TestCaseCandidate(ClassUnderTest clazzUT, Class AnonymousType){
		this(clazzUT);
		this.AnonymousType=AnonymousType;
	}
	
	public TestCaseCandidate(ClassUnderTest clazzUT, boolean isPrototype){
		this(clazzUT);
		this.isPrototype=isPrototype;
	}
	
	public Class getAnonymousType(){
		return AnonymousType;
	}
	//Original
	public void generateRandom() {
//		//Random modification
//		if(classUnderTest.getClazz().isAnonymousClass()){ 
//			ClassUnderTest parentClassUnderTest=new ClassUnderTest(classUnderTest.getClazz().getEnclosingMethod().getDeclaringClass());
//			try {
//				parentClassUnderTest.prepareDataMembers();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			//classUnderTest.getClazz().getGenericSuperclass();
//			parent=new TestCaseCandidate(parentClassUnderTest,classUnderTest.getClazz().getSuperclass()); //.getGenericSuperclass()
//			parent.generateRandom();
//			
//			Vector<Method> tergatMethod=new Vector<Method>();
//			tergatMethod.add(classUnderTest.getClazz().getEnclosingMethod());
//			MethodGenerator method=new MethodGenerator(parent,parentClassUnderTest.getClazz(),((InstanceGenerator)parent.genes.get(0)).getStub(),tergatMethod,true);
//			method.generateRandom();
//			parent.genes.add(method);	
//		}
		
		
		Method[] methods;
		//add a gene constructor 
		final InstanceGenerator con;
		int length=0;
		//Random modification
		//System.out.print("2A");
		if(JTE.currentTarget.getconstructorsMayReach().size()>0 && classUnderTest.equals(JTE.currentClassUnderTest))
			con=new InstanceGenerator(null,classUnderTest.getClazz(),JTE.currentTarget.getconstructorsMayReach(),false);
		else
			con=new InstanceGenerator(null,classUnderTest.getClazz(),false);
		//System.out.print("2A");
		generateObjects(con);
		genes.add(con);
		
		//System.out.print("3A");
		
		if(classUnderTest.getDataMembers().size()>0){
			//add a gene for each data member
			for(csbst.testing.DataMember dm: classUnderTest.getDataMembers()){
				if(dm.getMethodTransformers().size()>0){
					
					//System.out.println("M-S************");
					MethodGenerator method=new MethodGenerator((AbsractGenerator)null,classUnderTest.getClazz(),con.getStub(),dm.getMethodTransformers());
					//System.out.println(method);	
					generateObjects(method);
					//System.out.println(method);
					genes.add(method); 
				}
			}
			length = classUnderTest.getDataMembers().size();
		}	
		//System.out.print("3B");
		Random rand=new Random();
		length += rand.nextInt(classUnderTest.getDataMembers().size()+3)+1;		//JTE.sequencesLength
		//add a gene for each data member 
		//Random modification for(int i=0;i< length;i++){//
		for(int i=classUnderTest.getDataMembers().size();i< length;i++){
			//System.err.println(classUnderTest);
			MethodGenerator method=new MethodGenerator((AbsractGenerator)null,classUnderTest.getClazz(),con.getStub(),null);
			generateObjects(method);//.generateRandom();
			genes.add(method);
		}
		
		//System.out.print("3C");
		//Random modification
		if(classUnderTest.equals(JTE.currentClassUnderTest)){
			MethodGenerator method=new MethodGenerator((AbsractGenerator)null,classUnderTest.getClazz(),con.getStub(),JTE.currentTarget.getMethodsMayReach());
			method.generateRandom();
			genes.add(method);
		}

	}

	
	public void generateRandomMaDuM() {
		//randomly select a slice
		//randomly generate a sequence
		//define a slice for each method under test
		
		//if the MUT in reporters, transformers, or others you must add transformers to the set of methods that will generate � sequence. 
		// and constructors
		// I think that each path has its own possible sequences !!! for now don't manage this is very complicated
		 
		//select a method that may reach the test target
		Random randMut=new Random(); 
		int mut =0;
		if(JTE.currentTarget.getMethodsMayReach().size()>1)
			mut = randMut.nextInt(JTE.currentTarget.getMethodsMayReach().size());
		
		Method methodUnderTest=null;
		if(JTE.currentTarget.getMethodsMayReach().size()>0)
			methodUnderTest=JTE.currentTarget.getMethodsMayReach().get(mut); 
		
		//create MaDuM for the selected method
		Vector<Constructor> sliceConstructors=new Vector<Constructor>();
		Vector<Method> sliceTransformers=new Vector<Method>();		
		Vector<Method> sliceReporters=new Vector<Method>(); //each method in the sequence must have a list of reporters.
		Vector<Method> allReporters=new Vector<Method>();
		
		//get Constructor Transformers
		//add a gene for each data member
		if(methodUnderTest!=null){
			
			for(csbst.testing.DataMember dm: classUnderTest.getDataMembers()){
				if(dm.getMethodOthers().size()+dm.getMethodTransformers().size()>0  //dm.getMethodReporters().size()+dm.getMethodTransformers().size()+
						&& (dm.getMethodOthers().contains(methodUnderTest)||dm.getMethodTransformers().contains(methodUnderTest)
								) ){
					
					sliceConstructors.addAll(dm.getConstructorTransformers());
					sliceTransformers.addAll(dm.getMethodTransformers());
				}
				if(dm.getMethodReporters().size()>0 && 
						(dm.getMethodOthers().contains(methodUnderTest)) )
					sliceReporters.addAll(dm.getMethodReporters());
				
				allReporters.addAll(dm.getMethodReporters());
			}
			if(sliceTransformers.contains(methodUnderTest))
				sliceTransformers.removeElement(methodUnderTest);
			
			if(sliceReporters.contains(methodUnderTest))
				sliceReporters.removeElement(methodUnderTest);
			
			if(allReporters.contains(methodUnderTest))
				allReporters.removeElement(methodUnderTest);
		}
		
		//add a gene constructor
		final InstanceGenerator con;
		if(sliceConstructors.size()>0 && classUnderTest.equals(JTE.currentClassUnderTest)){
			Random rand1=new Random();
			Vector<Constructor> cVec=new Vector<Constructor>();
			int orderCons = rand1.nextInt(sliceConstructors.size());
			cVec.add(sliceConstructors.get(orderCons));
			
			con=new InstanceGenerator(null,classUnderTest.getClazz(),cVec,false);
		}
		else
			con=new InstanceGenerator(null,classUnderTest.getClazz(),false);
		
		//con=new InstanceGenerator(null,classUnderTest.getClazz(),false);
		if(!isStatic()){
			generateObjects(con);
			
		}
		genes.add(con);
		
		//Transformers
		int nMethod =0;
		if(sliceTransformers.size()>0){
			Random rand1=new Random();
			nMethod = rand1.nextInt(classUnderTest.getDataMembers().size()+JTE.sequencesLength);
			for(int i=0;i<nMethod;i++){
				Random rand0=new Random();
				int orderMeth = rand0.nextInt(sliceTransformers.size());
				Vector<Method> mVec=new Vector<Method>();
				mVec.add(sliceTransformers.get(orderMeth));
				//updateReporters(sliceTransformers.get(orderMeth), sliceReporters);
				MethodGenerator method=new MethodGenerator((AbsractGenerator)null,classUnderTest.getClazz(),con.getStub(),mVec);	
				generateObjects(method);
				genes.add(method);  
					
					 
			}
			//length =nMethod;
		}
		

		//TargetViewfinder
		if(methodUnderTest!=null){
			Vector<Method> muts=new Vector<Method>();
			muts.add(methodUnderTest);
			MethodGenerator method=new MethodGenerator((AbsractGenerator)null,classUnderTest.getClazz(),con.getStub(),muts);
			generateObjects(method);//.generateRandom();
			genes.add(method);
		}else{
			
			Random rand=new Random();
			int length =nMethod+ rand.nextInt(JTE.sequencesLength)+1;		
			//add a gene for each data member
			//Random modification for(int i=0;i< length;i++){//
			for(int i=nMethod;i< length;i++){
				//System.err.println(classUnderTest);
				MethodGenerator method=new MethodGenerator((AbsractGenerator)null,classUnderTest.getClazz(),con.getStub(),null);
				generateObjects(method);//.generateRandom();
				genes.add(method);
			}
		}

		//Reporters
		Vector<Method> reporters;
		if(sliceReporters.size()>0)
			reporters=sliceReporters;
		else
			reporters=allReporters;
		
		if(methodUnderTest!=null && reporters.size()>0 && classUnderTest.equals(JTE.currentClassUnderTest)){
			Random rand1=new Random();
			 int length = rand1.nextInt(reporters.size())+1;
			 if(length>3)
				 length=3;
			 Set<Method> existM=new HashSet();
			//add a gene for each data member 
			//Random modification for(int i=0;i< length;i++){//
			for(int i=0;i< length;i++){
				Vector<Method> mVec=new Vector<Method>();
				Random rand2=new Random();
				int j = rand2.nextInt(reporters.size());
				if(!existM.contains(reporters.get(j))){ 
					existM.add(reporters.get(j));
					mVec.add(reporters.get(j));
					MethodGenerator method=new MethodGenerator((AbsractGenerator)null,classUnderTest.getClazz(),con.getStub(),mVec);
					method.generateRandom();
					genes.add(method); 
				} 
			}
		}
	}

	private void updateReporters(Method methodUnderTest, Vector<Method>  sliceReporters){
		for(csbst.testing.DataMember dm: classUnderTest.getDataMembers()){
			if(dm.getMethodReporters().size()>0 && (dm.getMethodTransformers().contains(methodUnderTest)) )
				sliceReporters.addAll(dm.getMethodReporters());
		}
	}
	
	//NoOrientation
	public void generateRandomNoOrientation() {
		Method[] methods;
		//add a gene constructor
		final InstanceGenerator con;
		//if(JTE.currentTarget.getconstructorsMayReach().size()>0)
		//	 con=new GeneConstructor(null,clazzUT,JTE.currentTarget.getconstructorsMayReach(),true); 
		//else
		con=new InstanceGenerator(null,classUnderTest.getClazz(),false);
		if(!isStatic())
			generateObjects(con);
		genes.add(con);
		
		Random rand=new Random();
		int length = rand.nextInt(10);
		//add a gene for each data member
		for(int i=0;i< length;i++){
			MethodGenerator method=new MethodGenerator(null,classUnderTest.getClazz(),con.getStub(),null);
			generateObjects(method);
			genes.add(method);
		}	


	}
	
	private AbsractGenerator generateObjects(final AbsractGenerator ge){
	  	ge.generateRandom();
		return ge;
	}

	private void executeMethod(final Object obj, final MethodGenerator meth){
		//TimeOutMethodExecutor.executeMethod(obj, meth);
	  	meth.execute(obj,meth.getClazz());
	}
	
	private void executeMethod(final MethodGenerator meth){
		//TimeOutMethodExecutor.executeMethod(null, meth);;
		meth.execute();
	}

	public void mutate() {
		double mutPb=1.0/genes.size();
		for(int x=0;x<genes.size();x++)
			if(random.nextDouble()<=mutPb)
	          genes.get(x).mutate();
	}


	@Override
    public void defaultCrossover(EvolutionState state, int thread, 
            VectorIndividual ind) { 
		//uniform crossover
		int index;
		Random rand=new Random();
		if(genes.size()==0)
			return;
		if(genes.size()==1)
			index=0;
		else{		
			index=rand.nextInt(genes.size());
		}

		//Gene local=genes.get(index);	
		if(!(genes.get(index).isSameFamillyAs(((TestCaseCandidate)ind).genes.get(index)))
				||rand.nextBoolean()){
			//change the hole of genes
			AbsractGenerator tmpGene=(AbsractGenerator) this.genes.get(index).clone();
			genes.set(index, ((TestCaseCandidate)ind).genes.get(index));
			((TestCaseCandidate)ind).genes.set(index, tmpGene);
			
		}else{
			genes.get(index).defaultCrossover(((TestCaseCandidate)ind).genes.get(index));
		}		
	}
            
            
	public TestingFitness getFitness() {
		return (TestingFitness) this.fitness;
	}
	  
	public Set<Integer> getCoveredBranches() {
		return coveredBranches;
	}
	
	public Vector<AbsractGenerator> getGenes() {
		return genes;
	}

	public void execute() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		//if(true)return;
		//save the test candidate
		if(JTE.ExceptionsOriented)
			writeCurrentTestCandidate("LastTestCandidate");
		
		coveredBranches=new HashSet<Integer>();
		Object objUT=null;
		
		if(!isStatic())
			if(parent!=null && classUnderTest.getClazz().isAnonymousClass()){
				parent.execute();
				objUT=((MethodGenerator)parent.genes.get(parent.genes.size()-1)).getReturnedObject();
			}else{
			//if(!isStatic()){
				InstanceGenerator con=(InstanceGenerator)genes.elementAt(0);
				objUT=con.getInstanceOnce();
			}
		
		//System.out.println(objUT);
		//}
		//if(objUT==null)
		//	return; 
        try {
    		
    		for(int i=1;i<genes.size();i++)	{
    			if(interrupted)
    				break;
    			if(((MethodGenerator)genes.elementAt(i)).isStatic()){
    				if(genes.elementAt(i) instanceof ExternalMethodGenerator)
    					//((GeneExternalMethod)genes.elementAt(i)).execute();
    					executeMethod((ExternalMethodGenerator)genes.elementAt(i));
    				else
    					//((GeneMethod)genes.elementAt(i)).execute();
    					executeMethod((MethodGenerator)genes.elementAt(i));
    			}
    			else
    				if(genes.elementAt(i) instanceof ExternalMethodGenerator)
    					//((GeneExternalMethod)genes.elementAt(i)).execute(null,null);
    					executeMethod(null,(ExternalMethodGenerator)genes.elementAt(i));
    				else if(objUT!=null) //TODO if the object is null drop all methods calls
    					//((GeneMethod)genes.elementAt(i)).execute(objUT,((GeneConstructor)genes.elementAt(0)).getClazz());
    					executeMethod(objUT,(MethodGenerator)genes.elementAt(i));
    		}
    		//TestingFitness.underEvaluationGene=null;
    		if(interrupted) {isTestData=false; return;} 
    		
    		if(this.isTestData()){
    			for(AbsractGenerator g:this.genes)
    				if(((AbstractDynamicGenerator)g).getUnexpectedException()!=null){ //!=null ExceptionsFormatter.printException(exce,\""+JTE.projectPackagesPrefix+"\",\""+JTE.className+"\");
    					isGoodTestData=false;
    					break;
    				}
    			if(interrupted) {isTestData=false; return;}
    			 /********** Critical Section *******************/
//    			queue.add(this);
//	    		 synchronized (lockObject) {	
    			if(JTE.ExceptionsOriented) 
    				isGoodTestData=!isGoodTestData;
    			
	    			if(isGoodTestData){
	    				for(int i:this.coveredBranches)
	    					if(!JTE.allCoveredBranchesOnce.contains(i))
	    						JTE.allCoveredBranchesOnce.add(i);
	    					else
	    						JTE.allCoveredBranchesTwice.add(i);
	    				
	    				JTE.testDataSet.add(this);//((Chromosome) this.clone());
	    				//RandomTesting.testCaseWriterQueue.add(this);
	    				//this.writeCurrentTestCandidate(JTE.TEST_CASES_SURFIX+(JTE.testDataSet.size()-1));
	    			}else{
	    				boolean containsAll=true;
	    				for(int i:this.coveredBranches)
	    					if(!JTE.allCoveredBranchesWithErrors.contains(i)){
	    						containsAll=false;
	    						break;
	    					}
	    				if(!containsAll){//(!JTE.allCoveredBranchesWithErrors.containsAll(this.coveredBranches)){
	    					JTE.allCoveredBranchesWithErrors.addAll(this.coveredBranches); 
	    					JTE.testDataSetWithErrors.add(this);//((Chromosome) this.clone());
	    				}
	    			}
	    		}
    		    /********** Critical Section *******************/
//    		}
		    
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        isTestData=false;
	}
	
	public MethodDeclaration generateTestCaseSourceCode(ASTNode node, String methodName){
		return generateTestCaseSourceCode( node,  methodName, false, true);
	}
	
	
	
	public MethodDeclaration generateTestCaseSourceCode(ASTNode node, String methodName,boolean lastOne, boolean generateAsserts){
		//if(methodName!=null){
			//create a new MethodDeclatation
		
			
			AST ast=node.getAST();
			MethodDeclaration method=ast.newMethodDeclaration();
			method = ast.newMethodDeclaration();
			
			Annotation annotation = ast.newMarkerAnnotation();//("Test");
			annotation.setTypeName(ast.newName("Test"));//(timeout=1000)
			

//			MemberValuePair mvp=ast.newMemberValuePair();
//			mvp.setName(ast.newSimpleName("timeout"));
//			mvp.setValue((Expression) ASTNode.copySubtree(ast, String2Expression.getExpression("30000")));
//			annotation.values().add(mvp);
			
			
			method.modifiers().add(annotation);
			
			method.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			method.setName(ast.newSimpleName(methodName));
			//method.thrownExceptions().add(ast.new)	
			method.setBody(ast.newBlock());
			
			//create the method java doc
			Javadoc jd=ast.newJavadoc();
			TextElement txtElt=ast.newTextElement();
			try{
				txtElt.setText(this.toString());
			}catch(IllegalArgumentException exec){
				txtElt.setText("Error while generating chromosome text "); 
			}
			
			TagElement tagElt=ast.newTagElement();
			tagElt.fragments().add(txtElt);
			
			jd.tags().add(tagElt);		
			method.setJavadoc(jd);
			method.thrownExceptions().add(ast.newSimpleName("Throwable"));
		//}

		method.getBody().statements().addAll(getStatements(ast, lastOne, generateAsserts));
		return method;
	}
	
	public List<Statement>getStatements(AST ast){
		return getStatements( ast,  false, true);
	}
	public List<Statement>getStatements(AST ast, boolean lastOne, boolean generateAsserts){
		//if(statements!=null)
		//	return statements;
		
		String objectName=""; 
		statements=new ArrayList <Statement>();
		if(!isStatic()){
			if(parent!=null && classUnderTest.getClazz().isAnonymousClass()){
				//objectName="clsUT"+classUnderTest.getClazz().getSimpleName();
				statements.addAll(parent.getStatements( ast,lastOne, generateAsserts));
				objectName=((MethodGenerator)parent.genes.get(parent.genes.size()-1)).getReturnedObjectName();
				
			}else{
				InstanceGenerator con=(InstanceGenerator) genes.get(0);//constructor
				objectName="clsUT"+con.getClazz().getSimpleName();
				statements.addAll(con.getStatements(ast,objectName,"P1"));
				//method.getBody().statements().addAll(statements);
			}
		}
		
		//Throwable except=((GeneActive)gene).getUnexpectedException();
		//method.thrownExceptions().add(ast.newSimpleName("Throwable"));
		for(int i=1;i<genes.size();i++){
			final AbsractGenerator gene=genes.get(i);	
			
			if(genes.get(0).getObject()==null && !Modifier.isStatic(((MethodGenerator)gene).getExecutionWay().getMethod().getModifiers())){
				continue;
			}
			
//			if(gene instanceof AbstractDynamicGenerator && lastOne)
//				 ((AbstractDynamicGenerator)gene).SystematicallySurroundCall=true;
			//try{
			//++++++++++++++++++++++++++++++++++++++++++++++++++++
			final AST  astp=ast;
			final String objectNamep=objectName;
			final int ip=i;
			final List <Statement> statementsp=new ArrayList <Statement>();
			//System.out.println(" Method call : "+ i);
			final boolean asserts=generateAsserts;
			Thread thread = new Thread(){ 
				@Override public void run (){
					if(gene  instanceof MethodGenerator)
						statementsp.addAll(((MethodGenerator)gene).getStatements(astp,objectNamep,"P"+(ip+1), asserts));
					else
						statementsp.addAll(gene.getStatements(astp,objectNamep,"P"+(ip+1)));
				}
			};
			thread.setDaemon(false);
			thread.start();
			try {
				thread.join(7500); //7500 RandomTesting.cMaxWaitingTime
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(thread.isAlive()){
				thread.interrupt();	
				if(thread.isAlive()){
					//Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
					thread.setPriority(Thread.MIN_PRIORITY);
					RandomTesting.killerQueue.add(thread);
					interrupted=true;
					if(!RandomTesting.killerThread.isAlive())
						RandomTesting.killerThread.start();
				}
				
			}
			//++++++++++++++++++++++++++++++++++++++++++++++++++++
			if(!interrupted){
				statements.addAll(statementsp);
			}else{
				;
				//System.out.println(" 	this method is ignored : "+ i);
			}
//			}catch(Exception e){
//				e.printStackTrace();
//			}
			
//			if(gene instanceof AbstractDynamicGenerator && lastOne)
//				 ((AbstractDynamicGenerator)gene).SystematicallySurroundCall=false;
		}
		
		return statements;
	}
	
	private boolean isStatic(){
		
		
		boolean allMethodsStatic=true;
		Constructor[] allDeclaredConstructors=JTE.currentClassUnderTest.getClazz().getDeclaredConstructors();
		for(Constructor c:allDeclaredConstructors){
			if(!Modifier.isStatic(c.getModifiers())) {//&& !isParent(c)				
				allMethodsStatic=false;
				break;
			}
		}
		
		Method[] allDeclaredMethods=JTE.currentClassUnderTest.getClazz().getDeclaredMethods();
		for(Method m:allDeclaredMethods){
			if(!Modifier.isStatic(m.getModifiers())) {//&& !isParent(c)
				
				allMethodsStatic=false;
				break;
			}
		}

		//if(allMethodsStatic)
		//	return true; 
		
		//if(genes.size()<=1)
		//	return false;
		
		//if(allMethodsStatic)
		//	return true;
		
		for(int i=1;i<genes.size();i++){
			AbsractGenerator gene=genes.get(i);
			if (!((MethodGenerator)gene).isStatic()){
				allMethodsStatic=false;
				break;
			}
		}
		
		return allMethodsStatic;
	}
	private void setBD(int bID, double branchDistance) {
		((ApproachLevel) fitness).setBD(bID,branchDistance);
	}
	
	@Override
	public Parameter defaultBase() {
		return null;
	}

	@Override
	public void reset(EvolutionState state, int thread) {
		generateRandom();		
	}

	
	@Override
	public boolean equals(Object other){
		boolean areEqual=(fitness.equals(((TestCaseCandidate)other).getFitness()));
		 
		int i=0;
		while (areEqual && i<genes.size()){
			areEqual=areEqual&&(genes.get(i).equals(((TestCaseCandidate)other).getGenes().get(i)));
		}
			
		return areEqual;
	}
	
	@Override
	public int hashCode() {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();

        hash = ( hash << 1 | hash >>> 31 );
        for(int x=0;x<genes.size();x++)
        	if(genes.get(x)!=null)
        		hash = ( hash << 1 | hash >>> 31 ) ^ genes.get(x).hashCode();

        return hash;
	}
	
	@Override
	public String toString(){
		String str=new String();
		str="Chromosome :\r";
		int order=1;
		if(genes.get(0).getObject()!=null){
			str+="1)----->" +genes.get(0).toString()+"\r";
			order=2;
		}
		
		for(int i=1;i<genes.size();i++){
			//if(((GeneMethod)genes.get(i)).getMethod()==null)
			//	continue;
			
			str+=order+")----->" +genes.get(i).toString();
			order++;
			if(i<genes.size()-1)
				str+=", \r";
		}
		str+="\r Covered Branches:"+coveredBranches;
		return str;
	}
	
	@Override
	public Object clone(){
		TestCaseCandidate newChrom=new TestCaseCandidate(classUnderTest);
		newChrom.random=this.random;
		newChrom.coveredBranches=new HashSet();
		newChrom.coveredBranches.addAll(this.coveredBranches);
		newChrom.isTestData=isTestData;
		newChrom.AnonymousType=AnonymousType;
		for(AbsractGenerator gene:genes)
			newChrom.getGenes().add((AbsractGenerator)gene.clone());
			
		return newChrom;
	}

	public boolean isTestData() {
		return isTestData;
	}

	public void setIsTestData(boolean isTestData) {
		this.isTestData = isTestData;
		//if()
	}

	public void setInterrupted() {
		interrupted=true;
		
	}
	
	public void writeCurrentTestCandidate(String surfix){
		//create a CompilationUnit
		ASTParser parser = ASTParser.newParser(AST.JLS3); 
		parser.setSource("".toCharArray()); //The parser is initialized with an empty array
		CompilationUnit unit = (CompilationUnit) parser.createAST(null); 
		unit.recordModifications();
		AST ast = unit.getAST();
				
		//create the package
		//create the package
		if(JTE.subPath.indexOf(File.separator)>0){
			PackageDeclaration pkgDec=ast.newPackageDeclaration();
			pkgDec.setName(AbsractGenerator.generateQualifiedName(JTE.subPath.replace(File.separator, "."),ast));
			unit.setPackage(pkgDec);
		}else if(!JTE.subPath.equals("")){
			PackageDeclaration pkgDec=ast.newPackageDeclaration();
			pkgDec.setName(ast.newSimpleName(JTE.subPath));
			unit.setPackage(pkgDec);
		}
	
		ImportDeclaration importDeclaration2 = ast.newImportDeclaration();
		QualifiedName name2=AbsractGenerator.generateQualifiedName("org.junit.Test",ast);
		importDeclaration2.setName(name2);
		unit.imports().add(importDeclaration2);
		
		ImportDeclaration importDeclaration3 = ast.newImportDeclaration();
		QualifiedName name3=AbsractGenerator.generateQualifiedName("org.junit.Rule",ast);
		importDeclaration3.setName(name3);
		unit.imports().add(importDeclaration3);
		
		ImportDeclaration importDeclaration1 = ast.newImportDeclaration();
//		//org.junit.Assert.*
		QualifiedName name1=AbsractGenerator.generateQualifiedName("org.junit.Assert",ast);
		importDeclaration1.setName(name1);
		importDeclaration1.setStatic(true);
		importDeclaration1.setOnDemand(true);
		unit.imports().add(importDeclaration1);
		
		if(JTE.ExceptionsOriented ){
			ImportDeclaration importDeclaration4 = ast.newImportDeclaration();
			QualifiedName name4=AbsractGenerator.generateQualifiedName("csbst.utils.ExceptionsFormatter",ast);
			importDeclaration4.setName(name4);
			unit.imports().add(importDeclaration4);
		}
		
		//

		//Create a new class
		TypeDeclaration clazzNode= ast.newTypeDeclaration(); 
		clazzNode.setInterface(false);
		clazzNode.setName(ast.newSimpleName(JTE.srcFileName+surfix));		
		clazzNode.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		//clazzNode.setSuperclassType(ast.newSimpleType(ast.newName("TestCase")));
		unit.types().add(clazzNode);
		
		//JTE.requiredClasses.clear();
		clazzNode.bodyDeclarations().add(this.generateTestCaseSourceCode(clazzNode,"TestCase",true, false));

		
		//import required classes
		Set<Class> rc=new HashSet();
		rc.addAll(JTE.requiredClasses);
		
		for(Class cls:rc){
			
			if(cls.getCanonicalName()==null 
					|| !cls.getCanonicalName().contains(".")
					|| cls.isPrimitive() 
					|| (cls.getPackage()!=null && cls.getPackage().getName().toString().equals("java.lang"))) 
				continue;// cls.getCanonicalName().toString().startsWith("java.lang.") ) continue;
			
			
			ImportDeclaration impDec = ast.newImportDeclaration();
			String binaryName=cls.getName();
			if(cls.isMemberClass())
				binaryName=cls.getDeclaringClass().getName();
			if(cls.isLocalClass())
				binaryName=cls.getEnclosingClass().getName();
			
			binaryName=binaryName.replace("$", ".");
			
			Name impName;
			if(binaryName.lastIndexOf(".")<0){
				impName=ast.newSimpleName(binaryName);
				//impDec.setName();
			}
			else{
				impName=AbsractGenerator.generateQualifiedName(binaryName,ast);
				
			}
			impDec.setName(impName);
			
			boolean exist=false;
			for(Object imp1:unit.imports()){
				if(((ImportDeclaration)imp1).getName().toString().equals(impName)){
					exist=true;
					break;
				}
			}
			if(exist)  continue;
			
			unit.imports().add(impDec);
		}
		//JTE.requiredClasses.clear();

		//CompilationUnit unit = getTestCasesSourceCode();
		String fileName=JTE.testCasesPath+File.separator+ JTE.subPath+File.separator+JTE.srcFileName+surfix+".java";
		String txtFile=unit.toString();
		txtFile=txtFile.replaceAll("throw exce;", "");
		FileEditor.unit2File(txtFile, fileName);

	}
}
