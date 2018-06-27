package org.rscangel.client;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.*;
import java.util.Enumeration;

import org.rscangel.client.util.DataConversions;

public class SectorSaver
{
	private EngineHandle engineHandle;

	public SectorSaver( EngineHandle handle )
	{
		engineHandle = handle;
	}

	public boolean save()
	{
		if( engineHandle == null )
			return false;

		ZipFile tileArchive = engineHandle.getTileArchive();
		if( tileArchive == null )
			return false;

		String name = tileArchive.getName();
		if( name == null )
			return false;

		try
		{
			File file = File.createTempFile( "darkquest", "land.tmp" );
			FileOutputStream dest = new FileOutputStream( file.getPath() );

			ZipOutputStream out = new ZipOutputStream(
					new BufferedOutputStream( dest ) );

			prepareStream( tileArchive, out );
			saveEditedEntry( out );

			out.close();
			dest.close();
			out = null;
			dest = null;

			moveFile( file, new File( name ) );
			file = null;
			engineHandle.reloadTileArchive();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean getIsEditedEntry( String name )
	{
		if( engineHandle == null )
			return false;

		for( int i = 0; i < engineHandle.sectors.length; i++ )
		{
			if( engineHandle.sectors[i] == null )
				continue;

			if( engineHandle.sectors[i].getOrigin().equalsIgnoreCase( name ) )
				return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private void prepareStream( ZipFile in, ZipOutputStream out )
	{
		try
		{
			Enumeration entries = in.entries();

			ZipEntry entry;
			while( entries.hasMoreElements() )
			{
				entry = (ZipEntry) entries.nextElement();
				if( entry == null )
					continue;
				if( getIsEditedEntry( entry.getName() ) )
					continue;

				ByteBuffer data = DataConversions
						.streamToBuffer( new BufferedInputStream( in
								.getInputStream( entry ) ) );
				writeEntry( out, entry.getName(), data );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	private void writeEntry( ZipOutputStream out, String name, ByteBuffer data )
	{
		try
		{
			ZipEntry destEntry = new ZipEntry( name );
			out.putNextEntry( destEntry );
			out.write( (data.array()), 0, data.remaining() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	private void saveEditedEntry( ZipOutputStream out )
	{
		if( engineHandle == null )
			return;

		for( int i = 0; i < engineHandle.sectors.length; i++ )
		{
			if( engineHandle.sectors[i] == null )
				continue;

			try
			{
				String name = engineHandle.sectors[i].getOrigin();
				ByteBuffer data = engineHandle.sectors[i].pack();
				writeEntry( out, name, data );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}

	private void moveFile( File in, File out )
	{
		int BUFFER = 2048;
		byte data[] = new byte[BUFFER];
		BufferedInputStream origin = null;

		try
		{
			FileInputStream fi = new FileInputStream( in.getPath() );
			origin = new BufferedInputStream( fi, BUFFER );

			FileOutputStream dest = new FileOutputStream( out.getPath() );

			int count;
			while( (count = origin.read( data, 0, BUFFER )) != -1 )
			{
				dest.write( data, 0, count );
			}
			origin.close();
			dest.close();
			in.delete();

		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}
