import java.util.ArrayDeque;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;


public class MoveWidth implements Runnable{
	private double angleRotW;
	RegulatedMotor motorWidth;
	
	public MoveWidth(double angle, RegulatedMotor motor) {
		motorWidth = motor;
		angleRotW = angle;
	}
	public void run(){
		motorWidth.rotate((int)angleRotW);
	}

}
