
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
//import lejos.hardware.ev3.EV3;
//import lejos.hardware.lcd.LCD;
//import lejos.hardware.lcd.TextLCD;

public class Kine implements Runnable {
	private ArrayDeque<Task> Deque = new ArrayDeque<>();

	private double [] current= {0,0}; //where we currently are
	private RegulatedMotor motorWidth = new EV3LargeRegulatedMotor(MotorPort.C);
	private RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
	private RegulatedMotor motorZ = new EV3LargeRegulatedMotor(MotorPort.B);
	private int motorSpeed = 200;
	private int motorSpeedSlow = 100;
	private int motorSpeedFast= 200;
	private double width = 55.5;   //moveXY: distance between two width coordinates
	private double radW = 19;   //radius conveyor belt
	private double length = 60;    //moveXY: distance between two length coordinates
	private double radL = 15;   //radius wheels
	private double distanceZ = 47;   //pick and place: distance arm lowers/rises
	private double radiusZ = 8; //radius pinion
	private double distanceL = 27;   //pick and place: robot drives forward/backwards to pick/place
	private double distanceShiftW = 40; //Shift coordinate system to sensor
	private double distanceShiftL = 60; //Same
	private MoveLength moveL; //for threads
	private MoveWidth moveW; //for threads

	
	public boolean exit_program = false;
	public boolean scanDone = false;
	public int[] Move = { 0 , 0};
	


	public Kine(ArrayDeque<Task> deque) {
		//Communication array with Game main 
		Deque = deque;
	}

