package orsc.util.bzip;

import orsc.MiscFunctions;

public final class BZLib {
	private static final BZState sharedBlock = new BZState();

	private static void decompress(BZState state) {
		int var22 = 0;
		int[] var23 = null;
		int[] var24 = null;
		int[] var25 = null;
		state.m_f = 1;
		if (MiscFunctions.graphics_s_Mb == null) {
			MiscFunctions.graphics_s_Mb = new int[state.m_f * 100000];
		}

		boolean var26 = true;

		while (true) {
			while (var26) {
				byte var1 = getUChar(state);
				if (var1 == 23) {
					return;
				}

				var1 = getUChar(state);
				var1 = getUChar(state);
				var1 = getUChar(state);
				var1 = getUChar(state);
				var1 = getUChar(state);
				var1 = getUChar(state);
				var1 = getUChar(state);
				var1 = getUChar(state);
				var1 = getUChar(state);
				var1 = getBit(state);
				if (var1 != 0) {
					;
				}

				state.origPtr = 0;
				var1 = getUChar(state);
				state.origPtr = state.origPtr << 8 | var1 & 255;
				var1 = getUChar(state);
				state.origPtr = state.origPtr << 8 | var1 & 255;
				var1 = getUChar(state);
				state.origPtr = state.origPtr << 8 | var1 & 255;

				int var35;
				for (var35 = 0; var35 < 16; ++var35) {
					var1 = getBit(state);
					state.inUse_16[var35] = var1 == 1;
				}

				for (var35 = 0; var35 < 256; ++var35) {
					state.inUse[var35] = false;
				}

				int var36;
				for (var35 = 0; var35 < 16; ++var35) {
					if (state.inUse_16[var35]) {
						for (var36 = 0; var36 < 16; ++var36) {
							var1 = getBit(state);
							if (var1 == 1) {
								state.inUse[var35 * 16 + var36] = true;
							}
						}
					}
				}

				makeMaps(state);
				int alphaSize = state.nInUse + 2;
				int var39 = getBits(3, state);
				int var40 = getBits(15, state);

				for (var35 = 0; var35 < var40; ++var35) {
					var36 = 0;

					while (true) {
						var1 = getBit(state);
						if (var1 == 0) {
							state.sectorMtf[var35] = (byte) var36;
							break;
						}

						++var36;
					}
				}

				byte[] var27 = new byte[6];

				byte var29;
				for (var29 = 0; var29 < var39; var27[var29] = var29++) {
					;
				}

				for (var35 = 0; var35 < var40; ++var35) {
					var29 = state.sectorMtf[var35];

					byte var28;
					for (var28 = var27[var29]; var29 > 0; --var29) {
						var27[var29] = var27[var29 - 1];
					}

					var27[0] = var28;
					state.selector[var35] = var28;
				}

				int var37;
				for (var37 = 0; var37 < var39; ++var37) {
					int var49 = getBits(5, state);

					for (var35 = 0; var35 < alphaSize; ++var35) {
						while (true) {
							var1 = getBit(state);
							if (var1 == 0) {
								state.len[var37][var35] = (byte) var49;
								break;
							}

							var1 = getBit(state);
							if (var1 == 0) {
								++var49;
							} else {
								--var49;
							}
						}
					}
				}

				for (var37 = 0; var37 < var39; ++var37) {
					byte minLen = 32;
					byte maxLen = 0;

					for (var35 = 0; var35 < alphaSize; ++var35) {
						if (state.len[var37][var35] > maxLen) {
							maxLen = state.len[var37][var35];
						}

						if (state.len[var37][var35] < minLen) {
							minLen = state.len[var37][var35];
						}
					}

					createDecodeTables(state.limit[var37], state.base[var37], state.perm[var37], state.len[var37],
						minLen, maxLen, alphaSize);
					state.minLens[var37] = minLen;
				}

				int var41 = state.nInUse + 1;
				int var42 = -1;
				byte var43 = 0;

				for (var35 = 0; var35 <= 255; ++var35) {
					state.unzftab[var35] = 0;
				}

				int var56 = 4095;

				int var54;
				int var55;
				for (var54 = 15; var54 >= 0; --var54) {
					for (var55 = 15; var55 >= 0; --var55) {
						state.mtfa[var56] = (byte) (var54 * 16 + var55);
						--var56;
					}

					state.mtfbase[var54] = var56 + 1;
				}

				int var46 = 0;
				byte var53;
				++var42;
				var43 = 50;
				var53 = state.selector[var42];
				var22 = state.minLens[var53];
				var23 = state.limit[var53];
				var25 = state.perm[var53];
				var24 = state.base[var53];

				int var44 = var43 - 1;
				int var50 = var22;

				int var51;
				byte var52;
				for (var51 = getBits(var22, state); var51 > var23[var50]; var51 = var51 << 1 | var52) {
					++var50;
					var52 = getBit(state);
				}

				int var45 = var25[var51 - var24[var50]];

				while (true) {
					while (var45 != var41) {
						if (var45 != 0 && var45 != 1) {
							int var33 = var45 - 1;
							int var30;
							if (var33 < 16) {
								var30 = state.mtfbase[0];

								for (var1 = state.mtfa[var30 + var33]; var33 > 3; var33 -= 4) {
									int var34 = var30 + var33;
									state.mtfa[var34] = state.mtfa[var34 - 1];
									state.mtfa[var34 - 1] = state.mtfa[var34 - 2];
									state.mtfa[var34 - 2] = state.mtfa[var34 - 3];
									state.mtfa[var34 - 3] = state.mtfa[var34 - 4];
								}

								while (var33 > 0) {
									state.mtfa[var30 + var33] = state.mtfa[var30 + var33 - 1];
									--var33;
								}

								state.mtfa[var30] = var1;
							} else {
								int var31 = var33 / 16;
								int var32 = var33 % 16;
								var30 = state.mtfbase[var31] + var32;

								for (var1 = state.mtfa[var30]; var30 > state.mtfbase[var31]; --var30) {
									state.mtfa[var30] = state.mtfa[var30 - 1];
								}

								++state.mtfbase[var31];

								while (var31 > 0) {
									--state.mtfbase[var31];
									state.mtfa[state.mtfbase[var31]] = state.mtfa[state.mtfbase[var31 - 1] + 16 - 1];
									--var31;
								}

								--state.mtfbase[0];
								state.mtfa[state.mtfbase[0]] = var1;
								if (state.mtfbase[0] == 0) {
									var56 = 4095;

									for (var54 = 15; var54 >= 0; --var54) {
										for (var55 = 15; var55 >= 0; --var55) {
											state.mtfa[var56] = state.mtfa[state.mtfbase[var54] + var55];
											--var56;
										}

										state.mtfbase[var54] = var56 + 1;
									}
								}
							}

							++state.unzftab[state.setToUnseq[var1 & 255] & 255];
							MiscFunctions.graphics_s_Mb[var46] = state.setToUnseq[var1 & 255] & 255;
							++var46;
							if (var44 == 0) {
								++var42;
								var44 = 50;
								var53 = state.selector[var42];
								var22 = state.minLens[var53];
								var23 = state.limit[var53];
								var25 = state.perm[var53];
								var24 = state.base[var53];
							}

							--var44;
							var50 = var22;

							for (var51 = getBits(var22, state); var51 > var23[var50]; var51 = var51 << 1 | var52) {
								++var50;
								var52 = getBit(state);
							}

							var45 = var25[var51 - var24[var50]];
						} else {
							int var47 = -1;
							int var48 = 1;

							do {
								if (var45 == 0) {
									var47 += var48;
								} else {
									var47 += var48 * 2;
								}

								var48 *= 2;
								if (var44 == 0) {
									++var42;
									var44 = 50;
									var53 = state.selector[var42];
									var22 = state.minLens[var53];
									var23 = state.limit[var53];
									var25 = state.perm[var53];
									var24 = state.base[var53];
								}

								--var44;
								var50 = var22;

								for (var51 = getBits(var22, state); var51 > var23[var50]; var51 = var51 << 1 | var52) {
									++var50;
									var52 = getBit(state);
								}

								var45 = var25[var51 - var24[var50]];
							} while (var45 == 0 || var45 == 1);

							++var47;
							var1 = state.setToUnseq[state.mtfa[state.mtfbase[0]] & 255];

							for (state.unzftab[var1 & 255] += var47; var47 > 0; --var47) {
								MiscFunctions.graphics_s_Mb[var46] = var1 & 255;
								++var46;
							}
						}
					}

					state.stateOutLen = 0;
					state.stateOutCh = 0;
					state.cftab[0] = 0;

					for (var35 = 1; var35 <= 256; ++var35) {
						state.cftab[var35] = state.unzftab[var35 - 1];
					}

					for (var35 = 1; var35 <= 256; ++var35) {
						state.cftab[var35] += state.cftab[var35 - 1];
					}

					for (var35 = 0; var35 < var46; ++var35) {
						var1 = (byte) (MiscFunctions.graphics_s_Mb[var35] & 255);
						MiscFunctions.graphics_s_Mb[state.cftab[var1 & 255]] |= var35 << 8;
						++state.cftab[var1 & 255];
					}

					state.tpos = MiscFunctions.graphics_s_Mb[state.origPtr] >> 8;
					state.nblockUsed = 0;
					state.tpos = MiscFunctions.graphics_s_Mb[state.tpos];
					state.k0 = (byte) (state.tpos & 255);
					state.tpos >>= 8;
					++state.nblockUsed;
					state.saveNblock = var46;
					nextHeader(state);
					if (state.nblockUsed == state.saveNblock + 1 && state.stateOutLen == 0) {
						var26 = true;
						break;
					}

					var26 = false;
					break;
				}
			}

			return;
		}
	}

