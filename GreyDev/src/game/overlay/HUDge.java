package game.overlay;

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
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class HUDge {
	
	Sprite hud = new Sprite("hud", "hud");
	Sprite health;
	Sprite mana;
	int currHp = 100;
	int currMana = 100;;
	String drawText = "";
	Font menuFont = new Font("Baskerville Old Face", Font.TRUETYPE_FONT,35);
	Button inv;
//	Sprite[] hpSprites = {new Sprite("hp_0", "hp_0"),new Sprite("hp_10", "hp_10"), new Sprite("hp_20", "hp_20"), new Sprite("hp_30", "hp_30"), new Sprite("hp_40", "hp_40"),new Sprite("hp_50", "hp_50"), new Sprite("hp_60", "hp_60"), new Sprite("hp_70", "hp_70"), new Sprite("hp_80", "hp_80"), new Sprite("hp_90", "hp_90"), new Sprite("hp_100", "hp_100")};

//	Sprite[] mpSprites = {new Sprite("mp0", "mp0"),new Sprite("mp10","mp10"), new Sprite("mp20", "mp20"), new Sprite("mp30", "mp30"), new Sprite("mp40", "mp40"),new Sprite("mp50", "mp50"), new Sprite("mp60", "mp60"), new Sprite("mp70", "mp70"), new Sprite("mp80", "mp80"), new Sprite("mp90", "mp90"), new Sprite("mp100", "mp100")};


	public HUDge(){
		inv = new Button((int)(hud.getWidth()/2*Camera.scale - 45), (int)(Camera.height - hud.getHeight()*Camera.scale), 90,90);
		health = new Sprite("hp_0", "hp_0");
		System.out.println("Set up hudge");
	//	mana = new Sprite("mp100", "mp100"); TODO TURN ON ALL 100 AND RE-ARRANGE SPRITE SHEETS
	}
	
	public void drawText(String text){
		drawText=  text;
	}
	
	
	public void render(Graphics g){
		g.setFont(menuFont);
		g.setColor(Color.BLACK);
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(Camera.scale, Camera.scale);
		hud.render(g2, 0, (int) (Camera.height/Camera.scale - hud.getHeight()) );
		health.render(g2, 0, (int) (Camera.height/Camera.scale - hud.getHeight()) );
	//	mana.render(g2, 0, (int) (Camera.height/Camera.scale - hud.getHeight()) );
		FontMetrics fm = g.getFontMetrics(menuFont); // or another font
		int strw = fm.stringWidth(drawText);
		
		g2.drawString(drawText, (int)(hud.getWidth()/2  - strw/2),(int) (Camera.height/Camera.scale - hud.getHeight()/2 + 80));
		g2.scale(1/Camera.scale, 1/Camera.scale);

	}
	
	public void update(int hp, int mp){
		int hpVal =hp;

		
		if(hpVal < 0){
			hpVal = 0;
		}
	//	health = hpSprites[hpVal/10];
	//	mana = mpSprites[(mp - mp%10)/10];
		if(InputHandler.leftClick.keyTapped){
			if(inv.getPhysicsShape().contains(InputHandler.mouseLoc)){
				if(Globals.state != State.gameMenu)
					Globals.state = State.gameMenu;
				else
					Globals.state = State.inGame;
			}
		}
	}

}
