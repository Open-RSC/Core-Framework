package spriteeditor.entityhandling.defs;

import spriteeditor.entityhandling.defs.extras.ItemDropDef;

/**
 * The definition wrapper for npcs
 */
public class NPCDef extends EntityDef 
{
	/**
	 * The primary command
	 */
	public String command;
	/**
	 * Sprites used to make up this npc
	 */
	public int[] sprites;
	/**
	 * Colour of our hair
	 */
	public int hairColour;
	/**
	 * Colour of our top
	 */
	public int topColour;
	/**
	 * Colour of our legs
	 */
	public int bottomColour;
	/**
	 * Skin colour
	 */
	public int skinColour;
	/**
	 * Something to do with the camera
	 */
	public int camera1, camera2;
	/**
	 * Something to do with models
	 */
	public int walkModel, combatModel, combatSprite;
	/**
	 * The hit points
	 */
	public int hits;
	/**
	 * The attack lvl
	 */
	public int attack;
	/**
	 * The def lvl
	 */
	public int defense;
	/**
	 * The strength lvl
	 */
	public int strength;
	/**
	 * Whether the npc is attackable
	 */
	public boolean attackable;
	/**
	 * How long the npc takes to respawn
	 */
	public int respawnTime;
	/**
	 * Whether the npc is aggressive
	 */
	public boolean aggressive;
	/**
	 * Possible drops
	 */
	public ItemDropDef[] drops;
	
	/**
	 * @return this npc's command
	 */
	public String getCommand() 
	{
		return command;
	}
	
	/**
	 * @param index the sprite index to get
	 * @return this npc's sprite from the given index
	 */
	public int getSprite(int index) 
	{
		return sprites[index];
	}
	
	/**
	 * @return this npc's hair colour
	 */
	public int getHairColour() 
	{
		return hairColour;
	}
	
	/**
	 * @return this npc's top colour
	 */
	public int getTopColour() 
	{
		return topColour;
	}
	
	/**
	 * @return this npc's bottom colour
	 */
	public int getBottomColour() 
	{
		return bottomColour;
	}
	
	/**
	 * @return this npc's skin colour
	 */
	public int getSkinColour() 
	{
		return skinColour;
	}
	
	/**
	 * @return this npc's camera1 var
	 */
	public int getCamera1() 
	{
		return camera1;
	}
	
	/**
	 * @return this npc's camera2 var
	 */
	public int getCamera2() 
	{
		return camera2;
	}
	
	/**
	 * @return this npc's walk model id
	 */
	public int getWalkModel() 
	{
		return walkModel;
	}
	
	/**
	 * @return this npc's combat model id
	 */
	public int getCombatModel() 
	{
		return combatModel;
	}
	
	/**
	 * @return this npc's combat sprite
	 */
	public int getCombatSprite() 
	{
		return combatSprite;
	}

	/**
	 * @return this npc's hits level
	 */
	public int getHits() 
	{
		return hits;
	}

	/**
	 * @return this npc's attack level
	 */
	public int getAtt() 
	{
		return attack;
	}

	/**
	 * @return this npc's defense level
	 */
	public int getDef() 
	{
		return defense;
	}

	/**
	 * @return this npc's strength level
	 */
	public int getStr() 
	{
		return strength;
	}
	
	/**
	 * @return this npc's stats as an int[] array
	 */
	public int[] getStats() 
	{
		return new int[]{attack, defense, strength};
	}

	/**
	 * @return if this npc is attackable
	 */
	public boolean isAttackable() 
	{
		return attackable;
	}
	
	/**
	 * @return this npc's specific spawn time
	 */
	public int respawnTime() 
	{
		return respawnTime;
	}

	/**
	 * @return if this npc is aggressive or not
	 */
	public boolean isAggressive() 
	{
		return aggressive;
	}
}