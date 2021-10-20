package csbst.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.FieldAccess;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
 

public class DataMemberUseAnalyser extends ASTVisitor{
	private Map<IVariableBinding,Set<Integer>>  dataMemberTransformers=new HashMap<IVariableBinding,Set<Integer>>(); 	//<Data member index,ArrayList<branches>>
	private Map<IVariableBinding,Set<Integer>>  dataMemberOthers=new HashMap<IVariableBinding,Set<Integer>>(); 
	private Map<IVariableBinding,Set<Integer>>  dataMemberReporters=new HashMap<IVariableBinding,Set<Integer>>();
	
	public  Map<IVariableBinding,Set<Integer>> getDataMemberBranchTransformersMap(){
		return dataMemberTransformers;
	}

	public  Map<IVariableBinding,Set<Integer>> getDataMemberBranchOthersMap(){
		return dataMemberOthers;
	}
	
	public  Map<IVariableBinding,Set<Integer>> getDataMemberBranchReportersMap(){
		return dataMemberReporters;
	}
	
	private int getBranch(Assignment node){
		ASTNode branch;
		branch=node;
		while(branch.getProperty("numberBranch")==null){
			branch=branch.getParent();
			if(branch==null)
				return -1;
			//System.out.println("--------------------------------------------------");
			//System.out.println(branch);
		}
				
		return (Integer) branch.getProperty("numberBranch");
	}
	
	private int getBranch(SimpleName node){
		ASTNode branch;
		branch=node;
		while(branch.getProperty("numberBranch")==null){
			branch=branch.getParent();
			if(branch==null)
				return -1;
			//System.out.println("--------------------------------------------------");
			//System.out.println(branch);
		}
				
		return (Integer) branch.getProperty("numberBranch");
	}
	
	private int getBranch(MethodInvocation node){
		ASTNode branch;
		branch=node;
		while(branch.getProperty("numberBranch")==null){
			//System.out.println(branch..getNodeType());
			branch=branch.getParent();
			if(branch instanceof TypeDeclaration)
				return -1;
			
		}
				
		return (Integer) branch.getProperty("numberBranch");
	}

	@Override
	public boolean visit(SimpleName node){
		
		if(!(node.resolveBinding() instanceof IVariableBinding))
			return true;	
		
		//assignment statement
		if ((node.getParent() instanceof Assignment)&& node==((Assignment)node.getParent()).getLeftHandSide())
			return true;
		
		final SimpleName sN;
		//if (node.getLeftHandSide() instanceof SimpleName)
		
		IVariableBinding iVB=((IVariableBinding) node.resolveBinding());			
		if(iVB!=null && iVB.isField()){
			//System.out.println(" data member : "+node );
			iVB=iVB.getVariableDeclaration();
			Set<Integer> tmpSet=dataMemberOthers.get(iVB);
			if (tmpSet!=null){
				final int branch=getBranch(node);
				if(branch!=-1){
					tmpSet.add(branch);
					if(isInReturnStatement(node)){
						Set<Integer> tmpSet1=dataMemberReporters.get(iVB);
						tmpSet1.add(branch);
					}
				}
				
			}
    	}		
		return true;
	}
	
	private boolean isInReturnStatement(ASTNode node){
		ASTNode statement=node;
		while(statement!=null ){ //&& !(statement instanceof ReturnStatement)
			if(statement instanceof ReturnStatement)
				return true;
			statement=statement.getParent();
		}	
		return false;
	}

	@Override
	public boolean visit(FieldAccess node){
		
		//System.out.println(" data member : "+node );
		final SimpleName sN=node.getName();	
		if(!(sN.resolveBinding() instanceof IVariableBinding))
			return true;
		
		IVariableBinding iVB=((IVariableBinding) sN.resolveBinding());			
		if(iVB!=null && iVB.isField()){
			iVB=iVB.getVariableDeclaration();
			Set<Integer> tmpSet=dataMemberOthers.get(iVB);
			if (tmpSet!=null){
				final int branch=getBranch(sN);
				if(branch!=-1){
					tmpSet.add(branch);
				
					if(node.getParent() instanceof ReturnStatement)
					{
						Set<Integer> tmpSet1=dataMemberReporters.get(iVB);
						tmpSet1.add(branch);
					}
				}
			}
			

				
    	}		
		return true;
	}
	
