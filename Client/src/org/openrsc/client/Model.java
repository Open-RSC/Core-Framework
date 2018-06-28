package org.openrsc.client;

import java.io.DataInputStream;
import java.io.IOException;

public class Model {

    public Model(int i, int j) {
        anInt246 = 1;
        aBoolean247 = true;
        aBoolean254 = true;
        aBoolean255 = false;
        isGiantCrystal = false;
        anInt257 = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        aBoolean263 = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        anInt303 = 180;
        anInt304 = 155;
        anInt305 = 95;
        anInt306 = 256;
        anInt307 = 512;
        anInt308 = 32;
        method174(i, j);
        anIntArrayArray279 = new int[j][1];
        for (int k = 0; k < j; k++)
            anIntArrayArray279[k][0] = k;

    }

    public Model(int i, int j, boolean flag, boolean flag1, boolean flag2, boolean flag3, boolean flag4) {
        anInt246 = 1;
        aBoolean247 = true;
        aBoolean254 = true;
        aBoolean255 = false;
        isGiantCrystal = false;
        anInt257 = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        aBoolean263 = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        anInt303 = 180;
        anInt304 = 155;
        anInt305 = 95;
        anInt306 = 256;
        anInt307 = 512;
        anInt308 = 32;
        aBoolean260 = flag;
        aBoolean261 = flag1;
        aBoolean262 = flag2;
        aBoolean263 = flag3;
        aBoolean264 = flag4;
        method174(i, j);
    }

    private void method174(int i, int j) {
        anIntArray272 = new int[i];
        anIntArray273 = new int[i];
        anIntArray274 = new int[i];
        anIntArray232 = new int[i];
        aByteArray233 = new byte[i];
        anIntArray235 = new int[j];
        anIntArrayArray236 = new int[j][];
        anIntArray237 = new int[j];
        anIntArray238 = new int[j];
        anIntArray241 = new int[j];
        anIntArray240 = new int[j];
        anIntArray239 = new int[j];
        if (!aBoolean264) {
            anIntArray227 = new int[i];
            anIntArray228 = new int[i];
            anIntArray229 = new int[i];
            anIntArray230 = new int[i];
            anIntArray231 = new int[i];
        }
        if (!aBoolean263) {
            aByteArray259 = new byte[j];
            anIntArray258 = new int[j];
        }
        if (aBoolean260) {
            anIntArray275 = anIntArray272;
            anIntArray276 = anIntArray273;
            anIntArray277 = anIntArray274;
        } else {
            anIntArray275 = new int[i];
            anIntArray276 = new int[i];
            anIntArray277 = new int[i];
        }
        if (!aBoolean262 || !aBoolean261) {
            anIntArray242 = new int[j];
            anIntArray243 = new int[j];
            anIntArray244 = new int[j];
        }
        if (!aBoolean261) {
            anIntArray280 = new int[j];
            anIntArray281 = new int[j];
            anIntArray282 = new int[j];
            anIntArray283 = new int[j];
            anIntArray284 = new int[j];
            anIntArray285 = new int[j];
        }
        anInt234 = 0;
        anInt226 = 0;
        anInt271 = i;
        anInt278 = j;
        anInt286 = anInt287 = anInt288 = 0;
        anInt289 = anInt290 = anInt291 = 0;
        anInt292 = anInt293 = anInt294 = 256;
        anInt295 = anInt296 = anInt297 = anInt298 = anInt299 = anInt300 = 256;
        anInt301 = 0;
    }

    public void method175() {
        anIntArray227 = new int[anInt226];
        anIntArray228 = new int[anInt226];
        anIntArray229 = new int[anInt226];
        anIntArray230 = new int[anInt226];
        anIntArray231 = new int[anInt226];
    }

    public void method176() {
        anInt234 = 0;
        anInt226 = 0;
    }

    public void method177(int i, int j) {
        anInt234 -= i;
        if (anInt234 < 0)
            anInt234 = 0;
        anInt226 -= j;
        if (anInt226 < 0)
            anInt226 = 0;
    }

