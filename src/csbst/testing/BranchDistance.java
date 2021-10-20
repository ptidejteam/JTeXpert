package csbst.testing;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.BooleanLiteral;

import csbst.analysis.String2Expression;
import java.util.Hashtable;
import java.util.Map;


public class BranchDistance {
	private static int K=10;
	
	private static int expressionEvaluator(Expression expression, Map<String, Object> varsValues) throws Exception{
		
		if(expression instanceof ParenthesizedExpression){
			return expressionEvaluator(((ParenthesizedExpression)expression).getExpression(),varsValues);
		}
		
		if(expression instanceof NullLiteral || expression instanceof NumberLiteral || expression instanceof SimpleName 
				|| expression instanceof QualifiedName || expression instanceof MethodInvocation){
			return (Integer) varsValues.get(expression.toString());
		}
		
		//PrefixExpression.Operator.
			
		if(!(expression instanceof InfixExpression))
			throw new Exception ("Exepression cannot be expressed as a constraint : "+ expression.toString());
				
		Expression leftEx;
		Expression rightEx;
		InfixExpression.Operator op;	
		leftEx=((InfixExpression)expression).getLeftOperand();
		rightEx=((InfixExpression)expression).getRightOperand();
		op=((InfixExpression)expression).getOperator();
		
		if(expression instanceof InfixExpression){
				if(op==InfixExpression.Operator.DIVIDE)
					return expressionEvaluator(leftEx,varsValues)/expressionEvaluator(rightEx,varsValues);
			
				if(op==InfixExpression.Operator.MINUS)
					return expressionEvaluator(leftEx,varsValues)-expressionEvaluator(rightEx,varsValues);
			
				if(op==InfixExpression.Operator.PLUS)
					return expressionEvaluator(leftEx,varsValues)+expressionEvaluator(rightEx,varsValues);
				
				if(op==InfixExpression.Operator.REMAINDER)
					return expressionEvaluator(leftEx,varsValues)%expressionEvaluator(rightEx,varsValues);
				
				if(op==InfixExpression.Operator.TIMES)
					return expressionEvaluator(leftEx,varsValues)*expressionEvaluator(rightEx,varsValues);
				
				if(op==InfixExpression.Operator.AND)
					return expressionEvaluator(leftEx,varsValues)&expressionEvaluator(rightEx,varsValues);
				
				if(op==InfixExpression.Operator.OR)
					return expressionEvaluator(leftEx,varsValues)|expressionEvaluator(rightEx,varsValues);
								
				if(op==InfixExpression.Operator.XOR)
					return expressionEvaluator(leftEx,varsValues)^expressionEvaluator(rightEx,varsValues);

				if(op==InfixExpression.Operator.LEFT_SHIFT)
					return expressionEvaluator(leftEx,varsValues)<<expressionEvaluator(rightEx,varsValues);
				
				if(op==InfixExpression.Operator.RIGHT_SHIFT_SIGNED)
					return expressionEvaluator(leftEx,varsValues)>>expressionEvaluator(rightEx,varsValues);
				
				if(op==InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED)
					return expressionEvaluator(leftEx,varsValues)>>>expressionEvaluator(rightEx,varsValues);
						
				else{
					throw new Exception ("Exepression Contains unsupported operator : "+ expression.toString());
				}
			
		}
		else{
			throw new Exception ("Unsported expression type : "+ expression.toString());
		}
			
	}

	public static int getBranchDistance(String expression) throws Exception{
		return getBranchDistance(String2Expression.getExpression(expression),false);
	}	
	
	public static int getBranchDistance(Expression expression) throws Exception{
		return getBranchDistance(expression,false);
	}
	
