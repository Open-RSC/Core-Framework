package com.openrsc.interfaces.misc;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.ItemDef;
import com.openrsc.client.entityhandling.defs.NPCDef;
import orsc.Config;
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
	private ArrayList<SkillMenuEntry> skillMenuEntries;
	private boolean visible = false;
	private mudclient mc;
	private int panelColour, textColour, bordColour;
	private int x, y;

	public SkillGuideInterface(mudclient mc) {
		this.mc = mc;

		skillGuide = new Panel(mc.getSurface(), 15);

		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		skillMenuEntries = new ArrayList<SkillMenuEntry>();

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
		for (int i = -1; i <= skillMenuEntries.size(); i++) {
			skillGuide.setListEntry(skillGuideScroll, i + 1, "", 0, (String) null, (String) null);
		}

		int listStartPoint = skillGuide.getScrollPosition(skillGuideScroll);
		int listEndPoint = listStartPoint + 5;

		int levelX = x + 10;
		int spriteX = levelX + 15;
		int detailX = spriteX + 50;
		int allY = 0;
		allY = y + 82 + 16;

		for (int i = -1; i < skillMenuEntries.size(); i++) {
			if (i >= 100) {
				break;
			}

			if (i < listStartPoint || i > listEndPoint)
				continue;

			SkillMenuEntry curItem = skillMenuEntries.get(i);

			int gapHeight = (curItem instanceof SkillMenuItem) ? 37 : 37;

			mc.getSurface().drawBoxAlpha(detailX - 75, allY, width, gapHeight, 0x45454545, 90);
			drawString(curItem.getLevelReq(), levelX, allY + 25, 2, textColour);

			drawString(curItem.getSkillDetail(), detailX + 10, allY + 25, 2, textColour);

			//mc.getSurface().drawLineHoriz(detailX - 75, allY, width, 0);
			if (i != skillMenuEntries.size() - 1 && i != listEndPoint) {
				mc.getSurface().drawBoxBorder(detailX - 75, width, allY, gapHeight + 1, 0);
			}

			if (curItem instanceof SkillMenuItem) {
				ItemDef def = EntityHandler.getItemDef(((SkillMenuItem) curItem).getItemID());
				if (def != null) {
					mc.getSurface().drawSpriteClipping(mc.spriteSelect(def),
						spriteX + 5, allY + 2, 48, 32, def.getPictureMask(), 0,
						def.getBlueMask(), false, 0, 1);
				}
			} else if (curItem instanceof SkillMenuNPC) {
				NPCDef def = EntityHandler.getNpcDef(((SkillMenuNPC) curItem).getNpcID());
				if (def != null) {
					int height = 32;
					int width = def.getCamera1() / (def.getCamera2() / height);
					mc.drawNPCDef(def, spriteX + 15, allY + 2, width, height);
				}
			}

			allY += gapHeight;
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
		skillMenuEntries.clear();
		if (mc.getSkillGuideChosen().equals("Attack")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(66, "1", "Bronze"));
				skillMenuEntries.add(new SkillMenuItem(1, "1", "Iron"));
				skillMenuEntries.add(new SkillMenuItem(67, "5", "Steel"));
				skillMenuEntries.add(new SkillMenuItem(424, "10", "Black"));
				skillMenuEntries.add(new SkillMenuItem(68, "20", "Mithril"));
				skillMenuEntries.add(new SkillMenuItem(69, "30", "Adamantite"));
				skillMenuEntries.add(new SkillMenuItem(617, "30", "Battlestaves"));
				skillMenuEntries.add(new SkillMenuItem(397, "40", "Rune"));
				skillMenuEntries.add(new SkillMenuItem(684, "40", "Enchanted battlestaves"));
				skillMenuEntries.add(new SkillMenuItem(1000, "50", "Staff of Iban"));
				skillMenuEntries.add(new SkillMenuItem(593, "60", "Dragon"));
			} else if (curTab == 1) {
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1374, "99", "Attack Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Defense")) {
			skillMenuEntries.add(new SkillMenuItem(15, "1", "Leather"));
			skillMenuEntries.add(new SkillMenuItem(128, "1", "Bronze"));
			skillMenuEntries.add(new SkillMenuItem(2, "1", "Iron"));
			skillMenuEntries.add(new SkillMenuItem(129, "5", "Steel"));
			skillMenuEntries.add(new SkillMenuItem(433, "10", "Black"));
			skillMenuEntries.add(new SkillMenuItem(130, "20", "Mithril"));
			skillMenuEntries.add(new SkillMenuItem(131, "30", "Adamantite"));
			skillMenuEntries.add(new SkillMenuItem(404, "40", "Rune"));
			skillMenuEntries.add(new SkillMenuItem(1278, "60", "Dragon"));
			if (Config.S_WANT_CUSTOM_SPRITES) {
				skillMenuEntries.add(new SkillMenuItem(1519, "99", "Defense Cape"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Strength")) {
			skillMenuEntries.add(new SkillMenuItem(90, "", "Strength raises your max hit with melee"));
			skillMenuEntries.add(new SkillMenuItem(1288, "50", "Legend's Guild (with completion of Legend's Quest)"));
			if (Config.S_WANT_CUSTOM_SPRITES) {
				skillMenuEntries.add(new SkillMenuItem(1381, "99", "Strength Cape"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Hits")) {
			boolean harvesting = Config.S_WANT_HARVESTING && Config.S_WANT_CUSTOM_SPRITES;
			boolean runecraft = Config.S_WANT_RUNECRAFT && Config.S_WANT_CUSTOM_SPRITES;
			boolean customSprites = Config.S_WANT_CUSTOM_SPRITES;
			if (curTab == 0) { // Fish
				skillMenuEntries.add(new SkillMenuItem(350, "", "Shrimp - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(352, "", "Anchovies - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(355, "", "Sardine - Heals 4"));
				skillMenuEntries.add(new SkillMenuItem(362, "", "Herring - Heals 5"));
				skillMenuEntries.add(new SkillMenuItem(718, "", "Giant Carp - Heals 6"));
				skillMenuEntries.add(new SkillMenuItem(553, "", "Mackerel - Heals 6"));
				skillMenuEntries.add(new SkillMenuItem(359, "", "Trout - Heals 7"));
				skillMenuEntries.add(new SkillMenuItem(551, "", "Cod - Heals 7"));
				skillMenuEntries.add(new SkillMenuItem(364, "", "Pike - Heals 8"));
				skillMenuEntries.add(new SkillMenuItem(357, "", "Salmon - Heals 9"));
				skillMenuEntries.add(new SkillMenuItem(367, "", "Tuna - Heals 10"));
				skillMenuEntries.add(new SkillMenuItem(590, "", "Lava Eel - Heals 11"));
				skillMenuEntries.add(new SkillMenuItem(373, "", "Lobster - Heals 12"));
				skillMenuEntries.add(new SkillMenuItem(555, "", "Bass - Heals 13"));
				skillMenuEntries.add(new SkillMenuItem(370, "", "Swordfish - Heals 14"));
				skillMenuEntries.add(new SkillMenuItem(546, "", "Shark - Heals 20"));
				skillMenuEntries.add(new SkillMenuItem(1193, "", "Sea Turtle - Heals 20"));
				skillMenuEntries.add(new SkillMenuItem(1191, "", "Manta Ray - Heals 20"));
			} else if (curTab == 1) { // Pies
				skillMenuEntries.add(new SkillMenuItem(258, "", "Redberry Pie - Heals 6 (3 per slice)"));
				skillMenuEntries.add(new SkillMenuItem(259, "", "Meat Pie - Heals 8 (4 per slice)"));
				skillMenuEntries.add(new SkillMenuItem(257, "", "Apple Pie - Heals 10 (5 per slice)"));
				skillMenuEntries.add(new SkillMenuItem(325, "", "Plain Pizza - Heals 11"));
				skillMenuEntries.add(new SkillMenuItem(326, "", "Meat Pizza - Heals 14 (7 per slice)"));
				skillMenuEntries.add(new SkillMenuItem(327, "", "Anchovie Pizza - Heals 16 (8 per slice)"));
				skillMenuEntries.add(new SkillMenuItem(750, "", "Pineapple Pizza - Heals 20 (10 per slice)"));
				skillMenuEntries.add(new SkillMenuItem(1490, "", "Pumkpin pie - Heals 24 (12 per slice)"));
			} else if (curTab == 2) { // Produce
				skillMenuEntries.add(new SkillMenuItem(18, "", "Cabbage - Heals 1"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1352, "", "Red Cabbage - Heals 1"));
				skillMenuEntries.add(new SkillMenuItem(765, "", "Dwellberries - Heals 1"));
				skillMenuEntries.add(new SkillMenuItem(320, "", "Tomato - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(249, "", "Banana - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(855, "", "Lemon - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(856, "", "Lemon slices - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(860, "", "Diced lemon - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(863, "", "Lime - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(865, "", "Lime slices - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(864, "", "Lime chunks - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(857, "", "orange - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(858, "", "Orange slices - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(859, "", "Diced orange - Heals 2"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1349, "", "grapefruit - Heals 2"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1359, "", "grapefruit slices - Heals 2"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1360, "", "Diced grapefruit - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(748, "", "Fresh Pineapple - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(749, "", "Pineapple ring - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(862, "", "Pineapple chunks - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(936, "", "Jangerberries - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(1245, "", "Edible seaweed - Heals 4"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1348, "", "red apple - Heals 4"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1353, "", "Corn - Heals 6"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1350, "", "papaya - Heals 8"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1354, "", "White Pumpkin - Heals 10"));
			} else if (curTab == 3) { // Gnome Food
				skillMenuEntries.add(new SkillMenuItem(897, "", "King worm - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(896, "", "Toad legs - Heals 3"));
				skillMenuEntries.add(new SkillMenuItem(911, "", "Choc crunchies - Heals 7"));
				skillMenuEntries.add(new SkillMenuItem(914, "", "Spice crunchies - Heals 7"));
				skillMenuEntries.add(new SkillMenuItem(912, "", "Worm crunchies - Heals 8"));
				skillMenuEntries.add(new SkillMenuItem(913, "", "Toad crunchies - Heals 8"));
				skillMenuEntries.add(new SkillMenuItem(901, "", "Cheese and tomato batta - Heals 11"));
				skillMenuEntries.add(new SkillMenuItem(905, "", "Fruit batta - Heals 11"));
				skillMenuEntries.add(new SkillMenuItem(902, "", "Toad batta - Heals 11"));
				skillMenuEntries.add(new SkillMenuItem(906, "", "Veg batta - Heals 11"));
				skillMenuEntries.add(new SkillMenuItem(904, "", "Worm batta - Heals 11"));
				skillMenuEntries.add(new SkillMenuItem(908, "", "Vegball - Heals 12"));
				skillMenuEntries.add(new SkillMenuItem(909, "", "Worm hole - Heals 12"));
				skillMenuEntries.add(new SkillMenuItem(907, "", "Chocolate bomb - Heals 15"));
				skillMenuEntries.add(new SkillMenuItem(910, "", "Tangled toads legs - Heals 15"));
			} else if (curTab == 4) { // Drinks
				skillMenuEntries.add(new SkillMenuItem(193, "", "Beer - Heals 1"));
				skillMenuEntries.add(new SkillMenuItem(269, "", "Dwarven Stout - Heals 1"));
				skillMenuEntries.add(new SkillMenuItem(830, "", "Greenmans ale - Heals 1"));
				skillMenuEntries.add(new SkillMenuItem(268, "", "Wizard's Mind Bomb - Heals 1"));
				skillMenuEntries.add(new SkillMenuItem(267, "", "Asgarnian Ale - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(739, "", "Cup of tea - Heals 2-3"));
				skillMenuEntries.add(new SkillMenuItem(598, "", "Grog - Heals 3"));
				skillMenuEntries.add(new SkillMenuItem(870, "", "gin - Heals 4"));
				skillMenuEntries.add(new SkillMenuItem(869, "", "vodka - Heals 4"));
				skillMenuEntries.add(new SkillMenuItem(868, "", "Whisky - Heals 4"));
				skillMenuEntries.add(new SkillMenuItem(770, "", "chocolaty milk - Heals 4"));
				skillMenuEntries.add(new SkillMenuItem(876, "", "brandy - Heals 5"));
				skillMenuEntries.add(new SkillMenuItem(877, "", "blurberry special - Heals 5"));
				skillMenuEntries.add(new SkillMenuItem(875, "", "Chocolate saturday - Heals 5"));
				skillMenuEntries.add(new SkillMenuItem(872, "", "Drunk dragon - Heals 5"));
				skillMenuEntries.add(new SkillMenuItem(874, "", "SGG - Heals 5"));
				skillMenuEntries.add(new SkillMenuItem(878, "", "wizard blizzard - Heals 5"));
				skillMenuEntries.add(new SkillMenuItem(866, "", "fruit blast - Heals 8"));
				skillMenuEntries.add(new SkillMenuItem(879, "", "pineapple punch - Heals 9"));
				skillMenuEntries.add(new SkillMenuItem(142, "", "wine - Heals 11"));
				skillMenuEntries.add(new SkillMenuItem(737, "", "Poison chalice - ???"));
			} else if (curTab == 5) { // Other
				if (runecraft) skillMenuEntries.add(new SkillMenuItem(1410, "", "Fish oil - 50% chance to heal 1"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1459, "", "Sweetened Slices - Heals 1 or 2"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1460, "", "Sweetened Chunks - Heals 1 or 2"));
				skillMenuEntries.add(new SkillMenuItem(319, "", "Cheese - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(179, "", "spinach roll - Heals 2"));
				if (customSprites) skillMenuEntries.add(new SkillMenuItem(1417, "", "Pizza Bagel - Heals 2"));
				skillMenuEntries.add(new SkillMenuItem(132, "", "cookedmeat - Heals 3"));
				skillMenuEntries.add(new SkillMenuItem(1103, "", "Cooked Ugthanki Meat - Heals 3"));
				skillMenuEntries.add(new SkillMenuItem(337, "", "Chocolate Bar - Heals 3"));
				skillMenuEntries.add(new SkillMenuItem(138, "", "Bread - Heals 4"));
				skillMenuEntries.add(new SkillMenuItem(1269, "", "Oomlie meat Parcel - Heals 8"));
				skillMenuEntries.add(new SkillMenuItem(346, "", "Stew - Heals 9"));
				skillMenuEntries.add(new SkillMenuItem(677, "", "Easter egg - Heals 12"));
				skillMenuEntries.add(new SkillMenuItem(330, "", "Cake - Heals 12 (4 per slice)"));
				skillMenuEntries.add(new SkillMenuItem(422, "", "Pumpkin - Heals 14"));
				skillMenuEntries.add(new SkillMenuItem(332, "", "Chocolate Cake - Heals 15 (5 per slice)"));
				skillMenuEntries.add(new SkillMenuItem(709, "", "Curry - Heals 19"));
				skillMenuEntries.add(new SkillMenuItem(1102, "", "Tasty Ugthanki Kebab - Heals 19"));
				skillMenuEntries.add(new SkillMenuItem(210, "", "Kebab - ???"));
				skillMenuEntries.add(new SkillMenuItem(923, "", "Ugthanki Kebab - ???"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1463, "", "Seaweed Soup - Heals 26"));
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1522, "99", "Hits Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Ranged")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(189, "1", "Shortbow"));
				skillMenuEntries.add(new SkillMenuItem(188, "1", "Longbow"));
				skillMenuEntries.add(new SkillMenuItem(649, "5", "Oak shortbow"));
				skillMenuEntries.add(new SkillMenuItem(648, "10", "Oak longbow"));
				skillMenuEntries.add(new SkillMenuItem(651, "15", "Willow shortbow"));
				skillMenuEntries.add(new SkillMenuItem(650, "20", "Willow longbow"));
				skillMenuEntries.add(new SkillMenuItem(653, "25", "Maple shortbow"));
				skillMenuEntries.add(new SkillMenuItem(652, "30", "Maple longbow"));
				skillMenuEntries.add(new SkillMenuItem(655, "35", "Yew shortbow"));
				skillMenuEntries.add(new SkillMenuItem(654, "40", "Yew longbow"));
				skillMenuEntries.add(new SkillMenuItem(657, "45", "Magic shortbow"));
				skillMenuEntries.add(new SkillMenuItem(656, "50", "Magic longbow"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(60, "1", "Crossbow"));
				skillMenuEntries.add(new SkillMenuItem(59, "1", "Phoenix Crossbow"));
			} else if (curTab == 2) {
				skillMenuEntries.add(new SkillMenuItem(1080, "1", "All throwing knives"));
				skillMenuEntries.add(new SkillMenuItem(1013, "1", "Bronze darts & spears"));
				skillMenuEntries.add(new SkillMenuItem(1015, "1", "Iron darts & spears"));
				skillMenuEntries.add(new SkillMenuItem(1024, "5", "Steel darts & spears"));
				skillMenuEntries.add(new SkillMenuItem(1068, "20", "Mithril darts & spears"));
				skillMenuEntries.add(new SkillMenuItem(1069, "30", "Adamant darts & spears"));
				skillMenuEntries.add(new SkillMenuItem(1070, "40", "Rune darts & spears"));
			} else if (curTab == 3) {
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1524, "99", "Ranged Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Prayer")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(44, "1", "Thick skin - Increases your defense by 5%"));
				skillMenuEntries.add(new SkillMenuItem(44, "4", "Burst of strength - Increases your strength by 5%"));
				skillMenuEntries.add(new SkillMenuItem(44, "7", "Clarity of thought - Increases your attack by 5%"));
				skillMenuEntries.add(new SkillMenuItem(44, "10", "Rock skin - Increases your defense by 10%"));
				skillMenuEntries.add(new SkillMenuItem(44, "13", "Superhuman strength - Increases your strength by 10%"));
				skillMenuEntries.add(new SkillMenuItem(44, "16", "Improved reflexes + Increases your attack by 10%"));
				skillMenuEntries.add(new SkillMenuItem(44, "19", "Rapid restore - 2x restore rate for all stats except hits"));
				skillMenuEntries.add(new SkillMenuItem(44, "22", "Rapid heal - 2x restore rate for hitpoints stat"));
				skillMenuEntries.add(new SkillMenuItem(44, "25", "Protect items - Keep 1 extra item if you die"));
				skillMenuEntries.add(new SkillMenuItem(44, "28", "Steel skin - Increases your defense by 15%"));
				skillMenuEntries.add(new SkillMenuItem(44, "31", "Ultimate strength - Increases your strength by 15%"));
				skillMenuEntries.add(new SkillMenuItem(44, "34", "Incredible reflexes - Increases your attack by 15%"));
				skillMenuEntries.add(new SkillMenuItem(44, "37", "Paralyze monster - Stops monsters from fighting back"));
				skillMenuEntries.add(new SkillMenuItem(44, "40", "Protect from missiles - 100% protection from ranged attacks"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(388, "31", "Monastery"));
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1523, "99", "Prayer Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Magic")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(35, "1", "Wind strike - A strength 1 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(36, "3", "Confuse - Reduces your opponents attack by 5%"));
				skillMenuEntries.add(new SkillMenuItem(35, "5", "Water strike - A strength 2 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(46, "7", "Enchant lvl-1 amulet - For use on sapphire " + (Config.S_WANT_EQUIPMENT_TAB ? " and opal jewelry" : "amulets")));
				skillMenuEntries.add(new SkillMenuItem(35, "9", "Earth strike - A strength 3 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(36, "11", "Weaken - Reduces your opponents strength by 5%"));
				skillMenuEntries.add(new SkillMenuItem(35, "13", "Fire strike - A strength 4 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(40, "15", "Bones to bananas - Changes all held bones into bananas!"));
				skillMenuEntries.add(new SkillMenuItem(41, "17", "Wind Bolt - A strength 5 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(36, "19", "Curse - Reduces your opponents defense by 5%"));
				skillMenuEntries.add(new SkillMenuItem(40, "21", "Low level alchemy - Converts an item into gold"));
				skillMenuEntries.add(new SkillMenuItem(41, "23", "Water Bolt - A strength 6 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(42, "25", "Varrock teleport - Teleports you to Varrock"));
				skillMenuEntries.add(new SkillMenuItem(46, "27", "Enchant lvl-2 amulet - For use on emerald " + (Config.S_WANT_EQUIPMENT_TAB ? "jewelry" : "amulets")));
				skillMenuEntries.add(new SkillMenuItem(41, "29", "Earth Bolt - A strength 7 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(42, "31", "Lumbridge teleport - Teleports you to Lumbridge"));
				skillMenuEntries.add(new SkillMenuItem(42, "33", "Telekinetic grab - Take an item you can see but can't reach"));
				skillMenuEntries.add(new SkillMenuItem(41, "35", "Fire Bolt - A strength 8 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(42, "37", "Falador teleport - Teleports you to Falador"));
				skillMenuEntries.add(new SkillMenuItem(41, "39", "Crumble undead - Hits skeleton, ghosts & zombies hard!"));
				skillMenuEntries.add(new SkillMenuItem(38, "41", "Wind blast - A strength 9 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(40, "43", "Superheat item - Smelt 1 ore without a furnace"));
				skillMenuEntries.add(new SkillMenuItem(42, "45", "Camelot teleport - Teleports you to Camelot"));
				skillMenuEntries.add(new SkillMenuItem(38, "47", "Water blast - A strength 10 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(46, "49", "Enchant lvl-3 amulet - For use on ruby " + (Config.S_WANT_EQUIPMENT_TAB ? "jewelry" : "amulets")));
				skillMenuEntries.add(new SkillMenuItem(38, "50", "Iban blast - A strength 25 missile attack!"));
				skillMenuEntries.add(new SkillMenuItem(42, "51", "Ardougne teleport - Teleports you to Ardougne"));
				skillMenuEntries.add(new SkillMenuItem(38, "53", "Earth blast - A strength 11 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(40, "55", "High level alchemy - Converts an item into more gold"));
				skillMenuEntries.add(new SkillMenuItem(46, "56", "Charge water orb - Needs to be cast on a water obelisk"));
				skillMenuEntries.add(new SkillMenuItem(46, "57", "Enchant lvl-4 amulet - For use on diamond " + (Config.S_WANT_EQUIPMENT_TAB ? "jewelry" : "amulets")));
				skillMenuEntries.add(new SkillMenuItem(42, "58", "Watchtower teleport - Teleports you to the watchtower"));
				skillMenuEntries.add(new SkillMenuItem(38, "59", "Fire blast - A strength 12 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(46, "60", "Charge earth orb - Needs to be cast on a earth obelisk"));
				skillMenuEntries.add(new SkillMenuItem(619, "60", "Claws of Guthix - Summons the power of Guthix"));
				skillMenuEntries.add(new SkillMenuItem(619, "60", "Saradomin strike - Summons the power of Saradomin"));
				skillMenuEntries.add(new SkillMenuItem(619, "60", "Flames of Zamorak - Summons the power of Zamorak"));
				skillMenuEntries.add(new SkillMenuItem(619, "62", "Wind wave - A strength 13 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(46, "63", "Charge fire orb - Needs to be cast on a fire obelisk"));
				skillMenuEntries.add(new SkillMenuItem(619, "65", "Water wave - A strength 14 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(825, "66", "Vulnerability - Reduces your opponents defense by 10%"));
				skillMenuEntries.add(new SkillMenuItem(46, "66", "Charge air orb - Needs to be cast on a air obelisk"));
				skillMenuEntries.add(new SkillMenuItem(46, "68", "Enchant lvl-5 amulet - For use on dragonstone " + (Config.S_WANT_EQUIPMENT_TAB ? "jewelry" : "amulets")));
				skillMenuEntries.add(new SkillMenuItem(619, "70", "Earth wave - A strength 15 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(825, "73", "Enfeeble - Reduces your opponents strength by 10%"));
				skillMenuEntries.add(new SkillMenuItem(619, "75", "Fire wave - A strength 16 missile attack"));
				skillMenuEntries.add(new SkillMenuItem(825, "80", "Stun - Reduces your opponents attack by 10%"));
				skillMenuEntries.add(new SkillMenuItem(619, "80", "Charge - Increase your mage arena spells damage"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(184, "1", "Wizard hats and robes"));
				skillMenuEntries.add(new SkillMenuItem(702, "1", "Robes of Zamorak"));
				skillMenuEntries.add(new SkillMenuItem(1215, "60", "God capes"));
			} else if (curTab == 2) {
				skillMenuEntries.add(new SkillMenuItem(101, "1", "Basic staves"));
				skillMenuEntries.add(new SkillMenuItem(617, "30", "Battlestaves"));
				skillMenuEntries.add(new SkillMenuItem(684, "40", "Enchanted battlestaves"));
				skillMenuEntries.add(new SkillMenuItem(1000, "50", "Staff of Iban"));
				skillMenuEntries.add(new SkillMenuItem(1218, "60", "God staves"));
			} else if (curTab == 3) {
				skillMenuEntries.add(new SkillMenuItem(185, "66", "Wizards' Guild"));
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1382, "99", "Magic Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Cooking")) {
			boolean harvesting = Config.S_WANT_HARVESTING && Config.S_WANT_CUSTOM_SPRITES;
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(132, "1", "Cooked Meat"));
				skillMenuEntries.add(new SkillMenuItem(350, "1", "Shrimp"));
				skillMenuEntries.add(new SkillMenuItem(352, "1", "Anchovies"));
				skillMenuEntries.add(new SkillMenuItem(355, "1", "Sardine"));
				skillMenuEntries.add(new SkillMenuItem(362, "5", "Herring"));
				skillMenuEntries.add(new SkillMenuItem(718, "10", "Giant Carp"));
				skillMenuEntries.add(new SkillMenuItem(553, "10", "Mackerel"));
				skillMenuEntries.add(new SkillMenuItem(359, "15", "Trout"));
				skillMenuEntries.add(new SkillMenuItem(551, "18", "Cod"));
				skillMenuEntries.add(new SkillMenuItem(364, "20", "Pike"));
				skillMenuEntries.add(new SkillMenuItem(357, "25", "Salmon"));
				skillMenuEntries.add(new SkillMenuItem(367, "30", "Tuna"));
				skillMenuEntries.add(new SkillMenuItem(373, "40", "Lobster"));
				skillMenuEntries.add(new SkillMenuItem(555, "43", "Bass"));
				skillMenuEntries.add(new SkillMenuItem(370, "45", "Swordfish"));
				skillMenuEntries.add(new SkillMenuItem(590, "53", "Lava Eel"));
				skillMenuEntries.add(new SkillMenuItem(546, "80", "Shark"));
				skillMenuEntries.add(new SkillMenuItem(1193, "82", "Sea Turtle"));
				skillMenuEntries.add(new SkillMenuItem(1191, "91", "Manta Ray"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(325, "35", "Plain Pizza"));
				skillMenuEntries.add(new SkillMenuItem(326, "45", "Meat Pizza"));
				skillMenuEntries.add(new SkillMenuItem(327, "55", "Anchovie Pizza"));
				skillMenuEntries.add(new SkillMenuItem(750, "65", "Pineapple Pizza"));
			} else if (curTab == 2) {
				skillMenuEntries.add(new SkillMenuItem(258, "10", "Redberry Pie"));
				skillMenuEntries.add(new SkillMenuItem(259, "20", "Meat Pie"));
				skillMenuEntries.add(new SkillMenuItem(257, "30", "Apple Pie"));
			} else if (curTab == 3) {
				skillMenuEntries.add(new SkillMenuItem(346, "25", "Stew"));
				skillMenuEntries.add(new SkillMenuItem(709, "60", "Curry"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1463, "80", "Seaweed Soup"));
			} else if (curTab == 4) {
				skillMenuEntries.add(new SkillMenuItem(138, "1", "Bread"));
				skillMenuEntries.add(new SkillMenuItem(1105, "58", "Pitta Bread"));
			} else if (curTab == 5) {
				skillMenuEntries.add(new SkillMenuItem(330, "40", "Cake"));
				skillMenuEntries.add(new SkillMenuItem(332, "50", "Chocolate Cake"));
			} else if (curTab == 6) {
				skillMenuEntries.add(new SkillMenuItem(833, "1", "Cocktails"));
				skillMenuEntries.add(new SkillMenuItem(911, "15", "Choc Crunchies"));
				skillMenuEntries.add(new SkillMenuItem(912, "15", "Worm Crunchies"));
				skillMenuEntries.add(new SkillMenuItem(913, "15", "Toad Crunchies"));
				skillMenuEntries.add(new SkillMenuItem(914, "15", "Spice Crunchies"));
				skillMenuEntries.add(new SkillMenuItem(901, "25", "Cheese and Tomato Batta"));
				skillMenuEntries.add(new SkillMenuItem(902, "25", "Toad Batta"));
				skillMenuEntries.add(new SkillMenuItem(904, "25", "Worm Batta"));
				skillMenuEntries.add(new SkillMenuItem(905, "25", "Fruit Batta"));
				skillMenuEntries.add(new SkillMenuItem(906, "25", "Veg Batta"));
				skillMenuEntries.add(new SkillMenuItem(907, "30", "Chocolate Bomb"));
				skillMenuEntries.add(new SkillMenuItem(908, "30", "Vegball"));
				skillMenuEntries.add(new SkillMenuItem(909, "30", "Worm Hole"));
				skillMenuEntries.add(new SkillMenuItem(910, "30", "Tangled Toads Legs"));
			} else if (curTab == 7) {
				skillMenuEntries.add(new SkillMenuItem(192, "32", "Cooks' Guild"));
				skillMenuEntries.add(new SkillMenuItem(142, "35", "Wine"));
				skillMenuEntries.add(new SkillMenuItem(1269, "50", "Oomlie Meat Parcel"));
				skillMenuEntries.add(new SkillMenuItem(1102, "58", "Tasty Ugthanki Kebab"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(501, "70", "Wine of Zamorak"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1467, "70", "Wine of Saradomin"));
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1373, "99", "Cooking Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Woodcutting")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(14, "1", "Trees"));
				skillMenuEntries.add(new SkillMenuItem(632, "15", "Oak trees"));
				skillMenuEntries.add(new SkillMenuItem(633, "30", "Willow trees"));
				skillMenuEntries.add(new SkillMenuItem(634, "45", "Maple trees"));
				skillMenuEntries.add(new SkillMenuItem(635, "60", "Yew trees"));
				skillMenuEntries.add(new SkillMenuItem(636, "75", "Magic trees"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(405, "1", "All axes"));
			} else if (curTab == 2) {
				skillMenuEntries.add(new SkillMenuItem(88, "", "Woodcutting Guild (Coming soon)"));
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1525, "99", "Woodcutting Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Fletching")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(14, "1", "Logs - 10 arrow shafts"));
				skillMenuEntries.add(new SkillMenuItem(632, "15", "Oak logs - 15 arrow shafts"));
				skillMenuEntries.add(new SkillMenuItem(633, "30", "Willow logs - 20 arrow shafts"));
				skillMenuEntries.add(new SkillMenuItem(634, "45", "Maple logs - 25 arrow shafts"));
				skillMenuEntries.add(new SkillMenuItem(635, "60", "Yew logs - 30 arrow shafts"));
				skillMenuEntries.add(new SkillMenuItem(636, "75", "Magic logs - 35 arrow shafts"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(11, "1", "Bronze Arrows"));
				skillMenuEntries.add(new SkillMenuItem(638, "15", "Iron Arrows"));
				skillMenuEntries.add(new SkillMenuItem(640, "30", "Steel Arrows"));
				skillMenuEntries.add(new SkillMenuItem(786, "34", "Oyster pearl bolts"));
				skillMenuEntries.add(new SkillMenuItem(642, "45", "Mithril Arrows"));
				skillMenuEntries.add(new SkillMenuItem(644, "60", "Adamantite Arrows"));
				skillMenuEntries.add(new SkillMenuItem(646, "75", "Rune Arrows"));
			} else if (curTab == 2) {
				skillMenuEntries.add(new SkillMenuItem(189, "5", "Shortbows"));
				skillMenuEntries.add(new SkillMenuItem(188, "10", "Longbows"));
				skillMenuEntries.add(new SkillMenuItem(649, "20", "Oak shortbows"));
				skillMenuEntries.add(new SkillMenuItem(648, "25", "Oak longbows"));
				skillMenuEntries.add(new SkillMenuItem(651, "35", "Willow shortbows"));
				skillMenuEntries.add(new SkillMenuItem(650, "40", "Willow longbows"));
				skillMenuEntries.add(new SkillMenuItem(653, "50", "Maple shortbows"));
				skillMenuEntries.add(new SkillMenuItem(652, "55", "Maple longbows"));
				skillMenuEntries.add(new SkillMenuItem(655, "65", "Yew shortbows"));
				skillMenuEntries.add(new SkillMenuItem(654, "70", "Yew longbows"));
				skillMenuEntries.add(new SkillMenuItem(657, "80", "Magic shortbows"));
				skillMenuEntries.add(new SkillMenuItem(656, "85", "Magic longbows"));
			} else if (curTab == 3) {
				skillMenuEntries.add(new SkillMenuItem(1013, "1", "Bronze throwing dart"));
				skillMenuEntries.add(new SkillMenuItem(1015, "22", "Iron throwing dart"));
				skillMenuEntries.add(new SkillMenuItem(1024, "37", "Steel throwing dart"));
				skillMenuEntries.add(new SkillMenuItem(1068, "52", "Mithril throwing dart"));
				skillMenuEntries.add(new SkillMenuItem(1069, "67", "Adamantite throwing dart"));
				skillMenuEntries.add(new SkillMenuItem(1070, "82", "Rune throwing dart"));
			} else if (curTab == 4) {
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1376, "99", "Fletching Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Fishing")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(349, "1", "Shrimp - Small Fishing Net"));
				skillMenuEntries.add(new SkillMenuItem(354, "5", "Sardine - Fishing Rod and Bait"));
				skillMenuEntries.add(new SkillMenuItem(717, "10", "Giant Carp - Fishing Rod and Red vine worms"));
				skillMenuEntries.add(new SkillMenuItem(361, "10", "Herring - Fishing Rod and Bait"));
				skillMenuEntries.add(new SkillMenuItem(351, "15", "Anchovies - Small Fishing Net"));
				skillMenuEntries.add(new SkillMenuItem(552, "16", "Mackerel - Big Fishing Net"));
				skillMenuEntries.add(new SkillMenuItem(358, "20", "Trout - Fly Fishing Rod and Feathers"));
				skillMenuEntries.add(new SkillMenuItem(550, "23", "Cod - Big Fishing Net"));
				skillMenuEntries.add(new SkillMenuItem(363, "25", "Pike - Fishing Rod and Bait"));
				skillMenuEntries.add(new SkillMenuItem(356, "30", "Salmon - Fly Fishing Rod and Feathers"));
				skillMenuEntries.add(new SkillMenuItem(366, "35", "Tuna - Harpoon"));
				skillMenuEntries.add(new SkillMenuItem(372, "40", "Lobster - Lobster Pot"));
				skillMenuEntries.add(new SkillMenuItem(554, "46", "Bass - Big Fishing Net"));
				skillMenuEntries.add(new SkillMenuItem(369, "50", "Swordfish - Harpoon"));
				skillMenuEntries.add(new SkillMenuItem(545, "76", "Sharks - Harpoon"));
				skillMenuEntries.add(new SkillMenuItem(1192, "79", "Sea Turtle - Fishing Trawler"));
				skillMenuEntries.add(new SkillMenuItem(1190, "81", "Manta Ray - Fishing Trawler"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(379, "68", "Fishing Guild"));
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1380, "99", "Fishing Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Firemaking")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(14, "1", "Normal logs"));
				if (Config.S_CUSTOM_FIREMAKING) {
					skillMenuEntries.add(new SkillMenuItem(632, "15", "Oak logs"));
					skillMenuEntries.add(new SkillMenuItem(633, "30", "Willow logs"));
					skillMenuEntries.add(new SkillMenuItem(634, "45", "Maple logs"));
					skillMenuEntries.add(new SkillMenuItem(635, "60", "Yew logs"));
					skillMenuEntries.add(new SkillMenuItem(636, "75", "Magic logs"));
				}
			} else if (curTab == 1) {
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1520, "99", "Firemaking Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Crafting")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(16, "1", "Leather Gloves"));
				skillMenuEntries.add(new SkillMenuItem(17, "7", "Boots"));
				skillMenuEntries.add(new SkillMenuItem(15, "14", "Leather Armour"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(135, "1", "Pot"));
				skillMenuEntries.add(new SkillMenuItem(251, "4", "Pie Dish"));
				skillMenuEntries.add(new SkillMenuItem(341, "7", "Bowl"));
			} else if (curTab == 2) {
				skillMenuEntries.add(new SkillMenuItem(894, "10", "Opal"));
				skillMenuEntries.add(new SkillMenuItem(893, "13", "Jade"));
				skillMenuEntries.add(new SkillMenuItem(892, "16", "Red Topaz"));
				skillMenuEntries.add(new SkillMenuItem(164, "20", "Sapphire"));
				skillMenuEntries.add(new SkillMenuItem(163, "27", "Emerald"));
				skillMenuEntries.add(new SkillMenuItem(162, "34", "Ruby"));
				skillMenuEntries.add(new SkillMenuItem(161, "43", "Diamond"));
				skillMenuEntries.add(new SkillMenuItem(523, "55", "Dragonstone"));
			} else if (curTab == 3) {
				skillMenuEntries.add(new SkillMenuItem(283, "5", "Gold Ring"));
				skillMenuEntries.add(new SkillMenuItem(288, "6", "Gold Necklace"));
				skillMenuEntries.add(new SkillMenuItem(296, "8", "Gold Amulet"));
				skillMenuEntries.add(new SkillMenuItem(284, "8", "Sapphire Ring"));
				if (Config.S_WANT_CUSTOM_SPRITES) skillMenuEntries.add(new SkillMenuItem(1503, "9", "Gold Crown"));
				skillMenuEntries.add(new SkillMenuItem(289, "10", "Sapphire Necklace"));
				skillMenuEntries.add(new SkillMenuItem(297, "13", "Sapphire Amulet"));
				if (Config.S_WANT_CUSTOM_SPRITES) skillMenuEntries.add(new SkillMenuItem(1504, "15", "Sapphire Crown"));
				skillMenuEntries.add(new SkillMenuItem(44, "16", "Holy Symbol"));
				skillMenuEntries.add(new SkillMenuItem(1027, "16", "Unholy Symbol"));
				skillMenuEntries.add(new SkillMenuItem(285, "18", "Emerald Ring"));
				skillMenuEntries.add(new SkillMenuItem(290, "24", "Emerald Necklace"));
				skillMenuEntries.add(new SkillMenuItem(298, "30", "Emerald Amulet"));
				if (Config.S_WANT_CUSTOM_SPRITES) skillMenuEntries.add(new SkillMenuItem(1321, "30", "Opal Ring"));
				skillMenuEntries.add(new SkillMenuItem(286, "30", "Ruby Ring"));
				if (Config.S_WANT_CUSTOM_SPRITES) skillMenuEntries.add(new SkillMenuItem(1505, "36", "Emerald Crown"));
				skillMenuEntries.add(new SkillMenuItem(291, "40", "Ruby Necklace"));
				skillMenuEntries.add(new SkillMenuItem(287, "42", "Diamond Ring"));
				skillMenuEntries.add(new SkillMenuItem(299, "50", "Ruby Amulet"));
				skillMenuEntries.add(new SkillMenuItem(543, "54", "Dragonstone Ring"));
				skillMenuEntries.add(new SkillMenuItem(292, "56", "Diamond Necklace"));
				if (Config.S_WANT_CUSTOM_SPRITES) skillMenuEntries.add(new SkillMenuItem(1506, "60", "Ruby Crown"));
				skillMenuEntries.add(new SkillMenuItem(300, "70", "Diamond Amulet"));
				skillMenuEntries.add(new SkillMenuItem(544, "72", "Dragonstone Necklace"));
				skillMenuEntries.add(new SkillMenuItem(524, "80", "Dragonstone Amulet"));
				if (Config.S_WANT_CUSTOM_SPRITES) skillMenuEntries.add(new SkillMenuItem(1507, "84", "Diamond Crown"));
				if (Config.S_WANT_CUSTOM_SPRITES) skillMenuEntries.add(new SkillMenuItem(1508, "93", "Dragonstone Crown"));
			} else if (curTab == 4) {
				skillMenuEntries.add(new SkillMenuItem(207, "1", "Ball of Wool"));
				skillMenuEntries.add(new SkillMenuItem(676, "10", "Bow String"));
			} else if (curTab == 5) {
				skillMenuEntries.add(new SkillMenuItem(623, "1", "Molten Glass"));
				skillMenuEntries.add(new SkillMenuItem(620, "1", "Beer Glass"));
				skillMenuEntries.add(new SkillMenuItem(1018, "10", "Lens"));
				skillMenuEntries.add(new SkillMenuItem(465, "33", "Vial"));
				skillMenuEntries.add(new SkillMenuItem(611, "46", "Orb"));
			} else if (curTab == 6) {
				skillMenuEntries.add(new SkillMenuItem(616, "54", "Battlestaff of Water"));
				skillMenuEntries.add(new SkillMenuItem(618, "58", "Battlestaff of Earth"));
				skillMenuEntries.add(new SkillMenuItem(615, "62", "Battlestaff of Fire"));
				skillMenuEntries.add(new SkillMenuItem(617, "66", "Battlestaff of Air"));
			} else if (curTab == 7) {
				if (Config.S_WANT_RUNECRAFT && Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1385, "1", "Uncharged talismans"));
				}
				skillMenuEntries.add(new SkillMenuItem(779, "34", "Oyster Pearls"));
				skillMenuEntries.add(new SkillMenuItem(191, "40", "Crafting Guild"));
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1347, "90", "King Black Dragon Scale"));
					skillMenuEntries.add(new SkillMenuItem(1384, "99", "Crafting Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Smithing")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(169, "1", "Bronze - 1 Tin ore & 1 copper ore"));
				skillMenuEntries.add(new SkillMenuItem(170, "15", "Iron - 1 Iron ore - 50% Chance of success"));
				skillMenuEntries.add(new SkillMenuItem(384, "20", "Silver -  1 Silver ore"));
				skillMenuEntries.add(new SkillMenuItem(171, "30", "Steel - 2 Coal and 1 iron ore"));
				skillMenuEntries.add(new SkillMenuItem(172, "40", "Gold -  1 Gold ore"));
				skillMenuEntries.add(new SkillMenuItem(173, "50", "Mithril - 4 Coal & 1 mithril ore"));
				skillMenuEntries.add(new SkillMenuItem(174, "70", "Adamant - 6 Coal & 1 adamantite ore"));
				skillMenuEntries.add(new SkillMenuItem(408, "85", "Runite - 8 Coal & 1 runite ore"));
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1365, "90", "Dragon - 1 dragon axe or 1 dragon sword"));
					skillMenuEntries.add(new SkillMenuItem(1383, "99", "Smithing Cape"));
				}
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(62, "1", "Bronze Daggers - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(87, "1", "Bronze Axes - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(94, "2", "Bronze Maces - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(104, "3", "Bronze Medium Helms - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(66, "4", "Bronze Short Swords - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(1062, "4", "Bronze Dart Tips - 1 Bar makes 7"));
				skillMenuEntries.add(new SkillMenuItem(979, "4", "Bronze Wire - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(82, "5", "Bronze Scimitars - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(669, "5", "Bronze Arrowheads - 1 Bar makes 10"));
				skillMenuEntries.add(new SkillMenuItem(70, "6", "Bronze Longswords - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(108, "7", "Bronze Full Helms - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(1076, "7", "Bronze Throwing Knives - 1 Bar makes 2"));
				skillMenuEntries.add(new SkillMenuItem(124, "8", "Bronze Square Shields - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(205, "10", "Bronze Battleaxes - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(113, "11", "Bronze Chainbodies - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(128, "12", "Bronze Kiteshields - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(76, "14", "Bronze Two-handed Swords - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(214, "16", "Bronze Plated Skirts - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(206, "16", "Bronze Platelegs - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(117, "18", "Bronze Platebodies - 5 Bars"));
			} else if (curTab == 2) {
				skillMenuEntries.add(new SkillMenuItem(28, "15", "Iron Daggers - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(12, "16", "Iron Axes - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(0, "17", "Iron Maces - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(5, "18", "Iron Medium Helms - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(1, "19", "Iron Short Swords - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(1063, "19", "Iron Dart Tips - 1 Bar makes 7"));
				skillMenuEntries.add(new SkillMenuItem(83, "20", "Iron Scimitars - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(670, "20", "Iron Arrowheads - 1 Bar makes 10"));
				skillMenuEntries.add(new SkillMenuItem(71, "21", "Iron Longswords - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(6, "22", "Iron Full Helms - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(1075, "22", "Iron Throwing Knives - 1 Bar makes 2"));
				skillMenuEntries.add(new SkillMenuItem(3, "23", "Iron Square Shields - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(89, "25", "Iron Battleaxes - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(7, "26", "Iron Chainbodies - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(2, "27", "Iron Kiteshields - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(77, "29", "Iron Two-handed Swords - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(215, "31", "Iron Plated Skirts - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(9, "31", "Iron Platelegs - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(8, "33", "Iron Platebodies - 5 Bars"));
			} else if (curTab == 3) {
				skillMenuEntries.add(new SkillMenuItem(63, "30", "Steel Daggers - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(88, "31", "Steel Axes - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(95, "32", "Steel Maces - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(105, "33", "Steel Medium Helms - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(67, "34", "Steel Short Swords - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(1064, "34", "Steel Dart Tips - 1 Bar makes 7"));
				skillMenuEntries.add(new SkillMenuItem(419, "34", "Nails - 1 Bar makes 2"));
				skillMenuEntries.add(new SkillMenuItem(84, "35", "Steel Scimitars - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(671, "35", "Steel Arrowheads - 1 Bar makes 10"));
				skillMenuEntries.add(new SkillMenuItem(1041, "35", "Cannonball - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(72, "36", "Steel Longswords - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(109, "37", "Steel Full Helms - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(1077, "37", "Steel Throwing Knives - 1 Bar makes 2"));
				skillMenuEntries.add(new SkillMenuItem(125, "38", "Steel Square Shields - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(90, "40", "Steel Battleaxes - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(114, "41", "Steel Chainbodies - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(129, "42", "Steel Kiteshields - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(78, "44", "Steel Two-handed Swords - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(225, "46", "Steel Plated Skirts - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(121, "46", "Steel Platelegs - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(118, "48", "Steel Platebodies - 5 Bars"));
			} else if (curTab == 4) {
				skillMenuEntries.add(new SkillMenuItem(64, "50", "Mithril Daggers - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(203, "51", "Mithril Axes - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(96, "52", "Mithril Maces - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(106, "53", "Mithril Medium Helms - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(68, "54", "Mithril Short Swords - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(1065, "54", "Mithril Dart Tips - 1 Bar makes 7"));
				skillMenuEntries.add(new SkillMenuItem(85, "55", "Mithril Scimitars - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(672, "55", "Mithril Arrowheads - 1 Bar makes 10"));
				skillMenuEntries.add(new SkillMenuItem(73, "56", "Mithril Longswords - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(110, "57", "Mithril Full Helms - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(1078, "57", "Mithril Throwing Knives - 1 Bar makes 2"));
				skillMenuEntries.add(new SkillMenuItem(126, "58", "Mithril Square Shields - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(91, "60", "Mithril Battleaxes - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(115, "61", "Mithril Chainbodies - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(130, "62", "Mithril Kiteshields - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(79, "64", "Mithril Two-handed Swords - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(226, "66", "Mithril Plated Skirts - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(122, "66", "Mithril Platelegs - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(119, "68", "Mithril Platebodies - 5 Bars"));
			} else if (curTab == 5) {
				skillMenuEntries.add(new SkillMenuItem(65, "70", "Adamant Daggers - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(204, "71", "Adamant Axes - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(97, "72", "Adamant Maces - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(107, "73", "Adamant Medium Helms - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(69, "74", "Adamant Short Swords - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(1066, "74", "Adamantite Dart Tips - 1 Bar makes 7"));
				skillMenuEntries.add(new SkillMenuItem(86, "75", "Adamant Scimitars - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(673, "75", "Adamant Arrowheads - 1 Bar makes 10"));
				skillMenuEntries.add(new SkillMenuItem(71, "76", "Adamant Longswords - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(111, "77", "Adamant Full Helms - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(1079, "77", "Adamant Throwing Knives - 1 Bar makes 2"));
				skillMenuEntries.add(new SkillMenuItem(127, "78", "Adamant Square Shields - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(92, "80", "Adamant Battleaxes - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(116, "81", "Adamant Chainbodies - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(131, "82", "Adamant Kiteshields - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(80, "84", "Adamant Two-handed Swords - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(227, "86", "Adamant Plated Skirts - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(123, "86", "Adamant Platelegs - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(120, "88", "Adamant Platebodies - 5 Bars"));
			} else if (curTab == 6) {
				skillMenuEntries.add(new SkillMenuItem(396, "85", "Rune Daggers - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(405, "86", "Rune Axes - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(98, "87", "Rune Maces - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(399, "88", "Rune Medium Helms - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(397, "89", "Rune Short Swords - 1 Bar"));
				skillMenuEntries.add(new SkillMenuItem(1067, "89", "Rune Dart Tips - 1 Bar makes 7"));
				skillMenuEntries.add(new SkillMenuItem(398, "90", "Rune Scimitars - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(674, "90", "Rune Arrowheads - 1 Bar makes 10"));
				skillMenuEntries.add(new SkillMenuItem(75, "91", "Rune Longswords - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(112, "92", "Rune Full Helms - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(1080, "92", "Rune Throwing Knives - 1 Bar makes 2"));
				skillMenuEntries.add(new SkillMenuItem(403, "93", "Rune Square Shields - 2 Bars"));
				skillMenuEntries.add(new SkillMenuItem(93, "95", "Rune Battleaxes - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(400, "96", "Rune Chainbodies - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(404, "97", "Rune Kiteshields - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(81, "99", "Rune Two-handed Swords - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(406, "99", "Rune Plated Skirts - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(402, "99", "Rune Platelegs - 3 Bars"));
				skillMenuEntries.add(new SkillMenuItem(401, "99", "Rune Platebodies - 5 Bars"));
			} else if (curTab == 7) {
				skillMenuEntries.add(new SkillMenuItem(1278, "60", "Dragon Square Shield - Smith the 2 halves together"));
			}
		}
		if (mc.getSkillGuideChosen().equals("Mining")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(149, "1", "Clay"));
				skillMenuEntries.add(new SkillMenuItem(150, "1", "Copper Ore"));
				skillMenuEntries.add(new SkillMenuItem(202, "1", "Tin Ore"));
				skillMenuEntries.add(new SkillMenuItem(266, "10", "Blurite Ore"));
				skillMenuEntries.add(new SkillMenuItem(151, "15", "Iron Ore"));
				skillMenuEntries.add(new SkillMenuItem(383, "20", "Silver Ore"));
				skillMenuEntries.add(new SkillMenuItem(155, "30", "Coal"));
				skillMenuEntries.add(new SkillMenuItem(152, "40", "Gold"));
				skillMenuEntries.add(new SkillMenuItem(889, "40", "Gem Rocks"));
				skillMenuEntries.add(new SkillMenuItem(153, "55", "Mithril Ore"));
				skillMenuEntries.add(new SkillMenuItem(154, "70", "Adamantite Ore"));
				skillMenuEntries.add(new SkillMenuItem(409, "85", "Runite Ore"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(156, "1", "Bronze Pickaxe"));
				skillMenuEntries.add(new SkillMenuItem(1258, "1", "Iron Pickaxe"));
				skillMenuEntries.add(new SkillMenuItem(1259, "6", "Steel Pickaxe"));
				skillMenuEntries.add(new SkillMenuItem(1260, "21", "Mithril Pickaxe"));
				skillMenuEntries.add(new SkillMenuItem(1261, "31", "Adamant Pickaxe"));
				skillMenuEntries.add(new SkillMenuItem(1262, "41", "Rune Pickaxe"));
			} else if (curTab == 2) {
				skillMenuEntries.add(new SkillMenuItem(1259, "60", "Mining Guild"));
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1377, "99", "Mining Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Herblaw")) {
			boolean harvesting = Config.S_WANT_HARVESTING && Config.S_WANT_CUSTOM_SPRITES;
			boolean runecraft = Config.S_WANT_RUNECRAFT && Config.S_WANT_CUSTOM_SPRITES;
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(444, "3", EntityHandler.getItemDef(444).name));
				skillMenuEntries.add(new SkillMenuItem(445, "5", EntityHandler.getItemDef(445).name));
				skillMenuEntries.add(new SkillMenuItem(446, "11", EntityHandler.getItemDef(446).name));
				skillMenuEntries.add(new SkillMenuItem(447, "20", EntityHandler.getItemDef(447).name));
				skillMenuEntries.add(new SkillMenuItem(448, "25", EntityHandler.getItemDef(448).name));
				skillMenuEntries.add(new SkillMenuItem(449, "40", EntityHandler.getItemDef(449).name));
				skillMenuEntries.add(new SkillMenuItem(450, "48", EntityHandler.getItemDef(450).name));
				skillMenuEntries.add(new SkillMenuItem(451, "54", EntityHandler.getItemDef(451).name));
				skillMenuEntries.add(new SkillMenuItem(452, "65", EntityHandler.getItemDef(452).name));
				skillMenuEntries.add(new SkillMenuItem(453, "70", EntityHandler.getItemDef(453).name));
				skillMenuEntries.add(new SkillMenuItem(934, "75", EntityHandler.getItemDef(934).name));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(474, "3", "Attack potion - Guam leaf & eye of newt"));
				skillMenuEntries.add(new SkillMenuItem(566, "5", "Cure poison potion - Marrentill & ground unicorn horn"));
				skillMenuEntries.add(new SkillMenuItem(1176, "10", "Explosive compound - Nitro & nitrate & charcoal & a. root"));
				skillMenuEntries.add(new SkillMenuItem(222, "12", "Strength potion - Tarromin & limpwurt root"));
				if (runecraft) skillMenuEntries.add(new SkillMenuItem(1411, "12", "Runecraft potion - Marrentill & 10 fish oil"));
				skillMenuEntries.add(new SkillMenuItem(1053, "14", "Ogre potion - Guam leaf & jangerberries & ground bat bones"));
				skillMenuEntries.add(new SkillMenuItem(477, "22", "Stat restore potion - Harralander & red spiders' eggs"));
				skillMenuEntries.add(new SkillMenuItem(588, "25", "Blamish oil - Harralander & blamish snail slime"));
				skillMenuEntries.add(new SkillMenuItem(480, "30", "Defense potion - Ranarr weed & white berries"));
				skillMenuEntries.add(new SkillMenuItem(483, "38", "Restore prayer potion - Ranarr weed & snape grass"));
				skillMenuEntries.add(new SkillMenuItem(486, "45", "Super attack potion - Irit leaf & eye of newt"));
				skillMenuEntries.add(new SkillMenuItem(1253, "45", "Gujuo potion - Snake weed & ardrigal"));
				skillMenuEntries.add(new SkillMenuItem(569, "48", "Poison antidote - Irit leaf & ground unicorn horn"));
				skillMenuEntries.add(new SkillMenuItem(489, "50", "Fishing potion - Avantoe & snape grass"));
				skillMenuEntries.add(new SkillMenuItem(492, "55", "Super strength potion - Kwuarm & limpwurt root"));
				if (runecraft) skillMenuEntries.add(new SkillMenuItem(1414, "57", "Super Runecraft potion - Avantoe & 10 fish oil"));
				skillMenuEntries.add(new SkillMenuItem(572, "60", "Weapon poison potion - Kwuarm & ground blue dragon scale"));
				skillMenuEntries.add(new SkillMenuItem(495, "66", "Super defense potion - Cadantine & white berries"));
				skillMenuEntries.add(new SkillMenuItem(498, "72", "Ranging potion - Dwarf weed & wine of zamorak"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1468, "76", "Magic potion - Dwarf weed & wine of saradomin"));
				skillMenuEntries.add(new SkillMenuItem(963, "78", "Potion of zamorak - Torstol & jangerberries"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1471, "81", "Potion of Saradomin - Torstol & sliced dragonfruit"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1474, "83", "Super ranging potion - Ranging potion & half coconut"));
				if (harvesting) skillMenuEntries.add(new SkillMenuItem(1477, "85", "Super magic potion - Magic potion & half coconut"));
			} else if (curTab == 2) {
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1521, "99", "Herblaw Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Agility")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(896, "1", "Gnome Stronghold Agility Course"));
				skillMenuEntries.add(new SkillMenuItem(981, "1", "Gnomeball minigame"));
				skillMenuEntries.add(new SkillMenuItem(90, "35", "Barbarian Outpost Agility Course"));
				skillMenuEntries.add(new SkillMenuItem(412, "52", "Wilderness Agility Course"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(410, "5", "Falador West enter handholds"));
				skillMenuEntries.add(new SkillMenuItem(410, "10", "Brimhaven treeswing"));
				skillMenuEntries.add(new SkillMenuItem(410, "15", "Edgeville dungeon ropeswing"));
				skillMenuEntries.add(new SkillMenuItem(410, "15", "Yanille North climbing rocks"));
				skillMenuEntries.add(new SkillMenuItem(410, "18", "Yanille watchtower handholds"));
				skillMenuEntries.add(new SkillMenuItem(410, "20", "North-west of McGruber's Woods log balance"));
				skillMenuEntries.add(new SkillMenuItem(410, "25", "Lum river stepping stone"));
				skillMenuEntries.add(new SkillMenuItem(410, "25", "Glough's watch tower"));
				skillMenuEntries.add(new SkillMenuItem(410, "30", "Southern Gu'Tanoth bridge rock"));
				skillMenuEntries.add(new SkillMenuItem(410, "30", "West of Yanille tree swing"));
				skillMenuEntries.add(new SkillMenuItem(410, "32", "Ardougne river rock crossing"));
				skillMenuEntries.add(new SkillMenuItem(410, "32", "Cairn Isle rock climb"));
				skillMenuEntries.add(new SkillMenuItem(410, "32", "Southeastern Karamja stepping stones"));
				skillMenuEntries.add(new SkillMenuItem(410, "32", "Ah Za Roon temple pile of rubble"));
				skillMenuEntries.add(new SkillMenuItem(410, "32", "Tomb of Bervirius entrance"));
				skillMenuEntries.add(new SkillMenuItem(410, "32", "East Karamjan River log balance"));
				skillMenuEntries.add(new SkillMenuItem(410, "35", "Barbarian outpost handholds"));
				skillMenuEntries.add(new SkillMenuItem(410, "40", "Yanille Agility Dungeon ledge"));
				skillMenuEntries.add(new SkillMenuItem(410, "40", "Falador West exit handholds"));
				skillMenuEntries.add(new SkillMenuItem(410, "45", "White Wolf Mountain vine climb"));
				skillMenuEntries.add(new SkillMenuItem(410, "49", "Yanille Agility Dungeon pipe"));
				skillMenuEntries.add(new SkillMenuItem(410, "50", "Kharazi Jungle cave entrance"));
				skillMenuEntries.add(new SkillMenuItem(410, "50", "Taverly stepping stones to Catherby"));
				skillMenuEntries.add(new SkillMenuItem(410, "55", "Entrana wall rubble"));
				skillMenuEntries.add(new SkillMenuItem(410, "57", "Yanille Agility Dungeon rope swing"));
				skillMenuEntries.add(new SkillMenuItem(410, "67", "Yanille Agility Dungeon pile of rubble"));
				skillMenuEntries.add(new SkillMenuItem(410, "67", "Lava Maze stepping stones"));
				skillMenuEntries.add(new SkillMenuItem(410, "70", "Taverly Dungeon pipe crawl"));
			} else if (curTab == 2) {
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1518, "99", "Agility Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equals("Thieving")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuNPC(11, "1", "Man"));
				skillMenuEntries.add(new SkillMenuNPC(63, "10", "Farmer"));
				skillMenuEntries.add(new SkillMenuNPC(86, "25", "Warrior"));
				skillMenuEntries.add(new SkillMenuNPC(722, "25", "Workman"));
				skillMenuEntries.add(new SkillMenuNPC(342, "32", "Rogue"));
				skillMenuEntries.add(new SkillMenuNPC(65, "40", "Guard"));
				skillMenuEntries.add(new SkillMenuNPC(322, "55", "Knight"));
				skillMenuEntries.add(new SkillMenuNPC(574, "65", "Watchman"));
				skillMenuEntries.add(new SkillMenuNPC(323, "70", "Paladin"));
				skillMenuEntries.add(new SkillMenuNPC(582, "75", "Gnome"));
				skillMenuEntries.add(new SkillMenuNPC(324, "80", "Hero"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(739, "5", "Tea Stall"));
				skillMenuEntries.add(new SkillMenuItem(330, "5", "Baker's Stall"));
				skillMenuEntries.add(new SkillMenuItem(1061, "15", "Rock Cake Stall"));
				skillMenuEntries.add(new SkillMenuItem(200, "20", "Silk Stall"));
				skillMenuEntries.add(new SkillMenuItem(146, "35", "Fur Stall"));
				skillMenuEntries.add(new SkillMenuItem(383, "50", "Silver Stall"));
				skillMenuEntries.add(new SkillMenuItem(707, "65", "Spice Stall"));
				skillMenuEntries.add(new SkillMenuItem(157, "75", "Gem Stall"));
			} else if (curTab == 2) {
				skillMenuEntries.add(new SkillMenuItem(10, "13", "Ardougne Market house, Pirate's Hideout, Axe Hut"));
				skillMenuEntries.add(new SkillMenuItem(40, "28", "Ardougne Market house, Pirate's Hideout"));
				skillMenuEntries.add(new SkillMenuItem(10, "43", "Ardougne Market house, Axe Hut"));
				skillMenuEntries.add(new SkillMenuItem(671, "47", "Hemenster"));
				skillMenuEntries.add(new SkillMenuItem(619, "59", "Ardougne Chaos Druid Tower"));
				skillMenuEntries.add(new SkillMenuItem(545, "72", "Ardougne Castle"));
			} else if (curTab == 3) {
				skillMenuEntries.add(new SkillMenuItem(10, "7", "Ardougne Market house"));
				skillMenuEntries.add(new SkillMenuItem(40, "16", "Ardougne Market house"));
				skillMenuEntries.add(new SkillMenuItem(705, "16", "Ardougne Market house, Handelmort's mansion"));
				skillMenuEntries.add(new SkillMenuItem(155, "31", "Ardougne sewer mine"));
				skillMenuEntries.add(new SkillMenuItem(90, "32", "Axe Hut"));
				skillMenuEntries.add(new SkillMenuItem(262, "39", "Pirate's Hideout"));
				skillMenuEntries.add(new SkillMenuItem(444, "46", "Ardougne Chaos Druid Tower"));
				skillMenuEntries.add(new SkillMenuItem(545, "61", "Ardougne Castle upstairs"));
				skillMenuEntries.add(new SkillMenuItem(714, "82", "Yanille Agility Dungeon"));
			} else if (curTab == 4) {
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1375, "99", "Thieving Cape"));
				}
			}
		}
		if (mc.getSkillGuideChosen().equalsIgnoreCase("Runecraft")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(33, "1", "Air Rune"));
				skillMenuEntries.add(new SkillMenuItem(35, "1", "Mind Rune"));
				skillMenuEntries.add(new SkillMenuItem(32, "5", "Water Rune"));
				skillMenuEntries.add(new SkillMenuItem(34, "9", "Earth Rune"));
				skillMenuEntries.add(new SkillMenuItem(31, "14", "Fire Rune"));
				skillMenuEntries.add(new SkillMenuItem(36, "20", "Body Rune"));
				skillMenuEntries.add(new SkillMenuItem(46, "27", "Cosmic Rune"));
				skillMenuEntries.add(new SkillMenuItem(41, "35", "Chaos Rune"));
				skillMenuEntries.add(new SkillMenuItem(40, "44", "Nature Rune"));
				//skillItems.add(new SkillItem(42, "54","Law Rune"));
				//skillItems.add(new SkillItem(38, "65","Death Rune"));
				//skillItems.add(new SkillItem(619, "77","Blood Rune"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(33, "11", "Air Rune x2"));
				skillMenuEntries.add(new SkillMenuItem(35, "14", "Mind Rune x2"));
				skillMenuEntries.add(new SkillMenuItem(32, "19", "Water Rune x2"));
				skillMenuEntries.add(new SkillMenuItem(33, "22", "Air Rune x3"));
				skillMenuEntries.add(new SkillMenuItem(34, "26", "Earth Rune x2"));
				skillMenuEntries.add(new SkillMenuItem(35, "28", "Mind Rune x3"));
				skillMenuEntries.add(new SkillMenuItem(33, "33", "Air Rune x4"));
				skillMenuEntries.add(new SkillMenuItem(31, "35", "Fire Rune x2"));
				skillMenuEntries.add(new SkillMenuItem(32, "38", "Water Rune x3"));
				skillMenuEntries.add(new SkillMenuItem(35, "42", "Mind Rune x4"));
				skillMenuEntries.add(new SkillMenuItem(33, "44", "Air Rune x5"));
				skillMenuEntries.add(new SkillMenuItem(36, "46", "Body Rune x2"));
				skillMenuEntries.add(new SkillMenuItem(34, "52", "Earth Rune x3"));
				skillMenuEntries.add(new SkillMenuItem(33, "55", "Air Rune x6"));
				skillMenuEntries.add(new SkillMenuItem(35, "56", "Mind Rune x5"));
				skillMenuEntries.add(new SkillMenuItem(32, "57", "Water Rune x4"));
				skillMenuEntries.add(new SkillMenuItem(46, "59", "Cosmic Rune x2"));
				skillMenuEntries.add(new SkillMenuItem(33, "66", "Air Rune x7"));
				skillMenuEntries.add(new SkillMenuItem(35, "70", "Mind Rune x6"));
				skillMenuEntries.add(new SkillMenuItem(31, "70", "Fire Rune x3"));
				skillMenuEntries.add(new SkillMenuItem(41, "74", "Chaos Rune x2"));
				skillMenuEntries.add(new SkillMenuItem(32, "76", "Water Rune x5"));
				skillMenuEntries.add(new SkillMenuItem(33, "77", "Air Rune x8"));
				skillMenuEntries.add(new SkillMenuItem(34, "78", "Earth Rune x4"));
				skillMenuEntries.add(new SkillMenuItem(35, "84", "Mind Rune x7"));
				skillMenuEntries.add(new SkillMenuItem(33, "88", "Air Rune x9"));
				skillMenuEntries.add(new SkillMenuItem(40, "91", "Nature Rune x2"));
				skillMenuEntries.add(new SkillMenuItem(36, "92", "Body Rune x3"));
				skillMenuEntries.add(new SkillMenuItem(32, "95", "Water Rune x6"));
				skillMenuEntries.add(new SkillMenuItem(35, "98", "Mind Rune x8"));
				skillMenuEntries.add(new SkillMenuItem(33, "99", "Air Rune x10"));
			}
			else if (curTab == 2) {
				skillMenuEntries.add(new SkillMenuItem(1300, "1", "Imbue Air talisman"));
				skillMenuEntries.add(new SkillMenuItem(1301, "2", "Imbue Mind talisman"));
				skillMenuEntries.add(new SkillMenuItem(1302, "5", "Imbue Water talisman"));
				skillMenuEntries.add(new SkillMenuItem(1386, "8", "Cursed air talisman"));
				skillMenuEntries.add(new SkillMenuItem(1387, "9", "Cursed mind talisman"));
				skillMenuEntries.add(new SkillMenuItem(1303, "9", "Imbue Earth talisman"));
				skillMenuEntries.add(new SkillMenuItem(1388, "12", "Cursed water talisman"));
				skillMenuEntries.add(new SkillMenuItem(1304, "14", "Imbue Fire talisman"));
				skillMenuEntries.add(new SkillMenuItem(1398, "15", "Enfeebled air talisman"));
				skillMenuEntries.add(new SkillMenuItem(1399, "16", "Enfeebled mind talisman"));
				skillMenuEntries.add(new SkillMenuItem(1389, "16", "Cursed earth talisman"));
				skillMenuEntries.add(new SkillMenuItem(1400, "19", "Enfeebled water talisman"));
				skillMenuEntries.add(new SkillMenuItem(1305, "20", "Imbue Body talisman"));
				skillMenuEntries.add(new SkillMenuItem(1390, "21", "Cursed fire talisman"));
				skillMenuEntries.add(new SkillMenuItem(1401, "23", "Enfeebled earth talisman"));
				skillMenuEntries.add(new SkillMenuItem(1306, "27", "Imbue Cosmic talisman"));
				skillMenuEntries.add(new SkillMenuItem(1391, "27", "Cursed body talisman"));
				skillMenuEntries.add(new SkillMenuItem(1402, "28", "Enfeebled fire talisman"));
				skillMenuEntries.add(new SkillMenuItem(1392, "34", "Cursed cosmic talisman"));
				skillMenuEntries.add(new SkillMenuItem(1403, "34", "Enfeebled body talisman"));
				skillMenuEntries.add(new SkillMenuItem(1307, "35", "Imbue Chaos talisman"));
				skillMenuEntries.add(new SkillMenuItem(1404, "41", "Enfeebled cosmic talisman"));
				skillMenuEntries.add(new SkillMenuItem(1393, "42", "Cursed chaos talisman"));
				skillMenuEntries.add(new SkillMenuItem(1308, "44", "Imbue Nature talisman"));
				skillMenuEntries.add(new SkillMenuItem(1394, "51", "Cursed nature talisman"));
				skillMenuEntries.add(new SkillMenuItem(1405, "49", "Enfeebled chaos talisman"));
				skillMenuEntries.add(new SkillMenuItem(1406, "58", "Enfeebled nature talisman"));

			}
		}
		if (mc.getSkillGuideChosen().equalsIgnoreCase("Harvesting")) {
			if (curTab == 0) {
				skillMenuEntries.add(new SkillMenuItem(348, "1", "Potato"));
				skillMenuEntries.add(new SkillMenuItem(241, "5", "Onion"));
				skillMenuEntries.add(new SkillMenuItem(18, "7", "Cabbage"));
				skillMenuEntries.add(new SkillMenuItem(218, "9", "Garlic"));
				skillMenuEntries.add(new SkillMenuItem(320, "12", "Tomato"));
				skillMenuEntries.add(new SkillMenuItem(1353, "20", "Corn"));
				skillMenuEntries.add(new SkillMenuItem(1352, "30", "Red Cabbage"));
				skillMenuEntries.add(new SkillMenuItem(1354, "47", "White Pumpkin"));
				skillMenuEntries.add(new SkillMenuItem(1456, "50", "Sugar Cane"));
			} else if (curTab == 1) {
				skillMenuEntries.add(new SkillMenuItem(855, "15", "Lemon Tree"));
				skillMenuEntries.add(new SkillMenuItem(863, "21", "Lime Tree"));
				skillMenuEntries.add(new SkillMenuItem(1348, "27", "Apple Tree"));
				skillMenuEntries.add(new SkillMenuItem(249, "33", "Banana Palm"));
				skillMenuEntries.add(new SkillMenuItem(143, "36", "Grape Vine"));
				skillMenuEntries.add(new SkillMenuItem(857, "39", "Orange Tree"));
				skillMenuEntries.add(new SkillMenuItem(1349, "46", "Grapefruit Tree"));
				skillMenuEntries.add(new SkillMenuItem(861, "51", "Pineapple Plant"));
				skillMenuEntries.add(new SkillMenuItem(1350, "57", "Papaya Palm"));
				skillMenuEntries.add(new SkillMenuItem(1351, "68", "Coconut Palm"));
				skillMenuEntries.add(new SkillMenuItem(1457, "81", "Dragonfruit Tree"));
				skillMenuEntries.add(new SkillMenuItem(1465, "85", "Bless/Curse Grapes"));
			} else if (curTab == 2) {
				skillMenuEntries.add(new SkillMenuItem(236, "10", "Redberry Bush"));
				skillMenuEntries.add(new SkillMenuItem(55, "22", "Cadavaberry Bush"));
				skillMenuEntries.add(new SkillMenuItem(765, "36", "Dwellberry Bush"));
				skillMenuEntries.add(new SkillMenuItem(936, "48", "Jangerberry Bush"));
				skillMenuEntries.add(new SkillMenuItem(471, "59", "Whiteberry Bush"));
			} else if (curTab == 3) {
				skillMenuEntries.add(new SkillMenuItem(165, "9", "Herb - Guam (chance)"));
				skillMenuEntries.add(new SkillMenuItem(435, "14", "Herb - Marrentil (chance)"));
				skillMenuEntries.add(new SkillMenuItem(436, "19", "Herb - Tarromin (chance)"));
				skillMenuEntries.add(new SkillMenuItem(622, "23", "Sea weed"));
				skillMenuEntries.add(new SkillMenuItem(437, "26", "Herb - Harralander (chance)"));
				skillMenuEntries.add(new SkillMenuItem(438, "32", "Herb - Ranarr Weed (chance)"));
				skillMenuEntries.add(new SkillMenuItem(220, "42", "Limpwurt Root"));
				skillMenuEntries.add(new SkillMenuItem(439, "44", "Herb - Irit Leaf (chance)"));
				skillMenuEntries.add(new SkillMenuItem(440, "50", "Herb - Avantoe (chance)"));
				skillMenuEntries.add(new SkillMenuItem(441, "56", "Herb - Kwuarm (chance)"));
				skillMenuEntries.add(new SkillMenuItem(469, "61", "Snape Grass"));
				skillMenuEntries.add(new SkillMenuItem(442, "67", "Herb - Cadantine (chance)"));
				skillMenuEntries.add(new SkillMenuItem(443, "79", "Herb - Dwarf Weed (chance)"));
			} else if (curTab == 4) {
				if (Config.S_WANT_CUSTOM_SPRITES) {
					skillMenuEntries.add(new SkillMenuItem(1526, "99", "Harvesting Cape"));
				}
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

class SkillMenuEntry {

	private String levelReq, skillDetail;

	protected SkillMenuEntry(String levelReq, String skillDetail) {
		this.levelReq = levelReq;
		this.skillDetail = skillDetail;
	}

	public String getLevelReq() {
		return levelReq;
	}

	public String getSkillDetail() {
		return skillDetail;
	}
}

class SkillMenuItem extends SkillMenuEntry {

	private int itemID;

	public SkillMenuItem(int itemID, String level, String detail) {
		super(level, detail);
		this.itemID = itemID;
	}

	public int getItemID() {
		return itemID;
	}
}

class SkillMenuNPC extends SkillMenuEntry {
	private int npcID;

	public SkillMenuNPC(int npcID, String level, String detail) {
		super(level, detail);
		this.npcID = npcID;
	}

	public int getNpcID() {
		return npcID;
	}
}
