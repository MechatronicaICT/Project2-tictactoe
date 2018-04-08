import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.*;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;
import java.util.Scanner;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public abstract class task {
	
	// move xy-direction:
	static void moveXY(int [] first, int []second ){
		
		// control width
		int width = 60; //in mm
		int radW = 19;  //in mm
		int angleW = width/radW;
		
		RegulatedMotor motorWidth = new EV3LargeRegulatedMotor(MotorPort.C);
		double angleRotW = (first[0]-second[0])*angleW*180/(Math.PI);   //in degrees
		motorWidth.rotate((int)angleRotW);
		
		// control length
		int length = 20; //in mm
		int radL = 15;   //in mm
		int angleL = length/radL;
		
		RegulatedMotor motorLength = new EV3LargeRegulatedMotor(MotorPort.D);
		double angleRotL = (first[1]-second[1])*angleL*180/(Math.PI);    //in degrees
		motorLength.rotate((int)angleRotL);
	}

	// pick a block:
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
	public class move extends task{
		int count;
		int [] current;
		int [] start;
		int [] end;
	
		//constructor, hoe definieren we current? is eindepositie vorige zet
		move(int c, int [] s, int [] e){
			count=c;
			start=s;
			end=e;
		}
		
		//perform move
		void perform(){
			moveXY(current, start);
			pick();
			moveXY(start,end);
			place();
			current=end;
			count++;
		}
		
	}
	
	public class clean extends task{
		Board board;
		int [] current;
		
		//constructor, opnieuw current?
		clean(Board given, int [] c){
			board=given;
			current=c;
		}
		
		//hulpmethode
		int [] getStockPos(int number) {
			int [] out= {0,0};
			switch(number) {
			case 1: out= {3,0};
			case 2: out= {3,1};
			case 3: out= {3,3};
			case 4: out= {4,0};
			case 5: out= {4,1};
			}
			return out;
		}
		
		//hulpmethode
		int [] getStockPosition(int number) {
			int [] out= {0,0};
			switch (number) {
			case 1: out= {}
			case 2:
			case 3:
			case 4:
			case 5:
			}
			return out;
		}
		
		/**clean playing field, logic zou aantal zetten van zowel X als O moeten bijhouden (zie X- en Ocount),
		 * op die manier kunnen we makkelijk weten op welke positie we de stock moeten aanvullen*/
		void cleaningField(int Xcount, int Ocount) {
			for(int i=0;i<3;i++) {
				for(int j=0;j<0;j++) {
					int [] fieldPosition= {i,j};
					switch(board[i][j]) {
					case CROSS: {
						moveXY(current,fieldPosition);
						pick();
						moveXY(fieldPosition,getStockPos(Xcount));
						place();
					}
					case NOUGHT:{
						
					}
					case EMPTY:{
						
					}
					}
				}
			}
		}
	}
	
	public class homing extends task{
		
	}
	
}
