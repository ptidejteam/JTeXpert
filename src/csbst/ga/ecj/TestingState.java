package csbst.ga.ecj;

import ec.*;
import ec.util.Checkpoint;

public class TestingState extends EvolutionState
  {	
public void run(int condition)
    {
	//super.run(condition);
    if (condition == C_STARTED_FRESH)
        {
        startFresh();
        }
    else // condition == C_STARTED_FROM_CHECKPOINT
        {
        startFromCheckpoint();
        }
    
    /* the big loop */
    int result = R_NOTDONE;
    while ( result == R_NOTDONE )
        {
        result = evolve();
        }
    
    finish(result);
    }
	
  public void startFresh() 
      {
      //output.message("Setting up");
      setup(this,null);  // a garbage Parameter

      // POPULATION INITIALIZATION
      //output.message("Initializing Generation 0");
      statistics.preInitializationStatistics(this);
      population = initializer.initialPopulation(this, 0); // unthreaded
      statistics.postInitializationStatistics(this);

      // INITIALIZE CONTACTS -- done after initialization to allow
      // a hook for the user to do things in Initializer before
      // an attempt is made to connect to island models etc.
      exchanger.initializeContacts(this);
      evaluator.initializeContacts(this);
      }

  /**
   * @return
   * @throws InternalError
   */
  public int evolve()
      {
      //if (generation > 0) 
      //    output.message("Generation " + generation);

      // EVALUATION
      statistics.preEvaluationStatistics(this);
      evaluator.evaluatePopulation(this);
      statistics.postEvaluationStatistics(this);

      // SHOULD WE QUIT?
      if (evaluator.runComplete(this) && quitOnRunComplete)
          {
          //output.message("Found Ideal Individual");
          return R_SUCCESS;
          }

      // SHOULD WE QUIT?
      if (generation == numGenerations-1)
          {
          return R_FAILURE;
          }

      // PRE-BREEDING EXCHANGING
      statistics.prePreBreedingExchangeStatistics(this);
      population = exchanger.preBreedingExchangePopulation(this);
      statistics.postPreBreedingExchangeStatistics(this);

      String exchangerWantsToShutdown = exchanger.runComplete(this);
      if (exchangerWantsToShutdown!=null)
          { 
          output.message(exchangerWantsToShutdown);
          /*
           * Don't really know what to return here.  The only place I could
           * find where runComplete ever returns non-null is 
           * IslandExchange.  However, that can return non-null whether or
           * not the ideal individual was found (for example, if there was
           * a communication error with the server).
           * 
           * Since the original version of this code didn't care, and the
           * result was initialized to R_SUCCESS before the while loop, I'm 
           * just going to return R_SUCCESS here. 
           */
          
          return R_SUCCESS;
          }

      // BREEDING
      statistics.preBreedingStatistics(this);

      population = breeder.breedPopulation(this);
      
      // POST-BREEDING EXCHANGING
      statistics.postBreedingStatistics(this);
          
      // POST-BREEDING EXCHANGING
      statistics.prePostBreedingExchangeStatistics(this);
      population = exchanger.postBreedingExchangePopulation(this);
      statistics.postPostBreedingExchangeStatistics(this);

      // INCREMENT GENERATION AND CHECKPOINT
      generation++;
      if (checkpoint && generation%checkpointModulo == 0) 
          {
          output.message("Checkpointing");
          statistics.preCheckpointStatistics(this);
          Checkpoint.setCheckpoint(this);
          statistics.postCheckpointStatistics(this);
          }

      return R_NOTDONE;
      }

  /**
   * @param result
   */
  public void finish(int result) 
      {
      //Output.message("Finishing");
      /* finish up -- we completed. */
      statistics.finalStatistics(this,result);
      finisher.finishPopulation(this,result);
      exchanger.closeContacts(this,result);
      evaluator.closeContacts(this,result);
      }

  }
