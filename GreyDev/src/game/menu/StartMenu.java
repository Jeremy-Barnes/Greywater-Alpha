package game.menu;

import game.Core;
import game.engine.Camera;
import game.engine.InputBundle;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class StartMenu{
	String cursor = "";
	String[] options = new String[]{"Start New Game", "Load Old Game", "Exit"};
	
	int option = 0;
	
	Font menuFont = new Font("Baskerville Old Face", Font.TRUETYPE_FONT, 40);
	FontMetrics fm;

	int cursorLength = 0;
	
	Core parent;
	
	public StartMenu(Core parent){
		
		this.parent = parent;
	}
	
	

	public void render(Graphics g) {
		if(fm == null){
			fm = g.getFontMetrics(menuFont);
		}
		g.setColor(Color.darkGray);
		g.setFont(menuFont);
		for(int i = 0; i < 3; i++){
			String button = "";
			if(option == i){
				cursor = "*";
			}
			else
				cursor = "";
			button += options[i];
			int length = (int) fm.getStringBounds(button, g).getWidth();
			if(length > cursorLength){
				cursorLength = length;
			}

			g.drawString(cursor, Camera.width/2 -cursorLength/2 - 45, Camera.height/3 + 45*i);
			g.drawString(button, Camera.width/2 - length/2, Camera.height/3 + 40*i);
		}
	}

	public void update() {
		if(InputBundle.up && option > 0){
			option--;
		}
		if(InputBundle.down && option < 2){
			option++;
		}
		if(InputBundle.enter){
			choose();
		}
	}
	
	public void choose(){
		switch(option){
		case(0): parent.initNewGame(); break;
		case(1): parent.loadGame(); break;
		case(2): parent.exitGame(); break;
		}
	}


}