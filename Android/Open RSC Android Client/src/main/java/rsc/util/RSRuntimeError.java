package rsc.util;

public final class RSRuntimeError extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public Throwable error;
	public String message;

	public RSRuntimeError(Throwable error, String message) {
		try {
			this.error = error;
			this.message = message;
		} catch (RuntimeException var4) {
			throw var4;
		}
	}
}
