package test1;

public class TaskClearField extends Task {
	
	private Board CleanBoard; //kan ook naar opdracht
	
	//constructor, opnieuw current?
	TaskClearField(Board given){
		CleanBoard=given;
		
	}
	

	
	Board getCleanBoard(){
		return CleanBoard;
	}
	
	


}