

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.lcd.*;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;


public class GUI implements Runnable {
	
   protected Cell[][] cells; // the board's ROWS-by-COLS array of Cells
   protected Seed mySeed;    // computer's seed
   protected Seed oppSeed;   // opponent's seed	
   
   public int ROWS;
   public int COLS;
   public int Game;
   public int[] Zet = { 0 , 0};
   public int amountOfGames;
   public int gameMode; //0 = robot zet alles, 1 = mens zet blokjes zelf, robot scant achteraf
   public Seed firstPlayer;
   public int[] score;
   
   private int[] move_human;
   private int[][] pos_fields;
   
   public boolean drawBoard = false;
   public boolean moveNeeded = false;

   
   public boolean escape_pressed = false;
   public boolean exit_program = false;
   public boolean game_finished = false;
   public boolean clear_field = false;
   public Seed winner;
   
   public boolean firstGame = true;
   public boolean finished = false;
   public boolean invalidMove = false;

   int state_GUI = 1;
	
	public GUI(Board board) {
		cells = board.cells;
		ROWS = board.ROWS;
		COLS = board.COLS;
	}
	public void run(){
        while(!Thread.interrupted()) {
    		try {
    			loopcase();
    			if (exit_program) break;
    			//to slow down thread when doing nothing
    			Thread.sleep(50);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}   
    		   
        }
	}
	public void loopcase() {
		/*
		 * 1 = start state (best of "N" games, scan mode of gewoon zetten)
		 * 2 = Waiting
		 * 3 = Move needed from human
		 * 4 = 1 game gewonnen
		 * 5 = Best of N done, display score
		 */
		switch (state_GUI) {
		
		case 1:
				if (!escape_pressed&&firstGame) amountOfGames = amountOfGames();
				if (!escape_pressed&&firstGame&&!exit_program) gameMode = gameMode();
				if (!escape_pressed&&!exit_program) firstPlayer = firstPlayer();
				if (!escape_pressed) {
					state_GUI = 2;
					Game = 1;
				}
				return;				
		case 2: 
				//if back knop gedrukt -> afsluiten
				//if game = won -> naar case 4
				//of als zet nodig is van de human
				if(invalidMove) {
					state_GUI = 6;
				} else {
					if(game_finished) {
						state_GUI = 4;
					}
					else {
						if(moveNeeded) {
							state_GUI = 3;
							move_human =  new int[] { 1 , 1 };
							GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
							pos_fields = new int [][]{ { g.getWidth()/12, 5*g.getWidth() / 12, 9 * g.getWidth() / 12 }, { g.getHeight() / 12, 5 * g.getHeight() / 12, 9 * g.getHeight() / 12 } };
						}
						if (drawBoard) {
							drawBoard();
							drawBoard = false;
						}
					}
				}
				return;
		case 3: 
				if (gameMode == 0) {
					humanMove();
				} else if (gameMode == 1) {
					scanMove();
				}
				if (drawBoard) {
					drawBoard();
					drawBoard = false;
				}
				return;
		case 4:
				//iemand is gewonnen, overwinningstekst zetten, score, als oke nieuw spel beginnen
				winner(winner);
		        return;
		case 5:
				//final score weergeven
				finished();
				return;
		case 6: //invalid move or scan
				if (gameMode == 0) invalidMove();
				else if (gameMode == 1) invalidScan();
				return;
		default: System.out.println("fault GUI");
				return;	
		}
	}
	
	public void drawBoard() {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		final int SW = g.getWidth();
		final int SH = g.getHeight();
		g.clear();
		g.setFont(Font.getLargeFont());
		g.setStrokeStyle(0);
		g.drawLine(0, SH/3, SW, SH/3);
		g.drawLine(0, 2*SH/3, SW, 2*SH/3);
		g.drawLine(SW/3, 0, SW/3, SH);
		g.drawLine(2*SW/3, 0, 2*SW/3, SH);

		int [][] pos_fields = {{SW/6,SW/2,5*SW/6},{7*SH/24,15*SH/24,23*SH/24}};
		for (int ix=0;ix<3;ix++) {
			for (int iy=0;iy<3;iy++) {
				if (cells[iy][ix].content != Seed.EMPTY) {
					if (cells[iy][ix].content == Seed.CROSS) {
						g.drawChar('X',pos_fields[0][ix],pos_fields[1][iy],GraphicsLCD.HCENTER|GraphicsLCD.BASELINE);
					} else if (cells[iy][ix].content == Seed.NOUGHT) {
						g.drawChar('O',pos_fields[0][ix],pos_fields[1][iy],GraphicsLCD.HCENTER|GraphicsLCD.BASELINE);
					}
				}
			}
		}
		g.setFont(Font.getDefaultFont());

	}
	
