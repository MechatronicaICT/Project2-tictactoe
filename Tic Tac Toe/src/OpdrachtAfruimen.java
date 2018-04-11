//package test1;

public class OpdrachtAfruimen extends Opdracht {
	
	private Cell[][] CleanCell; //kan ook naar opdracht
	
	//constructor, opnieuw current?
	OpdrachtAfruimen(Cell[][] given){
		CleanCell=given;
		
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
	
	Cell[][] getCleancell(){
		return CleanCell;
	}
	
	


}