	public void run() {
		while(!Thread.interrupted()) {
			
			try {	
				//check if there is something in the array
				if(!Deque.isEmpty()) {

					motorWidth.setSpeed(motorSpeed);
					motorLength.setSpeed(motorSpeed);
					motorZ.setSpeed(100);
					
					Task tsk = Deque.peekFirst();
					//System.out.println(tsk.getClass().getName());
					executeTask(tsk);

				}    			

				if (exit_program) break; //exit program
				//to slow down thread when doing nothing
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		}
	}

	public void executeTask(Task tsk) {

		switch (tsk.getClass().getName()) {

		case "TaskMove":
			TaskMove tskMove = (TaskMove) Deque.removeFirst();  

			executeMove(tskMove);

			return;

		case "TaskClearField":
			TaskClearField tskClearField = (TaskClearField) Deque.removeFirst();  

			cleaningField(tskClearField);

			return;

		case "TaskScan":
			TaskScan tskScan = (TaskScan) Deque.removeFirst();  

			Move = scanningField(tskScan);

			scanDone = true;
			return;


		default:
			//code when an error occurs
			return;
		}
	}

	public void cleaningField(TaskClearField afruim) {
		double[][] stock_cross = {{3,0.5},{3.5,0.5},{4,0.5},{4.5,0.5},{5,0.5}};
		double[][] stock_nought = {{3,1.5},{3.5,1.5},{4,1.5},{4.5,1.5},{5,1.5}};
		boolean block_placed;

		Board CleanBoard = afruim.getCleanBoard();//remember cleancells 
		int[] Count = CleanBoard.amountOfCrossesandNoughts();

		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				block_placed = false;
				while(!exit_program && !block_placed) {
					block_placed = true;
					double [] fieldPosition= {i,j};
					switch(CleanBoard.cells[i][j].content){
					case CROSS: {
						moveXY(current,fieldPosition);
						pick();
						moveXY(fieldPosition,stock_cross[Count[0]-1]);
						place();
						current = stock_cross[Count[0]-1];
						Count[0]--;
						break;
					}
					case NOUGHT:{
						moveXY(current,fieldPosition);
						pick();
						moveXY(fieldPosition,stock_nought[Count[1]-1]);
						place();
						current = stock_nought[Count[1]-1];
						Count[1]--;
						break;
					}
					case EMPTY:{
						break;
					}
					}
				}
			}
		}
		moveXY(current, new double[] {0,0});
		current = new double[] {0,0};
	}

	
	// scan uitvoeren en oplossing teruggeven
	public int[] scanningField(TaskScan scan) {
//		RegulatedMotor motorWidth = new EV3LargeRegulatedMotor(MotorPort.C);
//		RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
		shiftCoordinate();
		Board ScanBoard = scan.getScanBoard();
		int [][] BestMoves = scan.getBestMoves();
		for(int k=0; k < BestMoves.length;k++) {
			int i = BestMoves[k][1];
			int j = BestMoves[k][2];
			double [] fieldPosition= {i,j};
			switch(ScanBoard.cells[i][j].content){
			case CROSS: {					
				break; //cell had already a cross, so no block can be placed here
			}
			case NOUGHT:{
				break;
			}
			case EMPTY:{
				moveXY(current,fieldPosition);
				current=fieldPosition;
				//Delay.msDelay(20);
				int measured_color = measurecolor();
				// 	System.out.print(Integer.toString(measured_color)+ "...");
				//Delay.msDelay(20);
				if(measured_color==0){
					//ScanBoard.cells[i][j].content=Seed.CROSS; //cross is red
					shiftCoordinateReturn();
					return new int[] {(int)fieldPosition[0], (int)fieldPosition[1]};
				}
				else if(measured_color==3){
					//ScanBoard.cells[i][j].content=Seed.NOUGHT;//Nought is yellow
					shiftCoordinateReturn();
					double[][] stock_nought = {{3,1.5},{3.5,1.5},{4,1.5},{4.5,1.5},{5,1.5}};
					int[] Count = ScanBoard.amountOfCrossesandNoughts();
					pick();
					moveXY(current,stock_nought[Count[1]]);
					place();
					current = stock_nought[Count[1]];
					shiftCoordinate();
				}
				break; //This remains empty so nothing is placed	
			}
			}
		}

		// go back to the original coordinatesystem
		shiftCoordinateReturn();

		return null;

	}

	public void executeMove(TaskMove move){
		moveXY(current, move.getStart());
		pick();
		moveXY(move.getStart(), move.getEnd());
		place();
		current=move.getEnd();		
	}
	 
	public void moveXY(double [] first, double []second ){
		double angleW = width/radW;
		double angleRotW = (first[1]-second[1])*angleW*180/(Math.PI);   //in degrees

		double angleL = length/radL;
		double angleRotL = (first[0]-second[0])*angleL*180/(Math.PI);    //in degrees

		moveW = new MoveWidth(angleRotW, motorWidth);
		Thread tMoveW = new Thread(moveW);
		tMoveW.start();

		moveL = new MoveLength(angleRotL, motorLength);
		Thread tMoveL = new Thread(moveL);
		tMoveL.start();
		
		while(tMoveW.isAlive() || tMoveL.isAlive()) {
			//Wait for threads to finish
		}
	}
	
	
	// pick a block:
	public void pick(){

		//movement down
		double angleRotZ = (distanceZ/radiusZ)*180/(Math.PI);
		//RegulatedMotor motorZ = new EV3LargeRegulatedMotor(MotorPort.B);
		motorZ.rotate((int) angleRotZ);

		//movement forward
		double angleRotForward = (distanceL/radL)*180/(Math.PI);
		//RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
		motorLength.setSpeed(motorSpeedSlow);
		motorLength.rotate((int)angleRotForward);
		//motorLength.close();
		motorLength.setSpeed(motorSpeed);

		//movement up
		motorZ.rotate(-(int) angleRotZ);
		//motorZ.close();
		

	}

	//place a block:
	public void place(){

		//movement down
		double angleRotZ = (distanceZ/radiusZ)*180/(Math.PI);
		//RegulatedMotor motorZ = new EV3LargeRegulatedMotor(MotorPort.B);
		motorZ.rotate((int) angleRotZ);

		//movement backward
		double angleRotForward = (distanceL/radL)*180/(Math.PI);
		//RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
		motorLength.setSpeed(motorSpeedSlow);
		motorLength.rotate(-(int)angleRotForward);
		//motorLength.close();
		motorLength.setSpeed(motorSpeed);

		//movement up
		motorZ.setSpeed(motorSpeedFast);
		motorZ.rotate(-(int)angleRotZ);
		motorZ.setSpeed(motorSpeedSlow);
		//motorZ.close();
		

	}

	public int measurecolor(){
		EV3ColorSensor colorsensor = new EV3ColorSensor(SensorPort.S1);
		SampleProvider color = colorsensor.getColorIDMode();
		float[] sample = new float[color.sampleSize()];
		//color ID. 0 = red; 1 = green; 2 = blue; 3 = yellow; 4 = magenta; 5 = orange; 6 = white; 7 = black; 8 = pink; 9 = gray; 10 = light gray	 
		color.fetchSample(sample, 0);
		int colorId=(int)sample[0];
		colorsensor.close();
		return colorId;
	}
	
	 public void shiftCoordinate() {
			//move color sensor above the block, without changing the coordinate of the current position
			//the coordinate (.,.) is now shifted
			double angleRotScan = (distanceShiftW/radW)*180/(Math.PI);
			double angleLengthScan = (distanceShiftL/radL)*180/(Math.PI);
			
			moveW = new MoveWidth(-angleRotScan, motorWidth);
			Thread tMoveW = new Thread(moveW);
			tMoveW.start();

			moveL = new MoveLength(-angleLengthScan, motorLength);
			Thread tMoveL = new Thread(moveL);
			tMoveL.start();
			
			while(tMoveW.isAlive() || tMoveL.isAlive()) {
				//Wait for threads to finish
			}
	 }
	 public void shiftCoordinateReturn() {
			//move color sensor above the block, without changing the coordinate of the current position
			//the coordinate (.,.) is now shifted
			double angleRotScan = (distanceShiftW/radW)*180/(Math.PI);
			double angleLengthScan = (distanceShiftL/radL)*180/(Math.PI);

			moveW = new MoveWidth(angleRotScan, motorWidth);
			Thread tMoveW = new Thread(moveW);
			tMoveW.start();

			moveL = new MoveLength(angleLengthScan, motorLength);
			Thread tMoveL = new Thread(moveL);
			tMoveL.start();
			
			while(tMoveW.isAlive() || tMoveL.isAlive()) {
				//Wait for threads to finish
			}
	 }
}