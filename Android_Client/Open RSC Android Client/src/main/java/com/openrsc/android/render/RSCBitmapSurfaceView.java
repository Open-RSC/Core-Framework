package com.openrsc.android.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;

import com.openrsc.client.R;
import com.openrsc.client.android.GameActivity;
import com.openrsc.client.model.Sprite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import orsc.multiclient.ClientPort;
import orsc.osConfig;
import orsc.util.Utils;

public abstract class RSCBitmapSurfaceView extends SurfaceView implements SurfaceHolder.Callback, ClientPort {

	private final int client_width = 512;
	private final int client_height = 334;

	protected final Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	private final Bitmap currentFrame = Bitmap.createBitmap(512, 334 + 12, Bitmap.Config.RGB_565);

	private final GameActivity gameActivity;
	private boolean m_hb;

	private Map<Integer, Sprite> statusSprites = new HashMap<>();

	public RSCBitmapSurfaceView(Context c) {
		super(c);
		gameActivity = (GameActivity) c;
		Utils.context = c;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		setLongClickable(true);
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setKeepScreenOn(true);
		loadStatusSprites();
	}

	private void loadStatusSprites() {
		int[] drawableIds = {R.drawable.battery_empty, R.drawable.battery_1_bar, R.drawable.battery_2_bar, R.drawable.battery_3_bar,
			R.drawable.battery_4_bar, R.drawable.battery_5_bar, R.drawable.battery_6_bar, R.drawable.battery_full, R.drawable.battery_charging,
			R.drawable.network_none, R.drawable.network_cell, R.drawable.network_wifi};
		for (int drawableId : drawableIds) {
			statusSprites.put(drawableId, getSpriteFromDrawableId(drawableId));
		}
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		setWillNotDraw(false);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo editorinfo) {
		BaseInputConnection bic = new BaseInputConnection(this, false);
		editorinfo.actionLabel = null;
		editorinfo.inputType = InputType.TYPE_NULL;
		editorinfo.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN;
		bic.finishComposingText();
		return bic;
	}

