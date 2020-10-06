/**
 * rscminus
 *
 * This file was part of rscminus.
 *
 * rscminus is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * rscminus is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with rscminus. If not,
 * see <http://www.gnu.org/licenses/>.
 *
 * Authors: see <https://github.com/RSCPlus/rscminus>
 */

package com.openrsc.server.login;

/**
 * <p>An implementation of an ISAAC cipher. See
 * <a href="http://en.wikipedia.org/wiki/ISAAC_(cipher)">
 * http://en.wikipedia.org/wiki/ISAAC_(cipher)</a> for more information.</p>
 *
 * <p>This implementation is based on the one written by Bob Jenkins, which is
 * available at <a href="http://www.burtleburtle.net/bob/java/rand/Rand.java">
 * http://www.burtleburtle.net/bob/java/rand/Rand.java</a>.</p>
 * @author Graham Edgecombe
 */
public class ISAACCipher {

    /**
     * The golden ratio.
     */
    public static final int RATIO = 0x9e3779b9;

    /**
     * The log of the size of the results and memory arrays.
     */
    public static final int SIZE_LOG = 8;

    /**
     * The size of the results and memory arrays.
     */
    public static final int SIZE = 1 << SIZE_LOG;

    /**
     * For pseudorandom lookup.
     */
    public static final int MASK = (SIZE - 1) << 2;

    /**
     * The count through the results.
     */
    private int count = 0;

    /**
     * The results.
     */
    private int results[] = new int[SIZE];

    /**
     * The internal memory state.
     */
    private int memory[] = new int[SIZE];

    /**
     * The accumulator.
     */
    private int a;

    /**
     * The last result.
     */
    private int b;

    /**
     * The counter.
     */
    private int c;

    /**
     * Changes the ISAAC cipher key.
     * @param seed The seed.
     */
    public void setKeys(int[] seed) {
        System.out.print("Setting keys: ");
        for(int i = 0; i < seed.length; i++) {
            System.out.print(String.format("%d ", seed[i]));
            results[i] = seed[i];
        }
        System.out.println();
        init(true);
    }

    /**
     * Resets the ISAAC cipher
     */
    public void reset() {
        for (int i = 0; i < memory.length; i++)
            memory[i] = 0x00;
        for (int i = 0; i < results.length; i++)
            results[i] = 0x00;
        a = 0;
        b = 0;
        c = 0;
    }

    /**
     * Gets the next value.
     * @return The next value.
     */
    public int getNextValue() {
        if(count-- == 0) {
            isaac();
            count = SIZE - 1;
        }
        return results[count];
    }

    /**
     * Generates 256 results.
     */
    public void isaac() {
        int i, j, x, y;
        b += ++c;
        for(i = 0, j = SIZE / 2; i < SIZE / 2;) {
            x = memory[i];
            a ^= a << 13;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a >>> 6;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a << 2;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a >>> 16;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;
        }
        for(j = 0; j < SIZE / 2;) {
            x = memory[i];
            a ^= a << 13;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a >>> 6;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a << 2;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a >>> 16;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;
        }
    }

    /**
     * Initialises the ISAAC.
     * @param flag Flag indicating if we should perform a second pass.
     */
    public void init(boolean flag) {
        int i;
        int a, b, c, d, e, f, g, h;
        a = b = c = d = e = f = g = h = RATIO;
        for(i = 0; i < 4; ++i) {
            a ^= b << 11;
            d += a;
            b += c;
            b ^= c >>> 2;
            e += b;
            c += d;
            c ^= d << 8;
            f += c;
            d += e;
            d ^= e >>> 16;
            g += d;
            e += f;
            e ^= f << 10;
            h += e;
            f += g;
            f ^= g >>> 4;
            a += f;
            g += h;
            g ^= h << 8;
            b += g;
            h += a;
            h ^= a >>> 9;
            c += h;
            a += b;
        }
        for(i = 0; i < SIZE; i += 8) {
            if(flag) {
                a += results[i];
                b += results[i + 1];
                c += results[i + 2];
                d += results[i + 3];
                e += results[i + 4];
                f += results[i + 5];
                g += results[i + 6];
                h += results[i + 7];
            }
            a ^= b << 11;
            d += a;
            b += c;
            b ^= c >>> 2;
            e += b;
            c += d;
            c ^= d << 8;
            f += c;
            d += e;
            d ^= e >>> 16;
            g += d;
            e += f;
            e ^= f << 10;
            h += e;
            f += g;
            f ^= g >>> 4;
            a += f;
            g += h;
            g ^= h << 8;
            b += g;
            h += a;
            h ^= a >>> 9;
            c += h;
            a += b;
            memory[i] = a;
            memory[i + 1] = b;
            memory[i + 2] = c;
            memory[i + 3] = d;
            memory[i + 4] = e;
            memory[i + 5] = f;
            memory[i + 6] = g;
            memory[i + 7] = h;
        }
        if(flag) {
            for(i = 0; i < SIZE; i += 8) {
                a += memory[i];
                b += memory[i + 1];
                c += memory[i + 2];
                d += memory[i + 3];
                e += memory[i + 4];
                f += memory[i + 5];
                g += memory[i + 6];
                h += memory[i + 7];
                a ^= b << 11;
                d += a;
                b += c;
                b ^= c >>> 2;
                e += b;
                c += d;
                c ^= d << 8;
                f += c;
                d += e;
                d ^= e >>> 16;
                g += d;
                e += f;
                e ^= f << 10;
                h += e;
                f += g;
                f ^= g >>> 4;
                a += f;
                g += h;
                g ^= h << 8;
                b += g;
                h += a;
                h ^= a >>> 9;
                c += h;
                a += b;
                memory[i] = a;
                memory[i + 1] = b;
                memory[i + 2] = c;
                memory[i + 3] = d;
                memory[i + 4] = e;
                memory[i + 5] = f;
                memory[i + 6] = g;
                memory[i + 7] = h;
            }
        }
        isaac();
        count = SIZE;
    }

}
