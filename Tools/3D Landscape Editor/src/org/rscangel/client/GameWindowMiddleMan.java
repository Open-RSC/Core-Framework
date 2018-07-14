package org.rscangel.client;

public abstract class GameWindowMiddleMan extends GameWindow
{
	protected abstract void resetVars();

	protected abstract void resetIntVars();

	public GameWindowMiddleMan()
	{
		username = "";
		password = "";
		packetData = new byte[5000];
		friendsListLongs = new long[400];
		friendsListOnlineStatus = new int[400];
		ignoreListLongs = new long[200];
	}

	public static int clientVersion = 1;
	public static int maxPacketReadCount;
	String username;
	String password;
	protected byte[] packetData;
	long lastPing;
	public int friendsCount;
	public long[] friendsListLongs;
	public int[] friendsListOnlineStatus;
	public int ignoreListCount;
	public long[] ignoreListLongs;
	public int blockChatMessages;
	public int blockPrivateMessages;
	public int blockTradeRequests;
	public int blockDuelRequests;
	public int socketTimeout;

}