    public Model(byte abyte0[], int i, boolean flag) {
        anInt246 = 1;
        aBoolean247 = true;
        aBoolean254 = true;
        aBoolean255 = false;
        isGiantCrystal = false;
        anInt257 = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        aBoolean263 = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        anInt303 = 180;
        anInt304 = 155;
        anInt305 = 95;
        anInt306 = 256;
        anInt307 = 512;
        anInt308 = 32;
        int j = DataOperations.getUnsigned2Bytes(abyte0, i);
        i += 2;
        int k = DataOperations.getUnsigned2Bytes(abyte0, i);
        i += 2;
        method174(j, k);
        anIntArrayArray279 = new int[k][1];
        for (int l = 0; l < j; l++) {
            anIntArray272[l] = DataOperations.getSigned2Bytes(abyte0, i);
            i += 2;
        }

        for (int i1 = 0; i1 < j; i1++) {
            anIntArray273[i1] = DataOperations.getSigned2Bytes(abyte0, i);
            i += 2;
        }

        for (int j1 = 0; j1 < j; j1++) {
            anIntArray274[j1] = DataOperations.getSigned2Bytes(abyte0, i);
            i += 2;
        }

        anInt226 = j;
        for (int k1 = 0; k1 < k; k1++)
            anIntArray235[k1] = abyte0[i++] & 0xff;

        for (int l1 = 0; l1 < k; l1++) {
            anIntArray237[l1] = DataOperations.getSigned2Bytes(abyte0, i);
            i += 2;
            if (anIntArray237[l1] == 32767)
                anIntArray237[l1] = anInt270;
        }

        for (int i2 = 0; i2 < k; i2++) {
            anIntArray238[i2] = DataOperations.getSigned2Bytes(abyte0, i);
            i += 2;
            if (anIntArray238[i2] == 32767)
                anIntArray238[i2] = anInt270;
        }

        for (int j2 = 0; j2 < k; j2++) {
            int k2 = abyte0[i++] & 0xff;
            if (k2 == 0)
                anIntArray241[j2] = 0;
            else
                anIntArray241[j2] = anInt270;
        }

        for (int l2 = 0; l2 < k; l2++) {
            anIntArrayArray236[l2] = new int[anIntArray235[l2]];
            for (int i3 = 0; i3 < anIntArray235[l2]; i3++)
                if (j < 256) {
                    anIntArrayArray236[l2][i3] = abyte0[i++] & 0xff;
                } else {
                    anIntArrayArray236[l2][i3] = DataOperations.getUnsigned2Bytes(abyte0, i);
                    i += 2;
                }

        }

        anInt234 = k;
        anInt246 = 1;
    }

    public Model(String path) {
        anInt246 = 1;
        aBoolean247 = true;
        aBoolean254 = true;
        aBoolean255 = false;
        isGiantCrystal = false;
        anInt257 = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        aBoolean263 = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        anInt303 = 180;
        anInt304 = 155;
        anInt305 = 95;
        anInt306 = 256;
        anInt307 = 512;
        anInt308 = 32;
        byte abyte0[] = null;
        try {
            java.io.InputStream inputstream = DataOperations.streamFromPath(path);
            DataInputStream datainputstream = new DataInputStream(inputstream);
            abyte0 = new byte[3];
            anInt309 = 0;
            for (int i = 0; i < 3; i += datainputstream.read(abyte0, i, 3 - i))
                ;
            int k = method206(abyte0);
            abyte0 = new byte[k];
            anInt309 = 0;
            for (int j = 0; j < k; j += datainputstream.read(abyte0, j, k - j))
                ;
            datainputstream.close();
        }
        catch (IOException _ex) {
            anInt226 = 0;
            anInt234 = 0;
            return;
        }
        int l = method206(abyte0);
        int i1 = method206(abyte0);
        method174(l, i1);
        anIntArrayArray279 = new int[i1][];
        for (int j3 = 0; j3 < l; j3++) {
            int j1 = method206(abyte0);
            int k1 = method206(abyte0);
            int l1 = method206(abyte0);
            vertex3I(j1, k1, l1);
        }

        for (int k3 = 0; k3 < i1; k3++) {
            int i2 = method206(abyte0);
            int j2 = method206(abyte0);
            int k2 = method206(abyte0);
            int l2 = method206(abyte0);
            anInt307 = method206(abyte0);
            anInt308 = method206(abyte0);
            int i3 = method206(abyte0);
            int ai[] = new int[i2];
            for (int l3 = 0; l3 < i2; l3++)
                ai[l3] = method206(abyte0);

            int ai1[] = new int[l2];
            for (int i4 = 0; i4 < l2; i4++)
                ai1[i4] = method206(abyte0);

            int j4 = method181(i2, ai, j2, k2);
            anIntArrayArray279[k3] = ai1;
            if (i3 == 0)
                anIntArray241[j4] = 0;
            else
                anIntArray241[j4] = anInt270;
        }

        anInt246 = 1;
    }

