import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import csbst.testing.ArgumentsAnalyzer;
import csbst.testing.JTE;
import csbst.testing.SystemExitControl;
import csbst.utils.ClassPathHacker;

public class GenerateTestData {
	//public static String commonsLangPath="";
	public static List<String> libPaths=new ArrayList<String>();
	public static void main(String[] args) throws Exception{		
		loadClassPath(args);  
	} 

	private static void loadClassPath(String[] args) throws Exception{
		List<URL> pathList=new ArrayList<URL>(); 
		
		String prgPath=getProjectPath();
		String binaryFile=prgPath;
		File prj =new File(prgPath);
		pathList.add(prj.toURL());

		if(args==null || args.length==0 ){
			
//		args=new String[]{"-jf","/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/EmpiricalEvaluation/Environment/benchmark/joda-time/GJChronology.java",//org.joda.time.chrono.GJChronology
//		"-cp","/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/EmpiricalEvaluation/Environment/benchmark/joda-time/cp/joda-time.jar:"+
//			  "/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/EmpiricalEvaluation/Environment/benchmark/joda-time/cp/joda-convert-1.5.jar",
//			  "-maxTime", "40",
//				"-o",
//				"-p ",
//				"-s"
//				};
		
//	//commons-lang
//		args=new String[]{"-jf", "/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/EmpiricalEvaluation/Environment/benchmark/commons/ArrayUtils.java", // commons-lang3-3.1.jar barbecue-1.5-beta1.jar
//				"-cp", "/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/EmpiricalEvaluation/Environment/benchmark/commons/cp/commons-lang3-3.1.jar",
//				"-maxTime", "60",
//				"-o",
//				"-p",
//				"-s"	
//				}; 
//		//commons-lang
//		args=new String[]{"-jf", "/Applications/eclipseLuna/WorkSpace/JTestExpert/JTExpert/sbstcontest2016/Chart-4/source/org/jfree/chart/axis/Axis.java", // commons-lang3-3.1.jar barbecue-1.5-beta1.jar
//				"-cp", "/Applications/eclipseLuna/WorkSpace/JTestExpert/JTExpert/sbstcontest2016/Chart-4/Build/:"+
//							"/Applications/eclipseLuna/WorkSpace/JTestExpert/JTExpert/sbstcontest2016/Chart-4/lib/servlet.jar",
//				"-maxTime", "60",
//				"-o",
//				"-p",
//				"-s"	
//				};
			
//			//Lang-61
//			args=new String[]{"-jf", "/Applications/eclipseLuna/WorkSpace/Lang-61/src/org/apache/commons/lang/text/StrBuilder.java", // commons-lang3-3.1.jar barbecue-1.5-beta1.jar
//					"-cp", "/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/JTExpert/Lang-61/target/classes/",
//					"-maxTime", "15",
//					"-o",
//					"-p",
//					"-s"
//					};  
			
//			
//			args=new String[]{"-jf", "/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/EmpiricalEvaluation/Environment/benchmark/triangle/Triangle.java", 
//					"-maxTime", "10",
//					"-o",
//					"-p",
//					"-s"
//					};
			
			args=new String[]{"-jf", "/Users/abdelilahsakti/gson/main/java/com/google/gson/internal/bind/JsonTreeReader.java", 
			"-cp", "/Users/abdelilahsakti/gson/target/classes",
			"-maxTime", "10",
			"-o",
			"-p",
			"-s"
			};
			
		}		
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//here load class path then load class under test
    	//load the class under test
		ArgumentsAnalyzer.AnalyzeCmd(args);
		if(!ArgumentsAnalyzer.instrumentedClassPath.equals("")){
			ClassPathHacker.addFile(ArgumentsAnalyzer.instrumentedClassPath);
			List<URL> pl=new ArrayList<URL>();
			pl.add((new File(ArgumentsAnalyzer.instrumentedClassPath)).toURL());
			for(String p:ArgumentsAnalyzer.classPath){ 
				pl.add((new File(p)).toURL());
				ClassPathHacker.addFile(p);
			}
//			//now instantiate class under test from the instrumented unit.			
			URL[] urls = new URL[pl.size()];
			urls=pathList.toArray(urls);
			
			ClassLoader cl =new URLClassLoader(urls);						
			Thread.currentThread().setContextClassLoader(cl);
	    	Class clss =Thread.currentThread().getContextClassLoader().loadClass(ArgumentsAnalyzer.className);
	    	
	    	String resource='/'+clss.getName().replace('.', '/')+".class";
			URL location = clss.getResource(resource);
	    	//System.err.println("+++++++++++++++"+location+"+++++++++++++++++++");
		}
    	//++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		int index;
		if(prgPath.endsWith(".jar")){ 
			index=prgPath.lastIndexOf('/');
			if(index>0){
				prgPath=prgPath.substring(0,index);
				prj =new File(prgPath);
			}  
		}else{
			binaryFile+=File.separator+"bin"+File.separator;//+"GenerateTestData.class";
					
		}
				
		File bin=new File(prj.getAbsolutePath()+File.separator+"bin");
		pathList.add(bin.toURL()); 
    	
    	File lib=new File(prj.getAbsolutePath()+File.separator+"lib"); 
    	if(lib.exists())
    		pathList.addAll(getJarListInLibDir(lib));
		
		
    	URL[] classPath=new URL[pathList.size()];
    	classPath=pathList.toArray(classPath);
    	
    	for(int i1=0;i1<pathList.size();i1++)
			ClassPathHacker.addFile(pathList.get(i1).getPath());
    	

    	
    	//JTE.commonsLangPath=commonsLangPath;
    	JTE.libPaths=libPaths;
    	JTE.projectPath=prgPath;
    	JTE.binaryFile=binaryFile;
    	//if()
    	csbst.testing.JTE.classPathList=pathList; 
    	csbst.testing.JTE.main(args); 
	}
    
    public static List<URL> getJarListInLibDir(File lib)throws Exception{
    	List<URL> pathList=new ArrayList<URL>();
    	//Character c;
        File[] files = lib.listFiles();
        for(int i=0;i<files.length;i++){
        	
        	//eclipse ecj
        	//if(files[i].toString().contains("commonsLang") && files[i].getName().endsWith(".jar")){
        	if(false && !files[i].toString().contains("eclipse") 
        			&& !files[i].toString().contains("ECJ-modified") 
        			&& files[i].getName().endsWith(".jar")){
        		libPaths.add(files[i].toString());
        		//System.out.println(files[i].toString());
        		//commonsLangPath=files[i].toString();//.getName();
        		//System.out.println(commonsLangPath);
        		continue;
        	}
        	
            if(files[i].getName().endsWith(".jar") ){
            	pathList.add(files[i].toURL());
            }else if(files[i].isDirectory()){
            	pathList.addAll(getJarListInLibDir(files[i]));
            }
        }
        return pathList;  
    }
    
    public static String getProjectPath(){
		String prgPath=GenerateTestData.class.getResource("GenerateTestData.class").getPath();
		int index=prgPath.indexOf(':');
		if(index>0)
			prgPath=prgPath.substring(prgPath.indexOf(':')+1);
		index=prgPath.lastIndexOf('/');
		if(index>0)
			prgPath=prgPath.substring(0,index);
		
		if(prgPath.endsWith("!"))
			prgPath=prgPath.substring(0,prgPath.length()-1); 
		
		if(prgPath.endsWith(File.separator+"bin"))
			prgPath=prgPath.substring(0,prgPath.length()-4);
		
		return prgPath;
    }
}
