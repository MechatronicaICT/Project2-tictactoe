//package test1;
public class OpdrachtScan extends Opdracht {

	/**ScanCell contains the positions of the already placed Crosses and Noughts. When ScanCell is empty it 
	 * has to measure the colorId to see if a block is placed*/
	private Cell[][] ScanCell; //kan ook naar opdracht
	
	public OpdrachtScan(Cell[][] given) {
		 ScanCell=given;
	}
	
	//return cells
	Cell[][] getScanCell(){
		return ScanCell;
	}
	
	 
}