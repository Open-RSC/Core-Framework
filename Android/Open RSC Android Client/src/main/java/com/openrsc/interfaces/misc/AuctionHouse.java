package com.openrsc.interfaces.misc;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import orsc.Config;
import orsc.enumerations.MessageType;
import orsc.graphics.gui.Panel;
import orsc.graphics.two.GraphicsController;
import orsc.mudclient;

public final class AuctionHouse {
	public int auctionScrollHandle;
	public int auctionSearchHandle;
	public Panel auctionMenu;
	public Panel myAuctions;
	public Panel selectItemGUI;
	public int activeInterface;
	public int myAuctionScrollHandle;
	public int textField_amount;
	public int textField_price;
	public int textField_priceEach;
	public int textField_buyAmount;
	private int x, y;
	private int width, height;
	private ArrayList<AuctionItem> auctionItems;
	private int newAuctionInventoryIndex = -1;
	private AuctionItem newAuctionItem = null;
	private int selectedAuction = -1;
	private int selectedCancelAuction = -1;

	private int selectItemAdd = 0;

	private boolean visible = false;
	private int selectedFilter;
	private String[] resources = {"log", "bones", "pickaxe", "dough"};
	private int orderingBy = 0;
	private Comparator<AuctionItem> auctionComparator = (o1, o2) -> {
		if (orderingBy == 0) { /* price down */
			return o1.getPrice() - o2.getPrice();
		} else if (orderingBy == 1) { /* price up */
			return o2.getPrice() - o1.getPrice();
		} else if (orderingBy == 2) { /* name */
			ItemDef d1 = EntityHandler.getItemDef(o1.getItemID());
			ItemDef d2 = EntityHandler.getItemDef(o2.getItemID());

			return d1.getName().compareToIgnoreCase(d2.getName());
		} else if (orderingBy == 3) { /* price each down */
			int priceEach1 = o1.getPrice() / o1.getAmount();
			int priceEach2 = o2.getPrice() / o2.getAmount();
			return priceEach1 - priceEach2;
		} else if (orderingBy == 4) { /* price each up */
			int priceEach1 = o1.getPrice() / o1.getAmount();
			int priceEach2 = o2.getPrice() / o2.getAmount();
			return priceEach2 - priceEach1;
		}
		ItemDef d1 = EntityHandler.getItemDef(o1.getAuctionID());
		ItemDef d2 = EntityHandler.getItemDef(o2.getAuctionID());

		return d1.getName().compareToIgnoreCase(d2.getName());
	};
	private String sortBy = "Price Down";
	private double fee;
	private mudclient mc;

	public AuctionHouse(mudclient mc) {
		this.mc = mc;

		width = 490;
		height = 326 - 47;

		x = (mc.getGameWidth() / 2) - width;
		y = (mc.getGameHeight() / 2) - height;

		auctionItems = new ArrayList<>();

		auctionMenu = new Panel(mc.getSurface(), 5);
		myAuctions = new Panel(mc.getSurface(), 15);

		auctionScrollHandle = auctionMenu.addScrollingList2(x + 97, y + 80, 390, 208, 1000, 7, true);
		auctionSearchHandle = auctionMenu.addLeftTextEntry(x + 315, y + 48, 174, 18, 1, 36, false, true);
		textField_buyAmount = auctionMenu.addLeftTextEntry(x + 100, y + 219, 101, 18, 1, 10, false, true);

		textField_price = myAuctions.addLeftTextEntry(x + 60, y + 130, 70, 18, 1, 8, false, true);
		textField_amount = myAuctions.addLeftTextEntry(x + 60, y + 209, 70, 18, 1, 8, false, true);
		textField_priceEach = myAuctions.addLeftTextEntry(x + 60, y + 169, 70, 18, 1, 8, false, true);

		myAuctionScrollHandle = myAuctions.addScrollingList2(x + 216, y + 74, 270, 179, 1000, 7, true);
	}

	public void reposition() {
		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		auctionMenu.reposition(auctionScrollHandle, x + 97, y + 80, 390, 208);
		auctionMenu.reposition(auctionSearchHandle, x + 315, y + 48, 174, 18);
		auctionMenu.reposition(textField_buyAmount, x + 100, y + 219, 101, 18);

		myAuctions.reposition(myAuctionScrollHandle, x + 216, y + 74, 270, 179);
		myAuctions.reposition(textField_amount, x + 60, y + 209, 70, 18);
		myAuctions.reposition(textField_price, x + 60, y + 130, 70, 18);
		myAuctions.reposition(textField_priceEach, x + 60, y + 169, 70, 18);
	}

	private boolean inBounds(int x, int y, int rectX, int rectY, int width, int height)
	{
		return x >= rectX && x < (rectX + width) && y >= rectY && y < (rectY + height);
	}

	public boolean onRender(GraphicsController graphics) {
		reposition();

		if (!Config.isAndroid() && mc.getMouseClick() == 1) {
			if(!inBounds(mc.getMouseX(), mc.getMouseY(), x, y, width, height + 12)) {
				auctionClose();
			}
		}

		graphics.drawBox(x, y, width, 12, 192);
		int colour = 10000536;
		graphics.drawBoxAlpha(x, y + 12, width, height, colour, 160);
		graphics.drawString("Auction House", x + 1, y + 10, 0xffffff, 1);

		drawButton(graphics, x + 2, y + 14, 80, 21, "Browse", activeInterface == 0, new ButtonHandler() {
			@Override
			void handle() {
				activeInterface = 0;
				auctionMenu.setFocus(-1);
				myAuctions.setFocus(-1);
			}
		});
		drawButton(graphics, x + 84, y + 14, 80, 21, "My Auctions", activeInterface == 1, new ButtonHandler() {
			@Override
			void handle() {
				activeInterface = 1;
				auctionMenu.setFocus(-1);
				myAuctions.setFocus(-1);
			}
		});

		drawButton(graphics, x + 408, y + 14, 80, 21, "Refresh", false, new ButtonHandler() {
			@Override
			void handle() {
				sendRefreshList();
				auctionMenu.setFocus(-1);
				myAuctions.setFocus(-1);
			}
		});

		drawTextHit(graphics, x + 405, y - 1, 81, 12, "Close window", false, new ButtonHandler() {
			@Override
			void handle() {
				auctionClose();
			}
		});

		if (activeInterface == 0) {
			drawAuctionMenu(graphics);
		} else if (activeInterface == 1) {
			drawMyAuctions(graphics);
		}
		return true;
	}

