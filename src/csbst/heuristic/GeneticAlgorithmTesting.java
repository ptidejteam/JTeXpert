package csbst.heuristic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import csbst.ga.ecj.TestCaseCandidate;
import csbst.ga.ecj.TestingState;
import csbst.testing.JTE;
import csbst.testing.fitness.NumberCoveredBranches;
import csbst.testing.fitness.TestingFitness;
import ec.EvolutionState;
import ec.Evolve;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
// implements Heuristic 
public class GeneticAlgorithmTesting{
	private int generations;
	public GeneticAlgorithmTesting (int generations){
		this.generations=generations;
	}
	
	
	public int run(String gaParametersFile){
		if(generations<=0)
			return 0;
		int currentGenNbr=0;
		try {
			TestingState state;
			ParameterDatabase parameters=new ParameterDatabase(new File(gaParametersFile));
			int nbrgeneration=generations/150;
			if(nbrgeneration<=0)
				nbrgeneration=1;
			//System.out.println(nbrgeneration);
			parameters.set(new Parameter("generations"), Integer.toString(nbrgeneration));
			if(JTE.seed!=0)
				parameters.set(new Parameter("seed.0"), Long.toString(JTE.seed));
			state = (TestingState) Evolve.initialize(parameters, 0);
			TestingFitness.evaluations=0;
			state.run(0);
			Evolve.cleanup(state);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return TestingFitness.evaluations;
	}
}