	private static byte getUChar(BZState var0) {
		return (byte) getBits(8, var0);
	}

	private static byte getBit(BZState var0) {
		return (byte) getBits(1, var0);
	}

	private static void makeMaps(BZState var0) {
		var0.nInUse = 0;

		for (int var1 = 0; var1 < 256; ++var1) {
			if (var0.inUse[var1]) {
				var0.setToUnseq[var0.nInUse] = (byte) var1;
				++var0.nInUse;
			}
		}

	}

	private static void createDecodeTables(int[] limit, int[] base, int[] perm, byte[] length, int minLen,
										   int maxLen, int alphaSize) {
		int var7 = 0;

		int var8;
		for (var8 = minLen; var8 <= maxLen; ++var8) {
			for (int var9 = 0; var9 < alphaSize; ++var9) {
				if (length[var9] == var8) {
					perm[var7] = var9;
					++var7;
				}
			}
		}

		for (var8 = 0; var8 < 23; ++var8) {
			base[var8] = 0;
		}

		for (var8 = 0; var8 < alphaSize; ++var8) {
			++base[length[var8] + 1];
		}

		for (var8 = 1; var8 < 23; ++var8) {
			base[var8] += base[var8 - 1];
		}

		for (var8 = 0; var8 < 23; ++var8) {
			limit[var8] = 0;
		}

		int var10 = 0;

		for (var8 = minLen; var8 <= maxLen; ++var8) {
			var10 += base[var8 + 1] - base[var8];
			limit[var8] = var10 - 1;
			var10 <<= 1;
		}

		for (var8 = minLen + 1; var8 <= maxLen; ++var8) {
			base[var8] = (limit[var8 - 1] + 1 << 1) - base[var8];
		}

	}

