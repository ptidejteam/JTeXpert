package csbst.analysis;


import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.ArrayList;

public class InfixExpressionDecomposer  extends ASTVisitor{
	//private InfixExpression rootExpression;
	private InfixExpression.Operator operator;
	private ArrayList<InfixExpression> atomicExpressions=new ArrayList<InfixExpression>();

	public InfixExpressionDecomposer(InfixExpression.Operator operator){
		//rootExpression=iE;
		this.operator=operator;
	}
	
	@Override
	public boolean visit(InfixExpression node){
		if(node.getOperator()!=operator){
			atomicExpressions.add(node);
			return false;
		}	
		return true;
	}
	
//	@Override
//	public boolean visit(TypeDeclaration node){
//		//don't explore sub classes
//		if(node.isLocalTypeDeclaration()||node.isMemberTypeDeclaration())
//			return false;
//		return true;
//	}
	
	public ArrayList<InfixExpression> getExpressionsList(){
		return atomicExpressions;
		
	}
}
