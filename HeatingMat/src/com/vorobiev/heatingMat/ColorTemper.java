package com.vorobiev.heatingMat;

public class ColorTemper 
{
	public static class RGB
	{
		private static final RGB rgb = new RGB();
		private RGB(){}
		public static RGB getInstance()
		{
			return rgb;
		}
		public int int_r = 0;
		public int int_g = 0;
		public int int_b = 0;
		public float float_r = 0;
		public float float_g = 0;
		public float float_b = 0;
	}
	private static final RGB rgb = RGB.getInstance();
	public static RGB getCurrentColor()
	{
		return rgb;
	}
	public static RGB getColor(float temperature)
	{
		if(temperature < -750)
		{
			rgb.int_r = 0;
			rgb.int_g = 0;
			rgb.int_b = 0;
		}
		else if(temperature >= -750 && temperature < -495)
		{
			rgb.int_r = 0;
			rgb.int_g = 0;
			rgb.int_b = 750 - (int)(temperature * -1);
		}
		else if(temperature >= -495 && temperature < -240)
		{
			rgb.int_r = 0;
			rgb.int_g = 495 + (int)temperature;
			rgb.int_b = 255;
		}
		else if(temperature >= -240 && temperature < 15)
		{
			rgb.int_r = 0;
			rgb.int_g = 255;
			rgb.int_b = 15 - (int)temperature;
		}
		else if(temperature >= 15 && temperature < 270)
		{
			rgb.int_r = (int)temperature - 15;
			rgb.int_g = 255;
			rgb.int_b = 0;
		}
		else if(temperature >= 270 && temperature < 525)
		{
			rgb.int_r = 255;
			rgb.int_g = 525 - (int)temperature;
			rgb.int_b = 0;
		}
		else if(temperature >= 525 && temperature <= 750)
		{
			rgb.int_r = 255;
			rgb.int_g = (int)temperature - 525;
			rgb.int_b = (int)temperature - 525;
		}
		else
		{
			rgb.int_r = 255;
			rgb.int_g = 225;
			rgb.int_b = 225;
		}
		rgb.float_r = (float)rgb.int_r / 255.0f;
		rgb.float_g = (float)rgb.int_g / 255.0f;
		rgb.float_b = (float)rgb.int_b / 255.0f;
		return rgb;
	}
}
