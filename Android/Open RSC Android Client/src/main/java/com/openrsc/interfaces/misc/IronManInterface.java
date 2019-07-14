package com.openrsc.interfaces.misc;


import com.openrsc.client.entityhandling.EntityHandler;
import orsc.graphics.gui.Panel;
import orsc.graphics.two.GraphicsController;
import orsc.mudclient;

public final class IronManInterface {
	public Panel ironmanPanel;
	public int iron_man_mode = 0;
	public int iron_man_restriction = 0;
	private int x, y;
	private int width, height;
	private boolean deactivationMenu = false;
	private boolean visible;
	private mudclient mc;
	private String[] ironManTitle = {"Standard Iron Man", "Hardcore Iron Man", "Ultimate Iron Man", "None"};
	private String[] ironManDescription =
			{
					"An Iron Man cannot trade, stake, receive PK loot, scavenge dropped items, nor play certain multiplayer minigames.",
					"In addition to the standard Iron Man rules, a Hardcore Iron Man only has 1 life. A dangerous death will result in being downgraded to a standard Iron Man.",
					"In addition to the standard Iron Man rules, an Ultimate Iron Man cannot use banks, nor retain any items on death in dangerous areas.",
					"- No Iron Man restrictions will apply to this account."
			};
	private String[] restrictionTitle = {"PIN", "Permanent"};
	private String[] restrictionDesc =
			{
					"You must enter your Bank Pin to request that Iron Man restrictions be removed.",
					"- The Iron Man restrictions can never be removed."
			};
	private int[] order = {3, 0, 2, 1};
	private int[] selectMode = {1, 3, 2, 0};

	public IronManInterface(mudclient mc) {
		this.mc = mc;

		width = 480;
		height = 265;

		x = (mc.getGameWidth() / 2) - width;
		y = (mc.getGameHeight() / 2) - height;

		ironmanPanel = new Panel(mc.getSurface(), 1);
	}

	private String getIronManTitleByID(int id) {
		return ironManTitle[id];
	}

	private String getIronManDescByID(int id) {
		return ironManDescription[id];
	}

	private String getIronManRestrictionByID(int id) {
		return restrictionTitle[id];
	}

	private String getIronManRestrictionDescByID(int id) {
		return restrictionDesc[id];
	}

	public void reposition() {
		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;
	}

	public boolean onRender(GraphicsController graphics) {
		reposition();
		drawIronmanInterface(graphics);
		if (deactivationMenu) {
			drawDeactivationMenu(graphics);
		}
		return true;
	}

