package com.openrsc.interfaces.misc;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;
import orsc.graphics.gui.Panel;
import orsc.mudclient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


public final class LostOnDeathInterface {
	public Panel lostOnDeathPanel;
	int itemSelected = -1, rightClickMenuX = 0, rightClickMenuY = 0;
	int width = 509;
	int height = 331;
	private ArrayList<OnDeathItem> onDeathItems;
	private boolean visible;
	private mudclient mc;
	private int panelColour, textColour, bordColour;
	private int x, y;

	public LostOnDeathInterface(mudclient mc) {
		this.mc = mc;

		lostOnDeathPanel = new Panel(mc.getSurface(), 15);

		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		onDeathItems = new ArrayList<OnDeathItem>();
	}

	public void reposition() {
		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;
	}

	public void onRender() {
		reposition();

		panelColour = 0x989898;
		textColour = 0xFFFFFF;
		bordColour = 0x000000;

		lostOnDeathPanel.handleMouse(mc.getMouseX(), mc.getMouseY(), mc.getMouseButtonDown(), mc.getLastMouseDown());

		// Draws the background
		mc.getSurface().drawBoxAlpha(x, y, width, height, panelColour, 160);
		mc.getSurface().drawBoxBorder(x, width, y, height, bordColour);

		// Draws the title
		drawStringCentered("Items on Death", x, y + 28, 5, textColour);

		this.drawButton(x + width - 35, y + 5, 30, 30, "X", 5, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.packetHandler.getClientStream().newPacket(212);
				mc.packetHandler.getClientStream().finishPacket();
				setVisible(false);
			}
		});

		mc.getSurface().drawLineHoriz(x + 1, y + 40, width - 1, 0);
		mc.getSurface().drawLineVert(x + 145, y + 40, 2, height - 40);
		mc.getSurface().drawLineHoriz(x + 1, y + 88, width - 1, 0);

		drawString("Items kept on death", x + 5, y + 70, 3, textColour);
		drawString("Items lost on death", x + 5, y + 115, 3, textColour);

		drawItemsLost();
	}

	public void drawItemsLost() {
		reposition();

		int curX = x + 160, curY = y + 48;
		int movedAtFlag = -1;

		onDeathItems.clear();
		if (mc.getInventoryItemCount() > 0) {
			populateOnDeathItems();
		}

		for (int i = 0; i < onDeathItems.size(); i++) {
			if (i >= 100) {
				break;
			}

			OnDeathItem curItem = onDeathItems.get(i);
			ItemDef def = EntityHandler.getItemDef(curItem.getItemID());

			if (!curItem.getLost() && movedAtFlag < 0) {
				curX = x + 160;
				curY += 48;
				movedAtFlag = i;
			}

			mc.getSurface().drawSpriteClipping(mc.spriteSelect(def),
				curX, curY, 48, 32, def.getPictureMask(), 0, false, 0, 1);
			if (def.getNotedFormOf() >= 0) {
				ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
				mc.getSurface().drawSpriteClipping(mc.spriteSelect(originalDef),
					curX, curY, 48, 32, originalDef.getPictureMask(), 0, false, 0, 1);
			}

			if (def.isStackable()) {
				drawString(mudclient.formatStackAmount((int) curItem.getStackCount()),
					curX + 1, curY + 10, 1, '\uffff');
			}

			drawString("Amount lost on death:", x + 5, y + 200, 3, textColour);
			drawString(this.getLossTotal(), x + 5, y + 220, 3, textColour);

			if (((i - movedAtFlag + 1) % 6) == 0) {
				curX = x + 160;
				curY += 48;
			} else {
				curX += 58;
			}
		}

		lostOnDeathPanel.drawPanel();
	}

	private void populateOnDeathItems() {
		int[] invyItems = mc.getInventoryItems();
		for (int i = 0; i < mc.getInventoryItemCount(); i++) {
			int count = mc.getInventoryItemCount();
			if (invyItems[i] > 0) {
				ItemDef def = EntityHandler.getItemDef(invyItems[i]);
				int stackCount = 1;
				if (def.isStackable()) {
					stackCount = mc.getInventoryItemsCount()[i];
					onDeathItems.add(new OnDeathItem(invyItems[i], def.getBasePrice(), stackCount, false));
				} else {
					onDeathItems.add(new OnDeathItem(invyItems[i], def.getBasePrice(), 1, false));
				}
			}
		}

		Collections.sort(onDeathItems, new Comparator<OnDeathItem>() {
			@Override
			public int compare(OnDeathItem obj1, OnDeathItem obj2) {
				return (int) (obj2.getPrice() - obj1.getPrice());
			}
		});

		int keepXItems = 0;
		if (mc.getLocalPlayer().skullVisible == 0) {
			keepXItems += 3;
		}
		//TODO - add check for item protect
		//TODO - fix stackables not being properly set to lost or not

		// Sets keepXItems amount of items first (sorted by price) to keep
		for (int i = 0; i < keepXItems; i++) {
			if (i >= onDeathItems.size()) {
				break;
			}
			// Handles special case of stackable items being kept
			if (EntityHandler.getItemDef(onDeathItems.get(i).getItemID()).isStackable()) {
				onDeathItems.add(i, new OnDeathItem(onDeathItems.get(i).getItemID(), onDeathItems.get(i).getPrice(), 1, false));
				onDeathItems.set(i + 1, new OnDeathItem(onDeathItems.get(i + 1).getItemID(), onDeathItems.get(i + 1).getPrice(), onDeathItems.get(i + 1).getStackCount() - 1, false));
			}
			onDeathItems.get(i).setLost(true);
		}

		// Handles special case of some stackables being kept and some being lost
		for (int i = 0; i < keepXItems; i++) {
			if (i >= onDeathItems.size()) {
				break;
			}
			if (!EntityHandler.getItemDef(onDeathItems.get(i).getItemID()).isStackable()) {
				break;
			}
			for (int j = i + 1; j < keepXItems; j++) {
				if (onDeathItems.get(i).getItemID() == onDeathItems.get(j).getItemID()) {
					onDeathItems.set(i, new OnDeathItem(onDeathItems.get(i).getItemID(), onDeathItems.get(i).getPrice(), onDeathItems.get(i).getStackCount() + 1, onDeathItems.get(i).getLost()));
					onDeathItems.remove(i + 1);
				}
			}
		}
	}

	private String getLossTotal() {
		long totalLost = 0;
		for (int i = 0; i < onDeathItems.size(); i++) {
			if (!onDeathItems.get(i).getLost()) {
				totalLost += onDeathItems.get(i).getPrice() * onDeathItems.get(i).getStackCount();
			}
		}
		totalLost = totalLost < 0 ? 0 : totalLost;
		return NumberFormat.getNumberInstance(Locale.US).format(totalLost);
	}

	public void drawString(String str, int x, int y, int font, int color) {
		mc.getSurface().drawString(str, x, y, color, font);
	}

	public void drawStringCentered(String str, int x, int y, int font, int color) {
		int stringWid = mc.getSurface().stringWidth(font, str);
		mc.getSurface().drawString(str, x + (width / 2) - (stringWid / 2) - 2, y, color, font);
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
		mc.getSurface().drawString(text, x + (width / 2) - (mc.getSurface().stringWidth(font, text) / 2) - 1, y + height / 2 + 5, textColour, font);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}

class OnDeathItem {

	private int itemID;
	private boolean lost;
	private long price, stackCount;

	public OnDeathItem(int itemID, long price, long stackCount, boolean lost) {
		this.itemID = itemID;
		this.price = price;
		this.stackCount = stackCount;
		this.lost = lost;
	}

	public int getItemID() {
		return itemID;
	}

	public long getPrice() {
		return price;
	}

	public long getStackCount() {
		return stackCount;
	}

	public void setStackCount(long stackCount) {
		this.stackCount = stackCount;
	}

	public boolean getLost() {
		return lost;
	}

	public void setLost(boolean lost) {
		this.lost = lost;
	}
}
