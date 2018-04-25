//package test1;
public class OpdrachtScan extends Opdracht {

	/**ScanCell contains the positions of the already placed Crosses and Noughts. When ScanCell is empty it 
	 * has to measure the colorId to see if a block is placed*/
	private Board ScanBoard; //kan ook naar opdracht
	
	public OpdrachtScan(Board given) {
		ScanBoard=given;
	}
	
	//return cells
	Board getScanBoard(){
		return ScanBoard;
	}
	
	 
}