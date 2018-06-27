package org.rscangel.client.util;

import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PersistenceManager
{
	private static final XStream xstream = new XStream();

	static
	{
		addAlias( "NPCDef", "org.rscangel.client.entityhandling.defs.NPCDef" );
		addAlias( "ItemDef", "org.rscangel.client.entityhandling.defs.ItemDef" );
		addAlias( "TextureDef",
				"org.rscangel.client.entityhandling.defs.extras.TextureDef" );
		addAlias( "AnimationDef",
				"org.rscangel.client.entityhandling.defs.extras.AnimationDef" );
		addAlias( "ItemDropDef",
				"org.rscangel.client.entityhandling.defs.extras.ItemDropDef" );
		addAlias( "SpellDef",
				"org.rscangel.client.entityhandling.defs.SpellDef" );
		addAlias( "PrayerDef",
				"org.rscangel.client.entityhandling.defs.PrayerDef" );
		addAlias( "TileDef", "org.rscangel.client.entityhandling.defs.TileDef" );
		addAlias( "DoorDef", "org.rscangel.client.entityhandling.defs.DoorDef" );
		addAlias( "ElevationDef",
				"org.rscangel.client.entityhandling.defs.ElevationDef" );
		addAlias( "GameObjectDef",
				"org.rscangel.client.entityhandling.defs.GameObjectDef" );
		addAlias( "org.rscangel.spriteeditor.Sprite",
				"org.rscangel.client.model.Sprite" );
	}

	private static void addAlias( String name, String className )
	{
		try
		{
			xstream.alias( name, Class.forName( className ) );
		}
		catch( ClassNotFoundException e )
		{
			e.printStackTrace();
		}
	}

	public static Object load( File file )
	{
		try
		{
			InputStream is = new GZIPInputStream( new FileInputStream( file ) );
			Object rv = xstream.fromXML( is );
			return rv;
		}
		catch( IOException ioe )
		{
			System.err.println( ioe.getMessage() );
		}
		return null;
	}

	public static void write( File file, Object o )
	{
		try
		{
			OutputStream os = new GZIPOutputStream( new FileOutputStream( file ) );
			xstream.toXML( o, os );
		}
		catch( IOException ioe )
		{
			System.err.println( ioe.getMessage() );
		}
	}
}
