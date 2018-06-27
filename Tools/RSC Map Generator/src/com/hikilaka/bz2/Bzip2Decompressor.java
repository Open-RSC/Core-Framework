package com.hikilaka.bz2;

public final class Bzip2Decompressor {

	public static int unpackData(byte[] buffer, int decmp_len, byte[] arc, int cmp_len, int arc_offset) {
		BZip2Entry entry = new BZip2Entry();
		entry.archive = arc;
		entry.next_in = arc_offset;
		entry.buffer = buffer;
		entry.buffer_offset = 0;
		entry.avail_in = cmp_len;
		entry.decmp_len = decmp_len;
		entry.bsLive = 0;
		entry.bsBuff = 0;
		entry.total_in_lo32 = 0;
		entry.total_in_hi32 = 0;
		entry.aem = 0;
		entry.aen = 0;
		entry.currBlockNo = 0;
		get_and_move_to_front_decode(entry);
		decmp_len -= entry.decmp_len;
		return decmp_len;
	}

	private static void glk(BZip2Entry entry) {
		byte byte4 = entry.afa;
		int i = entry.afb;
		int j = entry.afl;
		int k = entry.afj;
		int limit[] = BZip2Entry.limit_last;
		int l = entry.afi;
		byte buffer[] = entry.buffer;
		int buffer_idx = entry.buffer_offset;
		int decmp_len = entry.decmp_len;
		int demp_2 = decmp_len;
		int l1 = entry.aha + 1;
		label0:
			do {
				if(i > 0) {
					do {
						if (decmp_len == 0)
							break label0;
						if (i == 1)
							break;
						buffer[buffer_idx] = byte4;
						i--;
						buffer_idx++;
						decmp_len--;
					} while (true);
					if (decmp_len == 0) {
						i = 1;
						break;
					}
					buffer[buffer_idx] = byte4;
					buffer_idx++;
					decmp_len--;
				}
				boolean flag = true;
				while(flag)  {
					flag = false;
					if(j == l1) {
						i = 0;
						break label0;
					}
					byte4 = (byte)k;
					l = limit[l];
					byte byte0 = (byte)(l & 0xff);
					l >>= 8;
					j++;
					if(byte0 != k) {
						k = byte0;
						if(decmp_len == 0) {
							i = 1;
						} else {
							buffer[buffer_idx] = byte4;
							buffer_idx++;
							decmp_len--;
							flag = true;
							continue;
						}
						break label0;
					}
					if(j != l1)
						continue;
					if(decmp_len == 0) {
						i = 1;
						break label0;
					}
					buffer[buffer_idx] = byte4;
					buffer_idx++;
					decmp_len--;
					flag = true;
				}
				i = 2;
				l = limit[l];
				byte byte1 = (byte)(l & 0xff);
				l >>= 8;
				if(++j != l1)
					if(byte1 != k) {
						k = byte1;
					} else {
						i = 3;
						l = limit[l];
						byte byte2 = (byte)(l & 0xff);
						l >>= 8;
						if(++j != l1)
							if(byte2 != k) {
								k = byte2;
							} else {
								l = limit[l];
								byte byte3 = (byte)(l & 0xff);
								l >>= 8;
								j++;
								i = (byte3 & 0xff) + 4;
								l = limit[l];
								k = (byte)(l & 0xff);
								l >>= 8;
								j++;
							}
					}
			} while(true);
		int i2 = entry.aem;
		entry.aem += demp_2 - decmp_len;
		if(entry.aem < i2)
			entry.aen++;
		entry.afa = byte4;
		entry.afb = i;
		entry.afl = j;
		entry.afj = k;
		BZip2Entry.limit_last = limit;
		entry.afi = l;
		entry.buffer = buffer;
		entry.buffer_offset = buffer_idx;
		entry.decmp_len = decmp_len;
	}

