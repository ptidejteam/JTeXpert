package csbst.generators.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import csbst.generators.AbsractGenerator;
import csbst.testing.JTE;
import csbst.utils.FileEditor;

public class FloatGenerator extends AbstractPrimitive<Float>{
	
	public FloatGenerator(AbsractGenerator parent, Class cls) {
		super(parent,cls);
		absolutuBound=Float.MAX_VALUE;
		absolutlBound=Float.MIN_VALUE;
		uBound=absolutuBound;
		lBound=absolutlBound;
		this.generateRandom();
	}
	
	@Override
	public void setObject(Object obj){
		Float v=(Float)obj;
		super.setObject(v);
		//if(v>uBound || v<lBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}
	
	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false;
		returnValue=(gene instanceof FloatGenerator);				
		returnValue=returnValue &&(clazz.equals(gene.getClazz()));		
		return returnValue;
	}

	@Override
	public void setlBound(Float l){
		lBound=l;
		//if(absolutlBound>lBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}

	@Override
	public void setuBound(Float u){
		uBound=u;
		//if(absolutuBound<uBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}
	
	@Override
	public void generateRandom() {
		Random random=new Random();
		generateRandom(random);
	}

	public void generateRandom(Random random) {
		if(random.nextInt(100)<SEEDING_NULL_PROBABILITY && !clazz.equals(float.class)){
			this.setObject(null);
			return;
		}
		
		int probability=random.nextInt(100);
		if((probability<SEEDING_MIN_PROBABILITY &&  JTE.litteralConstantAnalyser.getFloatConstants().size()>SEEDING_MIN_NUMBER)
				|| (probability<SEEDING_MAX_PROBABILITY &&  JTE.litteralConstantAnalyser.getFloatConstants().size()>SEEDING_MAX_NUMBER)){
			int index =random.nextInt(JTE.litteralConstantAnalyser.getFloatConstants().size());
			
			//for(Float f:JTE.litteralConstantAnalyser.getFloatConstants())
			//	System.out.println(f);
			//object=JTE.litteralConstantAnalyser.getCharacterConstants().get(index);
			this.setObject((Float)JTE.litteralConstantAnalyser.getFloatConstants().get(index));
			return;
		}
		
		if(random.nextInt(100)<90){
			float range=201F;
			this.setObject((Float) (-100F+random.nextFloat()*range));
		}else
			this.setObject((Float) (lBound+random.nextFloat()*(uBound-lBound)));
		object=getObject();
	}

	@Override
	public int hashCode() {

        return 0;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}	
	
	@Override
    public void mutate(){
		generateRandom() ;
	}
	
	@Override
	public Object clone(){
		FloatGenerator newGene=new FloatGenerator(parent,clazz);
		if(object!=null)
			newGene.object=((Float)this.object).floatValue();
		newGene.fitness=this.fitness;
		newGene.clazz=this.clazz;
		newGene.variableBinding=this.variableBinding;
		newGene.random=this.random;
		newGene.seed=this.seed;
		
		return newGene;
	}
//	@Override
//	public List<Statement>getStatements(AST ast, String varName, String pName){	
//		//create a variable declaration
//		List<Statement>returnList=new ArrayList<Statement>();
//		VariableDeclarationFragment varDec=ast.newVariableDeclarationFragment();		
//		varDec.setName(ast.newSimpleName(varName+pName));		
//		if(object!=null){
//			NumberLiteral numberLiteral=ast.newNumberLiteral(toString());
//			varDec.setInitializer(numberLiteral);
//		}else{
//			NullLiteral boolLiteral=ast.newNullLiteral();
//			varDec.setInitializer(boolLiteral);
//		}
//		VariableDeclarationStatement varDecStat;
//		varDecStat = ast.newVariableDeclarationStatement(varDec);
//		varDecStat.setType(ast.newPrimitiveType(ASTEditor.getPrimitiveCode(clazz)));
//
//		returnList.add(varDecStat);    
//		return returnList;
//	}

	@Override
	public String toString(){
		if(object==null)
			return "null";
		Float f=(Float) object;
		
		return ""+f+"F";
	}
}
