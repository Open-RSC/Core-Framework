package com.openrsc.interfaces.misc;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;

import java.util.ArrayList;

import com.openrsc.client.model.Sprite;
import orsc.Config;
import orsc.enumerations.InputXAction;
import orsc.graphics.gui.InputXPrompt;
import orsc.mudclient;
import orsc.util.GenUtil;

import static orsc.Config.*;

public final class CustomBankInterface extends BankInterface {
	private static int fontSize = Config.isAndroid() ? Config.C_MENU_SIZE : 1;
	private static int fontSizeHeight;
	private int[] equipmentViewOrder = new int[]{0, 1, 2, 5, 4, 3, 8, 9, 6, 7, 10};
	private final int presetCount = 2;
	public Preset[] presets = new Preset[presetCount];
	public int selectedInventorySlot = -1;
	private int selectedEquipmentSlot = -1;
	private int selectedPresetSlot = 0;
	private int selectedPresetTab = -1;
	public int bankSearch;
	public int bankScroll;
	public int lastXAmount = 0;
	private int hotkey = -1;
	private boolean saveXAmount = false;
	private boolean rightClickMenu;
	private int organizeMode = 0;
	private boolean equipmentMode = false;
	private boolean presetMode = false;
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
		int tapPresetXOffset = x + 380 - presetCount * 17;
		int tapPresetYOffset = y + 3;
		fontSizeHeight = mc.getSurface().fontHeight(fontSize);

		if (presetMode) {
			renderPresetEdit();
			mc.setMouseClick(0);
			return true;
		}

		//Keyboard controls
		if (mc.controlPressed) {
			switch (hotkey) {
				case (int)'1':
					loadPreset(0);
					break;
				case (int)'2':
					loadPreset(1);
					break;
				case 4:
					if (equipmentMode)
						sendDepositAllEquipment();
					else
						sendDepositAllInventory();
					break;
				case -1:
				default:
					break;
			}
			hotkey = -1;
		}


		mc.getSurface().drawBox(x, y, width, 21, 192);
		int colour = 0x989898;
		mc.getSurface().drawBoxAlpha(x, y + 21, width, 309, colour, 160);
		mc.getSurface().drawBoxBorder(x, width, y, 331, 0x000000);

		drawString("The Bank of " + Config.SERVER_NAME, x + 196, y + 15, 1, 0xFFFFFF);

