package com.openrsc.data;


public class DataFileDecrypter {

        public static int unpackData(byte abyte0[], int i, byte abyte1[], int j,
                                     int k) {
                DataFileVariables dataFileVariables = new DataFileVariables();
                dataFileVariables.aByteArray465 = abyte1;
                dataFileVariables.anInt466 = k;
                dataFileVariables.aByteArray470 = abyte0;
                dataFileVariables.anInt471 = 0;
                dataFileVariables.anInt467 = j;
                dataFileVariables.anInt472 = i;
                dataFileVariables.anInt479 = 0;
                dataFileVariables.anInt478 = 0;
                dataFileVariables.anInt468 = 0;
                dataFileVariables.anInt469 = 0;
                dataFileVariables.anInt473 = 0;
                dataFileVariables.anInt474 = 0;
                dataFileVariables.anInt481 = 0;
                method131(dataFileVariables);
                i -= dataFileVariables.anInt472;
                return i;
        }

        private static void method130(DataFileVariables dataFileVariables) {
                byte byte4 = dataFileVariables.aByte475;
                int i = dataFileVariables.anInt476;
                int j = dataFileVariables.anInt486;
                int k = dataFileVariables.anInt484;
                int ai[] = DataFileVariables.anIntArray489;
                int l = dataFileVariables.anInt483;
                byte abyte0[] = dataFileVariables.aByteArray470;
                int i1 = dataFileVariables.anInt471;
                int j1 = dataFileVariables.anInt472;
                int k1 = j1;
                int l1 = dataFileVariables.anInt503 + 1;
                label0:
                do {
                        if (i > 0) {
                                do {
                                        if (j1 == 0)
                                                break label0;
                                        if (i == 1)
                                                break;
                                        abyte0[i1] = byte4;
                                        i--;
                                        i1++;
                                        j1--;
                                } while (true);
                                if (j1 == 0) {
                                        i = 1;
                                        break;
                                }
                                abyte0[i1] = byte4;
                                i1++;
                                j1--;
                        }
                        boolean flag = true;
                        while (flag) {
                                flag = false;
                                if (j == l1) {
                                        i = 0;
                                        break label0;
                                }
                                byte4 = (byte) k;
                                l = ai[l];
                                byte byte0 = (byte) (l & 0xff);
                                l >>= 8;
                                j++;
                                if (byte0 != k) {
                                        k = byte0;
                                        if (j1 == 0) {
                                                i = 1;
                                        } else {
                                                abyte0[i1] = byte4;
                                                i1++;
                                                j1--;
                                                flag = true;
                                                continue;
                                        }
                                        break label0;
                                }
                                if (j != l1)
                                        continue;
                                if (j1 == 0) {
                                        i = 1;
                                        break label0;
                                }
                                abyte0[i1] = byte4;
                                i1++;
                                j1--;
                                flag = true;
                        }
                        i = 2;
                        l = ai[l];
                        byte byte1 = (byte) (l & 0xff);
                        l >>= 8;
                        if (++j != l1)
                                if (byte1 != k) {
                                        k = byte1;
                                } else {
                                        i = 3;
                                        l = ai[l];
                                        byte byte2 = (byte) (l & 0xff);
                                        l >>= 8;
                                        if (++j != l1)
                                                if (byte2 != k) {
                                                        k = byte2;
                                                } else {
                                                        l = ai[l];
                                                        byte byte3 = (byte) (l & 0xff);
                                                        l >>= 8;
                                                        j++;
                                                        i = (byte3 & 0xff) + 4;
                                                        l = ai[l];
                                                        k = (byte) (l & 0xff);
                                                        l >>= 8;
                                                        j++;
                                                }
                                }
                } while (true);
                int i2 = dataFileVariables.anInt473;
                dataFileVariables.anInt473 += k1 - j1;
                if (dataFileVariables.anInt473 < i2)
                        dataFileVariables.anInt474++;
                dataFileVariables.aByte475 = byte4;
                dataFileVariables.anInt476 = i;
                dataFileVariables.anInt486 = j;
                dataFileVariables.anInt484 = k;
                DataFileVariables.anIntArray489 = ai;
                dataFileVariables.anInt483 = l;
                dataFileVariables.aByteArray470 = abyte0;
                dataFileVariables.anInt471 = i1;
                dataFileVariables.anInt472 = j1;
        }

