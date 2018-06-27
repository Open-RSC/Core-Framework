package spriteeditor.entityhandling.defs;

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

	/**
	 * @return this item's command
	 */
	public String getCommand() 
	{
		return command;
	}
	
	/**
	 * @return this item's sprite id
	 */
	public int getSprite() 
	{
		return sprite;
	}

	/**
	 * @return this item's base shop price
	 */
	public int getBasePrice() 
	{
		return basePrice;
	}

	/**
	 * @return if this item is stackable
	 */
	public boolean isStackable() 
	{
		return stackable;
	}

	/**
	 * @return if this item is wieldable/wearable
	 */
	public boolean isWieldable() 
	{
		return wieldable;
	}
	
	/**
	 * @return this item's picture colour overlay
	 */
	public int getPictureMask() 
	{
		return pictureMask;
	}
}