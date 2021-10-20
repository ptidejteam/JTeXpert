package csbst.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.Hashtable;

public class ASTBuilder {

	String javaText;
	String source;
	String[] sourcePath;
	String[] classPath;
	CompilationUnit astRoot;
	
	
	public ASTBuilder(String sourcePath, String source) throws IOException{
		this.source=source;
		this.sourcePath=sourcePath.split(";");
		File file = new File(sourcePath+"//"+source);
		BufferedReader in = new BufferedReader(new FileReader(file));
		StringBuffer buffer = new StringBuffer(); 
		String line = null;
		while (null != (line = in.readLine())) {
			buffer.append("\t" + line);
			buffer.append("\n");
			//if (monitor.isCanceled()) return;
		}	
		
		final String text = buffer.toString();		
		
		javaText=text;
		buildAST();
	}

	public ASTBuilder(String classPath, String sourcePath, String source) throws IOException{
		//ASTBuilder( sourcePath,  source);
		this.source=source;
		if(sourcePath!=null)
			this.sourcePath=sourcePath.split(File.pathSeparator);
		if(classPath!=null && !classPath.equals("") )
			this.classPath=classPath.split(File.pathSeparator);
		File file = new File(source);//(sourcePath+"//"+source);
		BufferedReader in = new BufferedReader(new FileReader(file));
		StringBuffer buffer = new StringBuffer(); 
		String line = null;
		while (null != (line = in.readLine())) {
			buffer.append("\t" + line);
			buffer.append("\n");
			//if (monitor.isCanceled()) return;
		}	
		
		final String text = buffer.toString();		
		
		javaText=text;
		buildAST();
	}
	
	CompilationUnit buildAST(){
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(javaText.toCharArray());
		parser.setEnvironment(classPath, sourcePath, null, true); 
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		//parser.setEnvironment(classPaths, sources,
		//		new String[] { "UTF-8" }, true);
		//parser.
		Hashtable<String, String> options = JavaCore.getDefaultOptions();
	    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
		parser.setCompilerOptions(options);//getCompilerOptions()
		parser.setUnitName(source);//(sourcePath+"//"+source);
		//return parser.createAST(null);
		
		
	    astRoot = (CompilationUnit) parser.createAST(null);

	    return astRoot;

	}
	
	public CompilationUnit getASTRoot(){
		return astRoot;
	}


}