	private void drawIronmanInterface(GraphicsController graphics) {
		graphics.drawBoxAlpha(x, y, width, height, 0x483E33, 255);
		graphics.drawLineHoriz(x, y + 24, width, 0x2A2926);
		graphics.drawBoxBorder(x, width, y, height, 0x2A2926);
		graphics.drawColoredStringCentered(mc.getGameWidth() / 2, "Iron Man Setup", 0xFF981F, 0, 3, y + 17);

		//content box
		graphics.drawBoxAlpha(x + 5, y + 29, 380, 185, 0x534A3F, 255);
		graphics.drawBoxBorder(x + 5, 380, y + 29, 185, 0x777775);
		graphics.drawLineHoriz(x + 5, y + 48, 380, 0x777775);
		graphics.drawLineHoriz(x + 5, y + 49, 380, 0x777775);
		graphics.drawColoredStringCentered(mc.getGameWidth() / 2 - 46, "Iron Man Mode", 0xFFFFFF, 0, 2, y + 43);


		//deactivation box
		graphics.drawBoxAlpha(x + 5, y + 221, 260, 38, 0x534A3F, 255);
		graphics.drawBoxBorder(x + 5, 260, y + 221, 38, 0x777775);

		//deactivation status
		graphics.drawBoxAlpha(x + 5 + 259, y + 221, 121, 38, 0x534A3F, 255);
		graphics.drawBoxBorder(x + 5 + 259, 121, y + 221, 38, 0x777775);

		//Deactivation select text
		graphics.drawString("Selected ", x + (width / 2 - graphics.stringWidth(1, "Selected ") / 2) + 56, y + 243, 0xFFFFFF, 0);

		//Deactivation selected option display
		graphics.drawString("- " + (getIronManMode() >= 1 ? this.iron_man_restriction == 0 ? "PIN." : "Permanent." : "None."), x + (width / 2) + 49 + 27, y + 243, 0xFF981F, 0);

		// iron helm, plate, legs sprites
		graphics.drawSpriteClipping(mc.spriteSelect(EntityHandler.getItemDef(8)), x + 410, y + 60, 48, 32, 0, 0, false, 0, 1);
		graphics.drawSpriteClipping(mc.spriteSelect(EntityHandler.getItemDef(9)), x + 390, y + 100, 48, 32, 0, 0, false, 0, 1);
		graphics.drawSpriteClipping(mc.spriteSelect(EntityHandler.getItemDef(6)), x + 425, y + 135, 48, 32, 0, 0, true, 0, 1);


		drawCloseButton(graphics, x + 457, y + 2, 21, 21, "X", new ButtonHandler() {
			@Override
			void handle() {
				if (!deactivationMenu) {
					setVisible(false);
				}
			}
		});
		int drawBoxX = x + 5;
		int drawBoxY = y + 25;
		int drawBoxWidth = 370;
		int drawBoxheight = 50;
		int circleY = y + 75;
		int titleY = 18;
		int descX = 21;
		int descY = 11;
		for (int i = 0; i < 4; i++) {
			final int modeID = i;
			if (modeID == 1) {
				drawBoxheight = 45;
				drawBoxY += 50;
				circleY += 50;
				titleY -= 8;
				descX -= 1;
			} else if (modeID == 2) {
				drawBoxheight = 45;
				drawBoxY += 45;
				circleY += 43;
				titleY += 5;
				descX += 0;
			} else if (modeID == 3) {
				drawBoxheight = 20;
				drawBoxY += 45;
				circleY += 33;
				titleY -= 2;
				descY = 0;
				descX += 29;
			}
			drawClickBox(graphics, drawBoxX + 5, drawBoxY + 26, drawBoxWidth, 340, drawBoxheight, getIronManTitleByID(modeID), titleY, getIronManDescByID(modeID), descX, descY, new ButtonHandler() {
				@Override
				void handle() {
					mc.packetHandler.getClientStream().newPacket(199);
					mc.packetHandler.getClientStream().writeBuffer1.putByte(7);
					mc.packetHandler.getClientStream().writeBuffer1.putByte(0);
					mc.packetHandler.getClientStream().writeBuffer1.putByte(selectMode[modeID]);
					mc.packetHandler.getClientStream().finishPacket();
				}
			});

			graphics.drawCircle(drawBoxX + 8 + 5, circleY, 8, 0x3A3026, 255, 0);
			if (i == order[iron_man_mode]) {
				graphics.drawSpriteClipping(mc.spriteSelect(EntityHandler.GUIparts.get(EntityHandler.GUIPARTS.CHECKMARK.id())), drawBoxX + 8, circleY - 5, 13, 10, 0, 0, false, 0, 1);
			}
		}

		drawClickBox(graphics, drawBoxX + 5, drawBoxY + 58, 250, 222, 34, "        Deactivation settings", titleY - 3, "Set restrictions on deactivating or downgrading your Iron Man status.", descX - 5, descY + 10, new ButtonHandler() {
			@Override
			void handle() {
				if (getIronManMode() >= 1 && getIronManMode() <= 3) {
					deactivationMenu = true;
				}
			}
		});

		//graphics.drawSpriteClipping(2340, drawBoxX + 8, circleY + 21, 48, 32, 0, 0, false, 0, 1);
		if (deactivationMenu)
			graphics.drawBoxAlpha(x, y, width, height, 0, 192);
	}

