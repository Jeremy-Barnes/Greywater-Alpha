package game.entities;

import game.Globals;
import game.engine.Camera;
import game.engine.audio.SoundLoader;
import game.entities.components.AnimEvent;
import game.entities.components.AnimListener;
import game.entities.components.Entity;
import game.entities.components.Sprite;
import game.entities.components.ai.PathFinder;
import game.overlay.InventoryMenu;
import game.world.World;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public abstract class Mob extends Entity implements AnimListener {

	public int direction; // current direction (N,S,E,W....) see Globals.java

	protected String currDirection = "South";
	public String name;
	public Entity target;

	boolean walks = true;
	public boolean playerFriend;

	PathFinder pathFinder;

	protected int HP = 100;
	protected double walkRate = 1; // used to determine how fast a mob's walk
									// animation cycle is
	protected Line2D sight;
	protected boolean validSight;
	public int sightRange;

	boolean attacking = false;
	protected InventoryMenu inv;
	public InventoryMenu currentLoot;
	protected World world;

	public void init(World w) {
		world = w;
		this.pathFinder = new PathFinder(w);
		graphicsComponent.addAnimListener(this);
	}

	/**
	 * Generic method for moving entities: Saves last valid position, moves the entity on its current trajectory, ticks the graphics component, moves
	 * the hitBox.
	 */
	public void tick() {
		if (HP <= 0 && !graphicsComponent.getCurrentImageName().contains("Die") && !graphicsComponent.getCurrentImageName().contains("Dead")) {
			attacking = false;
			graphicsComponent.animate(0.9, "Die");
		}

		super.tick();
		if (HP > 0)
			getInput(); // get input from AI or controls or whatever

		if (!physicsComponent.isMoving())
			physicsComponent.stopMovement();

		if (HP > 0)
			walk();

	}

	/**
	 * Dismisses any open loot windows, finds the direction of movement, shows the walk animation
	 */
	public void walk() {

		if (attacking) {
			graphicsComponent.animate(.5, "Attack" + currDirection);
			return;
		}

		if (physicsComponent.isMoving() && !attacking) { // display animation walk loop.
			direction = Globals.getIntDir(physicsComponent.destination.getX() - getX(), physicsComponent.destination.getY() - getY());
			currDirection = Globals.getStringDir(direction);
			graphicsComponent.loopImg(walkRate, "Walk" + currDirection);
			currentLoot = null;

		} else if (!attacking) {
			graphicsComponent.loopImg(.5, "Stand" + currDirection);
		}

	}

	/**
	 * Gets next action for this mob, can be AI logic or player input, subclasses!
	 */
	protected abstract void getInput();

	protected abstract void attack(Mob enemy);

	/**
	 * @return a line connecting the entity with the player
	 */
	public Line2D getSight() {
		return sight;
	}

	/**
	 * Determines whether or not the Mob has a valid sightline (exists, is not longer than sightRange, and doesn't collide with walls) Sets validSight
	 * to whether or not the sight is valid.
	 */
	public void validateSight() {
		if (sight != null && Globals.distance(sight.getP1(), sight.getP2()) <= this.sightRange && !world.checkWorldCollision(sight))
			validSight = true;
		else
			validSight = false;
	}

	/**
	 * Change the Mob's HP by the given amount.
	 * 
	 * @param damage - how much to change mob hp by
	 */
	public void damage(int damage) {
		HP -= damage;
		System.out.println(name + " took " + damage + " dmg ---> " + HP + " HP");
		if (HP <= 0) {
			graphicsComponent.animate(0.9, "Die");
		}
	}

	public abstract boolean interact();

	public int getHP() {
		return HP;
	}

	/**
	 * @return True if HP > 0
	 */
	public boolean isAlive() {
		if (HP > 0)
			return true;
		return false;
	}

	/**
	 * Allows another mob to interact with this mob.
	 * 
	 * @param interactor - caller
	 */
	public void getInteracted(Mob interactor) {
		if (!isAlive()) {
			interactor.currentLoot = inv;
		}
	}

	@Override
	public void handleEvent(AnimEvent e) {
		if (e.action.contains("Attack") && e.ending) {
			attacking = false;
		} 
		else if (e.action.contains("Walk") && !e.beginning) {
			int num = 0;
			num = new Random().nextInt(6) + 1;
			SoundLoader.playSingle(name + "Walk" + num);
		}
		else if(e.action.contains("Die") && e.ending){
			System.out.println(name + " died: " + HP);
			graphicsComponent = new Sprite(this.name, name + "Dead");
		}
	}

	public Rectangle2D getAttbox() {
		Point2D p = Globals.getIsoCoords(getX() + spriteXOff, getY() + spriteYOff);
		return new Rectangle2D.Double((int) Math.round(p.getX() ) + 50 - Camera.xOffset, (int) Math.round(p.getY() + 20)- Camera.yOffset, graphicsComponent.getWidth() - 100, graphicsComponent.getHeight() - 45);
	}
}
