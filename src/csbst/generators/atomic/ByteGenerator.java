package csbst.generators.atomic;

import java.util.Random;

import csbst.generators.AbsractGenerator;
import csbst.testing.JTE;

public class ByteGenerator extends AbstractPrimitive<Byte>{
	
	public ByteGenerator(AbsractGenerator parent, Class cls) {
		super(parent,cls);
		//this.setClazz(byte.class);	
		absolutuBound=Byte.MAX_VALUE;
		absolutlBound=Byte.MIN_VALUE;
		uBound=absolutuBound;
		lBound=absolutlBound;
		this.generateRandom();
	}

	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		boolean returnValue=false;
		returnValue=(gene instanceof ByteGenerator);				
		returnValue=returnValue &&(clazz.equals(gene.getClazz()));		
		return returnValue;
	}
	
	@Override
	public void setObject(Object obj){
		Byte v=(Byte)obj;
		super.setObject(v);
	}
	
	@Override
	public void setlBound(Byte l){
		lBound=l;
	}

	@Override
	public void setuBound(Byte u){
		uBound=u;
	}
	
	@Override
	public void generateRandom() {
		Random random=new Random();
		generateRandom(random);
	}

	public void generateRandom(Random random) {
		if(random.nextInt(100)<SEEDING_NULL_PROBABILITY && !clazz.equals(byte.class)){
			this.setObject(null);
			return;
		}
		
		int probability=random.nextInt(100);
		if((probability<SEEDING_MIN_PROBABILITY &&  JTE.litteralConstantAnalyser.getByteConstants().size()>SEEDING_MIN_NUMBER)
				|| (probability<SEEDING_MAX_PROBABILITY &&  JTE.litteralConstantAnalyser.getByteConstants().size()>SEEDING_MAX_NUMBER)){
			int index =random.nextInt(JTE.litteralConstantAnalyser.getByteConstants().size());
			
			//object=JTE.litteralConstantAnalyser.getCharacterConstants().get(index);
			this.setObject((byte)JTE.litteralConstantAnalyser.getByteConstants().get(index));
			return;
		}
		
		byte[]possibleValue=new byte[1];
		random.nextBytes(possibleValue);
		this.setObject(possibleValue[0]);
		object=getObject();
	}

	@Override
	public int hashCode() {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();
        if(this.getObject()==null)
        	return hash;
        hash = ( hash << 1 | hash >>> 31) ^(Byte)this.getObject();
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
		ByteGenerator newGene=new ByteGenerator(parent,clazz);
		if(object!=null)
			newGene.object=((Byte)this.object).byteValue();
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
		return ((Byte)getObject()).toString();
	}
}
//
//
//GeneByte{
//	
//	public GeneByte() {
//		super();
//		this.setClazz(byte.class);	
//		absolutuBound=(byte) (Math.pow(2, 7)-1);
//		absolutlBound=(byte) (-1*Math.pow(2, 7));
//		uBound=absolutuBound;
//		lBound=absolutlBound;
//	}
//	
//	public static void main(String[] args){		
//		GeneByte gpi=new GeneByte();
//		gpi.generateRandom();
//	}
//	@Override
//	public void setObject(Object obj){
//		Byte v=(Byte)obj;
//		super.setObject(v);
//	}
//	
//	@Override
//	public Object getObject(){
//		return (Byte)object;
//	}
//}
