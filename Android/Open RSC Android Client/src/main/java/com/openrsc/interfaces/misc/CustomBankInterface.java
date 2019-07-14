package com.openrsc.interfaces.misc;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;

import java.util.ArrayList;

import orsc.Config;
import orsc.enumerations.InputXAction;
import orsc.graphics.gui.InputXPrompt;
import orsc.mudclient;

public final class CustomBankInterface extends BankInterface {
	private static int fontSize = Config.isAndroid() ? Config.C_MENU_SIZE : 1;
	private static int fontSizeHeight;
	public int selectedInventorySlot = -1;
	public int bankSearch;
	public int bankScroll;
	public int lastXAmount = 0;
	private boolean saveXAmount = false;
	private boolean rightClickMenu;
	private int organizeMode = 0;
	private int rightClickMenuX;
	private int rightClickMenuY;
	private int draggingInventoryID = -1;
	private int draggingBankSlot = -1;
	private boolean swapNoteMode;
	private int x, y;
	private int[] bankItemSelector = {0, 0, 40, 80, 120, 160, 200};
	private BankTabShow bankTabShow = BankTabShow.FIRST_ITEM_IN_TAB;

	public CustomBankInterface(mudclient mc) {
		super(mc);
		if (Config.S_WANT_CUSTOM_BANKS) {
			width = 509;
			height = 331;
			x = (mc.getGameWidth() - width) / 2;
			y = (mc.getGameHeight() - height) / 2;
			bankScroll = bank.addScrollingList(x + 4, y + 21, width - 5, 172, 500, 7, true);
			bankSearch = bank.addLeftTextEntry(x + 375 + 6, y + 44, 110, 18, 0, 15, false, true);
		}
	}

