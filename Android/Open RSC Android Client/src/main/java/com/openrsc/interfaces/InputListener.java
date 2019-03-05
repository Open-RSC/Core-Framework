package com.openrsc.interfaces;

public abstract class InputListener {

	boolean onCharTyped(char c, int key) {
		return false;
	}

	public boolean onMouseMove(int x, int y) {
		return false;
	}

	public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
		return false;
	}

}