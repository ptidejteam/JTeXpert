package csbst.testing;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Vector;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;


public class Path <T>implements Comparable<Path>{
	private double difficultyCoefficient; //is the sum of all branches' difficulty coefficients.
	private Vector<Branch> branches=new Vector<Branch>();
	//private T methCons;	//the constructor or the method that contains the branch
	private T entryPoint;
	private MethodDeclaration topPoint;
	//private boolean isAccessible;
	private TypeDeclaration clazz;
	
	public Path(){
		difficultyCoefficient=1;
	}
	
	public void add(Branch branch){
		branches.add(0,branch);
		difficultyCoefficient+=branch.getDC();
	}
	
	public boolean contains(int branch){
		//boolean exist=false;
		for(Branch b:branches)
			if(b.getID()==branch)
				return true;
		return false;
	}
	
	public void addAll(Path pathCaller) {
		branches.addAll(pathCaller.getBranches());
		difficultyCoefficient+=pathCaller.getDC();
	}
	
	public Vector<Branch> getBranches(){
		return branches;
	}
	
	public T getEntryPoint(){
		return entryPoint;
	}
	
	public void setEntryPoint(T entry){
		entryPoint=entry;
	}
	
	public double getDC(){
		return difficultyCoefficient;
	}
	
	public int compareTo(Path p){
		 double diff=this.difficultyCoefficient-p.getDC();
		 if(diff<0)
			 return -1;
		 else if(diff>0)
			 return 1;
		 else
			 return 0;
	}

	public int size(){
		return branches.size();
	}

	public MethodDeclaration getTopPoint() {
		return topPoint;
	}

	public void setTopPoint(MethodDeclaration topPoint) {
		this.topPoint = topPoint;
	}
}
