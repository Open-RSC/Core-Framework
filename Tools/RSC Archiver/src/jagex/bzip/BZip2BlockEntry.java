package jagex.bzip;

public class BZip2BlockEntry
{

    byte streamIn[];
    int streamNextIn;
    int streamAvailableIn;
    int streamTotalInLo32;
    int streamTotalInHi32;
    byte streamOut[];
    int streamNextOut;
    int streamAvailableOut;
    int streamTotalOutLo32;
    int streamTotalOutHi32;
    byte streamOutCh;
    int stateOutCh;
    boolean blockRandomised;
    int bsBuff;
    int bsLive;
    int blockSize100k;
    int currentBlockNumber;
    int origPtr;
    int tPos;
    int k0;
    final int unzftab[] = new int[256];
    int nblockUsed;
    final int cftab[] = new int[257];
    public static int tt[];
    int inUseOffset;
    final boolean inUse[] = new boolean[256];
    final boolean inUse16[] = new boolean[16];
    final byte seqToUnseq[] = new byte[256];
    final byte mtfa[] = new byte[4096];
    final int mtfBase[] = new int[16];
    final byte selector[] = new byte[18002];
    final byte selectorMtf[] = new byte[18002];
    final byte len[][] = new byte[6][258];
    final int limit[][] = new int[6][258];
    final int base[][] = new int[6][258];
    final int perm[][] = new int[6][258];
    final int minLens[] = new int[6];
    int saveNblock;

    BZip2BlockEntry()
    {
    }
}
