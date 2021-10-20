package csbst.generators.atomic;

//import org.apache.commons.lang3.RandomStringUtils;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import csbst.generators.AbsractGenerator;
import csbst.generators.dynamic.AbstractDynamicGenerator;
import csbst.generators.dynamic.InstanceGenerator;
import csbst.generators.dynamic.MethodGenerator;
import csbst.testing.JTE;
import csbst.utils.FileEditor;
import csbst.utils.ClassLoaderUtil;
import csbst.utils.RandomStringGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClassGenerator  extends AbsractGenerator {
	//private AbsractGenerator actualGenarator;
	
	public ClassGenerator(AbsractGenerator parent) {
		super(parent, Class.class);
		generateRandom();
	}


	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false; 		
		return returnValue;
	}

	
	@Override
	public void generateRandom() {


		Random rand=new Random();
		if(rand.nextInt(100)<SEEDING_NULL_PROBABILITY ){ //&& !clazz.equals(short.class)
			this.setObject(null);
			object=null;
			return;
		}
		
		Random rand1=new Random();
		int proba =rand1.nextInt(100);
//		System.out.println();
//		System.out.println("Start generating Object"+ System.currentTimeMillis());
		
		if(proba<50  || JTE.litteralConstantAnalyser.getCastConstants().size()<1
				//||(!(parent instanceof InstanceGenerator) && !(parent instanceof MethodGenerator))
				||JTE.litteralConstantAnalyser.getCastConstants().size()<1){
			if(proba<20){
				object=InstanceGenerator.DefaultGenericType;
				return;
				//
			}
			else{
				Random rand2=new Random();
				int index =rand2.nextInt(InstanceGenerator.defaultClassesSet.size());
				object=InstanceGenerator.defaultClassesSet.get(index);
				return;
			}
		}
		else{
			Random rand3=new Random();
			int index =rand3.nextInt(JTE.litteralConstantAnalyser.getCastConstants().size());			
			String clsName="";
			clsName=ClassLoaderUtil.toCanonicalName(JTE.litteralConstantAnalyser.getCastConstants().get(index).getBinaryName());
				
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
					object=InstanceGenerator.defaultClassesSet.get(index2);
					return;
				} else{
					object=cls;
					return;
				}
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				JTE.litteralConstantAnalyser.getCastConstants().remove(index);
				//generateRandom() ;
				Random rand4=new Random();
				int index3 =rand4.nextInt(InstanceGenerator.defaultClassesSet.size());			
				object=InstanceGenerator.defaultClassesSet.get(index3);
				return;
			}
			
		}

	}

	@Override
	public void mutate() {
		generateRandom();
	}

	@Override
	public Object clone() {
		ClassGenerator newObj=new ClassGenerator(parent);		
		newObj.clazz=this.clazz;;
		newObj.variableBinding=this.variableBinding;
		newObj.fitness=this.fitness;
		newObj.object=this.object;
		newObj.seed=this.seed;
		newObj.random=this.random;
		return newObj;
	}
	
	@Override
	public List<Statement> getStatements(AST ast, String varName, String pName) {
		//create a variable declaration
		List<Statement>returnList=new ArrayList<Statement>();
		VariableDeclarationFragment varDec=ast.newVariableDeclarationFragment();		
		varDec.setName(ast.newSimpleName(varName+pName));
		
		VariableDeclarationStatement varDecStat;
		if(object!=null){
			//System.out.println(((Class)object).getName());
			Type TypeName =getType2UseInJunitClass((Class)object,ast);
			//Type clsName=ASTEditor. ASTEditor.generateQualifiedName(((Class)object).getCanonicalName(),ast);
			TypeLiteral classLiteral=ast.newTypeLiteral();//ast.newQualifiedName(clsName,ast.newSimpleName("class"));
			classLiteral.setType(TypeName);
    		varDec.setInitializer(classLiteral);
		}else{
			NullLiteral classLiteral=ast.newNullLiteral();
			varDec.setInitializer(classLiteral);
		}
		
		varDecStat = ast.newVariableDeclarationStatement(varDec);
		varDecStat.setType(ast.newSimpleType(ast.newSimpleName("Class")));


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
		if(object==null)
			return "NULL";
		return object.toString();
	}
}
