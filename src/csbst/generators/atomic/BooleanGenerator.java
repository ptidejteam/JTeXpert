package csbst.generators.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import csbst.generators.AbsractGenerator;
import csbst.utils.FileEditor;

public class BooleanGenerator extends AbstractPrimitive<Boolean>{
	
	public BooleanGenerator(AbsractGenerator parent,Class cls) {
		super(parent,cls);
		absolutuBound=Boolean.TRUE;
		absolutlBound=Boolean.FALSE;
		uBound=absolutuBound;
		lBound=absolutlBound;
		this.generateRandom();
	}
	
	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false;
		returnValue=(gene instanceof BooleanGenerator);				
		returnValue=returnValue &&(clazz.equals(gene.getClazz()));		
		return returnValue;
	}
	
	@Override
	public void setObject(Object obj){
		Boolean v=(Boolean)obj;
		super.setObject(v);
	}
	
	@Override
	public Object getObject(){
		return (Boolean)object;
	}
	
	@Override
	public void generateRandom() {
		Random random=new Random();
		generateRandom(random);
	}
	
	public void generateRandom(Random random) {	
		if(random.nextInt(100)<SEEDING_NULL_PROBABILITY && !clazz.equals(boolean.class)){
			this.setObject(null);
			return;
		}
		this.setObject(random.nextBoolean());
		object=getObject();
	}

	@Override
	public int hashCode() {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();
        hash = 0;//( hash << 1 | hash >>> 31) ^(int)this.getObject();
        return hash;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}	
	
	@Override
    public void mutate(){
		//mutation probability
		 generateRandom();
    }
	
	@Override
	public String toString(){
		if(object==null)
			return "null";
		return ((Boolean)getObject()).toString();
	}
	
	@Override
	public List<Statement>getStatements(AST ast, String varName, String pName){	
		//create a variable declaration
		List<Statement>returnList=new ArrayList<Statement>();
		VariableDeclarationFragment varDec=ast.newVariableDeclarationFragment();		
		varDec.setName(ast.newSimpleName(varName+pName));
		
		if(object!=null){		
			BooleanLiteral boolLiteral=ast.newBooleanLiteral((Boolean)object);
			varDec.setInitializer(boolLiteral);
		}else{
			NullLiteral boolLiteral=ast.newNullLiteral();
			varDec.setInitializer(boolLiteral);
		}
			
		VariableDeclarationStatement varDecStat;
		varDecStat = ast.newVariableDeclarationStatement(varDec);
		if(clazz.equals(boolean.class))
			varDecStat.setType(ast.newPrimitiveType(getPrimitiveCode(clazz)));
		else
			varDecStat.setType(ast.newSimpleType(ast.newSimpleName(clazz.getSimpleName())));

		returnList.add(varDecStat);    
		return returnList;
	}
	
	@Override
	public Object clone(){
		BooleanGenerator newGene=new BooleanGenerator(parent,clazz);
		if(object!=null)
			newGene.object=((Boolean)this.object).booleanValue();
		newGene.fitness=this.fitness;
		newGene.clazz=this.clazz;
		newGene.variableBinding=this.variableBinding;
		newGene.random=this.random;
		newGene.seed=this.seed;
		
		return newGene;
	}
}
