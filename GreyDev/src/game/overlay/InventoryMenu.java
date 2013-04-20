package game.overlay;

import game.Globals;
import game.engine.Camera;
import game.engine.InputHandler;
import game.engine.State;
import game.entities.components.Entity;
import game.entities.components.Sprite;
import game.entities.items.Item;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class InventoryMenu {
	public final int COLUMNS = 4;
	public final int ROWS = 9;

	private ArrayList<Slot> inv = new ArrayList<Slot>((ROWS + 1) * COLUMNS);
	private ArrayList<Slot> equip = new ArrayList<Slot>();
	private ArrayList<Slot> craftarea = new ArrayList<Slot>();
	private Slot craftOutput;

	private boolean objectSelected = false;
	private Item selectedItem;
	private Sprite slot;
	private Sprite craft;
	private Sprite weap;
	private Sprite area;
	private Sprite newThing;
	private Sprite goButton;

	private Button craftButton;
	private OverlayManager parent;

	/**
	 * Constructor, sets up the inventory slot image, and fills the inventory
	 * list with nulls (null = empty in this system)
	 */
	public InventoryMenu() {
		slot = new Sprite("invslot", "invslot");
		weap = new Sprite("WeaponSlot", "WeaponSlot");
		craft = new Sprite("Crafting", "Crafting");
		area = new Sprite("area", "area");
		newThing = new Sprite("New", "New");
		goButton = new Sprite("Go", "Go");
		for (int i = 0; i < COLUMNS * (ROWS + 1); i++) {
			int col = i % COLUMNS; // because inventory is a 1D arraylist, the 2D grid effect is achieved via math.
			int row = ROWS - i / COLUMNS;

			inv.add(new Slot(slot, (int) ((Camera.width - slot.getWidth() * Camera.scale * COLUMNS) + slot.getWidth() * Camera.scale * col), (int) (row * slot.getHeight() * Camera.scale)));
		}

		this.addItem(new Item("GodlyPlateoftheWhale", 0, 0));

		for (int i = 0; i < 4; i++) {
			int col = i % 2; // because inventory is a 1D arraylist, the 2D grid effect is achieved via math.
			int row = 2 - i / 2;

			craftarea.add(new Slot(craft, (int) (Camera.width - slot.getWidth() * Camera.scale * COLUMNS + craft.getWidth() * Camera.scale * col - area.getWidth() * Camera.scale / 2 - craft.getWidth() * Camera.scale), (int) (100 * Camera.scale + row * craft.getHeight() * Camera.scale)));
		}

		equip.add(new Slot(weap, (int) (Camera.width - area.getWidth() * Camera.scale - slot.getWidth() * Camera.scale * COLUMNS + (area.getWidth() * Camera.scale / 2 - weap.getWidth() * Camera.scale / 2)), (50)));

		craftOutput = new Slot(newThing, (int) (craftarea.get(3).getX() + 100 * Camera.scale), (int) (craftarea.get(3).getY()));
		craftButton = new Button(goButton, (int) (craftarea.get(3).getX()), (int) (craftarea.get(3).getY() + 200 * Camera.scale));
	}

	public void setParent(OverlayManager p){
		this.parent = p;
	}
	
	/**
	 * Adds a new item to the inventory grid. If grid is full, drops the item.
	 * 
	 * @param i
	 *            - the item to add.
	 */
	public void addItem(Item i) {
		selectedItem = i;
		objectSelected = true;
		placeItem(findEmptySlot());
		if (objectSelected) {
			dropItem();
			objectSelected = false;
			selectedItem = null;
		}
	}

	public Slot findEmptySlot() {
		for (Slot i : inv) {
			if (i.isEmpty())
				return i;
		}
		return null;
	}

	/**
	 * Render method, draws the grid and the items in it. Grid is one sprite
	 * tiled repeatedly.
	 * 
	 * Also draws the "selected item" which is the item being dragged currently.
	 * 
	 * @param g
	 *            - Graphics object
	 */
	public void render(Graphics g) {
		area.renderScaled(g, (int) (Camera.width - area.getWidth() * Camera.scale - slot.getWidth() * Camera.scale * COLUMNS), 0);
		for (int i = 0; i < COLUMNS * (ROWS + 1); i++) {
			inv.get(i).renderScaled(g);
			
		//	Rectangle r = inv.get(i).getPhysicsShape();
			//g.setColor(Color.PINK);
		//	g.drawRect(r.x, r.y, r.width, r.height);
			
		}
		for (Slot s : craftarea)
			s.renderScaled(g);

		for (Slot s : equip)
			s.renderScaled(g);

		craftButton.renderScaled(g);
		craftOutput.renderScaled(g);
		if (selectedItem != null) {
			selectedItem.render(g); // dragged item
		}
	}

	/**
	 * The tick method. If an object is selected (mouse dragged) it will move to
	 * follow the mouse. If the mouse button is pressed, it will try to place
	 * the currently selected item in the inventory (or drop it, depending on
	 * location). If no item is selected, it will try to select one.
	 */
	public void update() {
		if (objectSelected) {// make selection follow the mouse
			selectedItem.move((int) InputHandler.mouseLoc.getX(), (int) InputHandler.mouseLoc.getY());
			System.out.println("Selected");
			if (InputHandler.leftClick.keyTapped){
				placeItem(calcSlot(InputHandler.mouseLoc)); // place it if mouse
			System.out.println("Tap place");												// click
			}
		} else if (InputHandler.leftClick.keyTapped && Globals.state == State.gameMenu) {
			grabItem(InputHandler.mouseLoc); // pick up item from inventory
			System.out.println(selectedItem);
			if(!objectSelected && craftButton.getPhysicsShape().contains(InputHandler.mouseLoc)){
				craft();
				System.out.println("Craft?");
			}
		}//else{
	//		parent.displayText = "WORDS" + System.nanoTime();
			//get text from item from slot from mouse
	//	}
	}

	/**
	 * Determines which slot has been clicked. Used for placing/removing items
	 * from the inventory grid.
	 * 
	 * @param mouse
	 *            - the location of the mouse click on screen.
	 * @return - the slot (0-inv.size-1) or -1 if the slot is invalid/not a slot
	 */
	private Slot calcSlot(Point2D mouse) {
		for (Slot i : inv) {// check each slot in the inventory
			if (i.getPhysicsShape().contains(mouse)) { // if collides
				return i;
			}
		}
		for (Slot i : equip) {
			if (i.getPhysicsShape().contains(mouse)) {
				return i;
			}
		}
		for (Slot i : craftarea) {
			if (i.getPhysicsShape().contains(mouse)) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Destroys the selected item.
	 */
	private void dropItem() {

		// if the item was in inventory previously, remove it and set it to
		// null.
		if (inv.indexOf(selectedItem) > 0 && inv.indexOf(selectedItem) < COLUMNS * (ROWS + 1))
			inv.set(inv.indexOf(selectedItem), null);

		selectedItem = null;// no item is currently selected.
		objectSelected = false;
	}

	/**
	 * Picks up an item from within the grid.
	 * 
	 * @param mouse
	 *            - mouse click location
	 * @return the item selected
	 */
	private void grabItem(Point2D mouse) {

		for (Slot i : inv) {// check each slot in the inventory
			if (i.getPhysicsShape().contains(mouse)) { // if collides
				selectedItem = i.grabItem(); // it is selected
				if (selectedItem != null)
					objectSelected = true;
			}
		}

		for (Slot i : craftarea) {// check each slot in the inventory
			if (i.getPhysicsShape().contains(mouse)) { // if collides
				selectedItem = i.grabItem(); // it is selected
				if (selectedItem != null)
					objectSelected = true;
			}
		}

		for (Slot i : equip) {// check each slot in the inventory
			if (i.getPhysicsShape().contains(mouse)) { // if collides
				selectedItem = i.grabItem(); // it is selected
				if (selectedItem != null)
					objectSelected = true;
			}
		}
		
		if(craftOutput.getPhysicsShape().contains(mouse)){
			selectedItem = craftOutput.grabItem(); // it is selected
			if (selectedItem != null)
				objectSelected = true;
		}
			
	}

	/**
	 * Places the currently selected item into the grid
	 * 
	 * @param loc
	 *            - slot where the item should be placed
	 */
	private void placeItem(Slot dest) {
		// if the slot loc is invalid, abort!
		if (dest == null)
			return;

		if (dest.add(selectedItem)) {
			selectedItem = null; // item is no longer selected
			objectSelected = false;
		}
	}
	
	private void craft(){
		int newItem = 0;
		for(int i = 0; i < craftarea.size(); i ++){
			if(!craftarea.get(i).isEmpty()){
				newItem += craftarea.get(i).grabItem().itemID;
			}
		}
		if(newItem > 0)
			craftOutput.add(new Item("GodlyPlateoftheWhale", 0, 0));
		
	}
}