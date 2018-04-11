//package test1;

public abstract class Opdracht {
	
	protected int count;
	protected int [] start; //where we pick the block
	protected int [] end; //where we place the block

	//Mogelijkheid 2
	/**public Opdracht(int c, int[] s, int[] e) {
		count = c;
		start=s;
		end=e;
	}*/
	
	
	//overkoepelende zaken
	int getCount() {
		return count;
	}
	public void Countincr(){                                             
		count++; // reset        
	}  
	
	int[] getStart() {
		return start;
	}
	
	int[] getEnd() {
		return end;
	}
	
	int getOpdracht() {
		return 1;
	}
	
	
}

