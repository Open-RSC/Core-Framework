package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
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

public class WitchesPotion implements QuestInterface,TalkToNpcListener,
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

	class Hetty {
		public static final int SOUNDOFIT_ALRIGHT = 0;
	}

	private void hettyDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
			case 0:
				npcTalk(p, n, "Greetings traveller",
						"What could you want with an old woman like me");
				int choice = showMenu(p, n, new String[] {
						"I am in search of a quest",
						"I've heard that you are a witch" });
				if (choice == 0) {
					npcTalk(p, n, "Hmm maybe i can think of something for you",
							"Would you like to become more proficient in the dark arts?");
					int choice2 = showMenu(p, n, new String[] {
							"Yes help me become one with my darker side",
							"No I have my principles and honour",
							"What, you mean improve my magic?" });
					if (choice2 == 0) {
						hettyDialogue(p, n, Hetty.SOUNDOFIT_ALRIGHT);

					} else if (choice2 == 1) {
						npcTalk(p, n, "Suit yourself, but you're missing out");
					} else if (choice2 == 2) {
						npcTalk(p, n, "Yes improve your magic",
								"Do you have no sense of drama");
						int choice4 = showMenu(p, n, new String[] {
								"Yes I'd like to improve my magic",
								"No I'm not interested",
								"Show me the mysteries of the dark arts" });
						if (choice4 == 0) {
							hettyDialogue(p, n, Hetty.SOUNDOFIT_ALRIGHT);
						} else if (choice4 == 1) {
							npcTalk(p, n, "Many aren't to start off with",
									"But i think you'll be drawn back to this place");
						} else if (choice4 == 2) {
							hettyDialogue(p, n, Hetty.SOUNDOFIT_ALRIGHT);
						}
					}
				} else if (choice == 1) {
					npcTalk(p,
							n,
							"Yes it does seem to be getting fairly common knowledge",
							"I fear i may get a visit from the witch hunters of falador before long");
				}
				break;
			case 1:
				npcTalk(p, n, "So have you found the things for the potion");
				if (p.getInventory().hasItemId(271)
						&& p.getInventory().hasItemId(270)
						&& p.getInventory().hasItemId(134)
						&& p.getInventory().hasItemId(241)) {
					playerTalk(p, n, "Yes i have everything");
					npcTalk(p, n, "Excellent, can i have them then?");
					p.message("You pass the ingredients to Hetty");
					p.getInventory().remove(271, 1);
					p.getInventory().remove(270, 1);
					p.getInventory().remove(134, 1);
					p.getInventory().remove(241, 1);
					message(p,
							"Hetty puts all the ingredients in her cauldron",
							"She closes her eyes and begins to chant");
					npcTalk(p, n, "Ok drink from the cauldron");
					sleep(2000);
					p.updateQuestStage(getQuestId(), 2);
				} else {
					playerTalk(p, n, "No not yet");
					npcTalk(p, n, "Well remember what you need to get",
							"An eye of newt, a rat's tail, some burnt meat and an onion");
				}
				break;
			case 2:
				npcTalk(p, n, "Well are you going to drink the potion or not?");
				break;
			case -1:
				npcTalk(p, n, "Greetings traveller",
						"How's your magic coming along");
				playerTalk(p, n, "I'm practicing and slowly getting better");
				npcTalk(p, n, "Good, good");
				break;
			}
		}
		switch (cID) {
		case Hetty.SOUNDOFIT_ALRIGHT:
			npcTalk(p,
					n,
					"Ok, I'm going to make a potion to help bring out your darker self",
					"So that you can perform acts of dark magic with greater ease");
			playerTalk(p, n, "Dark magic?");
			npcTalk(p, n, "It's not as ominous as it sounds, trust me");
			int choice3 = showMenu(p, n, new String[] {
					"No, I don't like the sound of it", "Well, alright..." });
			if (choice3 == 0) {
				npcTalk(p, n, "Fine, suit yourself",
						"But I sense a great deal of dark power within you",
						"You'll change your mind one day");
			} else if (choice3 == 1) {
				npcTalk(p, n, "You will need certain ingredients");
				playerTalk(p, n, "What do i need?");
				npcTalk(p, n,
						"You need an eye of newt, a rat's tail, an onion and a piece of burnt meat");
				p.updateQuestStage(getQuestId(), 1);
			}
			break;
		}
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 148) {
			hettyDialogue(p, n, -1);
		} else if (n.getID() == 29) {
			if (p.getQuestStage(this) >= -1) {
				p.message("Rats can't talk!");
			}
		}
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (command.equals("drink from") && obj.getID() == 147
				&& obj.getX() == 316 && obj.getY() == 666) {
			if (player.getQuestStage(this) != 2) {
				/*
				 * Really sloppy, david :P. player.setBusy(true);
				 */
				playerTalk(player, null, "I'd rather not",
						"It doesn't look very tasty");
			} else {
				message(player, "You drink from the cauldron",
						"You feel yourself imbued with power");
				player.sendQuestComplete(Constants.Quests.WITCHS_POTION);
				return;
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 148 || n.getID() == 29) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 147 && command.equals("drink from")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == 29) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (p.getQuestStage(this) >= 1) {
			World.getWorld().registerItem(new GroundItem(271, n.getX(), n.getY(), 1, p));
			n.killedBy(p);
		} else {
			n.killedBy(p);
		}
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the witches potion quest");
		incQuestReward(player, Quests.questData.get(Quests.WITCHS_POTION), true);
		player.message("@gre@You haved gained 1 quest point!");
	}

	@Override
	public boolean isMembers() {
		return false;
	}
}