	private void auctionClose() {
		mc.packetHandler.getClientStream().newPacket(199);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(10);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(4);
		mc.packetHandler.getClientStream().finishPacket();
		resetAllVariables();
		setVisible(false);
	}

	private void sendRefreshList() {
		mc.packetHandler.getClientStream().newPacket(199);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(10);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(3);
		mc.packetHandler.getClientStream().finishPacket();
	}

	private void drawMyAuctions(GraphicsController graphics) {
		myAuctions.clearList(myAuctionScrollHandle);

		graphics.drawBoxAlpha(x + 3, y + 37, 129, 67, 0, 60);
		graphics.drawBoxBorder(x + 2, 130, y + 37, 68, 0x343434);


		int inventorySlot = 0;
		int i7 = 0xd0d0d0;
		int inventoryDrawX = x + 182;
		int inventoryDrawY = y + 40;

		int boxWidth = (49);
		int boxHeight = (34);

		// START RIGHT SIDE
		graphics.drawBoxAlpha(x + 138, y + 37, 349, 251, 0, 60);
		graphics.drawBoxBorder(x + 137, 350, y + 37, 252, 0x343434);

		if (newAuctionItem == null) {
			drawButtonFancy(graphics, x + 16, y + 37 + 10, 100, 48, "+ Select item", selectItemAdd == 1, new ButtonHandler() {
				@Override
				void handle() {
					selectItemAdd = 1;
				}
			});
		} else if (newAuctionItem != null) {
			ItemDef def = EntityHandler.getItemDef(newAuctionItem.getItemID());
			mc.getSurface().drawSpriteClipping(mudclient.spriteItem + def.getSprite(), x + 40, y + 55, 48, 32,
				def.getPictureMask(), 0, false, 0, 1);
			if (def.getNotedFormOf() >= 0) {
				ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
				mc.getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(), x + 47,
					y + 59, 33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
			}
			//graphics.drawString("Fee: +" + (int) getFee() + "gp", x + 6, y + 101, 0xffffff, 0);
		}
		graphics.drawBoxAlpha(x + 3, y + 37 + 71, 129, 181, 0, 60);
		graphics.drawBoxBorder(x + 2, 130, y + 37 + 70, 182, 0x343434);

		graphics.drawBoxAlpha(x + 57, y + 121, 70, 18, 0x0C0C0C, 228);
		graphics.drawBoxBorder(x + 57, 70, y + 121, 18, 0x35231B);
		graphics.drawString("GP Total:", x + 6, y + 133, 0xffffff, 0);

		graphics.drawLineHoriz(x + 5, y + 133 + 15, 124, 0x222222);


		graphics.drawBoxAlpha(x + 57, y + 133 + 27, 70, 18, 0x0C0C0C, 228);
		graphics.drawBoxBorder(x + 57, 70, y + 133 + 27, 18, 0x35231B);
		graphics.drawString("GP Each:", x + 6, y + 133 + 39, 0xffffff, 0);

		graphics.drawLineHoriz(x + 5, y + 133 + 39 + 16, 124, 0x222222);

		graphics.drawBoxAlpha(x + 57, y + 200, 70, 18, 0x0C0C0C, 228);
		graphics.drawBoxBorder(x + 57, 70, y + 200, 18, 0x35231B);
		graphics.drawString("Quantity:", x + 6, y + 133 + 39 + 16 + 24, 0xffffff, 0);

		graphics.drawLineHoriz(x + 5, y + 133 + 39 + 16 + 24 + 16, 124, 0x222222);

		drawButtonFancy(graphics, x + 16, y + 238, 100, 27, "Create Auction", newAuctionItem == null, new ButtonHandler() {
			@Override
			void handle() {
				sendCreateAuction();
			}
		});

		//graphics.drawString("Fee: 2.5%", x + 5 + 38, y + 280, 0xffffff, 0);
		// END RIGHT SIDE

		if (selectItemAdd == 1) {
			//graphics.drawString("Auction House has a fee of 2.5% upon adding your sale", x + 176, y + 285, 0xffffff, 0);
			graphics.drawString("My Inventory", x + 189, y + 64, 0xFFFF00, 1);
			drawButton(graphics, x + 402, y + 32 + 10, 80, 21, "< My Listings", false, new ButtonHandler() {
				@Override
				void handle() {
					selectItemAdd = 0;
					newAuctionItem = null;
					newAuctionInventoryIndex = -1;
					selectedCancelAuction = -1;
					myAuctions.hide(textField_priceEach);
					myAuctions.hide(textField_price);
					myAuctions.hide(textField_amount);
				}
			});
			for (int verticalSlots = 0; verticalSlots < 6; verticalSlots++) {
				for (int horizonalSlots = 0; horizonalSlots < 5; horizonalSlots++) {
					int drawX = inventoryDrawX + 7 + horizonalSlots * boxWidth;
					int drawY = inventoryDrawY + 28 + verticalSlots * boxHeight;
					graphics.drawBoxAlpha(drawX, drawY, boxWidth, boxHeight, i7, 160);
					if (newAuctionInventoryIndex == inventorySlot) {
						graphics.drawBoxAlpha(drawX, drawY, boxWidth, boxHeight, 0xff, 160);
					}
					graphics.drawBoxBorder(drawX, boxWidth + 1, drawY, boxHeight + 1, 0);
					if (inventorySlot < mc.getInventoryItemCount() && mc.getInventoryItems()[inventorySlot] != -1) {
						ItemDef def = EntityHandler.getItemDef(mc.getInventoryItems()[inventorySlot]);

						mc.getSurface().drawSpriteClipping(mudclient.spriteItem + def.getSprite(), drawX, drawY, 48,
							32, def.getPictureMask(), 0, false, 0, 1);
						if (def.getNotedFormOf() >= 0) {
							ItemDef originalDef = EntityHandler.getItemDef(def.getNotedFormOf());
							mc.getSurface().drawSpriteClipping(mudclient.spriteItem + originalDef.getSprite(), drawX + 7,
								drawY + 4, 33, 23, originalDef.getPictureMask(), 0, false, 0, 1);
						}
						graphics.drawString(String.valueOf(mc.getInventoryItemsCount()[inventorySlot]), drawX + 1,
							drawY + 10, 65280, 1);
					}
					if (mc.getMouseX() > drawX && mc.getMouseX() < drawX + boxWidth && mc.getMouseY() > drawY
						&& mc.getMouseY() < drawY + boxHeight) {
						graphics.drawBoxAlpha(drawX, drawY, boxWidth, boxHeight, i7, 160);
						if (mc.getMouseClick() == 1) {
							int itemID = mc.getInventoryItems()[inventorySlot];
							int amount = mc.getInventoryCount(itemID);
							if (itemID == 10 || EntityHandler.getItemDef(itemID).quest) {
								mc.showMessage(false, null, "This object cannot be added to auction", MessageType.GAME,
									0, null);
								return;
							}
							if (amount > 0) {
								int price = EntityHandler.getItemDef(itemID).getBasePrice();
								newAuctionInventoryIndex = inventorySlot;
								newAuctionItem = new AuctionItem(-2, itemID, amount, price * amount, "", 0);

								myAuctions.show(textField_priceEach);
								myAuctions.show(textField_price);
								myAuctions.show(textField_amount);
								myAuctions.setText(textField_amount, amount + "");
								myAuctions.setText(textField_price, price * amount + "");
								myAuctions.setText(textField_priceEach, price + "");
								/*if(price * amount * 0.025 < 5) {
									setFee(5);
								} else {
									setFee((int) (price * amount) * 0.025);
								}*/
							}
						}
					}
					inventorySlot++;
				}
			}
		} else if (selectItemAdd == 0) {
			if (newAuctionItem == null) {
				drawButtonFancy(graphics, x + 16, y + 37 + 10, 100, 48, "+ Select item", selectItemAdd == 1, new ButtonHandler() {
					@Override
					void handle() {
						selectItemAdd = 1;
					}
				});
			}

			LinkedList<AuctionItem> filteredList = new LinkedList<>();
			for (AuctionItem item : auctionItems) {
				if (item.getSeller().equalsIgnoreCase(mc.getUsername())) {
					filteredList.add(item);
				}
			}

			int listX = x + 210;
			int listY = y + 85;

			graphics.drawBoxAlpha(listX - 72, listY - 47, 348, 20, 0x3E557C, 192);
			graphics.drawString("My Listings", listX - 68, listY - 34, 0xffffff, 1);

			graphics.drawBoxAlpha(listX - 72, listY - 26, 348, 15, 0x192638, 192);
			graphics.drawBoxBorder(listX - 73, 350, listY - 27, 17, 0x292D30);

			graphics.drawString("Item", listX - 68, listY - 14, 0xffffff, 1);
			graphics.drawString("Name / Sale Prices", listX - 18, listY - 14, 0xffffff, 1);
			graphics.drawString("Time Left", listX + 208, listY - 14, 0xffffff, 1);

			int listStartPoint = myAuctions.getScrollPosition(myAuctionScrollHandle);
			int listEndPoint = listStartPoint + 4;
			for (int i = -1; i < filteredList.size(); i++) {
				myAuctions.setListEntry(myAuctionScrollHandle, i + 1, "", 0, null, null);
				if (i < listStartPoint || i > listEndPoint)
					continue;
				AuctionItem ahItem = filteredList.get(i);
				if (mc.getMouseX() >= listX - 72 && mc.getMouseY() >= listY - 11 && mc.getMouseX() <= listX + 275 - 12
					&& mc.getMouseY() <= listY - 11 + boxHeight) {
					graphics.drawBoxAlpha(listX - 72, listY - 11, 348, boxHeight, 0x980000, 128);
					if (mc.getMouseClick() == 1) {
						selectedCancelAuction = i;
					}
				} else {
					if (selectedCancelAuction == i) {
						graphics.drawBoxAlpha(listX - 72, listY - 11, 348, boxHeight, 0xff0000, 128);
					} else {
						graphics.drawBoxAlpha(listX - 72, listY - 11, 348, boxHeight, 0x45454545, 128);
					}
				}
				graphics.drawBoxBorder(listX - 73, 350, listY - 11, boxHeight + 1, 0x343434);
				ItemDef def = EntityHandler.getItemDef(ahItem.getItemID());
				if (def == null) {
					continue;
				}
				int price = ahItem.getPrice();
				int priceEach = 0;
				if (price > 0) {
					priceEach = price / ahItem.getAmount();
				}

				graphics.drawString(def.getName(), listX - 17, listY + boxHeight / 2 - 14, 0xffffff, 2);
				graphics.drawString("Buyout:", listX - 17, listY + boxHeight / 2 + 10 - 8, 0xc1b575, 0);
				graphics.drawString("Each:", listX + 90, listY + boxHeight / 2 + 10 - 8, 0xc1b575, 0);

				graphics.drawString(basicNumber(price) + " gp", listX + 21, listY + boxHeight / 2 + 10 - 8, 0xffffff, 0);

				graphics.drawString(basicNumber(priceEach) + " gp ea", listX + 118, listY + boxHeight / 2 + 10 - 8, 0xffffff, 0);
				graphics.drawString(getTime(ahItem) + "h", listX + 240, listY + boxHeight / 2 - 14, 0xffffff, 2);

				graphics.drawBoxAlpha(listX - 72, listY - 10, boxWidth + 1, boxHeight - 1, 0xfffffff, 128);

				mc.getSurface().drawSpriteClipping(mudclient.spriteItem + def.getSprite(), listX - 72, listY - 10, 48,
					32, def.getPictureMask(), 0, false, 0, 1);

				graphics.drawString(String.valueOf(ahItem.getAmount()), listX - 72 + 1, listY - 10 + 11, 65280, 3);
				listY += boxHeight + 2;
			}

			if (selectedCancelAuction >= 0) {
				int cancelAuctionColor = 0x980000;

				if (mc.getMouseX() >= x + 285 - 29 && mc.getMouseY() >= y + 260 && mc.getMouseX() <= x + 385 - 17
					&& mc.getMouseY() <= y + 20 + 260) {
					cancelAuctionColor = 0x500000;
					if (mc.getMouseClick() == 1 && selectedCancelAuction < filteredList.size()) {
						sendCancelAuction(filteredList.get(selectedCancelAuction).getAuctionID());
					}
				}
				graphics.drawBoxAlpha(x + 255, y + 260, 114, 22, cancelAuctionColor, 192);
				graphics.drawBoxBorder(x + 255, 114, y + 260, 22, 0xC8C7BE);
				graphics.drawString("Cancel Auction", x + 270, y + 275, 0xffffff, 1);
			}
		}
		myAuctions.drawPanel();
	}

