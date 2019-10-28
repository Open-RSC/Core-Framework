package com.openrsc.interfaces;

public abstract class MenuAction {

	private boolean hide;

	public void hide() {
		hide = true;
	}

	public boolean shouldHide() {
		return hide;
	}

	public abstract void action();
}
