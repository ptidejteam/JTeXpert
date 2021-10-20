package csbst.generators.dynamic;


import csbst.ga.ecj.TestCaseCandidate;
import csbst.generators.AbsractGenerator;
import csbst.generators.CopyGenerator;
import csbst.testing.JTE;
import ec.util.RandomChoice;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;


public abstract class AbstractDynamicGenerator<T>  extends AbsractGenerator{
	//protected T method;
	public static Map<Class,Long>stub2Cost=new HashMap<Class,Long>();
	protected static Long MAX_COST=1000000L;
	protected static int INITIAL_COST=20;
	protected static int PENALITY_COST=200;
	protected static int MAX_GENERATION_NBR=2;  
	//Random modification
	protected static boolean selectRandom=false;
	protected int lastGenerationCost;
	
	protected Vector<AbsractGenerator> parameters;
	protected Vector<T> recommandedWays;
	protected Vector<ExecutionWay>possibleWays=new Vector<ExecutionWay>();
	public ExecutionWay currentWay;
	public ExecutionWay getExecutionWay(){return currentWay;}; 
	protected Set<Class> exceptions=new HashSet<Class>();
	protected static Set<Class> accessibleClasses=new HashSet<Class>();
	protected Throwable unexpectedException;
	
	protected InstanceGenerator externalConstructor;
	//protected Vector<Class> possiblesStubs=new Vector();
	//protected Class stub;
	protected int generationNbr=0;
	protected boolean withInstance;
	protected ParameterizedType parameterizedType;
	protected TestCaseCandidate testCaseCandidate;
	protected boolean isParameter=true;
//	public GeneActive(Gene p, Class clazz){ SystematicallySurroundCall
//		super(p,clazz);
//	}
		
	public AbstractDynamicGenerator(AbsractGenerator parent,Class clazz, Class stub,ParameterizedType parameterizedType, Vector<T> recommandedMethodes, boolean wi){		
		super(parent,clazz);
		this.recommandedWays=recommandedMethodes;
		this.parameterizedType=parameterizedType;
		currentWay=new ExecutionWay(ExecutionSource.isNull);
		parameters=new Vector<AbsractGenerator>();
		withInstance=wi;
		this.stub=stub;
		if(this.stub==null)
			this.stub=clazz;
		
		//clazz.
		//parameterizedType.
		//possiblesStubs.add(clazz);

		
		lastGenerationCost=0;
		generateInstantiationWays(clazz);
		selectInstantiationWay();

		//if(clazz.getSimpleName().toString().equals("DocIdSetIterator"))
		//	lastGenerationCost=0;
		if(!canGoOn()){
			currentWay=new ExecutionWay(ExecutionSource.isNull); 
			parameters=new Vector<AbsractGenerator>();
			return;
		}
		//generateRandom();
	}

	public AbstractDynamicGenerator(AbsractGenerator parent,Class clazz, Class stub,Vector<T> recommandedMethodes, boolean wi){
		this( parent, clazz, stub, null, recommandedMethodes,  wi);
	}
	
	public AbstractDynamicGenerator(AbsractGenerator parent,Class clazz, Vector<T> recommandedMethodes, boolean wi){
		this( parent, clazz, clazz,recommandedMethodes,  wi);
	}

	public AbstractDynamicGenerator(AbsractGenerator parent,Class clazz, Class stub, Vector<T> recommandedMethodes){
		this(parent,clazz,stub,recommandedMethodes,true);
	}

	public AbstractDynamicGenerator(AbsractGenerator parent, Class clazz){
		this(parent,clazz,clazz,new Vector<T>(),true);
	}
	
	public Class getStub(){
		return stub;
	}
	
	protected boolean canGoOn(){
		return true;
	}
	
	public Vector<AbsractGenerator> getChromosome(){
		return parameters;
	}
	
	protected abstract Vector<ExecutionWay> generateInstantiationWays(Class cls);
	
