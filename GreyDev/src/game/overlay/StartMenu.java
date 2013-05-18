/**
 * StartMenu.java
 * @author Barnes 
 * 
 * This class is the main menu, so when the game first begins this is the menu that the players see.
 * It can be the pause menu as well, but I don't really like that much. We'll see.
 * 
 * It has lots of temporary stuff like "HOW TO PLAY" which will later go away and be replaced by a tutuorial level of some 
 * description, but for alpha build it's here to stay (5/9/13)
 * 
 * 
 */
package game.overlay;

import game.Core;
import game.Globals;
import game.engine.Camera;
import game.engine.InputHandler;
import game.engine.State;
import game.entities.components.Sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class StartMenu {

	/*
	 * These booleans are temporary (possibly) - since the menu has multiple screens (credits, how to play, menu), it needs to keep track of where it is
	 * If we move these screens to other classes later, these booleans will be unnecessary.
	 */
	boolean titleScrn = true;
	boolean howPlayScrn = false;
	boolean creditsScrn = false;

	/* Menu elements, buttons and the "cursor" */
	Sprite cursor = new Sprite("Bullet", "Bullet");
	Sprite title = new Sprite("Title", "Title");
	Sprite credits = new Sprite("Credits", "Credits");
	Sprite newGame = new Sprite("NewGame", "NewGame");
	Sprite howPlay = new Sprite("HowToPlay", "HowToPlay");
	Sprite exit = new Sprite("Exit", "Exit");
	Sprite back = new Sprite("Back", "Back");

	// List of main menu option buttons, to allow easy for-looping
	Sprite[] options = { newGame, howPlay, credits, exit };

	// Currently selected option
	int option = 0;

	// The font the menu uses for printing text (when the buttons aren't prerendered
	Font menuFont = new Font("Baskerville Old Face", Font.TRUETYPE_FONT, 60);
	FontMetrics fm; // for sizing up the font.

	// The game core, for resetting and starting new games and other exciting nonsense.
	Core parent;

	/**
	 * Constructor! Main menus don't have a lot of variation, so all this needs is to set up the parent Core.
	 * 
	 * @param parent
	 */
	public StartMenu(Core parent) {
		this.parent = parent;
	}

	/**
	 * Draws the menu to the screen
	 * 
	 * @param g - Graphics object
	 */
	public void render(Graphics g) {
		//if the game is over, show the win screen
		if (Globals.state == State.gameWon) {
			title.render(g, 0, 0);
			g.setColor(Color.ORANGE);
			g.setFont(menuFont);
			String s0 = "You Escaped! You win!";
			int strw = fm.stringWidth(s0);
			g.drawString(s0, (int) (Camera.width / 3 - strw / 2), Camera.height / 3 + 50);
			return;

		}
		if (fm == null) {
			fm = g.getFontMetrics(menuFont);
		}
		
		//if we're on the titlescreen, render the title and the main menu
		if (titleScrn) {
			title.render(g, 0, 0); //Greywater
			
			for (int i = 0; i < 4; i++) { //loop through menu options
				options[i].render(g, 0, 0); //and draw them
				
				if (option == i)  //if this element is the currently selected one, render the bullet at that location.
					cursor.render(g, 190, (int) (4 * Camera.height / 11 + 110 * i)); //TODO come back to this when the menu is finished for each resolution
			}
		}
		//if we're on the how to play screen, render that information instead
		else if (howPlayScrn) {
			g.setColor(Color.ORANGE);
			menuFont = new Font("Baskerville Old Face", Font.TRUETYPE_FONT, (int) (60*Camera.scale));
			fm = g.getFontMetrics(menuFont);
			g.setFont(menuFont);
			
			//set up the screentext
			String s0 = "Ms Sweepy has gotten lost in the sewers again!";
			String s1 = "The objective of the game is to find your robot - Ms Sweepy.";
			String s2 = "Be careful of the Watchmen, the automated security drones --";
			String s3 = "they attack indiscriminately. Find Sweepy and escape the sewers.";
			String s4 = "CONTROLS";
			String s5 = "WASD or Arrow Keys to move";
			String s6 = "Spacebar or click to attack/loot enemies";
			String s7 = "ESC to go to menu.";
			String[] s = { s0, s1, s2, s3, "", s4, s5, s6, s7 };
			
			//draw the strings
			for (int i = 0; i < s.length; i++) {
				int strw = fm.stringWidth(s[i]);
				g.drawString(s[i], (int) (Camera.width / 2 - strw / 2), 100 + 70 * i);
			}
			//render the back button TODO come back when the new assets happen
			back.render(g, (int) (Camera.width / 2 - 105 * Camera.scale), (int) (Camera.height - 200 * Camera.scale));

			//draw the cursor next to the back button
			cursor.render(g, Camera.width / 2 - 160, Camera.height - 200);

		}//otherwise, render the "credits" screen!
		else if (creditsScrn) {
			menuFont = new Font("Baskerville Old Face", Font.TRUETYPE_FONT, (int) (50*Camera.scale));
			g.setColor(Color.ORANGE);
			g.setFont(menuFont);
			fm = g.getFontMetrics(menuFont);
			
			//set up the screentext
			String s0 = "Jill Graves - Graphics Wizard";
			String s1 = "Dominique Barnes - Pixelmancer";
			String s2 = "Grace Hammons - Imagemeister";
			String s3 = "Jeremy Barnes - KeyboardMasher";
			String s4 = "SPECIAL THANKS";
			String s45 = "Alexandr Zhelanov - http://opengameart.org/";
			String s5 = "Iwan Gabovitch - http://opengameart.org/";
			String s6 = "Alvinwhatup2 - http://freesound.org/";
			String s7 = "FreqMan - http://freesound.org/people/FreqMan/";
			String s8 = "Brandon75689 - http://opengameart.org/";
			String s9 = "VERY SPECIAL THANKS";
			String s10 = "Coffee";
			String[] s = { s0, s1, s2, s3, "", s4, s45, s5, s6, s7, s8, "", s9, s10 };
			
			//draw the strings to screen
			Graphics2D g2 = (Graphics2D) g;
			for (int i = 0; i < s.length; i++) {
				int strw = fm.stringWidth(s[i]);
				g2.drawString(s[i], (int) (Camera.width / 2 - strw / 2), 50 + 60 * i); //TODO fix for resolution differences

			}
			//render the back button and cursor TODO come back when new art assets for differnet resolutions happen
			back.render(g, (int) (Camera.width / 2 - back.getWidth() * Camera.scale), (int) (Camera.height - back.getHeight() * Camera.scale));
			cursor.render(g, Camera.width / 2 - 160, Camera.height - 200);

		}
	}

	/**
	 * Update method, ticks the menu to move the cursor and respond to user input.
	 */
	public void tick() {
		if (InputHandler.up.keyTapped && option > 0) {
			option--; //move cursor up if not at top
		}
		else if (InputHandler.down.keyTapped && option < 3) {
			option++; //move cursor down if not at bottom
		}
		else if (InputHandler.use.keyTapped) {
			choose(); //if they hit enter, choose that selection
		}
//		else if (InputHandler.mouseClicked){
//			for(Sprite s : options){
//				Point2D p = InputHandler.getMouse();
//				if(p.getY() > s.)
// TODO MOUSE INPUT, NEEDS BUTTONS NOT SPRITES.
//					
//			}
//		}
		

	}

	/**
	 * Determines what the player has chosen based on their currently selected option integer.
	 */
	public void choose() {
		if (!titleScrn) { //if they aren't in the title now, they can only be going back to the title
			creditsScrn = false;
			titleScrn = true;
			howPlayScrn = false;
			return;
		}

		// Otherwise, they are on the title and they are choosing something from the menu.
		switch (option) {
			case (0):
				parent.initNewGame();
				break;
			case (1):
				creditsScrn = false;
				titleScrn = false;
				howPlayScrn = true;
				break;
			case (2):
				creditsScrn = true;
				titleScrn = false;
				howPlayScrn = false;
				break;
			case (3):
				parent.exitGame();
				break;
		}
	}
}
