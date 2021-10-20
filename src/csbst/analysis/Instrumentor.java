package csbst.analysis;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;

import csbst.generators.AbsractGenerator;
import csbst.testing.BranchDistance;
import csbst.testing.fitness.ApproachLevel;
import csbst.testing.fitness.NumberCoveredBranches;

public class Instrumentor extends ASTVisitor{	
	
	String className;
	String packageName="";
	/**
	 * addExecutionPathTracer allows to add a new statement at the index "index" in a block "b" to trace the program execution
	 * @param index is the position wherein the tracer statement must be inserted
	 * @param b is the block wherein the new statement must be inserted
	 * @param iteration is the iteration number for a loop 1 is its default value
	 * @param oldBranch is the current branch number 
	 */	
	private void addExecutionPathTracer(Block b, int index, String iteration, int oldBranch){
		if(b.getProperty(BranchesProperties.NUM_BRANCH)==null)
			return;
		if(b.statements().size()>0 && index<=b.statements().size())
			if((b.statements().get(index) instanceof SuperConstructorInvocation)||(b.statements().get(index) instanceof ConstructorInvocation))
				index++;
		//sdfjsfjhg
		
		int branch=(Integer) b.getProperty(BranchesProperties.NUM_BRANCH);

		//Create a new trace statement
		MethodInvocation trace = b.getAST().newMethodInvocation();
		trace.setExpression(b.getAST().newSimpleName(NumberCoveredBranches.class.getSimpleName()));//"ClassUnderTest"
		trace.setName(b.getAST().newSimpleName("maintainPathTrace"));
		NumberLiteral p1;
		p1=b.getAST().newNumberLiteral(""+branch);
		trace.arguments().add(p1);
//		if((b.getParent() instanceof ForStatement)
//				||(b.getParent() instanceof DoStatement)
//				||(b.getParent() instanceof WhileStatement)){
//			trace.arguments().add(b.getAST().newNumberLiteral("1"));//b.getAST().newSimpleName(("i"+branch));
//		}		
//		else{
			StringLiteral p2;
			//if(b.getParent() instanceof IfStatement 
			//		&& ((IfStatement)b.getParent()).getElseStatement().equals(b)){
				p2=b.getAST().newStringLiteral();
				if(packageName!=null && !packageName.equals(""))
					p2.setLiteralValue(packageName+"."+className);
				else
					p2.setLiteralValue(className);
				trace.arguments().add(p2);
			///}
			//else
			//	trace.arguments().add((Expression) ASTNode.copySubtree(b.getAST(), String2Expression.getExpression(iteration)));
//		}
		//Insert the trace stament in the AST at the block b
		ExpressionStatement eStatement2=b.getAST().newExpressionStatement(trace);
		b.statements().add(index,eStatement2);		
	}

	private void branchInstrumentation(Block block) throws Exception{
		Expression expression=(Expression) block.getProperty("expression") ;//(Expression) branch2ASTNode.get(branch).getProperty("Expression") ;//branchExpression.get(branch);
		if(expression.toString().equals("true"))
			return;
		
		//create method invocation to compute branch distance	
		MethodInvocation gBD=block.getAST().newMethodInvocation(); 
		gBD.setExpression(block.getAST().newSimpleName(ApproachLevel.class.getSimpleName()));
		gBD.setName(block.getAST().newSimpleName("maintainBranchDistance"));
		
		//create arguments
		gBD.arguments().add(block.getAST().newNumberLiteral(""+  block.getProperty(BranchesProperties.NUM_BRANCH)));
		gBD.arguments().add((Expression) ASTNode.copySubtree(block.getAST(), String2Expression.getExpression("1")));
		Expression exp;

	    //create a new expression using the node condition
		Expression nodeExp=(Expression) ASTNode.copySubtree(block.getAST(),expression);			
		exp=(Expression) ASTNode.copySubtree(block.getAST(), String2Expression.getExpression(BranchDistance.expressionFormater(nodeExp)));

		gBD.arguments().add(exp);
		ExpressionStatement gBDStatement=block.getAST().newExpressionStatement(gBD);
		
		//add the if statement to the program
		//you must insert the branch distance computation statement just before the parent node
		if(block.getParent().getParent() instanceof Block)
			((Block) block.getParent().getParent()).statements().add(((Block) block.getParent().getParent()).statements().indexOf(block.getParent()),gBDStatement);
		else
			block.statements().add(0,gBDStatement);

	}	
	
