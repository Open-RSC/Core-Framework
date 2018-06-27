package spriteeditor.entityhandling;

import spriteeditor.util.PersistenceManager;
import spriteeditor.entityhandling.defs.*;
import spriteeditor.entityhandling.defs.extras.*;

import java.io.File;

/**
 * This class handles the loading of entities from the def files,
 * and provides methods for relaying these entities to the application.
 */
public class EntityHandler
{
	/**
	 * The NPC def array
	 */
	private static NPCDef[] npcs;
	/**
	 * The item def array
	 */
	private static ItemDef[] items;
	/**
	 * The texture def array
	 */
	private static TextureDef[] textures;
	/**
	 * The animation def array
	 */
	private static AnimationDef[] animations;
	/**
	 * The inventory pic count
	 */
	private static int invPictureCount = 0;

	/**
	 * @param id the array index
	 * @return the npc def at the given array index
	 */
	public static NPCDef getNpcDef(int id) 
	{
		if(id < 0 || id >= npcs.length)
			return null;
		
		return npcs[id];
	}
	
	/**
	 * @return the count of NPCs
	 */
	public static int npcCount()
	{
		return npcs.length;
	}
	
	/**
	 * @param id the array index
	 * @return the item def at the given array index
	 */
	public static ItemDef getItemDef(int id) 
	{
		if(id < 0 || id >= items.length)
			return null;
		
		return items[id];
	}
	
	/**
	 * @return the count of inventory pics
	 */
	public static int invPictureCount() 
	{
		return invPictureCount;
	}
	
	/**
	 * @param id the array index
	 * @return the texture def at the given array index
	 */
	public static TextureDef getTextureDef(int id) 
	{
		if(id < 0 || id >= textures.length)
			return null;
		
		return textures[id];
	}
	
	/**
	 * @return the count of textures
	 */
	public static int textureCount() 
	{
		return textures.length;
	}
	
	/**
	 * @param id the array index
	 * @return the animation def from the given array index
	 */
	public static AnimationDef getAnimationDef(int id) 
	{
		if(id < 0 || id >= animations.length)
			return null;
		
		return animations[id];
	}
	
	/**
	 * @return the count of animations
	 */
	public static int animationCount() 
	{
		return animations.length;
	}

	/**
	 * Loads all definition data
	 */
	static 
	{
		npcs = (NPCDef[])PersistenceManager.load(new File("data", "NPCDef.xml.gz"));
		items = (ItemDef[])PersistenceManager.load(new File("data", "Items.xml.gz"));
		
		for(int id = 0; id < items.length; id++) 
		{
			if(items[id].getSprite() + 1 > invPictureCount)
				invPictureCount = items[id].getSprite() + 1;
		}
		
		textures = (TextureDef[])PersistenceManager.load(new File("data", "Textures.xml.gz"));
		animations = (AnimationDef[])PersistenceManager.load(new File("data", "Animations.xml.gz"));
	}
}