package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.give;
import static com.openrsc.server.plugins.Functions.ifnearvisnpc;
import static com.openrsc.server.plugins.Functions.mes;
import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.say;
import static com.openrsc.server.plugins.Functions.remove;
import static com.openrsc.server.plugins.Functions.thinkbubble;
import static com.openrsc.server.plugins.Functions.multi;
import static com.openrsc.server.plugins.Functions.delay;

public class Panning implements OpLocTrigger, UseLocTrigger, UseNpcTrigger, OpInvTrigger {

	private static int PANNING_POINT = 1058;

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return obj.getID() == PANNING_POINT;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == PANNING_POINT) {
			p.message("If I had a panning tray I could pan here");
		}
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return obj.getID() == PANNING_POINT;
	}

	private boolean handlePanning(GameObject obj, Item item, Player p) {
		if (!p.getCache().hasKey("unlocked_panning")) {
			return false;
		}
		p.setBusy(true);
		Functions.thinkbubble(p, new Item(ItemId.PANNING_TRAY.id()));
		p.playSound("mix");
		p.playerServerMessage(MessageType.QUEST, "You scrape the tray along the bottom");
		mes(p, "You swirl away the excess water");
		delay(1500);
		Functions.thinkbubble(p, new Item(ItemId.PANNING_TRAY_FULL.id()));
		p.playerServerMessage(MessageType.QUEST, "You lift the full tray from the water");
		p.getCarriedItems().getInventory().replace(ItemId.PANNING_TRAY.id(), ItemId.PANNING_TRAY_FULL.id());
		p.incExp(Skills.MINING, 20, true);
		p.setBusy(false);
		return false;
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == PANNING_POINT) {
			if (item.getCatalogId() == ItemId.PANNING_TRAY.id()) {
				Npc guide = ifnearvisnpc(p, NpcId.DIGSITE_GUIDE.id(), 15);
				if (guide != null) {
					// NOT SURE? if(p.getQuestStage(Quests.DIGSITE) < 2) {
					if (!p.getCache().hasKey("unlocked_panning")) {
						npcsay(p, guide, "Hey! you can't pan yet!");
						Functions.say(p, guide, "Why not ?");
						npcsay(p, guide, "We do not allow the uninvited to pan here");
						int menu = Functions.multi(p, guide,
							"Okay, forget it",
							"So how do I become invited then ?");
						if (menu == 0) {
							npcsay(p, guide, "You can of course use this place when you know what you are doing");
						} else if (menu == 1) {
							npcsay(p, guide, "I'm not supposed to let people pan here",
								"Unless they have permission from the authorities first",
								"Mind you I could let you have a go...",
								"If you're willing to do me a favour");
							Functions.say(p, guide, "What's that ?");
							npcsay(p, guide, "Well...to be honest...",
								"What I would really like...",
								"Is a nice cup of tea !");
							Functions.say(p, guide, "Tea !?");
							npcsay(p, guide, "Absolutely, I'm parched !",
								"If you could bring me one of those...",
								"I would be more than willing to let you pan here");
						}
					} else {
						handlePanning(obj, item, p);
					}
				}
			} else if (item.getCatalogId() == ItemId.PANNING_TRAY_FULL.id()) {
				p.playerServerMessage(MessageType.QUEST, "This panning tray already contains something");
			} else if (item.getCatalogId() == ItemId.PANNING_TRAY_GOLD_NUGGET.id()) {
				p.playerServerMessage(MessageType.QUEST, "This panning tray already contains gold");
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player p, Npc npc, Item item) {
		return npc.getID() == NpcId.DIGSITE_GUIDE.id();
	}

	@Override
	public void onUseNpc(Player p, Npc npc, Item item) {
		if (npc.getID() == NpcId.DIGSITE_GUIDE.id()) {
			if (item.getCatalogId() == ItemId.PANNING_TRAY.id()) {
				p.message("You give the panning tray to the guide");
				npcsay(p, npc, "Yes, this is a panning tray...");
			}
			if (item.getCatalogId() == ItemId.PANNING_TRAY_FULL.id()) {
				p.message("You give the full panning tray to the guide");
				npcsay(p, npc, "This is no good to me",
					"I don't deal with finds");
			}
			if (item.getCatalogId() == ItemId.PANNING_TRAY_GOLD_NUGGET.id()) {
				p.message("You give the full panning tray to the guide");
				npcsay(p, npc, "I am afraid I don't deal with finds",
					"That's not my job");
			}
			if (item.getCatalogId() == ItemId.CUP_OF_TEA.id()) {
				if (p.getCache().hasKey("unlocked_panning")) {
					npcsay(p, npc, "No thanks, I've had enough!");
				} else {
					npcsay(p, npc, "Ah! Lovely!",
						"You can't beat a good cuppa...",
						"You're free to pan all you want");
					Functions.say(p, npc, "Thanks");
					Functions.remove(p, ItemId.CUP_OF_TEA.id(), 1);
					p.getCache().store("unlocked_panning", true);
				}
			}
		}
	}

	@Override
	public boolean blockOpInv(Item item, Player p, String command) {
		return item.getCatalogId() == ItemId.PANNING_TRAY.id() || item.getCatalogId() == ItemId.PANNING_TRAY_FULL.id() || item.getCatalogId() == ItemId.PANNING_TRAY_GOLD_NUGGET.id();
	}


	@Override
	public void onOpInv(Item item, Player p, String command) {
		if (item.getCatalogId() == ItemId.PANNING_TRAY.id()) {
			p.playerServerMessage(MessageType.QUEST, "You search the contents of the tray");
			Functions.say(p, null, "Err, why am I searching an empty tray ?");
		} else if (item.getCatalogId() == ItemId.PANNING_TRAY_FULL.id()) {
			p.setBusy(true);
			mes(p, "You search the contents of the tray...");
			delay(1500);
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
			p.getCarriedItems().getInventory().replace(ItemId.PANNING_TRAY_FULL.id(), ItemId.PANNING_TRAY.id());
			if (addItem != -1) {
				if (addItem == ItemId.COINS.id()) {
					p.playerServerMessage(MessageType.QUEST, "You find some coins within the mud");
				} else if (addItem == ItemId.ROCK_SAMPLE_ORANGE.id()) {
					p.playerServerMessage(MessageType.QUEST, "You find a rock sample covered in mud");
				} else if (addItem == ItemId.UNCUT_OPAL.id() || addItem == ItemId.UNCUT_JADE.id() || addItem == ItemId.UNCUT_SAPPHIRE.id()) {
					p.playerServerMessage(MessageType.QUEST, "You find a gem within the mud!");
				} else if (addItem == ItemId.PANNING_TRAY_GOLD_NUGGET.id()) {
					p.playerServerMessage(MessageType.QUEST, "You find some gold nuggets within the mud!");
				}
				give(p, addItem, addAmount);
			} else {
				p.playerServerMessage(MessageType.QUEST, "The tray contains only plain mud");
			}
			p.setBusy(false);
		} else if (item.getCatalogId() == ItemId.PANNING_TRAY_GOLD_NUGGET.id()) {
			p.getCarriedItems().getInventory().replace(ItemId.PANNING_TRAY_GOLD_NUGGET.id(), ItemId.PANNING_TRAY.id());
			give(p, ItemId.GOLD_NUGGETS.id(), 1);
			p.message("You take the gold form the panning tray");
			p.message("You have a handful of gold nuggets");
		}
	}
}
