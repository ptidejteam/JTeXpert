package csbst.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
//import org.eclipse.jdt.core.dom.

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


public class MethodCallsAnalyser extends ASTVisitor{
	private TypeDeclaration classNode; //the class under test
	private Map<IMethodBinding,Set<Integer>>  methodCallers=new HashMap<IMethodBinding,Set<Integer>>(); //<invokedMethod,Set<branch>>	
	
	public Map<IMethodBinding,Set<Integer>> getMethodBranchCallersMap(){
		return methodCallers;
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
	
	public Set<Integer> getBranchesCall(IMethodBinding method){
		return methodCallers.get(method);
	}
	
	@Override
	public boolean visit(TypeDeclaration node){
		//don't explore sub classes
		if(node.isLocalTypeDeclaration()||node.isMemberTypeDeclaration())
			return true;
		
		classNode=node;
		return true;
	}
	
	@Override
	public boolean visit(MethodInvocation node){
		
		if (node.resolveMethodBinding()!=null){
			//String qName=node.resolveMethodBinding().getDeclaringClass().getTypeDeclaration().getName();
			IMethodBinding method=node.resolveMethodBinding().getMethodDeclaration();
			if(method.getName().toString().equals("setNext"))
				method=method;
			
			if(!Modifier.isPrivate(method.getModifiers()))
				return true;
			
			int branchCaller=getBranch(node);
			if(branchCaller>-1){
				//if(qName.equals(classNode.getName().toString())){
					if(!methodCallers.containsKey(method)){
						methodCallers.put(method,new HashSet<Integer>());				
					}
					methodCallers.get(method).add(branchCaller);
			}
				//}
		 }
		return true;
	}
}
