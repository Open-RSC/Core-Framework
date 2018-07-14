package org.rscangel.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SectionsConfig
{
	public SectionsConfig()
	{
		filename = "noname.cfg";
	}

	public SectionsConfig( String fname )
	{
		filename = fname + ".cfg";
	}

	public String getFileName()
	{
		return filename;
	}

	public String[] getSetionsList()
	{
		return parseBufferToStrings( getFileBytes() );
	}

	public int getMinHeight()
	{
		return minH;
	}

	public int getMaxHeight()
	{
		return maxH;
	}

	public int[] getMinX()
	{
		return minX;
	}

	public int[] getMaxX()
	{
		return maxX;
	}

	public int[][] getMinY()
	{
		return minY;
	}

	public int[][] getMaxY()
	{
		return maxY;
	}

	public void setLastSection( String lastSection )
	{
		String[] sectionsSorted = new String[MAX_CONFIG_SEGMENTS];
		String[] sections = new String[MAX_CONFIG_SEGMENTS];
		sections = parseBufferToStrings( getFileBytes() );
		sectionsSorted = sortSections( sections, lastSection );
		writeFileStrings( sectionsSorted );
	}

	public void calcMinMaxSectorsVal( String[] sections )
	{
		minH = 9999;
		maxH = 0;
		for( int k = 0; k < 256; k++ )
		{
			minX[k] = 9999;
			maxX[k] = 0;
			for( int j = 0; j < 256; j++ )
			{
				minY[k][j] = 9999;
				maxY[k][j] = 0;
			}
		}
		int i = 0;
		char[] buffer = new char[256];
		int Hstart = 0, Hend = 0, Xstart = 0, Xend = 0, Ystart = 0, Yend = 0;
		String Hval = "", Xval = "", Yval = "";
		while( i < sections.length )
		{
			buffer = sections[i].toCharArray();
			int j = 0;
			while( j < sections[i].length() )
			{
				if( buffer[j] == 'h' )
					Hstart = j + 1;
				else if( buffer[j] == 'x' )
				{
					Hend = j;
					Xstart = j + 1;
				}
				else if( buffer[j] == 'y' )
				{
					Xend = j;
					Ystart = j + 1;
				}
				j++;
			}
			Yend = j;
			Hval = sections[i].substring( Hstart, Hend );
			Xval = sections[i].substring( Xstart, Xend );
			Yval = sections[i].substring( Ystart, Yend );

			if( Integer.valueOf( Hval ) < minH )
				minH = Integer.valueOf( Hval );
			else if( Integer.valueOf( Hval ) > maxH )
				maxH = Integer.valueOf( Hval );

			int curH = Integer.valueOf( Hval );
			int curX = Integer.valueOf( Xval );
			int curY = Integer.valueOf( Yval );

			if( minX[curH] > curX )
				minX[curH] = curX;
			if( maxX[curH] < curX )
				maxX[curH] = curX;

			if( minY[curH][curX] > curY )
				minY[curH][curX] = curY;
			if( maxY[curH][curX] < curY )
				maxY[curH][curX] = curY;

			i++;
		}
	}

	private String[] parseBufferToStrings( byte[] inputBuf )
	{
		String[] sections = new String[MAX_CONFIG_SEGMENTS];
		int nstring = 0;
		byte[] stringBuf = new byte[256];
		// stringBuf=null;

		int i = 0, j = 0;
		while( inputBuf[i] != '\0' )
		{
			if( inputBuf[i] == '\r' )
			{
				String str = new String( stringBuf );
				sections[nstring] = str.substring( 0, j );
				nstring++;
				j = 0;
				i += 2;
				int k = 0;
				while( k < 256 )
				{
					stringBuf[k] = '\0';
					k++;
				}
			}
			stringBuf[j] = inputBuf[i];
			j++;
			i++;
		}
		inputBuf[0] = '1';

		return sections;
	}

	private String[] sortSections( String[] sections, String lastSection )
	{
		String[] sectionsSorted = new String[MAX_CONFIG_SEGMENTS];
		int sectN = 0;
		int findIndex = -1;

		while( sectN < sections.length )
		{
			if(sections[sectN] != null){
				if( sections[sectN].compareTo( lastSection ) == 0 )
				{
					findIndex = sectN;
					break;
				}
				sectN++;
			}
		}

		sectionsSorted[0] = lastSection;
		int i = 1, j = 1;
		while( i < sections.length )
		{
			if(sections[i] != null){
				if( findIndex != -1 )
				{
					if( findIndex + 1 == i )
					{
						i++;
						continue;
					}
					else
					{
						sectionsSorted[j] = sections[i - 1];
						j++;
					}
				}
				else
				{
					sectionsSorted[i] = sections[i - 1];
					j++;
				}
				i++;
			}
			else break;
		}
		if( sections[i - 1] != null
				&& sections[i - 1].compareTo( lastSection ) != 0
				&& i <= MAX_CONFIG_SEGMENTS ){
			if(j<20)
				sectionsSorted[j] = sections[i - 1];
		}
		return sectionsSorted;
	}

	private byte[] getFileBytes()
	{
		FileInputStream inStream;
		byte[] buffer = new byte[4096];
		try
		{
			inStream = new FileInputStream( filename );
			inStream.read( buffer );
			inStream.close();
		}
		catch( Exception ex )
		{
			System.out.println( ex.getMessage() );
		}

		return buffer;
	}

	private void writeFileStrings( String[] sections )
	{
		FileOutputStream outStream;

		try
		{
			outStream = new FileOutputStream( filename );
			int i = 0;
			while( sections[i] != null )
			{
				String str = sections[i];
				int j = 0;
				while( j < str.length() )
				{
					outStream.write( str.charAt( j ) );
					j++;
				}
				outStream.write( '\r' );
				outStream.write( '\n' );
				i++;
			}
			outStream.close();
		}
		catch( Exception ex )
		{
			System.out.println( ex.getMessage() );
		}
	}

	private final int MAX_CONFIG_SEGMENTS = 20;
	private String filename;

	private int minH;
	private int maxH;
	private int minX[] = new int[256];
	private int maxX[] = new int[256];
	private int minY[][] = new int[256][256];
	private int maxY[][] = new int[256][256];
}
