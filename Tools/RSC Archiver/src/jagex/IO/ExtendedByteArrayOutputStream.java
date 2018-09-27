package jagex.IO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExtendedByteArrayOutputStream extends ByteArrayOutputStream
{

    public ExtendedByteArrayOutputStream()
    {
    }

    public void writeShort(int s)
    {
        write(s >> 8);
        write(s);
    }

    public void write24Bytes(int s)
    {
        write(s >> 16);
        write(s >> 8);
        write(s);
    }

    public void writeInt(int s)
    {
        write(s >> 24);
        write(s >> 16);
        write(s >> 8);
        write(s);
    }

    public void writeLong(long l)
    {
        write((int)(l >> 56));
        write((int)(l >> 48));
        write((int)(l >> 40));
        write((int)(l >> 32));
        write((int)(l >> 24));
        write((int)(l >> 16));
        write((int)(l >> 8));
        write((int)l);
    }

    public void writeString(String s)
        throws IOException
    {
        write(s.getBytes());
        write(10);
    }
    
	public void writeSmart(int i) {
		if(i < 64 && i >= -64) {
			write(i + 64);
		} else {
			writeShort(i + 49152);
		}
	}
}
