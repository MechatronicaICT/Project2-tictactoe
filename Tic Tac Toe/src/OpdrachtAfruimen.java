//package test1;

public class OpdrachtAfruimen extends Opdracht {
	
	private Board CleanBoard; //kan ook naar opdracht
	
	//constructor, opnieuw current?
	OpdrachtAfruimen(Board given){
		CleanBoard=given;
		
	}
	

	
	Board getCleanBoard(){
		return CleanBoard;
	}
	
	


}
