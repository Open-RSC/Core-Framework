package com.openrsc.interfaces.misc;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;
import orsc.Config;
import orsc.enumerations.InputXAction;
import orsc.graphics.gui.InputXPrompt;
import orsc.graphics.gui.Panel;
import orsc.mudclient;

import java.util.ArrayList;

public class BankInterface {
	public static mudclient mc;

	public int selectedBankSlot = -1;
	private boolean swapNoteMode;
	private boolean swapCertMode;

	public int width, height;

	public Panel bank;
	ArrayList<BankItem> bankItems;

	BankInterface(mudclient m) {
		mc = m;
		width = 408; // WIDTH MODIFIER
		height = 334; // HEIGHT MODIFIER
		bank = new Panel(mc.getSurface(), 3);
		bankItems = new ArrayList<>();
	}

	private int selectedBankSlotItemID = -2;
	private int mouseOverBankPageText;

	private ArrayList<Integer> currentBankIDs = new ArrayList<>();
	private ArrayList<Integer> currentBankCounts = new ArrayList<>();

	public boolean onRender() {
		int currMouseX = mc.getMouseX();
		int currMouseY = mc.getMouseY();

		if(!mc.getInitLoginCleared()) {
			swapNoteMode = false;
			swapCertMode = false;
			mc.setInitLoginCleared(true);
		}

		// Set up bank list to loop through later.
		currentBankIDs = new ArrayList<>();
		currentBankCounts = new ArrayList<>();
		for (int i = 0; i < bankItems.size(); i++) {
			currentBankIDs.add(bankItems.get(i).itemID);
			currentBankCounts.add(bankItems.get(i).amount);
		}
		for (int i : mc.getInventoryItems()) {
			if (currentBankIDs.contains(i)) continue;
			currentBankIDs.add(i);
			currentBankCounts.add(0);
		}

		// Set Bank Page
		if (mouseOverBankPageText > 0 && currentBankIDs.size() <= 48)
			mouseOverBankPageText = 0;
		if (mouseOverBankPageText > 1 && currentBankIDs.size() <= 96)
			mouseOverBankPageText = 1;
		if (mouseOverBankPageText > 2 && currentBankIDs.size() <= 144)
			mouseOverBankPageText = 2;
		if (mc.getMouseClick() == 1 || ((mc.getMouseButtonDown() == 1 && mc.getMouseButtonDownTime() > 99999 && Config.isAndroid()) ||
			(mc.getMouseButtonDown() == 1 && mc.getMouseButtonDownTime() > 20 && !Config.isAndroid()))) {
			int selectedX = currMouseX - (mc.getGameWidth() / 2 - width / 2);
			int selectedY = currMouseY - (mc.getGameHeight() / 2 - height / 2 + 20);
			if (selectedX >= 0 && selectedY >= 16 && selectedX < 408 && selectedY < 280) {
				if (mc.inputX_Action == InputXAction.ACT_0) {
					selectSlot(selectedX, selectedY); // Set the slot we clicked on

					selectedX = mc.getGameWidth() / 2 - width / 2;
					selectedY = mc.getGameHeight() / 2 - height / 2 + 20;
				}

				// Check for a transaction
				if (this.selectedBankSlot > -1) {
					checkTransaction(currMouseX, currMouseY, selectedX, selectedY);
				}

				// Select bank page
			} else if (currentBankIDs.size() > 48 && selectedX >= 50 && selectedX <= 115 &&
				selectedY <= 16 && currMouseY > mc.getGameHeight() / 2 - 146) {
				mouseOverBankPageText = 0; // Select page 1
			} else if (currentBankIDs.size() > 48 && selectedX >= 115 && selectedX <= 180 &&
				selectedY <= 16 && currMouseY > mc.getGameHeight() / 2 - 146) {
				mouseOverBankPageText = 1; // Select page 2
			} else if (currentBankIDs.size() > 96 && selectedX >= 180 && selectedX <= 245 &&
				selectedY <= 16 && currMouseY > mc.getGameHeight() / 2 - 146) {
				mouseOverBankPageText = 2; // Select page 3
			} else if (currentBankIDs.size() > 144 && selectedX >= 245 && selectedX <= 310 &&
				selectedY <= 16 && currMouseY > mc.getGameHeight() / 2 - 146) {
				mouseOverBankPageText = 3; // Select page 4

			} else { // Close Bank
				bankClose();
				return false;
			}
		}

		// Draw the top header
		drawBankComponents(currMouseX, currMouseY);
		return true;
	}