	private static void get_and_move_to_front_decode(BZip2Entry entry) {
		int min_len_zt = 0;
		int limit_zt[] = null;
		int base_zt[] = null;
		int perm_zt[] = null;
		entry.block_size = 1;
		if(BZip2Entry.limit_last == null)
			BZip2Entry.limit_last = new int[entry.block_size * 0x186a0];
		boolean ran_bool = true;
		while(ran_bool)  {
			byte ran_byte = bs_read_ubyte(entry);
			if(ran_byte == 23)
				return;
			ran_byte = bs_read_ubyte(entry);
			ran_byte = bs_read_ubyte(entry);
			ran_byte = bs_read_ubyte(entry);
			ran_byte = bs_read_ubyte(entry);
			ran_byte = bs_read_ubyte(entry);
			entry.currBlockNo++;
			ran_byte = bs_read_ubyte(entry);
			ran_byte = bs_read_ubyte(entry);
			ran_byte = bs_read_ubyte(entry);
			ran_byte = bs_read_ubyte(entry);
			ran_byte = bs_read(entry);
			if(ran_byte != 0)
				entry.blockRandomized = true;
			else
				entry.blockRandomized = false;
			if(entry.blockRandomized)
				System.out.println("PANIC! RANDOMISED BLOCK!");
			entry.origPtr = 0;
			ran_byte = bs_read_ubyte(entry);
			entry.origPtr = entry.origPtr << 8 | ran_byte & 0xff;
			ran_byte = bs_read_ubyte(entry);
			entry.origPtr = entry.origPtr << 8 | ran_byte & 0xff;
			ran_byte = bs_read_ubyte(entry);
			entry.origPtr = entry.origPtr << 8 | ran_byte & 0xff;

			for(int i = 0; i < 16; i++) {
				byte bit = bs_read(entry);
				if(bit == 1)
					entry.in_use_16[i] = true;
				else
					entry.in_use_16[i] = false;
			}

			for(int i = 0; i < 256; i++)
				entry.in_use[i] = false;

			for(int i = 0; i < 16; i++)
				if(entry.in_use_16[i]) {
					for(int j = 0; j < 16; j++) {
						byte bit = bs_read(entry);
						if(bit == 1)
							entry.in_use[i * 16 + j] = true;
					}

				}

			make_maps(entry);
			int alpha_size = entry.in_use_shadow + 2;
			int n_groups = bs_read(3, entry);
			int n_selectors = bs_read(15, entry);
			for(int i = 0; i < n_selectors; i++) {
				int j = 0;
				do {
					byte bit = bs_read(entry);
					if(bit == 0)
						break;
					j++;
				} while(true);
				entry.selector_mtf[i] = (byte) j;
			} 

			byte pos[] = new byte[6];
			/* Undo the MTF values for the selectors. */
			for(byte v = 0; v < n_groups; v++)
				pos[v] = v;

			for(int i = 0; i < n_selectors; i++) {
				byte v = entry.selector_mtf[i];
				byte tmp = pos[v];
				for(; v > 0; v--)
					// nearly all times v is zero, 4 in most other cases
					pos[v] = pos[v - 1];

				pos[0] = tmp;
				entry.selector[i] = tmp;
			}
			//here i think
			for(int t = 0; t < n_groups; t++) {
				int cur = bs_read(5, entry);
				for(int i = 0; i < alpha_size; i++) {
					do {
						byte bit = bs_read(entry);
						if(bit == 0)
							break;
						bit = bs_read(entry);
						if(bit == 0)
							cur++;
						else
							cur--;
					} while(true);
					entry.len[t][i] = (byte) cur;
				}
			}

			//creates huffman encoding tables
			for(int t = 0; t < n_groups; t++) {
				byte min_len = 32;
				int max_len = 0;
				for(int i = 0; i < alpha_size; i++) {
					if(entry.len[t][i] > max_len) max_len = entry.len[t][i];
					if(entry.len[t][i] < min_len) min_len = entry.len[t][i];
				}

				hb_create_decode_tables(entry.limit[t], entry.base[t], entry.perm[t], entry.len[t], min_len, max_len, alpha_size);
				entry.min_lengths[t] = min_len;
			}

			int in_shadow = entry.in_use_shadow + 1;
			int group_no = -1;
			int group_pos = 0;
			for(int i = 0; i <= 255; i++)
				entry.unzftab[i] = 0;

			int kk = 4095;
			for(int i = 15; i >= 0; i--) {
				for(int j = 15; j >= 0; j--) {
					entry.mtfa[kk] = (byte) (i * 16 + j);
					kk--;
				}
				entry.mtfbase[i] = kk + 1;
			}

			int s = 0;
			if(group_pos == 0) {
				group_no++;
				group_pos = 50;
				byte zt = entry.selector[group_no];
				min_len_zt = entry.min_lengths[zt];
				limit_zt = entry.limit[zt];
				perm_zt = entry.perm[zt];
				base_zt = entry.base[zt];
			}
			group_pos--;
			int es = min_len_zt;
			int l7;
			byte uc;
			for(l7 = bs_read(es, entry); l7 > limit_zt[es]; l7 = l7 << 1 | uc) {
				es++;
				uc = bs_read(entry);
			}

			for(int zvec = perm_zt[l7 - base_zt[es]]; zvec != in_shadow;)
				if(zvec == 0 || zvec == 1) {
					int last_shadow = -1;
					int k6 = 1;
					do {
						if(zvec == 0)
							last_shadow += k6;
						else
							if(zvec == 1)
								last_shadow += 2 * k6;
						k6 *= 2;
						if(group_pos == 0) {
							group_no++;
							group_pos = 50;
							byte byte13 = entry.selector[group_no];
							min_len_zt = entry.min_lengths[byte13];
							limit_zt = entry.limit[byte13];
							perm_zt = entry.perm[byte13];
							base_zt = entry.base[byte13];
						}
						group_pos--;
						int j7 = min_len_zt;
						int i8;
						byte byte10;
						for(i8 = bs_read(j7, entry); i8 > limit_zt[j7]; i8 = i8 << 1 | byte10) {
							j7++;
							byte10 = bs_read(entry);
						}

						zvec = perm_zt[i8 - base_zt[j7]];
					} while(zvec == 0 || zvec == 1);
					last_shadow++;
					byte ch = entry.seq_to_unseq[entry.mtfa[entry.mtfbase[0]] & 0xff];
					entry.unzftab[ch & 0xff] += last_shadow;
					for(; last_shadow > 0; last_shadow--) {
						BZip2Entry.limit_last[s] = ch & 0xff;
						s++;
					}

				} else {
					int next_sym = zvec - 1;
					byte tmp;
					if(next_sym < 16) {
						int one = entry.mtfbase[0];
						tmp = entry.mtfa[one + next_sym];
						for(; next_sym > 3; next_sym -= 4) {
							int j = one + next_sym;
							entry.mtfa[j] = entry.mtfa[j - 1];
							entry.mtfa[j - 1] = entry.mtfa[j - 2];
							entry.mtfa[j - 2] = entry.mtfa[j - 3];
							entry.mtfa[j - 3] = entry.mtfa[j - 4];
						}

						for(; next_sym > 0; next_sym--)
							entry.mtfa[one + next_sym] = entry.mtfa[(one + next_sym) - 1];

						entry.mtfa[one] = tmp;
					} else {
						int l10 = next_sym / 16;
						int i11 = next_sym % 16;
						int k10 = entry.mtfbase[l10] + i11;
						tmp = entry.mtfa[k10];
						for(; k10 > entry.mtfbase[l10]; k10--)
							entry.mtfa[k10] = entry.mtfa[k10 - 1];

						entry.mtfbase[l10]++;
						for(; l10 > 0; l10--) {
							entry.mtfbase[l10]--;
							entry.mtfa[entry.mtfbase[l10]] = entry.mtfa[(entry.mtfbase[l10 - 1] + 16) - 1];
						}

						entry.mtfbase[0]--;
						entry.mtfa[entry.mtfbase[0]] = tmp;
						if(entry.mtfbase[0] == 0) {
							int ran_int = 4095;
							for(int k9 = 15; k9 >= 0; k9--) {
								for(int l9 = 15; l9 >= 0; l9--) {
									entry.mtfa[ran_int] = entry.mtfa[entry.mtfbase[k9] + l9];
									ran_int--;
								}

								entry.mtfbase[k9] = ran_int + 1;
							}

						}
					}
					entry.unzftab[entry.seq_to_unseq[tmp & 0xff] & 0xff]++;
					BZip2Entry.limit_last[s] = entry.seq_to_unseq[tmp & 0xff] & 0xff;
					s++;
					if(group_pos == 0) {
						group_no++;
						group_pos = 50;
						byte zt = entry.selector[group_no];
						min_len_zt = entry.min_lengths[zt];
						limit_zt = entry.limit[zt];
						perm_zt = entry.perm[zt];
						base_zt = entry.base[zt];
					}
					group_pos--;
					int zm = min_len_zt;
					int bs_live_shadow;
					byte thebyte;
					for(bs_live_shadow = bs_read(zm, entry); bs_live_shadow > limit_zt[zm]; bs_live_shadow = bs_live_shadow << 1 | thebyte) {
						zm++;
						thebyte = bs_read(entry);
					}

					zvec = perm_zt[bs_live_shadow - base_zt[zm]];
				}

			entry.afb = 0;
			entry.afa = 0;
			entry.ctftab[0] = 0;
			for(int i = 1; i <= 256; i++)
				entry.ctftab[i] = entry.unzftab[i - 1];

			for(int i = 1; i <= 256; i++)
				entry.ctftab[i] += entry.ctftab[i - 1];

			for(int i = 0; i < s; i++) {
				byte ch = (byte)(BZip2Entry.limit_last[i] & 0xff);
				BZip2Entry.limit_last[entry.ctftab[ch & 0xff]] |= i << 8;
				entry.ctftab[ch & 0xff]++;
			}

			entry.afi = BZip2Entry.limit_last[entry.origPtr] >> 8;
				entry.afl = 0;
				entry.afi = BZip2Entry.limit_last[entry.afi];
				entry.afj = (byte)(entry.afi & 0xff);
				entry.afi >>= 8;
			entry.afl++;
			entry.aha = s;
			glk(entry);
			if(entry.afl == entry.aha + 1 && entry.afb == 0)
				ran_bool = true;
			else
				ran_bool = false;
		}
	}

