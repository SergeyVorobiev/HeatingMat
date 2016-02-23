package com.vorobiev.heatingMat;

public class RunComputMatrix implements Runnable {
	
		
	private double[][] tmatrix;
	private double det;
	private int index;
	//массив в которую нужно вставить полученную температуру
	public static double[] temper;
	
	//перед вычислениями установим матрицу для вычисления. детерминант главной матрицы и индекс для вставки значения температуры
	public void set(double[][] tmatrix, double det, int index) {
		this.tmatrix = tmatrix;
		this.det = det;
		this.index = index;
	}
	private static synchronized void set(double value, int index) {
		temper[index] = value;
	}
	@Override
	public void run() {
		//получим количество строк матрицы
		int strVal = tmatrix.length;
		//получаем количество столбцов матрицы
		int colVal = tmatrix[strVal - 1].length;
		int t = 0; //мы начинаем с начала матрицы и прибавляем t пока не дойдем до длины матрицы
		while(t < (strVal - 1))
		{
			//если первый элемент матрицы равен нулю
			if(tmatrix[t][t] == 0)
			{
				boolean exist = false;
				//найдем строку в матрице начало которой не равно 0 и сложим ее с первой
				for(int i = t + 1; i < strVal; i++)
				{
					if(tmatrix[i][t] != 0)
					{
						exist = true;
						//складываем
						for(int j = t; j < colVal; j++)
							tmatrix[t][j] += tmatrix[i][j];
						break;
					}
				}
				if(!exist)
				{
					//throw new RuntimeException("первые элементы всех строк матрицы равны 0");
					set(0, index);
				}
			}
			for(int i = t + 1; i < strVal; i++)
			{
				//находим коэффициент для умножения на строки чтобы при сложении с
				//остальными получать 0
				double k = -(tmatrix[i][t] / tmatrix[t][t]);
				for(int j = t; j < colVal; j++)
					tmatrix[i][j] += k * tmatrix[t][j];
			}
			++t;
		}
		double deter = 1;
		for(int i = 0; i < strVal; i++)
			deter *= tmatrix[i][i];
		deter /= det;
		set(deter, index);
	}
}
