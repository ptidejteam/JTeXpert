package csbst.generators.atomic;

//import org.apache.commons.lang3.RandomStringUtils;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import csbst.generators.AbsractGenerator;
import csbst.generators.dynamic.AbstractDynamicGenerator;
import csbst.generators.dynamic.InstanceGenerator;
import csbst.generators.dynamic.MethodGenerator;
import csbst.testing.JTE;
import csbst.utils.ClassLoaderUtil;
import csbst.utils.RandomStringGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObjectGenerator  extends AbsractGenerator {
	private AbsractGenerator actualGenarator;
	
	public ObjectGenerator(AbsractGenerator parent) {
		super(parent, Object.class);
		generateRandom();
	}


	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false; 
		returnValue=(gene instanceof ObjectGenerator);				
		returnValue=returnValue &&(clazz.equals(gene.getClazz()));		
		return returnValue;
	}

	
	@Override
	public void generateRandom() {
//		if(true) return ;
		//MethodGenerator c;
		Random rand1=new Random();
		int proba =rand1.nextInt(100);
//		System.out.println();
//		System.out.println("Start generating Object"+ System.currentTimeMillis());
		//||JTE.litteralConstantAnalyser.getCastConstants().size()<1
		
		if(proba<50  || JTE.litteralConstantAnalyser.getCastConstants().size()<1
				//||(!(parent instanceof InstanceGenerator) && !(parent instanceof MethodGenerator))
				){
			if(proba<20){
				actualGenarator=createAdequateGene(this,InstanceGenerator.DefaultGenericType);
				//
			}
			else{
				Random rand=new Random();
				int index =rand.nextInt(InstanceGenerator.defaultClassesSet.size());
				actualGenarator=createAdequateGene(this,InstanceGenerator.defaultClassesSet.get(index));
			}
				//InstanceGenerator.defaultClassesSet.get(index)
			actualGenarator.generateRandom();
		}
		else{
			Random rand=new Random();
			int index =rand.nextInt(JTE.litteralConstantAnalyser.getCastConstants().size());			
			String clsName="";
			if(JTE.litteralConstantAnalyser.getCastConstants().get(index)!=null 
					&& JTE.litteralConstantAnalyser.getCastConstants().get(index).getBinaryName()!=null)
				clsName=ClassLoaderUtil.toCanonicalName(JTE.litteralConstantAnalyser.getCastConstants().get(index).getBinaryName());
			else{
				JTE.litteralConstantAnalyser.getCastConstants().remove(index);
				//generateRandom() ;
				Random rand2=new Random();
				int index2 =rand2.nextInt(InstanceGenerator.defaultClassesSet.size());			
				actualGenarator=createAdequateGene(this,InstanceGenerator.defaultClassesSet.get(index2));
				actualGenarator.generateRandom();
				return;
			}
				
			Class cls=null;
			try {
				//clsName=ClassLoaderUtil.toCanonicalName(clsName);
				cls = JTE.magicClassLoader.loadClass(clsName); //ClassLoaderUtil.getClass(clsName);//
				if(cls==null 
						|| !AbstractDynamicGenerator.isAccessible(cls)
						|| cls.equals(Object.class)){
					JTE.litteralConstantAnalyser.getCastConstants().remove(index);
					//generateRandom() ;
					Random rand2=new Random();
					int index2 =rand2.nextInt(InstanceGenerator.defaultClassesSet.size());			
					actualGenarator=createAdequateGene(this,InstanceGenerator.defaultClassesSet.get(index2));
					actualGenarator.generateRandom();
					return;
				} else{
					actualGenarator=createAdequateGene(this,cls);
					actualGenarator.generateRandom(); 
					if(actualGenarator.getObject()!=null){
						for(int i=0;i<20;i++)
							InstanceGenerator.defaultClassesSet.add(cls);
					}
					return;
				}
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				JTE.litteralConstantAnalyser.getCastConstants().remove(index);
				//generateRandom() ;
				Random rand3=new Random();
				int index3 =rand3.nextInt(InstanceGenerator.defaultClassesSet.size());			
				actualGenarator=createAdequateGene(this,InstanceGenerator.defaultClassesSet.get(index3));
				actualGenarator.generateRandom();
				//return;
				//e.printStackTrace();
			}
			
		}
		
//		System.out.println("Finish generating Object"+ System.currentTimeMillis()+ " class "+actualGenarator.getClazz());
		//object=actualGenarator.getObject();//.getRandomString();	
	}

	@Override
	public void mutate() {
		generateRandom();
	}

	@Override
	public Object clone() {
		ObjectGenerator newObj=new ObjectGenerator(parent);		
		newObj.clazz=this.clazz;;
		newObj.variableBinding=this.variableBinding;
		newObj.fitness=this.fitness;
		newObj.object=this.object;
		newObj.seed=this.seed;
		newObj.random=this.random;
		
		newObj.actualGenarator=(AbsractGenerator) this.actualGenarator.clone();
		return newObj;
	}
	
	@Override
	public Object getObject(){
		//if(true)return null;
		//System.out.println(actualGenarator.getObject());
		return actualGenarator.getObject();//actualGenarator;//object;		
	}
	
	@Override
	public List<Statement> getStatements(AST ast, String varName, String pName) {
		List<Statement>returnList=new ArrayList<Statement>();

		
		
		String objectName=varName+pName+"O0";
		returnList.addAll(actualGenarator.getStatements(ast, objectName, ""));
		
		VariableDeclarationFragment varDec=ast.newVariableDeclarationFragment();
		varDec.setName(ast.newSimpleName(varName));
		
		varDec.setInitializer(ast.newSimpleName(objectName));
		VariableDeclarationStatement varDecStat = ast.newVariableDeclarationStatement(varDec);
		
		Type TypeName1 =getType2UseInJunitClass(clazz,ast);
	    varDecStat.setType(TypeName1);
	    returnList.add(varDecStat);
		 
		return returnList;
	}

	@Override
	public int hashCode() {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();

        hash = ( hash << 1 | hash >>> 31 );

        return 0;
	}

	@Override
	public String toString(){	
		if(actualGenarator==null)
			return "NULL";
		return actualGenarator.toString();
	}
}
