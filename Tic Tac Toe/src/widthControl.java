
import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.*;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;
import java.util.Scanner;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class widthControl implements Runnable {
	public widthControl() {
		
	}
		public static int [] moveWidth(int [] start , int [] end) {
			//geometry field
			int width = 60;//in mm
			int radW = 19; //in mm
			int angleW = width/radW;
			//control motor
			RegulatedMotor motorWidth = new EV3LargeRegulatedMotor(MotorPort.C);
			double angleRotW = (end[0]-start[0])*angleW*180/(Math.PI); //in degrees
			motorWidth.rotate((int)angleRotW);
			start[0]= end[0];
			return start;
		}
				
		public void run() {
			int [] position= {0,0};
			int [] destination= {1,5};
			position = moveWidth(position,destination);
		}
		
		
	
}