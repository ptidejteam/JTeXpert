package csbst.analysis;


import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.Hashtable;

public class String2Expression {
	//private String txtExpression;
	private static Expression expression;
	private static Block block;
	private static MethodDeclaration method;
	private static FieldDeclaration field;
	
	private static class expressionVisitor extends ASTVisitor{
		@Override
		public boolean visit(IfStatement node){
			expression=node.getExpression();
			return false;
		}
	}
	
	private static class FieldVisitor extends ASTVisitor{
//		@Override
//		public void postVisit(ASTNode node){
//			System.out.println(node);
//			//return true;
//		}
		@Override
		public boolean visit(FieldDeclaration node){
			field=node;
			return false;
		}
	}
	
	private static class InfixExpressionVisitor extends ASTVisitor{
		@Override
		public boolean visit(Block node){
			block=node;
			return false;
		}
	}
	
	public static Expression getExpression(String txtExpression){
		Expression newExpression=null;
		String classX="class X{ X(){if("+txtExpression+");}}";
		//*****parse the string
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(classX.toCharArray());
		parser.setEnvironment(null, null, null, true);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setBindingsRecovery(true);
		Hashtable<String, String> options = JavaCore.getDefaultOptions();
	    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
		parser.setCompilerOptions(options);//getCompilerOptions()
		//parser.setUnitName(classPath+"//"+source);
		//return parser.createAST(null);
		CompilationUnit astRoot;
		astRoot = (CompilationUnit) parser.createAST(null);
		expressionVisitor ev=new expressionVisitor();
		astRoot.accept(ev);
		return expression;
	}
	
	
	public static FieldDeclaration getFieldDeclaration(String txtExpression){
		FieldDeclaration newExpression=null;
		String classX="import static org.junit.Assert.*; import org.junit.Test; import org.junit.Rule; class X{"+ txtExpression+"; X(){;}}";
		//*****parse the string
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(classX.toCharArray());
		parser.setEnvironment(null, null, null, true);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setBindingsRecovery(true);
		Hashtable<String, String> options = JavaCore.getDefaultOptions();
	    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
		parser.setCompilerOptions(options);//getCompilerOptions()
		//parser.setUnitName(classPath+"//"+source);
		//return parser.createAST(null);
		CompilationUnit astRoot;
		astRoot = (CompilationUnit) parser.createAST(null);
		FieldVisitor fv=new FieldVisitor();
		astRoot.accept(fv);
		return field;
	}
	
	public static Block getInfixExpression(String txtExpression){
		Expression newExpression=null;
		String classX="class X{ X(){ "+txtExpression+"}}";
		//*****parse the string
		//System.out.println(classX);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(classX.toCharArray());
		parser.setEnvironment(null, null, null, true);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setBindingsRecovery(true);
		Hashtable<String, String> options = JavaCore.getDefaultOptions();
	    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
		parser.setCompilerOptions(options);//getCompilerOptions()
		//parser.setUnitName(classPath+"//"+source);
		//return parser.createAST(null);
		CompilationUnit astRoot;
		astRoot = (CompilationUnit) parser.createAST(null);
		InfixExpressionVisitor ev=new InfixExpressionVisitor();
		astRoot.accept(ev);
		//System.out.println("4************"+method);
		return block;
	}
	
	public static MethodDeclaration getMethodDeclaration(String txtExpression){
		Expression newExpression=null;
		String classX="class X{ "+txtExpression+"}";
		//*****parse the string
		//System.out.println(classX);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(classX.toCharArray());
		parser.setEnvironment(null, null, null, true);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setBindingsRecovery(true);
		Hashtable<String, String> options = JavaCore.getDefaultOptions();
	    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
		parser.setCompilerOptions(options);//getCompilerOptions()
		//parser.setUnitName(classPath+"//"+source);
		//return parser.createAST(null);
		CompilationUnit astRoot;
		astRoot = (CompilationUnit) parser.createAST(null);
		MethodDeclarationVisitor ev=new MethodDeclarationVisitor();
		astRoot.accept(ev);
		//System.out.println("4************"+method);
		return method;
	}
	
	private static class MethodDeclarationVisitor extends ASTVisitor{
		@Override
		public boolean visit(MethodDeclaration node){
			method=node;
			return false;
		}
	}
	
}
