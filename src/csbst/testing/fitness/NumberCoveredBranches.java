package csbst.testing.fitness;


import java.util.HashSet;
import java.util.Set;

import csbst.ga.ecj.TestCaseCandidate;
import csbst.testing.JTE;

public class NumberCoveredBranches extends TestingFitness  {
	//
	public static void maintainPathTrace(int branch,int className){
		;
	}
	public static void maintainPathTrace(int branch,String className){
		
		//System.out.println(className);
		if((JTE.className==null)||!JTE.className.equals(className))
			return;

		if(underEvaluationChromosome!=null){ // && underEvaluationGene.getUnexpectedException()==null
			underEvaluationChromosome.getCoveredBranches().add(branch);
		}

		if(!JTE.allCoveredBranchesTwice.contains(branch) 
				&& (underEvaluationChromosome!=null)){	 //&& underEvaluationFitness!=null)
				//&& underEvaluationGene.getUnexpectedException()==null
			//JTE.allCoveredBranches.add(branch);
			//if(underEvaluationChromosome!=null && underEvaluationFitness!=null){
			//underEvaluationFitness.setDistance(underEvaluationFitness.getDistance() + 1);
			//JTE.allCoveredBranches.add(branch);
			underEvaluationChromosome.setIsTestData(true);

			//JTE.branchesTarget.remove(branch);
//				underEvaluationChromosome.setIsTestData(true);
//			}
		}
	}
	
	@Override
	public double fitness() {
		return 1.0/(1.0+distance);
	}
	
	@Override
	public boolean isIdealFitness() {
		return JTE.allCoveredBranchesOnce.contains(JTE.currentTarget.getBranch());
	}
	
//	public static Set<Integer>allCoveredBranches=new HashSet<Integer>();
//	
//	public static void maintainPathTrace(int branch,int iteration){
//		//if(underEvaluationChromosome==null)
//		//	return;	
//		
//		if(underEvaluationFitness==null)
//			return;	
//		
//		TestingFitness.underEvaluationChromosome.getCoveredBranches().add(branch);		
//		if(!allCoveredBranches.contains(branch)){
//			TestingFitness.underEvaluationChromosome.setIsTestData(true);
//			allCoveredBranches.add(branch);
//			underEvaluationFitness.setDistance(underEvaluationFitness.getDistance() + 1);			
//			//Csbst.testDataSet.add(TestingFitness.underEvaluationChromosome);
//		}
//	}
//	
//	@Override
//	public double fitness() {
//		return 1.0/(1.0+distance);
//	}
//	
////	@Override
////	public boolean isIdealFitness() {
////		if(branchesTarget)
////		return Csbst.branchesTarget.isEmpty();
////	}
	
}
