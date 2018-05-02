//package test1;

public abstract class Opdracht {
	
	protected double [] start; //where we pick the block
	protected double [] end; //where we place the block

	//Mogelijkheid 2
	/**public Opdracht(int c, int[] s, int[] e) {
		count = c;
		start=s;
		end=e;
	}*/
	
	
	//overkoepelende zaken

	
	double[] getStart() {
		return start;
	}
	
	double[] getEnd() {
		return end;
	}
	
	int getOpdracht() {
		return 1;
	}
	
	
}

