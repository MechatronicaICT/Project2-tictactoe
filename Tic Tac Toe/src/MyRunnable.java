package test1;
import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.*;
import lejos.utility.Delay;

import java.util.ArrayDeque;
import java.util.Scanner;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

//colorsensor imports:
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
//import lejos.utility.Delay;
import lejos.hardware.ev3.EV3;
//import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;

public class MyRunnable implements Runnable {
	public static ArrayDeque<Opdracht> Deque = new ArrayDeque<>();
	
    private int var;

    static void moveXY(int [] first, int []second ){
		
		// control width
		int width = 60; //in mm
		int radW = 19;  //in mm
		int angleW = width/radW;
		
		RegulatedMotor motorWidth = new EV3LargeRegulatedMotor(MotorPort.C);
		double angleRotW = (first[0]-second[0])*angleW*180/(Math.PI);   //in degrees
		motorWidth.rotate((int)angleRotW);
		
		motorWidth.close();
		
		// control length
		int length = 20; //in mm
		int radL = 15;   //in mm
		int angleL = length/radL;
		
		RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
		double angleRotL = (first[1]-second[1])*angleL*180/(Math.PI);    //in degrees
		motorLength.rotate((int)angleRotL);
		
		motorLength.close();
	}

	// pick a block:
	static void pick(){
		
		//movement down
		int distanceZ = 30; //mm
		int radiusZ = 8; //mm
		double angleRotZ = (distanceZ/radiusZ)*180/(Math.PI);
		RegulatedMotor motorZ = new EV3LargeRegulatedMotor(MotorPort.C);
		motorZ.rotate((int)angleRotZ);
		
		//movement forward
		int distanceL = 60; //mm
		int radL = 15; //mm
		double angleRotForward = (distanceL/radL)*180/(Math.PI);
		RegulatedMotor motorForward = new EV3LargeRegulatedMotor(MotorPort.D);
		motorForward.rotate((int)angleRotForward);
		
		motorForward.close();
		
		//movement up
		motorZ.rotate(-(int)angleRotZ);
		
		motorZ.close();
		
	}
	
	//place a block:
	static void place(){
		
		//movement down
		int distanceZ = 30; //mm
		int radiusZ = 8; //mm
		double angleRotZ = (distanceZ/radiusZ)*180/(Math.PI);
		RegulatedMotor motorZ = new EV3LargeRegulatedMotor(MotorPort.C);
		motorZ.rotate((int)angleRotZ);
		
		//movement backward
		int distanceL = 60; //mm
		int radL = 15; //mm
		double angleRotForward = (distanceL/radL)*180/(Math.PI);
		RegulatedMotor motorForward = new EV3LargeRegulatedMotor(MotorPort.D);
		motorForward.rotate(-(int)angleRotForward);
		
		motorForward.close();
		
		//movement up
		motorZ.rotate(-(int)angleRotZ);
		
		motorZ.close();
		
	}
	//colorsensor
	public static EV3ColorSensor colorsensor = new EV3ColorSensor(SensorPort.S1);
	 public static SampleProvider color = colorsensor.getColorIDMode();
	 public static float[] sample = new float[color.sampleSize()];
	 
	static int measurecolor(){
			//while(true){
			//EV3 ev3 =(EV3) BrickFinder.getLocal();
	 //TextLCD lcd = ev3.getTextLCD();
	 //color ID. 0 = red; 1 = green; 2 = blue; 3 = yellow; 4 = magenta; 5 = orange; 6 = white; 7 = black; 8 = pink; 9 = gray; 10 = light gray	 
	 //public static final float limitColor=2;
	 color.fetchSample(sample, 0);
	 int colorId=(int)sample[0];
	 //https://www.programcreek.com/java-api-examples/?api=lejos.hardware.sensor.EV3ColorSensor
	 //lcd.drawInt(colorId,0,0);
	 //System.out.println(colorId);
	 colorsensor.close();
	 return colorId;
	 //Delay.msDelay(9000);
		}
	 //}
	 
    public MyRunnable(ArrayDeque<Opdracht> deque) {
    	Deque = deque;
    	
    }

    public void run() {
    	

    	int [] position= {0,0};
		int [] destination= {1,5};
		//position = moveWidth(position,destination);
		//position = moveLength(position,destination);
		
       	
        // code in the other thread, can reference "var" variable

        while(!Thread.interrupted()) {
    		try {
    			Thread.sleep(1000);
    			System.out.println("thread 1" +  Thread.currentThread().getName());
    			
    			OpdrachtZet ttt = (OpdrachtZet) Deque.peekLast();
    			
    			System.out.println(ttt.what());
    			
    		
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}   
    		   
        }
    }
    
	
    
    public void writesomething() {
    	System.out.println("thread 12222");
    }

}