package com.openrsc.interfaces.misc;

import java.util.ArrayList;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;

import orsc.Config;
import orsc.mudclient;
import orsc.enumerations.InputXAction;
import orsc.graphics.gui.InputXPrompt;
import orsc.graphics.gui.Panel;

public class BankInterface {
	public static mudclient mc;

	public int selectedBankSlot = -1;

	public int width, height;
	protected boolean rightClickMenu;

	public Panel bank;
	protected ArrayList<BankItem> bankItems;

	public BankInterface(mudclient mc) {
		this.mc = mc;

    width = 408; // WIDTH MODIFIER
    height = 334; // HEIGHT MODIFIER
		bank = new Panel(mc.getSurface(), 3);
		bankItems = new ArrayList<BankItem>();
	}

	protected int selectedBankSlotItemID = -2;
	protected int mouseOverBankPageText;

	ArrayList<Integer> currentBankIDs = new ArrayList<>();
	ArrayList<Integer> currentBankCounts = new ArrayList<>();

	public boolean onRender() {
		int currMouseX = mc.getMouseX();
		int currMouseY = mc.getMouseY();

		// Set up bank list to loop through later.
		currentBankIDs.clear();
		currentBankCounts.clear();
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
    if (mc.getMouseClick() == 1 || (mc.getMouseButtonDown() == 1 && mc.getMouseButtonDownTime() > 20)) {
			int selectedX = currMouseX - (mc.getGameWidth() / 2 - width / 2);
			int selectedY = currMouseY - (mc.getGameHeight() / 2 - height / 2 + 20);
			if (selectedX >= 0 && selectedY >= 12 && selectedX < 408 && selectedY < 280) {
				selectSlot(selectedX, selectedY); // Set the slot we clicked on

				selectedX = mc.getGameWidth() / 2 - width / 2;
				selectedY = mc.getGameHeight() / 2 - height / 2 + 20;

				// Check for a transaction
				int itemID, amount;
				if (selectedBankSlot > -1)
					checkTransaction(currMouseX, currMouseY, selectedX, selectedY);

			// Select bank page
			} else if (currentBankIDs.size() > 48 && selectedX >= 50 && selectedX <= 115 &&
						selectedY <= 12 && currMouseY > mc.getGameHeight() / 2 - 146) {
				mouseOverBankPageText = 0; // Select page 1
			} else if (currentBankIDs.size() > 48 && selectedX >= 115 && selectedX <= 180 &&
						selectedY <= 12 && currMouseY > mc.getGameHeight() / 2 - 146) {
				mouseOverBankPageText = 1; // Select page 2
			} else if (currentBankIDs.size() > 96 && selectedX >= 180 && selectedX <= 245 &&
						selectedY <= 12 && currMouseY > mc.getGameHeight() / 2 - 146) {
				mouseOverBankPageText = 2; // Select page 3
			} else if (currentBankIDs.size() > 144 && selectedX >= 245 && selectedX <= 310 &&
						selectedY <= 12 && currMouseY > mc.getGameHeight() / 2 - 146) {
				mouseOverBankPageText = 3; // Select page 4

			} else { // Close Bank
				bankClose();
				return false;
			}
		}

		// Draw the top header
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
		if (currMouseX > relativeX + 320 && currMouseY >= relativeY && currMouseX < relativeX + 408 && currMouseY < relativeY + 12)
			closeButtonColour = 0xff0000;
		drawString("Close window", relativeX + 326, relativeY + 10, 1, closeButtonColour);
		drawString("Number in bank in green", relativeX + 7, relativeY + 24, 1, 65280);
		drawString("Number held in blue", relativeX + 289, relativeY + 24, 1, 65535);


		// Draw the items in the bank.
		drawBankItems(relativeX, relativeY);

		// Line between Withdraw & Deposit
    mc.getSurface().drawLineHoriz(relativeX + 5, relativeY + 256, width - 8, 0);

		// Draw the Quantity Buttons
    if (selectedBankSlot != -1) {
			drawQuantityButtons(currMouseX, currMouseY, relativeX, relativeY);
		}
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
							selectedBankSlot = selectedItemSlot;
						}
						return;
					}
				}
				selectedItemSlot++;
			}
		}
	}

	public void checkTransaction(int currMouseX, int currMouseY, int selectedX, int selectedY) {
		int itemID = selectedBankSlotItemID;
		int amount = currentBankCounts.get(selectedBankSlot);

		// Incremental Withdraw or Deposit
		if (currMouseX >= selectedX + 220 && currMouseY >= selectedY + 238 && currMouseX < selectedX + 250 && currMouseY <= selectedY + 249) {
			if (mc.mouseButtonItemCountIncrement == 0)
				mc.mouseButtonItemCountIncrement = 1;
			sendWithdraw(mc.mouseButtonItemCountIncrement); // Withdraw 1
		}
		else if (mc.getInventoryCount(itemID) >= 1 && currMouseX >= selectedX + 220 && currMouseY >= selectedY + 263
				&& currMouseX < selectedX + 250 && currMouseY <= selectedY + 274) {
			if (mc.mouseButtonItemCountIncrement == 0)
				mc.mouseButtonItemCountIncrement = 1;
			sendDeposit(mc.mouseButtonItemCountIncrement); // Deposit 1
		}

		// Non incremental Withdraw or deposit
		else if (mc.getMouseButtonDownTime() < 50) {
			if (amount >= 5 && currMouseX >= selectedX + 250 && currMouseY >=  selectedY + 238 && currMouseX < selectedX + 280
					&& currMouseY <= selectedY + 249) {
				sendWithdraw(5); // Withdraw 5
			}
			else if (amount >= 10 && currMouseX >= selectedX + 280 && currMouseY >= selectedY + 238 && currMouseX < selectedX + 305
					&& currMouseY <= selectedY + 249) {
				sendWithdraw(10); // Withdraw 10
			}
			else if (amount >= 50 && currMouseX >= selectedX + 305 && currMouseY >= selectedY + 238 && currMouseX < selectedX + 335
					&& currMouseY <= selectedY + 249) {
				sendWithdraw(50); // Withdraw 50
			}
			else if (currMouseX >= selectedX + 335 && currMouseY >= selectedY + 238 && currMouseX < selectedX + 368
					&& currMouseY <= selectedY + 249) {
				// Withdraw X
				mc.showItemModX(InputXPrompt.bankWithdrawX, InputXAction.BANK_WITHDRAW, true);
				mc.setMouseClick(0);
			}
			else if (currMouseX >= selectedX + 370 && currMouseY >= selectedY + 238 && currMouseX < selectedX + 400
					&& currMouseY <= selectedY + 249) {
				sendWithdraw(Integer.MAX_VALUE); // Withdraw All
			}
	
			// Depositing
			else if (mc.getInventoryCount(itemID) >= 5 && currMouseX >= selectedX + 250 && currMouseY >= selectedY + 263
					&& currMouseX < selectedX + 280 && currMouseY <= selectedY + 274) {
				sendDeposit(5); // Deposit 5
			}
			else if (mc.getInventoryCount(itemID) >= 10 && currMouseX >= selectedX + 280 && currMouseY >= selectedY + 263
					&& currMouseX < selectedX + 305 && currMouseY <= selectedY + 274) {
				sendDeposit(10); // Deposit 10
			}
			else if (mc.getInventoryCount(itemID) >= 50 && currMouseX >= selectedX + 305 && currMouseY >= selectedY + 263
					&& currMouseX < selectedX + 335 && currMouseY <= selectedY + 274) {
				sendDeposit(50); // Deposit 50
			}
			else if (currMouseX >= selectedX + 335 && currMouseY >= selectedY + 263 && currMouseX < selectedX + 368
					&& currMouseY <= selectedY + 274) {
				// Deposit X
				mc.showItemModX(InputXPrompt.bankDepositX, InputXAction.BANK_DEPOSIT, true);
				mc.setMouseClick(0);
			}
			else if (currMouseX >= selectedX + 370 && currMouseY >= selectedY + 263 && currMouseX < selectedX + 400
					&& currMouseY <= selectedY + 274) {
				sendDeposit(Integer.MAX_VALUE); // Deposit All
			}
		}
	}

	private void drawPageButtons(int currMouseX, int currMouseY, int relativeX, int relativeY) {
    int pageButtonMargin = 50;
    if (currentBankIDs.size() > 48) {
      int pageButtonColour = 0xffffff;
      if (mouseOverBankPageText == 0)
        pageButtonColour = 0xff0000;
      else if (currMouseX > relativeX + pageButtonMargin && currMouseY >= relativeY && currMouseX < relativeX + pageButtonMargin + 65 && currMouseY < relativeY + 12)
        pageButtonColour = 0xffff00;
      drawString("<page 1>", relativeX + pageButtonMargin, relativeY + 10, 1, pageButtonColour);
      pageButtonMargin += 65;
      pageButtonColour = 0xffffff;
      if (mouseOverBankPageText == 1)
        pageButtonColour = 0xff0000;
      else if (currMouseX > relativeX + pageButtonMargin && currMouseY >= relativeY && currMouseX < relativeX + pageButtonMargin + 65 && currMouseY < relativeY + 12)
        pageButtonColour = 0xffff00;
      drawString("<page 2>", relativeX + pageButtonMargin, relativeY + 10, 1, pageButtonColour);
      pageButtonMargin += 65;
    }
    if (currentBankIDs.size() > 96) {
      int pageButtonColour = 0xffffff;
      if (mouseOverBankPageText == 2)
        pageButtonColour = 0xff0000;
      else if (currMouseX > relativeX + pageButtonMargin && currMouseY >= relativeY && currMouseX < relativeX + pageButtonMargin + 65 && currMouseY < relativeY + 12)
        pageButtonColour = 0xffff00;
      drawString("<page 3>", relativeX + pageButtonMargin, relativeY + 10, 1, pageButtonColour);
      pageButtonMargin += 65;
    }
    if (currentBankIDs.size() > 144) {
      int pageButtonColour = 0xffffff;
      if (mouseOverBankPageText == 3)
        pageButtonColour = 0xff0000;
      else if (currMouseX > relativeX + pageButtonMargin && currMouseY >= relativeY && currMouseX < relativeX + pageButtonMargin + 65 && currMouseY < relativeY + 12)
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
				if (selectedBankSlot == inventorySlot) { // Selected
					mc.getSurface().drawBoxAlpha(slotX, slotY, 49, 34, 0xff0000, 160);
        } else { // Not Selected
          mc.getSurface().drawBoxAlpha(slotX, slotY, 49, 34, 0xd0d0d0, 160);
				}

        mc.getSurface().drawBoxBorder(slotX, 50, slotY, 35, 0);

				// Draw Item Sprite From Bank
				if (inventorySlot < currentBankIDs.size() && currentBankIDs.get(inventorySlot) != -1
						&& (currentBankCounts.get(inventorySlot) > 0 || mc.getInventoryCount(currentBankIDs.get(inventorySlot)) > 0)) {
					mc.getSurface().drawSpriteClipping(
							mc.spriteItem + EntityHandler.getItemDef(currentBankIDs.get(inventorySlot)).getSprite(),
							slotX, slotY, 48, 32,
							EntityHandler.getItemDef(currentBankIDs.get(inventorySlot)).getPictureMask(),
							0, false, 0, 1);
					drawString(""+currentBankCounts.get(inventorySlot), slotX + 1, slotY + 10, 1, 65280); // Amount in bank (green)

					inventoryCount = mc.getInventoryCount(currentBankIDs.get(inventorySlot));
					drawString(String.valueOf(inventoryCount), (slotX + 47) - mc.getSurface().stringWidth(1, String.valueOf(inventoryCount)), slotY + 29, 1, 65535); // Amount in inventory (blue)

				}
       	inventorySlot++;
			}
		}
	}

	private void drawQuantityButtons(int currMouseX, int currMouseY, int relativeX, int relativeY) {
		int itemID = selectedBankSlotItemID;
		int amount = currentBankCounts.get(selectedBankSlot);

		int quantityColour = 0xffffff;
		if (amount > 0) {
			drawString(
					"Withdraw " + " "
					+ EntityHandler.getItemDef(itemID).getName(),
					relativeX + 2, relativeY + 248, 1, 0xffffff);

			if (currMouseX >= relativeX + 220 && currMouseY >= relativeY + 238 &&
					currMouseX < relativeX + 250 && currMouseY <= relativeY + 249)
				quantityColour = 0xff0000;
			drawString("One", relativeX + 222, relativeY + 248, 1, quantityColour);

			if (amount >= 5) {
					quantityColour = 0xffffff;
				if (currMouseX >= relativeX + 250 && currMouseY >= relativeY + 238 &&
						currMouseX < relativeX + 280 && currMouseY <= relativeY + 249)
					quantityColour = 0xff0000;
				drawString("Five", relativeX + 252, relativeY + 248, 1, quantityColour);
			}

			if (amount >= 10) {
				quantityColour = 0xffffff;
				if (currMouseX >= relativeX + 280 && currMouseY >= relativeY + 238 &&
						currMouseX < relativeX + 305 && currMouseY <= relativeY + 249)
					quantityColour = 0xff0000;
				drawString("10", relativeX + 282, relativeY + 248, 1, quantityColour);
			}

			if (amount >= 50) {
				quantityColour = 0xffffff;
				if (currMouseX >= relativeX + 305 && currMouseY >= relativeY + 238 &&
						currMouseX < relativeX + 335 && currMouseY <= relativeY + 249)
					quantityColour = 0xff0000;
				drawString("50", relativeX + 307, relativeY + 248, 1, quantityColour);
			}

			quantityColour = 0xffffff;
			if (currMouseX >= relativeX + 335 && currMouseY >= relativeY + 238 &&
					currMouseX < relativeX + 368 && currMouseY <= relativeY + 249)
				quantityColour = 0xff0000;
			drawString("X", relativeX + 337, relativeY + 248, 1, quantityColour);

			quantityColour = 0xffffff;
			if (currMouseX >= relativeX + 370 && currMouseY >= relativeY + 238 &&
					currMouseX < relativeX + 400 && currMouseY <= relativeY + 249)
				quantityColour = 0xff0000;
			drawString("All", relativeX + 370, relativeY + 248, 1, quantityColour);
		}

		if (mc.getInventoryCount(itemID) > 0) {
			drawString("Deposit " + EntityHandler.getItemDef(itemID).getName(),
					relativeX + 2, relativeY + 273, 1, 0xffffff);

			quantityColour = 0xffffff;
			if (currMouseX >= relativeX + 220 && currMouseY >= relativeY + 263 &&
					currMouseX < relativeX + 250 && currMouseY <= relativeY + 274)
				quantityColour = 0xff0000;
			drawString("One", relativeX + 222, relativeY + 273, 1, quantityColour);

			if (mc.getInventoryCount(itemID) >= 5) {
				quantityColour = 0xffffff;
				if (currMouseX >= relativeX + 250 && currMouseY >= relativeY + 263 &&
						currMouseX < relativeX + 280 && currMouseY <= relativeY + 274)
					quantityColour = 0xff0000;
				drawString("Five", relativeX + 252, relativeY + 273, 1, quantityColour);
			}

			if (mc.getInventoryCount(itemID) >= 10) {
				quantityColour = 0xffffff;
				if (currMouseX >= relativeX + 280 && currMouseY >= relativeY + 263 &&
						currMouseX < relativeX + 305 && currMouseY <= relativeY + 274)
					quantityColour = 0xff0000;
				drawString("10", relativeX + 282, relativeY + 273, 1, quantityColour);
			}

			if (mc.getInventoryCount(itemID) >= 50) {
				quantityColour = 0xffffff;
				if (currMouseX >= relativeX + 305 && currMouseY >= relativeY + 263 &&
						currMouseX < relativeX + 335 && currMouseY <= relativeY + 274)
					quantityColour = 0xff0000;
				drawString("50", relativeX + 307, relativeY + 273, 1, quantityColour);
			}

			quantityColour = 0xffffff;
			if (currMouseX >= relativeX + 335 && currMouseY >= relativeY + 263 &&
					currMouseX < relativeX + 368 && currMouseY <= relativeY + 274)
				quantityColour = 0xff0000;
			drawString("X", relativeX + 337, relativeY + 273, 1, quantityColour);

			quantityColour = 0xffffff;
			if (currMouseX >= relativeX + 370 && currMouseY >= relativeY + 263 &&
					currMouseX < relativeX + 400 && currMouseY <= relativeY + 274)
				quantityColour = 0xff0000;
			drawString("All", relativeX + 370, relativeY + 273, 1, quantityColour);
		}
	}

	public void bankClose() {
		mc.getClientStream().newPacket(212);
		mc.getClientStream().finishPacket();
	}

	public void sendDeposit(int i) {
		int itemID = currentBankIDs.get(selectedBankSlot);
		mc.getClientStream().newPacket(23);
		mc.getClientStream().writeBuffer1.putShort(itemID);
		if (i > mc.getInventoryCount(itemID)) {
			i = mc.getInventoryCount(itemID);
		}
		mc.getClientStream().writeBuffer1.putInt(i);
		mc.getClientStream().finishPacket();
		rightClickMenu = false;
		if (mc.getMouseButtonDownTime() == 0) {
			mc.setMouseClick(0);
			mc.setMouseButtonDown(0);
		}
		if (mc.getInventoryCount(itemID) < 1) selectedBankSlot = -1;
	}

	public void sendWithdraw(int i) {
		int itemID = currentBankIDs.get(selectedBankSlot);
		int amt = currentBankCounts.get(selectedBankSlot);
		mc.getClientStream().newPacket(22);
		mc.getClientStream().writeBuffer1.putShort(itemID);
		if (i > amt) {
			i = amt;
		}
		mc.getClientStream().writeBuffer1.putInt(i);
		mc.getClientStream().finishPacket();
		rightClickMenu = false;
		if (mc.getMouseButtonDownTime() == 0) {
			mc.setMouseClick(0);
			mc.setMouseButtonDown(0);
		}
		if (amt < 1) selectedBankSlot = -1;
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

	class BankItem {

		public int bankID, itemID, amount;

		public BankItem(int bankID, int itemID, int amount) {
			this.bankID = bankID;
			this.itemID = itemID;
			this.amount = amount;
		}
	}
}
