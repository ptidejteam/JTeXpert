package csbst.testing;	

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormat;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

//import sbst.runtool.ReadStream;
























import com.google.common.io.Files;

import csbst.analysis.ASTBuilder;
import csbst.analysis.BranchesCoder;
import csbst.analysis.BranchesProperties;
import csbst.analysis.CUTAnalyser;
import csbst.analysis.DataMemberUseAnalyser;
import csbst.analysis.InfluenceAnalyser;
import csbst.analysis.Instrumentor;
import csbst.analysis.LittralConstantAnalyser;
import csbst.analysis.MethodCallsAnalyser;
import csbst.analysis.String2Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import csbst.ga.ecj.TestCaseCandidate;
import csbst.generators.AbsractGenerator;
import csbst.generators.dynamic.AbstractDynamicGenerator;
import csbst.generators.dynamic.InstanceGenerator;
import csbst.heuristic.RandomTesting;
import csbst.utils.FileEditor;
import csbst.utils.ClassLoaderUtil;
import csbst.utils.LoggingOutputStream;
import csbst.utils.StdOutErrLevel;


//import java.util.concurrent.ConcurrentSkipListSet;;

public class JTE{
		private JTE(){	
		}
		
		private static CompilationUnit compilationUnit;
		public static String className;
		public static String packageName="";
		public static String projectPackagesPrefix;
		public static String subPath;
		public static String testCasesPath;
		public static  String srcPath;
		public static  String srcPath0;
		public static String binPath;
		public static String[] classPath;
		private static String classPath0=""; 
		public static String srcFileName;
		private static String gaParametersFile;
		private static String interfacesFile;
		public static String jteOutputPath;
		private static String javaFileName;
		private static String originalFileName;
		private static String javaDirectoryName;
		public static String projectPath;
		public static String binaryFile;
		//public static String commonsLangPath;
		public static List<String> libPaths=new ArrayList<String>();
		
		public static int seed=0;
		private static boolean printProgress=false;
		private static boolean showErrors=false;
		public static  boolean ExceptionsOriented=false;
		//public static  boolean ExceptionsAnalysis=true;
		private static boolean overrideExistTestCase=false;
		private static boolean instrument=true;	
		private static boolean firstExcution=true;
		//private static boolean hardTimeLimit=false;
		private static String instrumentedClassPath="";

		public static  boolean writeTestCasesIsDone=false;
		private static  boolean cutCallsSystemExist=false;
		
		private static BranchesCoder branchCoderAnalyser;
		public static DataMemberUseAnalyser dataMemberUseAnalyser;
		private static MethodCallsAnalyser methodCallsAnalyser;
		private static InfluenceAnalyser influenceAnalyser;
		public static LittralConstantAnalyser litteralConstantAnalyser;
		
		public static Set<Integer> branchesTarget=new TreeSet<Integer>();
		public static Set<Integer>  allCoveredBranchesOnce=new HashSet<Integer>();
		public static Set<Integer>  allCoveredBranchesTwice=new HashSet<Integer>();
		public static Set<Integer> allCoveredBranchesWithErrors=new HashSet<Integer>();
		public static Set<Integer> allNotCoveredBranches=new HashSet<Integer>();
		private static  boolean timeWasting=false;
		private static  long lastTimeBranchCovered=0;
		private static  int lastSizetestDataSetWithErrors=0;
		private static int lastSaveNumberOfNCB=0;
		private static int maxEvaluations=0;
		private static long maxTime=0;
		private static long maxWaitingTime=40; //second
		//private static int maxExecutionTime=0;
		private static int totalEvaluations;
		private static int geneartionNumber=0;
		private static int nmberOfStuckThreads=0;
		private static int remaindEvaluations=0;
		private static Set<Integer>tmpUncoveredTarget;
		private static long totalTime;
		private static long totalSearchTime;
		private static long ActualstartTime;
		private static boolean timeAlreadyDetermined=false;
		private static boolean automaticTimeDetermination=false;
		
		private static boolean javaFilePerTestCase=false;

		
		public static ClassUnderTest currentClassUnderTest;
		public static Class mainClassUnderTest;
		public static Target currentTarget;
		private static Path currentPathTarget;
		
		public static ClassLoader magicClassLoader;//JTE
		//private static Class classUnderTest;
		
		private static Map<String,ClassUnderTest> classesUnderTest=new HashMap<String,ClassUnderTest>();		
		public static Set<TestCaseCandidate> testDataSet=new HashSet<TestCaseCandidate>();
		public static Set<TestCaseCandidate> testDataSetWithErrors=new HashSet<TestCaseCandidate>();
		private static Map<MethodDeclaration,Method>astMethod2ReflexionMethod=new HashMap<MethodDeclaration,Method>();
		private static Map<MethodDeclaration,Constructor> astMethod2ReflexionConstructor=new HashMap<MethodDeclaration,Constructor>(); 
		public static Set<Class>requiredClasses=new HashSet<Class>();

		//public static final String INSTRUMENTATION_SURFIX="JTEInst";
		public static final String TEST_CASES_SURFIX="JTETestCases";
		public static final boolean TESTCASES_SLICINIG=true;
		
		public static PrintStream stdout;
		public static PrintStream stderr;
		
		public static List<URL> classPathList= new ArrayList<URL>();
		
		public static String heuristicName="RA";
		public static int minEvaluationPerBranch=200;
		public static int maxRandomEvaluationsFitness=5;
		public static int maxThreadNumber=100;
		public static long startTimeNano;
		public static long startTime;
		public static int sequencesLength=3;
		public static long cMaxWaitingTime=7500;
		
		private static boolean Initialize() throws Exception{			
			System.out.println();
			System.out.println("Unit Under Test: "+ className);
//			ShutdownInterceptor shutdownInterceptor = new ShutdownInterceptor();
//			Runtime.getRuntime().addShutdownHook(shutdownInterceptor);
			
			loadClassPath();
			//stdout.println("Instrumenting the Java unit: ");
			createInstrumentedCUD();
			
			//compile the instrumented version
			//stdout.println("Compiling the instrumented version: "+binPath);
			//stdout.println("Compiling the instrumented version: "+subPath);
			List<String> pathList=new ArrayList<String>(); 
			URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();	
			pathList.add(binPath+File.separator);
			for (URL url : urlClassLoader.getURLs()){
				pathList.add(new File(url.getFile()).getAbsolutePath());
			}
			//File.separator+className+File.separator
			if(instrument){
				boolean compilation=CUTAnalyser.compileJUnitFile( srcPath+File.separator+subPath+File.separator+srcFileName+".java",  binPath,pathList);
				//if(!compileICUD())
				if(!compilation)
					return false;
			}

			//Pares the source code with different analyzers.
			//stdout.println("Analysing the source code: ");
			AnalyseCU();

			//instantiate the instrumented version
			//stdout.println("Instantiating  the main class: ");
			InstantiateICUD(className);
			
			String sourceOFCUT=AbstractDynamicGenerator.getResourcePath(mainClassUnderTest);
			binPath=binPath.replace("//", "/");
			
			if(!sourceOFCUT.endsWith(binPath) ){
				if(firstExcution){			
					String cmdTxt="";
					//
					File f=new File(binPath);
					if(binaryFile.endsWith(".jar"))
						 cmdTxt= "java  -classpath " + f.getAbsolutePath() + "/ -jar "+ binaryFile ; //-cp " + binPath + "/ -cp " + binPath + "
					else{
						cmdTxt= "java -classpath " + f.getAbsolutePath() + "/:"+ binaryFile +" GenerateTestData " ; //+ binPath + "/:" 
					}
					
					
					//System.err.println(originalFileName);
					long newMaxTime=maxTime;
					if(newMaxTime<0)
						newMaxTime=0;
					maxTime*=1.75;
					cmdTxt+=" -cp "+ binPath + "/"+File.pathSeparator+classPath0
					 + " -icp " + binPath + "/"+File.separator +" -cname " + className
					 + " -jf "+ originalFileName  + " -tp "+ jteOutputPath  +" -tc "+testCasesPath  
					 +" -maxTime " + newMaxTime
					 + " -fE "
					 + " -i "; //dont instrument. this is very important to test JTExpert librairies!!!???
					
					if(automaticTimeDetermination)
						cmdTxt+=" -aTD ";
					if(printProgress)
						cmdTxt+=" -p ";
					if(showErrors)
						cmdTxt+=" -s ";
					if(overrideExistTestCase)
						cmdTxt+=" -o ";
					if(ExceptionsOriented)
						cmdTxt+=" -E ";
					
					//System.err.println(cmdTxt);
					Process proc = Runtime.getRuntime().exec(cmdTxt);
					ReadStream s1 = new ReadStream("stdin", proc.getInputStream ());
					ReadStream s2 = new ReadStream("stderr", proc.getErrorStream ());
					s1.start ();
					s2.start ();
					
					int exitVal1 = proc.waitFor();
				}else{
					System.err.println("Class:"+sourceOFCUT);
					System.err.println("binPath:"+binPath);
					System.err.println(" Error : JTExpert cannot generate the test data for this Java file. This file is part of JTExpert !!! ");
				}
				SystemExitControl.enableSystemExitCall();
				System.exit(5555);
			}
			
			//loadCommonsLang();
			
			//read the file that contains interfaces and abstract class mapping to stubs
			if(interfacesFile!=null && !interfacesFile.equals("")){
				BufferedReader buff= new BufferedReader(new FileReader(interfacesFile));
		        String tmp_line = buff.readLine();
		        while (tmp_line!=null){
		        	String[]inter2class = tmp_line.split(";");
		        	Class clss1=magicClassLoader.loadClass(inter2class[0]);
		        	Vector<Class> lst1=InstanceGenerator.interface2Implementation.get(clss1);
		        	if(lst1!=null)
		        		lst1.add(magicClassLoader.loadClass(inter2class[1]));
		        	else{
		        		lst1=new Vector<Class>();
		        		lst1.add(magicClassLoader.loadClass(inter2class[1]));
		        		InstanceGenerator.interface2Implementation.put(clss1, lst1);
		        	}
		        	tmp_line = buff.readLine();
		        }
			}
			
			//classesUnderTest=new HashMap<Class,ClassUnderTest>();		
			//testDataSet=new HashSet<Chromosome>();
			//testDataSetWithErrors=new HashSet<Chromosome>();
			astMethod2ReflexionMethod=new HashMap<MethodDeclaration,Method>();
			astMethod2ReflexionConstructor=new HashMap<MethodDeclaration,Constructor>(); 
			
			//Gene.defaultClassesSet=new Vector<Class>();
			//GeneActive.stub2Cost=new HashMap<Class,Integer>();
			//GeneConstructor.class2InstantiationWays=new HashMap<Class,Vector<ExecutionWay>>();
			//GeneConstructor.requiredClasses=new HashSet<Class>();
			
			return true;
		}
		
		private static void setAllBranchesTarget(){
			//generate branches target
			branchesTarget.clear();
			for(int i=1;i<=branchCoderAnalyser.getLastBranch();i++ )
				branchesTarget.add(i);
		}
		
		private static void createInstrumentedCUD() throws Exception{
			//
			litteralConstantAnalyser=new LittralConstantAnalyser();
			compilationUnit.accept(litteralConstantAnalyser);
			
			//call the branchcoder visitor to identify class branches
			branchCoderAnalyser=new BranchesCoder();
			compilationUnit.accept(branchCoderAnalyser);			
			//call the instrumentor (instrumentor must be called after the BranchCoder)
			Instrumentor instrumentor=new Instrumentor();
			compilationUnit.accept(instrumentor);	
			
			
			//create the instrumented java file
			//backupSourceCode();
			FileEditor.unit2File(compilationUnit.toString(),srcPath+File.separator+subPath+File.separator+srcFileName+".java");
		}
		
		private static boolean compileICUD() throws IOException{
//			File dotclass =new File(binPath+File.separator+subPath+File.separator+srcFileName+".class");
			//if(dotclass.exists())
			//	return true;
			
//			//create the directory bin
			File instrumentedClass = new File(binPath+File.separator+subPath);
			instrumentedClass.getParentFile().mkdirs();
//			//Compile the instrumented version
			
			javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,
			                                                                         Locale.ENGLISH,
			                                                                         null); //""iso-8859-1" utf-8 Charset.forName("iso-8859-1")
//			//set classpath entries -dontwarn
			List<File> pathList=new ArrayList<File>(); 
//			for(String p:classPath)
//				pathList.add(new File(p));		
			//com.simontuffs.onejar.JarClassLoader c;
			//System.err.println (f);
			
			URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();	
			for (URL url : urlClassLoader.getURLs()){
				pathList.add(new File(url.getFile()));
				//System.err.println(url);
			}
					