    public Model(Model model[], int i, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        anInt246 = 1;
        aBoolean247 = true;
        aBoolean254 = true;
        aBoolean255 = false;
        isGiantCrystal = false;
        anInt257 = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        aBoolean263 = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        anInt303 = 180;
        anInt304 = 155;
        anInt305 = 95;
        anInt306 = 256;
        anInt307 = 512;
        anInt308 = 32;
        aBoolean260 = flag;
        aBoolean261 = flag1;
        aBoolean262 = flag2;
        aBoolean263 = flag3;
        method178(model, i, false);
    }

    public Model(Model models[], int i) {
        anInt246 = 1;
        aBoolean247 = true;
        aBoolean254 = true;
        aBoolean255 = false;
        isGiantCrystal = false;
        anInt257 = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        aBoolean263 = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        anInt303 = 180;
        anInt304 = 155;
        anInt305 = 95;
        anInt306 = 256;
        anInt307 = 512;
        anInt308 = 32;
        method178(models, i, true);
    }

    public void method178(Model models[], int i, boolean flag) {
        int j = 0;
        int k = 0;
        for (int l = 0; l < i; l++) {
            j += models[l].anInt234;
            k += models[l].anInt226;
        }

        method174(k, j);
        if (flag)
            anIntArrayArray279 = new int[j][];
        for (int i1 = 0; i1 < i; i1++) {
            Model model = models[i1];
            model.method202();
            anInt308 = model.anInt308;
            anInt307 = model.anInt307;
            anInt303 = model.anInt303;
            anInt304 = model.anInt304;
            anInt305 = model.anInt305;
            anInt306 = model.anInt306;
            for (int j1 = 0; j1 < model.anInt234; j1++) {
                int ai[] = new int[model.anIntArray235[j1]];
                int ai1[] = model.anIntArrayArray236[j1];
                for (int k1 = 0; k1 < model.anIntArray235[j1]; k1++)
                    ai[k1] = vertex3I(model.anIntArray272[ai1[k1]], model.anIntArray273[ai1[k1]], model.anIntArray274[ai1[k1]]);

                int l1 = method181(model.anIntArray235[j1], ai, model.anIntArray237[j1], model.anIntArray238[j1]);
                anIntArray241[l1] = model.anIntArray241[j1];
                anIntArray240[l1] = model.anIntArray240[j1];
                anIntArray239[l1] = model.anIntArray239[j1];
                if (flag)
                    if (i > 1) {
                        anIntArrayArray279[l1] = new int[model.anIntArrayArray279[j1].length + 1];
                        anIntArrayArray279[l1][0] = i1;
                        for (int i2 = 0; i2 < model.anIntArrayArray279[j1].length; i2++)
                            anIntArrayArray279[l1][i2 + 1] = model.anIntArrayArray279[j1][i2];

                    } else {
                        anIntArrayArray279[l1] = new int[model.anIntArrayArray279[j1].length];
                        for (int j2 = 0; j2 < model.anIntArrayArray279[j1].length; j2++)
                            anIntArrayArray279[l1][j2] = model.anIntArrayArray279[j1][j2];

                    }
            }

        }

        anInt246 = 1;
    }

    public int vertex3I(int x, int elevation, int y) {
        for (int l = 0; l < anInt226; l++)
            if (anIntArray272[l] == x && anIntArray273[l] == elevation && anIntArray274[l] == y)
                return l;

        if (anInt226 >= anInt271) {
            return -1;
        } else {
            anIntArray272[anInt226] = x;
            anIntArray273[anInt226] = elevation;
            anIntArray274[anInt226] = y;
            return anInt226++;
        }
    }

    public int method180(int i, int j, int k) {
        if (anInt226 >= anInt271) {
            return -1;
        } else {
            anIntArray272[anInt226] = i;
            anIntArray273[anInt226] = j;
            anIntArray274[anInt226] = k;
            return anInt226++;
        }
    }

    public int method181(int i, int ai[], int j, int k) {
        if (anInt234 >= anInt278) {
            return -1;
        } else {
            anIntArray235[anInt234] = i;
            anIntArrayArray236[anInt234] = ai;
            anIntArray237[anInt234] = j;
            anIntArray238[anInt234] = k;
            anInt246 = 1;
            return anInt234++;
        }
    }

