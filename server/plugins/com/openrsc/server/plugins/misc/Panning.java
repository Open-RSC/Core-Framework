package com.openrsc.server.plugins.misc;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showBubble;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;

public class Panning implements ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener, InvActionListener, InvActionExecutiveListener {

	private static int PANNING_POINT = 1058;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == PANNING_POINT;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == PANNING_POINT) {
			p.message("If I had a panning tray I could pan here");
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		return obj.getID() == PANNING_POINT;
	}

	private boolean handlePanning(GameObject obj, Item item, Player p) {
		if (!p.getCache().hasKey("unlocked_panning")) {
			return false;
		}
		p.setBusy(true);
		showBubble(p, new Item(ItemId.PANNING_TRAY.id()));
		p.playSound("mix");
		p.playerServerMessage(MessageType.QUEST, "You scrape the tray along the bottom");
		message(p, "You swirl away the excess water");
		sleep(1500);
		showBubble(p, new Item(ItemId.PANNING_TRAY_FULL.id()));
		p.playerServerMessage(MessageType.QUEST, "You lift the full tray from the water");
		p.getInventory().replace(ItemId.PANNING_TRAY.id(), ItemId.PANNING_TRAY_FULL.id());
		p.incExp(SKILLS.MINING.id(), 20, true);
		p.setBusy(false);
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == PANNING_POINT) {
			if (item.getID() == ItemId.PANNING_TRAY.id()) {
				Npc guide = getNearestNpc(p, NpcId.DIGSITE_GUIDE.id(), 15);
				if (guide != null) {
					// NOT SURE? if(p.getQuestStage(Constants.Quests.DIGSITE) < 2) {
					if (!p.getCache().hasKey("unlocked_panning")) {
						npcTalk(p, guide, "Hey! you can't pan yet!");
						playerTalk(p, guide, "Why not ?");
						npcTalk(p, guide, "We do not allow the uninvited to pan here");
						int menu = showMenu(p, guide,
							"Okay, forget it",
							"So how do I become invited then ?");
						if (menu == 0) {
							npcTalk(p, guide, "You can of course use this place when you know what you are doing");
						} else if (menu == 1) {
							npcTalk(p, guide, "I'm not supposed to let people pan here",
								"Unless they have permission from the authorities first",
								"Mind you I could let you have a go...",
								"If you're willing to do me a favour");
							playerTalk(p, guide, "What's that ?");
							npcTalk(p, guide, "Well...to be honest...",
								"What I would really like...",
								"Is a nice cup of tea !");
							playerTalk(p, guide, "Tea !?");
							npcTalk(p, guide, "Absolutely, I'm parched !",
								"If you could bring me one of those...",
								"I would be more than willing to let you pan here");
						}
					} else {
						handlePanning(obj, item, p);
					}
				}
			} else if (item.getID() == ItemId.PANNING_TRAY_FULL.id()) {
				p.playerServerMessage(MessageType.QUEST, "This panning tray already contains something");
			} else if (item.getID() == ItemId.PANNING_TRAY_GOLD_NUGGET.id()) {
				p.playerServerMessage(MessageType.QUEST, "This panning tray already contains gold");
			}
		}
	}

	@Override
	public boolean blockInvUseOnNpc(Player p, Npc npc, Item item) {
		return npc.getID() == NpcId.DIGSITE_GUIDE.id();
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc npc, Item item) {
		if (npc.getID() == NpcId.DIGSITE_GUIDE.id()) {
			if (item.getID() == ItemId.PANNING_TRAY.id()) {
				p.message("You give the panning tray to the guide");
				npcTalk(p, npc, "Yes, this is a panning tray...");
			}
			if (item.getID() == ItemId.PANNING_TRAY_FULL.id()) {
				p.message("You give the full panning tray to the guide");
				npcTalk(p, npc, "This is no good to me",
					"I don't deal with finds");
			}
			if (item.getID() == ItemId.PANNING_TRAY_GOLD_NUGGET.id()) {
				p.message("You give the full panning tray to the guide");
				npcTalk(p, npc, "I am afraid I don't deal with finds",
					"That's not my job");
			}
			if (item.getID() == ItemId.CUP_OF_TEA.id()) {
				if (p.getCache().hasKey("unlocked_panning")) {
					npcTalk(p, npc, "No thanks, I've had enough!");
				} else {
					npcTalk(p, npc, "Ah! Lovely!",
						"You can't beat a good cuppa...",
						"You're free to pan all you want");
					playerTalk(p, npc, "Thanks");
					removeItem(p, ItemId.CUP_OF_TEA.id(), 1);
					p.getCache().store("unlocked_panning", true);
				}
			}
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player p) {
		return item.getID() == ItemId.PANNING_TRAY.id() || item.getID() == ItemId.PANNING_TRAY_FULL.id() || item.getID() == ItemId.PANNING_TRAY_GOLD_NUGGET.id();
	}


	@Override
	public void onInvAction(Item item, Player p) {
		if (item.getID() == ItemId.PANNING_TRAY.id()) {
			p.playerServerMessage(MessageType.QUEST, "You search the contents of the tray");
			playerTalk(p, null, "Err, why am I searching an empty tray ?");
		} else if (item.getID() == ItemId.PANNING_TRAY_FULL.id()) {
			p.setBusy(true);
			message(p, "You search the contents of the tray...");
			sleep(1500);
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
			p.getInventory().replace(ItemId.PANNING_TRAY_FULL.id(), ItemId.PANNING_TRAY.id());
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
				addItem(p, addItem, addAmount);
			} else {
				p.playerServerMessage(MessageType.QUEST, "The tray contains only plain mud");
			}
			p.setBusy(false);
		} else if (item.getID() == ItemId.PANNING_TRAY_GOLD_NUGGET.id()) {
			p.getInventory().replace(ItemId.PANNING_TRAY_GOLD_NUGGET.id(), ItemId.PANNING_TRAY.id());
			addItem(p, ItemId.GOLD_NUGGETS.id(), 1);
			p.message("You take the gold form the panning tray");
			p.message("You have a handful of gold nuggets");
		}
	}
}