	public Object[] getParameters(){
		Object[] params=new Object[parameters.size()];
		for(int i=0;i<parameters.size();i++){
			if(parameters.get(i) instanceof CopyGenerator){
				//System.out.println(((GeneMethod)this).currentWay.method.getName());
				params[i]=((MethodGenerator)this).classUTInstance;
			}
			else
				params[i]=parameters.get(i).getObject();
		}
		return params;
	}
	
	protected void choseRandomlyStub(){
		Vector<Class> stubs =InstanceGenerator.generateStubs(this.getClazz());
		if(stubs.size()>0){
			Random rand=new Random();
			int index=rand.nextInt(stubs.size()+1);
			if(this  instanceof InstanceGenerator)
				if(index==stubs.size())
					stub=clazz;
				else
					stub=stubs.get(index);
			
		}
		//if(<SEEDING_NULL_PROBABILITY )
	}
	protected void selectInstantiationWay(){
		if(possibleWays.size()<1){
			currentWay=new ExecutionWay(ExecutionSource.isNull);
			parameters=new Vector<AbsractGenerator>();
			//Randomly select a stub
			if(this  instanceof InstanceGenerator)
				choseRandomlyStub();
			return;
		}
		
		currentWay=ExecutionWay.selectWay(possibleWays,this);
		if(this  instanceof InstanceGenerator)
			if(currentWay.source==ExecutionSource.constructor)
				stub=currentWay.cls;
			else
				stub=clazz;
		parameters=new Vector<AbsractGenerator>();
		
	}	
	
	protected void generateParameters(){
		boolean anyGeneNotInstiantiable=false ;
		parameters=new Vector<AbsractGenerator>();	
		Class[] methodParameters;
		
		if(currentWay.source==ExecutionSource.constructor){
			methodParameters=currentWay.constructor.getParameterTypes();
			//currentWay.constructor.
		}
		else{
			methodParameters=currentWay.method.getParameterTypes();
		}
		
		for(int i=0;i<methodParameters.length;i++){
			
				Class cls=methodParameters[i];
				AbsractGenerator gene;
				if((this instanceof MethodGenerator) && cls.isAssignableFrom(this.clazz) 
						&& (!((MethodGenerator)this).isStatic()) && random.nextInt(100)<SEEDING_MIN_PROBABILITY)
					gene=new CopyGenerator();
				else{	
					if(Collection.class.isAssignableFrom(cls)||Map.class.isAssignableFrom(cls))//(cls.equals(List.class)||cls.equals(Collection.class))
						if(currentWay.source==ExecutionSource.constructor)
							gene=createAdequateGene(this, cls, currentWay.constructor.getGenericParameterTypes()[i]);//
						else
							gene=createAdequateGene(this, cls, currentWay.method.getGenericParameterTypes()[i]);//
					else{
						
						gene=createAdequateGene(this,cls);
					}
					
					gene.generateRandom();
					if(gene.getObject()==null && gene instanceof InstanceGenerator 
							&& (((InstanceGenerator)gene).currentWay.source!=ExecutionSource.isNull))
						currentWay.cost+=PENALITY_COST;
					if(gene instanceof InstanceGenerator)
							currentWay.cost+=((InstanceGenerator)gene).lastGenerationCost; 
				}			
				parameters.add(gene);
				//System.out.println(" Par "+i+" value: "+gene.toString());
				
		}
	}
	
