package org.rscemulation.server.net;


public final class RSCPacket extends Packet
{
	private int pID;

	public RSCPacket(int pID, byte[] pData, boolean bare)
	{
		super(pData, bare);
		this.pID = pID;
	}

	public RSCPacket(int pID, byte[] pData) {
		this(pID, pData, false);
	}

	public int getID() {
		return this.pID;
	}
}