    public Model[] method182(int i, int j, int k, int l, int i1, int j1, int k1, boolean flag) {
        method202();
        int ai[] = new int[j1];
        int ai1[] = new int[j1];
        for (int l1 = 0; l1 < j1; l1++) {
            ai[l1] = 0;
            ai1[l1] = 0;
        }

        for (int i2 = 0; i2 < anInt234; i2++) {
            int j2 = 0;
            int k2 = 0;
            int i3 = anIntArray235[i2];
            int ai2[] = anIntArrayArray236[i2];
            for (int i4 = 0; i4 < i3; i4++) {
                j2 += anIntArray272[ai2[i4]];
                k2 += anIntArray274[ai2[i4]];
            }

            int k4 = j2 / (i3 * k) + (k2 / (i3 * l)) * i1;
            ai[k4] += i3;
            ai1[k4]++;
        }

        Model models[] = new Model[j1];
        for (int l2 = 0; l2 < j1; l2++) {
            if (ai[l2] > k1)
                ai[l2] = k1;
            models[l2] = new Model(ai[l2], ai1[l2], true, true, true, flag, true);
            models[l2].anInt307 = anInt307;
            models[l2].anInt308 = anInt308;
        }

        for (int j3 = 0; j3 < anInt234; j3++) {
            int k3 = 0;
            int j4 = 0;
            int l4 = anIntArray235[j3];
            int ai3[] = anIntArrayArray236[j3];
            for (int i5 = 0; i5 < l4; i5++) {
                k3 += anIntArray272[ai3[i5]];
                j4 += anIntArray274[ai3[i5]];
            }

            int j5 = k3 / (l4 * k) + (j4 / (l4 * l)) * i1;
            method183(models[j5], ai3, l4, j3);
        }

        for (int l3 = 0; l3 < j1; l3++)
            models[l3].method175();

        return models;
    }

    public void method183(Model model, int ai[], int i, int j) {
        int ai1[] = new int[i];
        for (int k = 0; k < i; k++) {
            int l = ai1[k] = model.vertex3I(anIntArray272[ai[k]], anIntArray273[ai[k]], anIntArray274[ai[k]]);
            model.anIntArray232[l] = anIntArray232[ai[k]];
            model.aByteArray233[l] = aByteArray233[ai[k]];
        }

        int i1 = model.method181(i, ai1, anIntArray237[j], anIntArray238[j]);
        if (!model.aBoolean263 && !aBoolean263)
            model.anIntArray258[i1] = anIntArray258[j];
        model.anIntArray241[i1] = anIntArray241[j];
        model.anIntArray240[i1] = anIntArray240[j];
        model.anIntArray239[i1] = anIntArray239[j];
    }

    public void method184(boolean flag, int i, int j, int k, int l, int i1) {
        anInt308 = 256 - i * 4;
        anInt307 = (64 - j) * 16 + 128;
        if (aBoolean262)
            return;
        for (int j1 = 0; j1 < anInt234; j1++)
            if (flag)
                anIntArray241[j1] = anInt270;
            else
                anIntArray241[j1] = 0;

        anInt303 = k;
        anInt304 = l;
        anInt305 = i1;
        anInt306 = (int) Math.sqrt(k * k + l * l + i1 * i1);
        method198();
    }

    public void method185(int i, int j, int k, int l, int i1) {
        anInt308 = 256 - i * 4;
        anInt307 = (64 - j) * 16 + 128;
        if (aBoolean262) {
            return;
        } else {
            anInt303 = k;
            anInt304 = l;
            anInt305 = i1;
            anInt306 = (int) Math.sqrt(k * k + l * l + i1 * i1);
            method198();
            return;
        }
    }

    public void method186(int i, int j, int k) {
        if (aBoolean262) {
            return;
        } else {
            anInt303 = i;
            anInt304 = j;
            anInt305 = k;
            anInt306 = (int) Math.sqrt(i * i + j * j + k * k);
            method198();
            return;
        }
    }

    public void method187(int i, int random) {
        aByteArray233[i] = (byte) random;
    }

    public void method188(int i, int j, int k) {
        anInt289 = anInt289 + i & 0xff;
        anInt290 = anInt290 + j & 0xff;
        anInt291 = anInt291 + k & 0xff;
        method192();
        anInt246 = 1;
    }

