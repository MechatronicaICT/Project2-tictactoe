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
import lejos.hardware.ev3.EV3;
//import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;

public class Kine implements Runnable {
	private ArrayDeque<Opdracht> Deque = new ArrayDeque<>();
	
  
	int [] current= {1,2}; //where we currently are
    
    //colorsensor
	public EV3ColorSensor colorsensor = new EV3ColorSensor(SensorPort.S1);
	public SampleProvider color = colorsensor.getColorIDMode();
	public float[] sample = new float[color.sampleSize()];



	
    public Kine(ArrayDeque<Opdracht> deque) {
    	//communicatie array met Game main opzetten
    	Deque = deque;
    	
    }

    public void run() {
        while(!Thread.interrupted()) {
    		try {
    			//check if there is something in the array
    			if(!Deque.isEmpty()) {
    				
    				
    				Opdracht opd = Deque.peekLast();
    				System.out.println("thread 1" +  opd.getClass().getName());
    			  				
    				executeOpdracht(opd);
    				
        			//System.out.println("thread 1" +  opd.getClass().getName());
        			   
    			}    			
    			
    			
    			//to slop down threath when doing nothing
    			Thread.sleep(500);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}   
    		   
        }
    }
    
	public void executeOpdracht(Opdracht opd) {

		switch (opd.getClass().getName()) {
		
		
		case "OpdrachtZet":
			OpdrachtZet opdZet = (OpdrachtZet) Deque.removeLast();  
			
			executeZet(opdZet);
			
			return;
			
		case "OpdrachtAfruimen":
			OpdrachtAfruimen opdAfruimen = (OpdrachtAfruimen) Deque.removeLast();  
			
			cleaningField(1,1,opdAfruimen);
			
			return;
			
		case "OpdrachtScan":
			OpdrachtScan opdScan = (OpdrachtScan) Deque.removeLast();  
			
			scanningField(opdScan);
			
			return;
			
		case "OpdrachtHoming":
			OpdrachtHoming opdHoming = (OpdrachtHoming) Deque.removeLast();  
			
			
			Homing(opdHoming.getHomeposition());
			
			return;
			
			
			
		default:
			//code bij opdtreden fouten
			return;
		}
	}
			
    

    
    
    /**clean playing field, logic zou aantal zetten van zowel X als O moeten bijhouden (zie X- en Ocount),
	 * op die manier kunnen we makkelijk weten op welke positie we de stock moeten aanvullen*/

	void cleaningField(int Xcount, int Ocount, OpdrachtAfruimen afruim) {
		
		Cell[][] CleanCell = afruim.getCleancell();//cleancells onthouden
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				int [] fieldPosition= {i,j};
				switch(CleanCell[i][j].content){
				case CROSS: {
					moveXY(current,fieldPosition);
					pick();
					moveXY(fieldPosition,afruim.getStockPosX(Xcount));
					place();
					break;
				}
				case NOUGHT:{
					moveXY(current,fieldPosition);
					pick();
					moveXY(fieldPosition,afruim.getStockPosO(Ocount));
					place();
					break;
				}
				case EMPTY:{
					break;
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
	
	// scan uitvoeren en oplossing teruggeven
	void scanningField(OpdrachtScan scan) {
		Cell[][] ScanCell = scan.getScanCell();
		
		loop:{
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				int [] fieldPosition= {i,j};
				switch(ScanCell[i][j].content){
				case CROSS: {					
					break; //cell had already a cross, so no block can be placed here
				}
				case NOUGHT:{
					break;
				}
				case EMPTY:{
					moveXY(current,fieldPosition);
					current=fieldPosition;
					if(measurecolor()==0){
						
						ScanCell[i][j].content=Seed.CROSS; //cross is red
						break loop; // the robot found the block that was placed so it doesn't have to scan the other positions
					}
					else if(measurecolor()==3){
						ScanCell[i][j].content=Seed.NOUGHT;//Nought is yellow
						break loop;
					}
					else break; //This remains empty so nothing is placed
					
				}
				}
			}
		}
		}
	
	
	}
	
	void executeZet(OpdrachtZet zet){
		
		moveXY(current, zet.getStart());
		pick();
		moveXY(zet.getStart(), zet.getEnd());
		place();
		current=zet.getEnd();
		zet.Countincr();
		
	}
    
    

    void moveXY(int [] first, int []second ){
		
		// control width
		int width = 60; //in mm
		int radW = 19;  //in mm
		int angleW = width/radW;
		
		RegulatedMotor motorWidth = new EV3LargeRegulatedMotor(MotorPort.C);
		double angleRotW = (first[0]-second[0])*angleW*180/(Math.PI);   //in degrees
		motorWidth.rotate((int)angleRotW);
		
		motorWidth.close();
		
		// control length
		int length = 20; //in mm
		int radL = 15;   //in mm
		int angleL = length/radL;
		
		RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
		double angleRotL = (first[1]-second[1])*angleL*180/(Math.PI);    //in degrees
		motorLength.rotate((int)angleRotL);
		
		motorLength.close();
	}

	// pick a block:
	void pick(){
		
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
		
		motorForward.close();
		
		//movement up
		motorZ.rotate(-(int)angleRotZ);
		
		motorZ.close();
		
	}
	
	//place a block:
	void place(){
		
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
		
		motorForward.close();
		
		//movement up
		motorZ.rotate(-(int)angleRotZ);
		
		motorZ.close();
		
	}

	 
	int measurecolor(){
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
	 //}
	 

    
	
    
 

}