package csbst.generators.containers;

import java.lang.reflect.Array;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import csbst.generators.AbsractGenerator;
import csbst.generators.atomic.CharGenerator;
import csbst.generators.atomic.DoubleGenerator;
import csbst.generators.atomic.FloatGenerator;
import csbst.utils.FileEditor;

public class ArrayGenerator  extends AbsractGenerator {
	public static final int maxLength=5;
	protected int length=0; 
	protected boolean isFixedSize;
	protected AbsractGenerator[] elements;
	
	public ArrayGenerator(AbsractGenerator parent, int l, Class type) {
		super(parent,type);
		length=l;	
		if(l!=0)
			isFixedSize=true;
		else
			isFixedSize=false;
		
//		if(type.equals(Object.class))
//			type=DefaultGenericType;
		
		clazz=type;
		generateRandom();
	}
	
	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false;
		returnValue=(gene instanceof ArrayGenerator);				
		returnValue=returnValue &&(clazz.equals(gene.getClazz()));		
		return returnValue;
	}
	
	@Override
	public void generateRandom() {
		if(random.nextInt(100)<SEEDING_NULL_PROBABILITY){
			this.setObject(null);
			elements=null;
			length=0;
			return;
		}
		
		if(!isFixedSize){
			int probability=random.nextInt(100);
			if(probability<5)
				length=0;
			else{
				Random rand=new Random();
				length=rand.nextInt(maxLength);
			}
		}
		
		elements=new AbsractGenerator[length];
		if(length>0){		
			for(int i=0;i<length;i++){
				elements[i]=createAdequateGene(this, clazz);
				elements[i].generateRandom();
			}
		}
		
		object=getObject();	
	}

	@Override
	public void mutate() {
		//mutation probability
		int muProp=random.nextInt(100);
		
		//if((muProp<=84 && this.getFitness().getBD()==0)||(muProp>84 && this.getFitness().getBD()!=0))
		//	 return;
		
		int muConProp=random.nextInt(100);

		if(!isFixedSize && muConProp<50){
			generateRandom();
			return;
		}
		
		if(length>0){
			int mutPb=100/length;
			for(int i=0;i<length;i++)
				if(random.nextInt(100)<=mutPb)
					elements[i].mutate();
		}
	}

	@Override
	public Object clone() {
		ArrayGenerator newArray=new ArrayGenerator(parent,length,clazz);		
		newArray.clazz=this.clazz;
		newArray.variableBinding=this.variableBinding;
		newArray.fitness=this.fitness;
		newArray.object=this.object;
		newArray.seed=this.seed;
		newArray.random=this.random;
		newArray.length=this.length;
		newArray.isFixedSize=this.isFixedSize;
		
		if(length>0){
			newArray.elements=new AbsractGenerator[length];
			for(int i=0;i<length;i++)
				newArray.elements[i]=(AbsractGenerator)this.elements[i].clone();
		}
		return newArray;
	}
	
	@Override
	public Object getObject(){
		//Array.newInstance(clazz, length);
		if(elements==null)
			return null;
		
		Object retVal=Array.newInstance(clazz, length);//new Object[length];
		for(int i=0;i<length;i++){
			try{
				if(elements[i].getObject()!=null)
					Array.set(retVal, i, elements[i].getObject()); 
			}catch(Exception e){
				System.err.println("Array Error :"+ elements[i].getObject() +" class :"+clazz);
				e.printStackTrace();
			}
		}
		return retVal;		
	}
	
	@Override
	public List<Statement> getStatements(AST ast, String varName, String pName) {
		List<Statement>returnList=new ArrayList<Statement>();

		VariableDeclarationFragment myVar=ast.newVariableDeclarationFragment();
		myVar.setName(ast.newSimpleName(varName));
		
		VariableDeclarationStatement myVarDec = ast.newVariableDeclarationStatement(myVar);
        
        if(clazz.isPrimitive()){
    		myVarDec.setType(ast.newArrayType(ast.newPrimitiveType(getPrimitiveCode(clazz))));
    		if(getObject()==null){
    			NullLiteral nullLiteral=ast.newNullLiteral();
    			myVar.setInitializer(nullLiteral);
    		}else{
    	        ArrayCreation aCreation= ast.newArrayCreation(); 
    	        myVar.setInitializer(aCreation);
    	        ArrayInitializer aInitializer=ast.newArrayInitializer();
	    		aCreation.setType(ast.newArrayType(ast.newPrimitiveType(getPrimitiveCode(clazz))));
		        for(int i=0;i<length;i++){
		        	if(clazz.equals(char.class)){
		        		CharacterLiteral charLiteral=ast.newCharacterLiteral();
		        		char a= (Character)elements[i].getObject();			        		
		        		String unicode="\'"+String.format("\\u%04x", (int) a)+"\'";
		        		charLiteral.setEscapedValue(unicode);
		        		//charLiteral.setCharValue((Character)elements[i].getObject());//CharGenerator.toUnicode(ch)
		        		aInitializer.expressions().add(charLiteral);
		        	}else if(clazz.equals(boolean.class)){
		        		BooleanLiteral boolLiteral=ast.newBooleanLiteral((Boolean)elements[i].getObject());
		        		aInitializer.expressions().add(boolLiteral);
		        	}else {
		        		if((elements[i] instanceof FloatGenerator || elements[i] instanceof DoubleGenerator) 
		        				&& elements[i].toString().contains("Infinity")) {
		        			String type="";
		        			if((elements[i] instanceof DoubleGenerator))
		        				type="Double";
		        			else
		        				type="Float";
		        			
		        			String prefix="";
		        			if((elements[i].toString().contains("-")))
		        				prefix="NEGATIVE_INFINITY";
		        			else
		        				prefix="POSITIVE_INFINITY";
		        			
		        			QualifiedName nQN= ast.newQualifiedName(ast.newSimpleName(type),ast.newSimpleName("POSITIVE_INFINITY"));
		        			aInitializer.expressions().add(nQN);
		        			//Double.NEGATIVE_INFINITY
		        		    
		        		}
		        		else{
		        			NumberLiteral numberLiteral=ast.newNumberLiteral(""+elements[i].toString());
		        			aInitializer.expressions().add(numberLiteral);
		        		}
		        	}
		        } 
		        aCreation.setInitializer(aInitializer);
    		}
        }
        else{
        	
        	Type TypeName =getType2UseInJunitClass(clazz,ast);
        	//Type TypeName =getType2UseInJunitClass(clazz,ast);
        	//Name qNameClassCopy= (Name) ASTNode.copySubtree(ast, qNameClass);
        	Type TypeNameCopy= (Type) ASTNode.copySubtree(ast, TypeName);
        	myVarDec.setType(ast.newArrayType(TypeNameCopy));
        	//myVarDec.setType(ast.newArrayType(ast.newSimpleType(qNameClassCopy)));
    		if(getObject()==null){
    			NullLiteral nullLiteral=ast.newNullLiteral();
    			myVar.setInitializer(nullLiteral);
    		}else{

    	        ArrayCreation aCreation= ast.newArrayCreation();
    	        myVar.setInitializer(aCreation);
    	        ArrayInitializer aInitializer=ast.newArrayInitializer();
	        	//aCreation.setType(ast.newArrayType(ast.newSimpleType(qNameClass)));
    	        aCreation.setType(ast.newArrayType(TypeName));
		        for(int i=0;i<length;i++){
		        	String newVarName=varName+pName+"P"+(i+1);
			    	returnList.addAll(elements[i].getStatements(ast,newVarName,""));
		        	aInitializer.expressions().add(ast.newSimpleName(newVarName));
		        }
		        aCreation.setInitializer(aInitializer);
    		}
        }
        
        
		returnList.add(myVarDec);
				
		return returnList;
	}
	
	@Override
	public int hashCode() {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();

        hash = ( hash << 1 | hash >>> 31 );
        if(elements==null)
        	return 0;
        for(int x=0;x<elements.length;x++)
        	if(elements[x]!=null)
        		hash = ( hash << 1 | hash >>> 31 ) ^ elements[x].hashCode();

        return hash;
	}

	@Override
	public String toString(){
		String str=new String();
		str="[";
		if(elements!=null)
			for(int i=0;i<elements.length;i++){
				str+=elements[i].toString();
				if(i<length-1)
					str+=",";
			}
		str+="]";
		
		return str;
	}
}