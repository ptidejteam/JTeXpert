package csbst.generators.atomic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.NullLiteral;

import csbst.generators.AbsractGenerator;
import csbst.testing.JTE;
import csbst.utils.FileEditor;
import csbst.utils.RandomStringGenerator;

public class CharGenerator extends AbstractPrimitive<Character>{
	
	public CharGenerator(AbsractGenerator parent, Class cls) {
		super(parent,cls);
		//this.setClazz(char.class);	
		absolutuBound=Character.MAX_VALUE;
		absolutlBound=Character.MIN_VALUE;
		uBound=absolutuBound;
		lBound=absolutlBound;
		this.generateRandom();
	}


	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false;
		returnValue=(gene instanceof CharGenerator);				
		returnValue=returnValue &&(clazz.equals(gene.getClazz()));		
		return returnValue;
	}
	
	@Override
	public void setObject(Object obj){
		Character v=(Character)obj;
		super.setObject(v);
		//if(v>uBound || v<lBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}
	
	@Override
	public void setlBound(Character l){
		lBound=l;
		//if(absolutlBound>lBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}

	@Override
	public void setuBound(Character u){
		uBound=u;
		//if(absolutuBound<uBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}
	
	@Override
	public void generateRandom() {
		Random random=new Random();
		generateRandom(random);
		
		if(getObject()!=null){
			AST ast= new AST();
			CharacterLiteral charLiteral=ast.newCharacterLiteral();
			//UnicodeBlock a=(Character.UnicodeBlock)Array.get(returnObject, i);
			char a= (Character) getObject();			        		
			String unicode="\'"+String.format("\\u%04x", (int) a)+"\'";//"\'\\"+CharGenerator.toUnicode((Character) Array.get(returnObject, i))+"\'";
			//System.out.println(unicode);
			 
			try{
				charLiteral.setEscapedValue(unicode); 
			}catch (IllegalArgumentException ie){
				generateRandom();
			}
		}
	}

	public void generateRandom(Random random) {
		if(random.nextInt(100)<SEEDING_NULL_PROBABILITY && !clazz.equals(char.class)){
			this.setObject(null);
			return;
		}
		
		if(random.nextInt(100)<90){//visible only
			int probability=random.nextInt(100);
			if((probability<SEEDING_MIN_PROBABILITY &&  JTE.litteralConstantAnalyser.getCharacterConstants().size()>SEEDING_MIN_NUMBER)
					|| (probability<SEEDING_MAX_PROBABILITY &&  JTE.litteralConstantAnalyser.getCharacterConstants().size()>SEEDING_MAX_NUMBER)){
				int index =random.nextInt(JTE.litteralConstantAnalyser.getCharacterConstants().size());
				
				//object=JTE.litteralConstantAnalyser.getCharacterConstants().get(index);
				this.setObject((Character)JTE.litteralConstantAnalyser.getCharacterConstants().get(index));
				return;
			}
			String str=new String();
			while (str==null || str.length()<1){
			RandomStringGenerator randStr=new RandomStringGenerator(1,random.nextBoolean(),random.nextBoolean(),
					random.nextBoolean(),random.nextBoolean(),random.nextBoolean(),random.nextBoolean());
			
				str=randStr.getRandomString();
			}
			
			this.setObject((Character)str.charAt(0));
		}
		else{
			char val=(char) (lBound+random.nextInt(uBound-lBound+1));
			Character valChar=val;
			this.setObject(valChar);
		}
		object=getObject();
	}

	@Override
	public int hashCode() {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();
        if(object==null)
        	return 1;
        
        hash = ( hash << 1 | hash >>> 31) ^(Character)this.getObject();
        return hash;
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
		CharGenerator newGene=new CharGenerator(parent,clazz);
		if(object!=null)
			newGene.object=((Character)this.getObject()).charValue();
		newGene.fitness=this.fitness;
		newGene.clazz=this.clazz;
		newGene.variableBinding=this.variableBinding;
		newGene.random=this.random;
		newGene.seed=this.seed;
		
		return newGene;
	}
	
	@Override
	public List<Statement>getStatements(AST ast, String varName, String pName){	
		//create a variable declaration
		//Character c=new Character('\u0032');
		List<Statement>returnList=new ArrayList<Statement>();
		VariableDeclarationFragment varDec=ast.newVariableDeclarationFragment();		
		varDec.setName(ast.newSimpleName(varName+pName));
		
		VariableDeclarationStatement varDecStat;
		if(object!=null){
			CharacterLiteral charLiteral=ast.newCharacterLiteral();
    		char a= (Character) this.getObject();			        		
    		String unicode="\'"+String.format("\\u%04x", (int) a)+"\'";
    		charLiteral.setEscapedValue(unicode);
			varDec.setInitializer(charLiteral);
		}else{
			NullLiteral charNull=ast.newNullLiteral();
			varDec.setInitializer(charNull);
		}
			
		varDecStat = ast.newVariableDeclarationStatement(varDec);
		//varDecStat.setType(ast.newPrimitiveType(ASTEditor.getPrimitiveCode(clazz)));
		if(clazz.equals(char.class))
			varDecStat.setType(ast.newPrimitiveType(getPrimitiveCode(clazz)));
		else
			varDecStat.setType(ast.newSimpleType(ast.newSimpleName(clazz.getSimpleName())));

		returnList.add(varDecStat);    
		return returnList;
	}
	
	@Override
	public String toString(){
		if(object==null)
			return "null";
		
		//Character.to
		return (toUnicode((Character)getObject()));//.toString();
	}
	
	public static String toUnicode(char ch) {
		String s=String.format("\\u%04x", (int) ch);
	    return s;
	}
}