	@Override
	public boolean onRender() {
		if (!Config.S_WANT_CUSTOM_BANKS) return super.onRender();

		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2 - 3;

		bank.reposition(bankSearch, x + 375 + 6, y + 44, 110, 18);
		bank.reposition(bankScroll, x + 4, y + 57, width - 5, 137);

		fontSizeHeight = mc.getSurface().fontHeight(fontSize);

		mc.getSurface().drawBox(x, y, width, 21, 192);
		int colour = 0x989898;
		mc.getSurface().drawBoxAlpha(x, y + 21, width, 309, colour, 160);
		mc.getSurface().drawBoxBorder(x, width, y, 331, 0x000000);

		drawString("The Bank of " + Config.SERVER_NAME, x + 196, y + 15, 1, 0xFFFFFF);

		int j3 = 0xFFFFFF;
		if (mc.getMouseX() > x + 415 && mc.getMouseY() >= y && mc.getMouseX() < x + width && mc.getMouseY() < y + 12 + 9) {
			j3 = 16711680;
		}
		drawString("Close Window", x + 401 + 19, y + 15, 1, j3);

		int tabWidth = 48;
		int tabHeight = 32;
		int tabX = x + 6;
		int tabY = y + 23;
		int bankPages = 0;
		if (bankItems.size() > 0 && bankItems.size() <= 40) {
			if (mc.bankPage == 2)
				mc.bankPage = 1;
			bankPages = 1;
		} else if (bankItems.size() > 40 && bankItems.size() <= 80) {
			if (mc.bankPage == 3)
				mc.bankPage = 2;
			bankPages = 2;
		} else if (bankItems.size() > 80 && bankItems.size() <= 120) {
			if (mc.bankPage == 4)
				mc.bankPage = 3;
			bankPages = 3;
		} else if (bankItems.size() > 120 && bankItems.size() <= 160) {
			if (mc.bankPage == 5)
				mc.bankPage = 4;
			bankPages = 4;
		} else if (bankItems.size() > 160 && bankItems.size() <= 200) {
			if (mc.bankPage == 6)
				mc.bankPage = 5;
			bankPages = 5;
		} else if (bankItems.size() > 200) {
			bankPages = 6;
		}

		for (int tabs = 0; tabs < bankPages + 1; tabs++) {
			int colorTab = 0x5A5A55;
			if (tabs == mc.bankPage) {
				colorTab = 0x989898;
			}
			mc.getSurface().drawBoxAlpha(tabX, tabY, tabWidth, tabHeight, colorTab, 192);
			mc.getSurface().drawBoxBorder(tabX, tabWidth + 1, tabY, tabHeight, 0x2D2C24);
			mc.getSurface().drawBoxBorder(tabX + 1, tabWidth - 1, tabY + 1, tabHeight - 2, 0x706452);
			int first_item = -1;
			for (BankItem bankItem : bankItems) {
				if (bankItem.itemID > 0) {
					first_item = bankItems.get(bankItemSelector[tabs]).itemID;
					break;
				}
			}
			if (tabs != 0) {
				switch (bankTabShow) {
					case DIGIT:
						mc.getSurface().drawString("" + tabs, tabX, tabY, 0xFFFFFF, 1);
						break;
					case FIRST_ITEM_IN_TAB:
						mc.getSurface().drawSpriteClipping(mc.spriteSelect(EntityHandler.getItemDef(first_item)),
								tabX, tabY, 48, 32, EntityHandler.getItemDef(first_item).getPictureMask(), 0, false, 0, 1);
						mc.getSurface().drawString("" + tabs, tabX + 2, tabY + 12, 0xFFFFFF, 3);
						break;
				}
			} else {
				mc.getSurface().drawString("ALL", tabX + 15, tabY + 20, 0xFFFFFF, 1);
			}
			if (mc.inputX_Action == InputXAction.ACT_0 && mc.mouseButtonClick != 0) {
				if (mc.getMouseX() > tabX && mc.getMouseY() >= tabY && mc.getMouseX() < tabX + tabWidth && mc.getMouseY() < tabY + tabHeight) {
					bank.setText(this.bankSearch, "");
					mc.bankPage = tabs;
					mc.setMouseClick(0);
				}
			}
			tabX += 51;
		}

		mc.getSurface().drawString("Search for item:", x + 371 + 7, y + 33, 0xffffff, 1);
		mc.getSurface().drawBoxAlpha(x + 371 + 6, y + 36, 120, 18, 0x222222, 255);
		mc.getSurface().drawBoxBorder(x + 371 + 6, 120, y + 36, 18, 0x474843);

		//mc.getSurface().drawString("Number in bank in green", x + 7, 34 + y, '\uff00', 1);
		int boxColour = 0xd0d0d0;

		if (mc.getMouseClick() != 0 || mc.getMouseButtonDownTime() >= 0) {
			mc.getMouseX();
			mc.getGameWidth();
			mc.getMouseY();
			mc.getGameHeight();

			int currMouseX = mc.getMouseX();
			int currMouseY = mc.getMouseY();
			int selectedX = currMouseX - (mc.getGameWidth() / 2 - width / 2);
			int selectedY = currMouseY - (mc.getGameHeight() / 2 - height / 2 + 20);

			if (selectedX < 0 || selectedY < 16 || selectedX >= 600 || selectedY >= 488) { // is cursor located outside of the bank window area?
				if (mc.getMouseClick() == 1) { // if click, close bank window
					resetVar();
					bankClose();
				}
			}

			if (mc.getMouseClick() != 0 && !rightClickMenu) {
				if (mc.getMouseX() > x + 380 && mc.getMouseY() >= y && mc.getMouseX() < x + width
						&& mc.getMouseY() < y + 12 + 9) { // close bank button
					resetVar();
					bankClose();
				} else if (mc.getMouseX() >= x + 8 && mc.getMouseX() <= x + 83 && mc.getMouseY() >= y + 206
						&& mc.getMouseY() <= y + 220) {
					sendDepositAll();
				} else if (mc.getMouseX() >= x + 349 && mc.getMouseX() <= x + 422 && mc.getMouseY() >= y + 206
						&& mc.getMouseY() <= y + 220) {
					swapNoteMode = false;
					sendNoteMode();
				} else if (mc.getMouseX() >= x + 423 && mc.getMouseX() <= x + 498 && mc.getMouseY() >= y + 206
						&& mc.getMouseY() <= y + 220) {
					swapNoteMode = true;
					sendNoteMode();
				} else if (mc.getMouseX() >= x + 186 && mc.getMouseX() <= x + 259 && mc.getMouseY() >= y + 206
						&& mc.getMouseY() <= y + 220) {
					organizeMode = 2;
				} else if (mc.getMouseX() >= x + 111 && mc.getMouseX() <= x + 184 && mc.getMouseY() >= y + 206
						&& mc.getMouseY() <= y + 220) {
					organizeMode = 1;
				} else if (mc.getMouseX() >= x + 259 && mc.getMouseX() <= x + 333 && mc.getMouseY() >= y + 206
						&& mc.getMouseY() <= y + 220) {
					organizeMode = 0;
				}
			}
		}

		String searchItem = bank.getControlText(bankSearch);
		ArrayList<BankItem> searchList = new ArrayList<BankItem>();
		for (BankItem item : bankItems) {
			ItemDef def = EntityHandler.getItemDef(item.itemID);
			if (searchItem.length() > 0) {
				if (def.getName().toLowerCase().contains(searchItem)) {
					searchList.add(item);
				}
			} else {
				searchList.add(item);
			}
		}
		int bankCount = 0;
		int bankSlotStart = (mc.bankPage - 1) * 40;

		// Scrollable first page
		if (mc.bankPage == 0) {
			bank.clearList(bankScroll);
			bank.show(bankScroll);
			bankCount = (int) ((searchList.size() - 1) / 10);
			for (int i = 0; i < bankCount + 1; i++) {
				bank.setListEntry(bankScroll, i, "", 0, (String) null, (String) null);
			}
			bankSlotStart = bank.getScrollPosition(bankScroll) * 10;
			if ((bankSlotStart / 10) > (bank.controlListCurrentSize[bankScroll] - 4)) {
				bank.resetListToIndex(bankScroll, (bankSlotStart / 10) - 1);
			}
		} else {
			bank.hide(bankScroll);
		}

		// Drawing bank-specific items
		for (int verticalSlots = 0; verticalSlots < 4; verticalSlots++) {
			for (int horizonalSlots = 0; horizonalSlots < 10; horizonalSlots++) {

				BankItem bankItem = null;
				if (bankSlotStart >= 0 && bankSlotStart < searchList.size()) {
					bankItem = searchList.get(bankSlotStart);
				}

				int drawX = x + 6 + horizonalSlots * 49;
				int drawY = y + 57 + verticalSlots * 34;

				mc.getSurface().drawBoxAlpha(drawX, drawY, 49, 34, boxColour, 160);
				mc.getSurface().drawBoxBorder(drawX, 50, drawY, 35, 0);
				if (bankItem != null) {

					/* Drawing Item Sprites */

					// Dragging items
					if (draggingBankSlot != -1 && bank.getControlText(bankSearch).isEmpty()) {
						ItemDef def = EntityHandler.getItemDef(bankItems.get(draggingBankSlot).itemID);
						mc.getSurface().drawSpriteClipping(mc.spriteSelect(def),
								mc.getMouseX(), mc.getMouseY(), 48, 32, def.getPictureMask(), 0, false, 0, 1);
						if (def.getNotedFormOf() >= 0) {
							ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
							mc.getSurface().drawSpriteClipping(mc.spriteSelect(originalDef),
									mc.getMouseX() + 7, mc.getMouseY() + 8, 29, 19,
									originalDef.getPictureMask(), 0, false, 0, 1);
						}
						drawString(mudclient.formatStackAmount(bankItems.get(draggingBankSlot).amount), mc.getMouseX(), mc.getMouseY(), 1, 65280);
					}

					// Noted Items
					if (bankSlotStart < bankItems.size() && bankItems.get(bankSlotStart).itemID != -1) {
						ItemDef def = EntityHandler.getItemDef(bankItem.itemID);
						if (draggingBankSlot != bankSlotStart) {
							mc.getSurface().drawSpriteClipping(mc.spriteSelect(def), drawX, drawY, 48, 32,
									def.getPictureMask(), 0, false, 0, 1);
							if (def.getNotedFormOf() >= 0) {
								ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
								mc.getSurface().drawSpriteClipping(mc.spriteSelect(originalDef), drawX + 7,
										drawY + 8, 29, 19, originalDef.getPictureMask(), 0, false, 0, 1);
							}
							drawString(mudclient.formatStackAmount(bankItem.amount), drawX + 1, drawY + 10, 1, 65280);
						}
					}

					// Organize mode dragging
					if (mc.getMouseX() > drawX && mc.getMouseX() < drawX + 49 && mc.getMouseY() > drawY
							&& mc.getMouseY() < drawY + 34 && !rightClickMenu && mc.inputX_Action == InputXAction.ACT_0) {
						if (organizeMode > 0 && !rightClickMenu && bank.getControlText(bankSearch).isEmpty()) {
							if (mc.getMouseButtonDownTime() > 0 && mc.getMouseButtonDown() == 1) {
								if (mc.getMouseButtonDownTime() < 2 && bankSlotStart < bankItems.size()
										&& bankItems.get(bankSlotStart).itemID != -1) {
									draggingBankSlot = bankItem.bankID;
								}
							} else if (draggingBankSlot > -1 && bankItems.get(bankSlotStart).itemID != -1) {
								sendItemSwap(draggingBankSlot, bankItem.bankID);
								draggingBankSlot = -1;
							}
						} else if (mc.getMouseClick() == 1 && !rightClickMenu && mc.inputX_Action == InputXAction.ACT_0) {
							selectedBankSlot = bankItem.bankID;
							sendWithdraw(1);
						}
					}

					// Right click menu
					if (mc.getMouseX() > drawX && mc.getMouseX() < drawX + 49 && mc.getMouseY() > drawY
							&& mc.getMouseY() < drawY + 34 && bankSlotStart < bankItems.size()
							&& bankItems.get(bankSlotStart).itemID != -1 && mc.inputX_Action == InputXAction.ACT_0) {
						if (mc.getMouseClick() == 2) {
							selectedBankSlot = bankItem.bankID;
							rightClickMenuX = mc.getMouseX();
							rightClickMenuY = mc.getMouseY();
							rightClickMenu = true;
							mc.setMouseClick(0);
						}
					}

					// Drawing item name
					if (mc.getMouseX() > drawX && mc.getMouseX() < drawX + 49 && mc.getMouseY() > drawY && mc.getMouseY() < drawY + 34) {
						if (bankItems.get(bankItem.bankID).itemID != -1) {
							drawString(EntityHandler.getItemDef(bankItems.get(bankItem.bankID).itemID).getName(), x + 7, y + 15, 1, 0xFFFFFF);
						}

					} else if (mc.getMouseX() <= x + 6 || mc.getMouseX() >= x + 496 || mc.getMouseY() <= y + 57 ||
							(mc.getMouseY() >= y + 193 && mc.getMouseY() <= y + 227) || mc.getMouseY() >= y + 329) {
						drawString(Integer.toString(bankItems.size()), x + 7, y + 15, 0, 0xFFFFFF);
						mc.getSurface().drawLineVert(x + 9 + (mc.getSurface().stringWidth(0, "" + bankItems.size())), y + 6, 0xFFFFFF, 10);
						drawString(Integer.toString(mc.bankItemsMax), x + 13 + (mc.getSurface().stringWidth(0, "" + bankItems.size())), y + 15, 1, 0xFFFFFF);
					}

					bankSlotStart++;
				}
			}
		}
		bank.drawPanel();

		int inventorySlot = 0;

		int inventoryDrawX = x;
		int inventoryDrawY = y + 190;

		int settingsY = y + 206;

		mc.getSurface().drawBoxAlpha(x + 6, settingsY - 1, 75, 16, 0x5A5A55, 192);
		mc.getSurface().drawBoxBorder(x + 6, 75, settingsY - 1, 16, 0x2D2C24);
		mc.getSurface().drawBoxBorder(x + 7, 73, settingsY, 14, 0x706452);
		drawString("Deposit All", x + 12, settingsY + 11, 1, 0xffffff);

		drawString("Rearrange mode:", x + 190, settingsY - 3, 1, 0xF89922);

		mc.getSurface().drawBoxAlpha(x + 112, settingsY - 1, 75, 16, (organizeMode == 1 ? 0x7E1F1C : 0x5A5A55), 192);
		mc.getSurface().drawBoxBorder(x + 112, 75, settingsY - 1, 16, 0x2D2C24);
		mc.getSurface().drawBoxBorder(x + 113, 73, settingsY, 14, 0x706452);
		drawString("Swap", x + 22 + 112, settingsY + 11, 1, 0xffffff);

		mc.getSurface().drawBoxAlpha(x + 186, settingsY - 1, 75, 16, (organizeMode == 2 ? 0x7E1F1C : 0x5A5A55), 192);
		mc.getSurface().drawBoxBorder(x + 186, 75, settingsY - 1, 16, 0x2D2C24);
		mc.getSurface().drawBoxBorder(x + 187, 73, settingsY, 14, 0x706452);
		drawString("Insert", x + 22 + 186, settingsY + 11, 1, 0xffffff);

		mc.getSurface().drawBoxAlpha(x + 260, settingsY - 1, 75, 16, (organizeMode == 0 ? 0x7E1F1C : 0x5A5A55), 192);
		mc.getSurface().drawBoxBorder(x + 260, 75, settingsY - 1, 16, 0x2D2C24);
		mc.getSurface().drawBoxBorder(x + 261, 73, settingsY, 14, 0x706452);
		drawString("None", x + 22 + 261, settingsY + 11, 1, 0xffffff);

		drawString("Withdraw as:", x + 378 + 14, settingsY - 3, 1, 0xF89922);

		mc.getSurface().drawBoxAlpha(x + 423 - 75, settingsY - 1, 75, 16, (!swapNoteMode ? 0x7E1F1C : 0x5A5A55), 192);
		mc.getSurface().drawBoxBorder(x + 423 - 75, 75, settingsY - 1, 16, 0x2D2C24);
		mc.getSurface().drawBoxBorder(x + 424 - 75, 73, settingsY, 14, 0x706452);
		drawString("Item", x + 26 + 423 - 75, settingsY + 11, 1, 0xffffff);

		mc.getSurface().drawBoxAlpha(x + 422, settingsY - 1, 75, 16, (swapNoteMode ? 0x7E1F1C : 0x5A5A55), 192);
		mc.getSurface().drawBoxBorder(x + 422, 75, settingsY - 1, 16, 0x2D2C24);
		mc.getSurface().drawBoxBorder(x + 423, 73, settingsY, 14, 0x706452);
		drawString("Note", x + 26 + 422, settingsY + 11, 1, 0xffffff);

		// Inventory Items Loop
		for (int verticalSlots = 0; verticalSlots < 3; verticalSlots++) {
			for (int horizonalSlots = 0; horizonalSlots < 10; horizonalSlots++) {

				int drawX = inventoryDrawX + 6 + horizonalSlots * 49;
				int drawY = inventoryDrawY + 35 + verticalSlots * 34;

				mc.getSurface().drawBoxAlpha(drawX, drawY, 49, 34, boxColour, 160);
				mc.getSurface().drawBoxBorder(drawX, 50, drawY, 35, 0);

				if (draggingInventoryID != -1
						&& (mc.getInventoryItemsCount()[draggingInventoryID]) != -1) {
					ItemDef def = EntityHandler.getItemDef(mc.getInventoryItems()[draggingInventoryID]);
					mc.getSurface().drawSpriteClipping(mc.spriteSelect(def),
							mc.getMouseX(), mc.getMouseY(), 48, 32, def.getPictureMask(), 0, false, 0, 1);
					if (def.getNotedFormOf() >= 0) {
						ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
						mc.getSurface().drawSpriteClipping(mc.spriteSelect(originalDef),
								mc.getMouseX() + 7, mc.getMouseY() + 8, 29, 19,
								originalDef.getPictureMask(), 0, false, 0, 1);
					}
				}

				// Draw inventory-only items
				if (inventorySlot < mc.getInventoryItemCount() && mc.getInventoryItems()[inventorySlot] != -1) {
					ItemDef def = EntityHandler.getItemDef(mc.getInventoryItems()[inventorySlot]);
					mc.getSurface().drawSpriteClipping(mc.spriteSelect(def), drawX, drawY, 48, 32,
							def.getPictureMask(), 0, false, 0, 1);

					if (def.getNotedFormOf() >= 0) { // Noted items
						ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
						mc.getSurface().drawSpriteClipping(mc.spriteSelect(originalDef),
								drawX + 7, drawY + 8, 29, 19, originalDef.getPictureMask(), 0, false,
								0, 1);
					}
					if (def.isStackable()) { // Stack items
						drawString(mudclient.formatStackAmount(mc.getInventoryItemsCount()[inventorySlot]),
								drawX + 1, drawY + 10, 1, '\uffff');
					}
				}

				if (mc.getMouseX() > drawX && mc.getMouseX() < drawX + 49 && mc.getMouseY() > drawY
						&& mc.getMouseY() < drawY + 34 && !rightClickMenu && mc.inputX_Action == InputXAction.ACT_0) {
					// Right-click Inventory Item (Menu)
					if (mc.getMouseClick() == 2) {
						if (mc.getMouseX() > drawX && mc.getMouseX() < drawX + 49 && mc.getMouseY() > drawY
								&& mc.getMouseY() < drawY + 34 && inventorySlot < mc.getInventoryItemCount()
								&& mc.getInventoryItems()[inventorySlot] != -1) {
							selectedInventorySlot = inventorySlot;
							rightClickMenuX = mc.getMouseX();
							rightClickMenuY = mc.getMouseY();
							rightClickMenu = true;
							mc.setMouseClick(0);
						}

						// Right-click Inventory Item (Organizing)
					} else if (organizeMode > 0 && !rightClickMenu && mc.inputX_Action == InputXAction.ACT_0) {
						if (mc.getMouseButtonDownTime() > 0 && mc.getMouseButtonDown() == 1) {
							if (mc.getMouseButtonDownTime() < 2
									&& inventorySlot < mc.getInventoryItemCount()
									&& mc.getInventoryItems()[inventorySlot] != -1) {
								draggingInventoryID = inventorySlot;
							}
						} else {
							if (draggingInventoryID > -1 && mc.getInventoryItems()[inventorySlot] != -1) {
								sendInventoryOrganize(draggingInventoryID, inventorySlot);
							}
							draggingInventoryID = -1;
						}

						// Deposit Clicked Item
					} else if (mc.getMouseClick() == 1 && !rightClickMenu) {
						selectedInventorySlot = inventorySlot;
						sendDeposit(1);
					}
				}

				// Draw item name on hover
				if (mc.getMouseX() > drawX && mc.getMouseX() < drawX + 49 && mc.getMouseY() > drawY && mc.getMouseY() < drawY + 34) {
					if (mc.getInventoryItems()[inventorySlot] != -1) {
						drawString(EntityHandler.getItemDef(mc.getInventoryItems()[inventorySlot]).getName(), x + 7, y + 15, 0, 0xFFFFFF);
					}

				}

				// Bank size hover
				else if (mc.getMouseX() <= x + 6 || mc.getMouseX() >= x + 496 || mc.getMouseY() <= y + 57 ||
						(mc.getMouseY() >= y + 193 && mc.getMouseY() <= y + 227) || mc.getMouseY() >= y + 329) {
					drawString(Integer.toString(bankItems.size()), x + 7, y + 15, 1, 0xFFFFFF);
					mc.getSurface().drawLineVert(x + 9 + (mc.getSurface().stringWidth(0, "" + bankItems.size())), y + 6, 0xFFFFFF, 10);
					drawString(Integer.toString(mc.bankItemsMax), x + 13 + (mc.getSurface().stringWidth(0, "" + bankItems.size())), y + 15, 1, 0xFFFFFF);
				}

				inventorySlot++;
			}
		}
		if (rightClickMenu && mc.inputX_Action == InputXAction.ACT_0) {
			// Recalcs menu height and width based on fontSize
			int menuHeight = fontSizeHeight * 7 + 5;
			if (lastXAmount > 1 && lastXAmount != 5 && lastXAmount != 10 && lastXAmount != 50) {
				menuHeight = fontSizeHeight * 8 + 5;
			}
			int menuWidth = mc.getSurface().stringWidth(fontSize, "Withdraw-All-But-1") + 8;

			if (selectedBankSlot > -1) {
				int checkMenuWidth = mc.getSurface().stringWidth(fontSize, EntityHandler.getItemDef(bankItems.get(selectedBankSlot).itemID).getName()) + 8;
				if (menuWidth < checkMenuWidth) {
					menuWidth = checkMenuWidth;
				}

				if (rightClickMenuX + menuWidth >= mc.getGameWidth()) {
					rightClickMenuX = mc.getGameWidth() - menuWidth - 5;
				}
				if (rightClickMenuY + menuHeight + 15 >= mc.getGameHeight()) {
					rightClickMenuY = mc.getGameHeight() - menuHeight - 25;
				}

				if (mc.getMouseX() >= rightClickMenuX - 10 && mc.getMouseX() <= rightClickMenuX + menuWidth + 5
						&& mc.getMouseY() >= rightClickMenuY - 5
						&& mc.getMouseY() <= rightClickMenuY + menuHeight + 20) {

					if (10 + mc.getSurface().stringWidth(fontSize, EntityHandler.getItemDef(bankItems.get(selectedBankSlot).itemID).getName()) > menuWidth) {
						menuWidth = 10 + mc.getSurface().stringWidth(fontSize, EntityHandler.getItemDef(bankItems.get(selectedBankSlot).itemID).getName());
					}

					mc.getSurface().drawBoxAlpha(rightClickMenuX, rightClickMenuY, menuWidth + 2, menuHeight + 20, 0x5C5548, 255);
					mc.getSurface().drawBoxAlpha(rightClickMenuX + 1, rightClickMenuY + 1, menuWidth, fontSize + 18, 0x000000, 255);
					mc.getSurface().drawBoxBorder(rightClickMenuX + 1, menuWidth, rightClickMenuY + 18, menuHeight + 1, 0x000000);

					drawString(EntityHandler.getItemDef(bankItems.get(selectedBankSlot).itemID).getName(), rightClickMenuX + 4, rightClickMenuY + fontSize + 15, fontSize, 0xFFFFFF);

					int i = 0xffffff;
					if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + 20
							&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight + 20) {
						i = 0xFDFF21;
						if (mc.getMouseClick() == 1) {
							sendWithdraw(1);
						}
					}

					int is = 0xffffff;
					if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight + 21
							&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 2 + 20) {
						is = 0xFDFF21;
						if (mc.getMouseClick() == 1) {
							sendWithdraw(5);
						}
					}
					int i3 = 0xffffff;
					if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 2 + 21
							&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 3 + 20) {
						i3 = 0xFDFF21;
						if (mc.getMouseClick() == 1) {
							sendWithdraw(10);
						}
					}
					int i4 = 0xffffff;
					if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 3 + 21
							&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 4 + 20) {
						i4 = 0xFDFF21;
						if (mc.getMouseClick() == 1) {
							sendWithdraw(50);
						}
					}
					int i5 = 0xffffff, i6 = 0xffffff, i7 = 0xffffff, i8 = 0xffffff;
					if (lastXAmount > 1 && lastXAmount != 5 && lastXAmount != 10 && lastXAmount != 50) {

						// Send "Withdraw X" after filling out input.
						if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 4 + 21
								&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 5 + 20) {
							i5 = 0xFDFF21;
							if (mc.getMouseClick() == 1) {
								saveXAmount = false;
								sendWithdraw(lastXAmount);
							}
						}

