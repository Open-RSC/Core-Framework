package com.openrsc.interfaces.misc;

import java.lang.reflect.Array;
import java.util.ArrayList;

import orsc.graphics.gui.Panel;
import orsc.graphics.two.GraphicsController;
import orsc.mudclient;


public final class QuestGuideInterface {
	private boolean visible = false;

	private ArrayList<QuestItem> questItems;

	public int questGuideScroll;

	public Panel questGuide;

	private mudclient mc;

	private int panelColour, textColour, bordColour, lineColour;

	int width = 430;
	int height = 320;
	int autoHeight = 0;

	int index = 0;
	int trackY = 0;

	private int x, y;

	public QuestGuideInterface(mudclient mc) {
		this.mc = mc;

		questGuide = new Panel(mc.getSurface(), 15);

		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		questItems = new ArrayList<QuestItem>();

		questGuideScroll = questGuide.addScrollingList(x + 4, y + 36, width - 5, height - 37, 100, 2, false);
	}

	public void reposition() {
		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		questGuide.reposition(questGuideScroll, x + 4, y + 36, width - 5, height - 37);
	}

	public void onRender(GraphicsController graphics) {
		reposition();

		int x = (mc.getGameWidth() - width) / 2;
		int y = (mc.getGameHeight() - height) / 2;

		panelColour = 0x989898; textColour = 0xffffff;
		bordColour = 0x000000; lineColour = 0x000000;

		questGuide.handleMouse(mc.getMouseX(), mc.getMouseY(), mc.getMouseButtonDown(), mc.getLastMouseDown());

		if (autoHeight - y > 320) {
			mc.getSurface().drawBoxAlpha(x, y, width, height, panelColour, 90);
			mc.getSurface().drawBoxBorder(x, width, y, height, bordColour);
		} else {
			mc.getSurface().drawBoxAlpha(x, y, width, autoHeight - y, panelColour, 90);
			mc.getSurface().drawBoxBorder(x, width, y, autoHeight - y, bordColour);
		}
		drawStringCentered(mc.getQuestGuideChosen(), x, y + 28, 5, textColour);

		this.drawButton(x + 394, y + 6, 30, 30, "X", 5, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.packetHandler.getClientStream().newPacket(212);
				mc.packetHandler.getClientStream().finishPacket();
				questGuide.resetScrollIndex(questGuideScroll);
				setVisible(false);
			}
		});

		mc.getSurface().drawLineHoriz(x, y + 35, width, lineColour);

		questItems.clear();

		if(mc.getQuestGuideProgress() == 0) {
			String questStart = "I can start the quest by speaking to " + mc.getQuestGuideStartWho() + " " + mc.getQuestGuideStartWhere() + ".";
			customAdd(questStart, 2, textColour);

			customAdd("", 2, textColour);
			customAdd("Requirements: ", 2, textColour);

			for (int i = 0; i < Array.getLength(mc.getQuestGuideRequirement()); i++) {
				customAdd("  - " + mc.getQuestGuideRequirement()[i], 2, textColour);
			}
		}
		else if(mc.getQuestGuideProgress() > 0) {
			customAdd("Quest progress coming soon...", 2, textColour);
		} else {
			customAdd("Congratulations you have completed " + mc.getQuestGuideChosen() + ".", 2, textColour);
		}


		customAdd("", 2, textColour);
		customAdd("Rewards: ", 2, textColour);

		for (int i = 0; i < Array.getLength(mc.getQuestGuideReward()); i++) {
			customAdd("  - " + mc.getQuestGuideReward()[i], 2, textColour);
		}

		questGuide.clearList(questGuideScroll);

		for(int i = -1; i <= questItems.size(); i++) {
			questGuide.setListEntry(questGuideScroll, i + 1, "", 0, (String) null, (String) null);
		}

		int listStartPoint = questGuide.getScrollPosition(questGuideScroll);
		int listEndPoint = listStartPoint + 17;

		trackY = y + 55;

		for (int i = -1; i < questItems.size(); i++) {
			if (i >= 100) {
				break;
			}

			if (i < listStartPoint || i > listEndPoint)
				continue;

			QuestItem curItem = questItems.get(i);

			drawString(curItem.getText(), x + 8, trackY, curItem.getFont(), curItem.getColor());

			trackY += 15;
		}

		// Temporary for tracking
		drawString("Progress: " + asStringStage(mc.getQuestGuideProgress()) + " (" + 
				Integer.toString(mc.getQuestGuideProgress()) + ")", x + width - 150, trackY, 2, this.textColour);
		trackY += 15;

		autoHeight = trackY;

		questGuide.drawPanel();
	}

	public String asStringStage(int progress) {
		return progress == 0 ? "Not started" : progress < 0 ? "Completed" : "In progress";
	}

	public void customAdd(String text, int font, int color) {
		int textWidth = mc.getSurface().stringWidth(font, text);
		if (textWidth > width - x - 8 && text.length() >= 76) {
			String text1 = text.substring(0, 76);
			text1 = text.substring(0, text1.lastIndexOf(" "));
			questItems.add(new QuestItem(text1, font, color));

			String text2 = text.substring(text1.lastIndexOf(" ") + 1);
			text2 = text2.substring(text2.indexOf(" ") + 1);
			customAdd(text2, font, color);
		} else {
			questItems.add(new QuestItem(text, font, color));
		}
	}

	public void drawString(String str, int x, int y, int font, int color) {
		mc.getSurface().drawShadowText(str, x, y, color, font, false);
	}

	public void drawStringCentered(String str, int x, int y, int font, int color) {
		int stringWid = mc.getSurface().stringWidth(font, str);
		drawString(str, x + (width/2) - (stringWid/2), y, font, color);
	}

	private void drawButton(int x, int y, int width, int height, String text, int font, boolean checked, ButtonHandler handler) {
		int bgBtnColour = 0x333333; // grey
		if (checked) {
			bgBtnColour = 16711680; // red
		}
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			if (!checked)
				bgBtnColour = 16711680; // blue
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		mc.getSurface().drawBoxAlpha(x, y, width, height, bgBtnColour, 192);
		mc.getSurface().drawBoxBorder(x, width, y, height, 0x242424);
		mc.getSurface().drawString(text, x + (width/2) - (mc.getSurface().stringWidth(font, text)/2) - 1, y + height / 2 + 5, textColour, font);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}

class QuestItem {

	private String text;
	private int font, color;

	public QuestItem(String text, int font, int color) {
		this.text = text;
		this.font = font;
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public int getFont() {
		return font;
	}

	public int getColor() {
		return color;
	}
}