	private static byte bs_read_ubyte(BZip2Entry entry) {
		return (byte) bs_read(8, entry);
	}

	private static byte bs_read(BZip2Entry entry) {
		return (byte) bs_read(1, entry);
	}

	private static int bs_read(int n, BZip2Entry entry) {
		int value;
		do {
			if(entry.bsLive >= n) {
				int thech = entry.bsBuff >> entry.bsLive - n & (1 << n) - 1;
					entry.bsLive -= n;
					value = thech;
					break;
			}
			entry.bsBuff = entry.bsBuff << 8 | entry.archive[entry.next_in] & 0xff;
			entry.bsLive += 8;
			entry.next_in++;
			entry.avail_in--;
			entry.total_in_lo32++;
			if(entry.total_in_lo32 == 0)
				entry.total_in_hi32++;
		} while(true);
		return value;
	}

	private static void make_maps(BZip2Entry entry) {
		entry.in_use_shadow = 0;
		for(int i = 0; i < 256; i++) {
			if(entry.in_use[i]) {
				entry.seq_to_unseq[entry.in_use_shadow] = (byte) i;
				entry.in_use_shadow++;
			}
		}
	}

	private static void hb_create_decode_tables(int[] limit, int[] base, int[] perm, byte length[], int min_len, int max_len, int alpha_size) {
		for (int i = min_len, pp = 0; i <= max_len; i++) {
			for (int j = 0; j < alpha_size; j++) {
				if (length[j] == i) {
					perm[pp++] = j;
				}
			}
		}

		for(int i = 0; i < 23; i++) {
			base[i] = 0;
			limit[i] = 0;
		}

		for(int i = 0; i < alpha_size; i++) {
			base[length[i] + 1]++;
		}

		for(int i = 1; i < 23; i++) {
			base[i] += base[i - 1];
		}

		int vec = 0;
		for(int i = min_len; i <= max_len; i++) {
			vec += base[i + 1] - base[i];
			limit[i] = vec - 1;
			vec <<= 1;
		}

		for(int i = min_len + 1; i <= max_len; i++) {
			base[i] = (limit[i - 1] + 1 << 1) - base[i];
		}
	}

}
