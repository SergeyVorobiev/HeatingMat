package com.vorobiev.heatingMat;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PanelFunction extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ButtonGroup leftGroup = new ButtonGroup();
	private JRadioButton lin = new JRadioButton();
	private JRadioButton sin = new JRadioButton();
	private JRadioButton exp = new JRadioButton();
	private JRadioButton log = new JRadioButton();
	
	private JCheckBox invertBox = new JCheckBox();
	private JCheckBox coldHotBox = new JCheckBox();
	private SpinnerNumberModel modelSin = new SpinnerNumberModel(0.01, 0.01, 10, 0.01);
	private SpinnerNumberModel modelSinTwo = new SpinnerNumberModel(0, -0.5, 0.5, 0.01);
	private SpinnerNumberModel modelSinThree = new SpinnerNumberModel(740, 100, 745, 1.0);
	private SpinnerNumberModel modelLinOne = new SpinnerNumberModel(1, 0, 10, 0.1);
	private SpinnerNumberModel modelMax = new SpinnerNumberModel(740, -745, 745, 1.0);
	private SpinnerNumberModel modelMin = new SpinnerNumberModel(-740, -745, 745, -1.0);
	private SpinnerNumberModel modelExpOne = new SpinnerNumberModel(1, 0, 10, 0.1);
	private SpinnerNumberModel modelExpTwo = new SpinnerNumberModel(0.08, 0, 1, 0.01);
	private SpinnerNumberModel modelLogOne = new SpinnerNumberModel(2, 0, 100, 1.0);
	private SpinnerNumberModel modelLogTwo = new SpinnerNumberModel(100, 0, 100000, 100.0);
	private SpinnerNumberModel modelStartTemper = new SpinnerNumberModel(0, -730, 730, 1.0);
	private SpinnerNumberModel modelSinStartTemper = new SpinnerNumberModel(0, -730, 730, 1.0);
	
	private JSpinner spinnerSin = new JSpinner(modelSin);
	private JSpinner spinnerSinTwo = new JSpinner(modelSinTwo);
	private JSpinner spinnerSinThree = new JSpinner(modelSinThree);
	private JSpinner spinnerLinOne = new JSpinner(modelLinOne);
	private JSpinner spinnerMax = new JSpinner(modelMax);
	private JSpinner spinnerMin = new JSpinner(modelMin);
	private JSpinner spinnerExpOne = new JSpinner(modelExpOne);
	private JSpinner spinnerExpTwo = new JSpinner(modelExpTwo);
	private JSpinner spinnerLogOne = new JSpinner(modelLogOne);
	private JSpinner spinnerLogTwo = new JSpinner(modelLogTwo);
	private JSpinner spinnerStartTemper = new JSpinner(modelStartTemper);
	private JSpinner spinnerStartSinTemper = new JSpinner(modelSinStartTemper);
	
	private int selectedFunction = 0;
	
	private double sin1;
	private double sin2;
	private double sin3;
	private double lin1;
	private double exp1;
	private double exp2;
	private double log1;
	private double log2;
	private double startTemper = 0;
	private double sinMinTemperatur = -740;
	private double sinMaxTemperatur = 740;
	private double sinStartTemper = 0;
	private double startPointSin = 0;
	private int hot = 1;
	private int hotExp = 1;
	private double timeExp = 0;
	private double alpha = 0;
	private volatile boolean  invert = true;
	private double time = 0;
	private double timesin = 0;
	private int k = 1;
	public static final double MAX_T = 750;
	public static final double MIN_T = -750;
	public static final double MAX_A = 745;
	
	public PanelFunction(int x, int y, int width, int height, Color color)
	{
		this.setLayout(null);
		this.setBounds(x, y, width, height);
		this.setBackground(color);
		
		lin.setSelected(true);
		spinnerSin.setEnabled(false);
		spinnerSinTwo.setEnabled(false);
		spinnerSinThree.setEnabled(false);
		spinnerStartSinTemper.setEnabled(false);
		spinnerLinOne.setEnabled(true);
		spinnerMax.setEnabled(true);
		spinnerMin.setEnabled(true);
		spinnerExpOne.setEnabled(false);
		spinnerExpTwo.setEnabled(false);
		spinnerLogOne.setEnabled(false);
		spinnerLogTwo.setEnabled(false);
		spinnerStartTemper.setEnabled(true);
		invertBox.setEnabled(true);
		
		leftGroup.add(lin);
		leftGroup.add(sin);
		leftGroup.add(exp);
		leftGroup.add(log);
		sin1 = (double)modelSin.getValue();
		sin2 = (double)modelSinTwo.getValue();
		sin3 = (double)modelSinThree.getValue();
		lin1 = (double)modelLinOne.getValue();
		exp1 = (double)modelExpOne.getValue();
		exp2 = (double)modelExpTwo.getValue();
		log1 = (double)modelLogOne.getValue();
		log2 = (double)modelLogTwo.getValue();
		initLinFunction();
		initSinFunction();
		initExpFunction();
		initLogFunction();
		initOtherFunction();	
	}
	private void initLinFunction()
	{
		JLabel llin1 = new JLabel("* t");
		JLabel llin2 = new JLabel("Макс");
		JLabel llin3 = new JLabel("Мин");
		
		lin.setBounds(10, 25, 20, 20);
		llin2.setBounds(10, 150, 30, 20);
		llin3.setBounds(110, 150, 30, 20);
		spinnerLinOne.setBounds(35, 25, 50, 20);
		llin1.setBounds(90, 25, 40, 20);
		
		lin.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				selectedFunction = 0;
				setEnableFunctionLin();
			}
		});
		spinnerLinOne.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				lin1 = (double) modelLinOne.getValue();
			}		
		});
		
		add(spinnerLinOne);
		add(lin);	
		add(llin1);
		add(llin2);
		add(llin3);
	}
	
	private void initSinFunction()
	{
		JLabel lsin1 = new JLabel("sin(");
		JLabel lsin2 = new JLabel("* t) +");
		JLabel lsin3 = new JLabel("Амплитуда");
		JLabel lsinStart = new JLabel("Нач темпер");
		
		sin.setBounds(10, 55, 20, 20); //переключатель
		lsin1.setBounds(35, 55, 50, 20);//надпись
		spinnerSin.setBounds(60, 55, 50, 20);
		lsin2.setBounds(115, 55, 30, 20); //надпись
		spinnerSinTwo.setBounds(145, 55, 50, 20);
		lsin3.setBounds(210, 55, 80, 20); //надпись
		spinnerSinThree.setBounds(280, 55, 50, 20);
		lsinStart.setBounds(210, 25, 70, 20);
		spinnerStartSinTemper.setBounds(280, 25, 50, 20);
		
		sin.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				selectedFunction = 1;
				setEnableFunctionSin();
			}
		
		});
		spinnerSin.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				sin1 = (double)spinnerSin.getValue();
			}		
		});
		spinnerSinTwo.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				sin2 =	(double)spinnerSinTwo.getValue();
				sin3 = (double)spinnerSinThree.getValue();
				double maxA = (1 - Math.abs(sin2)) * MAX_T;
				if(maxA < sin3)
				{	
					sin3 = maxA;
					spinnerSinThree.setValue(sin3);
				}
				startPointSin = sin2 * MAX_T;
				sinMaxTemperatur = startPointSin + sin3;
				sinMinTemperatur = startPointSin - sin3;
				
				if(sinMinTemperatur > (double)spinnerStartSinTemper.getValue())
					spinnerStartSinTemper.setValue(sinMinTemperatur);
				else if(sinMaxTemperatur < (double)spinnerStartSinTemper.getValue())
					spinnerStartSinTemper.setValue(sinMaxTemperatur);
				alpha = Math.asin(((double)spinnerStartSinTemper.getValue() - startPointSin) / sin3);
				if(hot == -1)
					alpha = Math.PI - alpha;
			}		
		});
		spinnerSinThree.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				sin3 = (double)spinnerSinThree.getValue();
				double maxA = (1 - Math.abs(sin2)) * MAX_T;
				if(maxA < sin3)
				{	
					sin3 = maxA;
					spinnerSinThree.setValue(sin3);
				}
				sinMaxTemperatur = startPointSin + sin3;
				sinMinTemperatur = startPointSin - sin3;
				if(sinMinTemperatur > (double)spinnerStartSinTemper.getValue())
					spinnerStartSinTemper.setValue(sinMinTemperatur);
				else if(sinMaxTemperatur < (double)spinnerStartSinTemper.getValue())
					spinnerStartSinTemper.setValue(sinMaxTemperatur);
				alpha = Math.asin(((double)spinnerStartSinTemper.getValue() - startPointSin) / sin3);
				if(hot == -1)
					alpha = Math.PI - alpha;
			}		
		});
		spinnerStartSinTemper.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0) {
				sinStartTemper = (double)spinnerStartSinTemper.getValue();
				if(sinStartTemper < sinMinTemperatur)
				{
					sinStartTemper = sinMinTemperatur + 1;
					spinnerStartSinTemper.setValue(sinStartTemper);
				}
				else if(sinStartTemper > sinMaxTemperatur)
				{
					sinStartTemper = sinMaxTemperatur - 1;
					spinnerStartSinTemper.setValue(sinStartTemper);
				}
				alpha = Math.asin(((double)spinnerStartSinTemper.getValue() - startPointSin) / sin3);
				if(hot == -1)
					alpha =  Math.PI - alpha;
			}	
		});
		add(lsin1);
		add(lsinStart);
		add(lsin2);
		add(lsin3);
		add(sin);
		add(spinnerSin);
		add(spinnerSinTwo);
		add(spinnerSinThree);
		add(spinnerStartSinTemper);
	}

	private void initExpFunction()
	{
		JLabel lexp1 = new JLabel("* e ^ (");
		JLabel lexp2 = new JLabel("* t)");
		exp.setBounds(10, 85, 20, 20);
		spinnerExpOne.setBounds(35, 85, 50, 20);
		lexp1.setBounds(90, 85, 40, 20);
		spinnerExpTwo.setBounds(125, 85, 50, 20);
		lexp2.setBounds(180, 85, 40, 20);
		exp.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				selectedFunction = 2;
				setEnableFunctionExp();
			}
		});
		spinnerExpOne.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				exp1 = (double)spinnerExpOne.getValue();
			}	
		});
		spinnerExpTwo.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				exp2 = (double)spinnerExpTwo.getValue();
			}	
		});
		add(exp);
		add(lexp1);
		add(lexp2);
		add(spinnerExpOne);
		add(spinnerExpTwo);
	}
	
	private void initLogFunction()
	{
		JLabel llog1 = new JLabel("* ln(");
		JLabel llog2 = new JLabel("* t)");
		log.setBounds(10, 115, 20, 20);
		spinnerLogOne.setBounds(35, 115, 50, 20);
		llog1.setBounds(90, 115, 30, 20);
		spinnerLogTwo.setBounds(115, 115, 65, 20);
		llog2.setBounds(185, 115, 30, 20);
		log.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				selectedFunction = 3;
				setEnableFunctionLog();
			}
		
		});
		spinnerLogOne.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				log1 = (double)spinnerLogOne.getValue();
			}		
		});
		spinnerLogTwo.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				log2 = (double)spinnerLogTwo.getValue();
			}
		});
		add(spinnerLogOne);
		add(llog1);
		add(spinnerLogTwo);
		add(llog2);
		add(log);
	}
	
	private void initOtherFunction()
	{
		JLabel text = new JLabel("Инверт(Макс/Мин)");
		JLabel temper = new JLabel("Нач темпер");
		JLabel cold = new JLabel("Нагрев / Охлажд");
		temper.setBounds(210, 85, 100, 20);
		spinnerStartTemper.setBounds(280, 85, 50, 20);
		spinnerMax.setBounds(45, 150, 50, 20);
		spinnerMin.setBounds(140, 150, 50, 20);
		cold.setBounds(205, 115, 120, 20);
		text.setBounds(200, 150, 120 , 20);
		invertBox.setBounds(310, 150, 20, 20);
		invertBox.setSelected(invert);
		coldHotBox.setSelected(true);
		coldHotBox.setBounds(310, 115, 20, 20);
		coldHotBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				hot = -hot;	
				hotExp = hot;
				alpha = Math.asin(((double)spinnerStartSinTemper.getValue() - startPointSin) / sin3);
					if(hot == -1)
						alpha = Math.PI - alpha;
			}		
		});
		invertBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				invert = !invert;
			}			
		});
		spinnerStartTemper.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				startTemper = (double)spinnerStartTemper.getValue();
				double leftMax = (double)spinnerMax.getValue();
				double leftMin = (double)spinnerMin.getValue();
				if(startTemper + 5 > leftMax)
				{
					startTemper += 5;
					spinnerMax.setValue(startTemper);
				}
				if( startTemper - 5 < leftMin)
				{
					startTemper -= 5;
					spinnerMin.setValue(startTemper);
				}
			}	
		});
		spinnerMax.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				double max = (double)spinnerMax.getValue();
				if(max - 5 < startTemper)
					spinnerMax.setValue(startTemper + 5);
			}		
		});
		spinnerMin.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				double min = (double)spinnerMin.getValue();
				if(min + 5 > startTemper)
					spinnerMin.setValue(startTemper - 5);
			}
		});
		add(spinnerMax);
		add(spinnerMin);
		add(cold);
		add(invertBox);
		add(coldHotBox);
		add(spinnerStartTemper);
		add(temper);
		add(text);
	}
	
	public void setEnableFunctionLin()
	{
		spinnerSin.setEnabled(false);
		spinnerSinTwo.setEnabled(false);
		spinnerSinThree.setEnabled(false);
		spinnerStartSinTemper.setEnabled(false);
		spinnerLinOne.setEnabled(true);
		spinnerMax.setEnabled(true);
		spinnerMin.setEnabled(true);
		spinnerExpOne.setEnabled(false);
		spinnerExpTwo.setEnabled(false);
		spinnerLogOne.setEnabled(false);
		spinnerLogTwo.setEnabled(false);
		spinnerStartTemper.setEnabled(true);
		invertBox.setEnabled(true);
	}

	public void setEnableFunctionSin()
	{
		spinnerSin.setEnabled(true);
		spinnerSinTwo.setEnabled(true);
		spinnerSinThree.setEnabled(true);
		spinnerStartSinTemper.setEnabled(true);
		spinnerLinOne.setEnabled(false);
		spinnerMax.setEnabled(false);
		spinnerMin.setEnabled(false);
		spinnerExpOne.setEnabled(false);
		spinnerExpTwo.setEnabled(false);
		spinnerLogOne.setEnabled(false);
		spinnerLogTwo.setEnabled(false);
		spinnerStartTemper.setEnabled(false);
		invertBox.setEnabled(false);
	}

	public void setEnableFunctionExp()
	{
		spinnerSin.setEnabled(false);
		spinnerSinTwo.setEnabled(false);
		spinnerSinThree.setEnabled(false);
		spinnerStartSinTemper.setEnabled(false);
		spinnerLinOne.setEnabled(false);
		spinnerMax.setEnabled(true);
		spinnerMin.setEnabled(true);
		spinnerExpOne.setEnabled(true);
		spinnerExpTwo.setEnabled(true);
		spinnerLogOne.setEnabled(false);
		spinnerLogTwo.setEnabled(false);
		spinnerStartTemper.setEnabled(true);
		invertBox.setEnabled(true);
	}

	public void setEnableFunctionLog()
	{
		spinnerSin.setEnabled(false);
		spinnerSinTwo.setEnabled(false);
		spinnerSinThree.setEnabled(false);
		spinnerStartSinTemper.setEnabled(false);
		spinnerLinOne.setEnabled(false);
		spinnerMax.setEnabled(true);
		spinnerMin.setEnabled(true);
		spinnerExpOne.setEnabled(false);
		spinnerExpTwo.setEnabled(false);
		spinnerLogOne.setEnabled(true);
		spinnerLogTwo.setEnabled(true);
		spinnerStartTemper.setEnabled(true);
		invertBox.setEnabled(true);
	}
	
	public void disableAllFunction()
	{
		spinnerSin.setEnabled(false);
		spinnerSinTwo.setEnabled(false);
		spinnerSinThree.setEnabled(false);
		spinnerStartSinTemper.setEnabled(false);
		spinnerLinOne.setEnabled(false);
		spinnerMax.setEnabled(false);
		spinnerMin.setEnabled(false);
		spinnerExpOne.setEnabled(false);
		spinnerExpTwo.setEnabled(false);
		spinnerLogOne.setEnabled(false);
		spinnerLogTwo.setEnabled(false);
		spinnerStartTemper.setEnabled(false);
		invertBox.setEnabled(false);
		lin.setEnabled(false);
		sin.setEnabled(false);
		exp.setEnabled(false);
		log.setEnabled(false);
		coldHotBox.setEnabled(false);
	}
	
	public void enableAllFunction()
	{
		coldHotBox.setEnabled(true);
		lin.setEnabled(true);
		sin.setEnabled(true);
		exp.setEnabled(true);
		log.setEnabled(true);
		switch(selectedFunction)
		{
		case 0:
			setEnableFunctionLin();
			return;
		case 1:
			setEnableFunctionSin();
			return;
		case 2:
			setEnableFunctionExp();
			return;
		case 3:
			setEnableFunctionLog();
			return;
		}
	}
	public void setSelectedFunction(int selected)
	{
		selectedFunction = selected;
		switch(selectedFunction)
		{
		case 0:
			leftGroup.setSelected(lin.getModel(), true);
			setEnableFunctionLin();
			return;
		case 1:
			leftGroup.setSelected(sin.getModel(), true);
			setEnableFunctionSin();
			return;
		case 2:
			leftGroup.setSelected(exp.getModel(), true);
			setEnableFunctionExp();
			return;
		case 3:
			leftGroup.setSelected(log.getModel(), true);
			setEnableFunctionLog();
			return;
		}
	}
	public double getTemperature(double dTime)
	{	
		time += dTime * k; //k или 1 или -1 необходим если после достижения максимума минимума
		//нужно развернуть функцию нагрева в обратную сторону
		timesin += dTime;
		timeExp += dTime * k;
		switch(selectedFunction)
		{
		case 0:
			double result = (lin1 * time) * hot + startTemper;
			return invertLin(result);
		case 1:
			return Math.sin(sin1 * timesin + alpha) * sin3 + (double)spinnerSinTwo.getValue() * MAX_T;
		case 2:
			double result1 = exp1 * Math.exp(timeExp * exp2) * hotExp + startTemper;
			return invertExp(result1);
		case 3:
			double result2 = log1 * Math.log(log2 * timeExp + 1) * hotExp + startTemper;
			return invertExp(result2);
		}
		return 0;			
	}
	private double invertExp(double result)
	{
		double currentMax = (double)spinnerMax.getValue();
		double currentMin = (double)spinnerMin.getValue();
		if(result > currentMax)
		{
			result = currentMax;
			if(invert)
			{	
				hotExp = -hotExp;
				startTemper = result;
				timeExp = 0;
			}
			else
			{
				k = 0;
			}
		}
		if(result < currentMin)
		{
			result = currentMin;
			if(invert)
			{
				result = currentMin;
				hotExp = -hotExp;
				startTemper = result;
				timeExp = 0;
			}
			else
			{
				k = 0;
			}
		}
		return result;
	}
	private double invertLin(double result)
	{
		double currentMax = (double)spinnerMax.getValue();
		double currentMin = (double)spinnerMin.getValue();
		if(result > currentMax)
		{
			result = currentMax;
			if(invert)
				k *= -1;
			else
				k = 0;		
		}
		else if(result < currentMin)
		{
			result = currentMin;
			if(invert)
				k *= -1;
			else
				k = 0;	
		}
		return result;
	}
	public void reset()
	{
		time = 0;
		timesin = 0;
		timeExp = 0;
		k = 1;
		hotExp = hot;
		startTemper = (double)spinnerStartTemper.getValue();
	}
}
