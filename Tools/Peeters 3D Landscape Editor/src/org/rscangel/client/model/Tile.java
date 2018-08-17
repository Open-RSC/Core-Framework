package org.rscangel.client.model;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A representation of one tile within our world map
 */
public class Tile
{
	/**
	 * The elevation of this tile
	 */
	public byte groundElevation = 0;

	/**
	 * The texture ID of this tile
	 */
	public byte groundTexture = 0;

	/**
	 * The texture ID of the roof of this tile
	 */
	public byte roofTexture = 0;

	/**
	 * The texture ID of any horizontal wall on this tile
	 */
	public byte horizontalWall = 0;

	/**
	 * The texture ID of any vertical wall on this tile
	 */
	public byte verticalWall = 0;

	/**
	 * The ID of any diagonal walls on this tile
	 */
	public int diagonalWalls = 0;

	/**
	 * The overlay texture ID
	 */
	public byte groundOverlay = 0;

	/**
	 * The tile name. 
	 */
	protected String mTileName = "";
	
	/**
	 * Is tile empty?
	 */
	public boolean mIsEmpty = false;
	
	/**
	 * Default ground overlay.
	 */
	public byte mDefaultGroundOverlay = 0;

	/**
	 * Writes the Tile raw data into a ByteBuffer
	 */
	// -------------------------------------------------------------------------------------------------------------------
	public ByteBuffer pack() throws IOException
	{
		ByteBuffer out = ByteBuffer.allocate( 10 );

		out.put( groundElevation );
		out.put( groundTexture );
		if( mIsEmpty )
		{
			out.put( mDefaultGroundOverlay );
		}
		else
		{
			out.put( groundOverlay );
		}
		out.put( roofTexture );

		out.put( horizontalWall );
		out.put( verticalWall );
		out.putInt( diagonalWalls );

		out.flip();
		return out;
	}

	/**
	 * Create a new tile from raw data packed into the given ByteBuffer
	 */
	// -------------------------------------------------------------------------------------------------------------------
	public static Tile unpack( ByteBuffer in ) throws IOException
	{
		if( in.remaining() < 10 )
		{
			throw new IOException( "Provided buffer too short" );
		}

		Tile tile = new Tile();

		tile.groundElevation = in.get();
		tile.groundTexture = in.get();
		tile.groundOverlay = in.get();
		tile.roofTexture = in.get();
		tile.horizontalWall = in.get();
		tile.verticalWall = in.get();
		tile.diagonalWalls = in.getInt();
		tile.mDefaultGroundOverlay = tile.groundOverlay;		

		 if( tile.groundOverlay == 8 || tile.groundOverlay == 0 )
		 {
			 if( tile.groundElevation == 0 &&
			 tile.groundTexture == 0 &&
			 tile.roofTexture == 0 &&
			 tile.horizontalWall == 0 &&
			 tile.verticalWall == 0 &&
			 tile.diagonalWalls == 0 )
			 {
				 tile.mIsEmpty = true;
				 tile.groundOverlay = 5;
			 }
		 }

		return tile;
	}

	/**
	 * @brief Sets the name of this tile.
	 */
	public void setName( String name )
	{
		mTileName = name;
	}

	/**
	 * @brief Retrieves the name of this tile.
	 */
	public String getTileName()
	{
		return mTileName;
	}
}