	public static int decompress(byte[] output, int outSize, byte[] input, int compressedSize, int inOffset) {
		synchronized (sharedBlock) {
			sharedBlock.input = input;
			sharedBlock.nextIn = inOffset;
			sharedBlock.output = output;
			sharedBlock.availOut = 0;
			sharedBlock.decompressedSize = outSize;
			sharedBlock.bsLive = 0;
			sharedBlock.bsBuff = 0;
			sharedBlock.m_c = 0;
			sharedBlock.m_G = 0;
			decompress(sharedBlock);
			outSize -= sharedBlock.decompressedSize;
			sharedBlock.input = null;
			sharedBlock.output = null;
			return outSize;
		}
	}

	private static void nextHeader(BZState state) {
		byte csNextOutCh = state.stateOutCh;
		int cStateOutLen = state.stateOutLen;
		int cNblockUsed = state.nblockUsed;
		int cK0 = state.k0;
		int[] cTt = MiscFunctions.graphics_s_Mb;
		int cTpos = state.tpos;
		byte[] output = state.output;
		int csNextOut = state.availOut;
		int var10 = state.decompressedSize;
		int sSaveNblockPP = state.saveNblock + 1;

		label63:
		while (true) {
			if (cStateOutLen > 0) {
				while (true) {
					if (var10 == 0) {
						break label63;
					}

					if (cStateOutLen == 1) {

						output[csNextOut] = csNextOutCh;
						++csNextOut;
						--var10;
						break;
					}

					output[csNextOut] = csNextOutCh;
					--cStateOutLen;
					++csNextOut;
					--var10;
				}
			}

			while (cNblockUsed != sSaveNblockPP) {
				csNextOutCh = (byte) cK0;
				cTpos = cTt[cTpos];
				byte var1 = (byte) cTpos;
				cTpos >>= 8;
				++cNblockUsed;
				if (var1 != cK0) {
					cK0 = var1;
					if (var10 == 0) {
						cStateOutLen = 1;
						break label63;
					}

					output[csNextOut] = csNextOutCh;
					++csNextOut;
					--var10;
				} else {
					if (cNblockUsed != sSaveNblockPP) {
						cStateOutLen = 2;
						cTpos = cTt[cTpos];
						var1 = (byte) cTpos;
						cTpos >>= 8;
						++cNblockUsed;
						if (cNblockUsed != sSaveNblockPP) {
							if (var1 != cK0) {
								cK0 = var1;
							} else {
								cStateOutLen = 3;
								cTpos = cTt[cTpos];
								var1 = (byte) cTpos;
								cTpos >>= 8;
								++cNblockUsed;
								if (cNblockUsed != sSaveNblockPP) {
									if (var1 != cK0) {
										cK0 = var1;
									} else {
										cTpos = cTt[cTpos];
										var1 = (byte) cTpos;
										cTpos >>= 8;
										++cNblockUsed;
										cStateOutLen = (var1 & 255) + 4;
										cTpos = cTt[cTpos];
										cK0 = (byte) cTpos;
										cTpos >>= 8;
										++cNblockUsed;
									}
								}
							}
						}
						continue label63;
					}

					if (var10 == 0) {
						cStateOutLen = 1;
						break label63;
					}

					output[csNextOut] = csNextOutCh;
					++csNextOut;
					--var10;
				}
			}

			cStateOutLen = 0;
			break;
		}

		int var13 = state.m_G;
		state.m_G += 0;
		if (state.m_G < var13) {
			;
		}

		state.stateOutCh = csNextOutCh;
		state.stateOutLen = cStateOutLen;
		state.nblockUsed = cNblockUsed;
		state.k0 = cK0;
		MiscFunctions.graphics_s_Mb = cTt;
		state.tpos = cTpos;
		state.output = output;
		state.availOut = csNextOut;
		state.decompressedSize = var10;
	}

	private static int getBits(int bits, BZState state) {
		while (state.bsLive < bits) {
			state.bsBuff = state.bsBuff << 8 | state.input[state.nextIn] & 255;
			state.bsLive += 8;
			++state.nextIn;
			++state.m_c;
			if (state.m_c == 0) {
				;
			}
		}

		int var2 = state.bsBuff >> state.bsLive - bits & (1 << bits) - 1;
		state.bsLive -= bits;
		return var2;
	}
}
