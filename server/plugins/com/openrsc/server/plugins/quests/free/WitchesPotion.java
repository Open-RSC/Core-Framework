package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class WitchesPotion implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	KillNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.WITCHS_POTION;
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
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.WITCHS_POTION), true);
		player.message("@gre@You haved gained 1 quest point!");
	}

	private void hettyDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
				case 0:
					npcsay(p, n, "Greetings Traveller",
						"What could you want with an old woman like me?");
					int choice = multi(p, n,
						"I am in search of a quest",
						"I've heard that you are a witch");
					if (choice == 0) {
						npcsay(p, n, "Hmm maybe I can think of something for you",
							"Would you like to become more proficient in the dark arts?");
						int choice2 = multi(p, n, false, //do not send over
							"Yes help me become one with my darker side",
							"No I have my principles and honour",
							"What you mean improve my magic?");
						if (choice2 == 0) {
							say(p, n, "Yes help me become one with my darker side");
							hettyDialogue(p, n, Hetty.SOUNDOFIT_ALRIGHT);
						} else if (choice2 == 1) {
							say(p, n, "No, I have my principles and honour");
							npcsay(p, n, "Suit yourself, but you're missing out");
						} else if (choice2 == 2) {
							say(p, n, "What you mean improve my magic?");
							npcsay(p, n, "Yes improve your magic",
								"Do you have no sense of drama?");
							int choice4 = multi(p, n,
								"Yes I'd like to improve my magic",
								"No I'm not interested",
								"Show me the mysteries of the dark arts");
							if (choice4 == 0) {
								p.message("The witch sighs");
								hettyDialogue(p, n, Hetty.SOUNDOFIT_ALRIGHT);
							} else if (choice4 == 1) {
								npcsay(p, n, "Many aren't to start off with",
									"But I think you'll be drawn back to this place");
							} else if (choice4 == 2) {
								hettyDialogue(p, n, Hetty.SOUNDOFIT_ALRIGHT);
							}
						}
					} else if (choice == 1) {
						npcsay(p,
							n,
							"Yes it does seem to be getting fairly common knowledge",
							"I fear I may get a visit from the witch hunters of Falador before long");
					}
					break;
				case 1:
					npcsay(p, n, "So have you found the things for the potion");
					if (p.getCarriedItems().hasCatalogID(ItemId.RATS_TAIL.id())
						&& p.getCarriedItems().hasCatalogID(ItemId.EYE_OF_NEWT.id())
						&& p.getCarriedItems().hasCatalogID(ItemId.BURNTMEAT.id())
						&& p.getCarriedItems().hasCatalogID(ItemId.ONION.id())) {
						say(p, n, "Yes I have everthing");
						npcsay(p, n, "Excellent, can I have them then?");
						p.message("You pass the ingredients to Hetty");
						p.getCarriedItems().remove(new Item(ItemId.RATS_TAIL.id()));
						p.getCarriedItems().remove(new Item(ItemId.EYE_OF_NEWT.id()));
						p.getCarriedItems().remove(new Item(ItemId.BURNTMEAT.id()));
						p.getCarriedItems().remove(new Item(ItemId.ONION.id()));
						Functions.mes(p,
							"Hetty put's all the ingredients in her cauldron",
							"Hetty closes her eyes and begins to chant");
						npcsay(p, n, "Ok drink from the cauldron");
						delay(p.getWorld().getServer().getConfig().GAME_TICK * 3);
						p.updateQuestStage(getQuestId(), 2);
					} else {
						say(p, n, "No not yet");
						npcsay(p, n, "Well remember you need to get",
							"An eye of newt, a rat's tail,some burnt meat and an onion");
					}
					break;
				case 2:
					npcsay(p, n, "Greetings Traveller",
						"Well are you going to drink the potion or not?");
					break;
				case -1:
					npcsay(p, n, "Greetings Traveller",
						"How's your magic coming along?");
					say(p, n, "I'm practicing and slowly getting better");
					npcsay(p, n, "good good");
					break;
			}
		}
		switch (cID) {
			case Hetty.SOUNDOFIT_ALRIGHT:
				npcsay(p,
					n,
					"Ok I'm going to make a potion to help bring out your darker self",
					"So that you can perform acts of  dark magic with greater ease",
					"You will need certain ingredients");
				say(p, n, "What do I need");
				npcsay(p, n,
					"You need an eye of newt, a rat's tail, an onion and a piece of burnt meat");
				p.updateQuestStage(getQuestId(), 1);
				break;
		}
	}

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.HETTY.id()) {
			hettyDialogue(p, n, -1);
		} /*else if (n.getID() == NpcId.RAT_WITCHES_POTION.id()) { // This is not proven to be authentic, the earliest reference for this is Moparscape Classic Punkrocker's quest version from July 2009
			if (p.getQuestStage(this) >= -1) {
				p.message("Rats can't talk!");
			}
		}*/
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {
		if (command.equals("drink from") && obj.getID() == 147
			&& obj.getX() == 316 && obj.getY() == 666) {
			if (player.getQuestStage(this) != 2) {
				say(player, null, "I'd rather not",
					"It doesn't look very tasty");
			} else {
				Functions.mes(player, "You drink from the cauldron",
					"You feel yourself imbued with power");
				player.sendQuestComplete(Quests.WITCHS_POTION);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.HETTY.id() /*|| n.getID() == NpcId.RAT_WITCHES_POTION.id()*/;
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command,
							  Player player) {
		return obj.getID() == 147 && command.equals("drink from");
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return n.getID() == NpcId.RAT_WITCHES_POTION.id();
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (p.getQuestStage(this) >= 1) {
			p.getWorld().registerItem(new GroundItem(p.getWorld(), ItemId.RATS_TAIL.id(), n.getX(), n.getY(), 1, p));
			n.killedBy(p);
		} else {
			n.killedBy(p);
		}
	}

	class Hetty {
		public static final int SOUNDOFIT_ALRIGHT = 0;
	}
}
