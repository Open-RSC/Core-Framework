package rsc.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import rsc.mudclient;
import rsc.util.GenUtil;

public final class Network_Socket extends Network_Base implements Runnable {
	private boolean closed = false;
	private InputStream inStream;
	private boolean m_X = true;
	private OutputStream outStream;
	private Socket sock;
	private final byte[] tmpRead = new byte[1];

	private byte[] writeBuffer;

	private int writeBufRead = 0;

	private int writeBufWrite = 0;

	public Network_Socket(Socket sock, mudclient var2) throws IOException {
		try {
			this.sock = sock;
			this.inStream = sock.getInputStream();
			this.outStream = sock.getOutputStream();
			this.m_X = false;
			var2.startThread(1, this);
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4,
					"da.<init>(" + (sock != null ? "{...}" : "null") + ',' + (var2 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	final int available() throws IOException {
		try {
			return this.closed ? 0 : this.inStream.available();
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "da.B(" + -124 + ')');
		}
	}

	@Override
	public final void close() {
		try {
			super.close();
			this.closed = true;

			try {
				if (this.inStream != null) {
					this.inStream.close();
				}

				if (null != this.outStream) {
					this.outStream.close();
				}

				if (null != this.sock) {
					this.sock.close();
				}
			} catch (IOException var5) {
				System.out.println("Error closing stream");
			}

			this.m_X = true;
			synchronized (this) {
				this.notify();
			}

			this.writeBuffer = null;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "da.G(" + closed + ')');
		}
	}

	@Override
	public final int read() throws IOException {
		try {
			if (!this.closed) {
				this.read(this.tmpRead, 0, 1);
				return 255 & this.tmpRead[0];
			} else {
				return 0;
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "da.A(" + true + ')');
		}
	}

	@Override
	final void read(byte[] data, int offset, int count) throws IOException {
		try {
			if (!this.closed) {
				int totalRead = 0;

				int readCount;
				for (; totalRead < count; totalRead += readCount) {
					if ((readCount = this.inStream.read(data, offset + totalRead, count - totalRead)) <= 0) {
						throw new IOException("EOF: " + totalRead + ", " + count);
					}
				}

			}
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8,
					"da.F(" + (data != null ? "{...}" : "null") + ',' + count + ',' + offset + ',' + "dummy" + ')');
		}
	}

	@Override
	public final void run() {
		try {
			while (!this.m_X) {
				int begin;
				int len;
				synchronized (this) {
					if (this.writeBufWrite == this.writeBufRead) {
						try {
							this.wait();
						} catch (InterruptedException var8) {
							;
						}
					}

					if (this.m_X) {
						return;
					}

					if (this.writeBufRead > this.writeBufWrite) {
						begin = 5000 - this.writeBufRead;
					} else {
						begin = this.writeBufWrite - this.writeBufRead;
					}

					len = this.writeBufRead;
				}

				if (begin > 0) {
					try {
						this.outStream.write(this.writeBuffer, len, begin);
					} catch (IOException var7) {
						this.errorHappened = true;
						this.errorCode = "Twriter:" + var7;
					}

					this.writeBufRead = (this.writeBufRead + begin) % 5000;

					try {
						if (this.writeBufRead == this.writeBufWrite) {
							this.outStream.flush();
						}
					} catch (IOException var6) {
						this.errorHappened = true;
						this.errorCode = "Twriter:" + var6;
					}
				}
			}

		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10, "da.run()");
		}
	}

	@Override
	final void send(byte[] data, int offset, int count) throws IOException {
		try {
			if (!this.closed) {
				if (null == this.writeBuffer) {
					this.writeBuffer = new byte[5000];
				}

				synchronized (this) {
					for (int i = 0; i < count; ++i) {
						this.writeBuffer[this.writeBufWrite] = data[offset + i];
						this.writeBufWrite = (this.writeBufWrite + 1) % 5000;
						if (this.writeBufWrite == (4900 + this.writeBufRead) % 5000) {
							throw new IOException("buffer overflow");
						}
					}

					this.notify();
				}
			}
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9,
					"da.D(" + (data != null ? "{...}" : "null") + ',' + offset + ',' + count + ',' + "dummy" + ')');
		}
	}
}
