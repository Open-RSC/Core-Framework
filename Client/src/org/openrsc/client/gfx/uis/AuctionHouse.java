package org.openrsc.client.gfx.uis;

import java.awt.Color;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.openrsc.client.Auction;
import org.openrsc.client.Raster;
import org.openrsc.client.mudclient;
import org.openrsc.client.entityhandling.EntityHandler;
import org.openrsc.client.gfx.GraphicalComponent;
import org.openrsc.client.gfx.GraphicalOverlay;
import org.openrsc.client.gfx.action.Action;
import org.openrsc.client.gfx.action.Hovering;
import org.openrsc.client.gfx.action.KeyListener;
import org.openrsc.client.gfx.action.ScrollListener;
import org.openrsc.client.gfx.components.Box;
import org.openrsc.client.gfx.components.DrawSprite;
import org.openrsc.client.gfx.components.DrawString;
import org.openrsc.client.gfx.components.GameFrame;
import org.openrsc.client.gfx.components.ScrollBar;
import org.openrsc.client.gfx.components.TextBox;

public class AuctionHouse extends GraphicalOverlay {
	private GameFrame frame = new GameFrame("Auction House", new Rectangle(30, 20, 442, 277));
	// private DrawString hoverItemName;
	private int auctionSize = 0;
	private List<Auction> auctions = new ArrayList<Auction>();
	private DecimalFormat df = new DecimalFormat("#,###");
	private Box auctionListButton, yourAuctionsButton;
	private View view = new View();
	private mudclient<?> mc;
	private boolean isAuctionView = true;
	private List<Box> slots = new ArrayList<Box>();
	private List<Box> yourAuctionSlots = new ArrayList<Box>();
	private ScrollBar scroll, scroll2;
	private int selectedIndex = -1;
	private String selectedItemName = "null";
	private int selectedItemID = -1;
	private long selectedItemQuantity = -1;
	private long selectedItemPriceEach = -1;
	private int type = 0;
	private int selectedInventoryItemIndex = -1;
	private int selectedCancelIndex = -1;

	public String shortenPrice(String str) {
		String s = String.valueOf(str);
		for (int j = s.length() - 3; j > 0; j -= 3) {
			s = s.substring(0, j) + "," + s.substring(j);
		}
		if (s.length() > 8) {
			s = "@gre@" + s.substring(0, s.length() - 5) + "M";
			s = s.replaceAll(",", "");
			s = s.substring(0, s.length() - 3) + "." + s.substring(s.length() - 3);
		} else if (s.length() > 4) {
			s = "@whi@" + s.substring(0, s.length() - 4) + "." + s.substring(s.length() - 3, s.length() - 1) + "K";
			s = s.replaceAll(",", "");
		}
		return s;
	}

	public String insertCommas(String str) {
		String s = String.valueOf(str);
		for (int j = s.length() - 3; j > 0; j -= 3) {
			s = s.substring(0, j) + "," + s.substring(j);
		}
		String s1 = s;
		if (s.length() > 8) {
			s = "@gre@" + s.substring(0, s.length() - 7) + "." + s.substring(s.length() - 6, s.length() - 4);
			s = s.replaceAll(",", "");
			s += " million@whi@ (" + s1 + ")gp";
		} else if (s.length() > 4) {
			s = "@cya@" + s.substring(0, s.length() - 4) + "." + s.substring(s.length() - 3, s.length() - 1);
			s = s.replaceAll(",", "");
			s += "K @whi@(" + s1 + ")gp";
		}
		return s;
	}

	public void resetScrollIndex() {
		scroll.setFirstIndex(0);
		scroll2.setFirstIndex(0);
	}

	private class View {
		public Box sortBy, typeBox, auctionList, selectedItemBox, typeAllButton, typeWieldableButton,
				typeConsumableButton, typeRunesButton, typeArrowsButton, typeNotesButton, createAuctionBox, yourAuctionsBox,
				selectedInventoryItemSpriteBox, createAuctionButton, cancelAuctionButton;
		public DrawString coins, searchWord, auctionSizeDisplay, selectedItemNameString, priceEach, priceAll, quantity,
				selectedInventoryItemAmount, totalPrice, salePrice, saleAmount;
		public DrawString[] inventoryItemAmounts = new DrawString[30];
		public Box[] inventoryItemBoxes = new Box[30];
		public DrawSprite[] inventoryItemSprites = new DrawSprite[30], noteSprites = new DrawSprite[30];
		public DrawSprite coinsSprite, selectedItemSprite, selectedInventoryItemSprite;
		private TextBox coinsAmount, searchBox, amountBox, salePriceInput, saleAmountInput;

