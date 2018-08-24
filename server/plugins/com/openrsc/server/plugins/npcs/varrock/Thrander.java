package com.openrsc.server.plugins.npcs.varrock;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Thrander implements TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	public static int THRANDER = 160;

	private int[] RED_FEATHER_HELMETS = { 108, 6, 109, 230, 110, 111, 112 };
	private int[] ORANGE_FEATHER_HELMETS = { 2144, 2146, 2148, 2150, 2152, 2154, 2156 };
	private int[] BLUE_FEATHER_HELMETS = { 2158, 2160, 2162, 2164, 2166, 2168, 2170 };
	private int[] PURPLE_FEATHER_HELMETS = { 2172, 2174, 2176, 2178, 2180, 2182, 2184 };
	private int[] YELLOW_FEATHER_HELMETS = { 2186, 2188, 2190, 2192, 2194, 2196, 2198 };
	private int[] GREEN_FEATHER_HELMETS = { 2200, 2202, 2204, 2206, 2208, 2210, 2212 };
	private int[] MATCHED_FEATHER_HELMETS = { 2214, 2216, 2218, 2220, 2222, 2224, 2226 };
	private int[] BLACK_FEATHER_HELMETS = { 2228, 2230, 2232, 2234, 2236, 2238, 2240 };
	private int[] WHITE_FEATHER_HELMETS = { 2242, 2244, 2246, 2248, 2250, 2252, 2254 };

	private int[] BRONZE_LARGE_HELMETS = { 108, 2144, 2158, 2172, 2186, 2200, 2214, 2228, 2242 };
	private int[] IRON_LARGE_HELMETS = { 6, 2146, 2160, 2174, 2188, 2202, 2216, 2230, 2244 };
	private int[] STEEL_LARGE_HELMETS = { 109, 2148, 2162, 2176, 2190, 2204, 2218, 2232, 2246 };
	private int[] BLACK_LARGE_HELMETS = { 230, 2150, 2164, 2178, 2192, 2206, 2220, 2234, 2248 };
	private int[] MITHRIL_LARGE_HELMETS = { 110, 2152, 2166, 2180, 2194, 2208, 2222, 2236, 2250 };
	private int[] ADAMANTITE_LARGE_HELMETS = { 111, 2154, 2168, 2182, 2196, 2210, 2224, 2238, 2252 };
	private int[] RUNE_LARGE_HELMETS = { 112, 2156, 2170, 2184, 2198, 2212, 2226, 2240, 2254 };

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == THRANDER;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Hello I'm Thrander the smith",
				"I'm an expert in armour modification",
				"Give me your armour designed for men",
				"And I can convert it into something more comfortable for a women",
				"And visa versa",
				"If you want me to modificate your helmet feather it will cost 500 gold",
				"Give me your helmet and pick the color of your choice");
	}

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == THRANDER && (getNewID(item) != -1 || inArray(item.getID(), 108, 6, 109, 230, 110, 111, 112, 2144, 2146, 2148, 2150, 2152, 2154, 2156,
				2158, 2160, 2162, 2164, 2166, 2168, 2170, 2172, 2174, 2176, 2178, 2180, 2182, 2184,
				2186, 2188, 2190, 2192, 2194, 2196, 2198, 2200, 2202, 2204, 2206, 2208, 2210, 2212,
				2214, 2216, 2218, 2220, 2222, 2224, 2226, 2228, 2230, 2232, 2234, 2236, 2238, 2240, 
				2242, 2244, 2246, 2248, 2250, 2252, 2254));
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, Item item) {
		if(inArray(item.getID(), 108, 6, 109, 230, 110, 111, 112, 2144, 2146, 2148, 2150, 2152, 2154, 2156,
				2158, 2160, 2162, 2164, 2166, 2168, 2170, 2172, 2174, 2176, 2178, 2180, 2182, 2184,
				2186, 2188, 2190, 2192, 2194, 2196, 2198, 2200, 2202, 2204, 2206, 2208, 2210, 2212,
				2214, 2216, 2218, 2220, 2222, 2224, 2226, 2228, 2230, 2232, 2234, 2236, 2238, 2240, 
				2242, 2244, 2246, 2248, 2250, 2252, 2254)) {
			npcTalk(player, npc, "Hello, changing your helm feather has a cost of 500 gold.",
					"Please pick a new color of your choice.");
			String[] menuText = { "Red (default)", "Orange", "Blue", "Purple", "Yellow", "Green", "Black", "White", "Matched" };
			int menu = showMenu(player, npc, menuText);
			int[] whatColor = null;
			if(menu == 0) {
				whatColor = RED_FEATHER_HELMETS;
			} else if(menu == 1) {
				whatColor = ORANGE_FEATHER_HELMETS;
			} else if(menu == 2) {
				whatColor = BLUE_FEATHER_HELMETS;
			} else if(menu == 3) {
				whatColor = PURPLE_FEATHER_HELMETS;
			} else if(menu == 4) {
				whatColor = YELLOW_FEATHER_HELMETS;
			} else if(menu == 5) {
				whatColor = GREEN_FEATHER_HELMETS;
			} else if(menu == 6) {
				whatColor = BLACK_FEATHER_HELMETS;
			} else if(menu == 7) {
				whatColor = WHITE_FEATHER_HELMETS;
			} else if(menu == 8) {
				whatColor = MATCHED_FEATHER_HELMETS;
			}

			if(menu != -1 && whatColor != null) {
				if(hasItem(player, 10, 500)) {
					Item newHelmet = null;
					int newHelmetArray = -1;
					if(inArray(item.getID(), BRONZE_LARGE_HELMETS)) 
						newHelmetArray = 0;
					else if(inArray(item.getID(), IRON_LARGE_HELMETS)) 
						newHelmetArray = 1;
					else if(inArray(item.getID(), STEEL_LARGE_HELMETS)) 
						newHelmetArray = 2;
					else if(inArray(item.getID(), BLACK_LARGE_HELMETS)) 
						newHelmetArray = 3;
					else if(inArray(item.getID(), MITHRIL_LARGE_HELMETS)) 
						newHelmetArray = 4;
					else if(inArray(item.getID(), ADAMANTITE_LARGE_HELMETS)) 
						newHelmetArray = 5;
					else if(inArray(item.getID(), RUNE_LARGE_HELMETS)) 
						newHelmetArray = 6;

					if(newHelmetArray != -1) {
						for(int i = 0; i < whatColor.length; i++) {
							if(i == newHelmetArray) {
								newHelmet = getItem(whatColor[i]);
								if(newHelmet == null) {
									npcTalk(player, npc, "Looks like I can't convert to that feather color");
									player.message("Nothing interesting happens");
									return;
								}
								if(newHelmet.getID() == 2254 && player.getQuestStage(Constants.Quests.LEGENDS_QUEST) != -1) {
									player.message("You need to complete the Legends Quest first.");
									return;
								}
								if(removeItem(player, item.getID(), 1)) {
									removeItem(player, 10, 500);
									message(player, npc, 1300, "You give Thrander a " + item.getDef().getName().toLowerCase() + " and 500 coins",
											"Thrander hammers it for a bit");
									player.message("Thrander gives you a " + menuText[menu] + " " + newHelmet.getDef().getName().toLowerCase() + "");
									addItem(player, newHelmet.getID(), 1);
									return;
								}
							}
						} 
					}
				} else {
					playerTalk(player, npc, "Oops, I don't have enough money");
				}
			} 
		}
		else if(inArray(item.getID(), 308, 312, 309, 313, 310, 311, 407, 117, 
				8, 118, 196, 119, 120, 401, 214, 215, 225, 434, 226, 227, 406, 206, 9, 121, 248, 122, 123, 402)) {
			int newID = -1;
			switch (item.getID()) {
			case 308: // Bronze top
				newID = 117;
				break;
			case 312: // Iron top
				newID = 8;
				break;
			case 309: // Steel top
				newID = 118;
				break;
			case 313: // Black top
				newID = 196;
				break;
			case 310: // Mithril top
				newID = 119;
				break;
			case 311: // Adamantite top
				newID = 120;
				break;
			case 407: // Rune top
				newID = 401;
				break;
			case 117: // Bronze body
				newID = 308;
				break;
			case 8: // Iron body
				newID = 312;
				break;
			case 118: // Steel body
				newID = 309;
				break;
			case 196: // Black body
				newID = 313;
				break;
			case 119: // Mithril body
				newID = 310;
				break;
			case 120: // Adamantite body
				newID = 311;
				break;
			case 401: // Rune body
				newID = 407;
				break;
			case 214: // Bronze skirt
				newID = 206;
				break;
			case 215: // Iron skirt
				newID = 9;
				break;
			case 225: // Steel skirt
				newID = 121;
				break;
			case 434: // Black skirt
				newID = 248;
				break;
			case 226: // Mithril skirt
				newID = 122;
				break;
			case 227: // Adamantite skirt
				newID = 123;
				break;
			case 406: // Rune skirt
				newID = 402;
				break;
			case 206: // Bronze legs
				newID = 214;
				break;
			case 9: // Iron legs
				newID = 215;
				break;
			case 121: // Steel legs
				newID = 225;
				break;
			case 248: // Black legs
				newID = 434;
				break;
			case 122: // Mithril legs
				newID = 226;
				break;
			case 123: // Adamantite legs
				newID = 227;
				break;
			case 402: // Rune legs
				newID = 406;
				break;
			}
			Item changedItem = getItem(newID);
			if(removeItem(player, item.getID(), 1)) {
				message(player, npc, 1300, "You give Thrander a " + item.getDef().getName().toLowerCase() + "",
						"Thrander hammers it for a bit");
				player.message("Thrander gives you a " + changedItem.getDef().getName().toLowerCase() + "");
				addItem(player, newID, 1);
			}
		}
	}
	public int getNewID(Item item) {
		int newID = -1;
		switch (item.getID()) {
		case 308: // Bronze top
			newID = 117;
			break;
		case 312: // Iron top
			newID = 8;
			break;
		case 309: // Steel top
			newID = 118;
			break;
		case 313: // Black top
			newID = 196;
			break;
		case 310: // Mithril top
			newID = 119;
			break;
		case 311: // Adamantite top
			newID = 120;
			break;
		case 407: // Rune top
			newID = 401;
			break;
		case 117: // Bronze body
			newID = 308;
			break;
		case 8: // Iron body
			newID = 312;
			break;
		case 118: // Steel body
			newID = 309;
			break;
		case 196: // Black body
			newID = 313;
			break;
		case 119: // Mithril body
			newID = 310;
			break;
		case 120: // Adamantite body
			newID = 311;
			break;
		case 401: // Rune body
			newID = 407;
			break;
		case 214: // Bronze skirt
			newID = 206;
			break;
		case 215: // Iron skirt
			newID = 9;
			break;
		case 225: // Steel skirt
			newID = 121;
			break;
		case 434: // Black skirt
			newID = 248;
			break;
		case 226: // Mithril skirt
			newID = 122;
			break;
		case 227: // Adamantite skirt
			newID = 123;
			break;
		case 406: // Rune skirt
			newID = 402;
			break;
		case 206: // Bronze legs
			newID = 214;
			break;
		case 9: // Iron legs
			newID = 215;
			break;
		case 121: // Steel legs
			newID = 225;
			break;
		case 248: // Black legs
			newID = 434;
			break;
		case 122: // Mithril legs
			newID = 226;
			break;
		case 123: // Adamantite legs
			newID = 227;
			break;
		case 402: // Rune legs
			newID = 406;
			break;
		}
		return newID;
	}

}
