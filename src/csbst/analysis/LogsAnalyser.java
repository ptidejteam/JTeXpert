package csbst.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import csbst.testing.JTE;
import csbst.utils.FileEditor;
import csbst.utils.ExceptionsFormatter;
import csbst.utils.ReadSpecificLine;
import csbst.utils.ExceptionsFormatter.ExceptionFormat;

//import com.google.java.contract.Invariant;
//import com.google.java.contract.Requires;


public class LogsAnalyser {
	private String logFile;
	private String[] sourceDirectories;
	
	public LogsAnalyser(String logFile,String[] sourceDirectories){
		this.logFile=logFile;
		this.sourceDirectories=sourceDirectories;
	}
	
	private void log2Bugs() throws IOException{
		if(logFile==null)
			return;
		
        String line = null;
        //int lineNo;
        FileReader fr=null;
        BufferedReader br=null;
        String BugsFile=""; 
        try {
                fr = new FileReader(logFile);
                br = new BufferedReader(fr);
                
                line = br.readLine();
                while (line!=null) {           	
                	if(line.startsWith(ExceptionsFormatter.EXCEPTION_PREAMBLE)){ //line.endsWith(", End") && 
                		String newLine=checkException(line);
                		if(newLine!=null && newLine.startsWith("false")){
                			newLine=newLine.replace("false, ", "");
                			BugsFile+=newLine+System.getProperty("line.separator");
                		}
                	}
                	line = br.readLine();
                }
        }finally{
        	if(fr!=null)
        		fr.close();
        	if(br!=null)
        		br.close();
        }	
        
        unit2File(BugsFile,  logFile+".bugs");
        System.out.println("A report of Unhandled Exceptions has been created at: "+logFile+".bugs");
	}
	
	public static void unit2File(String unit, String fileName){
		try {
			//StringBuffer sb;
				File file = new File(fileName);
				file.getParentFile().mkdirs();

				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));//,"iso-8859-1"
				out.write(unit);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			
	}
	
	private String checkException(String logLine){
		String newLine=null;
		if(logLine==null)
			return null;
		
		ExceptionsFormatter.ExceptionFormat exception=new ExceptionsFormatter.ExceptionFormat(logLine);

		if(exception==null) // || fields.length!=12
			return null;
		
		boolean checkThrow=checkException(exception.getClazz(),exception.getLine());
		if(!checkThrow)
			for(ExceptionFormat cause:exception.getCausesList()){
				checkThrow=checkException(cause.getClazz(),cause.getLine());
				if(checkThrow)
					break;
			}
			//st+=", "+cause.toSmallString();
		
		if(checkThrow)
			newLine="true";
		else
			newLine="false";
		
		newLine+=", "+exception.getBugString();
		
		return newLine;
		
	}
	
	private boolean checkException(String clazz,String line){
		//String clazz=exception.getClazz();
		if(clazz.contains("JTETestCases"))
			return true;
		
		int dollarPosition=clazz.indexOf('$');
		if(dollarPosition>0)
			clazz=clazz.substring(0, dollarPosition);
		
		//clazz=clazz.replace("Class=", "");//(0, dollarPosition);File.separator
		String javaclazz=clazz.replace(".", File.separator)+".java";
		
		String javaFile="";
		boolean exist=false;
		for(int i=0;i<sourceDirectories.length;i++){
			javaFile=sourceDirectories[i]+File.separator+javaclazz;
			File checkExist=new File(javaFile);
			if(checkExist.exists()){
				exist=true;
				break;
			}
			
				
		}
		
		boolean error=true;
		
		if(!exist)
			return error;

		int nLine=-1;
		try{
			nLine=Integer.parseInt(line);
		}catch(NumberFormatException e){ 
			return error;
		}
		
		String throwLine;
		try {
			throwLine=ReadSpecificLine.getLine(javaFile, nLine);
		} catch (IOException e) {
			//e.printStackTrace();
			return error;
		}
		
		if(throwLine==null)
			throwLine="";
		
		if(throwLine.contains("throw")
				||throwLine.contains("Exception")
				|| (throwLine.contains("check"))
				|| throwLine.contains("Assert")
				|| throwLine.contains("Invariant")
				|| throwLine.contains("Requires")
				|| (throwLine.contains("System")&&throwLine.contains("exit"))
				|| throwLine.contains("Throwable"))
			return true;
		else
			return false;
		
		//return false;
	}
	
	public static void main(String[] args) {
		
//		args=new String[]{"-log", "/Users/abdelilahsakti/PHD2011/SSBSE14/results/ArrayUtilsLogs.txt", //"logs.txt",
//		"-src","/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/commons-lang331Test/src/"};		
				
//		args=new String[]{"-log", "/Users/abdelilahsakti/PHD2011/SSBSE14/results/ApplicationId.txt", //"logs.txt",
//				"-src","/Users/abdelilahsakti/Documents/workspace/hadoop-annotations/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-archives/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-assemblies/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-auth/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-auth-examples/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-client/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-common/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-datajoin/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-dist/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-distcp/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-extras/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-gridmix/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-hdfs/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-hdfs-httpfs/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-hdfs-nfs/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-mapreduce-client-app/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-mapreduce-client-common/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-mapreduce-client-core/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-mapreduce-client-hs/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-mapreduce-client-hs-plugins/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-mapreduce-client-jobclient/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-mapreduce-client-shuffle/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-mapreduce-examples/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-maven-plugins/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-minicluster/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-minikdc/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-nfs/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-openstack/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-rumen/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-sls/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-streaming/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-tools-dist/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-yarn-api/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-yarn-applications-distributedshell/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-yarn-applications-unmanaged-am-launcher/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-yarn-client/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-yarn-common/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-yarn-server-applicationhistoryservice/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-yarn-server-common/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-yarn-server-nodemanager/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-yarn-server-resourcemanager/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-yarn-server-tests/src/main/java"
//				+ ":/Users/abdelilahsakti/Documents/workspace/hadoop-yarn-server-web-proxy/src/main/java"
//		};
		
		String src="";
		String log="";
		int i = 0;
        String arg;
        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            if (arg.equals("-src")) 
            	if (i < args.length) src=args[i++];
            	else{
                    System.err.println(arg+" requires a value");
                    System.exit(-1);
    	        }
            if (arg.equals("-log")) 
            	if (i < args.length) log=args[i++];
            	else{
                    System.err.println(arg+" requires a value");
                    System.exit(-1);
    	        }
        }
        
        String[] srcDirectories=src.split(File.pathSeparator);	
		LogsAnalyser bf=new LogsAnalyser(log,srcDirectories); //
		
		try {
			bf.log2Bugs();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(bf.checkException("RA60_SSBSE2014.o257049.251:296:    [junit] Start, class java.lang.NegativeArraySizeException, Message=null, Class=org.apache.hadoop.io.BytesWritable, Method=setCapacity, Line=144, TestCaseClass=org.apache.hadoop.io.BytesWritableJTETestCases, TestCaseMethod=TestCase6, TestCaseLine=247, End"));
	}

}
