package com.openrsc.interfaces.misc.clan;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import orsc.Config;
import orsc.enumerations.InputXAction;
import orsc.enumerations.MenuItemAction;
import orsc.graphics.gui.InputXPrompt;
import orsc.graphics.gui.Menu;
import orsc.graphics.gui.Panel;
import orsc.graphics.gui.SocialLists;
import orsc.graphics.two.GraphicsController;
import orsc.mudclient;

public final class ClanInterface {
	public int clanGUIScroll;
	public int clanSearchScroll;
	public int clanActivePanel = 1;
	public int clanName_field;
	public int clanTag_field;
	public int clanSearch_field;
	private int selectedClanMate = -1;
	private int selectedClanInSearch = -1;
	private ArrayList<ClanResult> readClans;
	public Panel clanSetupPanel;
	private Comparator<ClanResult> clanComperator = (o1, o2) -> {
		if (o1.getClanPoints() == o2.getClanPoints()) {
			return o1.getClanName().compareTo(o2.getClanName());
		}
		return o1.getClanPoints() > o2.getClanPoints() ? -1 : 1;
	};
	private mudclient mc;
	private int x, y;
	private int width, height;
	private int menuX = 0;
	private int menuY = 0;
	private int clanActiveInterface = 1;
	private boolean visible;
	private boolean menu_visible = false;
	private String invitationBy = null;
	private String invitationByClan = null;
	private String[] clanMateTable = {"Username", "Rank", "Kills", "Deaths", "Ratio"};
	private Menu rightClickMenu;

	public ClanInterface(mudclient mc) {
		this.mc = mc;

		width = 408;
		height = 246 - 25;

		x = (mc.getGameWidth() / 2) - width;
		y = (mc.getGameHeight() / 2) - height;

		readClans = new ArrayList<>();

		clanSetupPanel = new Panel(mc.getSurface(), 15);
		rightClickMenu = new Menu(mc.getSurface(), 1, "@ora@Choose Option");

		clanGUIScroll = clanSetupPanel.addScrollingList2(x + 97, y + 49 + 34, 307, 146, 500, 1, true);
		clanSearchScroll = clanSetupPanel.addScrollingList2(x + 97, y + 49 + 34, 307, 146, 1000, 1, true);

		clanName_field = clanSetupPanel.addCenteredTextEntry(x + 201, y + 102, 174, 16, 13, 3, false, true);
		clanTag_field = clanSetupPanel.addCenteredTextEntry(x + 201, y + 162, 174, 5, 13, 3, false, true);
		clanSearch_field = clanSetupPanel.addLeftTextEntry(x + 95, y + 61, 174, 13, 3, 16, false, false);
	}

	public void reposition() {
		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		clanSetupPanel.reposition(clanGUIScroll, x + 97, y + 49 + 34, 307, (selectedClanMate != -1 && mc.clan.isAllowed(0) ? 83 : 146));
		clanSetupPanel.reposition(clanSearchScroll, x + 100, y + 95, 307, 141);

		clanSetupPanel.reposition(clanName_field, x + 201, y + 102, 174, 18);
		clanSetupPanel.reposition(clanTag_field, x + 201, y + 174, 174, 18);
		clanSetupPanel.reposition(clanSearch_field, x + 95, y + 61, 174, 18);
	}

	public boolean onRender(GraphicsController graphics) {
		reposition();
		switch (clanActiveInterface) {
			case 1: // CLAN MAIN GUI
				drawClanMain(graphics);
				break;
			case 2: // CLAN INVITEED SCREEN
				drawInvite(graphics);
				break;
		}
		return true;
	}

	private void drawClanMates(GraphicsController graphics) {
		clanSetupPanel.clearList(clanGUIScroll);
		clanSetupPanel.show(clanGUIScroll);
		int leftBoxW = 402;
		int leftBoxH = 113;

		int boxWidth = 397;
		int boxHeight = 22;

		int newX = x + 4;
		int newY = y + 82;

		int tableBox = x + 5;
		int tableBoxW = 65;

		if (selectedClanMate != -1 && mc.clan.isAllowed(0)) {
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
				graphics.drawString(clanMateTable[i], tableBox + 4, y + 75, 0xf1f1f1, 1);
			} else {
				graphics.drawBoxAlpha(tableBox + 135, y + 48 + 11 + 2, tableBoxW, 20, 0x432C26, 192); //5F5147
				graphics.drawBoxBorder(tableBox + 135, tableBoxW, y + 48 + 11 + 2, 20, 0x4C4445);
				graphics.drawString(clanMateTable[i], tableBox + 4 + 135, y + 75, 0xf1f1f1, 1);
				tableBox += 66;
			}
		}

