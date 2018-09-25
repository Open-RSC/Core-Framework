package rsc.buffers;

import rsc.util.FastMath;
import rsc.util.GenUtil;
import rsc.util.StringUtil;

public class RSBufferUtils {

	static int[] menu_crcTable = new int[256];

	static {
		for (int i = 0; i < 256; ++i) {
			int v = i;

			for (int j = 0; j < 8; ++j) {
				if ((1 & v) == 1) {
					v = v >>> 1 ^ -306674912;
				} else {
					v >>>= 1;
				}
			}

			RSBufferUtils.menu_crcTable[i] = v;
		}

	}

	public static final int computeCRC(byte[] data, int count) {
		try {
			
			return RSBufferUtils.computeCRC(count, -49, data, 0);
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4,
					"mb.E(" + (data != null ? "{...}" : "null") + ',' + count + ',' + 0 + ')');
		}
	}

	public static final int computeCRC(int right, int var1, byte[] data, int left) {
		try {
			
			int crc = -1;

			for (int i = left; i < right; ++i) {
				crc = RSBufferUtils.menu_crcTable[(data[i] ^ crc) & 255] ^ crc >>> 8;
			}
			return ~crc;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6,
					"w.C(" + right + ',' + var1 + ',' + (data != null ? "{...}" : "null") + ',' + left + ')');
		}
	}

	public static final int get16(int offset, byte[] data) {
		try {
			
			return (data[1 + offset] & 255) + ((255 & data[offset]) << 8);
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4,
					"d.D(" + offset + ',' + "dummy" + ',' + (data != null ? "{...}" : "null") + ')');
		}
	}

	public static final int get32(int offset, byte[] data) {
		try {
			
			return (data[offset + 3] & 255) + (data[offset + 2] << 8 & 0xFF00) + ((255 & data[offset]) << 24)
					+ ((255 & data[1 + offset]) << 16);
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4,
					"m.A(" + "dummy" + ',' + offset + ',' + (data != null ? "{...}" : "null") + ')');
		}
	}

	public static final String getEncryptedString(RSBuffer buffer) {
		try {
			
			return getEncryptedString(buffer, 32767);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ia.C(" + (buffer != null ? "{...}" : "null") + ',' + false + ')');
		}
	}

	public static final String getEncryptedString(RSBuffer src, int limit) {
		try {
			

			try {
				int count = src.getSmart08_16();
				if (count > limit) {
					count = limit;
				}

				byte[] dest = new byte[count];
				src.packetEnd += RSBufferUtils.stringEncryption.decryptString(src.dataBuffer, dest, 0,
						src.packetEnd, -1, count);
				String str = getStringFromBytes(dest, 0, count);
				return str;
			} catch (Exception var6) {
				return "Cabbage";
			}
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7,
					"client.CD(" + 0 + ',' + (src != null ? "{...}" : "null") + ',' + limit + ')');
		}
	}

	public static final String getStringFromBytes(byte[] src, int offset, int count) {
		try {
			
			char[] dest = new char[count];
			int dh = 0;

			for (int i = 0; i < count; ++i) {
				int codepoint = 255 & src[offset + i];
				if (codepoint != 0) {
					if (codepoint >= 128 && codepoint < 160) {
						char c = StringUtil.specialCharLookup[codepoint - 128];
						if (c == 0) {
							c = '?';
						}
						codepoint = c;
					}
					dest[dh++] = (char) codepoint;
				}
			}
			return new String(dest, 0, dh);
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9,
					"ga.A(" + count + ',' + "dummy" + ',' + offset + ',' + (src != null ? "{...}" : "null") + ')');
		}
	}

	public static final int putEncryptedString(RSBuffer dest, String src) {
		try {
			
			int oldHead = dest.packetEnd;
			byte[] data = RSBufferUtils.stringToBytes(src);
			dest.putSmart08_16(data.length);
			dest.packetEnd += RSBufferUtils.stringEncryption.encryptString(data.length, dest.dataBuffer,
					dest.packetEnd, data, 0, 119);
			return dest.packetEnd - oldHead;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "u.B(" + "dummy" + ',' + (dest != null ? "{...}" : "null") + ','
					+ (src != null ? "{...}" : "null") + ')');
		}
	}

	static final int putStringIntoBytes(CharSequence str, int strLeft, int strRight, byte[] dest, int destOffset) {
		try {
			
			int count = strRight - strLeft;
			for (int i = 0; count > i; ++i) {
				char c = str.charAt(strLeft + i);
				if (c > 0 && c < 128 || c >= 160 && c <= 255) {
					dest[i + destOffset] = (byte) c;
				} else if (c == 8364) {
					dest[i + destOffset] = -128;
				} else if (c != 8218) {
					if (c != 402) {
						if (c == 8222) {
							dest[destOffset + i] = -124;
						} else if (c != 8230) {
							if (c == 8224) {
								dest[destOffset + i] = -122;
							} else if (c == 8225) {
								dest[i + destOffset] = -121;
							} else if (c != 710) {
								if (c == 8240) {
									dest[i + destOffset] = -119;
								} else if (c != 352) {
									if (c != 8249) {
										if (c != 338) {
											if (c != 381) {
												if (c != 8216) {
													if (c != 8217) {
														if (c == 8220) {
															dest[destOffset + i] = -109;
														} else if (c == 8221) {
															dest[i + destOffset] = -108;
														} else if (c == 8226) {
															dest[destOffset + i] = -107;
														} else if (c != 8211) {
															if (c != 8212) {
																if (c != 732) {
																	if (c == 8482) {
																		dest[i + destOffset] = -103;
																	} else if (c == 353) {
																		dest[destOffset + i] = -102;
																	} else if (c != 8250) {
																		if (c == 339) {
																			dest[destOffset + i] = -100;
																		} else if (c != 382) {
																			if (c != 376) {
																				dest[i + destOffset] = 63;
																			} else {
																				dest[destOffset + i] = -97;
																			}
																		} else {
																			dest[destOffset + i] = -98;
																		}
																	} else {
																		dest[i + destOffset] = -101;
																	}
																} else {
																	dest[i + destOffset] = -104;
																}
															} else {
																dest[destOffset + i] = -105;
															}
														} else {
															dest[destOffset + i] = -106;
														}
													} else {
														dest[i + destOffset] = -110;
													}
												} else {
													dest[i + destOffset] = -111;
												}
											} else {
												dest[i + destOffset] = -114;
											}
										} else {
											dest[i + destOffset] = -116;
										}
									} else {
										dest[i + destOffset] = -117;
									}
								} else {
									dest[i + destOffset] = -118;
								}
							} else {
								dest[destOffset + i] = -120;
							}
						} else {
							dest[i + destOffset] = -123;
						}
					} else {
						dest[destOffset + i] = -125;
					}
				} else {
					dest[i + destOffset] = -126;
				}
			}

			return count;
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "i.B(" + strRight + ',' + destOffset + ',' + strLeft + ','
					+ (str != null ? "{...}" : "null") + ',' + "dummy" + ',' + (dest != null ? "{...}" : "null") + ')');
		}
	}

	public static final int readShort(byte[] data, int var1, int index) {
		try {
			
			int val = FastMath.byteToUByte(data[index]) * 256 + FastMath.byteToUByte(data[1 + index]);
			if (val > 32767) {
				val -= 65536;
			}

			return val;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4,
					"w.B(" + (data != null ? "{...}" : "null") + ',' + -1 + ',' + index + ')');
		}
	}

	public static final byte[] stringToBytes(CharSequence str) {
		try {
			
			int len = str.length();
			byte[] out = new byte[len];

			for (int i = 0; i < len; ++i) {
				char c = str.charAt(i);
				if ((c <= 0 || c >= 128) && (c < 160 || c > 255)) {
					if (c != 8364) {
						if (c == 8218) {
							out[i] = -126;
						} else if (c != 402) {
							if (c != 8222) {
								if (c == 8230) {
									out[i] = -123;
								} else if (c != 8224) {
									if (c != 8225) {
										if (c != 710) {
											if (c == 8240) {
												out[i] = -119;
											} else if (c != 352) {
												if (c == 8249) {
													out[i] = -117;
												} else if (c == 338) {
													out[i] = -116;
												} else if (c != 381) {
													if (c != 8216) {
														if (c != 8217) {
															if (c == 8220) {
																out[i] = -109;
															} else if (c == 8221) {
																out[i] = -108;
															} else if (c == 8226) {
																out[i] = -107;
															} else if (c == 8211) {
																out[i] = -106;
															} else if (c == 8212) {
																out[i] = -105;
															} else if (c != 732) {
																if (c != 8482) {
																	if (c != 353) {
																		if (c != 8250) {
																			if (c != 339) {
																				if (c != 382) {
																					if (c == 376) {
																						out[i] = -97;
																					} else {
																						out[i] = 63;
																					}
																				} else {
																					out[i] = -98;
																				}
																			} else {
																				out[i] = -100;
																			}
																		} else {
																			out[i] = -101;
																		}
																	} else {
																		out[i] = -102;
																	}
																} else {
																	out[i] = -103;
																}
															} else {
																out[i] = -104;
															}
														} else {
															out[i] = -110;
														}
													} else {
														out[i] = -111;
													}
												} else {
													out[i] = -114;
												}
											} else {
												out[i] = -118;
											}
										} else {
											out[i] = -120;
										}
									} else {
										out[i] = -121;
									}
								} else {
									out[i] = -122;
								}
							} else {
								out[i] = -124;
							}
						} else {
							out[i] = -125;
						}
					} else {
						out[i] = -128;
					}
				} else {
					out[i] = (byte) c;
				}
			}

			return out;
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "h.A(" + (str != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}

	public static StringEncryption stringEncryption;

	public static final void setStringEncryptor(StringEncryption var0) {
		try {
			RSBufferUtils.stringEncryption = var0;
			
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "cb.C(" + (var0 != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}

	public static StringEncryption encryption = new StringEncryption(StringEncryption.asByte(22, 22, 22, 22, 22, 22, 21, 22,
	22, 20, 22, 22, 22, 21, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 3, 8, 22,
	16, 22, 16, 17, 7, 13, 13, 13, 16, 7, 10, 6, 16, 10, 11, 12, 12, 12, 12, 13, 13, 14, 14, 11, 14, 19, 15, 17,
	8, 11, 9, 10, 10, 10, 10, 11, 10, 9, 7, 12, 11, 10, 10, 9, 10, 10, 12, 10, 9, 8, 12, 12, 9, 14, 8, 12, 17,
	16, 17, 22, 13, 21, 4, 7, 6, 5, 3, 6, 6, 5, 4, 10, 7, 5, 6, 4, 4, 6, 10, 5, 4, 4, 5, 7, 6, 10, 6, 10, 22,
	19, 22, 14, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
	22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
	22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
	22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
	22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 21, 22, 21, 22, 22, 22, 21, 22, 22));

}
