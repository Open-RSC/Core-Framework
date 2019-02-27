package orsc.util;

public final class RSRuntimeError extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public Throwable error;
	public String message;

	RSRuntimeError(Throwable error, String message) {
		this.error = error;
		this.message = message;
	}
}
