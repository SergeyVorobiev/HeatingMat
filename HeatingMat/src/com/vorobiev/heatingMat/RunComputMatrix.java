package com.vorobiev.heatingMat;

public class RunComputMatrix implements Runnable {
	
		
	private double[][] tmatrix;
	private double det;
	private int index;
	//������ � ������� ����� �������� ���������� �����������
	public static double[] temper;
	
	//����� ������������ ��������� ������� ��� ����������. ����������� ������� ������� � ������ ��� ������� �������� �����������
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
		//������� ���������� ����� �������
		int strVal = tmatrix.length;
		//�������� ���������� �������� �������
		int colVal = tmatrix[strVal - 1].length;
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
					set(0, index);
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
		deter /= det;
		set(deter, index);
	}
}
