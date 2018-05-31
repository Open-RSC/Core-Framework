package org.rscemulation.server.entityhandling.defs.extras;

import org.rscemulation.server.model.InvItem;
import java.util.ArrayList;

public class StallThievingDefinition {

	/**
	* The level required to steal from this stall
	*/

	private int level;

	/**
	* The experience given from a successful stall thieving attempt
	*/

	private int experience;

	/**
	* The NPC ID that "owns" the stall
	*/

	private int npcOwner;

	/**
	*	The time it takes for the stall to respawn (in miliseconds)
	*/
	
	private int respawnTime;
	
	/**
	* An ArrayList representing the NPCs that "guard" the stall
	*/

	private ArrayList<Integer> stallGuardians;

	/**
	* An ArrayList containing the representations of all items that may be stolen from the stall
	*/

	private ArrayList<InvItem> stallLoot;

	/**
	* Constructs a StallThievingDefinition object
	*/

	public StallThievingDefinition(int level, int experience, int owner, int respawnTime) {
		this.level = level;
		this.experience = experience;
		this.npcOwner = owner;
		this.respawnTime = respawnTime;
		this.stallGuardians = new ArrayList<Integer>();
		this.stallLoot = new ArrayList<InvItem>();
	}

	/**
	*	Assigns a guardian to this stall
	*/

	public void addGuardian(int guardian) {
		stallGuardians.add(guardian);
	}

	/**
	*	Assigns a loot possibility to this stall
	*/

	public void addLoot(int lootID, int lootAmount) {
		stallLoot.add(new InvItem(lootID, lootAmount));
	}

	/**
	* Returns the level required to steal from this stall
	*/

	public int getLevel() {
		return level;
	}

	/**
	* Returns the experience given upon a successful thieving attempt
	*/

	public int getExperience() {
		return experience;
	}

	/**
	*	Returns the time in miliseconds that this stall should respawn in
	*/
	
	public int getRespawnTime() {
		return respawnTime;
	}
	
	/**
	* Returns the ID of the NPC that "owns" this stall
	*/

	public int getOwner() {
		return npcOwner;
	}
	
	/**
	* Returns the stall's "Guardians"
	*/
	
	public ArrayList<Integer> getGuardians() {
		return stallGuardians;
	}
	
	/**
	* Returns a pseudorandom InvItem
	*/
	
	public InvItem getLoot() {
		InvItem item = stallLoot.get(new java.util.Random().nextInt(stallLoot.size()));
		return new InvItem(item.getID(), item.getAmount());
	}
}