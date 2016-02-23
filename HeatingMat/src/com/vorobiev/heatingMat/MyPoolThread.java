package com.vorobiev.heatingMat;

public class MyPoolThread {
	private int core;
	private MyThreadPause[] threads;
	private int free;
	//private Object key;
	//private double[] values;
	public MyPoolThread(int numThreads) {
		core = numThreads;
		if(core < 1) core = 1;
		threads = new MyThreadPause[core];
		free = core;
		for(int i = 0; i < core; i++) {
		
			threads[i] = new MyThreadPause(null);
			threads[i].onPause();
			threads[i].start();
		}
		
	}
	public boolean isFree()
	{
		int tfree = free;
		return (tfree == core);
	}
	public void setThreadRunnable(Runnable runnable, int index) {
		threads[index].setRunnable(runnable);
	}
	public Runnable getThreadRunnable(int index) {
		return threads[index].getRunnable();
	}
	public boolean isFreeAll() {
		int free = 0;
		for(int i = 0; i < core; i++) {
			if(threads[i].getState() == Thread.State.WAITING) {
				++free;
			}
		}
		return (free == core);
	}
	public void comput(double[][] tmatrix, double det, int index) {
		while(true) {
			for(int i = 0; i < core; i++) {
				if(threads[i].getState() == Thread.State.WAITING) {
					RunComputMatrix run = (RunComputMatrix)threads[i].getRunnable();
					run.set(tmatrix, det, index);
					if(!threads[i].executeOnceAndWait()) {
						throw new RuntimeException("Not execute");
					}
					return;
				}
			}
		}
	}
	public void comput(Runnable runnable) {
		while(true) {
			for(int i = 0; i < core; i++) {
				if(threads[i].getState() == Thread.State.WAITING) {
					threads[i].setRunnable(runnable);
					if(!threads[i].executeOnceAndWait()) {
						throw new RuntimeException("Not execute");
					}
					return;
				}
			}
		}
	}
	public void dispose() {
		for(MyThreadPause thread : threads)
			thread.stopThread();
	}
	
}
