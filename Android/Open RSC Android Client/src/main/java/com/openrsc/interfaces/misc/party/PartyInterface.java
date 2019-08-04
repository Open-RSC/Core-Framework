package com.openrsc.interfaces.misc.party;

import orsc.Config;
import orsc.enumerations.InputXAction;
import orsc.enumerations.MenuItemAction;
import orsc.graphics.gui.InputXPrompt;
import orsc.graphics.gui.Menu;
import orsc.graphics.gui.Panel;
import orsc.graphics.gui.SocialLists;
import orsc.graphics.two.GraphicsController;
import orsc.mudclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public final class PartyInterface {
	public int partyGUIScroll;
	public int partySearchScroll;
	public int partyActivePanel = 1;
	public int partyName_field;
	public int partyTag_field;
	public int partySearch_field;
	private int selectedPartyMate = -1;
	private int selectedPartyInSearch = -1;
	private ArrayList<PartyResult> readPartys;
	public Panel partySetupPanel;
	private Comparator<PartyResult> partyComperator = (o1, o2) -> {
		if (o1.getPartyPoints() == o2.getPartyPoints()) {
			return o1.getPartyName().compareTo(o2.getPartyName());
		}
		return o1.getPartyPoints() > o2.getPartyPoints() ? -1 : 1;
	};
	private mudclient mc;
	private int x, y;
	private int width, height;
	private int menuX = 0;
	private int menuY = 0;
	private int partyActiveInterface = 1;
	private boolean visible;
	private boolean menu_visible = false;
	private String invitationBy = null;
	private String[] partyMateTable = {"Username", "Rank", "HP", "Deaths", "Ratio"};
	private Menu rightClickMenu;

	PartyInterface(mudclient mc) {
		this.mc = mc;

		width = 408;
		height = 246 - 25;

		x = (mc.getGameWidth() / 2) - width;
		y = (mc.getGameHeight() / 2) - height;

		readPartys = new ArrayList<>();

		partySetupPanel = new Panel(mc.getSurface(), 15);
		rightClickMenu = new Menu(mc.getSurface(), 1, "@ora@Choose Option");

		partyGUIScroll = partySetupPanel.addScrollingList2(x + 97, y + 49 + 34, 307, 146, 500, 1, true);
		partySearchScroll = partySetupPanel.addScrollingList2(x + 97, y + 49 + 34, 307, 146, 1000, 1, true);

		partyName_field = partySetupPanel.addCenteredTextEntry(x + 201, y + 102, 174, 16, 13, 3, false, true);
		partyTag_field = partySetupPanel.addCenteredTextEntry(x + 201, y + 162, 174, 5, 13, 3, false, true);
		partySearch_field = partySetupPanel.addLeftTextEntry(x + 95, y + 61, 174, 13, 3, 16, false, false);
	}

	public void reposition() {
		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		partySetupPanel.reposition(partyGUIScroll, x + 97, y + 49 + 34, 307, (selectedPartyMate != -1 && mc.party.isAllowed(0) ? 83 : 146));
		partySetupPanel.reposition(partySearchScroll, x + 100, y + 95, 307, 141);

		partySetupPanel.reposition(partyName_field, x + 201, y + 102, 174, 18);
		partySetupPanel.reposition(partyTag_field, x + 201, y + 174, 174, 18);
		partySetupPanel.reposition(partySearch_field, x + 95, y + 61, 174, 18);
	}

	public boolean onRender(GraphicsController graphics) {
		reposition();
		switch (partyActiveInterface) {
			case 1: // PARTY MAIN GUI
				drawPartyMain(graphics);
				break;
			case 2: // PARTY INVITEED SCREEN
				drawInvite(graphics);
				break;
		}
		return true;
	}

	private void drawPartyMates(GraphicsController graphics) {
		partySetupPanel.clearList(partyGUIScroll);
		partySetupPanel.show(partyGUIScroll);
		int leftBoxW = 402;
		int leftBoxH = 113;

		int boxWidth = 397;
		int boxHeight = 22;

		int newX = x + 4;
		int newY = y + 82;

		int tableBox = x + 5;
		int tableBoxW = 65;

		if (selectedPartyMate != -1 && mc.party.isAllowed(0)) {
			graphics.drawBoxAlpha(x + 3, y + 48 + 12, leftBoxW, leftBoxH - 4, 0x1D1915, 192); //5F5147
			graphics.drawBoxBorder(x + 3, leftBoxW, y + 48 + 11, leftBoxH - 4, 0x5F5147);
		} else {
			graphics.drawBoxAlpha(x + 3, y + 48 + 12, leftBoxW, leftBoxH + 59, 0x1D1915, 192); //5F5147
			graphics.drawBoxBorder(x + 3, leftBoxW, y + 48 + 11, leftBoxH + 59, 0x5F5147);
		}

		for (int i = 0; i < 5; i++) {
			if (i == 0) {
				graphics.drawBoxAlpha(tableBox, y + 48 + 11 + 2, tableBoxW + 69, 20, 0x432C26, 192); //5F5147
				graphics.drawBoxBorder(tableBox, tableBoxW + 69, y + 48 + 11 + 2, 20, 0x4C4445);
				graphics.drawString(partyMateTable[i], tableBox + 4, y + 75, 0xf1f1f1, 1);
			} else {
				graphics.drawBoxAlpha(tableBox + 135, y + 48 + 11 + 2, tableBoxW, 20, 0x432C26, 192); //5F5147
				graphics.drawBoxBorder(tableBox + 135, tableBoxW, y + 48 + 11 + 2, 20, 0x4C4445);
				graphics.drawString(partyMateTable[i], tableBox + 4 + 135, y + 75, 0xf1f1f1, 1);
				tableBox += 66;
			}
		}

		int color = 0x232220;
		int listStartPoint = partySetupPanel.getScrollPosition(partyGUIScroll);
		int listEndPoint = listStartPoint + (selectedPartyMate != -1 && mc.party.isAllowed(0) ? 3 : 6);
		int showing = 0;
		for (int i = -1; i < SocialLists.partyListCount; i++) {
			showing = i + 1;
			if (i >= 500) {
				break;
			}

			partySetupPanel.setListEntry(partyGUIScroll, i + 1, "", 0, null, null);

			if (i < listStartPoint || i > listEndPoint)
				continue;
			if (mc.party.isAllowed(0)) {
				if (mc.getMouseX() >= (newX) && mc.getMouseY() >= (newY) && mc.getMouseX() <= newX + 384
					&& mc.getMouseY() <= (newY - 5) + boxHeight && mc.inputX_Action == InputXAction.ACT_0) {
					graphics.drawBoxAlpha(newX + 1, newY, boxWidth + 1, boxHeight - 1, 0x202F39, 192);
					if (mc.getMouseClick() == 1) {
						selectedPartyMate = i;
						mc.setMouseClick(0);
					}
				} else {
					if (selectedPartyMate == i) {
						graphics.drawBoxAlpha(newX + 1, newY, boxWidth + 1, boxHeight - 1, 0x202F39, 192);

					} else {
						graphics.drawBoxAlpha(newX + 1, newY, boxWidth + 1, boxHeight - 1, color, 192);
					}
				}
			}

			if (i % 2 == 0) {
				color = 0x1C1B19;
			} else {
				color = 0x232220;
			}
			graphics.drawBoxBorder(newX, 400, newY, boxHeight, 0x343434);
			graphics.drawColoredString(newX + 3, newY + 16, mc.party.username[i], 2, 0xffffff, 0);
			graphics.drawColoredString(newX + 3 + 137, newY + 16, "" + mc.party.getPartyRankNames(mc.party.partyRank[i]), 2, 0xffffff, 0);
			graphics.drawColoredString(newX + 3 + 135 + 67, newY + 16, "" + mc.party.curHp[i] + "/" + mc.party.maxHp[i], 2, 0xffffff, 0);
			graphics.drawColoredString(newX + 3 + 135 + 67 + 67, newY + 16, "" + mc.party.getPlayerDeaths(i), 2, 0xffffff, 0);
			graphics.drawColoredString(newX + 3 + 135 + 67 + 67 + 67, newY + 16, "" + mc.party.getKDR(i), 2, 0xffffff, 0);
			newY += boxHeight - 1;
		}


		graphics.drawString("Total partymates: (" + showing + " / 5)", newX, y + 55, 0xEFB063, 0);
		if (selectedPartyMate != -1 && mc.party.isAllowed(0)) {
			graphics.drawString("Settings for: " + mc.party.username[selectedPartyMate], newX, y + 180, 0xB39684, 0);
			drawSubmitButton(graphics, newX + 250, y + 194, 130, 28, 18, 1, "Kick user", false, new ButtonHandler() {
				@Override
				void handle() {
					String[] kickMessage = new String[]{"Are you sure you want to kick " + mc.party.username[selectedPartyMate] + " from party?"};
					mc.partyKickPlayer = mc.party.username[selectedPartyMate];
					mc.showItemModX(kickMessage, InputXAction.KICK_PARTY_PLAYER, false);
				}
			});
			if (mc.party.isPartyLeader()) {
				drawSelectButton(graphics, x + 25, y + 195, 130, 26, 14, 8, 1, 0, "Rank user", "(right-click)", new ButtonHandler() {
					@Override
					void handle() {
						menuY = mc.mouseY - 7;
						menuX = mc.mouseX - x / 2;
						menu_visible = true;
						rightClickMenu.recalculateSize(0);
						rightClickMenu.addCharacterItem_WithID(0,
							"", MenuItemAction.PARTY_PROMOTE,
							"None", 0);
						rightClickMenu.addCharacterItem_WithID(2,
							"", MenuItemAction.PARTY_PROMOTE,
							"General", 2);
						rightClickMenu.addCharacterItem_WithID(1,
							"", MenuItemAction.PARTY_PROMOTE,
							"Delegate leadership", 1);
					}
				});
			}
		}
		partySetupPanel.drawPanel();
	}

	private void drawInvite(GraphicsController graphics) {
		partySetupPanel.hide(partyGUIScroll);
		partySetupPanel.hide(partySearchScroll);
		int leftBoxW = width - 88;
		int leftBoxH = 138;
		graphics.drawBox(x + 88 / 2, y - 10, leftBoxW, 20, 0x957357);
		graphics.drawBoxAlpha(x + 88 / 2, y + 10, leftBoxW, leftBoxH, 0x1D1915, 192); //5F5147
		graphics.drawBoxBorder(x + 88 / 2, leftBoxW, y - 10, leftBoxH + 20, 0x5F5147);
		graphics.drawColoredStringCentered(width / 2 + x, "Party Invitation!", 0xE5D8C0, 0, 1, y + 5);
		graphics.drawColoredStringCentered(width / 2 + x, invitationBy, 0xf1f1f1, 0, 1, y + 28);
		//graphics.drawColoredStringCentered(width / 2 + x, "" + SocialLists.partyListCount, 0xffffff, 0, 5, y + 58);
		graphics.drawColoredStringCentered(width / 2 + x, "Would you like to join this party?", 0xf1f1f1, 0, 1, y + 90);

		drawSubmitButton(graphics, x + 55, y + 110, 142, 28, 18, 1, "Accept", false, new ButtonHandler() {
			@Override
			void handle() {
				sendPartyAccept();
				setVisible(false);
			}
		});

		drawSubmitButton(graphics, x + 211, y + 110, 142, 28, 18, 1, "Decline", false, new ButtonHandler() {
			@Override
			void handle() {
				sendPartyDecline();
				setVisible(false);
			}
		});
	}

	private void drawPartyMain(GraphicsController graphics) {
		partySetupPanel.hide(partyGUIScroll);
		partySetupPanel.hide(partySearchScroll);
		partySetupPanel.hide(partyName_field);
		partySetupPanel.hide(partyTag_field);
		partySetupPanel.hide(partySearch_field);
		int boxColor = 0x1D1711;
		int headerColor = 0x957357;
		// HEADER
		graphics.drawBox(x, y, width, 15, headerColor);
		graphics.drawBoxAlpha(x, y + 15, width, height, boxColor, 192);
		graphics.drawBoxBorder(x - 1, width + 2, y - 1, height + 42, 0x5F5147);
		graphics.drawColoredStringCentered(width / 2 + x, "Party Settings", 0xE5D8C0, 0, 1, y + 12);
		graphics.drawLineHoriz(x, y + 43, width, 0x6E5D4E);
		// CONTENT
		if (!mc.party.inParty()) {
			drawButton(graphics, x + 3, y + 18, 125, 22, "Party Search", this.partyActivePanel == 3, new ButtonHandler() {
				@Override
				void handle() {
					partyActivePanel = 3;
					resetAll();
					sendPartySearch();
				}
			});
		} else {
			if (mc.party.inParty()) {
				drawButton(graphics, x + 3, y + 18, 125, 22, "Partymates", partyActivePanel == 2, new ButtonHandler() {
					@Override
					void handle() {
						partyActivePanel = 2;
						resetAll();
					}
				});
				drawButton(graphics, x + 3 + (mc.party.isPartyLeader() ? 256 : 128), y + 18, (mc.party.isPartyLeader() ? 146 : 146), 22, "Party Stats / Challenges", false, new ButtonHandler() {
					@Override
					void handle() {
						setVisible(false);
					}
				});
				if (mc.party.isAllowed(1) && !mc.party.isPartyLeader()) {
					drawSubmitButton(graphics, x + 280, y + 18, 125, 22, 15, 1, "Invite Player", false, new ButtonHandler() {
						@Override
						void handle() {
							mc.showItemModX(InputXPrompt.partyInvite, InputXAction.INVITE_PARTY_PLAYER, true);
							mc.showUiTab = 0;
						}
					});
				}
			}
		}
		if (!mc.party.inParty() || (mc.party.isPartyLeader() && mc.party.inParty())) {
			drawButton(graphics, x + 131, y + 18, 125, 22, "Party Setup", partyActivePanel == 1, new ButtonHandler() {
				@Override
				void handle() {
					partyActivePanel = 1;
				}
			});
		}

		switch (partyActivePanel) {
			case 1:
				drawPartySetup(graphics);
				break;
			case 2:
				if (mc.party.inParty()) {
					drawPartyMates(graphics);
				} else {
					partyActivePanel = 1;
				}
				break;
			case 3:
				if (!mc.party.inParty()) {
					drawPartySearch(graphics);
				}
				break;
		}

		// FOOTER
		drawCloseButton(graphics, x, y + 236, 408, 25, (Config.isAndroid() ? "Tap here to close" : "Click left mouse button to close"), false, new ButtonHandler() {
			@Override
			void handle() {
				setVisible(false);
			}
		});
		if (menu_visible) {
			drawRightClickMenu();
		}
	}

	private void drawPartySearch(GraphicsController graphics) {
		partySetupPanel.clearList(partySearchScroll);
		partySetupPanel.show(partySearchScroll);
		partySetupPanel.show(partySearch_field);

		Collections.sort(readPartys, partyComperator);
		String searchTerm = partySetupPanel.getControlText(partySearch_field);
		LinkedList<PartyResult> filteredList = new LinkedList<PartyResult>();
		for (PartyResult c : readPartys) {
			String party = c.getPartyName().toLowerCase();

			if (party.contains(searchTerm.toLowerCase())) {
				filteredList.add(c);
			}
		}

		int width = 200;
		int height = 28;

		int boxWidth = 387;
		int boxHeight = 32;

		int newX = x + 4;
		int newY = y + 96;
		int rightX = newX + 355;

		if (this.selectedPartyInSearch != -1) {
			partySetupPanel.setFocus(-1);
			partySetupPanel.hide(partySearch_field);
			final PartyResult vc = filteredList.get(selectedPartyInSearch);
			int horizColor = 0x4C4638;
			int horizWidth = 400;
			graphics.drawShadowText(vc.getPartyName() + " < " + vc.getPartyTag() + " >", x + 7, newY - 32, 0xFBFBF9, 5, false);
			graphics.drawLineHoriz(newX, newY - 25, horizWidth, horizColor);
			newY += 21;
			graphics.drawShadowText("Global Rank:", newX + 2, newY - 32, 0xB5DC4F, 1, false);
			graphics.drawColoredStringCentered(rightX, "#" + vc.getPartyGlobalRank(), 0xf1f1f1, 0, 1, newY - 32);
			graphics.drawLineHoriz(newX, newY - 27, horizWidth, horizColor);
			newY += 21;
			graphics.drawShadowText("Total Points:", newX + 2, newY - 32, 0xB5DC4F, 1, false);
			graphics.drawColoredStringCentered(rightX, "" + vc.getPartyPoints(), 0xf1f1f1, 0, 1, newY - 32);
			graphics.drawLineHoriz(newX, newY - 27, horizWidth, horizColor);
			newY += 21;
			graphics.drawShadowText("Total Members:", newX + 2, newY - 32, 0xB5DC4F, 1, false);
			graphics.drawColoredStringCentered(rightX, vc.getPartyMembersTotal() + "/15", 0xf1f1f1, 0, 1, newY - 32);
			graphics.drawLineHoriz(newX, newY - 27, horizWidth, horizColor);
			newY += 21;
			graphics.drawShadowText("Matches Won:", newX + 2, newY - 32, 0xB5DC4F, 1, false);
			graphics.drawColoredStringCentered(rightX, "0", 0xf1f1f1, 0, 1, newY - 32);
			graphics.drawLineHoriz(newX, newY - 27, horizWidth, horizColor);
			newY += 21;
			graphics.drawShadowText("Matches Lost:", newX + 2, newY - 32, 0xB5DC4F, 1, false);
			graphics.drawColoredStringCentered(rightX, "0", 0xf1f1f1, 0, 1, newY - 32);
			graphics.drawLineHoriz(newX, newY - 27, horizWidth, horizColor);
			newY += 21;
			graphics.drawShadowText("Type:", newX + 2, newY - 32, 0xB5DC4F, 1, false);
			graphics.drawColoredStringCentered(rightX, vc.getPartySearchSettingByName(vc.canJoin), 0xf1f1f1, 0, 1, newY - 32);
			graphics.drawLineHoriz(newX, newY - 27, horizWidth, horizColor);

			drawSubmitButton(graphics, x + 7, newY - 20, 394, 28, 18, 1, "Send Party Request", false, new ButtonHandler() {
				@Override
				void handle() {
					// MAGIC
				}
			});
		} else {
			graphics.drawString("Search partys:", x + 10, y + 65, 0xB5DC4F, 2);

			drawSearchButton(graphics, x + 90, y + 48, width, height, new ButtonHandler() {
				@Override
				void handle() {
					partySetupPanel.setFocus(partySearch_field);
				}
			});


			graphics.drawString("Party Points:", x + 330, y + 90, 0xB5DC4F, 0);
			drawSubmitButton(graphics, x + 295, y + 50, 103, 24, 17, 1, "Reset search", false, new ButtonHandler() {
				@Override
				void handle() {
					partySetupPanel.setFocus(partySearch_field);
					partySetupPanel.setText(partySearch_field, "");
				}
			});

			int color = 0xC2C8C3;
			int listStartPoint = partySetupPanel.getScrollPosition(partySearchScroll);
			int listEndPoint = listStartPoint + 3;
			for (int i = -1; i < filteredList.size(); i++) {
				if (i >= 500) {
					break;
				}
				graphics.drawString("Displaying search results For: " + (!searchTerm.isEmpty() ? "\"" + searchTerm.toLowerCase() + "\"" : "ALL") + " (" + (filteredList.size()) + ")", x + 5, y + 90, 0xf1f1f1, 0);
				partySetupPanel.setListEntry(partySearchScroll, i + 1, "", 0, null, null);

				if (i < listStartPoint || i > listEndPoint)
					continue;
				PartyResult cr = filteredList.get(i);
				if (mc.getMouseX() >= (newX) && mc.getMouseY() >= (newY) && mc.getMouseX() <= newX + 389
					&& mc.getMouseY() <= (newY) + boxHeight && mc.inputX_Action == InputXAction.ACT_0) {
					graphics.drawBoxAlpha(newX + 1, newY, boxWidth + 1, boxHeight - 1, 0x90E05B, 192);
					if (mc.getMouseClick() == 1) {
						selectedPartyInSearch = i;
						mc.setMouseClick(0);
					}
				} else {
					if (selectedPartyInSearch == i) {
						graphics.drawBoxAlpha(newX + 1, newY, boxWidth + 1, boxHeight - 1, 0x90E05B, 192);
					} else {
						graphics.drawBoxAlpha(newX + 1, newY, boxWidth + 1, boxHeight - 1, color, 192);
					}
				}

				if (i % 2 == 0) {
					color = 0xD9DCD6;
				} else {
					color = 0xC2C8C3;
				}
				graphics.drawBoxBorder(newX, 390, newY, boxHeight, 0x716F6C);

				graphics.drawShadowText(cr.getPartyName(), x + 7, newY + 15, 0xFBFBF9, 4, false);
				graphics.drawString(cr.getPartySearchSettingByName(cr.canJoin), x + 7, newY + 29, 0xF2A967, 0);

				graphics.drawString("Members:", x + 250, newY + 12, 0xf1f1f1, 0);
				graphics.drawShadowText(cr.getPartyMembersTotal() + "/ 15", x + 273, newY + 21, 0xFBFBF9, 0, true);
				graphics.drawLineVert(x + 320, newY, 0x716F6C, boxHeight);
				graphics.drawShadowText(cr.getPartyPoints() + " pts", x + 356, newY + 14, 0xFBFBF9, 1, true);
				graphics.drawString((Config.isAndroid() ? "Tap" : "Click") + " to view details", x + 120, newY + 29, 0xf1f1f1, 0);

				newY += boxHeight + 3;
			}
		}

		partySetupPanel.drawPanel();
	}

	private void drawPartySetup(GraphicsController graphics) {
		int leftBoxW = 196;
		int leftBoxH = 184;
		if (mc.party.inParty()) {
			// LEFT SIDE
			graphics.drawBoxAlpha(x + 3, y + 48, leftBoxW, leftBoxH, 0x1D1915, 192); //5F5147
			graphics.drawBoxBorder(x + 3, leftBoxW, y + 48, leftBoxH, 0x5F5147);

			// RIGHT SIDE
			graphics.drawBoxAlpha(x + 3 + 196 + 10, y + 48, leftBoxW, leftBoxH, 0x1D1915, 192); //5F5147
			graphics.drawBoxBorder(x + 3 + 196 + 10, leftBoxW, y + 48, leftBoxH, 0x5F5147);

			drawSubmitButton(graphics, x + 9, y + 59, 184, 32, 14, 1, "Share Loot", true, new ButtonHandler() {
				@Override
				void handle() {
					getClient().sendCommandString("shareloot");
					//setVisible(false);
				}
			});
			if (mc.party.shareLoot[0] > 0) {
				graphics.drawWrappedCenteredString("Yes ", x + 98, y + 86, 184, 1, 0xD9CD98, false);
			} else {
				graphics.drawWrappedCenteredString("No ", x + 98, y + 86, 184, 1, 0xD9CD98, false);
			}

			drawSelectButton(graphics, x + 9, y + 96, 184, 32, 14, 14, 1, 1, "Who can kick in party?", mc.party.getPartySettingByName(mc.party.getPartySetting(0)), new ButtonHandler() {
				@Override
				void handle() {
					menuY = mc.mouseY - 7;
					menuX = mc.mouseX - x / 2;
					menu_visible = true;
					rightClickMenu.recalculateSize(0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.PARTY_RANK_ALLOW_KICK,
						"Anyone", 0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.PARTY_RANK_ALLOW_KICK,
						"Owner", 1);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.PARTY_RANK_ALLOW_KICK,
						"General+", 2);
				}
			});

			drawSelectButton(graphics, x + 9, y + 133, 184, 32, 14, 14, 1, 1, "Who can invite into party?", mc.party.getPartySettingByName(mc.party.getPartySetting(1)), new ButtonHandler() {
				@Override
				void handle() {
					menuY = mc.mouseY - 7;
					menuX = mc.mouseX - x / 2;
					menu_visible = true;
					rightClickMenu.recalculateSize(0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.PARTY_RANK_ALLOW_INVITE,
						"Anyone", 0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.PARTY_RANK_ALLOW_INVITE,
						"Owner", 1);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.PARTY_RANK_ALLOW_INVITE,
						"General+", 2);
				}
			});

			/*drawSelectButton(graphics, x + 9, y + 131 + 39, 184, 32, 14, 14, 1, 1, "Accept party requests?", mc.party.getPartySearchSettingByName(), new ButtonHandler() {
				@Override
				void handle() {
					menuY = mc.mouseY - 7;
					menuX = mc.mouseX - x / 2;
					menu_visible = true;
					rightClickMenu.recalculateSize(0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.PARTY_ACCEPT_REQUESTS,
						"Anyone can join", 0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.PARTY_ACCEPT_REQUESTS,
						"Invite only", 1);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.PARTY_ACCEPT_REQUESTS,
						"Closed", 2);
				}
			});*/

			graphics.drawWrappedCenteredString("Right-click on a box to change options.", x + 101, y + 214, 175, 0, 0xD9CD98, false);

			drawSubmitButton(graphics, x + 235, y + 54, 146, 28, 18, 1, "Invite to Party", false, new ButtonHandler() {
				@Override
				void handle() {
					mc.showItemModX(InputXPrompt.partyInvite, InputXAction.INVITE_PARTY_PLAYER, true);
					mc.showUiTab = 0;
				}
			});
			graphics.drawLineHoriz(x + 210, y + 87, 194, 0x5F5147);
			drawSubmitButton(graphics, x + 235, y + 92, 146, 28, 18, 1, "Leave Party", false, new ButtonHandler() {
				@Override
				void handle() {
					getClient().sendCommandString("leaveparty");
					setVisible(false);
				}
			});
			graphics.drawLineHoriz(x + 210, y + 125, 194, 0x5F5147);
		} else {
			partySetupPanel.show(partyName_field);
			partySetupPanel.show(partyTag_field);
			graphics.drawBoxAlpha(x + 2, y + 48, width - 5, height - 37, 0x1D1915, 192); //5F5147
			graphics.drawBoxBorder(x + 2, width - 5, y + 48, height - 37, 0x5F5147);
			graphics.drawColoredStringCentered(width / 2 + x, "Choose a Party Name between 2-16 characters in length.", 0xf1f1f1, 0, 2, y + 64);
			drawInputButton(graphics, x + 14, y + 75, 380, 42, 14, 1, "Party Name:", false, new ButtonHandler() {
				@Override
				void handle() {
					partySetupPanel.setFocus(partyName_field);
				}
			});
			graphics.drawColoredStringCentered(width / 2 + x, "Enter your Party Tag between 2-5 characters in length.", 0xf1f1f1, 0, 2, y + 64 + 72);
			drawInputButton(graphics, x + 14, y + 147, 380, 42, 14, 1, "Party Tag:", false, new ButtonHandler() {
				@Override
				void handle() {
					partySetupPanel.setFocus(partyTag_field);
				}
			});
			drawSubmitButton(graphics, x + 132, y + 196, 142, 28, 18, 1, "Submit", false, new ButtonHandler() {
				@Override
				void handle() {
					sendCreateParty(partySetupPanel.getControlText(partyName_field), partySetupPanel.getControlText(partyTag_field));
				}
			});
		}
		partySetupPanel.drawPanel();
	}

	private void drawCloseButton(GraphicsController graphics, int x, int y, int width, int height, String text,
								 boolean checked, ButtonHandler handler) {
		int allColor = 0x957357;

		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height && !menu_visible) {
			if (!checked)
				allColor = 0x442C13;
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawBoxAlpha(x, y, width, height, allColor, 255);
		graphics.drawLineHoriz(x, y, width, 0x5F5147);
		graphics.drawString(text, x + (width / 2 - graphics.stringWidth(1, text) / 2), y + height / 2 + 5, 0xffffff, 1);
	}

	private void drawButton(GraphicsController graphics, int x, int y, int width, int height, String text,
							boolean checked, ButtonHandler handler) {
		int allColor = 0x231B15;
		if (checked) {
			allColor = 0x332A22;
		}
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			if (!checked)
				allColor = 0x2A221B;
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawBoxAlpha(x, y, width, height, allColor, 255);
		graphics.drawBoxBorder(x, width, y, height, 0xA68B71); // 0xA3510C
		graphics.drawString(text, x + (width / 2 - graphics.stringWidth(1, text) / 2), y + height / 2 + 5, 0xE3CCCF, 2);
	}

	private void drawInputButton(GraphicsController graphics, int x, int y, int width, int height, int heightText, int fontSize, String text,
								 boolean checked, ButtonHandler handler) {
		int allColor = 0x3D3428;
		if (checked) {
			allColor = 0x332A22;
		}
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawBoxAlpha(x, y, width, height, allColor, 255);
		graphics.drawBoxBorder(x, width, y, height, 0x777775);
		graphics.drawString(text, x + (width / 2 - graphics.stringWidth(1, text) / 2), y + heightText, 0xE3CCCF, fontSize);
	}

	private void drawSearchButton(GraphicsController graphics, int x, int y, int width, int height, ButtonHandler handler) {
		int allColor = 0xFBFCFE;
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawBoxAlpha(x, y, width, height, allColor, 255);
		graphics.drawBoxBorder(x, width, y, height, 0x080809);
	}

	private void drawSelectButton(GraphicsController graphics, int x, int y, int width, int height, int heightText, int secondaryHeightText, int fontSize, int secondaryFontSize, String text,
								  String secondaryText, ButtonHandler handler) {
		int allColor = 0x4F4841;
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			if (mc.getMouseClick() == 2) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}

		graphics.drawBoxAlpha(x, y, width, height, allColor, 255);
		graphics.drawBoxBorder(x, width, y, height, 0x7C6C5C);
		graphics.drawBoxBorder(x - 1, width + 2, y - 1, height + 2, 0x060607);
		graphics.drawString(text, x + (width / 2 - graphics.stringWidth(1, text) / 2), y + heightText - 2, 0xEA9F59, fontSize);
		graphics.drawColoredStringCentered(x + width / 2, secondaryText, 0xf1f1f1, 0, secondaryFontSize, y + heightText + secondaryHeightText);
	}

	private void drawSubmitButton(GraphicsController graphics, int x, int y, int width, int height, int heightText, int fontSize, String text,
								  boolean checked, ButtonHandler handler) {
		int allColor = 0x403020;
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			if (!checked)
				allColor = 0x423D2D;
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawBoxAlpha(x, y, width, height, allColor, 255);
		graphics.drawBoxBorder(x, width, y, height, 0x474745); // 0xA3510C
		graphics.drawString(text, x + (width / 2 - graphics.stringWidth(1, text) / 2), y + heightText, 0xFF9530, fontSize);
	}

	private void drawRightClickMenu() {
		int clickedIndex = -1;
		if (menu_visible) {
			int w = this.rightClickMenu.getWidth();
			int h = this.rightClickMenu.getHeight();
			if (menuX - 10 > mc.getMouseX() || mc.getMouseY() < menuY - 10
				|| mc.getMouseX() > menuX + w + 10 || mc.getMouseY() > 10 + h + menuY) {
				this.menu_visible = false;
			} else {
				if (mc.mouseButtonClick != 0 && menu_visible) {
					clickedIndex = this.rightClickMenu.handleClick(mc.mouseX, menuX, menuY, mc.mouseY);
					//System.out.println(clickedIndex);
				}
				if (clickedIndex >= 0) {
					mc.mouseButtonClick = 0;
					menu_visible = false;
					MenuItemAction act = this.rightClickMenu.getItemAction(clickedIndex);
					if (act == MenuItemAction.PARTY_PROMOTE) {
						if (clickedIndex == 1) {
							clickedIndex = 2;
						} else if (clickedIndex == 2) {
							clickedIndex = 1;
							String[] kickMessage = new String[]{"Are you sure you want to give " + mc.party.username[selectedPartyMate] + " the leadership?"};
							mc.partyKickPlayer = mc.party.username[selectedPartyMate];
							mc.showItemModX(kickMessage, InputXAction.PARTY_DELEGATE_LEADERSHIP, false);
							return;
						}
						sendPartyRank(mc.party.username[selectedPartyMate], clickedIndex);
					} else if (act == MenuItemAction.PARTY_RANK_ALLOW_KICK) {
						sendPartySettings(0, clickedIndex);
					} else if (act == MenuItemAction.PARTY_RANK_ALLOW_INVITE) {
						sendPartySettings(1, clickedIndex);
					} else if (act == MenuItemAction.PARTY_ACCEPT_REQUESTS) {
						sendPartySettings(2, clickedIndex);
					}
				}
				this.rightClickMenu.render(menuY, menuX, mc.mouseY, (byte) -12, mc.mouseX);
			}
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean b) {
		this.visible = b;
	}

	public void resetAll() {
		partyActiveInterface = 1;
		selectedPartyMate = -1;
		selectedPartyInSearch = -1;
	}

	private void resetAfterCreation() {
		partyActiveInterface = 1;
		partySetupPanel.setText(partyName_field, "");
		partySetupPanel.setText(partyTag_field, "");
		partySetupPanel.setText(partySearch_field, "");
		partySetupPanel.hide(partySearch_field);
		partySetupPanel.setFocus(-1);
	}

	private void sendCreateParty(String name, String tag) {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(12);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(0);
		getClient().packetHandler.getClientStream().writeBuffer1.putString(name);
		getClient().packetHandler.getClientStream().writeBuffer1.putString(tag);
		getClient().packetHandler.getClientStream().finishPacket();

		resetAfterCreation();
	}

	public void sendPartyLeave() {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(12);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(1);
		getClient().packetHandler.getClientStream().finishPacket();

		resetAll();
	}

	private void sendPartyAccept() {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(12);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(3);
		getClient().packetHandler.getClientStream().finishPacket();

		resetAll();
	}

	private void sendPartyDecline() {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(12);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(4);
		getClient().packetHandler.getClientStream().finishPacket();

		resetAll();
	}

	private void sendPartyRank(String playerName, int rank) {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(12);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(6);
		getClient().packetHandler.getClientStream().writeBuffer1.putString(playerName);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(rank);
		getClient().packetHandler.getClientStream().finishPacket();
	}

	private void sendPartySettings(int settingMode, int state) {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(12);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(7);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(settingMode);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(state);
		getClient().packetHandler.getClientStream().finishPacket();
	}

	private void sendPartySearch() {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(12);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(8);
		getClient().packetHandler.getClientStream().finishPacket();
	}

	public void initializeInvite(String byPlayer, String byParty) {
		invitationBy = "@yel@" + byPlayer + " @whi@has sent you a party invitation:";
		if (byParty != null) {
			this.setVisible(true);
			this.partyActiveInterface = 2;
		} else {
			this.setVisible(false);
		}
	}

	public mudclient getClient() {
		return mc;
	}

	public boolean keyDown(int key) {
		if (partyActiveInterface == 1) {
			if (partySetupPanel.focusOn(partyName_field) || partySetupPanel.focusOn(partyTag_field) || partySetupPanel.focusOn(partySearch_field)) {
				partySetupPanel.keyPress(key);
				return true;
			}
		}
		return false;
	}

	public void resetPartys() {
		readPartys.clear();
	}

	public void addParty(int partyID, String partyName, String partyTag, int members, int canJoin, int partyPoints, int partyRank) {
		readPartys.add(new PartyResult(partyID, partyName, partyTag, members, canJoin, partyPoints, partyRank));
	}

	abstract static class ButtonHandler {
		abstract void handle();
	}

	static class PartyResult {
		private String partyName, partyTag;
		private int membersTotal, partyPoints, canJoin, partyID, partyRank;
		private String[] partySearchSettings = {"@gr2@Anyone can join", "@yel@Invite only", "@red@Closed"};

		PartyResult(int partyID, String partyName, String partyTag, int members, int canJoin, int partyPoints, int partyRank) {
			this.partyID = partyID;
			this.partyName = partyName;
			this.partyTag = partyTag;
			this.membersTotal = members;
			this.canJoin = canJoin;
			this.partyPoints = partyPoints;
			this.partyRank = partyRank;
		}

		int getPartyGlobalRank() {
			return partyRank;
		}

		String getPartySearchSettingByName(int i) {
			return partySearchSettings[i];
		}

		int getPartyPoints() {
			return partyPoints;
		}

		public int getPartyID() {
			return partyID;
		}

		String getPartyName() {
			return partyName;
		}

		public void setPartyName(String name) {
			this.partyName = name;
		}

		String getPartyTag() {
			return partyTag;
		}

		public void setPartyTag(String tag) {
			this.partyTag = tag;
		}

		int getPartyMembersTotal() {
			return membersTotal;
		}
	}

}
