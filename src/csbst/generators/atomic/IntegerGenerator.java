package csbst.generators.atomic;

import java.util.Random;

import csbst.generators.AbsractGenerator;
import csbst.testing.JTE;

public class IntegerGenerator extends AbstractPrimitive<Integer>{
	
	public IntegerGenerator(AbsractGenerator parent, Class cls) {
		super(parent, cls);	
		absolutuBound=Integer.MAX_VALUE;
		absolutlBound=Integer.MIN_VALUE;
		uBound=absolutuBound;
		lBound=absolutlBound;
		this.generateRandom();
	}
 
	@Override
	public void setObject(Object obj){
		Integer v=(Integer)obj;
		super.setObject(v);
		//if(v>uBound || v<lBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}
	
	@Override
	public void setlBound(Integer l){
		lBound=l;
		//if(absolutlBound>lBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}

	@Override
	public void setuBound(Integer u){
		uBound=u;
		//if(absolutuBound<uBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}
	
	@Override
	public void generateRandom() {
		Random random=new Random();
		generateRandom(random);
	}

	public void generateRandom(Random random) {	
		
		//System.out.println(JTE.litteralConstantAnalyser.getIntegerConstants());
		//null value
		if(random.nextInt(100)<SEEDING_NULL_PROBABILITY && !clazz.equals(int.class)){
			object=null;
			this.setObject(null);
			return;
		}
		int probability=random.nextInt(100);
		if((probability<SEEDING_MIN_PROBABILITY &&  JTE.litteralConstantAnalyser.getIntegerConstants().size()>SEEDING_MIN_NUMBER) 
				|| (probability<SEEDING_MAX_PROBABILITY &&  JTE.litteralConstantAnalyser.getIntegerConstants().size()>SEEDING_MAX_NUMBER)){
			int index =random.nextInt(JTE.litteralConstantAnalyser.getIntegerConstants().size());
			//System.out.println(JTE.litteralConstantAnalyser.getIntegerConstants().get(index));
			//object=JTE.litteralConstantAnalyser.getCharacterConstants().get(index);
			this.setObject((Integer)JTE.litteralConstantAnalyser.getIntegerConstants().get(index));
			return;
		}
		//small domaine.
		if(random.nextInt(100)<100){
			int range=2001;
			int val=(random.nextInt(range)-1000);//-100
			//System.out.println("selected value:****************************************:  "+val);
			this.setObject(val);
		}else{
			long range=(long)uBound-(long)lBound+1;
			if(Integer.MAX_VALUE >=range){
				this.setObject((lBound+random.nextInt((int)range)));
			}
			else{
				range=(long)uBound/2-(long)lBound/2;
				this.setObject((Integer)(lBound+random.nextInt((int)range)));
				if(random.nextBoolean())
					this.setObject((2*(Integer)this.getObject()));
			}
		}
		object=getObject();
	}

	@Override
	public int hashCode() {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();
        if(object==null)
        	return 1;
        
        hash = ( hash << 1 | hash >>> 31) ^(Integer)this.getObject();
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
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false;
		returnValue=(gene instanceof IntegerGenerator);				
		returnValue=returnValue &&(clazz.equals(gene.getClazz()));		
		return returnValue;
	}
	
	@Override
	public Object clone(){
		IntegerGenerator newGene=new IntegerGenerator(parent,clazz);
		if(object!=null){
			//System.out.println(this.object);
			newGene.object=((Integer)this.object).intValue();
		}
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
		return ((Integer)getObject()).toString();
	}
}
