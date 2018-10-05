package com.hikilaka.bz2;


class BZip2Entry {

	public static int limit_last[];

	int[] unzftab = new int[256]; //done
	int[] ctftab = new int[257]; //done

	boolean[] in_use = new boolean[256]; //done
	boolean in_use_16[] = new boolean[16]; //done

	byte[] seq_to_unseq = new byte[256]; //done
	byte[] mtfa = new byte[4096]; //done
	int[] mtfbase = new int[16]; //done

	byte[] selector = new byte[18002]; //done
	byte[] selector_mtf = new byte[18002]; //done

	byte[][] len = new byte[6][258]; //done
	int[][] limit = new int[6][258]; //done
	int[][] base = new int[6][258]; //done
	int[][] perm = new int[6][258]; //done
	int[] min_lengths = new int[6]; //done

	byte archive[]; //done
	int next_in; //done
	int avail_in; //done

	byte buffer[]; //done
	int buffer_offset; //done
	int decmp_len; //done

	int bsBuff;
	int bsLive;
	int block_size;
	int in_use_shadow;
	boolean blockRandomized;
	byte afa;
	int total_in_lo32;
	int total_in_hi32;
	int aem;
	int aen;
	int afb;
	int currBlockNo;
	int origPtr;
	int afi;
	int afj;
	int afl;
	int aha;
}
