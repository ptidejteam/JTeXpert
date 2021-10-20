package csbst.generators.containers;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import csbst.generators.AbsractGenerator;
import csbst.generators.dynamic.InstanceGenerator;
import csbst.utils.FileEditor;

public class CollectionGenerator  extends AbsractGenerator {
	public static final int maxLength=10;
	protected int length=0;
	protected boolean isFixedSize;
	protected AbsractGenerator container;
	protected AbsractGenerator[] elements;
	protected boolean[] toInsert;
	protected Class containerType;
	protected Class elementType;
	protected Class cElementType;
	
	
	public CollectionGenerator(AbsractGenerator parent, int l, Class containerType, Class elementType) {
		super(parent,elementType); 
		//System.out.println("containerType: "+containerType+ " elementType: "+elementType);
		
		length=l;
		if(l!=0)
			isFixedSize=true;
		else
			isFixedSize=false;
		

//		clazz=elementType;
		this.elementType=elementType; 
		this.containerType=containerType;
		
//		isNullAccepted=false;
		
		generateRandom();
	}
	
	@Override
	public void generateRandom() {
		container=createAdequateGene(this, containerType); 
		container.generateRandom();
		if(container.getObject()==null){
			this.setObject(null);
			elements=null;
			length=0;
			return; 
		} 
		
		//find the correct type of elements according to the selected stub		
		Method add=null;
		Method allMethod[]=((InstanceGenerator)container).getStub().getMethods(); //.add //.get
		for(int i=0;i<allMethod.length;i++){
			if(allMethod[i].getName().equalsIgnoreCase("add")){
				add=allMethod[i];
				break;
			}
		}
			
					
		if(add!=null && !elementType.equals(Object.class) && !add.getParameterTypes()[0].isAssignableFrom(elementType) ){
			generateRandom();
			return;
		}
			
			
		if( elementType.equals(Object.class)){
			if(add!=null)
				cElementType=add.getParameterTypes()[0];
			else{
				//if(elementType.equals(Object.class)){
				Random rand=new Random(System.currentTimeMillis());
				int muProp=rand.nextInt(100);
				if(muProp<20){
					if(muProp<10){ 
						rand=new Random();
						int index =rand.nextInt(InstanceGenerator.defaultClassesSet.size());
						cElementType=InstanceGenerator.defaultClassesSet.get(index);//InstanceGenerator.DefaultGenericType;//InstanceGenerator.defaultClassesSet.get(index);
					}
				}else
					cElementType=InstanceGenerator.DefaultGenericType;
				//}
			}
		}else
			cElementType=elementType;
		
		
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
		toInsert=new boolean[length];
		
		for(int i=0;i<length;i++){
			toInsert[i]=true;
			elements[i]=createAdequateGene(this, cElementType);			
			elements[i].generateRandom();
		}		
		object=this.getObject();	
	}


	
	