	private void drawDeactivationMenu(GraphicsController graphics) {
		graphics.drawBoxAlpha(x + (width / 2) - 190, y + (height / 2) - 44, 380, 90, 0x524B40, 255);
		graphics.drawBoxBorder(x + (width / 2) - 190, 380, y + (height / 2) - 44, 90, 0x777775);
		graphics.drawLineHoriz(x + (width / 2) - 190, y + (height / 2) - 24, 380, 0x777775);

		drawCloseButton(graphics, x + (width) - 72, y + (height / 2) - 43, 21, 19, "X", new ButtonHandler() {
			@Override
			void handle() {
				deactivationMenu = false;
			}
		});

		graphics.drawColoredStringCentered(mc.getGameWidth() / 2, "After leaving Tutorial Island...", 0xFFFFFF, 0, 2, y + (height / 2) - 30);
		int drawBoxX = x + 50;
		int drawBoxY = y + 85;
		int drawBoxWidth = 370;
		int drawBoxheight = 45;
		int circleY = y + 130;
		int titleY = 11;
		int descX = 21;
		int descY = 11;
		for (int i = 0; i < 2; i++) {
			final int restrictionID = i;
			drawClickRestrictionBox(graphics, drawBoxX + 5, drawBoxY + 26, drawBoxWidth, 340, drawBoxheight, getIronManRestrictionByID(restrictionID), titleY, getIronManRestrictionDescByID(restrictionID), descX - 1, descY, new ButtonHandler() {
				@Override
				void handle() {

					mc.packetHandler.getClientStream().newPacket(199);
					mc.packetHandler.getClientStream().writeBuffer1.putByte(7);
					mc.packetHandler.getClientStream().writeBuffer1.putByte(1);
					mc.packetHandler.getClientStream().writeBuffer1.putByte(restrictionID);
					mc.packetHandler.getClientStream().finishPacket();
				}
			});
			graphics.drawCircle(drawBoxX + 8 + 5, circleY, 8, 0x3A3026, 255, 0);
			if (i == this.iron_man_restriction) {
				graphics.drawSpriteClipping(mc.spriteSelect(EntityHandler.GUIparts.get(EntityHandler.GUIPARTS.CHECKMARK.id())), drawBoxX + 8, circleY - 5, 13, 10, 0, 0, false, 0, 1);
			}
			drawBoxheight = 20;
			drawBoxY += 45;
			circleY += 36;
			titleY += 3;
			descY = 0;
			descX += 55;
		}
	}

	public int getIronManMode() {
		return iron_man_mode;
	}

	public void setIronManMode(int i) {
		this.iron_man_mode = i;
	}

	public void setIronManRestriction(int i) {
		this.iron_man_restriction = i;

	}

	public void updateMode(int i) {
		if (getIronManMode() <= 0 || getIronManMode() >= 4) {
			return;
		}
		if (i > 1 || i < 0) {
			i = 0;
		}
		mc.packetHandler.getClientStream().newPacket(199);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(8);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(i);
		mc.packetHandler.getClientStream().finishPacket();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	private void drawCloseButton(GraphicsController graphics, int x, int y, int width, int height, String text, ButtonHandler handler) {
		int allColor = 0x5F523C;
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			allColor = 0x544838;
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawBoxAlpha(x, y, width, height, allColor, 192);
		graphics.drawBoxBorder(x, width, y, height, 0);
		graphics.drawString(text, x + (width / 2 - graphics.stringWidth(1, text) / 2), y + height / 2 + 5, 0, 4);
	}

	private void drawClickBox(GraphicsController graphics, int x, int y, int width, int wrapWidth, int height, String title, int titleY, String description, int descriptionX, int descriptionY, ButtonHandler handler) {
		int allColor = 0x534A3F;
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height && !deactivationMenu) {
			allColor = 0x675F56;
			if (mc.getMouseClick() == 1) {

				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawBoxAlpha(x, y, width, height, allColor, 192);
		graphics.drawBoxBorder(x, width, y, height, 0x464644);
		graphics.drawString(title, x + 20, y + titleY, 0xFFFFFF, 0);
		graphics.drawWrappedCenteredString(description, x + descriptionX, y + titleY + descriptionY, wrapWidth, 0, 0xFF981F, true, false);
	}

	private void drawClickRestrictionBox(GraphicsController graphics, int x, int y, int width, int wrapWidth, int height, String title, int titleY, String description, int descriptionX, int descriptionY, ButtonHandler handler) {
		int allColor = 0x534A3F;
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height && deactivationMenu) {
			allColor = 0x675F56;
			if (mc.getMouseClick() == 1) {

				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawBoxAlpha(x, y, width, height, allColor, 192);
		graphics.drawString(title, x + 20, y + titleY, 0xFFFFFF, 0);
		graphics.drawWrappedCenteredString(description, x + descriptionX, y + titleY + descriptionY, wrapWidth, 0, 0xFF981F, true, false);
	}
}
