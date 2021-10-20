package csbst.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


/**
 * class that encapsulates some extracted properties from the source code by a static analysis. 
 * This mainly analysis the class under test and keeps influence relationship between  all branches and parameters or data members.
 *
 */


public class InfluenceAnalyser extends ASTVisitor{
	private TypeDeclaration classNode; //the class under test
	//private Map<Integer,Vector<IVariableBinding>>  directInfluenceVector=new HashMap<Integer,Vector<IVariableBinding>>();
	private Map<IVariableBinding, Set<IVariableBinding>> directIfluencePropagators=new HashMap<IVariableBinding,Set<IVariableBinding>>();
	private Set<IVariableBinding> indirectIfluencer=new HashSet<IVariableBinding>();
	
	/**
	 * influencePorterChecker checks if the variable sN that is assigned the expression exp may be an influence porter 
	 * for any data member or parameter of the current method
	 * @param sN : is an assigned variable at a assignment or initialization statement
	 * @param exp : is the assigned expression to sN
	 */
	private void influencePorterChecker(SimpleName sN,Expression exp){
		final IVariableBinding variableName=(IVariableBinding) sN.resolveBinding();	
		
		for(IVariableBinding V:indirectIfluencer)
			directIfluencePropagators.get(V).add(variableName);
		//check if this statement makes a variable as an influence porter for a data member or a parameter in this case add it to the propagator set
		exp.accept(new ASTVisitor() {
            public boolean visit(SimpleName sN) {            	
            	if(sN.resolveBinding()!=null){
            		for(Map.Entry<IVariableBinding, Set<IVariableBinding>> entry:directIfluencePropagators.entrySet())
            			if (entry.getValue().contains(sN.resolveBinding()))
            				entry.getValue().add(variableName);
            	}
                return true;
            }
        });		
	}

	/**
	 * maintainInfluenceVectors determines the set of parameters and data members that influence the current branch 
	 * @param branchNumber is the current branch number
	 * @param exp	is the conditional expression to satisfy for reaching the branch branchNumber
	 */
	private void maintainBranchInfluenceVectors(final Block node, final Set<IVariableBinding> indirectInfluencerSet){
		//, Expression exp, final Set<IVariableBinding> indirectInfluencerSet){
		//TODO 1) maintain indirect influence 2) try to manage Then and Else in an accurate way (the solution is in stakes)
		final Vector<IVariableBinding>parmsInf=new Vector<IVariableBinding>();
		node.getProperty("inDirectIfluencePropagators");
		Expression exp=(Expression) node.getParent().getProperty("expression");
		if(exp==null)
			return;
		
		exp.accept(new ASTVisitor() {
			private void analyse(SimpleName sN){
            	if(sN.resolveBinding()!=null){
            		Map<IVariableBinding, Set<IVariableBinding>> tmpDIP=(Map<IVariableBinding, Set<IVariableBinding>>) node.getParent().getProperty("inDirectIfluencePropagators");
            		for(Map.Entry<IVariableBinding, Set<IVariableBinding>> entry:tmpDIP.entrySet()) //directIfluencePropagators.entrySet()
            			if ((entry.getValue().contains(sN.resolveBinding()))){
            				parmsInf.add(entry.getKey()); //mention that the parameter or data member as influencer of this branch
            				entry.getValue().add((IVariableBinding) sN.resolveBinding());
            				if(!indirectIfluencer.contains(entry.getKey()))
            					indirectInfluencerSet.add(entry.getKey()); //keep this variable as new indirect influencer to remove it when leaving this branch 
            				indirectIfluencer.add(entry.getKey());           				
            			}
            		
            	}
			}
            public boolean visit(SimpleName sN) {           	
            	analyse(sN);
                return true;
            }
            public boolean visit(FieldAccess fA) {  
            	SimpleName sN=fA.getName();
            	analyse(sN);
                return true;
            }
        });
		//directInfluenceVector.put((Integer) node.getProperty("numberBranch"), parmsInf);
		Set<IVariableBinding>influencers=new HashSet<IVariableBinding>();
		node.setProperty("influencers", parmsInf);
		//System.out.println("branchNumber : "+ node.getProperty("numberBranch") + " Expression : "+ node.getProperty("expression") +"  indirectIfluencer : "+ node.getProperty("influencers"));
		//dataMemebersInfluencedVector.put(branchNumber, dataMmInf);
	}

