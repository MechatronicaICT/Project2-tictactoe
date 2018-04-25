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
	
	
	int [] current= {0,0}; //where we currently are
    
	public boolean exit_program = false;
	
	public boolean scanDone = false;
	public int[] Zet = { 0 , 0};

    //colorsensor

	private int motorSpeed = 400;

	
    public Kine(ArrayDeque<Opdracht> deque) {
    	//communicatie array met Game main opzetten
    	Deque = deque;
    	
    }

    public void run() {
        while(!Thread.interrupted()) {
    		try {	
    			//check if there is something in the array
    			if(!Deque.isEmpty()) {
    				
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
			
			Homing(opdHoming.getHomeposition());
			
			return;
			
			
			
		default:
			//code bij opdtreden fouten
			return;
		}
	}
			
    

    
    
    /**clean playing field, logic zou aantal zetten van zowel X als O moeten bijhouden (zie X- en Ocount),
	 * op die manier kunnen we makkelijk weten op welke positie we de stock moeten aanvullen*/

	void cleaningField(OpdrachtAfruimen afruim) {
		int[][] stock_cross = {{3,0},{3,1},{3,2},{4,0},{4,1}};
		int[][] stock_nought = {{4,2},{5,0},{5,1},{5,2},{6,0}};
		boolean block_placed;

		Board CleanBoard = afruim.getCleanBoard();//cleancells onthouden
		int[] Count = CleanBoard.amountOfCrossesandNoughts();

		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				block_placed = false;
				while(!exit_program && !block_placed) {
					block_placed = true;
					int [] fieldPosition= {i,j};
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
	void Homing(int[] Homeposition) {
		//data van opdracht stockeren
		
		moveXY(current,Homeposition);
		current=Homeposition;
		if(measurecolor()==7){
			current=Homeposition; //Robot is home
		}
		else{
			for(int i=-1;i<1;i++) {
				for(int j=-1;j<1;j++) {
					int [] fieldPosition= {Homeposition[0]+i,Homeposition[1]+j};
						moveXY(current, fieldPosition);
						if(measurecolor()==7){
							current=Homeposition; 
							break;
					/**actually we are current at the fieldPosition which can have the coordinates {7,3}
					 * but when we are homing we give it the coordinates of the homeposition
					 * so we can start again at a proper place in our playing grid
					 */
						}
						else{
							current=fieldPosition;
						}
				}
			}
		}
	}
	
	// scan uitvoeren en opslossing teruggeven
	int[] scanningField(OpdrachtScan scan) {
		Board ScanBoard = scan.getScanBoard();
		int [] Move_Scanned = {0,0};
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				int [] fieldPosition= {i,j};
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
						Move_Scanned = new int[] {i,j};
						return Move_Scanned;
					}
					else if(measured_color==3){
						//ScanBoard.cells[i][j].content=Seed.NOUGHT;//Nought is yellow
						Move_Scanned = new int[] {i,j};
						return Move_Scanned;
					}
					break; //This remains empty so nothing is placed
					
				}
				}
			}
		}
		return null;
	
	}
	
	void executeZet(OpdrachtZet zet){
		//System.out.println(Integer.toString(zet.getStart()[0]));
		//System.out.println(Integer.toString(zet.getStart()[1]));

		moveXY(current, zet.getStart());
		pick();
		moveXY(zet.getStart(), zet.getEnd());
		place();
		current=zet.getEnd();		
	}
    
    

    void moveXY(int [] first, int []second ){
		
		// control width
		int width = 60; //in mm
		int radW = 19;  //in mm
		int angleW = width/radW;
		
		RegulatedMotor motorWidth = new EV3LargeRegulatedMotor(MotorPort.C);
		motorWidth.setSpeed(motorSpeed);
		double angleRotW = (first[1]-second[1])*angleW*180/(Math.PI);   //in degrees
		motorWidth.rotate((int)angleRotW);
		
		motorWidth.close();
		
		// control length
		int length = 45; //in mm
		int radL = 15;   //in mm
		int angleL = length/radL;
		
		RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
		motorLength.setSpeed(motorSpeed);
		double angleRotL = (first[0]-second[0])*angleL*180/(Math.PI);    //in degrees
		motorLength.rotate(-(int)angleRotL);
		
		motorLength.close();
	}

	// pick a block:
	void pick(){
		
		//movement down
		int distanceZ = 35; //mm
		int radiusZ = 8; //mm
		double angleRotZ = (distanceZ/radiusZ)*180/(Math.PI);
		RegulatedMotor motorZ = new EV3LargeRegulatedMotor(MotorPort.B);
		motorZ.rotate((int)angleRotZ);
		
		//movement forward
		int distanceL = 50; //mm
		int radL = 15; //mm
		double angleRotForward = (distanceL/radL)*180/(Math.PI);
		RegulatedMotor motorForward = new EV3LargeRegulatedMotor(MotorPort.D);
		motorForward.setSpeed(motorSpeed);
		motorForward.rotate((int)angleRotForward);
		
		motorForward.close();
		
		//movement up
		motorZ.rotate(-(int)angleRotZ);
		
		motorZ.close();
		
	}
	
	//place a block:
	void place(){
		
		//movement down
		int distanceZ = 35; //mm
		int radiusZ = 8; //mm
		double angleRotZ = (distanceZ/radiusZ)*180/(Math.PI);
		RegulatedMotor motorZ = new EV3LargeRegulatedMotor(MotorPort.B);
		motorZ.rotate((int)angleRotZ);
		
		//movement backward
		int distanceL = 50; //mm
		int radL = 15; //mm
		double angleRotForward = (distanceL/radL)*180/(Math.PI);
		RegulatedMotor motorForward = new EV3LargeRegulatedMotor(MotorPort.D);
		motorForward.setSpeed(motorSpeed);
		motorForward.rotate(-(int)angleRotForward);
		
		motorForward.close();
		
		//movement up
		motorZ.rotate(-(int)angleRotZ);
		
		motorZ.close();
		
	}

	 
	int measurecolor(){
		EV3ColorSensor colorsensor = new EV3ColorSensor(SensorPort.S1);
		SampleProvider color = colorsensor.getColorIDMode();

		float[] sample = new float[color.sampleSize()];
		//while(true){
		//EV3 ev3 =(EV3) BrickFinder.getLocal();
		 //TextLCD lcd = ev3.getTextLCD();
		 //color ID. 0 = red; 1 = green; 2 = blue; 3 = yellow; 4 = magenta; 5 = orange; 6 = white; 7 = black; 8 = pink; 9 = gray; 10 = light gray	 
		 //public static final float limitColor=2;
		 color.fetchSample(sample, 0);
		 int colorId=(int)sample[0];
		 //https://www.programcreek.com/java-api-examples/?api=lejos.hardware.sensor.EV3ColorSensor
		 //lcd.drawInt(colorId,0,0);
		 //System.out.println(colorId);
		 colorsensor.close();
		 return colorId;
		 //Delay.msDelay(9000);
	}

	 

    
	
    
 

}