package csbst.generators.containers;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
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
import java.util.Map;
import java.util.Random;

import csbst.generators.AbsractGenerator;
import csbst.generators.dynamic.InstanceGenerator;
import csbst.utils.FileEditor;

public class MapGenerator  extends AbsractGenerator {
	public static final int maxLength=5;
	protected int length=0;
	protected boolean isFixedSize;
	protected AbsractGenerator container; //Map
	protected Class containerType;
	protected boolean[] toInsert;
	
	protected AbsractGenerator[] keys;	
	protected Class keyType;
	protected Class cKeyType;
	protected AbsractGenerator[] values;	
	protected Class valueType;
	protected Class cValueType;
	public MapGenerator(AbsractGenerator parent, int l, Class containerType, Class keyType, Class valueType) {
		super(parent,containerType); 
		length=l;	
		if(l!=0)  
			isFixedSize=true;
		else 
			isFixedSize=false;
		
		this.keyType=keyType;
		this.valueType=keyType;
		this.containerType=containerType;
		
		generateRandom(); 
	} 
	
	@Override
	public void generateRandom() {
		container=createAdequateGene(this, containerType);
		container.generateRandom();
		
		if(container.getObject()==null){ 
			this.setObject(null);
			keys=null; 
			length=0;
			return;
		}
		cKeyType=keyType;
		cValueType=valueType;
		
		
		//find the correct type of elements according to the selected stub		
		Method put=null;
		Method allMethod[]=((InstanceGenerator)container).getStub().getMethods(); //.add //.get
		for(int i=0;i<allMethod.length;i++){
			if(allMethod[i].getName().equalsIgnoreCase("put")){
				put=allMethod[i];
				break;
			}
		}
			
					
		if(put!=null && !keyType.equals(Object.class) && !put.getParameterTypes()[0].isAssignableFrom(keyType) ){
			generateRandom();
			return;
		}
		
		if(put!=null && !valueType.equals(Object.class) && !put.getParameterTypes()[1].isAssignableFrom(valueType) ){
			generateRandom();
			return;
		}
			
			
		if( keyType.equals(Object.class)){
			if(put!=null)
				cKeyType=put.getParameterTypes()[0];
			else{
				//if(elementType.equals(Object.class)){
				Random rand=new Random(System.currentTimeMillis());
				int muProp=rand.nextInt(100);
				if(muProp<20){
					if(muProp<10){ 
						rand=new Random();
						int index =rand.nextInt(InstanceGenerator.defaultClassesSet.size());
						cKeyType=InstanceGenerator.defaultClassesSet.get(index);//InstanceGenerator.DefaultGenericType;//InstanceGenerator.defaultClassesSet.get(index);
					}
				}else
					cKeyType=InstanceGenerator.DefaultGenericType;
				//}
			}
		}else
			cKeyType=keyType;


		if( valueType.equals(Object.class)){
			if(put!=null)
				cValueType=put.getParameterTypes()[1]; 
			else{
				//if(elementType.equals(Object.class)){
				Random rand=new Random(System.currentTimeMillis());
				int muProp=rand.nextInt(100);
				if(muProp<20){
					if(muProp<10){ 
						rand=new Random();
						int index =rand.nextInt(InstanceGenerator.defaultClassesSet.size());
						cValueType=InstanceGenerator.defaultClassesSet.get(index);//InstanceGenerator.DefaultGenericType;//InstanceGenerator.defaultClassesSet.get(index);
					}
				}else
					cValueType=InstanceGenerator.DefaultGenericType;
				//}
			}
		}else
			cValueType=valueType; 
		
//		Method[] classMethods=container.getObject().getClass().getMethods();
//		Method putMeth=null;
//		int j=0; 
//		while((putMeth==null ) && j<classMethods.length){
//			if(classMethods[j].getName()=="put")
//				putMeth=classMethods[j];
//			j++; 
//		}
//		
//		if(putMeth!=null){
//			Class[] keyValue=putMeth.getParameterTypes();
//			cKeyType=keyValue[0];
//			cKeyType=keyValue[1];
//		}
		
		
//		if(cKeyType.equals(Object.class)){
//			Random rand=new Random();
//			int muProp=rand.nextInt(100);
//			if(muProp<90){
//				//rand=new Random();
//				//int index =rand.nextInt(InstanceGenerator.defaultClassesSet.size());
//				cKeyType=InstanceGenerator.DefaultGenericType;//InstanceGenerator.defaultClassesSet.get(index);
//			}
//		}
//		
//		 
//		if(cValueType.equals(Object.class)){ 
//			Random rand=new Random();
//			int muProp=rand.nextInt(100);
//			if(muProp<90){
//				rand=new Random();
//				//int index =rand.nextInt(InstanceGenerator.defaultClassesSet.size());
//				cValueType=InstanceGenerator.DefaultGenericType;// InstanceGenerator.defaultClassesSet.get(index);
//			}
//		}
		
		if(!isFixedSize){
			int probability=random.nextInt(100);
			if(probability<5)
				length=0;
			else{
				Random rand=new Random();
				length=rand.nextInt(maxLength);
			}
		}
		
		keys=new AbsractGenerator[length]; 
		values=new AbsractGenerator[length];
		toInsert=new boolean[length];
		
	
		for(int i=0;i<length;i++){
			toInsert[i]=true;
			keys[i]=createAdequateGene(this, cKeyType);
			while(keys[i].getObject()==null)
				keys[i].generateRandom();
			
			values[i]=createAdequateGene(this, cValueType);
			while(values[i].getObject()==null)
				values[i].generateRandom();
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

	        for(int i=0;i<length;i++){
	        	if(!toInsert[i])
	        		continue;
	        	String keyVarName=newVarNameContainer+"K"+(i+1);
		    	returnList.addAll(keys[i].getStatements(ast,keyVarName,""));
		    	String valueVarName=newVarNameContainer+"V"+(i+1);
		    	returnList.addAll(values[i].getStatements(ast,valueVarName,""));
		    	
		    	invokeAsList=ast.newMethodInvocation();
	   		 	invokeAsList.setExpression(ast.newSimpleName(newVarNameContainer));
	   		 	invokeAsList.setName(ast.newSimpleName("put"));       		 	
	   		 	invokeAsList.arguments().add(ast.newSimpleName(keyVarName));
	   		 	invokeAsList.arguments().add(ast.newSimpleName(valueVarName));
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
			}
				
		return returnList;
	}

	@Override
	public Object getObject(){		
		Map c=(Map) container.getObject();
		if(c==null) // || container.getObject()==null
			return c;
		
		//if(!c.isEmpty())
		try{
			c.clear();
		}	catch (Exception e) {
			;//pour java.util.Collections$UnmodifiableMap
		}
		
		//execution of a method ????
		//add has the same problme
		for(int i=0;i<length;i++)
			if(keys[i].getObject()!=null)
				try {
					c.put(keys[i].getObject(),values[i].getObject());
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
        if(keys==null)
        	return 0;
        for(int x=0;x<keys.length;x++)
        	if(keys[x]!=null)
        		hash = ( hash << 1 | hash >>> 31 ) ^ keys[x].hashCode();

        return hash;
	}

	@Override
	public String toString(){
		String str=new String();
		str="[";
		if(keys!=null)
			for(int i=0;i<keys.length;i++){
				if(!toInsert[i])
					continue;
				str+=keys[i].toString();
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
					keys[i].mutate();
		}
	}
	
	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		// TODO Auto-generated method stub
		return false;
	}
}