	private void sendCancelAuction(int auctionID) {
		mc.packetHandler.getClientStream().newPacket(199);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(10);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(2);
		mc.packetHandler.getClientStream().writeBuffer1.putInt(auctionID);
		mc.packetHandler.getClientStream().finishPacket();
		selectedCancelAuction = -1;
	}

	private void sendCreateAuction() {
		if (newAuctionItem != null) {
			if (newAuctionItem.getAmount() <= 0) {
				newAuctionItem.setAmount(1);
			}
			if (newAuctionItem.getPrice() <= 0) {
				newAuctionItem.setPrice(1);
			}
			mc.packetHandler.getClientStream().newPacket(199);
			mc.packetHandler.getClientStream().writeBuffer1.putByte(10);
			mc.packetHandler.getClientStream().writeBuffer1.putByte(1);
			mc.packetHandler.getClientStream().writeBuffer1.putInt(newAuctionItem.getItemID());
			mc.packetHandler.getClientStream().writeBuffer1.putInt(newAuctionItem.getAmount());
			mc.packetHandler.getClientStream().writeBuffer1.putInt(newAuctionItem.getPrice());
			mc.packetHandler.getClientStream().finishPacket();

			myAuctions.setText(textField_amount, "");
			myAuctions.setText(textField_price, "");
			myAuctions.setText(textField_priceEach, "");
			myAuctions.hide(textField_amount);
			myAuctions.hide(textField_price);
			myAuctions.hide(textField_priceEach);
			selectItemAdd = 0;
			newAuctionItem = null;
			newAuctionInventoryIndex = -1;
		}
	}

