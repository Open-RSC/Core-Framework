package rsc.buffers;

import rsc.util.FastMath;
import rsc.util.GenUtil;

public final class RSBuffer_Bits extends RSBuffer {
	private int bitHead;

	public final int getBitHead() {
		try {
			
			return this.bitHead;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ja.A(" + "dummy" + ')');
		}
	}

	public final void startBitAccess() {
		try {
			this.bitHead = this.packetEnd * 8;
			
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ja.B(" + -2231 + ')');
		}
	}

	public final int getBitMask(int count) {
		try {
			
			int bite = this.bitHead >> 3;
			int shift = 8 - (this.bitHead & 7);

			this.bitHead += count;
			int result = 0;
			while (count > shift) {
				result += (FastMath.bitwiseMaskForShift[shift] & this.dataBuffer[bite++]) << count - shift;
				count -= shift;
				shift = 8;
			}

			if (count != shift) {
				result += this.dataBuffer[bite] >> shift - count & FastMath.bitwiseMaskForShift[count];
			} else {
				result += this.dataBuffer[bite] & FastMath.bitwiseMaskForShift[shift];
			}

			return result;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ja.C(" + count + ')');
		}
	}

	public RSBuffer_Bits(int size) {
		super(size);
	}

	public final void endBitAccess() {
		try {
			this.packetEnd = (7 + this.bitHead) / 8;
			
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ja.D(" + 25505 + ')');
		}
	}

}
