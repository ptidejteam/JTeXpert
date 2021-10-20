package csbst.ga.ecj;

import ec.*;
import java.io.*;


import ec.steadystate.SteadyStateStatisticsForm;
import ec.util.*;
import ec.eval.*;

public class TestingStatistic extends Statistics  implements SteadyStateStatisticsForm// implements ProvidesBestSoFar
  {
  public Individual[] getBestSoFar() { return best_of_run; }
      
  /** log file parameter */
  public static final String P_STATISTICS_FILE = "file";

  /** The Statistics' log */
  public int statisticslog;

  /** compress? */
  public static final String P_COMPRESS = "gzip";

  public static final String P_FULL = "gather-full";

  public boolean doFull;

  public Individual[] best_of_run;
  public long lengths[];

  // timings
  public long lastTime;
  
  // usage
  public long lastUsage;
  
  public TestingStatistic() { /*best_of_run = null;*/ statisticslog = 0; /* stdout */ }

  public void setup(final EvolutionState state, final Parameter base)
      {
      super.setup(state,base);
      File statisticsFile = state.parameters.getFile(
          base.push(P_STATISTICS_FILE),null);

      if (statisticsFile!=null) try
                                    {
                                    statisticslog = state.output.addLog(statisticsFile,
                                        !state.parameters.getBoolean(base.push(P_COMPRESS),null,false),
                                        state.parameters.getBoolean(base.push(P_COMPRESS),null,false));
                                    }
          catch (IOException i)
              {
              state.output.fatal("An IOException occurred while trying to create the log " + statisticsFile + ":\n" + i);
              }
      doFull = state.parameters.getBoolean(base.push(P_FULL),null,false);
      }


  public void preInitializationStatistics(final EvolutionState state)
      {
      super.preInitializationStatistics(state);
      
      //if (doFull) 
          {
          Runtime r = Runtime.getRuntime();
          lastTime = System.currentTimeMillis();
          lastUsage = r.totalMemory() - r.freeMemory();
          }
      }
  
  public void postInitializationStatistics(final EvolutionState state)
      {
      super.postInitializationStatistics(state);
      
      // set up our best_of_run array -- can't do this in setup, because
      // we don't know if the number of subpopulations has been determined yet
      best_of_run = new Individual[state.population.subpops.length];
      
      // print out our generation number
      state.output.print("iteration,evaluations,runtime[s],fitness[MIN] \r", statisticslog);
      }

  public void preBreedingStatistics(final EvolutionState state)
      {
      super.preBreedingStatistics(state);
      }

  public void postBreedingStatistics(final EvolutionState state) 
      {
      super.postBreedingStatistics(state);
      state.output.print("" + (state.generation + 1) + ", ", statisticslog); // 1 because we're putting the breeding info on the same line as the generation it *produces*, and the generation number is increased *after* breeding occurs, and statistics for it

      // gather timings
      if (doFull)
          {
          Runtime r = Runtime.getRuntime();
          long curU =  r.totalMemory() - r.freeMemory();          
          state.output.print("" + (System.currentTimeMillis()-lastTime) + " ",  statisticslog);
          state.output.print("" + (curU-lastUsage) + " ",  statisticslog);            
          }
      }

  public void preEvaluationStatistics(final EvolutionState state)
      {
      super.preEvaluationStatistics(state);
      }

  /** Prints out the statistics, but does not end with a println --
      this lets overriding methods print additional statistics on the same line */
  protected void _postEvaluationStatistics(final EvolutionState state)
      {
	  state.output.print("" + (state.generation + 1) + ",", statisticslog);
	  state.output.print("" + (300*(state.generation + 1)) + ",", statisticslog);
      // gather timings
      //if (doFull)
          {
          Runtime r = Runtime.getRuntime();
          long curU =  r.totalMemory() - r.freeMemory();          
          state.output.print("" + (System.currentTimeMillis()-lastTime) + ",",  statisticslog);
          //state.output.print("" + (curU-lastUsage) + " ",  statisticslog);            
          }
      

      long lengthPerGen = 0;
      Individual[] best_i = new Individual[state.population.subpops.length];
      for(int x=0;x<state.population.subpops.length;x++)
          {
          // fitness information
          double meanFitness = 0.0;

          for(int y=0;y<state.population.subpops[x].individuals.length;y++)
              {
              // best individual ((Chromosome)state.population.subpops[i].individuals[j]).getFitness()
              if (best_i[x]==null ||
                  ((TestCaseCandidate)state.population.subpops[x].individuals[y]).getFitness().betterThan(((TestCaseCandidate)best_i[x]).getFitness()))
                  best_i[x] = state.population.subpops[x].individuals[y];

              // mean fitness for population
              meanFitness += ((TestCaseCandidate)state.population.subpops[x].individuals[y]).getFitness().fitness();
              }
          
          // compute fitness stats
          meanFitness /= state.population.subpops[x].individuals.length;
          //state.output.print("" + meanFitness + " " + best_i[x].fitness.fitness() + " ",
          //    statisticslog);

          // now test to see if it's the new best_of_run[x]
          if (best_of_run[x]==null || ((TestCaseCandidate) best_i[x]).getFitness().betterThan(((TestCaseCandidate) best_of_run[x]).getFitness()))
              best_of_run[x] = (Individual)(best_i[x].clone());
          }
      
	      Individual bestIndividual = null;
	      for(int p=0;p<best_i.length;p++)
	    	  if (bestIndividual==null ||
	    			  ((TestCaseCandidate) best_i[p]).getFitness().betterThan(((TestCaseCandidate) bestIndividual).getFitness()))
	    		  bestIndividual= best_i[p];
	      
	    state.output.print("" + (((TestCaseCandidate) bestIndividual).getFitness().fitness()) , statisticslog);
	      	
      // we're done!
      }

  public void postEvaluationStatistics(final EvolutionState state)
      {
      super.postEvaluationStatistics(state);
      _postEvaluationStatistics(state);
      state.output.println("", statisticslog);
      }

  }