	public static boolean isAccessible(Class cls1){
		try{
			Class cls=cls1;
			
			if(cls.equals(JTE.currentClassUnderTest.getClazz()))
				return true;
			
			while(cls.isArray())
				cls=cls.getComponentType();
			
			if(cls.isPrimitive())
				return true;
			
			if(accessibleClasses.contains(cls))
				return true;
			
			boolean accessibility=Modifier.isPublic(cls.getModifiers()) ;
			if(!accessibility)
				accessibility=JTE.packageName.equals(cls.getPackage().getName()) 
									&& !Modifier.isPrivate(cls.getModifiers()) 
										&& !Modifier.isProtected(cls.getModifiers());
			
			if(!accessibility)
				return false;
			
			//the file exist in the class path!!!!
			//bad experience with the followin expression 
			accessibility=isInClassPath(cls);// && accessibility;
			
			if(accessibility && cls.getDeclaringClass()!=null)
				return isAccessible(cls.getDeclaringClass());
			if(accessibility)
				accessibleClasses.add(cls);
			return accessibility;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	static boolean isInClassPath(Class cls){
		//if(path.endsWith("/rt.jar")||path.endsWith("/classes.jar"))
		if(isInJavapath(cls))
			return true;
		
		String path=getResourcePath(cls);
		for(int i=0;i<JTE.classPath.length;i++){
			File f=new File(JTE.classPath[i]);
			if(path.equalsIgnoreCase(f.getAbsolutePath()))
					return true;
		}
		//System.err.println("Class inaccessible : "+cls);
		return false;

	}

	
	protected static boolean isInJavapath(Class clss){
		String rs =getResourcePath(clss);
		String javaHome=System.getProperty("java.home");
		File jh=new File(javaHome);
		jh=jh.getParentFile();
		return (getResourcePath(clss).startsWith(jh.getAbsolutePath()));
		//return clss.getPackage().getName().startsWith("java.");
	}	
	
//	private boolean inClasspath(Class cls){ 	
//		//System.out.println("java.home "+System.getProperty("java.home"));
//		//System.out.println("c rs "+getResourcePath(String.class));
//		for(String p:JTE.classPath){
//			File f=new File(p);
//			if(getResourcePath(cls).equalsIgnoreCase(f.getAbsolutePath()))
//				return true;
//		}
//		return false;
//	} 
//////les parameters d'une method d'instaciation doivent etre dans le path class!!!!
//System.out.println("class: "+cls);
//System.out.println("ressource: "+getResourcePath(cls));
////if(cls.getCanonicalName().equalsIgnoreCase("org.eclipse.core.resources.IFile")
////		|| cls.getCanonicalName().equalsIgnoreCase("org.eclipse.core.runtime.IProgressMonitor")){
//	
//	System.out.println("path: "+path);
//	System.out.println("absolut: "+f.getAbsolutePath());
////}	
	//les parameters d'une method d'instaciation doivent etre dans le path class!!!!
//	if(cls.getCanonicalName().equalsIgnoreCase("org.eclipse.core.resources.IFile")
//			|| cls.getCanonicalName().equalsIgnoreCase("org.eclipse.core.runtime.IProgressMonitor")){
//		System.out.println("ressource: "+getResourcePath(cls));
//		System.out.println("ressource: "+f.getAbsolutePath());
//	}
	
	public static String getResourcePath(Class clss){
		String resource='/'+clss.getName().replace('.', '/')+".class";
		URL location = clss.getResource(resource);
		
		String locstr=location.toString();
		int first=locstr.lastIndexOf(":")+1;
		int last =locstr.indexOf("!");
		if(last<=0)
			last =locstr.indexOf(resource);
		last=(last>0) ? last : locstr.length();	
		
		String path=locstr.substring(first, last);
		
		//System.out.println(path); .stratsWith(
		return path;
	}
	
	protected boolean isAccessible(Method method){
		Class declaringClass=method.getDeclaringClass();
		
		try{
			for(Class p: method.getParameterTypes()){
				if(p.isAnonymousClass()||!isAccessible(p)){
					//aParameterIsAnonymous=true;
					return false;
				}
		}
		
		return (isAccessible(declaringClass)
				&& !method.getName().toString().startsWith("access$")
				&&(!Modifier.isAbstract(method.getModifiers()))
				&&(!Modifier.isPrivate(method.getModifiers()))
				&&(!Modifier.isProtected(method.getModifiers()))
				&&((Modifier.isPublic(method.getModifiers()) ||JTE.packageName.equals(declaringClass.getPackage().getName())))
				);
		}catch(Exception e){
			
			e.printStackTrace();
			return false;
		}
	}

	protected boolean isAccessible(Constructor con){
		Class declaringClass=con.getDeclaringClass();
		
		//boolean aParameterIsAnonymous=false;
		for(Class p: con.getParameterTypes()){
			if(p.isAnonymousClass()||!isAccessible(p)){
				//aParameterIsAnonymous=true;
				return false;
			}
		}
		
		String pkg="";
		if(declaringClass.getPackage()!=null)
			pkg=declaringClass.getPackage().getName();
			
		return (
				(!Modifier.isProtected(con.getModifiers())
						&&!Modifier.isPrivate(con.getModifiers()))
						&& JTE.packageName.equals(pkg)
				        ||(Modifier.isPublic(con.getModifiers())&&isAccessible(declaringClass))
				);
	}

	@Override
	public int hashCode() {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();

        hash = ( hash << 1 | hash >>> 31 );
        if(parameters!=null)
        	for(int x=0;x<parameters.size();x++)
        		if(parameters.get(x)!=null)
        			hash = ( hash << 1 | hash >>> 31 ) ^ parameters.get(x).hashCode();

        return hash;
	}
	
	@Override
	public void generateRandom() {	
		//System.out.println(); 
		//System.out.print(this+"**x");
		if((currentWay.source==ExecutionSource.isNull)){
			parameters=new Vector<AbsractGenerator>();
			return;
		}
		//System.out.print("y");
		Random rand=new Random();
		if(rand.nextInt(100)<SEEDING_NULL_PROBABILITY && withInstance && isParameter){ //Only parameters can be null && !clazz.equals(JTE.currentClassUnderTest.getClazz())
			currentWay=new ExecutionWay(ExecutionSource.isNull);
			parameters=new Vector<AbsractGenerator>();
			return;
		}
		//System.out.print("z");
		if(currentWay.source==ExecutionSource.externalMethod){			
			 if(!Modifier.isStatic(currentWay.method.getModifiers())){
				 try{
					 //System.out.println("start:"+currentWay.method);
					 externalConstructor=new InstanceGenerator(this,currentWay.method.getDeclaringClass(),true);
					 externalConstructor.isParameter=false;
					 //System.out.println("*************************");
					 externalConstructor.generateRandom();
					 //System.out.println("stop:"+currentWay.method);
				 } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}		
		//System.out.print("w");
		if(currentWay.source==ExecutionSource.constructor 
				|| currentWay.source==ExecutionSource.factoryMethod
						|| currentWay.source==ExecutionSource.externalMethod)
			generateParameters();
		//System.out.println("**"+this);
	}

	@Override
	public void mutate() {
		//mutation probability
		//int muProp=random.nextInt(100);
		int parNbr=0;
		if(parameters!=null)
			parNbr=parameters.size();
		double mutPb=1.0/(1.0+parNbr);

		if(random.nextDouble()<=mutPb){
			lastGenerationCost=0;
			generateRandom();
			return;
		}
		if(parameters!=null){			
			for(AbsractGenerator gene :parameters)
				if(random.nextDouble()<=mutPb)
					gene.mutate();
		}
	}
	
	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false;
		if(this instanceof MethodGenerator)
			returnValue=(gene instanceof MethodGenerator);
		
		if(this instanceof InstanceGenerator)
			returnValue=(gene instanceof InstanceGenerator);
				
		returnValue=returnValue &&(currentWay.source==((AbstractDynamicGenerator)gene).currentWay.source);
		returnValue=returnValue &&(currentWay.constructor==((AbstractDynamicGenerator)gene).currentWay.constructor);
		returnValue=returnValue &&(currentWay.field==((AbstractDynamicGenerator)gene).currentWay.field);
		returnValue=returnValue &&(currentWay.method==((AbstractDynamicGenerator)gene).currentWay.method);		
		return returnValue;
	}
	
	@Override
    public void defaultCrossover(AbsractGenerator gene) { 
		//uniform crossover
		Random rand=new Random();
		if(parameters==null || this.parameters.size()==0
				|| ((AbstractDynamicGenerator)gene).parameters==null || ((AbstractDynamicGenerator)gene).parameters.size()==0)
			return;
		
		int len=(parameters.size()<=((AbstractDynamicGenerator)gene).parameters.size()?parameters.size():((AbstractDynamicGenerator)gene).parameters.size());
		int index;
		if(len==1)
			index=0;
		else
			index=rand.nextInt(len);
		
		if((!(parameters.get(index).isSameFamillyAs((AbsractGenerator) ((AbstractDynamicGenerator)gene).parameters.get(index)))
				||rand.nextBoolean())){
			AbsractGenerator tmpGene=(AbsractGenerator) this.parameters.get(index).clone();
			parameters.set(index, (AbsractGenerator) ((AbstractDynamicGenerator)gene).parameters.get(index));
			((AbstractDynamicGenerator)gene).parameters.set(index, tmpGene);
		}else{
			parameters.get(index).defaultCrossover((AbsractGenerator) ((AbstractDynamicGenerator)gene).parameters.get(index));
		}
		
	}
	
	public abstract  boolean isStatic();

	public Set<Class> getExceptions() {
		return exceptions;
	}
	
	public void addExceptionClass(Class exception){
		if(!isAccessible(exception))
			return;			
		for(Class e:exceptions){
			if(exception.isAssignableFrom(e)){
				e=exception;
				return;
			}
			if(e.isAssignableFrom(exception))
				return;
		}
		exceptions.add(exception);
	}

	public Throwable getUnexpectedException() {
		return unexpectedException;
	}
//
	public void setUnexpectedException(Throwable ue) {
		this.unexpectedException =ue;
		if(ue==null)
			return;
		
		this.unexpectedException = ue.getCause();
		//if(unexpectedException!=null)
		//	 addExceptionClass(unexpectedException.getClass());

	}

	protected static boolean isParent(Class cls, InstanceGenerator  gc){
		AbsractGenerator currentParent=gc.parent;
		while (currentParent!=null && !(currentParent instanceof MethodGenerator)){
			if(currentParent instanceof InstanceGenerator){
				if(((InstanceGenerator)currentParent).clazz.equals(cls))
					return true;
				if((((InstanceGenerator)currentParent).getStub()!=null)&&((InstanceGenerator)currentParent).getStub().equals(cls))
					return true;
			}
			currentParent=currentParent.getParent();
		}
		return false;
	}
	
	private static boolean isParent(Method md, InstanceGenerator gc){
		Class dc=md.getDeclaringClass();
		if(isParent(dc,gc)|| gc.clazz.equals(dc)|| gc.getStub().equals(dc))
			return true;
		Class [] methodParameters=md.getParameterTypes();
		for(Class p:methodParameters)
			if(isParent(p,gc)|| gc.clazz.equals(p)|| gc.getStub().equals(p))
				return true;

		return false;
	}

	private static boolean isParent(Constructor con, InstanceGenerator gc){
		Class [] methodParameters=con.getParameterTypes();
		for(Class p:methodParameters)
			if(isParent(p,gc)||gc.clazz.equals(p)||gc.getStub().equals(p))
				return true;

		return false;
	}
	
	public static boolean isParent(ExecutionWay iw,InstanceGenerator gc) {
		switch (iw.source){
		case constructor:
			return isParent(iw.constructor,gc);
		case externalMethod:
			return isParent(iw.method,gc);
		}
		return false;
	}	
	
	protected enum ExecutionSource{
		newInstance,
		constructor,
		factoryMethod,
		externalMethod,
		dataMember,
		//isStatic,
		isNull
	}
	//cost, instanceSource , method, constructor, field
	public static class ExecutionWay{
		int level=0;
		int cost;
		int initialCost;
		ExecutionSource source;
		Constructor constructor;
		Method method;
		public Method getMethod(){return method;};
		Field field;
		Class cls;
		
		ExecutionWay(Class cls){
			source=ExecutionSource.newInstance;
			cost=0;
			cost+=INITIAL_COST;
			this.cls=cls;
		}
		
		ExecutionWay(Field field){
			this.field=field;
			source=ExecutionSource.dataMember;
			cost=0;
			cost+=INITIAL_COST;
			cls=field.getDeclaringClass();
		}

		ExecutionWay(Constructor constructor){
			this(constructor,false);
			//cls=constructor.getDeclaringClass();
		}
		
		ExecutionWay(Constructor constructor,boolean withoutInitialCost){
			this.constructor=constructor;
			source=ExecutionSource.constructor;
			cost=constructor.getParameterTypes().length*INITIAL_COST;
			if(!withoutInitialCost){
				cost+=INITIAL_COST;
				initialCost=INITIAL_COST;
			}
			cls=constructor.getDeclaringClass();
		}
		
		ExecutionWay(Method method){
			this(method,INITIAL_COST);
		}

		
		ExecutionWay(Method method,int InitialCost){
			this.method=method;
			source=ExecutionSource.factoryMethod;
			cost=method.getParameterTypes().length*INITIAL_COST;
			//if(!withoutInitialCost)
			cost+=InitialCost;
			initialCost=InitialCost;
			cls=method.getDeclaringClass();
		}
		
		ExecutionWay(Method method,Class clss){
			//this.constructor=constructor;
			this.method=method;
			source=ExecutionSource.externalMethod;
			cost=(method.getParameterTypes().length+1)*INITIAL_COST;
			cost+=INITIAL_COST;
			cls=method.getDeclaringClass();
		}
		
		public static int getInitialCost(ExecutionWay iw){
			int theCost=iw.initialCost;
			switch (iw.source){
			case constructor:
				theCost+=iw.constructor.getParameterTypes().length*INITIAL_COST;
				break;
			case factoryMethod:
			case externalMethod:
				theCost+=(iw.method.getParameterTypes().length+1)*INITIAL_COST;
				break;
			case dataMember:
			case isNull:
				theCost=0;
			}
			return theCost;
		}
		
		public ExecutionWay(ExecutionWay iw) {	
			this.constructor=iw.constructor;
			this.cost=iw.cost;
			this.field=iw.field;
			this.method=iw.method;
			this.source=iw.source;
			cls=iw.cls;
		}

		public ExecutionWay(ExecutionSource isstatic) {
			this.source=isstatic;
			cls=null;
		}

		//, Class stb 
		 static ExecutionWay selectWay(Vector<ExecutionWay> allWays, AbstractDynamicGenerator gc){
			 if(selectRandom){
				 Random rand=new Random();
				 int index=rand.nextInt(allWays.size());
				 return allWays.get(index);
			 }
			 
//			InstantiationWay bestWay=null;
			double probabilities[]=new double[allWays.size()];
			long minCost=0;
			for(int i=0;i<allWays.size();i++){
				Long stubCost=null;
				if(gc instanceof InstanceGenerator)
					stubCost=((InstanceGenerator)gc).stub2Cost.get(allWays.get(i));
				long wayCost=0;
				if(stubCost!=null)
					wayCost=stubCost;
				wayCost+=allWays.get(i).cost;
				if(minCost>wayCost)
					minCost=wayCost;
				if(gc instanceof InstanceGenerator)
					if(isParent(allWays.get(i),((InstanceGenerator)gc))) wayCost+=MAX_COST;
				if(wayCost<0)
					wayCost=MAX_COST;
				probabilities[i]=1.0/(INITIAL_COST+wayCost);
				if(probabilities[i]<0)
					probabilities[i]=1.0/MAX_COST;
				//if(probabilities[i]<0)
				//	probabilities[i]=0;
			}
			if(gc instanceof InstanceGenerator)
				((InstanceGenerator)gc).stub2Cost.put(((InstanceGenerator)gc).getStub(),minCost);
			
			 // organize the distribution.  All zeros in fitness is fine
			 RandomChoice.organizeDistribution(probabilities, true);
			 Random rand=new Random();
			 int index=RandomChoice.pickFromDistribution(probabilities,rand.nextDouble());//rand.nextInt(allWays.size());
			 //index=indices[index];
			 allWays.get(index).toString();
			 return allWays.get(index);
		}

		public String toString(){
			String str=new String();
			switch (source){
			case constructor:
				str=getResourcePath(constructor.getDeclaringClass())+" * "+constructor.getName();
				break;
			case factoryMethod:
			case externalMethod:
				str=method.getName();
				break;
			case dataMember:
				str=field.getName();
			//case isStatic:
			//	str="static";
			case isNull:
				str="null";
			}
			str+="[cost="+cost+"]";
			return str;
			
		}

	}

}
