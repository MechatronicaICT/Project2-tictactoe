
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

	private int[] Move;

	public int amountOfGames;
	public int gamesLeft;
	public int gameMode; //0 = robot zet alles, 1 = mens zet blokjes zelf, robot scant achteraf
	
	public int[] score = {0,0};

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
		kine = new Kine(arrOpdrachten);
		Thread tKine = new Thread(kine);
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

		aiPlayer1 = new AIPlayerMinimax(board); //Difficult AI
		aiPlayer1.setSeed(Seed.NOUGHT);
		aiPlayer2 = new AIPlayerTableLookup(board); //Easy AI
		aiPlayer2.setSeed(Seed.NOUGHT);

		try {
			while (true) {
				loopCase();
				if (guiLejos.escape_pressed) {
					//spel resetten
					state = 1;
					guiLejos.escape_pressed = false;
				} else if (guiLejos.exit_program) {
					//tKine.stop();
					//tguiLejos.stop();
					break;
				}
				//main opdrachten trager laten draaien
				Thread.sleep(50);
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
		 * 3 = read move from human
		 * 4 = update board with new move
		 * 5 = end, someone has won
		 * 6 = 
		 * 
		 */
		switch (state) {

		case 1: //start
			//test array via runnable
			//OpdrachtZet oZet = new OpdrachtZet(currentCol, null, null);
			//arrOpdrachten.add(oZet);
			/////////////

			if(guiLejos.Game != 0) {
				guiLejos.Game = 0;
				// Initialize the game-board and current status
				gameMode = guiLejos.gameMode;
				amountOfGames = guiLejos.amountOfGames;
				if (guiLejos.firstGame) gamesLeft = amountOfGames;
				if (gamesLeft > 0) {
					initGame();
					guiLejos.drawBoard = true;
					state = 2;
				}
			}
			return;


		case 2:  //Game

			if (currentPlayer == Seed.CROSS) {
				//Human aan zet
				guiLejos.moveNeeded = true;
				state = 3;
			} else {
				// AI aan zet
				Move = aiPlayer2.move();
				state = 4;
			}
			return;

		case 3: //Read move from human
			if (!guiLejos.moveNeeded) {
				Move = guiLejos.Zet;
				state = 4;
			}
			return;

		case 4: //Update board with move
			int row = Move[0];
			int col = Move[1];
			if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
					&& board.cells[row][col].content == Seed.EMPTY) {
				board.cells[row][col].content = currentPlayer;
				currentRow = row;
				currentCol = col;
				guiLejos.drawBoard = true;
				if (hasWon(currentPlayer)) { // check for win
					currentState = (currentPlayer == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
					state = 5;
				} else if (isDraw()) { // check for draw
					currentState = GameState.DRAW;
					state = 5;
				} else {
					currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS; //switch current player
					state = 2;
				}
			} else {
				guiLejos.invalidMove = true;
				state = 3; 
				guiLejos.moveNeeded = true;
			}

			return;

		case 5: //End: someone has won this game
			//GUI: someone has won!
			// Print message if game-over
			if (currentState == GameState.CROSS_WON||currentState == GameState.NOUGHT_WON) {
				if (currentPlayer == Seed.CROSS) score[0]++;
				else score[1]++;
				guiLejos.score = score;
				guiLejos.game_finished = true;
				guiLejos.winner = currentPlayer;
			} else if (currentState == GameState.DRAW) {
				guiLejos.game_finished = true;
				guiLejos.winner = Seed.EMPTY;
			}
			
			state = 1;
			gamesLeft--;
			if (gamesLeft  == 0) {
				guiLejos.finished = true;
				state = 1;
			}
			return;
			
		case 6:
			return;
			//error			
		default: System.out.println("fault GameMain");
		return;	
		}
	}

	/** Initialize the game-board contents and the current states */
	public void initGame() {
		board.init(); // clear the board contents
		currentPlayer = guiLejos.firstPlayer;
		currentState = GameState.PLAYING; // ready to play

	}

	/**
	 * The player with "theSeed" makes one move, with input validation. Update
	 * Cell's content, Board's currentRow and currentCol.
	 */
	//	
	//	public void updateBoard(int row, int col) {
	//		if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
	//				&& board.cells[row][col].content == Seed.EMPTY) {
	//			board.cells[row][col].content = currentPlayer;
	//			currentRow = row;
	//			currentCol = col;
	//			guiLejos.drawBoard = 1;
	//			if (hasWon(currentPlayer)) { // check for win
	//				currentState = (currentPlayer == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
	//				state = 4;
	//			} else if (isDraw()) { // check for draw
	//				currentState = GameState.DRAW;
	//				state = 4;
	//			}
	//			currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS; //switch current player
	//		} else {
	//			//invalidmove in GUI
	//		}
	//	}
	//	public void playerMove(Seed theSeed) {
	//		boolean validInput = false; // for validating input
	//
	//		do {
	//			int row = 0;
	//			int col = 0;
	//			if (theSeed == Seed.CROSS) {
	//				// System.out.println("Player 'X', enter your move (row[1-3] column[1-3]): ");
	//				// int[] test = aiPlayer1.move();
	//
	//				// row = test[0];
	//				// col = test[1];
	//
	//				// row = in.nextInt() - 1;
	//				// col = in.nextInt() - 1;
	//				//  waight for zet speler
	//				int[] test = guiLejos.humanMove();
	//				
	//				// zet verwerken
	//				row = test[0];
	//				col = test[1];
	//			} else {
	//				// System.out.println("Player 'O', enter your move (row[1-3] column[1-3]): ");
	//				// AI zet opvragen
	//				int[] test = aiPlayer2.move();
	//				// zet verwerken
	//				row = test[0];
	//				col = test[1];
	//
	//			}
	//
	//			if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
	//					&& board.cells[row][col].content == Seed.EMPTY) {
	//				board.cells[row][col].content = theSeed;
	//				currentRow = row;
	//				currentCol = col;
	//				validInput = true; // input okay, exit loop
	//			} else {
	//				guiLejos.invalidMove();
	//			}
	//		} while (!validInput); // repeat until input is valid
	//	}
	//
	//	/** Update the currentState after the player with "theSeed" has moved */
	//	public void updateGame(Seed theSeed) {
	//		if (hasWon(theSeed)) { // check for win
	//			currentState = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
	//			state = 4;
	//		} else if (isDraw()) { // check for draw
	//			currentState = GameState.DRAW;
	//			state = 4;
	//		}
	//		// Otherwise, no change to current state (still GameState.PLAYING).
	//	}



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