package csbst.utils;

public class TimeOut extends Thread {
    Thread threadToInterrupt = null;    
    public TimeOut() {
        // interrupt thread that creates this TimeOut.
        threadToInterrupt = Thread.currentThread();
        setDaemon(true);
    }
    
    public void run() {           
		System.out.println("TimeOut has terminated normaly");
        
    }
    
    public void stopThread(){
    	System.out.println("TimeOut has been interrupted");
    	threadToInterrupt.interrupt();
    	
    }
    
}