    public void method189(int i, int j, int k) {
        anInt289 = i & 0xff;
        anInt290 = j & 0xff;
        anInt291 = k & 0xff;
        method192();
        anInt246 = 1;
    }

    public void method190(int i, int j, int k) {
        anInt286 += i;
        anInt287 += j;
        anInt288 += k;
        method192();
        anInt246 = 1;
    }

    public void method191(int i, int j, int k) {
        anInt286 = i;
        anInt287 = j;
        anInt288 = k;
        method192();
        anInt246 = 1;
    }

    private void method192() {
        if (anInt295 != 256 || anInt296 != 256 || anInt297 != 256 || anInt298 != 256 || anInt299 != 256 || anInt300 != 256) {
            anInt301 = 4;
            return;
        }
        if (anInt292 != 256 || anInt293 != 256 || anInt294 != 256) {
            anInt301 = 3;
            return;
        }
        if (anInt289 != 0 || anInt290 != 0 || anInt291 != 0) {
            anInt301 = 2;
            return;
        }
        if (anInt286 != 0 || anInt287 != 0 || anInt288 != 0) {
            anInt301 = 1;
            return;
        } else {
            anInt301 = 0;
            return;
        }
    }

    private void method193(int i, int j, int k) {
        for (int l = 0; l < anInt226; l++) {
            anIntArray275[l] += i;
            anIntArray276[l] += j;
            anIntArray277[l] += k;
        }

    }

    private void method194(int i, int j, int k) {
        for (int i3 = 0; i3 < anInt226; i3++) {
            if (k != 0) {
                int l = anIntArray265[k];
                int k1 = anIntArray265[k + 256];
                int j2 = anIntArray276[i3] * l + anIntArray275[i3] * k1 >> 15;
                anIntArray276[i3] = anIntArray276[i3] * k1 - anIntArray275[i3] * l >> 15;
                anIntArray275[i3] = j2;
            }
            if (i != 0) {
                int i1 = anIntArray265[i];
                int l1 = anIntArray265[i + 256];
                int k2 = anIntArray276[i3] * l1 - anIntArray277[i3] * i1 >> 15;
                anIntArray277[i3] = anIntArray276[i3] * i1 + anIntArray277[i3] * l1 >> 15;
                anIntArray276[i3] = k2;
            }
            if (j != 0) {
                int j1 = anIntArray265[j];
                int i2 = anIntArray265[j + 256];
                int l2 = anIntArray277[i3] * j1 + anIntArray275[i3] * i2 >> 15;
                anIntArray277[i3] = anIntArray277[i3] * i2 - anIntArray275[i3] * j1 >> 15;
                anIntArray275[i3] = l2;
            }
        }

    }

    private void method195(int i, int j, int k, int l, int i1, int j1) {
        for (int k1 = 0; k1 < anInt226; k1++) {
            if (i != 0)
                anIntArray275[k1] += anIntArray276[k1] * i >> 8;
            if (j != 0)
                anIntArray277[k1] += anIntArray276[k1] * j >> 8;
            if (k != 0)
                anIntArray275[k1] += anIntArray277[k1] * k >> 8;
            if (l != 0)
                anIntArray276[k1] += anIntArray277[k1] * l >> 8;
            if (i1 != 0)
                anIntArray277[k1] += anIntArray275[k1] * i1 >> 8;
            if (j1 != 0)
                anIntArray276[k1] += anIntArray275[k1] * j1 >> 8;
        }

    }

    private void method196(int i, int j, int k) {
        for (int l = 0; l < anInt226; l++) {
            anIntArray275[l] = anIntArray275[l] * i >> 8;
            anIntArray276[l] = anIntArray276[l] * j >> 8;
            anIntArray277[l] = anIntArray277[l] * k >> 8;
        }

    }

