package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class VampireSlayer implements QuestInterface,TalkToNpcListener,
		TalkToNpcExecutiveListener, ObjectActionListener,
		ObjectActionExecutiveListener, PlayerKilledNpcExecutiveListener,
		PlayerKilledNpcListener, PlayerAttackNpcExecutiveListener {
	@Override
	public int getQuestId() {
		return Constants.Quests.VAMPIRE_SLAYER;
	}

	@Override
	public String getQuestName() {
		return "Vampire slayer";
	}

	private void morganDialogue(Player p, Npc n) {
		switch (p.getQuestStage(this)) {
		case 0:
			npcTalk(p, n, "Please please help us, bold hero");
			playerTalk(p, n, "What's the problem?");
			npcTalk(p,
					n,
					"Our little village has been dreadfully ravaged by an evil vampire",
					"There's hardly any of us left",
					"We need someone to get rid of him once and for good");
			int choice = showMenu(p, n, new String[] {
					"No. vampires are scary", "Ok I'm up for an adventure",
					"I tried fighting him. He wouldn't die" });
			if (choice == 0) {
				npcTalk(p, n, "I don't blame you");
			} else if (choice == 1) {
				npcTalk(p,
						n,
						"I think first you should seek help",
						"I have a friend who is a retired vampire hunter",
						"Called Dr Hallow",
						"He may be able to give you some tips",
						"He can normally be found in the Jolly boar inn these days",
						"He's a bit of an old soak",
						"Mention his old friend Morgan",
						"I'm sure he wouldn't want me to be killed by a vampire");
				playerTalk(p, n, "I'll look him up then");
				p.updateQuestStage(getQuestId(), 1);
			} else if (choice == 2) {
				npcTalk(p,
						n,
						"Maybe you're not going about it right",
						"I think first you should seek help",
						"I have a friend who is a retired vampire hunter",
						"Called Dr Hallow",
						"He may be able to give you some tips",
						"He can normally be found in the Jolly boar inn these days",
						"He's a bit of an old soak",
						"Mention his old friend Morgan",
						"I'm sure he wouldn't want me to be killed by a vampire");
				playerTalk(p, n, "I'll look him up then");
				p.updateQuestStage(getQuestId(), 1);
			}
			break;
		case 1:
		case 2:
			npcTalk(p, n, "How are you doing with your quest?");
			playerTalk(p, n, "I'm working on it still");
			npcTalk(p, n, "Please hurry", "Every day we live in fear of lives",
					"That we will be the vampires next victim");
			break;
		case -1:
			npcTalk(p, n, "How are you doing with your quest?");
			playerTalk(p, n, "I have slain the foul creature");
			npcTalk(p, n, "Thank you, thank you",
					"You will always be a hero in our village");
			break;
		}
	}

	private void harlowDialogue(Player p, Npc n) {
		switch (p.getQuestStage(this)) {
		case -1:
		case 1:
		case 2:
			String[] options;
			npcTalk(p, n, "Buy me a drink pleassh");
			if (!hasItem(p, 217)
					&& p.getQuestStage(Constants.Quests.VAMPIRE_SLAYER) != -1) {
				options = new String[] { "No you've had enough", "Ok mate",
						"Morgan needs your help" };
			} else {
				options = new String[] { "No you've had enough", "Ok mate" };
			}
			int choice = showMenu(p, n, options);
			if (choice == 0) {
			} else if (choice == 1) {
				if (p.getInventory().hasItemId(193)) {
					p.message("You give a beer to Dr Harlow");
					p.getInventory().remove(
							p.getInventory().getLastIndexById(193));
					npcTalk(p, n, "Cheersh matey");
				} else {
					playerTalk(p, n, "I'll just go and buy one");
				}
			} else if (choice == 2) {
				npcTalk(p, n, "Morgan you shhay?");
				playerTalk(p, n,
						"His village is being terrorised by a vampire",
						"He wanted me to ask you how I should go about stoping it");
				npcTalk(p, n,
						"Buy me a beer then I will teash you what you need to know");
				int choice2 = showMenu(p, n, new String[] { "Ok mate",
						"But this is your friend Morgan we're talking about" });
				if (choice2 == 0) {
					if (p.getInventory().hasItemId(193)) {
						p.message("You give a beer to Dr Harlow");
						npcTalk(p, n, "Cheersh matey");
						p.getInventory().remove(p.getInventory().getLastIndexById(193));
						playerTalk(p, n, "So tell me how to kill vampires then");
						npcTalk(p, n,
								"Yesh yesh vampires I was very good at killing em once");
						p.message("Dr Harlow appears to sober up slightly");
						npcTalk(p,
								n,
								"Well you're gonna to kill it with a stake",
								"Otherwishe he'll just regenerate",
								"Yes your killing blow must be done with a stake",
								"I jusht happen to have one on me");
						p.message("Dr Harlow hands you a stake");
						p.getInventory().add(new Item(217));
						npcTalk(p,
								n,
								"You'll need a hammer to hand to drive it in properly as well",
								"One last thing",
								"It's wise to carry garlic with you",
								"Vampires are weakened somewhat if they can smell garlic",
								"Dunno where you'd find that though",
								"Remember even then a vampire is a dangeroush foe");
						playerTalk(p, n, "Thank you very much");
						p.updateQuestStage(getQuestId(), 2);
					} else {
						playerTalk(p, n, "I'll just go and buy one");

					}
				} else if (choice2 == 1) {
					npcTalk(p, n, "Buy ush a drink anyway");
				}
			}
			break;
		}
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 97) {
			morganDialogue(p, n);
		}
		if (n.getID() == 98) {
			harlowDialogue(p, n);
		}

	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (player.getQuestStage(this) == -1 && command.equals("search")
				&& obj.getID() == 136 && obj.getY() == 3380) {
			player.message("There's a pillow in here");
			return;
		} else if (obj.getID() == 141 && obj.getY() == 1562) {
			if (!player.getInventory().hasItemId(218)) {
				player.message("You search the cupboard");
				player.message("You find a clove of garlic that you take");
				player.getInventory().add(new Item(218));
			} else {
				player.message("You search the cupboard");
				player.message("The cupboard is empty");
			}
			return;
		} else if (obj.getID() == 136 && obj.getY() == 3380) {
			for(Npc npc : player.getRegion().getNpcs()) {
				if(npc.getID() == 96 && npc.getAttribute("spawnedFor", null).equals(player)) {
					player.message("There's nothing there.");
					return;
				}
			}
			
			final Npc n = spawnNpc(96, 206, 3381, 1000 * 60 * 5, player);
			n.setShouldRespawn(false);
			player.message("A vampire jumps out off the coffin");
			return;
		}
	}
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 97 || n.getID() == 98) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 136 && obj.getY() == 3380) {
			return true;
		}
		if (obj.getID() == 141 && obj.getY() == 1562) {
			return true;
		}
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the vampire slayer quest");
		player.incQuestExp(ATTACK, player.getSkills().getMaxStat(ATTACK) * 600 + 1300);
		player.incQuestPoints(3);
		player.message("@gre@You haved gained 3 quest points!");

	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == 96) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == 96) {
			if (p.getInventory().wielding(217) == true
					&& p.getInventory().hasItemId(168)) {
				p.getInventory().remove(p.getInventory().getLastIndexById(217));
				p.message("You hammer the stake in to the vampires chest!");
				n.killedBy(p);
				n.remove();
				// Completed Vampire Slayer Quest.
				if (p.getQuestStage(this) == 2) {
					p.sendQuestComplete(Constants.Quests.VAMPIRE_SLAYER);
				}
			} else {
				n.getSkills().setLevel(3, 35);
				p.message("The vampire seems to regenerate");
			}
		}
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if (n.getID() == 96) {
			if (p.getInventory().hasItemId(218)) {
				p.message("The vampire appears to weaken");
			}
		}
		return false;
	}
}
