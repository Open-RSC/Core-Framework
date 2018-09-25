package rsc.util.bzip;
final class BZState {
	int availOut = 0;
	int[][] base = new int[6][258];
	int bsBuff;
	int bsLive;
	int[] cftab = new int[257];
	int decompressedSize;
	byte[] input;
	boolean[] inUse = new boolean[256];
	boolean[] inUse_16 = new boolean[16];
	int k0;
	byte[][] len = new byte[6][258];
	int[][] limit = new int[6][258];
	int m_c;
	int m_f;
	int m_G;
	int[] minLens = new int[6];
	byte[] mtfa = new byte[4096];
	int[] mtfbase = new int[16];
	int nblockUsed;
	int nextIn = 0;
	int nInUse;
	int origPtr;
	byte[] output;
	int[][] perm = new int[6][258];
	int saveNblock;
	byte[] sectorMtf = new byte[18002];
	byte[] selector = new byte[18002];
	byte[] setToUnseq = new byte[256];
	byte stateOutCh;
	int stateOutLen;

	int tpos;

	int[] unzftab = new int[256];
}
