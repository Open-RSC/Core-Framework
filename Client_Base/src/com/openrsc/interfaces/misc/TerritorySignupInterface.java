package com.openrsc.interfaces.misc;

import orsc.graphics.gui.Panel;
import orsc.mudclient;


public final class TerritorySignupInterface {
	public Panel territorySignup;
	int width = 250;
	int height = 200;
	int autoHeight = 0;
	int index = 0;
	int trackY = 0;
	private boolean visible = false;
	private mudclient mc;
	private int panelColour, textColour, bordColour, lineColour;
	private int x, y;

	public TerritorySignupInterface(mudclient mc) {
		this.mc = mc;

		territorySignup = new Panel(mc.getSurface(), 15);

		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;
	}

	public void reposition() {
		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;
	}

	public void onRender() {
		reposition();

		int x = (mc.getGameWidth() - width) / 2;
		int y = (mc.getGameHeight() - height) / 2;

		panelColour = 0x989898;
		textColour = 0xffffff;
		bordColour = 0x000000;
		lineColour = 0x000000;

		territorySignup.handleMouse(mc.getMouseX(), mc.getMouseY(), mc.getMouseButtonDown(), mc.getLastMouseDown());

		if (autoHeight - y > 200) {
			mc.getSurface().drawBoxAlpha(x, y, width, height, panelColour, 90);
			mc.getSurface().drawBoxBorder(x, width, y, height, bordColour);
		} else {
			mc.getSurface().drawBoxAlpha(x, y, width, autoHeight - y, panelColour, 90);
			mc.getSurface().drawBoxBorder(x, width, y, autoHeight - y, bordColour);
		}
		drawStringCentered("Territory Signup", x, y + 28, 5, textColour);

		this.drawCloseButton(x + 214, y + 6, 30, 30, "X", 5, new ButtonHandler() {
			@Override
			void handle() {
				setVisible(false);
			}
		});

		mc.getSurface().drawLineHoriz(x, y + 35, width, lineColour);

		trackY = y + 55;

		drawString("Time until war begins: ", x + 8, trackY, 3, textColour);
		trackY += 15;

		// TODO - add check to see if player is signed up
		// if (checkSignup() == true) {
		if (false) {
			this.drawButton(x + 75, trackY, 100, 30, "Drop out", 4, false, new ButtonHandler() {
				@Override
				void handle() {
					// TODO - add handler to drop player from territory
				}
			});
		} else {
			this.drawButton(x + 75, trackY, 100, 30, "Signup", 4, false, new ButtonHandler() {
				@Override
				void handle() {
					// TODO - add handler to sign player up to territory
				}
			});
		}
		trackY += 45;

		this.drawButton(x + 75, trackY, 100, 30, "Switch teams", 4, false, new ButtonHandler() {
			@Override
			void handle() {
				// TODO - add handler to switch teams
			}
		});
		trackY += 45;

		autoHeight = trackY;

		territorySignup.drawPanel();
	}

	public void drawString(String str, int x, int y, int font, int color) {
		mc.getSurface().drawShadowText(str, x, y, color, font, false);
	}

	public void drawStringCentered(String str, int x, int y, int font, int color) {
		int stringWid = mc.getSurface().stringWidth(font, str);
		drawString(str, x + (width / 2) - (stringWid / 2), y, font, color);
	}

	private void drawCloseButton(int x, int y, int width, int height, String text, int font, ButtonHandler handler) {
		int bgBtnColour = 0x333333; // grey
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			bgBtnColour = 16711680; // blue
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		mc.getSurface().drawBoxAlpha(x, y, width, height, bgBtnColour, 192);
		mc.getSurface().drawBoxBorder(x, width, y, height, 0x242424);
		mc.getSurface().drawString(text, x + (width / 2) - (mc.getSurface().stringWidth(font, text) / 2) - 1, y + height / 2 + 5, textColour, font);
	}

	private void drawButton(int x, int y, int width, int height, String text, int font, boolean checked, ButtonHandler handler) {
		int bgBtnColour = 0x333333; // grey
		if (checked) {
			bgBtnColour = 16711680; // red
		}
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			if (!checked) {
				bgBtnColour = 0x6580B7; // blue
			}
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		mc.getSurface().drawBoxAlpha(x, y, width, height, bgBtnColour, 192);
		mc.getSurface().drawBoxBorder(x, width, y, height, 0x242424);
		mc.getSurface().drawString(text, x + (width / 2) - (mc.getSurface().stringWidth(font, text) / 2) - 1, y + height / 2 + 5, textColour, font);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
