package com.vorobiev.heatingMat;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class Main 
{
	public static final int RUN = 1;
	public static final int PAUSE = 2;
	public static final int STOP = 3;
	public static int Status = 3;
	public static final int width = 1356;
	public static final int height = 700;
	public static final int maxSizeNodes = 301;
	public static int sizeNodes = 91;
	public static int centerNodes = sizeNodes / 2 + 1;
	public static Thread thread;
	public static MyFrame frame;
	public static Container frameContainer;
	public static PanelFunction leftPanel;
	public static PanelFunction rightPanel;
	public static CenterPanel centerPanel;
	public static PanelGraphics graphicsPanel;
	private static GLCanvas glCanvas;
	private static volatile boolean run = true;
	private static int sleep;
	public static void main(String[] args) 
	{
		GLProfile glp = GLProfile.getDefault();
		final GLCapabilities caps = new GLCapabilities(glp);
		caps.setDoubleBuffered(true);
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run()
			{			
				initLookAndFeel();
				initFrame();
				initCenterPanel();
				initBarGL(caps);	
				initFunctionPanels();
				initGraphicsPanel();
				frame.setVisible(true);
				new Thread(update).start();
			}
		});
	}
	private static void initLookAndFeel()
	{
		try 
		{
			try 
			{
				for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
				{
					if ("Windows".equals(info.getName())) 
					{
						UIManager.setLookAndFeel(info.getClassName());
			            break;
			        }
			    }
			} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		} catch (UnsupportedLookAndFeelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	private static void initFrame()
	{
		frame = new MyFrame("Нагрев Стержня", width, height, Color.WHITE);
		URL imgURL = Main.class.getResource("2.png");
		frame.setIconImage(Toolkit.getDefaultToolkit().createImage(imgURL));
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent arg0) 
			{
				run = false;
				
				CenterPanel.computTemper.dispose();
				if(CenterPanel.computTemper == null)
					System.out.println("computTemeper is null");
				System.exit(0);
			}
		});
		frameContainer = frame.getContentPane();
		JLabel label = new JLabel("Воробьев С. В.   2015г");
		label.setForeground(Color.GRAY);
		label.setFont(new Font("Serif", 10, 10));
		label.setBounds(1230, 650, 200, 20);
		frameContainer.add(label);
	}
	private static void initCenterPanel()
	{
		centerPanel = new CenterPanel(5, 5, 1330, 110, Color.WHITE);
		Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.RED, Color.RED);
		Border titled = BorderFactory.createTitledBorder(border, "Нагрев/Охлаждение стержня методом конечных разностей", 2, 0);
		centerPanel.setBorder(titled);
		frameContainer.add(centerPanel);
	}
	private static void initBarGL(GLCapabilities caps)
	{
		glCanvas = new GLCanvas(caps);
		glCanvas.setBounds(345, 127, 650, 170);
		glCanvas.addGLEventListener(new GLRender());
		frameContainer.add(glCanvas);
	}
	private static void initFunctionPanels()
	{
		leftPanel = new PanelFunction(5, 120, 340, 180, Color.WHITE);
		rightPanel = new PanelFunction(995, 120, 340, 180, Color.WHITE);
		Border lborder = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.RED, Color.RED);
		Border ltitled = BorderFactory.createTitledBorder(lborder, "Функция (нагрева / охлаждения) слева", 2, 0);
		leftPanel.setBorder(ltitled);
		leftPanel.setSelectedFunction(2);
		frameContainer.add(leftPanel);
		
		Border rborder = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.RED, Color.RED);
		Border rtitled = BorderFactory.createTitledBorder(rborder, "Функция (нагрева / охлаждения) справа", 2, 0);
		rightPanel.setBorder(rtitled);
		rightPanel.setSelectedFunction(1);
		frameContainer.add(rightPanel);
	}
	
	private static void initGraphicsPanel()
	{
		graphicsPanel = new PanelGraphics(5, 330, 1330, 320, Color.WHITE);
		frameContainer.add(graphicsPanel);	
		
	}
	static Runnable update = new Runnable()
	{
		@Override
		public void run()
		{
			long time = System.nanoTime();
			float deltaTime = 0;
			while(!Thread.currentThread().isInterrupted() && run)
			{
				deltaTime = (System.nanoTime() - time) / 1000000000.0f;
				time = System.nanoTime();
				sleep = 0;
				if(deltaTime < 0.016f)
				{
					sleep = (int)(1000.0f * (0.016f - deltaTime));
					try {
						Thread.sleep(sleep);
						//Thread.sleep(1000);
					} catch (InterruptedException e) {
						Thread.interrupted();
						e.printStackTrace();
					}
				}
				CenterPanel.lfps.setText(String.valueOf((int)(1 / (deltaTime + sleep * 0.001f))));
				frame.getContentPane().repaint();
				glCanvas.display();
			}
		}
	};
}
