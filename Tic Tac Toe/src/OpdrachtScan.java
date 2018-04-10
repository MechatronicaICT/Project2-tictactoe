package test1;
public class OpdrachtScan extends Opdracht {

	Cell[][] ScanCell; //kan ook naar opdracht
	public OpdrachtScan(Cell[][] given) {
		 ScanCell=given;
	}
	/**ScanCell contains the positions of the already placed Crosses and Noughts. When ScanCell is empty it 
	 * has to measure the colorId to see if a block is placed*/
	 
	void scanningField() {
		
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
					MyRunnable.moveXY(current,fieldPosition);
					current=fieldPosition;
					if(MyRunnable.measurecolor()==0){
						
						ScanCell[i][j].content=Seed.CROSS; //cross is red
						break loop; // the robot found the block that was placed so it doesn't have to scan the other positions
					}
					else if(MyRunnable.measurecolor()==3){
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
	 
}