package csbst.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import csbst.testing.BranchDistance;

/**
 * class that encapsulates some extracted properties from the source code by a static analysis. 
 * This mainly instruments, analysis the class under test and keeps some required data for class testing: 
 * a list of methods that directly or indirectly may use a given data member (there are four types of use: constructor, reporter, transformer, Others) ;
 * a list of branches that invoke a given method, this latter is identified by a unique integer number;
 * the branch dominator of a given branch, wherein this latter is control dependent on the former (branchDominator);
 * a list of parameters and data members may influence a given branch(parameters and data members).
 * the list of difficulty coefficients for each branch (a dc for each individual clause in the conjunctive form) 
 *
 */
public class InstrumentorOld extends ASTVisitor{
	private AST ast;
	private CompilationUnit compilationUnit;
	private Integer cBranch; //current branch
	private int currentBranch;
	private int branchesCounter;
	private TypeDeclaration classNode; //the class under test
	private int methodNodeIndex; //the index of the current method
	private Map<Integer,Integer> branchDominator=new HashMap<Integer,Integer>();//<branch,dominator>
	private Stack<Integer> cDominator =new Stack<Integer>(); //current dominator is in the top
	private Map<Integer,ArrayList<Double>> branchDC=new HashMap<Integer,ArrayList<Double>>();//<branch,[Difficulty Coefficients]>
	private Map<Integer,ASTNode> branch2ASTNode=new HashMap<Integer,ASTNode>();
	private Map<Integer,Integer> branch2Method=new HashMap<Integer,Integer>(); //<branch,the method that contains the branch>
	private List<Set<IVariableBinding>> dataMembersIfluencePropagators=new ArrayList<Set<IVariableBinding>>();
	private List<Set<IVariableBinding>> parametersIfluencePropagators=new ArrayList<Set<IVariableBinding>>();
	
	
	public InstrumentorOld(CompilationUnit cU){
		compilationUnit=cU;
		 this.ast=cU.getAST();
		 cBranch=new Integer(-1);	
		 currentBranch=0;
		 //parentBranch=0;
	}
	
	/**
	 * addExecutionPathTracer allows to add a new statement at the index "index" in a block "b" to trace the program execution
	 * @param index is the position wherein the tracer statement must be inserted
	 * @param b is the block wherein the new statement must be inserted
	 * @param iteration is the iteration number for a loop 1 is its default value
	 * @param oldBranch is the current branch number 
	 */	
	private void addExecutionPathTracer(Block b, int index, String iteration, int oldBranch){
		if(b.getParent() instanceof Initializer)
			return;
		
		int branch;
		if((b.getParent() instanceof IfStatement 
				&& ((IfStatement)b.getParent()).getElseStatement().equals(b)))
			branch=(Integer)b.getParent().getProperty("BranchNumber"); //this property (BranchNumber) has been saved while analyzing branches
		else if(oldBranch>-1)
				branch=oldBranch;
			else{
					cBranch++;
					branch=cBranch;
				}
		//Create a new trace statement
		MethodInvocation trace = ast.newMethodInvocation();
		trace.setName(ast.newSimpleName("maintainPathTrace"));
		NumberLiteral p1;
		p1=ast.newNumberLiteral(""+branch);
		trace.arguments().add(p1);
		if((b.getParent() instanceof ForStatement)
				||(b.getParent() instanceof DoStatement)
				||(b.getParent() instanceof WhileStatement)){
			trace.arguments().add(ast.newSimpleName("i"+branch));
		}		
		else{
			NumberLiteral p2;
			if(b.getParent() instanceof IfStatement 
					&& ((IfStatement)b.getParent()).getElseStatement().equals(b)){
				p2=ast.newNumberLiteral("-1");
				trace.arguments().add(p2);
			}
			else
				trace.arguments().add((Expression) ASTNode.copySubtree(ast, String2Expression.getExpression(iteration)));
		}
		//Insert the trace stament in the AST at the block b
		ExpressionStatement eStatement2=ast.newExpressionStatement(trace);
		b.statements().add(index,eStatement2);		
	}
	
