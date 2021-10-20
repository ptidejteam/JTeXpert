package csbst.ga.ecj;
import java.lang.reflect.InvocationTargetException;

import csbst.generators.AbsractGenerator;
import csbst.testing.JTE;
import csbst.testing.fitness.TestingFitness;
import ec.*;
import ec.util.*;
import ec.vector.VectorDefaults;
 
public class ChromosomeSpecies extends Species
    {
	public static final String P_VECTORSPECIES = "species";
    public static final String P_GENE = "gene";
    public AbsractGenerator genePrototype;

    public void setup(final EvolutionState state, final Parameter base)
        {
        Parameter def = defaultBase();
        // make sure that super.setup is done AFTER we've loaded our gene prototype.
        super.setup(state,base);
        }
    @Override
    public Individual newIndividual(final EvolutionState state, int thread)
    {
	    TestCaseCandidate newind = new TestCaseCandidate(JTE.currentClassUnderTest);//(Individual)(i_prototype.clone());
	    newind.generateRandom();
	    newind.evaluated = false;
	
	    // Set the species to me
	    newind.species = this;
	    //newind.
	   
		
	    	  //return;
	    // ...and we're ready!
	    return newind;
    }
    
	@Override
	public Parameter defaultBase() {
		return VectorDefaults.base().push(P_VECTORSPECIES);
	}

        
    }

