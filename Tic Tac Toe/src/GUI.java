

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
   
   int state_GUI = 1;
	
	public GUI(Board board) {
		cells = board.cells;
		ROWS = board.ROWS;
		COLS = board.COLS;
		
		//drawBoard();
	}
	public void run(){
        while(!Thread.interrupted()) {
    		try {
    			loopcase();
    			//to slow down thread when doing nothing
    			Thread.sleep(500);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}   
    		   
        }
	}
	public void loopcase() {
		/*
		 * 1 = start state (best of "N" games, scan mode of gewoon zetten)
		 * 2 = game playing
		 * 3 = end game
		 */
		switch (state_GUI) {
		
		case 1:
				amountOfGames = amountOfGames();
				gameMode = gameMode();
				firstPlayer = firstPlayer();
				state_GUI = 2;
				Game = 1;
				return;				
		case 2: 
				drawBoard();
				return;
		case 3: 	
				return;
		//error			
		default: System.out.println("fault");
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
	}
	
	int[] humanMove() {
		int[] move_human = { 1 , 1 };
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		final int SW = g.getWidth();
		final int SH = g.getHeight();
		int[][] pos_fields = { { SW/12, 5*SW / 12, 9 * SW / 12 }, { SH / 12, 5 * SH / 12, 9 * SH / 12 } };
		while (true) {
			drawBoard();
			g.setStrokeStyle(1);
			g.drawRect(pos_fields[0][move_human[1]], pos_fields[1][move_human[0]], 30, 30);
			int but = Button.waitForAnyPress();
            if ((but & Button.ID_ENTER) != 0) {
            	break;
            }
            if ((but & Button.ID_LEFT) != 0) {
            	move_human[1] = move_human[1]-1;
            	if (move_human[1] < 0) move_human[1] = move_human[1]+3;
            }
            if ((but & Button.ID_RIGHT) != 0) {
            	move_human[1] = move_human[1]+1;
            	if (move_human[1] > 2) move_human[1] = move_human[1]-3;
            }
            if ((but & Button.ID_UP) != 0) {
            	move_human[0] = move_human[0]-1;
            	if (move_human[0] < 0) move_human[0] = move_human[0]+3;
            }
            if ((but & Button.ID_DOWN) != 0) {
            	move_human[0] = move_human[0]+1;
            	if (move_human[0] > 2) move_human[0] = move_human[0]-3;
            }
		}
		g.setStrokeStyle(0);
		return move_human;
		
	}
	
	public void clear() {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.clear();
		g.setFont(Font.getDefaultFont());
	}
	
	public void winner(Seed winner) {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.clear();
		g.setFont(Font.getDefaultFont());
		String s ="";
		if (winner == Seed.CROSS) s = "Player X Won!";
		if (winner == Seed.NOUGHT) s = "Player O Won";
		if (winner == Seed.EMPTY) s = "It's a Draw!";
		g.drawString(s, g.getWidth()/2, g.getHeight()/2, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
		Delay.msDelay(3000);
	}
	
	public void invalidMove() {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.clear();
		g.setFont(Font.getDefaultFont());
		g.drawString("Invalid Move, please try again...",  g.getWidth()/2, g.getHeight()/2, GraphicsLCD.HCENTER|GraphicsLCD.BOTTOM);
		Delay.msDelay(1000);
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
            if (((but & Button.ID_LEFT) != 0)||((but & Button.ID_RIGHT) != 0)||((but & Button.ID_UP) != 0)||((but & Button.ID_DOWN) != 0)) {
            	firstPlayer = (firstPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
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
            if ((but & Button.ID_LEFT) != 0) {
            	amount--;
            }
            if ((but & Button.ID_RIGHT) != 0) {
            	amount++;
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
            if (((but & Button.ID_LEFT) != 0)||((but & Button.ID_RIGHT) != 0)||((but & Button.ID_UP) != 0)||((but & Button.ID_DOWN) != 0)) {
            	gameMode = (gameMode == 0) ? 1 : 0;
            }
		}
		return gameMode;
	}
	
	
	
	/** Paint itself */
	//MAG WEG!!!!//
	public void paint() {
      for (int row = 0; row < ROWS; ++row) {
         for (int col = 0; col < COLS; ++col) {
            cells[row][col].paint();   // each cell paints itself
            if (col < COLS - 1) System.out.print("|");
         }
         System.out.println();
         if (row < ROWS - 1) {
            System.out.println("-----------");
         }
      }
   }
	
// communicatie tussen Gui en Game main	
	
	// game mode resetten
	public void resetGame(){                                             
	         Game = 0; // reset        
	}                                       
	// retreive what game mode 
	public int getGame(){                                                
	         return Game; // return game mode
	}
	
	// speler zet opvragen
	public void resetZet(){                                             
        Zet[1] = 0; // reset                                             
        Zet[2] = 0; // reset
        
	}                                       
	// retreive what zet
	public int[] getZet(){                                                 
        return Zet; // return zet
	}                                               
  
	public int getGameMode(){                                                
        return gameMode; // return game mode
	}
	
	public int getAmountOfGames() {
		return amountOfGames;
	}
	public Seed getFirstPlayer() {
		return firstPlayer;
	}
}
