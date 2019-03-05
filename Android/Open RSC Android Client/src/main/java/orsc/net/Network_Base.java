package orsc.net;

import java.io.IOException;

import orsc.buffers.RSBuffer_Bits;
import orsc.util.GenUtil;

class Network_Base {

	private final int writeBufferSize = 5000;
	public int m_d = 0;
	public RSBuffer_Bits writeBuffer1;
	String errorCode = "";
	boolean errorHappened = false;
	private int packetReadAttempts = 0;
	private int incomingPacketLength = 0;
	private int readyPackets = 0;
	private int packetStart = 0;

	Network_Base() {
		try {
			this.writeBuffer1 = new RSBuffer_Bits(this.writeBufferSize);
			this.writeBuffer1.packetEnd = 3;
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "b.<init>()");
		}
	}

	public final void flush(int minReady, boolean var2) throws IOException {
		try {
			if (this.errorHappened) {
				this.writeBuffer1.packetEnd = 3;
				this.packetStart = 0;
				this.errorHappened = false;
				throw new IOException(this.errorCode);
			} else {
				++this.readyPackets;
				if (minReady <= this.readyPackets) {
					if (this.packetStart > 0) {
						this.readyPackets = 0;
						this.send(this.writeBuffer1.dataBuffer, 0, this.packetStart);
					}

					this.writeBuffer1.packetEnd = 3;
					this.packetStart = 0;
				}
			}
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "b.R(" + minReady + ',' + true + ')');
		}
	}

	public final void finishPacketAndFlush() throws IOException {
		try {
			this.finishPacket();
			this.flush(0, true);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "b.P(" + -6924 + ')');
		}
	}

	int read() throws IOException {
		try {
			return 0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "b.A(" + true + ')');
		}
	}

	void send(byte[] data, int offset, int count) throws IOException {
		try {
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6,
					"b.D(" + (data != null ? "{...}" : "null") + ',' + offset + ',' + count + ',' + "dummy" + ')');
		}
	}

	public final int decodeIncomingOpcode(int opcode) {
		try {
			return opcode;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "b.K(" + "dummy" + ',' + opcode + ')');
		}
	}

	private int readIncomingPacket(byte[] data) {
		try {
			try {
				++this.packetReadAttempts;
				if (this.m_d > 0 && this.packetReadAttempts > this.m_d) {
					this.errorHappened = true;
					this.errorCode = "time-out";
					this.m_d += this.m_d;
					return 0;
				}

				if (this.incomingPacketLength == 0 && this.available() >= 2) {
					this.incomingPacketLength = ((short) ((read() & 0xff) << 8) | (short) (read() & 0xff));
					incomingPacketLength -= 2;
				}
				if (this.incomingPacketLength > 0 && this.available() >= this.incomingPacketLength) {
					this.read(data, this.incomingPacketLength);
					int packetLength = this.incomingPacketLength;
					this.packetReadAttempts = 0;
					this.incomingPacketLength = 0;
					return packetLength;
				}
			} catch (IOException var4) {
				this.errorHappened = true;
				this.errorCode = var4.getMessage();
			}

			return 0;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "b.O(" + (data != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}

	int available() throws IOException {
		try {
			return 0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "b.B(" + -124 + ')');
		}
	}

	private void read(byte[] data, int count) throws IOException {
		try {
			this.read(data, 0, count);
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5,
					"b.M(" + (data != null ? "{...}" : "null") + ',' + "dummy" + ',' + count + ')');
		}
	}

	public final boolean hasFinishedPackets() {
		try {
			return this.packetStart > 0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "b.S(" + "dummy" + ')');
		}
	}

	public final void newPacket(int opcode) {
		try {
			// (4/5) of write buffer is filled; flush
			if (this.writeBufferSize * 4 / 5 < this.packetStart) {
				try {
					this.flush(0, true);
				} catch (IOException var4) {
					this.errorHappened = true;
					this.errorCode = var4.getMessage();
				}
			}

			this.writeBuffer1.packetEnd = this.packetStart + 2;
			this.writeBuffer1.putByte(opcode);
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "b.N(" + opcode + ',' + "dummy" + ')');
		}
	}

	public final int readIncomingPacket(RSBuffer_Bits data) {
		try {
			data.packetEnd = 0;
			return this.readIncomingPacket(data.dataBuffer);
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "b.Q(" + "dummy" + ',' + "{...}" + ')');
		}
	}

	void close() {
		try {
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "b.G(" + true + ')');
		}
	}

	void read(byte[] data, int offset, int count) throws IOException {
		try {
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6,
					"b.F(" + (data != null ? "{...}" : "null") + ',' + count + ',' + offset + ',' + "dummy" + ')');
		}
	}

	public final void finishPacket() {
		try {
			int packetLen = this.writeBuffer1.packetEnd - this.packetStart - 2;

			this.writeBuffer1.dataBuffer[this.packetStart] = (byte) (packetLen >> 8);
			this.writeBuffer1.dataBuffer[this.packetStart + 1] = (byte) packetLen;


			this.packetStart = this.writeBuffer1.packetEnd;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "b.L(" + "dummy" + ')');
		}
	}
}