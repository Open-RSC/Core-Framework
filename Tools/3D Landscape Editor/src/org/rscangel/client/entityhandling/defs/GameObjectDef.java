package org.rscangel.client.entityhandling.defs;

/**
 * The definition wrapper for game objects
 */
public class GameObjectDef extends EntityDef
{

	/**
	 * The first command of the object
	 */
	public String command1;
	/**
	 * The second command of the object
	 */
	public String command2;
	/**
	 * The object type. Can't figure out the significance of it. Item type 2 & 3
	 * seem to be special
	 */
	public int type;
	/**
	 * The width of the object
	 */
	public int width;
	/**
	 * The height of the object
	 */
	public int height;
	/**
	 * Can't figure out what this one is for, either.
	 */
	public int groundItemVar;

	public String objectModel;

	public int modelID;

	public String getObjectModel()
	{
		return objectModel;
	}

	public String getCommand1()
	{
		return command1.toLowerCase();
	}

	public String getCommand2()
	{
		return command2.toLowerCase();
	}

	public int getType()
	{
		return type;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getGroundItemVar()
	{
		return groundItemVar;
	}
}