		int color = 0x232220;
		int listStartPoint = clanSetupPanel.getScrollPosition(clanGUIScroll);
		int listEndPoint = listStartPoint + (selectedClanMate != -1 && mc.clan.isAllowed(0) ? 3 : 6);
		int showing = 0;
		for (int i = -1; i < SocialLists.clanListCount; i++) {
			showing = i + 1;
			if (i >= 500) {
				break;
			}

			clanSetupPanel.setListEntry(clanGUIScroll, i + 1, "", 0, null, null);

			if (i < listStartPoint || i > listEndPoint)
				continue;
			if (mc.clan.isAllowed(0)) {
				if (mc.getMouseX() >= (newX) && mc.getMouseY() >= (newY) && mc.getMouseX() <= newX + 384
					&& mc.getMouseY() <= (newY - 5) + boxHeight && mc.inputX_Action == InputXAction.ACT_0) {
					graphics.drawBoxAlpha(newX + 1, newY, boxWidth + 1, boxHeight - 1, 0x202F39, 192);
					if (mc.getMouseClick() == 1) {
						selectedClanMate = i;
						mc.setMouseClick(0);
					}
				} else {
					if (selectedClanMate == i) {
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
			graphics.drawColoredString(newX + 3, newY + 16, mc.clan.username[i], 2, 0xffffff, (mc.clan.clanRank[i] == 1 ? 3 : mc.clan.clanRank[i] == 2 ? 4 : 0));
			graphics.drawColoredString(newX + 3 + 137, newY + 16, "" + mc.clan.getClanRankNames(mc.clan.clanRank[i]), 2, 0xffffff, 0);
			graphics.drawColoredString(newX + 3 + 135 + 67, newY + 16, "" + mc.clan.getPlayerKills(i), 2, 0xffffff, 0);
			graphics.drawColoredString(newX + 3 + 135 + 67 + 67, newY + 16, "" + mc.clan.getPlayerDeaths(i), 2, 0xffffff, 0);
			graphics.drawColoredString(newX + 3 + 135 + 67 + 67 + 67, newY + 16, "" + mc.clan.getKDR(i), 2, 0xffffff, 0);
			newY += boxHeight - 1;
		}


		graphics.drawString("Total clanmates: (" + showing + " / 15)", newX, y + 55, 0xEFB063, 0);
		if (selectedClanMate != -1 && mc.clan.isAllowed(0)) {
			graphics.drawString("Settings for: " + mc.clan.username[selectedClanMate], newX, y + 180, 0xB39684, 0);
			drawSubmitButton(graphics, newX + 250, y + 194, 130, 28, 18, 1, "Kick user", false, new ButtonHandler() {
				@Override
				void handle() {
					String[] kickMessage = new String[]{"Are you sure you want to kick " + mc.clan.username[selectedClanMate] + " from clan?"};
					mc.clanKickPlayer = mc.clan.username[selectedClanMate];
					mc.showItemModX(kickMessage, InputXAction.KICK_CLAN_PLAYER, false);
				}
			});
			if (mc.clan.isClanLeader()) {
				drawSelectButton(graphics, x + 25, y + 195, 130, 26, 14, 8, 1, 0, "Rank user", "(right-click)", new ButtonHandler() {
					@Override
					void handle() {
						menuY = mc.mouseY - 7;
						menuX = mc.mouseX - x / 2;
						menu_visible = true;
						rightClickMenu.recalculateSize(0);
						rightClickMenu.addCharacterItem_WithID(0,
							"", MenuItemAction.CLAN_PROMOTE,
							"None", 0);
						rightClickMenu.addCharacterItem_WithID(2,
							"", MenuItemAction.CLAN_PROMOTE,
							"General", 2);
						rightClickMenu.addCharacterItem_WithID(1,
							"", MenuItemAction.CLAN_PROMOTE,
							"Delegate leadership", 1);
					}
				});
			}
		}
		clanSetupPanel.drawPanel();
	}

	private void drawInvite(GraphicsController graphics) {
		clanSetupPanel.hide(clanGUIScroll);
		clanSetupPanel.hide(clanSearchScroll);
		int leftBoxW = width - 88;
		int leftBoxH = 138;
		graphics.drawBox(x + 88 / 2, y - 10, leftBoxW, 20, 0x957357);
		graphics.drawBoxAlpha(x + 88 / 2, y + 10, leftBoxW, leftBoxH, 0x1D1915, 192); //5F5147
		graphics.drawBoxBorder(x + 88 / 2, leftBoxW, y - 10, leftBoxH + 20, 0x5F5147);
		graphics.drawColoredStringCentered(width / 2 + x, "Clan Invitation!", 0xE5D8C0, 0, 1, y + 5);
		graphics.drawColoredStringCentered(width / 2 + x, invitationBy, 0xf1f1f1, 0, 1, y + 28);
		graphics.drawColoredStringCentered(width / 2 + x, "@cla@" + invitationByClan, 0xffffff, 0, 5, y + 58);
		graphics.drawColoredStringCentered(width / 2 + x, "Would you like to join clan?", 0xf1f1f1, 0, 1, y + 90);

		drawSubmitButton(graphics, x + 55, y + 110, 142, 28, 18, 1, "Accept", false, new ButtonHandler() {
			@Override
			void handle() {
				sendClanAccept();
				setVisible(false);
			}
		});

		drawSubmitButton(graphics, x + 211, y + 110, 142, 28, 18, 1, "Decline", false, new ButtonHandler() {
			@Override
			void handle() {
				sendClanDecline();
				setVisible(false);
			}
		});
	}

	private void drawClanMain(GraphicsController graphics) {
		clanSetupPanel.hide(clanGUIScroll);
		clanSetupPanel.hide(clanSearchScroll);
		clanSetupPanel.hide(clanName_field);
		clanSetupPanel.hide(clanTag_field);
		clanSetupPanel.hide(clanSearch_field);
		int boxColor = 0x1D1711;
		int headerColor = 0x957357;
		// HEADER
		graphics.drawBox(x, y, width, 15, headerColor);
		graphics.drawBoxAlpha(x, y + 15, width, height, boxColor, 192);
		graphics.drawBoxBorder(x - 1, width + 2, y - 1, height + 42, 0x5F5147);
		graphics.drawColoredStringCentered(width / 2 + x, "Clan Settings", 0xE5D8C0, 0, 1, y + 12);
		graphics.drawLineHoriz(x, y + 43, width, 0x6E5D4E);
		// CONTENT
		if (!mc.clan.inClan()) {
			drawButton(graphics, x + 3, y + 18, 125, 22, "Clan Search", this.clanActivePanel == 3, new ButtonHandler() {
				@Override
				void handle() {
					clanActivePanel = 3;
					resetAll();
					sendClanSearch();
				}
			});
		} else {
			if (mc.clan.inClan()) {
				drawButton(graphics, x + 3, y + 18, 125, 22, "Clanmates", clanActivePanel == 2, new ButtonHandler() {
					@Override
					void handle() {
						clanActivePanel = 2;
						resetAll();
					}
				});
				drawButton(graphics, x + 3 + (mc.clan.isClanLeader() ? 256 : 128), y + 18, (mc.clan.isClanLeader() ? 146 : 146), 22, "Clan Stats / Challenges", false, new ButtonHandler() {
					@Override
					void handle() {
						setVisible(false);
					}
				});
				if (mc.clan.isAllowed(1) && !mc.clan.isClanLeader()) {
					drawSubmitButton(graphics, x + 280, y + 18, 125, 22, 15, 1, "Invite Player", false, new ButtonHandler() {
						@Override
						void handle() {
							mc.showItemModX(InputXPrompt.clanInvite, InputXAction.INVITE_CLAN_PLAYER, true);
							mc.showUiTab = 0;
						}
					});
				}
			}
		}
		if (!mc.clan.inClan() || (mc.clan.isClanLeader() && mc.clan.inClan())) {
			drawButton(graphics, x + 131, y + 18, 125, 22, "Clan Setup", clanActivePanel == 1, new ButtonHandler() {
				@Override
				void handle() {
					clanActivePanel = 1;
				}
			});
		}

		switch (clanActivePanel) {
			case 1:
				drawClanSetup(graphics);
				break;
			case 2:
				if (mc.clan.inClan()) {
					drawClanMates(graphics);
				} else {
					clanActivePanel = 1;
				}
				break;
			case 3:
				if (!mc.clan.inClan()) {
					drawClanSearch(graphics);
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

	private void drawClanSearch(GraphicsController graphics) {
		clanSetupPanel.clearList(clanSearchScroll);
		clanSetupPanel.show(clanSearchScroll);
		clanSetupPanel.show(clanSearch_field);

		Collections.sort(readClans, clanComperator);
		String searchTerm = clanSetupPanel.getControlText(clanSearch_field);
		LinkedList<ClanResult> filteredList = new LinkedList<ClanResult>();
		for (ClanResult c : readClans) {
			String clan = c.getClanName().toLowerCase();

			if (clan.contains(searchTerm.toLowerCase())) {
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

		if (this.selectedClanInSearch != -1) {
			clanSetupPanel.setFocus(-1);
			clanSetupPanel.hide(clanSearch_field);
			final ClanResult vc = filteredList.get(selectedClanInSearch);
			int horizColor = 0x4C4638;
			int horizWidth = 400;
			graphics.drawShadowText(vc.getClanName() + " < " + vc.getClanTag() + " >", x + 7, newY - 32, 0xFBFBF9, 5, false);
			graphics.drawLineHoriz(newX, newY - 25, horizWidth, horizColor);
			newY += 21;
			graphics.drawShadowText("Global Rank:", newX + 2, newY - 32, 0xB5DC4F, 1, false);
			graphics.drawColoredStringCentered(rightX, "#" + vc.getClanGlobalRank(), 0xf1f1f1, 0, 1, newY - 32);
			graphics.drawLineHoriz(newX, newY - 27, horizWidth, horizColor);
			newY += 21;
			graphics.drawShadowText("Total Points:", newX + 2, newY - 32, 0xB5DC4F, 1, false);
			graphics.drawColoredStringCentered(rightX, "" + vc.getClanPoints(), 0xf1f1f1, 0, 1, newY - 32);
			graphics.drawLineHoriz(newX, newY - 27, horizWidth, horizColor);
			newY += 21;
			graphics.drawShadowText("Total Members:", newX + 2, newY - 32, 0xB5DC4F, 1, false);
			graphics.drawColoredStringCentered(rightX, vc.getClanMembersTotal() + "/15", 0xf1f1f1, 0, 1, newY - 32);
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
			graphics.drawColoredStringCentered(rightX, vc.getClanSearchSettingByName(vc.canJoin), 0xf1f1f1, 0, 1, newY - 32);
			graphics.drawLineHoriz(newX, newY - 27, horizWidth, horizColor);

			drawSubmitButton(graphics, x + 7, newY - 20, 394, 28, 18, 1, "Send Clan Request", false, new ButtonHandler() {
				@Override
				void handle() {
					// MAGIC
				}
			});
		} else {
			graphics.drawString("Search clans:", x + 10, y + 65, 0xB5DC4F, 2);

			drawSearchButton(graphics, x + 90, y + 48, width, height, new ButtonHandler() {
				@Override
				void handle() {
					clanSetupPanel.setFocus(clanSearch_field);
				}
			});


			graphics.drawString("Clan Points:", x + 330, y + 90, 0xB5DC4F, 0);
			drawSubmitButton(graphics, x + 295, y + 50, 103, 24, 17, 1, "Reset search", false, new ButtonHandler() {
				@Override
				void handle() {
					clanSetupPanel.setFocus(clanSearch_field);
					clanSetupPanel.setText(clanSearch_field, "");
				}
			});

			int color = 0xC2C8C3;
			int listStartPoint = clanSetupPanel.getScrollPosition(clanSearchScroll);
			int listEndPoint = listStartPoint + 3;
			for (int i = -1; i < filteredList.size(); i++) {
				if (i >= 500) {
					break;
				}
				graphics.drawString("Displaying search results For: " + (!searchTerm.isEmpty() ? "\"" + searchTerm.toLowerCase() + "\"" : "ALL") + " (" + (filteredList.size()) + ")", x + 5, y + 90, 0xf1f1f1, 0);
				clanSetupPanel.setListEntry(clanSearchScroll, i + 1, "", 0, null, null);

				if (i < listStartPoint || i > listEndPoint)
					continue;
				ClanResult cr = filteredList.get(i);
				if (mc.getMouseX() >= (newX) && mc.getMouseY() >= (newY) && mc.getMouseX() <= newX + 389
					&& mc.getMouseY() <= (newY) + boxHeight && mc.inputX_Action == InputXAction.ACT_0) {
					graphics.drawBoxAlpha(newX + 1, newY, boxWidth + 1, boxHeight - 1, 0x90E05B, 192);
					if (mc.getMouseClick() == 1) {
						selectedClanInSearch = i;
						mc.setMouseClick(0);
					}
				} else {
					if (selectedClanInSearch == i) {
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

				graphics.drawShadowText(cr.getClanName(), x + 7, newY + 15, 0xFBFBF9, 4, false);
				graphics.drawString(cr.getClanSearchSettingByName(cr.canJoin), x + 7, newY + 29, 0xF2A967, 0);

				graphics.drawString("Members:", x + 250, newY + 12, 0xf1f1f1, 0);
				graphics.drawShadowText(cr.getClanMembersTotal() + "/ 15", x + 273, newY + 21, 0xFBFBF9, 0, true);
				graphics.drawLineVert(x + 320, newY, 0x716F6C, boxHeight);
				graphics.drawShadowText(cr.getClanPoints() + " pts", x + 356, newY + 14, 0xFBFBF9, 1, true);
				graphics.drawString((Config.isAndroid() ? "Tap" : "Click") + " to view details", x + 120, newY + 29, 0xf1f1f1, 0);

				newY += boxHeight + 3;
			}
		}

		clanSetupPanel.drawPanel();
	}

	private void drawClanSetup(GraphicsController graphics) {
		int leftBoxW = 196;
		int leftBoxH = 184;
		if (mc.clan.inClan()) {
			// LEFT SIDE
			graphics.drawBoxAlpha(x + 3, y + 48, leftBoxW, leftBoxH, 0x1D1915, 192); //5F5147
			graphics.drawBoxBorder(x + 3, leftBoxW, y + 48, leftBoxH, 0x5F5147);

			// RIGHT SIDE
			graphics.drawBoxAlpha(x + 3 + 196 + 10, y + 48, leftBoxW, leftBoxH, 0x1D1915, 192); //5F5147
			graphics.drawBoxBorder(x + 3 + 196 + 10, leftBoxW, y + 48, leftBoxH, 0x5F5147);

			graphics.drawBoxAlpha(x + 9, y + 54, 184, 37, 0x544B40, 255);
			graphics.drawBoxBorder(x + 9, 184, y + 54, 37, 0x7D7161);
			graphics.drawBoxBorder(x + 8, 186, y + 53, 39, 0x060607);
			graphics.drawColoredStringCentered(x + 32 + (184 / 2 - graphics.stringWidth(1, "My clan:") / 2), "My clan:", 0xEA9F59, 0, 1, y + 66);
			graphics.drawColoredStringCentered(x + 101, mc.clan.getClanName() + " <@cla@" + mc.clan.getClanTag() + "@whi@>", 0xf1f1f1, 0, 1, y + 85);

			drawSelectButton(graphics, x + 9, y + 96, 184, 32, 14, 14, 1, 1, "Who can kick in clan?", mc.clan.getClanSettingByName(mc.clan.getClanSetting(0)), new ButtonHandler() {
				@Override
				void handle() {
					menuY = mc.mouseY - 7;
					menuX = mc.mouseX - x / 2;
					menu_visible = true;
					rightClickMenu.recalculateSize(0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.CLAN_RANK_ALLOW_KICK,
						"Anyone", 0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.CLAN_RANK_ALLOW_KICK,
						"Owner", 1);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.CLAN_RANK_ALLOW_KICK,
						"General+", 2);
				}
			});

			drawSelectButton(graphics, x + 9, y + 133, 184, 32, 14, 14, 1, 1, "Who can invite into clan?", mc.clan.getClanSettingByName(mc.clan.getClanSetting(1)), new ButtonHandler() {
				@Override
				void handle() {
					menuY = mc.mouseY - 7;
					menuX = mc.mouseX - x / 2;
					menu_visible = true;
					rightClickMenu.recalculateSize(0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.CLAN_RANK_ALLOW_INVITE,
						"Anyone", 0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.CLAN_RANK_ALLOW_INVITE,
						"Owner", 1);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.CLAN_RANK_ALLOW_INVITE,
						"General+", 2);
				}
			});

			drawSelectButton(graphics, x + 9, y + 131 + 39, 184, 32, 14, 14, 1, 1, "Accept clan requests?", mc.clan.getClanSearchSettingByName(), new ButtonHandler() {
				@Override
				void handle() {
					menuY = mc.mouseY - 7;
					menuX = mc.mouseX - x / 2;
					menu_visible = true;
					rightClickMenu.recalculateSize(0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.CLAN_ACCEPT_REQUESTS,
						"Anyone can join", 0);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.CLAN_ACCEPT_REQUESTS,
						"Invite only", 1);
					rightClickMenu.addCharacterItem_WithID(0,
						"", MenuItemAction.CLAN_ACCEPT_REQUESTS,
						"Closed", 2);
				}
			});

			graphics.drawWrappedCenteredString("Right-click on a box to change options.", x + 101, y + 214, 175, 0, 0xD9CD98, false);

			drawSubmitButton(graphics, x + 235, y + 54, 146, 28, 18, 1, "Invite to Clan", false, new ButtonHandler() {
				@Override
				void handle() {
					mc.showItemModX(InputXPrompt.clanInvite, InputXAction.INVITE_CLAN_PLAYER, true);
					mc.showUiTab = 0;
				}
			});
			graphics.drawLineHoriz(x + 210, y + 87, 194, 0x5F5147);
		} else {
			clanSetupPanel.show(clanName_field);
			clanSetupPanel.show(clanTag_field);
			graphics.drawBoxAlpha(x + 2, y + 48, width - 5, height - 37, 0x1D1915, 192); //5F5147
			graphics.drawBoxBorder(x + 2, width - 5, y + 48, height - 37, 0x5F5147);
			graphics.drawColoredStringCentered(width / 2 + x, "Choose a Clan Name between 2-16 characters in length.", 0xf1f1f1, 0, 2, y + 64);
			drawInputButton(graphics, x + 14, y + 75, 380, 42, 14, 1, "Clan Name:", false, new ButtonHandler() {
				@Override
				void handle() {
					clanSetupPanel.setFocus(clanName_field);
				}
			});
			graphics.drawColoredStringCentered(width / 2 + x, "Enter your Clan Tag between 2-5 characters in length.", 0xf1f1f1, 0, 2, y + 64 + 72);
			drawInputButton(graphics, x + 14, y + 147, 380, 42, 14, 1, "Clan Tag:", false, new ButtonHandler() {
				@Override
				void handle() {
					clanSetupPanel.setFocus(clanTag_field);
				}
			});
			drawSubmitButton(graphics, x + 132, y + 196, 142, 28, 18, 1, "Submit", false, new ButtonHandler() {
				@Override
				void handle() {
					sendCreateClan(clanSetupPanel.getControlText(clanName_field), clanSetupPanel.getControlText(clanTag_field));
				}
			});
		}
		clanSetupPanel.drawPanel();
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
					if (act == MenuItemAction.CLAN_PROMOTE) {
						if (clickedIndex == 1) {
							clickedIndex = 2;
						} else if (clickedIndex == 2) {
							clickedIndex = 1;
							String[] kickMessage = new String[]{"Are you sure you want to give " + mc.clan.username[selectedClanMate] + " the leadership?"};
							mc.clanKickPlayer = mc.clan.username[selectedClanMate];
							mc.showItemModX(kickMessage, InputXAction.CLAN_DELEGATE_LEADERSHIP, false);
							return;
						}
						sendClanRank(mc.clan.username[selectedClanMate], clickedIndex);
					} else if (act == MenuItemAction.CLAN_RANK_ALLOW_KICK) {
						sendClanSettings(0, clickedIndex);
					} else if (act == MenuItemAction.CLAN_RANK_ALLOW_INVITE) {
						sendClanSettings(1, clickedIndex);
					} else if (act == MenuItemAction.CLAN_ACCEPT_REQUESTS) {
						sendClanSettings(2, clickedIndex);
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
		clanActiveInterface = 1;
		selectedClanMate = -1;
		selectedClanInSearch = -1;
	}

	private void resetAfterCreation() {
		clanActiveInterface = 1;
		clanSetupPanel.setText(clanName_field, "");
		clanSetupPanel.setText(clanTag_field, "");
		clanSetupPanel.setText(clanSearch_field, "");
		clanSetupPanel.hide(clanSearch_field);
		clanSetupPanel.setFocus(-1);
	}

	private void sendCreateClan(String name, String tag) {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(11);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(0);
		getClient().packetHandler.getClientStream().writeBuffer1.putString(name);
		getClient().packetHandler.getClientStream().writeBuffer1.putString(tag);
		getClient().packetHandler.getClientStream().finishPacket();

		resetAfterCreation();
	}

	public void sendClanLeave() {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(11);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(1);
		getClient().packetHandler.getClientStream().finishPacket();

		resetAll();
	}

	private void sendClanAccept() {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(11);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(3);
		getClient().packetHandler.getClientStream().finishPacket();

		resetAll();
	}

	private void sendClanDecline() {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(11);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(4);
		getClient().packetHandler.getClientStream().finishPacket();

		resetAll();
	}

	private void sendClanRank(String playerName, int rank) {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(11);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(6);
		getClient().packetHandler.getClientStream().writeBuffer1.putString(playerName);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(rank);
		getClient().packetHandler.getClientStream().finishPacket();
	}

	private void sendClanSettings(int settingMode, int state) {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(11);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(7);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(settingMode);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(state);
		getClient().packetHandler.getClientStream().finishPacket();
	}

	public void sendClanSearch() {
		getClient().packetHandler.getClientStream().newPacket(199);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(11);
		getClient().packetHandler.getClientStream().writeBuffer1.putByte(8);
		getClient().packetHandler.getClientStream().finishPacket();
	}

	public void initializeInvite(String byPlayer, String byClan) {
		invitationBy = "@yel@" + byPlayer + " @whi@has sent you a clan invitation:";
		invitationByClan = byClan;
		if (invitationBy != null && invitationByClan != null) {
			this.setVisible(true);
			this.clanActiveInterface = 2;
		} else {
			this.setVisible(false);
		}
	}

	public mudclient getClient() {
		return mc;
	}

	public boolean keyDown(int key) {
		if (clanActiveInterface == 1) {
			if (clanSetupPanel.focusOn(clanName_field) || clanSetupPanel.focusOn(clanTag_field) || clanSetupPanel.focusOn(clanSearch_field)) {
				clanSetupPanel.keyPress(key);
				return true;
			}
		}
		return false;
	}

	public void resetClans() {
		readClans.clear();
	}

	public void addClan(int clanID, String clanName, String clanTag, int members, int canJoin, int clanPoints, int clanRank) {
		readClans.add(new ClanResult(clanID, clanName, clanTag, members, canJoin, clanPoints, clanRank));
	}

	abstract class ButtonHandler {
		abstract void handle();
	}

	class ClanResult {
		private String clanName, clanTag;
		private int membersTotal, clanPoints, canJoin, clanID, clanRank;
		private String[] clanSearchSettings = {"@gr2@Anyone can join", "@yel@Invite only", "@red@Closed"};

		ClanResult(int clanID, String clanName, String clanTag, int members, int canJoin, int clanPoints, int clanRank) {
			this.clanID = clanID;
			this.clanName = clanName;
			this.clanTag = clanTag;
			this.membersTotal = members;
			this.canJoin = canJoin;
			this.clanPoints = clanPoints;
			this.clanRank = clanRank;
		}

		int getClanGlobalRank() {
			return clanRank;
		}

		String getClanSearchSettingByName(int i) {
			return clanSearchSettings[i];
		}

		int getClanPoints() {
			return clanPoints;
		}

		public int getClanID() {
			return clanID;
		}

		public String getClanName() {
			return clanName;
		}

		public void setClanName(String name) {
			this.clanName = name;
		}

		public String getClanTag() {
			return clanTag;
		}

		public void setClanTag(String tag) {
			this.clanTag = tag;
		}

		int getClanMembersTotal() {
			return membersTotal;
		}
	}

}
