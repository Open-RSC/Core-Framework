package com.openrsc.server.plugins.minigames.barcrawl;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class AlfredGrimhandBarCrawl implements MiniGameInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener, ObjectActionListener,
	ObjectActionExecutiveListener {
	
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
	public void handleReward(Player p) {
		//mini-quest complete handled already
	}
	
	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 311 && obj.getX() == 494;
	}

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.BARBARIAN_GUARD.id();
	}

	@Override
	public void onObjectAction(final GameObject obj, String command, Player p) {
		if (obj.getID() == 311 && obj.getX() == 494) {
			if (p.getCache().hasKey("barcrawl_completed")) {
				doGate(p, obj);
				return;
			}
			Npc barbarian = p.getWorld().getNpc(NpcId.BARBARIAN_GUARD.id(), 494, 500, 538, 550);
			if (barbarian != null) {
				barbarian.initializeTalkScript(p);
			}
		}
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if (n.getID() == NpcId.BARBARIAN_GUARD.id()) {
			if (p.getCache().hasKey("barcrawl_completed")) {
				npcTalk(p, n, "Ello friend");
				return;
			}
			if (p.getCache().hasKey("barcrawl")) {
				npcTalk(p, n, "So hows the barcrawl coming along?");
				if (!hasItem(p, ItemId.BARCRAWL_CARD.id())) {
					final int third = showMenu(p, n, false, //do not send over
						"I've lost my  barcrawl card",
						"Not to bad, my barcrawl card is in my bank now");
					if (third == 0) {
						npcTalk(p, n, "What are you like?",
							"You're gonna have to start all over now",
							"Here you go, have another barcrawl card");
						addItem(p, ItemId.BARCRAWL_CARD.id(), 1);
						p.getCache().remove("barone");
						p.getCache().remove("bartwo");
						p.getCache().remove("barthree");
						p.getCache().remove("barfour");
						p.getCache().remove("barfive");
						p.getCache().remove("barsix");
					} else if (third == 1) {
						playerTalk(p, n, "Not to bad, my barcrawl card is in my bank now");
						npcTalk(p, n,
							"You need it with you when you are going on a barcrawl");
					}
					return;
				} else if (p.getCache().hasKey("barone")
					&& p.getCache().hasKey("bartwo")
					&& p.getCache().hasKey("barthree")
					&& p.getCache().hasKey("barfour")
					&& p.getCache().hasKey("barfive")
					&& p.getCache().hasKey("barsix") && hasItem(p, ItemId.BARCRAWL_CARD.id())) {
					playerTalk(p, n,
						"I think I jusht about done them all, but I losht count");
					message(p, "You give the card to the barbarian");
					removeItem(p, ItemId.BARCRAWL_CARD.id(), 1);
					npcTalk(p,
						n,
						"Yep that seems fine",
						"I never learned to read, but you look like you've drunk plenty",
						"You can come in now");
					p.getCache().store("barcrawl_completed", true);
					//allows another completion of barcrawl if player had drop tricked
					p.getCache().remove("barone");
					p.getCache().remove("bartwo");
					p.getCache().remove("barthree");
					p.getCache().remove("barfour");
					p.getCache().remove("barfive");
					p.getCache().remove("barsix");
					p.sendMiniGameComplete(this.getMiniGameId(), Optional.empty());
				} else {
					playerTalk(p, n, "I haven't finished it yet");
					npcTalk(p, n,
						"Well come back when you have, you lightweight");
				}
				return;
			}
			npcTalk(p, n, "Oi whaddya want?");
			final int first = showMenu(p, n,
				"I want to come through this gate", "I want some money");
			if (first == 0) {
				npcTalk(p, n, "Barbarians only", "Are you a barbarian?",
					"You don't look like one");
				final int second = showMenu(p, n,
					"Hmm, yep you've got me there",
					"Looks can be deceiving, I am in fact a barbarian");
				if (second == 0) {
					// NOTHING
				} else if (second == 1) {
					npcTalk(p,
						n,
						"If you're a barbarian you need to be able to drink like one",
						"We barbarians like a good drink",
						"And I have the perfect challenge for you",
						"The Alfred Grimhand barcrawl",
						"First done by Alfred Grimhand");
					message(p, "The guard hands you a barcrawl card");
					addItem(p, ItemId.BARCRAWL_CARD.id(), 1);
					npcTalk(p,
						n,
						"Take that card to each of the bars named on it",
						"The bartenders all know what it means",
						"We're kinda well known",
						"They'll give you their strongest drink and sign your card",
						"When you done all that, we'll be happy to let you in");
					p.getCache().store("barcrawl", true);
				}
			} else if (first == 1) {
				npcTalk(p, n, "Well do I look like a banker to you?");
			}
		}
	}
}
