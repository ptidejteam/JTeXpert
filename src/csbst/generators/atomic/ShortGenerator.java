package csbst.generators.atomic;

import java.util.Random;

import csbst.generators.AbsractGenerator;
import csbst.testing.JTE;

public class ShortGenerator extends AbstractPrimitive<Short>{
	
	public ShortGenerator(AbsractGenerator parent,Class cls) {
		super(parent,cls);
		//this.setClazz(short.class);	
		absolutuBound=Short.MAX_VALUE;
		absolutlBound=Short.MIN_VALUE;
		uBound=absolutuBound;
		lBound=absolutlBound;
		this.generateRandom();
	}


	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false;
		returnValue=(gene instanceof ShortGenerator);				
		returnValue=returnValue &&(clazz.equals(gene.getClazz()));		
		return returnValue;
	}

	@Override
	public void setObject(Object obj){
		Short v=(Short)obj;
		super.setObject(v);
	}
	
	@Override
	public void setlBound(Short l){
		lBound=l;
	}

	@Override
	public void setuBound(Short u){
		uBound=u;
	}
	
	@Override
	public void generateRandom() {
		Random random=new Random();
		generateRandom(random);
	}

	public void generateRandom(Random random) {
		if(random.nextInt(100)<SEEDING_NULL_PROBABILITY && !clazz.equals(short.class)){
			this.setObject(null);
			return;
		}
		
		int probability=random.nextInt(100);
		if((probability<SEEDING_MIN_PROBABILITY &&  JTE.litteralConstantAnalyser.getShortConstants().size()>SEEDING_MIN_NUMBER) 
				|| (probability<SEEDING_MAX_PROBABILITY &&  JTE.litteralConstantAnalyser.getShortConstants().size()>SEEDING_MAX_NUMBER)){
			int index =random.nextInt(JTE.litteralConstantAnalyser.getShortConstants().size());
			
			//object=JTE.litteralConstantAnalyser.getCharacterConstants().get(index);
			this.setObject((short)JTE.litteralConstantAnalyser.getShortConstants().get(index));
			return;
		}
		
		if(random.nextInt(100)<50){
			short range=201;
			this.setObject((short) (-100+random.nextInt(range)));
		}else{
			int range=((int)uBound-(int)lBound+1);
			int value=random.nextInt(range);
			while (value>Short.MAX_VALUE || value<Short.MIN_VALUE)
				value=random.nextInt(range);
			
			Short val=(short) value;
			this.setObject(val);
		}
		object=getObject();	
	}

	@Override
	public int hashCode() {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();
        if(object==null)
        	return hash;
        
        hash = ( hash << 1 | hash >>> 31) ^(Short)this.getObject();
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
		ShortGenerator newGene=new ShortGenerator(parent,clazz);
		if(object!=null)
			newGene.object=((Short)this.object).shortValue();
		newGene.fitness=this.fitness;
		newGene.clazz=this.clazz;
		newGene.variableBinding=this.variableBinding;
		newGene.random=this.random;
		newGene.seed=this.seed;
		
		return newGene;
	}

	@Override
	public String toString(){
		if(object==null)
			return "null";
		return ((Short)getObject()).toString();
	}
}
//
//
//GeneShort{
//	
//	public GeneShort() {
//		super();
//		this.setClazz(short.class);	
//		absolutuBound=(short) (Math.pow(2, 7)-1);
//		absolutlBound=(short) (-1*Math.pow(2, 7));
//		uBound=absolutuBound;
//		lBound=absolutlBound;
//	}
//	
//	public static void main(String[] args){		
//		GeneShort gpi=new GeneShort();
//		gpi.generateRandom();
//	}
//	@Override
//	public void setObject(Object obj){
//		Short v=(Short)obj;
//		super.setObject(v);
//	}
//	
//	@Override
//	public Object getObject(){
//		return (Short)object;
//	}
//}
