package test1;

public class OpdrachtAfruimen extends Opdracht {
	
	Cell[][] CleanCell; //kan ook naar opdracht
	
	//constructor, opnieuw current?
	OpdrachtAfruimen(Cell[][] given, int [] c){
		CleanCell=given;
		current=c;
	}
	
	//hulpmethode
	int [] getStockPosX(int number) {
		int [] out= {0,0};
		switch(number) {
		case 1: out[0]=3;//out= {3,0};
				out[1]=0;
				break;
		case 2: out[0]=3;//out= {3,1};
				out[1]=1;
				break;
		case 3: out[0]=3;//out= {3,2};
				out[1]=2;
				break;
		case 4: out[0]=4;//out= {4,0};
				out[1]=0;
				break;
		case 5: out[0]=4;//out= {4,1};
				out[1]=1;
				break;
		}
		return out;
	}
	
	//hulpmethode
	int [] getStockPosO(int number) {
		int [] out= {0,0};
		switch (number) {
		case 1: out[0]=4;//out= {4,2};
				out[1]=2;
				break;
		case 2: out[0]=5;//out= {5,0};
				out[1]=0;
				break;
		case 3: out[0]=5;//out= {5,1};
				out[1]=1;
				break;
		case 4: out[0]=5;//out= {5,2};
				out[1]=2;
				break;
		case 5: out[0]=6;//out= {6,1};
				out[1]=1;
				break;
		}
		return out;
	}
	
	/**clean playing field, logic zou aantal zetten van zowel X als O moeten bijhouden (zie X- en Ocount),
	 * op die manier kunnen we makkelijk weten op welke positie we de stock moeten aanvullen*/
	void cleaningField(int Xcount, int Ocount) {
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				int [] fieldPosition= {i,j};
				switch(CleanCell[i][j].content){
				case CROSS: {
					MyRunnable.moveXY(current,fieldPosition);
					MyRunnable.pick();
					MyRunnable.moveXY(fieldPosition,getStockPosX(Xcount));
					MyRunnable.place();
					break;
				}
				case NOUGHT:{
					MyRunnable.moveXY(current,fieldPosition);
					MyRunnable.pick();
					MyRunnable.moveXY(fieldPosition,getStockPosO(Ocount));
					MyRunnable.place();
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
