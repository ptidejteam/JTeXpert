package csbst.testing;

//import GenerateTestData;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;







import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
//import org.eclipse.jdt.core.dom.AST;
//import org.eclipse.jdt.core.dom.Statement;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
//import org.junit.rules.Timeout;

import com.google.common.io.Files;





//import csbst.heuristic.RandomTesting;
//import csbst.generators.dynamic.AbstractDynamicGenerator;
//import csbst.utils.ASTEditor;
import csbst.analysis.CUTAnalyser;
import csbst.generators.AbsractGenerator;
import csbst.utils.ClassPathHacker;
import csbst.utils.ExceptionsFormatter;
import csbst.utils.FileEditor;

public class JUnitExecutor {
	//static String javaSourcePath;
	//static String javaClassPath;
	static String javaClassName;
	static String oldClassName;
	static String newClassName;
	static String subPath;
	static String javaSourceFile;
	static String testCasesPath;
	static String binPath;
	static String classPath;
	static boolean lastExecution=false;
	static boolean splitTestCases=false;
	static boolean executeOneTestCaseAtTime=false;
	static final int COMPILATION_ERROR=255;
	static final int TIMEOUT_ERROR=254;
	static final int STOP_REQUEST=253;
	static final long CLASS_TIMEOUT=30000; //35s
	static final long TESTCASE_TIMEOUT=2000; //3s
	static final String BACKUP_SURFIX=".backup";
	static final String TMP_SURFIX=".tmp";
	static int returnValue=0; 
	static int version=0;//3s
	//static String[] classPath;
	static List<URL> pathList=new ArrayList<URL>();
	static Vector<Integer> wrongAssertionMethods=new Vector<Integer>(); 
	static Vector<Integer> timeOutMethods=new Vector<Integer>();
	static Vector<Integer> cleanMethods=new Vector<Integer>();
	static String[] arguments;
	public static boolean externalCall=true;
	
	static String wrongAssertionMethodsFileName;
	