    private void method197() {
        anInt248 = anInt250 = anInt252 = 0xf423f;
        anInt302 = anInt249 = anInt251 = anInt253 = 0xfff0bdc1;
        for (int i = 0; i < anInt234; i++) {
            int ai[] = anIntArrayArray236[i];
            int k = ai[0];
            int i1 = anIntArray235[i];
            int j1;
            int k1 = j1 = anIntArray275[k];
            int l1;
            int i2 = l1 = anIntArray276[k];
            int j2;
            int k2 = j2 = anIntArray277[k];
            for (int j = 0; j < i1; j++) {
                int l = ai[j];
                if (anIntArray275[l] < j1)
                    j1 = anIntArray275[l];
                else if (anIntArray275[l] > k1)
                    k1 = anIntArray275[l];
                if (anIntArray276[l] < l1)
                    l1 = anIntArray276[l];
                else if (anIntArray276[l] > i2)
                    i2 = anIntArray276[l];
                if (anIntArray277[l] < j2)
                    j2 = anIntArray277[l];
                else if (anIntArray277[l] > k2)
                    k2 = anIntArray277[l];
            }

            if (!aBoolean261) {
                anIntArray280[i] = j1;
                anIntArray281[i] = k1;
                anIntArray282[i] = l1;
                anIntArray283[i] = i2;
                anIntArray284[i] = j2;
                anIntArray285[i] = k2;
            }
            if (k1 - j1 > anInt302)
                anInt302 = k1 - j1;
            if (i2 - l1 > anInt302)
                anInt302 = i2 - l1;
            if (k2 - j2 > anInt302)
                anInt302 = k2 - j2;
            if (j1 < anInt248)
                anInt248 = j1;
            if (k1 > anInt249)
                anInt249 = k1;
            if (l1 < anInt250)
                anInt250 = l1;
            if (i2 > anInt251)
                anInt251 = i2;
            if (j2 < anInt252)
                anInt252 = j2;
            if (k2 > anInt253)
                anInt253 = k2;
        }

    }

    public void method198() {
        if (aBoolean262)
            return;
        int i = anInt307 * anInt306 >> 8;
        for (int j = 0; j < anInt234; j++)
            if (anIntArray241[j] != anInt270)
                anIntArray241[j] = (anIntArray242[j] * anInt303 + anIntArray243[j] * anInt304 + anIntArray244[j] * anInt305) / i;

        int ai[] = new int[anInt226];
        int ai1[] = new int[anInt226];
        int ai2[] = new int[anInt226];
        int ai3[] = new int[anInt226];
        for (int k = 0; k < anInt226; k++) {
            ai[k] = 0;
            ai1[k] = 0;
            ai2[k] = 0;
            ai3[k] = 0;
        }

        for (int l = 0; l < anInt234; l++)
            if (anIntArray241[l] == anInt270) {
                for (int i1 = 0; i1 < anIntArray235[l]; i1++) {
                    int k1 = anIntArrayArray236[l][i1];
                    ai[k1] += anIntArray242[l];
                    ai1[k1] += anIntArray243[l];
                    ai2[k1] += anIntArray244[l];
                    ai3[k1]++;
                }

            }

        for (int j1 = 0; j1 < anInt226; j1++)
            if (ai3[j1] > 0)
                anIntArray232[j1] = (ai[j1] * anInt303 + ai1[j1] * anInt304 + ai2[j1] * anInt305) / (i * ai3[j1]);

    }

    public void method199() {
        if (aBoolean262 && aBoolean261)
            return;
        for (int i = 0; i < anInt234; i++) {
            int ai[] = anIntArrayArray236[i];
            int j = anIntArray275[ai[0]];
            int k = anIntArray276[ai[0]];
            int l = anIntArray277[ai[0]];
            int i1 = anIntArray275[ai[1]] - j;
            int j1 = anIntArray276[ai[1]] - k;
            int k1 = anIntArray277[ai[1]] - l;
            int l1 = anIntArray275[ai[2]] - j;
            int i2 = anIntArray276[ai[2]] - k;
            int j2 = anIntArray277[ai[2]] - l;
            int k2 = j1 * j2 - i2 * k1;
            int l2 = k1 * l1 - j2 * i1;
            int i3;
            for (i3 = i1 * i2 - l1 * j1; k2 > 8192 || l2 > 8192 || i3 > 8192 || k2 < -8192 || l2 < -8192 || i3 < -8192; i3 >>= 1)
            {
                k2 >>= 1;
                l2 >>= 1;
            }

            int j3 = (int) (256D * Math.sqrt(k2 * k2 + l2 * l2 + i3 * i3));
            if (j3 <= 0)
                j3 = 1;
            anIntArray242[i] = (k2 * 0x10000) / j3;
            anIntArray243[i] = (l2 * 0x10000) / j3;
            anIntArray244[i] = (i3 * 65535) / j3;
            anIntArray240[i] = -1;
        }

        method198();
    }