	/*
	 * getMethodIndexFromSignature returns the index of a method by using its signature
	 * 
	 */
	private int getMethodIndexFromSignature(String methodSignature){
		for(int i=0;i<classNode.getMethods().length;i++)			
			if(classNode.getMethods()[i].resolveBinding().getMethodDeclaration().toString().equals(methodSignature))
				return i;
		return -1;
	}

	private int getDataMemberIndexFromSignature(String DataMemberSignature){
		int index=0;
		for(int i=0;i<classNode.getFields().length;i++){
			String modifiers= classNode.getFields()[i].modifiers().toString();
			modifiers=modifiers.replace(",", "");
			modifiers=modifiers.replace("[", "");
			modifiers=modifiers.replace("]", "");
			String type=classNode.getFields()[i].getType().toString();
			for(int j=0;j<classNode.getFields()[i].fragments().size();j++,index++)	{
				int equalIndex=classNode.getFields()[i].fragments().get(j).toString().indexOf("=");
				if(equalIndex<0) equalIndex=classNode.getFields()[i].fragments().get(j).toString().length();	
				String name=classNode.getFields()[i].fragments().get(j).toString().substring(0, equalIndex);
				
				String cSignature=modifiers +" "+type+" "+name;
				if(cSignature.equals(DataMemberSignature))
					return index;
			}
		}
		return -1;
	}
	
//	@Override
//	public boolean visit(Assignment node){
//		//addSnapshot(node);
//		if (! (node.getLeftHandSide() instanceof SimpleName))
//			return false;
//		
//		final IVariableBinding variableName=(IVariableBinding) ((SimpleName)node.getLeftHandSide()).resolveBinding();
//		
//		//check if this statement makes a variable as an influence propagator for a data member or a parameter
//		//System.out.println(" current Expression : " + node.getRightHandSide());
//		node.getRightHandSide().accept(new ASTVisitor() {
//            public boolean visit(SimpleName sN) {
//            	
//            	if(sN.resolveBinding()!=null){
//            		//System.out.println(" current variable : " +sN.resolveBinding());
//            		for(int i=parametersIfluencePropagators.size()-1;i>=0;i--)
//            			if (parametersIfluencePropagators.get(i).contains(sN.resolveBinding()))//{
//            				parametersIfluencePropagators.get(i).add(variableName);
//            		
//            		for(int i=dataMembersIfluencePropagators.size()-1;i>=0;i--)
//            			if (dataMembersIfluencePropagators.get(i).contains(sN.resolveBinding()))//{
//            				dataMembersIfluencePropagators.get(i).add(variableName);
//	                	//System.out.println(" Porteur : "+sN +" new propagator" + variableName);
//	                	//return false;
//	                //}
//            	}
//                return true;
//            }
//        });						
//		return true;
//	}
	
//	private boolean IsPropagator(final IVariableBinding var,final Expression exp){
//		//boolean ref=false;
//		final Boolean referenced;//=new Boolean(false);
//        exp.accept(new ASTVisitor() {
//            public boolean visit(SimpleName node) {
//                if (node.resolveBinding().equals(var)){
//                	//referenced=new Boolean(true);
//                	return false;
//                }
//                return true;
//            }
//        });
//        
//		return false;	
//	}
	
	@Override
	public boolean visit(TypeDeclaration node){
		classNode=node;
		branchesCounter=0;
//		 //create a set of influence propagators for each data member (not static or final)
//		 //classNode.getFields()[0].getAST()
//		 for(int i=classNode.getFields().length;i>0;i--){
//			 for(int j=classNode.getFields()[i-1].fragments().size();j>0;j--){
//				 Set<IVariableBinding> tmpSet=new HashSet<IVariableBinding>();
//				 tmpSet.add((IVariableBinding)((VariableDeclaration) classNode.getFields()[i-1].fragments().get(j-1)).resolveBinding());
//				 dataMembersIfluencePropagators.add(tmpSet);				 
//				 //System.out.println(((VariableDeclaration) classNode.getFields()[i-1].fragments().get(j-1)).resolveBinding());//.resolveBinding()
//			 }
//		 }
//	
		return true;
	}

