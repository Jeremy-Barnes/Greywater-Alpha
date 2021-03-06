/**
 * This class is intended to be the image class for all visually represented items.
 * It holds no position data, all objects need a class of their own to hold that data and
 * do collision detection.
 * 
 * *********Important note on the name system in this class.********************
 * Some string variables are used, primarily "name" and "ident"
 * 
 * This system was built to work a very specific way, using a hashmap to store images.
 * The system used is as follows: each sprite for a character is given a formulaic name.
 * For the Character Tavish, his animation walking north is Tavish_Walk_North. This allows the 
 * direction to be passed each time he changes bearing (Walk_South for instance) without passing
 * his name each time.
 * 
 * See the Image Loader class for a full understanding if you are confused.
 *  
 *  
 * @author Jeremy Barnes
 * January 10, 2013
 */
package game.entities.components;

import game.Core;
import game.engine.ImageLoader;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Sprite {

	// what it uses to represent itself visually
	private BufferedImage sprite;

	private boolean isLooping;
	private boolean isTicking;

	private String name;
	private String currImgName;

	// time keepers
	private double cycleLength_Millis;
	private long totalAnimTime_Millis;
	private double sequenceDuration_Millis;
	private int animPeriod_Millis;

	// series length and position trackers (not time, image-count)
	public int seriesPosition;// TODO private
	private int seriesLength;

	private List<AnimListener> listeners;

	/**
	 * Constructor for sprites.
	 * 
	 * @param name - The name of the character/entity (Such as Tavish)
	 * @param imgName - default image to start with.
	 */
	public Sprite(String name, String imgName) {
		animPeriod_Millis = (int) (Core.animPeriodNano / 1000000);
		this.name = name;
		currImgName = imgName;
		forceImage(imgName);
		listeners = new ArrayList<AnimListener>();
	}

	/**
	 * Draws the sprite
	 * 
	 * @param g - graphics object for rendering
	 * @param x - x co-ordinate to render at
	 * @param y - y co-ordinate to render at
	 */
	public void render(Graphics g, int x, int y) {
		tick();
		BufferedImage rep = getCurrentImage();
//		if (name.contains("Tavish")) {
//			System.out.println(this.name + " Img accel,priority,type3,truevolatile");
//			try{
//				System.out.println(rep.getCapabilities(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()).isAccelerated());
//			}catch(Exception e){
//				e.printStackTrace();
//				System.out.println(e.getMessage());
//				System.out.println();
//
//			}
//			
//			System.out.println(rep.getAccelerationPriority());
//			System.out.println(rep.getType());
//			System.out.println(rep.getCapabilities(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()).isTrueVolatile());
//			System.out.println();
//			rep.setAccelerationPriority(1f);
//			System.out.println("Headless?");
//			System.out.println(GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance());
//			System.out.println("String");
//			System.out.println(GraphicsEnvironment.getLocalGraphicsEnvironment().toString());
//			System.out.println("DefaultScreen");
//			System.out.println(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
//			System.out.println("Configs");
//			System.out.println(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getConfigurations().toString());
//			System.out.println("DefConfigCaps");
//			System.out.println(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getImageCapabilities().toString());
//			System.out.println("DefConfig");
//			System.out.println(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
//		}

		g.drawImage(rep, x, y, rep.getWidth(), rep.getHeight(), null);

	}

	public void drawTransparent(Graphics g, int x, int y, float opacity) {
		BufferedImage tranny = getCurrentImage();
		Graphics2D g2d = ((Graphics2D) g);
		Composite c = g2d.getComposite();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g2d.drawImage(tranny, x, y, null);
		g2d.setComposite(c);
	}

	/**
	 * Updates the image if it is animated, assumed to be called once every anim-period.
	 */
	public void tick() {
		if (isTicking) {
			totalAnimTime_Millis = (long) ((totalAnimTime_Millis + animPeriod_Millis) % sequenceDuration_Millis);
			int originalSeriesPos = seriesPosition;
			seriesPosition = (int) (totalAnimTime_Millis / cycleLength_Millis);
			if (originalSeriesPos != seriesPosition && (seriesPosition == 3 || seriesPosition == 0))
				fireEvent(currImgName, false, false);
		}
		if (seriesPosition >= seriesLength - 1 && !isLooping && seriesLength - 1 > -1)
			stopAnim(); // if it isn't looping, stop
		if (seriesPosition > seriesLength && isLooping) {
			seriesPosition = 0; // if it is looping, go back to beginning
			fireEvent(currImgName, true, false);
			fireEvent(currImgName, false, true);
		}
	}

	/**
	 * @return the current image for the sprite as a buffered image
	 */
	public BufferedImage getCurrentImage() {
		if (isTicking)
			return ImageLoader.getGroupedImage(currImgName, seriesPosition);
		return sprite;
	}

	public String getCurrentImageName() {
		return currImgName;
	}

	/**
	 * Loops an animation for a set period of time
	 * 
	 * @param duration_seconds - length of time to loop in seconds
	 * @param ident - Images are loaded as name+ident (Tavish + _Walk_North)
	 */
	public void loopImg(double duration_seconds, String ident) {
		if (currImgName.equalsIgnoreCase(name + ident)) {
			return;
		}
		fireEvent(name + ident, false, true);
		isLooping = true;
		isTicking = true;
		seriesPosition = 0;
		totalAnimTime_Millis = 0;
		sequenceDuration_Millis = duration_seconds * 1000;
		seriesLength = ImageLoader.getSeriesCount(name + ident);
		cycleLength_Millis = sequenceDuration_Millis / (seriesLength);
		currImgName = name + ident;
	}

	/**
	 * Plays an animation once.
	 * 
	 * @param duration_seconds - length of time to play the animation in seconds
	 * @param ident - Images are loaded as name+ident (Tavish + _Walk_North)
	 */
	public void animate(double duration_seconds, String ident) {
		fireEvent(name + ident, false, true);
		isLooping = false;
		isTicking = true;
		if (currImgName.equalsIgnoreCase(name + ident)) {
			return;
		}
		seriesPosition = 0;
		totalAnimTime_Millis = 0;
		sequenceDuration_Millis = duration_seconds * 1000;
		seriesLength = ImageLoader.getSeriesCount(name + ident) ;
		cycleLength_Millis = sequenceDuration_Millis / seriesLength;
		currImgName = name + ident;
	}

	/**
	 * Force-sets an image, circumventing the name + ident system.
	 * 
	 * @param name - the image you want to set.
	 */
	public void forceImage(String name) {
		sprite = ImageLoader.getSingleImage(name);
		if (sprite == null)
			System.out.println(name + " null!");
		currImgName = name;
		isTicking = false;
		isLooping = false;
	}

	public int getWidth() {
		return getCurrentImage().getWidth();
	}

	public int getHeight() {
		return getCurrentImage().getHeight();
	}

	public boolean isAnimating() {
		return isTicking;
	}

	private void stopAnim() {
		isTicking = false;
		isLooping = false;
		seriesPosition = 0;
		fireEvent(currImgName, true, false);
	}

	public void addAnimListener(AnimListener listener) {
		listeners.add(listener);
	}

	public void removeAnimListener(AnimListener listener) {
		listeners.remove(listener);
	}

	public void fireEvent(String message, boolean ending, boolean starting) {
		AnimEvent event = new AnimEvent(this, message, ending, starting);
		for (AnimListener listener : listeners) {
			listener.handleEvent(event);
		}
	}

}