	private void selectSlot(int selectedX, int selectedY) {
		int selectedItemSlot = mouseOverBankPageText * 48;
		for (int verticalSlots = 0; verticalSlots < 6; verticalSlots++) {
			for (int horizontalSlots = 0; horizontalSlots < 8; horizontalSlots++) {
				int slotX = 7 + horizontalSlots * 49;
				int slotY = 28 + verticalSlots * 34;

				// If the selection is in area
				if (selectedX > slotX && selectedX < slotX + 49 &&
					selectedY > slotY && selectedY < slotY + 34) {

					// Check if the click was on a bank item.
					if (selectedItemSlot < currentBankIDs.size()
						&& currentBankIDs.get(selectedItemSlot) != -1) {
						if (currentBankCounts.get(selectedItemSlot) > 0 ||
							mc.getInventoryCount(currentBankIDs.get(selectedItemSlot)) > 0) {
							selectedBankSlotItemID = currentBankIDs.get(selectedItemSlot);
							this.selectedBankSlot = selectedItemSlot;
						}
						return;
					}
				}
				selectedItemSlot++;
			}
		}
	}

	private void checkTransaction(int currMouseX, int currMouseY, int selectedX, int selectedY) {
		int itemID = selectedBankSlotItemID;
		int amount = currentBankCounts.get(this.selectedBankSlot);

		final boolean L_WANT_CERT_DEPOSIT = Config.S_WANT_CERT_DEPOSIT && isCert(itemID);

		// Incremental Withdraw or Deposit
		if (currMouseX >= selectedX + 220 && currMouseY >= selectedY + 240
			&& currMouseX < selectedX + 250 && currMouseY <= selectedY + 251) {
			if (mc.mouseButtonItemCountIncrement == 0)
				mc.mouseButtonItemCountIncrement = 1;
			if (Config.S_WANT_BANK_NOTES) {
				this.swapNoteMode = !this.swapNoteMode;
				sendNoteMode();
			} else
				sendWithdraw(mc.mouseButtonItemCountIncrement); // Withdraw 1
		}
		else if (mc.getInventoryCount(itemID) >= 1 && currMouseX >= selectedX + 220 && currMouseY >= selectedY + 265
			&& currMouseX < selectedX + 250 && currMouseY <= selectedY + 276) {
			if (mc.mouseButtonItemCountIncrement == 0)
				mc.mouseButtonItemCountIncrement = 1;
			if (L_WANT_CERT_DEPOSIT) {
				this.swapCertMode = !this.swapCertMode;
				sendCertMode();
			} else
				sendDeposit(mc.mouseButtonItemCountIncrement); // Deposit 1
		}

		// Non incremental Withdraw or deposit
		else if ((mc.getMouseButtonDownTime() < 99999 && Config.isAndroid()) || (mc.getMouseButtonDownTime() < 50 && !Config.isAndroid()) ) {
			if ((amount >= 5 || Config.S_WANT_BANK_NOTES) && currMouseX >= selectedX + 250 && currMouseY >=  selectedY + 240
				&& currMouseX < selectedX + 280 && currMouseY <= selectedY + 251) {
				if (Config.S_WANT_BANK_NOTES) {
					this.swapNoteMode = !this.swapNoteMode;
					sendNoteMode();
				} else
					sendWithdraw(5); // Withdraw 5
			}
			else if ((amount >= 10 || Config.S_WANT_BANK_NOTES) && currMouseX >= selectedX + 280 && currMouseY >= selectedY + 240
				&& currMouseX < selectedX + 305 && currMouseY <= selectedY + 251) {
				if (!Config.S_WANT_BANK_NOTES)
					sendWithdraw(10); // Withdraw 10
			}
			else if ((amount >= 50 || Config.S_WANT_BANK_NOTES) && currMouseX >= selectedX + 305 && currMouseY >= selectedY + 240
				&& currMouseX < selectedX + 335 && currMouseY <= selectedY + 251) {
				if (Config.S_WANT_BANK_NOTES)
					sendWithdraw(1);
				else
					sendWithdraw(50); // Withdraw 50
			}
			else if (currMouseX >= selectedX + 340 && currMouseY >= selectedY + 240
				&& currMouseX < selectedX + 368 && currMouseY <= selectedY + 251) {
				// Withdraw X
				mc.showItemModX(InputXPrompt.bankWithdrawX, InputXAction.BANK_WITHDRAW, true);
				mc.setMouseClick(0);
			}
			else if (currMouseX >= selectedX + 370 && currMouseY >= selectedY + 240
				&& currMouseX < selectedX + 400 && currMouseY <= selectedY + 251) {
				sendWithdraw(Integer.MAX_VALUE); // Withdraw All
			}

			// Depositing
			else if ((mc.getInventoryCount(itemID) >= 5 || L_WANT_CERT_DEPOSIT) && currMouseX >= selectedX + 250 && currMouseY >= selectedY + 265
				&& currMouseX < selectedX + 280 && currMouseY <= selectedY + 276) {
				if (L_WANT_CERT_DEPOSIT) {
					this.swapCertMode = !this.swapCertMode;
					sendCertMode();
				} else
					sendDeposit(5); // Deposit 5
			}
			else if ((mc.getInventoryCount(itemID) >= 10 || L_WANT_CERT_DEPOSIT) && currMouseX >= selectedX + 280 && currMouseY >= selectedY + 265
				&& currMouseX < selectedX + 305 && currMouseY <= selectedY + 276) {
				if (!L_WANT_CERT_DEPOSIT)
					sendDeposit(10); // Deposit 10
			}
			else if ((mc.getInventoryCount(itemID) >= 50 || L_WANT_CERT_DEPOSIT) && currMouseX >= selectedX + 305 && currMouseY >= selectedY + 265
				&& currMouseX < selectedX + 335 && currMouseY <= selectedY + 276) {
				if (L_WANT_CERT_DEPOSIT)
					sendDeposit(1);
				else
					sendDeposit(50); // Deposit 50
			}
			else if (currMouseX >= selectedX + 340 && currMouseY >= selectedY + 265
				&& currMouseX < selectedX + 368 && currMouseY <= selectedY + 276) {
				// Deposit X
				mc.showItemModX(InputXPrompt.bankDepositX, InputXAction.BANK_DEPOSIT, true);
				mc.setMouseClick(0);
			}
			else if (currMouseX >= selectedX + 370 && currMouseY >= selectedY + 265
				&& currMouseX < selectedX + 400 && currMouseY <= selectedY + 276) {
				sendDeposit(Integer.MAX_VALUE); // Deposit All
			}
		}
	}

