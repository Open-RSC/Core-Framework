package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.handlers.CommandHandler;
import com.openrsc.server.plugins.authentic.commands.RegularPlayer;
import com.openrsc.server.plugins.authentic.npcs.varrock.Reldo;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class Superchisel implements OpInvTrigger, UseInvTrigger, UseNpcTrigger {
	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		boolean firstTime = !player.getCache().hasKey("superchiseluses");
		if (firstTime) {
			player.getCache().set("superchiseluses", 1);
		} else {
			player.getCache().set("superchiseluses", player.getCache().getInt("superchiseluses") + 1);
		}

		mes("You twiddle the superchisel");
		if (firstTime) delay(3);
		mes("You remember your mother scolding you...");
		if (firstTime) delay(3);
		mes("@yel@Mum: Stop fidgeting! It's a bad habit!");

		boolean firstTimeFluffs = player.getCache().hasKey("superchiselfluffsuses");
		boolean reldoSpeak = false;
		boolean startedsuperchiselquest = player.getCache().hasKey("superchiselquest");
		int superchiselqueststate = 0;
		if (startedsuperchiselquest) {
			superchiselqueststate = player.getCache().getInt("superchiselquest");
		}
		int menu;
		if ((player.getClientVersion() >= 174 || !firstTimeFluffs) && !startedsuperchiselquest) {
			menu = multi(player,
				"Saved commands",
				"Saved commands 2",
				"ResetCrystal",
				"Fluffs",
				"Stop fidgeting!");
		} else {
			menu = multi(player,
				"Saved commands",
				"Saved commands 2",
				"ResetCrystal",
				"Reldo",
				"Stop fidgeting!");
			reldoSpeak = true;
		}

		switch (menu) {
			case 0: // Saved commands
			case 1:
				String[] savedCommands = loadSavedCommands(player, menu);
				int commandChoice = multi(player, null, savedCommands);
				if (commandChoice == -1) break;
				if (savedCommands[commandChoice].equalsIgnoreCase("(unset)")) {
					tellHowToSetCommand(player, commandChoice + (menu * 5));
				} else {
					CommandHandler.handleCommandString(player, savedCommands[commandChoice]);
				}
				break;
			case 2: // Reset Crystal
				ResetCrystal.doResetCrystal(player, false);
				break;
			case 3: // Fluffs / Reldo
				boolean allowedToQuery = handleSuperchiselQuestChat(player, superchiselqueststate, firstTimeFluffs, startedsuperchiselquest);

				if (player.getCache().hasKey("superchiselfluffsuses")) {
					player.getCache().set("superchiselfluffsuses", player.getCache().getInt("superchiselfluffsuses") + 1);
				} else {
					player.getCache().set("superchiselfluffsuses", 1);
				}

				if (allowedToQuery) {
					if (reldoSpeak) {
						player.playerServerMessage(MessageType.QUEST, "After consulting with Traiborn, Reldo relays a list of all players currently online:");
					} else {
						if (player.getCarriedItems().hasCatalogID(ItemId.GERTRUDES_CAT.id())) {
							player.playerServerMessage(MessageType.QUEST, "Fluffs seems offended that you won't stroke her, but meows the names of all players online anyway:");
						} else {
							player.playerServerMessage(MessageType.QUEST, "Fluffs meows through the superchisel and you think can hear the names of all players online???"); // what is happening
						}
					}

					RegularPlayer.queryOnlinePlayers(player, true, false);
				}
				break;
			case 4:
				player.playerServerMessage(MessageType.QUEST, "You put the superchisel back in your sack...");
				break;
		}
	}

	private void tellHowToSetCommand(Player player, int savedCommandNumber) {
		player.playerServerMessage(MessageType.QUEST, "To define a saved command here in the superchisel, type @mag@::defineslot" + savedCommandNumber);
		player.playerServerMessage(MessageType.QUEST, "followed by the full command string you would like saved & selectable here.");
	}

	private String[] loadSavedCommands(Player player, int menu) {
		String[] savedCommands = new String[5];
		for (int i = 0; i < savedCommands.length; i++) {
			if (player.getCache().hasKey("savedcommand" + (i + menu * 5))) {
				savedCommands[i] = player.getCache().getString("savedcommand" + (i + menu * 5));
			} else {
				savedCommands[i] = "(unset)";
			}
		}
		return savedCommands;
	}

	private boolean handleSuperchiselQuestChat(Player player, int superchiselQuestState, boolean firstTimeFluffs, boolean startedSuperchiselQuest) {
		if (superchiselQuestState == -1) return true;
		if (superchiselQuestState > 0) { // superchisel quest in progress
			switch (superchiselQuestState) {
				case 1:
					mes("You speak into the superchisel");
					delay(3);
					say(player, null, "Hey Reldo can you hear me");
					mes("@yel@Reldo: What sorcery is this?");
					delay(3);
					say(player, null, "I'm speaking to you through a superchisel!");
					mes("@yel@Reldo: ...a superchisel?");
					delay(3);
					say(player, null, "Yes, to be honest, I'm not really sure what it is!",
						"But when I twiddle it, I feel super!");
					mes("@yel@Reldo: Fascinating...!");
					delay(3);
					mes("@yel@Reldo: I'd love to have a look at this artifact if you brought it by some time.");
					delay(3);
					say(player, null, "Sure, I'll see about doing that.",
						"But I'm talking to you today because",
						"I've heard you're the man in charge of census keeping");
					mes("@yel@Reldo: Yes, that is true.");
					delay(3);
					say(player, null, "Do you think you'd be able to tell me the names", "of all players online at this moment then?");
					mes("@yel@Reldo: Uhm,... officially no.");
					delay(3);
					mes("@yel@Reldo: But if you let me see that \"superchisel\","); delay(3);
					mes("@yel@Reldo: I'll see what we can work out.");
					player.getCache().set("superchiselquest", 2);
					return false;
				case 2:
					mes("You speak into the superchisel");
					delay(3);
					say(player, null, "Hey Reldo can you hear me");
					mes("@yel@Reldo: Yes, hello!"); delay(3);
					say(player, null, "Could you tell me the names of all players online?");
					mes("@yel@Reldo: I'm still quite keen to have a look at that thing!"); delay(3);
					mes("@yel@Reldo: I'm waiting for you in the Palace library in Varrock");
					return false;
				case 3:
				case 4:
					// Portion of the quest where Reldo is holding the superchisel, so the player shouldn't have one to reach here.
					mes("You speak into the superchisel");
					delay(3);
					say(player, null, "Hey Reldo can you hear me");
					mes("@yel@Reldo: Huh?? What's going on??"); delay(3);
					mes("@yel@Reldo: Is there ... a second superchisel?"); delay(3);
					say(player, null, "Yeah actually I can get as many as I want");
					mes("@yel@Reldo: ... How?"); delay(3);
					say(player, null, "I just kind of think about it and they appear in my knapsack.");
					mes("@yel@Reldo: You must surely be a very powerful mage."); delay(3);
					mes("@yel@Reldo: Perhaps even one of the \"administrators\" this chisel keeps whispering to me about...");
					return false;
				default:
					mes("Nothing interesting happens.");
					return false;
			}
		} else if (firstTimeFluffs || (player.getClientVersion() < 174 && !startedSuperchiselQuest)) {
			mes("You speak into the superchisel"); delay(3);
			say(player, null, "Uhm, Fluffs, are you in there?");
			mes("@yel@Fluffs: meow???"); delay(3);
			say(player, null, "Wow it's working!!!");
			if (player.getClientVersion() < 174) { // Fluffs released 2003-07-28
				say(player, null, "Hey do you know anything about online players in this version?");
				mes("@yel@Fluffs: uhm.... no, I don't live there."); delay(3);
				mes("@yel@Fluffs: you really want to talk to someone local,"); delay(3);
				mes("@yel@Fluffs: someone in charge of census taking"); delay(3);
				mes("@yel@Fluffs: There's someone like that in Varrock,"); delay(3);
				mes("@yel@Fluffs: smart guy named Reldo"); delay(3);
				mes("@yel@Fluffs: I'm trying to convince Gerty to move out there some day"); delay(4);
				mes("@yel@Fluffs: Away from this podunk nowhere cottage"); delay(3);
				mes("@yel@Fluffs: closer to those of power and influence"); delay(4);
				if (config().INFLUENCE_INSTEAD_QP) {
					say(player, null, "Yes, well there certainly is Influence here");
				}
				say(player, null, "Thanks for the advice Fluffs,","I'll try Reldo next time.");
				mes("@yel@Fluffs: Mrrroeow.");
				player.getCache().set("superchiselquest", 1);
				return false;
			} else {
				if (player.getCarriedItems().hasCatalogID(ItemId.GERTRUDES_CAT.id())) {
					mes("@yel@Fluffs: I'm literally right here"); delay(3);
					mes("@yel@Fluffs: stop speaking into that thing it's weird"); delay(3);
				} else {
					mes("@yel@Fluffs: Well, I suppose working from home with Gertrude"); delay(3);
					mes("@yel@Fluffs: is better than caravanning around with you"); delay(3);
				}
			}
			player.getCache().set("superchiselfluffsuses", 0);
			delay(3);
		}
		return true;
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.SUPERCHISEL.id();
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		// mes("It does kind of look like those would go together, eh?");

		// THIS IS ALL MADE UP.
		// IT IS NOT BASED ON REAL RSC DATA.
		// IT IS A BASELESS GUESS.
		// PLEASE DO NOT MISTAKE THIS WITH THE AUTHENTIC BEHAVIOUR OF THE SUPER CHISEL.
		// -- LOGG 2022-03-17
		Item notSuperchisel = item1;
		if (item1.getCatalogId() == ItemId.SUPERCHISEL.id()) {
			notSuperchisel = item2;
		}
		if (notSuperchisel.getCatalogId() == ItemId.BALL_OF_WOOL.id()) {
			int choice = multi(player, "Make stringed amulet",
				"Spawn uncut gems",
				"Spawn cut gems",
				"Give crafting tools",
				"Give gold bars");
			switch (choice) {
				case 0:
					int whatKindOfAmulet = multi(player, "Gold", "Sapphire", "Emerald", "Ruby", "Diamond");
					switch (whatKindOfAmulet) {
						case 0:
							give(player, ItemId.GOLD_AMULET.id(), 1);
							player.incExp(Skill.CRAFTING.id(), 30 * 4, true);
							break;
						case 1:
							give(player, ItemId.SAPPHIRE_AMULET.id(), 1);
							player.incExp(Skill.CRAFTING.id(), 65 * 4, true);
							break;
						case 2:
							give(player, ItemId.EMERALD_AMULET.id(), 1);
							player.incExp(Skill.CRAFTING.id(), 70 * 4, true);
							break;
						case 3:
							give(player, ItemId.RUBY_AMULET.id(), 1);
							player.incExp(Skill.CRAFTING.id(), 85 * 4, true);
							break;
						case 4:
							give(player, ItemId.DIAMOND_AMULET.id(), 1);
							player.incExp(Skill.CRAFTING.id(), 100 * 4, true);
							break;
					}
				case 1:
					give(player, ItemId.UNCUT_SAPPHIRE.id(), 3);
					give(player, ItemId.UNCUT_EMERALD.id(), 3);
					give(player, ItemId.UNCUT_RUBY.id(), 3);
					give(player, ItemId.UNCUT_DIAMOND.id(), 3);
					break;
				case 2:
					give(player, ItemId.SAPPHIRE.id(), 3);
					give(player, ItemId.EMERALD.id(), 3);
					give(player, ItemId.RUBY.id(), 3);
					give(player, ItemId.DIAMOND.id(), 3);
					break;
				case 3:
					int toolChoice = multi(player, "Ball of Wool", "Moulds", "Chisel");
					switch (toolChoice) {
						case 0:
							give(player, ItemId.BALL_OF_WOOL.id(), 1);
							break;
						case 1:
							give(player, ItemId.RING_MOULD.id(), 1);
							give(player, ItemId.NECKLACE_MOULD.id(), 1);
							give(player, ItemId.AMULET_MOULD.id(), 1);
							break;
						case 2:
							give(player, ItemId.CHISEL.id(), 1);
							break;
					}
				case 4:
					give(player, ItemId.GOLD_BAR.id(), 10);
					break;
			}
		} else if (notSuperchisel.getCatalogId() == ItemId.UNCUT_SAPPHIRE.id()) {
			if (player.getCarriedItems().remove(new Item(ItemId.UNCUT_SAPPHIRE.id())) > -1) {
				give(player, ItemId.SAPPHIRE.id(), 1);
				player.incExp(Skill.CRAFTING.id(), 50 * 4, true);
			}
		} else if (notSuperchisel.getCatalogId() == ItemId.UNCUT_EMERALD.id()) {
			if (player.getCarriedItems().remove(new Item(ItemId.UNCUT_EMERALD.id())) > -1) {
				give(player, ItemId.EMERALD.id(), 1);
				player.incExp(Skill.CRAFTING.id(), (int) (67.5 * 4), true);
			}
		} else if (notSuperchisel.getCatalogId() == ItemId.UNCUT_RUBY.id()) {
			if (player.getCarriedItems().remove(new Item(ItemId.UNCUT_RUBY.id())) > -1) {
				give(player, ItemId.RUBY.id(), 1);
				player.incExp(Skill.CRAFTING.id(), 85 * 4, true);
			}
		} else if (notSuperchisel.getCatalogId() == ItemId.UNCUT_DIAMOND.id()) {
			if (player.getCarriedItems().remove(new Item(ItemId.UNCUT_DIAMOND.id())) > -1) {
				give(player, ItemId.DIAMOND.id(), 1);
				player.incExp(Skill.CRAFTING.id(), (int) (107.5 * 4), true);
			}
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.BALL_OF_WOOL.id(), ItemId.SUPERCHISEL.id())
			|| compareItemsIds(item1, item2, ItemId.SUPERCHISEL.id(), ItemId.UNCUT_SAPPHIRE.id())
			|| compareItemsIds(item1, item2, ItemId.SUPERCHISEL.id(), ItemId.UNCUT_EMERALD.id())
			|| compareItemsIds(item1, item2, ItemId.SUPERCHISEL.id(), ItemId.UNCUT_RUBY.id())
			|| compareItemsIds(item1, item2, ItemId.SUPERCHISEL.id(), ItemId.UNCUT_DIAMOND.id())
		;
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (player.getCache().hasKey("superchiselquest")) {
			int chiselqueststate = player.getCache().getInt("superchiselquest");
			if (chiselqueststate >= 2) {
				Reldo.superchiselQuestDialogue(player, npc, chiselqueststate);
				return;
			}
		}

		player.message("Nothing interesting happens.");
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return player.hasElevatedPriveledges() && item.getCatalogId() == ItemId.SUPERCHISEL.id() && npc.getID() == NpcId.RELDO.id();
	}
}
