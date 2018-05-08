package test1;
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
	private AIPlayerMinimax aiPlayer1; //the difficult AI player
	private AIPlayerTableLookup aiPlayer2; //the easy AI player
	private AIPlayerMinimax human; //the human implemented as AI
	private GUI guiLejos;
	private Kine kine;
	public static ArrayDeque<Task> arrTasks = new ArrayDeque<>(); 
	public static final int ROWS = 3;
	public static final int COLS = 3;
	int currentRow, currentCol; // the current seed's row and column
	int state = 1;
	private int[] Move;
	private int amountOfGames;
	private int gamesLeft;
	private int gameMode; //0 = normal mode, 1 = scan mode
	private int[] score = {0,0};
	private double[][] stock_cross = {{3,0.5},{3.5,0.5},{4,0.5},{4.5,0.5},{5,0.5}}; //stock positions
	private double[][] stock_nought = {{3,1.5},{3.5,1.5},{4,1.5},{4.5,1.5},{5,1.5}}; //stock positions


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

		//Start up kine thread
		kine = new Kine(arrTasks);
		Thread tKine = new Thread(kine);
		tKine.start();

		board = new Board(3, 3); // allocate game-board

		//Start up Gui thread
		guiLejos = new GUI(board);
		Thread tguiLejos = new Thread(guiLejos);
		tguiLejos.start();

		aiPlayer1 = new AIPlayerMinimax(board); //Difficult AI
		aiPlayer1.setSeed(Seed.NOUGHT);
		aiPlayer2 = new AIPlayerTableLookup(board); //Easy AI
		aiPlayer2.setSeed(Seed.NOUGHT);

		human = new AIPlayerMinimax(board); //To calculate most probable move from human
		human.setSeed(Seed.CROSS);

		try {
			while (true) {
				loopCase();
				if (guiLejos.escape_pressed) {
					//reset game, go back to state 1
					state = 1;
					guiLejos.escape_pressed = false;
				} else if (guiLejos.exit_program) {
					kine.exit_program = true;
					break;
				}
				Thread.sleep(50);
			}  // repeat until exit_program

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public void loopCase() {
		// init state
		/*
		 * 1 = start state
		 * 2 = game mode
		 * 3 = read move from human
		 * 4 = update board with new move
		 * 5 = end, someone has won
		 * 
		 * 
		 */
		switch (state) {

		case 1: //start

			if(guiLejos.clear_field) {
				//return all blocks to starting position
				guiLejos.clear_field = false;
				TaskClearField oClear = new TaskClearField(board);
				arrTasks.add(oClear);
			}

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
				//Human has to perform a move
				guiLejos.moveNeeded = true;
				state = 3;
			} else {
				// AI has to perform a move
				Move = aiPlayer2.move(); //calculate the best move for the AI
				state = 4;
			}
			return;

		case 3: //Read move from human
			if (gameMode == 0) {
				//Normal mode
				if (!guiLejos.moveNeeded) {
					Move = guiLejos.Zet;
					state = 4;
				}
			} else if (gameMode == 1) {
				//Scan mode
				if (!guiLejos.moveNeeded) {
					int[][] bestmoves = human.bestMoves(); //calculate most probable moves from human
					TaskScan oScan = new TaskScan(board, bestmoves);
					arrTasks.add(oScan);
					while(true) {
						if (kine.scanDone) {
							kine.scanDone = false;
							if (kine.Zet == null) {
								//invalidScan
								guiLejos.invalidMove = true;
								guiLejos.moveNeeded = true;
							} else {
								Move = kine.Zet;
								state = 4;
							}
							break;
						}
					}
				}
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

				if (gameMode == 0) {
					//normal mode
					int[] stock_amount = board.amountOfCrossesandNoughts();
					double[] start_pos = (currentPlayer == Seed.CROSS) ? stock_cross[stock_amount[0]-1]: stock_nought[stock_amount[1]-1];
					TaskMove tMove = new TaskMove(start_pos, new double[] {row,col});
					arrTasks.add(tMove);


				} else if (gameMode == 1) {
					//scan mode
					if (currentPlayer == Seed.CROSS) {
						//move human -> block is already placed!!
					} else if (currentPlayer == Seed.NOUGHT) {
						//move AI -> place block
						int[] stock_amount = board.amountOfCrossesandNoughts();
						double[] start_pos = (currentPlayer == Seed.CROSS) ? stock_cross[stock_amount[0]-1]: stock_nought[stock_amount[1]-1];
						TaskMove tMove = new TaskMove(start_pos, new double[] {row,col});
						arrTasks.add(tMove);
					}
				}

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