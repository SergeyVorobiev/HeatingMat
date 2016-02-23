package com.vorobiev.heatingMat;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public class GLRender implements GLEventListener 
{
	public static final float WIDTH = 111;
	public static final float HEIGHT = 10;
	@Override
	public void display(GLAutoDrawable draw) 
	{
		GL2 gl = draw.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		BarGL.draw(gl);
	}

	@Override
	public void dispose(GLAutoDrawable arg0) 
	{

	}

	@Override
	public void init(GLAutoDrawable arg0) 
	{
	}

	@Override
	public void reshape(GLAutoDrawable draw, int x, int y, int width, int height) 
	{
		GL2 gl = draw.getGL().getGL2();
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glOrthof(0.0f, WIDTH, 0.0f, HEIGHT, -1.0f, 1.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
}