	private void drawButton(GraphicsController graphics, int x, int y, int width, int height, String text,
							boolean checked, ButtonHandler handler) {
		int allColor = 0x333333;
		if (checked) {
			allColor = 0x659CDE;
		}
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			if (!checked)
				allColor = 0x263751;
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawBoxAlpha(x, y, width, height, allColor, 192);
		graphics.drawBoxBorder(x, width, y, height, 0x242424);
		graphics.drawString(text, x + (width / 2 - graphics.stringWidth(1, text) / 2), y + height / 2 + 5, 0xffffff, 1);
	}

	private void drawButtonFancy(GraphicsController graphics, int x, int y, int width, int height, String text,
								 boolean checked, ButtonHandler handler) {
		int allColor = 0x0A2B56;
		if (checked) {
			allColor = 0x659CDE;
		}
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			if (!checked)
				allColor = 0x263751;
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawBoxAlpha(x, y, width, height, allColor, 192);
		graphics.drawBoxBorder(x, width, y, height, 0xBFA086);
		graphics.drawString(text, x + (width / 2 - graphics.stringWidth(1, text) / 2), y + height / 2 + 5, 0xffffff, 1);
	}

	private void drawTextHit(GraphicsController graphics, int x, int y, int width, int height, String text,
							 boolean checked, ButtonHandler handler) {
		int allColor = 0xffffff;
		if (checked) {
			allColor = 0x6b8e23;
		}
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			allColor = 16711680;
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawString(text, x + (width / 2 - graphics.stringWidth(1, text) / 2), y + height / 2 + 5, allColor, 1);
	}

