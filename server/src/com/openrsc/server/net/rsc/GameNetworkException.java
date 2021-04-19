package com.openrsc.server.net.rsc;

public class GameNetworkException extends RuntimeException  {
	private final Object struct;
	private final String reason;
	private final String exposedDetail; // any exposed detail to send out

	public GameNetworkException(final Object struct, final String reason, final String exposedDetail) {
		super(struct.getClass().getSimpleName() + ": " + reason);
		this.struct = struct;
		this.reason = reason;
		this.exposedDetail = exposedDetail;
	}

	public GameNetworkException(GameNetworkException ex) {
		this(ex.getObject(), ex.getReason(), ex.getExposedDetail());
	}

	public Object getObject() {
		return struct;
	}

	public String getReason() { return reason; }

	public String getExposedDetail() { return exposedDetail; }
}