		int j3 = 0xFFFFFF;
		if (mc.getMouseX() > x + 415 && mc.getMouseY() >= y && mc.getMouseX() < x + width && mc.getMouseY() < y + 12 + 9) {
			j3 = 16711680;
		}
		if (S_WANT_BANK_PRESETS) {
			if (mc.getMouseX() >= tapPresetXOffset && mc.getMouseX() < tapPresetXOffset + presetCount * 17
				&& mc.getMouseY() >= tapPresetYOffset && mc.getMouseY() < tapPresetYOffset + 17) {
				if (mc.mouseButtonClick == 0)
					selectedPresetTab = (mc.getMouseX() - tapPresetXOffset) / 17;
				else if (selectedPresetTab != -1){
					loadPreset(selectedPresetTab);
					mc.mouseButtonClick = 0;
				}
			} else if (mc.mouseButtonClick == 1 && mc.getMouseX() > x + 380 && mc.getMouseX() < x + 420
				&& mc.getMouseY() >= y && mc.getMouseY() < y + 12 + 9) {
				presetMode = true;
			} else
				selectedPresetTab = -1;

			for (int p = 0; p < presetCount; p++) {
				mc.getSurface().drawBoxAlpha(tapPresetXOffset + 17 * p, tapPresetYOffset, 17, 17, selectedPresetTab == p ? 0x7E1F1C : 0x5A5A55 , 160);
				mc.getSurface().drawBoxBorder(tapPresetXOffset + 17 * p, 17, tapPresetYOffset, 17, 0x000000);
				drawString("" + (p + 1), tapPresetXOffset + 17 * p + 6, tapPresetYOffset + fontSizeHeight, 1, 0xFFFFFF);

			}

			mc.getSurface().drawSpriteClipping(mc.spriteSelect(EntityHandler.GUIPARTS.BANK_PRESET_OPTIONS.getDef()),
				x + 390, y + 3, 17, 17, 0, 0, false, 0, 0, 0xCCFFFFFF);
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
		int boxColourGreyed = 0x101010;
		int modeWidth = Config.S_WANT_EQUIPMENT_TAB ? 55 : 75;
		int modeOffset = x + (Config.S_WANT_EQUIPMENT_TAB ? 162 : 112);
		int textStart = modeOffset + modeWidth / 2 - 14;

		if (mc.getMouseClick() != 0 || mc.getMouseButtonDownTime() >= 0) {
			if (mc.getMouseX() > x + width || mc.getMouseX() < x
				|| mc.getMouseY() > y + height || mc.getMouseY() < y) {
				if (!rightClickMenu && mc.mouseButtonClick != 0) {
					resetVar();
					bankClose();
				}
			}

			if (mc.getMouseClick() != 0 && !rightClickMenu) {
				if (mc.getMouseX() >= x + 420 && mc.getMouseY() >= y && mc.getMouseX() < x + width
					&& mc.getMouseY() < y + 12 + 9) { // close bank button
					resetVar();
					bankClose();
				} else if (mc.getMouseX() >= x + 8 && mc.getMouseX() <= x + 83 && mc.getMouseY() >= y + 206
					&& mc.getMouseY() <= y + 220) {
					if (equipmentMode)
						sendDepositAllEquipment();
					else
						sendDepositAllInventory();
				} else if (Config.S_WANT_EQUIPMENT_TAB && mc.getMouseX() >= modeOffset - 68
					&& mc.getMouseX() < modeOffset - 40 && mc.getMouseY() > y + 197 && mc.getMouseY() < y + 225) {
					equipmentMode = false;
					selectedInventorySlot = -1;
					selectedBankSlot = -1;
					selectedEquipmentSlot = -1;
					rightClickMenu = false;
				} else if (Config.S_WANT_EQUIPMENT_TAB && mc.getMouseX() >= modeOffset - 40
					&& mc.getMouseX() < modeOffset - 12 && mc.getMouseY() > y + 197 && mc.getMouseY() < y + 225) {
					equipmentMode = true;
					selectedInventorySlot = -1;
					selectedBankSlot = -1;
					selectedEquipmentSlot = -1;
					rightClickMenu = false;
					swapNoteMode = false;
					sendNoteMode();
				} else if (mc.getMouseX() >= x + 349 && mc.getMouseX() <= x + 422 && mc.getMouseY() >= y + 206
					&& mc.getMouseY() <= y + 220) {
					swapNoteMode = false;
					sendNoteMode();
				} else if (!equipmentMode && mc.getMouseX() >= x + 423 && mc.getMouseX() <= x + 498 && mc.getMouseY() >= y + 206
					&& mc.getMouseY() <= y + 220) {
					swapNoteMode = true;
					sendNoteMode();
				} else if (mc.getMouseX() >= modeOffset && mc.getMouseX() <= modeOffset + modeWidth && mc.getMouseY() >= y + 206
					&& mc.getMouseY() <= y + 220) {
					organizeMode = 1;
				} else if (mc.getMouseX() >= modeOffset + modeWidth && mc.getMouseX() <= modeOffset + 2 * modeWidth && mc.getMouseY() >= y + 206
					&& mc.getMouseY() <= y + 220) {
					organizeMode = 2;
				} else if (mc.getMouseX() >= modeOffset + 2 * modeWidth && mc.getMouseX() <= modeOffset + 3 * modeWidth && mc.getMouseY() >= y + 206
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
				ItemDef bankDef = null;
				if (bankSlotStart >= 0 && bankSlotStart < searchList.size()) {
					bankItem = searchList.get(bankSlotStart);
				}
				if (bankItem != null)
					bankDef = EntityHandler.getItemDef(bankItem.itemID);

				int drawX = x + 6 + horizonalSlots * 49;
				int drawY = y + 57 + verticalSlots * 34;

				if (!equipmentMode || (equipmentMode && bankDef != null && bankDef.isWieldable()))
					mc.getSurface().drawBoxAlpha(drawX, drawY, 49, 34, boxColour, 160);
				else
					mc.getSurface().drawBoxAlpha(drawX, drawY, 49, 34, boxColourGreyed, 160);

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
								def.getPictureMask(), 0, false, 0, 1, (equipmentMode && !def.isWieldable()) ? 0x60FFFFFF : 0xFFFFFFFF);
							if (def.getNotedFormOf() >= 0) {
								ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
								mc.getSurface().drawSpriteClipping(mc.spriteSelect(originalDef), drawX + 7,
									drawY + 8, 29, 19, originalDef.getPictureMask(), 0, false, 0, 1);
							}
							drawString(mudclient.formatStackAmount(bankItem.amount), drawX + 1, drawY + 10, 1, (equipmentMode && !def.isWieldable()) ? 0x404040 : 65280);
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
							if (equipmentMode && EntityHandler.getItemDef(bankItem.itemID).isWieldable()) {
								mc.packetHandler.getClientStream().newPacket(172);
								mc.packetHandler.getClientStream().writeBuffer1.putShort(selectedBankSlot);
								mc.packetHandler.getClientStream().finishPacket();
								selectedBankSlot = -1;
								rightClickMenu = false;
							} else if (!equipmentMode){
								sendWithdraw(1);
							}
						}
					}

					// Right click menu
					if (mc.getMouseX() > drawX && mc.getMouseX() < drawX + 49 && mc.getMouseY() > drawY
						&& mc.getMouseY() < drawY + 34 && bankSlotStart < bankItems.size()
						&& bankItems.get(bankSlotStart).itemID != -1 && mc.inputX_Action == InputXAction.ACT_0) {
						if (mc.getMouseClick() == 2) {
							selectedBankSlot = bankItem.bankID;
							if (!equipmentMode || (equipmentMode && EntityHandler.getItemDef(bankItem.itemID).isWieldable())) {
								rightClickMenuX = mc.getMouseX();
								rightClickMenuY = mc.getMouseY();
								rightClickMenu = true;
							}
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

		if (Config.S_WANT_EQUIPMENT_TAB) {
			mc.getSurface().drawBoxAlpha(modeOffset - 68, settingsY - 10, 28, 28, equipmentMode ? 0x5A5A55 : 0x7E1F1C, 192);
			mc.getSurface().drawBoxAlpha(modeOffset - 40, settingsY - 10, 28, 28, equipmentMode ? 0x7E1F1C : 0x5A5A55, 192);
			mc.getSurface().drawBoxBorder(modeOffset - 68, 28, settingsY - 10, 28, 0x2D2C24);
			mc.getSurface().drawBoxBorder(modeOffset - 67, 26, settingsY - 9, 26, 0x706452);
			mc.getSurface().drawBoxBorder(modeOffset - 40, 28, settingsY - 10, 28, 0x2D2C24);
			mc.getSurface().drawBoxBorder(modeOffset - 39, 26, settingsY - 9, 26, 0x706452);
			mc.getSurface().drawSpriteClipping(mc.spriteSelect(EntityHandler.GUIPARTS.BANK_EQUIP_BAG.getDef()),
				modeOffset - 67, settingsY - 10,
				26,26,0x0,0x0,false,0,0);
			mc.getSurface().drawSpriteClipping(mc.spriteSelect(EntityHandler.GUIPARTS.BANK_EQUIP_HELM.getDef()),
				modeOffset - 39, settingsY - 10,
				26,26,0x0,0x0,false,0,0);
		}
		drawString("Rearrange mode:", x + 190, settingsY - 3, 1, 0xF89922);

		mc.getSurface().drawBoxAlpha(modeOffset, settingsY - 1, modeWidth, 16, (organizeMode == 1 ? 0x7E1F1C : 0x5A5A55), 192);
		mc.getSurface().drawBoxBorder(modeOffset, modeWidth, settingsY - 1, 16, 0x2D2C24);
		mc.getSurface().drawBoxBorder(modeOffset + 1, modeWidth - 2, settingsY, 14, 0x706452);
		drawString("Swap", textStart, settingsY + 11, 1, 0xffffff);

		mc.getSurface().drawBoxAlpha(modeOffset + modeWidth - 1, settingsY - 1, modeWidth, 16, (organizeMode == 2 ? 0x7E1F1C : 0x5A5A55), 192);
		mc.getSurface().drawBoxBorder(modeOffset + modeWidth - 1, modeWidth, settingsY - 1, 16, 0x2D2C24);
		mc.getSurface().drawBoxBorder(modeOffset + modeWidth, modeWidth - 2, settingsY, 14, 0x706452);
		drawString("Insert", textStart + modeWidth - 3, settingsY + 11, 1, 0xffffff);

		mc.getSurface().drawBoxAlpha(modeOffset + 2 * (modeWidth - 1), settingsY - 1, modeWidth, 16, (organizeMode == 0 ? 0x7E1F1C : 0x5A5A55), 192);
		mc.getSurface().drawBoxBorder(modeOffset + 2 * (modeWidth - 1), modeWidth, settingsY - 1, 16, 0x2D2C24);
		mc.getSurface().drawBoxBorder(modeOffset + 2 * modeWidth - 1, modeWidth - 2, settingsY, 14, 0x706452);
		drawString("None", textStart + 2 * modeWidth, settingsY + 11, 1, 0xffffff);

		drawString("Withdraw as:", x + 378 + 14, settingsY - 3, 1, 0xF89922);

		mc.getSurface().drawBoxAlpha(x + 423 - 75, settingsY - 1, 75, 16, (!swapNoteMode ? 0x7E1F1C : 0x5A5A55), 192);
		mc.getSurface().drawBoxBorder(x + 423 - 75, 75, settingsY - 1, 16, 0x2D2C24);
		mc.getSurface().drawBoxBorder(x + 424 - 75, 73, settingsY, 14, 0x706452);
		drawString("Item", x + 26 + 423 - 75, settingsY + 11, 1, 0xffffff);

		mc.getSurface().drawBoxAlpha(x + 422, settingsY - 1, 75, 16, equipmentMode ? boxColourGreyed : (swapNoteMode ? 0x7E1F1C : 0x5A5A55), 192);
		mc.getSurface().drawBoxBorder(x + 422, 75, settingsY - 1, 16, 0x2D2C24);
		mc.getSurface().drawBoxBorder(x + 423, 73, settingsY, 14, 0x706452);
		drawString("Note", x + 26 + 422, settingsY + 11, 1, 0xffffff);

		// Inventory Items Loop
		if (Config.S_WANT_EQUIPMENT_TAB && equipmentMode) {
			int xOffset = x + 20;
			int yOffset = settingsY + 20;
			Sprite todraw;
			mc.getSurface().drawBoxAlpha(x + 6, yOffset - 1, (width - 16)/2, 104, boxColour, 192);
			mc.getSurface().drawBoxAlpha(xOffset + 231, yOffset - 1, (width - 16)/2 + 1, 104, 0x0, 192);
			mc.getSurface().drawBoxBorder(x + 6, width - 16, yOffset - 1, 104, 0x0);
			mc.getSurface().drawLineVert(xOffset + 231, yOffset - 1, 0x0, 104);
			for (int currSkill = 0; currSkill < 3; ++currSkill) {
				mc.getSurface().drawString(mc.equipmentStatNames[currSkill] + ":@yel@" + mc.playerStatEquipment[currSkill],
					xOffset + 249, yOffset + 26 + currSkill * 13, 0xFFFFFF, 1);
				if (2 > currSkill) {
					mc.getSurface().drawString(
						mc.equipmentStatNames[currSkill + 3] + ":@yel@" + mc.playerStatEquipment[3 + currSkill],
						xOffset + 348, yOffset + 26 + currSkill * 13, 0xFFFFFF, 1);
				}
				mc.getSurface().drawLineHoriz(xOffset, yOffset + 228, 245, 0);
			}
			for (int i = 0; i < Config.S_PLAYER_SLOT_COUNT; i++) {
				if (mc.equippedItems[this.equipmentViewOrder[i]] == null) {
					todraw = mc.spriteSelect(EntityHandler.GUIparts.get(EntityHandler.GUIPARTS.EQUIPSLOT_HELM.id() + this.equipmentViewOrder[i]));
					mc.getSurface().drawSpriteClipping(todraw,
						xOffset,
						yOffset,
						todraw.getWidth(), todraw.getHeight(),
						0, 0, false, 0, 0, 0x80FFFFFF);
				} else {
					todraw = mc.spriteSelect(EntityHandler.GUIPARTS.EQUIPSLOT_HIGHLIGHT.getDef());
					mc.getSurface().drawSpriteClipping(
						todraw,
						xOffset,
						yOffset,
						todraw.getWidth(), todraw.getHeight(),
						mc.equippedItems[this.equipmentViewOrder[i]].getPictureMask(), 0, false, 0, 0, 0xC0FFFFFF);
					todraw = mc.spriteSelect(mc.equippedItems[this.equipmentViewOrder[i]]);
					mc.getSurface().drawSpriteClipping(
						todraw,
						xOffset,
						yOffset,
						todraw.getSomething1(), todraw.getSomething2(),
						mc.equippedItems[this.equipmentViewOrder[i]].getPictureMask(), 0, false, 0, 0);
					if (mc.equippedItems[this.equipmentViewOrder[i]].isStackable())
						mc.getSurface().drawString("" + mc.equippedItemAmount[this.equipmentViewOrder[i]],
							xOffset,
							yOffset + 15, 0xFFFF00, 1);
				}
				if ((i % 4) == 3) {
					xOffset = x + 20;
					yOffset += 35;
				} else {
					xOffset += 55;
				}
			}

			if (selectedEquipmentSlot == -1) {
				int xDiff = mc.getMouseX() - (x + 20);
				int yDiff = mc.getMouseY() - (settingsY + 20);
				if (xDiff % 55 < 49 && xDiff >= 0 && xDiff < x + 234
					&& yDiff >= 0 && yDiff <= 105) {

					selectedEquipmentSlot = (xDiff / 55) + (yDiff / 35) * 4;
					if (selectedEquipmentSlot < Config.S_PLAYER_SLOT_COUNT) {
						selectedEquipmentSlot = this.equipmentViewOrder[selectedEquipmentSlot];
						if (mc.equippedItems[selectedEquipmentSlot] != null) {
							drawString(mc.equippedItems[selectedEquipmentSlot].getName(), x + 7, y + 15, 1, 0xFFFFFF);
							if (mc.getMouseClick() == 2) {
								rightClickMenuX = mc.getMouseX();
								rightClickMenuY = mc.getMouseY();
								rightClickMenu = true;
							} else if (mc.getMouseClick() == 1) {
								mc.packetHandler.getClientStream().newPacket(173);
								mc.packetHandler.getClientStream().writeBuffer1.putShort(mc.equippedItems[selectedEquipmentSlot].id);
								mc.packetHandler.getClientStream().finishPacket();
								selectedEquipmentSlot = -1;
								rightClickMenu = false;
							} else
								selectedEquipmentSlot = -1;
						} else {
							selectedEquipmentSlot = -1;
							rightClickMenu = false;
						}
						mc.setMouseClick(0);
					} else {
						selectedEquipmentSlot = -1;
						rightClickMenu = false;
					}
				}
			}

		} else {
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
		}

		if (rightClickMenu && mc.inputX_Action == InputXAction.ACT_0) {
			// Recalcs menu height and width based on fontSize
			int menuHeight = fontSizeHeight * 7 + 5;
			if (lastXAmount > 1 && lastXAmount != 5 && lastXAmount != 10 && lastXAmount != 50) {
				menuHeight = fontSizeHeight * 8 + 5;
			}
			int menuWidth = mc.getSurface().stringWidth(fontSize, "Withdraw-All-But-1") + 8;
			if (equipmentMode)
				menuHeight = fontSizeHeight + 5;
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


					if (equipmentMode) {
						if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + 20
							&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight + 20) {
							if (mc.getMouseClick() == 1) {
								mc.packetHandler.getClientStream().newPacket(172);
								mc.packetHandler.getClientStream().writeBuffer1.putShort(selectedBankSlot);
								mc.packetHandler.getClientStream().finishPacket();
								selectedBankSlot = -1;
								rightClickMenu = false;
							} else if (mc.getMouseClick() == 0)
								i = 0xFDFF21;
						}
						drawString("Wield", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight + 20, fontSize, i);
					} else {
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


			} else if (selectedEquipmentSlot > -1) {
				if (mc.equippedItems[selectedEquipmentSlot] != null) {
					menuWidth = mc.getSurface().stringWidth(fontSize, mc.equippedItems[selectedEquipmentSlot].getName()) + 8;
					int menuWidth2 = mc.getSurface().stringWidth(fontSize, "Unequip to bank") + 8;
					if (menuWidth2 > menuWidth)
						menuWidth = menuWidth2;
					menuHeight = fontSizeHeight*2 + 8;
					mc.getSurface().drawBoxAlpha(rightClickMenuX, rightClickMenuY, menuWidth + 1, menuHeight, 0x5C5548, 255);
					mc.getSurface().drawBoxAlpha(rightClickMenuX + 1, rightClickMenuY + 1, menuWidth, fontSize + 18, 0x000000, 255);
					mc.getSurface().drawBoxBorder(rightClickMenuX + 1, menuWidth, rightClickMenuY, menuHeight + 1, 0x000000);

					drawString(mc.equippedItems[selectedEquipmentSlot].getName(), rightClickMenuX + 4, rightClickMenuY + fontSize + 15, fontSize, 0xFFFFFF);
					int color = 0xFFFFFFFF;

					if (mc.getMouseX() > rightClickMenuX && mc.getMouseY() >= rightClickMenuY + 25
						&& mc.getMouseX() < rightClickMenuX + menuWidth && mc.getMouseY() < rightClickMenuY + fontSizeHeight + 20) {
						if (mc.getMouseClick() == 1) {
							if (mc.equippedItems[selectedEquipmentSlot] != null) {
								mc.packetHandler.getClientStream().newPacket(173);
								mc.packetHandler.getClientStream().writeBuffer1.putShort(mc.equippedItems[selectedEquipmentSlot].id);
								mc.packetHandler.getClientStream().finishPacket();
							}
							selectedEquipmentSlot = -1;
							rightClickMenu = false;
						} else if (mc.getMouseClick() == 0) {
							color = 0xFDFF21;
						}
					} else if (mc.getMouseX() < rightClickMenuX || mc.getMouseX() > rightClickMenuX + menuWidth
					|| mc.getMouseY() < rightClickMenuY || mc.getMouseY() > rightClickMenuY + menuHeight){
							rightClickMenu = false;
							selectedEquipmentSlot = -1;
					}
					drawString("Unequip to bank", rightClickMenuX + 4, rightClickMenuY + fontSizeHeight + 20, fontSize, color);
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

	private void sendDepositAllInventory() {
		mc.packetHandler.getClientStream().newPacket(24);
		mc.packetHandler.getClientStream().finishPacket();
		rightClickMenu = false;
		mc.setMouseClick(0);
		mc.setMouseButtonDown(0);
		selectedInventorySlot = -1;
	}
	private void sendDepositAllEquipment() {
		mc.packetHandler.getClientStream().newPacket(26);
		mc.packetHandler.getClientStream().finishPacket();
		rightClickMenu = false;
		mc.setMouseClick(0);
		mc.setMouseButtonDown(0);
		selectedEquipmentSlot = -1;
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
		if (mc.inputX_Action == InputXAction.ACT_0) {
			if (bank.focusOn(bankSearch)) {
				if (mc.bankPage != 0)
					mc.bankPage = 0;
				bank.keyPress(key);
			} else {
				this.hotkey = key;
			}
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
	public void initPresets() {
		for (int p = 0; p < presetCount; p++)
			presets[p] = new Preset();

	}

	public void updatePreset(int id, Item[] inventoryItems, Item[] equipmentItems) {
		for (int i = 0; i < Config.S_PLAYER_INVENTORY_SLOTS; i++)
		{
			if (inventoryItems[i] != null) {
				presets[id].inventory[i].setID(inventoryItems[i].getID());
				presets[id].inventory[i].setAmount(inventoryItems[i].getAmount());
			} else {
				presets[id].inventory[i].setID(-1);
				presets[id].inventory[i].setAmount(0);
			}
		}

		for (int i = 0; i < S_PLAYER_SLOT_COUNT; i++)
		{
			if (equipmentItems[i] != null) {
				presets[id].equipment[i].setID(equipmentItems[i].getID());
				presets[id].equipment[i].setAmount(equipmentItems[i].getAmount());
			} else {
				presets[id].equipment[i].setID(-1);
				presets[id].equipment[i].setAmount(0);
			}
		}
	}

	private void saveSetup(int slot) {
		Item[] inventoryItems = new Item[S_PLAYER_INVENTORY_SLOTS];
		Item[] equipmentItems = new Item[S_PLAYER_SLOT_COUNT];
		for (int i = 0; i < S_PLAYER_INVENTORY_SLOTS; i++) {
			if (i < mc.getInventoryItemCount())
				inventoryItems[i] = new Item(mc.getInventoryItemID(i), mc.getInventoryItemSize(i));
			else
				inventoryItems[i] = new Item(-1, 0);
		}
		for (int i = 0; i < S_PLAYER_SLOT_COUNT; i++) {
			if (mc.equippedItems[i] != null) {
				equipmentItems[i] = new Item(mc.equippedItems[i].id, mc.equippedItemAmount[i]);
			}
		}
		updatePreset(slot, inventoryItems, equipmentItems);
		mc.packetHandler.getClientStream().newPacket(27);
		mc.packetHandler.getClientStream().writeBuffer1.putShort(slot);
		mc.packetHandler.getClientStream().finishPacket();
	}

	private void loadPreset(int slot) {
		if (! S_WANT_BANK_PRESETS)
			return;
		mc.packetHandler.getClientStream().newPacket(28);
		mc.packetHandler.getClientStream().writeBuffer1.putShort(slot);
		mc.packetHandler.getClientStream().finishPacket();
	}

	private void renderPresetEdit() {
		int invcolumns = 5;
		int invrows = (int)(Config.S_PLAYER_INVENTORY_SLOTS / invcolumns);
		int inventoryXOffset = x + width - invcolumns * 49 -2;
		int inventoryYOffset = y + 21;
		int presetButtonWidth = width / presetCount;
		mc.getSurface().drawBox(x, y, width, 21, 192);
		mc.getSurface().drawBoxAlpha(x, y + 21, width, 309, 0x989898, 160);
		mc.getSurface().drawBoxBorder(x, width, y, 331, 0x000000);
		drawString("Assign Presets", x + 208, y + 15, 1, 0xFFFFFF);
		int color = 0xFFFFFFFF;
		if (mc.getMouseX() > x + width || mc.getMouseX() < x
		|| mc.getMouseY() > y + height || mc.getMouseY() < y) {
			if (mc.mouseButtonClick != 0)
				presetMode = false;
		} else if (mc.getMouseX() >= x + 420 && mc.getMouseX() <= x + 420 + mc.getSurface().stringWidth(1, "Close Window")
		&& mc.getMouseY() >= y && mc.getMouseY() < y+15) {
			if (mc.mouseButtonClick != 0) {
				presetMode = false;
			} else
				color = 0xFFFF0000;
		} else if (mc.getMouseY() >= inventoryYOffset + invrows * 34 + 1
			&& mc.getMouseY() < inventoryYOffset + invrows * 34 + 35) {
			if (mc.mouseButtonClick != 0) {
				selectedPresetSlot = (mc.getMouseX() - x) / presetButtonWidth;
			}
		} else if (mc.getMouseY() >= inventoryYOffset + invrows * 34 + 35) {
			if (mc.mouseButtonClick != 0) {
				saveSetup(selectedPresetSlot);
			}
		}
		drawString("Close Window", x + 420, y + 15, 1, color);
		for (int i = 0; i < presetCount; i++) {
			mc.getSurface().drawBoxAlpha(x + i * presetButtonWidth, inventoryYOffset + invrows * 34 + 1, presetButtonWidth, 33, selectedPresetSlot == i ? 0x7E1F1C :0x989898, 160);
			mc.getSurface().drawBoxBorder(x + i * presetButtonWidth, presetButtonWidth, inventoryYOffset + invrows * 34, 34, 0x000000);
			drawString("Preset Slot " + (i + 1), x + presetButtonWidth / 2 + presetButtonWidth * i - mc.getSurface().stringWidth(1, "Preset Slot 1") / 2, inventoryYOffset + invrows * 34 + 21, 1, 0xFFFFFF);
		}

		drawString("Save current setup to this preset slot", x, inventoryYOffset + invrows * 34 + 50,1,0x0);
		//Draw the inventory panel on the screen
		for (int i = 0; i < invcolumns; i++) {
			mc.getSurface().drawLineVert(inventoryXOffset + i * 49, inventoryYOffset, 0, invrows * 34);
		}
		for (int i = 0; i < invrows + 1; i++) {
			mc.getSurface().drawLineHoriz(inventoryXOffset, inventoryYOffset + i * 34, invcolumns * 49, 0);
		}
		int row = 0, col = 0;
		for (int i = 0; i < Config.S_PLAYER_INVENTORY_SLOTS; i++) {
			mc.getSurface().drawBoxAlpha(inventoryXOffset + col * 49 +1, inventoryYOffset + row * 34+1, 48, 33, GenUtil.buildColor(181, 181, 181), 128);
			ItemDef def = presets[selectedPresetSlot].inventory[i].getDef();
			if (def != null) {
				mc.getSurface().drawSpriteClipping(
					mc.spriteSelect(def),
					inventoryXOffset + col * 49 +1, inventoryYOffset + row * 34+1,
					48, 32, def.getPictureMask(), 0,
					false, 0, 0);

				if (def.getNotedFormOf() >= 0) {
					ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
					mc.getSurface().drawSpriteClipping(mc.spriteSelect(originalDef),
						inventoryXOffset + col * 49 +1,inventoryYOffset + row * 34+1, 33, 23,
						originalDef.getPictureMask(), 0, false, 0, 1);
				}
				if (def.isStackable()) {
					mc.getSurface().drawString("" + presets[selectedPresetSlot].inventory[i].getAmount(),
						inventoryXOffset + col * 49 +1,inventoryYOffset + row * 34+1 + fontSizeHeight,
						0xFFFF00, 1);
				}
			}
			col++;
			if (col >= invcolumns) {
				col = 0;
				row++;
			}
		}

		//draw the equipment tab on the screen
		ItemDef equipDef;
		Sprite todraw;
		for (int i = 0; i < Config.S_PLAYER_SLOT_COUNT; i++) {
			equipDef = presets[selectedPresetSlot].equipment[i].getDef();
			if (equipDef == null) {
				todraw = mc.spriteSelect(EntityHandler.GUIparts.get(EntityHandler.GUIPARTS.EQUIPSLOT_HELM.id() + i));
				mc.getSurface().drawSpriteClipping(todraw
					, x + mc.equipIconXLocations[i]
					, y + 21 + mc.equipIconYLocations[i],
					todraw.getWidth(), todraw.getHeight(),
					0, 0, false, 0, 0, 0x80FFFFFF);
			} else {
				todraw = mc.spriteSelect(EntityHandler.GUIPARTS.EQUIPSLOT_HIGHLIGHT.getDef());
				mc.getSurface().drawSpriteClipping(
					todraw,
					x + mc.equipIconXLocations[i],
					y + 21 + mc.equipIconYLocations[i],
					todraw.getWidth(), todraw.getHeight(),
					0, 0, false, 0, 0, 0xC0FFFFFF);
				todraw = mc.spriteSelect(presets[selectedPresetSlot].equipment[i].getDef());
				mc.getSurface().drawSpriteClipping(
					todraw,
					x + mc.equipIconXLocations[i],
					y + 21 + mc.equipIconYLocations[i],
					todraw.getSomething1(), todraw.getSomething2(),
					presets[selectedPresetSlot].equipment[i].getDef().getPictureMask(), 0, false, 0, 0 ^ -15251);
				if (presets[selectedPresetSlot].equipment[i].getDef().isStackable())
					mc.getSurface().drawString("" + presets[selectedPresetSlot].equipment[i].getAmount(),
						x + mc.equipIconXLocations[i] + 2,
						y + 21 + mc.equipIconYLocations[i] + 11, 0xFFFF00, 1);
			}
		}

	}

	public static class Item{
		private int id;
		private int amount;

		public Item() {
			this.id = -1;
			this.amount = 0;
		}

		public Item(int id, int amount) {
			this.id = id;
			this.amount = amount;
		}

		public ItemDef getDef() { return EntityHandler.getItemDef(id); }
		public void setAmount(int amount) { this.amount = amount; }
		public void setID(int id) { this.id = id; }
		public int getAmount() { return this.amount; }
		public int getID() { return this.id; }
	}

	public static class Preset{
		public Item[] inventory;
		public Item[] equipment;

		public Preset() {
			inventory = new Item[Config.S_PLAYER_INVENTORY_SLOTS];
			equipment = new Item[Config.S_PLAYER_SLOT_COUNT];

			for (int i = 0; i < inventory.length; i++)
				inventory[i] = new Item();
			for (int i = 0; i < equipment.length; i++)
				equipment[i] = new Item();
		}
	}
}
