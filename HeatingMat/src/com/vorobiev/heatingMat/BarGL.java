package com.vorobiev.heatingMat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.media.opengl.GL2;

import com.vorobiev.heatingMat.ColorTemper.RGB;

public class BarGL 
{
	public static final int maxSizeNodes = 301;
	public static final int maxVertex = (301 + 1) * 2;
	public static int sizeVertex;
	public static int center;
	private static FloatBuffer color;
	private static FloatBuffer vertex;
	private static int k;
	private static int j;
	private static float alpha = 0.41f;
	private static float da = 0.003f;
	static
	{
		ByteBuffer byteColor = ByteBuffer.allocateDirect(maxVertex * 4 * 4);
		byteColor.order(ByteOrder.nativeOrder());
		color = byteColor.asFloatBuffer();
		
		ByteBuffer byteVertex = ByteBuffer.allocateDirect(maxVertex * 4 * 3);
		byteVertex.order(ByteOrder.nativeOrder());
		vertex = byteVertex.asFloatBuffer();
		updateSizeVertex();	
	}
	public static void updateSizeVertex()
	{
		synchronized(PanelGraphics.key)
		{
			float x;
			k = 0;
			j = 0;
			int l = 0;
			center = Main.sizeNodes / 2;
			sizeVertex = (Main.sizeNodes + 1) * 2;
			for(int i = 0; i <= Main.sizeNodes; i++)
			{
				
				x = GLRender.WIDTH / Main.sizeNodes * i;
				if(x == 0)
					x = 0.1f;
				vertex.put(k++, x);
				vertex.put(k++, 0);
				vertex.put(k++, 0);
				vertex.put(k++, x);
				vertex.put(k++, 9.9f);
				vertex.put(k++, 0);
				if(i == center)
					setColorTwoVertex((float)CenterPanel.temper[l]);
				else
					setColorTwoVertex((float)CenterPanel.temper[l++]);
			}
		}
	}
	public static void updateBufferColor()
	{
		synchronized(PanelGraphics.key)
		{
			int l = 0;
			j = 0;
			for(int i = 0; i <= Main.sizeNodes; i++)
			{
				if(i == center)
					setColorTwoVertex((float)CenterPanel.temper[l]);
				else
					setColorTwoVertex((float)CenterPanel.temper[l++]);
			}
		}
	}
	private static void setColorTwoVertex(float valueTemper)
	{
		
		RGB rgb = ColorTemper.getColor(valueTemper);
		color.put(j++, rgb.float_r);
		color.put(j++, rgb.float_g);
		color.put(j++, rgb.float_b);
		color.put(j++, 1.0f);
		color.put(j++, rgb.float_r);
		color.put(j++, rgb.float_g);
		color.put(j++, rgb.float_b);
		color.put(j++, 1.0f);
	}
	public static void draw(GL2 gl)
	{
		synchronized(PanelGraphics.key)
		{
			vertex.position(0);
			color.position(0);
			gl.glEnableClientState(GL2.GL_VERTEX_ARRAY); //активируем массив вершин
			gl.glEnableClientState(GL2.GL_COLOR_ARRAY); //активируем массив цвета
			gl.glColorPointer(4, GL2.GL_FLOAT, 0, color); //укажем массив содержащий цвет
			gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertex); //укажем массив содержащий координаты вершин
		
			setColorAlpha(color, 1);
			if(Main.Status != Main.RUN)
			{	
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
				gl.glDrawArrays(GL2.GL_QUAD_STRIP, 0, sizeVertex);
				alpha += da;
				if(alpha >= 1 || alpha <= 0.4f)da *= -1;
				setColorAlpha(color, alpha);
			}
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
			gl.glDrawArrays(GL2.GL_QUAD_STRIP, 0, sizeVertex);
		}
	}
	public static void setColorAlpha(FloatBuffer color, float level)
	{
		if(color.get(3) == level)return;
		for(int i = 0; i < color.capacity() / 4; i++)
			color.put(i * 4 + 3, level);
	}
}
