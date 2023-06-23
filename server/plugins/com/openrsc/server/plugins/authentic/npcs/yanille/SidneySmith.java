package com.openrsc.server.plugins.authentic.npcs.yanille;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.authentic.npcs.Certer;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class SidneySmith implements TalkNpcTrigger, UseNpcTrigger {

	public static final int SIDNEY_SMITH = NpcId.SIDNEY_SMITH.id();

	/**
	 * ITEM UNCERTED
	 */
	public static final int PRAYER_RESTORE_POT = ItemId.FULL_RESTORE_PRAYER_POTION.id();
	public static final int SUPER_ATTACK_POT = ItemId.FULL_SUPER_ATTACK_POTION.id();
	public static final int SUPER_STRENGTH_POT = ItemId.FULL_SUPER_STRENGTH_POTION.id();
	public static final int SUPER_DEFENSE_POT = ItemId.FULL_SUPER_DEFENSE_POTION.id();
	public static final int DRAGON_BONES = ItemId.DRAGON_BONES.id();
	public static final int LIMPWURT_ROOT = ItemId.LIMPWURT_ROOT.id();

	/**
	 * ITEM CERTED
	 */
	public static final int PRAYER_CERT = ItemId.PRAYER_POTION_CERTIFICATE.id();
	public static final int SUPER_ATTACK_CERT = ItemId.SUPER_ATTACK_POTION_CERTIFICATE.id();
	public static final int SUPER_DEFENSE_CERT = ItemId.SUPER_DEFENSE_POTION_CERTIFICATE.id();
	public static final int SUPER_STRENGTH_CERT = ItemId.SUPER_STRENGTH_POTION_CERTIFICATE.id();
	public static final int DRAGON_BONES_CERT = ItemId.DRAGON_BONE_CERTIFICATE.id();
	public static final int LIMPWURT_ROOT_CERT = ItemId.LIMPWURT_ROOT_CERTIFICATE.id();

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == SIDNEY_SMITH;
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == SIDNEY_SMITH) {
			sidneyCert(player, n, -1);
		}
	}

	private void sidneyCert(Player player, Npc n, int cID) {
		if (cID == -1) {
			npcsay(player, n, "Hello, I'm Sidney Smith, the certification Clerk.",
				"How can I help you ?");

			ArrayList<String> options = new ArrayList<>();
			if (!player.getCertOptOut()) {
				options.add("I'd like to certificate some goods please.");
			}
			options.add("I'd like to change some certificates for goods please.");
			options.add("What is certification ?");
			options.add("Which goods do you certificate ?");
			String[] finalOptions = new String[options.size()];

			int menu = multi(player, n, false, //do not send over
				options.toArray(finalOptions));
			if (menu >= 0 && player.getCertOptOut()) {
				++menu;
			}

			if (menu == 0) {
				say(player, n, "I'd like to certificate some goods please.");
				sidneyCert(player, n, Sidney.GOODS_TO_CERTIFICATE);
			} else if (menu == 1) {
				say(player, n, "I'd like to change some certificates for goods please.");
				sidneyCert(player, n, Sidney.CERTIFICATE_TO_GOODS);
			} else if (menu == 2) {
				say(player, n, "What is certification?");
				sidneyCert(player, n, Sidney.WHAT_IS_CERTIFICATION);
			} else if (menu == 3) {
				say(player, n, "Which goods do you certificate ?");
				sidneyCert(player, n, Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE);
			}
		}
		switch (cID) {
			case Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE:
				npcsay(player, n, "Well, I can certificate the following items.",
					"Prayer Restore Potion,",
					"Super Attack Potion,",
					"Super Defense Potion,",
					"Super Strength Potion,",
					"Dragon Bones,",
					"and Limpwurt Root.");
				ArrayList<String> options1 = new ArrayList<>();
				options1.add("How many items do you need to make a certificate.");
				if (!player.getCertOptOut()) {
					options1.add("I'd like to certificate some goods please.");
				}
				options1.add("I'd like to change some certificates for goods please.");
				options1.add("Ok, thanks.");
				String[] finalOptions = new String[options1.size()];
				int SUB_MENU_ONE = multi(player, n,
					options1.toArray(finalOptions));

				if (SUB_MENU_ONE >= 1 && player.getCertOptOut()) {
					++SUB_MENU_ONE;
				}

				if (SUB_MENU_ONE == 0) {
					sidneyCert(player, n, Sidney.HOW_MANY_ITEMS_TO_MAKE_CERTIFICATE);
				} else if (SUB_MENU_ONE == 1) {
					sidneyCert(player, n, Sidney.GOODS_TO_CERTIFICATE);
				} else if (SUB_MENU_ONE == 2) {
					sidneyCert(player, n, Sidney.CERTIFICATE_TO_GOODS);
				}
				break;
			case Sidney.WHAT_IS_CERTIFICATION:
				npcsay(player, n, "It's quite easy really..",
					"You swap some goods for certificates which are easier to store.",
					"I specialise in certificating very rare items.",
					"The kinds of items only Legendary Runescape citizens will own.");

				ArrayList<String> options2 = new ArrayList<>();
				if (!player.getCertOptOut()) {
					options2.add("I'd like to certificate some goods please.");
				}
				options2.add("I'd like to change some certificates for goods please.");
				options2.add("Ok thanks.");
				String[] finalOptions2 = new String[options2.size()];

				int SUB_MENU_TWO = multi(player, n,
					options2.toArray(finalOptions2));

				if (SUB_MENU_TWO >= 0 && player.getCertOptOut()) {
					++SUB_MENU_TWO;
				}

				if (SUB_MENU_TWO == 0) {
					sidneyCert(player, n, Sidney.GOODS_TO_CERTIFICATE);
				} else if (SUB_MENU_TWO == 1) {
					sidneyCert(player, n, Sidney.CERTIFICATE_TO_GOODS);
				}
				break;
			case Sidney.HOW_MANY_ITEMS_TO_MAKE_CERTIFICATE:
				npcsay(player, n, "Well, you need at the least five items to make a certificate.",
					"We'll turn any five items into one certificate.",
					"It makes storage and transportation much easier.");
				int SUB_MENU_THREE = multi(player, n,
					"Which goods do you certificate?",
					"Ok, thanks.");
				if (SUB_MENU_THREE == 0) {
					sidneyCert(player, n, Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE);
				}
				break;
			case Sidney.CERTIFICATE_TO_GOODS:
				if (!player.getCarriedItems().hasCatalogID(PRAYER_CERT, Optional.of(false)) &&
					!player.getCarriedItems().hasCatalogID(SUPER_ATTACK_CERT, Optional.of(false)) &&
					!player.getCarriedItems().hasCatalogID(SUPER_DEFENSE_CERT, Optional.of(false)) &&
					!player.getCarriedItems().hasCatalogID(SUPER_STRENGTH_CERT, Optional.of(false)) &&
					!player.getCarriedItems().hasCatalogID(DRAGON_BONES_CERT, Optional.of(false)) &&
					!player.getCarriedItems().hasCatalogID(LIMPWURT_ROOT_CERT, Optional.of(false))) {
					npcsay(player, n, "Sorry, but you don't have any certificates that I can change.",
						"I can only change the following certificates into goods.",
						"Dragon Bone Certificates,",
						"Limpwurt Root Certificates,",
						"Prayer Potion Certificates,",
						"Super Attack Potion Certificates,",
						"Super Defense Potion Certificates,",
						"and Super Strength Potion Certificates.");
				} else {
					npcsay(player, n, "Ok then, which certificates would you like to change?");
					certMenuOne(player, n);
				}
				break;
			case Sidney.GOODS_TO_CERTIFICATE:
				if (!ifheld(player, PRAYER_RESTORE_POT, 5) &&
					!ifheld(player, SUPER_ATTACK_POT, 5) &&
					!ifheld(player, SUPER_DEFENSE_POT, 5) &&
					!ifheld(player, SUPER_STRENGTH_POT, 5) &&
					!ifheld(player, DRAGON_BONES, 5) &&
					!ifheld(player, LIMPWURT_ROOT, 5)) {
					npcsay(player, n, "Sorry, but you either don't have enough items for me to certificate.",
						"or you don't have the right type of items for me to certificate.");
					int SUB_MENU_FOUR = multi(player, n,
						"Which goods do you certificate?",
						"How many items do you need to make a certificate.");
					if (SUB_MENU_FOUR == 0) {
						sidneyCert(player, n, Sidney.WHICH_GOODS_DO_YOU_CERTIFICATE);
					} else if (SUB_MENU_FOUR == 1) {
						sidneyCert(player, n, Sidney.HOW_MANY_ITEMS_TO_MAKE_CERTIFICATE);
					}
				} else {
					npcsay(player, n, "Which goods would you like to certificate?");
					goodsMenuOne(player, n);
				}
				break;
		}
	}

	private void goodsMenuOne(Player player, Npc n) {
		int goods = multi(player,
			"* Prayer Restore Potion * ",
			"* Super Attack Potion *",
			"* Super Defense Potion *",
			"* Super Strength Potion *",
			"-*- Menu 2 -*-");
		if (goods == 0) {
			if (ifheld(player, PRAYER_RESTORE_POT, 5)) {
				calculateExchangeMenu(player, n, false, new Item(PRAYER_RESTORE_POT), new Item(PRAYER_CERT));
			} else {
				npcsay(player, n, "You don't have any Prayer potions to certificate.",
					"Which goods would you like to certificate?");
				goodsMenuOne(player, n);
			}
		} else if (goods == 1) {
			if (ifheld(player, SUPER_ATTACK_POT, 5)) {
				calculateExchangeMenu(player, n, false, new Item(SUPER_ATTACK_POT), new Item(SUPER_ATTACK_CERT));
			} else {
				npcsay(player, n, "You don't have enough Super Attack potions to certificate.");
				say(player, n, "Ok thanks.");
			}
		} else if (goods == 2) {
			if (ifheld(player, SUPER_DEFENSE_POT, 5)) {
				calculateExchangeMenu(player, n, false, new Item(SUPER_DEFENSE_POT), new Item(SUPER_DEFENSE_CERT));
			} else {
				npcsay(player, n, "You don't have any Super Defense potions to certificate.",
					"Which goods would you like to certificate?");
				goodsMenuOne(player, n);
			}
		} else if (goods == 3) {
			if (ifheld(player, SUPER_STRENGTH_POT, 5)) {
				calculateExchangeMenu(player, n, false, new Item(SUPER_STRENGTH_POT), new Item(SUPER_STRENGTH_CERT));
			} else {
				npcsay(player, n, "You don't have any Super Strength potions to certificate.",
					"Which goods would you like to certificate?");
				goodsMenuOne(player, n);
			}
		} else if (goods == 4) {
			goodsMenuTwo(player, n);
		}
	}

	private void goodsMenuTwo(Player player, Npc n) {
		int goods = multi(player,
			"* Dragon Bones *",
			"* Limpwurt Root *",
			"-*- Menu 1 -*-");
		if (goods == 0) {
			if (ifheld(player, DRAGON_BONES, 5)) {
				calculateExchangeMenu(player, n, false, new Item(DRAGON_BONES), new Item(DRAGON_BONES_CERT));
			} else {
				npcsay(player, n, "You don't have any Dragon Bones to certificate.",
					"Which goods would you like to certificate?");
				goodsMenuOne(player, n);
			}
		} else if (goods == 1) {
			if (ifheld(player, LIMPWURT_ROOT, 5)) {
				calculateExchangeMenu(player, n, false, new Item(LIMPWURT_ROOT), new Item(LIMPWURT_ROOT_CERT));
			} else {
				npcsay(player, n, "You don't have any Limpwurt Roots to certificate.",
					"Which goods would you like to certificate?");
				goodsMenuOne(player, n);
			}
		} else if (goods == 2) {
			goodsMenuOne(player, n);
		}
	}

	private void certMenuOne(Player player, Npc n) {
		int certs = multi(player,
			"* Restore Prayer Potion Certificates * ",
			"* Super Attack Potion Certificates *",
			"* Super Defense Potion Certificates *",
			"* Super Strength Potion Certificates *",
			"-*- Menu 2 -*-");
		if (certs == 0) {
			if (player.getCarriedItems().hasCatalogID(PRAYER_CERT, Optional.of(false))) {
				calculateExchangeMenu(player, n, true, new Item(PRAYER_RESTORE_POT), new Item(PRAYER_CERT));
			} else {
				npcsay(player, n, "Sorry, but you don't have any ",
					"Prayer Restore Potion Certificates to change.");
			}
		} else if (certs == 1) {
			if (player.getCarriedItems().hasCatalogID(SUPER_ATTACK_CERT, Optional.of(false))) {
				calculateExchangeMenu(player, n, true, new Item(SUPER_ATTACK_POT), new Item(SUPER_ATTACK_CERT));
			} else {
				npcsay(player, n, "Sorry, but you don't have any ",
					"Super attack Potion Certificates to change.");
			}
		} else if (certs == 2) {
			if (player.getCarriedItems().hasCatalogID(SUPER_DEFENSE_CERT, Optional.of(false))) {
				calculateExchangeMenu(player, n, true, new Item(SUPER_DEFENSE_POT), new Item(SUPER_DEFENSE_CERT));
			} else {
				npcsay(player, n, "Sorry, but you don't have any ",
					"Super Defense Potion Certificates to change.");
			}
		} else if (certs == 3) {
			if (player.getCarriedItems().hasCatalogID(SUPER_STRENGTH_CERT, Optional.of(false))) {
				calculateExchangeMenu(player, n, true, new Item(SUPER_STRENGTH_POT), new Item(SUPER_STRENGTH_CERT));
			} else {
				npcsay(player, n, "Sorry, but you don't have any ",
					"Super Strength Potion Certificates to change.");
			}
		} else if (certs == 4) {
			certMenuTwo(player, n);
		}
	}

	private void certMenuTwo(Player player, Npc n) {
		int menu = multi(player,
			"* Dragon Bones Certificates *",
			"* Limpwurt Root Certificates *",
			"-*- Menu 1 -*-");
		if (menu == 0) {
			if (player.getCarriedItems().hasCatalogID(DRAGON_BONES_CERT, Optional.of(false))) {
				calculateExchangeMenu(player, n, true, new Item(DRAGON_BONES), new Item(DRAGON_BONES_CERT));
			} else {
				npcsay(player, n, "Sorry, but you don't have any ",
					"Dragon Bone Certificates to change.");
			}
		} else if (menu == 1) {
			if (player.getCarriedItems().hasCatalogID(LIMPWURT_ROOT_CERT, Optional.of(false))) {
				calculateExchangeMenu(player, n, true, new Item(LIMPWURT_ROOT), new Item(LIMPWURT_ROOT_CERT));
			} else {
				npcsay(player, n, "Sorry, but you don't have any ",
					"Limpwurt Root Certificates to change.");
			}
		} else if (menu == 2) {
			certMenuOne(player, n);
		}
	}

	private void calculateExchangeMenu(Player player, Npc n, boolean useCertificate, Item i, Item certificate) {
		int count = player.getCarriedItems().getInventory().countId(useCertificate ? certificate.getCatalogId() : i.getCatalogId());
		int mainMenu = -1;
		if (useCertificate) {
			npcsay(player, n, "How many " + i.getDef(player.getWorld()).getName() + " certificates do you want to change?");
			if (count == 1) {
				int firstMenu = multi(player, "None thanks.", "One Certificate please");
				if (firstMenu != -1) {
					if (firstMenu == 0) {
						npcsay(player, n, "Ok, suit yourself.");
						return;
					} else if (firstMenu == 1) {
						mainMenu = 0;
					}
				}
			} else if (count == 2) {
				mainMenu = multi(player, "One Certificate please", "Two Certificates Please");
			} else if (count == 3) {
				mainMenu = multi(player, "One Certificate please", "Two Certificates Please", "Three Certificates Please.");
			} else if (count == 4) {
				mainMenu = multi(player, "One Certificate please", "Two Certificates Please", "Three Certificates Please.", "Four Certificates Please");
			} else if (count >= 5) {
				mainMenu = multi(player, "One Certificate please", "Two Certificates Please", "Three Certificates Please.", "Four Certificates Please", "Five Certificates Please.");
			}

		} else {
			npcsay(player, n, "How many " + i.getDef(player.getWorld()).getName() + " would you like to certificate?");
			if (count >= 5 && count < 10) {
				int firstMenu = multi(player, "None", "Five");
				if (firstMenu != -1) {
					if (firstMenu == 0) {
						player.message("You decide not to change any items.");
						return;
					} else if (firstMenu == 1) {
						mainMenu = 0;
					}
				}
			} else if (count >= 10 && count < 15) {
				mainMenu = multi(player, "Five", "Ten");
			} else if (count >= 15 && count < 20) {
				mainMenu = multi(player, "Five", "Ten", "Fifteen");
			} else if (count >= 20 && count < 25) {
				mainMenu = multi(player, "Five", "Ten", "Fifteen", "Twenty");
			} else if (count >= 25) {
				mainMenu = multi(player, "Five", "Ten", "Fifteen", "Twenty", "Twenty Five");
			} else {
				npcsay(player, n, "Sorry, but you don't have enough " + i.getDef(player.getWorld()).getName() + ".",
					"You need at least five to make a certificate.");
				return;
			}
		}
		if (mainMenu != -1) {
			if (useCertificate) {
				npcsay(player, n, "Ok, that's your " + i.getDef(player.getWorld()).getName() + " certificates done.");
				mainMenu += 1;
				int itemAmount = mainMenu * 5;
				if (player.getCarriedItems().remove(new Item(certificate.getCatalogId(), mainMenu)) > -1) {
					for (int x = 0; x < itemAmount; x++) {
						player.getCarriedItems().getInventory().add(new Item(i.getCatalogId(), 1));
					}
				}
				say(player, n, "Ok thanks.");
			} else {
				npcsay(player, n, "Ok, that's your " + i.getDef(player.getWorld()).getName() + " certificated.");
				mainMenu += 1;
				int itemAmount = mainMenu * 5;
				for (int x = 0; x < itemAmount; x++) {
					player.getCarriedItems().remove(new Item(i.getCatalogId()));
				}
				player.getCarriedItems().getInventory().add(new Item(certificate.getCatalogId(), mainMenu));
				say(player, n, "Ok thanks.");
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == SIDNEY_SMITH && inArray(item.getCatalogId(), PRAYER_RESTORE_POT,
			SUPER_ATTACK_POT, SUPER_STRENGTH_POT, SUPER_DEFENSE_POT, DRAGON_BONES,
			LIMPWURT_ROOT, PRAYER_CERT, SUPER_ATTACK_CERT, SUPER_DEFENSE_CERT,
			SUPER_STRENGTH_CERT, DRAGON_BONES_CERT, LIMPWURT_ROOT_CERT);
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		// Check if UIM want to exchange for bank or market certs
		if (Certer.UIMCertBlock(player, npc, item)) {
			if (item.getNoted()) {
				Certer.UIMCert(player, npc, item);
				return;
			} else {
				mes("Would you like to exchange this item for bank or market certificates?");
				delay(3);
				final int option = multi(player, npc, false,
					"Bank certificates", "Market certificates", "Nevermind");
				if (option == -1 || option == 2) return;
				else if (option == 0) {
					Certer.UIMCert(player, npc, item);
					return;
				}
				// If they chose option 2, we do not return and carry on to the code below
			}
		} else if (Certer.certExchangeBlock(player, npc, item)) {
			mes("Would you like to exchange this market certification for bank certifications or items?");
			delay(3);
			final int option = multi(player, npc, false,
				"Bank certificates", "Items", "Nevermind");
			if (option == -1 || option == 2) return;
			else if (option == 0) {
				Certer.exchangeMarketForBankCerts(player, npc, item);
				return;
			}
			// If they chose option 2, we do not return and carry on to the code below
		}
		if (npc.getID() == SIDNEY_SMITH && inArray(item.getCatalogId(), PRAYER_RESTORE_POT,
			SUPER_ATTACK_POT, SUPER_STRENGTH_POT, SUPER_DEFENSE_POT, DRAGON_BONES,
			LIMPWURT_ROOT, PRAYER_CERT, SUPER_ATTACK_CERT, SUPER_DEFENSE_CERT,
			SUPER_STRENGTH_CERT, DRAGON_BONES_CERT, LIMPWURT_ROOT_CERT)) {
			switch (ItemId.getById(item.getCatalogId())) {
				case FULL_RESTORE_PRAYER_POTION:
					calculateExchangeMenu(player, npc, false, item, new Item(PRAYER_CERT));
					break;
				case FULL_SUPER_ATTACK_POTION:
					calculateExchangeMenu(player, npc, false, item, new Item(SUPER_ATTACK_CERT));
					break;
				case FULL_SUPER_STRENGTH_POTION:
					calculateExchangeMenu(player, npc, false, item, new Item(SUPER_STRENGTH_CERT));
					break;
				case FULL_SUPER_DEFENSE_POTION:
					calculateExchangeMenu(player, npc, false, item, new Item(SUPER_DEFENSE_CERT));
					break;
				case DRAGON_BONES:
					calculateExchangeMenu(player, npc, false, item, new Item(DRAGON_BONES_CERT));
					break;
				case LIMPWURT_ROOT:
					calculateExchangeMenu(player, npc, false, item, new Item(LIMPWURT_ROOT_CERT));
					break;
				case PRAYER_POTION_CERTIFICATE:
					calculateExchangeMenu(player, npc, true, new Item(PRAYER_RESTORE_POT), item);
					break;
				case SUPER_ATTACK_POTION_CERTIFICATE:
					calculateExchangeMenu(player, npc, true, new Item(SUPER_ATTACK_POT), item);
					break;
				case SUPER_STRENGTH_POTION_CERTIFICATE:
					calculateExchangeMenu(player, npc, true, new Item(SUPER_STRENGTH_POT), item);
					break;
				case SUPER_DEFENSE_POTION_CERTIFICATE:
					calculateExchangeMenu(player, npc, true, new Item(SUPER_DEFENSE_POT), item);
					break;
				case DRAGON_BONE_CERTIFICATE:
					calculateExchangeMenu(player, npc, true, new Item(DRAGON_BONES), item);
					break;
				case LIMPWURT_ROOT_CERTIFICATE:
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
