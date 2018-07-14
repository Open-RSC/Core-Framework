package org.openrsc.client.loader.various;

public abstract interface ProgressCallback {
	public abstract void onComplete(byte[] paramArrayOfByte);

	public abstract void update(int paramInt1, int paramInt2);
}