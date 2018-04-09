
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Scanner;

/**
 * The main class for the Tic-Tac-Toe (Console-OO, non-graphics version) It acts
 * as the overall controller of the game.
 */
public class GameMain {
	private Board board; // the game board
	private GameState currentState; // the current state of the game (of enum GameState)
	private Seed currentPlayer; // the current player (of enum Seed)
	private AIPlayerMinimax AIplayer1;
	private AIPlayerTableLookup AIplayer2;
	private GUI GUIlejos;
	private MyRunnable myRunnable;
	
	public static ArrayDeque<Opdracht> arrOpdrachten = new ArrayDeque<>();

	// Named-constants for the dimensions
	public static final int ROWS = 3;
	public static final int COLS = 3;
	public int score = 0;

	int currentRow, currentCol; // the current seed's row and column
	int state = 1;
	
	private static Scanner in = new Scanner(System.in); // input Scanner

	/**
	 * Constructor to setup the game
	 * 
	 * @throws InterruptedException
	 */
	public GameMain() throws InterruptedException {

		myRunnable = new MyRunnable(arrOpdrachten);
		Thread t = new Thread(myRunnable);
		t.start();
		
		board = new Board(3, 3); // allocate game-board

		//testtje
		OpdrachtZet oZet = new OpdrachtZet(String.valueOf(1),"f");
		arrOpdrachten.add(oZet);
		///
		
		AIplayer1 = new AIPlayerMinimax(board);
		AIplayer1.setSeed(Seed.CROSS);
		AIplayer2 = new AIPlayerTableLookup(board);
		AIplayer2.setSeed(Seed.NOUGHT);
		GUIlejos = new GUI(board);
		


		System.out.println("sdfsdf");
		
		
		while (true) {
			loopCase();
			
			//main opdrachten trager laten draaien
			Thread.sleep(100);
		}  // repeat until game-over
	
		
	}

	public void loopCase() {
		// init state
		/*
		 * 1 = start toetstand
		 * 2 = bevindt zich in game modus
		 * 3 = game ai zet
		 * 4 = eind spel
		 * 5 = scan modus
		 * 6 = homing modus
		 * 
		 */
		switch (state) {
		
		// start
		case 1:
			
				//test array via runnable
				OpdrachtZet oZet = new OpdrachtZet(String.valueOf(score),"f");
				arrOpdrachten.add(oZet);
				score = score+ 1;
				/////////////
    			
				if(GUIlejos.getGame() != 0) {
					GUIlejos.resetGame();
					// Initialize the game-board and current status
					initGame();
					state = 2;
				}
				return;
								
		// Game		
		case 2: 
			
				int[] arrCheck = {0,0};
				//check of de gui al een zet heeft gekregen
				if(!(Arrays. equals(arrCheck,GUIlejos.getZet()))) {  
					
				playerMove(currentPlayer); // update the content, currentRow and currentCol
				// GUIlejos.paint(); // ask the board to paint itself
				GUIlejos.drawBoard();
				updateGame(currentPlayer); // update currentState
				// Switch player
				currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
				
				//array met opdracht zet aanvullen
				OpdrachtZet opdrachtje = new OpdrachtZet("rerer","f");
				arrOpdrachten.add(opdrachtje);
				
				// Print message if game-over
				if (currentState == GameState.CROSS_WON) {
					GUIlejos.winner(currentPlayer);
					// go to end game
					state = 4;							
				} else if (currentState == GameState.NOUGHT_WON) {
					GUIlejos.winner(currentPlayer);
					// go to end game
					state = 4;
				} else if (currentState == GameState.DRAW) {
					GUIlejos.winner(Seed.EMPTY);
					// go to end game
					state = 4;
				}
				//AI zet
				state =  3;
				}
				return;
				
		
		// game AI zet		
		case 3: 	
			
				return;
		
		// end game		
		case 4: 
				
				return;
		// scan		
		case 5: 
				return;
		//homing	
		case 6: 
				return;
		//error			
		default: System.out.println("fault");
				return;	
		}
	}
	
	/** Initialize the game-board contents and the current states */
	public void initGame() {
		board.init(); // clear the board contents
		currentPlayer = GUIlejos.firstPlayer();
		currentState = GameState.PLAYING; // ready to play
	}

	/**
	 * The player with "theSeed" makes one move, with input validation. Update
	 * Cell's content, Board's currentRow and currentCol.
	 */
	public void playerMove(Seed theSeed) {
		boolean validInput = false; // for validating input

		do {
			int row = 0;
			int col = 0;
			if (theSeed == Seed.CROSS) {
				// System.out.println("Player 'X', enter your move (row[1-3] column[1-3]): ");
				// int[] test = AIplayer1.move();

				// row = test[0];
				// col = test[1];

				// row = in.nextInt() - 1;
				// col = in.nextInt() - 1;
				//  waight for zet speler
				int[] test = GUIlejos.humanMove();
				
				// zet verwerken
				row = test[0];
				col = test[1];
			} else {
				// System.out.println("Player 'O', enter your move (row[1-3] column[1-3]): ");
				// AI zet opvragen
				int[] test = AIplayer2.move();
				// zet verwerken
				row = test[0];
				col = test[1];

			}

			if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
					&& board.cells[row][col].content == Seed.EMPTY) {
				board.cells[row][col].content = theSeed;
				currentRow = row;
				currentCol = col;
				validInput = true; // input okay, exit loop
			} else {
				GUIlejos.invalidMove();
			}
		} while (!validInput); // repeat until input is valid
	}

	/** Update the currentState after the player with "theSeed" has moved */
	public void updateGame(Seed theSeed) {
		if (hasWon(theSeed)) { // check for win
			currentState = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
		} else if (isDraw()) { // check for draw
			currentState = GameState.DRAW;
		}
		// Otherwise, no change to current state (still GameState.PLAYING).
	}

	/** The entry main() method */
	public static void main(String[] args) {
		try {
			new GameMain();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Let the constructor do the job
	}

	public static int ROWS() {
		return Board.ROWS;
	}

	public static int COLS() {
		return Board.COLS;
	}

	/** Return true if it is a draw (i.e., no more EMPTY cell) */
	public boolean isDraw() {
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				if (board.cells[row][col].content == Seed.EMPTY) {
					return false; // an empty seed found, not a draw, exit
				}
			}
		}
		return true; // no empty cell, it's a draw
	}

	/**
	 * Return true if the player with "theSeed" has won after placing at
	 * (currentRow, currentCol)
	 */
	public boolean hasWon(Seed theSeed) {
		return (board.cells[currentRow][0].content == theSeed // 3-in-the-row
				&& board.cells[currentRow][1].content == theSeed && board.cells[currentRow][2].content == theSeed
				|| board.cells[0][currentCol].content == theSeed // 3-in-the-column
						&& board.cells[1][currentCol].content == theSeed
						&& board.cells[2][currentCol].content == theSeed
				|| currentRow == currentCol // 3-in-the-diagonal
						&& board.cells[0][0].content == theSeed && board.cells[1][1].content == theSeed
						&& board.cells[2][2].content == theSeed
				|| currentRow + currentCol == 2 // 3-in-the-opposite-diagonal
						&& board.cells[0][2].content == theSeed && board.cells[1][1].content == theSeed
						&& board.cells[2][0].content == theSeed);
	}

}