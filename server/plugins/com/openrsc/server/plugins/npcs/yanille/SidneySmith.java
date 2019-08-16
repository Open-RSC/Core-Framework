package com.openrsc.server.plugins.npcs.yanille;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class SidneySmith implements TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	public static final int SIDNEY_SMITH = 778;

	/**
	 * ITEM UNCERTED
	 */
	public static final int PRAYER_RESTORE_POT = 483;
	public static final int SUPER_ATTACK_POT = 486;
	public static final int SUPER_STRENGTH_POT = 492;
	public static final int SUPER_DEFENSE_POT = 495;
	public static final int DRAGON_BONES = 814;
	public static final int LIMPWURT_ROOT = 220;
	/**
	 * ITEM CERTED
	 */
	public static final int PRAYER_CERT = 1272;
	public static final int SUPER_ATTACK_CERT = 1273;
	public static final int SUPER_DEFENSE_CERT = 1274;
	public static final int SUPER_STRENGTH_CERT = 1275;
	public static final int DRAGON_BONES_CERT = 1270;
	public static final int LIMPWURT_ROOT_CERT = 1271;

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == SIDNEY_SMITH;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == SIDNEY_SMITH) {
			sidneyCert(p, n, -1);
		}
	}

	private void sidneyCert(Player p, Npc n, int cID) {
		if (cID == -1) {
			npcTalk(p, n, "Hello, I'm Sidney Smith, the certification Clerk.",
				"How can I help you ?");
			int menu = showMenu(p, n,
				"I'd like to certificate some goods please.",
				"I'd like to change some certificates for goods please.",
				"What is certification ?",
				"Which goods do you certificate ?");
			if (menu == 0) {
				sidneyCert(p, n, Sidney.GOODS_TO_CERTIFICATE);
			} else if (menu == 1) {
				sidneyCert(p, n, Sidney.CERTIFICATE_TO_GOODS);
			} else if (menu == 2) {
				sidneyCert(p, n, Sidney.WHAT_IS_CERTIFICATION);
			} else if (menu == 3) {
				sidneyCert(p, n, Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE);
			}
		}
		switch (cID) {
			case Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE:
				npcTalk(p, n, "Well, I can certificate the following items.",
					"Prayer Restore Potion,",
					"Super Attack Potion,",
					"Super Defense Potion,",
					"Super Strength Potion,",
					"Dragon Bones,",
					"and Limpwurt Root.");
				int SUB_MENU_ONE = showMenu(p, n,
					"How many items do you need to make a certificate.",
					"I'd like to certificate some goods please.",
					"I'd like to change some certificates for goods please.",
					"Ok, thanks.");
				if (SUB_MENU_ONE == 0) {
					sidneyCert(p, n, Sidney.HOW_MANY_ITEMS_TO_MAKE_CERTIFICATE);
				} else if (SUB_MENU_ONE == 1) {
					sidneyCert(p, n, Sidney.GOODS_TO_CERTIFICATE);
				} else if (SUB_MENU_ONE == 2) {
					sidneyCert(p, n, Sidney.CERTIFICATE_TO_GOODS);
				}
				break;
			case Sidney.WHAT_IS_CERTIFICATION:
				npcTalk(p, n, "It's quite easy really..",
					"You swap some goods for certificates which are easier to store.",
					"I specialise in certificating very rare items.",
					"The kinds of items only Legendary Runescape citizens will own.");
				int SUB_MENU_TWO = showMenu(p, n,
					"I'd like to certificate some goods please.",
					"I'd like to change some certificates for goods please.",
					"Ok thanks.");
				if (SUB_MENU_TWO == 0) {
					sidneyCert(p, n, Sidney.GOODS_TO_CERTIFICATE);
				} else if (SUB_MENU_TWO == 1) {
					sidneyCert(p, n, Sidney.CERTIFICATE_TO_GOODS);
				}
				break;
			case Sidney.HOW_MANY_ITEMS_TO_MAKE_CERTIFICATE:
				npcTalk(p, n, "Well, you need at the least five items to make a certificate.",
					"We'll turn any five items into one certificate.",
					"It makes storage and transportation much easier.");
				int SUB_MENU_THREE = showMenu(p, n,
					"Which goods do you certificate?",
					"Ok, thanks.");
				if (SUB_MENU_THREE == 0) {
					sidneyCert(p, n, Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE);
				}
				break;
			case Sidney.CERTIFICATE_TO_GOODS:
				if (!hasItem(p, PRAYER_CERT) &&
					!hasItem(p, SUPER_ATTACK_CERT) &&
					!hasItem(p, SUPER_DEFENSE_CERT) &&
					!hasItem(p, SUPER_STRENGTH_CERT) &&
					!hasItem(p, DRAGON_BONES_CERT) &&
					!hasItem(p, LIMPWURT_ROOT_CERT)) {
					npcTalk(p, n, "Sorry, but you don't have any certificates that I can change.",
						"I can only change the following certificates into goods.",
						"Dragon Bone Certificates,",
						"Limpwurt Root Certificates,",
						"Prayer Potion Certificates,",
						"Super Attack Potion Certificates,",
						"Super Defense Potion Certificates,",
						"and Super Strength Potion Certificates.");
				} else {
					npcTalk(p, n, "Ok then, which certificates would you like to change?");
					certMenuOne(p, n);
				}
				break;
			case Sidney.GOODS_TO_CERTIFICATE:
				if (!hasItem(p, PRAYER_RESTORE_POT, 5) &&
					!hasItem(p, SUPER_ATTACK_POT, 5) &&
					!hasItem(p, SUPER_DEFENSE_POT, 5) &&
					!hasItem(p, SUPER_STRENGTH_POT, 5) &&
					!hasItem(p, DRAGON_BONES, 5) &&
					!hasItem(p, LIMPWURT_ROOT, 5)) {
					npcTalk(p, n, "Sorry, but you either don't have enough items for me to certificate.",
						"or you don't have the right type of items for me to certificate.");
					int SUB_MENU_FOUR = showMenu(p, n,
						"Which goods do you certificate?",
						"How many items do you need to make a certificate.");
					if (SUB_MENU_FOUR == 0) {
						sidneyCert(p, n, Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE);
					} else if (SUB_MENU_FOUR == 1) {
						sidneyCert(p, n, Sidney.HOW_MANY_ITEMS_TO_MAKE_CERTIFICATE);
					}
				} else {
					npcTalk(p, n, "Which goods would you like to certificate?");
					goodsMenuOne(p, n);
				}
				break;
		}
	}

	private void goodsMenuOne(Player p, Npc n) {
		int goods = showMenu(p,
			"* Prayer Restore Potion * ",
			"* Super Attack Potion *",
			"* Super Defense Potion *",
			"* Super Strength Potion *",
			"-*- Menu 2 -*-");
		if (goods == 0) {
			if (hasItem(p, PRAYER_RESTORE_POT, 5)) {
				calculateExchangeMenu(p, n, false, new Item(PRAYER_RESTORE_POT), new Item(PRAYER_CERT));
			} else {
				npcTalk(p, n, "You don't have any Prayer potions to certificate.",
					"Which goods would you like to certificate?");
				goodsMenuOne(p, n);
			}
		} else if (goods == 1) {
			if (hasItem(p, SUPER_ATTACK_POT, 5)) {
				calculateExchangeMenu(p, n, false, new Item(SUPER_ATTACK_POT), new Item(SUPER_ATTACK_CERT));
			} else {
				npcTalk(p, n, "You don't have enough Super Attack potions to certificate.");
				playerTalk(p, n, "Ok thanks.");
			}
		} else if (goods == 2) {
			if (hasItem(p, SUPER_DEFENSE_POT, 5)) {
				calculateExchangeMenu(p, n, false, new Item(SUPER_DEFENSE_POT), new Item(SUPER_DEFENSE_CERT));
			} else {
				npcTalk(p, n, "You don't have any Super Defense potions to certificate.",
					"Which goods would you like to certificate?");
				goodsMenuOne(p, n);
			}
		} else if (goods == 3) {
			if (hasItem(p, SUPER_STRENGTH_POT, 5)) {
				calculateExchangeMenu(p, n, false, new Item(SUPER_STRENGTH_POT), new Item(SUPER_STRENGTH_CERT));
			} else {
				npcTalk(p, n, "You don't have any Super Strength potions to certificate.",
					"Which goods would you like to certificate?");
				goodsMenuOne(p, n);
			}
		} else if (goods == 4) {
			goodsMenuTwo(p, n);
		}
	}

	private void goodsMenuTwo(Player p, Npc n) {
		int goods = showMenu(p,
			"* Dragon Bones *",
			"* Limpwurt Root *",
			"-*- Menu 1 -*-");
		if (goods == 0) {
			if (hasItem(p, DRAGON_BONES, 5)) {
				calculateExchangeMenu(p, n, false, new Item(DRAGON_BONES), new Item(DRAGON_BONES_CERT));
			} else {
				npcTalk(p, n, "You don't have any Dragon Bones to certificate.",
					"Which goods would you like to certificate?");
				goodsMenuOne(p, n);
			}
		} else if (goods == 1) {
			if (hasItem(p, LIMPWURT_ROOT, 5)) {
				calculateExchangeMenu(p, n, false, new Item(LIMPWURT_ROOT), new Item(LIMPWURT_ROOT_CERT));
			} else {
				npcTalk(p, n, "You don't have any Limpwurt Roots to certificate.",
					"Which goods would you like to certificate?");
				goodsMenuOne(p, n);
			}
		} else if (goods == 2) {
			goodsMenuOne(p, n);
		}
	}

	private void certMenuOne(Player p, Npc n) {
		int certs = showMenu(p,
			"* Restore Prayer Potion Certificates * ",
			"* Super Attack Potion Certificates *",
			"* Super Defense Potion Certificates *",
			"* Super Strength Potion Certificates *",
			"-*- Menu 2 -*-");
		if (certs == 0) {
			if (hasItem(p, PRAYER_CERT)) {
				calculateExchangeMenu(p, n, true, new Item(PRAYER_RESTORE_POT), new Item(PRAYER_CERT));
			} else {
				npcTalk(p, n, "Sorry, but you don't have any ",
					"Prayer Restore Potion Certificates to change.");
			}
		} else if (certs == 1) {
			if (hasItem(p, SUPER_ATTACK_CERT)) {
				calculateExchangeMenu(p, n, true, new Item(SUPER_ATTACK_POT), new Item(SUPER_ATTACK_CERT));
			} else {
				npcTalk(p, n, "Sorry, but you don't have any ",
					"Super attack Potion Certificates to change.");
			}
		} else if (certs == 2) {
			if (hasItem(p, SUPER_DEFENSE_CERT)) {
				calculateExchangeMenu(p, n, true, new Item(SUPER_DEFENSE_POT), new Item(SUPER_DEFENSE_CERT));
			} else {
				npcTalk(p, n, "Sorry, but you don't have any ",
					"Super Defense Potion Certificates to change.");
			}
		} else if (certs == 3) {
			if (hasItem(p, SUPER_STRENGTH_CERT)) {
				calculateExchangeMenu(p, n, true, new Item(SUPER_STRENGTH_POT), new Item(SUPER_STRENGTH_CERT));
			} else {
				npcTalk(p, n, "Sorry, but you don't have any ",
					"Super Strength Potion Certificates to change.");
			}
		} else if (certs == 4) {
			certMenuTwo(p, n);
		}
	}

	private void certMenuTwo(Player p, Npc n) {
		int menu = showMenu(p,
			"* Dragon Bones Certificates *",
			"* Limpwurt Root Certificates *",
			"-*- Menu 1 -*-");
		if (menu == 0) {
			if (hasItem(p, DRAGON_BONES_CERT)) {
				calculateExchangeMenu(p, n, true, new Item(DRAGON_BONES), new Item(DRAGON_BONES_CERT));
			} else {
				npcTalk(p, n, "Sorry, but you don't have any ",
					"Dragon Bone Certificates to change.");
			}
		} else if (menu == 1) {
			if (hasItem(p, LIMPWURT_ROOT_CERT)) {
				calculateExchangeMenu(p, n, true, new Item(LIMPWURT_ROOT), new Item(LIMPWURT_ROOT_CERT));
			} else {
				npcTalk(p, n, "Sorry, but you don't have any ",
					"Limpwurt Root Certificates to change.");
			}
		} else if (menu == 2) {
			certMenuOne(p, n);
		}
	}

	private void calculateExchangeMenu(Player p, Npc n, boolean useCertificate, Item i, Item certificate) {
		int count = p.getInventory().countId(useCertificate ? certificate.getID() : i.getID());
		int mainMenu = -1;
		if (useCertificate) {
			npcTalk(p, n, "How many " + i.getDef(p.getWorld()).getName() + " certificates do you want to change?");
			if (count == 1) {
				int firstMenu = showMenu(p, "None thanks.", "One Certificate please");
				if (firstMenu != -1) {
					if (firstMenu == 0) {
						npcTalk(p, n, "Ok, suit yourself.");
						return;
					} else if (firstMenu == 1) {
						mainMenu = 0;
					}
				}
			} else if (count == 2) {
				mainMenu = showMenu(p, "One Certificate please", "Two Certificates Please");
			} else if (count == 3) {
				mainMenu = showMenu(p, "One Certificate please", "Two Certificates Please", "Three Certificates Please.");
			} else if (count == 4) {
				mainMenu = showMenu(p, "One Certificate please", "Two Certificates Please", "Three Certificates Please.", "Four Certificates Please");
			} else if (count >= 5) {
				mainMenu = showMenu(p, "One Certificate please", "Two Certificates Please", "Three Certificates Please.", "Four Certificates Please", "Five Certificates Please.");
			}

		} else {
			npcTalk(p, n, "How many " + i.getDef(p.getWorld()).getName() + " would you like to certificate?");
			if (count >= 5 && count < 10) {
				int firstMenu = showMenu(p, "None", "Five");
				if (firstMenu != -1) {
					if (firstMenu == 0) {
						p.message("You decide not to change any items.");
						return;
					} else if (firstMenu == 1) {
						mainMenu = 0;
					}
				}
			} else if (count >= 10 && count < 15) {
				mainMenu = showMenu(p, "Five", "Ten");
			} else if (count >= 15 && count < 20) {
				mainMenu = showMenu(p, "Five", "Ten", "Fifteen");
			} else if (count >= 20 && count < 25) {
				mainMenu = showMenu(p, "Five", "Ten", "Fifteen", "Twenty");
			} else if (count >= 25) {
				mainMenu = showMenu(p, "Five", "Ten", "Fifteen", "Twenty", "Twenty Five");
			} else {
				npcTalk(p, n, "Sorry, but you don't have enough " + i.getDef(p.getWorld()).getName() + ".",
					"You need at least five to make a certificate.");
				return;
			}
		}
		if (mainMenu != -1) {
			if (useCertificate) {
				npcTalk(p, n, "Ok, that's your " + i.getDef(p.getWorld()).getName() + " certificates done.");
				mainMenu += 1;
				int itemAmount = mainMenu * 5;
				if (p.getInventory().remove(certificate.getID(), mainMenu) > -1) {
					for (int x = 0; x < itemAmount; x++) {
						p.getInventory().add(new Item(i.getID(), 1));
					}
				}
				playerTalk(p, n, "Ok thanks.");
			} else {
				npcTalk(p, n, "Ok, that's your " + i.getDef(p.getWorld()).getName() + " certificated.");
				mainMenu += 1;
				int itemAmount = mainMenu * 5;
				for (int x = 0; x < itemAmount; x++) {
					p.getInventory().remove(i.getID(), 1);
				}
				p.getInventory().add(new Item(certificate.getID(), mainMenu));
				playerTalk(p, n, "Ok thanks.");
			}
		}
	}

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == SIDNEY_SMITH && inArray(item.getID(), PRAYER_RESTORE_POT,
			SUPER_ATTACK_POT, SUPER_STRENGTH_POT, SUPER_DEFENSE_POT, DRAGON_BONES,
			LIMPWURT_ROOT, PRAYER_CERT, SUPER_ATTACK_CERT, SUPER_DEFENSE_CERT,
			SUPER_STRENGTH_CERT, DRAGON_BONES_CERT, LIMPWURT_ROOT_CERT);
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == SIDNEY_SMITH && inArray(item.getID(), PRAYER_RESTORE_POT,
			SUPER_ATTACK_POT, SUPER_STRENGTH_POT, SUPER_DEFENSE_POT, DRAGON_BONES,
			LIMPWURT_ROOT, PRAYER_CERT, SUPER_ATTACK_CERT, SUPER_DEFENSE_CERT,
			SUPER_STRENGTH_CERT, DRAGON_BONES_CERT, LIMPWURT_ROOT_CERT)) {
			switch (item.getID()) {
				case PRAYER_RESTORE_POT:
					calculateExchangeMenu(player, npc, false, item, new Item(PRAYER_CERT));
					break;
				case SUPER_ATTACK_POT:
					calculateExchangeMenu(player, npc, false, item, new Item(SUPER_ATTACK_CERT));
					break;
				case SUPER_STRENGTH_POT:
					calculateExchangeMenu(player, npc, false, item, new Item(SUPER_STRENGTH_CERT));
					break;
				case SUPER_DEFENSE_POT:
					calculateExchangeMenu(player, npc, false, item, new Item(SUPER_DEFENSE_CERT));
					break;
				case DRAGON_BONES:
					calculateExchangeMenu(player, npc, false, item, new Item(DRAGON_BONES_CERT));
					break;
				case LIMPWURT_ROOT:
					calculateExchangeMenu(player, npc, false, item, new Item(LIMPWURT_ROOT_CERT));
					break;
				case PRAYER_CERT:
					calculateExchangeMenu(player, npc, true, new Item(PRAYER_RESTORE_POT), item);
					break;
				case SUPER_ATTACK_CERT:
					calculateExchangeMenu(player, npc, true, new Item(SUPER_ATTACK_POT), item);
					break;
				case SUPER_STRENGTH_CERT:
					calculateExchangeMenu(player, npc, true, new Item(SUPER_STRENGTH_POT), item);
					break;
				case SUPER_DEFENSE_CERT:
					calculateExchangeMenu(player, npc, true, new Item(SUPER_DEFENSE_POT), item);
					break;
				case DRAGON_BONES_CERT:
					calculateExchangeMenu(player, npc, true, new Item(DRAGON_BONES), item);
					break;
				case LIMPWURT_ROOT_CERT:
					calculateExchangeMenu(player, npc, true, new Item(LIMPWURT_ROOT), item);
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
	}

	class Sidney {
		public static final int CERTIFICATE_TO_GOODS = 0;
		public static final int GOODS_TO_CERTIFICATE = 1;
		public static final int WHAT_IS_CERTIFICATION = 2;
		public static final int WHICH_GOODS_DO_YOU_CERTIFICATE = 3;
		public static final int HOW_MANY_ITEMS_TO_MAKE_CERTIFICATE = 4;
	}
}
