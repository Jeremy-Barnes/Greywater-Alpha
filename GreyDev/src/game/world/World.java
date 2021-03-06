/**
 * 
 * 
 * @author Jeremy Barnes
 */
package game.world;

import game.Globals;
import game.engine.Camera;
import game.entities.Mob;
import game.entities.Player;
import game.entities.Sweepy;
import game.entities.Wall;
import game.entities.Watchman;
import game.entities.components.Entity;
import game.entities.components.Sprite;
import game.overlay.InventoryMenu;
import game.overlay.OverlayManager;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class World {

	double tileWidth;
	double tileHeight;

	int xLength;
	int yHeight;

	public Tile[][] tileMap;
	Wall[][] walls;

	Sprite tile;
	Player player;

	public ArrayList<Mob> mobList;

	private Comparator<Entity> spriteSorter = new Comparator<Entity>() {

		@Override
		public int compare(Entity e1, Entity e2) {
			if (e1.getDepth() < e2.getDepth())
				return -1;
			if (e1.getDepth() > e2.getDepth())
				return 1;
			return 0;
		}

	};

	public World(Sprite t) {

		tileHeight = (Camera.scale * t.getHeight());
		tileWidth = (Camera.scale * t.getWidth() / 2); // same as tileHeight (square/flatspace)

		Globals.tileHeight = (Camera.scale * t.getHeight());
		Globals.tileWidth = (Camera.scale * t.getWidth() / 2); // same as tileHeight (square/flatspace)

		tile = t;

		InventoryMenu i = new InventoryMenu();
		mobList = new ArrayList<Mob>();
		player = new Player(39, 3, i);
		player.init(this);
		OverlayManager.init(player);
		// player.addPathFinder(this);
		mobList.add(player);
		Point2D p = Globals.getIsoCoords(player.getX(), player.getY());
		Camera.moveTo((int) p.getX(), (int) p.getY());
		loadEnviro(9);

	}

	public void render(Graphics g) {
		// points used for render culling
		// long starter = System.currentTimeMillis();

		Point origin = Globals.findTile((int) player.getX() - Camera.width / 2, (int) player.getY() - Camera.height);
		Point bottomRight = Globals.findTile((int) player.getX() + Camera.width, (int) player.getY() + Camera.height);

		if (origin.x < 0)
			origin.setLocation(0, origin.y);
		if (origin.y < 0)
			origin.setLocation(origin.x, 0);

		if (bottomRight.x >= xLength)
			bottomRight.setLocation(xLength-1, bottomRight.y);
		if (bottomRight.y >= yHeight )
			bottomRight.setLocation(bottomRight.x, yHeight-1);

		ArrayList<Entity> sortList = new ArrayList<Entity>();

		for (Entity e : mobList) {
			sortList.add(e);
		}

		// render loop
		for (int x = origin.x; x < bottomRight.x; x++) {
			for (int y = bottomRight.y; y >= origin.y; y--) {
				try {
					if (tileMap[x][y] != null){
					//	renderct++;
						tileMap[x][y].render(g);
					}
					if (walls[x][y] != null)
						sortList.add(walls[x][y]);
				} catch (Exception e) {
					System.out.println("EXCEPTION IN WORLD RENDER LOOP");
					e.printStackTrace();
				}
				;
			}
		}

		Collections.sort(sortList, spriteSorter);
		if(player.target != null)
			player.selection.render(g, (int)((Mob) player.target).getAttbox().getX() - 10, (int)((Mob) player.target).getAttbox().getY() + 130);
		for (Entity e : sortList) {
		//	renderct++;
			e.render(g);
		}
	
	}

	public void tick() {

		// update mobs and if they are using AI, check to see if the player is visible to them

		for (Mob e : mobList) {
			e.tick();
		}

		Point2D p = Globals.getIsoCoords(player.getX(), player.getY());
		Camera.moveTo((int) p.getX(), (int) p.getY());

	}

	public boolean checkWorldCollision(Shape s) {
		if(s == null)
			return false;
		Point area = Globals.findTile(s.getBounds().x, s.getBounds().y);

		int areaX = area.x;
		int areaY = area.y;
		areaX--;
		areaY--;
		if (areaX < 0)
			areaX = 0;
		if (areaY < 0)
			areaY = 0;
		int areaXEnd = areaX + 21;
		int areaYEnd = areaY + 21;
		if (areaXEnd > xLength)
			areaXEnd = xLength;
		if (areaYEnd > yHeight)
			areaYEnd = yHeight;

		for (int x = areaX; x < areaXEnd; x++) {
			for (int y = areaY; y < areaYEnd; y++) {
				if (walls[x][y] == null)
					continue;
				if (s.intersects(walls[x][y].getPhysicsShape())) {
					// walls[x][y].printName();
					return true;
				}
			}
		}
		return false;
	}

	public boolean checkEntityCollision(Shape s) {
		for (Entity e : mobList) {
			if (s.intersects(e.getPhysicsShape()))
				return true;
		}
		return false;
	}

	public Entity getEntityCollision(Shape s, Mob caller) {
		Mob deadMob = null;
		Mob friendMob = null;
		for (Mob e : mobList) {
			if (e == null)
				continue;
			if (e == caller)
				continue;
			if (e.playerFriend)
				continue;
			if (s.intersects(e.getAttbox())) {
				if (e.isAlive())
					return e;
				else if (e.playerFriend)
					friendMob = e;
				else
					deadMob = e;
			}
		}
		if (deadMob != null)
			return deadMob;
		else if (friendMob != null)
			return friendMob;
		return null;
	}

	public boolean checkValidTile(int x, int y) {
		if (x < 0 || y < 0)
			return false;
		Point p = Globals.findTile(x, y);
		if (p.x < 0 || p.x > xLength || p.y < 0 || p.y > yHeight)
			return false;

		Rectangle r = new Rectangle(x, y, 1, 1);
		//if (checkWorldCollision(r))
			//return false;
		if(checkWallCollision(x,y))
			return false;
		if (p.x > 0 && p.x < xLength && p.y > 0 && p.y < yHeight)
			return true;

		return false;

	}
	
	public boolean checkWallCollision(int x, int y){
		if (x < 0 || y < 0)
			return false;
		Point p = Globals.findTile(x, y);
		if (p.x < 0 || p.x > xLength || p.y < 0 || p.y > yHeight)
			return false;
		if(walls[p.x][p.y]!= null)
			return true;
		return false;
	}

	private void loadEnviro(int lvlno) {
		Sprite[] floor = { new Sprite("ft1", "ft1"), new Sprite("ft2", "ft2"), new Sprite("ft3", "ft3"), new Sprite("ft4", "ft4"), new Sprite("ft5", "ft5"), new Sprite("ft6", "ft6"), new Sprite("ft7", "ft7"), new Sprite("ft8", "ft8") };
		Sprite[] wall = { new Sprite("wt1", "wt1"), new Sprite("wt2", "wt2"), new Sprite("wt3", "wt3"), new Sprite("wt4", "wt4"), new Sprite("wt5", "wt5"), new Sprite("wt6", "wt6"), new Sprite("wt7", "wt7"), new Sprite("wt8", "wt8"), new Sprite("wt9", "wt9"), new Sprite("wt10", "wt10") };
		Random rand = new Random();
		Sprite column = new Sprite("column", "column");
		try {

			InputStream readFile = this.getClass().getClassLoader().getResourceAsStream("Level" + lvlno + ".txt");
			Scanner filer = new Scanner(readFile);
			String line;

			xLength = Integer.parseInt(filer.nextLine());
			yHeight = Integer.parseInt(filer.nextLine());
			System.out.println("XLength " + xLength);
			System.out.println("YLength " + yHeight);
			tileMap = new Tile[xLength][yHeight];
			walls = new Wall[xLength][yHeight];

			for (int y = 0; y < yHeight; y++) {

				line = filer.nextLine();
				if (line.contains("//")) {
					y--;
					continue;
				}

				for (int x = 0; x < xLength; x++) {
					if (line.charAt(x) == '0') {
						double xCo = x * tileWidth;
						double yCo = y * tileHeight;

						tileMap[x][y] = new Tile(floor[rand.nextInt(8)], xCo - x, yCo - y);
					}
				}
			}
			filer.close();
			filer = null;
			line = "";
			readFile.close();

			//readFile = new File("Walls" + (lvlno) + ".txt");
			 readFile = this.getClass().getClassLoader().getResourceAsStream("Walls" + lvlno + ".txt");

			filer = new Scanner(readFile);
			ArrayList<double[]> sweepyList = new ArrayList();
			for (int y = 0; y < yHeight; y++) {
				line = filer.nextLine();
				if (line.contains("//")) {
					y--;
					continue;
				}
				for (int x = 0; x < xLength; x++) {

					double xCo = x * tileWidth;
					double yCo = y * tileHeight;
					if (line.charAt(x) == 'W' || line.charAt(x) == 'S') {
						int choice = rand.nextInt(9);

						walls[x][y] = new Wall(xCo - x, yCo - y, wall[choice], tileWidth * 2.0 / tileHeight, tileWidth, tileHeight, player, true);
					} else if (line.charAt(x) == 'C') {
						walls[x][y] = new Wall(xCo - x, yCo - y, column, tileWidth * (2.0) / tileHeight, tileWidth, tileHeight, player, false);
					} else if (line.charAt(x) == 'E') {
						walls[x][y] = new Wall(xCo - x, yCo - y, new Sprite("ex4", "ex4"), tileWidth * (2.0) / tileHeight, tileWidth, tileHeight, player, false);
					} else if (line.charAt(x) == 'P') {
						walls[x][y] = new Wall(xCo - x, yCo - y, new Sprite("ex3", "ex3"), tileWidth * (2.0) / tileHeight, tileWidth, tileHeight, player, false);
					} else if (line.charAt(x) == 'I') {
						walls[x][y] = new Wall(xCo - x, yCo - y, new Sprite("ex2", "ex2"), tileWidth * (2.0) / tileHeight, tileWidth, tileHeight, player, false);
					} else if (line.charAt(x) == 'N') {
						walls[x][y] = new Wall(xCo - x, yCo - y, new Sprite("ex1", "ex1"), tileWidth * (2.0) / tileHeight, tileWidth, tileHeight, player, false);
					} else if (line.charAt(x) == 'T') {
						Watchman w = new Watchman(xCo, yCo, player);
						w.init(this);
						mobList.add(w);
					} else if (line.charAt(x) == 'X') {
						sweepyList.add(new double[]{xCo,yCo});
					}
				}
			}
			
			int index = rand.nextInt(sweepyList.size());
			double[] d = sweepyList.get(index);
			Sweepy sw = new Sweepy(d[0], d[1], player);
			sw.init(this);
			mobList.add(sw);
			

			filer.close();
			filer = null;
			readFile.close();

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
