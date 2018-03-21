

import lejos.hardware.BrickFinder;
//import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.ev3.EV3;
//import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;

public class program {
	public static EV3ColorSensor colorsensor = new EV3ColorSensor(SensorPort.S1);
	 public static SampleProvider color = colorsensor.getColorIDMode();
	 public static float[] sample = new float[color.sampleSize()];
	 
	public static void main(String[]args){
		EV3 ev3 =(EV3) BrickFinder.getLocal();
 TextLCD lcd = ev3.getTextLCD();
 //color ID. 0 = red; 1 = green; 2 = blue; 3 = yellow; 4 = magenta; 5 = orange; 6 = white; 7 = black; 8 = pink; 9 = gray; 10 = light gray	 
 //public static final float limitColor=2;
 color.fetchSample(sample, 0);
 int colorId=(int)sample[0];
 //https://www.programcreek.com/java-api-examples/?api=lejos.hardware.sensor.EV3ColorSensor
 lcd.drawInt(colorId,0,0);
 }
 }
 
