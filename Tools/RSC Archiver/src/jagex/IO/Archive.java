package jagex.IO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import jagex.bzip.BZip2Decompressor;

public class Archive
{

    ArrayList<ByteArray> files;
    byte finalBuffer[];
    int totalFiles;
    ArrayList<Integer> identifiers;
    ArrayList<Integer> decompressedSizes;
    ArrayList<Integer> compressedSizes;
    ArrayList<Integer> startOffsets;
    boolean compressedAsWhole;

    public Archive()
    {
    	files = new ArrayList<ByteArray>();
        identifiers = new ArrayList<Integer>();
        decompressedSizes = new ArrayList<Integer>();
        compressedSizes = new ArrayList<Integer>();
        startOffsets = new ArrayList<Integer>();
        byte[] seeder = new byte[8];
        Stream stream = new Stream(seeder);
        stream.write3Bytes(8);
        stream.write3Bytes(8);
        stream.writeShort(0);
        finalBuffer = seeder;
        totalFiles = 0;
        compressedAsWhole = false;
    }
    
    public Archive(byte abyte0[])
    {
        files = new ArrayList<ByteArray>();
        identifiers = new ArrayList<Integer>();
        decompressedSizes = new ArrayList<Integer>();
        compressedSizes = new ArrayList<Integer>();
        startOffsets = new ArrayList<Integer>();
        Stream stream = new Stream(abyte0);
        int decompressedSize = stream.readU3Bytes();
        int compressedSize = stream.readU3Bytes();
        if(compressedSize != decompressedSize)
        {
            byte abyte1[] = new byte[decompressedSize];
            BZip2Decompressor.decompressBuffer(abyte1, decompressedSize, abyte0, compressedSize, 6);
            finalBuffer = abyte1;
            stream = new Stream(finalBuffer);
            compressedAsWhole = true;
        } else
        {
            finalBuffer = abyte0;
            compressedAsWhole = false;
        }
        totalFiles = stream.readUShort();
        int offset = stream.caret + totalFiles * 10;
        for(int l = 0; l < totalFiles; l++)
        {
            identifiers.add(Integer.valueOf(stream.readUInt()));
            decompressedSizes.add(Integer.valueOf(stream.readU3Bytes()));
            compressedSizes.add(Integer.valueOf(stream.readU3Bytes()));
            startOffsets.add(Integer.valueOf(offset));
            offset += ((Integer)compressedSizes.get(l)).intValue();
            System.out.println((new StringBuilder()).append("Read ").append(identifiers.get(l)).append(" dc: ").append(decompressedSizes.get(l)).append(" cs: ").append(compressedSizes.get(l)).toString());
            files.add(new ByteArray(getFileAt(l)));
        }

    }
    

	public Vector<String> getNames() {
		Vector<String> names = new Vector<String>();
		for (int l = 0; l < totalFiles; l++) {
			names.add(identifiers.get(l).toString());
		}
		return names;
	}
	
	public String getName(int indx)
	{
		return identifiers.get(indx).toString();
	}

	public byte[] recompile() throws IOException {
        byte[] compressedWhole = compileUncompressed();
        int compressedWholeDecompressedSize = compressedWhole.length;
        compressedWhole = DataUtils.compress(compressedWhole);
        int compressedWholeSize = compressedWhole.length;
        byte[] compressedIndividually = compileCompressed();
        int compressedIndividuallySize = compressedIndividually.length;
        boolean compressedAsWhole = false;
        if (compressedWholeSize < compressedIndividuallySize) {
            compressedAsWhole = true;
        }
        ExtendedByteArrayOutputStream finalBuf = new ExtendedByteArrayOutputStream();
        if (compressedAsWhole) {
            finalBuf.write24Bytes(compressedWholeDecompressedSize);
            finalBuf.write24Bytes(compressedWholeSize);
            finalBuf.write(compressedWhole);
        }
        else
        {
            finalBuf.write24Bytes(compressedIndividuallySize);
            finalBuf.write24Bytes(compressedIndividuallySize);
            finalBuf.write(compressedIndividually);
        }
        finalBuf.close();
        return finalBuf.toByteArray();
    }

    private byte[] compileUncompressed() throws IOException {
        byte[] fileInfoSection;
        byte[] filesSection;

        ExtendedByteArrayOutputStream fileBuf = new ExtendedByteArrayOutputStream();
        for (int i = 0; i < totalFiles; i++) {
            decompressedSizes.set(i, files.get(i).length);
            compressedSizes.set(i, files.get(i).length);
            fileBuf.write(files.get(i).getBytes());
        }
        filesSection = fileBuf.toByteArray();
        fileBuf.close();
        ExtendedByteArrayOutputStream fileInfo = new ExtendedByteArrayOutputStream();
        fileInfo.writeShort(totalFiles);
        for(int i = 0; i < totalFiles; i++) {
            fileInfo.writeInt(identifiers.get(i));
            fileInfo.write24Bytes(decompressedSizes.get(i));
            fileInfo.write24Bytes(compressedSizes.get(i));
        }
        fileInfoSection = fileInfo.toByteArray();
        fileInfo.close();
        ExtendedByteArrayOutputStream finalBuffer = new ExtendedByteArrayOutputStream();
        finalBuffer.write(fileInfoSection);
        finalBuffer.write(filesSection);
        finalBuffer.close();
        return finalBuffer.toByteArray();
    }