	private void drawAuctionMenu(GraphicsController graphics) {
		Collections.sort(auctionItems, auctionComparator);
		auctionMenu.clearList(auctionScrollHandle);

		graphics.drawBoxAlpha(x + 2, y + 61, 81, 223 + 4, 0, 60);
		graphics.drawBoxBorder(x + 2, 82, y + 61, 224 + 4, 0x343434);
		graphics.drawBoxAlpha(x + 3, y + 62, 80, 18, 0x6b8e23, 192);
		graphics.drawString("Categories", x + 12, y + 75, 0xffffff, 1);
		drawButton(graphics, x + 5, y + 85, 76, 18, "All", selectedFilter == 0, new ButtonHandler() {
			@Override
			void handle() {
				selectedFilter = 0;
				selectedAuction = -1;
				auctionMenu.resetScrollIndex(auctionScrollHandle);
			}
		});

		drawButton(graphics, x + 5, y + 105, 76, 18, "Weapon", selectedFilter == 1, new ButtonHandler() {
			@Override
			void handle() {
				selectedFilter = 1;
				selectedAuction = -1;
				auctionMenu.resetScrollIndex(auctionScrollHandle);
			}
		});

		drawButton(graphics, x + 5, y + 125, 76, 18, "Armour", selectedFilter == 2, new ButtonHandler() {
			@Override
			void handle() {
				selectedFilter = 2;
				selectedAuction = -1;
				auctionMenu.resetScrollIndex(auctionScrollHandle);
			}
		});

		drawButton(graphics, x + 5, y + 145, 76, 18, "Consumable", selectedFilter == 3, new ButtonHandler() {
			@Override
			void handle() {
				selectedFilter = 3;
				selectedAuction = -1;
				auctionMenu.resetScrollIndex(auctionScrollHandle);
			}
		});

		drawButton(graphics, x + 5, y + 165, 76, 18, "Projectile", selectedFilter == 4, new ButtonHandler() {
			@Override
			void handle() {
				selectedFilter = 4;
				selectedAuction = -1;
				auctionMenu.resetScrollIndex(auctionScrollHandle);
			}
		});

		drawButton(graphics, x + 5, y + 185, 76, 18, "Jewelry", selectedFilter == 5, new ButtonHandler() {
			@Override
			void handle() {
				selectedFilter = 5;
				selectedAuction = -1;
				auctionMenu.resetScrollIndex(auctionScrollHandle);
			}
		});
		drawButton(graphics, x + 5, y + 205, 76, 18, "Ore & Bar", selectedFilter == 6, new ButtonHandler() {
			@Override
			void handle() {
				selectedFilter = 6;
				selectedAuction = -1;
				auctionMenu.resetScrollIndex(auctionScrollHandle);
			}
		});
		drawButton(graphics, x + 5, y + 225, 76, 18, "Herblaw", selectedFilter == 7, new ButtonHandler() {
			@Override
			void handle() {
				selectedFilter = 7;
				selectedAuction = -1;
				auctionMenu.resetScrollIndex(auctionScrollHandle);
			}
		});
		drawButton(graphics, x + 5, y + 245, 76, 18, "Rare", selectedFilter == 8, new ButtonHandler() {
			@Override
			void handle() {
				selectedFilter = 8;
				selectedAuction = -1;
				auctionMenu.resetScrollIndex(auctionScrollHandle);
			}
		});
		drawButton(graphics, x + 5, y + 265, 76, 18, "Misc", selectedFilter == 9, new ButtonHandler() {
			@Override
			void handle() {
				selectedFilter = 9;
				selectedAuction = -1;
				auctionMenu.resetScrollIndex(auctionScrollHandle);
			}
		});

		graphics.drawBoxAlpha(x + 2, y + 37, width - 4, 22, 0, 128);

		graphics.drawString("Your money:", x + 4, y + 52, 0xffffff, 1);
		graphics.drawString(method74(mc.getInventoryCount(10)), x + 76, y + 52, 0xffffff, 2);

		graphics.drawString("Search:", x + 265, y + 52, 0xffffff, 1);
		graphics.drawBoxAlpha(x + 312, y + 39, 174, 18, 0x222222, 255);
		graphics.drawBoxBorder(x + 312, 174, y + 39, 18, 0x474843);
		String searchTerm = auctionMenu.getControlText(auctionSearchHandle);
		drawButton(graphics, x + 265, y + 14, 141, 21, "Sort: " + sortBy, false, new ButtonHandler() {
			@Override
			void handle() {
				orderingBy++;
				if (orderingBy >= 5)
					orderingBy = 0;

				if (orderingBy == 0) {
					sortBy = "Price Down";
				} else if (orderingBy == 1) {
					sortBy = "Price Up";
				} else if (orderingBy == 2) {
					sortBy = "Name";
				} else if (orderingBy == 3) {
					sortBy = "Price Each (down)";
				} else if (orderingBy == 4) {
					sortBy = "Price Each (up)";
				}
				Collections.sort(auctionItems, auctionComparator);
			}
		});

		LinkedList<AuctionItem> filteredList = new LinkedList<>();
		for (AuctionItem item : auctionItems) {
			ItemDef def = EntityHandler.getItemDef(item.getItemID());

			String itemName = def.getName().toLowerCase();
			String exactItemName = def.getName().toLowerCase();
			String command = def.getCommand().toLowerCase();

			String[] commandFilter = null;
			String[] nameFilter = null;
			String[] exactNameFilter = null;

			if (selectedFilter == 1 && ((24 & def.wearableID) == 0 || !def.isWieldable() || itemName.contains("shield"))) {
				continue;
			} else if (selectedFilter == 2 && ((24 & def.wearableID) != 0 || !def.isWieldable()) && !itemName.contains("shield")) {
				continue;
			} else if (selectedFilter == 3) {
				commandFilter = new String[]{"drink", "eat"};
				nameFilter = new String[]{"raw"};
			} else if (selectedFilter == 4) {
				nameFilter = new String[]{"-rune", "arrow", "bolt"};
			} else if (selectedFilter == 5) {
				nameFilter = new String[]{"uncut", "sapphire", "emerald", "ruby", "diamond", "dragonstone"};
				exactNameFilter = new String[]{"opal", "jade", "amulet of accuracy", "gold amulet", "brass necklace",
					"gold necklace", "holy symbol of saradomin", "unblessed holy symbol"};
			} else if (selectedFilter == 6) {
				nameFilter = new String[]{" ore", "coal", "bar", "clay"};
				exactNameFilter = new String[]{"gold", "silver", "silver certificate", "gold certificate"};
			} else if (selectedFilter == 7) {
				commandFilter = new String[]{"identify"};
				nameFilter = new String[]{"unfinished", "vial", "weed", "ground", "root", "scale"};
				exactNameFilter = new String[]{"Guam Leaf", "Marrentill", "Tarromin", "Harralander", "Irit leaf",
					"Avantoe", "Kwuarm", "Cadantine", "Torstol", "Pestle and mortar", "Eye of newt", "Jangerberries",
					"Red spiders eggs", "White berries", "Snape grass", "Wine of zamorak"};
			} else if (selectedFilter == 8) {
				nameFilter = new String[]{"halloween", "bunny", "party", "pumpkin", "easter", "scythe", "cracker"};
				exactNameFilter = new String[]{"Disc of returning", "santa's hat"};
			}
			if (selectedFilter == 9) {
				nameFilter = resources;
				exactNameFilter = new String[]{
					"fur", "leather", "wool", "bow string", "flax", "cow hide",
					"knife", "egg", "bucket", "milk", "flour", "skull", "grain",
					"needle", "thread", "holy", "water", "cadavaberries",
					"pot", "jug", "grapes", "shears", "tinderbox",
					"chisel", "hammer", "ashes", "apron", "chef's hat", "skirt", "silk",
					"flier", "garlic", "redberries", "rope", "bad wine", "cape",
					"eye of newt", "lobster pot", "net", "fishing rod", "fly fishing rod", "harpoon",
					"fishing bait", "feather"
				};
			}
			boolean skip = true;

			if (nameFilter != null) {
				for (String n : nameFilter) {
					if (itemName.contains(n)) {
						skip = false;
						break;
					}
				}
			}

			if (exactNameFilter != null) {
				for (String enf : exactNameFilter) {
					if (exactItemName.equalsIgnoreCase(enf)) {
						skip = false;
						break;
					}
				}
			}

			if (commandFilter != null && skip) {
				for (String c : commandFilter) {
					if (command.contains(c)) {
						skip = false;
						break;
					}
				}
			}

			if (nameFilter != null || commandFilter != null || exactNameFilter != null) {
				if (skip) {
					continue;
				}
			}

			if (itemName.contains(searchTerm.toLowerCase())) {
				filteredList.add(item);
			}
		}
		if (selectedAuction == -1) {
			auctionMenu.clearList(auctionScrollHandle);
			auctionMenu.hide(textField_buyAmount);
			auctionMenu.show(auctionScrollHandle);
			int boxWidth = (48);
			int boxHeight = (33);

			int listX = x + 90;
			int listY = y + 85;
			graphics.drawBoxAlpha(listX - 4, listY - 23, 401, 18, 0x6b8e23, 192);
			graphics.drawBoxAlpha(listX - 4, listY - 5, 401, 208, 0, 60);
			graphics.drawBoxBorder(listX - 4, 402, listY - 4 - 20, 208 + 20, 0x343434);

			graphics.drawString("Item", listX + 1, listY - 10, 0xffffff, 1);

			graphics.drawString("Sale Price", listX + 295, listY - 10, 0xffffff, 1);

			int listStartPoint = auctionMenu.getScrollPosition(auctionScrollHandle);
			int listEndPoint = listStartPoint + 5;
			int showing = 0;
			for (int i = -1; i < filteredList.size(); i++) {
				showing = i + 1;
				if (i >= 500) {
					break;
				}
				auctionMenu.setListEntry(auctionScrollHandle, i + 1, "", 0, null, null);

				if (i < listStartPoint || i > listEndPoint)
					continue;
				AuctionItem ahItem = filteredList.get(i);
				if (mc.getMouseX() >= (listX - 3) && mc.getMouseY() >= (listY - 5) && mc.getMouseX() <= listX + 384
					&& mc.getMouseY() <= (listY - 5) + boxHeight) {
					graphics.drawBoxAlpha(listX - 3, listY - 5, 400, boxHeight, 0x980000, 128);
					if (mc.getMouseClick() == 1) {
						selectedAuction = i;
						auctionMenu.setText(textField_buyAmount, "1");
						auctionMenu.setFocus(textField_buyAmount);
						mc.setMouseClick(0);
					}
				} else {
					if (selectedAuction == i) {
						graphics.drawBoxAlpha(listX - 3, listY - 5, 400, boxHeight, 0xff0000, 128);
					} else {
						graphics.drawBoxAlpha(listX - 3, listY - 5, 400, boxHeight, 0x45454545, 128);
					}
				}
				graphics.drawBoxBorder(listX - 4, 402, listY - 5, boxHeight + 1, 0x343434);
				ItemDef def = EntityHandler.getItemDef(ahItem.getItemID());
				int price = ahItem.getPrice();
				int priceEach = 0;
				if (price > 0) {
					priceEach = price / ahItem.getAmount();
				}

				graphics.drawString(mc.ellipsize(def.getName(), 22), listX + 50, listY + boxHeight / 2, 0xffffff, 2);
				graphics.drawString(getTime(ahItem) + " hours", listX + 200, listY + boxHeight / 2, 0xffffff, 2);
				graphics.drawString(basicNumber(priceEach) + " gp (ea)", listX + 295, listY + boxHeight / 2 - 8, 0xffffff, 0);
				graphics.drawString(basicNumber(price) + " gp (all)", listX + 295, listY + boxHeight / 2 + 10 - 4, 0xffffff, 0);
				graphics.drawBoxAlpha(listX - 3, listY - 4, boxWidth + 1, boxHeight - 1, 0xfffffff, 128);

				mc.getSurface().drawSpriteClipping(mudclient.spriteItem + def.getSprite(), listX - 3, listY - 5, 48,
					32, def.getPictureMask(), 0, false, 0, 1);

				graphics.drawString(String.valueOf(ahItem.getAmount()), listX + 1 - 3, listY + 10 - 4, 65280, 1);
				listY += boxHeight + 2;
			}
			graphics.drawString("Showing: " + (showing) + "/" + (filteredList.size()) + " items", listX + 49, y + 75,
				0xffffff, 1);
			graphics.drawString("Expires in", listX + 201, y + 75, 0xffffff, 1);
		}

		if (selectedAuction != -1 && selectedAuction < filteredList.size()) {
			int selectX = x + 90;
			int selectY = y + 85;
			auctionMenu.hide(auctionScrollHandle);
			auctionMenu.show(textField_buyAmount);
			final AuctionItem ahItem = filteredList.get(selectedAuction);
			graphics.drawBoxAlpha(selectX - 4, selectY - 23, 401, 18, 0xff0000, 192);
			graphics.drawBoxAlpha(selectX - 4, selectY - 5, 401, 208, 0, 60);
			graphics.drawBoxBorder(selectX - 4, 402, selectY - 4 - 20, 208 + 20, 0x343434);

			drawButton(graphics, selectX + 376, selectY - 24, 22, 19, "X", false, new ButtonHandler() {
				@Override
				void handle() {
					activeInterface = 0;
					selectedAuction = -1;
				}
			});

			ItemDef def = EntityHandler.getItemDef(ahItem.getItemID());
			int price = ahItem.getPrice();
			int priceEach = 0;
			if (price > 0) {
				priceEach = price / ahItem.getAmount();
			}

			graphics.drawBoxAlpha(selectX + 3, selectY, 389, 197, 0x454545, 192);
			graphics.drawBoxBorder(selectX + 2, 390, selectY, 197 + 1, 0x343434);
			graphics.drawLineHoriz(selectX + 6, selectY + 100, 382, 0x222222);

			graphics.drawString("Selected item: " + def.getName(), selectX + 2, selectY - 10, 0xffffff, 1);

			graphics.drawString("Description: " + def.getDescription(), selectX + 8, selectY + 15, 0xffffff, 1);


			graphics.drawBoxAlpha(selectX + 8, selectY + 22, 49, 33, 0xfffffff, 128);
			graphics.drawBoxBorder(selectX + 8, 50, selectY + 22, 34, 0);

			mc.getSurface().drawSpriteClipping(mudclient.spriteItem + def.getSprite(), selectX + 8, selectY + 22, 48, 32,
				def.getPictureMask(), 0, false, 0, 1);
			graphics.drawString(String.valueOf(ahItem.getAmount()), selectX + 10, selectY + 22 + 11, 65280, 1);

			graphics.drawString(getTime(ahItem) + "h left", selectX + 60, selectY + 32, 0xffffff, 2);

			graphics.drawString("Quantity: " + method74(ahItem.getAmount()), selectX + 200, selectY + 32, 0xffffff, 2);
			graphics.drawString("Total: " + method74(price) + "gp", selectX + 200, selectY + 32 + 14, 0xffffff, 2);
			graphics.drawString("Each: " + method74(priceEach) + "gp", selectX + 200, selectY + 32 + 28, 0xffffff, 2);

			graphics.drawString("Seller: " + ahItem.getSeller(), selectX + 8, selectY + 83, 0xffffff, 2);

			if (mc.getLocalPlayer().isMod()) {
				drawButton(graphics, selectX + 186, selectY + 68, 200, 22, "@red@[Staff] Delete Item", false, new ButtonHandler() {
					@Override
					void handle() {
						sendModCancelAuction(ahItem.getAuctionID());
					}
				});
			} else {
				drawButton(graphics, selectX + 186, selectY + 68, 200, 22, "@gre@Add Seller to Friendlist", false, new ButtonHandler() {
					@Override
					void handle() {
						mc.addFriend(ahItem.getSeller());
					}
				});
			}

			graphics.drawString("Enter amount:", selectX + 8, selectY + 120, 0xffffff, 1);
			graphics.drawBoxAlpha(selectX + 7, selectY + 125, 100, 18, 0x222222, 255);
			graphics.drawBoxBorder(selectX + 7, 101, selectY + 125, 18, 0x555555);

			String amountText = auctionMenu.getControlText(textField_buyAmount);

			if (amountText.length() > 0 && amountText.length() < 10) {
				int checkoutPrice = Integer.parseInt(amountText);

				if (checkoutPrice > ahItem.getAmount()) {
					checkoutPrice = ahItem.getAmount();
				}
				if (checkoutPrice <= 0) {
					checkoutPrice = 1;
				}

				if (checkoutPrice <= ahItem.getAmount()) {
					graphics.drawString("Checkout Price: " + method74(priceEach * checkoutPrice) + "gp", selectX + 8, selectY + 156, 0xffffff, 2);
					drawButtonFancy(graphics, selectX + 8, selectY + 125 + 29 + 11, 378, 22, "Purchase Now", false, new ButtonHandler() {
						@Override
						void handle() {
							sendAuctionBuy(ahItem);
						}
					});
				}
			}
		} else {
			selectedAuction = -1;
		}
		auctionMenu.drawPanel();
	}

