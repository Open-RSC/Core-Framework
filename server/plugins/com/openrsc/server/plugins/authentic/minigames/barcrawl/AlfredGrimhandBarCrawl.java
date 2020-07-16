package com.openrsc.server.plugins.authentic.minigames.barcrawl;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class AlfredGrimhandBarCrawl implements MiniGameInterface, TalkNpcTrigger,
	OpLocTrigger {

	@Override
	public int getMiniGameId() {
		return Minigames.ALFRED_GRIMHANDS_BARCRAWL;
	}

	@Override
	public String getMiniGameName() {
		return "Alfred Grimhand's Barcrawl (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		//mini-quest complete handled already
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 311 && obj.getX() == 494;
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.BARBARIAN_GUARD.id();
	}

	@Override
	public void onOpLoc(Player player, final GameObject obj, String command) {
		if (obj.getID() == 311 && obj.getX() == 494) {
			if (player.getCache().hasKey("barcrawl_completed")) {
				doGate(player, obj);
				return;
			}
			Npc barbarian = player.getWorld().getNpc(NpcId.BARBARIAN_GUARD.id(), 494, 500, 538, 550);
			if (barbarian != null) {
				barbarian.initializeTalkScript(player);
			}
		}
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (n.getID() == NpcId.BARBARIAN_GUARD.id()) {
			if (player.getCache().hasKey("barcrawl_completed")) {
				npcsay(player, n, "Ello friend");
				return;
			}
			if (player.getCache().hasKey("barcrawl")) {
				npcsay(player, n, "So hows the barcrawl coming along?");
				if (!player.getCarriedItems().hasCatalogID(ItemId.BARCRAWL_CARD.id(), Optional.of(false))) {
					final int third = multi(player, n, false, //do not send over
						"I've lost my  barcrawl card",
						"Not to bad, my barcrawl card is in my bank now");
					if (third == 0) {
						npcsay(player, n, "What are you like?",
							"You're gonna have to start all over now",
							"Here you go, have another barcrawl card");
						give(player, ItemId.BARCRAWL_CARD.id(), 1);
						player.getCache().remove("barone");
						player.getCache().remove("bartwo");
						player.getCache().remove("barthree");
						player.getCache().remove("barfour");
						player.getCache().remove("barfive");
						player.getCache().remove("barsix");
					} else if (third == 1) {
						say(player, n, "Not to bad, my barcrawl card is in my bank now");
						npcsay(player, n,
							"You need it with you when you are going on a barcrawl");
					}
					return;
				} else if (player.getCache().hasKey("barone")
					&& player.getCache().hasKey("bartwo")
					&& player.getCache().hasKey("barthree")
					&& player.getCache().hasKey("barfour")
					&& player.getCache().hasKey("barfive")
					&& player.getCache().hasKey("barsix") && player.getCarriedItems().hasCatalogID(ItemId.BARCRAWL_CARD.id(), Optional.of(false))) {
					say(player, n,
						"I think I jusht about done them all, but I losht count");
					mes("You give the card to the barbarian");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.BARCRAWL_CARD.id()));
					npcsay(player,
						n,
						"Yep that seems fine",
						"I never learned to read, but you look like you've drunk plenty",
						"You can come in now");
					player.getCache().store("barcrawl_completed", true);
					//allows another completion of barcrawl if player had drop tricked
					player.getCache().remove("barone");
					player.getCache().remove("bartwo");
					player.getCache().remove("barthree");
					player.getCache().remove("barfour");
					player.getCache().remove("barfive");
					player.getCache().remove("barsix");
					player.sendMiniGameComplete(this.getMiniGameId(), Optional.empty());
				} else {
					say(player, n, "I haven't finished it yet");
					npcsay(player, n,
						"Well come back when you have, you lightweight");
				}
				return;
			}
			npcsay(player, n, "Oi whaddya want?");
			final int first = multi(player, n,
				"I want to come through this gate", "I want some money");
			if (first == 0) {
				npcsay(player, n, "Barbarians only", "Are you a barbarian?",
					"You don't look like one");
				final int second = multi(player, n,
					"Hmm, yep you've got me there",
					"Looks can be deceiving, I am in fact a barbarian");
				if (second == 0) {
					// NOTHING
				} else if (second == 1) {
					npcsay(player,
						n,
						"If you're a barbarian you need to be able to drink like one",
						"We barbarians like a good drink",
						"And I have the perfect challenge for you",
						"The Alfred Grimhand barcrawl",
						"First done by Alfred Grimhand");
					mes("The guard hands you a barcrawl card");
					delay(3);
					give(player, ItemId.BARCRAWL_CARD.id(), 1);
					npcsay(player,
						n,
						"Take that card to each of the bars named on it",
						"The bartenders all know what it means",
						"We're kinda well known",
						"They'll give you their strongest drink and sign your card",
						"When you done all that, we'll be happy to let you in");
					player.getCache().store("barcrawl", true);
				}
			} else if (first == 1) {
				npcsay(player, n, "Well do I look like a banker to you?");
			}
		}
	}
}
