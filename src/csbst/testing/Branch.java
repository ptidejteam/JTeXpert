package csbst.testing;

public class Branch {
	private int branchID;
	private double difficultyCoefficient;
	private int level;	

	public Branch(int branch, double dc, int l){
		branchID=branch;
		difficultyCoefficient=dc;
		level=l;
	}
	
	public Branch(int branch){
		branchID=branch;
	}
	
	public double getDC(){
		return difficultyCoefficient;
	}

	
	public int getID(){
		return branchID;
	}
	
	public double getLevel(){
		return level;
	}
}