    public void method200() {
        if (anInt246 == 2) {
            anInt246 = 0;
            for (int i = 0; i < anInt226; i++) {
                anIntArray275[i] = anIntArray272[i];
                anIntArray276[i] = anIntArray273[i];
                anIntArray277[i] = anIntArray274[i];
            }

            anInt248 = anInt250 = anInt252 = 0xff676981;
            anInt302 = anInt249 = anInt251 = anInt253 = 0x98967f;
            return;
        }
        if (anInt246 == 1) {
            anInt246 = 0;
            for (int j = 0; j < anInt226; j++) {
                anIntArray275[j] = anIntArray272[j];
                anIntArray276[j] = anIntArray273[j];
                anIntArray277[j] = anIntArray274[j];
            }

            if (anInt301 >= 2)
                method194(anInt289, anInt290, anInt291);
            if (anInt301 >= 3)
                method196(anInt292, anInt293, anInt294);
            if (anInt301 >= 4)
                method195(anInt295, anInt296, anInt297, anInt298, anInt299, anInt300);
            if (anInt301 >= 1)
                method193(anInt286, anInt287, anInt288);
            method197();
            method199();
        }
    }

    public void method201(int i, int j, int k, int l, int i1, int j1, int k1,
                          int l1) {
        method200();
        if (anInt252 > Camera.anInt453 || anInt253 < Camera.anInt452 || anInt248 > Camera.anInt449 || anInt249 < Camera.anInt448 || anInt250 > Camera.anInt451 || anInt251 < Camera.anInt450) {
            aBoolean247 = false;
            return;
        }
        aBoolean247 = true;
        int l2 = 0;
        int i3 = 0;
        int j3 = 0;
        int k3 = 0;
        int l3 = 0;
        int i4 = 0;
        if (j1 != 0) {
            l2 = anIntArray266[j1];
            i3 = anIntArray266[j1 + 1024];
        }
        if (i1 != 0) {
            l3 = anIntArray266[i1];
            i4 = anIntArray266[i1 + 1024];
        }
        if (l != 0) {
            j3 = anIntArray266[l];
            k3 = anIntArray266[l + 1024];
        }
        for (int j4 = 0; j4 < anInt226; j4++) {
            int k4 = anIntArray275[j4] - i;
            int l4 = anIntArray276[j4] - j;
            int i5 = anIntArray277[j4] - k;
            if (j1 != 0) {
                int i2 = l4 * l2 + k4 * i3 >> 15;
                l4 = l4 * i3 - k4 * l2 >> 15;
                k4 = i2;
            }
            if (i1 != 0) {
                int j2 = i5 * l3 + k4 * i4 >> 15;
                i5 = i5 * i4 - k4 * l3 >> 15;
                k4 = j2;
            }
            if (l != 0) {
                int k2 = l4 * k3 - i5 * j3 >> 15;
                i5 = l4 * j3 + i5 * k3 >> 15;
                l4 = k2;
            }
            if (i5 >= l1)
                anIntArray230[j4] = (k4 << k1) / i5;
            else
                anIntArray230[j4] = k4 << k1;
            if (i5 >= l1)
                anIntArray231[j4] = (l4 << k1) / i5;
            else
                anIntArray231[j4] = l4 << k1;
            anIntArray227[j4] = k4;
            anIntArray228[j4] = l4;
            anIntArray229[j4] = i5;
        }

    }

    public void method202() {
        method200();
        for (int i = 0; i < anInt226; i++) {
            anIntArray272[i] = anIntArray275[i];
            anIntArray273[i] = anIntArray276[i];
            anIntArray274[i] = anIntArray277[i];
        }

        anInt286 = anInt287 = anInt288 = 0;
        anInt289 = anInt290 = anInt291 = 0;
        anInt292 = anInt293 = anInt294 = 256;
        anInt295 = anInt296 = anInt297 = anInt298 = anInt299 = anInt300 = 256;
        anInt301 = 0;
    }

    public Model method203() {
        Model models[] = new Model[1];
        models[0] = this;
        Model model = new Model(models, 1);
        model.anInt245 = anInt245;
        model.isGiantCrystal = isGiantCrystal;
        return model;
    }

    public Model method204(boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        Model models[] = new Model[1];
        models[0] = this;
        Model model = new Model(models, 1, flag, flag1, flag2, flag3);
        model.anInt245 = anInt245;
        return model;
    }

    public void method205(Model model) {
        anInt289 = model.anInt289;
        anInt290 = model.anInt290;
        anInt291 = model.anInt291;
        anInt286 = model.anInt286;
        anInt287 = model.anInt287;
        anInt288 = model.anInt288;
        method192();
        anInt246 = 1;
    }

