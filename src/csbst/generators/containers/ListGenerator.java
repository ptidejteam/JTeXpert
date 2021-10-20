package csbst.generators.containers;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import csbst.generators.AbsractGenerator;
import csbst.generators.dynamic.InstanceGenerator;
import csbst.testing.JTE;
import csbst.utils.FileEditor;

public class ListGenerator  extends ArrayGenerator{	
	public ListGenerator(AbsractGenerator parent,Class type) {
		super(parent,0,type); 
	}

	@Override
	public List<Statement> getStatements(AST ast, String varName, String pName) {
		List<Statement>returnList=new ArrayList<Statement>();
		String newVarName=varName+pName+"P1";
		returnList.addAll(super.getStatements(ast, newVarName, ""));
		//returnList.addAll(elements[i].getStatements(ast,newVarName,""));

		VariableDeclarationFragment myVar=ast.newVariableDeclarationFragment();
		myVar.setName(ast.newSimpleName(varName));

		//new ArrayList<Integer>(Arrays.asList(new 
		ClassInstanceCreation classInstanceArrayList= ast.newClassInstanceCreation();
	    classInstanceArrayList.setType(ast.newSimpleType(ast.newSimpleName("ArrayList")));
	     
       
	    MethodInvocation invokeAsList=ast.newMethodInvocation();
	    invokeAsList.setExpression(ast.newSimpleName("Arrays"));
	    invokeAsList.setName(ast.newSimpleName("asList"));
	    
	    classInstanceArrayList.arguments().add(invokeAsList);
	    
		VariableDeclarationStatement myVarDec = ast.newVariableDeclarationStatement(myVar);
		myVarDec.setType(ast.newSimpleType(ast.newSimpleName("List")));
        myVar.setInitializer(classInstanceArrayList);
        
        invokeAsList.arguments().add(ast.newSimpleName(newVarName));
        
		returnList.add(myVarDec);
		
		JTE.requiredClasses.add(List.class);
		JTE.requiredClasses.add(Arrays.class);
		JTE.requiredClasses.add(ArrayList.class);
		return returnList;       

	}
	
	@Override
	public Object getObject(){
		//Array.newInstance(clazz, length);
		Object retVal=new ArrayList();//new Object[length];
		//retVal
		//Object list = field.getType().newInstance();

		try {
			Method add = List.class.getDeclaredMethod("add",Object.class);
			//add.invoke(list, addToAddToList);
			for(int i=0;i<length;i++){
				try {
					add.invoke(retVal, elements[i].getObject());
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//Array.set(retVal, i, elements[i].getObject());
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return retVal;		
	}

	@Override
	public Object clone() {
		ListGenerator newList=new ListGenerator(parent,clazz);		
		newList.clazz=this.clazz;;
		newList.variableBinding=this.variableBinding;
		newList.fitness=this.fitness;
		newList.object=this.object;
		newList.seed=this.seed;
		newList.random=this.random;
		newList.length=this.length;
		newList.isFixedSize=this.isFixedSize;
		
		if(length>0){
			newList.elements=new AbsractGenerator[length];
			for(int i=0;i<length;i++)
				newList.elements[i]=(AbsractGenerator)this.elements[i].clone();
		}
		return newList;
	}

}
