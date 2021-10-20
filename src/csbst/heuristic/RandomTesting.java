package csbst.heuristic;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import csbst.ga.ecj.TestCaseCandidate;
import csbst.generators.AbsractGenerator;
import csbst.generators.dynamic.AbstractDynamicGenerator;
import csbst.testing.JTE;
import csbst.testing.fitness.NumberCoveredBranches;
import csbst.testing.fitness.TestingFitness;
// implements Heuristic 
public class RandomTesting{
	private int generations;
	private int currentGenNbr;
	public static Thread killerThread;
	public static Thread testCaseWriterThread;
	public static final long iMaxWaitingTime=500;
	public static final long mMaxWaitingTime=7500;
	long timeOut;
	//private int maxtime;
	
	public static ConcurrentLinkedQueue<Thread> killerQueue = new ConcurrentLinkedQueue<Thread>();
	static{
		killerThread = new Thread(){ 
		@Override public void run (){		
				while(true){
					Thread t;
					while ((t  = killerQueue.poll()) != null) {					
						if(t.isAlive()){
							t.interrupt();
							
							if(t.isAlive()){
								//t.stop();
								killerQueue.add(t);
							}
							 
						}
					
			         }
				}
			}
		};
		killerThread.setPriority(Thread.MIN_PRIORITY);
	}
	
	
	public static ConcurrentLinkedQueue<TestCaseCandidate> testCaseWriterQueue = new ConcurrentLinkedQueue<TestCaseCandidate>();
	static int currentTestCase=0;
	static{
		testCaseWriterThread = new Thread(){ 
		@Override public void run (){		
				while(true){
					TestCaseCandidate ch;
					while ((ch  = testCaseWriterQueue.poll()) != null) { 
						String surfix=JTE.TEST_CASES_SURFIX+currentTestCase+"_0_";
						String fileName=JTE.testCasesPath+File.separator+ JTE.subPath+File.separator+JTE.srcFileName+surfix+".java";
						String clsName=JTE.className+surfix;
						ch.writeCurrentTestCandidate(surfix);						
						JTE.correctExceptions( fileName,clsName, "noSplit",false); 
						currentTestCase++;
			         }
				}
			}
		};
		testCaseWriterThread.setDaemon(true);
		
		testCaseWriterThread.setPriority(Thread.MAX_PRIORITY);
	}
	
	public RandomTesting (int generations){
		this.generations=generations;
		//this.timeOut=timeOut;
		currentGenNbr=0;
	}
	
	public int getGeneartion(){
		return currentGenNbr;
	}
	
	private static int chmod(String filename, int mode) {
	    try {
	        Class<?> fspClass = Class.forName("java.util.prefs.FileSystemPreferences");
	        Method chmodMethod = fspClass.getDeclaredMethod("chmod", String.class, Integer.TYPE);
	        chmodMethod.setAccessible(true);
	        return (Integer)chmodMethod.invoke(null, filename, mode);
	    } catch (Throwable ex) {
	        return -1;
	    }
	}
	
	public int run(final Set<Integer>toCheck){ 
		if(generations<=0)
			return 0;
		currentGenNbr=0;
		final TestCaseCandidate currentChromosome=new TestCaseCandidate(JTE.currentClassUnderTest);
		//copyChromosome.getFitness().evaluate(copyChromosome);
		currentGenNbr++;
		
		Thread thread = new Thread(){ 
			@Override public void run (){
				Random randM=new Random();
				int MaDuM = randM.nextInt(100); 
				if(MaDuM<0)
					currentChromosome.generateRandom();
				else
					currentChromosome.generateRandomMaDuM();
				
				currentChromosome.getFitness().evaluate(currentChromosome); 
			}
		};
		thread.setDaemon(false);
		thread.start();
		try {
			thread.join(1000); //7500 cMaxWaitingTime JTE.cMaxWaitingTime
		} catch (InterruptedException e) {
			e.printStackTrace();
		}catch (Exception e) { //this to avoid System.exit()
			e.printStackTrace();
		}
		
		if(thread.isAlive()){
			thread.interrupt();	
			if(thread.isAlive()){
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				//thread.setPriority(Thread.MIN_PRIORITY);
				killerQueue.add(thread);
				currentChromosome.setInterrupted();
				if(!killerThread.isAlive())
					killerThread.start();
			}
			return -1;
		}
		
//		//if(currentChromosome.isGoodTestData){
//		System.err.println("%%%%%% :- "+testCaseWriterQueue.size());
//		//testCaseWriterQueue.add(currentChromosome);
//		if(!testCaseWriterThread.isAlive())
//			testCaseWriterThread.start();
		

		return currentGenNbr; 
	}
}

//currentGenNbr++;
//while(currentGenNbr<generations && !((NumberCoveredBranches)currentChromosome.getFitness()).isIdealFitness()&&!toCheck.isEmpty()){
//	currentChromosome=new Chromosome(JTE.currentClassUnderTest.getClazz());
//	currentChromosome.generateRandom();
//	currentChromosome.getFitness().evaluate(currentChromosome);
//	currentGenNbr++;
//}
