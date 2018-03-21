import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.*;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;
import java.util.Scanner;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class MyRunnable implements Runnable {
	
    private int var;

    public MyRunnable() {

    }

    public void run() {
    	
<<<<<<< HEAD
    	int [] position= {0,0};
		int [] destination= {1,5};
		position = moveWidth(position,destination);
		position = moveLength(position,destination);
       
       	}
        // code in the other thread, can reference "var" variable
=======
        while(!Thread.interrupted()) {
    		try {
    			Thread.sleep(1000);
    			System.out.println("thread 1" +  Thread.currentThread().getName());
    			
    			
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}   
    		   
         }
        // code in the other thread, can reference "var" variable
    }
    
    public void writesomething() {
    	System.out.println("thread 12222");
    }

>>>>>>> f85fa85e8fe8e2b68212098a1c1f66c032309e22
}