	private void defineNewBranch(ASTNode node){
		branchesCounter++;
		node.setProperty("numberParentBranch", currentBranch);
		currentBranch=branchesCounter;		
	}
	
	@Override
	public boolean visit(MethodDeclaration node){
		node.resolveBinding().getMethodDeclaration().toString();
		methodNodeIndex=getMethodIndexFromSignature(node.resolveBinding().getMethodDeclaration().toString());
		cDominator.clear();
		defineNewBranch(node);
		return true;
	}

	@Override
	public void endVisit(MethodDeclaration node){
		currentBranch=(Integer) node.getProperty("numberParentBranch");
	}
	
	
	private void computeDifficultyCoefficient(Integer branch,Expression exp, List decomposedExpression){
		
		InfixExpressionDecomposer iExpDecomposer;
		if(decomposedExpression.get(0)!=null && ((Boolean)decomposedExpression.get(0))==true)
			iExpDecomposer=new InfixExpressionDecomposer(InfixExpression.Operator.CONDITIONAL_AND);
		else 
			iExpDecomposer=new InfixExpressionDecomposer(InfixExpression.Operator.CONDITIONAL_OR);
		
		exp.accept(iExpDecomposer);
		
		decomposedExpression.addAll(iExpDecomposer.getExpressionsList());
		
		//compute Difficulty coefficient
		ArrayList dCs=new ArrayList();
		for(InfixExpression e :iExpDecomposer.getExpressionsList())
			try {
				double DC=DifficultyCoefficient.getDifficultyCoefficient(e);
				dCs.add(new Double(DC));					
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		branchDC.put(cBranch, dCs);
	}

	private void branchDistanceSimpleInstrumentation(Block bInstrumentation,int index,Integer branch, String iteration, boolean forNegation) throws Exception{
		Expression expression=(Expression) branch2ASTNode.get(branch).getProperty("Expression") ;//branchExpression.get(branch);
		//create method invocation to compute branch distance	
		MethodInvocation gBD=ast.newMethodInvocation(); 
		gBD.setName(ast.newSimpleName("maintainBranchDistance"));
		//create argument
		Expression exp;
		if(!forNegation)
			exp=(Expression) ASTNode.copySubtree(ast, String2Expression.getExpression(BranchDistance.expressionFormater(expression)));
		else {
	        //create an expression using the negation of the node condition and a test of the existence of the node in the testTarget
			PrefixExpression notExpression=ast.newPrefixExpression();
			notExpression.setOperator(PrefixExpression.Operator.NOT);
			Expression exp2=(Expression) ASTNode.copySubtree(ast,expression);
			ParenthesizedExpression pExp=ast.newParenthesizedExpression();
			pExp.setExpression(exp2);
			notExpression.setOperand(pExp);			
			exp=(Expression) ASTNode.copySubtree(ast, String2Expression.getExpression(BranchDistance.expressionFormater(notExpression)));
		}
		gBD.arguments().add(exp);
		ExpressionStatement gBDStatement=ast.newExpressionStatement(gBD);
		
		//create an ifstatement to check the existence in the test target
		MethodInvocation isPartOfTestTarget=ast.newMethodInvocation(); 
		isPartOfTestTarget.setName(ast.newSimpleName("isPartOfTestTarget"));
		isPartOfTestTarget.arguments().add(ast.newNumberLiteral(""+branch));
		if(iteration=="0"||iteration=="1")
			isPartOfTestTarget.arguments().add(ast.newNumberLiteral(""+iteration));
		else 
			isPartOfTestTarget.arguments().add((Expression) ASTNode.copySubtree(ast, String2Expression.getExpression(iteration)));
		
		IfStatement ifStatement =ast.newIfStatement();
		ifStatement.setExpression(isPartOfTestTarget);
		ifStatement.setThenStatement(gBDStatement);
		
		//add the if statement to the program
		bInstrumentation.statements().add(index,ifStatement);

	}	
	
	private void branchDistanceInstrumentation(Block bInstrumentation,int index, Integer branch, String iteration, boolean forNegation) throws Exception{	
		Expression expression=(Expression) branch2ASTNode.get(branch).getProperty("Expression") ;

        //create an expression using the negation of the node condition and a test of the existence of the node in the testTarget
		PrefixExpression notExpression=ast.newPrefixExpression();
		notExpression.setOperator(PrefixExpression.Operator.NOT);
		Expression exp2=(Expression) ASTNode.copySubtree(ast,expression);
		ParenthesizedExpression pExp=ast.newParenthesizedExpression();
		pExp.setExpression(exp2);
		notExpression.setOperand(pExp);
		
		//create method invocation to compute branch distance	
		MethodInvocation gBD=ast.newMethodInvocation(); 
		gBD.setName(ast.newSimpleName("maintainBranchDistance"));
		//create argument
		Expression exp;
		if(!forNegation)
			exp=(Expression) ASTNode.copySubtree(ast, String2Expression.getExpression(BranchDistance.expressionFormater(expression)));
		else 
			exp=(Expression) ASTNode.copySubtree(ast, String2Expression.getExpression(BranchDistance.expressionFormater(notExpression)));
		
		gBD.arguments().add(exp);
		//insert a new statement in the original program
		ExpressionStatement gBDStatement=ast.newExpressionStatement(gBD);
		
		//check the existence in the test target
		MethodInvocation methodInvocation=ast.newMethodInvocation(); 
		methodInvocation.setName(ast.newSimpleName("isPartOfTestTarget"));
		methodInvocation.arguments().add(ast.newNumberLiteral(""+branch));
		if(iteration=="0"||iteration=="1")
			methodInvocation.arguments().add(ast.newNumberLiteral(""+iteration));
		else 
			methodInvocation.arguments().add((Expression) ASTNode.copySubtree(ast, String2Expression.getExpression(iteration)));
		
		//final if expression
		InfixExpression ifExpression=ast.newInfixExpression();
		ifExpression.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
		if(!forNegation)
			ifExpression.setLeftOperand(notExpression);
		else
			ifExpression.setLeftOperand((Expression) ASTNode.copySubtree(ast,pExp));
		
		ifExpression.setRightOperand(methodInvocation);
		IfStatement ifStatement =ast.newIfStatement();
		ifStatement.setExpression(ifExpression);

		ifStatement.setThenStatement(gBDStatement);
		
		//add the if statement before the node
		bInstrumentation.statements().add(index, ifStatement);

	}	
		
	@Override
	public boolean visit(Block node){
		
		if(node.getProperty("Source")=="Instrumentation")
			return true;
		
		addExecutionPathTracer(node,0,"1",-1);
		
		//Maintain dominator
		Integer tmpDominator=new Integer(-1);
		if(!(cDominator.empty()))
			tmpDominator=cDominator.peek();
		branchDominator.put(new Integer(cBranch),new Integer(tmpDominator));
		cDominator.push(new Integer(cBranch));
		return true;
	}
	
	@Override
	public void endVisit(Block node){
		if(node.getProperty("Source")=="Instrumentation")
			return;
		
		cDominator.pop();
	}
	
//	
//	@Override
//	public boolean visit(MethodInvocation node){
//		if (node.resolveMethodBinding()!=null){
//			String qName=node.resolveMethodBinding().getDeclaringClass().getTypeDeclaration().getName();
//			if(qName.equals(classNode.getName()) ){
//				int calledMethodIndex=getMethodIndexFromSignature(node.resolveMethodBinding().getMethodDeclaration().toString());
//				if(!methodInvokedBy.containsKey(calledMethodIndex)){
//					methodInvokedBy.put(new Integer(calledMethodIndex),new ArrayList<Integer>());				
//				}
//				methodInvokedBy.get(calledMethodIndex).add(cBranch);
//			}
//		 }
//		return true;
//	}

//	@Override
//	public boolean visit(SimpleName node){
//		if (node.resolveBinding()!=null && node.resolveBinding().getKind()==3 ){
//			int index=getDataMemberIndexFromSignature(node.resolveBinding().toString());
//			//IVariableBinding variableBinding = null;
//			//variableBinding = (IVariableBinding)node.resolveBinding();
//			//variableBinding.isParameter();variableBinding.isField();
//			
//			// TODO Determine the type of use : Constructor, Reporter, Transformer, Others
//			// At this moment we need only the transformers (wherein the data member is in the left side of an assignment statement)
//			if(index!=-1){
//				if(!dataMemberTransformedBy.containsKey(index)){
//					dataMemberTransformedBy.put(new Integer(index),new ArrayList<Integer>());
//				}
//				dataMemberTransformedBy.get(index).add(methodNodeIndex);
//				//System.out.println("DataMember : "+ (IVariableBinding)node.resolveBinding() +" Used By: "+ classNode.getMethods()[methodNodeIndex].resolveBinding().getMethodDeclaration().toString());
//			}
//		}
//		return true;
//	}	
	
	@Override
	public boolean visit(IfStatement node){
		if(node.getExpression().toString().contains("isPartOfTestTarget"))
			return false;
		
//		// determine the influence parameters vector and data member vector 
//			node.getExpression().accept(new ASTVisitor() {
//	            public boolean visit(SimpleName sN) {
//	            	
//	            	if(sN.resolveBinding()!=null){
//	            		//System.out.println(" current variable : " +sN.resolveBinding());
//	            		for(int i=parametersIfluencePropagators.size()-1;i>=0;i--)
//	            			if (parametersIfluencePropagators.get(i).contains(sN.resolveBinding()))
//	            				;//{
//	            				//parametersIfluencePropagators.get(i).add(variableName);
//	            		
//	            		for(int i=dataMembersIfluencePropagators.size()-1;i>=0;i--)
//	            			if (dataMembersIfluencePropagators.get(i).contains(sN.resolveBinding()))
//	            				;//{
//	            				//dataMembersIfluencePropagators.get(i).add(variableName);
//		                	//System.out.println(" Porteur : "+sN +" new propagator" + variableName);
//		                	//return false;
//		                //}
//	            	}
//	                return true;
//	            }
//	        });		
		
		if(!(node.getThenStatement() instanceof Block)){
			Block b=ast.newBlock();
			Statement body = node.getThenStatement();
			node.setThenStatement(b);
			body.delete();
			b.statements().add(body);
		}
		if(!(node.getElseStatement() instanceof Block)){
			Block b=ast.newBlock();
			Statement body = node.getElseStatement();
			node.setElseStatement(b);
			if (body!=null){ 
				body.delete();
				b.statements().add(body);
			}
		}
		
		node.setProperty("BranchNumber", cBranch+1);
		node.setProperty("Expression", node.getExpression());
		branch2ASTNode.put(cBranch+1,node);
		branch2Method.put(cBranch+1, methodNodeIndex);
		return true;
	}

	@Override
	public void endVisit(IfStatement node){
		if(node.getExpression().toString().contains("isPartOfTestTarget"))
			return;
		
		try {
			branchDistanceSimpleInstrumentation((Block)node.getThenStatement(),0,(Integer)node.getProperty("BranchNumber"),"-1",true);
			branchDistanceSimpleInstrumentation((Block)node.getElseStatement(),0,(Integer)node.getProperty("BranchNumber"),"1",false);
		} catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean visit(SwitchCase node){
		SwitchStatement parent = (SwitchStatement) node.getParent();
	    int index = parent.statements().indexOf(node);
	    Statement nextStatement =(Statement) parent.statements().get(index+1);
	    //parent.statements().add(index,eStatement2);
	    //node.getExpression()
		if(!(nextStatement instanceof Block)){
			//create block 
			Block b = ast.newBlock();
			Statement body = nextStatement;
			parent.statements().add(index+1,b);
			body.delete();
			b.statements().add(body);
		}
		
		String txtExpression="";
		if(node.getExpression()!=null)
			txtExpression=parent.getExpression() +"=="+node.getExpression();
		else 
			for(int i=0;i<parent.statements().size();i++){
				if(parent.statements().get(i) instanceof SwitchCase && !parent.statements().get(i).equals(node)){
					if (txtExpression!="") txtExpression+=" && ";
						txtExpression+=parent.getExpression() +"!="+((SwitchCase)parent.statements().get(i)).getExpression();
				}
			}
		
		node.setProperty("BranchNumber", cBranch+1);
		node.setProperty("Expression", (Expression) ASTNode.copySubtree(ast, String2Expression.getExpression(txtExpression)));
		branch2ASTNode.put(cBranch+1,node);	
		branch2Method.put(cBranch+1, methodNodeIndex);
		return true;
	}	
	
	@Override
	public void endVisit(SwitchCase node){
		
	    try {
			ASTNode switchStatement =node.getParent();
			Block b =(Block) switchStatement.getParent();
			int index = b.statements().indexOf(switchStatement);
	    	int branch=(Integer) node.getProperty("BranchNumber");
	    	branchDistanceInstrumentation(b,index,branch,"1",false);
	    	//instrumentLoop(body,branch);
		} catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean visit(ForStatement node){
		if(node.getProperty("Encapsulated")==null){
			return false;
		}
		
		Block b = ast.newBlock();
		if(!(node.getBody() instanceof Block)){
			//create block 	
			Statement body = node.getBody();
			node.setBody(b);
			body.delete();
			b.statements().add(body);
		}
		else
			b=(Block) node.getBody();
		
		Block parent = (Block) node.getParent();
	    int index = parent.statements().indexOf(node);
	    
		//put initializers out side of forstatement
		for(int i=0;i<node.initializers().size();i++ ){
			Expression vde=(Expression) node.initializers().get(i);
			if (vde instanceof VariableDeclarationExpression){
				for(int j=0;j<((VariableDeclarationExpression) vde).fragments().size();j++){
					VariableDeclarationFragment vdf=(VariableDeclarationFragment) ASTNode.copySubtree(ast,(ASTNode) ((VariableDeclarationExpression) vde).fragments().get(j));
					VariableDeclarationStatement vds=ast.newVariableDeclarationStatement(vdf);
					parent.statements().add(index,vds);
				}
			}
			node.initializers().remove(i);
		}

		node.setProperty("BranchNumber", cBranch+1);
		node.setProperty("Expression", node.getExpression());
		branch2ASTNode.put(cBranch+1,node);
		branch2Method.put(cBranch+1, methodNodeIndex);
	    return true;
	}	

	@Override
	public void endVisit(ForStatement node){
		if(node.getProperty("Encapsulated")==null){
			encapsulatingNodeInBlock(node);
			node.setProperty("Encapsulated","YES");
			return;
		}
	    try {
	    	Block body=(Block) node.getBody();
	    	int branch=(Integer) node.getProperty("BranchNumber");
	    	instrumentLoop(body,branch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean visit(WhileStatement node){
		Block b = ast.newBlock();
		if(!(node.getBody() instanceof Block)){
			//create block 	
			Statement body = node.getBody();
			node.setBody(b);
			body.delete();
			b.statements().add(body);
		}
		else
			b=(Block) node.getBody();
		
		node.setProperty("BranchNumber", cBranch+1);
		node.setProperty("Expression", node.getExpression());
		branch2ASTNode.put(cBranch+1,node);	
		branch2Method.put(cBranch+1, methodNodeIndex);
	    return true;
	}	

	@Override
	public void endVisit(WhileStatement node){
		try {
			int branch=(Integer)node.getProperty("BranchNumber");			
			instrumentLoop((Block)node.getBody(), branch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	@Override
	public boolean visit(DoStatement node){
		Block b = ast.newBlock();
		if(!(node.getBody() instanceof Block)){
			//create block 	
			Statement body = node.getBody();
			node.setBody(b);
			body.delete();
			b.statements().add(body);
		}
		else
			b=(Block) node.getBody();

		node.setProperty("BranchNumber", cBranch+1);
		node.setProperty("Expression", node.getExpression());
		branch2ASTNode.put(cBranch+1,node);	
		branch2Method.put(cBranch+1, methodNodeIndex);
		return true;
	}	

	@Override
	public void endVisit(DoStatement node){
	    try {
	    	Block body=(Block) node.getBody();
	    	int branch=(Integer) node.getProperty("BranchNumber");
	    	instrumentLoop(body,branch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Override
//	public boolean visit(Assignment node){
//		ASTNode sN=node.getLeftHandSide();
//		
//		if (sN.getNodeType() != ASTNode.SIMPLE_NAME)
//            return true;
//		
//		if (((SimpleName) sN).resolveBinding().getKind() == IBinding.VARIABLE)
//			System.out.println(sN);
//		//useDefAnalyser.
//	    System.out.println(useDefAnalyser.findUsesDefsOf(sN).toString());
//	    return true;
//	}
	
	private void encapsulatingNodeInBlock(ASTNode node){
		Block parent = (Block) node.getParent();
	    int index = parent.statements().indexOf(node);
	    
		Block newBlock = ast.newBlock();
		parent.statements().add(newBlock);
		newBlock.setProperty("Source","Instrumentation");
		
		node.delete();
		newBlock.statements().add(node);
	}

	private void instrumentLoop(Block body, int branch) throws Exception{
		//		
		Statement loopNode=(Statement) body.getParent();
		Block parent = (Block) loopNode.getParent();
	    int index = parent.statements().indexOf(loopNode);
	    
	 	//insert  a variable declaration to track the iteration number inside a loop
	    String varName="i"+branch;
	    insertIntegerVariableDeclaration(parent, index, varName,"1");
	    
	    //increment iteration tracer
	    incrementIntegerVariable(body,varName);		
	    
	    //branch distance instrumentation
		branchDistanceSimpleInstrumentation(body,0,branch,"-1*i"+branch,true);
		branchDistanceSimpleInstrumentation(parent,index+2,branch,"i"+branch,false);
	}
	
	private void incrementIntegerVariable(Block block, String name){
		final PostfixExpression inc =  ast.newPostfixExpression();
		inc.setOperator(org.eclipse.jdt.core.dom.PostfixExpression.Operator.INCREMENT);
		inc.setOperand(ast.newSimpleName(name));	
		ExpressionStatement eStatement=ast.newExpressionStatement(inc);
		block.statements().add(eStatement);		
	}
	
	private void insertIntegerVariableDeclaration(Block block, int index, String name, String initialize){
		final VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName(name));
		vdf.setInitializer(ast.newNumberLiteral(initialize));
		
		final VariableDeclarationStatement vde = ast.newVariableDeclarationStatement(vdf);
		vde.setType(ast.newPrimitiveType(PrimitiveType.INT));
		
		block.statements().add(index,vde);
	}
//	/**
//	 * 1- Starts from parameters and find there direct use then make influence at each conditional statement has a direct use.
//	 * 2- for each assignment use propagate this use (indirect use).
//	 * 3- each assignment in an influenced condition it is also influenced by the parameter.
//	 * @param varBinding
//	 * @param method
//	 * @return
//	 */
//    private Set<ASTNode> findRefsToVariable(final IVariableBinding varBinding, MethodDeclaration method) {
//        final Set<ASTNode> refs= new HashSet<ASTNode>();
//
//        method.accept(new ASTVisitor() {
//            public boolean visit(SimpleName node) {
//                if (node.resolveBinding().equals(varBinding))
//                    refs.add(node);
//                return false;
//            }
//        });
//        return refs;
//    }
	
//	public static void main(String[] args) throws IOException{
//		ASTBuilder ASTRoot=new ASTBuilder("//Applications//eclipse//test","ArithmeticUtils.java");
//		CompilationUnit node=ASTRoot.getASTRoot();	
//		//AST ast=node.getAST();
//		InstrumentorOld iTest=new InstrumentorOld(node);
//		node.accept(iTest);
//		
//		 FileWriter fstream = new FileWriter("//Applications//eclipse//test//branchDistanceInst.java");
//		 BufferedWriter out = new BufferedWriter(fstream);
//		 out.write(node.toString());
//		 //Close the output stream
//		 out.close();
//	}	
}
