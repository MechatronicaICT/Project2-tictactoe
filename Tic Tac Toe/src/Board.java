/**
 * The Board class models the game-board.
 */
public class Board {  // save as Board.java
   // Named-constants for the dimensions
   public static int ROWS;
   public static int COLS;
 
   // package access
   Cell[][] cells;  // a board composes of ROWS-by-COLS Cell instances
 
   /** Constructor to initialize the game board */
   public Board(int rows, int cols) {
	  ROWS = rows;
	  COLS = cols;
	  
      cells = new Cell[ROWS][COLS];  // allocate the array
      for (int row = 0; row < ROWS; ++row) {
         for (int col = 0; col < COLS; ++col) {
            cells[row][col] = new Cell(row, col); // allocate element of the array
         }
      }
   }
 
   /** Initialize (or re-initialize) the contents of the game board */
   public void init() {
      for (int row = 0; row < ROWS; ++row) {
         for (int col = 0; col < COLS; ++col) {
            cells[row][col].clear();  // clear the cell content
         }
      }
   }
    
   
   public int Cols() {
	   return COLS;
   }
 
   public int Rows() {
	   return ROWS;
   }
   
   public int[] amountOfCrossesandNoughts() {
		int cross = 0;
		int nought = 0;
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				if (cells[row][col].content == Seed.NOUGHT) {
					nought++;
				} else if (cells[row][col].content == Seed.CROSS) {
					cross++;
				}
			}
		}
		return new int[] {cross,nought};
	}

     

}