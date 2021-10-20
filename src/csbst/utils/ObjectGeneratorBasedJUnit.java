package csbst.utils;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import junit.framework.TestCase;
import csbst.generators.AbsractGenerator;
import csbst.generators.dynamic.MethodGenerator;
import csbst.testing.JTE;

public class ObjectGeneratorBasedJUnit  extends TestCase {
	public  AbsractGenerator gene;
	
	@Rule  
	public Timeout to = new Timeout(5);

	public ObjectGeneratorBasedJUnit(AbsractGenerator ge){
		gene=ge;
	}
	
	@Test(timeout=5)
    public void testObject() {
		gene.generateRandom();
    }
}