	public static int getBranchDistance(Expression expression, boolean negation) throws Exception{
		//boolean negation=false;
		if(expression instanceof ParenthesizedExpression){
			return getBranchDistance(((ParenthesizedExpression)expression).getExpression(),negation);
		}
		
		//PostfixExpression.Operator.
		if(expression instanceof PrefixExpression)
			if(((PrefixExpression)expression).getOperator()==PrefixExpression.Operator.NOT){
				return getBranchDistance(((PrefixExpression)expression).getOperand(),!(negation & true));
			}
		
		if(!(expression instanceof InfixExpression))
			throw new Exception ("Exepression cannot be expressed as a constraint : "+ expression.toString());
				
		Expression leftEx;
		Expression rightEx;
		InfixExpression.Operator op;	
		leftEx=((InfixExpression)expression).getLeftOperand();
		rightEx=((InfixExpression)expression).getRightOperand();
		op=((InfixExpression)expression).getOperator();

		if((op==InfixExpression.Operator.CONDITIONAL_AND && !negation)||(op==InfixExpression.Operator.CONDITIONAL_OR  && negation))
			return getBranchDistance(leftEx,negation)+getBranchDistance(rightEx,negation);
		
		if((op==InfixExpression.Operator.CONDITIONAL_OR  && !negation)||(op==InfixExpression.Operator.CONDITIONAL_AND && negation))
			return Math.min(getBranchDistance(leftEx,negation),getBranchDistance(rightEx,negation));	
		
		//
		int leftVal=Integer.parseInt(leftEx.toString().equals("null")? "0":leftEx.toString()); //expressionEvaluator(leftEx);
		int rigthVal=Integer.parseInt(rightEx.toString().equals("null")? "0":rightEx.toString()); //expressionEvaluator(rightEx);
		int distance=0;
		
		if((op==InfixExpression.Operator.EQUALS  && !negation)||(op==InfixExpression.Operator.NOT_EQUALS  && negation)){	
			if(leftVal==rigthVal)
				distance=0;
			else
				distance= Math.abs(leftVal-rigthVal)+K;
		}
		
		else if((op==InfixExpression.Operator.NOT_EQUALS  && !negation)||(op==InfixExpression.Operator.EQUALS  && negation)){ 		
			if(leftVal!=rigthVal)
				distance=0;
			else
				distance= K;
		}
		
		else if((op==InfixExpression.Operator.GREATER  && !negation)||(op==InfixExpression.Operator.LESS_EQUALS  && negation)){
			if(leftVal>rigthVal)
				distance=0;
			else
				distance= rigthVal-leftVal+K;
		}
		
		else if((op==InfixExpression.Operator.LESS_EQUALS  && !negation)||(op==InfixExpression.Operator.GREATER  && negation)){
			if(leftVal<=rigthVal)
				distance=0;
			else
				distance=leftVal-rigthVal+K;

		}
		
		else if((op==InfixExpression.Operator.GREATER_EQUALS  && !negation)||(op==InfixExpression.Operator.LESS  && negation)){		
			if(leftVal>=rigthVal)
				distance=0;
			else
				distance=rigthVal-leftVal+K;
		}
		
		else if((op==InfixExpression.Operator.LESS  && !negation)||(op==InfixExpression.Operator.GREATER_EQUALS  && negation)){
			if(leftVal<rigthVal)
				distance=0;
			else
				distance= leftVal-rigthVal+K;
		}
		
		else{
			throw new Exception (" Exepression contains unsupported operator : "+ expression.toString());
		}
		return distance;		
	}
	
	public static double normalize(int distance){
		return 1.0*distance/(distance+1);
	}
	
	public static String expressionFormater(Expression expression) throws Exception{
		
		if(expression instanceof ParenthesizedExpression){
			return "\"(\"+"+expressionFormater(((ParenthesizedExpression)expression).getExpression())+"+\")\"";
		}
		//PostfixExpression.Operator.
		if(expression instanceof PrefixExpression)
			if(((PrefixExpression)expression).getOperator()==PrefixExpression.Operator.NOT){
				return "\"!\"+"+expressionFormater(((PrefixExpression)expression).getOperand());
			}
		
		if(expression instanceof NullLiteral|| expression instanceof BooleanLiteral||  expression instanceof NumberLiteral||expression instanceof SimpleName || expression instanceof QualifiedName || expression instanceof MethodInvocation){
			return expression.toString();
		}
			
		if(!(expression instanceof InfixExpression))
			throw new Exception ("Exepression cannot be expressed as a constraint : "+ expression.toString());
				
		Expression leftEx;
		Expression rightEx;
		InfixExpression.Operator op;	
		leftEx=((InfixExpression)expression).getLeftOperand();
		rightEx=((InfixExpression)expression).getRightOperand();
		op=((InfixExpression)expression).getOperator();

		if((op==InfixExpression.Operator.CONDITIONAL_AND)||(op==InfixExpression.Operator.CONDITIONAL_OR)
				||(op==InfixExpression.Operator.EQUALS)||(op==InfixExpression.Operator.GREATER)
				||(op==InfixExpression.Operator.GREATER_EQUALS)||(op==InfixExpression.Operator.LESS)
				||(op==InfixExpression.Operator.LESS_EQUALS)||(op==InfixExpression.Operator.NOT_EQUALS))
			return expressionFormater(leftEx)+"+\""+op+"\"+"+expressionFormater(rightEx);

		return "("+expression.toString()+")";
			
	}
}