	public static Thread killerThread;
	public static ConcurrentLinkedQueue<Thread> killerQueue = new ConcurrentLinkedQueue<Thread>();
	static{
		killerThread = new Thread(){ 
		@Override public void run (){		
				while(true){
					Thread t;
					while ((t  = killerQueue.poll()) != null) {					
						if(t.isAlive()){
							t.interrupt();
							
							if(t.isAlive()){
								//t.stop();
								killerQueue.add(t);
							}
							 
						}
					
//						try {
//							sleep(50);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
			         }
				}
			}
		};
		killerThread.setPriority(Thread.MIN_PRIORITY);
	}
	public static void main(String[] args) throws Exception{	
	
		///Applications/eclipseLuna/WorkSpace/JTestExpert/jteOutput/testcases/org/apache/commons/lang/text
//		if(args==null || args.length<6)
//			args=new String[]{"org.apache.commons.lang.text.StrBuilderJTETestCases_0_0",
//					        "Applications/eclipseLuna/WorkSpace/JTestExpert/jteOutput/testcases/org/apache/commons/lang/text/StrBuilderJTETestCases_0_0.java", 
//							"/Applications/eclipseLuna/WorkSpace/Lang-61/bin/",
//							"/Applications/eclipseLuna/WorkSpace/Lang-61/bin/:/Applications/eclipseLuna/WorkSpace/JTestExpert/jteOutput/bin/org.apache.commons.lang.text.StrBuilder/:/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/JTExpert/Lang-61/target/classes/:/Applications/eclipseLuna/WorkSpace/JTestExpert/:/Applications/eclipseLuna/WorkSpace/JTestExpert/bin/:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/choco/choco-solver-2_1_2.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/commonsLang/commons-lang3-3.3.2.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/ecj/ECJ-modified.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.core.contenttype_3.4.100.v20110423-0524.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.core.jobs_3.5.101.v20120113-1953.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.core.resources_3.7.101.v20120125-1505.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.core.runtime.compatibility_3.2.100.v20100505.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.core.runtime.source_3.7.0.v20110110.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.core.runtime_3.7.0.v20110110.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.equinox.common_3.6.0.v20110523.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.equinox.preferences_3.4.2.v20120111-2020.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.jdt.compiler.apt_1.0.400.v0110816-0800.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.jdt.compiler.tool_1.0.100.v_B79_R37x.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.jdt.core_3.7.3.v20120119-1537.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.jdt.ui_3.7.2.v20120109-1427.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.jface.source_3.7.0.v20110928-1505.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.jface.text.source_3.7.2.v20111213-1208.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.jface.text_3.7.2.v20111213-1208.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.jface_3.7.0.v20110928-1505.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.osgi_3.7.2.v20120110-1415.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.swt.cocoa.macosx.x86_64_3.7.2.v3740f.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.text_3.5.101.v20110928-1504.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/eclipse/org.eclipse.ui_3.7.0.v20110928-1505.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/junit/junit.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/junit/org.hamcrest.core_1.1.0.v20090501071000.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/reflections/lib/guava-14.0.1.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/reflections/lib/javassist-3.12.GA/javassist.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/reflections/lib/slf4j-1.7.5/slf4j-api-1.7.5.jar:/Applications/eclipseLuna/WorkSpace/JTestExpert/lib/reflections/reflections-0.9.9-RC1.jar:"+
//							"/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/JTExpert/lib/junit/junit.jar:"+
//							"/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/JTExpert/lib/junit/hamcrest.jar",
//							"split",
//							"/Applications/eclipseLuna/WorkSpace/Lang-61/src"
//							};
////		String s="\ufffd";
////		s=StringEscapeUtils.escapeJava(s);
//		for (int i =0;i<args.length; i++)
//			System.out.println("args: "+i +"  "+args[i]);
		
//	
		////Lang-61
//		args=new String[]{"-jf", "/Applications/eclipseLuna/WorkSpace/Lang-61/src/org/apache/commons/lang/text/StrBuilder.java", // commons-lang3-3.1.jar barbecue-1.5-beta1.jar
//				"-cp", "/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/JTExpert/Lang-61/target/classes/",
//				"-maxTime", "40",
//				"-o",
//				"-p",
//				"-s"
//				}; 
		
		arguments=args;

		
		javaClassName=args[0];
		subPath=javaClassName.replace(".", File.separator);
		oldClassName=javaClassName.substring(javaClassName.lastIndexOf(".")+1, javaClassName.length());
		
		String ver=javaClassName.substring(javaClassName.indexOf("_")+1, javaClassName.lastIndexOf("_"));
		//System.err.println("ver :"+ ver);
		version=Integer.parseInt(ver);
		
		newClassName=oldClassName;
		newClassName=newClassName.replace("_"+version+"_", "_"+(version+1)+"_");;
		javaSourceFile=args[1];
		binPath=args[2];
		classPath=args[3];		
		if(args[4].equalsIgnoreCase("split"))
			splitTestCases=true;
		testCasesPath=args[5];
		//System.out.println(wrongMethodsFileName);
		if(args.length>=7){
			wrongAssertionMethodsFileName=args[6];
			//System.err.println("wrongAssertionMethodsFileName: "+wrongAssertionMethodsFileName);
			readWrongMethods(wrongAssertionMethodsFileName);
			//System.err.println("wrongAssertionMethods: "+wrongAssertionMethods);
			
			executeOneTestCaseAtTime=true;
		}
		
		if(args.length>=8)
			if(args[7].equalsIgnoreCase("stop"))
				lastExecution=true;
		//System.out.print(wrongMethods);
		
		startCorrection();
	}
	
	static void startCorrection() throws Exception{
		pathList=ClassPathHacker.loadClassPath(classPath);
		
		 String[] tableClasspath=classPath.split(File.pathSeparator);
		 List<String>listClasspath=Arrays.asList(tableClasspath);
		boolean compilation=CUTAnalyser.compileJUnitFile( javaSourceFile,  binPath,listClasspath);
		
		//if(!compileJUnitFile()){
		if(!compilation){
			returnValue=0;//COMPILATION_ERROR;
			//return;
			if(externalCall)
				System.exit(COMPILATION_ERROR);
			else
				return;
		}
		
		//save a copy
        //FileEditor.copyFileUsingStream(new File(javaSourceFile), new File(javaSourceFile+BACKUP_SURFIX));
        int nmw;
        
		if(lastExecution){
	    	if(executeOneTestCaseAtTime)	
	    		nmw=runTestsOneAtTime();
	    	else
	    		nmw=runTests();
	    	
	    	if(nmw==0 && externalCall)
	    		System.exit(version);//(nmw);
	    	else{
	    		returnValue=0; //STOP_REQUEST;
	    		return;
	    	}
			//System.exit(STOP_REQUEST);
		}
				
		
    	if(executeOneTestCaseAtTime)	
    		nmw=runTestsOneAtTime();
    	else
    		nmw=runTests();
    	
    	//System.err.println("version: "+version); && nmw!=COMPILATION_ERROR
    	if(nmw!=0 ){
    		//System.err.println("+++++++++++++++++++++++: "+nmw);
    		arguments[0]=javaClassName.replace("_"+version+"_", "_"+(version+1)+"_");
    		arguments[1]=javaSourceFile.replace("_"+version+"_", "_"+(version+1)+"_");
    		
    		version++;   
    		main(arguments);
    		//startCorrection();
    	}

		
    	
    	if(externalCall)
    		System.exit(version); //System.exit(nmw);
    	
    	version=0;
    	returnValue=0;//nmw;
		return;
		
	} 