	private void drawBankComponents(int currMouseX, int currMouseY) {
		int relativeX = mc.getGameWidth() / 2 - width / 2; // WAS 256
		int relativeY = mc.getGameHeight() / 2 - height / 2 + 20; // WAS 170
		mc.getSurface().drawBox(relativeX, relativeY, 408, 12, 192);
		int backgroundColour = 0x989898;
		mc.getSurface().drawBoxAlpha(relativeX, relativeY + 12, 408, 17, backgroundColour, 160);
		mc.getSurface().drawBoxAlpha(relativeX, relativeY + 29, 8, 204, backgroundColour, 160);
		mc.getSurface().drawBoxAlpha(relativeX + 399, relativeY + 29, 9, 204, backgroundColour, 160);
		mc.getSurface().drawBoxAlpha(relativeX, relativeY + 233, 408, 47, backgroundColour, 160);
		drawString("Bank", relativeX + 1, relativeY + 10, 1, 0xffffff);

		// Draw Bank Page Buttons
		drawPageButtons(currMouseX, currMouseY, relativeX, relativeY);

		// Draw Top Descriptions & Close Button
		int closeButtonColour = 0xffffff;
		if (currMouseX > relativeX + 320 && currMouseY >= relativeY + 3 && currMouseX < relativeX + 408 && currMouseY < relativeY + 15)
			closeButtonColour = 0xff0000;
		drawString("Close window", relativeX + 326, relativeY + 10, 1, closeButtonColour);
		drawString("Number in bank in green", relativeX + 7, relativeY + 24, 1, 0x00ff00);
		drawString("Number held in blue", relativeX + 289, relativeY + 24, 1, 0x00ffff);


		// Draw the items in the bank.
		drawBankItems(relativeX, relativeY);

		// Line between Withdraw & Deposit
		mc.getSurface().drawLineHoriz(relativeX + 5, relativeY + 256, width - 8, 0);

		// Draw the Quantity Buttons
		if (this.selectedBankSlot != -1) {
			drawQuantityButtons(currMouseX, currMouseY, relativeX, relativeY);
		} else {
			mc.getSurface().drawColoredStringCentered(relativeX + 204, "Select an object to withdraw or deposit", 0xFFFF00, 0, 3,
					relativeY + 248);
		}
	}

