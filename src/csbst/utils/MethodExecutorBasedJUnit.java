package csbst.utils;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import junit.framework.TestCase;
import csbst.generators.dynamic.MethodGenerator;
import csbst.testing.JTE;

public class MethodExecutorBasedJUnit  extends TestCase {
	public  Object object;
	public  MethodGenerator method;
	
	@Rule  
	public Timeout to = new Timeout(5);
	
	public MethodExecutorBasedJUnit(Object obj, MethodGenerator meth){
		object=obj;
		method=meth;
	}
	
	@Test(timeout=5)
    public void testMethod() {
		//JTE.stdout.println("@Test is invoked, indeed.");
    	if(object==null)
    		method.execute();
    	else
    		method.execute(object,method.getClazz());
    }
	
	@After
	public void after() {
	    JTE.stdout.println("@After is invoked, indeed.");
	}
}
