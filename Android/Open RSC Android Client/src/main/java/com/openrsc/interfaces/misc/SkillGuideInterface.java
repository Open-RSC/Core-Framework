package com.openrsc.interfaces.misc;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;
import orsc.graphics.gui.Panel;
import orsc.graphics.two.GraphicsController;
import orsc.mudclient;

import java.util.ArrayList;


public final class SkillGuideInterface {
	public int curTab = 0;
	public int skillGuideScroll;
	public Panel skillGuide;
	int width = 430;
	int height = 320;
	int autoHeight = 0;
	// Different y values used for larger skill guides with more tabs
	boolean largeSkillGuide = false;
	private ArrayList<SkillItem> skillItems;
	private boolean visible = false;
	private mudclient mc;
	private int panelColour, textColour, bordColour;
	private int x, y;

	public SkillGuideInterface(mudclient mc) {
		this.mc = mc;

		skillGuide = new Panel(mc.getSurface(), 15);

		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		skillItems = new ArrayList<SkillItem>();

		skillGuideScroll = skillGuide.addScrollingList2(x + 4, y + 79, width - 5, height - 77, 100, 7, true);
	}

	public void reposition() {
		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		skillGuide.reposition(skillGuideScroll, x + 4, y + 81, width - 5, height - 82);
	}

