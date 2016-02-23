package com.vorobiev.heatingMat;

public class MyThreadPause extends Thread 
{
	private Runnable runnable;
	private volatile boolean pause = false;
	private volatile boolean exit = false;
	private boolean isRun = true;
	MyThreadPause(Runnable runnable) 
	{
		this.runnable = runnable;
	}
	public void setRunnable(Runnable run) {
		runnable = run;
	}
	public Runnable getRunnable() {
		return runnable;
	}
	public void onPause()
	{
		pause = true;
	}
	public void stopThread()
	{
		synchronized(this)
		{
			
			exit = true;
			if(pause) continues();
		}
	}
	public void continues()
	{
		synchronized(this)
		{
			pause = false;
			isRun = true;
			if(this.getState() == Thread.State.WAITING) {
				this.notify();
			}
		}
	}
	@Override
	public void run() 
	{
		while(true)
		{
			synchronized(this)
			{
				if(pause)
				{
					try
					{
						isRun = false;
						wait();	
					} 
					catch (InterruptedException e) 
					{
						System.out.println("wait exception");
						e.printStackTrace();
					}
				}
				if(exit) {
					System.out.println("exit");
					break;
				}
			}
			if(runnable == null) {
				throw new RuntimeException("runnable is null");
			}
			runnable.run();
		}
		//System.out.println("Stop");
	}
	//метод run будет блокироваться каждый цикл из за pause = true но выполняться один раз при каждом
	//вызове executeOnceAndWait() из за метода notifyAll()
	public synchronized boolean isRun() {
		return isRun;
	}
	public boolean executeOnceAndWait()
	{
		synchronized(this)
		{
			if(this.getState() == Thread.State.WAITING) {
				pause = true;
				isRun = true;
				this.notify();
				return true;
			}
			return false;
		}
	}
}
