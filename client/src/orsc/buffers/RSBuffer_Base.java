package orsc.buffers;

import orsc.util.GenUtil;

class RSBuffer_Base {
	RSBuffer_Base previous;
	RSBuffer_Base next;

	final void removeThisBufferFromChain() {
		try {
			if (null != this.next) {
				this.next.previous = this.previous;
				this.previous.next = this.next;
				this.previous = null;
				this.next = null;
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ib.PA(" + -27331 + ')');
		}
	}
}
