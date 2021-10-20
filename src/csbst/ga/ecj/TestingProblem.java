package csbst.ga.ecj;

import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import csbst.testing.fitness.TestingFitness;



import ec.*;
import ec.util.RandomChoice;
import ec.vector.*;

public class TestingProblem extends Problem //implements SimpleProblemForm //GroupedProblemForm
  {
	public int[][] paramsBranchesUseMatrix;
	
  public void preprocessPopulation( final EvolutionState state, Population pop, boolean countVictoriesOnly )
      {      	
	      //randomly exchange 10% of two populations
	      	if(state.generation%20==0 && state.generation!=0){
	      		for( int i = 0 ; i < pop.subpops.length/2 ; i++ ){
	      			int x=state.random[0].nextInt(pop.subpops.length);
	      			int y=state.random[0].nextInt(pop.subpops.length);
	      			int minLen=(pop.subpops[x].individuals.length>pop.subpops[y].individuals.length ? pop.subpops[y].individuals.length : pop.subpops[x].individuals.length);
	      			int p= minLen/10;
	      			if(x!=y){
	      				for(int k=0;k<p;k++){
	      					int z=state.random[0].nextInt(minLen);
	      					Individual tmp = (Individual)pop.subpops[x].individuals[z].clone();
	      					pop.subpops[x].individuals[z]=(Individual) pop.subpops[y].individuals[z].clone();
	      					pop.subpops[y].individuals[z]=tmp;
	      					
	      				}
	      			}
	      		}
	      	}
	      	
	      	//populations compete
	      	if(state.generation%4==0 && state.generation!=0){
	            double [] lR = new double[pop.subpops.length];
	            double [] meanfitnesses = new double[pop.subpops.length];

	            for(int p=0;p<pop.subpops.length;p++)
	            	meanfitnesses[p]=pop.subpops[p].getMeanFitness();
	            
	            int [] indices = new int[pop.subpops.length];
	            
	            RandomChoice.linearRanking(1.7,meanfitnesses,lR,indices);
	            meanfitnesses=lR;
	            // organize the distribution.  All zeros in fitness is fine
	            RandomChoice.organizeDistribution(meanfitnesses, true);
	            
    			int x=indices[RandomChoice.pickFromDistribution(meanfitnesses,state.random[0].nextDouble())];// state.random[0].nextInt(pop.subpops.length);
    			int y=indices[RandomChoice.pickFromDistribution(meanfitnesses,state.random[0].nextDouble())];//state.random[0].nextInt(pop.subpops.length);
				if(pop.subpops[x].getProg()<pop.subpops[y].getProg()){
					int tmp=x;
					x=y;
					y=x;
				}
    			int minLen=(pop.subpops[x].individuals.length>pop.subpops[y].individuals.length ? pop.subpops[y].individuals.length : pop.subpops[x].individuals.length);
    			int p=minLen/10;
    			if(p<1) p=1;
    			p=state.random[0].nextInt(p)+1;
    			if(x!=y){
    				for(int k=0;k<p;k++){
    					 	lR = new double[pop.subpops[y].individuals.length];
    			            meanfitnesses = new double[pop.subpops[y].individuals.length];
	    					for(int i=0;i<pop.subpops[y].individuals.length;i++)
	    		            	meanfitnesses[i]=pop.subpops[y].individuals[i].fitness.fitness();
	    		            indices = new int[pop.subpops[y].individuals.length];
	    		            RandomChoice.linearRanking(1.7,meanfitnesses,lR,indices);
	    		            meanfitnesses=lR;
	    		            RandomChoice.organizeDistribution(meanfitnesses, true);
    						int z=indices[indices.length-RandomChoice.pickFromDistribution(meanfitnesses,state.random[0].nextDouble())-1];//state.random[0].nextInt(pop.subpops[y].individuals.length);
	
    						if( pop.subpops[y].individuals.length>5){
    						Individual[] indsX = new Individual[pop.subpops[x].individuals.length+1];
    						Individual[] indsY = new Individual[pop.subpops[y].individuals.length-1];
    						
    						for(int j=0;j<pop.subpops[x].individuals.length;j++){
    							indsX[j]=pop.subpops[x].individuals[j];
    						}
    						indsX[pop.subpops[x].individuals.length]=pop.subpops[y].individuals[z];
    						
    						//pop.subpops[x].individuals= new Individual[indsX.length];
    						pop.subpops[x].individuals=indsX;
    						
    						for(int j=0;j<z;j++){
    							indsY[j]=pop.subpops[y].individuals[j];
    						}
    						for(int j=z+1;j<pop.subpops[y].individuals.length;j++){
    							indsY[j-1]=pop.subpops[y].individuals[j];
    						}
    						//pop.subpops[y].individuals= new Individual[indsY.length];
    						pop.subpops[y].individuals=indsY;
    					}	
    				}
    			}
    			//System.out.println("*** lengthX: "+pop.subpops[x].individuals.length+ " lengthY: "+pop.subpops[y].individuals.length+" *********");
	      	}
      }

  public void postprocessPopulation( final EvolutionState state, Population pop, boolean countVictoriesOnly )
      {
	  	Map <Double,Integer> avgFitness=new HashMap();
	      for( int i = 0 ; i < pop.subpops.length ; i++ ){
	    	  double meanFitness=0;
	          for( int j = 0 ; j < pop.subpops[i].individuals.length ; j++ ){
	        	  
	              pop.subpops[i].individuals[j].evaluated = true;//original source code
	              
	              meanFitness += ((TestCaseCandidate)state.population.subpops[i].individuals[j]).getFitness().fitness();
	          }
	          // compute fitness stats
	          meanFitness /= state.population.subpops[i].individuals.length;
	          avgFitness.put(new Double(meanFitness), new Integer(i));
	          state.population.subpops[i].setMeanFitness(meanFitness);
	      }
      
	      Iterator it = avgFitness.entrySet().iterator();
	      int i=0;
	      while(it.hasNext()){
	    	  Map.Entry pairs = (Map.Entry)it.next();
	    	  pop.subpops[(Integer) pairs.getValue()].setProg(i+1);
	    	  i++;
	      }
      }

//@Override
public void evaluate(EvolutionState state, Individual ind, int subpopulation,
		int threadnum) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	 // for(int i=0;i<ind.length;i++)
	      if( ! ( ind instanceof TestCaseCandidate ) )
	          state.output.fatal( "The individuals in the testing problem should be Chromosome." );
	      
	      ((TestCaseCandidate)ind).getFitness().evaluate((TestCaseCandidate)ind);//.execute();
	      //currentChromosome.getFitness().evaluate(currentChromosome);

	      //if(ind.fitness.isIdealFitness())
	    //	  state.finish(1);
	}

  }