	//copy past from ArrayGenerator
	@Override
	public List<Statement> getStatements(AST ast, String varName, String pName) {
		List<Statement>returnList=new ArrayList<Statement>();
		String newVarNameContainer=varName+pName;
		returnList.addAll(container.getStatements(ast, newVarNameContainer, ""));
		
		if(container.getObject()==null)
			return returnList;
		 
		 MethodInvocation invokeAsList;
       
        if(cElementType.isPrimitive()){
	        for(int i=0;i<length;i++){
	        	if(!toInsert[i]) 
	        		continue;
	        	if(cElementType.equals(char.class)){
	        		CharacterLiteral charLiteral=ast.newCharacterLiteral();
	        		charLiteral.setCharValue((Character)elements[i].getObject());
	        		
	        		invokeAsList=ast.newMethodInvocation();
	       		 	invokeAsList.setExpression(ast.newSimpleName(newVarNameContainer));
	       		 	invokeAsList.setName(ast.newSimpleName("add"));
	       		 	
	       		 	invokeAsList.arguments().add(charLiteral);
	       		 	
	       		 	Statement statement=ast.newExpressionStatement(invokeAsList);
					if(SystematicallySurroundCall){ //||unexpectedException!=null)			    	
				    	TryStatement tryStatement=ast.newTryStatement(); 
				    	
			    		Class except=Exception.class;			    	
				    	tryStatement.catchClauses().add(getCatchClause(except,ast,false));
					    	
				    	Block b =ast.newBlock();
				    	b.statements().add(statement);
				    	tryStatement.setBody(b);

				    	returnList.add(tryStatement);
				    }else
				    	returnList.add(statement);
	       		 	//returnList.add(statement);
	       		 	
	        	}else if(cElementType.equals(boolean.class)){
	        		BooleanLiteral boolLiteral=ast.newBooleanLiteral((Boolean)elements[i].getObject());	        		
	        		invokeAsList=ast.newMethodInvocation();
	       		 	invokeAsList.setExpression(ast.newSimpleName(newVarNameContainer));
	       		 	invokeAsList.setName(ast.newSimpleName("add"));       		 	
	       		 	invokeAsList.arguments().add(boolLiteral);
	       		 	Statement statement=ast.newExpressionStatement(invokeAsList);
					if(SystematicallySurroundCall){ //||unexpectedException!=null)			    	
				    	TryStatement tryStatement=ast.newTryStatement(); 
				    	
			    		Class except=Exception.class;			    	
				    	tryStatement.catchClauses().add(getCatchClause(except,ast,false));
					    	
				    	Block b =ast.newBlock();
				    	b.statements().add(statement);
				    	tryStatement.setBody(b);

				    	returnList.add(tryStatement);
				    }else
				    	returnList.add(statement);
	       		 	//returnList.add(statement);
	        	}else {
	        		
	        		NumberLiteral numberLiteral=ast.newNumberLiteral(""+elements[i].toString());
	        		if(cElementType.equals(double.class))
	        			 numberLiteral=ast.newNumberLiteral(""+elements[i].toString()+"D");
	        		if(cElementType.equals(float.class))
	        			 numberLiteral=ast.newNumberLiteral(""+elements[i].toString()+"F");
	        		if(cElementType.equals(long.class))
	        			 numberLiteral=ast.newNumberLiteral(""+elements[i].toString()+"L");
	        		
	        		invokeAsList=ast.newMethodInvocation();
	       		 	invokeAsList.setExpression(ast.newSimpleName(newVarNameContainer));
	       		 	invokeAsList.setName(ast.newSimpleName("add"));       		 	
	       		 	invokeAsList.arguments().add(numberLiteral);
	       		 	Statement statement=ast.newExpressionStatement(invokeAsList);
	       		 	
					if(SystematicallySurroundCall){ //||unexpectedException!=null)			    	
				    	TryStatement tryStatement=ast.newTryStatement(); 
				    	
			    		Class except=Exception.class;			    	
				    	tryStatement.catchClauses().add(getCatchClause(except,ast,false));
					    	
				    	Block b =ast.newBlock();
				    	b.statements().add(statement);
				    	tryStatement.setBody(b);

				    	returnList.add(tryStatement);
				    }else
				    	returnList.add(statement);
	       		 	//returnList.add(statement);
	        	}
	        }
	        //aCreation.setInitializer(aInitializer);
    	}
        else{

	        for(int i=0;i<length;i++){
	        	if(!toInsert[i]) 
	        		continue;
	        	
	        	invokeAsList=ast.newMethodInvocation();
	        	if(elements[i].getObject()==null){
//	        		Type TypeName =getType2UseInJunitClass(elements[i].getClazz(),ast);
//	        		CastExpression ce=ast.newCastExpression();
//	        		ce.setType(TypeName);
//	        		ce.setExpression(ast.newNullLiteral());	        		
	        		invokeAsList.arguments().add(ast.newNullLiteral());
	        	}else{
		        	String newVarName=newVarNameContainer+"C"+(i+1);
			    	returnList.addAll(elements[i].getStatements(ast,newVarName,""));
	       		 	invokeAsList.arguments().add(ast.newSimpleName(newVarName));
	        	}
	        	invokeAsList.setExpression(ast.newSimpleName(newVarNameContainer));
	        	invokeAsList.setName(ast.newSimpleName("add"));       		 	      		 	
       		 	Statement statement=ast.newExpressionStatement(invokeAsList);
				if(SystematicallySurroundCall){ //||unexpectedException!=null)			    	
			    	TryStatement tryStatement=ast.newTryStatement(); 
			    	
		    		Class except=Exception.class;			    	
			    	tryStatement.catchClauses().add(getCatchClause(except,ast,false));
				    	
			    	Block b =ast.newBlock();
			    	b.statements().add(statement);
			    	tryStatement.setBody(b);

			    	returnList.add(tryStatement);
			    }else
			    	returnList.add(statement);
       		 	//returnList.add(statement);
    		}
        }
				
		return returnList;
	}

	@Override
	public Object getObject(){		
		Collection c=(Collection) container.getObject();
		if(c==null)
			return c;
		try{
			c.clear();
		}catch(Exception e){
			
		}
		for(int i=0;i<length;i++)
			//if(elements[i].getObject()!=null)
				try {
					c.add(elements[i].getObject());
				} catch (Exception e) { 
					toInsert[i]=false;
				}
		
		object=c;
		return c;		
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
				if(!toInsert[i])
					continue;
				str+=elements[i].toString();
				
				if(i<length-1)
					str+=",";
			}
		str+="]";
		
		return str;
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
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		// TODO Auto-generated method stub
		return false;
	}
}