		public void update(boolean flag) {
			/* Buy shit */
			sortBy.setVisible(flag);
			coins.setVisible(flag);
			coinsSprite.setVisible(flag);
			coinsAmount.setVisible(flag);
			searchWord.setVisible(flag);
			searchBox.setVisible(flag);
			typeBox.setVisible(flag);
			auctionList.setVisible(flag);
			scroll.setVisible(flag);
			selectedItemBox.setVisible(flag);
			coinsAmount.setTextUnchangable(df.format(mc.inventoryCount(10)));
			auctionSizeDisplay.setText("Showing: " + auctionSize + " items");
			if (!flag)
				searchBox.setText("");
			List<Auction> auctionList = filterName(view.searchBox.getText().replaceAll("\\*", ""), auctions);
			for (int i = 0; i < (auctionList.size() > 4 ? 4 : auctionList.size()); i++) {
				Box slot = slots.get(i);
				slot.setVisible(flag);
				if (auctionList.size() > i + scroll.getIndex() && i + scroll.getIndex() > -1 && auctionList.get(i + scroll.getIndex()).getIndex() == selectedIndex) {
					slot.setFill(0xFF0000);
					slot.setOpaque(128);
				} else {
					slot.setFill(0xFFEFED);
					slot.setOpaque(50);
				}
			}
			if (auctionList.size() <= 4) {
				scroll.setVisible(false);
				for (int i = auctionList.size(); i < 4; i++)
					slots.get(i).setVisible(false);
			}
			if (selectedIndex != -1) {
				selectedItemNameString.setText("Selected item: " + selectedItemName);
				selectedItemSprite.setSpriteIndex(2150 + EntityHandler.getItemDef(selectedItemID).getSprite());
				selectedItemSprite.setOverlay(EntityHandler.getItemDef(selectedItemID).getPictureMask());
				priceEach.setText("Price each: " + insertCommas(String.valueOf(selectedItemPriceEach)));
				priceAll.setText(
						"Price for all: " + insertCommas(String.valueOf(selectedItemPriceEach * selectedItemQuantity)));
				quantity.setText("Quantity: " + selectedItemQuantity);
				selectedItemBox.setVisible(true);
			} else
				selectedItemBox.setVisible(false);

			/* TODO: Sell shit */
			createAuctionBox.setVisible(!flag);
			yourAuctionsBox.setVisible(!flag);
			scroll2.setVisible(!flag);
			List<Auction> yourAuctions = new ArrayList<Auction>();
			for (Auction auction : auctions)
				if (auction.isOwner())
					yourAuctions.add(auction);

			for (int i = 0; i < (yourAuctions.size() > 5 ? 5 : yourAuctions.size()); i++) {
				Box slot = yourAuctionSlots.get(i);
				slot.setVisible(!flag);
				if (yourAuctions.size() > i + scroll2.getIndex() && i + scroll2.getIndex() > -1 && yourAuctions.get(i + scroll2.getIndex()).getIndex() == selectedCancelIndex) {
					slot.setFill(0xFF0000);
					slot.setOpaque(128);
				} else {
					slot.setFill(0xFFEFED);
					slot.setOpaque(50);
				}
				if (slot.isVisible() && slot.hovering && yourAuctions.size() > i + scroll2.getIndex() && i + scroll2.getIndex() > -1 && EntityHandler
						.getItemDef(yourAuctions.get(i + scroll2.getIndex()).getID()).getName().length() > 13)
					mc.gameGraphics.drawCenteredString(
							EntityHandler.getItemDef(yourAuctions.get(i + scroll2.getIndex()).getID()).getName(),
							mc.mouseX, mc.mouseY - 16, 1, 0xFFFFFF);
			}
			if (yourAuctions.size() <= 5) {
				scroll2.setVisible(false);
				for (int i = yourAuctions.size(); i < 5; i++)
					yourAuctionSlots.get(i).setVisible(false);
			}
			int offset = 0;
			for (int i = 0; i < 30; i++) {
				final int newIndex = i + offset;
				if (newIndex == selectedInventoryItemIndex)
					inventoryItemBoxes[i].setFill(0xFF0000);
				else
					inventoryItemBoxes[i].setFill(0x989898);
				if (i + offset >= mc.inventoryCount) {
					inventoryItemSprites[i].setVisible(false);
					inventoryItemAmounts[i].setVisible(false);
					noteSprites[i].setVisible(false);
					inventoryItemBoxes[i].setAction(null);
					inventoryItemBoxes[i].setFillHovering(inventoryItemBoxes[i].getFill());
				} else {
					inventoryItemBoxes[i].setFillHovering(0xFF0000);
					if (mc.inventoryItems[newIndex] == 10
							|| EntityHandler.getItemDef(mc.inventoryItems[newIndex]).quest) {
						offset++;
						i--;
						continue;
					}
					inventoryItemSprites[i].setVisible(true);
					if (EntityHandler.getItemDef(mc.inventoryItems[newIndex]).isNote()) {
						noteSprites[i].setVisible(true);
						inventoryItemSprites[i].setScaleX(25);
						inventoryItemSprites[i].setScaleY(15);
						inventoryItemSprites[i].setBoundarys(new Rectangle(7, 5, 45, 30));
					} else {
						noteSprites[i].setVisible(false);
						inventoryItemSprites[i].setScaleX(35);
						inventoryItemSprites[i].setScaleY(25);
						inventoryItemSprites[i].setBoundarys(new Rectangle(0, 0, 45, 30));
					}
					inventoryItemSprites[i]
							.setSpriteIndex(2150 + EntityHandler.getItemDef(mc.inventoryItems[newIndex]).getSprite());
					inventoryItemSprites[i]
							.setOverlay(EntityHandler.getItemDef(mc.inventoryItems[newIndex]).getPictureMask());
					inventoryItemBoxes[i].setAction(new Action() {
						@Override
						public void action(int x, int y, int button) {
							setSelectedInventoryItemIndex(newIndex);
							salePriceInput.setTextUnchangable(String.valueOf(EntityHandler
									.getItemDef(mc.inventoryItems[selectedInventoryItemIndex]).getBasePrice()));
							saleAmountInput.setTextUnchangable(
									String.valueOf(mc.inventoryCount(mc.inventoryItems[selectedInventoryItemIndex])));
							totalPrice.setText("Total price: " + EntityHandler
									.getItemDef(mc.inventoryItems[selectedInventoryItemIndex]).getBasePrice()
									* mc.inventoryCount(mc.inventoryItems[selectedInventoryItemIndex]));
						}
					});
					if (EntityHandler.getItemDef(mc.inventoryItems[newIndex]).isStackable()) {
						inventoryItemAmounts[i].setText(String.valueOf(mc.inventoryCount(mc.inventoryItems[newIndex])));
						inventoryItemAmounts[i].setVisible(true);
					} else
						inventoryItemAmounts[i].setVisible(false);
				}
			}
			if (selectedCancelIndex != -1)
				cancelAuctionButton.setVisible(true);
			else
				cancelAuctionButton.setVisible(false);

			if (selectedInventoryItemIndex != -1) {
				selectedInventoryItemSpriteBox.setVisible(true);
				totalPrice.setVisible(true);
				salePrice.setVisible(true);
				salePriceInput.setVisible(true);
				saleAmount.setVisible(true);
				saleAmountInput.setVisible(true);
				createAuctionButton.setVisible(true);
				selectedInventoryItemAmount
						.setText(String.valueOf(mc.inventoryCount(mc.inventoryItems[selectedInventoryItemIndex])));
				selectedInventoryItemSprite.setSpriteIndex(
						2150 + EntityHandler.getItemDef(mc.inventoryItems[selectedInventoryItemIndex]).getSprite());
				selectedInventoryItemSprite.setOverlay(
						EntityHandler.getItemDef(mc.inventoryItems[selectedInventoryItemIndex]).getPictureMask());
			} else {
				selectedInventoryItemSpriteBox.setVisible(false);
				totalPrice.setVisible(false);
				salePrice.setVisible(false);
				salePriceInput.setVisible(false);
				saleAmount.setVisible(false);
				saleAmountInput.setVisible(false);
				createAuctionButton.setVisible(false);
			}
		}
	}

