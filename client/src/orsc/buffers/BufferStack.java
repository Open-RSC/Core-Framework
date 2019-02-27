package orsc.buffers;

import orsc.util.GenUtil;

public final class BufferStack {
	public static byte[] s_i;
	RSBuffer_Base writeHead = new RSBuffer_Base();
	private RSBuffer_Base readHead;

	public BufferStack() {
		try {
			this.writeHead.next = this.writeHead;
			this.writeHead.previous = this.writeHead;
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "db.<init>()");
		}
	}

	static void insertAfter(RSBuffer_Base insert, byte var1, RSBuffer_Base after) {
		try {
			if (null != insert.next) {
				insert.removeThisBufferFromChain();
			}
			insert.previous = after;
			insert.next = after.next;
			insert.next.previous = insert;
			insert.previous.next = insert;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "ac.B(" + "{...}" + ',' + "dummy" + ',' + (after != null ? "{...}" : "null") + ')');
		}
	}

	final void add(RSBuffer_Base of) {
		try {
			if (null != of.next) {
				of.removeThisBufferFromChain();
			}

			// [... writeHead] -> [..., of, writeHead]
			of.next = this.writeHead;
			of.previous = this.writeHead.previous;
			of.next.previous = of;
			of.previous.next = of;

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "db.C(" + "{...}" + ',' + ')');
		}
	}

	final RSBuffer_Base seekToSecondToLast() {
		try {
			RSBuffer_Base activeMod = this.writeHead.previous;
			if (this.writeHead == activeMod) {
				this.readHead = null;
				return null;
			} else {
				this.readHead = activeMod.previous;
				return activeMod;
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "db.D(" + -123 + ')');
		}
	}

	final RSBuffer_Base pop() {
		try {
			RSBuffer_Base var2 = this.readHead;
			if (this.writeHead != var2) {
				this.readHead = var2.previous;
				return var2;
			} else {
				this.readHead = null;
				return null;
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "db.A(" + 80 + ')');
		}
	}
}