    public int method206(byte abyte0[]) {
        for (; abyte0[anInt309] == 10 || abyte0[anInt309] == 13; anInt309++)
            ;
        int i = anIntArray268[abyte0[anInt309++] & 0xff];
        int j = anIntArray268[abyte0[anInt309++] & 0xff];
        int k = anIntArray268[abyte0[anInt309++] & 0xff];
        int l = (i * 4096 + j * 64 + k) - 0x20000;
        if (l == 0x1e240)
            l = anInt270;
        return l;
    }

    public int anInt226;
    public int anIntArray227[];
    public int anIntArray228[];
    public int anIntArray229[];
    public int anIntArray230[];
    public int anIntArray231[];
    public int anIntArray232[];
    public byte aByteArray233[];
    public int anInt234;
    public int anIntArray235[];
    public int anIntArrayArray236[][];
    public int anIntArray237[];
    public int anIntArray238[];
    public int anIntArray239[];
    public int anIntArray240[];
    public int anIntArray241[];
    public int anIntArray242[];
    public int anIntArray243[];
    private int anIntArray244[];
    public int anInt245;
    public int anInt246;
    public boolean aBoolean247;
    public int anInt248;
    public int anInt249;
    public int anInt250;
    public int anInt251;
    public int anInt252;
    public int anInt253;
    public boolean aBoolean254;
    public boolean aBoolean255;
    public boolean isGiantCrystal;
    public int anInt257;
    public int anIntArray258[];
    public byte aByteArray259[];
    private boolean aBoolean260;
    public boolean aBoolean261;
    public boolean aBoolean262;
    public boolean aBoolean263;
    public boolean aBoolean264;
    private static int anIntArray265[];
    private static int anIntArray266[];
    private static byte aByteArray267[];
    private static int anIntArray268[];
    private int anInt270;
    public int anInt271;
    public int anIntArray272[];
    public int anIntArray273[];
    public int anIntArray274[];
    public int anIntArray275[];
    public int anIntArray276[];
    public int anIntArray277[];
    private int anInt278;
    private int anIntArrayArray279[][];
    private int anIntArray280[];
    private int anIntArray281[];
    private int anIntArray282[];
    private int anIntArray283[];
    private int anIntArray284[];
    private int anIntArray285[];
    private int anInt286;
    private int anInt287;
    private int anInt288;
    private int anInt289;
    private int anInt290;
    private int anInt291;
    private int anInt292;
    private int anInt293;
    private int anInt294;
    private int anInt295;
    private int anInt296;
    private int anInt297;
    private int anInt298;
    private int anInt299;
    private int anInt300;
    private int anInt301;
    private int anInt302;
    private int anInt303;
    private int anInt304;
    private int anInt305;
    private int anInt306;
    protected int anInt307;
    protected int anInt308;
    private int anInt309;

    static {
        anIntArray265 = new int[512];
        anIntArray266 = new int[2048];
        aByteArray267 = new byte[64];
        anIntArray268 = new int[256];
        for (int i = 0; i < 256; i++) {
            anIntArray265[i] = (int) (Math.sin((double) i * 0.02454369D) * 32768D);
            anIntArray265[i + 256] = (int) (Math.cos((double) i * 0.02454369D) * 32768D);
        }

        for (int j = 0; j < 1024; j++) {
            anIntArray266[j] = (int) (Math.sin((double) j * 0.00613592315D) * 32768D);
            anIntArray266[j + 1024] = (int) (Math.cos((double) j * 0.00613592315D) * 32768D);
        }

        for (int k = 0; k < 10; k++)
            aByteArray267[k] = (byte) (48 + k);

        for (int l = 0; l < 26; l++)
            aByteArray267[l + 10] = (byte) (65 + l);

        for (int i1 = 0; i1 < 26; i1++)
            aByteArray267[i1 + 36] = (byte) (97 + i1);

        aByteArray267[62] = -93;
        aByteArray267[63] = 36;
        for (int j1 = 0; j1 < 10; j1++)
            anIntArray268[48 + j1] = j1;

        for (int k1 = 0; k1 < 26; k1++)
            anIntArray268[65 + k1] = k1 + 10;

        for (int l1 = 0; l1 < 26; l1++)
            anIntArray268[97 + l1] = l1 + 36;

        anIntArray268[163] = 62;
        anIntArray268[36] = 63;
    }
}