	@Override
	public boolean visit(Assignment node){
		if (! (node.getLeftHandSide() instanceof SimpleName || node.getLeftHandSide() instanceof FieldAccess))
			return false;
		
		final SimpleName sN;
		if (node.getLeftHandSide() instanceof SimpleName)
			sN=(SimpleName)node.getLeftHandSide();	
		else 
			sN=((FieldAccess)node.getLeftHandSide()).getName();	
		
		influencePorterChecker(sN, node.getRightHandSide());
						
		return true;
	}
	
	@Override
	public boolean visit(TypeDeclaration node){
		classNode=node;
		//add any data member as influence porter for itself
		 for(int i=classNode.getFields().length;i>0;i--){
			 for(int j=classNode.getFields()[i-1].fragments().size();j>0;j--){
				 Set<IVariableBinding> tmpSet=new HashSet<IVariableBinding>();
				 IVariableBinding field=(IVariableBinding)((VariableDeclaration) classNode.getFields()[i-1].fragments().get(j-1)).resolveBinding();
				 tmpSet.add(field);
				 directIfluencePropagators.put(field,tmpSet);
			 }
		 }
	
		return true;
	}
	
	@Override
	public boolean visit(MethodDeclaration node){
		//add any parameter as influence porter for itself
		 for(int i=node.parameters().size();i>0;i--){
				 Set<IVariableBinding> tmpSet=new HashSet<IVariableBinding>();
				 IVariableBinding parameter=(IVariableBinding)((VariableDeclaration)node.parameters().get(i-1)).resolveBinding();
				 tmpSet.add(parameter);
				 directIfluencePropagators.put(parameter,tmpSet);	
		 }
		return true;
	}	
	
	@Override
	public void endVisit(MethodDeclaration node){
		//delete all parameters
		for(int i=node.parameters().size();i>0;i--){
			 IVariableBinding parameter=(IVariableBinding)((VariableDeclaration)node.parameters().get(i-1)).resolveBinding();
					 directIfluencePropagators.remove(parameter);
		 }
	}	
	
	@Override
	public boolean visit(Block node){		
		//If the block is not a branch leave this method
		if(node.getProperty("numberBranch")!=null){	
			//return true; 
		
			//Create a set of indirect influencer
			Set<IVariableBinding> indirectInfluencerSet=new HashSet<IVariableBinding>();
			node.setProperty("indirectInfluencerSet", indirectInfluencerSet);
			
			//if(node.getParent() instanceof IfStatement){
			if(node.getParent().getProperty("isMultiBranchesRoot")=="YES"){
				directIfluencePropagators.clear();
				for(Map.Entry<IVariableBinding, Set<IVariableBinding>> entry:((Map<IVariableBinding,Set<IVariableBinding>>) node.getParent().getProperty("inDirectIfluencePropagators")).entrySet())
					directIfluencePropagators.put(entry.getKey(),new HashSet(entry.getValue()));
				//directIfluencePropagators.putAll((Map<IVariableBinding,Set<IVariableBinding>>) node.getParent().getProperty("inDirectIfluencePropagators"));
				//System.out.println("branchNumber : "+ node.getProperty("numberBranch")+ " "+ ((IfStatement) node.getParent()).getExpression() +"  In : "+ node.getParent().getProperty("inDirectIfluencePropagators"));
				
			}
			
			if(node.getParent().getProperty("isMultiBranchesRoot")=="YES")
				maintainBranchInfluenceVectors(node,indirectInfluencerSet);
			//System.out.println("branchNumber : "+ node.getProperty("numberBranch")+ "  "+ (Expression )node.getProperty("expression") +" is Influenced by "+ directInfluenceVector.get(node.getProperty("numberBranch")));
		}
		return true;
	}

	@Override
	public void endVisit(Block node){	
		//System.out.println(" New Block");
		if(node.getProperty("numberBranch")!=null){
			Set<IVariableBinding> indirectInfluencerSet=(Set<IVariableBinding>) node.getProperty("indirectInfluencerSet");
			indirectIfluencer.removeAll(indirectInfluencerSet);
			
			//if(node.getParent() instanceof IfStatement){
			if(node.getParent().getProperty("isMultiBranchesRoot")=="YES"){
				for(Map.Entry<IVariableBinding, Set<IVariableBinding>> entry: directIfluencePropagators.entrySet())
					((Map<IVariableBinding,Set<IVariableBinding>>) node.getParent().getProperty("outDirectIfluencePropagators")).get(entry.getKey()).addAll(entry.getValue());
				//System.out.println("branchNumber : "+ node.getProperty("numberBranch")+ " "+ ((IfStatement) node.getParent()).getExpression() +"  Out : "+ node.getParent().getProperty("outDirectIfluencePropagators"));
			}
				
		}
	}

	
	@Override
	public boolean visit(VariableDeclarationFragment node){
		//System.out.println(node+ "  Init = "+node.getInitializer());
		
		if(node.getInitializer() !=null)
			influencePorterChecker(node.getName(), node.getInitializer());
		//System.out.println(node);
		return true;
	}
	
