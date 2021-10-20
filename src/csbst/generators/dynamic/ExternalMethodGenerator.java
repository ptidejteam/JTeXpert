package csbst.generators.dynamic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import csbst.generators.AbsractGenerator;
import csbst.testing.JTE;

public class ExternalMethodGenerator extends MethodGenerator{	
	private InstanceGenerator constructor;
	public ExternalMethodGenerator(AbsractGenerator parent,Class clazz,Class stub, Vector<Method> possibleMethodes) {
		super(parent,clazz, stub,possibleMethodes);
	}

	public List<Statement>getStatements(AST ast, String varName, String pName){	
		//TODO add constructor generation and change the variable name
		return super.getStatements(ast, varName, pName);
	}
	
	@Override
	public Object clone() {
		ExternalMethodGenerator newMeth=new ExternalMethodGenerator(parent,clazz,stub, this.recommandedWays);
		
		newMeth.clazz=this.clazz;
		//newMeth.method=this.method;
		newMeth.variableBinding=this.variableBinding;
		newMeth.fitness=this.fitness;
		newMeth.currentWay=currentWay;
		newMeth.object=this.object;
		newMeth.seed=this.seed;
		newMeth.random=this.random;
		//newMeth.setExceptions(exceptions);
		newMeth.parameters=new Vector<AbsractGenerator>();
		if(parameters!=null){
			newMeth.parameters=new Vector<AbsractGenerator>();
			for(AbsractGenerator gene:parameters){
				if(gene!=null)
					newMeth.parameters.add((AbsractGenerator)gene.clone());
			}
		}
		newMeth.stub=this.stub;
		if(recommandedWays!=null)
			newMeth.recommandedWays=recommandedWays;
		
		newMeth.unexpectedException=unexpectedException;
		newMeth.exceptions=new HashSet();
		newMeth.exceptions.addAll(exceptions);
		newMeth.generationNbr=generationNbr;
		newMeth.parent=parent;
		newMeth.possibleWays=possibleWays;
		
		if(externalConstructor!=null)
			newMeth.externalConstructor=(InstanceGenerator)externalConstructor.clone();
		//=====
		return newMeth;
	}
	
	@Override
	public void execute(final Object obj,Class clsUT){		
		//clsUT=method.getDeclaringClass();
		constructor=new InstanceGenerator(null, currentWay.method.getDeclaringClass(),true);
		super.execute(constructor.getObject(),currentWay.method.getDeclaringClass());
	}

}