	@Override
	public boolean visit(Block node){		
		if(node.getProperty(BranchesProperties.NUM_BRANCH)!=null){		
			try {
				//branchInstrumentation(node);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			addExecutionPathTracer(node,0,"1",-1);
		}
		return true;
	}


	@Override
	public boolean visit(PackageDeclaration node){
		packageName=node.getName().toString();
		
		return true;
	}
	
	@Override
	public boolean visit(TypeDeclaration node){
		//don't explore sub classes
		if(node.isLocalTypeDeclaration()||node.isMemberTypeDeclaration())
			return true;
		
		className=node.getName().toString();
		
		return true;
	}
//	@Override
//	public boolean visit(TypeDeclaration node){
//		SimpleName newName=node.getAST().newSimpleName(node.getName()+ClassUnderTest.INSTRUMENTATION_SURFIX);
//		node.setName(newName);
//		return true;
//	}

//	@Override
//	public boolean visit(MethodDeclaration node){
//		if(node.isConstructor()){
//			SimpleName newName=node.getAST().newSimpleName(node.getName()+ClassUnderTest.INSTRUMENTATION_SURFIX);
//			node.setName(newName);
//		}
//		return true;
//	}
	
	@Override
	public boolean visit(CompilationUnit node){	
		//PackageDeclaration pkgDecl=node.getPackage();
		String  pckgName="";
		if(node.getPackage()!=null)
			pckgName=node.getPackage().getName().getFullyQualifiedName();
		
		ImportDeclaration importDeclaration = node.getAST().newImportDeclaration();
		importDeclaration.setName(AbsractGenerator.generateQualifiedName("csbst.testing.fitness",node.getAST()));
		importDeclaration.setOnDemand(true);
		node.imports().add(importDeclaration);
	
//		ImportDeclaration importDeclaration1 = node.getAST().newImportDeclaration();
//		importDeclaration1.setName(ASTEditor.generateQualifiedName(pckgName,node.getAST()));
//		importDeclaration1.setOnDemand(true);
//		node.imports().add(importDeclaration1);	

//		if(pckgName==null || pckgName.equals(""))
//			pckgName="testing";
//			//pckgName=node.getAST().newQualifiedName(node.getPackage().getName(),node.getAST().newSimpleName("testing"));
//		else
//			pckgName+=".testing";
//		node.getPackage().setName(ASTEditor.generateQualifiedName(pckgName,node.getAST()));
		
		return true;
	}
		
	private Name ASTEditor(String pckgName, AST ast) {
		// TODO Auto-generated method stub
		return null;
	}

//	public static void main(String[] args) throws IOException{
//		ASTBuilder ASTRoot=new ASTBuilder("//Applications//eclipse//test","ArithmeticUtils.java");
//		CompilationUnit node=ASTRoot.getASTRoot();	
//		//AST ast=node.getAST();
//		BranchesCoder bC=new BranchesCoder();
//		node.accept(bC);
//		Instrumentor iTest=new Instrumentor();
//		node.accept(iTest);
//		
//		 FileWriter fstream = new FileWriter("//Applications//eclipse//test//ArithmeticUtilsInst.java");
//		 BufferedWriter out = new BufferedWriter(fstream);
//		 out.write(node.toString());
//		 //Close the output stream
//		 out.close();
//	}	

}
