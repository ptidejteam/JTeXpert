package csbst.generators.atomic;


import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.util.ArrayList;
import java.util.List;

import csbst.generators.AbsractGenerator;
import csbst.utils.FileEditor;


public abstract class AbstractPrimitive<T> extends AbsractGenerator{
//	protected T object;
	protected T uBound;
	protected T lBound;
	protected T absolutuBound;//=(int) (Math.pow(2, 31)-1);// 2147483647;
	protected T absolutlBound;//=(int) (-1*Math.pow(2, 31));//-2147483648;
	
	public AbstractPrimitive(AbsractGenerator parent, Class type){
		super(parent,type);
		//this.setClazz(type);
	}

	@Override
	public List<Statement>getStatements(AST ast, String varName, String pName){	
		//create a variable declaration
		List<Statement>returnList=new ArrayList<Statement>();
		VariableDeclarationFragment varDec=ast.newVariableDeclarationFragment();		
		varDec.setName(ast.newSimpleName(varName+pName));
		
		VariableDeclarationStatement varDecStat;
		if(object!=null){
			NumberLiteral numberLiteral=ast.newNumberLiteral(""+this.toString());
    		varDec.setInitializer(numberLiteral);
		}else{
			NullLiteral numberLiteral=ast.newNullLiteral();
			varDec.setInitializer(numberLiteral);
		}
		varDecStat = ast.newVariableDeclarationStatement(varDec);
		//varDecStat.setType(ast.newPrimitiveType(ASTEditor.getPrimitiveCode(clazz)));
		if(getPrimitiveCode(clazz)!=null)
			varDecStat.setType(ast.newPrimitiveType(getPrimitiveCode(clazz)));
		else
			varDecStat.setType(ast.newSimpleType(ast.newSimpleName(clazz.getSimpleName())));


		returnList.add(varDecStat);    
		return returnList;
	}
	
//	public GenePrimitive(Class<T> type, T u, T l, T v){
//		this(type,u,l);
//		this.setObject(v);
//	}
	
	public void setuBound(T u){
		uBound=u;
	}
	
	public void setlBound(T l){
		lBound=l;
	}
	
	public T getuBound(){
		return uBound;
	}
	
	public T getlBound(){
		return lBound;
	}
	
	public T getAbsolutuBound(){
		return absolutuBound;
	}
	
	public T getAbsolutlBound(){
		return absolutlBound;
	}	
	@Override
	public boolean equals(Object other) {
		boolean areEqual=super.equals(other);
		areEqual=areEqual &&(uBound.equals(((AbstractPrimitive)other).getuBound()));
		areEqual=areEqual &&(lBound.equals(((AbstractPrimitive)other).getlBound()));
		areEqual=areEqual &&(absolutuBound.equals(((AbstractPrimitive)other).getuBound()));
		areEqual=areEqual &&(absolutlBound.equals(((AbstractPrimitive)other).getAbsolutlBound()));
		return areEqual;
	}

    public static final Class<?> getGeneClass(Class cls) {
        if (cls.equals(Byte.class)||cls.equals(byte.class))
            return ByteGenerator.class;
        if (cls.equals(Short.class)||cls.equals(short.class))
            return ShortGenerator.class;
        if (cls.equals(Integer.class)||cls.equals(int.class))
            return IntegerGenerator.class;
        if (cls.equals(Long.class)||cls.equals(long.class))
            return LongGenerator.class;
        if (cls.equals(Boolean.class)||cls.equals(boolean.class))
            return BooleanGenerator.class;
        if (cls.equals(char.class)||cls.equals(Character.class))
            return CharGenerator.class;
        if (cls.equals(Float.class)||cls.equals(float.class))
            return FloatGenerator.class;
        if (cls.equals(Double.class)||cls.equals(double.class))
            return DoubleGenerator.class;
        //if(cls.equals(Object.class))
    	//	return DefaultGenericType;
        
        return null;
    }
}