	private void drawPageButtons(int currMouseX, int currMouseY, int relativeX, int relativeY) {
		int pageButtonMargin = 50;
		int pageButtonColour = 0xffffff;
		if (mouseOverBankPageText == 0)
			pageButtonColour = 0xff0000;
		else if (currMouseX > relativeX + pageButtonMargin && currMouseY >= relativeY + 4
			&& currMouseX < relativeX + pageButtonMargin + 65 && currMouseY < relativeY + 16)
			pageButtonColour = 0xffff00;
		drawString("<page 1>", relativeX + pageButtonMargin, relativeY + 10, 1, pageButtonColour);
		pageButtonMargin += 65;
		if (currentBankIDs.size() > 48) {
			pageButtonColour = 0xffffff;
			if (mouseOverBankPageText == 1)
				pageButtonColour = 0xff0000;
			else if (currMouseX > relativeX + pageButtonMargin && currMouseY >= relativeY + 4
				&& currMouseX < relativeX + pageButtonMargin + 65 && currMouseY < relativeY + 16)
				pageButtonColour = 0xffff00;
			drawString("<page 2>", relativeX + pageButtonMargin, relativeY + 10, 1, pageButtonColour);
			pageButtonMargin += 65;
		}
		if (currentBankIDs.size() > 96) {
			pageButtonColour = 0xffffff;
			if (mouseOverBankPageText == 2)
				pageButtonColour = 0xff0000;
			else if (currMouseX > relativeX + pageButtonMargin && currMouseY >= relativeY + 4
				&& currMouseX < relativeX + pageButtonMargin + 65 && currMouseY < relativeY + 16)
				pageButtonColour = 0xffff00;
			drawString("<page 3>", relativeX + pageButtonMargin, relativeY + 10, 1, pageButtonColour);
			pageButtonMargin += 65;
		}
		if (currentBankIDs.size() > 144) {
			pageButtonColour = 0xffffff;
			if (mouseOverBankPageText == 3)
				pageButtonColour = 0xff0000;
			else if (currMouseX > relativeX + pageButtonMargin && currMouseY >= relativeY + 4
				&& currMouseX < relativeX + pageButtonMargin + 65 && currMouseY < relativeY + 16)
				pageButtonColour = 0xffff00;
			drawString("<page 4>", relativeX + pageButtonMargin, relativeY + 10, 1, pageButtonColour);
		}
	}