	private String method74(int i) {
		String s = String.valueOf(i);
		for (int j = s.length() - 3; j > 0; j -= 3)
			s = s.substring(0, j) + "," + s.substring(j);

		if (s.length() > 8)
			s = "@gre@" + s.substring(0, s.length() - 8) + " million @whi@(" + s + ")";
		else if (s.length() > 4) {
			s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
		}
		return s;
	}

	private String basicNumber(int priceEach) {
		if (priceEach >= 1000000) {
			double millions = priceEach / 1000000D;
			return "@gre@" + String.format("%.2f", millions) + "M";
		} else if (priceEach >= 1000) {
			double thousands = priceEach / 1000D;

			return String.format("%.2f", thousands) + "@whi@K";
		}
		return "" + priceEach;
	}

	private double getFee() {
		return fee;
	}

	private void setFee(double d) {
		this.fee = d;
	}

	private String getTime(AuctionItem ahItem) {
		int h = ahItem.getHoursLeft();
		String col = "";
		if (h <= 1)
			col = "@red@";
		else if (h <= 3)
			col = "@or3@";
		else if (h <= 6)
			col = "@or2@";
		else if (h <= 12)
			col = "@lre@";
		else if (h <= 16)
			col = "@or1@";
		else if (h <= 20)
			col = "@gr1@";
		else if (h <= 24)
			col = "@gre@";
		return col + "" + h;
	}

