package jagex.bzip;

import java.io.*;

public class CBZip2OutputStream extends OutputStream
    implements BZip2Constants
{
    private static class StackElem
    {

        int ll;
        int hh;
        int dd;

        private StackElem()
        {
        }

    }


    protected static final int SETMASK = 0x200000;
    protected static final int CLEARMASK = 0xffdfffff;
    protected static final int GREATER_ICOST = 15;
    protected static final int LESSER_ICOST = 0;
    protected static final int SMALL_THRESH = 20;
    protected static final int DEPTH_THRESH = 10;
    protected static final int QSORT_STACK_SIZE = 1000;
    int last;
    int origPtr;
    int blockSize100k;
    boolean blockRandomised;
    int bytesOut;
    int bsBuff;
    int bsLive;
    CRC mCrc;
    private boolean inUse[];
    private int nInUse;
    private char seqToUnseq[];
    private char unseqToSeq[];
    private char selector[];
    private char selectorMtf[];
    private char block[];
    private int quadrant[];
    private int zptr[];
    private short szptr[];
    private int ftab[];
    private int nMTF;
    private int mtfFreq[];
    private int workFactor;
    private int workDone;
    private int workLimit;
    private boolean firstAttempt;
    private int nBlocksRandomised;
    private int currentChar;
    private int runLength;
    boolean closed;
    private int blockCRC;
    private int combinedCRC;
    private int allowableBlockSize;
    private OutputStream bsStream;
    private int incs[] = {
        1, 4, 13, 40, 121, 364, 1093, 3280, 9841, 29524, 
        0x159fd, 0x40df8, 0xc29e9, 0x247dbc
    };

    private static void panic()
    {
        System.out.println("panic");
    }

    private void makeMaps()
    {
        nInUse = 0;
        for(int i = 0; i < 256; i++)
        {
            if(inUse[i])
            {
                seqToUnseq[nInUse] = (char)i;
                unseqToSeq[i] = (char)nInUse;
                nInUse++;
            }
        }

    }

    protected static void hbMakeCodeLengths(char len[], int freq[], int alphaSize, int maxLen)
    {
        int heap[] = new int[260];
        int weight[] = new int[516];
        int parent[] = new int[516];
        for(int i = 0; i < alphaSize; i++)
        {
            weight[i + 1] = (freq[i] != 0 ? freq[i] : 1) << 8;
        }

        do
        {
            int nNodes = alphaSize;
            int nHeap = 0;
            heap[0] = 0;
            weight[0] = 0;
            parent[0] = -2;
            for(int i = 1; i <= alphaSize; i++)
            {
                parent[i] = -1;
                nHeap++;
                heap[nHeap] = i;
                int zz = nHeap;
                int tmp;
                for(tmp = heap[zz]; weight[tmp] < weight[heap[zz >> 1]]; zz >>= 1)
                {
                    heap[zz] = heap[zz >> 1];
                }

                heap[zz] = tmp;
            }

            if(nHeap >= 260)
            {
                panic();
            }
            while(nHeap > 1) 
            {
                int n1 = heap[1];
                heap[1] = heap[nHeap];
                nHeap--;
                int zz = 0;
                int yy = 0;
                int tmp = 0;
                zz = 1;
                tmp = heap[zz];
                do
                {
                    yy = zz << 1;
                    if(yy > nHeap)
                    {
                        break;
                    }
                    if(yy < nHeap && weight[heap[yy + 1]] < weight[heap[yy]])
                    {
                        yy++;
                    }
                    if(weight[tmp] < weight[heap[yy]])
                    {
                        break;
                    }
                    heap[zz] = heap[yy];
                    zz = yy;
                } while(true);
                heap[zz] = tmp;
                int n2 = heap[1];
                heap[1] = heap[nHeap];
                nHeap--;
                zz = 0;
                yy = 0;
                tmp = 0;
                zz = 1;
                tmp = heap[zz];
                do
                {
                    yy = zz << 1;
                    if(yy > nHeap)
                    {
                        break;
                    }
                    if(yy < nHeap && weight[heap[yy + 1]] < weight[heap[yy]])
                    {
                        yy++;
                    }
                    if(weight[tmp] < weight[heap[yy]])
                    {
                        break;
                    }
                    heap[zz] = heap[yy];
                    zz = yy;
                } while(true);
                heap[zz] = tmp;
                nNodes++;
                parent[n1] = parent[n2] = nNodes;
                weight[nNodes] = (weight[n1] & 0xffffff00) + (weight[n2] & 0xffffff00) | 1 + ((weight[n1] & 0xff) <= (weight[n2] & 0xff) ? weight[n2] & 0xff : weight[n1] & 0xff);
                parent[nNodes] = -1;
                nHeap++;
                heap[nHeap] = nNodes;
                zz = 0;
                int tmp1 = 0;
                zz = nHeap;
                for(tmp1 = heap[zz]; weight[tmp1] < weight[heap[zz >> 1]]; zz >>= 1)
                {
                    heap[zz] = heap[zz >> 1];
                }

                heap[zz] = tmp1;
            }
            if(nNodes >= 516)
            {
                panic();
            }
            boolean tooLong = false;
            for(int i = 1; i <= alphaSize; i++)
            {
                int j = 0;
                for(int k = i; parent[k] >= 0;)
                {
                    k = parent[k];
                    j++;
                }

                len[i - 1] = (char)j;
                if(j > maxLen)
                {
                    tooLong = true;
                }
            }

            if(tooLong)
            {
                int i = 1;
                while(i < alphaSize) 
                {
                    int j = weight[i] >> 8;
                    j = 1 + j / 2;
                    weight[i] = j << 8;
                    i++;
                }
            } else
            {
                return;
            }
        } while(true);
    }

    public CBZip2OutputStream(OutputStream inStream)
        throws IOException
    {
        this(inStream, 9);
    }

    public CBZip2OutputStream(OutputStream inStream, int inBlockSize)
        throws IOException
    {
        mCrc = new CRC();
        inUse = new boolean[256];
        seqToUnseq = new char[256];
        unseqToSeq = new char[256];
        selector = new char[18002];
        selectorMtf = new char[18002];
        mtfFreq = new int[258];
        currentChar = -1;
        runLength = 0;
        closed = false;
        block = null;
        quadrant = null;
        zptr = null;
        ftab = null;
        bsSetStream(inStream);
        workFactor = 50;
        if(inBlockSize > 9)
        {
            inBlockSize = 9;
        }
        if(inBlockSize < 1)
        {
            inBlockSize = 1;
        }
        blockSize100k = inBlockSize;
        allocateCompressStructures();
        initialize();
        initBlock();
    }

    public void write(int bv)
        throws IOException
    {
        int b = (256 + bv) % 256;
        if(currentChar != -1)
        {
            if(currentChar == b)
            {
                runLength++;
                if(runLength > 254)
                {
                    writeRun();
                    currentChar = -1;
                    runLength = 0;
                }
            } else
            {
                writeRun();
                runLength = 1;
                currentChar = b;
            }
        } else
        {
            currentChar = b;
            runLength++;
        }
    }

    private void writeRun()
        throws IOException
    {
        if(last < allowableBlockSize)
        {
            inUse[currentChar] = true;
            for(int i = 0; i < runLength; i++)
            {
                mCrc.updateCRC((char)currentChar);
            }

            switch(runLength)
            {
            case 1: // '\001'
                last++;
                block[last + 1] = (char)currentChar;
                break;

            case 2: // '\002'
                last++;
                block[last + 1] = (char)currentChar;
                last++;
                block[last + 1] = (char)currentChar;
                break;

            case 3: // '\003'
                last++;
                block[last + 1] = (char)currentChar;
                last++;
                block[last + 1] = (char)currentChar;
                last++;
                block[last + 1] = (char)currentChar;
                break;

            default:
                inUse[runLength - 4] = true;
                last++;
                block[last + 1] = (char)currentChar;
                last++;
                block[last + 1] = (char)currentChar;
                last++;
                block[last + 1] = (char)currentChar;
                last++;
                block[last + 1] = (char)currentChar;
                last++;
                block[last + 1] = (char)(runLength - 4);
                break;
            }
        } else
        {
            endBlock();
            initBlock();
            writeRun();
        }
    }

    protected void finalize()
        throws Throwable
    {
        close();
        super.finalize();
    }

    public void close()
        throws IOException
    {
        if(closed)
        {
            return;
        }
        if(runLength > 0)
        {
            writeRun();
        }
        currentChar = -1;
        endBlock();
        endCompression();
        closed = true;
        super.close();
        bsStream.close();
    }

    public void flush()
        throws IOException
    {
        super.flush();
        bsStream.flush();
    }

    private void initialize()
        throws IOException
    {
        bytesOut = 0;
        nBlocksRandomised = 0;
        bsPutUChar(104);
        bsPutUChar(48 + blockSize100k);
        combinedCRC = 0;
    }

    private void initBlock()
    {
        mCrc.initialiseCRC();
        last = -1;
        for(int i = 0; i < 256; i++)
        {
            inUse[i] = false;
        }

        allowableBlockSize = 0x186a0 * blockSize100k - 20;
    }

    private void endBlock()
        throws IOException
    {
        blockCRC = mCrc.getFinalCRC();
        combinedCRC = combinedCRC << 1 | combinedCRC >>> 31;
        combinedCRC ^= blockCRC;
        doReversibleTransformation();
        bsPutUChar(49);
        bsPutUChar(65);
        bsPutUChar(89);
        bsPutUChar(38);
        bsPutUChar(83);
        bsPutUChar(89);
        bsPutint(blockCRC);
        if(blockRandomised)
        {
            bsW(1, 1);
            nBlocksRandomised++;
        } else
        {
            bsW(1, 0);
        }
        moveToFrontCodeAndSend();
    }

    private void endCompression()
        throws IOException
    {
        bsPutUChar(23);
        bsPutUChar(114);
        bsPutUChar(69);
        bsPutUChar(56);
        bsPutUChar(80);
        bsPutUChar(144);
        bsPutint(combinedCRC);
        bsFinishedWithStream();
    }

    private void hbAssignCodes(int code[], char length[], int minLen, int maxLen, int alphaSize)
    {
        int vec = 0;
        for(int n = minLen; n <= maxLen; n++)
        {
            for(int i = 0; i < alphaSize; i++)
            {
                if(length[i] == n)
                {
                    code[i] = vec;
                    vec++;
                }
            }

            vec <<= 1;
        }

    }

    private void bsSetStream(OutputStream f)
    {
        bsStream = f;
        bsLive = 0;
        bsBuff = 0;
        bytesOut = 0;
    }

    private void bsFinishedWithStream()
        throws IOException
    {
        while(bsLive > 0) 
        {
            int ch = bsBuff >> 24;
            try
            {
                bsStream.write(ch);
            }
            catch(IOException e)
            {
                throw e;
            }
            bsBuff <<= 8;
            bsLive -= 8;
            bytesOut++;
        }
    }

    private void bsW(int n, int v)
        throws IOException
    {
        while(bsLive >= 8) 
        {
            int ch = bsBuff >> 24;
            try
            {
                bsStream.write(ch);
            }
            catch(IOException e)
            {
                throw e;
            }
            bsBuff <<= 8;
            bsLive -= 8;
            bytesOut++;
        }
        bsBuff |= v << 32 - bsLive - n;
        bsLive += n;
    }

    private void bsPutUChar(int c)
        throws IOException
    {
        bsW(8, c);
    }

    private void bsPutint(int u)
        throws IOException
    {
        bsW(8, u >> 24 & 0xff);
        bsW(8, u >> 16 & 0xff);
        bsW(8, u >> 8 & 0xff);
        bsW(8, u & 0xff);
    }

    private void bsPutIntVS(int numBits, int c)
        throws IOException
    {
        bsW(numBits, c);
    }

    private void sendMTFValues()
        throws IOException
    {
        char len[][] = new char[6][258];
        int nSelectors = 0;
        int alphaSize = nInUse + 2;
        for(int t = 0; t < 6; t++)
        {
            for(int v = 0; v < alphaSize; v++)
            {
                len[t][v] = '\017';
            }

        }

        if(nMTF <= 0)
        {
            panic();
        }
        int nGroups;
        if(nMTF < 200)
        {
            nGroups = 2;
        } else
        if(nMTF < 600)
        {
            nGroups = 3;
        } else
        if(nMTF < 1200)
        {
            nGroups = 4;
        } else
        if(nMTF < 2400)
        {
            nGroups = 5;
        } else
        {
            nGroups = 6;
        }
        int nPart = nGroups;
        int remF = nMTF;
        int gs = 0;
        while(nPart > 0) 
        {
            int tFreq = remF / nPart;
            int ge = gs - 1;
            int aFreq;
            for(aFreq = 0; aFreq < tFreq && ge < alphaSize - 1; aFreq += mtfFreq[ge])
            {
                ge++;
            }

            if(ge > gs && nPart != nGroups && nPart != 1 && (nGroups - nPart) % 2 == 1)
            {
                aFreq -= mtfFreq[ge];
                ge--;
            }
            for(int v = 0; v < alphaSize; v++)
            {
                if(v >= gs && v <= ge)
                {
                    len[nPart - 1][v] = '\0';
                } else
                {
                    len[nPart - 1][v] = '\017';
                }
            }

            nPart--;
            gs = ge + 1;
            remF -= aFreq;
        }
        int rfreq[][] = new int[6][258];
        int fave[] = new int[6];
        short cost[] = new short[6];
        for(int iter = 0; iter < 4; iter++)
        {
            for(int t = 0; t < nGroups; t++)
            {
                fave[t] = 0;
            }

            for(int t = 0; t < nGroups; t++)
            {
                for(int v = 0; v < alphaSize; v++)
                {
                    rfreq[t][v] = 0;
                }

            }

            nSelectors = 0;
            int totc = 0;
            int ge;
            for(gs = 0; gs < nMTF; gs = ge + 1)
            {
                ge = (gs + 50) - 1;
                if(ge >= nMTF)
                {
                    ge = nMTF - 1;
                }
                for(int t = 0; t < nGroups; t++)
                {
                    cost[t] = 0;
                }

                if(nGroups == 6)
                {
                    short cost1;
                    short cost2;
                    short cost3;
                    short cost4;
                    short cost5;
                    short cost0 = cost1 = cost2 = cost3 = cost4 = cost5 = 0;
                    for(int i = gs; i <= ge; i++)
                    {
                        short icv = szptr[i];
                        cost0 += len[0][icv];
                        cost1 += len[1][icv];
                        cost2 += len[2][icv];
                        cost3 += len[3][icv];
                        cost4 += len[4][icv];
                        cost5 += len[5][icv];
                    }

                    cost[0] = cost0;
                    cost[1] = cost1;
                    cost[2] = cost2;
                    cost[3] = cost3;
                    cost[4] = cost4;
                    cost[5] = cost5;
                } else
                {
                    for(int i = gs; i <= ge; i++)
                    {
                        short icv = szptr[i];
                        for(int t = 0; t < nGroups; t++)
                        {
                            cost[t] += len[t][icv];
                        }

                    }

                }
                int bc = 0x3b9ac9ff;
                int bt = -1;
                for(int t = 0; t < nGroups; t++)
                {
                    if(cost[t] < bc)
                    {
                        bc = cost[t];
                        bt = t;
                    }
                }

                totc += bc;
                fave[bt]++;
                selector[nSelectors] = (char)bt;
                nSelectors++;
                for(int i = gs; i <= ge; i++)
                {
                    rfreq[bt][szptr[i]]++;
                }

            }

            for(int t = 0; t < nGroups; t++)
            {
                hbMakeCodeLengths(len[t], rfreq[t], alphaSize, 20);
            }

        }

        rfreq = (int[][])null;
        fave = null;
        cost = null;
        if(nGroups >= 8)
        {
            panic();
        }
        if(nSelectors >= 32768 || nSelectors > 18002)
        {
            panic();
        }
        char pos[] = new char[6];
        for(int i = 0; i < nGroups; i++)
        {
            pos[i] = (char)i;
        }

        for(int i = 0; i < nSelectors; i++)
        {
            char ll_i = selector[i];
            int j = 0;
            char tmp;
            for(tmp = pos[j]; ll_i != tmp;)
            {
                j++;
                char tmp2 = tmp;
                tmp = pos[j];
                pos[j] = tmp2;
            }

            pos[0] = tmp;
            selectorMtf[i] = (char)j;
        }

        int code[][] = new int[6][258];
        for(int t = 0; t < nGroups; t++)
        {
            int minLen = 32;
            int maxLen = 0;
            for(int i = 0; i < alphaSize; i++)
            {
                if(len[t][i] > maxLen)
                {
                    maxLen = len[t][i];
                }
                if(len[t][i] < minLen)
                {
                    minLen = len[t][i];
                }
            }

            if(maxLen > 20)
            {
                panic();
            }
            if(minLen < 1)
            {
                panic();
            }
            hbAssignCodes(code[t], len[t], minLen, maxLen, alphaSize);
        }

        boolean inUse16[] = new boolean[16];
        for(int i = 0; i < 16; i++)
        {
            inUse16[i] = false;
            for(int j = 0; j < 16; j++)
            {
                if(inUse[i * 16 + j])
                {
                    inUse16[i] = true;
                }
            }

        }

        int nBytes = bytesOut;
        for(int i = 0; i < 16; i++)
        {
            if(inUse16[i])
            {
                bsW(1, 1);
            } else
            {
                bsW(1, 0);
            }
        }

        for(int i = 0; i < 16; i++)
        {
            if(!inUse16[i])
            {
                continue;
            }
            for(int j = 0; j < 16; j++)
            {
                if(inUse[i * 16 + j])
                {
                    bsW(1, 1);
                } else
                {
                    bsW(1, 0);
                }
            }

        }

        nBytes = bytesOut;
        bsW(3, nGroups);
        bsW(15, nSelectors);
        for(int i = 0; i < nSelectors; i++)
        {
            for(int j = 0; j < selectorMtf[i]; j++)
            {
                bsW(1, 1);
            }

            bsW(1, 0);
        }

        nBytes = bytesOut;
        for(int t = 0; t < nGroups; t++)
        {
            int curr = len[t][0];
            bsW(5, curr);
            for(int i = 0; i < alphaSize; i++)
            {
                for(; curr < len[t][i]; curr++)
                {
                    bsW(2, 2);
                }

                for(; curr > len[t][i]; curr--)
                {
                    bsW(2, 3);
                }

                bsW(1, 0);
            }

        }

        nBytes = bytesOut;
        int selCtr = 0;
        for(gs = 0; gs < nMTF;)
        {
            int ge = (gs + 50) - 1;
            if(ge >= nMTF)
            {
                ge = nMTF - 1;
            }
            for(int i = gs; i <= ge; i++)
            {
                bsW(len[selector[selCtr]][szptr[i]], code[selector[selCtr]][szptr[i]]);
            }

            gs = ge + 1;
            selCtr++;
        }

        if(selCtr != nSelectors)
        {
            panic();
        }
    }

    private void moveToFrontCodeAndSend()
        throws IOException
    {
        bsPutIntVS(24, origPtr);
        generateMTFValues();
        sendMTFValues();
    }

    private void simpleSort(int lo, int hi, int d)
    {
        int bigN = (hi - lo) + 1;
        if(bigN < 2)
        {
            return;
        }
        int hp;
        for(hp = 0; incs[hp] < bigN; hp++) { }
label0:
        for(hp--; hp >= 0; hp--)
        {
            int h = incs[hp];
            int i = lo + h;
            do
            {
                if(i > hi)
                {
                    continue label0;
                }
                int v = zptr[i];
                int j = i;
                do
                {
                    if(!fullGtU(zptr[j - h] + d, v + d))
                    {
                        break;
                    }
                    zptr[j] = zptr[j - h];
                    j -= h;
                } while(j > (lo + h) - 1);
                zptr[j] = v;
                if(++i > hi)
                {
                    continue label0;
                }
                v = zptr[i];
                j = i;
                do
                {
                    if(!fullGtU(zptr[j - h] + d, v + d))
                    {
                        break;
                    }
                    zptr[j] = zptr[j - h];
                    j -= h;
                } while(j > (lo + h) - 1);
                zptr[j] = v;
                if(++i > hi)
                {
                    continue label0;
                }
                v = zptr[i];
                j = i;
                do
                {
                    if(!fullGtU(zptr[j - h] + d, v + d))
                    {
                        break;
                    }
                    zptr[j] = zptr[j - h];
                    j -= h;
                } while(j > (lo + h) - 1);
                zptr[j] = v;
                i++;
            } while(workDone <= workLimit || !firstAttempt);
            return;
        }

    }

    private void vswap(int p1, int p2, int n)
    {
        int temp = 0;
        for(; n > 0; n--)
        {
            temp = zptr[p1];
            zptr[p1] = zptr[p2];
            zptr[p2] = temp;
            p1++;
            p2++;
        }

    }

    private char med3(char a, char b, char c)
    {
        if(a > b)
        {
            char t = a;
            a = b;
            b = t;
        }
        if(b > c)
        {
            char t = b;
            b = c;
            c = t;
        }
        if(a > b)
        {
            b = a;
        }
        return b;
    }

    private void qSort3(int loSt, int hiSt, int dSt)
    {
        StackElem stack[] = new StackElem[1000];
        for(int count = 0; count < 1000; count++)
        {
            stack[count] = new StackElem();
        }

        int sp = 0;
        stack[sp].ll = loSt;
        stack[sp].hh = hiSt;
        stack[sp].dd = dSt;
        sp++;
        do
        {
            if(sp <= 0)
            {
                break;
            }
            if(sp >= 1000)
            {
                panic();
            }
            sp--;
            int lo = stack[sp].ll;
            int hi = stack[sp].hh;
            int d = stack[sp].dd;
            if(hi - lo < 20 || d > 10)
            {
                simpleSort(lo, hi, d);
                if(workDone > workLimit && firstAttempt)
                {
                    return;
                }
            } else
            {
                int med = med3(block[zptr[lo] + d + 1], block[zptr[hi] + d + 1], block[zptr[lo + hi >> 1] + d + 1]);
                int ltLo;
                int unLo = ltLo = lo;
                int gtHi;
                int unHi = gtHi = hi;
                do
                {
                    int temp;
                    if(unLo <= unHi)
                    {
                        int n = block[zptr[unLo] + d + 1] - med;
                        if(n == 0)
                        {
                            temp = 0;
                            temp = zptr[unLo];
                            zptr[unLo] = zptr[ltLo];
                            zptr[ltLo] = temp;
                            ltLo++;
                            unLo++;
                            continue;
                        }
                        if(n <= 0)
                        {
                            unLo++;
                            continue;
                        }
                    }
                    do
                    {
                        if(unLo > unHi)
                        {
                            break;
                        }
                        int n = block[zptr[unHi] + d + 1] - med;
                        if(n == 0)
                        {
                            temp = 0;
                            temp = zptr[unHi];
                            zptr[unHi] = zptr[gtHi];
                            zptr[gtHi] = temp;
                            gtHi--;
                            unHi--;
                            continue;
                        }
                        if(n < 0)
                        {
                            break;
                        }
                        unHi--;
                    } while(true);
                    if(unLo > unHi)
                    {
                        break;
                    }
                    temp = 0;
                    temp = zptr[unLo];
                    zptr[unLo] = zptr[unHi];
                    zptr[unHi] = temp;
                    unLo++;
                    unHi--;
                } while(true);
                if(gtHi < ltLo)
                {
                    stack[sp].ll = lo;
                    stack[sp].hh = hi;
                    stack[sp].dd = d + 1;
                    sp++;
                } else
                {
                    int n = ltLo - lo >= unLo - ltLo ? unLo - ltLo : ltLo - lo;
                    vswap(lo, unLo - n, n);
                    int m = hi - gtHi >= gtHi - unHi ? gtHi - unHi : hi - gtHi;
                    vswap(unLo, (hi - m) + 1, m);
                    n = (lo + unLo) - ltLo - 1;
                    m = (hi - (gtHi - unHi)) + 1;
                    stack[sp].ll = lo;
                    stack[sp].hh = n;
                    stack[sp].dd = d;
                    sp++;
                    stack[sp].ll = n + 1;
                    stack[sp].hh = m - 1;
                    stack[sp].dd = d + 1;
                    sp++;
                    stack[sp].ll = m;
                    stack[sp].hh = hi;
                    stack[sp].dd = d;
                    sp++;
                }
            }
        } while(true);
    }

    private void mainSort()
    {
        int runningOrder[] = new int[256];
        int copy[] = new int[256];
        boolean bigDone[] = new boolean[256];
        for(int i = 0; i < 20; i++)
        {
            block[last + i + 2] = block[i % (last + 1) + 1];
        }

        for(int i = 0; i <= last + 20; i++)
        {
            quadrant[i] = 0;
        }

        block[0] = block[last + 1];
        if(last < 4000)
        {
            for(int i = 0; i <= last; i++)
            {
                zptr[i] = i;
            }

            firstAttempt = false;
            workDone = workLimit = 0;
            simpleSort(0, last, 0);
        } else
        {
            int numQSorted = 0;
            for(int i = 0; i <= 255; i++)
            {
                bigDone[i] = false;
            }

            for(int i = 0; i <= 0x10000; i++)
            {
                ftab[i] = 0;
            }

            int c1 = block[0];
            for(int i = 0; i <= last; i++)
            {
                int c2 = block[i + 1];
                ftab[(c1 << 8) + c2]++;
                c1 = c2;
            }

            for(int i = 1; i <= 0x10000; i++)
            {
                ftab[i] += ftab[i - 1];
            }

            c1 = block[1];
            int j;
            for(int i = 0; i < last; i++)
            {
                int c2 = block[i + 2];
                j = (c1 << 8) + c2;
                c1 = c2;
                ftab[j]--;
                zptr[ftab[j]] = i;
            }

            j = (block[last + 1] << 8) + block[1];
            ftab[j]--;
            zptr[ftab[j]] = last;
            for(int i = 0; i <= 255; i++)
            {
                runningOrder[i] = i;
            }

            int h = 1;
            do
            {
                h = 3 * h + 1;
            } while(h <= 256);
            do
            {
                h /= 3;
                for(int i = h; i <= 255; i++)
                {
                    int vv = runningOrder[i];
                    j = i;
                    do
                    {
                        if(ftab[runningOrder[j - h] + 1 << 8] - ftab[runningOrder[j - h] << 8] <= ftab[vv + 1 << 8] - ftab[vv << 8])
                        {
                            break;
                        }
                        runningOrder[j] = runningOrder[j - h];
                        j -= h;
                    } while(j > h - 1);
                    runningOrder[j] = vv;
                }

            } while(h != 1);
            for(int i = 0; i <= 255; i++)
            {
                int ss = runningOrder[i];
                for(j = 0; j <= 255; j++)
                {
                    int sb = (ss << 8) + j;
                    if((ftab[sb] & 0x200000) == 0x200000)
                    {
                        continue;
                    }
                    int lo = ftab[sb] & 0xffdfffff;
                    int hi = (ftab[sb + 1] & 0xffdfffff) - 1;
                    if(hi > lo)
                    {
                        qSort3(lo, hi, 2);
                        numQSorted += (hi - lo) + 1;
                        if(workDone > workLimit && firstAttempt)
                        {
                            return;
                        }
                    }
                    ftab[sb] |= 0x200000;
                }

                bigDone[ss] = true;
                if(i < 255)
                {
                    int bbStart = ftab[ss << 8] & 0xffdfffff;
                    int bbSize = (ftab[ss + 1 << 8] & 0xffdfffff) - bbStart;
                    int shifts;
                    for(shifts = 0; bbSize >> shifts > 65534; shifts++) { }
                    for(j = 0; j < bbSize; j++)
                    {
                        int a2update = zptr[bbStart + j];
                        int qVal = j >> shifts;
                        quadrant[a2update] = qVal;
                        if(a2update < 20)
                        {
                            quadrant[a2update + last + 1] = qVal;
                        }
                    }

                    if(bbSize - 1 >> shifts > 65535)
                    {
                        panic();
                    }
                }
                for(j = 0; j <= 255; j++)
                {
                    copy[j] = ftab[(j << 8) + ss] & 0xffdfffff;
                }

                for(j = ftab[ss << 8] & 0xffdfffff; j < (ftab[ss + 1 << 8] & 0xffdfffff); j++)
                {
                    c1 = block[zptr[j]];
                    if(!bigDone[c1])
                    {
                        zptr[copy[c1]] = zptr[j] != 0 ? zptr[j] - 1 : last;
                        copy[c1]++;
                    }
                }

                for(j = 0; j <= 255; j++)
                {
                    ftab[(j << 8) + ss] |= 0x200000;
                }

            }

        }
    }

    private void randomiseBlock()
    {
        int rNToGo = 0;
        int rTPos = 0;
        for(int i = 0; i < 256; i++)
        {
            inUse[i] = false;
        }

        for(int i = 0; i <= last; i++)
        {
            if(rNToGo == 0)
            {
                rNToGo = (char)rNums[rTPos];
                if(++rTPos == 512)
                {
                    rTPos = 0;
                }
            }
            rNToGo--;
            block[i + 1] ^= rNToGo != 1 ? '\0' : '\001';
            block[i + 1] &= '\377';
            inUse[block[i + 1]] = true;
        }

    }

    private void doReversibleTransformation()
    {
        workLimit = workFactor * last;
        workDone = 0;
        blockRandomised = false;
        firstAttempt = true;
        mainSort();
        if(workDone > workLimit && firstAttempt)
        {
            randomiseBlock();
            workLimit = workDone = 0;
            blockRandomised = true;
            firstAttempt = false;
            mainSort();
        }
        origPtr = -1;
        int i = 0;
        do
        {
            if(i > last)
            {
                break;
            }
            if(zptr[i] == 0)
            {
                origPtr = i;
                break;
            }
            i++;
        } while(true);
        if(origPtr == -1)
        {
            panic();
        }
    }

    private boolean fullGtU(int i1, int i2)
    {
        char c1 = block[i1 + 1];
        char c2 = block[i2 + 1];
        if(c1 != c2)
        {
            return c1 > c2;
        }
        i1++;
        i2++;
        c1 = block[i1 + 1];
        c2 = block[i2 + 1];
        if(c1 != c2)
        {
            return c1 > c2;
        }
        i1++;
        i2++;
        c1 = block[i1 + 1];
        c2 = block[i2 + 1];
        if(c1 != c2)
        {
            return c1 > c2;
        }
        i1++;
        i2++;
        c1 = block[i1 + 1];
        c2 = block[i2 + 1];
        if(c1 != c2)
        {
            return c1 > c2;
        }
        i1++;
        i2++;
        c1 = block[i1 + 1];
        c2 = block[i2 + 1];
        if(c1 != c2)
        {
            return c1 > c2;
        }
        i1++;
        i2++;
        c1 = block[i1 + 1];
        c2 = block[i2 + 1];
        if(c1 != c2)
        {
            return c1 > c2;
        }
        i1++;
        i2++;
        int k = last + 1;
        do
        {
            c1 = block[i1 + 1];
            c2 = block[i2 + 1];
            if(c1 != c2)
            {
                return c1 > c2;
            }
            int s1 = quadrant[i1];
            int s2 = quadrant[i2];
            if(s1 != s2)
            {
                return s1 > s2;
            }
            i1++;
            i2++;
            c1 = block[i1 + 1];
            c2 = block[i2 + 1];
            if(c1 != c2)
            {
                return c1 > c2;
            }
            s1 = quadrant[i1];
            s2 = quadrant[i2];
            if(s1 != s2)
            {
                return s1 > s2;
            }
            i1++;
            i2++;
            c1 = block[i1 + 1];
            c2 = block[i2 + 1];
            if(c1 != c2)
            {
                return c1 > c2;
            }
            s1 = quadrant[i1];
            s2 = quadrant[i2];
            if(s1 != s2)
            {
                return s1 > s2;
            }
            i1++;
            i2++;
            c1 = block[i1 + 1];
            c2 = block[i2 + 1];
            if(c1 != c2)
            {
                return c1 > c2;
            }
            s1 = quadrant[i1];
            s2 = quadrant[i2];
            if(s1 != s2)
            {
                return s1 > s2;
            }
            i1++;
            i2++;
            if(i1 > last)
            {
                i1 -= last;
                i1--;
            }
            if(i2 > last)
            {
                i2 -= last;
                i2--;
            }
            k -= 4;
            workDone++;
        } while(k >= 0);
        return false;
    }

    private void allocateCompressStructures()
    {
        int n = 0x186a0 * blockSize100k;
        block = new char[n + 1 + 20];
        quadrant = new int[n + 20];
        zptr = new int[n];
        ftab = new int[0x10001];
        if(block != null && quadrant != null && zptr != null)
        {
            if(ftab != null);
        }
        szptr = new short[2 * n];
    }

    private void generateMTFValues()
    {
        char yy[] = new char[256];
        makeMaps();
        int EOB = nInUse + 1;
        for(int i = 0; i <= EOB; i++)
        {
            mtfFreq[i] = 0;
        }

        int wr = 0;
        int zPend = 0;
        for(int i = 0; i < nInUse; i++)
        {
            yy[i] = (char)i;
        }

        for(int i = 0; i <= last; i++)
        {
            char ll_i = unseqToSeq[block[zptr[i]]];
            int j = 0;
            char tmp;
            for(tmp = yy[j]; ll_i != tmp;)
            {
                j++;
                char tmp2 = tmp;
                tmp = yy[j];
                yy[j] = tmp2;
            }

            yy[0] = tmp;
            if(j == 0)
            {
                zPend++;
                continue;
            }
            if(zPend > 0)
            {
                zPend--;
                do
                {
                    switch(zPend % 2)
                    {
                    case 0: // '\0'
                        szptr[wr] = 0;
                        wr++;
                        mtfFreq[0]++;
                        break;

                    case 1: // '\001'
                        szptr[wr] = 1;
                        wr++;
                        mtfFreq[1]++;
                        break;
                    }
                    if(zPend < 2)
                    {
                        break;
                    }
                    zPend = (zPend - 2) / 2;
                } while(true);
                zPend = 0;
            }
            szptr[wr] = (short)(j + 1);
            wr++;
            mtfFreq[j + 1]++;
        }

        if(zPend > 0)
        {
            zPend--;
            do
            {
                switch(zPend % 2)
                {
                case 0: // '\0'
                    szptr[wr] = 0;
                    wr++;
                    mtfFreq[0]++;
                    break;

                case 1: // '\001'
                    szptr[wr] = 1;
                    wr++;
                    mtfFreq[1]++;
                    break;
                }
                if(zPend < 2)
                {
                    break;
                }
                zPend = (zPend - 2) / 2;
            } while(true);
        }
        szptr[wr] = (short)EOB;
        wr++;
        mtfFreq[EOB]++;
        nMTF = wr;
    }
}
