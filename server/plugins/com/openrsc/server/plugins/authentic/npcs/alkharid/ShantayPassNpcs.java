package com.openrsc.server.plugins.authentic.npcs.alkharid;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;
import com.openrsc.server.plugins.RuneScript;
import com.openrsc.server.plugins.custom.misc.WoodcuttingGuild;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class ShantayPassNpcs extends AbstractShop implements OpLocTrigger, TakeObjTrigger {

	private static final Logger LOGGER = LogManager.getLogger(ShantayPassNpcs.class);
	//private static int ASSISTANT = NpcId.NpcId.ASSISTANT.id().id();
	//private static int SHANTAY_DISCLAIMER = ItemId.A_FREE_SHANTAY_DISCLAIMER.id();
	//private static int SHANTAY_STANDING_GUARD = NpcId.SHANTAY_PASS_GUARD_STANDING.id();
	//private static int SHANTAY_MOVING_GUARD = NpcId.SHANTAY_PASS_GUARD_MOVING.id();
	//private static int SHANTAY = NpcId.NpcId.SHANTAY.id().id();
	private static int BANK_CHEST = 942;
	private static int STONE_GATE = 916;
	//private static int SHANTAY_PASS = ItemId.SHANTAY_DESERT_PASS.id();
	private final Shop shop = new Shop(false, 10000, 120, 70, 3,
		new Item(ItemId.JUG_OF_WATER.id(), 15),
		new Item(ItemId.BOWL_OF_WATER.id(), 15),
		new Item(ItemId.BUCKET_OF_WATER.id(), 15),
		new Item(ItemId.EMPTY_WATER_SKIN.id(), 15),
		new Item(ItemId.FULL_WATER_SKIN.id(), 15),
		new Item(ItemId.DESERT_ROBE.id(), 15),
		new Item(ItemId.DESERT_SHIRT.id(), 15),
		new Item(ItemId.DESERT_BOOTS.id(), 15),
		new Item(ItemId.TINDERBOX.id(), 5),
		new Item(ItemId.CHISEL.id(), 5),
		new Item(ItemId.HAMMER.id(), 5),
		new Item(ItemId.BRONZE_BAR.id(), 5),
		new Item(ItemId.FEATHER.id(), 200),
		new Item(ItemId.SHANTAY_DESERT_PASS.id(), 20),
		new Item(ItemId.KNIFE.id(), 20)
	);
	private boolean inJail = false;

	@Override
	public void onTalkNpc(final Player player, Npc n) {
		// Shantay NPCs should not be interactable in F2P.
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD
			&& player.getWorld().getServer().getEntityHandler().getNpcDef(n.getID()).isMembers()) {
			// Do nothing, since NpcTalkTo.java will send a message about members world.
			return;
		}

		if (n.getID() == NpcId.SHANTAY_PASS_GUARD_STANDING.id()) {
			npcsay(player, n, "Hello there!", "What can I do for you?");
			int menu = multi(player, n, "I'd like to go into the desert please.",
				"Nothing thanks.");
			if (menu == 0) {
				npcsay(player, n, "Of course!");
				if (!player.getCarriedItems().hasCatalogID(ItemId.SHANTAY_DESERT_PASS.id(), Optional.of(false))) {
					npcsay(player, n, "You'll need a Shantay pass to go through the gate into the desert.",
						"See Shantay, he'll sell you one for a very reasonable price.");
				} else {
					int menus;
					if (!player.getCarriedItems().hasCatalogID(ItemId.A_FREE_SHANTAY_DISCLAIMER.id(), Optional.of(false))) {
						mes("There is a large poster on the wall near the gateway. It reads..");
						delay(3);
						mes("@gre@The Desert is a VERY Dangerous place...do not enter if you are scared of dying.");
						delay(3);
						mes("@gre@Beware of high temperatures, sand storms, robbers, and slavers...");
						delay(3);
						mes("@gre@No responsibility is taken by Shantay ");
						delay(3);
						mes("@gre@If anything bad should happen to you in any circumstances whatsoever.");
						delay(3);
						mes("That seems pretty scary! Are you sure you want to go through?");
						delay(3);
						menus = multi(player,
							"Yeah, that poster doesn't scare me!",
							"No, I'm having serious second thoughts now.");
					} else {
						mes("A poster on the wall says exactly the same as the disclaimer.");
						delay(3);
						mes("Are you sure you want to go through?");
						delay(3);
						menus = multi(player,
							"Yeah, I'm not scared!",
							"No, I'm having serious second thoughts now.");
					}
					if (menus == 0) {
						final Npc npc = addnpc(n.getWorld(), NpcId.SHANTAY_PASS_GUARD_STANDING.id(), 63, 731);
						player.getWorld().getServer().getGameEventHandler().add(
							new SingleEvent(player.getWorld(), null, config().GAME_TICK * 50, "Shantay Pass Talk Delay") {
								public void action() {
									npcYell(player, npc, "Right, time for dinner!");
									getWorld().getServer().getGameEventHandler().add(new SingleEvent(getWorld(), null, npc.getConfig().GAME_TICK * 5, "Shantay Pass Guard Remove") {
										@Override
										public void action() {
											npc.remove();
										}
									});
								}
							});
						delay(2);
						npcsay(player, npc, "Can I see your Shantay Desert Pass please.");
						player.message("You hand over a Shantay Pass.");
						player.getCarriedItems().remove(new Item(ItemId.SHANTAY_DESERT_PASS.id()));
						say(player, npc, "Sure, here you go!");
						if (!player.getCarriedItems().hasCatalogID(ItemId.A_FREE_SHANTAY_DISCLAIMER.id(), Optional.of(false))) {
							npcsay(player, npc, "Here, have a disclaimer...",
								"It means that Shantay isn't responsible if you die in the desert.");
							player.message("The guard gives you a disclaimer.");
							give(player, ItemId.A_FREE_SHANTAY_DISCLAIMER.id(), 1);
						}
						player.message("you go through the gate");
						player.teleport(62, 735);
					} else if (menus == 1) {
						mes("You decide that your visit to the desert can be postponed..");
						delay(3);
						player.message("Perhaps indefinitely!");
					}

				}
			} else if (menu == 1) {
				npcsay(player, n, "Ok then, have a nice day.");
			}
			return;
		}
		if (n.getID() == NpcId.SHANTAY_PASS_GUARD_MOVING.id()) {
			npcsay(player, n, "Go talk to Shantay or one of his assistants.",
				"I'm on duty and I don't have time to talk to the likes of you!");
			mes("The guard seems quite bad tempered,");
			delay(3);
			mes("probably from having to wear heavy armour in this intense heat.");
			delay(3);
			return;
		}
		boolean isShantay = false;
		if (n.getID() == NpcId.SHANTAY.id()) {
			isShantay = true;
			if (DataConversions.random(0, 25) == 0) { // 1 in 25 chance to drop kebab recipe
				GroundItem groundItem = new GroundItem(player.getWorld(), ItemId.SCRUMPLED_PIECE_OF_PAPER.id(), n.getX(), n.getY(), 1, player);
				player.getWorld().registerItem(groundItem);
			}

			npcsay(player, n, "Hello Effendi, I am Shantay.");
			if (!player.getCarriedItems().hasCatalogID(ItemId.A_FREE_SHANTAY_DISCLAIMER.id(), Optional.of(false))) {
				npcsay(player, n, "I see you're new!",
					"Make sure you read the poster before going into the desert.");
			}
			if (player.getQuestStage(Quests.TOURIST_TRAP) == 0) {
				npcsay(player, n, "There is a heartbroken Mother just past the gates and in the Desert.",
					"Her name is Irena and she mourns her lost Daughter. Such a shame.");
			}
		} else if (n.getID() == NpcId.ASSISTANT.id()) {
			npcsay(player, n, "Hello Effendi, I am a Shantay Pass Assistant.");
			if (!player.getCarriedItems().hasCatalogID(ItemId.A_FREE_SHANTAY_DISCLAIMER.id(), Optional.of(false))) {
				npcsay(player, n, "I see you're new!",
					"Make sure you read the poster before going into the desert.");
			}
		}
		int menu = multi(player, n, "What is this place?",
			"Can I see what you have to sell please?", "I must be going.");
		if (menu == 0) {
			if (inJail) {
				npcsay(player,
					n,
					"You should be in jail!",
					"Well, no doubt the authorities in Port Sarim know what they're doing.",
					"But if you get into any more trouble, you'll be stuck back in jail.");
				inJail = false;
				return;
			}
			npcsay(player, n, "This is the pass of Shantay.");
			if (isShantay) {
				npcsay(player, n, "I guard this area with my men.",
					"I am responsible for keeping this pass open and repaired.",
					"My men and I prevent outlaws from getting out of the desert.",
					"And we stop the inexperienced from a dry death in the sands.");
			} else {
				npcsay(player, n, "Mr Shantay guards this area with his men.",
					"He is responsible for keeping this pass open and repaired.",
					"He and his men prevent outlaws from getting out of the desert.",
					"And he stops the inexperienced from a dry death in the sands.");
			}
			npcsay(player, n, "Which would you say you were?");
			int menu2 = multi(player, n,
				"I am definitely an outlaw, prepare to die!",
				"I am a little inexperienced.",
				"Er, neither, I'm an adventurer.");
			if (menu2 == 0) {
				npcsay(player, n, "Ha, very funny.....", player.getText("ShantayPassNpcsGuardsArrestThem"));
				mes("The guards arrest you and place you in the jail.");
				delay(3);
				if (isShantay) {
					player.teleport(67, 729, false);
					player.getCache().store("shantay_jail", true);
				}
				npcsay(player,
					n,
					"You'll have to stay in there until you pay the fine of five gold pieces.",
					"Do you want to pay now?");
				inJail = true;
				int menu6 = multi(player, n, "Yes, Ok.",
					"No thanks, you're not having my money.");
				if (menu6 == 0) {
					npcsay(player, n,
						"Good, I see that you have come to your senses.");
					if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 5) {
						mes("You hand over five gold pieces to Shantay.");
						delay(3);
						npcsay(player, n,
							"Great Effendi, now please try to keep the peace.");
						if (isShantay) {
							mes("Shantay unlocks the door to the cell.");
							delay(3);
						} else {
							mes("The assistant unlocks the door to the cell.");
							delay(3);
						}
						player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
						if (player.getCache().hasKey("shantay_jail")) {
							player.getCache().remove("shantay_jail");
						}
						inJail = false;
					} else {
						npcsay(player,
							n,
							"You don't have that kind of cash on you I see.",
							"But perhaps you have some in your bank?",
							"You can transfer some money from your bank and pay the fine.",
							"or you will be sent to a maximum security prison in Port Sarim.",
							"Which is it going to be?");
						int menu8 = multi(player, n, false, //do not send over
							"I'll pay the fine.",
							"I'm not paying the fine!");
						if (menu8 == 0) {
							say(player, n, "I'll pay the fine.");
							if (player.isIronMan(2)) {
								player.message("As an Ultimate Ironman, you cannot use the bank.");
								return;
							}
							npcsay(player, n,
								"Ok then..., you'll need access to your bank.");
							player.setAccessingBank(true);
							ActionSender.showBank(player);
							if (player.getCache().hasKey("shantay_jail")) {
								player.getCache().remove("shantay_jail");
							}
							inJail = false;
						} else if (menu8 == 1) {
							say(player, n, "No thanks, you're not having my money.");
							sendToPortSarim(player, n, 1);
						}
					}
				} else if (menu6 == 1) {
					npcsay(player,
						n,
						"You have a choice.",
						"You can either pay five gold pieces or...",
						"You can be transported to a maximum security prison in Port Sarim.",
						"Will you pay the five gold pieces?");
					int menu7 = multi(player, n, "Yes, Ok.", "No, do your worst!");
					if (menu7 == 0) {
						npcsay(player, n,
							"Good, I see that you have come to your senses.");
						if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 5) {
							mes("You hand over five gold pieces to Shantay.");
							delay(3);
							npcsay(player, n,
								"Great Effendi, now please try to keep the peace.");
							if (isShantay) {
								mes("Shantay unlocks the door to the cell.");
								delay(3);
							} else {
								mes("The assistant unlocks the door to the cell.");
								delay(3);
							}
							player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
							if (player.getCache().hasKey("shantay_jail")) {
								player.getCache().remove("shantay_jail");
							}
							inJail = false;
						} else {
							npcsay(player,
								n,
								"You don't have that kind of cash on you I see.",
								"But perhaps you have some in your bank?",
								"You can transfer some money from your bank and pay the fine.",
								"or you will be sent to a maximum security prison in Port Sarim.",
								"Which is it going to be?");
							int menu8 = multi(player, n, false, //do not send over
								"I'll pay the fine.",
								"I'm not paying the fine!");
							if (menu8 == 0) {
								say(player, n, "I'll pay the fine.");
								if (player.isIronMan(2)) {
									player.message("As an Ultimate Ironman, you cannot use the bank.");
									return;
								}
								npcsay(player, n,
									"Ok then..., you'll need access to your bank.");
								player.setAccessingBank(true);
								ActionSender.showBank(player);
								if (player.getCache().hasKey("shantay_jail")) {
									player.getCache().remove("shantay_jail");
								}
								inJail = false;
							} else if (menu8 == 1) {
								say(player, n, "No thanks, you're not having my money.");
								sendToPortSarim(player, n, 1);
							}
						}
					} else if (menu7 == 1) {
						sendToPortSarim(player, n, 0);
					}
				}
			} else if (menu2 == 1) {
				npcsay(player,
					n,
					"Can I recommend that you purchase a full waterskin and a knife!",
					"These items will no doubt save your life...",
					"A waterskin will keep water from evaporating in the desert.",
					"And a keen woodsman with a knife can extract the juice from a cactus.",
					"Before you go into the desert, it's advisable to wear desert clothes.",
					"It's very hot in the desert and you'll surely cook if you wear armour.",
					"To  keep the pass open and bandit free, we charge a small toll of five gold pieces.",
					isShantay ? "You can buy a desert pass from me, just ask me the open the shop."
						: "You can buy a desert pass from me, just ask me to open the shop.",
					"You can also use our free banking services by clicking on the chest.");
				int menu5 = multi(player, n,
					"Can I see what you have to sell please?",
					"I must be going.");
				if (menu5 == 0) {
					npcsay(player, n, "Absolutely Effendi!");
					player.setAccessingShop(shop);
					ActionSender.showShop(player, shop);
				} else if (menu5 == 1) {
					npcsay(player, n, " So long...");
				}
			} else if (menu2 == 2) {
				npcsay(player,
					n,
					"Great, I have just the thing for the desert adventurer.",
					"I sell desert clothes which will keep you cool in the heat of the desert.",
					"I also sell waterskins so that you won't die in the desert.",
					"A waterskin and a knife help you survive from the juice of a cactus.",
					"Use the chest to store your items, we'll take them to the bank.",
					"It's hot in the desert, you'll bake in all that armour.",
					"To keep the pass open we ask for 5 gold pieces.",
					"and we give you a Shantay Pass, just ask to see what I sell to buy one.");
				int menu3 = multi(player, n,
					"Can I see what you have to sell please?",
					"I must be going.",
					"Why do I have to pay to go into the desert?");
				if (menu3 == 0) {
					npcsay(player, n, "Absolutely Effendi!");
					player.setAccessingShop(shop);
					ActionSender.showShop(player, shop);
				} else if (menu3 == 1) {
					npcsay(player, n, "So long...");
				} else if (menu3 == 2) {
					if (isShantay) {
						mes("Shantay opens his arms wide as if too embrace you.");
						delay(3);
						npcsay(player, n, "Effendi, you insult me!",
							"I am not interested in making a profit from you!");
					} else {
						mes("The Assistant opens his arms wide as if too embrace you.");
						delay(3);
						npcsay(player, n, "Effendi, you insult me!",
							"We are not interested in making a profit from you!");
					}
					npcsay(player,
						n,
						"I merely seek to cover my expenses in keeping this pass open.",
						"There is repair work to carry out and also the mens wages to consider.",
						"For the paltry sum of 5 Gold pieces, I think we offer a great service.");
					int menu4 = multi(player, n,
						"Can I see what you have to sell please?",
						"I must be going.");
					if (menu4 == 0) {

					} else if (menu4 == 1) {
						npcsay(player, n, " Absolutely Effendi!");
						player.setAccessingShop(shop);
						ActionSender.showShop(player, shop);
					}
				}
			}
		} else if (menu == 1) {
			npcsay(player, n, "Absolutely Effendi!");
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		} else if (menu == 2) {
			npcsay(player, n, "So long...");
		}
	}

	private void sendToPortSarim(Player player, Npc n, int path) {
		if (path == 0) {
			npcsay(player, n,
				"You are to be transported to a maximum security prison in Port Sarim.",
				"I hope you've learnt an important lesson from this.");
		} else if (path == 1) {
			npcsay(player, n,
				"Very well, I grow tired of you, you'll be taken to a new jail in Port Sarim.");
		}
		player.teleport(281, 665, false);
		player.getCache().remove("shantay_jail");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.ASSISTANT.id() || n.getID() == NpcId.SHANTAY.id() || n.getID() == NpcId.SHANTAY_PASS_GUARD_MOVING.id()
			|| n.getID() == NpcId.SHANTAY_PASS_GUARD_STANDING.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == BANK_CHEST || (obj.getID() == STONE_GATE && player.getY() < 735)) {
			return true;
		}
		return false;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		// F2P cannot use bank or interact with Stone Gate.
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.message("you must be on a members' world to do that");
			return;
		}

		if (obj.getID() == BANK_CHEST) {
			if (player.isIronMan(2)) {
				player.message("As an Ultimate Ironman, you cannot use the bank.");
				return;
			}

			if (obj.getX() == 58 && obj.getY() == 731) {
				mes("This chest is used by Shantay and his men.");
				delay(2);
				mes("They can put things in and out of storage for you.");
				delay(2);
				mes("You open the bank.");
				delay(2);
			} else if (config().WANT_WOODCUTTING_GUILD && obj.getX() == 556 && obj.getY() == 455) {
				if (RuneScript.ifnearnpc(NpcId.MCGRUBOR.id())) {
					WoodcuttingGuild.mcGruborDialogue(player);
				} else {
					mes("I should talk to McGrubor before I use his bank chest");
				}
				return;
			}

			if (validatebankpin(player, null)) {
				player.setAccessingBank(true);
				ActionSender.showBank(player);
			}
		}
		if (obj.getID() == STONE_GATE && player.getY() < 735) {
			if (command.equals("go through")) {
				int menu;
				if (!player.getCarriedItems().hasCatalogID(ItemId.A_FREE_SHANTAY_DISCLAIMER.id(), Optional.of(false))) {
					mes("There is a large poster on the wall near the gateway. It reads..");
					delay(3);
					mes("@gre@The Desert is a VERY Dangerous place...do not enter if you are scared of dying.");
					delay(3);
					mes("@gre@Beware of high temperatures, sand storms, robbers, and slavers...");
					delay(3);
					mes("@gre@No responsibility is taken by Shantay ");
					delay(3);
					mes("@gre@If anything bad should happen to you in any circumstances whatsoever.");
					delay(3);
					mes("That seems pretty scary! Are you sure you want to go through?");
					delay(3);
					menu = multi(player,
						"Yeah, that poster doesn't scare me!",
						"No, I'm having serious second thoughts now.");
				} else {
					mes("A poster on the wall says exactly the same as the disclaimer.");
					delay(3);
					mes("Are you sure you want to go through?");
					delay(3);
					menu = multi(player,
						"Yeah, I'm not scared!",
						"No, I'm having serious second thoughts now.");
				}
				Npc shantayGuard = ifnearvisnpc(player, NpcId.SHANTAY_PASS_GUARD_STANDING.id(), 5);
				if (menu == 0) {
					if (!player.getCarriedItems().hasCatalogID(ItemId.SHANTAY_DESERT_PASS.id(), Optional.of(false))) {
						mes("A guard stops you on your way out of the gate...");
						delay(3);
						if (shantayGuard != null) {
							npcsay(player, shantayGuard, "You need a Shantay pass to get through this gate.",
								"See Shantay, he will sell you one for a very reasonable price.");
						} else {
							player.message("Shantay guard seem to be busy at the moment.");
						}
					} else {
						if (shantayGuard != null) {
							npcsay(player, shantayGuard, "Can I see your Shantay Desert Pass please.");
							player.message("You hand over a Shantay Pass.");
							player.getCarriedItems().remove(new Item(ItemId.SHANTAY_DESERT_PASS.id()));
							say(player, shantayGuard, "Sure, here you go!");
							if (!player.getCarriedItems().hasCatalogID(ItemId.A_FREE_SHANTAY_DISCLAIMER.id(), Optional.of(false))) {
								npcsay(player, shantayGuard, "Here, have a disclaimer...",
									"It means that Shantay isn't responsible if you die in the desert.");
								player.message("The guard gives you a disclaimer.");
								give(player, ItemId.A_FREE_SHANTAY_DISCLAIMER.id(), 1);
							}
							player.message("you go through the gate");
							player.teleport(62, 735);
						} else {
							player.message("Shantay guard seem to be busy at the moment.");
						}
					}
				} else if (menu == 1) {
					mes("You decide that your visit to the desert can be postponed..");
					delay(3);
					player.message("Perhaps indefinitely!");
				}
			} else if (command.equals("look")) {
				mes("You look at the huge Stone Gate.");
				delay(3);
				mes("On the gate is a large poster, it reads.");
				delay(3);
				mes("@gre@The Desert is a VERY Dangerous place...do not enter if you are scared of dying.");
				delay(3);
				mes("@gre@Beware of high temperatures, sand storms, robbers, and slavers...");
				delay(3);
				mes("@gre@No responsibility is taken by Shantay ");
				delay(3);
				mes("@gre@If anything bad should happen to you in any circumstances whatsoever.");
				delay(3);
				mes("Despite this warning lots of people seem to pass through the gate.");
				delay(3);
			}
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.A_FREE_SHANTAY_DISCLAIMER.id();
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.A_FREE_SHANTAY_DISCLAIMER.id()) {
			player.message("This looks very important indeed, would you like to read it now?");
			give(player, ItemId.A_FREE_SHANTAY_DISCLAIMER.id(), 1);
			i.remove();
			int menu = multi(player, "Yes, I'll read it now!", "No thanks, it'll keep!");
			if (menu == 0) {
				ActionSender.sendBox(player, "@red@*** Shantay Disclaimer***% %@gre@The Desert is a VERY Dangerous place.% %@red@Do not enter if you're scared of dying.% %@gre@Beware of high temperatures, sand storms, and slavers% %@red@No responsibility is taken by Shantay% %@gre@If anything bad happens to you under any circumstances.", true);
			} else if (menu == 1) {
				player.message("You decide not to read the disclaimer.");
			}
		}
	}
}