	@Override
	public boolean onKeyPreIme(int keyCode, @NonNull KeyEvent event) { // @NonNull?
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) { // ACTION_DOWN
			post(new Runnable() {
				@Override
				public void run() {
					if (osConfig.F_SHOWING_KEYBOARD) {
						gameActivity.closeKeyboard();
					} else {
						gameActivity.drawKeyboard();
					}
				}
			});
		}
		return super.onKeyPreIme(keyCode, event);
	}

	@Override
	public boolean drawLoading(int i) {
		drawLoadingScreen("Loading...", 0);
		return true;
	}

	private void drawLoadingScreen(String state, int percent) {
		try {

			int x = (this.client_width - 281) / 2;
			int y = (this.client_height - 148) / 2;

			Paint paint = new Paint();
			paint.setTextSize(15);
			paint.setTextAlign(Align.CENTER);
			Canvas canvas = new Canvas(currentFrame);
			canvas.drawColor(0, Mode.CLEAR);

			paint.setStyle(Paint.Style.FILL);
			canvas.drawRect(0, 0, client_width, client_height, paint);
			paint.setStyle(Paint.Style.STROKE);

			// if (!this.m_hb) {
			// canvas.drawBitmap(this.loadingJagLogo, (float) x, (float) y,
			// null);
			// }

			x += 2;
			y += 90;

			paint.setColor(Color.rgb(132, 132, 132));
			if (this.m_hb) {
				paint.setColor(Color.rgb(220, 0, 0));
			}

			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(x - 2, y - 2, x + 280, y + 23, paint);

			paint.setStyle(Paint.Style.FILL);
			canvas.drawRect(x, y, x + ((percent * 277) / 100), y + 20, paint);

			paint.setStyle(Paint.Style.STROKE);

			paint.setColor(Color.rgb(198, 198, 198));
			if (this.m_hb) {
				paint.setColor(Color.rgb(255, 255, 255));
			}

			canvas.drawText(state, x + 138, y + 10, paint);

			if (!this.m_hb) {
				canvas.drawText("Powered by Open RuneScape Classic", x + 138, y + 30, paint);
				canvas.drawText("We support open source", x + 138, y + 44, paint);
			} else {
				paint.setColor(Color.rgb(132, 132, 152));
				canvas.drawText("We support open source", x + 138, client_height - 20, paint);
			}
		} catch (Exception var6) {
			var6.printStackTrace();
		}
	}

	@Override
	public void showLoadingProgress(int percentage, String status) {
		drawLoadingScreen(status, percentage);
		int x = (this.client_width - 281) / 2;
		x += 2;
		int y = (this.client_height - 148) / 2;
		y += 90;
		int progress = percentage * 277 / 100;

		Canvas canvas = new Canvas(currentFrame);
		Paint paint = new Paint();
		paint.setColor(Color.rgb(132, 132, 132));
		if (this.m_hb) {
			paint.setColor(Color.rgb(220, 0, 0));
		}

		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(x, y, x + progress, y + 20, paint);

		paint.setColor(Color.BLACK);

		canvas.drawRect(progress + x, y, x + 277 - progress, y + 20, paint);

		paint.setColor(Color.rgb(198, 198, 198));
		if (this.m_hb) {
			paint.setColor(Color.rgb(255, 255, 255));
		}

		paint.setTextAlign(Align.CENTER);
		canvas.drawText(status, x + 138, y + 10, paint);
	}

	@Override
	public void initListeners() {

	}

	@Override
	public void crashed() {

	}

	@Override
	public void drawLoadingError() {

	}

	@Override
	public void drawOutOfMemoryError() {

	}

	@Override
	public boolean isDisplayable() {
		return true;
	}

	@Override
	public void drawTextBox(String line2, byte var2, String line1) {
		Canvas canvas = new Canvas(currentFrame);

		Paint paint = new Paint();

		paint.setColor(Color.rgb(132, 132, 132));
		if (this.m_hb) {
			paint.setColor(Color.rgb(220, 0, 0));
		}

		int x = 512 / 2 - 140;
		int y = 334 / 2 - 25;
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(x, y, x + 280, y + 50, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
		canvas.drawRect(x, y, x + 280, y + 50, paint);

		paint.setTextAlign(Align.CENTER);
		canvas.drawText(line1, client_width >> 1, (client_height >> 1) - 10, paint);
		canvas.drawText(line2, client_width >> 1, 10 + (client_height >> 1), paint);
		paint.setColor(Color.BLACK);
	}

	@Override
	public void initGraphics() {
	}

	@Override
	public void draw() {
		if (gameActivity.getMudclient() != null) {
			currentFrame.setPixels(gameActivity.getMudclient().getSurface().pixelData, 0,
				gameActivity.getMudclient().getGameWidth(), 0, 0, gameActivity.getMudclient().getGameWidth(),
				gameActivity.getMudclient().getGameHeight() + 12);
			postInvalidate();
		}
	}

	private void doDraw(Canvas c) {
		if (gameActivity.getMudclient() == null) {
			return;
		}
		c.drawRGB(0, 0, 0);
		int resizedWidth = c.getWidth();
		int resizedHeight = c.getHeight();
		c.scale(((float) resizedWidth / (float) gameActivity.getMudclient().getGameWidth()),
			((float) resizedHeight / (float) (gameActivity.getMudclient().getGameHeight() + 12)));
		c.drawBitmap(currentFrame, 0, 0, bitmapPaint);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		doDraw(canvas);
	}

	@Override
	public void close() {

	}

	@Override
	public String getCacheLocation() {
		return getContext().getFilesDir().getAbsolutePath() + File.separator;
	}

	@Override
	public void resized() {

	}

	@Override
	public Sprite getSpriteFromByteArray(ByteArrayInputStream byteArrayInputStream) {
		Bitmap bp = BitmapFactory.decodeStream(byteArrayInputStream);
		int width = bp.getWidth();
		int height = bp.getHeight();
		int[] captchaPixels = new int[width * height];
		int px = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				captchaPixels[px++] = bp.getPixel(x, y);
			}
		}

		bp.recycle();
		Sprite sprite = new Sprite(captchaPixels, width, height);
		sprite.setSomething(width, height);
		return sprite;
	}

	private Sprite getSpriteFromDrawableId(int drawableId) {
		Drawable drawable = getResources().getDrawable(drawableId);
		BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
		Bitmap bitmap = bitmapDrawable.getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); //use the compression format of your need
		ByteArrayInputStream is = new ByteArrayInputStream(stream.toByteArray());

		return getSpriteFromByteArray(is);
	}

	@Override
	public boolean getBatteryCharging() {
		return gameActivity.getBatteryCharging();
	}

	@Override
	public int getBatteryPercent() {
		return gameActivity.getBatteryPercent();
	}

	@Override
	public Sprite getBattery(int level) {
		int drawableId = R.drawable.battery_empty;
		switch (level) {
			case 8:
				drawableId = R.drawable.battery_charging;
				break;
			case 7:
				drawableId = R.drawable.battery_full;
				break;
			case 6:
				drawableId = R.drawable.battery_6_bar;
				break;
			case 5:
				drawableId = R.drawable.battery_5_bar;
				break;
			case 4:
				drawableId = R.drawable.battery_4_bar;
				break;
			case 3:
				drawableId = R.drawable.battery_3_bar;
				break;
			case 2:
				drawableId = R.drawable.battery_2_bar;
				break;
			case 1:
				drawableId = R.drawable.battery_1_bar;
				break;
			case 0:
				drawableId = R.drawable.battery_empty;
				break;
		}
		return statusSprites.get(drawableId);
	}

	@Override
	public String getConnectivityText() {
		return gameActivity.getConnectivityText();
	}

	@Override
	public Sprite getConnectivity(int level) {
		int drawableId = R.drawable.network_none;
		switch (level) {
			case 2:
				drawableId = R.drawable.network_wifi;
				break;
			case 1:
				drawableId = R.drawable.network_cell;
				break;
			case 0:
				drawableId = R.drawable.network_none;
				break;
		}
		return statusSprites.get(drawableId);
	}

	private AudioTrack audioTrack;

	@Override
	public void playSound(byte[] soundData, int offset, int dataLength) {
		int bufferSize = AudioTrack.getMinBufferSize(16000,
			AudioFormat.CHANNEL_IN_STEREO,
			AudioFormat.ENCODING_PCM_16BIT);
	}

	@Override
	public void stopSoundPlayer() {
	}

	public void drawKeyboard() {
	}

	public void closeKeyboard() {
	}

	public boolean saveCredentials(String creds) {
		return false;
	}

	public String loadCredentials() {
		return null;
	}
}
