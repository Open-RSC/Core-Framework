package rsc.buffers;

public abstract class RSBuffer_Sound extends RSBuffer_Base {
	public RSBuffer_Sound nextSound;
	public RSBuffer_Variant2 buffer;
	public int m_i;
	public volatile boolean usePCM = true;

	public abstract void muckAround(int var1);

	public abstract int d();

	public abstract RSBuffer_Sound nextToPlay();

	public abstract void getPCMData(int[] var1, int var2, int var3);

	public abstract RSBuffer_Sound getPlaybackHead();

	public int c() {
		return 255;
	}

	final void getPCMData_or_B(int[] var1, int var2, int var3) {
		if (this.usePCM) {
			this.getPCMData(var1, var2, var3);
		} else {
			this.muckAround(var3);
		}

	}
}
