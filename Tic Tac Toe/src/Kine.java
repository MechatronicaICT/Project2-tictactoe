
//package test1;
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
	private ArrayDeque<Opdracht> Deque = new ArrayDeque<>();

	private double [] current= {0,0}; //where we currently are
	private double [] Homeposition= {0,0}; //position where the homing takes place
	private RegulatedMotor motorWidth = new EV3LargeRegulatedMotor(MotorPort.C);
	private RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
	private RegulatedMotor motorZ = new EV3LargeRegulatedMotor(MotorPort.B);
	private int motorSpeed = 800;
	private int width = 60;   //moveXY: distance between two width coordinates
	private int radW = 19;   //radius conveyor belt
	private int length = 60;    //moveXY: distance between two length coordinates
	private int radL = 15;   //radius wheels
	private int distanceZ = 31;   //pick and place: distance arm lowers/rises
	private int radiusZ = 8; //radius pinion
	private int distanceL = 30;   //pick and place: robot drives forward/backwards to pick/place
	private int distanceShiftZ = 40; //Shift coordinate system to sensor
	private int distanceShiftL = 60; //Same
	private int pixelX=10;   //homing: #samples in one dimension for homing
	private int pixelY=10; 	//homing: idem
	private int angleX=5;	//homing: angle between two samples for homing
	private int angleY=5;	//homing: idem
	private double filterValue=1; //homing
	private MoveLength moveL; //for threads
	private MoveWidth moveW; //for threads

	
	public boolean exit_program = false;
	public boolean scanDone = false;
	public int[] Zet = { 0 , 0};
	


	public Kine(ArrayDeque<Opdracht> deque) {
		//communicatie array met Game main opzetten
		Deque = deque;
	}

	public void run() {
		while(!Thread.interrupted()) {
			
			try {	
				//check if there is something in the array
				if(!Deque.isEmpty()) {

					motorWidth.setSpeed(motorSpeed);
					motorLength.setSpeed(motorSpeed);
					
					Opdracht opd = Deque.peekFirst();
					System.out.println(opd.getClass().getName());
					executeOpdracht(opd);

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

	public void executeOpdracht(Opdracht opd) {

		switch (opd.getClass().getName()) {

		case "OpdrachtZet":
			OpdrachtZet opdZet = (OpdrachtZet) Deque.removeFirst();  

			executeZet(opdZet);

			return;

		case "OpdrachtAfruimen":
			OpdrachtAfruimen opdAfruimen = (OpdrachtAfruimen) Deque.removeFirst();  

			cleaningField(opdAfruimen);

			return;

		case "OpdrachtScan":
			OpdrachtScan opdScan = (OpdrachtScan) Deque.removeFirst();  

			Zet = scanningField(opdScan);

			scanDone = true;
			return;

		case "OpdrachtHoming":
			OpdrachtHoming opdHoming = (OpdrachtHoming) Deque.removeFirst();  

			Homing();

			return;

		default:
			//code bij opdtreden fouten
			return;
		}
	}

	public void cleaningField(OpdrachtAfruimen afruim) {
		double[][] stock_cross = {{3,0.5},{3.5,0.5},{4,0.5},{4.5,0.5},{5,0.5}};
		double[][] stock_nought = {{3,1.5},{3.5,1.5},{4,1.5},{4.5,1.5},{5,1.5}};
		boolean block_placed;

		Board CleanBoard = afruim.getCleanBoard();//cleancells onthouden
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
	}

	// home positie resetten
	public void Homing() {
				
		//go to homeposition
		moveXY(current,Homeposition);
		current=Homeposition;
		
//		RegulatedMotor motorWidth = new EV3LargeRegulatedMotor(MotorPort.C);
//		RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);


		//shift to sensorposition
//		int DistanceShiftScanW=40;
//		double angleRotScanW= (DistanceShiftScanW/radW)*180/(Math.PI);
//		motorWidth.rotate(-(int)angleRotScanW);
//		int DistanceShiftScanL=60; 
//		double angleRotScanL = (DistanceShiftScanL/radW)*180/(Math.PI);
//		motorLength.rotate((int)angleRotScanL);
		shiftCoordinate();

		
		//scan region, calculate difference between pixels, filter, determine center of target
		double [][] image = makeImage();
		double [][] grad = gradient(image);
		double [][] edges = filter(grad);
		int [] center = home(edges);
		
		//move to center + shift to pickcoordinates
//		int angleWback=center[1]*angleY-(int)angleRotScanW;
//		motorWidth.rotate(angleWback);
//		//motorWidth.close();
//		int angleLback=-(angleX*pixelX-center[0])*angleX-(int)angleRotScanL;
//		motorLength.rotate(angleLback);
		//motorLength.close();
		shiftCoordinateReturn();
	}

	// scan uitvoeren en oplossing teruggeven
	public int[] scanningField(OpdrachtScan scan) {
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
				System.out.print(Integer.toString(measured_color)+ "...");
				//Delay.msDelay(20);
				if(measured_color==0){
					//ScanBoard.cells[i][j].content=Seed.CROSS; //cross is red
					shiftCoordinateReturn();
					return new int[] {(int)fieldPosition[0], (int)fieldPosition[1]};
				}
				else if(measured_color==3){
					//ScanBoard.cells[i][j].content=Seed.NOUGHT;//Nought is yellow
					shiftCoordinateReturn();
					return new int[] {(int)fieldPosition[0], (int)fieldPosition[1]};
				}
				break; //This remains empty so nothing is placed	
			}
			}
		}

		// go back to the original coordinatesystem
		shiftCoordinateReturn();

		return null;

	}

	public void executeZet(OpdrachtZet zet){
		moveXY(current, zet.getStart());
		pick();
		moveXY(zet.getStart(), zet.getEnd());
		place();
		current=zet.getEnd();		
	}
	 
	public void moveXY(double [] first, double []second ){
		int angleW = width/radW;
		double angleRotW = (first[1]-second[1])*angleW*180/(Math.PI);   //in degrees

		int angleL = length/radL;
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
		motorZ.rotate((int)angleRotZ);

		//movement forward
		double angleRotForward = (distanceL/radL)*180/(Math.PI);
		//RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
		motorLength.setSpeed(motorSpeed);
		motorLength.rotate((int)angleRotForward);
		//motorLength.close();

		//movement up
		motorZ.rotate(-(int)angleRotZ);
		//motorZ.close();
		

	}

	//place a block:
	public void place(){

		//movement down
		double angleRotZ = (distanceZ/radiusZ)*180/(Math.PI);
		//RegulatedMotor motorZ = new EV3LargeRegulatedMotor(MotorPort.B);
		motorZ.rotate((int)angleRotZ);

		//movement backward
		double angleRotForward = (distanceL/radL)*180/(Math.PI);
		//RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
		motorLength.setSpeed(motorSpeed);
		motorLength.rotate(-(int)angleRotForward);
		//motorLength.close();

		//movement up
		motorZ.rotate(-(int)angleRotZ);
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
	
	public double [][] makeImage(){
//		 RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
//		 RegulatedMotor motorWidth = new EV3LargeRegulatedMotor(MotorPort.C);

		 EV3ColorSensor colorsensor = new EV3ColorSensor(SensorPort.S1);
		 SampleProvider red = colorsensor.getRedMode();
		 float[] sample2 = new float[red.sampleSize()];
		 double [][] image = new double [pixelX][pixelY];
		 
		 for(int i=0;i<pixelX;i++) {
			 for(int j=0;j<pixelY;j++) {
				 red.fetchSample(sample2,0);
				 image[i][j]=(double)sample2[0];
				 motorWidth.rotate(angleY);
			 }
			 motorWidth.rotate(-pixelX*angleX);
			 motorLength.rotate(angleX);
		 }
		 colorsensor.close();
		 return image;
	 }
	 
	public double [][] gradient(double[][] im){
		 double [][] M=new double [pixelX][pixelY];
		 M[0][0]=Math.abs(im[0][0]-im[0][1])+Math.abs(im[0][0]-im[1][0]);
		 M[pixelX-1][0]=Math.abs(im[pixelX-1][0]-im[pixelX-1][1])+Math.abs(im[pixelX-1][0]-im[pixelX-2][0]);
		 M[0][pixelY-1]=Math.abs(im[0][pixelY-1]-im[0][pixelY-2])+Math.abs(im[0][pixelY-1]-im[1][pixelY-1]);
		 M[pixelX-1][pixelY-1]=Math.abs(im[pixelX-1][pixelY-1]-im[pixelX-2][pixelY-1])+Math.abs(im[pixelX-1][pixelY-1]-im[pixelX-1][pixelY-2]);
		  for(int j=1;j<pixelY-1;j++) {
			 M[0][j]=Math.abs(im[0][j]-im[1][j])+Math.abs(im[0][j]-im[0][j-1])+Math.abs(im[0][j]-im[0][j+1]);
			 M[pixelX-1][j]=Math.abs(im[pixelX-1][j]-im[1][j])+Math.abs(im[pixelX-1][j]-im[0][j-1])+Math.abs(im[pixelX-1][j]-im[0][j+1]);
		 }
		  for(int i=1;i<pixelX-1;i++) {
			 M[i][0]=Math.abs(im[i][0]-im[i][1])+Math.abs(im[i][0]-im[i-1][0])+Math.abs(im[i][0]-im[i+1][0]);
			 M[i][pixelY-1]=Math.abs(im[i][pixelY-1]-im[i][pixelY-2])+Math.abs(im[i][pixelY-1]-im[i-1][pixelY-1])+Math.abs(im[i][pixelY-1]-im[i+1][pixelY-1]);
			 for(int j=1;j<pixelY-1;j++) {
				 M[i][j]=Math.abs(im[i][j]-im[i-1][j])+Math.abs(im[i][j]-im[i+1][j])+Math.abs(im[i][j]-im[i][j-1])+Math.abs(im[i][j]-im[i][j+1]);
			 }
		 }
		
		 return M;
	 }
	 
	public double [][] filter(double [][] M){
		 double sum=0;
		 double mean;
		 double[][] F= new double[pixelX][pixelY];
		 for(int i=0;i<pixelX;i++) {
			 for(int j=0;j<pixelY;j++) {
				 sum=+M[i][j];
			 }
		 }
		 mean=sum/(pixelX*pixelY);
		 double comp=mean*filterValue;
		 for(int i=0;i<pixelX;i++) {
			 for(int j=0;j<pixelY;j++) {
				 if (M[i][j]<comp){
					 F[i][j]=1;
				 }
				 else  F[i][j]=0;
			 }
		 }
		 return F;
	 }
	 
	 public int [] home(double [][] F){
		 int sumX=0;
		 int ones=0;
		 int sumY=0;
		 for(int i=0;i<pixelX;i++) {
			 for(int j=0;j<pixelY;j++) {
				 sumX+=F[i][j]*i;
				 sumY+=F[i][j]*j;
				 ones+=F[i][j];
			 }
		 }
		 int X=Math.round(sumX/ones);
		 int Y=Math.round(sumY/ones);
		 int [] home= {X,Y};
		 return home;
	 }
	 public void shiftCoordinate() {
			//move color sensor above the block, without changing the coordinate of the current position
			//the coordinate (.,.) is now shifted
			double angleRotScan = (distanceShiftZ/radW)*180/(Math.PI);
			motorWidth.rotate(-(int)angleRotScan);
			double angleLengthScan = (distanceShiftL/radL)*180/(Math.PI);
			motorLength.rotate((int)angleLengthScan);
	 }
	 public void shiftCoordinateReturn() {
			//move color sensor above the block, without changing the coordinate of the current position
			//the coordinate (.,.) is now shifted
			double angleRotScan = (distanceShiftZ/radW)*180/(Math.PI);
			motorWidth.rotate((int)angleRotScan);
			double angleLengthScan = (distanceShiftL/radL)*180/(Math.PI);
			motorLength.rotate(-(int)angleLengthScan);
	 }
}