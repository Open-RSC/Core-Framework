package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class WitchesPotion implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener, ObjectActionListener,
	ObjectActionExecutiveListener, PlayerKilledNpcExecutiveListener,
	PlayerKilledNpcListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.WITCHS_POTION;
	}

	@Override
	public String getQuestName() {
		return "Witch's potion";
	}
	
	@Override
	public boolean isMembers() {
		return false;
	}
	
	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the witches potion quest");
		incQuestReward(player, Quests.questData.get(Quests.WITCHS_POTION), true);
		player.message("@gre@You haved gained 1 quest point!");
	}

	private void hettyDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
				case 0:
					npcTalk(p, n, "Greetings Traveller",
						"What could you want with an old woman like me?");
					int choice = showMenu(p, n,
						"I am in search of a quest",
						"I've heard that you are a witch");
					if (choice == 0) {
						npcTalk(p, n, "Hmm maybe I can think of something for you",
							"Would you like to become more proficient in the dark arts?");
						int choice2 = showMenu(p, n, false, //do not send over
							"Yes help me become one with my darker side",
							"No I have my principles and honour",
							"What you mean improve my magic?");
						if (choice2 == 0) {
							playerTalk(p, n, "Yes help me become one with my darker side");
							hettyDialogue(p, n, Hetty.SOUNDOFIT_ALRIGHT);
						} else if (choice2 == 1) {
							playerTalk(p, n, "No, I have my principles and honour");
							npcTalk(p, n, "Suit yourself, but you're missing out");
						} else if (choice2 == 2) {
							playerTalk(p, n, "What you mean improve my magic?");
							npcTalk(p, n, "Yes improve your magic",
								"Do you have no sense of drama?");
							int choice4 = showMenu(p, n,
								"Yes I'd like to improve my magic",
								"No I'm not interested",
								"Show me the mysteries of the dark arts");
							if (choice4 == 0) {
								p.message("The witch sighs");
								hettyDialogue(p, n, Hetty.SOUNDOFIT_ALRIGHT);
							} else if (choice4 == 1) {
								npcTalk(p, n, "Many aren't to start off with",
									"But I think you'll be drawn back to this place");
							} else if (choice4 == 2) {
								hettyDialogue(p, n, Hetty.SOUNDOFIT_ALRIGHT);
							}
						}
					} else if (choice == 1) {
						npcTalk(p,
							n,
							"Yes it does seem to be getting fairly common knowledge",
							"I fear I may get a visit from the witch hunters of Falador before long");
					}
					break;
				case 1:
					npcTalk(p, n, "So have you found the things for the potion");
					if (p.getInventory().hasItemId(ItemId.RATS_TAIL.id())
						&& p.getInventory().hasItemId(ItemId.EYE_OF_NEWT.id())
						&& p.getInventory().hasItemId(ItemId.BURNTMEAT.id())
						&& p.getInventory().hasItemId(ItemId.ONION.id())) {
						playerTalk(p, n, "Yes I have everthing");
						npcTalk(p, n, "Excellent, can I have them then?");
						p.message("You pass the ingredients to Hetty");
						p.getInventory().remove(ItemId.RATS_TAIL.id(), 1);
						p.getInventory().remove(ItemId.EYE_OF_NEWT.id(), 1);
						p.getInventory().remove(ItemId.BURNTMEAT.id(), 1);
						p.getInventory().remove(ItemId.ONION.id(), 1);
						message(p,
							"Hetty put's all the ingredients in her cauldron",
							"Hetty closes her eyes and begins to chant");
						npcTalk(p, n, "Ok drink from the cauldron");
						sleep(2000);
						p.updateQuestStage(getQuestId(), 2);
					} else {
						playerTalk(p, n, "No not yet");
						npcTalk(p, n, "Well remember you need to get",
							"An eye of newt, a rat's tail,some burnt meat and an onion");
					}
					break;
				case 2:
					npcTalk(p, n, "Greetings Traveller",
						"Well are you going to drink the potion or not?");
					break;
				case -1:
					npcTalk(p, n, "Greetings Traveller",
						"How's your magic coming along?");
					playerTalk(p, n, "I'm practicing and slowly getting better");
					npcTalk(p, n, "good good");
					break;
			}
		}
		switch (cID) {
			case Hetty.SOUNDOFIT_ALRIGHT:
				npcTalk(p,
					n,
					"Ok I'm going to make a potion to help bring out your darker self",
					"So that you can perform acts of  dark magic with greater ease",
					"You will need certain ingredients");
				playerTalk(p, n, "What do I need");
				npcTalk(p, n,
					"You need an eye of newt, a rat's tail, an onion and a piece of burnt meat");
				p.updateQuestStage(getQuestId(), 1);
				break;
		}
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.HETTY.id()) {
			hettyDialogue(p, n, -1);
		} /*else if (n.getID() == NpcId.RAT_WITCHES_POTION.id()) { // This is not proven to be authentic, the earliest reference for this is Moparscape Classic Punkrocker's quest version from July 2009
			if (p.getQuestStage(this) >= -1) {
				p.message("Rats can't talk!");
			}
		}*/
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (command.equals("drink from") && obj.getID() == 147
			&& obj.getX() == 316 && obj.getY() == 666) {
			if (player.getQuestStage(this) != 2) {
				playerTalk(player, null, "I'd rather not",
					"It doesn't look very tasty");
			} else {
				message(player, "You drink from the cauldron",
					"You feel yourself imbued with power");
				player.sendQuestComplete(Constants.Quests.WITCHS_POTION);
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.HETTY.id() || n.getID() == NpcId.RAT_WITCHES_POTION.id();
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
									 Player player) {
		return obj.getID() == 147 && command.equals("drink from");
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == NpcId.RAT_WITCHES_POTION.id();
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (p.getQuestStage(this) >= 1) {
			World.getWorld().registerItem(new GroundItem(ItemId.RATS_TAIL.id(), n.getX(), n.getY(), 1, p));
			n.killedBy(p);
		} else {
			n.killedBy(p);
		}
	}

	class Hetty {
		public static final int SOUNDOFIT_ALRIGHT = 0;
	}
}