        private static void method131(DataFileVariables dataFileVariables) {
                int k8 = 0;
                int ai[] = null;
                int ai1[] = null;
                int ai2[] = null;
                dataFileVariables.anInt480 = 1;
                if (DataFileVariables.anIntArray489 == null)
                        DataFileVariables.anIntArray489 = new int[dataFileVariables.anInt480 * 0x186a0];
                boolean flag19 = true;
                while (flag19) {
                        byte byte0 = method132(dataFileVariables);
                        if (byte0 == 23)
                                return;
                        byte0 = method132(dataFileVariables);
                        byte0 = method132(dataFileVariables);
                        byte0 = method132(dataFileVariables);
                        byte0 = method132(dataFileVariables);
                        byte0 = method132(dataFileVariables);
                        dataFileVariables.anInt481++;
                        byte0 = method132(dataFileVariables);
                        byte0 = method132(dataFileVariables);
                        byte0 = method132(dataFileVariables);
                        byte0 = method132(dataFileVariables);
                        byte0 = method133(dataFileVariables);
                        if (byte0 != 0)
                                dataFileVariables.aBoolean477 = true;
                        else
                                dataFileVariables.aBoolean477 = false;
                        if (dataFileVariables.aBoolean477)
                                System.out.println("PANIC! RANDOMISED BLOCK!");
                        dataFileVariables.anInt482 = 0;
                        byte0 = method132(dataFileVariables);
                        dataFileVariables.anInt482 = dataFileVariables.anInt482 << 8
                                | byte0 & 0xff;
                        byte0 = method132(dataFileVariables);
                        dataFileVariables.anInt482 = dataFileVariables.anInt482 << 8
                                | byte0 & 0xff;
                        byte0 = method132(dataFileVariables);
                        dataFileVariables.anInt482 = dataFileVariables.anInt482 << 8
                                | byte0 & 0xff;
                        for (int j = 0; j < 16; j++) {
                                byte byte1 = method133(dataFileVariables);
                                if (byte1 == 1)
                                        dataFileVariables.aBooleanArray492[j] = true;
                                else
                                        dataFileVariables.aBooleanArray492[j] = false;
                        }

                        for (int k = 0; k < 256; k++)
                                dataFileVariables.aBooleanArray491[k] = false;

                        for (int l = 0; l < 16; l++)
                                if (dataFileVariables.aBooleanArray492[l]) {
                                        for (int i3 = 0; i3 < 16; i3++) {
                                                byte byte2 = method133(dataFileVariables);
                                                if (byte2 == 1)
                                                        dataFileVariables.aBooleanArray491[l * 16 + i3] = true;
                                        }

                                }

                        method135(dataFileVariables);
                        int i4 = dataFileVariables.anInt490 + 2;
                        int j4 = method134(3, dataFileVariables);
                        int k4 = method134(15, dataFileVariables);
                        for (int i1 = 0; i1 < k4; i1++) {
                                int j3 = 0;
                                do {
                                        byte byte3 = method133(dataFileVariables);
                                        if (byte3 == 0)
                                                break;
                                        j3++;
                                } while (true);
                                dataFileVariables.aByteArray497[i1] = (byte) j3;
                        }

                        byte abyte0[] = new byte[6];
                        for (byte byte16 = 0; byte16 < j4; byte16++)
                                abyte0[byte16] = byte16;

                        for (int j1 = 0; j1 < k4; j1++) {
                                byte byte17 = dataFileVariables.aByteArray497[j1];
                                byte byte15 = abyte0[byte17];
                                for (; byte17 > 0; byte17--)
                                        abyte0[byte17] = abyte0[byte17 - 1];

                                abyte0[0] = byte15;
                                dataFileVariables.aByteArray496[j1] = byte15;
                        }

                        for (int k3 = 0; k3 < j4; k3++) {
                                int l6 = method134(5, dataFileVariables);
                                for (int k1 = 0; k1 < i4; k1++) {
                                        do {
                                                byte byte4 = method133(dataFileVariables);
                                                if (byte4 == 0)
                                                        break;
                                                byte4 = method133(dataFileVariables);
                                                if (byte4 == 0)
                                                        l6++;
                                                else
                                                        l6--;
                                        } while (true);
                                        dataFileVariables.aByteArrayArray498[k3][k1] = (byte) l6;
                                }

                        }

                        for (int l3 = 0; l3 < j4; l3++) {
                                byte byte8 = 32;
                                int i = 0;
                                for (int l1 = 0; l1 < i4; l1++) {
                                        if (dataFileVariables.aByteArrayArray498[l3][l1] > i)
                                                i = dataFileVariables.aByteArrayArray498[l3][l1];
                                        if (dataFileVariables.aByteArrayArray498[l3][l1] < byte8)
                                                byte8 = dataFileVariables.aByteArrayArray498[l3][l1];
                                }

                                method136(dataFileVariables.anIntArrayArray499[l3],
                                        dataFileVariables.anIntArrayArray500[l3],
                                        dataFileVariables.anIntArrayArray501[l3],
                                        dataFileVariables.aByteArrayArray498[l3], byte8, i, i4);
                                dataFileVariables.anIntArray502[l3] = byte8;
                        }

                        int l4 = dataFileVariables.anInt490 + 1;
                        int i5 = -1;
                        int j5 = 0;
                        for (int i2 = 0; i2 <= 255; i2++)
                                dataFileVariables.anIntArray485[i2] = 0;

                        int j9 = 4095;
                        for (int l8 = 15; l8 >= 0; l8--) {
                                for (int i9 = 15; i9 >= 0; i9--) {
                                        dataFileVariables.aByteArray494[j9] = (byte) (l8 * 16 + i9);
                                        j9--;
                                }

                                dataFileVariables.anIntArray495[l8] = j9 + 1;
                        }

                        int i6 = 0;
                        if (j5 == 0) {
                                i5++;
                                j5 = 50;
                                byte byte12 = dataFileVariables.aByteArray496[i5];
                                k8 = dataFileVariables.anIntArray502[byte12];
                                ai = dataFileVariables.anIntArrayArray499[byte12];
                                ai2 = dataFileVariables.anIntArrayArray501[byte12];
                                ai1 = dataFileVariables.anIntArrayArray500[byte12];
                        }
                        j5--;
                        int i7 = k8;
                        int l7;
                        byte byte9;
                        for (l7 = method134(i7, dataFileVariables); l7 > ai[i7]; l7 = l7 << 1
                                | byte9) {
                                i7++;
                                byte9 = method133(dataFileVariables);
                        }

                        for (int k5 = ai2[l7 - ai1[i7]]; k5 != l4; )
                                if (k5 == 0 || k5 == 1) {
                                        int j6 = -1;
                                        int k6 = 1;
                                        do {
                                                if (k5 == 0)
                                                        j6 += k6;
                                                else if (k5 == 1)
                                                        j6 += 2 * k6;
                                                k6 *= 2;
                                                if (j5 == 0) {
                                                        i5++;
                                                        j5 = 50;
                                                        byte byte13 = dataFileVariables.aByteArray496[i5];
                                                        k8 = dataFileVariables.anIntArray502[byte13];
                                                        ai = dataFileVariables.anIntArrayArray499[byte13];
                                                        ai2 = dataFileVariables.anIntArrayArray501[byte13];
                                                        ai1 = dataFileVariables.anIntArrayArray500[byte13];
                                                }
                                                j5--;
                                                int j7 = k8;
                                                int i8;
                                                byte byte10;
                                                for (i8 = method134(j7, dataFileVariables); i8 > ai[j7]; i8 = i8 << 1
                                                        | byte10) {
                                                        j7++;
                                                        byte10 = method133(dataFileVariables);
                                                }

                                                k5 = ai2[i8 - ai1[j7]];
                                        } while (k5 == 0 || k5 == 1);
                                        j6++;
                                        byte byte5 = dataFileVariables.aByteArray493[dataFileVariables.aByteArray494[dataFileVariables.anIntArray495[0]] & 0xff];
                                        dataFileVariables.anIntArray485[byte5 & 0xff] += j6;
                                        for (; j6 > 0; j6--) {
                                                DataFileVariables.anIntArray489[i6] = byte5 & 0xff;
                                                i6++;
                                        }

                                } else {
                                        int j11 = k5 - 1;
                                        byte byte6;
                                        if (j11 < 16) {
                                                int j10 = dataFileVariables.anIntArray495[0];
                                                byte6 = dataFileVariables.aByteArray494[j10 + j11];
                                                for (; j11 > 3; j11 -= 4) {
                                                        int k11 = j10 + j11;
                                                        dataFileVariables.aByteArray494[k11] = dataFileVariables.aByteArray494[k11 - 1];
                                                        dataFileVariables.aByteArray494[k11 - 1] = dataFileVariables.aByteArray494[k11 - 2];
                                                        dataFileVariables.aByteArray494[k11 - 2] = dataFileVariables.aByteArray494[k11 - 3];
                                                        dataFileVariables.aByteArray494[k11 - 3] = dataFileVariables.aByteArray494[k11 - 4];
                                                }

                                                for (; j11 > 0; j11--)
                                                        dataFileVariables.aByteArray494[j10 + j11] = dataFileVariables.aByteArray494[(j10 + j11) - 1];

                                                dataFileVariables.aByteArray494[j10] = byte6;
                                        } else {
                                                int l10 = j11 / 16;
                                                int i11 = j11 % 16;
                                                int k10 = dataFileVariables.anIntArray495[l10] + i11;
                                                byte6 = dataFileVariables.aByteArray494[k10];
                                                for (; k10 > dataFileVariables.anIntArray495[l10]; k10--)
                                                        dataFileVariables.aByteArray494[k10] = dataFileVariables.aByteArray494[k10 - 1];

                                                dataFileVariables.anIntArray495[l10]++;
                                                for (; l10 > 0; l10--) {
                                                        dataFileVariables.anIntArray495[l10]--;
                                                        dataFileVariables.aByteArray494[dataFileVariables.anIntArray495[l10]] = dataFileVariables.aByteArray494[(dataFileVariables.anIntArray495[l10 - 1] + 16) - 1];
                                                }

                                                dataFileVariables.anIntArray495[0]--;
                                                dataFileVariables.aByteArray494[dataFileVariables.anIntArray495[0]] = byte6;
                                                if (dataFileVariables.anIntArray495[0] == 0) {
                                                        int i10 = 4095;
                                                        for (int k9 = 15; k9 >= 0; k9--) {
                                                                for (int l9 = 15; l9 >= 0; l9--) {
                                                                        dataFileVariables.aByteArray494[i10] = dataFileVariables.aByteArray494[dataFileVariables.anIntArray495[k9]
                                                                                + l9];
                                                                        i10--;
                                                                }

                                                                dataFileVariables.anIntArray495[k9] = i10 + 1;
                                                        }

                                                }
                                        }
                                        dataFileVariables.anIntArray485[dataFileVariables.aByteArray493[byte6 & 0xff] & 0xff]++;
                                        DataFileVariables.anIntArray489[i6] = dataFileVariables.aByteArray493[byte6 & 0xff] & 0xff;
                                        i6++;
                                        if (j5 == 0) {
                                                i5++;
                                                j5 = 50;
                                                byte byte14 = dataFileVariables.aByteArray496[i5];
                                                k8 = dataFileVariables.anIntArray502[byte14];
                                                ai = dataFileVariables.anIntArrayArray499[byte14];
                                                ai2 = dataFileVariables.anIntArrayArray501[byte14];
                                                ai1 = dataFileVariables.anIntArrayArray500[byte14];
                                        }
                                        j5--;
                                        int k7 = k8;
                                        int j8;
                                        byte byte11;
                                        for (j8 = method134(k7, dataFileVariables); j8 > ai[k7]; j8 = j8 << 1
                                                | byte11) {
                                                k7++;
                                                byte11 = method133(dataFileVariables);
                                        }

                                        k5 = ai2[j8 - ai1[k7]];
                                }

                        dataFileVariables.anInt476 = 0;
                        dataFileVariables.aByte475 = 0;
                        dataFileVariables.anIntArray487[0] = 0;
                        for (int j2 = 1; j2 <= 256; j2++)
                                dataFileVariables.anIntArray487[j2] = dataFileVariables.anIntArray485[j2 - 1];

                        for (int k2 = 1; k2 <= 256; k2++)
                                dataFileVariables.anIntArray487[k2] += dataFileVariables.anIntArray487[k2 - 1];

                        for (int l2 = 0; l2 < i6; l2++) {
                                byte byte7 = (byte) (DataFileVariables.anIntArray489[l2] & 0xff);
                                DataFileVariables.anIntArray489[dataFileVariables.anIntArray487[byte7 & 0xff]] |= l2 << 8;
                                dataFileVariables.anIntArray487[byte7 & 0xff]++;
                        }

                        dataFileVariables.anInt483 = DataFileVariables.anIntArray489[dataFileVariables.anInt482] >> 8;
                        dataFileVariables.anInt486 = 0;
                        dataFileVariables.anInt483 = DataFileVariables.anIntArray489[dataFileVariables.anInt483];
                        dataFileVariables.anInt484 = (byte) (dataFileVariables.anInt483 & 0xff);
                        dataFileVariables.anInt483 >>= 8;
                        dataFileVariables.anInt486++;
                        dataFileVariables.anInt503 = i6;
                        method130(dataFileVariables);
                        if (dataFileVariables.anInt486 == dataFileVariables.anInt503 + 1
                                && dataFileVariables.anInt476 == 0)
                                flag19 = true;
                        else
                                flag19 = false;
                }
        }

