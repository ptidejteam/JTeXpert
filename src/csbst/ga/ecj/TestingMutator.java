package csbst.ga.ecj;

import csbst.generators.dynamic.MethodGenerator;
import ec.vector.*;
import ec.*;
import ec.util.*;

public class TestingMutator extends BreedingPipeline
  {
  public static final String P_OURMUTATION = "testing-mutation";
  public Parameter defaultBase() { return VectorDefaults.base().push(P_OURMUTATION); }
  
  public static final int NUM_SOURCES = 1;
  // Return 1 -- we only use one source
  public int numSources() { return NUM_SOURCES; }

  public int produce(final int min, 
      final int max, 
      final int start,
      final int subpopulation,
      final Individual[] inds,
      final EvolutionState state,
      final int thread) 
      {
      // grab individuals from our source and stick 'em right into inds.
      // we'll modify them from there
      int n = sources[0].produce(min,max,start,subpopulation,inds,state,thread);


      // should we bother?
      if (!state.random[thread].nextBoolean(likelihood))
          return reproduce(n, start, subpopulation, inds, state, thread, false);  // DON'T produce children from source -- we already did


      // clone the individuals if necessary -- if our source is a BreedingPipeline
      // they've already been cloned, but if the source is a SelectionMethod, the
      // individuals are actual individuals from the previous population
      if (!(sources[0] instanceof BreedingPipeline))
          for(int q=start;q<n+start;q++)
              inds[q] = (Individual)(inds[q].clone());

      // Check to make sure that the individuals are IntegerVectorIndividuals and
      // grab their species.  For efficiency's sake, we assume that all the 
      // individuals in inds[] are the same type of individual and that they all
      // share the same common species -- this is a safe assumption because they're 
      // all breeding from the same subpopulation.

      if (!(inds[start] instanceof TestCaseCandidate)) // uh oh, wrong kind of individual
          state.output.fatal("OurMutatorPipeline didn't get an Chromosome." +
              "The offending individual is in subpopulation " + subpopulation + " and it's:" + inds[start]);
      ChromosomeSpecies species = (ChromosomeSpecies)(inds[start].species);

      
      // mutate 'em! species.minGene(i)
      for(int q=start;q<n+start;q++)
          {
    	  TestCaseCandidate i = (TestCaseCandidate)inds[q];
          	double mutp=1.0/i.getGenes().size();

	         double [] propa = new double[i.getGenes().size()];
	         for(int x=0;x<i.getGenes().size();x++)
	        		 propa[x]=mutp;
			for(int x=0;x<i.getGenes().size();x++)
				if (state.random[thread].nextBoolean(propa[x])){
					//if(i.getGenes().get(x) instanceof GeneMethod)					
					//	((GeneMethod)i.getGenes().get(x)).execute(i.getGenes().get(0).getObject());
					i.getGenes().get(x).mutate();
					//if(i.getGenes().get(x) instanceof GeneMethod)					
					//	((GeneMethod)i.getGenes().get(x)).execute(i.getGenes().get(0).getObject());
	             }         
			i.evaluated=false;
          }
      
      return n;
      }

  }
  
  
