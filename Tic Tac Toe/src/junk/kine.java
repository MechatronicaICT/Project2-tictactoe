package junk;

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

public class kine implements Runnable {
	
	
	public static ArrayDeque<Opdracht> arrOpdrachten = new ArrayDeque<>();
	

    public kine(ArrayDeque<Opdracht> deque) {
    	arrOpdrachten = deque;
    	
    }
	
	public void run() {
		//read array, perform task, repeat
	}
	
}