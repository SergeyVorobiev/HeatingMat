package com.vorobiev.heatingMat;

public class Matrix
{
	private double[][] matrix;
	private boolean isComput = false;
	private double det;
	private int strVal;
	private int colVal;
	public Matrix()
	{
		matrix = null;
	}
	public Matrix(double[][] matrix)
	{
		//������� ���������� ����� �������
		strVal = matrix.length;
		//�������� ���������� �������� �������
		colVal = matrix[strVal - 1].length;
		if(strVal != colVal)
			throw new RuntimeException("���������� ����� �� ����� ���������� ��������");
		this.matrix = matrix;
	}
	public double computMatrix()
	{
		double[][] tempMatrix = clone(matrix);
		if(!isComput)
			det = decreaseMatrix(tempMatrix); //������� ������� �������	
		isComput = true;
		return det;
	}
	public double[][] clone(double[][] matrix)
	{
		double[][] clone = new double[matrix.length][];
		for(int i = 0; i < matrix.length; i++)
			clone[i] = matrix[i].clone();
		return clone;
	}
	public void print()
	{
		print(this.matrix);
	}
	public void print(double[][] tempMatrix)
	{
		for(int i = 0; i < strVal; i++)
		{
			for(int j = 0; j < colVal; j++)
			{
				System.out.print(tempMatrix[i][j] + ", ");
			}
			System.out.println();
		}
	}
	public double decreaseMatrix(double[][] tmatrix)
	{	
		int t = 0; //�� �������� � ������ ������� � ���������� t ���� �� ������ �� ����� �������
		while(t < (strVal - 1))
		{
			//���� ������ ������� ������� ����� ����
			if(tmatrix[t][t] == 0)
			{
				boolean exist = false;
				//������ ������ � ������� ������ ������� �� ����� 0 � ������ �� � ������
				for(int i = t + 1; i < strVal; i++)
				{
					if(tmatrix[i][t] != 0)
					{
						exist = true;
						//����������
						for(int j = t; j < colVal; j++)
							tmatrix[t][j] += tmatrix[i][j];
						break;
					}
				}
				if(!exist)
				{
					//throw new RuntimeException("������ �������� ���� ����� ������� ����� 0");
					return 0;
				}
			}
			for(int i = t + 1; i < strVal; i++)
			{
				//������� ����������� ��� ��������� �� ������ ����� ��� �������� �
				//���������� �������� 0
				double k = -(tmatrix[i][t] / tmatrix[t][t]);
				for(int j = t; j < colVal; j++)
					tmatrix[i][j] += k * tmatrix[t][j];
			}
			++t;
		}
		double deter = 1;
		for(int i = 0; i < strVal; i++)
			deter *= tmatrix[i][i];
		return deter;
	}
	public double[][] getMatrix()
	{
		return matrix;
	}
	public void setMatrix(double[][] matrix)
	{
		//������� ���������� ����� �������
		strVal = matrix.length;
		//�������� ���������� �������� �������
		colVal = matrix[strVal - 1].length;
		if(strVal != colVal)
			throw new RuntimeException("���������� ����� �� ����� ���������� ��������");
		this.matrix = matrix;
		this.isComput = false;
	}
	public boolean isComput()
	{
		return isComput;
	}
	public void getValues(double[] equals, double[] values)
	{
		if((equals.length != colVal) || colVal != values.length)
			throw new RuntimeException("���������� �������� �� ��������� � ����������� ����� ���� ������ ������� ��� ��������� ����������� ����������� � ����������� �����������");
		if(!isComput)
			computMatrix();
		double[][] tmatrix = null;
		double tempDet;
		for(int i = 0; i < colVal; i++) //��������� �� ���� �������� ������� �� ���������� �� ���������
		{
			tmatrix = this.clone(matrix);
			for(int j = 0; j < strVal; j++)
				tmatrix[j][i] = equals[j];
			tempDet = decreaseMatrix(tmatrix);
			values[i] = tempDet / det;
		}
	}
	public void getParallelValues(MyPoolThread pool, double[] equals, double[] values) {
		if((equals.length != colVal) || colVal != values.length)
			throw new RuntimeException("���������� �������� �� ��������� � ����������� ����� ���� ������ ������� ��� ��������� ����������� ����������� � ����������� �����������");
		if(!isComput)
			computMatrix();
		double[][] tmatrix = null;
		//double tempDet;
		RunComputMatrix.temper = values;
		for(int i = 0; i < colVal; i++) //��������� �� ���� �������� ������� �� ���������� �� ���������
		{
			tmatrix = this.clone(matrix);
			for(int j = 0; j < strVal; j++)
				tmatrix[j][i] = equals[j];
			pool.comput(tmatrix, det, i);
		}
		while(!pool.isFreeAll());
	}
	public double getDet()
	{
		if(!isComput)
			computMatrix();
		return det;
	}

}
