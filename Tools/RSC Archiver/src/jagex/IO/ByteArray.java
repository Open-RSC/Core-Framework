package jagex.IO;

public class ByteArray
{

    private byte bytes[];
    public int length;

    ByteArray(byte bytes[])
    {
        length = 0;
        this.bytes = bytes;
        length = bytes.length;
    }

    public byte[] getBytes()
    {
        return bytes;
    }

    public void setBytes(byte bytes[])
    {
        this.bytes = bytes;
        length = bytes.length;
    }
}