						// Open "Withdraw X" input.
						if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 5 + 21
								&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 6 + 20) {
							i6 = 0xFDFF21;
							if (mc.getMouseClick() == 1) {
								saveXAmount = true;
								mc.showItemModX(InputXPrompt.bankWithdrawX, InputXAction.BANK_WITHDRAW, true);
								rightClickMenu = false;
								mc.setMouseClick(0);
							}
						}

						// Send "Withdraw" with max value.
						if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 6 + 21
								&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 7 + 20) {
							i7 = 0xFDFF21;
							if (mc.getMouseClick() == 1) {
								saveXAmount = false;
								sendWithdraw(Integer.MAX_VALUE);
							}
						}

						// Send "Withdraw" with "all-but-one".
						if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 7 + 21
								&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 8 + 20) {
							i8 = 0xFDFF21;
							if (mc.getMouseClick() == 1) {
								saveXAmount = false;
								sendWithdraw(bankItems.get(selectedBankSlot).amount - 1);
							}
						}
					} else {
						if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 4 + 21
								&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 5 + 20) {
							i5 = 0xFDFF21;
							if (mc.getMouseClick() == 1) {
								saveXAmount = true;
								mc.showItemModX(InputXPrompt.bankWithdrawX, InputXAction.BANK_WITHDRAW, true);
								rightClickMenu = false;
								mc.setMouseClick(0);
							}
						}
						if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 5 + 21
								&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 6 + 20) {
							i6 = 0xFDFF21;
							if (mc.getMouseClick() == 1) {
								saveXAmount = false;
								sendWithdraw(bankItems.get(selectedBankSlot).amount - 1);
							}
						}
						if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 6 + 21
								&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 7 + 20) {
							i7 = 0xFDFF21;
							if (mc.getMouseClick() == 1) {
								saveXAmount = false;
								sendWithdraw(Integer.MAX_VALUE);
							}
						}
					}

					drawString("Withdraw-1", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight + 20, fontSize, i);
					drawString("Withdraw-5", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 2 + 20, fontSize, is);
					drawString("Withdraw-10", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 3 + 20, fontSize, i3);
					drawString("Withdraw-50", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 4 + 20, fontSize, i4);
					if (lastXAmount > 1 && lastXAmount != 5 && lastXAmount != 10 && lastXAmount != 50) {
						drawString("Withdraw-" + lastXAmount, rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 5 + 20, fontSize, i5);
						drawString("Withdraw-X", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 6 + 20, fontSize, i6);
						drawString("Withdraw-All", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 7 + 20, fontSize, i7);
						drawString("Withdraw-All-But-1", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 8 + 20, fontSize, i8);
					} else {
						drawString("Withdraw-X", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 5 + 20, fontSize, i5);
						drawString("Withdraw-All-But-1", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 6 + 20, fontSize, i6);
						drawString("Withdraw-All", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 7 + 20, fontSize, i7);
					}
				} else {
					rightClickMenu = false;
					selectedBankSlot = -1;
				}
			} else if (selectedInventorySlot > -1) {
				// Recalcs menu height and width based on fontSize
				menuHeight = fontSizeHeight * 6;

				menuWidth = mc.getSurface().stringWidth(fontSize, "Deposit-All") + 8;
				int checkMenuWidth = mc.getSurface().stringWidth(fontSize, EntityHandler.getItemDef(mc.getInventoryItems()[selectedInventorySlot]).getName()) + 8;
				if (menuWidth < checkMenuWidth) {
					menuWidth = checkMenuWidth;
				}

				if (rightClickMenuX + menuWidth >= mc.getGameWidth()) {
					rightClickMenuX = mc.getGameWidth() - menuWidth - 5;
				}
				if (rightClickMenuY + menuHeight + 15 >= mc.getGameHeight()) {
					rightClickMenuY = mc.getGameHeight() - menuHeight - 25;
				}

				if (mc.getMouseX() >= rightClickMenuX - 10 && mc.getMouseX() <= rightClickMenuX + menuWidth + 5
						&& mc.getMouseY() >= rightClickMenuY - 5
						&& mc.getMouseY() <= rightClickMenuY + menuHeight + 20) {
					mc.getSurface().drawBoxAlpha(rightClickMenuX, rightClickMenuY, menuWidth + 2, menuHeight + 20, 0x5C5548, 255);
					mc.getSurface().drawBoxAlpha(rightClickMenuX + 1, rightClickMenuY + 1, menuWidth, 16, 0x000000, 255);
					mc.getSurface().drawBoxBorder(rightClickMenuX + 1, menuWidth, rightClickMenuY + 18, menuHeight + 1, 0x000000);

					drawString(EntityHandler.getItemDef(mc.getInventoryItems()[selectedInventorySlot]).getName(), rightClickMenuX + 4, rightClickMenuY + 13, fontSize, 0xFFFFFF);

					int i = 0xffffff;
					if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + 15
							&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight + 15) {
						i = 0xFDFF21;
						if (mc.getMouseClick() == 1) {
							sendDeposit(1);
						}
					}
					int is = 0xffffff;
					if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight + 16
							&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 2 + 15) {
						is = 0xFDFF21;
						if (mc.getMouseClick() == 1) {
							sendDeposit(5);
						}
					}
					int i3 = 0xffffff;
					if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 2 + 16
							&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 3 + 15) {
						i3 = 0xFDFF21;
						if (mc.getMouseClick() == 1) {
							sendDeposit(10);
						}
					}
					int i4 = 0xffffff;
					if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 3 + 16
							&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 4 + 15) {
						i4 = 0xFDFF21;
						if (mc.getMouseClick() == 1) {
							sendDeposit(50);
						}
					}
					int i5 = 0xffffff;
					if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 4 + 16
							&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 5 + 15) {
						i5 = 0xFDFF21;
						if (mc.getMouseClick() == 1) {
							mc.showItemModX(InputXPrompt.bankDepositX, InputXAction.BANK_DEPOSIT, true);
							mc.setMouseClick(0);
							rightClickMenu = false;
						}
					}
					int i6 = 0xffffff;
					if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + fontSizeHeight * 5 + 16
							&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight * 6 + 15) {
						i6 = 0xFDFF21;
						if (mc.getMouseClick() == 1) {
							sendDeposit(Integer.MAX_VALUE);
						}
					}
					drawString("Deposit-1", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight + 15, fontSize, i);
					drawString("Deposit-5", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 2 + 15, fontSize, is);
					drawString("Deposit-10", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 3 + 15, fontSize, i3);
					drawString("Deposit-50", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 4 + 15, fontSize, i4);
					drawString("Deposit-X", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 5 + 15, fontSize, i5);
					drawString("Deposit-All", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight * 6 + 15, fontSize, i6);
				} else {
					rightClickMenu = false;
					selectedInventorySlot = -1;
				}
			}
		}
		return true;
	}

	private void sendNoteMode() {
		mc.packetHandler.getClientStream().newPacket(199);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(1);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(swapNoteMode ? 1 : 0);
		mc.packetHandler.getClientStream().finishPacket();
	}

	private void sendInventoryOrganize(int draggingInventoryID2, int inventorySlot) {
		mc.packetHandler.getClientStream().newPacket(199);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(organizeMode == 1 ? 4 : 5);
		mc.packetHandler.getClientStream().writeBuffer1.putInt(draggingInventoryID2);
		mc.packetHandler.getClientStream().writeBuffer1.putInt(inventorySlot);
		mc.packetHandler.getClientStream().finishPacket();
		mc.setMouseClick(0);
	}

	private void sendItemSwap(int draggingBankSlot2, int currentSlot) {
		if (!bank.getControlText(bankSearch).isEmpty()) {
			return;
		}
		mc.packetHandler.getClientStream().newPacket(199);
		mc.packetHandler.getClientStream().writeBuffer1.putByte((organizeMode == 1 ? 2 : 3));
		mc.packetHandler.getClientStream().writeBuffer1.putInt(draggingBankSlot2);
		mc.packetHandler.getClientStream().writeBuffer1.putInt(currentSlot);
		mc.packetHandler.getClientStream().finishPacket();
		mc.setMouseClick(0);
	}

	public void sendDeposit(int i) {
		if (Config.S_WANT_CUSTOM_BANKS) {
			mc.packetHandler.getClientStream().newPacket(23);
			mc.packetHandler.getClientStream().writeBuffer1.putShort(mc.getInventoryItems()[selectedInventorySlot]);
			if (i > mc.getInventoryCount(mc.getInventoryItems()[selectedInventorySlot])) {
				i = mc.getInventoryCount(mc.getInventoryItems()[selectedInventorySlot]);
			}
			mc.packetHandler.getClientStream().writeBuffer1.putInt(i);
			mc.packetHandler.getClientStream().finishPacket();
			rightClickMenu = false;
			mc.setMouseClick(0);
			mc.setMouseButtonDown(0);
			if (mc.getInventoryCount(mc.getInventoryItems()[selectedInventorySlot]) < 1) {
				selectedInventorySlot = -1;
			}
		} else {
			// Authentic Bank Deposit
			super.sendDeposit(i);
		}
	}

	private void sendDepositAll() {
		for (int i = 0; i < mc.getInventoryItemCount(); i++) {
			selectedInventorySlot = i;
			sendDeposit(Integer.MAX_VALUE);
		}
	}

	public void sendWithdraw(int i) {
		if (Config.S_WANT_CUSTOM_BANKS) {
			mc.packetHandler.getClientStream().newPacket(22);
			mc.packetHandler.getClientStream().writeBuffer1.putShort(bankItems.get(selectedBankSlot).itemID);
			if (i > bankItems.get(selectedBankSlot).amount) {
				i = bankItems.get(selectedBankSlot).amount;
			}
			mc.packetHandler.getClientStream().writeBuffer1.putInt(i);
			mc.packetHandler.getClientStream().finishPacket();
			rightClickMenu = false;
			selectedBankSlot = -1;
			mc.setMouseClick(0);
			mc.setMouseButtonDown(0);
		} else {
			super.sendWithdraw(i);
		}
	}

	public boolean keyDown(int key) {
		if (bank.focusOn(bankSearch) && mc.inputX_Action == InputXAction.ACT_0) {
			if (mc.bankPage != 0)
				mc.bankPage = 0;
			bank.keyPress(key);
		}
		return true;
	}

	private void resetVar() {
		//bank.resetList(this.bankScroll);
		bank.clearList(this.bankSearch);
		bank.setText(this.bankSearch, "");
		bank.setFocus(-1);
		//mc.bankPage = 0;
	}

	public enum BankTabShow {
		FIRST_ITEM_IN_TAB,
		DIGIT;
	}

}
