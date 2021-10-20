package csbst.generators.atomic;

import java.util.Random;

import csbst.generators.AbsractGenerator;
import csbst.testing.JTE;

public class LongGenerator extends AbstractPrimitive<Long>{
	
	public LongGenerator(AbsractGenerator parent,Class cls) {
		super(parent, cls);	
		absolutuBound=Long.MAX_VALUE;
		absolutlBound=Long.MIN_VALUE;
		uBound=absolutuBound;
		lBound=absolutlBound;
		this.generateRandom();
	}
	
	@Override
	public void setObject(Object obj){
		Long v=(Long)obj;
		super.setObject(v);
		//if(v>uBound || v<lBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}
	
	@Override
	public void setlBound(Long l){
		lBound=l;
		//if(absolutlBound>lBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}

	@Override
	public void setuBound(Long u){
		uBound=u;
		//if(absolutuBound<uBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}
	
	@Override
	public void generateRandom() {
		Random random=new Random();
		generateRandom(random);
	}


	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false;
		returnValue=(gene instanceof LongGenerator);				
		returnValue=returnValue &&(clazz.equals(gene.getClazz()));		
		return returnValue;
	}
	
	public void generateRandom(Random random) {
		if(random.nextInt(100)<SEEDING_NULL_PROBABILITY && !clazz.equals(long.class)){
			this.setObject(null);
			return;
		}
		int probability=random.nextInt(100);
		if((probability<SEEDING_MIN_PROBABILITY &&  JTE.litteralConstantAnalyser.getLongConstants().size()>SEEDING_MIN_NUMBER) 
				|| (probability<SEEDING_MAX_PROBABILITY &&  JTE.litteralConstantAnalyser.getLongConstants().size()>SEEDING_MAX_NUMBER)){
			int index =random.nextInt(JTE.litteralConstantAnalyser.getLongConstants().size());
			
			//object=JTE.litteralConstantAnalyser.getCharacterConstants().get(index);
			this.setObject((Long)JTE.litteralConstantAnalyser.getLongConstants().get(index));
			return;
		}
		
		if(random.nextInt(100)<90){
			long range=201;
			this.setObject((long) (-100+random.nextInt((int) range)));
		}else
		{
			
			long randomValue = random.nextLong();	
			if(random.nextInt()<50)
				randomValue*=-1;
			this.setObject((Long)randomValue);
		}
		object=getObject();
	}

	@Override
	public int hashCode() {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();
        //hash =0;// ( hash << 1 | hash >>> 31) ^(Integer)this.getObject();
        return hash;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}	
	
	@Override
    public void mutate(){
		generateRandom();    
	}
	
	@Override
	public Object clone(){
		LongGenerator newGene=new LongGenerator(parent,clazz);
		if(object!=null)
			newGene.object=((Long)this.object).longValue();
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
		return ((Long)getObject()).toString()+"L";
	}
}