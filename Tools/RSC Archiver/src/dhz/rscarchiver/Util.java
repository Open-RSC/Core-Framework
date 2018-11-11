package dhz.rscarchiver;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Util
{
	public static byte[] getFileBytes(File file) throws IOException
	{
		return getFileBytes(file.getAbsolutePath());
	}
	
	public static byte[] getFileBytes(String filename) throws IOException
	{
		RandomAccessFile raf = new RandomAccessFile(filename, "r");
		byte[] buffer = new byte[(int) raf.length()];
		raf.read(buffer);
		raf.close();
		return buffer;
	}

	static boolean isInt(String s)
	{
		try
		{
			Integer.parseInt(s);
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}

	static byte[] fileToBytes(File file) throws IOException
	{
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		byte[] data = new byte[(int) raf.length()];
		raf.readFully(data);
		raf.close();
		return data;
	}
}
