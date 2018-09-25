package com.openrsc.android.updater;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class TextProgressBar extends ProgressBar {
	private String text = "";
	private int textColor = Color.BLACK;
	private float textSize = 15;
	private Paint textPaint;
	private Rect bounds;

	public TextProgressBar(Context context) {
		super(context);
		textPaint = new Paint();
		bounds = new Rect();
	}

	public TextProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		textPaint = new Paint();
		bounds = new Rect();
	}

	public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		textPaint = new Paint();
		bounds = new Rect();
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		textPaint.setAntiAlias(true);
		textPaint.setColor(textColor);
		textPaint.setTextSize(textSize);
		
		textPaint.getTextBounds(text, 0, text.length(), bounds);
		int x = getWidth() / 2 - bounds.centerX();
		int y = getHeight() / 2 - bounds.centerY();
		canvas.drawText(text, x, y, textPaint);
	}

	public String getText() {
		return text;
	}

	public synchronized void setText(String text) {
		if (text != null) {
			this.text = text;
		} else {
			this.text = "";
		}
		postInvalidate();
		drawableStateChanged();
	}

	public int getTextColor() {
		return textColor;
	}

	public synchronized void setTextColor(int textColor) {
		this.textColor = textColor;
		drawableStateChanged();

		postInvalidate();
	}

	public float getTextSize() {
		return textSize;
	}

	public synchronized void setTextSize(float textSize) {
		this.textSize = textSize;
		drawableStateChanged();
		postInvalidate();
	}
}