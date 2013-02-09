/**
 * WALL
 * 
 * Walls handle fairly differently based on directionality, so it is important to put in the right slope
 * Positive vs negative. Walls point up from left to right will have a positive slope, walls pointing down
 * will have a negative slope.
 * 
 * This, like the tile, is a special case of the Entity family, in that it never update/ticks because it doesn't move.
 * 
 * PLEASE SEE THE ENTITY CLASS FOR INFORMATION ON THE ISOMETRIC DANCE.
 * 
 * @author Jeremy Barnes  - Jan 20/13
 * 
 */
package game.entities;

import game.entities.components.Entity;
import game.entities.components.Sprite;
import game.entities.components.Tangible;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class Wall extends Entity{
	
	int tileWidth;
	int tileHeight;

	int xDepth;
	int wallWidth;
	int transparentSpace;
	double isoAngle;


	/**
	 * 
	 * Constructor, sets up the polygon off the grid, moves it on to the grid,
	 * sets up the sprite offsets and graphics component.
	 * @param x - the starting x position of the bounding box 
	 * @param y - the starting y position of the bounding box
	 * @param wall - the sprite that this wall will be represented by
	 * @param isoAngle - the slope of the wall
	 * @param tileWidth - used for aligning it to the floor grid.
	 */
	public Wall(int x, int y, Sprite wall, double isoAngle, int tileWidth, int tileHeight) {

		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.graphicsComponent = wall;
		
		//math stuff used to set up xP and yP
		this.isoAngle = isoAngle;
		xDepth = graphicsComponent.getWidth() /6; //depth of the wall in pixels (thin side, not the face)
		wallWidth = graphicsComponent.getWidth() - xDepth; //length of the face of the wall
		transparentSpace = (int) (wallWidth * isoAngle); // how much of the image is transparent space
		

		physicsComponent = new Tangible(x,y, tileWidth, tileHeight, 0);

		super.xPos = x;
		super.yPos = y;
		

			spriteXOff =  -(4*tileWidth) - xDepth/2 - 10;
			spriteYOff = -(4*tileHeight) - xDepth - 3;

	}

	public void render(Graphics g){		
		graphicsComponent.tick(); //maybe walls are animated
		//TODO will need to render it transparent when player is behind it.
		super.render(g);
//		Rectangle r = this.getPhysicsShape();
//		g.setColor(Color.GREEN); TODO REMOVE POST DEVELOPMENT
//		g.drawRect(r.x, r.y, r.width, r.height);
	}

	@Override
	protected void getInput() {return;} //walls, also, don't need input.

	
}
