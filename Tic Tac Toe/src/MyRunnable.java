import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.*;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;

import java.util.ArrayDeque;
import java.util.Scanner;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class MyRunnable implements Runnable {
	public static ArrayDeque<Opdracht> Deque = new ArrayDeque<>();
	
    private int var;

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