	private void drawBankItems(int relativeX, int relativeY) {
		int inventorySlot = mouseOverBankPageText * 48;
		int inventoryCount;
		for (int verticalSlots = 0; verticalSlots < 6; verticalSlots++) {
			for (int horizontalSlots = 0; horizontalSlots < 8; horizontalSlots++) {
				int slotX = relativeX + 7 + horizontalSlots * 49;
				int slotY = relativeY + 28 + verticalSlots * 34;

				// Background Colour of Bank Tile
				if (this.selectedBankSlot == inventorySlot) { // Selected
					mc.getSurface().drawBoxAlpha(slotX, slotY, 49, 34, 0xff0000, 160);
				} else { // Not Selected
					mc.getSurface().drawBoxAlpha(slotX, slotY, 49, 34, 0xd0d0d0, 160);
				}

				mc.getSurface().drawBoxBorder(slotX, 50, slotY, 35, 0);

				// Draw Item Sprite From Bank
				if (inventorySlot < currentBankIDs.size() && currentBankIDs.get(inventorySlot) != -1
					&& (currentBankCounts.get(inventorySlot) > 0 || mc.getInventoryCount(currentBankIDs.get(inventorySlot)) > 0)) {

					ItemDef def = EntityHandler.getItemDef(currentBankIDs.get(inventorySlot));
					mc.getSurface().drawSpriteClipping(
						mc.spriteItem + def.getSprite(),
						slotX, slotY, 48, 32,
						def.getPictureMask(),
						0, false, 0, 1);
					if (def.getNotedFormOf() >= 0) { // Noted items
						ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
						mc.getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(),
							slotX + 7, slotY + 5, 29, 19, originalDef.getPictureMask(), 0, false,
							0, 1);
					}

					drawString(""+currentBankCounts.get(inventorySlot), slotX + 1, slotY + 10, 1, 0x00ff00); // Amount in bank (green)

					inventoryCount = mc.getInventoryCount(currentBankIDs.get(inventorySlot));
					drawString(String.valueOf(inventoryCount), (slotX + 47) - mc.getSurface().stringWidth(1, String.valueOf(inventoryCount)), slotY + 29, 1, 0x00ffff); // Amount in inventory (blue)

				}
				inventorySlot++;
			}
		}
	}

	private void drawQuantityButtons(int currMouseX, int currMouseY, int relativeX, int relativeY) {
		int itemID = selectedBankSlotItemID;
		int amount = currentBankCounts.get(this.selectedBankSlot);

		int quantityColour = 0xffffff;
		if (amount > 0) {
			drawString(
				"Withdraw " + EntityHandler.getItemDef(itemID).getName(),
				relativeX + 2, relativeY + 248, 1, 0xffffff);

			if (Config.S_WANT_BANK_NOTES) {
				if (currMouseX >= relativeX + 220 && currMouseY >= relativeY + 240 &&
					currMouseX < relativeX + 250 && currMouseY <= relativeY + 251)
					quantityColour = 0xff0000;
				drawString("Note: ", relativeX + 222, relativeY + 248, 1, quantityColour);
				drawString(swapNoteMode ? "On" : "Off",
					relativeX + 257, relativeY + 248, 1, swapNoteMode ? 0x00FF00 : 0xFF0000);

				quantityColour = 0xffffff;
				if (currMouseX >= relativeX + 305 && currMouseY >= relativeY + 240 &&
					currMouseX < relativeX + 335 && currMouseY <= relativeY + 251)
					quantityColour = 0xff0000;
				drawString("One", relativeX + 307, relativeY + 248, 1, quantityColour);

			}

			else { // Authentic

				if (currMouseX >= relativeX + 220 && currMouseY >= relativeY + 240 &&
					currMouseX < relativeX + 250 && currMouseY <= relativeY + 251)
					quantityColour = 0xff0000;
				drawString("One", relativeX + 222, relativeY + 248, 1, quantityColour);

				if (amount >= 5) {
					quantityColour = 0xffffff;
					if (currMouseX >= relativeX + 250 && currMouseY >= relativeY + 240 &&
						currMouseX < relativeX + 280 && currMouseY <= relativeY + 251)
						quantityColour = 0xff0000;
					drawString("Five", relativeX + 252, relativeY + 248, 1, quantityColour);
				}

				if (amount >= 10) {
					quantityColour = 0xffffff;
					if (currMouseX >= relativeX + 280 && currMouseY >= relativeY + 240 &&
						currMouseX < relativeX + 305 && currMouseY <= relativeY + 251)
						quantityColour = 0xff0000;
					drawString("10", relativeX + 282, relativeY + 248, 1, quantityColour);
				}

				if (amount >= 50) {
					quantityColour = 0xffffff;
					if (currMouseX >= relativeX + 305 && currMouseY >= relativeY + 240 &&
						currMouseX < relativeX + 335 && currMouseY <= relativeY + 251)
						quantityColour = 0xff0000;
					drawString("50", relativeX + 307, relativeY + 248, 1, quantityColour);
				}
			}

			quantityColour = 0xffffff;
			if (currMouseX >= relativeX + 340 && currMouseY >= relativeY + 240 &&
				currMouseX < relativeX + 368 && currMouseY <= relativeY + 251)
				quantityColour = 0xff0000;
			drawString("X", relativeX + 346, relativeY + 248, 1, quantityColour);

			quantityColour = 0xffffff;
			if (currMouseX >= relativeX + 370 && currMouseY >= relativeY + 240 &&
				currMouseX < relativeX + 400 && currMouseY <= relativeY + 251)
				quantityColour = 0xff0000;
			drawString("All", relativeX + 370, relativeY + 248, 1, quantityColour);
		}

		if (mc.getInventoryCount(itemID) > 0) {
			drawString("Deposit " + EntityHandler.getItemDef(itemID).getName(),
				relativeX + 2, relativeY + 273, 1, 0xffffff);

			quantityColour = 0xffffff;

			if(Config.S_WANT_CERT_DEPOSIT && isCert(itemID)) {
				if (currMouseX >= relativeX + 220 && currMouseY >= relativeY + 265 &&
					currMouseX < relativeX + 250 && currMouseY <= relativeY + 276)
					quantityColour = 0xff0000;
				drawString("Uncert: ", relativeX + 212, relativeY + 273, 1, quantityColour);
				drawString(swapCertMode ? "On" : "Off",
					relativeX + 257, relativeY + 273, 1, swapCertMode ? 0x00FF00 : 0xFF0000);

				quantityColour = 0xffffff;

				if (currMouseX >= relativeX + 305 && currMouseY >= relativeY + 265 &&
					currMouseX < relativeX + 335 && currMouseY <= relativeY + 276)
					quantityColour = 0xff0000;
				drawString("One", relativeX + 307, relativeY + 273, 1, quantityColour);
			}
			else {
				if (currMouseX >= relativeX + 220 && currMouseY >= relativeY + 265 &&
					currMouseX < relativeX + 250 && currMouseY <= relativeY + 276)
					quantityColour = 0xff0000;
				drawString("One", relativeX + 222, relativeY + 273, 1, quantityColour);

				if (mc.getInventoryCount(itemID) >= 5) {
					quantityColour = 0xffffff;
					if (currMouseX >= relativeX + 250 && currMouseY >= relativeY + 265 &&
						currMouseX < relativeX + 280 && currMouseY <= relativeY + 276)
						quantityColour = 0xff0000;
					drawString("Five", relativeX + 252, relativeY + 273, 1, quantityColour);
				}

				if (mc.getInventoryCount(itemID) >= 10) {
					quantityColour = 0xffffff;
					if (currMouseX >= relativeX + 280 && currMouseY >= relativeY + 265 &&
						currMouseX < relativeX + 305 && currMouseY <= relativeY + 276)
						quantityColour = 0xff0000;
					drawString("10", relativeX + 282, relativeY + 273, 1, quantityColour);
				}

				if (mc.getInventoryCount(itemID) >= 50) {
					quantityColour = 0xffffff;
					if (currMouseX >= relativeX + 305 && currMouseY >= relativeY + 265 &&
						currMouseX < relativeX + 335 && currMouseY <= relativeY + 276)
						quantityColour = 0xff0000;
					drawString("50", relativeX + 307, relativeY + 273, 1, quantityColour);
				}
			}

			quantityColour = 0xffffff;
			if (currMouseX >= relativeX + 340 && currMouseY >= relativeY + 265 &&
				currMouseX < relativeX + 368 && currMouseY <= relativeY + 276)
				quantityColour = 0xff0000;
			drawString("X", relativeX + 346, relativeY + 273, 1, quantityColour);

			quantityColour = 0xffffff;
			if (currMouseX >= relativeX + 370 && currMouseY >= relativeY + 265 &&
				currMouseX < relativeX + 400 && currMouseY <= relativeY + 276)
				quantityColour = 0xff0000;
			drawString("All", relativeX + 370, relativeY + 273, 1, quantityColour);
		}
	}

	void bankClose() {
		this.mc.setShowDialogBank(false);
		this.selectedBankSlot = -1;
		mc.packetHandler.getClientStream().newPacket(212);
		mc.packetHandler.getClientStream().finishPacket();
	}

	public void sendDeposit(int i) {
		int itemID = currentBankIDs.get(this.selectedBankSlot);
		mc.packetHandler.getClientStream().newPacket(23);
		mc.packetHandler.getClientStream().writeBuffer1.putShort(itemID);
		if (i > mc.getInventoryCount(itemID)) {
			i = mc.getInventoryCount(itemID);
		}
		mc.packetHandler.getClientStream().writeBuffer1.putInt(i);
		mc.packetHandler.getClientStream().finishPacket();
		if (mc.getMouseButtonDownTime() == 0) {
			mc.setMouseClick(0);
			mc.setMouseButtonDown(0);
		}
		if (mc.getInventoryCount(itemID) - i < 1) this.selectedBankSlot = -1;
		// checks if player has an uncerted item in bank when depositing to item a cert
		// if not clear the bank slot to force user update selected slot
		if(swapCertMode && isCert(itemID)) {
			if(!currentBankIDs.contains(uncertedID(itemID))) this.selectedBankSlot = -1;
		}
	}

	public void sendWithdraw(int i) {
		int itemID = currentBankIDs.get(this.selectedBankSlot);
		int amt = currentBankCounts.get(this.selectedBankSlot);
		mc.packetHandler.getClientStream().newPacket(22);
		mc.packetHandler.getClientStream().writeBuffer1.putShort(itemID);
		if (i > amt) {
			i = amt;
		}
		mc.packetHandler.getClientStream().writeBuffer1.putInt(i);
		mc.packetHandler.getClientStream().finishPacket();
		if (mc.getMouseButtonDownTime() == 0) {
			mc.setMouseClick(0);
			mc.setMouseButtonDown(0);
		}
		if (amt - i < 1) this.selectedBankSlot = -1;
	}

	public void drawString(String str, int x, int y, int font, int color) {
		mc.getSurface().drawString(str, x, y, color, font);
	}

	public void resetBank() {
		bankItems.clear();
	}

	public void addBank(int bankID, int itemID, int amount) {
		bankItems.add(new BankItem(bankID, itemID, amount));
	}

	public void updateBank(int slot, int itemID, int amount) {
		if(amount == 0) {
			bankItems.remove(slot);
			for (slot = 0; slot < bankItems.size(); slot++) {
				bankItems.get(slot).bankID = slot;
			}
			return;
		}
		if(bankItems.size() <= slot) {
			bankItems.add(new BankItem(slot, itemID, amount));
		}
		if(bankItems.get(slot) != null) {
			bankItems.get(slot).bankID = slot;
			bankItems.get(slot).itemID = itemID;
			bankItems.get(slot).amount = amount;
		}
	}

	private void sendNoteMode() {
		mc.packetHandler.getClientStream().newPacket(199);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(1);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(swapNoteMode ? 1 : 0);
		mc.packetHandler.getClientStream().finishPacket();
	}

	private void sendCertMode() {
		mc.packetHandler.getClientStream().newPacket(199);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(0);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(swapCertMode ? 1 : 0);
		mc.packetHandler.getClientStream().finishPacket();
	}

	private boolean isCert(int itemID) {
		int[] certIds = {
			/** Ores **/
			517, 518, 519, 520, 521,
			/** Bars **/
			528, 529, 530, 531, 532,
			/** Fish **/
			533, 534, 535, 536, 628, 629, 630, 631,
			/** Logs **/
			711, 712, 713,
			/** Misc **/
			1270, 1271, 1272, 1273, 1274, 1275
		};
		ArrayList<Integer> certArr = new ArrayList<Integer>();
		for (int id : certIds) {
			certArr.add(id);
		}
		return certArr.contains(itemID);
	}

	private int uncertedID(int itemID) {

		if(itemID == 517) {
			return 151;
		} else if(itemID == 518) {
			return 155;
		} else if(itemID == 519) {
			return 153;
		} else if(itemID == 520) {
			return 383;
		} else if(itemID == 521) {
			return 152;
		} else if(itemID == 528) {
			return 170;
		} else if(itemID == 529) {
			return 171;
		} else if(itemID == 530) {
			return 173;
		} else if(itemID == 531) {
			return 384;
		} else if(itemID == 532) {
			return 172;
		} else if(itemID == 533) {
			return 373;
		} else if(itemID == 534) {
			return 372;
		} else if(itemID == 535) {
			return 370;
		} else if(itemID == 536) {
			return 369;
		} else if(itemID == 628) {
			return 555;
		} else if(itemID == 629) {
			return 554;
		} else if(itemID == 630) {
			return 546;
		} else if(itemID == 631) {
			return 545;
		} else if(itemID == 711) {
			return 635;
		} else if(itemID == 712) {
			return 634;
		} else if(itemID == 713) {
			return 633;
		} else if(itemID == 1270) {
			return 814;
		} else if(itemID == 1271) {
			return 220;
		} else if(itemID == 1272) {
			return 483;
		} else if(itemID == 1273) {
			return 486;
		} else if(itemID == 1274) {
			return 495;
		} else if(itemID == 1275) {
			return 492;
		} else {
			return itemID;
		}
	}

	class BankItem {

		int bankID, itemID, amount;

		BankItem(int bankID, int itemID, int amount) {
			this.bankID = bankID;
			this.itemID = itemID;
			this.amount = amount;
		}
	}
}
