package csbst.testing;

import java.io.File;

public class ArgumentsAnalyzer {
	public static String className;
	public static  String srcPath0;
	public static String packageName="";
	public static String projectPackagesPrefix;
	public static String subPath;
	public static String testCasesPath;
	public static  String srcPath;
	public static String binPath;
	public static String[] classPath;
	private static String classPath0=""; 
	public static String srcFileName;
	private static String gaParametersFile;
	private static String jteOutputPath;
	private static String javaFileName;
	private static String javaDirectoryName;
	public static String projectPath;
	public static String binaryFile;
	
	public static int seed=0;
	public static boolean printProgress=false;
	public static boolean showErrors=false;
	public static  boolean ExceptionsOriented=false;
	public static  boolean ExceptionsAnalysis=true;
	public static boolean overrideExistTestCase=false;
	public static boolean instrument=true;	
	public static String instrumentedClassPath="";
	public static long maxTime=0;
	
	public static void AnalyzeCmd(String[] args) throws Exception{				
		String usageTxt="JTExpert -jf Java_unit_under_test  [-cp classpath] [-maxTime search_time_in_seconds] " + System.getProperty("line.separator") +"";
		usageTxt+="-jf 	: use it to set the Java file under test. JTExpert Generates test data suite for this file;" + System.getProperty("line.separator") +"";
		usageTxt+="-cp 	: use it to set the class path. Paths have to be separated by the system pathseparator (e.g., for linux is :);  " + System.getProperty("line.separator") +"";
		usageTxt+="-tp 	: use it to set the work directory, wherein the test suite will be saved;  " + System.getProperty("line.separator") +"";			
		usageTxt+="-maxTime : sets the max search time. " + System.getProperty("line.separator") +"";
		usageTxt+="-o	: use it to override an existing test data file ; " + System.getProperty("line.separator") +"";
		usageTxt+="-p	: use it to disply a progress bars; " + System.getProperty("line.separator") +"";
		usageTxt+="-s 	: use to show messages and errors thrown by the class under test; " + System.getProperty("line.separator") +"";
		usageTxt+="-seed: seed to use for the random number generator. " + System.getProperty("line.separator") +""; 
		usageTxt+="-E	: use it to activate the Exception-Otriented Test-Data Generation search. " + System.getProperty("line.separator") +"";
		
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
            	if (i < args.length) javaFileName=args[i++];
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
            if (arg.equals("-ppp")) 
            	if (i < args.length)projectPackagesPrefix=args[i++];
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
            if (arg.equals("-maxTime"))
            	if (i < args.length) maxTime=Integer.parseInt(args[i++]);
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
	        if (arg.equals("-i")) instrument=false; 
	        if (arg.equals("-p")) printProgress=true; 
            if (arg.equals("-s")) showErrors=true; 
	        if (arg.equals("-E")) ExceptionsOriented=true; 

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

        
        if(jteOutputPath==null||jteOutputPath.equals("")){
        	File relativeJTEPath = new File("");
			String absolutJTEPath=relativeJTEPath.getAbsolutePath();
			jteOutputPath=absolutJTEPath+File.separator+"jteOutput";
        }
        if(testCasesPath==null||testCasesPath.equals(""))
        	testCasesPath=jteOutputPath+File.separator+"testcases";
        
        binPath=jteOutputPath+File.separator+"bin";
        srcPath=jteOutputPath+File.separator+"src";        
		classPath=classPath0.split(File.pathSeparator);
	}

}
