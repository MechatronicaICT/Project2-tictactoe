package test1;
public class OpdrachtHoming extends Opdracht {

	int [] homeposition={6,2}; //make homingposition black--> colorId=7
	public OpdrachtHoming(int [] h) {
		 homeposition=h;  
	}
	 
		void Homing() {
			MyRunnable.moveXY(current,homeposition);
			current=homeposition;
			if(MyRunnable.measurecolor()==7){
				current=homeposition; //Robot is home
			}
			else{
				for(int i=-1;i<1;i++) {
					for(int j=-1;j<1;j++) {
						int [] fieldPosition= {homeposition[0]+i,homeposition[1]+j};
							MyRunnable.moveXY(current, fieldPosition);
							if(MyRunnable.measurecolor()==7){
								current=homeposition; 
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
		}
		
		
	
	
	 