	public void populateBuyView() {
		final List<Auction> auctions = filterName(view.searchBox.getText().replaceAll("\\*", ""), this.auctions);
		scroll.setMenuListTextCount(auctions.size());
		for (int i = 0; i < (auctions.size() > 4 ? 4 : auctions.size()); i++) {
			final Box slot = slots.get(i);
			for (GraphicalComponent gc : slot.getComponents())
				gc.setVisible(false);
			slot.setComponents(new ArrayList<GraphicalComponent>());
			int slotXOffset = 0;
			final int i1 = i + scroll.getIndex();
			if (auctions.size() <= i1 || i1 < 0)
				continue;
			slot.setAction(new Action() {
				@Override
				public void action(int x, int y, int button) {
					if (button == 1) {
						if (!mc.tester) {
							setSelectedIndex(auctions.get(i1).getIndex());
							setSelectedItemName(EntityHandler.getItemDef(auctions.get(i1).getID()).getName());
							setSelectedItemID(auctions.get(i1).getID());
							setSelectedItemPriceEach(auctions.get(i1).getPrice());
							setSelectedItemQuantity(auctions.get(i1).getAmount());
						} else {
							for (int ix = 0; ix < mc.menuLength; ix++) {
								int k = mc.tradeWindowX + 2;
								int i1 = mc.tradeWindowY + 11 + (ix + 1) * 15;
								if (mc.mouseX <= k - 2 || mc.mouseY <= i1 - 12 || mc.mouseY >= i1 + 4
										|| mc.mouseX >= (k - 3) + mc.menuWidth)
									continue;
								mc.menuClick(ix);
							}
							mc.tradeWindowX = -100;
							mc.tradeWindowY = -100;
							mc.mouseButtonClick = 0;
							mc.tester = false;
							mc.setValue = false;
						}
					} else if (button == 2 && !mc.tester
							&& (mc.ourPlayer.admin == 1 || mc.ourPlayer.admin == 2)) {
						mc.tradeWindowX = x;
						mc.tradeWindowY = y;

						for (int jx = 0; jx < mc.menuLength; jx++) {
							mc.menuText1[jx] = null;
							mc.menuText2[jx] = null;
							mc.menuActionVariable[jx] = -1;
							mc.menuActionVariable2[jx] = -1;
							mc.menuID[jx] = -1;
						}
						mc.menuLength = 0;
						
						mc.menuText1[mc.menuLength] = "Cancel";
						mc.menuText2[mc.menuLength] = "Auction";
						mc.menuID[mc.menuLength] = 787;
						mc.menuActionVariable[mc.menuLength] = auctions.get(i1).getIndex();
						mc.menuLength++;

						mc.tester = true;
					}
				}
			});
			Box spriteBox = new Box(new Rectangle(slotXOffset, 0, 45, 30));
			spriteBox.setFillHovering(spriteBox.getFill());
			spriteBox.setCentered(false);
			slot.add(spriteBox);
			if (EntityHandler.getItemDef(auctions.get(i1).getID()).isNote()) {
				DrawSprite noteSprite = new DrawSprite(2604, new Rectangle(2, 0, 45, 30), 35, 25, 0);
				spriteBox.add(noteSprite);
				DrawSprite itemSprite = new DrawSprite(
						2150 + EntityHandler.getItemDef(auctions.get(i1).getID()).getSprite(),
						new Rectangle(7, 7, 45, 30), 25, 15,
						EntityHandler.getItemDef(auctions.get(i1).getID()).getPictureMask());
				spriteBox.add(itemSprite);
			} else {
				DrawSprite itemSprite = new DrawSprite(
						2150 + EntityHandler.getItemDef(auctions.get(i1).getID()).getSprite(),
						new Rectangle(5, 3, 45, 30), 35, 25,
						EntityHandler.getItemDef(auctions.get(i1).getID()).getPictureMask());
				spriteBox.add(itemSprite);
			}
			DrawString amount = new DrawString(df.format(auctions.get(i1).getAmount()), false,
					new Rectangle(2, 0, 0, 0));
			amount.setColor(Color.green.getRGB());
			amount.setFillHovering(Color.green.getRGB());
			spriteBox.add(amount);
			slotXOffset += spriteBox.getWidth() + 15;
			DrawString itemName = new DrawString(EntityHandler.getItemDef(auctions.get(i1).getID()).getName(), false,
					new Rectangle(slotXOffset, 7,
							mc.gameGraphics.textWidth(EntityHandler.getItemDef(auctions.get(i1).getID()).getName(), 1),
							14));
			itemName.setColor(Color.white.getRGB());
			itemName.setFillHovering(Color.white.getRGB());
			slot.add(itemName);
			slotXOffset += 180;
			DrawString priceEach = new DrawString(shortenPrice(String.valueOf(auctions.get(i1).getPrice())) + " (ea)",
					false, new Rectangle(slotXOffset, 0, 0, 14));
			priceEach.setColor(Color.white.getRGB());
			priceEach.setFillHovering(Color.white.getRGB());
			slot.add(priceEach);
			DrawString priceAll = new DrawString(
					shortenPrice(String.valueOf(auctions.get(i1).getPrice() * auctions.get(i1).getAmount())) + " (all)",
					false, new Rectangle(slotXOffset, 15, 0, 14));
			priceAll.setColor(Color.white.getRGB());
			priceAll.setFillHovering(Color.white.getRGB());
			slot.add(priceAll);
		}
	}

	public void populateYourAuctions() {
		final List<Auction> yourAuctions = new ArrayList<Auction>();
		for (Auction auction : auctions)
			if (auction.isOwner())
				yourAuctions.add(auction);
		scroll2.setMenuListTextCount(yourAuctions.size());
		for (int i = 0; i < (yourAuctions.size() > 5 ? 5 : yourAuctions.size()); i++) {
			final Box slot = yourAuctionSlots.get(i);
			for (GraphicalComponent gc : slot.getComponents())
				gc.setVisible(false);
			slot.setComponents(new ArrayList<GraphicalComponent>());
			int slotXOffset = 0;
			final int i1 = i + scroll2.getIndex();
			if (yourAuctions.size() <= i1 || i1 < 0)
				continue;
			slot.setAction(new Action() {
				@Override
				public void action(int x, int y, int button) {
					setSelectedCancelIndex(yourAuctions.get(i1).getIndex());
				}
			});
			Box spriteBox = new Box(new Rectangle(slotXOffset, 0, 45, 30));
			spriteBox.setFillHovering(spriteBox.getFill());
			spriteBox.setCentered(false);
			slot.add(spriteBox);
			if (EntityHandler.getItemDef(yourAuctions.get(i1).getID()).isNote()) {
				DrawSprite noteSprite = new DrawSprite(2604, new Rectangle(2, 0, 45, 30), 35, 25, 0);
				spriteBox.add(noteSprite);
				DrawSprite itemSprite = new DrawSprite(
						2150 + EntityHandler.getItemDef(yourAuctions.get(i1).getID()).getSprite(),
						new Rectangle(7, 7, 45, 30), 25, 15,
						EntityHandler.getItemDef(yourAuctions.get(i1).getID()).getPictureMask());
				spriteBox.add(itemSprite);
			} else {
				DrawSprite itemSprite = new DrawSprite(
						2150 + EntityHandler.getItemDef(yourAuctions.get(i1).getID()).getSprite(),
						new Rectangle(5, 3, 45, 30), 35, 25,
						EntityHandler.getItemDef(yourAuctions.get(i1).getID()).getPictureMask());
				spriteBox.add(itemSprite);
			}
			/*
			 * DrawSprite sprite = new DrawSprite( 2150 +
			 * EntityHandler.getItemDef(yourAuctions.get(i1).getID()).getSprite(
			 * ), new Rectangle(5, 0, 45, 30), 35, 25,
			 * EntityHandler.getItemDef(yourAuctions.get(i1).getID()).
			 * getPictureMask()); spriteBox.add(sprite);
			 */
			DrawString amount = new DrawString(df.format(yourAuctions.get(i1).getAmount()), false,
					new Rectangle(2, 0, 0, 0));
			amount.setColor(Color.green.getRGB());
			amount.setFillHovering(Color.green.getRGB());
			spriteBox.add(amount);
			slotXOffset += spriteBox.getWidth() + 5;
			String name = EntityHandler.getItemDef(yourAuctions.get(i1).getID()).getName();
			if (name.length() > 13)
				name = name.substring(0, 13) + "...";
			DrawString itemName = new DrawString(name, false, new Rectangle(slotXOffset, 7,
					mc.gameGraphics.textWidth(EntityHandler.getItemDef(yourAuctions.get(i1).getID()).getName(), 1),
					14));
			itemName.setColor(Color.white.getRGB());
			itemName.setFillHovering(Color.white.getRGB());

			slot.add(itemName);
			slotXOffset += 120;
			slotXOffset += 5;
			DrawString priceEach = new DrawString(
					shortenPrice(String.valueOf(yourAuctions.get(i1).getPrice())) + " (ea)", false,
					new Rectangle(slotXOffset, 0, 0, 14));
			priceEach.setColor(Color.white.getRGB());
			priceEach.setFillHovering(Color.white.getRGB());
			slot.add(priceEach);
			DrawString priceAll = new DrawString(
					shortenPrice(String.valueOf(yourAuctions.get(i1).getPrice() * yourAuctions.get(i1).getAmount()))
							+ " (all)",
					false, new Rectangle(slotXOffset, 15, 0, 14));
			priceAll.setColor(Color.white.getRGB());
			priceAll.setFillHovering(Color.white.getRGB());
			slot.add(priceAll);
		}
	}

