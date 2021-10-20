package csbst.ga.ecj;

import java.lang.reflect.InvocationTargetException;


import ec.Evaluator;
import ec.EvolutionState;
import ec.coevolve.GroupedProblemForm;
import ec.util.Parameter;
import ec.Individual;

public class TestingEvaluator extends Evaluator
  {
  // checks to make sure that the Problem implements SimpleProblemForm
  public void setup(final EvolutionState state, final Parameter base)
      {
      super.setup(state,base);
      if (!(p_problem instanceof TestingProblem))
          state.output.fatal("" + this.getClass() + " used, but the Problem is not of SimpleProblemForm",
              base.push(P_PROBLEM));
      }
  
  public void evaluatePopulation(final EvolutionState state)
      {
	  ((TestingProblem)p_problem).preprocessPopulation(state,state.population, false);
      int numinds[][] = 
          new int[state.evalthreads][state.population.subpops.length];
      int from[][] = 
          new int[state.evalthreads][state.population.subpops.length];
      
      for(int y=0;y<state.evalthreads;y++)
          for(int x=0;x<state.population.subpops.length;x++)
              {
              // figure numinds
              if (y<state.evalthreads-1) // not last one
                  numinds[y][x]=
                      state.population.subpops[x].individuals.length/
                      state.evalthreads;
              else // in case we're slightly off in division
                  numinds[y][x]=
                      state.population.subpops[x].individuals.length/
                      state.evalthreads +
                      
                      (state.population.subpops[x].individuals.length -
                          (state.population.subpops[x].individuals.length /
                          state.evalthreads)  // note integer division
                      *state.evalthreads);                    

              // figure from
              from[y][x]=
                  (state.population.subpops[x].individuals.length/
                  state.evalthreads) * y;
              }

      if (state.evalthreads==1)
          evalPopChunk(state,numinds[0],from[0],0,(TestingProblem)(p_problem.clone()));  
      
      else
          {
          Thread[] t = new Thread[state.evalthreads];
          
          // start up the threads
          for(int y=0;y<state.evalthreads;y++)
              {
              SimpleEvaluatorThread r = new SimpleEvaluatorThread();
              r.threadnum = y;
              r.numinds = numinds[y];
              r.from = from[y];
              r.me = this;
              r.state = state;
              r.p = (TestingProblem)(p_problem.clone());
              t[y] = new Thread(r);
              t[y].start();
              }

          // gather the threads
          for(int y=0;y<state.evalthreads;y++) try
                                                   {
                                                   t[y].join();
                                                   }
              catch(InterruptedException e)
                  {
                  state.output.fatal("Whoa! The main evaluation thread got interrupted!  Dying...");
                  }

          }
      	((TestingProblem)p_problem).postprocessPopulation(state, state.population, false);
      }

  protected void evalPopChunk(EvolutionState state, int[] numinds, int[] from,
      int threadnum, TestingProblem p)
      {
      ((ec.Problem)p).prepareToEvaluate(state,threadnum);
      
      for(int pop=0;pop<state.population.subpops.length;pop++)
          {
          // start evaluation'!
          int upperbound = from[pop]+numinds[pop];
          for (int x=from[pop];x<upperbound;x++)
              {
              try {
				p.evaluate(state,state.population.subpops[pop].individuals[x], pop, threadnum);
				if(state.population.subpops[pop].individuals[x].fitness.isIdealFitness()){
			    	  state.finish(1);
			    	  return;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
              }
          }
      ((ec.Problem)p).finishEvaluating(state,threadnum);
      }
  
  /** The SimpleEvaluator determines that a run is complete by asking
      each individual in each population if he's optimal; if he 
      finds an individual somewhere that's optimal,
      he signals that the run is complete. */
  public boolean runComplete(final EvolutionState state)
      {
      for(int x = 0;x<state.population.subpops.length;x++)
          for(int y=0;y<state.population.subpops[x].individuals.length;y++)
              if ( ((TestCaseCandidate)state.population.subpops[x].
                 individuals[y]).getFitness().isIdealFitness())
                  return true;
      return false;
      }
  }

/** A private helper class for implementing multithreaded evaluation */
class SimpleEvaluatorThread implements Runnable
  {
  public int[] numinds;
  public int[] from;
  public TestingEvaluator me;
  public EvolutionState state;
  public int threadnum;
  public TestingProblem p;
  public synchronized void run() 
      { me.evalPopChunk(state,numinds,from,threadnum,p); }
  }
