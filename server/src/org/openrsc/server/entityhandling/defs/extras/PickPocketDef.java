package org.openrsc.server.entityhandling.defs.extras;

import java.util.HashMap;
import org.openrsc.server.model.InvItem;
public class PickPocketDef {

	/**
	* The level required to perform this pickpocket
	*/

	private int level;

	/**
	* The experience that this NPC gives when pickpocketed successfully
	*/

	private int experience;	
	
	private HashMap<Integer, int[]> pickpocketLoot;

	/**
	 * The message that the player recieves upon being caught
	 */
	
	private String caughtMessage;
	
	/**
	* Constructs a new PickPocketDefinition object
	*/

	public PickPocketDef(int level, int experience, String caughtMessage) {
		this.pickpocketLoot = new HashMap<Integer, int[]>();
		this.level = level;
		this.experience = experience;
		this.caughtMessage = caughtMessage;
	}

	/**
	* Returns the level required to perform this pickpocket
	*/

	public int getLevel() {
		return level;
	}

	/**
	* Returns a psuedorandom item
	*/

	public InvItem getLoot() {
		int random = new java.util.Random().nextInt(pickpocketLoot.size());
		return new InvItem(pickpocketLoot.get(random)[0], pickpocketLoot.get(random)[1]);
	}

	/**
	*	Adds an item to the pickpocket loot list
	*/
	
	public void addLoot(int itemID, int itemAmount) {
		int index = pickpocketLoot.size();
		pickpocketLoot.put(index, new int[] {itemID, itemAmount});
	}
	
	/**
	* Returns the experience that this NPC gives when pickpocketed successfully
	*/

	public int getExperience() {
		return experience;
	}
	
	/**
	 * Returns the message that the player recieves upon being caught
	 */
	
	public String getCaughtMessage() {
		return caughtMessage;
	}
	
}