	@Override
	public boolean visit(Assignment node){
		//addSnapshot(node);
		
		//System.out.println(" left Type : "+node.getLeftHandSide().getClass());
		if (! (node.getLeftHandSide() instanceof SimpleName || node.getLeftHandSide() instanceof FieldAccess))
			return true;
		
		final SimpleName sN;
		if (node.getLeftHandSide() instanceof SimpleName)
			sN=(SimpleName)node.getLeftHandSide();	
		else 
			sN=((FieldAccess)node.getLeftHandSide()).getName();	
		
		if(!(sN.resolveBinding() instanceof IVariableBinding))
			return true;
		
		IVariableBinding iVB=((IVariableBinding) sN.resolveBinding());			
		if(iVB!=null && iVB.isField()){
			iVB=iVB.getVariableDeclaration();
			Set<Integer> tmpSet=dataMemberTransformers.get(iVB);
			if (tmpSet!=null){
				final int branch=getBranch(node);
				if(branch!=-1)
					tmpSet.add(branch);
				//System.out.println(" data member : "+sN +" transformer branch " + branch);
			}
    	}
        return true;
	}

	@Override
	public boolean visit(MethodInvocation node){
		
		if (! (node.getExpression()instanceof SimpleName || node.getExpression() instanceof FieldAccess))
			return true;
		
		final SimpleName sN;
		if (node.getExpression() instanceof SimpleName)
			sN=(SimpleName)node.getExpression();	
		else 
			sN=((FieldAccess)node.getExpression()).getName();	
		
		if(!(sN.resolveBinding() instanceof IVariableBinding))
			return true;
		
		IVariableBinding iVB=(IVariableBinding) sN.resolveBinding();		
		if(iVB!=null && iVB.isField()){
			iVB=iVB.getVariableDeclaration();
			//System.out.println(iVB); //(IVariableBinding)((VariableDeclaration) node.getFields()[i].fragments().get(j)).resolveBinding()
			//System.out.println(dataMemberTransformers);
			Set<Integer> tmpSet=dataMemberTransformers.get(iVB.getVariableDeclaration());
			if (tmpSet!=null){
				final int branch=getBranch(node);
				if(branch!=-1)
					tmpSet.add(branch);
				
			}
				
    	}
		return true;
	}
	
	@Override
	public boolean visit(TypeDeclaration node){
		//don't explore sub classes
		//if(node.isLocalTypeDeclaration()||node.isMemberTypeDeclaration())
		//	return false;

		 for(int i=node.getFields().length-1;i>=0;i--){
			 for(int j=node.getFields()[i].fragments().size()-1;j>=0;j--){
				 //System.out.println(" data member : "+ ((VariableDeclaration) node.getFields()[i].fragments().get(j)).resolveBinding().toString());
				 //System.out.println(" data member : "+ Modifier.isFinal(((VariableDeclaration) node.getFields()[i].fragments().get(j)).resolveBinding().getModifiers()));
				 if(((VariableDeclaration) node.getFields()[i].fragments().get(j)).resolveBinding()!=null
						 && !(
								 Modifier.isFinal(((VariableDeclaration) node.getFields()[i].fragments().get(j)).resolveBinding().getModifiers())
								 && (((VariableDeclaration) node.getFields()[i].fragments().get(j)).resolveBinding().getType().isPrimitive()
										 || ((VariableDeclaration) node.getFields()[i].fragments().get(j)).resolveBinding().getType().getName().equals("String")
										 )
										 
							)
						 ){
					 Set<Integer> tmpSet=new HashSet<Integer>();
					 dataMemberTransformers.put((IVariableBinding)((VariableDeclaration) node.getFields()[i].fragments().get(j)).resolveBinding(),tmpSet);
					 Set<Integer> tmpSet1=new HashSet<Integer>();
					 dataMemberOthers.put((IVariableBinding)((VariableDeclaration) node.getFields()[i].fragments().get(j)).resolveBinding(),tmpSet1);
					 Set<Integer> tmpSet2=new HashSet<Integer>();
					 dataMemberReporters.put((IVariableBinding)((VariableDeclaration) node.getFields()[i].fragments().get(j)).resolveBinding(),tmpSet2);
				 }
			 }
		 }
	
		return true;
	}
}
