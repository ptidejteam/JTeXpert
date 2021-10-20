package csbst.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
//import java.lang.reflect.Modifier;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.Expression;
//import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.Assignment;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BranchesCoder extends ASTVisitor{
	//private AST ast;
	private int currentBranch;
	private int branchesCounter=0;
	private TypeDeclaration classNode; //the class under test
	private MethodDeclaration currentMethod; //the index of the current method
	private Map<Integer,Block> branch2ASTNode=new HashMap<Integer,Block>();
		
	public Map<Integer,Block> getBranch2BlockMap(){
		return branch2ASTNode;
	}
	
//	public Map<Integer,MethodDeclaration> getBranch2MethodMap(){
//		return branch2Method;
//	}
	public TypeDeclaration getClassNode(){
		//classNode.
		return classNode;
	}
	
	
	public int getLastBranch(){
		return branchesCounter;
	}
	
	private void defineNewBranch(Block node, Expression exp){
		if(currentMethod==null) return;
		
		branchesCounter++;
		node.setProperty("numberBranch", branchesCounter);
		node.setProperty("methodContainer", currentMethod);
		if(exp!=null){
			node.setProperty("expression", exp);
			if (!exp.toString().equalsIgnoreCase("true")){
				try {
					node.setProperty("difficultyCoefficient", DifficultyCoefficient.getDifficultyCoefficient(exp));
					//System.out.println(DifficultyCoefficient.getDifficultyCoefficient(exp));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		currentBranch=branchesCounter;	
		branch2ASTNode.put(currentBranch,node);	
		//branch2Method.put(currentBranch, currentMethod);
	}

	private void encapsulatingNodeInBlock(ASTNode node){
		Block parent = (Block) node.getParent();
	    int index = parent.statements().indexOf(node);
	    
		Block newBlock = node.getAST().newBlock();
		parent.statements().add(index,newBlock);
		newBlock.setProperty("Source","Instrumentation");
		
		node.delete();
		newBlock.statements().add(node);
	}
	
	@Override
	public boolean visit(TypeDeclaration node){
		//don't explore sub classes
		if(node.isLocalTypeDeclaration()||node.isMemberTypeDeclaration())
			return true;
		
		classNode=node;
		branchesCounter=0;
		currentBranch=0;
		return true;
	}
	
	@Override
	public boolean visit(ClassInstanceCreation node){
		//don't explore constructor that define new methods (anonymous classes)
		//System.out.println(node);
		return true;
	}
	
	@Override
	public boolean visit(MethodDeclaration node){
		//System.out.println(node.modifiers());
		if(Modifier.isAbstract(node.getModifiers())||node.getBody()==null)
			return false;
		
		//IMethod mthd=node.
		currentMethod=node;//.resolveBinding() ;//getMethodIndexFromSignature(node.resolveBinding().getMethodDeclaration().toString());
		//Set<Integer>callers=new HashSet<Integer>();
		//node.setProperty("callers", callers);
		//System.out.println(currentMethod);
		defineNewBranch(node.getBody(), String2Expression.getExpression("true"));
		return true;
	}
	
	@Override
	public void endVisit(MethodDeclaration node){
		currentMethod=null;
	}

	@Override
	public boolean visit(IfStatement node){
		//if(node.getExpression().toString().contains("isPartOfTestTarget"))
		//	return false;
		
		if(currentMethod==null) return true;
		
		int parentBranch=currentBranch;
		node.setProperty("numberParentBranch", parentBranch);
		node.setProperty("isMultiBranchesRoot","YES");
		node.setProperty("expression", node.getExpression());
		
		if(!(node.getThenStatement() instanceof Block)){
			Block b=node.getAST().newBlock();
			Statement body = node.getThenStatement();
			node.setThenStatement(b);
			body.delete();
			b.statements().add(body);
			defineNewBranch(b, node.getExpression());
			b.setProperty("numberParentBranch", parentBranch);
		}
		else{
			defineNewBranch((Block)node.getThenStatement(), node.getExpression());
			((Block)node.getThenStatement()).setProperty("numberParentBranch", parentBranch);
		}
		
		if(!(node.getElseStatement() instanceof Block)){
			Block b=node.getAST().newBlock();
			Statement body = node.getElseStatement();
			node.setElseStatement(b);
			if (body!=null){ 
				body.delete();
				b.statements().add(body);
			}
			//defineNewBranch(b, node.getExpression());
			defineNewBranch(b, String2Expression.getExpression("!("+node.getExpression().toString()+")"));
			b.setProperty("numberParentBranch", parentBranch);
		}
		else{
			//defineNewBranch(node.getThenStatement(), node.getExpression());
			defineNewBranch((Block)node.getElseStatement(),  String2Expression.getExpression("!("+node.getExpression().toString()+")"));
			((Block)node.getElseStatement()).setProperty("numberParentBranch", parentBranch);
		}

		return true;
	}
	
	@Override
	public boolean visit(AssertStatement node){
		if(currentMethod==null) return true;
		
		IfStatement ifStatement= node.getAST().newIfStatement();
		Expression exp=(Expression) ASTNode.copySubtree(node.getAST(),(ASTNode)node.getExpression());
		//VariableDeclarationFragment vdf=(VariableDeclarationFragment) ASTNode.copySubtree(node.getAST(),(ASTNode) ((VariableDeclarationExpression) vde).fragments().get(j));
		//exp.co.copySubtree(node.getAST(), node.getExpression());//node.getAST().newConditionalExpression();
		ifStatement.setExpression(exp);
		
		Block bIf=node.getAST().newBlock();
		ifStatement.setThenStatement(bIf);
		int parentBranch=currentBranch;
		defineNewBranch(bIf, exp);
		bIf.setProperty("numberParentBranch", parentBranch);
		Block bElse=node.getAST().newBlock();
		ifStatement.setElseStatement(bElse);
		parentBranch=currentBranch;
		defineNewBranch(bElse, String2Expression.getExpression("!("+exp+")"));
		bElse.setProperty("numberParentBranch", parentBranch);
		
		if(node.getParent() instanceof Block) //try task 1155
			((Block)node.getParent()).statements().add(((Block)node.getParent()).statements().lastIndexOf(node), ifStatement);
		else if(node.getParent() instanceof SwitchStatement)
			((SwitchStatement)node.getParent()).statements().add(((SwitchStatement)node.getParent()).statements().lastIndexOf(node), ifStatement);

//		Block b=node.getAST().newBlock();
//		int parentBranch=currentBranch;
//		defineNewBranch(b, String2Expression.getExpression("true"));
//		b.setProperty("numberParentBranch", parentBranch);
		

		return true;
	}
	
	@Override
	public boolean visit(ConditionalExpression node){
		if(currentMethod==null) return true;
		
		IfStatement ifStatement= node.getAST().newIfStatement();
		Expression exp=(Expression) ASTNode.copySubtree(node.getAST(),(ASTNode)node.getExpression());
		//VariableDeclarationFragment vdf=(VariableDeclarationFragment) ASTNode.copySubtree(node.getAST(),(ASTNode) ((VariableDeclarationExpression) vde).fragments().get(j));
		//exp.co.copySubtree(node.getAST(), node.getExpression());//node.getAST().newConditionalExpression();
		ifStatement.setExpression(exp);
		
		Block bIf=node.getAST().newBlock();
		ifStatement.setThenStatement(bIf);
		int parentBranch=currentBranch;
		defineNewBranch(bIf, exp);
		bIf.setProperty("numberParentBranch", parentBranch);
		Block bElse=node.getAST().newBlock();
		ifStatement.setElseStatement(bElse);
		parentBranch=currentBranch;
		defineNewBranch(bElse, String2Expression.getExpression("!("+exp+")"));
		bElse.setProperty("numberParentBranch", parentBranch);
		
		
//		System.out.println(node);
//		System.out.println(node.getParent() +" ** "+ node.getParent().getClass()+" ** "+  (node.getParent() instanceof Statement));
//		System.out.println(node.getParent().getParent());
		Statement eStat;
		ASTNode n=node.getParent();
		while (n!=null && !(n instanceof Statement))
			n=n.getParent();
		
		if(n!=null){
			eStat=(Statement) n;
			if(eStat.getParent() instanceof Block){
				Block container=(Block)eStat.getParent();
				int index=container.statements().lastIndexOf(eStat);
				
				if(container.statements().size()>0 && index<=container.statements().size())
					if((container.statements().get(index) instanceof SuperConstructorInvocation)||(container.statements().get(index) instanceof ConstructorInvocation))
						return true; 	 //index++;		 //
				container.statements().add(index, ifStatement);
			}else if(eStat.getParent() instanceof SwitchStatement){
				SwitchStatement container=(SwitchStatement)eStat.getParent();
				int index=container.statements().lastIndexOf(eStat);
				
				if(container.statements().size()>0 && index<=container.statements().size())
					if((container.statements().get(index) instanceof SuperConstructorInvocation)||(container.statements().get(index) instanceof ConstructorInvocation))
						return true; 	 //index++;		 //
				container.statements().add(index, ifStatement);
			}
		}
		
		
		
		return true;
	}
	
	
//	@Override
//	public boolean visit(Assignment node){
//		//System.out.println(node.getExpression());
////		if(node.getRightHandSide()!=null)
////			System.out.println(node.getRightHandSide().resolveTypeBinding());
////			if(node.getRightHandSide().resolveTypeBinding()!=null 
////					&& node.getRightHandSide().resolveTypeBinding().getBinaryName().equalsIgnoreCase("Z")
////					&& !(node.getRightHandSide() instanceof BooleanLiteral)
////					){
////				System.out.println("************************************************************************");
////				System.out.println(node);
////				System.out.println(node.getRightHandSide());				
////				System.out.println("************************************************************************");		
////			}
//		
////		IfStatement ifStatement= node.getAST().newIfStatement();
////		Expression exp=(Expression) ASTNode.copySubtree(node.getAST(),(ASTNode)node.getExpression());
////		ifStatement.setExpression(exp);
////		
////		Block bIf=node.getAST().newBlock();
////		ifStatement.setThenStatement(bIf);
////		int parentBranch=currentBranch;
////		defineNewBranch(bIf, exp);
////		bIf.setProperty("numberParentBranch", parentBranch);
////		Block bElse=node.getAST().newBlock();
////		ifStatement.setElseStatement(bElse);
////		parentBranch=currentBranch;
////		defineNewBranch(bElse, String2Expression.getExpression("!("+exp+")"));
////		bElse.setProperty("numberParentBranch", parentBranch);
////		
////		Statement eStat;
////		ASTNode n=node.getParent();
////		while (n!=null && !(n instanceof Statement))
////			n=n.getParent();
////		if(n!=null){
////			eStat=(Statement) n;
////			Block container=(Block)eStat.getParent();
////			int index=container.statements().lastIndexOf(eStat);
////			if(container.statements().size()>0 && index<=container.statements().size())
////				if((container.statements().get(index) instanceof SuperConstructorInvocation)||(container.statements().get(index) instanceof ConstructorInvocation))
////					return true; 	 //index++;		 //
////			container.statements().add(index, ifStatement);
////		}
//		
//		
//		
//		return true;
//	}
	
	
	@Override
	public boolean visit(ReturnStatement node){
		if(currentMethod==null) return true;
		//System.out.println(node.getExpression());
		if(node.getExpression()!=null 
				&& node.getExpression().resolveTypeBinding()!=null  
				&& node.getExpression().resolveTypeBinding().getBinaryName()!=null)
			//System.out.println(node.getExpression().resolveTypeBinding());
			if(node.getExpression().resolveTypeBinding()!=null 
					&& node.getExpression().resolveTypeBinding().getBinaryName().equalsIgnoreCase("Z")
					&& !(node.getExpression() instanceof BooleanLiteral)
					){
				IfStatement ifStatement= node.getAST().newIfStatement();
				Expression exp=(Expression) ASTNode.copySubtree(node.getAST(),(ASTNode)node.getExpression());
				ifStatement.setExpression(exp);
				
				Block bIf=node.getAST().newBlock();
				ifStatement.setThenStatement(bIf);
				int parentBranch=currentBranch;
				defineNewBranch(bIf, exp);
				bIf.setProperty("numberParentBranch", parentBranch);
				Block bElse=node.getAST().newBlock();
				ifStatement.setElseStatement(bElse);
				parentBranch=currentBranch;
				defineNewBranch(bElse, String2Expression.getExpression("!("+exp+")"));
				bElse.setProperty("numberParentBranch", parentBranch);
				
				
				Statement eStat;

				eStat=(Statement) node;
				
				if(eStat.getParent() instanceof Block){
					Block container=(Block)eStat.getParent();
					int index=container.statements().lastIndexOf(eStat);
					if(container.statements().size()>0 && index<=container.statements().size())
						if((container.statements().get(index) instanceof SuperConstructorInvocation)||(container.statements().get(index) instanceof ConstructorInvocation))
							return true; 	 //index++;		 //
					container.statements().add(index, ifStatement);
				}else if(eStat.getParent() instanceof SwitchStatement){ //try with the task 219
					SwitchStatement container=(SwitchStatement)eStat.getParent();
					int index=container.statements().lastIndexOf(eStat);
					if(container.statements().size()>0 && index<=container.statements().size())
						if((container.statements().get(index) instanceof SuperConstructorInvocation)||(container.statements().get(index) instanceof ConstructorInvocation))
							return true; 	 //index++;		 //
					container.statements().add(index, ifStatement);
				}

		}
		return true;
	}
	
	
	@Override
	public boolean visit(SwitchStatement node){
		if(currentMethod==null) return true;
		
		node.setProperty("expression", node.getExpression());
		node.setProperty("isMultiBranchesRoot","YES");
		node.setProperty("numberParentBranch", currentBranch);
		return true;
	}
	
	@Override
	public boolean visit(SwitchCase node){
		if(currentMethod==null) return true;
		
		ASTNode ss=node.getParent();
		while(!( ss instanceof SwitchStatement))
			ss=ss.getParent();
		
		//System.out.println("statmenet s:"+ node);
		//System.out.println("SwitchStatement:"+ ss);
		
		SwitchStatement parent = (SwitchStatement)ss;// node.getParent();
	    int index = parent.statements().indexOf(node);
	    //System.out.println("index:"+ index +" size:"+parent.statements().size());
	    if(parent.statements().size()<=index+1){
	    	return false;
	    }
	    Statement nextStatement =(Statement) parent.statements().get(index+1);
	    //parent.statements().add(index,eStatement2);
	    //node.getExpression()	   
	    if((nextStatement instanceof SwitchCase))
	    	return false;
	    
	    Block b=null;	
		if(!(nextStatement instanceof Block)){
			//create block 
			b = node.getAST().newBlock();
			
			parent.statements().add(index+1,b);
			
//			Statement s = nextStatement;
//			
//			while(!(s instanceof SwitchCase)){	
//				//System.out.println("statmenet s:"+ s + " index:" +parent.statements().indexOf(s) + " size:"+parent.statements().size());
//				s.delete();
//				b.statements().add(s);
//				if(index+2<parent.statements().size()){
//					s=(Statement) parent.statements().get(index+2);
//				}else
//					break;
//			}
			b.setProperty("numberParentBranch",parent.getProperty("numberParentBranch"));
			//b.setProperty("toBeDeleted","YES");
		}
		else{
			b=(Block) nextStatement;
			b.setProperty("numberParentBranch",parent.getProperty("numberParentBranch"));
		}
		
		String txtExpression="";
		if(node.getExpression()!=null)
			//TODO add the or expression for all cases
			txtExpression=parent.getExpression() +"=="+node.getExpression();
		else 
			for(int i=0;i<parent.statements().size();i++){
				if(parent.statements().get(i) instanceof SwitchCase && !parent.statements().get(i).equals(node)){
					if (txtExpression!="") txtExpression+=" && ";
						txtExpression+=parent.getExpression() +"!="+((SwitchCase)parent.statements().get(i)).getExpression();
				}
			}

		defineNewBranch(b,  String2Expression.getExpression(txtExpression));
		return true;
	}	
	
	@Override
	public boolean visit(ForStatement node){
		if(currentMethod==null) return true;
		
		if(node.getProperty("isEncapsulated")==null){
			//return false;
		}
		
		//encapsulatingNodeInBlock(node);
		node.setProperty("expression", node.getExpression());
		node.setProperty("isMultiBranchesRoot","YES");
		Block b = node.getAST().newBlock();
		if(!(node.getBody() instanceof Block)){
			//create block 	
			Statement body = node.getBody();
			node.setBody(b);
			body.delete();
			b.statements().add(body);
			b.setProperty("numberParentBranch",currentBranch);
		}
		else{
			b=(Block) node.getBody();
			b.setProperty("numberParentBranch",currentBranch);
		}
		
//		Block parent = (Block) node.getParent();
//	    int index = parent.statements().indexOf(node);
//	    
//		//put initializers out side of forstatement
//		for(int i=0;i<node.initializers().size();i++ ){
//			Expression vde=(Expression) node.initializers().get(i);
//			if (vde instanceof VariableDeclarationExpression){
//				for(int j=0;j<((VariableDeclarationExpression) vde).fragments().size();j++){
//					VariableDeclarationFragment vdf=(VariableDeclarationFragment) ASTNode.copySubtree(node.getAST(),(ASTNode) ((VariableDeclarationExpression) vde).fragments().get(j));
//					VariableDeclarationStatement vds=node.getAST().newVariableDeclarationStatement(vdf);
//					parent.statements().add(index,vds);
//				}
//			}
//			//else
//				
//			node.initializers().remove(i);
//		}

		defineNewBranch(b, node.getExpression());
	    return true;
	}	

//	@Override
//	public void endVisit(ForStatement node){
//		
////		if(node.getProperty("isEncapsulated")==null){
////			encapsulatingNodeInBlock(node);
////			node.setProperty("expression", node.getExpression());
////			node.setProperty("isEncapsulated","YES");
////		}
//	}
	
	@Override
	public boolean visit(WhileStatement node){
		if(currentMethod==null) return true;
		
		node.setProperty("expression", node.getExpression());
		node.setProperty("isMultiBranchesRoot","YES");
		Block b = node.getAST().newBlock();
		if(!(node.getBody() instanceof Block)){
			//create block 	
			Statement body = node.getBody();
			node.setBody(b);
			body.delete();
			b.statements().add(body);
			b.setProperty("numberParentBranch",currentBranch);
		}
		else{
			b=(Block) node.getBody();
			b.setProperty("numberParentBranch",currentBranch);
		}
		
		defineNewBranch(b, node.getExpression());
	    return true;
	}	
	
	@Override
	public boolean visit(DoStatement node){
		if(currentMethod==null) return true;
		
		node.setProperty("expression", node.getExpression());
		node.setProperty("isMultiBranchesRoot","YES");
		Block b = node.getAST().newBlock();
		if(!(node.getBody() instanceof Block)){
			//create block 	
			Statement body = node.getBody();
			node.setBody(b);
			body.delete();
			b.statements().add(body);
			b.setProperty("numberParentBranch",currentBranch);
		}
		else{
			b=(Block) node.getBody();
			b.setProperty("numberParentBranch",currentBranch);
		}
		
		defineNewBranch(b, node.getExpression());
		return true;
	}	

	@Override
	public boolean visit(Block node){
		if(currentMethod==null) return true;
		
		if(node.getProperty("numberBranch")!=null)		
			currentBranch=(Integer)node.getProperty("numberBranch");	
		return true;
	}
	
	@Override
	public void endVisit(Block node){
		if(currentMethod==null) return;
		
		if(node.getProperty("numberParentBranch")!=null)
			currentBranch=(Integer) node.getProperty("numberParentBranch");
	}	
}