	private void sendModCancelAuction(int auctionID) {
		selectedAuction = -1;
		mc.packetHandler.getClientStream().newPacket(199);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(10);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(5);
		mc.packetHandler.getClientStream().writeBuffer1.putInt(auctionID);
		mc.packetHandler.getClientStream().finishPacket();
	}

	private void sendAuctionBuy(AuctionItem ahItem) {
		int t = Integer.parseInt(auctionMenu.getControlText(textField_buyAmount));
		mc.packetHandler.getClientStream().newPacket(199);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(10);
		mc.packetHandler.getClientStream().writeBuffer1.putByte(0);
		mc.packetHandler.getClientStream().writeBuffer1.putInt(ahItem.getAuctionID());
		mc.packetHandler.getClientStream().writeBuffer1.putInt(t);
		mc.packetHandler.getClientStream().finishPacket();
		if (t >= ahItem.getAmount() || ahItem.getAmount() <= 1 || ahItem.getSeller().equals(mc.getLocalPlayer().displayName)) {
			selectedAuction = -1;
		}
	}

	public boolean keyDown(int key) {
		if (activeInterface == 0) {
			if (auctionMenu.focusOn(auctionSearchHandle) || auctionMenu.focusOn(textField_buyAmount)) {
				if (auctionMenu.focusOn(auctionSearchHandle)) {
					selectedAuction = -1;
				}
				if (auctionMenu.focusOn(textField_buyAmount)) {
					if (auctionMenu.getControlText(textField_buyAmount).length() == 0 && key == 48) {
						return true;
					}
					if (key >= 48 && key <= 57 || key == 8) {
						auctionMenu.keyPress(key);
					}
				} else
					auctionMenu.keyPress(key);
				return true;
			}
		} else if (activeInterface == 1) {
			if (myAuctions.focusOn(textField_amount) || myAuctions.focusOn(textField_price)
				|| myAuctions.focusOn(textField_priceEach)) {
				if (newAuctionItem != null) {
					if (key >= 48 && key <= 57 || key == 8) {
						myAuctions.keyPress(key);
					}
					String amountText = myAuctions.getControlText(textField_amount);
					String priceText = myAuctions.getControlText(textField_price);
					String priceEachText = myAuctions.getControlText(textField_priceEach);

					if (amountText.length() == 0) {
						return true;
					}
					if (priceText.length() == 0) {
						return true;
					}
					if (priceEachText.length() == 0) {
						return true;
					}

					int amount = Integer.parseInt(amountText);
					int price = Integer.parseInt(priceText);
					int priceEach = Integer.parseInt(priceEachText);

					if (amount > mc.getInventoryCount(newAuctionItem.getItemID())) {
						amount = mc.getInventoryCount(newAuctionItem.getItemID());
					}
					if (amount <= 0) {
						amount = 0;
					}

					if (myAuctions.focusOn(textField_amount)) {
						price = amount * priceEach;
					} else if (myAuctions.focusOn(textField_price)) {
						priceEach = price / amount;
					} else if (myAuctions.focusOn(textField_priceEach)) {
						price = amount * priceEach;
					}

					newAuctionItem.setAmount(amount);
					newAuctionItem.setPrice(price);
					/*if(price * 0.025 < 5) {
						setFee(5);
					} else {
						setFee(price * 0.025);
					}*/
					updateTextFields(amount, price, priceEach);
				}
				return true;
			}
		}
		return false;
	}

