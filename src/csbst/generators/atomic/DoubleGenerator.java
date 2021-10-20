package csbst.generators.atomic;

import java.util.Random;

import csbst.generators.AbsractGenerator;
import csbst.testing.JTE;

public class DoubleGenerator extends AbstractPrimitive<Double>{
	
	public DoubleGenerator(AbsractGenerator parent,Class cls) {
		super(parent,cls);
		absolutuBound=Double.MAX_VALUE;
		absolutlBound=Double.MIN_VALUE;
		uBound=absolutuBound;
		lBound=absolutlBound;
		this.generateRandom();
	}

	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false;
		returnValue=(gene instanceof DoubleGenerator);				
		returnValue=returnValue &&(clazz.equals(gene.getClazz()));		
		return returnValue;
	}
	
	@Override
	public void setObject(Object obj){
		Double v=(Double)obj;
		super.setObject(v);
		//if(v>uBound || v<lBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}
	
	@Override
	public void setlBound(Double l){
		lBound=l;
		//if(absolutlBound>lBound)
		//	throw new IllegalArgumentException("Parameter object is out of range");
	}

	@Override
	public void setuBound(Double u){
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
		if(random.nextInt(100)<SEEDING_NULL_PROBABILITY && !clazz.equals(double.class)){
			this.setObject(null);
			return;
		}
		
		int probability=random.nextInt(100);
		if((probability<SEEDING_MIN_PROBABILITY &&  JTE.litteralConstantAnalyser.getDoubleConstants().size()>SEEDING_MIN_NUMBER)
				||(probability<SEEDING_MAX_PROBABILITY &&  JTE.litteralConstantAnalyser.getDoubleConstants().size()>SEEDING_MAX_NUMBER)){
			int index =random.nextInt(JTE.litteralConstantAnalyser.getDoubleConstants().size());
			
			//object=JTE.litteralConstantAnalyser.getCharacterConstants().get(index);
			this.setObject((Double)JTE.litteralConstantAnalyser.getDoubleConstants().get(index));
			return;
		}
		
		if(random.nextInt(100)<90){
			double range=201D;
			this.setObject((Double) (-100.0+random.nextDouble()*range));
		}else
			this.setObject((Double) (lBound+random.nextDouble()*(uBound-lBound)));
		
		object=getObject();
	}

	@Override
	public int hashCode() {

        return 0;
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
		DoubleGenerator newGene=new DoubleGenerator(parent,clazz);
		if(object!=null)
			newGene.object=((Double)this.object).doubleValue();
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
		String value =((Double)getObject()).toString();
		if(!value.contains("Infinity"))
			value= value+"D";

		return value;
	}
}
