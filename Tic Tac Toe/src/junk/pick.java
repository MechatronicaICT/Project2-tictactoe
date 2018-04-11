package junk;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class pick {
	public static void main(String[] args) {
		//if(position=pickposition){
		pick();
		//}
	
		//if(position=placeposition){
			place();
		//}
		}
	
	//pick a block:
	
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
		
		//movement up
		
		motorZ.rotate(-(int)angleRotZ);
		
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
		
		//movement up
		
		motorZ.rotate(-(int)angleRotZ);
		
	}
}
