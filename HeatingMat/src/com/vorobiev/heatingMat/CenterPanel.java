package com.vorobiev.heatingMat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.vorobiev.heatingMat.ColorTemper.RGB;

public class CenterPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JButton button = new JButton("Старт");
	private static JButton buttonTwo = new JButton("Пауза");
	private static SpinnerNumberModel model1 = new SpinnerNumberModel(0, -740, 740, 1.0);
	private static SpinnerNumberModel model2 = new SpinnerNumberModel(0, -740, 740, 1.0);
	private static JSpinner spinnerTemperLeft = new JSpinner(model1);
	private static JSpinner spinnerTemperRight = new JSpinner(model2);
	public final static SpinnerNumberModel model = new SpinnerNumberModel(1, 0.01, 10, 0.01);
	public final static SpinnerNumberModel modelA = new SpinnerNumberModel(0.01, 0.001, 1, 0.001);
	public final static SpinnerNumberModel modelNode = new SpinnerNumberModel(Main.sizeNodes, 21, Main.maxSizeNodes, 10);
	private static JSpinner spinner = new JSpinner(model);
	public static JSpinner spinnerA = new JSpinner(modelA);
	public static JSpinner spinnerNode = new JSpinner(modelNode);
	public static double[] temper = new double[Main.sizeNodes];
	private Font font = new Font("SansSerif", Font.BOLD, 15);
	public static ComputTemper computTemper;
	public JLabel label2 = new JLabel("T" + Main.sizeNodes);
	public static JLabel lfps;
	private static JLabel labelFPS = new JLabel("FPS:");
	private Polygon ramka = new Polygon();
	private float[] color = new float[3];
	private int currentList = 0;
	private int dList = 1;
	public CenterPanel(int x, int y, int width, int height, Color color)
	{
		this.setLayout(null);
		this.setBounds(x, y, width, height);
		this.setBackground(color);
		JLabel label1 = new JLabel("T1");
		lfps = new JLabel("FPS: ");
		labelFPS.setBounds(350, 50, 50, 20);
		lfps.setBounds(380, 50, 60, 20);
		label1.setBounds(350, 80, 20, 20);
		label2.setBounds(895, 80, 40, 20);
		spinnerTemperLeft.setBounds(370, 80, 50, 20);
		spinnerTemperRight.setBounds(930, 80, 50, 20);
		
		spinnerTemperLeft.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				setTemperature();
			}	
		});
		spinnerTemperRight.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				setTemperature();
			}	
		});
		ramka.addPoint(1000, 20);
		ramka.addPoint(1320, 20);
		ramka.addPoint(1320, 96);
		ramka.addPoint(1000, 96);
		computTemper = initComputTemper();
		add(spinnerTemperRight);
		add(spinnerTemperLeft);
		add(label1);
		add(label2);
		add(lfps);
		add(labelFPS);
		initialButton();
		initialSpinner();
	}
	private void initialSpinner()
	{
		JLabel label4 = new JLabel("Теплопроводность: ");
		JLabel label5 = new JLabel("Дельта времени: ");
		JLabel label6 = new JLabel("Количество узлов: ");
		label4.setBounds(100, 80, 150, 20);
		label5.setBounds(100, 50, 150, 20);
		label6.setBounds(100, 20, 150, 20);
		spinnerA.setBounds(220, 80, 50, 20);
		spinner.setBounds(220, 50, 50, 20);
		spinnerNode.setBounds(220, 20, 50, 20);
		spinnerNode.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				synchronized(PanelGraphics.key)
				{
					Main.sizeNodes = (int)spinnerNode.getValue();
					Main.centerNodes = Main.sizeNodes / 2 + 1;
					PanelGraphics.setChangeModelSpinners();
					temper = new double[Main.sizeNodes];
					setTemperature();
					computTemper = initComputTemper();
					PanelGraphics.reconfigSizeList();
					label2.setText("T" + Main.sizeNodes);
					BarGL.updateSizeVertex();
				}
			}		
		});
		spinner.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent event) 
			{
				computTemper.setDeltaTime((double)model.getValue());
			}		
		});
		spinnerA.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				computTemper.setA((double)modelA.getValue());
			}			
		});
		
		
		add(label4);
		add(label5);
		add(label6);
		add(spinnerA);
		add(spinner);
		add(spinnerNode);
	}
	public static void setTemperature()
	{
		synchronized(PanelGraphics.key)
		{
			temper[0] = (double)spinnerTemperLeft.getValue();
			temper[Main.sizeNodes - 1] = (double)spinnerTemperRight.getValue();
			double summ = temper[Main.sizeNodes - 1] - temper[0];
			double offset = summ / Main.sizeNodes;
			for(int i = 1; i < (Main.sizeNodes - 1); i++)
				temper[i] = temper[0] + offset * i;
			BarGL.updateBufferColor();
		}
	}
	public static void disableSpinnerTempers()
	{
		spinnerTemperLeft.setEnabled(false);
		spinnerTemperRight.setEnabled(false);
	}
	public static void enableSpinnerTempers()
	{
		spinnerTemperLeft.setEnabled(true);
		spinnerTemperRight.setEnabled(true);
	}
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		String str;
		g.setColor(Color.BLACK);
		g.setFont(font);
		str = String.format("%.2f", computTemper.getLastTime());
		g.drawString("Время: " + str + " ед.", getWidth() / 2 - 50, 50);
		g.drawPolygon(ramka);
		if(Main.Status != Main.STOP)
		{
			if(currentList == Main.sizeNodes - 1)dList = -1;
			else if(currentList == 0)dList = 1;
			
			currentList += dList;
			int startX;
			int startY;
			int finishX;
			int finishY;
			int dx = ramka.xpoints[0] + 1;
			float l = 320.0f / 430.0f; //длина графика на максимальное количество элементов в списке
			int cList;
			for(int j = 0; j < 3; j++)
			{
				if(j == 2)
					cList = currentList;
				else
					cList = j * (Main.sizeNodes - 1);
				ArrayList<Double> list = PanelGraphics.genList.get(cList);
				startX = dx;
				for(int i = 1; i < list.size(); i++)
				{
					startY = 58 - list.get(i - 1).intValue() / 20;
					finishX = (int)(dx + i * l);
					finishY = 58 - list.get(i).intValue() / 20;
					RGB rgb = ColorTemper.getColor(list.get(i).floatValue());
					Color.RGBtoHSB(rgb.int_r, rgb.int_g, rgb.int_b, color);
					g.setColor(Color.getHSBColor(color[0], color[1], color[2]));
					g.drawLine(startX, startY, finishX, finishY);
					startX = finishX;
				}
			}
		}
		else
			currentList = 0;
	}
	private void initialButton()
	{
		buttonTwo.setBounds(688, 80, 90, 20);
		button.setBounds(578, 80, 90, 20);
		buttonTwo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				if(Main.Status == Main.RUN)
				{
					buttonTwo.setText("Продолжить");
					Main.Status = Main.PAUSE;
				}
				else if(Main.Status == Main.PAUSE)
				{
					buttonTwo.setText("Пауза");
					Main.Status = Main.RUN;
				}
			}			
		});
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				if(Main.Status == Main.STOP)
				{
					Main.Status = Main.RUN;
					buttonTwo.setEnabled(true);
					button.setText("Стоп");
					Main.leftPanel.disableAllFunction();
					Main.rightPanel.disableAllFunction();
					disableSpinnerTempers();
					spinnerNode.setEnabled(false);
				}
				else if(Main.Status == Main.RUN || Main.Status == Main.PAUSE)
				{
					if(Main.Status == Main.PAUSE)
						buttonTwo.setText("Пауза");
					Main.leftPanel.enableAllFunction();
					Main.rightPanel.enableAllFunction();
					enableSpinnerTempers();
					spinnerNode.setEnabled(true);
					buttonTwo.setEnabled(false);
					Main.Status = Main.STOP;
					button.setText("Старт");
					computTemper = initComputTemper();
					PanelGraphics.clearLists();
					Main.leftPanel.reset();	
					Main.rightPanel.reset();
					setTemperature();
				}
			}		
		});
		add(button);
		add(buttonTwo);
	}
	private static ComputTemper initComputTemper()
	{
		IFunDiffTemperature left = new IFunDiffTemperature()
		{
			@Override
			public double getCurrentTemperature(double dtime) 
			{	
				return Main.leftPanel.getTemperature(dtime);
			}			
		};
				
		//функция нагрева стержня справа
		IFunDiffTemperature right = new IFunDiffTemperature()
		{
			@Override
			public double getCurrentTemperature(double dtime) 
			{
				return Main.rightPanel.getTemperature(dtime);
			}	
		};
				
		//толщина стержня
		double l = 101; //ед
				
		//коэффициент приращения времени
		double deltaTime = (double)spinner.getValue();
				
		//коэффициент теплопроводности
		double a = (double)spinnerA.getValue();
		return new ComputTemper(l, Main.sizeNodes, temper, deltaTime, a, left, right);
	}
}
