package com.openrsc.interfaces.misc;

import orsc.Config;
import orsc.graphics.gui.Panel;
import orsc.graphics.two.GraphicsController;
import orsc.enumerations.InputXAction;
import orsc.graphics.gui.InputXPrompt;
import orsc.mudclient;


public final class PointsToGpInterface {
	public Panel experienceConfig;
	public int experienceConfigScroll;
	public boolean selectSkillMenu = false;
	int width = 350, height = 75;
	private boolean visible = false;
	private mudclient mc;
	private int panelColour, textColour, bordColour, lineColour;
	private int x, y;

	public PointsToGpInterface(mudclient mc) {
		this.mc = mc;

		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		experienceConfig = new Panel(mc.getSurface(), 5);
		experienceConfigScroll = experienceConfig.addScrollingList(x + 95, y + 34, 160, height - 40, 20, 2, false);
	}

	public void reposition() {
		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		experienceConfig.reposition(experienceConfigScroll, x + 95, y + 34, 160, height - 40);
	}

	public void onRender(GraphicsController graphics) {
		reposition();

		drawExperienceConfig();

		if (selectSkillMenu) {
			drawSelectSkillMenu();
		}
	}

	private void drawExperienceConfig() {
		reposition();

		panelColour = 0x989898;
		textColour = 0xffffff;
		bordColour = 0x000000;
		lineColour = 0x000000;

		experienceConfig.handleMouse(mc.getMouseX(), mc.getMouseY(), mc.getMouseButtonDown(), mc.getLastMouseDown());
		mc.getSurface().drawBoxAlpha(x, y - 50, width, height, panelColour, 90);
		mc.getSurface().drawBoxBorder(x, width, y - 50, height, bordColour);

		this.drawString("Exchange Points to Gp", x + 10, y - 35, 3, textColour);
		this.drawString(Config.S_OPENPK_POINTS_TO_GP_RATIO + " Points = 1 Gp", x + 10, y - 18, 3, textColour);
		this.drawString("Points: " + mc.getPoints(), x + 10, y + 20, 3, textColour);
		this.drawButton(x + 198, y - 20, 85, 28, "@yel@Exchange", 3, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.pointsToGp, InputXAction.POINTS_TO_GP, true);
			}
		});
		this.drawCloseButton(x + 318, y - 44, 24, 24, "X", 5, new ButtonHandler() {
			@Override
			void handle() {
				if (!selectSkillMenu) {
					mc.packetHandler.getClientStream().newPacket(212);
					mc.packetHandler.getClientStream().finishPacket();
					setVisible(false);
				}
			}
		});
	}

	private void drawSelectSkillMenu() {
		reposition();

		mc.getSurface().drawBoxAlpha(x + 90, y + 5, 166, height - 10, panelColour, 90);
		mc.getSurface().drawBoxBorder(x + 90, 166, y + 5, height - 10, bordColour);

		this.drawStringCentered("Select a skill to track", x - 12, y + 22, 3, textColour);

		mc.getSurface().drawLineHoriz(x + 90, y + 30, 166, lineColour);

		this.drawCloseButton(x + 237, y + 6, 18, 18, "X", 2, new ButtonHandler() {
			@Override
			void handle() {
				experienceConfig.resetScrollIndex(experienceConfigScroll);
				selectSkillMenu = false;
			}
		});

		String[] skillNames = mc.getSkillNamesLong();

		experienceConfig.clearList(experienceConfigScroll);

		for (int i = 0; i < mudclient.skillCount; i++) {
			experienceConfig.setListEntry(experienceConfigScroll, i, "@whi@" + skillNames[i], 0, (String) null, (String) null);
		}

		int index = experienceConfig.getControlSelectedListIndex(experienceConfigScroll);
		if (index >= 0 && mc.mouseButtonClick == 1) {
			mc.selectedSkill = index;
			experienceConfig.resetScrollIndex(experienceConfigScroll);
			selectSkillMenu = false;
			mc.setMouseClick(0);
		}

		experienceConfig.drawPanel();

		Config.C_EXPERIENCE_COUNTER_MODE = 2;
	}

	private void drawString(String str, int x, int y, int font, int color) {
		if (color == 0xFFFFFF) {
			mc.getSurface().drawShadowText(str, x, y, color, font, false);
		} else {
			mc.getSurface().drawString(str, x, y, color, font);
		}
	}

	private void drawStringCentered(String str, int x, int y, int font, int color) {
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
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height && !selectSkillMenu) {
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
