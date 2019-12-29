package com.openrsc.server.login;

public abstract class LoginExecutorProcess {
	private boolean processed = false;

	public final void process() {
		processInternal();
		processed = true;
	}

	public final boolean isProcessed() { return processed; }

	abstract protected void processInternal();
}
