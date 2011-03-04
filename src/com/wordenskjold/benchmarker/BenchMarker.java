package com.wordenskjold.benchmarker;


/**
 * Simple Benchmarker for small running-time measurements.
 * Pass a Runnable or a Benchmarkable to a BenchMarker object, and call start to measure the 
 * running time in ms of the method or runnable.
 * 
 * The benchmarker runs the given method n+1 times, where n is the value of the 
 * accuracy value (default 10). Since no classes is loaded at first run,
 * it initializes itself by running the method once, and ignores the result for a more presise measurement.
 * 
 * The result is calculated as the average running time of each of the method executions.
 * 
 * @author Frederik Wordenskjold ( http://www.wordenskjold.com )
 *
 */
public class BenchMarker {
	
	private Benchmarkable toBenchmark;
	private Runnable runnable;
	//Number of times run method is called
	private int accuracy = 10;
	
	public BenchMarker(Benchmarkable toBenchmark){
		this.toBenchmark = toBenchmark;
	}
	
	public BenchMarker(Runnable runnable){
		this.runnable = runnable;
	}
	
	public BenchMarker(){}
	
	public void addBenchmarkable(Benchmarkable toBenchmark){
		this.toBenchmark = toBenchmark;
		runnable = null;
	}
	
	public void addRunnable(Runnable runnable){
		this.runnable = runnable;
		toBenchmark = null;
	}
	
	public void start(){
		final double[] time = new double[accuracy];
		Thread t = new Thread(){
			public void run(){
				long currentTime = 0;
				for(int i = 0; i < accuracy; i++){
					try{
						currentTime = System.nanoTime();
						if(toBenchmark != null)
							toBenchmark.benchMark();
						else if(runnable != null)
							runnable.run();
						else 
							throw new NullPointerException();
						time[i] = System.nanoTime() - currentTime;
					}
					catch(NullPointerException e){
						System.out.println("ERROR : ");
						e.printStackTrace();
					}	
				}	
			}
		};
		//Run it once to for class loading-times, etc.
		int tmp = accuracy;
		accuracy = 1;
		t.run();
		accuracy = tmp;
		t.run();
		double sum = 0;
		// Find the mean and print the result
		for(double d : time) sum+=d;
		printResult(sum/time.length);
	}
	
	/*
	 * The next two run-methods should be used for fast measurements, as they are less accurate (but faster).
	 */
	public static void run(Benchmarkable m){
		long currentTime = System.nanoTime();
		m.benchMark();
		printResult(System.nanoTime() - currentTime);
	}
	
	public static void run(Runnable r){
		long currentTime = System.nanoTime();
		r.run();
		printResult(System.nanoTime() - currentTime);
	}
	
	private static void printResult(double time){
		System.out.println("The method took "+(time*1e-6)+" ms to execute");
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}
}
