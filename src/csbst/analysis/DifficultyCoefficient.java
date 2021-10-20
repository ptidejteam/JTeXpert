package csbst.analysis;

import static choco.Choco.*;


import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

import choco.Choco;
import choco.Options;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.HashSet;
import java.util.Set;

public class DifficultyCoefficient {
	private final static int MaxInt=1000;//Choco.MAX_UPPER_BOUND;
	private final static int MinInt=-1000;//Choco.MIN_LOWER_BOUND;
	private final static double B=100;
	
	
	public static double alphaPlus(Expression expression) throws Exception{
		short nbrVars=getNumberOfVariables(expression);
		return Math.pow(2, 32)/nbrVars;
	}
	
	public static double getDifficultyCoefficient(Expression expression) throws Exception{
		return 1.0;
		//return DifficultyCoefficient.alpha(expression)*B*B+DifficultyCoefficient.beta(expression)*B+1;
	}

	
	public static double getDifficultyLevelPlus(Expression expression) throws Exception{
		//short nbrVars=getNumberOfVariables(expression);
		//double val=Math.pow(2, 32)/nbrVars;//2^7-1
		//val<<=24;
		//System.out.println("nmbr variables "+val); (2^24)*(1/ARITY)*ALPHA + (2^16)*BETA
		//System.out.println("betaPlus "+betaPlus(expression));
		return alphaPlus(expression)+betaPlus(expression);
	}
	
	public static IntegerExpressionVariable ExpressionToChocoExpression(Expression expression) throws Exception{
		return ExpressionToChocoExpression(expression,1);
	}
	
	public static IntegerExpressionVariable ExpressionToChocoExpression(Expression expression,int signe) throws Exception{
		//+ - * / %
		if(expression instanceof ParenthesizedExpression){
			return ExpressionToChocoExpression(((ParenthesizedExpression)expression).getExpression());
		}
		
		if(expression instanceof PrefixExpression)
			if(((PrefixExpression)expression).getOperator()==PrefixExpression.Operator.MINUS)
				return ExpressionToChocoExpression(((PrefixExpression)expression).getOperand(),-1);
		
		if(expression instanceof NumberLiteral){
			//IntegerVariable op1;
			//System.out.println(expression);
			return Choco.constant(signe*Integer.parseInt(((NumberLiteral)expression).toString()));
		}

		if(expression instanceof NullLiteral){
			//IntegerVariable op1;
			//System.out.println(expression);
			return Choco.constant(0);
		}
		
		if(expression instanceof SimpleName){
			IntegerVariable op1;
			op1=makeIntVar(expression.toString(),MinInt,MaxInt,Options.V_BOUND);
			return op1;
		}
		
		if(expression instanceof QualifiedName){
			//IntegerVariable op1;
			//String name=expression.toString().replace(".", "_");
			
			IVariableBinding variableBinding = null;
			variableBinding = (IVariableBinding)((QualifiedName)expression).resolveBinding();
			if(variableBinding ==null)
				throw new Exception ("A constant cannot be binded to its value : "+expression.toString()); //System.out.println("");
			//if(variableBinding.getType())
			int value;
			//System.out.println(expression.toString()+"    "+ variableBinding.getType().getName());
			if(!(variableBinding.getType().getName().equals("int") )){
				long val1=(Long) variableBinding.getConstantValue();
				if(val1>MaxInt) 
					value=MaxInt;
				else if(val1<MinInt) 
					value=MinInt;
				else
					value=Integer.parseInt(variableBinding.getConstantValue().toString());
			}
			else
				value=Integer.parseInt(variableBinding.getConstantValue().toString());
			
			if(value>MaxInt) value=MaxInt;
			if(value<MinInt) value=MinInt;
			return Choco.constant(value);

		}
		
		if(expression instanceof MethodInvocation){
			IntegerVariable op1;
			op1=makeIntVar("Relaxation",MinInt,MaxInt,Options.V_BOUND);
			return op1;
		}
		
		if(expression instanceof InfixExpression){
			//Choco.an
			//if(((InfixExpression)expression).getLeftOperand() instanceof NumberLiteral){
				if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.DIVIDE)
					return Choco.div(ExpressionToChocoExpression(((InfixExpression)expression).getLeftOperand()), ExpressionToChocoExpression(((InfixExpression)expression).getRightOperand()));
			
				if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.MINUS)
					return Choco.minus(ExpressionToChocoExpression(((InfixExpression)expression).getLeftOperand()), ExpressionToChocoExpression(((InfixExpression)expression).getRightOperand()));
			
