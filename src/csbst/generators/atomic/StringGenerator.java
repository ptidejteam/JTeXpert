package csbst.generators.atomic;

//import org.apache.commons.lang3.RandomStringUtils;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import csbst.generators.AbsractGenerator;
import csbst.testing.JTE;
import csbst.utils.RandomStringGenerator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.URL;
//import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.StringEscapeUtils;

public class StringGenerator  extends AbsractGenerator {
	public final int MAX_LENGTH=255;
	public final int AVG_LENGTH=50;
	private int length=0;
	private final boolean isFixedSize;
	//private String
	//private Gene[] elements;
	static String vectorAsConcatenatedString = ""; 
	static{
		for (Character c : JTE.litteralConstantAnalyser.getCharacterConstants())
			if(RandomStringGenerator.alphabet.indexOf(""+c)>0
					||RandomStringGenerator.capAlpha.indexOf(""+c)>0
					||RandomStringGenerator.num.indexOf(""+c)>0
					||RandomStringGenerator.specialCharacters.indexOf(""+c)>0)
		    vectorAsConcatenatedString += c;
	}
	
	public StringGenerator(AbsractGenerator parent,int l) {
		super(parent, String.class);
		length=l;	
		if(l!=0)
			isFixedSize=true; 
		else
			isFixedSize=false;
		clazz=String.class;
		generateRandom();
	}


	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false;
		returnValue=(gene instanceof StringGenerator);				
		returnValue=returnValue &&(clazz.equals(gene.getClazz()));		
		return returnValue;
	}

	
	@Override
	public void generateRandom() {
		
		//TODO
		//insert a generator of dates
		//insert a generator of paths (directories, files, ...)
		//
//		String vectorAsConcatenated = ""; 
//		for (Character c : JTE.litteralConstantAnalyser.getCharacterConstants())
//			if(RandomStringGenerator.alphabet.indexOf(""+c)>0
//				||RandomStringGenerator.capAlpha.indexOf(""+c)>0
//				||RandomStringGenerator.num.indexOf(""+c)>0
//				||RandomStringGenerator.specialCharacters.indexOf(""+c)>0)
//		    vectorAsConcatenated += c;
		
//		if(true){
//			object= vectorAsConcatenated;
//			return;
//		}
		Random rand=new Random();
		if(rand.nextInt(100)<SEEDING_NULL_PROBABILITY ){ //&& !clazz.equals(short.class)
			this.setObject(null);
			length=0;
			object=null;
			return;
		}
		
		
//		object="/Users/abdelilahsakti/PHD2011/SBST2015/benchmarks/jedit-5.0.0/dist/classes/core/org/gjt/sp/jedit/EditPlugin.class";
//		length="/Users/abdelilahsakti/PHD2011/SBST2015/benchmarks/jedit-5.0.0/dist/classes/core/org/gjt/sp/jedit/EBPlugin.class".length();
//		if(true)return;
		
		
		if(!isFixedSize){
			///Users/abdelilahsakti/PHD2011/SBST2015/benchmarks/jedit-5.0.0/dist/classes/core/org/gjt/sp/jedit
			//seed a constant
			rand=new Random();
			int probability=rand.nextInt(100);			
			if((probability<SEEDING_MIN_PROBABILITY &&  JTE.litteralConstantAnalyser.getStringConstants().size()>SEEDING_MIN_NUMBER)
					||(probability<SEEDING_MAX_PROBABILITY &&  JTE.litteralConstantAnalyser.getStringConstants().size()>SEEDING_MAX_NUMBER)){
				//System.out.println("From litteralConstant");
				int index =rand.nextInt(JTE.litteralConstantAnalyser.getStringConstants().size());
				
				object=JTE.litteralConstantAnalyser.getStringConstants().get(index);	
				length=JTE.litteralConstantAnalyser.getStringConstants().get(index).length();
				return;
			}
			
			//generate a string from object
			rand=new Random();
			probability=rand.nextInt(100);			
			if((probability<SEEDING_MIN_PROBABILITY &&  JTE.litteralConstantAnalyser.getStringConstants().size()>SEEDING_MIN_NUMBER)
					||(probability<SEEDING_MAX_PROBABILITY &&  JTE.litteralConstantAnalyser.getStringConstants().size()>SEEDING_MAX_NUMBER)){
				//System.out.println("From Objects");
				ObjectGenerator newObj=new ObjectGenerator(this);
				//newObj.generateRandom();
				if(newObj.getObject()!=null){
					try{
						String str=newObj.getObject().toString();
						if(str.contains("@") && str.contains(".")){
							//System.out.println(str);
							String ss=str.substring(str.indexOf("@")+1);
							long ll=Long.parseLong(ss, 16);
							//System.out.println(ll);
							if(ll>0){
								generateRandom();
								return;
							}
						}
						object=	str;
						length=str.length();
						return;
					}catch(Exception e){
						//si l objet ne dispose pas de la methode tostring ou celle-la produit une erreur
					}
				}
			}
			//generate an environment constant
			//
			probability=rand.nextInt(100);			
			if((probability<SEEDING_MIN_PROBABILITY &&  JTE.litteralConstantAnalyser.getStringConstants().size()>SEEDING_MIN_NUMBER)
					||(probability<SEEDING_MAX_PROBABILITY &&  JTE.litteralConstantAnalyser.getStringConstants().size()>SEEDING_MAX_NUMBER)){
				//System.out.println("From litteralConstant");
				int index =rand.nextInt(JTE.litteralConstantAnalyser.getStringConstants().size());
				
				String token=JTE.litteralConstantAnalyser.getStringConstants().get(index);
				//find a file or path that contains this constant
				List<String> files=null;
				 try {
					 files=getListFileDir(token);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 if(files!=null && files.size()>0){
					 rand=new Random();
					 int ind=rand.nextInt(files.size());
					 String s=files.get(ind);
					 object=s;
					 length=s.length();
					 return;
				 }
			}
		}
		
		rand=new Random();
		int probability=rand.nextInt(100);
		if(probability<5)
			length=0;
		else
			if(random.nextInt(100)<70)//short string
				length=rand.nextInt(AVG_LENGTH);
			else
				length=rand.nextInt(MAX_LENGTH);
		//randomly generate a string
		String str=new String();
		RandomStringGenerator randStr;
		rand=new Random();
		probability=rand.nextInt(100);
		if((probability<SEEDING_MIN_PROBABILITY &&  JTE.litteralConstantAnalyser.getCharacterConstants().size()>SEEDING_MIN_NUMBER)
				|| (probability<SEEDING_MAX_PROBABILITY &&  JTE.litteralConstantAnalyser.getCharacterConstants().size()>SEEDING_MAX_NUMBER)){
			//int index =random.nextInt(JTE.litteralConstantAnalyser.getCharacterConstants().size());
			
			//object=JTE.litteralConstantAnalyser.getCharacterConstants().get(index);
			//this.setObject((Character)JTE.litteralConstantAnalyser.getCharacterConstants().get(index));
			
			//seed strings generate from characters that are extracted from the source code
			//System.out.println("From Characters");

			
			rand=new Random();
			randStr=new RandomStringGenerator(length,rand.nextBoolean(),rand.nextBoolean(),rand.nextBoolean(),
					rand.nextBoolean(),rand.nextBoolean(),rand.nextBoolean(), vectorAsConcatenatedString);
		}else{	
			//System.out.println("From Random");
			rand=new Random();
			randStr=new RandomStringGenerator(length,rand.nextBoolean(),rand.nextBoolean(),rand.nextBoolean(),
				rand.nextBoolean(),rand.nextBoolean(),rand.nextBoolean());
		}

		String newString=randStr.getRandomString();
		newString=StringEscapeUtils.escapeJava(newString);
		object=newString;	
	}

	
    public static List<String> getListFileDir(String token)throws Exception{
    	List<String> pathList=new ArrayList<String>();
    	if(JTE.classPath==null || JTE.classPath.length==0)
    		return pathList;
    	
    	
    	//JTE.classPath;
    	Random rand=new Random();
		int index=rand.nextInt(JTE.classPath.length);
    	File path=new File(JTE.classPath[index]);
    	if(path==null)
    		return pathList;
    	
    	if(!path.isDirectory()){
    		pathList.add(path.getAbsolutePath());
    		return pathList;
    	}
        File[] files = path.listFiles();
        for(int i=0;i<files.length;i++){
        	if(files[i].getAbsolutePath().contains(token))
        		pathList.add(files[i].getAbsolutePath());
            if(files[i].isDirectory()){
            	rand=new Random();
        		int proba=rand.nextInt(100);
        		if(proba<50)
        			pathList.addAll(getListFileDir(token));
            }
        }
        return pathList;  
    }
	
	@Override
	public void mutate() {
		//mutation probability
		//int muProp=random.nextInt(99);
		
		//if((muProp<=84 && this.getFitness().getBD()==0)||(muProp>84 && this.getFitness().getBD()!=0))
		//	 return;
		
		///int muConProp=random.nextInt(99);

		generateRandom();
//		if(!isFixedSize && muConProp<50){
//			generateRandom();
//			return;
//		}
//		
//		if(length>0){
//			int mutPb=100/length;
//			for(int i=0;i<length;i++)
//				if(random.nextInt(100)<=mutPb){
//					String str=(String) object;
//					str.toCharArray()[i]= (char) random.nextInt(Character.MAX_VALUE);
//				}
//		}
	}

	@Override
	public Object clone() {
		StringGenerator newStr=new StringGenerator(parent,length);		
		newStr.clazz=this.clazz;;
		newStr.variableBinding=this.variableBinding;
		newStr.fitness=this.fitness;
		newStr.object=this.object;
		newStr.seed=this.seed;
		newStr.random=this.random;

		return newStr;
	}
	
	public String toHex(String arg) {
	    return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
	}
	
	@Override
	public Object getObject(){
		String s=(String)object;
		if(s!=null)
			s=StringEscapeUtils.escapeJava(s);
		return s;		
	}
	
	@Override
	public List<Statement> getStatements(AST ast, String varName, String pName) {
		List<Statement>returnList=new ArrayList<Statement>();
		
		VariableDeclarationFragment string=ast.newVariableDeclarationFragment();
  		string.setName(ast.newSimpleName(varName));
  		 if(getObject()!=null){
  			 ClassInstanceCreation classInstance= ast.newClassInstanceCreation();
  			 classInstance.setType(ast.newSimpleType(ast.newSimpleName(clazz.getSimpleName())));   
		    StringLiteral sl=ast.newStringLiteral();
		    String str =(String)getObject();
		    str =str.replace("\\", "\\\\");
//		    for(int i=0;i<str.length();i++){
//		    	Character c=str.toCharArray()[i];
//		    	if(c.isSurrogate(c)){
//		    		//String s=String.format("\\u%04x", (int) c);
//		    		String unicode="\'"+String.format("\\u%04x", (int) c)+"\'";
//		    		str=str.replaceAll(""+c, unicode);
//		    	}
//		    }
		    sl.setLiteralValue(str);//((String)object).replace("\\", "\\\\"));
		    classInstance.arguments().add((sl));
		    string.setInitializer(classInstance);
	    }else{
	    	NullLiteral nl=ast.newNullLiteral();
		    string.setInitializer(nl);
	    }
	    
		VariableDeclarationStatement stringStmt = ast.newVariableDeclarationStatement(string);
		stringStmt.setType(ast.newSimpleType(ast.newSimpleName(clazz.getSimpleName())));
	
		returnList.add(stringStmt);
		return returnList;
	}

	@Override
	public int hashCode() {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();

        hash = ( hash << 1 | hash >>> 31 );
        //if(parameters!=null)
//        for(int x=0;x<length;x++)
//        	if(elements[x]!=null)
//        		hash = ( hash << 1 | hash >>> 31 ) ^ elements[x].hashCode();

        return 0;
	}

	@Override
	public String toString(){	
		
		return "String";//(String) object;
	}
	
}
