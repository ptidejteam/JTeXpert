package csbst.analysis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.eclipse.jdt.core.dom.CompilationUnit;

import csbst.generators.dynamic.AbstractDynamicGenerator;
import csbst.utils.FileEditor;

public class CUTAnalyser {
	public  BranchesCoder branchCoderAnalyser;
	public  DataMemberUseAnalyser dataMemberUseAnalyser;
	public  MethodCallsAnalyser methodCallsAnalyser;
	public  InfluenceAnalyser influenceAnalyser;
	public  LittralConstantAnalyser litteralConstantAnalyser;
	private  CompilationUnit compilationUnit;
	public   String srcPath;
	public   String subPath;
	public   String srcFileName;
	
	private void createInstrumentedCUD(){
		litteralConstantAnalyser=new LittralConstantAnalyser();
		compilationUnit.accept(litteralConstantAnalyser);
		//call the branchcoder visitor to identify class branches
		branchCoderAnalyser=new BranchesCoder();
		compilationUnit.accept(branchCoderAnalyser);			
		//call the instrumentor (instrumentor must be called after the BranchCoder)
		Instrumentor instrumentor=new Instrumentor();
		compilationUnit.accept(instrumentor);	
		
		//create the instrumented java file
		FileEditor.unit2File(compilationUnit.toString(),srcPath+File.separator+subPath+File.separator+srcFileName+".java");
	}
	
	private void AnalyseCU(){			
		methodCallsAnalyser=new MethodCallsAnalyser();
		compilationUnit.accept(methodCallsAnalyser);
		
		//Analyze Data members and parameters uses.
		dataMemberUseAnalyser=new DataMemberUseAnalyser();
		compilationUnit.accept(dataMemberUseAnalyser);
		
		influenceAnalyser=new InfluenceAnalyser();
		compilationUnit.accept(influenceAnalyser);
	}
	
	public  Class InstantiateICUD(String className){
		Class cls=null;
		try {
			cls=Thread.currentThread().getContextClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cls;
	}
	
	public static boolean compileJUnitFile(String javaSourceFile, String binpath, List<String> classpath) throws IOException{	
		
		File instrumentedClass = new File(binpath);
		instrumentedClass.mkdirs();
		
		javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,
		                                                                         Locale.ENGLISH,
		                                                                         null); 
		//set classpath entries 
		List<File> pathList=new ArrayList<File>(); 
//		//second put all required libraries
		for(String p:classpath){ 
			if(!p.endsWith(".jar")&& !p.endsWith(File.separator))
				p+=File.separator;
			pathList.add(new File(p));
			//System.err.println(p);
		}		
			
		File dotclass =new File(binpath);
		try {
			fileManager.setLocation(StandardLocation.CLASS_PATH, pathList);
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT,Collections.singleton(dotclass));
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch(Throwable t){
			
		}

		File[] files;
		files=new File[]{new File(javaSourceFile)};//javaFileName
			
		Iterable fileObjects = fileManager.getJavaFileObjects(files);
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null/*options*/, null, fileObjects);
		
		if(!task.call()){
			System.err.println ("Compilation' errors in the file: "+javaSourceFile);
			 //stderr.println (task.toString()); 
			 for (Diagnostic diagnostic : diagnostics.getDiagnostics())
				 System.err.println(diagnostic.getMessage(null));			 
			 return false;
		}
					
		return true;
	}
}