	private void updateTextFields(int amount, int price, int priceEach) {
		myAuctions.setText(textField_price, "" + price);
		myAuctions.setText(textField_priceEach, "" + priceEach);
		myAuctions.setText(textField_amount, "" + amount);
	}

	public void resetAuctionItems() {
		auctionItems.clear();
	}

	public void addAuction(int auctionID, int itemID, int amount, int price, String seller, int hoursLeft) {
		auctionItems.add(new AuctionItem(auctionID, itemID, amount, price, seller, hoursLeft));
	}

	private void resetAllVariables() {
		auctionMenu.clearList(auctionSearchHandle);
		auctionMenu.resetScrollIndex(auctionScrollHandle);
		myAuctions.resetScrollIndex(myAuctionScrollHandle);
		auctionMenu.setText(auctionSearchHandle, "");
		myAuctions.setText(textField_amount, "");
		myAuctions.setText(textField_priceEach, "");
		myAuctions.setText(textField_price, "");
		selectItemAdd = 0;
		newAuctionItem = null;
		newAuctionInventoryIndex = -1;
		selectedCancelAuction = -1;
		selectedAuction = -1;
		activeInterface = 0;
		selectedFilter = 0;
		orderingBy = 0;
		sortBy = "Price Down";
		auctionMenu.setFocus(-1);
		myAuctions.setFocus(-1);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}

abstract class ButtonHandler {
	abstract void handle();
}

class AuctionItem {

	private int auctionID, itemID, amount, price;
	private String seller;
	private int hoursLeft;

	AuctionItem(int auctionID, int itemID, int amount, int price, String seller2, int hoursLeft) {
		this.auctionID = auctionID;
		this.itemID = itemID;
		this.amount = amount;
		this.price = price;
		this.seller = seller2;
		this.hoursLeft = hoursLeft;
	}

	int getAuctionID() {
		return auctionID;
	}

	public void setAuctionID(int auctionID) {
		this.auctionID = auctionID;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	int getPrice() {
		return price;
	}

	void setPrice(int price) {
		this.price = price;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	int getHoursLeft() {
		return hoursLeft;
	}

	public void setHoursLeft(int hoursLeft) {
		this.hoursLeft = hoursLeft;
	}
}
