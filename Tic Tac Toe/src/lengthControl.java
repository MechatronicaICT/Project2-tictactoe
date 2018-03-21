import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.*;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;
import java.util.Scanner;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class lengthControl implements Runnable {
	public lengthControl() {
		
	}
		
		public static int [] moveLength(int [] start , int [] end) {
			//geometry of field
			int length = 20;//in mm
			int radL = 15;//in mm
			int angleL = length/radL;
			
			RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
			double angleRotL = (end[1]-start[1])*angleL*180/(Math.PI);//in degrees
			motorLength.rotate((int)angleRotL);
			start[1]= end[1];
			return start;
		}
		
		public void run() {
			int [] position= {0,0};
			int [] destination= {1,5};
			position = moveLength(position,destination);
		}
		
		
	
}