	private static Class getTestCasesClass(){
		//System.err.println ("Execution: ");
		URL[] urls = new URL[pathList.size()];
		urls=pathList.toArray(urls);		
		
		URLClassLoader urlClassLoader =new URLClassLoader(urls);// JTEClassLoader(null,urls);//Thread.currentThread().getContextClassLoader();// 						
		Thread.currentThread().setContextClassLoader(urlClassLoader);
		Class testCasesClass=null;
		try {
			//String urlName=javaClassName.replaceFirst("\\.", "/");
			testCasesClass = urlClassLoader.loadClass(javaClassName); 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		return testCasesClass;
	}
	
	private static int runTests(){		
		Class testCasesClass=getTestCasesClass();
		if(testCasesClass==null){
			System.err.println("Could not load the class");
			return 0;
		}
			//boolean toDrop=false;
			//final Vector<String> cleanMethods=new Vector<String>();
			Vector<String> methodsToDrop=new Vector<String>();
			final Class tccp=testCasesClass;
			final Set<Integer>wrongAssertionsLines=new HashSet<Integer>();
			final Map<Integer,String>goodTryCatchLines=new HashMap<Integer,String>();
				int sizeBeforeExecution=wrongAssertionsLines.size()+goodTryCatchLines.size();
					Thread thread = new Thread(){  
						@Override public void run (){
									Result result = new JUnitCore().run(tccp);
									for( Failure f:result.getFailures()){
										//System.out.println(f.getException());
										//if(f.getException().getStackTrace().length<2)
										//	continue;
											//System.out.println(f.getException());
											f.getException().printStackTrace();
										ExceptionsFormatter.ExceptionFormat exception=new ExceptionsFormatter.ExceptionFormat(f.getException(),javaClassName,false);
										if(exception.getLine()!=""){
											int value=Integer.parseInt(exception.getLine());
											if(f.getException().getClass().equals(ComparisonFailure.class)
													||AssertionError.class.isAssignableFrom(f.getException().getClass())
													|| f.getException().getClass().equals(Assert.class)){
												
												wrongAssertionsLines.add(value); 
											}else{
												goodTryCatchLines.put(value, f.getException().getClass().getName());
												}
											}else{
												//a big problem with exceptions that do not have any details
												
												//System.err.println("all-----------Message:- "+f.getMessage());
												//System.err.println("all-----------Trace:- "+f.getTrace());
												//f.getException().printStackTrace();
												;
											}
										}
								}
							};
					
					thread.setDaemon(false);
					thread.start();
					try {
						thread.join(CLASS_TIMEOUT); //35s toute une class
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//System.err.println("fin");
					if(thread.isAlive()){
						thread.interrupt();	
						if(thread.isAlive()){
							Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
							thread.setPriority(Thread.MIN_PRIORITY);
							killerQueue.add(thread);
							//interrupted=true;
							if(!killerThread.isAlive())
								killerThread.start();
						}
						
							return TIMEOUT_ERROR;
//						}
							
					}
//				}
		
				//split the file
		if((wrongAssertionsLines.size()+goodTryCatchLines.size()==0))
			lastExecution=true;
		commentsLines(javaSourceFile,wrongAssertionsLines,goodTryCatchLines);

		int nmw=wrongAssertionsLines.size()+goodTryCatchLines.size();
		if(nmw>100)
	    		nmw=100;
		return nmw; //wrongMethods.size();//
	}

	private static int runTestsOneAtTime(){		
		Class testCasesClass=getTestCasesClass();
		if(testCasesClass==null){
			System.err.println("Could not load the class");
			return 0;
		}
			long startingTime=System.currentTimeMillis();
			//boolean toDrop=false;
			//final Vector<Integer> cleanMethods=new Vector<Integer>();
			//Vector<Integer> methodsToDrop=new Vector<Integer>();
			final Class tccp=testCasesClass;
			final Set<Integer>wrongAssertionsLines=new HashSet<Integer>(); //lines numbers
			final Map<Integer,String>goodTryCatch=new HashMap<Integer,String>();
			//System.err.println("wrongMethods: "+wrongAssertionMethods);
			for(int m:wrongAssertionMethods){
				final String mp="TestCase"+m; 
				int sizeBeforeExecution=wrongAssertionsLines.size()+goodTryCatch.size();
					Thread thread = new Thread(){  
						@Override public void run (){
							//System.err.println("Execution start :"+mp);
							Request request = Request.method(tccp,mp);	  
							Result result = new JUnitCore().run(request);
							{
									for( Failure f:result.getFailures()){
										//f.getException().printStackTrace();
										ExceptionsFormatter.ExceptionFormat exception=new ExceptionsFormatter.ExceptionFormat(f.getException(),javaClassName,false);
										if(exception.getLine()!=""){
											int value=Integer.parseInt(exception.getLine());
											if(f.getException().getClass().equals(ComparisonFailure.class)
													||AssertionError.class.isAssignableFrom(f.getException().getClass())
													|| f.getException().getClass().equals(Assert.class)){
												
												wrongAssertionsLines.add(value); 
											}else{
												goodTryCatch.put(value, f.getException().getClass().getName());
											}
										}else{
											System.err.println("once-----------Message:- "+f.getMessage());
											System.err.println("once-----------Trace:- "+f.getTrace());
											f.getException().printStackTrace();
											//f.getException().
										}
					 				
									}
								}
							}
						};
					
					thread.setDaemon(false);
					thread.start();
					try {
						thread.join(TESTCASE_TIMEOUT); //7500 toute une method
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//System.err.println("fin");
					if(thread.isAlive()){
						thread.interrupt();	
						if(thread.isAlive()){
							Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
							thread.setPriority(Thread.MIN_PRIORITY);
							killerQueue.add(thread);
							//interrupted=true;
							if(!killerThread.isAlive())
								killerThread.start();
						}	
						timeOutMethods.add(m);
						System.err.println("TimeOut : "+m);
					}
					
					if(sizeBeforeExecution==wrongAssertionsLines.size()+goodTryCatch.size())
						cleanMethods.add(m);
					
					if((System.currentTimeMillis()-startingTime)>CLASS_TIMEOUT){
						if((wrongAssertionsLines.size()+goodTryCatch.size()==0))
							lastExecution=true;
						try {
							commentsLines(javaSourceFile,wrongAssertionsLines,goodTryCatch,true);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return TIMEOUT_ERROR;
					}
						
				}
		
		//split the file
		//you must keep this line here
		int nmw=wrongAssertionsLines.size()+goodTryCatch.size();
		//System.err.println("Avant : "+wrongAssertionsLines.size()+goodTryCatch.size());
		if((nmw==0))
			lastExecution=true;
		//the set wrongAssertionsLines may change the content because commentLines may add additional lines
		commentsLines(javaSourceFile,wrongAssertionsLines,goodTryCatch);
		
		if(timeOutMethods.size()==wrongAssertionMethods.size()){
			return TIMEOUT_ERROR;
		}
		
		try {
			//System.err.println("cleanMethods: "+cleanMethods);
			writeWrongMethods(wrongAssertionMethodsFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.err.println("Apres : "+wrongAssertionsLines.size()+goodTryCatch.size());
		if(nmw>100){
			nmw=100;
		}
		
		
		
		return nmw; //wrongMethods.size();//

	}
	
    private static void commentsLines(String javaSourceFile2,
		Set<Integer> wrongAssertions, Map<Integer, String> goodTryCatch) {
    	try {
			commentsLines( javaSourceFile2, wrongAssertions, goodTryCatch, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }

	public static void commentsLines(String filname,Set<Integer> lines2bCommented, Map<Integer,String> goodTryCatch, boolean stopModifications) throws IOException { 
        FileReader source=null;
        BufferedReader sourceBr=null;
        FileWriter dest=null;
        BufferedWriter destBr=null;
        boolean fineshedCorrectly=false;
        boolean header=true;
        String textHeader="";
        String textCurrentMethod="";
        Set<Integer> uncompletMethods=new HashSet<Integer>();
        
        //System.err.println("splitTestCases && lastExecution"+ splitTestCases +" "+ lastExecution);
        
        try {     		
                source = new FileReader(filname);
                sourceBr = new BufferedReader(source);
                
                
                String newVersion="_"+(version+1)+"_";
                String oldVersion="_"+version+"_";
                String newFilname=filname;
                newFilname=newFilname.replace(oldVersion, newVersion);
                dest = new FileWriter(newFilname+TMP_SURFIX);
                destBr = new BufferedWriter(dest);
                
                String cLineMinus1="";
                String cLine =sourceBr.readLine();
                int lineNumber=1;
                int cMethod=0;
                boolean startModifications=false;  
                int next2TryCatch=6;
                while (cLine!=null) {
                	if(cLine.contains("@Test public void TestCase") &&  !stopModifications 
                			//&& !(executeOneTestCaseAtTime && !wrongAssertionMethods.contains(cMethod)) 
                			){ 
                		startModifications=true;
//                		if(cLine.contains(wrongMethods.get(cMethod))){
//                				startModifications=true;
//                				cMethod++;
//                		}else{
//                			startModifications=false; 
//                			//startAnalyzingTry=true; 
//                		}
                	}else{               	
	                	if(startModifications && !cLine.startsWith("//"))
	                	{ 
		                	if(lines2bCommented.contains(lineNumber)){ //error in assert statment
		                		uncompletMethods.add(cMethod);
		                		//System.err.println("Modification 1");
		                		startModifications=false;
		                		cLine="//"+cLine;
		                		//activate it in case of try
		                		if(next2TryCatch<6) //(cLine.contains("throw exce;") && goodTryCatch.containsKey(lineNumber-3))
		                			startModifications=true;
		                			
		                	}else if(goodTryCatch.containsKey(lineNumber) && !cLineMinus1.contains("try {") ){ //exception in assert statment
		                		//System.err.println("Modification 2");
		                			startModifications=false;
		                			cLine="//"+cLine;	
		                	}else if (cLine.contains("try {") ){ //throw exception 
		                			//startModifications=false;
		                		if(!goodTryCatch.containsKey(lineNumber+1)){
		                			if(!cLineMinus1.contains("//Exception") && !cLineMinus1.contains("System.out.println(")){
		                				//System.err.println("Modification 3");
		                				next2TryCatch=0;
		                				cLine="//"+cLine;
			                			lines2bCommented.add(lineNumber+2); 
			                			lines2bCommented.add(lineNumber+3);
			                			lines2bCommented.add(lineNumber+4);
			                			lines2bCommented.add(lineNumber+5);
		                			}else if (cLineMinus1.contains("System.out.println(")){
		                				//next2TryCatch=0; 
		                				//lines2bCommented.add(lineNumber+4);
		                			}
		                				
		                		}else if(!cLineMinus1.contains("//Exception")){
		                				destBr.write("//Exception");
		                				destBr.newLine();
		                		}		
		                		
		                	}else if(goodTryCatch.containsKey(lineNumber-1) && (!cLine.contains("fail(\"Expected Exception\");"))){
		                		destBr.write("		fail(\"Expected Exception\");"); 
		                		destBr.newLine();
		                	}else if(cLine.contains("Throwable")){
		                		Object obj=goodTryCatch.get(lineNumber-2); 
		                		if(obj!=null){
			                		String Exception=(String) obj;
			                		//if(Exception.contains("java."))
			                		//	cLine=cLine.replace("Throwable", Exception);
			                		lines2bCommented.add(lineNumber+1);
		                		}
		                		
		                	}//else 
	                	} 
                	}
                	
                	//
                	if(lastExecution && cLine.contains("throw exce;"))
                		cLine="//"+cLine;
                		
                	if(!cLine.startsWith("//") || cLine.contains("//Exception"))
                	{ // ||   cLineMinus1.contains("System.out.println(")
                		if(cLine.contains("public class "))
            				cLine=cLine.replace(oldClassName, newClassName);
                		destBr.write(cLine);
                		destBr.newLine(); 
                		//if(splitTestCases && lastExecution)
                		{
	                		if(header){
	                			textHeader+=cLine+"\n";
	                		}
	                		else if(cLine.contains("/**") && textCurrentMethod!=""){ //new method
	                			//write a file for the method (lastexecution)
	                			String subClassName=newClassName+cMethod;//outputDirectory+javaClassName+cMethod+".java";
	                			String wholeTextMethod=textHeader.replaceAll(newClassName, subClassName);
	                			wholeTextMethod+=textCurrentMethod;
	                			wholeTextMethod+="}\n";
	                			
	                			String fileName=testCasesPath+File.separator+subPath+cMethod+".java";
	                			File so =new File(fileName);
	                			if(splitTestCases &&  (((lastExecution||!uncompletMethods.contains(cMethod))))){
	                				//System.err.println("writing"+ testCasesPath+File.separator+subPath+cMethod+".java"); &&!so.exists()
	                				
	            	    			if(lastExecution && !uncompletMethods.contains(cMethod))
	            	    			{
//	            	    				String textCurrentMethodWithExceptions=wholeTextMethod;
//	            	    				textCurrentMethodWithExceptions=textCurrentMethodWithExceptions.replaceAll(newClassName, oldClassName);
//	            	    				String fileName1=testCasesPath+File.separator+subPath+(cMethod)+".java";
//	            	    				FileEditor.unit2File( textCurrentMethodWithExceptions,  fileName);
	            	    				
//	            	    				String textCurrentMethodWithoutExceptions=textCurrentMethodWithExceptions;
//
//	            	    				textCurrentMethodWithoutExceptions=textCurrentMethodWithoutExceptions.replaceAll("(.*)try \\{(.*)", "\\{ ");
//	            	    				textCurrentMethodWithoutExceptions=textCurrentMethodWithoutExceptions.replaceAll("(.*)catch \\((.*)", "\\{ ");
//	            	    				textCurrentMethodWithoutExceptions=textCurrentMethodWithoutExceptions.replaceAll("(.*)fail\\((.*)", " ");
//	            	    				textCurrentMethodWithoutExceptions=textCurrentMethodWithoutExceptions.replaceAll(oldClassName+(cMethod), oldClassName+(cMethod)+"B");
//	            	    				//JTETestCases_10_00B
//	            	    				//oldClassName
//	            	    				if(textCurrentMethodWithoutExceptions.length()!=textCurrentMethodWithExceptions.length()){
//	            	    					String fileNameB=testCasesPath+File.separator+subPath+(cMethod)+"B.java";
//	            	    					FileEditor.unit2File( textCurrentMethodWithoutExceptions,  fileNameB);
//	            	    				}
	            	    				//
	            	    				
	            	    			}
	            	    			else{
	            	    				//FileEditor.unit2File( wholeTextMethod,  fileName+".fail");
	            		        		//File de =new File(fileName+".fail");
	            		        		//Files.move(so, de);
	            	    			}
	                			}
	                			//start collecting text of the next method
	                			textCurrentMethod=cLine+"\n";
	                			cMethod++;
	                		}
	                		else
	                			textCurrentMethod+=cLine+"\n";
	                		
	                		if(cLine.contains("public class ")){
	                			header=false;
	                		}
                		}
                	}
                	
                	
                	cLineMinus1=cLine;
                    cLine =sourceBr.readLine();
	                lineNumber++;
	                next2TryCatch++;
                }
              //write last method (test case)
                String fileName=testCasesPath+File.separator+subPath+cMethod+".java";
                File so =new File(fileName);
                if(splitTestCases && (((lastExecution||!uncompletMethods.contains(cMethod))))){
                	
	            	String subClassName=newClassName+cMethod;//outputDirectory+javaClassName+cMethod+".java"; &&!so.exists()
	    			String wholeTextMethod=textHeader.replaceAll(newClassName, subClassName);
	    			wholeTextMethod+=textCurrentMethod;
	    			
//	    			if(!uncompletMethods.contains(cMethod))
//	    				FileEditor.unit2File( wholeTextMethod,  fileName);
//	    			else{
//	    				FileEditor.unit2File( wholeTextMethod,  fileName+".fail");
//		        		//File de =new File(fileName+".fail");
//		        		//Files.move(so, de);
//	    			}
                }
                
                destBr.flush();
                fineshedCorrectly=true;
                
        }finally{
        	if(sourceBr!=null)
        		sourceBr.close();
        	if(source!=null)
        		source.close();      	
        	if(destBr!=null )
        		destBr.close();
        	if(dest!=null)
        		dest.close();
        	
        }
        
        if(fineshedCorrectly)
        {				
        	{
        		
        		int x=version+1;
        		String newVersion="_"+x+"_";
                String oldVersion="_"+version+"_";
                String newFilname=filname;
                newFilname=newFilname.replace(oldVersion, newVersion);
                
                //System.err.println("newFilname "+newFilname);
        		File de =new File(newFilname);
            	File so =new File(newFilname+TMP_SURFIX);
            	so.renameTo(de);
            	
            	if(lastExecution){
            		//de.delete();

                     String fVersion="_"+0+"_";
                     newFilname=filname;
                     newFilname=newFilname.replace( newVersion,fVersion);
                     
                     //File firstVersion =new File(newFilname);
                     //firstVersion.delete();
            	}
            	
            	//delete files
            	//System.err.println("filname "+filname);
            	File old =new File(filname);
            	if(version>0)
            		old.delete();
            	//File backup =new File(filname+BACKUP_SURFIX);	
            	//backup.delete();
            	//}
        	}
        }
    }
	
	private static boolean compileJUnitFile() throws IOException{	
		//System.err.println ("Compilation: ");
		javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,
		                                                                         Locale.ENGLISH,
		                                                                         null); //""iso-8859-1" utf-8 Charset.forName("iso-8859-1")
//		//set classpath entries 
		List<File> pathList=new ArrayList<File>(); 
		
		File fut=new File(binPath);
		pathList.add(fut.getAbsoluteFile());
		
		URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();	
		for (URL url : urlClassLoader.getURLs()){
			pathList.add(new File(url.getFile()));
		}
			
		File dotclass =new File(binPath);
		try {
			fileManager.setLocation(StandardLocation.CLASS_PATH, pathList);
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT,Collections.singleton(dotclass));
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch(Throwable t){
			
		}

		File[] files;
		files=new File[]{new File(javaSourceFile)};//javaFileName -Xlint:none
			
		Iterable options = Arrays.asList("-Xlint:none"); //,, classOutputFolder
		Iterable fileObjects = fileManager.getJavaFileObjects(files);
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options/*null options*/, null, fileObjects);
		
		if(!task.call()){
			System.err.println ("Compilation' errors in the JUnit file: ");
			 //stderr.println (task.toString()); 
			 for (Diagnostic diagnostic : diagnostics.getDiagnostics())
				 System.err.println(diagnostic.getMessage(null));			 
			 return false;
		}
					
		return true;
	}
	
	private static void readWrongMethods(String filename)throws IOException {
        FileReader source=null;
        BufferedReader sourceBr=null;
        
        try {
			source = new FileReader(filename);
			sourceBr = new BufferedReader(source);
			String cLine =sourceBr.readLine();
			if(cLine==null)
				return;
			String[] methods=cLine.split(";");
			for(String m:methods)
				wrongAssertionMethods.add(Integer.parseInt(m));
		}catch (Exception e) {
			
		}
        finally{
        	if(sourceBr!=null)
        		sourceBr.close();
        	if(source!=null)
        		source.close();   
        }
	}
	
	private static void writeWrongMethods(String filename)throws IOException {
		
		wrongAssertionMethods.removeAll( cleanMethods);
		wrongAssertionMethods.removeAll( timeOutMethods);
		if(wrongAssertionMethods.size()>0){
			String wm="";
			for(Integer m:wrongAssertionMethods)
				wm+=m+";";
			wm=wm.substring(0,wm.length()-1)+"\n\r";
			FileEditor.unit2File(wm, wrongAssertionMethodsFileName);
		}

		if(timeOutMethods.size()>0){
			String wm="";
			for(Integer m:timeOutMethods)
				wm+=m+";";
			wm=wm.substring(0,wm.length()-1)+"\n\r";
			FileEditor.unit2File(wm, wrongAssertionMethodsFileName+"TimeOut");
		}
	}
}