				if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.PLUS)
					return Choco.plus(ExpressionToChocoExpression(((InfixExpression)expression).getLeftOperand()), ExpressionToChocoExpression(((InfixExpression)expression).getRightOperand()));
			
				if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.REMAINDER)
					return Choco.mod(ExpressionToChocoExpression(((InfixExpression)expression).getLeftOperand()), ExpressionToChocoExpression(((InfixExpression)expression).getRightOperand()));

				if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.TIMES)
					return Choco.mult(ExpressionToChocoExpression(((InfixExpression)expression).getLeftOperand()), ExpressionToChocoExpression(((InfixExpression)expression).getRightOperand()));
//				if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.AND)
//					return Choco.((IntegerVariable)ExpressionToChocoExpression(((InfixExpression)expression).getLeftOperand()), (IntegerVariable)ExpressionToChocoExpression(((InfixExpression)expression).getRightOperand()));
				else{
					IntegerVariable op1;
					op1=makeIntVar("Relaxation",MinInt,MaxInt,Options.V_BOUND);
					return op1;
					//throw new Exception ("Exepression Contains unsupported operator : "+ expression.toString());
				}
			
		}
		else{
			throw new Exception ("Unsported expression type : "+ expression.toString());
		}
			
	}
	public static int getSearchSpaceSize(Expression expression) throws Exception{
		//Set<String>variablesSet=new HashSet<String>();
		return  (int) Math.pow(MaxInt-MinInt+1,getNumberOfVariables(expression));//getSearchSpaceSize(expression, variablesSet);
	}
	
	public static short getNumberOfVariables(Expression expression) throws Exception{
		Set<String>variablesSet=new HashSet<String>();
		return getNumberOfVariables(expression, (short) 0,variablesSet);
	}
	
	public static short getNumberOfVariables(Expression expression, short currentVariablesNumber, Set<String> variablesSet) throws Exception{
		//+ - * / %
		//System.out.println("Expression ="+"    "+ expression) ;
		if(expression instanceof ParenthesizedExpression){
			return getNumberOfVariables(((ParenthesizedExpression)expression).getExpression(),currentVariablesNumber,variablesSet);
		}
		
		if(expression instanceof PrefixExpression)
				return getNumberOfVariables(((PrefixExpression)expression).getOperand(),currentVariablesNumber,variablesSet);
		
		if(expression instanceof InfixExpression){
				return (short) (currentVariablesNumber
						+getNumberOfVariables(((InfixExpression)expression).getLeftOperand(),(short) 0,variablesSet)
						+getNumberOfVariables(((InfixExpression)expression).getRightOperand(),(short) 0,variablesSet));
		}
	
		if((expression instanceof NumberLiteral)||(expression instanceof QualifiedName)||(expression instanceof MethodInvocation)){
			return currentVariablesNumber;
		}
		
		if(expression instanceof SimpleName){
			
			if(variablesSet.contains(((SimpleName)expression).toString()))
				return currentVariablesNumber;
			else{
				
				variablesSet.add(((SimpleName)expression).toString());
				return (short) (currentVariablesNumber+1);
				
			}
		}

		else{
			throw new Exception ("Unsported expression type : "+ expression.toString());
		}
			
	}
	
	public static double getNumberOfSolutions(Expression expression) throws Exception{
		return getNumberOfSolutions(expression, false);
	}
	
	public static double getNumberOfSolutions(Expression expression, boolean negation) throws Exception{
		double numberOfSolutions=0;
		//ex1 And ex2 , ex1 Or ex2 //utiliser le domain d'une expression definir un X et Y. pour And utilise le max. pour Or utiliser le min
		if(expression instanceof ParenthesizedExpression){
			return getNumberOfSolutions(((ParenthesizedExpression)expression).getExpression(),negation);
		}

		//PostfixExpression.Operator.
		if(expression instanceof PrefixExpression)
			if(((PrefixExpression)expression).getOperator()==PrefixExpression.Operator.NOT){
				return getNumberOfSolutions(((PrefixExpression)expression).getOperand(),!(negation & true));
			}
		
		if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.CONDITIONAL_AND)
			return Math.min(getNumberOfSolutions(((InfixExpression)expression).getLeftOperand(),negation), getNumberOfSolutions(((InfixExpression)expression).getRightOperand(),negation));
			
		if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.CONDITIONAL_OR)
			return Math.max(getNumberOfSolutions(((InfixExpression)expression).getLeftOperand(),negation), getNumberOfSolutions(((InfixExpression)expression).getRightOperand(),negation));
		
		//ex1 == ex2, ex1 != ex1, ex1 < ex2 , <=, >, >= ; X==ex1 and Y==ex2 and X==Y
		//System.out.println("valeur calculŽ");
		
		Model csp=new CPModel();
		IntegerVariable X;
		X=makeIntVar("Xexpression",MinInt,MaxInt,Options.V_BOUND);
		IntegerVariable Y;
		Y=makeIntVar("Yexpression",MinInt,MaxInt,Options.V_BOUND);
		
		csp.addConstraint(Choco.eq(X, ExpressionToChocoExpression(((InfixExpression)expression).getLeftOperand())));
		csp.addConstraint(Choco.eq(Y, ExpressionToChocoExpression(((InfixExpression)expression).getRightOperand())));	
		
		InfixExpression.Operator op=((InfixExpression)expression).getOperator();
		
		if(op==InfixExpression.Operator.EQUALS){
			csp.addConstraint(Choco.eq(X, Y));
		}
		
		else if(op==InfixExpression.Operator.GREATER){
			csp.addConstraint(Choco.gt(X, Y));
		}
		
		else if(op==InfixExpression.Operator.GREATER_EQUALS){
			csp.addConstraint(Choco.geq(X, Y));
		}
		
		else if(op==InfixExpression.Operator.LESS){
			csp.addConstraint(Choco.lt(X, Y));
		}
		
		else if(op==InfixExpression.Operator.LESS_EQUALS){
			csp.addConstraint(Choco.leq(X, Y));
		}
		
		else if(op==InfixExpression.Operator.NOT_EQUALS){
			csp.addConstraint(Choco.neq(X, Y));
		}
		else
			throw new Exception ("Exepression Contains unsupported comparaison operator : "+ expression.toString());

		//*****************
	    Solver solver = new CPSolver();
	    solver.setTimeLimit(200);
		solver.read(csp);		    
		solver.propagate();
		int xSup=solver.getVar(X).getSup();
		int xInf=solver.getVar(X).getInf();
		int ySup=solver.getVar(Y).getSup();
		int yInf=solver.getVar(Y).getInf();
		int dx=xSup-xInf+1; 
		int dy=ySup-yInf+1;
		
		if(op==InfixExpression.Operator.EQUALS){		
			numberOfSolutions= dx;
		}
		
		else if(op==InfixExpression.Operator.GREATER){	
			numberOfSolutions= (xSup*(ySup+1)-yInf*dx-
					1.0/2*(Math.pow(ySup,2)+ySup+
							Math.pow(xInf,2)-xInf));
		}
		
		else if(op==InfixExpression.Operator.GREATER_EQUALS){		
			int dxINdy=0;
			if (ySup>=xInf && xSup>=yInf)
				dxINdy=Math.min(ySup, xSup)-
						Math.max(yInf, xInf)+1;
			
			numberOfSolutions= (xSup*(ySup+1)-yInf*dx-
					1.0/2*(Math.pow(ySup,2)+ySup+
							Math.pow(xInf,2)-xInf)+dxINdy);
		}
		
		else if(op==InfixExpression.Operator.LESS){
			numberOfSolutions= (ySup*(xSup+1)-xInf*dy-
					1.0/2*(Math.pow(xSup,2)+xSup+
							Math.pow(yInf,2)-yInf));
		}
		
		else if(op==InfixExpression.Operator.LESS_EQUALS){
			int dxINdy=0;
			if (xSup>=yInf && ySup>=xInf)
				dxINdy=Math.min(xSup, ySup)-
						Math.max(xInf, yInf)+1;
			
			//System.out.println(csp.constraintsToString());
			
			numberOfSolutions= ySup*(xSup+1)-xInf*dy-(1.0/2)*(Math.pow(xSup,2)+xSup+Math.pow(yInf,2)-yInf)+dxINdy;
		}
		
		else if(op==InfixExpression.Operator.NOT_EQUALS){ 		
			int dxINdy=0;		
			if (xSup>=yInf && ySup>=xInf)
				dxINdy=Math.min(xSup, ySup)-
						Math.max(xInf, yInf);
					
			numberOfSolutions= dx*dy-dxINdy;
		}
		
		return numberOfSolutions;

	}	
	
	public static double beta(Expression expression) throws Exception{
		return beta(expression, false);
	}
	
	public static double beta(Expression expression, boolean negation) throws Exception{
		double density=1.0;
		//ex1 And ex2 , ex1 Or ex2 //utiliser le domain d'une expression definir un X et Y. pour And utilise le max pour Or utiliser le min
		if(expression instanceof ParenthesizedExpression){
			return beta(((ParenthesizedExpression)expression).getExpression(),negation);
		}

		//PostfixExpression.Operator.
		if(expression instanceof PrefixExpression)
			if(((PrefixExpression)expression).getOperator()==PrefixExpression.Operator.NOT){
				return beta(((PrefixExpression)expression).getOperand(),!(negation & true));
			}
		
		if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.CONDITIONAL_AND)
			return Math.max(beta(((InfixExpression)expression).getLeftOperand(),negation), beta(((InfixExpression)expression).getRightOperand(),negation));
			
		if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.CONDITIONAL_OR)
			return Math.min(beta(((InfixExpression)expression).getLeftOperand(),negation), beta(((InfixExpression)expression).getRightOperand(),negation));
		
		//ex1 == ex2, ex1 != ex1, ex1 < ex2 , <=, >, >= ; X==ex1 and Y==ex2 and X==Y
		//System.out.println("valeur calculŽ");
		
		Model csp=new CPModel();
		IntegerVariable X;
		X=makeIntVar("Xexpression",MinInt,MaxInt,Options.V_BOUND);
		IntegerVariable Y;
		Y=makeIntVar("Yexpression",MinInt,MaxInt,Options.V_BOUND);
		
		csp.addConstraint(Choco.eq(X, ExpressionToChocoExpression(((InfixExpression)expression).getLeftOperand())));
		csp.addConstraint(Choco.eq(Y, ExpressionToChocoExpression(((InfixExpression)expression).getRightOperand())));	
		
		InfixExpression.Operator op=((InfixExpression)expression).getOperator();
		
		if(op==InfixExpression.Operator.EQUALS){
			csp.addConstraint(Choco.eq(X, Y));
		}
		
		else if(op==InfixExpression.Operator.GREATER){
			csp.addConstraint(Choco.gt(X, Y));
		}
		
		else if(op==InfixExpression.Operator.GREATER_EQUALS){
			csp.addConstraint(Choco.geq(X, Y));
		}
		
		else if(op==InfixExpression.Operator.LESS){
			csp.addConstraint(Choco.lt(X, Y));
		}
		
		else if(op==InfixExpression.Operator.LESS_EQUALS){
			csp.addConstraint(Choco.leq(X, Y));
		}
		
		else if(op==InfixExpression.Operator.NOT_EQUALS){
			csp.addConstraint(Choco.neq(X, Y));
		}
		else
			throw new Exception ("Exepression Contains unsupported comparaison operator : "+ expression.toString());

		//*****************
	    Solver solver = new CPSolver();
	    solver.setTimeLimit(200);
		solver.read(csp);		    
		solver.propagate();
		int xSup=solver.getVar(X).getSup();
		int xInf=solver.getVar(X).getInf();
		int ySup=solver.getVar(Y).getSup();
		int yInf=solver.getVar(Y).getInf();
		int dx=xSup-xInf+1; 
		int dy=ySup-yInf+1;
		
		if(op==InfixExpression.Operator.EQUALS){		
			density= 1.0/dx;
		}
		
		else if(op==InfixExpression.Operator.GREATER){	
			density= (xSup*(ySup+1)-yInf*dx-
					1.0/2*(Math.pow(ySup,2)+ySup+
							Math.pow(xInf,2)-xInf))/(dy*dx);
		}
		
		else if(op==InfixExpression.Operator.GREATER_EQUALS){		
			int dxINdy=0;
			if (ySup>=xInf && xSup>=yInf)
				dxINdy=Math.min(ySup, xSup)-
						Math.max(yInf, xInf)+1;
			
			density= (xSup*(ySup+1)-yInf*dx-
					1.0/2*(Math.pow(ySup,2)+ySup+
							Math.pow(xInf,2)-xInf)+dxINdy)/(dy*dx);
		}
		
		else if(op==InfixExpression.Operator.LESS){
			density= (ySup*(xSup+1)-xInf*dy-
					1.0/2*(Math.pow(xSup,2)+xSup+
							Math.pow(yInf,2)-yInf))/(dx*dy);
		}
		
		else if(op==InfixExpression.Operator.LESS_EQUALS){
			int dxINdy=0;
			if (xSup>=yInf && ySup>=xInf)
				dxINdy=Math.min(xSup, ySup)-
						Math.max(xInf, yInf)+1;
			
			//System.out.println(csp.constraintsToString());
			
			density= (ySup*(xSup+1)-xInf*dy-(1.0/2)*(Math.pow(xSup,2)+xSup+Math.pow(yInf,2)-yInf)+dxINdy)/(dx*dy);
		}
		
		else if(op==InfixExpression.Operator.NOT_EQUALS){ 		
			int dxINdy=0;		
			if (xSup>=yInf && ySup>=xInf)
				dxINdy=Math.min(xSup, ySup)-
						Math.max(xInf, yInf);
					
			density= 1.0*(dx*dy-dxINdy)/(dx*dy);
		}
		
		return 1-density;

	}
	
	public static double alpha(Expression expression) throws Exception{	
		
		if(expression instanceof ParenthesizedExpression){
			return alpha(((ParenthesizedExpression)expression).getExpression());
		}
				
		Model csp=new CPModel();	
		//System.out.println(expression);
		csp.addConstraint(ExpressionToConstraint(expression));//(InfixExpression)
		
		
		Solver solver = new CPSolver();
	    solver.setTimeLimit(200);
		solver.read(csp);	
		double dBefore=1.0;
		for(int v=0;v<solver.getNbIntVars();v++){
			dBefore*=(solver.getIntVar(v).getSup()-solver.getIntVar(v).getInf()+1);
		}
		
		solver.propagate();
		double dAfter=1.0;
		for(int v=0;v<solver.getNbIntVars();v++){
			dAfter*=(solver.getIntVar(v).getSup()-solver.getIntVar(v).getInf()+1);
			//System.out.println(expression + "  "+ csp.constraintsToString()+"["+dBefore+","+dAfter+"]"+solver.getNbIntVars()+"["+solver.getIntVar(v).getSup()+","+solver.getIntVar(v).getInf()+"]");
		}		
		//System.out.println(expression);
        return 1-dAfter/dBefore; 		
	}
	
	public static Constraint ExpressionToConstraint(Expression expression) throws Exception{
		return ExpressionToConstraint(expression,false);
	}
	
	public static Constraint ExpressionToConstraint(Expression expression, boolean negation) throws Exception{
		
		if(expression instanceof ParenthesizedExpression){
			return ExpressionToConstraint(((ParenthesizedExpression)expression).getExpression(),negation);
		}
		
		//PostfixExpression.Operator.
		if(expression instanceof PrefixExpression)
			if(((PrefixExpression)expression).getOperator()==PrefixExpression.Operator.NOT){
				return ExpressionToConstraint(((PrefixExpression)expression).getOperand(),!(negation)); // & true
			}
		
		if(!(expression instanceof InfixExpression))
			throw new Exception ("Exepression cannot be expressed as a constraint : "+ expression.toString());
				
		Expression leftEx;
		Expression rightEx;
		InfixExpression.Operator op;	
		leftEx=((InfixExpression)expression).getLeftOperand();
		rightEx=((InfixExpression)expression).getRightOperand();
		op=((InfixExpression)expression).getOperator();
		
		if(!(leftEx instanceof NumberLiteral) && !(rightEx instanceof NumberLiteral)){
			if(op==InfixExpression.Operator.EQUALS)
				if(!negation)
					return Choco.eq(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));
				else
					return Choco.neq(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));
			
			if(op==InfixExpression.Operator.GREATER)
				if(!negation)
					return Choco.gt(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));
				else
					return Choco.leq(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));
			
			if(op==InfixExpression.Operator.GREATER_EQUALS)
				if(!negation)
					return Choco.geq(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));
				else
					return Choco.lt(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));

			
			if(op==InfixExpression.Operator.LESS)
				if(!negation)
					return Choco.lt(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));
				else 
					return Choco.geq(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));
			
			if(op==InfixExpression.Operator.LESS_EQUALS)
				if(!negation)
					return Choco.leq(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));
				else
					return Choco.gt(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));
			
			if(op==InfixExpression.Operator.NOT_EQUALS)
				if(!negation)
					return Choco.neq(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));
				else
					return Choco.eq(ExpressionToChocoExpression(leftEx), ExpressionToChocoExpression(rightEx));
			
			if(op==InfixExpression.Operator.CONDITIONAL_AND)
				if(!negation)
					return Choco.and(ExpressionToConstraint(leftEx,negation), ExpressionToConstraint(rightEx,negation));
				else
					return Choco.or(ExpressionToConstraint(leftEx,negation), ExpressionToConstraint(rightEx,negation));
			
			if(op==InfixExpression.Operator.CONDITIONAL_OR)
				if(!negation)
					return Choco.or(ExpressionToConstraint(leftEx,negation), ExpressionToConstraint(rightEx,negation));
				else
					return Choco.and(ExpressionToConstraint(leftEx,negation), ExpressionToConstraint(rightEx,negation));
			//else
			throw new Exception ("Exepression Contains unsupported comparaison operator : "+ expression.toString());
		}
		
		if(leftEx instanceof NumberLiteral){
				if(op==InfixExpression.Operator.EQUALS)
					if(!negation)
						return Choco.eq(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
					else
						return Choco.neq(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
				
				if(op==InfixExpression.Operator.GREATER)
					if(!negation)
						return Choco.gt(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
					else
						return Choco.leq(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
				
				if(op==InfixExpression.Operator.GREATER_EQUALS)
					if(!negation)
						return Choco.geq(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
					else
						return Choco.lt(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
				
				if(op==InfixExpression.Operator.LESS)
					if(!negation)
						return Choco.lt(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
					else
						return Choco.geq(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
				
				if(op==InfixExpression.Operator.LESS_EQUALS)
					if(!negation)
						return Choco.leq(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
					else
						return Choco.gt(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
				
				if(op==InfixExpression.Operator.NOT_EQUALS)
					if(!negation)
						return Choco.neq(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
					else
						return Choco.eq(Integer.parseInt(leftEx.toString()), ExpressionToChocoExpression(rightEx));
				//else
					
				throw new Exception ("Exepression Contains unsupported comparaison operator : "+ expression.toString());
		}
		
		if(rightEx instanceof NumberLiteral){
			if(op==InfixExpression.Operator.EQUALS)
				if(!negation)
					return Choco.eq(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
				else
					return Choco.neq(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
			
			if(op==InfixExpression.Operator.GREATER)
				if(!negation)
					return Choco.gt(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
				else
					return Choco.leq(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
			
			if(op==InfixExpression.Operator.GREATER_EQUALS)
				if(!negation)
					return Choco.geq(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
				else
					return Choco.lt(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
					
			if(op==InfixExpression.Operator.LESS)
				if(!negation)
					return Choco.lt(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
				else
					return Choco.geq(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
			
			if(op==InfixExpression.Operator.LESS_EQUALS)
				if(!negation)
					return Choco.leq(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
				else
					return Choco.gt(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
			
			if(op==InfixExpression.Operator.NOT_EQUALS)
				if(!negation)
					return Choco.neq(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
				else
					return Choco.eq(ExpressionToChocoExpression(leftEx), Integer.parseInt(rightEx.toString()));
			//else
			
			throw new Exception ("Exepression Contains unsupported comparaison operator : "+ expression.toString());
		}
		
		return null;
	}

	
	public static double betaPlus(Expression expression) throws Exception{
		return betaPlus(expression, false);
	}
	public static double betaPlus(Expression expression, boolean negation) throws Exception{
		double density=1.0;
		//ex1 And ex2 , ex1 Or ex2 //utiliser le domain d'une expression definir un X et Y. pour And utilise le max pour Or utiliser le min
		if(expression instanceof ParenthesizedExpression){
			return betaPlus(((ParenthesizedExpression)expression).getExpression(),negation);
		}

		//PostfixExpression.Operator.
		if(expression instanceof PrefixExpression)
			if(((PrefixExpression)expression).getOperator()==PrefixExpression.Operator.NOT){
				return betaPlus(((PrefixExpression)expression).getOperand(),!(negation & true));
			}
		
		if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.CONDITIONAL_AND)
			return (short) Math.max(betaPlus(((InfixExpression)expression).getLeftOperand(),negation), betaPlus(((InfixExpression)expression).getRightOperand(),negation));
			
		if(((InfixExpression)expression).getOperator()==InfixExpression.Operator.CONDITIONAL_OR)
			return (short) Math.min(betaPlus(((InfixExpression)expression).getLeftOperand(),negation), betaPlus(((InfixExpression)expression).getRightOperand(),negation));
		
		density=1.0*getNumberOfSolutions(expression)/(getSearchSpaceSize(expression));	
		double beta=Math.pow(2,16)*(1-density); //2^15-1=32767
		
		return beta;

	}
	
	public static void main(String[] args) throws Exception {
		Expression expression=String2Expression.getExpression("X==Y");
		System.out.println("alpha="+"    "+ alpha(expression));
		System.out.println("beta="+"    "+ beta(expression));
		System.out.println("betaPlus="+"    "+ betaPlus(expression));
		System.out.println("getSearchSpaceSize="+"    "+ getNumberOfVariables(expression));
		System.out.println("getDifficultyCoefficientPlus="+"    "+ getDifficultyLevelPlus(expression));
		//System.out.println("getSearchSpaceSize="+"    "+ getSearchSpaceSize(expression));
		
		
	}
}
