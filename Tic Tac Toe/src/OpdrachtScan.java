//package test1;
public class OpdrachtScan extends Opdracht {

	/**ScanCell contains the positions of the already placed Crosses and Noughts. When ScanCell is empty it 
	 * has to measure the colorId to see if a block is placed*/
	private Board ScanBoard; //kan ook naar opdracht
	private int[][] BestMoves;
	
	public OpdrachtScan(Board given, int[][] given2) {
		ScanBoard=given;
		BestMoves = given2;
	}
	
	//return cells
	Board getScanBoard(){
		return ScanBoard;
	}
	
	int[][] getBestMoves(){
		return BestMoves;
	}
}