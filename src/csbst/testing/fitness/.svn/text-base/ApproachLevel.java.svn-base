package csbst.testing.fitness;


import java.util.Vector;

import org.eclipse.jdt.core.dom.IVariableBinding;

import csbst.testing.JTE;
import csbst.testing.Path;
import ec.Fitness;
import ec.util.Parameter;

public class ApproachLevel extends TestingFitness{
	private double DC;		//standard branch
	private double DL;		//with path in branch
	private double level;   //with path in branch
	private double branchDistance;
	private int branchID;
	private int influence=1;
	private double maxSubBD=0;
	private double minSubBD=0;
	private Vector<ApproachLevel> branchFitness=new Vector<ApproachLevel>();//branches BD

	public static void maintainBranchDistance(int branch,int iteration, String expression){
		if(underEvaluationFitness==null)
			return;
		//use currentChromosome
		if(JTE.getCurrentPathTarget().contains(branch))
			try {
				
	//			underEvaluationFitness.setBD(branch,BranchDistance.getBranchDistance(expression));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public ApproachLevel(){
		branchDistance=0;
	}
	
	public ApproachLevel(double bd){
		branchDistance=bd;
	}

	
	public ApproachLevel(double bd, int bID){
		branchDistance=bd;
		branchID=bID;
	}

	public ApproachLevel(Path currentPathTarget,IVariableBinding iVariableBinding) {
		branchFitness=new Vector<ApproachLevel>();
		for(int i=0;i<currentPathTarget.size();i++){
			ApproachLevel tmpFit=null;//new ApproachLevel(0,currentPathTarget.getBranches().get(i).getID());
			if(iVariableBinding==null)
				tmpFit.influence=1;
			else{
				tmpFit.influence=JTE.getInfluence(iVariableBinding,tmpFit.getBranchID());
			}	
			branchFitness.add(tmpFit);
		}
			
	}
	
	public double getMinBD(){
		return minSubBD;
	}

	public double getMaxBD(){
		return maxSubBD;
	}
	
	public int getBranchID(){
		return branchID;
	}
	
	
	public boolean equals(ApproachLevel other){
		boolean areEqual=(DC==other.getDC());
		 areEqual=areEqual&&(branchDistance==other.getBD());
		 areEqual=areEqual&&(branchFitness.size()==other.getHeirarchy().size());
		 
		int i=0;
		while (areEqual && i<branchFitness.size()){
			areEqual=areEqual&&(branchFitness.get(i).equals(other.getHeirarchy().get(i)));
		}
			
		return areEqual;
	}

	public Vector<ApproachLevel> getHeirarchy(){
		return branchFitness;
	}
	public double getDC(){
		return DC;
	}
	
	public void setDC(double dc){
		DC=dc;
	}
	
	public double getBD(){
		return branchDistance;
	}
	
	public void setBD(double bd){
		branchDistance=bd;
	}
	
	public void setLevel(int l){
		level=l;
	}
	

	@Override
	public int compareTo(Object obj) {
		ApproachLevel fit=(ApproachLevel) obj;
		if(level>fit.getDC())
			return -1;
		else if (level<fit.getDC())
			return 1;
		else if (branchDistance>fit.getBD())
			return -1;
		else if (branchDistance<fit.getBD())
			return 1;
		else 
			return 0;
	}
	
	public double getNormalizedBD(){
		return branchDistance/(branchDistance+1);
	}
	
	public  double getNormalizedBDLog(){
    	return 1-1/(1+Math.log(1+branchDistance));//distance/(distance+1);//1-Math.pow(1.001, -1*distance);
    }
	
	
	public  double getNormalizedBDPow(){
    	return 1-Math.pow(1.001, -1*branchDistance);
    }

	
	public void evaluatePathFitness(){
		//TODO introduce the influence here
		for(ApproachLevel fit:branchFitness)
			this.setBD(this.getBD()+fit.getBD());
	}
	
	@Override
	public Parameter defaultBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double fitness() {
		return .0+level+getNormalizedBDLog();
	}

	@Override
	public boolean isIdealFitness() {
		return branchDistance==0;
	}

	@Override
	public boolean equivalentTo(Fitness _fitness) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean betterThan(Fitness _fitness) {
		
		return compareTo(_fitness)==1;
	}

	public void setBD(int bID, double branchDistance) {
		for(int i =0 ;i<branchFitness.size();i++) // PathFitness fit:branchFitness
			if(branchFitness.get(i).getBranchID()==bID){
				branchFitness.get(i).setBD(branchDistance);
				branchFitness.get(i).setLevel(branchDistance==0?0:branchFitness.size()-i);
				if(maxSubBD<branchFitness.get(i).getBD())
					maxSubBD=branchFitness.get(i).getBD();
				if(minSubBD>branchFitness.get(i).getBD())
					minSubBD=branchFitness.get(i).getBD();
			}	
	}
	
	@Override
	public String toString(){
		String str=new String();
		str="branchID: "+branchID+"= "+fitness()+ " [";
		for(int i=0;i<branchFitness.size();i++){
			str+=branchFitness.get(i).toString();
			if(i<branchFitness.size()-1)
				str+=", ";
		}
		str+="]";
		return str;
	}
}
