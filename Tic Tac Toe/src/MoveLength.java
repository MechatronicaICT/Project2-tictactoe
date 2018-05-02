import java.util.ArrayDeque;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;


public class MoveLength implements Runnable{
	private double angleRotL;
	RegulatedMotor motorLength;
	
	public MoveLength(double angle, RegulatedMotor motor) {
		motorLength = motor;
		angleRotL = angle;
	}
	public void run(){
		motorLength.rotate(-(int)angleRotL);
	}

}
