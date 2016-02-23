package com.vorobiev.heatingMat;

public class ComputTemper 
{
	private double lastTime = 0;
	private double deltaTime;
	private IFunDiffTemperature left;
	private IFunDiffTemperature right;
	private double a; //коэффициент теплопроводности
	private double s; //шаг глубины нагрева в материале
	private int sizeNodes;
	private double currentTempLeft;
	private double currentTempRight;
	protected double startTempLeft;
	protected double startTempRight;
	protected double upLeftBounds;
	protected double upRightBounds;
	protected double downLeftBounds;
	protected double downRightBounds;
	//укажем температуру объекта в начальный момент времени
	private double[] temper = null;
	private Matrix generalMatrix = null;
	private boolean isActualGeneralMatrix = false;
	private Object synchron = new Object();
	private double l;
	private MyPoolThread pool;
	/*public ComputTemper(double l, int sizeNodes, double temperature, double deltaTime, 
							double a, IFunDiffTemperature left, IFunDiffTemperature right)
	{
		this.currentTempLeft = temperature;
		this.currentTempRight = temperature;
		this.startTempLeft = temperature;
		this.startTempRight = temperature;
		this.l = l;
		this.s = 1;
		this.sizeNodes = sizeNodes;
		temper = new double[sizeNodes];
		this.deltaTime = deltaTime;
		this.a = a;
		this.left = left;
		this.right = right;
		if(temperature != 0)
		{
			for(int i = 0; i < temper.length; i++)	
				temper[i] = temperature;
		}
	}*/
	public ComputTemper(double l, int sizeNodes, double[] temperature, double deltaTime, 
			double a, IFunDiffTemperature left, IFunDiffTemperature right)
	{
		this.currentTempLeft = temperature[0];
		this.currentTempRight = temperature[temperature.length - 1];
		this.startTempLeft = temperature[0];
		this.startTempRight = temperature[temperature.length - 1];
		this.l = l;
		this.s = l / sizeNodes;
		this.sizeNodes = sizeNodes;
		temper = temperature;
		this.deltaTime = deltaTime;
		this.a = a;
		this.left = left;
		this.right = right;
		int core = Runtime.getRuntime().availableProcessors() - 1;
		pool = new MyPoolThread(core);
		for(int i = 0; i < core; i++) {
			pool.setThreadRunnable(new RunComputMatrix(), i);
		}
	}
	public void setA(double a)
	{
		synchronized(synchron)
		{
			this.a = a;
			reconfigGeneralMatrix();
		}	
	}
	public void setDeltaTime(double time)
	{
		synchronized(synchron)
		{
			this.deltaTime = time;
			reconfigGeneralMatrix();
		}
	}
	public double getA()
	{
		return a;
	}
	public double getStartWidth()
	{
		return l;
	}
	public double getLastTime()
	{
		return lastTime;
	}
	public double getCurrentTempLeft()
	{
		return currentTempLeft;
	}
	public double getCurrentTempRight()
	{
		return currentTempRight;
	}
	public double[] getTemperature()
	{
		return temper;
	}
	public double[] getComputTemperature()
	{
		synchronized(synchron)
		{
			if(!isActualGeneralMatrix)
				setGeneralMatrix();
			//generalMatrix.getValues(getEquals(temper), temper);
			synchronized(PanelGraphics.key) {
				generalMatrix.getParallelValues(pool, getEquals(temper), temper);
				//generalMatrix.getValuesPoolThread(getEquals(temper), temper, poolThread);
			}
			
		}
		return temper;
	}
	public double getDeltaTime()
	{
		return deltaTime;
	}
	public double getS()
	{
		return s;
	}
	private double[] getEquals(double[] temperatures)
	{
		
		double[] equals = new double[temperatures.length];
		double tempTime = deltaTime;
		double ta = a;
		lastTime += tempTime;
		currentTempLeft = left.getCurrentTemperature(tempTime);// + startTempLeft;
		currentTempRight = right.getCurrentTemperature(tempTime);// + startTempRight;
		for(int i = 0; i < temperatures.length; i++)
		{
			if(i == 0)
				equals[i] = (ta * temperatures[i]) / tempTime + 
					currentTempLeft / (s * s);
			else if(i == (temperatures.length - 1))
				equals[i] = (ta * temperatures[i]) / tempTime + 
					currentTempRight / (s * s);
			else
				equals[i] = (ta * temperatures[i] / tempTime);
		}
		return equals;
	}
	public void reconfigGeneralMatrix()
	{
		synchronized(synchron)
		{
			if(generalMatrix == null || generalMatrix.getMatrix() == null)return;
			double[][] newMatrix = new double[temper.length][temper.length];
			double[][] matrix = generalMatrix.getMatrix();
			int k = -1; //коэффициент сдвига матрицы для каждой строки
			double k1 = - 1 / (s * s);
			double k2 = a / deltaTime + 2 / (s * s);
			double k3 = k1;
			for(int i = 0; i < temper.length; i++)
			{
				for(int j = 0; j < temper.length; j++)
				{
					if(j == k)
						newMatrix[i][j] = k1;
					else if(j == (k + 1))
						newMatrix[i][j] = k2;
					else if(j == (k + 2))
						newMatrix[i][j] = k3;
					else 
						newMatrix[i][j] = matrix[i][j];
				}
				k++;									
			}
			generalMatrix.setMatrix(newMatrix);
		}
	}
	private void setGeneralMatrix()
	{
		double[][] matrix = new double[temper.length][temper.length];
		int k = -1; //коэффициент сдвига матрицы для каждой строки
		double k1 = - 1 / (s * s);
		double k2 = a / deltaTime + 2 / (s * s);
		double k3 = k1;
		for(int i = 0; i < temper.length; i++)
		{
			for(int j = 0; j < temper.length; j++)
			{
				if(j == k)
					matrix[i][j] = k1;
				else if(j == (k + 1))
					matrix[i][j] = k2;
				else if(j == (k + 2))
					matrix[i][j] = k3;
				//else 
					//matrix[0][j] = temper[j];
			}
			k++;									
		}
		isActualGeneralMatrix = true;
		generalMatrix = new Matrix(matrix);
	}
	public int getSizeNodes()
	{
		return sizeNodes;
	}
	public void dispose() {
		pool.dispose();
	}
}
