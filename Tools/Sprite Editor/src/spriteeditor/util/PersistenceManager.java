package spriteeditor.util;

import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Handles the input and output of XML through XStream
 */
public class PersistenceManager 
{
	/**
	 * The XStream object this persistence manager uses to i/o serialized xml data
	 */
	private static final XStream xstream = new XStream();

	/**
	 * Load aliases
	 */
	static 
	{
		addAlias("NPCDef", "spriteeditor.entityhandling.defs.NPCDef");
		addAlias("ItemDef", "spriteeditor.entityhandling.defs.ItemDef");
		addAlias("TextureDef", "spriteeditor.entityhandling.defs.extras.TextureDef");
		addAlias("AnimationDef", "spriteeditor.entityhandling.defs.extras.AnimationDef");
		addAlias("ItemDropDef", "spriteeditor.entityhandling.defs.extras.ItemDropDef");
	}
	
	/**
	 * Adds the given alias link to the given class name
	 * @param name the class alias
	 * @param className the official class name
	 */
	private static void addAlias(String name, String className) 
	{
		try 
		{
			xstream.alias(name, Class.forName(className));
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param file the file to load
	 * @return a <code>Object</code> representation of the given file
	 */
	public static Object load(File file) 
	{
		try 
		{
			System.out.println("Loading data file: " + file.getName());
			InputStream is =  new GZIPInputStream(new FileInputStream(file));
			Object rv = xstream.fromXML(is);
			is.close();
			return rv;
		} catch(IOException ioe) 
		{
			System.err.println(ioe.getMessage());
		}
		
		return null;
	}

	/**
	 * Serializes the given object into the given file
	 * @param file the file to write to
	 * @param o the object to output
	 */
	public static void write(File file, Object o) 
	{
		try 
		{
			OutputStream os = new GZIPOutputStream(new FileOutputStream(file));
			xstream.toXML(o, os);
			os.close();
		} catch(IOException ioe) 
		{
			System.err.println(ioe.getMessage());
		}
	}
}