	@Override
	public void onRender() {
		super.onRender();
		view.update(isAuctionView);
		if (isAuctionView) {
			auctionListButton.setFill(0xFFFF00);
			auctionListButton.setFillHovering(0xFFFF00);
			yourAuctionsButton.setFill(0xFFEFED);
			yourAuctionsButton.setFillHovering(0xAAFF00);
		} else {
			yourAuctionsButton.setFill(0xFFFF00);
			yourAuctionsButton.setFillHovering(0xFFFF00);
			auctionListButton.setFill(0xFFEFED);
			auctionListButton.setFillHovering(0xAAFF00);
		}
	}

	public List<Auction> filterName(String name, List<Auction> auctions) {
		List<Auction> found = new ArrayList<Auction>();
		for (Auction auction : auctions) {
			if (!name.isEmpty()
					&& EntityHandler.getItemDef(auction.getID()).getName().toLowerCase().contains(name.toLowerCase()))
				found.add(auction);
			else if (type == 1 && EntityHandler.getItemDef(auction.getID()).isWieldable())
				found.add(auction);
			else if (type == 2 && (EntityHandler.getItemDef(auction.getID()).getCommand().equalsIgnoreCase("eat")
					|| EntityHandler.getItemDef(auction.getID()).getCommand().equalsIgnoreCase("drink")))
				found.add(auction);
			else if (type == 3 && EntityHandler.getItemDef(auction.getID()).getName().toLowerCase().contains("-rune"))
				found.add(auction);
			else if (type == 4 && (EntityHandler.getItemDef(auction.getID()).getName().toLowerCase().contains("arrow")
					|| EntityHandler.getItemDef(auction.getID()).getName().toLowerCase().contains("bolt")))
				found.add(auction);
			else if(type == 5 && EntityHandler.getItemDef(auction.getID()).isNote())
				found.add(auction);
			else if (type == 0 && name.isEmpty())
				found.add(auction);
		}
		return found;
	}

