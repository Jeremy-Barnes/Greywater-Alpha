/**
 * TANGIBLE CLASS 
 * 
 * This class is used to hold data about an Entities' hitbox, including position, speed, and accessor methods.
 * In reality, this class is the Entity. The Entity interacts with the world entirely through this class because it is the
 * 2D representation. 
 * 
 *  ***********  IMPORTANT ************************
 * The graphics component (sprite) is the isometric image that is rendered to the screen, but isometric math is unnecessarily
 * intense, and wasteful to do when cheaper, more effective methods are available. To that end, the world is ACTUALLY a top down
 * rectangular world, and is merely represented isometrically.
 * 
 * This class is the 2D physics component (hitbox) and the iso aspect is the graphics component.
 * This class controls the speed, movement, location, and collision of the object as if it were top-down, and then
 * that data is taken by the entity class and math'd into isometric silliness.
 * 
 * I adopted this paradigm from Roger Engelbert
 * http://www.rengelbert.com/tutorial.php?id=76&show_all=true
 * 
 * 
 * @author Jeremy Barnes Dec 28/12 
 */
package game.entities.components;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Tangible {

	/* ****** POSITIONAL VARIABLES ****** */
	public Point2D destination;
	private double speed = 0; // how fast it goes from position to dest. Default - 0;
	private double xDelta = 0.;
	private double yDelta = 0.;

	// physics object
	private Rectangle2D hitBox;

	/**
	 * Constructor. Sets up location and square hit space
	 * 
	 * @param x - X co-ordinate of hitBox (upper left corner)
	 * @param y - Y co-ordinate of hitBox (upper left corner)
	 * @param s - how many pixels per update the sprite moves
	 * @param hitWidth - width of the hitBox
	 * @param hitHeight - height of hitBox
	 */
	public Tangible(double x, double y, int hitWidth, int hitHeight, double s) {

		destination = new Point2D.Double(x, y);
		this.speed = s;
		hitBox = new Rectangle2D.Double(x,y, hitWidth, hitHeight);
	//	position = new Vec2D(x,y);
	//	direction = new Vec2D(x,y);
	}

	/**
	 * Used to move the object if it has a destination. If not, does nothing.
	 */
	public void tick() {
		
		if(hitBox.getX() != destination.getX())
			xDelta = Integer.signum((int) (destination.getX() - hitBox.getX())) * speed ;
		if(hitBox.getY() != destination.getY())
			yDelta = Integer.signum((int) (destination.getY() - hitBox.getY())) * speed;
		
		updateHitSpace(xDelta + hitBox.getX(), yDelta + hitBox.getY());
		xDelta = 0;
		yDelta = 0;
		
	}

	/**
	 * Updates the hitspace (teleports)
	 * 
	 * @param x - new x co-ordinate
	 * @param y - new y co-ordinate
	 */
	public void updateHitSpace(double x, double y) {
		hitBox.setRect(x,y, hitBox.getWidth(), hitBox.getHeight());
	}

	/**
	 * Moves by x and y amount.
	 * Does not move TO X and Y.
	 * 
	 * @param x - how much to move in the x direction
	 * @param y - how much to move in the y direction
	 */
	public void move(int x, int y) {
		destination.setLocation(destination.getX() + x, destination.getY() + y);
	}

	/**
	 * Sets character destination to X,Y
	 * 
	 * @param d - where to go in the x direction
	 * @param e - where to go in the y direction
	 */
	public void moveTo(double x, double y) {
		destination.setLocation(x,y);
	}

	/**
	 * Stops all movement immediately.
	 */
	public void stopMovement() {
		destination.setLocation(hitBox.getX(), hitBox.getY());
	}

	/**
	 * Stops X movement immediately
	 */
	public void stopXMovement() {
		destination.setLocation(hitBox.getX(), destination.getY());
	}

	/**
	 * Stops Y movement immediately
	 */
	public void stopYMovement() {
		destination.setLocation(destination.getX(), hitBox.getY());
	}

	/**
	 * @return the Rectangle used for collision detection.
	 */
	public Rectangle2D getHitBox() {
	//	hitBox.setRect(position.x, position.y, hitBox.getWidth(), hitBox.getHeight());
		return hitBox;
	}

	/**
	 * @return Whether or not the physicscomponent still has distance to traverse.
	 */
	public boolean isMoving() {
		if ((destination.getX() - hitBox.getX()) < 2. && (destination.getX() - hitBox.getX()) > -2.)
			if((destination.getY() - hitBox.getY() < 2.) && (destination.getY() - hitBox.getY()) > -2.)
				return false;
			
		return true;
	}
}