        private static byte method132(DataFileVariables dataFileVariables) {
                return (byte) method134(8, dataFileVariables);
        }

        private static byte method133(DataFileVariables dataFileVariables) {
                return (byte) method134(1, dataFileVariables);
        }

        private static int method134(int i, DataFileVariables dataFileVariables) {
                int j;
                do {
                        if (dataFileVariables.anInt479 >= i) {
                                int k = dataFileVariables.anInt478 >> dataFileVariables.anInt479
                                        - i
                                        & (1 << i) - 1;
                                dataFileVariables.anInt479 -= i;
                                j = k;
                                break;
                        }
                        dataFileVariables.anInt478 = dataFileVariables.anInt478 << 8
                                | dataFileVariables.aByteArray465[dataFileVariables.anInt466]
                                & 0xff;
                        dataFileVariables.anInt479 += 8;
                        dataFileVariables.anInt466++;
                        dataFileVariables.anInt467--;
                        dataFileVariables.anInt468++;
                        if (dataFileVariables.anInt468 == 0)
                                dataFileVariables.anInt469++;
                } while (true);
                return j;
        }

        private static void method135(DataFileVariables dataFileVariables) {
                dataFileVariables.anInt490 = 0;
                for (int i = 0; i < 256; i++)
                        if (dataFileVariables.aBooleanArray491[i]) {
                                dataFileVariables.aByteArray493[dataFileVariables.anInt490] = (byte) i;
                                dataFileVariables.anInt490++;
                        }

        }

        private static void method136(int ai[], int ai1[], int ai2[],
                                      byte abyte0[], int i, int j, int k) {
                int l = 0;
                for (int i1 = i; i1 <= j; i1++) {
                        for (int l2 = 0; l2 < k; l2++)
                                if (abyte0[l2] == i1) {
                                        ai2[l] = l2;
                                        l++;
                                }

                }

                for (int j1 = 0; j1 < 23; j1++)
                        ai1[j1] = 0;

                for (int k1 = 0; k1 < k; k1++)
                        ai1[abyte0[k1] + 1]++;

                for (int l1 = 1; l1 < 23; l1++)
                        ai1[l1] += ai1[l1 - 1];

                for (int i2 = 0; i2 < 23; i2++)
                        ai[i2] = 0;

                int i3 = 0;
                for (int j2 = i; j2 <= j; j2++) {
                        i3 += ai1[j2 + 1] - ai1[j2];
                        ai[j2] = i3 - 1;
                        i3 <<= 1;
                }

                for (int k2 = i + 1; k2 <= j; k2++)
                        ai1[k2] = (ai[k2 - 1] + 1 << 1) - ai1[k2];

        }
}
