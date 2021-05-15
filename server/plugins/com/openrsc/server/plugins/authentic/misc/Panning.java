package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class Panning implements OpLocTrigger, UseLocTrigger, UseNpcTrigger, OpInvTrigger {

	private static int PANNING_POINT = 1058;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == PANNING_POINT;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == PANNING_POINT) {
			player.message("If I had a panning tray I could pan here");
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == PANNING_POINT;
	}

	private boolean handlePanning(GameObject obj, Item item, Player player) {
		if (!player.getCache().hasKey("unlocked_panning")) {
			return false;
		}
		thinkbubble(new Item(ItemId.PANNING_TRAY.id()));
		player.playSound("mix");
		player.playerServerMessage(MessageType.QUEST, "You scrape the tray along the bottom");
		mes("You swirl away the excess water");
		delay(3);
		thinkbubble(new Item(ItemId.PANNING_TRAY_FULL.id()));
		player.playerServerMessage(MessageType.QUEST, "You lift the full tray from the water");
		player.getCarriedItems().remove(new Item(ItemId.PANNING_TRAY.id()));
		player.getCarriedItems().getInventory().add(new Item(ItemId.PANNING_TRAY_FULL.id()));
		player.incExp(Skill.MINING.id(), 20, true);
		return false;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == PANNING_POINT) {
			if (item.getCatalogId() == ItemId.PANNING_TRAY.id()) {
				Npc guide = ifnearvisnpc(player, NpcId.DIGSITE_GUIDE.id(), 15);
				if (guide != null) {
					// NOT SURE? if(p.getQuestStage(Quests.DIGSITE) < 2) {
					if (!player.getCache().hasKey("unlocked_panning")) {
						npcsay(player, guide, "Hey! you can't pan yet!");
						say(player, guide, "Why not ?");
						npcsay(player, guide, "We do not allow the uninvited to pan here");
						int menu = multi(player, guide,
							"Okay, forget it",
							"So how do I become invited then ?");
						if (menu == 0) {
							npcsay(player, guide, "You can of course use this place when you know what you are doing");
						} else if (menu == 1) {
							npcsay(player, guide, "I'm not supposed to let people pan here",
								"Unless they have permission from the authorities first",
								"Mind you I could let you have a go...",
								"If you're willing to do me a favour");
							say(player, guide, "What's that ?");
							npcsay(player, guide, "Well...to be honest...",
								"What I would really like...",
								"Is a nice cup of tea !");
							say(player, guide, "Tea !?");
							npcsay(player, guide, "Absolutely, I'm parched !",
								"If you could bring me one of those...",
								"I would be more than willing to let you pan here");
						}
					} else {
						handlePanning(obj, item, player);
					}
				}
			} else if (item.getCatalogId() == ItemId.PANNING_TRAY_FULL.id()) {
				player.playerServerMessage(MessageType.QUEST, "This panning tray already contains something");
			} else if (item.getCatalogId() == ItemId.PANNING_TRAY_GOLD_NUGGET.id()) {
				player.playerServerMessage(MessageType.QUEST, "This panning tray already contains gold");
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.DIGSITE_GUIDE.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == NpcId.DIGSITE_GUIDE.id()) {
			if (item.getCatalogId() == ItemId.PANNING_TRAY.id()) {
				player.message("You give the panning tray to the guide");
				npcsay(player, npc, "Yes, this is a panning tray...");
			}
			if (item.getCatalogId() == ItemId.PANNING_TRAY_FULL.id()) {
				player.message("You give the full panning tray to the guide");
				npcsay(player, npc, "This is no good to me",
					"I don't deal with finds");
			}
			if (item.getCatalogId() == ItemId.PANNING_TRAY_GOLD_NUGGET.id()) {
				player.message("You give the full panning tray to the guide");
				npcsay(player, npc, "I am afraid I don't deal with finds",
					"That's not my job");
			}
			if (item.getCatalogId() == ItemId.CUP_OF_TEA.id()) {
				if (player.getCache().hasKey("unlocked_panning")) {
					npcsay(player, npc, "No thanks, I've had enough!");
				} else {
					npcsay(player, npc, "Ah! Lovely!",
						"You can't beat a good cuppa...",
						"You're free to pan all you want");
					say(player, npc, "Thanks");
					player.getCarriedItems().remove(new Item(ItemId.CUP_OF_TEA.id()));
					player.getCache().store("unlocked_panning", true);
				}
			}
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.PANNING_TRAY.id() || item.getCatalogId() == ItemId.PANNING_TRAY_FULL.id() || item.getCatalogId() == ItemId.PANNING_TRAY_GOLD_NUGGET.id();
	}


	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.PANNING_TRAY.id()) {
			player.playerServerMessage(MessageType.QUEST, "You search the contents of the tray");
			say(player, null, "Err, why am I searching an empty tray ?");
		} else if (item.getCatalogId() == ItemId.PANNING_TRAY_FULL.id()) {
			mes("You search the contents of the tray...");
			delay(3);
			int randomNumber = DataConversions.random(0, 100);
			int addItem = -1;
			int addAmount = 1;
			if (randomNumber < 40) { // 40%
				addItem = -1;
			} else if (randomNumber < 50) { // 10%
				addItem = ItemId.UNCUT_OPAL.id();
			} else if (randomNumber < 60) { // 10%
				addItem = ItemId.UNCUT_JADE.id();
			} else if (randomNumber < 70) { // 10%
				addItem = ItemId.COINS.id();
				int[] randomCoins = {1, 2, 5, 10};
				addAmount = randomCoins[DataConversions.random(0, (randomCoins.length - 1))];
			} else if (randomNumber < 80) { // 10%
				addItem = ItemId.ROCK_SAMPLE_ORANGE.id();
			} else if (randomNumber < 90) { // 10%
				addItem = ItemId.GOLD_NUGGETS.id();
			} else if (randomNumber < 100) { // 10%
				addItem = ItemId.UNCUT_SAPPHIRE.id();
			}
			player.getCarriedItems().remove(new Item(ItemId.PANNING_TRAY_FULL.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.PANNING_TRAY.id()));
			if (addItem != -1) {
				if (addItem == ItemId.COINS.id()) {
					player.playerServerMessage(MessageType.QUEST, "You find some coins within the mud");
				} else if (addItem == ItemId.ROCK_SAMPLE_ORANGE.id()) {
					player.playerServerMessage(MessageType.QUEST, "You find a rock sample covered in mud");
				} else if (addItem == ItemId.UNCUT_OPAL.id() || addItem == ItemId.UNCUT_JADE.id() || addItem == ItemId.UNCUT_SAPPHIRE.id()) {
					player.playerServerMessage(MessageType.QUEST, "You find a gem within the mud!");
				} else if (addItem == ItemId.PANNING_TRAY_GOLD_NUGGET.id()) {
					player.playerServerMessage(MessageType.QUEST, "You find some gold nuggets within the mud!");
				}
				give(player, addItem, addAmount);
			} else {
				player.playerServerMessage(MessageType.QUEST, "The tray contains only plain mud");
			}
		} else if (item.getCatalogId() == ItemId.PANNING_TRAY_GOLD_NUGGET.id()) {
			player.getCarriedItems().remove(new Item(ItemId.PANNING_TRAY_GOLD_NUGGET.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.PANNING_TRAY.id()));
			give(player, ItemId.GOLD_NUGGETS.id(), 1);
			player.message("You take the gold form the panning tray");
			player.message("You have a handful of gold nuggets");
		}
	}
}