			try {
				fileManager.setLocation(StandardLocation.CLASS_PATH, pathList);
				fileManager.setLocation(StandardLocation.CLASS_OUTPUT,Collections.singleton(new File(binPath)));
			} catch (IOException e) {
				e.printStackTrace();
			}
//			catch(Throwable t){
//				
//			}

			File[] files;
			files=new File[]{new File(srcPath+File.separator+subPath+File.separator+srcFileName+".java")};//javaFileName
				
			Iterable fileObjects = fileManager.getJavaFileObjects(files);
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null/*options*/, null, fileObjects);
			
			
			if(!task.call()){
				System.err.println ("Compilation' errors in the instrumented file: ");
				 //stderr.println (task.toString());
				 for (Diagnostic diagnostic : diagnostics.getDiagnostics())
					 System.err.println(diagnostic.getMessage(null));
					 //System.err.format("Error on line %d in %s%n",
			         //                    diagnostic.getLineNumber(),
			         //                    ( (diagnostic==null?null:(FileObject)diagnostic.getSource()).toUri()));
				 
				 return false;
				 //System.exit(-1);
			}
			
			//add the class under test to the magicclassloader
			//byte[] bytecode =IOUtil.readFile(binPath+File.separator+subPath+File.separator+srcFileName+".class");
			
			//Class cls =magicClassLoader.loadClass(className);//
			//setClassUnderTest(cls);
						
			return true;
			
		}
		
		private static Vector<File> getJavaFiles(String path){
			Vector<File> allFiles=new Vector<File>();
			File directory=new File(path);
			File[] files=directory.listFiles();
			for(File f:files){
				if(f.isFile()&&f.getName().toString().endsWith(".java"))
					allFiles.add(f);
				if(f.isDirectory())
					allFiles.addAll(getJavaFiles(f.toString()));
			}
			return allFiles;
		}
		
		private static void InstantiateICUD(String className) throws ClassNotFoundException, IOException {
			Class cls;
//			if(className.equalsIgnoreCase(JTE.className)){
//				File file=new File(binPath);
//				URL url = file.toURL();          // file:/c:/myclasses/
//				URL[] urls = new URL[]{url};
//				ClassLoader cl = new URLClassLoader(urls);
//				cls =cl.loadClass(className);
//			}else{
				 cls =magicClassLoader.loadClass(className);	
//			}
				 //System.out.println(AbstractDynamicGenerator.getResourcePath(cls));
			mainClassUnderTest=cls;
			setClassUnderTest(cls);	
		}
		
		private static Class findMemberClass(String canonicalClassName, Class cls){
			if (canonicalClassName==null ||cls==null)
				return null;
			if (cls.getCanonicalName()==null)
				return null;
			
			if(canonicalClassName.equals(cls.getCanonicalName()))
				return cls;
			for(Class c:cls.getDeclaredClasses()){
				if(c.getCanonicalName()!=null && canonicalClassName.startsWith(c.getCanonicalName())
						&&(canonicalClassName.length()==c.getCanonicalName().length()||(canonicalClassName.length()>c.getCanonicalName().length()&&canonicalClassName.charAt(c.getCanonicalName().length()+1)=='.')))
					return findMemberClass(canonicalClassName, c);
			}
			return null;
			
		}

		private static Class findDeclaringClass(String canonicalClassName, Class cls){
			Class c=cls;
			while(c!=null && !canonicalClassName.equals(c.getCanonicalName())){
				c=c.getDeclaringClass();
			}
			return c;
			
		}
		
		private static Class findFriendClass(String declaringClassName, String canonicalClassName, Class cls){
			Class c=cls;
			if(c==null)
				return null;
			
			while(c!=null && !declaringClassName.equals(c.getName())){
				c=c.getDeclaringClass();
			}
			
			return findMemberClass(canonicalClassName,c);
			
		}
		
		private static void AnalyseCU() throws ClassNotFoundException{			
			methodCallsAnalyser=new MethodCallsAnalyser();
			compilationUnit.accept(methodCallsAnalyser);
			
			//Analyze Data members and parameters uses.
			dataMemberUseAnalyser=new DataMemberUseAnalyser();
			compilationUnit.accept(dataMemberUseAnalyser);
			
			influenceAnalyser=new InfluenceAnalyser();
			compilationUnit.accept(influenceAnalyser);
		}

		public static Vector<Path> getAccessiblePaths(int branch) throws IOException{
			return getAccessiblePaths(branch, null);
		}
		
		public static Vector<Path> getAccessiblePaths(int initBranch, Path oldPath) throws IOException{
			int cBranch=initBranch;
			Vector<Path> allPaths=new Vector<Path>();
			Path cPath=new Path();
			if(oldPath!=null)
				cPath.addAll(oldPath);
			double DC=1;
			if((Double)branchCoderAnalyser.getBranch2BlockMap().get(cBranch).getProperty(BranchesProperties.DIFF_COEF)!=null)
				DC=(Double)branchCoderAnalyser.getBranch2BlockMap().get(cBranch).getProperty(BranchesProperties.DIFF_COEF);
			cPath.add(new Branch(cBranch,DC,cPath.size()+1));
			
			//System.out.println("*****"+cBranch);
			while(!(branchCoderAnalyser.getBranch2BlockMap().get(cBranch).getParent() instanceof MethodDeclaration)){
				//System.out.println(cBranch);
				cBranch=(Integer) branchCoderAnalyser.getBranch2BlockMap().get(cBranch).getProperty(BranchesProperties.NUM_PARENT_BRANCH);
				DC=1;
				if((Double)branchCoderAnalyser.getBranch2BlockMap().get(cBranch).getProperty(BranchesProperties.DIFF_COEF)!=null)
					DC=(Double)branchCoderAnalyser.getBranch2BlockMap().get(cBranch).getProperty(BranchesProperties.DIFF_COEF);
				cPath.add(new Branch(cBranch,DC,cPath.size()+1));
				
			} 
			
			
			MethodDeclaration currentMethod=(MethodDeclaration)branchCoderAnalyser.getBranch2BlockMap().get(cBranch).getParent();
			//if(currentMethod.getName().toString().equals("withOffsetParsed"))
			//	methodCallsAnalyser=methodCallsAnalyser;
			//.getParent();
			//generate a path start at method
			boolean isAcssiblePath=true;
			//boolean isProtectedPath=false;
			IMethodBinding imb=currentMethod.resolveBinding();
			
			if(imb!=null){
				
				isAcssiblePath=isAccessible(imb);
			}
			else{
				for(int i=0;i<currentMethod.modifiers().size();i++){
					if(currentMethod.modifiers().get(i).toString().equalsIgnoreCase("public")) 
						break;
					//isProtectedPath=currentMethod.modifiers().get(i).toString().equalsIgnoreCase("protected");
					if(currentMethod.modifiers().get(i).toString().equalsIgnoreCase("private")
							||currentMethod.modifiers().get(i).toString().equalsIgnoreCase("protected")
							){
						isAcssiblePath=false;
						break;
					}				
				}
				
				if(currentMethod.getParent() instanceof AnonymousClassDeclaration){
					isAcssiblePath=false;
				}else
				{
					
					TypeDeclaration declaringClazz=(TypeDeclaration) currentMethod.getParent();	
					for(int i=0;i<declaringClazz.modifiers().size();i++){//TODO what about the accessibility of class container. this need a recursive method to get the correct accessibility
						if(declaringClazz.modifiers().get(i).toString().equalsIgnoreCase("public")) 
							break;
						//isProtectedPath=isProtectedPath || declaringClazz.modifiers().get(i).toString().equalsIgnoreCase("protected");
						if(declaringClazz.modifiers().get(i).toString().equalsIgnoreCase("private")
								||declaringClazz.modifiers().get(i).toString().equalsIgnoreCase("protected")
								){
							isAcssiblePath=false;
							break;
						}				
					}
				}
				
			}
			
			if(isAcssiblePath){
				
				try {
					if(!currentMethod.isConstructor()){
						cPath.setEntryPoint(methodDec2MethodRef(currentMethod));
					}
					else{
						cPath.setEntryPoint(methodDec2ConstructorRef(currentMethod));
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				allPaths.add(cPath);
			}
			else {//TODO create a map that contains paths for each already computed branch
				if(methodCallsAnalyser.getMethodBranchCallersMap().get(currentMethod.resolveBinding())!=null){
					for(int br:methodCallsAnalyser.getMethodBranchCallersMap().get(currentMethod.resolveBinding())){
						if(!cPath.contains(br)) //avoid cycle that may be generated by recursion
							allPaths.addAll(getAccessiblePaths(br,cPath));
					}
				}
//				else{
//					cPath.setTopPoint(currentMethod);
//					allPaths.add(cPath);//TODO not accessible paths.  also I think that I can handle protected methods and some public methods in anonymous classes
//				}
			}

			return allPaths;
		}

		private static boolean isAccessible(IMethodBinding method){
			if (method==null) return false;
			boolean accessibility=true;
			if(Modifier.isPrivate(method.getModifiers())
					|| Modifier.isProtected(method.getModifiers()))
				accessibility= false;
			
			for(ITypeBinding p: method.getParameterTypes()){
				
				if(p.isAnonymous()||!isAccessible(p)){
					//aParameterIsAnonymous=true;
					return false;
				}
			}
			
			if(accessibility){
				accessibility=isAccessible(method.getDeclaringClass());
			}
			
			return accessibility;
		}
		
		private static boolean isAccessible(ITypeBinding clazz){
			//Random modification
//			if(clazz.isAnonymous()) 
//				return false;
			if(clazz==null || clazz.isAnonymous())
				return false;
			
			boolean accessibility=true;
			if(Modifier.isPrivate(clazz.getModifiers())
					|| Modifier.isProtected(clazz.getModifiers()))
				accessibility= false;
			if(accessibility && clazz.isAnonymous()){ // && clazz.getSuperclass().equals(clazz.getDeclaringMethod().getReturnType())
				accessibility=isAccessible(clazz.getDeclaringMethod());
			}
			
			if(accessibility && !clazz.isAnonymous() && clazz.getDeclaringClass()!=null){
				accessibility=isAccessible(clazz.getDeclaringClass());
			}
			
			return accessibility;
		}
	    
		  
		  private static int getMaxEvaluations(){
			  int EstimatedTotalEvaluations=0;
				if(maxEvaluations==0)
					if(maxTime==0 || totalEvaluations==0)
						EstimatedTotalEvaluations= branchCoderAnalyser.getLastBranch()*(minEvaluationPerBranch);
					else{
						long currentTime = System.nanoTime();//.totalEvaluations();
						long allowedTime=maxTime;
						long avgTimePerEvaluation=((currentTime-startTimeNano)/totalEvaluations);
						long evalTime;
						if(avgTimePerEvaluation>0)
							evalTime=(allowedTime*1000000000)/avgTimePerEvaluation;
						else 
							evalTime= branchCoderAnalyser.getLastBranch()*(minEvaluationPerBranch);
						EstimatedTotalEvaluations= (int) evalTime;
					}
				else{
					if(maxTime==0 || totalEvaluations==0 )
						EstimatedTotalEvaluations= maxEvaluations;
					else{
						long currentTime = System.nanoTime();//.totalEvaluations();
						long allowedTime=maxTime;
						long avgTimePerEvaluation=((currentTime-startTimeNano)/totalEvaluations);
						
						long evalTime;
						if(avgTimePerEvaluation>0)
							evalTime=(allowedTime*1000000000)/avgTimePerEvaluation;
						else 
							evalTime= branchCoderAnalyser.getLastBranch()*(minEvaluationPerBranch);
						
						if(evalTime<maxEvaluations)
							EstimatedTotalEvaluations=(int) evalTime;
						else
							EstimatedTotalEvaluations= maxEvaluations;
					}
						
				}
				return EstimatedTotalEvaluations; 
		  }
		private static int getRemaindEvaluations(){
			return getMaxEvaluations()-totalEvaluations;
		}
		
		
		private static void generateTestData() throws Exception{
			stdout.println("Test-data-generation stage: " );
			//GeneConstructor.requiredClasses.clear();
			setAllBranchesTarget();
			
			allCoveredBranchesOnce.clear();
			allCoveredBranchesTwice.clear();
			totalEvaluations=0;
			geneartionNumber=0;
			nmberOfStuckThreads=0;
			remaindEvaluations=0;
			writeTestCasesIsDone=false;
			lastSaveNumberOfNCB=0;
			
			startTime = System.currentTimeMillis();
			lastTimeBranchCovered = startTime;
			startTimeNano = System.nanoTime();

			if (maxTime==0) 
				automaticTimeDetermination=true;
			
			if(automaticTimeDetermination ){
				//lastTimeUpdate=currentTime;
				//determineRequiredTime();
				maxTime=600;//30+100*minEvaluationPerBranch*branchCoderAnalyser.getLastBranch()/getMaxEvaluations()+(nmberOfStuckThreads*500);
				maxWaitingTime=40;
			}
			
			//KillMeAfterTimeOut killMeAfterTimeOut=new KillMeAfterTimeOut(2*(maxTime));//+maxTimeTestCaseCorrection);
			
			//TestDataSaver testDataSaver=new TestDataSaver(maxTime);
			//killMeAfterTimeOut.interupt();

			try{
				
					geneartionNumber++;
					branchesTarget.removeAll(allCoveredBranchesTwice);
					printProgress(totalEvaluations);
				//}
				
				remaindEvaluations=getRemaindEvaluations();
				//Phase II uses the heuristic search on all branches
				//int avgEvalPerBranch;//=totalEF/(branchCoderAnalyser.getLastBranch()-allCoveredBranches.size());
				
				//Set<Integer>
				tmpUncoveredTarget=new HashSet<Integer>();
				tmpUncoveredTarget.addAll(branchesTarget);
				
				//System.err.println("before +++++++++++++++++++: "+ maxTime);
				iteration();
				
			}
			catch(Exception e){
				
				e.printStackTrace();
			}
			finally{
				//if((!branchesTarget.isEmpty() && remaindEvaluations>0 && nmberOfStuckThreads<maxThreadNumber))
				//	goto restart;
				//	iteration();
				if(nmberOfStuckThreads>0)
					System.err.println("More than one Thread were used: "+(nmberOfStuckThreads+1) + "  ***  "+currentClassUnderTest.getClazz());
				

				writeTestCases();
				
			}
			//System.exit(5555);
		}
		
		static void determineRequiredTime(){
			if(!automaticTimeDetermination || timeAlreadyDetermined)
				return;
			//timeAlreadyDetermined=true;
			//automaticTimeDetermination=false;
			
			
			//en moyen 100 evaluations / branches
			if(maxTime==0){
				maxTime=600;//30+100*minEvaluationPerBranch*branchCoderAnalyser.getLastBranch()/getMaxEvaluations()+(nmberOfStuckThreads*500);
				maxWaitingTime=40;
			}
			
			//doit �tre d�finit en fonction de nombre de branches et le nomber de threads bloqu�es
			
			
			if(maxTime>600){ //les cas compliqu�s
				maxTime=600;
				
			}
			
			if(maxWaitingTime>50)
				maxWaitingTime=50;
			
			if(maxTime<40){ //les cas facile
				maxTime=40;
				maxWaitingTime=20;
			}
			
			//maxTime+=maxTime/10;
			
			long beforecMaxWaitingTime=cMaxWaitingTime;
	
			//cMaxWaitingTime=(maxTime+40)*1000/branchCoderAnalyser.getLastBranch();//+(nmberOfStuckThreads*500);			
			//if(cMaxWaitingTime>RandomTesting.mMaxWaitingTime)
				cMaxWaitingTime=RandomTesting.mMaxWaitingTime;
			
			//if(nmberOfStuckThreads>=branchCoderAnalyser.getLastBranch()/10)
				cMaxWaitingTime*=2*nmberOfStuckThreads; //2*beforecMaxWaitingTime;
			
			if(cMaxWaitingTime<500)
				cMaxWaitingTime=500;
			if(cMaxWaitingTime>20000)
				cMaxWaitingTime=20000;
			
			if(beforecMaxWaitingTime<=2*cMaxWaitingTime/3)
				InstanceGenerator.class2InstantiationWays.clear();
			
//			for(int i=0;i<10000;i++)
//				System.out.println("***************************: "+cMaxWaitingTime);
			
			//RandomTesting.cMaxWaitingTime=20000;
			//KillMeAfterTimeOut killMeAfterTimeOut=new KillMeAfterTimeOut(maxTime);//+maxTimeTestCaseCorrection);
			
		}
		
		static void iteration(){
			int branch = 0;
			try{
				//
				long lastTimeUpdate=startTime;
				
				maxWaitingTime=maxTime/4;
				if(maxWaitingTime<50){
					maxWaitingTime=50;
				}
				
				//
				while (!timeWasting && !branchesTarget.isEmpty()) // && remaindEvaluations>0 && nmberOfStuckThreads<maxThreadNumber
				{
					branch=branchesTarget.iterator().next();
					int size = branchesTarget.size();
					
					int item = new Random(System.currentTimeMillis()).nextInt(size);
					int i = 0;
					for(int obj : branchesTarget)
					{
					    if (i == item)
					    	branch= obj;
					    i = i + 1;
					}
					//Random modification
					//System.out.println();
					//System.out.println(branch+"*"+"1");
					currentTarget=new Target(branch);
					
					//if(currentTarget==null)
					//	System.out.println(branch+"*******************rb7a");
					
					//System.out.println("2*");
					int consumedEF=0;
					//if(currentTarget.size()>0){	
					int avgEvalPerBranch=remaindEvaluations/(tmpUncoveredTarget.size());
					//System.out.print("2");
					if(avgEvalPerBranch<minEvaluationPerBranch)
						avgEvalPerBranch=remaindEvaluations;
					
					
					if(geneartionNumber*minEvaluationPerBranch<avgEvalPerBranch && geneartionNumber<5)
						avgEvalPerBranch=geneartionNumber*minEvaluationPerBranch;
						
						RandomTesting RA=new RandomTesting(1);
						consumedEF=RA.run(branchesTarget);
	//				}
						
					if(consumedEF==-1){
						nmberOfStuckThreads++;
						//time alignment;
						
						
					}
					//System.out.println("3*");
					long currentTime   = System.currentTimeMillis();
					//determine the necessary time && (currentTime-lastTimeUpdate>=5000|| (nmberOfStuckThreads>0 && (currentTime-lastTimeUpdate>=500)))
					if(automaticTimeDetermination ){
						lastTimeUpdate=currentTime;
						//if(maxTime==0){
						maxTime=600;//30+100*minEvaluationPerBranch*branchCoderAnalyser.getLastBranch()/getMaxEvaluations()+(nmberOfStuckThreads*500);
						maxWaitingTime=40;
						//}
						//determineRequiredTime();
					}

	
					//consumedEF=1;// RA.getGeneartion();
					totalEvaluations++;//=consumedEF;
					remaindEvaluations=1;//getRemaindEvaluations();
	
					//}
					//Random modification
					
					branchesTarget.remove(currentTarget.getBranch());
					int beforeNbrTargets=branchesTarget.size();
					branchesTarget.removeAll(allCoveredBranchesOnce);
					int afterNbrTargets=branchesTarget.size();
					tmpUncoveredTarget.removeAll(allCoveredBranchesTwice);
					if(branchesTarget.isEmpty()){
						geneartionNumber++;
						branchesTarget.addAll(tmpUncoveredTarget);
					}
					//System.out.println("4*");
					//if(consumedEF==-1);
					if(beforeNbrTargets-afterNbrTargets>0|| testDataSetWithErrors.size()-lastSizetestDataSetWithErrors>0){
						lastTimeBranchCovered = currentTime;
						lastSizetestDataSetWithErrors=testDataSetWithErrors.size();
					}
						
					
					if((totalEvaluations>10)
							&&((automaticTimeDetermination && (currentTime-lastTimeBranchCovered>1000*90 || (geneartionNumber>50000))
											|| currentTime-startTime>0.8*1000*(maxTime)))){ //60000 maxTimeTestCaseCorrection*1000

						timeWasting=true;
					}
					
					printProgress(consumedEF);
				}
			}catch(Exception e){
				e.printStackTrace(); //concurrent error may occur on allCoveredBranch && remaindEvaluations>0
			}
			finally{
				if (!timeWasting && !branchesTarget.isEmpty()  && nmberOfStuckThreads<maxThreadNumber){
					branchesTarget.remove(branch);
					//int beforeNbrTargets=branchesTarget.size();
					//branchesTarget.removeAll(allCoveredBranches);
					//int afterNbrTargets=branchesTarget.size();
					//tmpUncoveredTarget.removeAll(allCoveredBranches);
					tmpUncoveredTarget.remove(branch);
					if(branchesTarget.isEmpty()){
						geneartionNumber++;
						branchesTarget.addAll(tmpUncoveredTarget);
					}
					
					iteration();
				}
			}
			
		}
		
		
		static void writeTestCases(){
			 writeTestCases(true);
		}
		
		static void writeTestCases(boolean close){
		try{
			stdout.println();
			stdout.println("Test-data-writing stage: ");
			long endTime   = System.currentTimeMillis();
			if(writeTestCasesIsDone)
				return;
			
			if( close)
				writeTestCasesIsDone=true;
			
			if(close)
				for(TestCaseCandidate ch:testDataSetWithErrors){
					if(!allCoveredBranchesOnce.containsAll(ch.getCoveredBranches())){
						allCoveredBranchesOnce.addAll(ch.getCoveredBranches());
						testDataSet.add(ch);
					}
				}
//			Set<Chromosome> toRemove=new HashSet<Chromosome>();
//			for(Chromosome ch1:testDataSet)
//				for(Chromosome ch2:testDataSet)
//					if(ch1!=ch2){
//						if(ch1.getCoveredBranches().containsAll(ch2.getCoveredBranches()))
//							toRemove.add(ch2);
//						else
//						if(ch2.getCoveredBranches().containsAll(ch1.getCoveredBranches()))
//							toRemove.add(ch1);
//					}
//			
//			for(Chromosome ch1:toRemove)
//				testDataSet.remove(ch1);
				
			totalTime = endTime - ActualstartTime;//startTime;
			totalSearchTime = endTime - startTime;
			//if(close)
			allNotCoveredBranches.clear();
			
			for(int i=1;i<branchCoderAnalyser.getLastBranch();i++){
				if(!allCoveredBranchesOnce.contains(i))
					allNotCoveredBranches.add(i);
			}
			
			if(!close && testDataSet.size()==lastSaveNumberOfNCB)
				return;
			
			lastSaveNumberOfNCB=testDataSet.size();//+testDataSetWithErrors.size();

			//CompilationUnit unit = getTestCasesSourceCode();
			
			CompilationUnit allUnits[] = getTestCasesSourceCodeList();
			
			Thread thread []=new Thread [allUnits.length];
			
			for(int fi=0;fi<1;fi++){
				final String clsName=className+TEST_CASES_SURFIX+"_0_"+ fi;
				final String fileName=testCasesPath+File.separator+ subPath+File.separator+srcFileName+TEST_CASES_SURFIX+"_0_"+ fi+".java";
				//String fileName=testCasesPath+File.separator+ subPath+File.separator+allUnits[fi]..getClass().getName()+".java";
				FileEditor.unit2File(allUnits[fi].toString(), fileName);
				
				FileEditor.unit2File(allUnits[fi].toString(), jteOutputPath+"/testCaseCopy/"+ subPath+File.separator+srcFileName+TEST_CASES_SURFIX+"_0_"+ fi+".java");
				
				 //TestCaseCorrector tcc =new TestCaseCorrector(fileName,clsName);
				close=true;
				JTE.correctExceptions( fileName,clsName, "Split",true);
//				 thread[fi] = new Thread(){ 
//						@Override public void run (){
//							JTE.correctExceptions( fileName,clsName, "Split",true);
//						}
//					};
//					thread[fi].setDaemon(true);
//					thread[fi].start();
				
				if(close){
//					writeSummaryFile();	
//					stdout.println("Total Time (ms): "+(System.currentTimeMillis()-ActualstartTime));
//					
//					//if (true) return;
//					if(TESTCASES_SLICINIG){
//						correctExceptions( fileName,className+TEST_CASES_SURFIX, "Split",true); 
//					}
//					else
//						correctExceptions( fileName,className+TEST_CASES_SURFIX, "noSplit",true); 
//	
					testDataSet.clear();
					testDataSetWithErrors.clear();
//					
				}
			}
			
//			for(int fi=0;fi<allUnits.length;fi++)
//				try {
//					thread[fi].join(); 
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}catch (Exception e) { //this to avoid System.exit()
//					e.printStackTrace();
//				}
			//Thread.currentThread().sleep(1000*maxTime - (System.currentTimeMillis()-ActualstartTime));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(close){
				SystemExitControl.enableSystemExitCall();
				System.exit(5555);
			}
		}
			
		}
		
		public static void correctExceptions(String fileName,String className,String split, boolean onceAtTime){
			String args[]=new String[6];
			args[0]=className;//+TEST_CASES_SURFIX;
			args[1]=fileName;
			args[2]=binPath+File.separator;
			String cp="";
			for(String p:classPath){
				cp+=p+File.pathSeparator;
			}
			for(URL url:classPathList){
				cp+=url.getFile()+File.pathSeparator;
			}
			args[3]=cp.substring(0, cp.length()-1);	
			args[4]=split;
			args[5]=testCasesPath;
			
			String wrongMethodsFile="";
			if(onceAtTime){
				String wrongAssertionsMethods="";
				for(int i=0;i<lastSaveNumberOfNCB;i++)
					wrongAssertionsMethods+=i+";";
				if(wrongAssertionsMethods.length()>0)
					wrongAssertionsMethods=wrongAssertionsMethods.substring(0, wrongAssertionsMethods.length()-1)+"\n";
				
				File wrongMethodsDir=new File(jteOutputPath);
	        	wrongMethodsFile=wrongMethodsDir.getAbsolutePath()+File.separator+"logs"+File.separator+className+"wrongAssertionsMethods"; //+className+File.separator
	        	//stdout.println("wrongAssertionsMethods : "+wrongAssertionsMethods);
	        	FileEditor.unit2File(wrongAssertionsMethods, wrongMethodsFile);
	        	//stdout.println("wrongAssertionsMethods : OK");
			}
//			if(!wrongMethods.equals(""))
			{
				//JTExpert 
				String cp1=AbstractDynamicGenerator.getResourcePath(JUnitExecutor.class); 
				//JUnit you must find another way getthe resourcesses of junit
				cp1+=":"+projectPath+"/lib/junit/junit.jar:"+projectPath+"/lib/junit/hamcrest.jar" ;
				
				//stdout.println("javaExec reading : ");
				String javaExec=System.getProperty("java.home")+File.separator+"bin"+File.separator+"java -XX:-OmitStackTraceInFastThrow "; //-XX:-OmitStackTraceInFastThrow 
				
				//stdout.println("Test-case-execution stage: ");
				int exitVal1=1; 
				//int execution=0; 
				//long startingTime=System.currentTimeMillis();// && execution<50
				while(exitVal1>0){
					String cmdTxt=javaExec+" -cp "+ cp1+" csbst.testing.JUnitExecutor "
							+ args[0] +" " +args[1]+" "+args[2]+" "+args[3]+" "+args[4]+" "+args[5];
					
					if(onceAtTime)
						cmdTxt+=" "+wrongMethodsFile;
					//!automaticTimeDetermination && 
					//if(System.currentTimeMillis()-startTime>=1.04*1000*(maxTime-5))
					//	cmdTxt+=" Stop";
					
					Process proc=null;
					try {
//						if(onceAtTime && split.equalsIgnoreCase("split")){
//							JUnitExecutor.externalCall=false;
//							System.err.println("Local execution");
//							JUnitExecutor.main(args);
//							exitVal1 = (int) JUnitExecutor.returnValue;
//						}else
						{
							JUnitExecutor.externalCall=true;
							//System.err.println("External execution");
							proc = Runtime.getRuntime().exec(cmdTxt);
							ReadStream sin = new ReadStream("stdin", proc.getInputStream ());
							ReadStream sout = new ReadStream("stderr", proc.getErrorStream ());
							sin.start ();
							sout.start ();
							//if(!dontWait)
							exitVal1 = proc.waitFor();
						}
						
						stdout.println("# version:"+exitVal1);
						boolean forcedExit=false; 
						switch (exitVal1){
						case JUnitExecutor.COMPILATION_ERROR:
							//rename the file 
			        		//File so =new File(fileName);
			        		//File de =new File(fileName+".Error");
			        		//Files.move(so, de);
			            	//if(!(splitTestCases && lastExecution)){
//							if(onceAtTime && split.equalsIgnoreCase("split"))
//							{
//								
//								for(int i=0;i<lastSaveNumberOfNCB;i++){
//									try{
//										String fn=fileName;//testCasesPath+File.separator+ subPath+File.separator+srcFileName+TEST_CASES_SURFIX+i+".java";
//										//ch.writeCurrentTestCandidate(TEST_CASES_SURFIX+i);
//										correctExceptions( fn,className+i,"NoSplit", false);
//									}catch(Exception e){
//										//e.printStackTrace();
//									}
//									i++;
//								}
//							}
							System.err.println(" System exits with compilation error ");
							exitVal1=0;
							break;
							//correctExceptions( fileName, className, split,  true);
						case JUnitExecutor.STOP_REQUEST:
								exitVal1=0; //to leave the loop
							break;
						case JUnitExecutor.TIMEOUT_ERROR:
							if(!onceAtTime){
								onceAtTime=true;
								correctExceptions( fileName, className, split,  true);
							}
							else{
								onceAtTime=false;
								correctExceptions( fileName, className, split,  false);
							}
							exitVal1=0; //to leave the loop
							break;
						case 0:
						default:
							if(onceAtTime && split.equalsIgnoreCase("split")){
								onceAtTime=false;
								String fn=fileName;//testCasesPath+File.separator+ subPath+File.separator+srcFileName+TEST_CASES_SURFIX+".java";
								fn=fn.replace("_0_", "_"+(exitVal1+1)+"_");
								String cn;
								cn=className.replace("_0_", "_"+(exitVal1+1)+"_");
								
								//correctExceptions( fn,cn,"Split", onceAtTime);
							}
							exitVal1=0;
							break;
						//default:
						//	break;
						}		
						//System.out.println(proc.getErrorStream());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (Exception e) { //this to avoid System.exit()
						e.printStackTrace();
					}finally {
					    if(proc != null)
					    	proc.destroy();
					}
			            
			           // execution++;
				}
				
				stdout.println("Total Time (ms): "+(System.currentTimeMillis()-ActualstartTime));
				//stdout.println(maxTime);
			}			
		}
		
		private static void writeSummaryFile(){
			FileWriter jteSummary;
			try {
				jteSummary= new FileWriter(new File(testCasesPath+File.separator+ subPath+File.separator+srcFileName+"jteSummary.csv"));
				
//				jteSummary.write("Unit under test");jteSummary.write(";");	
//				jteSummary.write("Nbr Of Branches");jteSummary.write(";");
//				jteSummary.write("Nbr Of Covere dBranches");jteSummary.write(";"); 
//				jteSummary.write("Coverage");jteSummary.write(";");
//				jteSummary.write("Nbr Of Fitness Evaluations");jteSummary.write(";");
//				jteSummary.write("Search time (ms)");jteSummary.write(";");
//				jteSummary.write("Runtime (ms)");//jteSummary.write(";");
//				jteSummary.write("\r");
				
				jteSummary.write(className);jteSummary.write(";");	
				jteSummary.write(""+branchCoderAnalyser.getLastBranch());jteSummary.write(";");
				jteSummary.write(""+allCoveredBranchesOnce.toArray().length);jteSummary.write(";");
				DecimalFormat df = new DecimalFormat("#.##");
				jteSummary.write(""+df.format(100.00*allCoveredBranchesOnce.size()/(branchCoderAnalyser.getLastBranch())));jteSummary.write(";");
				jteSummary.write(""+totalEvaluations);jteSummary.write(";");
				jteSummary.write(""+(int) totalSearchTime);jteSummary.write(";");
				jteSummary.write(""+(int) totalTime);
				jteSummary.write("\r");
				
				jteSummary.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private static void printProgress(int cons){
			if(!printProgress)
				return;
			String bar1 = "[..................................................]";
			String bar2 = "[                                                  ]";
			DecimalFormat df = new DecimalFormat("##.00");
			double sprc=1.0*(totalEvaluations)/(getMaxEvaluations());
			if(sprc>0.5){
				sequencesLength=(int) (10*sprc);
				if(sequencesLength<3)
					sequencesLength=3;
			}
				
			int sprg=(int) (sprc*(bar1.length()));
			String search="search: ";
			if(bar1.length()<sprg)
				sprg=bar1.length();
			search+=bar1.substring(0, sprg);
			search+=bar2.substring(sprg);
			search+="["+df.format(100.00*sprc)+"%]";

			sprc=1.0*allCoveredBranchesOnce.size()/(branchCoderAnalyser.getLastBranch());
			
			
			
			sprg=(int) (sprc*bar1.length());
			String coverage="coverage: ";
			if(bar1.length()<sprg)
				sprg=bar1.length();
			coverage+=bar1.substring(0, sprg);
			coverage+=bar2.substring(sprg);
			coverage+="["+df.format(100.00*sprc)+"%]";
			
			stdout.print("\r"+search+"     "+coverage);
		}
		
		private static CompilationUnit getTestCasesSourceCode(){
			//create a CompilationUnit
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource("".toCharArray()); //The parser is initialized with an empty array
			CompilationUnit unit = (CompilationUnit) parser.createAST(null); 
			unit.recordModifications();
			AST ast = unit.getAST();
			
			
			//create the class java doc
			Javadoc jd=ast.newJavadoc();
			StringBuffer txt=new StringBuffer("");
			//txt.append(JTE.requiredClasses+"" + System.getProperty("line.separator") +"");
			//txt.append(JTE.requiredClasses.size()+"" + System.getProperty("line.separator") +"");
			txt.append("This class was automatically generated to test the "+ className+" class according to all branches coverage criterion" + System.getProperty("line.separator") +"");
			txt.append("ExceptionsOriented: "+ ExceptionsOriented +" " + System.getProperty("line.separator") +"");
			txt.append("projectPackagesPrefix:"+projectPackagesPrefix+" " + System.getProperty("line.separator") +"");
			txt.append("Covered branches: "+ allCoveredBranchesOnce.toString()+"" + System.getProperty("line.separator") +"");//allNotCoveredBranches
			txt.append("Uncovered branches: "+ allNotCoveredBranches.toString()+"" + System.getProperty("line.separator") +"");
			txt.append("Total number of branches: "+ (branchCoderAnalyser.getLastBranch())+"" + System.getProperty("line.separator") +"");
			txt.append("Total number of covered branches: "+ allCoveredBranchesOnce.toArray().length+"" + System.getProperty("line.separator") +"");
			DecimalFormat df = new DecimalFormat("#.##");
			txt.append("Coverage : "+df.format(100.00*allCoveredBranchesOnce.size()/(branchCoderAnalyser.getLastBranch()))+"%" + System.getProperty("line.separator") +"");
			txt.append("Evaluations : "+totalEvaluations+"" + System.getProperty("line.separator") +"");
			txt.append("search time (ms): "+totalSearchTime+"" + System.getProperty("line.separator") +"");
			txt.append("total runtime (ms): "+totalTime);
			
			TextElement txtElt=ast.newTextElement();
			txtElt.setText(txt.toString());
			
			TagElement tagElt=ast.newTagElement();
			tagElt.fragments().add(txtElt);

			//String[]
			stdout.println(txt);
			String fileName=testCasesPath+File.separator+ subPath+File.separator+srcFileName+TEST_CASES_SURFIX+".java";
			stdout.println("A JUnit test suite was created at: "+fileName);
			
			//create the package
			if(subPath.indexOf(File.separator)>0){
				PackageDeclaration pkgDec=ast.newPackageDeclaration();
				pkgDec.setName(AbsractGenerator.generateQualifiedName(subPath.replace(File.separator, "."),ast));
				unit.setPackage(pkgDec);
			}else if(!subPath.equals("")){
				PackageDeclaration pkgDec=ast.newPackageDeclaration();
				pkgDec.setName(ast.newSimpleName(subPath));
				unit.setPackage(pkgDec);
			}

			ImportDeclaration importDeclaration1 = ast.newImportDeclaration();
//			//org.junit.Assert.*
			QualifiedName name1=AbsractGenerator.generateQualifiedName("org.junit.Assert",ast);
			importDeclaration1.setName(name1);
			importDeclaration1.setStatic(true);
			importDeclaration1.setOnDemand(true);
			unit.imports().add(importDeclaration1);
			
			ImportDeclaration importDeclaration2 = ast.newImportDeclaration();
			QualifiedName name2=AbsractGenerator.generateQualifiedName("org.junit.Test",ast);
			importDeclaration2.setName(name2);
			unit.imports().add(importDeclaration2);
			
			ImportDeclaration importDeclaration3 = ast.newImportDeclaration();
			QualifiedName name3=AbsractGenerator.generateQualifiedName("org.junit.Rule",ast);
			importDeclaration3.setName(name3);
			unit.imports().add(importDeclaration3); 	
			
			//Create a new class
			TypeDeclaration clazzNode= ast.newTypeDeclaration();
			clazzNode.setInterface(false);
			clazzNode.setName(ast.newSimpleName(srcFileName+TEST_CASES_SURFIX));		
			clazzNode.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			//clazzNode.setSuperclassType(ast.newSimpleType(ast.newName("TestCase")));
			unit.types().add(clazzNode);
			
			int i=0;
			for(TestCaseCandidate ch: testDataSet){
				//stdout.println("Writing Test Case :"+i);

				clazzNode.bodyDeclarations().add(ch.generateTestCaseSourceCode(clazzNode,"TestCase"+i,false,true));
				i++;
			}

			
			//import required classes
			for(Class cls:JTE.requiredClasses){
				
				if(cls.getCanonicalName()==null 
						|| !cls.getCanonicalName().contains(".")
						|| cls.isPrimitive() 
						|| (cls.getPackage()!=null && cls.getPackage().getName().toString().equals("java.lang"))) 
					continue;// 
				
				
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
				//QualifiedName impName=ASTEditor.generateQualifiedName(binaryName,ast);
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
			
			jd.tags().add(tagElt);		
			clazzNode.setJavadoc(jd);
			return unit;
		}

		private static CompilationUnit[] getTestCasesSourceCodeList(){
			int numTestCaseInFile=3000;
			int numfiles=testDataSet.size()/numTestCaseInFile+1;
			CompilationUnit allUnits[]=new CompilationUnit[numfiles];
			
			for(int fi=0;fi<numfiles;fi++){
				//create a CompilationUnit
				ASTParser parser = ASTParser.newParser(AST.JLS3);
				parser.setSource("".toCharArray()); //The parser is initialized with an empty array
				
				CompilationUnit unit = (CompilationUnit) parser.createAST(null); 
				unit.recordModifications();
				AST ast = unit.getAST();
				
				//create the class java doc
				Javadoc jd=ast.newJavadoc();
				StringBuffer txt=new StringBuffer("");
				//txt.append(JTE.requiredClasses+"" + System.getProperty("line.separator") +"");
				//txt.append(JTE.requiredClasses.size()+"" + System.getProperty("line.separator") +"");
				txt.append("This class was automatically generated to test the "+ className+" class according to all branches coverage criterion" + System.getProperty("line.separator") +"");
				txt.append("ExceptionsOriented: "+ ExceptionsOriented +" " + System.getProperty("line.separator") +"");
				txt.append("projectPackagesPrefix:"+projectPackagesPrefix+" " + System.getProperty("line.separator") +"");
				txt.append("Covered branches: "+ allCoveredBranchesOnce.toString()+"" + System.getProperty("line.separator") +"");//allNotCoveredBranches
				txt.append("Uncovered branches: "+ allNotCoveredBranches.toString()+"" + System.getProperty("line.separator") +"");
				txt.append("Total number of branches: "+ (branchCoderAnalyser.getLastBranch())+"" + System.getProperty("line.separator") +"");
				txt.append("Total number of covered branches: "+ allCoveredBranchesOnce.toArray().length+"" + System.getProperty("line.separator") +"");
				DecimalFormat df = new DecimalFormat("#.##");
				txt.append("Coverage : "+df.format(100.00*allCoveredBranchesOnce.size()/(branchCoderAnalyser.getLastBranch()))+"%" + System.getProperty("line.separator") +"");
				txt.append("Evaluations : "+totalEvaluations+"" + System.getProperty("line.separator") +"");
				txt.append("search time (ms): "+totalSearchTime+"" + System.getProperty("line.separator") +"");
				txt.append("total runtime (ms): "+totalTime);
				
				TextElement txtElt=ast.newTextElement();
				txtElt.setText(txt.toString());
				
				TagElement tagElt=ast.newTagElement();
				tagElt.fragments().add(txtElt);
	
				//String[]
				stdout.println(txt);
				String fileName=testCasesPath+File.separator+ subPath+File.separator+srcFileName+TEST_CASES_SURFIX+".java";
				stdout.println("A JUnit test suite was created at: "+fileName);
				
				//create the package
				if(subPath.indexOf(File.separator)>0){
					PackageDeclaration pkgDec=ast.newPackageDeclaration();
					pkgDec.setName(AbsractGenerator.generateQualifiedName(subPath.replace(File.separator, "."),ast));
					unit.setPackage(pkgDec);
				}else if(!subPath.equals("")){
					PackageDeclaration pkgDec=ast.newPackageDeclaration();
					pkgDec.setName(ast.newSimpleName(subPath));
					unit.setPackage(pkgDec);
				}
	
				ImportDeclaration importDeclaration1 = ast.newImportDeclaration();
	//			//org.junit.Assert.*
				QualifiedName name1=AbsractGenerator.generateQualifiedName("org.junit.Assert",ast);
				importDeclaration1.setName(name1);
				importDeclaration1.setStatic(true);
				importDeclaration1.setOnDemand(true);
				unit.imports().add(importDeclaration1);
				
				ImportDeclaration importDeclaration2 = ast.newImportDeclaration();
				QualifiedName name2=AbsractGenerator.generateQualifiedName("org.junit.Test",ast);
				importDeclaration2.setName(name2);
				unit.imports().add(importDeclaration2);
				
				ImportDeclaration importDeclaration3 = ast.newImportDeclaration();
				QualifiedName name3=AbsractGenerator.generateQualifiedName("org.junit.Rule",ast);
				importDeclaration3.setName(name3);
				unit.imports().add(importDeclaration3); 
				
				//org.junit.rules.Timeout x;
				//org.junit.rules.Timeout
				ImportDeclaration importDeclaration4 = ast.newImportDeclaration();
				QualifiedName name4=AbsractGenerator.generateQualifiedName("org.junit.rules.Timeout",ast);
				importDeclaration4.setName(name4);
				unit.imports().add(importDeclaration4);
				
				//Create a new class
				TypeDeclaration clazzNode= ast.newTypeDeclaration();
				clazzNode.setInterface(false);
				clazzNode.setName(ast.newSimpleName(srcFileName+TEST_CASES_SURFIX+"_0_"+fi));		
				clazzNode.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
				//clazzNode.setSuperclassType(ast.newSimpleType(ast.newName("TestCase")));
				unit.types().add(clazzNode);
				
				//create a time out rule
				VariableDeclarationFragment vdf=ast.newVariableDeclarationFragment();
				vdf.setName(ast.newSimpleName("globalTimeout"));
				//vdf.
				
				FieldDeclaration fd= (FieldDeclaration)ASTNode.copySubtree(ast, String2Expression.getFieldDeclaration("@Rule public Timeout globalTimeout= new Timeout(10000);"));
				clazzNode.bodyDeclarations().add(fd);
				//System.out.println();
				
				for(int i=fi*numTestCaseInFile; i<(fi+1)*(numTestCaseInFile);i++){
					if(i>=testDataSet.size())
						break;
					TestCaseCandidate ch= (TestCaseCandidate) testDataSet.toArray()[i];
					clazzNode.bodyDeclarations().add(ch.generateTestCaseSourceCode(clazzNode,"TestCase"+i,false,true));
				}
	
				
				//import required classes
				for(Class cls:JTE.requiredClasses){
					
					if(cls.getCanonicalName()==null 
							|| !cls.getCanonicalName().contains(".")
							|| cls.isPrimitive() 
							|| (cls.getPackage()!=null && cls.getPackage().getName().toString().equals("java.lang"))) 
						continue;// 
					
					
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
					//QualifiedName impName=ASTEditor.generateQualifiedName(binaryName,ast);
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
				
				jd.tags().add(tagElt);		
				clazzNode.setJavadoc(jd);
				
				allUnits[fi]=unit;
			}
			return allUnits;
		}
				
		
		private static void checkCUTCompatibility(MethodDeclaration declaredMethod) throws IOException{
			boolean isMemberClass=false;
			boolean isLocalClass=false;
			String declaringClassName=className;
			String canonicalClassName="";
			
			if(declaredMethod.getParent() instanceof TypeDeclaration){
				TypeDeclaration declaringClazz=(TypeDeclaration) declaredMethod.getParent();
				isMemberClass=declaringClazz.isMemberTypeDeclaration();
				isLocalClass=declaringClazz.isLocalTypeDeclaration();
				
				canonicalClassName=declaringClassName;
				//String surfix="";
				if(declaringClazz!=null && (declaringClazz.isMemberTypeDeclaration()||declaringClazz.isLocalTypeDeclaration())){
					canonicalClassName+="."+declaringClazz.getName();
					//declaringClazz=(TypeDeclaration) declaringClazz.getParent();
				}	
				
				if(declaringClazz!=null && (declaringClazz.isLocalTypeDeclaration())){
					declaringClassName+="."+declaringClazz.getName();
				}
			}else if( declaredMethod.getParent() instanceof EnumDeclaration){
				EnumDeclaration declaringClazz=(EnumDeclaration) declaredMethod.getParent();
				isMemberClass=declaringClazz.isMemberTypeDeclaration();
				isLocalClass=declaringClazz.isLocalTypeDeclaration();
				
				
				canonicalClassName=declaringClassName;
				if(declaringClazz!=null && (declaringClazz.isMemberTypeDeclaration())){
					canonicalClassName+="."+declaringClazz.getName();
					//declaringClazz= declaringClazz.getParent();
				}	
	
				if(declaringClazz!=null && declaringClazz.isLocalTypeDeclaration()){
					declaringClassName+="."+declaringClazz.getName();
				}
			}else if( declaredMethod.getParent() instanceof AnonymousClassDeclaration){
				ITypeBinding type=((AnonymousClassDeclaration)declaredMethod.getParent()).resolveBinding();
				if(type!=null){
					canonicalClassName=type.getBinaryName();
					declaringClassName=canonicalClassName;
				}
				if(canonicalClassName==null)
					return;
			}else{
				
				//try {
					System.err.println ("Mismatch error between the source code and the compiled class at the function: checkCUTCompatibility "+declaredMethod.getName());
				//} catch (Exception e) {
					//e.printStackTrace();
				//}
			}

			if(currentClassUnderTest.getClazz().isAnonymousClass()
					 ||!canonicalClassName.equals(currentClassUnderTest.getClazz().getCanonicalName().toString())){
				 Class candidatCUT=null;
				 if((currentClassUnderTest.getClazz().isMemberClass()||isMemberClass)&& !currentClassUnderTest.getClazz().isAnonymousClass()){					 
					 if(canonicalClassName.startsWith(currentClassUnderTest.getClazz().getCanonicalName().toString()))
						 candidatCUT=findMemberClass(canonicalClassName, currentClassUnderTest.getClazz());
					 
					 else if(currentClassUnderTest.getClazz().getCanonicalName().toString().startsWith(canonicalClassName))
						 candidatCUT=findDeclaringClass(canonicalClassName, currentClassUnderTest.getClazz());
					 else
						 candidatCUT=findFriendClass(declaringClassName, canonicalClassName, currentClassUnderTest.getClazz());
				 }
				 
				 if(currentClassUnderTest.getClazz().isAnonymousClass()||(!currentClassUnderTest.getClazz().isMemberClass()&&!isMemberClass)||candidatCUT==null){
						 try {
							Class cls =magicClassLoader.loadClass(declaringClassName);//
							setClassUnderTest(cls);
							//InstantiateICUD(declaringClassName);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						 
						 candidatCUT=findMemberClass(canonicalClassName, currentClassUnderTest.getClazz());
				}
				 if(candidatCUT!=null)
					 setClassUnderTest(candidatCUT);
			 }	
		}
		
		private static Method methodDec2MethodRefSimple(MethodDeclaration declaredMethod) throws IOException{			
			checkCUTCompatibility(declaredMethod);
			Method reflexionMethod=null;	
			Method[] refelexionMethods = currentClassUnderTest.getClazz().getDeclaredMethods();
			List declaredParemeter=declaredMethod.parameters();

		    for (int m = 0 ; m < refelexionMethods.length ; m++) {
		    	reflexionMethod = refelexionMethods[m];
		        Class[] cParameterTypes = reflexionMethod.getParameterTypes();

		        String reflexionSimpleName=reflexionMethod.getName().toString();
		        reflexionSimpleName=reflexionSimpleName.replace("$", ".");
		        String []HirarchicalName=reflexionSimpleName.split("\\.");
		        reflexionSimpleName=HirarchicalName[HirarchicalName.length-1];

		        if (!reflexionSimpleName.equals(declaredMethod.getName().toString())
		        		|| cParameterTypes.length!=declaredParemeter.size()) continue;
		        
		        boolean matched=true;		        
	        	for (int p = 0 ; p < cParameterTypes.length ; p++){
					Type currentType=((SingleVariableDeclaration)declaredMethod.parameters().get(p)).getType();
					  
					//System.out.println("Test-data-generation stage: BEFORE");
	        		while(currentType.isArrayType()||cParameterTypes[p].isArray()){
	        			if(!currentType.isArrayType()||!cParameterTypes[p].isArray()){
	        				matched=false;
		        			break;
	        			}
	        			currentType=((ArrayType)currentType).getElementType();
	        			cParameterTypes[p]=cParameterTypes[p].getComponentType();
	        		}
	        		
	        		if (currentType.isPrimitiveType()){
	        			if(!(cParameterTypes[p].isAssignableFrom(getPrimitiveClass(currentType.toString())))){
	        				matched=false;
		        			break;
	        			}
	        		}
	        		else{
	        			//boolean generic=false ;
	        			Class clstmp=null;
	        			String clsCompleteName=getQualifiedName(currentType.toString());
	        			if(!clsCompleteName.equals(""))
	        				try {
	        					clstmp=magicClassLoader.loadClass(clsCompleteName);
	        				} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
	        			else{
		        			clstmp=getClassFromSimpleName(currentType.toString());;//possibleClasses.get(0);	        				
	        			}

		        		if (clstmp==null || cParameterTypes==null||cParameterTypes[p]==null||!(cParameterTypes[p].isAssignableFrom(clstmp))){
		        			matched=false;
		        			break;
		        		}
	        		}
	        	}
	        	if (matched) {
	        		astMethod2ReflexionMethod.put(declaredMethod, reflexionMethod);
	        		return reflexionMethod;
	        	}		        
		    }
		    //try {
		    	System.err.println ("Mismatch error between the source code and the compiled class at the function: methodDec2MethodRefSimple "+declaredMethod.getName());
			//} catch (Exception e) {
				//e.printStackTrace();
			//}
			return null;
		}
	
		private static String getQualifiedName(String simpleName){
			String qName="";
			if(simpleName.equals("Object"))
				return "java.lang.Object";
			if(currentClassUnderTest.getClazz().getSimpleName().toString().equals(simpleName))
				return currentClassUnderTest.getClazz().getCanonicalName();
						
			for(Object i:compilationUnit.imports())
				if(((ImportDeclaration)i).getName().toString().endsWith("."+ simpleName))
					qName=((ImportDeclaration)i).getName().toString();
					
			return qName;
		}
		
		private static Class getClassFromSimpleName(String simpleName){		
			//search in the project under test
		    Set<Class<?>> allClasses = InstanceGenerator.reflections.getSubTypesOf(Object.class);
		    for(Class<?> c : allClasses) {
		    	if(c.getSimpleName().toString().equals(simpleName))
		    		return c;         
		    }
		    
		    //search in the java.lang package
		    Reflections reflections1=InstanceGenerator.package2Reflections.get("java.lang");
			if(reflections1==null){
				File java_home = new File(System.getProperty("java.home"));
				File java_classes=new File(java_home.getParent()+"/Classes/classes.jar");
				if(InstanceGenerator.JAVA_VERSION>=1.7) 
					java_classes=new File(java_home.getParent()+"/jre/lib/rt.jar");
				//stdout.println(java_classes);
				URL[] urls = new URL[1];
				for(int i =0;i<1;i++){
					try {
						urls[i]=java_classes.toURL();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				
				reflections1 = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(false /* don't exclude Object.class */))
				.filterInputsBy(new FilterBuilder().includePackage("java.lang"))
				.setUrls(urls));
				InstanceGenerator.package2Reflections.put("java.lang", reflections1);
			}
		    allClasses = reflections1.getSubTypesOf(Object.class); //java.lang
		    //fqns = new ArrayList<Class>();
		    for(Class<?> c : allClasses) {
		    	if(c.getSimpleName().toString().equals(simpleName))
		    		return c;         
		    }
		    //}
		    return null;
		}
		
		private static Method methodDec2MethodRef(MethodDeclaration declaredMethod) throws ClassNotFoundException, IOException{
			Method reflexionMethod=null;
			reflexionMethod=astMethod2ReflexionMethod.get(declaredMethod);
 			if(reflexionMethod!=null)
 				return reflexionMethod;
 			
			IMethodBinding imb=declaredMethod.resolveBinding();
			ITypeBinding[] declaredParemeterType=null;
			ITypeBinding currentType;
			if(imb!=null){
				checkCUTCompatibility(declaredMethod);
				declaredParemeterType=imb.getParameterTypes();
			}
			else{
				return methodDec2MethodRefSimple(declaredMethod);
			}
			
			Method[] refelexionMethods=null;
			try{
					refelexionMethods= currentClassUnderTest.getClazz().getDeclaredMethods();
			}catch(ClassFormatError e){}
			catch(Exception e){}
			
			if(refelexionMethods==null)
				return null;

			
			List declaredParemeter=declaredMethod.parameters();
		    for (int m = 0 ; m < refelexionMethods.length ; m++) {
		    	
		    	reflexionMethod = refelexionMethods[m];
		        Class[] cParameterTypes = reflexionMethod.getParameterTypes();
		        String reflexionSimpleName=reflexionMethod.getName().toString();
		        reflexionSimpleName=reflexionSimpleName.replace("$", ".");
		        String []HirarchicalName=reflexionSimpleName.split("\\.");
		        if(HirarchicalName.length>0)
		        	reflexionSimpleName=HirarchicalName[HirarchicalName.length-1];
		        
		        if (!reflexionSimpleName.equals(declaredMethod.getName().toString())
		        		|| cParameterTypes.length!=declaredParemeter.size()) 
		        	continue;
		        
		        if(imb==null && cParameterTypes.length>0)
		        	return null;
		        
		        boolean matched=true;		        
	        	for (int p = 0 ; p < cParameterTypes.length ; p++){
	        		currentType=declaredParemeterType[p];       			
	        		while(currentType.isArray()||cParameterTypes[p].isArray()){
	        			if(!currentType.isArray()||!cParameterTypes[p].isArray()){
	        				matched=false;
		        			break;
	        			}
	        			cParameterTypes[p]=cParameterTypes[p].getComponentType();
	        			currentType=currentType.getComponentType();
	        		}

	        		if (currentType.isPrimitive()){
	        			if(!(getPrimitiveClass(currentType.getName()).isAssignableFrom(cParameterTypes[p]))){
	        				matched=false;
		        			break;
	        			}
	        		}else{
	        			boolean generic=false ;
	        			Class clstmp;
		        		String clsCompleteName=currentType.getBinaryName();
		        		if(!currentType.isMember()&&!currentType.isLocal())
		        			clsCompleteName=currentType.getQualifiedName();//.getBinaryName();
		        		
		        		//generic method
	        			if(imb.isGenericMethod()) {
	        				for (int n=0; n<imb.getTypeParameters().length;n++)
	        					if(imb.getTypeParameters()[n].getName().equals(currentType.getName())){
	        						generic=true;
	        						break;
	        					}
	        				if (generic)
	        					continue;
	        			}
	        			//generic class
	        			if(imb.getDeclaringClass().isGenericType()) {
	        				for (int n=0; n<imb.getDeclaringClass().getTypeParameters().length;n++)
	        					if(imb.getDeclaringClass().getTypeParameters()[n].getName().equals(currentType.getName())){
	        						generic=true;
	        						break;
	        					}
	        				if (generic)
	        					continue;
	        			}
    					if(currentType.isGenericType()||currentType.isParameterizedType()){
    						clsCompleteName=currentType.getErasure().getBinaryName();
    						clstmp=magicClassLoader.loadClass(ClassLoaderUtil.toCanonicalName(clsCompleteName));
    						//TODO more cases must be treated 
    						//currentType.getTypeArguments()[0].isWildcardType()
    						//currentType.getTypeArguments()[0].isUpperbound()
    						//currentType.isParameterizedType()
    						if (cParameterTypes[p].isAssignableFrom(clstmp) || clsCompleteName.equals(cParameterTypes[p].getCanonicalName()))
    							continue;
    						else{
    							matched=false;
    		        			break;
    						}
    					}

    					
    					clstmp=ClassLoaderUtil.getClass(ClassLoaderUtil.toCanonicalName(clsCompleteName));
		        		if (clstmp==null 
		        				||cParameterTypes==null 
		        				||cParameterTypes[p]==null
		        				|| (!clstmp.isAssignableFrom(cParameterTypes[p]) && !clsCompleteName.equals(cParameterTypes[p].getCanonicalName()))
		        				){
		        			matched=false;
		        			break;
		        		}
	        		}
	        	}
	        	if (matched) {
	        		astMethod2ReflexionMethod.put(declaredMethod, reflexionMethod);
	        		return reflexionMethod;
	        	}		        
		    }
		    //try {
		    System.err.println ("Mismatch error between the source code and the compiled class at the function: methodDec2MethodRef "+declaredMethod.getName());
			//} catch (Exception e) {
				//e.printStackTrace();
			//}
			return null;
		}

		private static Constructor methodDec2ConstructorRefSimple(MethodDeclaration declaredMethod) throws IOException{
			checkCUTCompatibility(declaredMethod);			
			Constructor reflexionMethod=null;	
			Constructor[] refelexionMethods = currentClassUnderTest.getClazz().getDeclaredConstructors();
			List declaredParemeter=declaredMethod.parameters();

		    for (int m = 0 ; m < refelexionMethods.length ; m++) {
		    	reflexionMethod = refelexionMethods[m];
		        Class[] cParameterTypes = reflexionMethod.getParameterTypes();
		        String reflexionSimpleName=reflexionMethod.getName().toString();
		        reflexionSimpleName=reflexionSimpleName.replace("$", ".");
		        String []HirarchicalName=reflexionSimpleName.split("\\.");
		        reflexionSimpleName=HirarchicalName[HirarchicalName.length-1];
		        if (!reflexionSimpleName.equals(declaredMethod.getName().toString())
		        		|| cParameterTypes.length!=declaredParemeter.size()) continue;
		        
		        boolean matched=true;		        
	        	for (int p = 0 ; p < cParameterTypes.length ; p++){
					Type currentType=((SingleVariableDeclaration)declaredMethod.parameters().get(p)).getType();
					String currentTypeString=currentType.toString();      			
					while(currentType.isArrayType()||cParameterTypes[p].isArray()){
	        			if(!currentType.isArrayType()||!cParameterTypes[p].isArray()){
	        				matched=false;
		        			break;
	        			}
	        			currentType=((ArrayType)currentType).getElementType();
	        			cParameterTypes[p]=cParameterTypes[p].getComponentType();
//	        			else {
//	        				stdout.println(ClassLoaderUtil.toCanonicalName(currentType.toString()));
//	        				stdout.println(cParameterTypes[p].toString());
//	        				cParameterTypes[p]=cParameterTypes[p].getComponentType();
//	        				currentTypeString=currentType.toString().substring(0, currentType.toString().length()-2);
//	        				//ITypeBinding itb=currentType.resolveBinding();
//	        				//currentType=(Type) itb.getComponentType();//.componentType;
//	        				//stdout.println(currentType.toString());
//	        			}
	        		} 
	        		if (currentType.isPrimitiveType()){
	        			if(!(cParameterTypes[p].isAssignableFrom(getPrimitiveClass(currentTypeString)))){
	        				matched=false;
		        			break;
	        			}
	        		}else{
	        			//boolean generic=false ;
	        			Class clstmp=null;
	        			String clsCompleteName=getQualifiedName(currentType.toString());
	        			if(!clsCompleteName.equals(""))
	        				try {
	        					clstmp=magicClassLoader.loadClass(clsCompleteName);
	        				} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
	        			else{
		        			clstmp=getClassFromSimpleName(currentType.toString());//possibleClasses.get(0);	        				
	        			}

		        		if (clstmp==null || cParameterTypes==null||cParameterTypes[p]==null||!(cParameterTypes[p].isAssignableFrom(clstmp))){
		        			matched=false;
		        			break;
		        		}
	        		}
	        	}
	        	if (matched) {
	        		astMethod2ReflexionConstructor.put(declaredMethod, reflexionMethod);
	        		return reflexionMethod;
	        	}		        
		    }
		    //try {
		    System.err.println ("Mismatch error between the source code and the compiled class at the function: methodDec2ConstructorRefSimple "+declaredMethod.getName());
			//} catch (Exception e) {
				//e.printStackTrace();
			//}
			return null;
		}

		private static Constructor methodDec2ConstructorRef(MethodDeclaration declaredMethod) throws ClassNotFoundException, IOException{
			//if(astMethod2ReflexionMethod.)
			Constructor reflexionConstructor=null;
			reflexionConstructor=astMethod2ReflexionConstructor.get(declaredMethod);
 			if(reflexionConstructor!=null)
 				return reflexionConstructor;
 			
 			
 			
			List declaredParemeter=declaredMethod.parameters();
			IMethodBinding imb=declaredMethod.resolveBinding();
			
			if(imb==null)
				return methodDec2ConstructorRefSimple(declaredMethod);
			else{
				checkCUTCompatibility(declaredMethod);
			}
			
			Constructor[] refelexionConstructors = currentClassUnderTest.getClazz().getDeclaredConstructors();
			ITypeBinding[] declaredParemeterType=imb.getParameterTypes();
			ITypeBinding currentType;

		    for (int c = 0 ; c < refelexionConstructors.length ; c++) {
		    	
		    	reflexionConstructor = refelexionConstructors[c];
		        Class[] cParameterTypes = reflexionConstructor.getParameterTypes();
		        
		        String reflexionSimpleName=reflexionConstructor.getName().toString();
		        reflexionSimpleName=reflexionSimpleName.replace("$", ".");
		        String []HirarchicalName=reflexionSimpleName.split("\\.");
		        reflexionSimpleName=HirarchicalName[HirarchicalName.length-1];

		        if (!reflexionSimpleName.equals(declaredMethod.getName().toString())
		        		|| cParameterTypes.length!=declaredParemeter.size()) continue;
		        
		        boolean matched=true;
	        	for (int p = 0 ; p < cParameterTypes.length ; p++){
	        		currentType=declaredParemeterType[p];       			
	        		while(currentType.isArray()||cParameterTypes[p].isArray()){
	        			if(!currentType.isArray()||!cParameterTypes[p].isArray()){
	        				matched=false;
		        			break;
	        			}
	        			else {
	        				cParameterTypes[p]=cParameterTypes[p].getComponentType();
	        				currentType=currentType.getComponentType();
	        			}
	        		}

	        		if (currentType.isPrimitive()){
	        			if(!(cParameterTypes[p].isAssignableFrom(getPrimitiveClass(currentType.getName())))){
	        				matched=false;
		        			break;
	        			}
	        		}
	        		else{
	        			boolean generic=false ; 
	        			Class clstmp;
	        			String clsCompleteName=currentType.getBinaryName();
		        		
		        		//generic method
	        			if(imb.isGenericMethod()) {
	        				for (int n=0; n<imb.getTypeParameters().length;n++)
	        					if(imb.getTypeParameters()[n].getName().equals(currentType.getName())){
	        						generic=true;
	        						break;
	        					}
	        				if (generic)
	        					continue;
	        			}
	        			//generic class
	        			if(imb.getDeclaringClass().isGenericType()) {
	        				for (int n=0; n<imb.getDeclaringClass().getTypeParameters().length;n++)
	        					if(imb.getDeclaringClass().getTypeParameters()[n].getName().equals(currentType.getName())){
	        						generic=true;
	        						break;
	        					}
	        				if (generic)
	        					continue;
	        			}
    					if(currentType.isGenericType()||currentType.isParameterizedType()){
    						clsCompleteName=currentType.getBinaryName();
    						clstmp=magicClassLoader.loadClass(ClassLoaderUtil.toCanonicalName(clsCompleteName));
    						//TODO more cases must be analyzed 
    						//currentType.getTypeArguments()[0].isWildcardType()
    						//currentType.getTypeArguments()[0].isUpperbound()
    						//currentType.isParameterizedType()
    						if (cParameterTypes[p].isAssignableFrom(clstmp))
    							continue;
    					}

	        			clstmp=magicClassLoader.loadClass(ClassLoaderUtil.toCanonicalName(clsCompleteName));
	        		if (clstmp==null || cParameterTypes==null||cParameterTypes[p]==null
	        				||(!cParameterTypes[p].isAssignableFrom(clstmp) && !clsCompleteName.equals(cParameterTypes[p].getCanonicalName()))
	        				){
	        			matched=false;
	        			break;
	        		}
	        		}
	        	}
	        	if (matched) {
	        		astMethod2ReflexionConstructor.put(declaredMethod, reflexionConstructor);
	        		return reflexionConstructor;
	        	}		        
		    }
		    try {
				System.err.println ("Mismatch error between the source code and the compiled class at the function: methodDec2ConstructorRef "+declaredMethod.getName());
			} catch (Exception e) {
				//e.printStackTrace();
			}
			return null;
		}

		
		public static Path getCurrentPathTarget() {
			return currentPathTarget;
		}

		public static void setCurrentPathTarget(Path path) {
			JTE.currentPathTarget = path;
		}

		public static IVariableBinding getVariableBinding(Method method, int paramIndex) {
			MethodDeclaration cMethod=null;
			MethodDeclaration[] classMethods =branchCoderAnalyser.getClassNode().getMethods();// clsUT.getMethods();
			Class[] declaredParemeterTypes=method.getParameterTypes();
			
		    for (int i = 0 ; i < classMethods.length ; i++) {
		    	cMethod = classMethods[i];
		    	List cParametersType = cMethod.parameters();
		        
		        if (!cMethod.getName().toString().equals(method.getName().toString())
		        		|| cParametersType.size()!=declaredParemeterTypes.length) continue;
		        
		        boolean matched=true;
	        	for (int j = 0 ; j < cParametersType.size() ; j++)
	        		if (!(cParametersType.get(j).getClass().isAssignableFrom(declaredParemeterTypes[j]))){
	        			matched=false;
	        			break;
	        		}
	        	if (matched) {
	        		return ((VariableDeclaration)cMethod.parameters().get(paramIndex)).resolveBinding();
	        	}		        
		    }
		    
			return null;
		}

		public static int getInfluence(IVariableBinding iVariableBinding,
				int branchID) {
			if(branchCoderAnalyser.getBranch2BlockMap().get(branchID).getProperty("influencers")!=null)
				if(((Vector<IVariableBinding>)branchCoderAnalyser.getBranch2BlockMap().get(branchID).getProperty("influencers")).contains(iVariableBinding))
				return 1;
			//influenceAnalyser
			return 0;
		}
		
	    public static final Class<?> getPrimitiveClass(String typeName) {
	        if (typeName.equals("byte"))
	            return byte.class;
	        if (typeName.equals("short"))
	            return short.class;
	        if (typeName.equals("int"))
	            return int.class;
	        if (typeName.equals("long"))
	            return long.class;
	        if (typeName.equals("char"))
	            return char.class;
	        if (typeName.equals("float"))
	            return float.class;
	        if (typeName.equals("double"))
	            return double.class;
	        if (typeName.equals("boolean"))
	            return boolean.class;
	        if (typeName.equals("void"))
	            return void.class;
	        throw new IllegalArgumentException("Not primitive type : " + typeName);
	    }
//	    
//	    public void deleteClasses(File folder) throws IOException {
//	        File[] files = folder.listFiles();
//	        for(int i=0;i<files.length;i++){
//	            if(files[i].isFile() && files[i].getName().endsWith(".class")){
//	            	files[i].delete();
//	            }
//	        }
//	    }

	    
	    private static void copyFileUsingStream(String source, String dest) throws IOException{
	    	if(source.equals(dest))
	    		return;
		    File originalFile=new File(source);
		    if(!originalFile.exists())
		    	return;
			File backupFile=new File(dest);   
			backupFile.getParentFile().mkdirs();
			FileEditor.copyFileUsingStream(originalFile,backupFile);
	    }
	    
		public static void main(String[] args) throws Exception{				
			String usageTxt="JTExpert -jf Java_unit_under_test  [-cp classpath] [-maxTime search_time_in_seconds] " + System.getProperty("line.separator") +"";
			
			//usageTxt+="-sp	: source path directories separated by commas " + System.getProperty("line.separator") +"";
			//usageTxt+="-jc 	: java class under test name  " + System.getProperty("line.separator") +"";
			usageTxt+="-jf 	: use it to set the Java file under test. JTExpert Generates test data suite for this file;" + System.getProperty("line.separator") +"";
			//usageTxt+="-jd 	: java directory under test. Generate test data for any java file in this directory or in any sub directory " + System.getProperty("line.separator") +"";//interfaces or abstracts classes mapping file. Each line in this file must contains to classes name separated by commas the second one is a stub to instantiate of the first one  " + System.getProperty("line.separator") +""; 
			usageTxt+="-cp 	: use it to set the class path. Paths have to be separated by the system pathseparator (e.g., for linux is :);  " + System.getProperty("line.separator") +"";
			//usageTxt+="-bp 	: binary path directories separated by commas its default value is the first path in cp. Where putting the generated binary files.  " + System.getProperty("line.separator") +"";			
			usageTxt+="-tp 	: use it to set the work directory, wherein the test suite will be saved;  " + System.getProperty("line.separator") +"";			
			usageTxt+="-maxTime : sets the max search time. " + System.getProperty("line.separator") +"";
			usageTxt+="-o	: use it to override an existing test data file ; " + System.getProperty("line.separator") +"";
			usageTxt+="-p	: use it to disply a progress bars; " + System.getProperty("line.separator") +"";
			usageTxt+="-s 	: use it to show messages and errors thrown by the class under test; " + System.getProperty("line.separator") +"";
			usageTxt+="-seed: seed to use for the random number generator. " + System.getProperty("line.separator") +""; 
			usageTxt+="-E	: use it to activate the Exception-Otriented Test-Data Generation search. " + System.getProperty("line.separator") +"";
			//usageTxt+="-gp 	: genetic algorithm parameters file. The default file is saved with JTE in its home directory  " + System.getProperty("line.separator") +"";
			//usageTxt+="-im 	: interfaces or abstracts classes mapping file. Each line in this file must contains to classes name separated by commas the second one is a stub to instantiate of the first one  " + System.getProperty("line.separator") +""; 

			
	        int i = 0;
	        String arg;
	        while (i < args.length && args[i].startsWith("-")) {
	            arg = args[i++];
	            if (arg.equals("-sp")) 
	            	if (i < args.length) srcPath0=args[i++];
	            	else{
	                    System.err.println(arg+" requires a value");
	                    System.exit(-1);
	    	        }
	            if (arg.equals("-jf")) 
	            	if (i < args.length) {javaFileName=args[i++]; originalFileName=javaFileName;}
	            	else{
	                    System.err.println(arg+" requires a value");
	                    System.exit(-1);
	    	        }
	            if (arg.equals("-jd")) 
	            	if (i < args.length) javaDirectoryName=args[i++];
	            	else{
	                    System.err.println(arg+" requires a value");
	                    System.exit(-1);
	    	        }
	            if (arg.equals("-cp")) //
	            	if (i < args.length)classPath0=args[i++];
	            	else{
	                    System.err.println(arg+" requires a value");
	                    System.exit(-1);
	    	        }
	            if (arg.equals("-tc")) //testCasesPath
	            	if (i < args.length)testCasesPath=args[i++];
	            	else{
	                    System.err.println(arg+" requires a value");
	                    System.exit(-1);
	    	        }
	            if (arg.equals("-tp")) 
	            	if (i < args.length)jteOutputPath=args[i++];
	            	else{
	                    System.err.println(arg+" requires a value");
	                    System.exit(-1);
	    	        }
	            if (arg.equals("-gp")) 
	            	if (i < args.length)gaParametersFile=args[i++];
	            	else{
	                    System.err.println(arg+" requires a value");
	                    System.exit(-1);
	    	        }
	            if (arg.equals("-im")) 
	            	if (i < args.length)interfacesFile=args[i++];
	            	else{
	                    System.err.println(arg+" requires a value");
	                    System.exit(-1);
	    	        }
	            if (arg.equals("-ppp")) 
	            	if (i < args.length)projectPackagesPrefix=args[i++];
	            	else{
	                    System.err.println(arg+" requires a value");
	                    System.exit(-1);
	    	        }
	            if (arg.equals("-hn")) 
	            	if (i < args.length)heuristicName=args[i++];
	            	else{
	                    System.err.println(arg+" requires a value");
	                    System.exit(-1);
	            	}
	            if (arg.equals("-seed")) {
		            	String seedstr="";
		            	if (i < args.length)
		            		seedstr=args[i++];
		            	else{
		                    System.err.println(arg+" requires a value "+ seedstr);
		                    System.exit(-1);
		    	        }
		            	
		            	if(seedstr.length()>9) {seedstr=seedstr.substring(0,9);}
		            		seed=Integer.parseInt(seedstr);
		            	
		            }
	           if (arg.equals("-maxEval"))
		            	if (i < args.length) maxEvaluations=Integer.parseInt(args[i++]);
		            	else{
		                    System.err.println(arg+" requires a value ");
		                    System.exit(-1);
		    	        }	 
	            if (arg.equals("-maxTime"))
	            	if (i < args.length) {maxTime=Integer.parseInt(args[i++]);}// maxTime-=5;}
	            	else{
	                    System.err.println(arg+" requires a value ");
	                    System.exit(-1);
	    	        }
	            if (arg.equals("-icp"))
	            	if (i < args.length) instrumentedClassPath=args[i++];
	            	else{
	                    System.err.println(arg+" requires a value ");
	                    System.exit(-1);
	    	        }
	            if (arg.equals("-cname"))
	            	if (i < args.length) className=args[i++];
	            	else{
	                    System.err.println(arg+" requires a value ");
	                    System.exit(-1);
	    	        }
	            
		        if (arg.equals("-o")) overrideExistTestCase=true;
		        if (arg.equals("-i")) instrument=false; //=Boolean.parseBoolean(args[i++]);
		        if (arg.equals("-p")) printProgress=true; //=Boolean.parseBoolean(args[i++]);
	            if (arg.equals("-s")) showErrors=true; //=Boolean.parseBoolean(args[i++]);
		        if (arg.equals("-E")) ExceptionsOriented=true; //Boolean.parseBoolean(args[i++]);
		        if (arg.equals("-fE")) firstExcution=false;
		        if (arg.equals("-aTD")) automaticTimeDetermination=true;
		        
	        }
		        
	        boolean parssingErreur=false;
		    if((srcPath0==null||srcPath0.equals(""))
		    		&& (javaFileName==null||javaFileName.equals(""))
		    		&& ( javaDirectoryName==null||javaDirectoryName.equals("")))  {
		    	parssingErreur=true;
		    	System.err.println("A source path directory is required");
		    }
		    if((className==null || className.equals(""))
		    		&& (javaFileName==null||javaFileName.equals(""))
		    		&& ( javaDirectoryName==null||javaDirectoryName.equals("")))  {
		    	parssingErreur=true;
		    	System.err.println("A class name is required");
		    }	
	        if (parssingErreur){
	            System.err.println("Usage: " + System.getProperty("line.separator") +""+ usageTxt);
	            System.exit(-1);
	        }
	        
	        if(gaParametersFile==null||gaParametersFile.equals("")){
	        	File relativeJTEPath = new File("");
				String absolutJTEPath=relativeJTEPath.getAbsolutePath();
	        	gaParametersFile=absolutJTEPath+File.separator+"testing.params";
	        }
//	        if(projectPath==null||projectPath.equals("")){
//	        	projectPath=srcPath0.split(";")[0];
//	        	projectPath=projectPath.substring(0, projectPath.lastIndexOf("/"));
//	        }       
//	        if(binPath==null||binPath.equals("")){
//	        	binPath=projectPath+File.separator+"bin"+File.separator;
//	        }
	        
	        if(jteOutputPath==null||jteOutputPath.equals("")){
	        	File relativeJTEPath = new File("");
				String absolutJTEPath=relativeJTEPath.getAbsolutePath();
				jteOutputPath=absolutJTEPath+File.separator+"jteOutput";
	        }
	        if(testCasesPath==null||testCasesPath.equals(""))
	        	testCasesPath=jteOutputPath+File.separator+"testcases";
	        //instrumentationPath=jteOutputPath+File.separator+"instrumeted";
	        //backupPath=jteOutputPath+File.separator+"backup";
	        
	        binPath=jteOutputPath+File.separator+"bin";
	        //deleteDirectory(binPath);
	        
	        //binPath=projectPath+File.separator+"bin";
	        
	        //fl.delete();fl.
	        srcPath=jteOutputPath+File.separator+"src";

	        //if(classPath0==null||classPath0.equals(""))
	        //classPath0=binPath+File.separator+File.pathSeparator+classPath0;	        
			classPath=classPath0.split(File.pathSeparator);
//			if(classPath0.contains(":"))
//				classPath=classPath0.split(":");
			//load class path
			//loadClassPath();			
			
			//stdout=System.out;
			//stderr=System.err;
			//className=cName;
			
			//System.getProperty("java.io.tmpdir");
			//System.setProperty("user.dir", System.getProperty("java.io.tmpdir"));
			
			
			if(javaFileName!=null&&!javaFileName.equals("")){
				generateTestData4JavaSrcFile();			
			}else if(className!=null && !className.equals("")){
				//allSrcPaths=srcPath0.split(";");
				String[] clsNameHierarchy =className.split("\\.");
				srcFileName=clsNameHierarchy[clsNameHierarchy.length-1];
				
				packageName=className.substring(0, className.lastIndexOf("."));
				subPath=packageName.replace(".", File.separator);
				
				javaFileName=srcPath0+File.separator+subPath+File.separator+srcFileName+".java";
				copyFileUsingStream(javaFileName,srcPath+File.separator+subPath+File.separator+srcFileName+".java");
				javaFileName=srcPath+File.separator+subPath+File.separator+srcFileName+".java";
				
				ASTBuilder ASTRoot=new ASTBuilder(classPath0, srcPath, javaFileName);
				compilationUnit=ASTRoot.getASTRoot();
				
				if(Initialize())
					generateTestData();
			}else{
				generateTestData4JavaSrcPath(javaDirectoryName);
			}
			SystemExitControl.enableSystemExitCall();
		}

		private static void generateTestData4JavaSrcPath(String javaSrcPath) throws Exception{
			File srcDirectory = new File(javaSrcPath);
			File[] files =srcDirectory.listFiles();
			for(int f=0;f<files.length;f++){
				if(files[f].isFile() && files[f].getName().endsWith(".java")){
					javaFileName=files[f].toString();
					generateTestData4JavaSrcFile();
				}
				if(files[f].isDirectory()){
					generateTestData4JavaSrcPath(files[f].toString());
				}
			}
		}
		
		private static void generateTestData4JavaSrcFile() throws Exception{
			//open result file			
			ActualstartTime=System.currentTimeMillis();
			srcFileName=javaFileName.substring(javaFileName.lastIndexOf(File.separator)+1,javaFileName.length());
			srcFileName=srcFileName.substring(0, srcFileName.length()-5); 
			
			//load the java file to get the actual package
			ASTBuilder ASTRoot=new ASTBuilder(null, null, javaFileName);
			compilationUnit=ASTRoot.getASTRoot();
			
			if(compilationUnit.getPackage()!=null){
				packageName=compilationUnit.getPackage().getName().toString();
				if(projectPackagesPrefix==null || projectPackagesPrefix=="")
					projectPackagesPrefix=packageName;
				if(!packageName.contains(projectPackagesPrefix))
					projectPackagesPrefix=packageName;
			}else
				packageName="";
			
			subPath=packageName.replace(".", File.separator);
			
			className=srcFileName;
			if(!packageName.equals(""))
				className=packageName+"."+srcFileName;
			
			
			binPath=binPath+File.separator+className;
			File fbin=new File(binPath);
	        fbin.mkdirs();
			classPath0=binPath+File.separator+File.pathSeparator+classPath0;
			classPath=classPath0.split(File.pathSeparator);
			loadClassPath();
			
			String fileTC=testCasesPath+File.separator+ subPath+File.separator+srcFileName+TEST_CASES_SURFIX+".java";
			File f = new File(fileTC);
			if(!overrideExistTestCase && f.exists()){
				System.out.println("A test case file already exists in the distination diectory " + System.getProperty("line.separator") +"("+testCasesPath+File.separator+ subPath+File.separator+srcFileName+TEST_CASES_SURFIX+".java"+"). " + System.getProperty("line.separator") +"To override it change the option overrideExistTestCase into true");
				return;
			}
			
			copyFileUsingStream(javaFileName,srcPath+File.separator+subPath+File.separator+srcFileName+".java");
			originalFileName=javaFileName;
			javaFileName=srcPath+File.separator+subPath+File.separator+srcFileName+".java";
			
			
			
			ASTRoot=new ASTBuilder(classPath0, srcPath, javaFileName);
			compilationUnit=ASTRoot.getASTRoot();
			
			redirectStdOutput();
			//System.out.println("Preventing System.exit");
			SystemExitControl.forbidSystemExitCall();
			if(Initialize()){
				generateTestData();
			}
		}
		

		//private static
		private static void loadClassPath() throws Exception{
			List<URL> pathList=new ArrayList<URL>();
//			//second put all class path required for CUT 
			for(String p:classPath){ 
				pathList.add((new File(p)).toURL());
			}
			
			pathList.addAll(classPathList);
//			//now instantiate class under test from the instrumented unit.			
			URL[] urls = new URL[pathList.size()];
			urls=pathList.toArray(urls);
			
			magicClassLoader =new URLClassLoader(urls);// JTEClassLoader(null,urls);//Thread.currentThread().getContextClassLoader();// 						
			Thread.currentThread().setContextClassLoader(magicClassLoader);
		}
	    
//	    public static List<URL> getJarListInLibDir(File lib)throws Exception{
//	    	List<URL> pathList=new ArrayList<URL>();
//	    	
//	        File[] files = lib.listFiles();
//	        for(int i=0;i<files.length;i++){
//	            if(files[i].getName().endsWith(".jar")){ //files[i].isFile() && 
//	            	pathList.add(files[i].toURL());
//	            }else if(files[i].isDirectory()){
//	            	pathList.addAll(getJarListInLibDir(files[i]));
//	            }
//	        }
//	        return pathList;
//	    }

//		public static void deleteDirectory(String directory2Delete){
//			File srcDirectory = new File(directory2Delete);
//			if(!srcDirectory.exists())
//				return;
//			File[] files =srcDirectory.listFiles();
//			if(files==null)
//				return;
//			for(int f=0;f<files.length;f++){
//				if(files[f].isFile())
//					files[f].delete();
//				if(files[f].isDirectory())
//					deleteDirectory(files[f].toString());
//			}
//			srcDirectory.delete();
//		}
		
		public static void setClassUnderTest(Class cls) throws IOException {
			while((cls.getDeclaringClass()!=null)
					&&((Modifier.isPrivate(cls.getModifiers()))||(Modifier.isProtected(cls.getModifiers())))){
				cls=cls.getDeclaringClass();
			}
			
			currentClassUnderTest=classesUnderTest.get(cls.getCanonicalName());
			if(currentClassUnderTest ==null){
				currentClassUnderTest=new ClassUnderTest(cls);
				
				classesUnderTest.put(cls.getCanonicalName(),currentClassUnderTest);
				ClassUnderTest savecut=currentClassUnderTest;
				currentClassUnderTest.prepareDataMembers();
				currentClassUnderTest=savecut;
			}
			
		}
		
		private static void redirectStdOutput(){
	        // initialize logging to go to rolling log file
	        LogManager logManager = LogManager.getLogManager();
	        logManager.reset();

	        //FileHandler.pattern=c:/logs/myApp.log  ;
	        // log file max size 10K, 3 rolling files, append-on-open
	        Handler fileHandler=null ;
	        //Handler 
	        try {
	        	File logsDir=new File(jteOutputPath);
	        	String dir=logsDir.getAbsolutePath()+File.separator+"logs"+File.separator+className;
	        	logsDir=new File(dir);
	        	logsDir.mkdirs();
				fileHandler = new FileHandler(logsDir.getAbsolutePath()+File.separator+"jte%u.log", 10000, 3, true); //"logs"
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        fileHandler.setFormatter(new SimpleFormatter());
	        Logger.getLogger("").addHandler(fileHandler);
	 
	        // preserve old stdout/stderr streams in case they might be useful      
	        stdout = System.out;                                        
	        stderr = System.err;                                        

	        // now rebind stdout/stderr to logger                                   
	        Logger logger;                                                          
	        LoggingOutputStream los;         

	        logger = Logger.getLogger("stdout");                                    
	        los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);  
	        if(!showErrors)
	        	System.setOut(new PrintStream(los, true));                              

	        logger = Logger.getLogger("stderr");                                    
	        los= new LoggingOutputStream(logger, StdOutErrLevel.STDERR);     
	        if(!showErrors)
	        	System.setErr(new PrintStream(los, true));  			
		}
}

class KillMeAfterTimeOut {
		private static long timeOut;
		//private long startTime;
		public KillMeAfterTimeOut(long to){
			//if(true)
			//	return;
			timeOut=to;
			//startTime=System.currentTimeMillis();
			//TimerTasck tt=
			new Timer(true).schedule(
					new TimerTask() {
			      public void run() {
			    	  System.err.println("Forced shutdown. saving test data for: "+JTE.currentClassUnderTest.getClazz().getName());
			    	  JTE.writeTestCases();
			      }
			    }, 1000*(timeOut));
		}

	}

class TestDataSaver {
	private static long timeOut;
	//private long startTime;
	public TestDataSaver(long to){
		timeOut=to;
		long pas=timeOut/3;
		if(pas<30)
			pas=30;
		
		long schedule=timeOut;
		//while (schedule>pas){
			new Timer(true).schedule(new TimerTask() {
			      public void run() {
			    	  //System.err.println("Forced shutdown. saving test data for: "+JTE.currentClassUnderTest.getClazz().getName());
			    	  JTE.writeTestCases(false);
			    	  String fileName=JTE.testCasesPath+File.separator+ JTE.subPath+File.separator+JTE.srcFileName+JTE.TEST_CASES_SURFIX+".java";
			    	 
			    	  
			    	  //JTE.correctExceptions( fileName,JTE.className+JTE.TEST_CASES_SURFIX, "Split",true); 
			    	  //File so =new File(fileName);
			    	  //File de =new File(fileName+".root");
			    	  //if()
//	        		try {
//						Files.move(so, de);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
			      }
			    }, 1000*(pas));
		//	schedule-=pas;
		//}
		
	}

}


class TestCaseCorrector {
	public TestCaseCorrector(final String fileName,final String className){
		new Timer(true).schedule(
				new TimerTask() {
		      public void run() {
		    	  JTE.correctExceptions( fileName,className, "Split",true); 
		      }
		    }, 10);
	}

}

class ProgramWithExitCall {
	 
    public static void main(String[] args) {
    	
        System.exit(2);
    }
}

//class ShutdownInterceptor extends Thread {
//		public void run() {
//			JTE.writeTestCases();
//		}
//	}