	void scanMove() {
		//in scan mode: press ok to scan
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.clear();
		g.setFont(Font.getDefaultFont());
		g.drawString("Waiting for human move",  g.getWidth()/2, g.getHeight()/2, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);

		int but = Button.waitForAnyPress();
        if ((but & Button.ID_ENTER) != 0) {
        	//block is placed
        	moveNeeded = false;
        	state_GUI = 2;
        }
        else if ((but & Button.ID_ESCAPE) != 0) {
        	escape_pressed = true;
        	state_GUI = 1;
        }

	}
	
	void humanMove() {
		//if back knop ingedrukt -> afsluiten
		//while loop eruit en humanmove in de case2
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.setStrokeStyle(1);
		g.drawRect(pos_fields[0][move_human[1]], pos_fields[1][move_human[0]], 30, 30);
		int but = Button.waitForAnyPress();
        if (((but & Button.ID_LEFT) != 0)||((but & Button.ID_RIGHT) != 0)||((but & Button.ID_UP) != 0)||((but & Button.ID_DOWN) != 0)||((but & Button.ID_ENTER) != 0)) {
        	drawBoard();
        }
        if ((but & Button.ID_ENTER) != 0) {
    		g.setStrokeStyle(0);
        	state_GUI = 2;
        	Zet = move_human;
        	moveNeeded = false;
        }
        else if ((but & Button.ID_LEFT) != 0) {
        	move_human[1] = move_human[1]-1;
        	if (move_human[1] < 0) move_human[1] = move_human[1]+3;
        }
        else if ((but & Button.ID_RIGHT) != 0) {
        	move_human[1] = move_human[1]+1;
        	if (move_human[1] > 2) move_human[1] = move_human[1]-3;
        }
        else if ((but & Button.ID_UP) != 0) {
        	move_human[0] = move_human[0]-1;
        	if (move_human[0] < 0) move_human[0] = move_human[0]+3;
        }
        else if ((but & Button.ID_DOWN) != 0) {
        	move_human[0] = move_human[0]+1;
        	if (move_human[0] > 2) move_human[0] = move_human[0]-3;
        }
        else if ((but & Button.ID_ESCAPE) != 0) {
        	g.setStrokeStyle(0);
        	escape_pressed = true;
        	state_GUI = 1;
        }
		
	}
	
	public void clear() {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.clear();
		g.setFont(Font.getDefaultFont());
	}
	