	public AuctionHouse(final mudclient<?> mc) {
		super(mc);
		setMenu(true);
		this.mc = mc;
		frame.setOpaque(130);
		frame.setFill(7);
		add(frame);
		/*
		 * hoverItemName = new DrawString("", true, new Rectangle(mc.mouseX,
		 * mc.mouseY - 16, 0, 0)); hoverItemName.setColor(Color.white.getRGB());
		 * hoverItemName.setFillHovering(Color.white.getRGB());
		 * hoverItemName.setVisible(false); add(hoverItemName);
		 */
		Box closeBox = new Box(new Rectangle(frame.getWidth() - 20, 1, 20, 15));
		closeBox.setFill(5);
		closeBox.setText("X", Color.white.getRGB());
		closeBox.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				setVisible(false);
			}
		});
		frame.add(closeBox);

		int xOffset = 5;
		int yOffset = closeBox.getHeight() + 5;
		auctionListButton = new Box(
				new Rectangle(xOffset, yOffset, mc.gameGraphics.textWidth("Auction List", 1) + 6, 16));
		auctionListButton.setFill(0xFFEFED);
		auctionListButton.setFillHovering(0xAAFF00);
		auctionListButton.setOpaque(50);
		auctionListButton.setText("Auction List", 0xFFFFFF);
		auctionListButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				isAuctionView = true;
				resetScrollIndex();
				populateBuyView();
				populateYourAuctions();
				selectedIndex = -1;
			}
		});
		frame.add(auctionListButton);
		xOffset += auctionListButton.getWidth() + 5;
		yourAuctionsButton = new Box(
				new Rectangle(xOffset, yOffset, mc.gameGraphics.textWidth("Your Auctions", 1) + 6, 16));
		yourAuctionsButton.setFill(0xFFEFED);
		yourAuctionsButton.setFillHovering(0xAAFF00);
		yourAuctionsButton.setOpaque(50);
		yourAuctionsButton.setText("Your Auctions", 0xFFFFFF);
		yourAuctionsButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				isAuctionView = false;
				resetScrollIndex();
				populateBuyView();
				populateYourAuctions();
				selectedIndex = -1;
			}
		});
		frame.add(yourAuctionsButton);
		xOffset += yourAuctionsButton.getWidth() * 2 - 26;
		view.sortBy = new Box(new Rectangle(xOffset, yOffset, mc.gameGraphics.textWidth("Sort by: Name", 1) + 26, 16));
		view.sortBy.setFill(0xFFEFED);
		view.sortBy.setFillHovering(0xFFFF00);
		view.sortBy.setOpaque(50);
		view.sortBy.setText("Sort by: Name", 0xFFFFFF);
		view.sortBy.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				// SORT ITEMS
				mc.displayMessage("TODO: Sort items", 3, 0);
			}
		});
		frame.add(view.sortBy);
		int refreshWidth = mc.gameGraphics.textWidth("Refresh", 1) + 26;
		xOffset = frame.getWidth() - refreshWidth - 5;
		Box refreshButton = new Box(new Rectangle(xOffset, yOffset, refreshWidth, 16));
		refreshButton.setFill(0xFFEFED);
		refreshButton.setFillHovering(0xFFFF00);
		refreshButton.setOpaque(50);
		refreshButton.setText("Refresh", 0xFFFFFF);
		refreshButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				mc.requestAuctionHouse();
				selectedIndex = -1;
			}
		});
		frame.add(refreshButton);
		xOffset = 10;
		yOffset += auctionListButton.getHeight() + 5;
		view.coins = new DrawString("Coins", false, new Rectangle(xOffset, yOffset, 0, 0));
		view.coins.setColor(0xFFFFFF);
		frame.add(view.coins);
		xOffset += mc.gameGraphics.textWidth("Coins", 1) - 5;
		yOffset -= 8;
		view.coinsSprite = new DrawSprite(2150 + EntityHandler.getItemDef(10).getSprite(),
				new Rectangle(xOffset, yOffset, 0, 0), 40, 23, 0x0);
		frame.add(view.coinsSprite);
		xOffset += 40;
		yOffset += 8;
		view.coinsAmount = new TextBox(new Rectangle(xOffset, yOffset, 90, 16));
		view.coinsAmount.setTextUnchangable("");
		view.coinsAmount.setOpaque(50);
		view.coinsAmount.setFill(0xFFEFED);
		view.coinsAmount.setFillHovering(view.coinsAmount.getFill());
		frame.add(view.coinsAmount);
		xOffset += 100;
		view.searchWord = new DrawString("Search word:", false, new Rectangle(xOffset, yOffset, 0, 0));
		view.searchWord.setColor(0xFFFFFF);
		frame.add(view.searchWord);
		xOffset += mc.gameGraphics.textWidth("Search word:", 1) + 5;

		view.searchBox = new TextBox(new Rectangle(xOffset, yOffset, 170, 16));
		view.searchBox.setOpaque(50);
		view.searchBox.setTextColor(0xFFFFFF);
		view.searchBox.setFill(0xFFEFED);
		view.searchBox.setFillHovering(view.searchBox.getFill());
		view.searchBox.addKeyListener(new KeyListener() {
			@Override
			public boolean onKey(char keyChar, int key) {
				if (!view.searchBox.isVisible() || !view.searchBox.isSelected() || key == 10 || mc.inputBoxType != 0) {
					return false;
				}
				String validCharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_ ";
				if(key == 27) { /* escape */
					view.searchBox.setText("");
					view.searchBox.setSelected(false);
					resetScrollIndex();
					populateBuyView();
					return true;
				}
				if (key == 8) {
					view.searchBox.append("DELETE");
					resetScrollIndex();
					populateBuyView();
					return true;
				}
				if (view.searchBox.getText().length() < 30)
					for (int k = 0; k < validCharSet.length(); k++)
						if ((char) key == validCharSet.charAt(k)) {
							view.searchBox.append(keyChar + "");
							resetScrollIndex();
							populateBuyView();
							return true;
						}
				return false;
			}
		});
		view.searchBox.setAction(new Action() {
			public void action(int x, int y, int mouseClick) {
				if (mouseClick == 1) {
					view.searchBox.setSelected(true);
					view.amountBox.setSelected(false);
				}
			}
		});
		frame.add(view.searchBox);
		xOffset = 1;
		yOffset += view.searchBox.getHeight() + 5;
		view.typeBox = new Box(new Rectangle(xOffset, yOffset, 90, 145));
		view.typeBox.setFill(0x003F00);
		view.typeBox.setFillHovering(view.typeBox.getFill());
		view.typeBox.setOpaque(130);
		view.typeBox.setCentered(false);
		frame.add(view.typeBox);
		xOffset += view.typeBox.getWidth() + 1;
		view.auctionList = new Box(new Rectangle(xOffset, yOffset, frame.getWidth() - 95, 145));
		view.auctionList.setFill(0x003F00);
		view.auctionList.setFillHovering(view.auctionList.getFill());
		view.auctionList.setOpaque(130);
		frame.add(view.auctionList);
		xOffset = 1;
		yOffset += view.auctionList.getHeight() + 2;
		view.selectedItemBox = new Box(new Rectangle(xOffset, yOffset, frame.getWidth() - 2, 63));
		view.selectedItemBox.setFill(7);
		view.selectedItemBox.setFillHovering(7);
		view.selectedItemBox.setOpaque(50);
		frame.add(view.selectedItemBox);
		view.selectedItemNameString = new DrawString("Selected item:", false, new Rectangle(0, 0, 0, 0));
		view.selectedItemNameString.setColor(0xFFFFFF);
		view.selectedItemBox.add(view.selectedItemNameString);
		xOffset = 330;
		yOffset = 0;
		DrawString enterAmount = new DrawString("Enter Amount", false, new Rectangle(xOffset, yOffset, 0, 0));
		enterAmount.setColor(0xFFFFFF);
		view.selectedItemBox.add(enterAmount);
		xOffset -= 7;
		yOffset += mc.gameGraphics.messageFontHeight(1) + 3;
		view.amountBox = new TextBox(new Rectangle(xOffset, yOffset, 90, 16));
		view.amountBox.setOpaque(50);
		view.amountBox.setTextColor(0xFFFFFF);
		view.amountBox.setFill(0xFFEFED);
		view.amountBox.setFillHovering(view.amountBox.getFill());
		view.amountBox.addKeyListener(new KeyListener() {
			@Override
			public boolean onKey(char keyChar, int key) {
				if (!view.selectedItemBox.isVisible() || !view.amountBox.isSelected() || mc.inputBoxType != 0 || key == 10) {
					return false;
				}
				String validCharSet = "0123456789";
				if(key == 27) { /* escape */
					view.amountBox.setText("");
					view.amountBox.setSelected(false);
					return true;
				}
				if (key == 8) {
					view.amountBox.append("DELETE");
					return true;
				}
				if (view.amountBox.getText().length() < 10)
					for (int k = 0; k < validCharSet.length(); k++)
						if ((char) key == validCharSet.charAt(k)) {
							view.amountBox.append(keyChar + "");
							return true;
						}
				return false;
			}
		});
		view.amountBox.setAction(new Action() {
			public void action(int x, int y, int mouseClick) {
				if (mouseClick == 1) {
					view.amountBox.setSelected(true);
					view.searchBox.setSelected(false);
				}
			}
		});
		view.selectedItemBox.add(view.amountBox);
		xOffset += 9;
		yOffset += 22;
		Box purchaseButton = new Box(new Rectangle(xOffset, yOffset, refreshWidth, 16));
		purchaseButton.setFill(0xFFEFED);
		purchaseButton.setFillHovering(0xFFFF00);
		purchaseButton.setOpaque(50);
		purchaseButton.setText("Purchase", 0xFFFFFF);
		purchaseButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				if(selectedIndex == -1 || view.amountBox.getText().replaceAll("\\*", "").isEmpty())
					return;
				mc.buyFromAuctionHouse(selectedIndex, selectedItemID, selectedItemPriceEach, Long.parseLong(view.amountBox.getText().replaceAll("\\*", "")));
				selectedIndex = -1;
			}
		});
		view.selectedItemBox.add(purchaseButton);
		yOffset = mc.gameGraphics.messageFontHeight(1);
		Box spriteBox = new Box(new Rectangle(0, yOffset + 5, 45, 30));
		spriteBox.setFillHovering(spriteBox.getFill());
		spriteBox.setCentered(false);
		view.selectedItemBox.add(spriteBox);
		view.selectedItemSprite = new DrawSprite(2150 + EntityHandler.getItemDef(0).getSprite(),
				new Rectangle(0, 0, 45, 30), 40, 30, EntityHandler.getItemDef(0).getPictureMask());
		spriteBox.add(view.selectedItemSprite);
		xOffset = 47;
		view.priceAll = new DrawString("Price for all:", false, new Rectangle(xOffset, yOffset, 0, 0));
		view.priceAll.setColor(0xFFFFFF);
		view.selectedItemBox.add(view.priceAll);
		yOffset += 13;
		view.priceEach = new DrawString("Price each:", false, new Rectangle(xOffset, yOffset, 0, 0));
		view.priceEach.setColor(0xFFFFFF);
		view.selectedItemBox.add(view.priceEach);
		yOffset += 13;
		view.quantity = new DrawString("Quantity:", false, new Rectangle(xOffset, yOffset, 0, 0));
		view.quantity.setColor(0xFFFFFF);
		view.selectedItemBox.add(view.quantity);
		xOffset = 0;
		yOffset = 0;
		DrawString typeString = new DrawString("Type", false, new Rectangle(xOffset, yOffset, 0, 0));
		typeString.setColor(0xFFFFFF);
		view.typeBox.add(typeString);
		yOffset += mc.gameGraphics.messageFontHeight(1);
		view.typeAllButton = new Box(new Rectangle(xOffset, yOffset, 88, 16));
		view.typeAllButton.setFill(0xAAFF00);
		view.typeAllButton.setFillHovering(0xAAFF00);
		view.typeAllButton.setOpaque(50);
		view.typeAllButton.setText("All", 0xFFFFFF);
		view.typeAllButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				type = 0;
				view.typeAllButton.setFill(0xAAFF00);
				view.typeWieldableButton.setFill(0xFFEFED);
				view.typeConsumableButton.setFill(0xFFEFED);
				view.typeRunesButton.setFill(0xFFEFED);
				view.typeArrowsButton.setFill(0xFFEFED);
				view.typeNotesButton.setFill(0xFFEFED);
				resetScrollIndex();
				populateBuyView();
			}
		});
		view.typeBox.add(view.typeAllButton);
		yOffset += 17;
		view.typeWieldableButton = new Box(new Rectangle(xOffset, yOffset, 88, 16));
		view.typeWieldableButton.setFill(0xFFEFED);
		view.typeWieldableButton.setFillHovering(0xAAFF00);
		view.typeWieldableButton.setOpaque(50);
		view.typeWieldableButton.setText("Wieldable", 0xFFFFFF);
		view.typeWieldableButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				type = 1;
				view.typeAllButton.setFill(0xFFEFED);
				view.typeWieldableButton.setFill(0xAAFF00);
				view.typeConsumableButton.setFill(0xFFEFED);
				view.typeRunesButton.setFill(0xFFEFED);
				view.typeArrowsButton.setFill(0xFFEFED);
				view.typeNotesButton.setFill(0xFFEFED);
				resetScrollIndex();
				populateBuyView();
			}
		});
		view.typeBox.add(view.typeWieldableButton);
		yOffset += 17;
		view.typeConsumableButton = new Box(new Rectangle(xOffset, yOffset, 88, 16));
		view.typeConsumableButton.setFill(0xFFEFED);
		view.typeConsumableButton.setFillHovering(0xAAFF00);
		view.typeConsumableButton.setOpaque(50);
		view.typeConsumableButton.setText("Consumable", 0xFFFFFF);
		view.typeConsumableButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				type = 2;
				view.typeAllButton.setFill(0xFFEFED);
				view.typeWieldableButton.setFill(0xFFEFED);
				view.typeConsumableButton.setFill(0xAAFF00);
				view.typeRunesButton.setFill(0xFFEFED);
				view.typeArrowsButton.setFill(0xFFEFED);
				view.typeNotesButton.setFill(0xFFEFED);
				resetScrollIndex();
				populateBuyView();
			}
		});
		view.typeBox.add(view.typeConsumableButton);
		yOffset += 17;
		view.typeRunesButton = new Box(new Rectangle(xOffset, yOffset, 88, 16));
		view.typeRunesButton.setFill(0xFFEFED);
		view.typeRunesButton.setFillHovering(0xAAFF00);
		view.typeRunesButton.setOpaque(50);
		view.typeRunesButton.setText("Runes", 0xFFFFFF);
		view.typeRunesButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				type = 3;
				view.typeAllButton.setFill(0xFFEFED);
				view.typeWieldableButton.setFill(0xFFEFED);
				view.typeConsumableButton.setFill(0xFFEFED);
				view.typeRunesButton.setFill(0xAAFF00);
				view.typeArrowsButton.setFill(0xFFEFED);
				view.typeNotesButton.setFill(0xFFEFED);
				resetScrollIndex();
				populateBuyView();
			}
		});
		view.typeBox.add(view.typeRunesButton);
		yOffset += 17;
		view.typeArrowsButton = new Box(new Rectangle(xOffset, yOffset, 88, 16));
		view.typeArrowsButton.setFill(0xFFEFED);
		view.typeArrowsButton.setFillHovering(0xAAFF00);
		view.typeArrowsButton.setOpaque(50);
		view.typeArrowsButton.setText("Arrows", 0xFFFFFF);
		view.typeArrowsButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				type = 4;
				view.typeAllButton.setFill(0xFFEFED);
				view.typeWieldableButton.setFill(0xFFEFED);
				view.typeConsumableButton.setFill(0xFFEFED);
				view.typeRunesButton.setFill(0xFFEFED);
				view.typeArrowsButton.setFill(0xAAFF00);
				view.typeNotesButton.setFill(0xFFEFED);
				resetScrollIndex();
				populateBuyView();
			}
		});
		view.typeBox.add(view.typeArrowsButton);
		yOffset += 17;
		view.typeNotesButton = new Box(new Rectangle(xOffset, yOffset, 88, 16));
		view.typeNotesButton.setFill(0xFFEFED);
		view.typeNotesButton.setFillHovering(0xAAFF00);
		view.typeNotesButton.setOpaque(50);
		view.typeNotesButton.setText("Notes", 0xFFFFFF);
		view.typeNotesButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				type = 5;
				view.typeAllButton.setFill(0xFFEFED);
				view.typeWieldableButton.setFill(0xFFEFED);
				view.typeConsumableButton.setFill(0xFFEFED);
				view.typeRunesButton.setFill(0xFFEFED);
				view.typeArrowsButton.setFill(0xFFEFED);
				view.typeNotesButton.setFill(0xAAFF00);
				resetScrollIndex();
				populateBuyView();
			}
		});
		view.typeBox.add(view.typeNotesButton);
		xOffset = 5;
		yOffset = 5;
		DrawString item = new DrawString("Item", false, new Rectangle(xOffset, yOffset, 0, 0));
		item.setColor(0xFFFFFF);
		view.auctionList.add(item);
		xOffset += mc.gameGraphics.textWidth("Item", 1) + 30;
		view.auctionSizeDisplay = new DrawString("Showing: " + auctionSize + " items", false,
				new Rectangle(xOffset, yOffset, 0, 0));
		view.auctionSizeDisplay.setColor(0xFFFFFF);
		view.auctionList.add(view.auctionSizeDisplay);
		xOffset += mc.gameGraphics.textWidth("Showing: 0000 items", 1) + 70;
		DrawString salePrice = new DrawString("Sale Price", false, new Rectangle(xOffset, yOffset, 0, 0));
		salePrice.setColor(0xFFFFFF);
		view.auctionList.add(salePrice);
		int slotXOffset = 0;
		int slotYOffset = 25;
		for (int i = 0; i < 4; i++) {
			slots.add(new Box(new Rectangle(slotXOffset, slotYOffset, frame.getWidth() - 95 - 10, 30)));
			slots.get(i).setFill(0xFFEFED);
			slots.get(i).setOpaque(50);
			view.auctionList.add(slots.get(i));
			slotYOffset += slots.get(i).getHeight();
		}

		xOffset = 0;
		yOffset = closeBox.getHeight() + 23;
		view.createAuctionBox = new Box(
				new Rectangle(xOffset, yOffset, frame.getWidth() / 2 - 35, frame.getHeight() - yOffset + 16));
		view.createAuctionBox.setFill(0x003F00);
		view.createAuctionBox.setFillHovering(0x003F00);
		frame.add(view.createAuctionBox);
		xOffset += view.createAuctionBox.getWidth();
		view.yourAuctionsBox = new Box(
				new Rectangle(xOffset, yOffset, frame.getWidth() / 2 + 35, frame.getHeight() - yOffset + 16));
		view.yourAuctionsBox.setFill(0x003F00);
		view.yourAuctionsBox.setFillHovering(0x003F00);
		frame.add(view.yourAuctionsBox);
		xOffset = 5;
		yOffset = 5;
		DrawString createAuctionString = new DrawString("Create auction", false, new Rectangle(xOffset, yOffset, 0, 0));
		createAuctionString.setSize(5);
		createAuctionString.setColor(0xFFFFFF);
		view.createAuctionBox.add(createAuctionString);
		yOffset += mc.gameGraphics.messageFontHeight(5) + 5;
		int index = 0;
		for (int i = 0; i < 6; i++) {
			xOffset = 5;
			for (int j = 0; j < 5; j++) {
				view.inventoryItemBoxes[index] = new Box(new Rectangle(xOffset, yOffset, 35, 25));
				view.inventoryItemBoxes[index].setCentered(false);
				view.createAuctionBox.add(view.inventoryItemBoxes[index]);
				view.noteSprites[index] = new DrawSprite(2604, new Rectangle(0, -3, 45, 30), 35, 25, 0);
				view.inventoryItemBoxes[index].add(view.noteSprites[index]);
				view.inventoryItemSprites[index] = new DrawSprite(0, new Rectangle(0, 0, 35, 25), 35, 25, 0);
				view.inventoryItemBoxes[index].add(view.inventoryItemSprites[index]);
				view.inventoryItemAmounts[index] = new DrawString("", false, new Rectangle(0, 0, 0, 0));
				view.inventoryItemAmounts[index].setVisible(false);
				view.inventoryItemAmounts[index].setColor(0xFFFF00);
				view.inventoryItemBoxes[index].add(view.inventoryItemAmounts[index++]);
				xOffset += 35;
			}
			yOffset += 25;
		}
		xOffset = 7;
		yOffset += 3;
		view.selectedInventoryItemSpriteBox = new Box(new Rectangle(xOffset, yOffset, 45, 30));
		view.selectedInventoryItemSpriteBox.setFillHovering(view.selectedInventoryItemSpriteBox.getFill());
		view.selectedInventoryItemSpriteBox.setCentered(false);
		view.createAuctionBox.add(view.selectedInventoryItemSpriteBox);
		view.selectedInventoryItemSprite = new DrawSprite(0, new Rectangle(0, 0, 35, 25), 35, 25, 0);
		view.selectedInventoryItemSpriteBox.add(view.selectedInventoryItemSprite);
		view.selectedInventoryItemAmount = new DrawString("", false, new Rectangle(0, 0, 0, 0));
		view.selectedInventoryItemAmount.setColor(0xFFFFFF);
		view.selectedInventoryItemSpriteBox.add(view.selectedInventoryItemAmount);
		xOffset += 50;
		view.totalPrice = new DrawString("Total price: ", false, new Rectangle(xOffset, yOffset, 0, 0));
		view.totalPrice.setColor(0xFFFFFF);
		view.createAuctionBox.add(view.totalPrice);
		yOffset += mc.gameGraphics.messageFontHeight(1);
		view.salePrice = new DrawString("Price each: ", false, new Rectangle(xOffset, yOffset, 0, 0));
		view.salePrice.setColor(0xFFFFFF);
		view.createAuctionBox.add(view.salePrice);
		view.salePriceInput = new TextBox(
				new Rectangle(xOffset + mc.gameGraphics.textWidth("Price each: ", 1), yOffset, 50, 16));
		view.salePriceInput.setOpaque(50);
		view.salePriceInput.setTextColor(0xFFFFFF);
		view.salePriceInput.setFill(0xFFEFED);
		view.salePriceInput.setFillHovering(view.salePriceInput.getFill());
		view.salePriceInput.addKeyListener(new KeyListener() {
			@Override
			public boolean onKey(char keyChar, int key) {
				if (getSelectedInventoryItemIndex() == -1 || !view.salePriceInput.isSelected() || mc.inputBoxType != 0 || key == 10) {
					return false;
				}
				String validCharSet = "0123456789";
				if(key == 27) { /* escape */
					view.salePriceInput.setSelected(false);
					return true;
				}
				if (key == 8) {
					view.salePriceInput.append("DELETE");
					return true;
				}
				if (view.salePriceInput.getText().length() < 10)
					for (int k = 0; k < validCharSet.length(); k++)
						if ((char) key == validCharSet.charAt(k)) {
							view.salePriceInput.append(keyChar + "");
							if (!view.salePriceInput.getText().isEmpty() && !view.saleAmountInput.getText().isEmpty())
								view.totalPrice.setText("Total price: " + Long
										.parseLong(view.salePriceInput.getText().replaceAll("\\*", ""))
										* Long.parseLong(view.saleAmountInput.getText().replaceAll("\\*", "")));
							return true;
						}
				return false;
			}
		});
		view.salePriceInput.setAction(new Action() {
			public void action(int x, int y, int mouseClick) {
				if (mouseClick == 1) {
					view.salePriceInput.setSelected(true);
					view.saleAmountInput.setSelected(false);
				}
			}
		});
		view.createAuctionBox.add(view.salePriceInput);
		yOffset += mc.gameGraphics.messageFontHeight(1);
		view.saleAmount = new DrawString("Amount: ", false, new Rectangle(xOffset, yOffset, 0, 0));
		view.saleAmount.setColor(0xFFFFFF);
		view.createAuctionBox.add(view.saleAmount);
		view.saleAmountInput = new TextBox(
				new Rectangle(xOffset + mc.gameGraphics.textWidth("Price each: ", 1), yOffset + 2, 50, 16));
		view.saleAmountInput.setOpaque(50);
		view.saleAmountInput.setTextColor(0xFFFFFF);
		view.saleAmountInput.setFill(0xFFEFED);
		view.saleAmountInput.setFillHovering(view.saleAmountInput.getFill());
		view.saleAmountInput.addKeyListener(new KeyListener() {
			@Override
			public boolean onKey(char keyChar, int key) {
				if (getSelectedInventoryItemIndex() == -1 || !view.saleAmountInput.isSelected() || mc.inputBoxType != 0 || key == 10) {
					return false;
				}
				String validCharSet = "0123456789";
				if(key == 27) { /* escape */
					view.saleAmountInput.setSelected(false);
					return true;
				}
				if (key == 8) {
					view.saleAmountInput.append("DELETE");
					return true;
				}
				if (view.saleAmountInput.getText().length() < 10)
					for (int k = 0; k < validCharSet.length(); k++)
						if ((char) key == validCharSet.charAt(k)) {
							view.saleAmountInput.append(keyChar + "");
							if (!view.salePriceInput.getText().isEmpty() && !view.saleAmountInput.getText().isEmpty())
								view.totalPrice.setText("Total price: " + Long.parseLong(view.salePriceInput.getText().replaceAll("\\*", ""))
										* Long.parseLong(view.saleAmountInput.getText().replaceAll("\\*", "")));
							return true;
						}
				return false;
			}
		});
		view.saleAmountInput.setAction(new Action() {
			public void action(int x, int y, int mouseClick) {
				if (mouseClick == 1) {
					view.saleAmountInput.setSelected(true);
					view.salePriceInput.setSelected(false);
				}
			}
		});
		view.createAuctionBox.add(view.saleAmountInput);
		yOffset += 17;
		view.createAuctionButton = new Box(
				new Rectangle(7, yOffset, mc.gameGraphics.textWidth("Create Auction", 1) + 5, 16));
		view.createAuctionButton.setFill(0xFFEFED);
		view.createAuctionButton.setFillHovering(0xAAFF00);
		view.createAuctionButton.setOpaque(50);
		view.createAuctionButton.setText("Create Auction", 0xFFFFFF);
		view.createAuctionButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				if (getSelectedInventoryItemIndex() == -1 || view.saleAmountInput.getText().replaceAll("\\*", "").isEmpty() || view.salePriceInput.getText().replaceAll("\\*", "").isEmpty()) {
					return;
				}
				mc.createAuction(mc.inventoryItems[getSelectedInventoryItemIndex()],
						Long.parseLong(view.saleAmountInput.getText().replaceAll("\\*", "")),
						Long.parseLong(view.salePriceInput.getText().replaceAll("\\*", "")));
				setSelectedInventoryItemIndex(-1);
			}
		});
		view.createAuctionBox.add(view.createAuctionButton);
		xOffset = 5;
		yOffset = 5;
		DrawString yourAuctionsString = new DrawString("Your Auctions", false, new Rectangle(xOffset, yOffset, 0, 0));
		yourAuctionsString.setSize(5);
		yourAuctionsString.setColor(0xFFFFFF);
		view.yourAuctionsBox.add(yourAuctionsString);
		yOffset += mc.gameGraphics.messageFontHeight(5) + 5;
		DrawString itemString = new DrawString("Item", false, new Rectangle(xOffset, yOffset, 0, 0));
		itemString.setColor(0xFFFFFF);
		view.yourAuctionsBox.add(itemString);
		xOffset += 140;
		DrawString salePriceString = new DrawString("Sale Price", false, new Rectangle(xOffset, yOffset, 0, 0));
		salePriceString.setColor(0xFFFFFF);
		view.yourAuctionsBox.add(salePriceString);
		yOffset += mc.gameGraphics.messageFontHeight(1);
		scroll2 = new ScrollBar(new Rectangle(view.yourAuctionsBox.getWidth() - 14, yOffset, 14, 150));
		scroll2.setSize(30);
		view.yourAuctionsBox.add(scroll2);
		scroll2.addScrollListener(new ScrollListener() {
			@Override
			public void onScrollUpdate(int index) {
				if (!visible)
					return;
				List<Auction> yourAuctions = new ArrayList<Auction>();
				for (Auction auction : auctions)
					if (auction.isOwner())
						yourAuctions.add(auction);
				if (index >= 0 && index <= yourAuctions.size() - 5)
					populateYourAuctions();
				else if (index > yourAuctions.size() - 5)
					scroll2.setFirstIndex(yourAuctions.size() - 5);
				else
					scroll2.setFirstIndex(0);
			}

			@Override
			public void scrolling(int type) {
			}
		});
		scroll = new ScrollBar(new Rectangle(view.auctionList.getWidth() - 14, 22, 14, 120));
		scroll.setSize(30);
		view.auctionList.add(scroll);
		scroll.addScrollListener(new ScrollListener() {
			@Override
			public void onScrollUpdate(int index) {
				if (!visible)
					return;
				List<Auction> a = filterName(view.searchBox.getText().replaceAll("\\*", ""), auctions);
				if (index >= 0 && index <= a.size() - 4)
					populateBuyView();
				else if (index > a.size() - 4)
					scroll.setFirstIndex(a.size() - 4);
				else
					scroll.setFirstIndex(0);
			}

			@Override
			public void scrolling(int type) {
			}
		});

		xOffset = 0;
		for (int i = 0; i < 5; i++) {
			yourAuctionSlots.add(new Box(new Rectangle(xOffset, yOffset, view.yourAuctionsBox.getWidth() - 10, 30)));
			yourAuctionSlots.get(i).setFill(0xFFEFED);
			yourAuctionSlots.get(i).setOpaque(50);
			view.yourAuctionsBox.add(yourAuctionSlots.get(i));
			yOffset += yourAuctionSlots.get(i).getHeight();
		}
		xOffset += 75;
		yOffset += 30;
		view.cancelAuctionButton = new Box(
				new Rectangle(xOffset, yOffset, mc.gameGraphics.textWidth("Cancel Auction", 1) + 5, 16));
		view.cancelAuctionButton.setFill(0xFFEFED);
		view.cancelAuctionButton.setFillHovering(0xAAFF00);
		view.cancelAuctionButton.setOpaque(50);
		view.cancelAuctionButton.setText("Cancel Auction", 0xFFFFFF);
		view.cancelAuctionButton.setAction(new Action() {
			@Override
			public void action(int x, int y, int button) {
				if (getSelectedCancelIndex() == -1) {
					//System.out.println("derp");
					return;
				}
				mc.cancelAuction(getSelectedCancelIndex());
				setSelectedCancelIndex(-1);
			}
		});
		view.yourAuctionsBox.add(view.cancelAuctionButton);
	}

	public int getAuctionSize() {
		return auctionSize;
	}

	public void setAuctionSize(int auctionSize) {
		this.auctionSize = auctionSize;
	}

	public List<Auction> getAuctions() {
		return auctions;
	}

	public void setAuctions(List<Auction> auctions) {
		this.auctions = auctions;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible)
			mc.closeAuctionHouse();
		else
			type = 0;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public int getSelectedCancelIndex() {
		return selectedCancelIndex;
	}

	public void setSelectedCancelIndex(int selectedCancelIndex) {
		this.selectedCancelIndex = selectedCancelIndex;
	}

	public String getSelectedItemName() {
		return selectedItemName;
	}

	public void setSelectedItemName(String selectedItemName) {
		this.selectedItemName = selectedItemName;
	}

	public int getSelectedItemID() {
		return selectedItemID;
	}

	public void setSelectedItemID(int selectedItemID) {
		this.selectedItemID = selectedItemID;
	}

	public long getSelectedItemQuantity() {
		return selectedItemQuantity;
	}

	public void setSelectedItemQuantity(long selectedItemQuantity) {
		view.amountBox.setTextUnchangable(String.valueOf(selectedItemQuantity));
		this.selectedItemQuantity = selectedItemQuantity;
	}

	public long getSelectedItemPriceEach() {
		return selectedItemPriceEach;
	}

	public void setSelectedItemPriceEach(long selectedItemPriceEach) {
		this.selectedItemPriceEach = selectedItemPriceEach;
	}

	private void setSelectedInventoryItemIndex(int newIndex) {
		this.selectedInventoryItemIndex = newIndex;
	}

	private int getSelectedInventoryItemIndex() {
		return this.selectedInventoryItemIndex;
	}
}
