package org.rscangel.client;

import org.rscangel.client.entityhandling.EntityHandler;
import org.rscangel.client.entityhandling.defs.*;
import org.rscangel.client.model.Sector;
import org.rscangel.client.model.Tile;
import org.rscangel.client.util.Config;
import org.rscangel.client.util.DataConversions;

import java.io.File;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.nio.ByteBuffer;
import java.io.BufferedInputStream;

public class EngineHandle
{
	// -------------------------------------------------------------------------------------------------------------------
	public int getStepCount( int walkSectionX, int walkSectionY, int x1,
			int y1, int x2, int y2, int[] walkSectionXArray,
			int[] walkSectionYArray, boolean flag )
	{
		int[][] tmpTiles = new int[96][96];

		for( int k1 = 0; k1 < 96; k1++ )
		{
			for( int l1 = 0; l1 < 96; l1++ )
			{
				tmpTiles[k1][l1] = 0;
			}
		}

		int i2 = 0;
		int j2 = 0;
		int k2 = walkSectionX;
		int l2 = walkSectionY;
		tmpTiles[walkSectionX][walkSectionY] = 99;
		walkSectionXArray[i2] = walkSectionX;
		walkSectionYArray[i2++] = walkSectionY;
		int i3 = walkSectionXArray.length;
		boolean flag1 = false;

		while( j2 != i2 )
		{
			k2 = walkSectionXArray[j2];
			l2 = walkSectionYArray[j2];
			j2 = (j2 + 1) % i3;

			if( k2 >= x1 && k2 <= x2 && l2 >= y1 && l2 <= y2 )
			{
				flag1 = true;
				break;
			}

			if( flag )
			{
				if( k2 > 0 && k2 - 1 >= x1 && k2 - 1 <= x2 && l2 >= y1
						&& l2 <= y2 && (walkableValue[k2 - 1][l2] & 8) == 0 )
				{
					flag1 = true;
					break;
				}

				if( k2 < 95 && k2 + 1 >= x1 && k2 + 1 <= x2 && l2 >= y1
						&& l2 <= y2 && (walkableValue[k2 + 1][l2] & 2) == 0 )
				{
					flag1 = true;
					break;
				}

				if( l2 > 0 && k2 >= x1 && k2 <= x2 && l2 - 1 >= y1
						&& l2 - 1 <= y2 && (walkableValue[k2][l2 - 1] & 4) == 0 )
				{
					flag1 = true;
					break;
				}

				if( l2 < 95 && k2 >= x1 && k2 <= x2 && l2 + 1 >= y1
						&& l2 + 1 <= y2 && (walkableValue[k2][l2 + 1] & 1) == 0 )
				{
					flag1 = true;
					break;
				}
			}

			if( k2 > 0 && tmpTiles[k2 - 1][l2] == 0
					&& (walkableValue[k2 - 1][l2] & 0x78) == 0 )
			{
				walkSectionXArray[i2] = k2 - 1;
				walkSectionYArray[i2] = l2;
				i2 = (i2 + 1) % i3;
				tmpTiles[k2 - 1][l2] = 2;
			}

			if( k2 < 95 && tmpTiles[k2 + 1][l2] == 0
					&& (walkableValue[k2 + 1][l2] & 0x72) == 0 )
			{
				walkSectionXArray[i2] = k2 + 1;
				walkSectionYArray[i2] = l2;
				i2 = (i2 + 1) % i3;
				tmpTiles[k2 + 1][l2] = 8;
			}

			if( l2 > 0 && tmpTiles[k2][l2 - 1] == 0
					&& (walkableValue[k2][l2 - 1] & 0x74) == 0 )
			{
				walkSectionXArray[i2] = k2;
				walkSectionYArray[i2] = l2 - 1;
				i2 = (i2 + 1) % i3;
				tmpTiles[k2][l2 - 1] = 1;
			}

			if( l2 < 95 && tmpTiles[k2][l2 + 1] == 0
					&& (walkableValue[k2][l2 + 1] & 0x71) == 0 )
			{
				walkSectionXArray[i2] = k2;
				walkSectionYArray[i2] = l2 + 1;
				i2 = (i2 + 1) % i3;
				tmpTiles[k2][l2 + 1] = 4;
			}

			if( k2 > 0 && l2 > 0 && (walkableValue[k2][l2 - 1] & 0x74) == 0
					&& (walkableValue[k2 - 1][l2] & 0x78) == 0
					&& (walkableValue[k2 - 1][l2 - 1] & 0x7c) == 0
					&& tmpTiles[k2 - 1][l2 - 1] == 0 )
			{
				walkSectionXArray[i2] = k2 - 1;
				walkSectionYArray[i2] = l2 - 1;
				i2 = (i2 + 1) % i3;
				tmpTiles[k2 - 1][l2 - 1] = 3;
			}

			if( k2 < 95 && l2 > 0 && (walkableValue[k2][l2 - 1] & 0x74) == 0
					&& (walkableValue[k2 + 1][l2] & 0x72) == 0
					&& (walkableValue[k2 + 1][l2 - 1] & 0x76) == 0
					&& tmpTiles[k2 + 1][l2 - 1] == 0 )
			{
				walkSectionXArray[i2] = k2 + 1;
				walkSectionYArray[i2] = l2 - 1;
				i2 = (i2 + 1) % i3;
				tmpTiles[k2 + 1][l2 - 1] = 9;
			}

			if( k2 > 0 && l2 < 95 && (walkableValue[k2][l2 + 1] & 0x71) == 0
					&& (walkableValue[k2 - 1][l2] & 0x78) == 0
					&& (walkableValue[k2 - 1][l2 + 1] & 0x79) == 0
					&& tmpTiles[k2 - 1][l2 + 1] == 0 )
			{
				walkSectionXArray[i2] = k2 - 1;
				walkSectionYArray[i2] = l2 + 1;
				i2 = (i2 + 1) % i3;
				tmpTiles[k2 - 1][l2 + 1] = 6;
			}

			if( k2 < 95 && l2 < 95 && (walkableValue[k2][l2 + 1] & 0x71) == 0
					&& (walkableValue[k2 + 1][l2] & 0x72) == 0
					&& (walkableValue[k2 + 1][l2 + 1] & 0x73) == 0
					&& tmpTiles[k2 + 1][l2 + 1] == 0 )
			{
				walkSectionXArray[i2] = k2 + 1;
				walkSectionYArray[i2] = l2 + 1;
				i2 = (i2 + 1) % i3;
				tmpTiles[k2 + 1][l2 + 1] = 12;
			}
		}

		if( !flag1 )
		{
			return -1;
		}

		j2 = 0;
		walkSectionXArray[j2] = k2;
		walkSectionYArray[j2++] = l2;
		int k3;

		for( int j3 = k3 = tmpTiles[k2][l2]; k2 != walkSectionX
				|| l2 != walkSectionY; j3 = tmpTiles[k2][l2] )
		{
			if( j3 != k3 )
			{
				k3 = j3;
				walkSectionXArray[j2] = k2;
				walkSectionYArray[j2++] = l2;
			}

			if( (j3 & 2) != 0 )
			{
				k2++;
			}
			else if( (j3 & 8) != 0 )
			{
				k2--;
			}

			if( (j3 & 1) != 0 )
			{
				l2++;
			}
			else if( (j3 & 4) != 0 )
			{
				l2--;
			}
		}

		return j2;
	}
	// -------------------------------------------------------------------------------------------------------------------
	public int getSector( int i, int j )
	{
		if( i < 0 || i >= 96 || j < 0 || j >= 96 )
		{
			return 0;
		}

		int byte0 = 0;

		if( i >= 48 && j < 48 )
		{
			byte0 = 1;
			i -= 48;
		}
		else if( i < 48 && j >= 48 )
		{
			byte0 = 2;
			j -= 48;
		}
		else if( i >= 48 && j >= 48 )
		{
			byte0 = 3;
			i -= 48;
			j -= 48;
		}

		return byte0;
	}
	// -------------------------------------------------------------------------------------------------------------------
	public int getGroundElevation( int i, int j )
	{
		if( i < 0 || i >= 96 || j < 0 || j >= 96 )
		{
			return 0;
		}

		int byte0 = 0;

		if( i >= 48 && j < 48 )
		{
			byte0 = 1;
			i -= 48;
		}
		else if( i < 48 && j >= 48 )
		{
			byte0 = 2;
			j -= 48;
		}
		else if( i >= 48 && j >= 48 )
		{
			byte0 = 3;
			i -= 48;
			j -= 48;
		}

		Sector sector = sectors[byte0];
		if( sector == null )
			return 0;

		Tile tile = sector.getTile( i, j );
		if( tile == null )
			return 0;

		return (tile.groundElevation & 0xff) * 3;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void updateObject( int x, int y, int k, int l )
	{
		if( x < 0 || y < 0 || x >= 95 || y >= 95 )
		{
			return;
		}

		if( EntityHandler.getObjectDef( k ).getType() == 1
				|| EntityHandler.getObjectDef( k ).getType() == 2 )
		{
			int i1;
			int j1;

			if( l == 0 || l == 4 )
			{
				i1 = EntityHandler.getObjectDef( k ).getWidth();
				j1 = EntityHandler.getObjectDef( k ).getHeight();
			}
			else
			{
				j1 = EntityHandler.getObjectDef( k ).getWidth();
				i1 = EntityHandler.getObjectDef( k ).getHeight();
			}

			for( int k1 = x; k1 < x + i1; k1++ )
			{
				for( int l1 = y; l1 < y + j1; l1++ )
				{
					if( EntityHandler.getObjectDef( k ).getType() == 1 )
					{
						walkableValue[k1][l1] &= 0xffbf;
					}
					else if( l == 0 )
					{
						walkableValue[k1][l1] &= 0xfffd;

						if( k1 > 0 )
						{
							andMinusWalkable( k1 - 1, l1, 8 );
						}
					}
					else if( l == 2 )
					{
						walkableValue[k1][l1] &= 0xfffb;

						if( l1 < 95 )
						{
							andMinusWalkable( k1, l1 + 1, 1 );
						}
					}
					else if( l == 4 )
					{
						walkableValue[k1][l1] &= 0xfff7;

						if( k1 < 95 )
						{
							andMinusWalkable( k1 + 1, l1, 2 );
						}
					}
					else if( l == 6 )
					{
						walkableValue[k1][l1] &= 0xfffe;

						if( l1 > 0 )
						{
							andMinusWalkable( k1, l1 - 1, 4 );
						}
					}
				}
			}

			method407( x, y, i1, j1 );
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public int getAveragedElevation( int i, int j )
	{
		int k = i >> 7;
		int l = j >> 7;
		int i1 = i & 0x7f;
		int j1 = j & 0x7f;

		if( k < 0 || l < 0 || k >= 95 || l >= 95 )
		{
			return 0;
		}

		int k1;
		int l1;
		int i2;

		if( i1 <= 128 - j1 )
		{
			k1 = getGroundElevation( k, l );
			l1 = getGroundElevation( k + 1, l ) - k1;
			i2 = getGroundElevation( k, l + 1 ) - k1;
		}
		else
		{
			k1 = getGroundElevation( k + 1, l + 1 );
			l1 = getGroundElevation( k, l + 1 ) - k1;
			i2 = getGroundElevation( k + 1, l ) - k1;
			i1 = 128 - i1;
			j1 = 128 - j1;
		}

		return k1 + (l1 * i1) / 128 + (i2 * j1) / 128;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void method400()
	{
		for( int i = 0; i < 96; i++ )
		{
			for( int j = 0; j < 96; j++ )
			{
				if( getGroundTexturesOverlay( i, j ) == 250 )
				{
					if( i == 47 && getGroundTexturesOverlay( i + 1, j ) != 250
							&& getGroundTexturesOverlay( i + 1, j ) != 2 )
					{
						setGroundTexturesOverlay( i, j, 9 );
					}
					else if( j == 47
							&& getGroundTexturesOverlay( i, j + 1 ) != 250
							&& getGroundTexturesOverlay( i, j + 1 ) != 2 )
					{
						setGroundTexturesOverlay( i, j, 9 );
					}
					else
					{
						setGroundTexturesOverlay( i, j, 2 );
					}
				}
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void method401( int i, int j, int k, boolean realodCurrent )
	{
		garbageCollect();
		method409( i, j, k, true, true );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void method402( int i, int j, int k, int l, int i1 )
	{
		Model model = aModelArray596[i + j * 8];

		for( int j1 = 0; j1 < model.anInt226; j1++ )
		{
			if( model.anIntArray272[j1] == k * 128
					&& model.anIntArray274[j1] == l * 128 )
			{
				model.method187( j1, i1 );
				return;
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void method403( int i, int j, int k, int l, int i1 )
	{
		int j1 = EntityHandler.getDoorDef( i ).getModelVar1();

		if( anIntArrayArray581[j][k] < 0x13880 )
		{
			anIntArrayArray581[j][k] += 0x13880 + j1;
		}

		if( anIntArrayArray581[l][i1] < 0x13880 )
		{
			anIntArrayArray581[l][i1] += 0x13880 + j1;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void setGroundTexturesOverlay( int i, int j, int k )
	{
		if( i < 0 || i >= 96 || j < 0 || j >= 96 )
		{
			return;
		}

		byte byte0 = 0;
		if( i >= 48 && j < 48 )
		{
			byte0 = 1;
			i -= 48;
		}
		else if( i < 48 && j >= 48 )
		{
			byte0 = 2;
			j -= 48;
		}
		else if( i >= 48 && j >= 48 )
		{
			byte0 = 3;
			i -= 48;
			j -= 48;
		}

		sectors[byte0].getTile( i, j ).groundOverlay = (byte) k;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void orWalkable( int i, int j, int k )
	{
		walkableValue[i][j] |= k;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void garbageCollect()
	{
		if( requiresClean )
		{
			camera.cleanupModels();
		}

		for( int i = 0; i < 64; i++ )
		{
			aModelArray596[i] = null;

			for( int j = 0; j < 4; j++ )
			{
				aModelArrayArray580[j][i] = null;
			}
			for( int k = 0; k < 4; k++ )
			{
				aModelArrayArray598[k][i] = null;
			}
		}

		System.gc();
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void method407( int i, int j, int k, int l )
	{
		if( i < 1 || j < 1 || i + k >= 96 || j + l >= 96 )
		{
			return;
		}

		for( int i1 = i; i1 <= i + k; i1++ )
		{
			for( int j1 = j; j1 <= j + l; j1++ )
			{
				if( (getWalkableValue( i1, j1 ) & 0x63) != 0
						|| (getWalkableValue( i1 - 1, j1 ) & 0x59) != 0
						|| (getWalkableValue( i1, j1 - 1 ) & 0x56) != 0
						|| (getWalkableValue( i1 - 1, j1 - 1 ) & 0x6c) != 0 )
				{
					method419( i1, j1, 35 );
				}
				else
				{
					method419( i1, j1, 0 );
				}
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void method408( int i, int j, int k, int l )
	{
		if( i < 0 || j < 0 || i >= 95 || j >= 95 )
			return;

		if( EntityHandler.getDoorDef( l ).getDoorType() == 1 )
		{
			if( k == 0 )
			{
				walkableValue[i][j] |= 1;

				if( j > 0 )
				{
					orWalkable( i, j - 1, 4 );
				}
			}
			else if( k == 1 )
			{
				walkableValue[i][j] |= 2;

				if( i > 0 )
				{
					orWalkable( i - 1, j, 8 );
				}
			}
			else if( k == 2 )
			{
				walkableValue[i][j] |= 0x10;
			}
			else if( k == 3 )
			{
				walkableValue[i][j] |= 0x20;
			}

			method407( i, j, 1, 1 );
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void method409( int i, int j, int k, boolean flag, boolean loadSection )
		{
		int l = (i + 24) / 48;
		int i1 = (j + 24) / 48;

		if( loadSection )
		{
			loadSection( l - 1, i1 - 1, k, 0 );
			loadSection( l, i1 - 1, k, 1 );
			loadSection( l - 1, i1, k, 2 );
			loadSection( l, i1, k, 3 );
		}

		method400();

		if( aModel == null )
		{
			aModel = new Model( 18688, 18688, true, true, false, false, true );
		}

		if( flag )
		{
			gameImage.method211();

			for( int j1 = 0; j1 < 96; j1++ )
			{
				for( int l1 = 0; l1 < 96; l1++ )
				{
					walkableValue[j1][l1] = 0;
				}
			}

			Model model = aModel;
			model.method176();

			for( int j2 = 0; j2 < 96; j2++ )
			{
				for( int i3 = 0; i3 < 96; i3++ )
				{
					int i4 = -getGroundElevation( j2, i3 );

					if( getGroundTexturesOverlay( j2, i3 ) > 0
							&& EntityHandler.getTileDef(
									getGroundTexturesOverlay( j2, i3 ) - 1 )
									.getUnknown() == 4 )
						i4 = 0;

					if( getGroundTexturesOverlay( j2 - 1, i3 ) > 0
							&& EntityHandler.getTileDef(
									getGroundTexturesOverlay( j2 - 1, i3 ) - 1 )
									.getUnknown() == 4 )
						i4 = 0;

					if( getGroundTexturesOverlay( j2, i3 - 1 ) > 0
							&& EntityHandler.getTileDef(
									getGroundTexturesOverlay( j2, i3 - 1 ) - 1 )
									.getUnknown() == 4 )
						i4 = 0;

					if( getGroundTexturesOverlay( j2 - 1, i3 - 1 ) > 0
							&& EntityHandler
									.getTileDef(
											getGroundTexturesOverlay( j2 - 1,
													i3 - 1 ) - 1 ).getUnknown() == 4 )
						i4 = 0;

					int j5 = model.method179( j2 * 128, i4, i3 * 128 );
					int j7 = (int) (Math.random() * 10D) - 5;

					model.method187( j5, j7 );
				}
			}

			for( int j3 = 0; j3 < 95; j3++ )
			{
				for( int j4 = 0; j4 < 95; j4++ )
				{
					int k5 = getGroundTexture( j3, j4 );
					int k7 = groundTextureArray[k5];
					int i10 = k7;
					int k12 = k7;
					int l14 = 0;

					if( k == 1 || k == 2 )
					{
						k7 = 0xbc614e;
						i10 = 0xbc614e;
						k12 = 0xbc614e;
					}

					if( getGroundTexturesOverlay( j3, j4 ) > 0 )
					{
						int l16 = getGroundTexturesOverlay( j3, j4 );
						int l5 = EntityHandler.getTileDef( l16 - 1 )
								.getUnknown();
						int i19 = method427( j3, j4 );
						k7 = i10 = EntityHandler.getTileDef( l16 - 1 )
								.getColour();

						if( l5 == 4 )
						{
							k7 = 1;
							i10 = 1;

							if( l16 == 12 )
							{
								k7 = 31;
								i10 = 31;
							}
						}

						if( l5 == 5 )
						{
							if( getDiagonalWalls( j3, j4 ) > 0
									&& getDiagonalWalls( j3, j4 ) < 24000 )
								if( getOverlayIfRequired( j3 - 1, j4, k12 ) != 0xbc614e
										&& getOverlayIfRequired( j3, j4 - 1,
												k12 ) != 0xbc614e )
								{
									k7 = getOverlayIfRequired( j3 - 1, j4, k12 );
									l14 = 0;
								}
								else if( getOverlayIfRequired( j3 + 1, j4, k12 ) != 0xbc614e
										&& getOverlayIfRequired( j3, j4 + 1,
												k12 ) != 0xbc614e )
								{
									i10 = getOverlayIfRequired( j3 + 1, j4, k12 );
									l14 = 0;
								}
								else if( getOverlayIfRequired( j3 + 1, j4, k12 ) != 0xbc614e
										&& getOverlayIfRequired( j3, j4 - 1,
												k12 ) != 0xbc614e )
								{
									i10 = getOverlayIfRequired( j3 + 1, j4, k12 );
									l14 = 1;
								}
								else if( getOverlayIfRequired( j3 - 1, j4, k12 ) != 0xbc614e
										&& getOverlayIfRequired( j3, j4 + 1,
												k12 ) != 0xbc614e )
								{
									k7 = getOverlayIfRequired( j3 - 1, j4, k12 );
									l14 = 1;
								}
						}
						else if( l5 != 2 || getDiagonalWalls( j3, j4 ) > 0
								&& getDiagonalWalls( j3, j4 ) < 24000 )
							if( method427( j3 - 1, j4 ) != i19
									&& method427( j3, j4 - 1 ) != i19 )
							{
								k7 = k12;
								l14 = 0;
							}
							else if( method427( j3 + 1, j4 ) != i19
									&& method427( j3, j4 + 1 ) != i19 )
							{
								i10 = k12;
								l14 = 0;
							}
							else if( method427( j3 + 1, j4 ) != i19
									&& method427( j3, j4 - 1 ) != i19 )
							{
								i10 = k12;
								l14 = 1;
							}
							else if( method427( j3 - 1, j4 ) != i19
									&& method427( j3, j4 + 1 ) != i19 )
							{
								k7 = k12;
								l14 = 1;
							}
						if( EntityHandler.getTileDef( l16 - 1 ).getObjectType() != 0 )
							walkableValue[j3][j4] |= 0x40;
						if( EntityHandler.getTileDef( l16 - 1 ).getUnknown() == 2 )
							walkableValue[j3][j4] |= 0x80;
					}
					method413( j3, j4, l14, k7, i10 );
					int i17 = ((getGroundElevation( j3 + 1, j4 + 1 ) - getGroundElevation(
							j3 + 1, j4 )) + getGroundElevation( j3, j4 + 1 ))
							- getGroundElevation( j3, j4 );
					if( k7 != i10 || i17 != 0 )
					{
						int ai[] = new int[3];
						int ai7[] = new int[3];
						if( l14 == 0 )
						{
							if( k7 != 0xbc614e )
							{
								ai[0] = j4 + j3 * 96 + 96;
								ai[1] = j4 + j3 * 96;
								ai[2] = j4 + j3 * 96 + 1;
								int l21 = model.method181( 3, ai, 0xbc614e, k7 );
								selectedX[l21] = j3;
								selectedY[l21] = j4;
								model.anIntArray258[l21] = 0x30d40 + l21;
							}
							if( i10 != 0xbc614e )
							{
								ai7[0] = j4 + j3 * 96 + 1;
								ai7[1] = j4 + j3 * 96 + 96 + 1;
								ai7[2] = j4 + j3 * 96 + 96;
								int i22 = model.method181( 3, ai7, 0xbc614e,
										i10 );
								selectedX[i22] = j3;
								selectedY[i22] = j4;
								model.anIntArray258[i22] = 0x30d40 + i22;
							}
						}
						else
						{
							if( k7 != 0xbc614e )
							{
								ai[0] = j4 + j3 * 96 + 1;
								ai[1] = j4 + j3 * 96 + 96 + 1;
								ai[2] = j4 + j3 * 96;
								int j22 = model.method181( 3, ai, 0xbc614e, k7 );
								selectedX[j22] = j3;
								selectedY[j22] = j4;
								model.anIntArray258[j22] = 0x30d40 + j22;
							}
							if( i10 != 0xbc614e )
							{
								ai7[0] = j4 + j3 * 96 + 96;
								ai7[1] = j4 + j3 * 96;
								ai7[2] = j4 + j3 * 96 + 96 + 1;
								int k22 = model.method181( 3, ai7, 0xbc614e,
										i10 );
								selectedX[k22] = j3;
								selectedY[k22] = j4;
								model.anIntArray258[k22] = 0x30d40 + k22;
							}
						}
					}
					else if( k7 != 0xbc614e )
					{
						int ai1[] = new int[4];
						ai1[0] = j4 + j3 * 96 + 96;
						ai1[1] = j4 + j3 * 96;
						ai1[2] = j4 + j3 * 96 + 1;
						ai1[3] = j4 + j3 * 96 + 96 + 1;
						int l19 = model.method181( 4, ai1, 0xbc614e, k7 );
						selectedX[l19] = j3;
						selectedY[l19] = j4;
						model.anIntArray258[l19] = 0x30d40 + l19;
					}
				}

			}

			for( int k4 = 1; k4 < 95; k4++ )
			{
				for( int i6 = 1; i6 < 95; i6++ )
				{
					if( getGroundTexturesOverlay( k4, i6 ) > 0
							&& EntityHandler.getTileDef(
									getGroundTexturesOverlay( k4, i6 ) - 1 )
									.getUnknown() == 4 )
					{
						int l7 = EntityHandler.getTileDef(
								getGroundTexturesOverlay( k4, i6 ) - 1 )
								.getColour();
						int j10 = model.method179( k4 * 128,
								-getGroundElevation( k4, i6 ), i6 * 128 );
						int l12 = model.method179( (k4 + 1) * 128,
								-getGroundElevation( k4 + 1, i6 ), i6 * 128 );
						int i15 = model.method179( (k4 + 1) * 128,
								-getGroundElevation( k4 + 1, i6 + 1 ),
								(i6 + 1) * 128 );
						int j17 = model.method179( k4 * 128,
								-getGroundElevation( k4, i6 + 1 ),
								(i6 + 1) * 128 );
						int ai2[] = { j10, l12, i15, j17 };
						int i20 = model.method181( 4, ai2, l7, 0xbc614e );
						selectedX[i20] = k4;
						selectedY[i20] = i6;
						model.anIntArray258[i20] = 0x30d40 + i20;
						method413( k4, i6, 0, l7, l7 );
					}
					else if( getGroundTexturesOverlay( k4, i6 ) == 0
							|| EntityHandler.getTileDef(
									getGroundTexturesOverlay( k4, i6 ) - 1 )
									.getUnknown() != 3 )
					{
						if( getGroundTexturesOverlay( k4, i6 + 1 ) > 0
								&& EntityHandler
										.getTileDef(
												getGroundTexturesOverlay( k4,
														i6 + 1 ) - 1 )
										.getUnknown() == 4 )
						{
							int i8 = EntityHandler.getTileDef(
									getGroundTexturesOverlay( k4, i6 + 1 ) - 1 )
									.getColour();
							int k10 = model.method179( k4 * 128,
									-getGroundElevation( k4, i6 ), i6 * 128 );
							int i13 = model
									.method179( (k4 + 1) * 128,
											-getGroundElevation( k4 + 1, i6 ),
											i6 * 128 );
							int j15 = model.method179( (k4 + 1) * 128,
									-getGroundElevation( k4 + 1, i6 + 1 ),
									(i6 + 1) * 128 );
							int k17 = model.method179( k4 * 128,
									-getGroundElevation( k4, i6 + 1 ),
									(i6 + 1) * 128 );
							int ai3[] = { k10, i13, j15, k17 };
							int j20 = model.method181( 4, ai3, i8, 0xbc614e );
							selectedX[j20] = k4;
							selectedY[j20] = i6;
							model.anIntArray258[j20] = 0x30d40 + j20;
							method413( k4, i6, 0, i8, i8 );
						}
						if( getGroundTexturesOverlay( k4, i6 - 1 ) > 0
								&& EntityHandler
										.getTileDef(
												getGroundTexturesOverlay( k4,
														i6 - 1 ) - 1 )
										.getUnknown() == 4 )
						{
							int j8 = EntityHandler.getTileDef(
									getGroundTexturesOverlay( k4, i6 - 1 ) - 1 )
									.getColour();
							int l10 = model.method179( k4 * 128,
									-getGroundElevation( k4, i6 ), i6 * 128 );
							int j13 = model
									.method179( (k4 + 1) * 128,
											-getGroundElevation( k4 + 1, i6 ),
											i6 * 128 );
							int k15 = model.method179( (k4 + 1) * 128,
									-getGroundElevation( k4 + 1, i6 + 1 ),
									(i6 + 1) * 128 );
							int l17 = model.method179( k4 * 128,
									-getGroundElevation( k4, i6 + 1 ),
									(i6 + 1) * 128 );
							int ai4[] = { l10, j13, k15, l17 };
							int k20 = model.method181( 4, ai4, j8, 0xbc614e );
							selectedX[k20] = k4;
							selectedY[k20] = i6;
							model.anIntArray258[k20] = 0x30d40 + k20;
							method413( k4, i6, 0, j8, j8 );
						}
						if( getGroundTexturesOverlay( k4 + 1, i6 ) > 0
								&& EntityHandler
										.getTileDef(
												getGroundTexturesOverlay(
														k4 + 1, i6 ) - 1 )
										.getUnknown() == 4 )
						{
							int k8 = EntityHandler.getTileDef(
									getGroundTexturesOverlay( k4 + 1, i6 ) - 1 )
									.getColour();
							int i11 = model.method179( k4 * 128,
									-getGroundElevation( k4, i6 ), i6 * 128 );
							int k13 = model
									.method179( (k4 + 1) * 128,
											-getGroundElevation( k4 + 1, i6 ),
											i6 * 128 );
							int l15 = model.method179( (k4 + 1) * 128,
									-getGroundElevation( k4 + 1, i6 + 1 ),
									(i6 + 1) * 128 );
							int i18 = model.method179( k4 * 128,
									-getGroundElevation( k4, i6 + 1 ),
									(i6 + 1) * 128 );
							int ai5[] = { i11, k13, l15, i18 };
							int l20 = model.method181( 4, ai5, k8, 0xbc614e );
							selectedX[l20] = k4;
							selectedY[l20] = i6;
							model.anIntArray258[l20] = 0x30d40 + l20;
							method413( k4, i6, 0, k8, k8 );
						}
						if( getGroundTexturesOverlay( k4 - 1, i6 ) > 0
								&& EntityHandler
										.getTileDef(
												getGroundTexturesOverlay(
														k4 - 1, i6 ) - 1 )
										.getUnknown() == 4 )
						{
							int l8 = EntityHandler.getTileDef(
									getGroundTexturesOverlay( k4 - 1, i6 ) - 1 )
									.getColour();
							int j11 = model.method179( k4 * 128,
									-getGroundElevation( k4, i6 ), i6 * 128 );
							int l13 = model
									.method179( (k4 + 1) * 128,
											-getGroundElevation( k4 + 1, i6 ),
											i6 * 128 );
							int i16 = model.method179( (k4 + 1) * 128,
									-getGroundElevation( k4 + 1, i6 + 1 ),
									(i6 + 1) * 128 );
							int j18 = model.method179( k4 * 128,
									-getGroundElevation( k4, i6 + 1 ),
									(i6 + 1) * 128 );
							int ai6[] = { j11, l13, i16, j18 };
							int i21 = model.method181( 4, ai6, l8, 0xbc614e );
							selectedX[i21] = k4;
							selectedY[i21] = i6;
							model.anIntArray258[i21] = 0x30d40 + i21;
							method413( k4, i6, 0, l8, l8 );
						}
					}
				}
			}

			model.method184( true, 40, 48, -50, -10, -50 );
			aModelArray596 = aModel.method182( 0, 0, 1536, 1536, 8, 64, 233,
					false );
			for( int j6 = 0; j6 < 64; j6++ )
			{
				camera.addModel( aModelArray596[j6] );
			}
			for( int i9 = 0; i9 < 96; i9++ )
			{
				for( int k11 = 0; k11 < 96; k11++ )
				{
					anIntArrayArray581[i9][k11] = getGroundElevation( i9, k11 );
				}
			}
		}
		aModel.method176();
		int k1 = 0x606060;
		for( int i2 = 0; i2 < 95; i2++ )
		{
			for( int k2 = 0; k2 < 95; k2++ )
			{
				int k3 = getVerticalWall( i2, k2 );
				if( k3 > 0
						&& EntityHandler.getDoorDef( k3 - 1 ).getUnknown() == 0 )
				{
					method421( aModel, k3 - 1, i2, k2, i2 + 1, k2 );
					if( flag
							&& EntityHandler.getDoorDef( k3 - 1 ).getDoorType() != 0 )
					{
						walkableValue[i2][k2] |= 1;
						if( k2 > 0 )
						{
							orWalkable( i2, k2 - 1, 4 );
						}
					}
					if( flag )
					{
						gameImage.drawLineX( i2 * 3, k2 * 3, 3, k1 );
					}
				}
				k3 = getHorizontalWall( i2, k2 );
				if( k3 > 0
						&& EntityHandler.getDoorDef( k3 - 1 ).getUnknown() == 0 )
				{
					method421( aModel, k3 - 1, i2, k2, i2, k2 + 1 );
					if( flag
							&& EntityHandler.getDoorDef( k3 - 1 ).getDoorType() != 0 )
					{
						walkableValue[i2][k2] |= 2;
						if( i2 > 0 )
						{
							orWalkable( i2 - 1, k2, 8 );
						}
					}
					if( flag )
					{
						gameImage.drawLineY( i2 * 3, k2 * 3, 3, k1 );
					}
				}
				k3 = getDiagonalWalls( i2, k2 );
				if( k3 > 0 && k3 < 12000
						&& EntityHandler.getDoorDef( k3 - 1 ).getUnknown() == 0 )
				{
					method421( aModel, k3 - 1, i2, k2, i2 + 1, k2 + 1 );
					if( flag
							&& EntityHandler.getDoorDef( k3 - 1 ).getDoorType() != 0 )
					{
						walkableValue[i2][k2] |= 0x20;
					}
					if( flag )
					{
						gameImage.setPixelColour( i2 * 3, k2 * 3, k1 );
						gameImage.setPixelColour( i2 * 3 + 1, k2 * 3 + 1, k1 );
						gameImage.setPixelColour( i2 * 3 + 2, k2 * 3 + 2, k1 );
					}
				}
				if( k3 > 12000
						&& k3 < 24000
						&& EntityHandler.getDoorDef( k3 - 12001 ).getUnknown() == 0 )
				{
					method421( aModel, k3 - 12001, i2 + 1, k2, i2, k2 + 1 );
					if( flag
							&& EntityHandler.getDoorDef( k3 - 12001 )
									.getDoorType() != 0 )
					{
						walkableValue[i2][k2] |= 0x10;
					}
					if( flag )
					{
						gameImage.setPixelColour( i2 * 3 + 2, k2 * 3, k1 );
						gameImage.setPixelColour( i2 * 3 + 1, k2 * 3 + 1, k1 );
						gameImage.setPixelColour( i2 * 3, k2 * 3 + 2, k1 );
					}
				}
			}
		}

		if( flag )
		{
			gameImage.storeSpriteHoriz( mudclient.SPRITE_MEDIA_START - 1, 0, 0,
					285, 285 );
		}
		aModel.method184( false, 60, 24, -50, -10, -50 );
		aModelArrayArray580[k] = aModel.method182( 0, 0, 1536, 1536, 8, 64,
				338, true );
		for( int l2 = 0; l2 < 64; l2++ )
		{
			camera.addModel( aModelArrayArray580[k][l2] );
		}
		for( int l3 = 0; l3 < 95; l3++ )
		{
			for( int l4 = 0; l4 < 95; l4++ )
			{
				int k6 = getVerticalWall( l3, l4 );
				if( k6 > 0 )
					method403( k6 - 1, l3, l4, l3 + 1, l4 );
				k6 = getHorizontalWall( l3, l4 );
				if( k6 > 0 )
					method403( k6 - 1, l3, l4, l3, l4 + 1 );
				k6 = getDiagonalWalls( l3, l4 );
				if( k6 > 0 && k6 < 12000 )
					method403( k6 - 1, l3, l4, l3 + 1, l4 + 1 );
				if( k6 > 12000 && k6 < 24000 )
					method403( k6 - 12001, l3 + 1, l4, l3, l4 + 1 );
			}
		}

		for( int i5 = 1; i5 < 95; i5++ )
		{
			for( int l6 = 1; l6 < 95; l6++ )
			{
				int j9 = getRoofTexture( i5, l6 );
				if( j9 > 0 )
				{
					int l11 = i5;
					int i14 = l6;
					int j16 = i5 + 1;
					int k18 = l6;
					int j19 = i5 + 1;
					int j21 = l6 + 1;
					int l22 = i5;
					int j23 = l6 + 1;
					int l23 = 0;
					int j24 = anIntArrayArray581[l11][i14];
					int l24 = anIntArrayArray581[j16][k18];
					int j25 = anIntArrayArray581[j19][j21];
					int l25 = anIntArrayArray581[l22][j23];
					if( j24 > 0x13880 )
						j24 -= 0x13880;
					if( l24 > 0x13880 )
						l24 -= 0x13880;
					if( j25 > 0x13880 )
						j25 -= 0x13880;
					if( l25 > 0x13880 )
						l25 -= 0x13880;
					if( j24 > l23 )
						l23 = j24;
					if( l24 > l23 )
						l23 = l24;
					if( j25 > l23 )
						l23 = j25;
					if( l25 > l23 )
						l23 = l25;
					if( l23 >= 0x13880 )
						l23 -= 0x13880;
					if( j24 < 0x13880 )
						anIntArrayArray581[l11][i14] = l23;
					else
						anIntArrayArray581[l11][i14] -= 0x13880;
					if( l24 < 0x13880 )
						anIntArrayArray581[j16][k18] = l23;
					else
						anIntArrayArray581[j16][k18] -= 0x13880;
					if( j25 < 0x13880 )
						anIntArrayArray581[j19][j21] = l23;
					else
						anIntArrayArray581[j19][j21] -= 0x13880;
					if( l25 < 0x13880 )
						anIntArrayArray581[l22][j23] = l23;
					else
						anIntArrayArray581[l22][j23] -= 0x13880;
				}
			}
		}

		aModel.method176();
		for( int i7 = 1; i7 < 95; i7++ )
		{
			for( int k9 = 1; k9 < 95; k9++ )
			{
				int i12 = getRoofTexture( i7, k9 );
				if( i12 > 0 )
				{
					int j14 = i7;
					int k16 = k9;
					int l18 = i7 + 1;
					int k19 = k9;
					int k21 = i7 + 1;
					int i23 = k9 + 1;
					int k23 = i7;
					int i24 = k9 + 1;
					int k24 = i7 * 128;
					int i25 = k9 * 128;
					int k25 = k24 + 128;
					int i26 = i25 + 128;
					int j26 = k24;
					int k26 = i25;
					int l26 = k25;
					int i27 = i26;
					int j27 = anIntArrayArray581[j14][k16];
					int k27 = anIntArrayArray581[l18][k19];
					int l27 = anIntArrayArray581[k21][i23];
					int i28 = anIntArrayArray581[k23][i24];

					int j28 = 0;
					ElevationDef elevation = EntityHandler
							.getElevationDef( i12 - 1 );
					if( elevation != null )
					{
						j28 = elevation.getUnknown1();
					}
					else
					{
						j28 = 0;
					}

					if( method424( j14, k16 ) && j27 < 0x13880 )
					{
						j27 += j28 + 0x13880;
						anIntArrayArray581[j14][k16] = j27;
					}
					if( method424( l18, k19 ) && k27 < 0x13880 )
					{
						k27 += j28 + 0x13880;
						anIntArrayArray581[l18][k19] = k27;
					}
					if( method424( k21, i23 ) && l27 < 0x13880 )
					{
						l27 += j28 + 0x13880;
						anIntArrayArray581[k21][i23] = l27;
					}
					if( method424( k23, i24 ) && i28 < 0x13880 )
					{
						i28 += j28 + 0x13880;
						anIntArrayArray581[k23][i24] = i28;
					}
					if( j27 >= 0x13880 )
						j27 -= 0x13880;
					if( k27 >= 0x13880 )
						k27 -= 0x13880;
					if( l27 >= 0x13880 )
						l27 -= 0x13880;
					if( i28 >= 0x13880 )
						i28 -= 0x13880;
					byte byte0 = 16;
					if( !method416( j14 - 1, k16 ) )
						k24 -= byte0;
					if( !method416( j14 + 1, k16 ) )
						k24 += byte0;
					if( !method416( j14, k16 - 1 ) )
						i25 -= byte0;
					if( !method416( j14, k16 + 1 ) )
						i25 += byte0;
					if( !method416( l18 - 1, k19 ) )
						k25 -= byte0;
					if( !method416( l18 + 1, k19 ) )
						k25 += byte0;
					if( !method416( l18, k19 - 1 ) )
						k26 -= byte0;
					if( !method416( l18, k19 + 1 ) )
						k26 += byte0;
					if( !method416( k21 - 1, i23 ) )
						l26 -= byte0;
					if( !method416( k21 + 1, i23 ) )
						l26 += byte0;
					if( !method416( k21, i23 - 1 ) )
						i26 -= byte0;
					if( !method416( k21, i23 + 1 ) )
						i26 += byte0;
					if( !method416( k23 - 1, i24 ) )
						j26 -= byte0;
					if( !method416( k23 + 1, i24 ) )
						j26 += byte0;
					if( !method416( k23, i24 - 1 ) )
						i27 -= byte0;
					if( !method416( k23, i24 + 1 ) )
						i27 += byte0;
					i12 = EntityHandler.getElevationDef( i12 - 1 )
							.getUnknown2();
					j27 = -j27;
					k27 = -k27;
					l27 = -l27;
					i28 = -i28;
					if( getDiagonalWalls( i7, k9 ) > 12000
							&& getDiagonalWalls( i7, k9 ) < 24000
							&& getRoofTexture( i7 - 1, k9 - 1 ) == 0 )
					{
						int ai8[] = new int[3];
						ai8[0] = aModel.method179( l26, l27, i26 );
						ai8[1] = aModel.method179( j26, i28, i27 );
						ai8[2] = aModel.method179( k25, k27, k26 );
						aModel.method181( 3, ai8, i12, 0xbc614e );
					}
					else if( getDiagonalWalls( i7, k9 ) > 12000
							&& getDiagonalWalls( i7, k9 ) < 24000
							&& getRoofTexture( i7 + 1, k9 + 1 ) == 0 )
					{
						int ai9[] = new int[3];
						ai9[0] = aModel.method179( k24, j27, i25 );
						ai9[1] = aModel.method179( k25, k27, k26 );
						ai9[2] = aModel.method179( j26, i28, i27 );
						aModel.method181( 3, ai9, i12, 0xbc614e );
					}
					else if( getDiagonalWalls( i7, k9 ) > 0
							&& getDiagonalWalls( i7, k9 ) < 12000
							&& getRoofTexture( i7 + 1, k9 - 1 ) == 0 )
					{
						int ai10[] = new int[3];
						ai10[0] = aModel.method179( j26, i28, i27 );
						ai10[1] = aModel.method179( k24, j27, i25 );
						ai10[2] = aModel.method179( l26, l27, i26 );
						aModel.method181( 3, ai10, i12, 0xbc614e );
					}
					else if( getDiagonalWalls( i7, k9 ) > 0
							&& getDiagonalWalls( i7, k9 ) < 12000
							&& getRoofTexture( i7 - 1, k9 + 1 ) == 0 )
					{
						int ai11[] = new int[3];
						ai11[0] = aModel.method179( k25, k27, k26 );
						ai11[1] = aModel.method179( l26, l27, i26 );
						ai11[2] = aModel.method179( k24, j27, i25 );
						aModel.method181( 3, ai11, i12, 0xbc614e );
					}
					else if( j27 == k27 && l27 == i28 )
					{
						int ai12[] = new int[4];
						ai12[0] = aModel.method179( k24, j27, i25 );
						ai12[1] = aModel.method179( k25, k27, k26 );
						ai12[2] = aModel.method179( l26, l27, i26 );
						ai12[3] = aModel.method179( j26, i28, i27 );
						aModel.method181( 4, ai12, i12, 0xbc614e );
					}
					else if( j27 == i28 && k27 == l27 )
					{
						int ai13[] = new int[4];
						ai13[0] = aModel.method179( j26, i28, i27 );
						ai13[1] = aModel.method179( k24, j27, i25 );
						ai13[2] = aModel.method179( k25, k27, k26 );
						ai13[3] = aModel.method179( l26, l27, i26 );
						aModel.method181( 4, ai13, i12, 0xbc614e );
					}
					else
					{
						boolean flag1 = true;
						if( getRoofTexture( i7 - 1, k9 - 1 ) > 0 )
						{
							flag1 = false;
						}
						if( getRoofTexture( i7 + 1, k9 + 1 ) > 0 )
						{
							flag1 = false;
						}
						if( !flag1 )
						{
							int ai14[] = new int[3];
							ai14[0] = aModel.method179( k25, k27, k26 );
							ai14[1] = aModel.method179( l26, l27, i26 );
							ai14[2] = aModel.method179( k24, j27, i25 );
							aModel.method181( 3, ai14, i12, 0xbc614e );
							int ai16[] = new int[3];
							ai16[0] = aModel.method179( j26, i28, i27 );
							ai16[1] = aModel.method179( k24, j27, i25 );
							ai16[2] = aModel.method179( l26, l27, i26 );
							aModel.method181( 3, ai16, i12, 0xbc614e );
						}
						else
						{
							int ai15[] = new int[3];
							ai15[0] = aModel.method179( k24, j27, i25 );
							ai15[1] = aModel.method179( k25, k27, k26 );
							ai15[2] = aModel.method179( j26, i28, i27 );
							aModel.method181( 3, ai15, i12, 0xbc614e );
							int ai17[] = new int[3];
							ai17[0] = aModel.method179( l26, l27, i26 );
							ai17[1] = aModel.method179( j26, i28, i27 );
							ai17[2] = aModel.method179( k25, k27, k26 );
							aModel.method181( 3, ai17, i12, 0xbc614e );
						}
					}
				}
			}
		}
		aModel.method184( true, 50, 50, -50, -10, -50 );
		aModelArrayArray598[k] = aModel.method182( 0, 0, 1536, 1536, 8, 64,
				169, true );
		for( int l9 = 0; l9 < 64; l9++ )
		{
			camera.addModel( aModelArrayArray598[k][l9] );
		}
		if( aModelArrayArray598[k][0] == null )
		{
			throw new RuntimeException( "null roof!" );
		}
		for( int j12 = 0; j12 < 96; j12++ )
		{
			for( int k14 = 0; k14 < 96; k14++ )
			{
				if( anIntArrayArray581[j12][k14] >= 0x13880 )
				{
					anIntArrayArray581[j12][k14] -= 0x13880;
				}
			}
		}
	}

	public int getRoofTexture( int i, int j )
	{
		if( i < 0 || i >= 96 || j < 0 || j >= 96 )
		{
			return 0;
		}
		byte byte0 = 0;
		if( i >= 48 && j < 48 )
		{
			byte0 = 1;
			i -= 48;
		}
		else if( i < 48 && j >= 48 )
		{
			byte0 = 2;
			j -= 48;
		}
		else if( i >= 48 && j >= 48 )
		{
			byte0 = 3;
			i -= 48;
			j -= 48;
		}
		return sectors[byte0].getTile( i, j ).roofTexture;
	}

	public void andMinusWalkable( int i, int j, int k )
	{
		walkableValue[i][j] &= 65535 - k;
	}

	public void method412( int i, int j, int k, int l )
	{
		if( i < 0 || j < 0 || i >= 95 || j >= 95 )
		{
			return;
		}
		if( EntityHandler.getObjectDef( k ).getType() == 1
				|| EntityHandler.getObjectDef( k ).getType() == 2 )
		{
			int i1;
			int j1;
			if( l == 0 || l == 4 )
			{
				i1 = EntityHandler.getObjectDef( k ).getWidth();
				j1 = EntityHandler.getObjectDef( k ).getHeight();
			}
			else
			{
				j1 = EntityHandler.getObjectDef( k ).getWidth();
				i1 = EntityHandler.getObjectDef( k ).getHeight();
			}
			for( int k1 = i; k1 < i + i1; k1++ )
			{
				for( int l1 = j; l1 < j + j1; l1++ )
					if( EntityHandler.getObjectDef( k ).getType() == 1 )
						walkableValue[k1][l1] |= 0x40;
					else if( l == 0 )
					{
						walkableValue[k1][l1] |= 2;
						if( k1 > 0 )
						{
							orWalkable( k1 - 1, l1, 8 );
						}
					}
					else if( l == 2 )
					{
						walkableValue[k1][l1] |= 4;
						if( l1 < 95 )
						{
							orWalkable( k1, l1 + 1, 1 );
						}
					}
					else if( l == 4 )
					{
						walkableValue[k1][l1] |= 8;
						if( k1 < 95 )
						{
							orWalkable( k1 + 1, l1, 2 );
						}
					}
					else if( l == 6 )
					{
						walkableValue[k1][l1] |= 1;
						if( l1 > 0 )
						{
							orWalkable( k1, l1 - 1, 4 );
						}
					}
			}
			method407( i, j, i1, j1 );
		}
	}

	public void method413( int i, int j, int k, int l, int i1 )
	{
		int j1 = i * 3;
		int k1 = j * 3;
		int l1 = camera.method302( l );
		int i2 = camera.method302( i1 );
		l1 = l1 >> 1 & 0x7f7f7f;
		i2 = i2 >> 1 & 0x7f7f7f;
		if( k == 0 )
		{
			gameImage.drawLineX( j1, k1, 3, l1 );
			gameImage.drawLineX( j1, k1 + 1, 2, l1 );
			gameImage.drawLineX( j1, k1 + 2, 1, l1 );
			gameImage.drawLineX( j1 + 2, k1 + 1, 1, i2 );
			gameImage.drawLineX( j1 + 1, k1 + 2, 2, i2 );
		}
		else if( k == 1 )
		{
			gameImage.drawLineX( j1, k1, 3, i2 );
			gameImage.drawLineX( j1 + 1, k1 + 1, 2, i2 );
			gameImage.drawLineX( j1 + 2, k1 + 2, 1, i2 );
			gameImage.drawLineX( j1, k1 + 1, 1, l1 );
			gameImage.drawLineX( j1, k1 + 2, 2, l1 );
		}
	}

	public void updateDoor( int x, int y, int dir, int type )
	{
		if( x < 0 || y < 0 || x >= 95 || y >= 95 )
		{
			return;
		}
		if( EntityHandler.getDoorDef( type ).getDoorType() == 1 )
		{
			if( dir == 0 )
			{
				walkableValue[x][y] &= 0xfffe;
				if( y > 0 )
				{
					andMinusWalkable( x, y - 1, 4 );
				}
			}
			else if( dir == 1 )
			{
				walkableValue[x][y] &= 0xfffd;
				if( x > 0 )
				{
					andMinusWalkable( x - 1, y, 8 );
				}
			}
			else if( dir == 2 )
			{
				walkableValue[x][y] &= 0xffef;
			}
			else if( dir == 3 )
			{
				walkableValue[x][y] &= 0xffdf;
			}
			method407( x, y, 1, 1 );
		}
	}

	public int getVerticalWall( int i, int j )
	{
		if( i < 0 || i >= 96 || j < 0 || j >= 96 )
		{
			return 0;
		}
		byte byte0 = 0;
		if( i >= 48 && j < 48 )
		{
			byte0 = 1;
			i -= 48;
		}
		else if( i < 48 && j >= 48 )
		{
			byte0 = 2;
			j -= 48;
		}
		else if( i >= 48 && j >= 48 )
		{
			byte0 = 3;
			i -= 48;
			j -= 48;
		}
		return sectors[byte0].getTile( i, j ).verticalWall & 0xff;
	}

	public boolean method416( int i, int j )
	{
		return getRoofTexture( i, j ) > 0 || getRoofTexture( i - 1, j ) > 0
				|| getRoofTexture( i - 1, j - 1 ) > 0
				|| getRoofTexture( i, j - 1 ) > 0;
	}

	public int getGroundTexturesOverlay( int i, int j )
	{
		if( i < 0 || i >= 96 || j < 0 || j >= 96 )
		{
			return 0;
		}
		byte byte0 = 0;
		if( i >= 48 && j < 48 )
		{
			byte0 = 1;
			i -= 48;
		}
		else if( i < 48 && j >= 48 )
		{
			byte0 = 2;
			j -= 48;
		}
		else if( i >= 48 && j >= 48 )
		{
			byte0 = 3;
			i -= 48;
			j -= 48;
		}
		return sectors[byte0].getTile( i, j ).groundOverlay & 0xff;
	}

	public void method419( int i, int j, int k )
	{
		int l = i / 12;
		int i1 = j / 12;
		int j1 = (i - 1) / 12;
		int k1 = (j - 1) / 12;
		method402( l, i1, i, j, k );
		if( l != j1 )
		{
			method402( j1, i1, i, j, k );
		}
		if( i1 != k1 )
		{
			method402( l, k1, i, j, k );
		}
		if( l != j1 && i1 != k1 )
		{
			method402( j1, k1, i, j, k );
		}
	}

	public int getDiagonalWalls( int i, int j )
	{
		if( i < 0 || i >= 96 || j < 0 || j >= 96 )
		{
			return 0;
		}
		byte byte0 = 0;
		if( i >= 48 && j < 48 )
		{
			byte0 = 1;
			i -= 48;
		}
		else if( i < 48 && j >= 48 )
		{
			byte0 = 2;
			j -= 48;
		}
		else if( i >= 48 && j >= 48 )
		{
			byte0 = 3;
			i -= 48;
			j -= 48;
		}
		return sectors[byte0].getTile( i, j ).diagonalWalls;
	}

	public void method421( Model model, int i, int j, int k, int l, int i1 )
	{
		method419( j, k, 40 );
		method419( l, i1, 40 );
		int j1 = EntityHandler.getDoorDef( i ).getModelVar1();
		int k1 = EntityHandler.getDoorDef( i ).getModelVar2();
		int l1 = EntityHandler.getDoorDef( i ).getModelVar3();
		int i2 = j * 128;
		int j2 = k * 128;
		int k2 = l * 128;
		int l2 = i1 * 128;
		int i3 = model.method179( i2, -anIntArrayArray581[j][k], j2 );
		int j3 = model.method179( i2, -anIntArrayArray581[j][k] - j1, j2 );
		int k3 = model.method179( k2, -anIntArrayArray581[l][i1] - j1, l2 );
		int l3 = model.method179( k2, -anIntArrayArray581[l][i1], l2 );
		int i4 = model.method181( 4, new int[] { i3, j3, k3, l3 }, k1, l1 );
		if( EntityHandler.getDoorDef( i ).getUnknown() == 5 )
		{
			model.anIntArray258[i4] = 30000 + i;
		}
		else
		{
			model.anIntArray258[i4] = 0;
		}
	}

	public int getOverlayIfRequired( int x, int y, int underlay )
	{
		int texture = getGroundTexturesOverlay( x, y );
		if( texture == 0 )
		{
			return underlay;
		}
		return EntityHandler.getTileDef( texture - 1 ).getColour();
	}

	public int getGroundTexture( int i, int j )
	{
		if( i < 0 || i >= 96 || j < 0 || j >= 96 )
		{
			return 0;
		}
		byte byte0 = 0;
		if( i >= 48 && j < 48 )
		{
			byte0 = 1;
			i -= 48;
		}
		else if( i < 48 && j >= 48 )
		{
			byte0 = 2;
			j -= 48;
		}
		else if( i >= 48 && j >= 48 )
		{
			byte0 = 3;
			i -= 48;
			j -= 48;
		}
		return sectors[byte0].getTile( i, j ).groundTexture & 0xFF;
	}

	public boolean method424( int i, int j )
	{
		return getRoofTexture( i, j ) > 0 && getRoofTexture( i - 1, j ) > 0
				&& getRoofTexture( i - 1, j - 1 ) > 0
				&& getRoofTexture( i, j - 1 ) > 0;
	}

	public int getWalkableValue( int i, int j )
	{
		if( i < 0 || j < 0 || i >= 96 || j >= 96 )
		{
			return 0;
		}
		return walkableValue[i][j];
	}

	public int getHorizontalWall( int i, int j )
	{
		if( i < 0 || i >= 96 || j < 0 || j >= 96 )
		{
			return 0;
		}
		byte byte0 = 0;
		if( i >= 48 && j < 48 )
		{
			byte0 = 1;
			i -= 48;
		}
		else if( i < 48 && j >= 48 )
		{
			byte0 = 2;
			j -= 48;
		}
		else if( i >= 48 && j >= 48 )
		{
			byte0 = 3;
			i -= 48;
			j -= 48;
		}
		return sectors[byte0].getTile( i, j ).horizontalWall & 0xff;
	}

	public int method427( int x, int y )
	{
		int texture = getGroundTexturesOverlay( x, y );
		if( texture == 0 )
		{
			return -1;
		}
		return EntityHandler.getTileDef( texture - 1 ).getUnknown() != 2 ? 0
				: 1;
	}

	public void registerObjectDir( int x, int y, int dir )
	{
		if( x < 0 || x >= 96 || y < 0 || y >= 96 )
		{
			return;
		}
		objectDirs[x][y] = dir;
	}

	public EngineHandle( Camera camera, GameImage gameImage )
	{
		this.camera = camera;
		this.gameImage = gameImage;

		objectDirs = new int[96][96];
		selectedX = new int[18432];
		selectedY = new int[18432];

		aModelArrayArray580 = new Model[4][64];
		aModelArrayArray598 = new Model[4][64];
		anIntArrayArray581 = new int[96][96];
		requiresClean = true;
		aModelArray596 = new Model[64];
		groundTextureArray = new int[256];
		walkableValue = new int[96][96];
		playerIsAlive = false;
		sectors = new Sector[4];

		try
		{
			tileArchive = new ZipFile( new File( Config.CONF_DIR
					+ "/Landscape.dq" ) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}

		for( int i = 0; i < 64; i++ )
		{
			groundTextureArray[i] = Camera.method305( 255 - i * 4,
					255 - (int) ((double) i * 1.75D), 255 - i * 4 );
		}
		for( int j = 0; j < 64; j++ )
		{
			groundTextureArray[j + 64] = Camera.method305( j * 3, 144, 0 );
		}
		for( int k = 0; k < 64; k++ )
		{
			groundTextureArray[k + 128] = Camera.method305(
					192 - (int) ((double) k * 1.5D),
					144 - (int) ((double) k * 1.5D), 0 );
		}
		for( int l = 0; l < 64; l++ )
		{
			groundTextureArray[l + 192] = Camera.method305(
					96 - (int) ((double) l * 1.5D),
					48 + (int) ((double) l * 1.5D), 0 );
		}
	}

	public void loadSection( int sectionX, int sectionY, int height, int sector )
	{
		if( sectors[sector] != null )
			return;

		Sector s = null;
		try
		{
			String filename = "h" + height + "x" + sectionX + "y" + sectionY;
			ZipEntry e = tileArchive.getEntry( filename );
			if( e == null )
			{
				s = new Sector( filename );
				if( height == 0 || height == 3 )
				{
					for( int i = 0; i < 2304; i++ )
					{
						Tile tile = s.getTile( i );
						if( height == 0 )
						{
							tile.groundOverlay = (byte)(-6);
						}
						else
						{
							tile.groundOverlay = 5;
							tile.mDefaultGroundOverlay = 8;
							tile.mIsEmpty = true;
						}

						//s.getTile( i ).groundOverlay = (byte) (height == 0 ? -6 : 8);
						// s.getTile(i).groundOverlay = (byte)(height == 0 ? -6 : 5);
					}
				}
			}
			else
			{
				ByteBuffer data = DataConversions.streamToBuffer(new BufferedInputStream(tileArchive.getInputStream( e )));
				s = Sector.unpack( data, filename );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
		sectors[sector] = s;
	}

	public ZipFile getTileArchive()
	{
		return tileArchive;
	}

	public void reloadTileArchive()
	{
		try
		{
			if( tileArchive != null )
			{
				tileArchive.close();
			}

			tileArchive = new ZipFile( new File( Config.CONF_DIR
					+ "/Landscape.dq" ) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	private ZipFile tileArchive;

	public int[][] objectDirs;
	public int[] selectedX;
	public int[] selectedY;
	public int[][] walkableValue;
	public boolean playerIsAlive;
	public Model aModel;
	private GameImage gameImage;
	public Camera camera;
	private boolean requiresClean;

	public Sector[] sectors;

	Model[][] aModelArrayArray580;
	int[][] anIntArrayArray581;
	Model[] aModelArray596;
	private int[] groundTextureArray;
	Model[][] aModelArrayArray598;
}