	void winner(Seed winner) {
		//Winner of 1 game
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.clear();
		g.setFont(Font.getDefaultFont());
		String s ="";
		if (winner == Seed.CROSS) s = "Player X Won!";
		if (winner == Seed.NOUGHT) s = "Player O Won";
		if (winner == Seed.EMPTY) s = "It's a Draw!";
		g.drawString(s, g.getWidth()/2, g.getHeight()/2, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
		g.drawString(Integer.toString(score[0]) + " - " + Integer.toString(score[1]), g.getWidth()/2, 2*g.getHeight()/3, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
		int but = Button.waitForAnyPress();
        if ((but & Button.ID_ENTER) != 0) {
        	state_GUI = 1;
        	game_finished = false;
        	if (firstGame) firstGame = false;
        	if (finished) state_GUI = 5;
        	clear_field = true;
        }

	}
	
	void finished() {
		//Final Screen!
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.clear();
		g.setFont(Font.getDefaultFont());
		String s ="";
		if (score[0] > score[1]) s = "Human (X) Won!";
		else if (score[0] < score[1]) s = "Computer (O) Won";
		else s = "It's a Draw!";
		g.drawString(s, g.getWidth()/2, g.getHeight()/4, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
		g.drawString("Final Score:", g.getWidth()/2, g.getHeight()/2, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
		g.drawString(Integer.toString(score[0]) + " - " + Integer.toString(score[1]), g.getWidth()/2, 3*g.getHeight()/4, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
		int but = Button.waitForAnyPress();
        if ((but & Button.ID_ENTER) != 0) {
        	g.clear();
        	state_GUI = 2;
        	exit_program = true;
        }
	}
	
	public void invalidMove() {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.clear();
		g.setFont(Font.getDefaultFont());
		g.drawString("Invalid Move",  g.getWidth()/2, g.getHeight()/2, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
		g.drawString("try again...",  g.getWidth()/2, 2*g.getHeight()/3, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);

		int but = Button.waitForAnyPress();
        if ((but & Button.ID_ENTER) != 0) {
        	g.clear();
        	state_GUI = 2;
        	invalidMove = false;
        	drawBoard = true;
        }
	}
	
	public void invalidScan() {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.clear();
		g.setFont(Font.getDefaultFont());
		g.drawString("Invalid Scan",  g.getWidth()/2, g.getHeight()/2, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
		g.drawString("try again...",  g.getWidth()/2, 2*g.getHeight()/3, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);

		int but = Button.waitForAnyPress();
        if ((but & Button.ID_ENTER) != 0) {
        	g.clear();
        	state_GUI = 2;
        	invalidMove = false;
        	drawBoard = true;
        }
	}
	
	Seed firstPlayer() {
		Seed firstPlayer = Seed.CROSS;
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		String explanation = "";
		while (true) {
			g.clear();
			g.drawString("Who begins?",  g.getWidth()/2, g.getHeight()/2, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
			explanation = (firstPlayer == Seed.CROSS) ? "Human (X)" : "Computer (O)";
			g.drawString(explanation, g.getWidth()/2, 2*g.getHeight()/3, GraphicsLCD.HCENTER);
			int but = Button.waitForAnyPress();
            if ((but & Button.ID_ENTER) != 0) {
            	break;
            }
            else if (((but & Button.ID_LEFT) != 0)||((but & Button.ID_RIGHT) != 0)||((but & Button.ID_UP) != 0)||((but & Button.ID_DOWN) != 0)) {
            	firstPlayer = (firstPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
            }
            else if ((but & Button.ID_ESCAPE) != 0) {
            	escape_pressed = true;
            	state_GUI = 1;
            	firstGame = true;
            	break;
            }
		}
		return firstPlayer;
	}
	

	int amountOfGames() {
		//choose "best of N games"
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		int amount = 1;
		while (true) {
			g.clear();
			g.drawString("How many games?",  g.getWidth()/2, g.getHeight()/2, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
			g.drawString(Integer.toString(amount), g.getWidth()/2, 2*g.getHeight()/3, GraphicsLCD.HCENTER);
			int but = Button.waitForAnyPress();
            if ((but & Button.ID_ENTER) != 0) {
            	break;
            }
            else if ((but & Button.ID_LEFT) != 0) {
            	amount--;
            }
            else if ((but & Button.ID_RIGHT) != 0) {
            	amount++;
            }
            else if ((but & Button.ID_ESCAPE) != 0) {
            	exit_program = true;
            	break;
            }
            amount = (amount <0) ? 0 : amount;
		}
		return amount;
	}
	
	int gameMode() {
		//choose game mode"
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		int gameMode = 0;
		String explanation = "";
		while (true) {
			g.clear();
			g.drawString("Which gamemode?",  g.getWidth()/2, g.getHeight()/2, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
			explanation = (gameMode == 0) ? "Normal mode" : "Scan mode";
			g.drawString(explanation, g.getWidth()/2, 2*g.getHeight()/3, GraphicsLCD.HCENTER);
			int but = Button.waitForAnyPress();
            if ((but & Button.ID_ENTER) != 0) {
            	break;
            }
            else if (((but & Button.ID_LEFT) != 0)||((but & Button.ID_RIGHT) != 0)||((but & Button.ID_UP) != 0)||((but & Button.ID_DOWN) != 0)) {
            	gameMode = (gameMode == 0) ? 1 : 0;
            }
            else if ((but & Button.ID_ESCAPE) != 0) {
            	escape_pressed = true;
            	state_GUI = 1;
            	break;
            }
		}
		return gameMode;
	}
	
//	
//// communicatie tussen Gui en Game main	
//	
//	// game mode resetten
//	public void resetGame(){                                             
//	         Game = 0; // reset        
//	}                                       
//	// retreive what game mode 
//	public int getGame(){                                                
//	         return Game; // return game mode
//	}
//	
//	// speler zet opvragen
//	public void resetZet(){                                             
//        Zet[1] = 0; // reset                                             
//        Zet[2] = 0; // reset
//        
//	}                                       
//	// retrieve what zet
//	public int[] getZet(){                                                 
//        return Zet; // return zet
//	}                                               
//  
//	public int getGameMode(){                                                
//        return gameMode; // return game mode
//	}
//	
//	public int getAmountOfGames() {
//		return amountOfGames;
//	}
//	public Seed getFirstPlayer() {
//		return firstPlayer;
//	}
//	public void moveNeeded() {
//		moveNeeded = 1;
//	}
}