	private void initializeMultiBranchesRoot(ASTNode node){
		//make a "In" copy of directIfluencePropagators to use it for each branch
		final Map<IVariableBinding, Set<IVariableBinding>> inDirectIfluencePropagators=new HashMap<IVariableBinding,Set<IVariableBinding>>();
		for(Map.Entry<IVariableBinding, Set<IVariableBinding>> entry:directIfluencePropagators.entrySet())
			inDirectIfluencePropagators.put(entry.getKey(),new HashSet(entry.getValue()));
		//inDirectIfluencePropagators.putAll(directIfluencePropagators);
		node.setProperty("inDirectIfluencePropagators", inDirectIfluencePropagators);
		//create a Out of directIfluencePropagators
		Map<IVariableBinding, Set<IVariableBinding>> outDirectIfluencePropagators=new HashMap<IVariableBinding,Set<IVariableBinding>>();
		for(Map.Entry<IVariableBinding, Set<IVariableBinding>> entry:directIfluencePropagators.entrySet())
			outDirectIfluencePropagators.put(entry.getKey(),new HashSet(entry.getValue()));
		//outDirectIfluencePropagators.putAll(directIfluencePropagators);
		node.setProperty("outDirectIfluencePropagators", outDirectIfluencePropagators);
	}
	
	private void endOfMultiBranchesRoot(ASTNode node){
		directIfluencePropagators.clear();
		directIfluencePropagators.putAll((Map<IVariableBinding,Set<IVariableBinding>>) node.getProperty("outDirectIfluencePropagators"));
	}
	@Override
	public boolean visit(IfStatement node){
		if(node.getProperty("isMultiBranchesRoot")!="YES")
			return true;
		initializeMultiBranchesRoot(node);
		
		return true;
	}
	
	@Override
	public void endVisit(IfStatement node){
		//To avoid if statements that are created at the instrumentation phase
		if(node.getProperty("isMultiBranchesRoot")!="YES") 
			return;
		endOfMultiBranchesRoot(node);
	}

	@Override
	public boolean visit(SwitchStatement node){
		initializeMultiBranchesRoot(node);	
		return true;
	}
	
	@Override
	public void endVisit(SwitchStatement node){	
		endOfMultiBranchesRoot(node);
	}

	@Override
	public boolean visit(ForStatement node){
		initializeMultiBranchesRoot(node);	
		return true;
	}
	
	@Override
	public void endVisit(ForStatement node){	
		endOfMultiBranchesRoot(node);
	}

	@Override
	public boolean visit(WhileStatement node){
		initializeMultiBranchesRoot(node);	
		return true;
	}
	
	@Override
	public void endVisit(WhileStatement node){	
		endOfMultiBranchesRoot(node);
	}

	@Override
	public boolean visit(DoStatement node){
		initializeMultiBranchesRoot(node);	
		return true;
	}
	
	@Override
	public void endVisit(DoStatement node){	
		endOfMultiBranchesRoot(node);
	}

	
//	public static void main(String[] args) throws IOException{
//		ASTBuilder ASTRoot=new ASTBuilder("//Applications//eclipse//test","triangle.java");
//		CompilationUnit node=ASTRoot.getASTRoot();	
//		//AST ast=node.getAST();
//		BranchesCoder bC=new BranchesCoder();
//		node.accept(bC);
//		Instrumentor iTest=new Instrumentor();
//		node.accept(iTest);
//		InfluenceAnalyser sA=new InfluenceAnalyser();
//		node.accept(sA);
//		
//		
//		 FileWriter fstream = new FileWriter("//Applications//eclipse//test//triangleInst.java");
//		 BufferedWriter out = new BufferedWriter(fstream);
//		 out.write(node.toString());
//		 //Close the output stream
//		 out.close();
//	}
	
}
