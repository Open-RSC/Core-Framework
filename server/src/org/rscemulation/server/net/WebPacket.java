package org.rscemulation.server.net;
 
 
public final class WebPacket extends Packet
{
	private int pID;
 
	public WebPacket(int pID, byte[] pData)
	{
		super(pData, false);
		this.pID = pID;
	}
 
	public int getID()
	{
		return this.pID;
	}
 
	public String toString() {
		return super.toString() + " pid = " + this.pID;
	}
 
	public String readPaddedString(int padding) {
		if (this.caret + padding > this.pData.length)
			return "BUFFER OVERFLOW";
		String string = "";
		for (int idx = 0; idx < padding; ++idx) {
			int data = this.pData[(this.caret++)];
			if (data != 0) {
				string = string + (char)data;
			}
		}
		return string;
	}
 
	public short readShort()
	{
   		return (short)((this.pData[(this.caret++)] & 0xFF) << 8 | this.pData[(this.caret++)] & 0xFF);
	}
 
	public int readInt()
	{
   		return (this.pData[(this.caret++)] & 0xFF) << 24 | (this.pData[(this.caret++)] & 0xFF) << 16 | (this.pData[(this.caret++)] & 0xFF) << 8 | this.pData[(this.caret++)] & 0xFF;
	}
}