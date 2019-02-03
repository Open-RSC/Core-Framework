package orsc.multiclient;

import com.openrsc.client.model.Sprite;

import java.io.ByteArrayInputStream;

public interface ClientPort {

	boolean drawLoading(int i);

	void showLoadingProgress(int percentage, String status);

	void initListeners();

	void crashed();

	void drawLoadingError();

	void drawOutOfMemoryError();

	boolean isDisplayable();

	void drawTextBox(String line2, byte var2, String line1);

	void initGraphics();

	void draw();

	void close();

	String getCacheLocation();

	void resized();

	Sprite getSpriteFromByteArray(ByteArrayInputStream byteArrayInputStream);

	void playSound(byte[] soundData, int offset, int dataLength);

	void stopSoundPlayer();

	void drawKeyboard();

	boolean saveCredentials(String creds);

	boolean saveHideIp(int preference);

	String loadCredentials();

	int loadHideIp();
}