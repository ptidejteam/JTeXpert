package csbst.utils;

import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

	 public static void main(String[] args) throws Exception {
		 
		 	int y=10<<-6;
		 	System.out.println(y);
		 	String s=args[0]+args[0];
		 	System.out.println(s);
//	        // initialize logging to go to rolling log file
//	        LogManager logManager = LogManager.getLogManager();
//	        logManager.reset();
//
//	        // log file max size 10K, 3 rolling files, append-on-open
//	        Handler fileHandler = new FileHandler("log", 10000, 3, true);
//	        fileHandler.setFormatter(new SimpleFormatter());
//	        Logger.getLogger("").addHandler(fileHandler);
//	 
//	        // preserve old stdout/stderr streams in case they might be useful      
//	        PrintStream stdout = System.out;                                        
//	        PrintStream stderr = System.err;                                        
//
//	        // now rebind stdout/stderr to logger                                   
//	        Logger logger;                                                          
//	        LoggingOutputStream los;                                                
//
//	        logger = Logger.getLogger("stdout");                                    
//	        los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);           
//	        System.setOut(new PrintStream(los, true));                              
//
//	        logger = Logger.getLogger("stderr");                                    
//	        los= new LoggingOutputStream(logger, StdOutErrLevel.STDERR);            
//	        System.setErr(new PrintStream(los, true));  
//	        
//	        // show stdout going to logger
//	        System.out.println("Hello world!");
//
//	        // now log a message using a normal logger
//	        logger = Logger.getLogger("test");
//	        logger.info("This is a test log message");
//
//	        // now show stderr stack trace going to logger
//	        try {
//	            throw new RuntimeException("Test");
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//
//	        // and output on the original stdout
//	        stdout.println("Hello on old stdout");

	    }                                                                          

	}                                                                              
