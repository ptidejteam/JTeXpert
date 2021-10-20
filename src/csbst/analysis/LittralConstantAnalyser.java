package csbst.analysis;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class LittralConstantAnalyser extends ASTVisitor{
	private Map<IVariableBinding,Set<Integer>>  dataMemberTransformers=new HashMap<IVariableBinding,Set<Integer>>(); 	//<Data member index,ArrayList<branches>>
	
	private Vector<String> stringConstants=new Vector<String>();
	private Vector<Character> characterConstants=new Vector<Character>();
	private Vector<Byte> byteConstants=new Vector<Byte>();
	private Vector<Short> shortConstants=new Vector<Short>();
	private Vector<Integer> integerConstants=new Vector<Integer>();
	private Vector<Long> longConstants=new Vector<Long>();
	private Vector<Float> floatConstants=new Vector<Float>(); 
	private Vector<Double> doubleConstants=new Vector<Double>();
	
	private Vector<ITypeBinding> castConstants=new Vector<ITypeBinding>();
	
	public  Vector<ITypeBinding> getCastConstants(){
		return castConstants;
	}
	
	
	public  Vector<Byte> getByteConstants(){
		return byteConstants;
	}
	
	
	public  Vector<Short> getShortConstants(){
		return shortConstants;
	}
	
	public  Vector<Integer> getIntegerConstants(){
		return integerConstants;
	}
	
	public  Vector<Long> getLongConstants(){
		return longConstants;
	}	
	
	public  Vector<Float> getFloatConstants(){
		return floatConstants;
	}
	
	public  Vector<Double> getDoubleConstants(){
		return doubleConstants;
	}
	
	public  Vector<String> getStringConstants(){
		return stringConstants;
	}
	
	public  Vector<Character> getCharacterConstants(){
		return characterConstants;
	}
	
	@Override
	public boolean visit(CastExpression node){
		try{
			if(node.getType()!=null)
				if( node.getType().resolveBinding()!=null)
					castConstants.add(node.getType().resolveBinding());
		}catch (Exception e){
			//e.printStackTrace();
		}
		return true;
	}
	
	private boolean isThrowStatement(StringLiteral node){
		ASTNode n=node;
		//Block b;
		while (n!=null && !(n instanceof Block)){
			if(n instanceof ThrowStatement)
				return true;
			n=n.getParent();
		}
		return false;
	}
	@Override
	public boolean visit(StringLiteral node){
		if(!isThrowStatement(node))
			stringConstants.add(node.getLiteralValue());
		
//		if(throwLine.contains("throw")
//				||throwLine.contains("Exception")
//				|| (throwLine.contains("check"))
//				|| throwLine.contains("Assert")
//				|| throwLine.contains("Invariant")
//				|| throwLine.contains("Requires")
//				|| (throwLine.contains("System")&&throwLine.contains("exit"))
//				|| throwLine.contains("Throwable"))
		
		return true;
	}

	@Override
	public boolean visit(CharacterLiteral node){
		//System.out.println(node.charValue());
		characterConstants.add(node.charValue());
		return true;
	}
	
	@Override
	public boolean visit(SimpleName node){
		//node.resolveConstantExpressionValue();
		if(node.resolveConstantExpressionValue()!=null && node.resolveTypeBinding()!=null &&  !node.resolveConstantExpressionValue().toString().equalsIgnoreCase("NaN")){
			String className=node.resolveTypeBinding().getBinaryName();
			insert(className,node.resolveConstantExpressionValue().toString());
		}
		return true;
	}
	
	
	@Override
	public boolean visit(NumberLiteral node){
		String className=node.resolveTypeBinding().getBinaryName();
		if(node.resolveConstantExpressionValue()!=null && className!=null){
			insert(className,node.resolveConstantExpressionValue().toString());
		}
		return true;
	}
	
	private void insert(String className, String value){
		if (className.equals("B")||className.equalsIgnoreCase("java.lang.Byte")){
			byteConstants.add(Byte.parseByte(value));
			byteConstants.add((byte) (Byte.parseByte(value)+1));
			byteConstants.add((byte) (Byte.parseByte(value)-1));
		}
		if (className.equals("S")||className.equalsIgnoreCase("java.lang.Short")){
			shortConstants.add(Short.parseShort(value));
			shortConstants.add((short) (Short.parseShort(value)+1));
			shortConstants.add((short) (Short.parseShort(value)-1));
		}
		if (className.equals("I")||className.equalsIgnoreCase("java.lang.Integer")){
			integerConstants.add(Integer.parseInt(value));
			integerConstants.add((int)(Integer.parseInt(value)+1));
			integerConstants.add((int)(Integer.parseInt(value)-1));
		}
		if (className.equals("J")||className.equalsIgnoreCase("java.lang.Long")){
			longConstants.add(Long.parseLong(value));
			longConstants.add((long)(Long.parseLong(value)+1L));
			longConstants.add((long)(Long.parseLong(value)-1L));	
		}
		if (className.equals("F")||className.equalsIgnoreCase("java.lang.Float")){
			floatConstants.add(Float.parseFloat(value));
			floatConstants.add((float)(Float.parseFloat(value)+1));
			floatConstants.add((float)(Float.parseFloat(value)-1));
		}
		if (className.equals("D")||className.equalsIgnoreCase("java.lang.Double")){
			doubleConstants.add(Double.parseDouble(value));	
			doubleConstants.add((double)(Double.parseDouble(value)+1));
			doubleConstants.add((double)(Double.parseDouble(value)-1));
		}
		if (className.equalsIgnoreCase("java.lang.String")){
			//if(!value.contains("\\u"))
				stringConstants.add(value);
			//else
			//TODO add this char to the characterConstants	
		}
	}

}
