package csbst.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import csbst.generators.dynamic.MethodGenerator;
import csbst.testing.JTE;

public class TimeOutMethodExecutor {

    public static class MethodExecutionJob implements Callable<String> {
    	Object object;
    	MethodGenerator method;
    	public MethodExecutionJob(Object obj, MethodGenerator meth){
    		object=obj;
    		method=meth;
    	}
    	
        @Override
        public String call() throws Exception {
        	if(object==null)
        		method.execute();
        	else
        		method.execute(object,method.getClazz());
            return "result";
        }

    }

    public static void  executeMethod(Object obj, MethodGenerator meth) {

        Future<String> control = Executors.newSingleThreadExecutor().submit(new MethodExecutionJob(obj,meth));

        try {

            String result = control.get(500, TimeUnit.MILLISECONDS);

        } catch (TimeoutException ex) {
            // 5 seconds expired, we cancel the job !!!
        	JTE.stdout.println("Method timeout");
            control.cancel(true);
            

        }
        catch (InterruptedException ex) {

        } catch (ExecutionException ex) {

        }

    }

}
