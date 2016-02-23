package com.vorobiev.heatingMat;

import java.awt.Color;
import java.awt.Container;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class MyFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyFrame(String title, int width, int height, Color color)
	{
		this.setLayout(null);
		this.setTitle(title);
		this.setSize(width, height);
		Toolkit tools = Toolkit.getDefaultToolkit();
		this.setLocation(tools.getScreenSize().width / 2 - width / 2, tools.getScreenSize().height / 2 - height / 2 - 20);
		Container container = this.getContentPane();
		container.setBackground(color);
	}
}