    private byte[] compileCompressed() throws IOException {
        byte[] fileInfoSection;
        byte[] filesSection;

        ExtendedByteArrayOutputStream fileBuf = new ExtendedByteArrayOutputStream();
        for (int i = 0; i < totalFiles; i++) {
            decompressedSizes.set(i, files.get(i).length);
            byte[] compressed = DataUtils.bz2Compress(files.get(i).getBytes());
            compressedSizes.set(i, compressed.length);
            fileBuf.write(compressed);
        }
        filesSection = fileBuf.toByteArray();
        fileBuf.close();
        ExtendedByteArrayOutputStream fileInfo = new ExtendedByteArrayOutputStream();
        fileInfo.writeShort(totalFiles);
        for(int i = 0; i < totalFiles; i++) {
            fileInfo.writeInt(identifiers.get(i));
            fileInfo.write24Bytes(decompressedSizes.get(i));
            fileInfo.write24Bytes(compressedSizes.get(i));
        }
        fileInfoSection = fileInfo.toByteArray();
        fileInfo.close();
        ExtendedByteArrayOutputStream finalBuffer = new ExtendedByteArrayOutputStream();
        finalBuffer.write(fileInfoSection);
        finalBuffer.write(filesSection);
        finalBuffer.close();
        return finalBuffer.toByteArray();
    }

    public byte[] getFileAt(int at)
    {
    	int savedSize = decompressedSizes.get(at);
    	if(savedSize == 0)
    		return files.get(at).getBytes();
        byte dataBuffer[] = new byte[savedSize];
        if(!compressedAsWhole)
        {
            BZip2Decompressor.decompressBuffer(dataBuffer, ((Integer)decompressedSizes.get(at)).intValue(), finalBuffer, ((Integer)compressedSizes.get(at)).intValue(), ((Integer)startOffsets.get(at)).intValue());
        } else
        {
            System.arraycopy(finalBuffer, ((Integer)startOffsets.get(at)).intValue(), dataBuffer, 0, ((Integer)decompressedSizes.get(at)).intValue());
        }
        return dataBuffer;
    }
    
    public byte[] getFile(int identifier)
    {
        for(int k = 0; k < totalFiles; k++)
        {
            if(((Integer)identifiers.get(k)).intValue() == identifier)
            {
                return getFileAt(k);
            }
        }

        return null;
    }

    public byte[] getFile(String identStr)
    {
        int identifier = 0;
        identStr = identStr.toUpperCase();
        for(int j = 0; j < identStr.length(); j++)
        {
            identifier = (identifier * 61 + identStr.charAt(j)) - 32;
        }

        return getFile(identifier);
    }

    public static int getHash(String s)
    {
        int identifier = 0;
        s = s.toUpperCase();
        for(int j = 0; j < s.length(); j++)
        {
            identifier = (identifier * 61 + s.charAt(j)) - 32;
        }

        return identifier;
    }
    
    public String getNameFromIdentifier(int hashedName)
    {
    	System.out.println("Starting unhash?");
    	return getPrevCharFromHash(hashedName);
    }
    
    private String getPrevCharFromHash(int id)
    {
    	int identifier = id + 32;
    	for(int i = 48; i < 91; i++)
    	{
    		double check = (identifier - i)/61d;
    		System.out.println(check);
    		if((check == Math.floor(check)) && !Double.isInfinite(check))
    		{
    			String cha = new String(Character.toChars(i));
    			System.out.println(cha);
    			identifier = identifier - i;
    			identifier = identifier / 61;
    			if(identifier == 0)
    			{
    				return cha;
    			}
    			String prevString = getPrevCharFromHash(identifier);
    			if(getPrevCharFromHash(identifier) != null)
    			{
    				return prevString + cha;
    			}
    			else
    				identifier = id + 32;
    		}
    	}
    	return null;
    }

    public ByteArray getByteArray(String s)
    {
        return getByteArray(getHash(s));
    }

    public ByteArray getByteArray(int identifier)
    {
        int pos = identifiers.indexOf(Integer.valueOf(identifier));
        return (ByteArray)files.get(pos);
    }

    public int getIndexForName(String s)
    {
        return identifiers.indexOf(Integer.valueOf(getHash(s)));
    }
    
    public int getIndexForHash(int i)
    {
    	return identifiers.indexOf(i);
    }

    public void updateFile(String s, byte data[])
    {
        updateFile(getIndexForName(s), data);
    }

    public void removeFile(String s)
    {
        removeFile(getIndexForName(s));
    }

    public void renameFile(int index, int newName)
    {
        identifiers.set(index, Integer.valueOf(newName));
    }

    public void updateFile(int index, byte data[])
    {
        ((ByteArray)files.get(index)).setBytes(data);
    }

    public void removeFile(int index)
    {
        files.remove(index);
        identifiers.remove(index);
        compressedSizes.remove(index);
        decompressedSizes.remove(index);
        totalFiles--;
    }

    public void addFile(String s, byte data[])
    {
        addFile(getHash(s), data);
    }

    public void addFile(int identifier, byte data[])
    {
        identifiers.add(Integer.valueOf(identifier));
        decompressedSizes.add(Integer.valueOf(0));
        compressedSizes.add(Integer.valueOf(0));
        files.add(new ByteArray(data));
        totalFiles++;
    }
    
    public int[] getFileIdentifiers()
    {
    	int[] names = new int[identifiers.size()];
    	for(int i = 0; i < identifiers.size(); i++)
    	{
    		names[i] = identifiers.get(i);
    	}
    	return names;
    }
    
    public int getNumFiles()
    {
    	return totalFiles;
    }
}
