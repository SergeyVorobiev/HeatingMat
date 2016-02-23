package com.vorobiev.heatingMat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.vorobiev.heatingMat.ColorTemper.RGB;

public class PanelGraphics extends JPanel
{
	private static final long serialVersionUID = 1L;
	private float[] color = new float[3];
	private double[] temperature;
	public static final Object key = new Object();
	private Polygon leftPolygon;
	private Polygon rightPolygon;
	private Polygon centerPolygon;
	public static ArrayList<ArrayList<Double>> genList = new ArrayList<ArrayList<Double>>();
	private static int leftList = 0;
	private static int rightList = Main.sizeNodes - 1;
	private static int centerList = Main.sizeNodes / 2;
	public static boolean repeatLeft = true;
	public static boolean repeatRight = true;
	public static boolean repeatCenter = true;
	private Font font = new Font("SansSerif", Font.BOLD, 15);
	public static SpinnerNumberModel modelOne = new SpinnerNumberModel(1, 1, Main.sizeNodes, 1);
	public static SpinnerNumberModel modelTwo = new SpinnerNumberModel(Main.centerNodes, 1, Main.sizeNodes, 1);
	public static SpinnerNumberModel modelThree = new SpinnerNumberModel(Main.sizeNodes, 1, Main.sizeNodes, 1);
	public static JSpinner spinner1 = new JSpinner(modelOne);
	public static JSpinner spinner2 = new JSpinner(modelTwo);
	public static JSpinner spinner3 = new JSpinner(modelThree);
	public PanelGraphics(int x, int y, int width, int height, Color color)
	{
		this.setLayout(null);
		this.setBounds(x, y, width, height);
		this.setBackground(color);
		
		leftPolygon = new Polygon();
		leftPolygon.addPoint(0, 40);
		leftPolygon.addPoint(431, 40);
		leftPolygon.addPoint(431, 291);
		leftPolygon.addPoint(0, 291);
		
		centerPolygon = new Polygon();
		centerPolygon.addPoint(448, 40);
		centerPolygon.addPoint(879, 40);
		centerPolygon.addPoint(879, 291);
		centerPolygon.addPoint(448, 291);
		
		rightPolygon = new Polygon();
		rightPolygon.addPoint(897, 40);
		rightPolygon.addPoint(1329, 40);
		rightPolygon.addPoint(1329, 291);
		rightPolygon.addPoint(897, 291);
		
		for(int i = 0; i < Main.sizeNodes; i++)
			genList.add(new ArrayList<Double>());
		initSpinner();
		
	}
	public static void reconfigSizeList()
	{
		leftList = 0;
		rightList = Main.sizeNodes - 1;
		centerList = Main.sizeNodes / 2;
		genList.clear();
		for(int i = 0; i < Main.sizeNodes; i++)
			genList.add(new ArrayList<Double>());
	}
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		drawGrid(g);
		//System.out.println("PanelGraphics.paintComponent:" + Thread.currentThread().toString());
		synchronized(key)
		{
			drawTemperature(g);
			drawGraphics(g, genList.get(leftList), leftPolygon);
			drawGraphics(g, genList.get(rightList), rightPolygon);
			if(repeatCenter)
				drawGraphics(g, genList.get(centerList), centerPolygon);
			else
			{
				g.setColor(Color.BLACK);
				g.drawPolygon(centerPolygon);
			}
			if(repeatLeft)
				drawGraphics(g, genList.get(leftList), centerPolygon);
			if(repeatRight)
				drawGraphics(g, genList.get(rightList), centerPolygon);
		}
	}
	private void drawGrid(Graphics g)
	{
		g.setColor(Color.decode("#f4f4f4"));
		int temp = 0;
		int k = -1;
		for(int j = 0; j < 3; j++)
		{
			
			for(int i = 1; i < 43; i++)
			{
				temp = k + i * 10;
				g.drawLine(temp, 41, temp, 290);
			}
			for(int i = 1; i < 25; i++)
			{
				temp = 40 + i * 10;
				g.drawLine(k + 1, temp, k + 428, temp);
			}
			k += 450;
		}
			
	}

	public static void clearLists()
	{
		synchronized(key)
		{
			for(int i = 0; i < Main.sizeNodes; i++)
				genList.get(i).clear();
		}
	}
	private void drawTemperature(Graphics g)
	{
		if(Main.Status == Main.RUN)
		{
			temperature = CenterPanel.computTemper.getComputTemperature();
			for(int i = 0; i < Main.sizeNodes; i++)
			{	
				ArrayList<Double> list = genList.get(i);
				if(list.size() == 429)
					list.remove(0);
				list.add(temperature[i]);
			}
		}
		else 
			temperature = CenterPanel.computTemper.getTemperature();
		BarGL.updateBufferColor();
		/*
		int startDrawLine = this.getWidth() / 2 - widthObject * 3;
		for(int i = 0; i < widthObject; i++)
		{
			getRGB((int)temperature[i]);
			Color.RGBtoHSB(r, gr, b, color);
			g.setColor(Color.getHSBColor(color[0], color[1], color[2]));
			g.fillRect(startDrawLine + i * 6, 120, 6, 180);
		}	*/
		g.setFont(font);
		String str;
		double tleft = temperature[leftList];
		if(tleft > 0) g.setColor(Color.RED);
		else g.setColor(Color.BLUE);
		str = String.format("%.2f", tleft);
		g.drawString("T" + (leftList+1) + ": " + str + " ед.", 180, 310);
		
		double tcenter = temperature[centerList];
		if(tcenter > 0) g.setColor(Color.RED);
		else g.setColor(Color.BLUE);
		str = String.format("%.2f", tcenter);
		g.drawString("T" + (centerList+1) + ": " + str + " ед.", 635, 310);
		
		double tright = temperature[rightList];
		if(tright > 0) g.setColor(Color.RED);
		else g.setColor(Color.BLUE);
		str = String.format("%.2f", tright);
		g.drawString("T" + (rightList+1) + ": " + str + " ед.", 1090, 310);
	}
	private void drawGraphics(Graphics g, ArrayList<Double> list, Polygon polygon)
	{
		g.setColor(Color.BLACK);
		g.drawPolygon(polygon);
		if(Main.Status != Main.STOP)
		{
			int startX;
			int startY;
			int finishX;
			int finishY;
			startX = (0) + polygon.xpoints[0] + 1;
			for(int i = 1; i < list.size(); i++)
			{
				startY = 165 - list.get(i - 1).intValue() / 6;
				finishX = (i) +  polygon.xpoints[0] + 1;
				finishY = 165 - list.get(i).intValue() / 6;
				RGB rgb = ColorTemper.getColor(list.get(i).floatValue());
				Color.RGBtoHSB(rgb.int_r, rgb.int_g, rgb.int_b, color);
				g.setColor(Color.getHSBColor(color[0], color[1], color[2]));
				g.drawLine(startX, startY, finishX, finishY);
				startX = finishX;
			}
		}
	}
	
	private void initSpinner()
	{
		JLabel label1 = new JLabel("”зел: ");
		JLabel label2 = new JLabel("”зел: ");
		JLabel label3 = new JLabel("”зел: ");
		
		final JCheckBox checkBox = new JCheckBox();
		final JCheckBox checkBox1 = new JCheckBox();
		final JCheckBox checkBox2 = new JCheckBox();
		checkBox.setBounds(500, 10, 20, 20);
		checkBox1.setBounds(540, 10, 20, 20);
		checkBox2.setBounds(580, 10, 20, 20);
		checkBox1.setSelected(repeatCenter);
		checkBox.setSelected(repeatLeft);
		checkBox2.setSelected(repeatRight);
		
		checkBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanelGraphics.repeatLeft = checkBox.isSelected();	
			}			
		});
		checkBox1.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanelGraphics.repeatCenter = checkBox1.isSelected();
			}			
		});
		checkBox2.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PanelGraphics.repeatRight = checkBox2.isSelected();	
			}			
		});
		label1.setBounds(180, 10, 100, 20);
		label2.setBounds(630, 10, 100, 20);
		label3.setBounds(1080, 10, 100, 20);
		spinner1.setBounds(220, 10, 60, 20);
		spinner2.setBounds(670, 10, 60, 20);
		spinner3.setBounds(1120, 10, 60, 20);
		add(label1);
		add(label1);
		add(label2);
		add(label3);
		add(checkBox);
		add(checkBox1);
		add(checkBox2);
		
		spinner1.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				leftList = (int)modelOne.getValue() - 1;
			}			
		});	
		spinner2.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				centerList = (int)modelTwo.getValue() - 1;
			}			
		});	
		spinner3.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				rightList = (int)modelThree.getValue() - 1;
			}			
		});	
			
		add(spinner1);
		add(spinner2);
		add(spinner3);
	}
	public static void setChangeModelSpinners()
	{
		modelOne = new SpinnerNumberModel(1, 1, Main.sizeNodes, 1);
		modelTwo = new SpinnerNumberModel(Main.centerNodes, 1, Main.sizeNodes, 1);
		modelThree = new SpinnerNumberModel(Main.sizeNodes, 1, Main.sizeNodes, 1);
		spinner1.setModel(modelOne);
		spinner2.setModel(modelTwo);
		spinner3.setModel(modelThree);
	}
}
