package org.rscangel.client.entityhandling.defs;

/**
 * The definition wrapper for items
 */
public class ItemDef extends EntityDef
{

	/**
	 * The command of the object
	 */
	public String command;
	/**
	 * The base price of the object
	 */
	public int basePrice;
	/**
	 * The sprite id
	 */
	public int sprite;
	/**
	 * Whether the item is stackable or not
	 */
	public boolean stackable;
	/**
	 * Whether the item is wieldable or not
	 */
	public boolean wieldable;
	/**
	 * PictureMask
	 */
	public int pictureMask;

	public String getCommand()
	{
		return command;
	}

	public int getSprite()
	{
		return sprite;
	}

	public int getBasePrice()
	{
		return basePrice;
	}

	public boolean isStackable()
	{
		return stackable;
	}

	public boolean isWieldable()
	{
		return wieldable;
	}

	public int getPictureMask()
	{
		return pictureMask;
	}

}
