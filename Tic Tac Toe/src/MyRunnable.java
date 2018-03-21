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
    	
    	int [] position= {0,0};
		int [] destination= {1,5};
		position = moveWidth(position,destination);
		position = moveLength(position,destination);
       
       	}
        // code in the other thread, can reference "var" variable
}