	public void onRender(GraphicsController graphics) {
		reposition();
		int x = (mc.getGameWidth() - width) / 2;
		int y = (mc.getGameHeight() - height) / 2;

		panelColour = 0x989898;
		textColour = 0xffffff;
		bordColour = 0x000000;

		skillGuide.handleMouse(mc.getMouseX(), mc.getMouseY(), mc.getMouseButtonDown(), mc.getLastMouseDown());

		// Draws the background
		mc.getSurface().drawBoxAlpha(x, y, width, autoHeight - y, panelColour, 160);
		mc.getSurface().drawBoxBorder(x, width, y, autoHeight - y, bordColour);

		// Draws the title
		if (mc.skillGuideChosenTabs.size() <= 4) {
			largeSkillGuide = false;
			drawStringCentered(mc.getSkillGuideChosen(), x, y + 28, 5, textColour);
		} else {
			largeSkillGuide = true;
			drawStringCentered(mc.getSkillGuideChosen(), x, y + 20, 5, textColour);
		}

		this.drawButton(x + 394, y + 6, 30, 30, "X", 5, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.packetHandler.getClientStream().newPacket(212);
				mc.packetHandler.getClientStream().finishPacket();
				skillGuide.resetScrollIndex(skillGuideScroll);
				curTab = 0;
				setVisible(false);
			}
		});

		int tabDrawX = 0;
		int tabDrawY = 0;
		if (largeSkillGuide) {
			tabDrawX = 220 - (45 * 4);
			tabDrawY = 27;
		} else {
			tabDrawX = 220 - (45 * mc.skillGuideChosenTabs.size());
			tabDrawY = 45;
		}
		int tabDrawXDiff = 75;
		int tabDrawYDiff = 20;

		// Draws the tab pickers
		for (int i = 0; i < mc.skillGuideChosenTabs.size(); i++) {
			// Starts new row of tabs
			if (i == 4) {
				tabDrawY += 25;
				tabDrawX = 220 - (45 * (mc.skillGuideChosenTabs.size() - i));
			}
			this.drawTab(x + tabDrawX, y + tabDrawY, tabDrawXDiff, tabDrawYDiff, mc.skillGuideChosenTabs.get(i), 1);
			tabDrawX += tabDrawXDiff + 10;
		}

		mc.getSurface().drawLineHoriz(x + 1, y + 81, width - 2, 0);
		mc.getSurface().drawBoxAlpha(x + 1, y + 82, width - 2, 16, 0x6580B7, 192);

		mc.getSurface().drawString("Level", x + 5, y + 94, 0xffffff, 2);
		//mc.getSurface().drawString("Item", x + 5 + 35, y + 94, 0xffffff, 2);
		mc.getSurface().drawString("Advancement", x + 5 + 80, y + 94, 0xffffff, 2);

		drawSkillItems();
	}

	public void drawSkillItems() {
		int x = (mc.getGameWidth() - width) / 2;
		int y = (mc.getGameHeight() - height) / 2;

		// Gets all items in the list for what skill was chosen
		populateSkillItems();

		// Sets up scroll
		skillGuide.clearList(skillGuideScroll);
		for (int i = -1; i <= skillItems.size(); i++) {
			skillGuide.setListEntry(skillGuideScroll, i + 1, "", 0, (String) null, (String) null);
		}

		int listStartPoint = skillGuide.getScrollPosition(skillGuideScroll);
		int listEndPoint = listStartPoint + 5;

		int levelX = x + 10;
		int spriteX = levelX + 15;
		int detailX = spriteX + 50;
		int allY = 0;
		allY = y + 82 + 16;

		for (int i = -1; i < skillItems.size(); i++) {
			if (i >= 100) {
				break;
			}

			if (i < listStartPoint || i > listEndPoint)
				continue;

			SkillItem curItem = skillItems.get(i);
			ItemDef def = EntityHandler.getItemDef(curItem.getItemID());
			String levelReq = curItem.getLevelReq();
			String skillDetail = curItem.getSkillDetail();

			mc.getSurface().drawBoxAlpha(detailX - 75, allY, width, 37, 0x45454545, 90);
			drawString(levelReq, levelX, allY + 25, 2, textColour);

			//mc.getSurface().drawLineHoriz(detailX - 75, allY, width, 0);
			if (i != skillItems.size() - 1 && i != listEndPoint) {
				mc.getSurface().drawBoxBorder(detailX - 75, width, allY, 37 + 1, 0);
			}
			mc.getSurface().drawSpriteClipping(mc.spriteSelect(def),
					spriteX + 5, allY + 2, 48, 32, def.getPictureMask(), 0, false, 0, 1);

			drawString(skillDetail, detailX + 10, allY + 25, 2, textColour);

			allY += 37;
		}
		autoHeight = allY;

		skillGuide.drawPanel();
	}

	public void changeTab(int tabNum) {
		curTab = tabNum;
		skillGuide.resetScrollIndex(skillGuideScroll);
		drawSkillItems();
	}

	public void drawString(String str, int x, int y, int font, int color) {
		mc.getSurface().drawString(str, x, y, color, font);
	}

	public void drawStringCentered(String str, int x, int y, int font, int color) {
		int stringWid = mc.getSurface().stringWidth(font, str);
		mc.getSurface().drawShadowText(str, x + (width / 2) - (stringWid / 2) - 2, y, color, font, false);
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

	// Used for drawing tabs
	// Keeps track of current tab and tab hovered over
	private void drawTab(int x, int y, int width, int height, String text, int font) {
		int bgBtnColour = 0x333333; // grey
		boolean current = mc.skillGuideChosenTabs.get(curTab).equals(text);
		if (current) {
			bgBtnColour = 0x659CDE; // red
		} else if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			bgBtnColour = 0x6580B7; // blue
			if (mc.getMouseClick() == 1) {
				for (int i = 0; i < mc.skillGuideChosenTabs.size(); i++) {
					if (mc.skillGuideChosenTabs.get(i) == text) {
						changeTab(i);
					}
				}
				mc.setMouseClick(0);
			}
		}
		mc.getSurface().drawBoxAlpha(x, y, width, height, bgBtnColour, 192);
		mc.getSurface().drawBoxBorder(x, width, y, height, 0x242424);
		mc.getSurface().drawString(text, x + (width / 2) - (mc.getSurface().stringWidth(font, text) / 2), y + height / 2 + 5, textColour, font);
	}

	public void populateSkillItems() {
		skillItems.clear();
		if (mc.getSkillGuideChosen().equals("Attack")) {
			skillItems.add(new SkillItem(66, "1", "Bronze"));
			skillItems.add(new SkillItem(1, "1", "Iron"));
			skillItems.add(new SkillItem(67, "5", "Steel"));
			skillItems.add(new SkillItem(424, "10", "Black"));
			skillItems.add(new SkillItem(68, "20", "Mithril"));
			skillItems.add(new SkillItem(69, "30", "Adamantite"));
			skillItems.add(new SkillItem(617, "30", "Battlestaves"));
			skillItems.add(new SkillItem(397, "40", "Rune"));
			skillItems.add(new SkillItem(684, "40", "Enchanted battlestaves"));
			skillItems.add(new SkillItem(1000, "50", "Staff of Iban"));
			skillItems.add(new SkillItem(593, "60", "Dragon"));
		}
		if (mc.getSkillGuideChosen().equals("Defense")) {
			skillItems.add(new SkillItem(128, "1", "Bronze"));
			skillItems.add(new SkillItem(2, "1", "Iron"));
			skillItems.add(new SkillItem(129, "5", "Steel"));
			skillItems.add(new SkillItem(433, "10", "Black"));
			skillItems.add(new SkillItem(130, "20", "Mithril"));
			skillItems.add(new SkillItem(131, "30", "Adamantite"));
			skillItems.add(new SkillItem(404, "40", "Rune"));
			skillItems.add(new SkillItem(1278, "60", "Dragon"));
		}
		if (mc.getSkillGuideChosen().equals("Strength")) {
			skillItems.add(new SkillItem(90, "", "Strength raises your max hit with melee"));
		}
		if (mc.getSkillGuideChosen().equals("Hits")) {
			skillItems.add(new SkillItem(193, "", "Beer - Heals 1"));
			skillItems.add(new SkillItem(350, "", "Shrimp - Heals 2"));
			skillItems.add(new SkillItem(352, "", "Anchovies - Heals 2"));
			skillItems.add(new SkillItem(249, "", "Banana - Heals 2"));
			skillItems.add(new SkillItem(132, "", "Cooked Meat - Heals 3"));
			skillItems.add(new SkillItem(355, "", "Sardine - Heals 4"));
			skillItems.add(new SkillItem(138, "", "Bread - Heals 4"));
			skillItems.add(new SkillItem(362, "", "Herring - Heals 5"));
			skillItems.add(new SkillItem(718, "", "Giant Carp - Heals 6"));
			skillItems.add(new SkillItem(553, "", "Mackerel - Heals 6"));
			skillItems.add(new SkillItem(258, "", "Redberry Pie - Heals 6"));
			skillItems.add(new SkillItem(359, "", "Trout - Heals 7"));
			skillItems.add(new SkillItem(551, "", "Cod - Heals 7"));
			skillItems.add(new SkillItem(364, "", "Pike - Heals 8"));
			skillItems.add(new SkillItem(259, "", "Meat Pie - Heals 8"));
			skillItems.add(new SkillItem(1269, "", "Oomlie Meat Parcel - Heals 8"));
			skillItems.add(new SkillItem(357, "", "Salmon - Heals 9"));
			skillItems.add(new SkillItem(346, "", "Stew - Heals 9"));
			skillItems.add(new SkillItem(367, "", "Tuna - Heals 10"));
			skillItems.add(new SkillItem(325, "", "Plain Pizza - Heals 10"));
			skillItems.add(new SkillItem(257, "", "Apple Pie - Heals 10"));
			skillItems.add(new SkillItem(142, "", "Wine - Heals 11"));
			skillItems.add(new SkillItem(373, "", "Lobster - Heals 12"));
			skillItems.add(new SkillItem(330, "", "Cake - Heals 12"));
			skillItems.add(new SkillItem(555, "", "Bass - Heals 13"));
			skillItems.add(new SkillItem(370, "", "Swordfish - Heals 14"));
			skillItems.add(new SkillItem(590, "", "Lava Eel - Heals 14"));
			skillItems.add(new SkillItem(326, "", "Meat Pizza - Heals 14"));
			skillItems.add(new SkillItem(332, "", "Chocolate Cake - Heals 15"));
			skillItems.add(new SkillItem(327, "", "Anchovie Pizza - Heals 16"));
			skillItems.add(new SkillItem(709, "", "Curry - Heals 19"));
			skillItems.add(new SkillItem(1102, "", "Tasty Ugthanki Kebab - Heals 19"));
			skillItems.add(new SkillItem(546, "", "Shark - Heals 20"));
			skillItems.add(new SkillItem(750, "", "Pineapple Pizza - Heals 20"));
			skillItems.add(new SkillItem(1193, "", "Sea Turtle - Heals 21"));
			skillItems.add(new SkillItem(1191, "", "Manta Ray - Heals 22"));
		}
		if (mc.getSkillGuideChosen().equals("Ranged")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(189, "1", "Shortbow"));
				skillItems.add(new SkillItem(188, "1", "Longbow"));
				skillItems.add(new SkillItem(649, "5", "Oak shortbow"));
				skillItems.add(new SkillItem(648, "10", "Oak longbow"));
				skillItems.add(new SkillItem(651, "15", "Willow shortbow"));
				skillItems.add(new SkillItem(650, "20", "Willow longbow"));
				skillItems.add(new SkillItem(653, "25", "Maple shortbow"));
				skillItems.add(new SkillItem(652, "30", "Maple longbow"));
				skillItems.add(new SkillItem(655, "35", "Yew shortbow"));
				skillItems.add(new SkillItem(654, "40", "Yew longbow"));
				skillItems.add(new SkillItem(657, "45", "Magic shortbow"));
				skillItems.add(new SkillItem(656, "50", "Magic longbow"));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(60, "1", "Crossbow"));
				skillItems.add(new SkillItem(59, "1", "Phoenix Crossbow"));
			} else if (curTab == 2) {
				skillItems.add(new SkillItem(1080, "1", "All throwing knives"));
				skillItems.add(new SkillItem(1013, "1", "Bronze darts & spears"));
				skillItems.add(new SkillItem(1015, "1", "Iron darts & spears"));
				skillItems.add(new SkillItem(1024, "5", "Steel darts & spears"));
				skillItems.add(new SkillItem(1068, "20", "Mithril darts & spears"));
				skillItems.add(new SkillItem(1069, "30", "Adamant darts & spears"));
				skillItems.add(new SkillItem(1070, "40", "Rune darts & spears"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Prayer")) {
			skillItems.add(new SkillItem(44, "1", "Thick skin - Increases your defense by 5%"));
			skillItems.add(new SkillItem(44, "4", "Burst of strength - Increases your strength by 5%"));
			skillItems.add(new SkillItem(44, "7", "Clarity of thought - Increases your attack by 5%"));
			skillItems.add(new SkillItem(44, "10", "Rock skin - Increases your defense by 10%"));
			skillItems.add(new SkillItem(44, "13", "Superhuman strength - Increases your strength by 10%"));
			skillItems.add(new SkillItem(44, "16", "Improved reflexes + Increases your attack by 10%"));
			skillItems.add(new SkillItem(44, "19", "Rapid restore - 2x restore rate for all stats except hits"));
			skillItems.add(new SkillItem(44, "22", "Rapid heal - 2x restore rate for hitpoints stat"));
			skillItems.add(new SkillItem(44, "25", "Protect items - Keep 1 extra item if you die"));
			skillItems.add(new SkillItem(44, "28", "Steel skin - Increases your defense by 15%"));
			skillItems.add(new SkillItem(44, "31", "Ultimate strength - Increases your strength by 15%"));
			skillItems.add(new SkillItem(44, "34", "Incredible reflexes - Increases your attack by 15%"));
			skillItems.add(new SkillItem(44, "37", "Paralyze monster - Stops monsters from fighting back"));
			skillItems.add(new SkillItem(44, "40", "Protect from missiles - 100% protection from ranged attacks"));
		}
		if (mc.getSkillGuideChosen().equals("Magic")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(35, "1", "Wind strike - A strength 1 missile attack"));
				skillItems.add(new SkillItem(36, "3", "Confuse - Reduces your opponents attack by 5%"));
				skillItems.add(new SkillItem(35, "5", "Water strike - A strength 2 missile attack"));
				skillItems.add(new SkillItem(46, "7", "Enchant lvl-1 amulet - For use on sapphire amulets"));
				skillItems.add(new SkillItem(35, "9", "Earth strike - A strength 3 missile attack"));
				skillItems.add(new SkillItem(36, "11", "Weaken - Reduces your opponents strength by 5%"));
				skillItems.add(new SkillItem(35, "13", "Fire strike - A strength 4 missile attack"));
				skillItems.add(new SkillItem(40, "15", "Bones to bananas - Changes all held bones into bananas!"));
				skillItems.add(new SkillItem(41, "17", "Wind Bolt - A strength 5 missile attack"));
				skillItems.add(new SkillItem(36, "19", "Curse - Reduces your opponents defense by 5%"));
				skillItems.add(new SkillItem(40, "21", "Low level alchemy - Converts an item into gold"));
				skillItems.add(new SkillItem(41, "23", "Water Bolt - A strength 6 missile attack"));
				skillItems.add(new SkillItem(42, "25", "Varrock teleport - Teleports you to Varrock"));
				skillItems.add(new SkillItem(46, "27", "Enchant lvl-2 amulet - For use on emerald amulets"));
				skillItems.add(new SkillItem(41, "29", "Earth Bolt - A strength 7 missile attack"));
				skillItems.add(new SkillItem(42, "31", "Lumbridge teleport - Teleports you to Lumbridge"));
				skillItems.add(new SkillItem(42, "33", "Telekinetic grab - Take an item you can see but can't reach"));
				skillItems.add(new SkillItem(41, "35", "Fire Bolt - A strength 8 missile attack"));
				skillItems.add(new SkillItem(42, "37", "Falador teleport - Teleports you to Falador"));
				skillItems.add(new SkillItem(41, "39", "Crumble undead - Hits skeleton, ghosts & zombies hard!"));
				skillItems.add(new SkillItem(38, "41", "Wind blast - A strength 9 missile attack"));
				skillItems.add(new SkillItem(40, "43", "Superheat item - Smelt 1 ore without a furnace"));
				skillItems.add(new SkillItem(42, "45", "Camelot teleport - Teleports you to Camelot"));
				skillItems.add(new SkillItem(38, "47", "Water blast - A strength 10 missile attack"));
				skillItems.add(new SkillItem(46, "49", "Enchant lvl-3 amulet - For use on ruby amulets"));
				skillItems.add(new SkillItem(38, "50", "Iban blast - A strength 25 missile attack!"));
				skillItems.add(new SkillItem(42, "51", "Ardougne teleport - Teleports you to Ardougne"));
				skillItems.add(new SkillItem(38, "53", "Earth blast - A strength 11 missile attack"));
				skillItems.add(new SkillItem(40, "55", "High level alchemy - Converts an item into more gold"));
				skillItems.add(new SkillItem(46, "56", "Charge water orb - Needs to be cast on a water obelisk"));
				skillItems.add(new SkillItem(46, "57", "Enchant lvl-4 amulet - For use on diamond amulets"));
				skillItems.add(new SkillItem(42, "58", "Watchtower teleport - Teleports you to the watchtower"));
				skillItems.add(new SkillItem(38, "59", "Fire blast - A strength 12 missile attack"));
				skillItems.add(new SkillItem(46, "60", "Charge earth orb - Needs to be cast on a earth obelisk"));
				skillItems.add(new SkillItem(619, "60", "Claws of Guthix - Summons the power of Guthix"));
				skillItems.add(new SkillItem(619, "60", "Saradomin strike - Summons the power of Saradomin"));
				skillItems.add(new SkillItem(619, "60", "Flames of Zamorak - Summons the power of Zamorak"));
				skillItems.add(new SkillItem(619, "62", "Wind wave - A strength 13 missile attack"));
				skillItems.add(new SkillItem(46, "63", "Charge fire orb - Needs to be cast on a fire obelisk"));
				skillItems.add(new SkillItem(619, "65", "Water wave - A strength 14 missile attack"));
				skillItems.add(new SkillItem(825, "66", "Vulnerability - Reduces your opponents defense by 10%"));
				skillItems.add(new SkillItem(46, "66", "Charge air orb - Needs to be cast on a air obelisk"));
				skillItems.add(new SkillItem(46, "68", "Enchant lvl-5 amulet - For use on dragonstone amulets"));
				skillItems.add(new SkillItem(619, "70", "Earth wave - A strength 15 missile attack"));
				skillItems.add(new SkillItem(825, "73", "Enfeeble - Reduces your opponents strength by 10%"));
				skillItems.add(new SkillItem(619, "75", "Fire wave - A strength 16 missile attack"));
				skillItems.add(new SkillItem(825, "80", "Stun - Reduces your opponents attack by 10%"));
				skillItems.add(new SkillItem(619, "80", "Charge - Increase your mage arena spells damage"));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(184, "1", "Wizard hats and robes"));
				skillItems.add(new SkillItem(702, "1", "Robes of Zamorak"));
				skillItems.add(new SkillItem(1215, "60", "God capes"));
			} else if (curTab == 2) {
				skillItems.add(new SkillItem(101, "1", "Basic staves"));
				skillItems.add(new SkillItem(617, "30", "Battlestaves"));
				skillItems.add(new SkillItem(684, "40", "Enchanted battlestaves"));
				skillItems.add(new SkillItem(1000, "50", "Staff of Iban"));
				skillItems.add(new SkillItem(1218, "60", "God staves"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Cooking")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(132, "1", "Cooked Meat"));
				skillItems.add(new SkillItem(350, "1", "Shrimp"));
				skillItems.add(new SkillItem(352, "1", "Anchovies"));
				skillItems.add(new SkillItem(355, "1", "Sardine"));
				skillItems.add(new SkillItem(362, "5", "Herring"));
				skillItems.add(new SkillItem(718, "10", "Giant Carp"));
				skillItems.add(new SkillItem(553, "10", "Mackerel"));
				skillItems.add(new SkillItem(359, "15", "Trout"));
				skillItems.add(new SkillItem(551, "18", "Cod"));
				skillItems.add(new SkillItem(364, "20", "Pike"));
				skillItems.add(new SkillItem(357, "25", "Salmon"));
				skillItems.add(new SkillItem(367, "30", "Tuna"));
				skillItems.add(new SkillItem(373, "40", "Lobster"));
				skillItems.add(new SkillItem(555, "43", "Bass"));
				skillItems.add(new SkillItem(370, "45", "Swordfish"));
				skillItems.add(new SkillItem(590, "53", "Lava Eel"));
				skillItems.add(new SkillItem(546, "80", "Shark"));
				skillItems.add(new SkillItem(1193, "82", "Sea Turtle"));
				skillItems.add(new SkillItem(1191, "91", "Manta Ray"));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(325, "35", "Plain Pizza"));
				skillItems.add(new SkillItem(326, "45", "Meat Pizza"));
				skillItems.add(new SkillItem(327, "55", "Anchovie Pizza"));
				skillItems.add(new SkillItem(750, "65", "Pineapple Pizza"));
			} else if (curTab == 2) {
				skillItems.add(new SkillItem(258, "10", "Redberry Pie"));
				skillItems.add(new SkillItem(259, "20", "Meat Pie"));
				skillItems.add(new SkillItem(257, "30", "Apple Pie"));
			} else if (curTab == 3) {
				skillItems.add(new SkillItem(346, "25", "Stew"));
				skillItems.add(new SkillItem(709, "60", "Curry"));
			} else if (curTab == 4) {
				skillItems.add(new SkillItem(138, "1", "Bread"));
				skillItems.add(new SkillItem(1105, "58", "Pitta Bread"));
			} else if (curTab == 5) {
				skillItems.add(new SkillItem(330, "40", "Cake"));
				skillItems.add(new SkillItem(332, "50", "Chocolate Cake"));
			} else if (curTab == 6) {
				skillItems.add(new SkillItem(833, "1", "Cocktails"));
				skillItems.add(new SkillItem(911, "15", "Choc Crunchies"));
				skillItems.add(new SkillItem(912, "15", "Worm Crunchies"));
				skillItems.add(new SkillItem(913, "15", "Toad Crunchies"));
				skillItems.add(new SkillItem(914, "15", "Spice Crunchies"));
				skillItems.add(new SkillItem(901, "25", "Cheese and Tomato Batta"));
				skillItems.add(new SkillItem(902, "25", "Toad Batta"));
				skillItems.add(new SkillItem(904, "25", "Worm Batta"));
				skillItems.add(new SkillItem(905, "25", "Fruit Batta"));
				skillItems.add(new SkillItem(906, "25", "Veg Batta"));
				skillItems.add(new SkillItem(907, "30", "Chocolate Bomb"));
				skillItems.add(new SkillItem(908, "30", "Vegball"));
				skillItems.add(new SkillItem(909, "30", "Worm Hole"));
				skillItems.add(new SkillItem(910, "30", "Tangled Toads Legs"));
			} else if (curTab == 7) {
				skillItems.add(new SkillItem(142, "35", "Wine"));
				skillItems.add(new SkillItem(1269, "50", "Oomlie Meat Parcel"));
				skillItems.add(new SkillItem(1102, "58", "Tasty Ugthanki Kebab"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Woodcutting")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(14, "1", "Trees"));
				skillItems.add(new SkillItem(632, "15", "Oak trees"));
				skillItems.add(new SkillItem(633, "30", "Willow trees"));
				skillItems.add(new SkillItem(634, "45", "Maple trees"));
				skillItems.add(new SkillItem(635, "60", "Yew trees"));
				skillItems.add(new SkillItem(636, "75", "Magic trees"));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(405, "1", "All axes"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Fletching")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(11, "1", "Bronze Arrows"));
				skillItems.add(new SkillItem(638, "15", "Iron Arrows"));
				skillItems.add(new SkillItem(640, "30", "Steel Arrows"));
				skillItems.add(new SkillItem(786, "34", "Oyster pearl bolts"));
				skillItems.add(new SkillItem(642, "45", "Mithril Arrows"));
				skillItems.add(new SkillItem(644, "60", "Adamantite Arrows"));
				skillItems.add(new SkillItem(646, "75", "Rune Arrows"));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(189, "5", "Shortbows"));
				skillItems.add(new SkillItem(188, "10", "Longbows"));
				skillItems.add(new SkillItem(649, "20", "Oak shortbows"));
				skillItems.add(new SkillItem(648, "25", "Oak longbows"));
				skillItems.add(new SkillItem(651, "35", "Willow shortbows"));
				skillItems.add(new SkillItem(650, "40", "Willow longbows"));
				skillItems.add(new SkillItem(653, "50", "Maple shortbows"));
				skillItems.add(new SkillItem(652, "55", "Maple longbows"));
				skillItems.add(new SkillItem(655, "65", "Yew shortbows"));
				skillItems.add(new SkillItem(654, "70", "Yew longbows"));
				skillItems.add(new SkillItem(657, "80", "Magic shortbows"));
				skillItems.add(new SkillItem(656, "85", "Magic longbows"));
			} else if (curTab == 2) {
				skillItems.add(new SkillItem(1013, "1", "Bronze throwing dart"));
				skillItems.add(new SkillItem(1015, "22", "Iron throwing dart"));
				skillItems.add(new SkillItem(1024, "37", "Steel throwing dart"));
				skillItems.add(new SkillItem(1068, "52", "Mithril throwing dart"));
				skillItems.add(new SkillItem(1069, "67", "Adamantite throwing dart"));
				skillItems.add(new SkillItem(1070, "82", "Rune throwing dart"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Fishing")) {
			skillItems.add(new SkillItem(349, "1", "Shrimp - Small Fishing Net"));
			skillItems.add(new SkillItem(354, "5", "Sardine - Fishing Rod and Bait"));
			skillItems.add(new SkillItem(717, "10", "Giant Carp - Fishing Rod and Red vine worms"));
			skillItems.add(new SkillItem(361, "10", "Herring - Fishing Rod and Bait"));
			skillItems.add(new SkillItem(351, "15", "Anchovies - Small Fishing Net"));
			skillItems.add(new SkillItem(552, "16", "Mackerel - Big Fishing Net"));
			skillItems.add(new SkillItem(358, "20", "Trout - Fly Fishing Rod and Feathers"));
			skillItems.add(new SkillItem(550, "23", "Cod - Big Fishing Net"));
			skillItems.add(new SkillItem(363, "25", "Pike - Fishing Rod and Bait"));
			skillItems.add(new SkillItem(356, "30", "Salmon - Fly Fishing Rod and Feathers"));
			skillItems.add(new SkillItem(366, "35", "Tuna - Harpoon"));
			skillItems.add(new SkillItem(372, "40", "Lobster - Lobster Pot"));
			skillItems.add(new SkillItem(554, "46", "Bass - Big Fishing Net"));
			skillItems.add(new SkillItem(369, "50", "Swordfish - Harpoon"));
			skillItems.add(new SkillItem(545, "76", "Sharks - Harpoon"));
			skillItems.add(new SkillItem(1192, "79", "Sea Turtle - Fishing Trawler"));
			skillItems.add(new SkillItem(1190, "81", "Manta Ray - Fishing Trawler"));
		}
		if (mc.getSkillGuideChosen().equals("Firemaking")) {
			skillItems.add(new SkillItem(14, "1", "Normal logs"));
		}
		if (mc.getSkillGuideChosen().equals("Crafting")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(16, "1", "Leather Gloves"));
				skillItems.add(new SkillItem(17, "7", "Boots"));
				skillItems.add(new SkillItem(15, "14", "Leather Armour"));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(135, "1", "Pot"));
				skillItems.add(new SkillItem(251, "4", "Pie Dish"));
				skillItems.add(new SkillItem(341, "7", "Bowl"));
			} else if (curTab == 2) {
				skillItems.add(new SkillItem(894, "10", "Opal"));
				skillItems.add(new SkillItem(893, "13", "Jade"));
				skillItems.add(new SkillItem(892, "16", "Red Topaz"));
				skillItems.add(new SkillItem(164, "20", "Sapphire"));
				skillItems.add(new SkillItem(163, "27", "Emerald"));
				skillItems.add(new SkillItem(162, "34", "Ruby"));
				skillItems.add(new SkillItem(161, "43", "Diamond"));
				skillItems.add(new SkillItem(523, "55", "Dragonstone"));
			} else if (curTab == 3) {
				skillItems.add(new SkillItem(283, "5", "Gold Ring"));
				skillItems.add(new SkillItem(288, "6", "Gold Necklace"));
				skillItems.add(new SkillItem(296, "8", "Gold Amulet"));
				skillItems.add(new SkillItem(284, "8", "Sapphire Ring"));
				skillItems.add(new SkillItem(289, "10", "Sapphire Necklace"));
				skillItems.add(new SkillItem(297, "13", "Sapphire Amulet"));
				skillItems.add(new SkillItem(44, "16", "Holy Symbol"));
				skillItems.add(new SkillItem(1027, "16", "Unholy Symbol"));
				skillItems.add(new SkillItem(285, "18", "Emerald Ring"));
				skillItems.add(new SkillItem(290, "24", "Emerald Necklace"));
				skillItems.add(new SkillItem(298, "30", "Emerald Amulet"));
				skillItems.add(new SkillItem(286, "30", "Ruby Ring"));
				skillItems.add(new SkillItem(291, "40", "Ruby Necklace"));
				skillItems.add(new SkillItem(287, "42", "Diamond Ring"));
				skillItems.add(new SkillItem(299, "50", "Ruby Amulet"));
				skillItems.add(new SkillItem(543, "54", "Dragonstone Ring"));
				skillItems.add(new SkillItem(292, "56", "Diamond Necklace"));
				skillItems.add(new SkillItem(300, "70", "Diamond Amulet"));
				skillItems.add(new SkillItem(544, "72", "Dragonstone Necklace"));
				skillItems.add(new SkillItem(524, "80", "Dragonstone Amulet"));
			} else if (curTab == 4) {
				skillItems.add(new SkillItem(207, "1", "Ball of Wool"));
				skillItems.add(new SkillItem(676, "10", "Bow String"));
			} else if (curTab == 5) {
				skillItems.add(new SkillItem(623, "1", "Molten Glass"));
				skillItems.add(new SkillItem(620, "1", "Beer Glass"));
				skillItems.add(new SkillItem(1018, "10", "Lens"));
				skillItems.add(new SkillItem(465, "33", "Vial"));
				skillItems.add(new SkillItem(611, "46", "Orb"));
			} else if (curTab == 6) {
				skillItems.add(new SkillItem(616, "54", "Battlestaff of Water"));
				skillItems.add(new SkillItem(618, "58", "Battlestaff of Earth"));
				skillItems.add(new SkillItem(615, "62", "Battlestaff of Fire"));
				skillItems.add(new SkillItem(617, "66", "Battlestaff of Air"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Smithing")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(169, "1", "Bronze - 1 Tin ore & 1 copper ore"));
				skillItems.add(new SkillItem(170, "15", "Iron - 1 Iron ore - 50% Chance of success"));
				skillItems.add(new SkillItem(384, "20", "Silver -  1 Silver ore"));
				skillItems.add(new SkillItem(171, "30", "Steel - 2 Coal and 1 iron ore"));
				skillItems.add(new SkillItem(172, "40", "Gold -  1 Gold ore"));
				skillItems.add(new SkillItem(173, "50", "Mithril - 4 Coal & 1 mithril ore"));
				skillItems.add(new SkillItem(174, "70", "Adamant - 6 Coal & 1 adamantite ore"));
				skillItems.add(new SkillItem(408, "85", "Runite - 8 Coal & 1 runite ore"));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(62, "1", "Bronze Daggers - 1 Bar"));
				skillItems.add(new SkillItem(87, "1", "Bronze Axes - 1 Bar"));
				skillItems.add(new SkillItem(94, "2", "Bronze Maces - 1 Bar"));
				skillItems.add(new SkillItem(104, "3", "Bronze Medium Helms - 1 Bar"));
				skillItems.add(new SkillItem(66, "4", "Bronze Short Swords - 1 Bar"));
				skillItems.add(new SkillItem(1062, "4", "Bronze Dart Tips - 1 Bar makes 7"));
				skillItems.add(new SkillItem(979, "4", "Bronze Wire - 1 Bar"));
				skillItems.add(new SkillItem(82, "5", "Bronze Scimitars - 2 Bars"));
				skillItems.add(new SkillItem(669, "5", "Bronze Arrowheads - 1 Bar makes 10"));
				skillItems.add(new SkillItem(70, "6", "Bronze Longswords - 2 Bars"));
				skillItems.add(new SkillItem(108, "7", "Bronze Full Helms - 2 Bars"));
				skillItems.add(new SkillItem(1076, "7", "Bronze Throwing Knives - 1 Bar makes 2"));
				skillItems.add(new SkillItem(124, "8", "Bronze Square Shields - 2 Bars"));
				skillItems.add(new SkillItem(205, "10", "Bronze Battleaxes - 3 Bars"));
				skillItems.add(new SkillItem(113, "11", "Bronze Chainbodies - 3 Bars"));
				skillItems.add(new SkillItem(128, "12", "Bronze Kiteshields - 3 Bars"));
				skillItems.add(new SkillItem(76, "14", "Bronze Two-handed Swords - 3 Bars"));
				skillItems.add(new SkillItem(214, "16", "Bronze Plated Skirts - 3 Bars"));
				skillItems.add(new SkillItem(206, "16", "Bronze Platelegs - 3 Bars"));
				skillItems.add(new SkillItem(117, "18", "Bronze Platebodies - 5 Bars"));
			} else if (curTab == 2) {
				skillItems.add(new SkillItem(28, "15", "Iron Daggers - 1 Bar"));
				skillItems.add(new SkillItem(12, "16", "Iron Axes - 1 Bar"));
				skillItems.add(new SkillItem(0, "17", "Iron Maces - 1 Bar"));
				skillItems.add(new SkillItem(5, "18", "Iron Medium Helms - 1 Bar"));
				skillItems.add(new SkillItem(1, "19", "Iron Short Swords - 1 Bar"));
				skillItems.add(new SkillItem(1063, "19", "Iron Dart Tips - 1 Bar makes 7"));
				skillItems.add(new SkillItem(83, "20", "Iron Scimitars - 2 Bars"));
				skillItems.add(new SkillItem(670, "20", "Iron Arrowheads - 1 Bar makes 10"));
				skillItems.add(new SkillItem(71, "21", "Iron Longswords - 2 Bars"));
				skillItems.add(new SkillItem(6, "22", "Iron Full Helms - 2 Bars"));
				skillItems.add(new SkillItem(1075, "22", "Iron Throwing Knives - 1 Bar makes 2"));
				skillItems.add(new SkillItem(3, "23", "Iron Square Shields - 2 Bars"));
				skillItems.add(new SkillItem(89, "25", "Iron Battleaxes - 3 Bars"));
				skillItems.add(new SkillItem(7, "26", "Iron Chainbodies - 3 Bars"));
				skillItems.add(new SkillItem(2, "27", "Iron Kiteshields - 3 Bars"));
				skillItems.add(new SkillItem(77, "29", "Iron Two-handed Swords - 3 Bars"));
				skillItems.add(new SkillItem(215, "31", "Iron Plated Skirts - 3 Bars"));
				skillItems.add(new SkillItem(9, "31", "Iron Platelegs - 3 Bars"));
				skillItems.add(new SkillItem(8, "33", "Iron Platebodies - 5 Bars"));
			} else if (curTab == 3) {
				skillItems.add(new SkillItem(63, "30", "Steel Daggers - 1 Bar"));
				skillItems.add(new SkillItem(88, "31", "Steel Axes - 1 Bar"));
				skillItems.add(new SkillItem(95, "32", "Steel Maces - 1 Bar"));
				skillItems.add(new SkillItem(105, "33", "Steel Medium Helms - 1 Bar"));
				skillItems.add(new SkillItem(67, "34", "Steel Short Swords - 1 Bar"));
				skillItems.add(new SkillItem(1064, "34", "Steel Dart Tips - 1 Bar makes 7"));
				skillItems.add(new SkillItem(84, "35", "Steel Scimitars - 2 Bars"));
				skillItems.add(new SkillItem(671, "35", "Steel Arrowheads - 1 Bar makes 10"));
				skillItems.add(new SkillItem(1041, "35", "Cannonball - 1 Bar"));
				skillItems.add(new SkillItem(72, "36", "Steel Longswords - 2 Bars"));
				skillItems.add(new SkillItem(109, "37", "Steel Full Helms - 2 Bars"));
				skillItems.add(new SkillItem(1077, "37", "Steel Throwing Knives - 1 Bar makes 2"));
				skillItems.add(new SkillItem(125, "38", "Steel Square Shields - 2 Bars"));
				skillItems.add(new SkillItem(90, "40", "Steel Battleaxes - 3 Bars"));
				skillItems.add(new SkillItem(114, "41", "Steel Chainbodies - 3 Bars"));
				skillItems.add(new SkillItem(129, "42", "Steel Kiteshields - 3 Bars"));
				skillItems.add(new SkillItem(78, "44", "Steel Two-handed Swords - 3 Bars"));
				skillItems.add(new SkillItem(225, "46", "Steel Plated Skirts - 3 Bars"));
				skillItems.add(new SkillItem(121, "46", "Steel Platelegs - 3 Bars"));
				skillItems.add(new SkillItem(118, "48", "Steel Platebodies - 5 Bars"));
			} else if (curTab == 4) {
				skillItems.add(new SkillItem(64, "50", "Mithril Daggers - 1 Bar"));
				skillItems.add(new SkillItem(203, "51", "Mithril Axes - 1 Bar"));
				skillItems.add(new SkillItem(96, "52", "Mithril Maces - 1 Bar"));
				skillItems.add(new SkillItem(106, "53", "Mithril Medium Helms - 1 Bar"));
				skillItems.add(new SkillItem(68, "54", "Mithril Short Swords - 1 Bar"));
				skillItems.add(new SkillItem(1065, "54", "Mithril Dart Tips - 1 Bar makes 7"));
				skillItems.add(new SkillItem(85, "55", "Mithril Scimitars - 2 Bars"));
				skillItems.add(new SkillItem(672, "55", "Mithril Arrowheads - 1 Bar makes 10"));
				skillItems.add(new SkillItem(73, "56", "Mithril Longswords - 2 Bars"));
				skillItems.add(new SkillItem(110, "57", "Mithril Full Helms - 2 Bars"));
				skillItems.add(new SkillItem(1078, "57", "Mithril Throwing Knives - 1 Bar makes 2"));
				skillItems.add(new SkillItem(126, "58", "Mithril Square Shields - 2 Bars"));
				skillItems.add(new SkillItem(91, "60", "Mithril Battleaxes - 3 Bars"));
				skillItems.add(new SkillItem(115, "61", "Mithril Chainbodies - 3 Bars"));
				skillItems.add(new SkillItem(130, "62", "Mithril Kiteshields - 3 Bars"));
				skillItems.add(new SkillItem(79, "64", "Mithril Two-handed Swords - 3 Bars"));
				skillItems.add(new SkillItem(226, "66", "Mithril Plated Skirts - 3 Bars"));
				skillItems.add(new SkillItem(122, "66", "Mithril Platelegs - 3 Bars"));
				skillItems.add(new SkillItem(119, "68", "Mithril Platebodies - 5 Bars"));
			} else if (curTab == 5) {
				skillItems.add(new SkillItem(65, "70", "Adamant Daggers - 1 Bar"));
				skillItems.add(new SkillItem(204, "71", "Adamant Axes - 1 Bar"));
				skillItems.add(new SkillItem(97, "72", "Adamant Maces - 1 Bar"));
				skillItems.add(new SkillItem(107, "73", "Adamant Medium Helms - 1 Bar"));
				skillItems.add(new SkillItem(69, "74", "Adamant Short Swords - 1 Bar"));
				skillItems.add(new SkillItem(1066, "74", "Adamantite Dart Tips - 1 Bar makes 7"));
				skillItems.add(new SkillItem(86, "75", "Adamant Scimitars - 2 Bars"));
				skillItems.add(new SkillItem(673, "75", "Adamant Arrowheads - 1 Bar makes 10"));
				skillItems.add(new SkillItem(71, "76", "Adamant Longswords - 2 Bars"));
				skillItems.add(new SkillItem(111, "77", "Adamant Full Helms - 2 Bars"));
				skillItems.add(new SkillItem(1079, "77", "Adamant Throwing Knives - 1 Bar makes 2"));
				skillItems.add(new SkillItem(127, "78", "Adamant Square Shields - 2 Bars"));
				skillItems.add(new SkillItem(92, "80", "Adamant Battleaxes - 3 Bars"));
				skillItems.add(new SkillItem(116, "81", "Adamant Chainbodies - 3 Bars"));
				skillItems.add(new SkillItem(131, "82", "Adamant Kiteshields - 3 Bars"));
				skillItems.add(new SkillItem(80, "84", "Adamant Two-handed Swords - 3 Bars"));
				skillItems.add(new SkillItem(227, "86", "Adamant Plated Skirts - 3 Bars"));
				skillItems.add(new SkillItem(123, "86", "Adamant Platelegs - 3 Bars"));
				skillItems.add(new SkillItem(120, "88", "Adamant Platebodies - 5 Bars"));
			} else if (curTab == 6) {
				skillItems.add(new SkillItem(396, "85", "Rune Daggers - 1 Bar"));
				skillItems.add(new SkillItem(405, "86", "Rune Axes - 1 Bar"));
				skillItems.add(new SkillItem(98, "87", "Rune Maces - 1 Bar"));
				skillItems.add(new SkillItem(399, "88", "Rune Medium Helms - 1 Bar"));
				skillItems.add(new SkillItem(397, "89", "Rune Short Swords - 1 Bar"));
				skillItems.add(new SkillItem(1067, "89", "Rune Dart Tips - 1 Bar makes 7"));
				skillItems.add(new SkillItem(398, "90", "Rune Scimitars - 2 Bars"));
				skillItems.add(new SkillItem(674, "90", "Rune Arrowheads - 1 Bar makes 10"));
				skillItems.add(new SkillItem(75, "91", "Rune Longswords - 2 Bars"));
				skillItems.add(new SkillItem(112, "92", "Rune Full Helms - 2 Bars"));
				skillItems.add(new SkillItem(1080, "92", "Rune Throwing Knives - 1 Bar makes 2"));
				skillItems.add(new SkillItem(403, "93", "Rune Square Shields - 2 Bars"));
				skillItems.add(new SkillItem(93, "95", "Rune Battleaxes - 3 Bars"));
				skillItems.add(new SkillItem(400, "96", "Rune Chainbodies - 3 Bars"));
				skillItems.add(new SkillItem(404, "97", "Rune Kiteshields - 3 Bars"));
				skillItems.add(new SkillItem(81, "99", "Rune Two-handed Swords - 3 Bars"));
				skillItems.add(new SkillItem(406, "99", "Rune Plated Skirts - 3 Bars"));
				skillItems.add(new SkillItem(402, "99", "Rune Platelegs - 3 Bars"));
				skillItems.add(new SkillItem(401, "99", "Rune Platebodies - 5 Bars"));
			} else if (curTab == 7) {
				skillItems.add(new SkillItem(1278, "60", "Dragon Square Shield - Smith the 2 halves together"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Mining")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(149, "1", "Clay"));
				skillItems.add(new SkillItem(150, "1", "Copper Ore"));
				skillItems.add(new SkillItem(202, "1", "Tin Ore"));
				skillItems.add(new SkillItem(266, "10", "Blurite Ore"));
				skillItems.add(new SkillItem(151, "15", "Iron Ore"));
				skillItems.add(new SkillItem(383, "20", "Silver Ore"));
				skillItems.add(new SkillItem(155, "30", "Coal"));
				skillItems.add(new SkillItem(152, "40", "Gold"));
				skillItems.add(new SkillItem(889, "40", "Gem Rocks"));
				skillItems.add(new SkillItem(153, "55", "Mithril Ore"));
				skillItems.add(new SkillItem(154, "70", "Adamantite Ore"));
				skillItems.add(new SkillItem(409, "85", "Runite Ore"));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(156, "1", "Bronze Pickaxe"));
				skillItems.add(new SkillItem(1258, "1", "Iron Pickaxe"));
				skillItems.add(new SkillItem(1259, "6", "Steel Pickaxe"));
				skillItems.add(new SkillItem(1260, "21", "Mithril Pickaxe"));
				skillItems.add(new SkillItem(1261, "31", "Adamant Pickaxe"));
				skillItems.add(new SkillItem(1262, "41", "Rune Pickaxe"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Herblaw")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(444, "3", EntityHandler.getItemDef(444).name));
				skillItems.add(new SkillItem(445, "5", EntityHandler.getItemDef(445).name));
				skillItems.add(new SkillItem(446, "11", EntityHandler.getItemDef(446).name));
				skillItems.add(new SkillItem(447, "20", EntityHandler.getItemDef(447).name));
				skillItems.add(new SkillItem(448, "25", EntityHandler.getItemDef(448).name));
				skillItems.add(new SkillItem(449, "40", EntityHandler.getItemDef(449).name));
				skillItems.add(new SkillItem(450, "48", EntityHandler.getItemDef(450).name));
				skillItems.add(new SkillItem(451, "54", EntityHandler.getItemDef(451).name));
				skillItems.add(new SkillItem(452, "65", EntityHandler.getItemDef(452).name));
				skillItems.add(new SkillItem(453, "70", EntityHandler.getItemDef(453).name));
				skillItems.add(new SkillItem(934, "75", EntityHandler.getItemDef(934).name));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(474, "3", "Attack potion - Guam leaf & eye of newt"));
				skillItems.add(new SkillItem(566, "5", "Cure poison potion - Marrentill & ground unicorn horn"));
				skillItems.add(new SkillItem(1176, "10", "Explosive compound - Nitro & nitrate & charcoal & a. root"));
				skillItems.add(new SkillItem(222, "12", "Strength potion - Tarromin & limpwurt root"));
				skillItems.add(new SkillItem(1053, "14", "Ogre potion - Guam leaf & jangerberries & ground bat bones"));
				skillItems.add(new SkillItem(477, "22", "Stat restore potion - Harralander & red spiders' eggs"));
				skillItems.add(new SkillItem(588, "25", "Blamish oil - Harralander & blamish snail slime"));
				skillItems.add(new SkillItem(480, "30", "Defense potion - Ranarr weed & white berries"));
				skillItems.add(new SkillItem(483, "38", "Restore prayer potion - Ranarr weed & snape grass"));
				skillItems.add(new SkillItem(486, "45", "Super attack potion - Irit leaf & eye of newt"));
				skillItems.add(new SkillItem(1253, "45", "Gujuo potion - Snake weed & ardrigal"));
				skillItems.add(new SkillItem(569, "48", "Poison antidote - Irit leaf & ground unicorn horn"));
				skillItems.add(new SkillItem(489, "50", "Fishing potion - Avantoe & snape grass"));
				skillItems.add(new SkillItem(492, "55", "Super strength potion - Kwuarm & limpwurt root"));
				skillItems.add(new SkillItem(572, "60", "Weapon poison potion - Kwuarm & ground blue dragon scale"));
				skillItems.add(new SkillItem(495, "66", "Super defense potion - Cadantine & white berries"));
				skillItems.add(new SkillItem(498, "72", "Ranging potion - Dwarf weed & wine of zamorak"));
				skillItems.add(new SkillItem(963, "78", "Potion of zamorak - Torstol & jangerberries"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Agility")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(896, "1", "Gnome Stronghold Agility Course"));
				skillItems.add(new SkillItem(981, "1", "Gnomeball minigame"));
				skillItems.add(new SkillItem(90, "35", "Barbarian Outpost Agility Course"));
				skillItems.add(new SkillItem(412, "52", "Wilderness Agility Course"));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(410, "5", "Falador West handholds"));
				skillItems.add(new SkillItem(410, "10", "Brimhaven treeswing"));
				skillItems.add(new SkillItem(410, "15", "Edgeville dungeon ropeswing"));
				skillItems.add(new SkillItem(410, "15", "Yanille North climbing rocks"));
				skillItems.add(new SkillItem(410, "18", "Yanille watchtower handholds"));
				skillItems.add(new SkillItem(410, "20", "North-west of McGrouber's Woods log balance"));
				skillItems.add(new SkillItem(410, "25", "Lum river stepping stone"));
				skillItems.add(new SkillItem(410, "25", "Glough's watch tower"));
				skillItems.add(new SkillItem(410, "30", "Southern Gu'Tanoth bridge rock"));
				skillItems.add(new SkillItem(410, "30", "West of Yanille tree swing"));
				skillItems.add(new SkillItem(410, "32", "Ardougne river rock crossing"));
				skillItems.add(new SkillItem(410, "32", "Cairn Isle rock climb"));
				skillItems.add(new SkillItem(410, "32", "Southeastern Karamja stepping stones"));
				skillItems.add(new SkillItem(410, "32", "Ah Za Roon temple pile of rubble"));
				skillItems.add(new SkillItem(410, "32", "Tomb of Bervirius entrance"));
				skillItems.add(new SkillItem(410, "32", "East Karamjan River log balance"));
				skillItems.add(new SkillItem(410, "35", "Barbarian outpost handholds"));
				skillItems.add(new SkillItem(410, "40", "Yanille Agility Dungeon ledge"));
				skillItems.add(new SkillItem(410, "45", "White Wolf Mountain vine climb"));
				skillItems.add(new SkillItem(410, "49", "Yanille Agility Dungeon pipe"));
				skillItems.add(new SkillItem(410, "50", "Kharazi Jungle cave entrance"));
				skillItems.add(new SkillItem(410, "57", "Yanille Agility Dungeon rope swing"));
				skillItems.add(new SkillItem(410, "67", "Yanille Agility Dungeon pile of rubble"));
				skillItems.add(new SkillItem(410, "70", "Taverly Dungeon pipe crawl"));
				skillItems.add(new SkillItem(410, "82", "Catherby island stepping stones"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Thieving")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(586, "1", "Man"));
				skillItems.add(new SkillItem(1145, "10", "Farmer"));
				skillItems.add(new SkillItem(84, "25", "Warrior"));
				skillItems.add(new SkillItem(1115, "25", "Workman"));
				skillItems.add(new SkillItem(177, "32", "Rogue"));
				skillItems.add(new SkillItem(104, "40", "Guard"));
				skillItems.add(new SkillItem(6, "55", "Knight"));
				skillItems.add(new SkillItem(105, "65", "Watchman"));
				skillItems.add(new SkillItem(97, "70", "Paladin"));
				skillItems.add(new SkillItem(895, "75", "Gnome"));
				skillItems.add(new SkillItem(75, "80", "Hero"));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(739, "5", "Tea Stall"));
				skillItems.add(new SkillItem(330, "5", "Baker's Stall"));
				skillItems.add(new SkillItem(1061, "15", "Rock Cake Stall"));
				skillItems.add(new SkillItem(200, "20", "Silk Stall"));
				skillItems.add(new SkillItem(146, "35", "Fur Stall"));
				skillItems.add(new SkillItem(383, "50", "Silver Stall"));
				skillItems.add(new SkillItem(707, "65", "Spice Stall"));
				skillItems.add(new SkillItem(157, "75", "Gem Stall"));
			} else if (curTab == 2) {
				skillItems.add(new SkillItem(10, "13", "Ardougne Market house, Pirate's Hideout, Axe Hut"));
				skillItems.add(new SkillItem(40, "28", "Ardougne Market house, Pirate's Hideout"));
				skillItems.add(new SkillItem(10, "43", "Ardougne Market house, Axe Hut"));
				skillItems.add(new SkillItem(671, "47", "Hemenster"));
				skillItems.add(new SkillItem(619, "59", "Ardougne Chaos Druid Tower"));
				skillItems.add(new SkillItem(545, "72", "Ardougne Castle"));
			} else if (curTab == 3) {
				skillItems.add(new SkillItem(10, "7", "Ardougne Market house"));
				skillItems.add(new SkillItem(40, "16", "Ardougne Market house"));
				skillItems.add(new SkillItem(705, "16", "Ardougne Market house, Handelmort's mansion"));
				skillItems.add(new SkillItem(155, "31", "Ardougne sewer mine"));
				skillItems.add(new SkillItem(90, "32", "Axe Hut"));
				skillItems.add(new SkillItem(262, "39", "Pirate's Hideout"));
				skillItems.add(new SkillItem(444, "46", "Ardougne Chaos Druid Tower"));
				skillItems.add(new SkillItem(545, "61", "Ardougne Castle upstairs"));
				skillItems.add(new SkillItem(714, "82", "Yanille Agility Dungeon"));
			}
		}
		if (mc.getSkillGuideChosen().equalsIgnoreCase("Runecrafting")) {
			if (curTab == 0) {
				skillItems.add(new SkillItem(33, "1","Air Rune"));
				skillItems.add(new SkillItem(35, "1","Mind Rune"));
				skillItems.add(new SkillItem(32, "5","Water Rune"));
				skillItems.add(new SkillItem(34, "9","Earth Rune"));
				skillItems.add(new SkillItem(31, "14","Fire Rune"));
				skillItems.add(new SkillItem(36, "20","Body Rune"));
				skillItems.add(new SkillItem(46, "27","Cosmic Rune"));
				skillItems.add(new SkillItem(41, "35","Chaos Rune"));
				skillItems.add(new SkillItem(40, "44","Nature Rune"));
				//skillItems.add(new SkillItem(42, "54","Law Rune"));
				//skillItems.add(new SkillItem(38, "65","Death Rune"));
				//skillItems.add(new SkillItem(619, "77","Blood Rune"));
			} else if (curTab == 1) {
				skillItems.add(new SkillItem(33, "11","Air Rune x2"));
				skillItems.add(new SkillItem(35, "14","Mind Rune x2"));
				skillItems.add(new SkillItem(32, "19","Water Rune x2"));
				skillItems.add(new SkillItem(33, "22","Air Rune x3"));
				skillItems.add(new SkillItem(34, "26","Earth Rune x2"));
				skillItems.add(new SkillItem(35, "28","Mind Rune x3"));
				skillItems.add(new SkillItem(33, "33","Air Rune x4"));
				skillItems.add(new SkillItem(31, "35","Fire Rune x2"));
				skillItems.add(new SkillItem(32, "38","Water Rune x3"));
				skillItems.add(new SkillItem(35, "42","Mind Rune x4"));
				skillItems.add(new SkillItem(33, "44","Air Rune x5"));
				skillItems.add(new SkillItem(36, "46","Body Rune x2"));
				skillItems.add(new SkillItem(34, "52","Earth Rune x3"));
				skillItems.add(new SkillItem(33, "55","Air Rune x6"));
				skillItems.add(new SkillItem(35, "56","Mind Rune x5"));
				skillItems.add(new SkillItem(32, "57","Water Rune x4"));
				skillItems.add(new SkillItem(46, "59","Cosmic Rune x2"));
				skillItems.add(new SkillItem(33, "66","Air Rune x7"));
				skillItems.add(new SkillItem(35, "70","Mind Rune x6"));
				skillItems.add(new SkillItem(31, "70","Fire Rune x3"));
				skillItems.add(new SkillItem(41, "74","Chaos Rune x2"));
				skillItems.add(new SkillItem(32, "76","Water Rune x5"));
				skillItems.add(new SkillItem(33, "77","Air Rune x8"));
				skillItems.add(new SkillItem(34, "78","Earth Rune x4"));
				skillItems.add(new SkillItem(35, "84","Mind Rune x7"));
				skillItems.add(new SkillItem(33, "88","Air Rune x9"));
				skillItems.add(new SkillItem(40, "91","Nature Rune x2"));
				skillItems.add(new SkillItem(36, "92","Body Rune x3"));
				skillItems.add(new SkillItem(32, "95","Water Rune x6"));
				skillItems.add(new SkillItem(35, "98","Mind Rune x8"));
				skillItems.add(new SkillItem(33, "99","Air Rune x10"));
			}
		}

	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}

class SkillItem {

	private int itemID;
	private String levelReq, skillDetail;

	public SkillItem(int itemID, String levelReq, String skillDetail) {
		this.itemID = itemID;
		this.levelReq = levelReq;
		this.skillDetail = skillDetail;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public String getLevelReq() {
		return levelReq;
	}

	public void setLevelReq(String levelReq) {
		this.levelReq = levelReq;
	}

	public String getSkillDetail() {
		return skillDetail;
	}

	public void setSkillDetail(String skillDetail) {
		this.skillDetail = skillDetail;
	}
}
