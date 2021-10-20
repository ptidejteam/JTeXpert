package csbst.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PostfixExpression.Operator;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class CreateJavaClass {

	private static CompilationUnit getTestCasesSourceCode(){		
		//create a CompilationUnit
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource("".toCharArray()); //The parser is initialized with an empty array
		CompilationUnit unit = (CompilationUnit) parser.createAST(null); 
		unit.recordModifications();
		AST ast = unit.getAST();

		
		//Example of a package import ("org.junit.Test")
		ImportDeclaration importDeclaration = ast.newImportDeclaration();
		
		QualifiedName nQN;
		nQN= ast.newQualifiedName(ast.newQualifiedName(ast.newSimpleName("org"),ast.newSimpleName("junit")),ast.newSimpleName("Test"));
		importDeclaration.setName(nQN);
		unit.imports().add(importDeclaration);

		
		//Example of class creation
		//Create a new class
		TypeDeclaration clazzNode= ast.newTypeDeclaration();
		clazzNode.setInterface(false);
		clazzNode.setName(ast.newSimpleName("ClassName"));		
		clazzNode.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		clazzNode.setSuperclassType(ast.newSimpleType(ast.newName("SuperClassName")));
		unit.types().add(clazzNode);


		//Example of a field creation
		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();					
		vdf.setInitializer(ast.newNumberLiteral("0"));
		vdf.setName(ast.newSimpleName("FieldName"));
		
		FieldDeclaration vds = ast.newFieldDeclaration(vdf);
		List modifiers = vds.modifiers();
		vds.setType(ast.newSimpleType(ast.newSimpleName("Integer")));
		modifiers.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD)); 
		modifiers.add(ast.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD));
						
		clazzNode.bodyDeclarations().add(0, vds);

		
		//Example of a method creation
		MethodDeclaration method=ast.newMethodDeclaration();
		method = ast.newMethodDeclaration();
		
		method.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		method.setName(ast.newSimpleName("MethodName"));
		method.setBody(ast.newBlock());
		
		//Example of statements creation {int i=FieldName; i++; i=i+2}
		
		//This list is optional (you can add a statement directly to the body by using add method)
		List<Statement>returnList=new ArrayList<Statement>();
		
		//Create a variable declaration ( int i=FieldName;)
		VariableDeclarationFragment varDec=ast.newVariableDeclarationFragment();
		varDec.setName(ast.newSimpleName("i"));
		varDec.setInitializer(ast.newSimpleName("FieldName"));
		
		VariableDeclarationStatement varDecStat = ast.newVariableDeclarationStatement(varDec);
		
		//insert the statement into the list
		returnList.add(varDecStat);	
		
		//Create a statement (i++)
		PostfixExpression inc =  ast.newPostfixExpression();
		inc.setOperator(Operator.INCREMENT); //org.eclipse.jdt.core.dom.PostfixExpression.
		inc.setOperand(ast.newSimpleName("i"));	
		ExpressionStatement incStatement=ast.newExpressionStatement(inc);
		
		//insert the statement into the list
		returnList.add(incStatement);
		
		//Create a statement (i=i+2)
		Assignment Ass = ast.newAssignment();
    	Ass.setLeftHandSide(ast.newSimpleName("i"));
    	InfixExpression rightHandSideExpression=ast.newInfixExpression();
    	rightHandSideExpression.setOperator(InfixExpression.Operator.PLUS);   	
    	rightHandSideExpression.setLeftOperand(ast.newSimpleName("i"));
    	rightHandSideExpression.setRightOperand(ast.newNumberLiteral("2"));
    	Ass.setRightHandSide(rightHandSideExpression);
    	ExpressionStatement AssStatement=ast.newExpressionStatement(Ass);
		
    	//insert the statement into the list
    	returnList.add(AssStatement);
		
		method.getBody().statements().addAll(returnList);
				
		clazzNode.bodyDeclarations().add(method);
		

		return unit;
	}
	
	public static void main(String[] args) throws Exception{
		 java.util.Collection c = null;// java.util.Collection, isArray, java.util.Map
		 
		 java.util.Map m= new HashMap();m.put(null,null); m.put(null,null);
		 
		 List a=new ArrayList(); a.add(null);
		 Set s=new HashSet(); s.addAll(a); s.add(null);
		 
		 int[] b = new int[0];
		 Vector v = new Vector(); v.add(null); 
		 
		//System.out.println("c "+c.getClass().isArray());
		System.out.println("a "+a.getClass().isArray()+" "+a.getClass().isAssignableFrom(Collection.class)+" "+Collection.class.isAssignableFrom(a.getClass()));
		System.out.println("b "+b.getClass().isArray()+" "+b.getClass().isAssignableFrom(Collection.class)+" "+Collection.class.isAssignableFrom(b.getClass()));
		System.out.println("v "+v.getClass().isArray()+" "+v.getClass().isAssignableFrom(Collection.class)+" "+Collection.class.isAssignableFrom(v.getClass()));
		System.out.println("m "+m.getClass().isArray()+" "+m.getClass().isAssignableFrom(Collection.class)+" "+Collection.class.isAssignableFrom(m.getClass()));
		System.out.println("s "+s.getClass().isArray()+" "+s.getClass().isAssignableFrom(Collection.class)+" "+Collection.class.isAssignableFrom(s.getClass()));

		
	}
}
