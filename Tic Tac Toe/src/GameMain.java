
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
	private AIPlayerMinimax aiPlayer1;
	private AIPlayerTableLookup aiPlayer2;
	private GUI guiLejos;
	private Kine kine;
	
	public static ArrayDeque<Opdracht> arrOpdrachten = new ArrayDeque<>();

	// Named-constants for the dimensions
	public static final int ROWS = 3;
	public static final int COLS = 3;

	int currentRow, currentCol; // the current seed's row and column
	int state = 1;
	
	public int amountOfGames;
	public int gamesLeft;
	public int gameMode; //0 = robot zet alles, 1 = mens zet blokjes zelf, robot scant achteraf

	//private static Scanner in = new Scanner(System.in); // input Scanner

	
	/** The entry main() method */
	public static void main(String[] args) {
	
		new GameMain();
		
	}
	
	
	/**
	 * Constructor to setup the game
	 * 
	 * @throws InterruptedException
	 */
	
	public GameMain(){

		//opstarten kine thread
		//kine = new Kine(arrOpdrachten);
		//Thread tKine = new Thread(kine);
		//tKine.start();
		
		board = new Board(3, 3); // allocate game-board
	
		
		//opstarten Gui thread
		guiLejos = new GUI(board);
		//gebruiken wanneer classe een runnable is
		Thread tguiLejos = new Thread(guiLejos);
		tguiLejos.start();
			
		
		

		//testtje
		//OpdrachtZet oZet = new OpdrachtZet(score, null, null);
		//arrOpdrachten.add(oZet);
		///
		
		aiPlayer1 = new AIPlayerMinimax(board);
		aiPlayer1.setSeed(Seed.CROSS);
		aiPlayer2 = new AIPlayerTableLookup(board);
		aiPlayer2.setSeed(Seed.NOUGHT);
		
		
		
		
		
		//System.out.println("sdfsdf");
		try {
			
			while (true) {
				loopCase();
				
				
				//main opdrachten trager laten draaien
				Thread.sleep(2000);
			}  // repeat until game-over
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Let the constructor do the job1
		
		//tKine.stop();
		
	
		
	}

	public void loopCase() {
		// init state
		/*
		 * 1 = start state
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
				OpdrachtZet oZet = new OpdrachtZet(currentCol, null, null);
				arrOpdrachten.add(oZet);
				/////////////

				if(guiLejos.getGame() != 0) {
					guiLejos.resetGame();
					// Initialize the game-board and current status
					gameMode = guiLejos.getGameMode();
					amountOfGames = guiLejos.getAmountOfGames();
					gamesLeft = amountOfGames;
					if (gamesLeft > 0) {
						initGame();
						state = 2;
					}
				}
				return;
								
		// Game		
		case 2: 
			
				int[] arrCheck = {0,0};
				//check of de gui al een zet heeft gekregen
				if(!(Arrays. equals(arrCheck,guiLejos.getZet()))) {  
					
				playerMove(currentPlayer); // update the content, currentRow and currentCol
				guiLejos.drawBoard();
				updateGame(currentPlayer); // update currentState
				// Switch player
				currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
				
				//array met opdracht zet aanvullen
				OpdrachtZet opdrachtje = new OpdrachtZet(currentCol, null, null);
				arrOpdrachten.add(opdrachtje);
				
				// Print message if game-over
				if (currentState == GameState.CROSS_WON) {
					guiLejos.winner(currentPlayer);
					// go to end game
					state = 4;							
				} else if (currentState == GameState.NOUGHT_WON) {
					guiLejos.winner(currentPlayer);
					// go to end game
					state = 4;
				} else if (currentState == GameState.DRAW) {
					guiLejos.winner(Seed.EMPTY);
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
		currentPlayer = guiLejos.getFirstPlayer();
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
				// int[] test = aiPlayer1.move();

				// row = test[0];
				// col = test[1];

				// row = in.nextInt() - 1;
				// col = in.nextInt() - 1;
				//  waight for zet speler
				int[] test = guiLejos.humanMove();
				
				// zet verwerken
				row = test[0];
				col = test[1];
			} else {
				// System.out.println("Player 'O', enter your move (row[1-3] column[1-3]): ");
				// AI zet opvragen
				int[] test = aiPlayer2.move();
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
				guiLejos.invalidMove();
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