package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class TheRestlessGhost implements QuestInterface,PickupExecutiveListener, PickupListener,
		TalkToNpcListener, TalkToNpcExecutiveListener, ObjectActionListener,
		InvUseOnObjectListener, InvUseOnObjectExecutiveListener,
		ObjectActionExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.THE_RESTLESS_GHOST;
	}

	@Override
	public String getQuestName() {
		return "The restless ghost";
	}

	class Ghost {
		public static final int DONTSPEAK = 0;
		public static final int GOODBYE = 1;
		public static final int NOTSURE = 2;
		public static final int WHY = 3;
		public static final int SCARY = 4;
	}

	private void ghostDialogue(Player p, Npc n, int cID) {
		if (n.getID() == 15) {
			if(p.getQuestStage(this) == -1) {
				p.message("the ghost doesn't appear interested in talking");
				return;
			}
			if (cID == -1) {
				if (p.getQuestStage(this) == 3) {
					playerTalk(p, n, "Hello ghost, how are you?");
					npcTalk(p, n, "How are you doing finding my skull?");
					if (!hasItem(p, 27)) {
						playerTalk(p, n, "Sorry, I can't find it at the moment");
						npcTalk(p,
								n,
								"Ah well keep on looking",
								"I'm pretty sure it's somewhere in the tower south west from here",
								"There's a lot of levels to the tower, though",
								"I suppose it might take a little while to find");
					} else if (hasItem(p, 27)) {
						playerTalk(p, n, "I have found it");
						npcTalk(p,
								n,
								"Hurrah now i can stop being a ghost",
								"You just need to put it in my coffin over there",
								"And I will be free");
					}
					return;
				}
				if (p.getQuestStage(this) == 0
						|| !p.getInventory().wielding(24)) {
					playerTalk(p, n, "Hello ghost, how are you?");
					npcTalk(p, n, "Wooo woooo wooooo");
					int choice = showMenu(p, n, new String[] {
							"Sorry I don't speak ghost",
							"Ooh that's interesting",
							"Any hints where I can find some treasure?" });
					if (choice == 0) {
						ghostDialogue(p, n, Ghost.DONTSPEAK);
					} else if (choice == 1) {
						npcTalk(p, n, "Woo wooo", "Wooooooooooooooo");
						int choice2 = showMenu(p, n,
								new String[] { "Did he really?",
										"Yeah that's what I thought" });
						if (choice2 == 0) {
							npcTalk(p, n, "Woo");
							int choice3 = showMenu(p, n, new String[] {
									"My brother had exactly the same problem",
									"Goodbye. Thanks for the chat" });
							if (choice3 == 0) {
								npcTalk(p, n, "Woo wooooo",
										"Wooooo woo woo woo");
								int choice4 = showMenu(
										p,
										n,
										new String[] {
												"Goodbye. Thanks for the chat",
												"You'll have to give me the recipe sometime" });
								if (choice4 == 0) {
									ghostDialogue(p, n, Ghost.GOODBYE);
								} else if (choice4 == 1) {
									npcTalk(p, n, "Woooooooo woo woooooooo");
									int choice6 = showMenu(p, n, new String[] {
											"Goodbye. Thanks for the chat",
											"Hmm I'm not sure about that" });
									if (choice6 == 0) {
										ghostDialogue(p, n, Ghost.GOODBYE);
									} else if (choice6 == 1) {
										ghostDialogue(p, n, Ghost.NOTSURE);
									}
								}
							} else if (choice3 == 1) {
								npcTalk(p, n, "Wooo wooo",
										"Wooo woooooooooooooooo");
								int choice7 = showMenu(p, n, new String[] {
										"Goodbye. Thanks for the chat",
										"Hmm I'm not sure about that" });
								if (choice7 == 0) {
									ghostDialogue(p, n, Ghost.GOODBYE);
								} else if (choice7 == 1) {
									ghostDialogue(p, n, Ghost.NOTSURE);
								}
							}
						} else if (choice2 == 1) {
							npcTalk(p, n, "Wooo woooooooooooooooo");
							int choice5 = showMenu(p, n, new String[] {
									"Goodbye. Thanks for the chat",
									"Hmm I'm not sure about that" });
							if (choice5 == 0) {
								ghostDialogue(p, n, Ghost.GOODBYE);
							} else if (choice5 == 1) {
								ghostDialogue(p, n, Ghost.NOTSURE);
							}
						}
					} else if (choice == 2) {
						npcTalk(p, n, "Wooooooooo woo!");
						int choice8 = showMenu(p, n, new String[] {
								"Sorry I don't speak ghost",
								"Thank you. You've beeen very helpful" });
						if (choice8 == 0) {
							ghostDialogue(p, n, Ghost.DONTSPEAK);
						} else if (choice8 == 1) {
							npcTalk(p, n, "Wooooooooo");
						}
					}
				} else {
					playerTalk(p, n, "Hello ghost, how are you?");
					npcTalk(p, n, "Not very good actually");
					playerTalk(p, n, "What's the problem then?");
					npcTalk(p, n, "Did you just understand what I said?");
					int choice = showMenu(
							p,
							n,
							new String[] {
									"Yep, now tell me what the problem is",
									"No, you sound like you're speaking nonsense to me",
									"Wow, this amulet works" });
					if (choice == 0) {
						npcTalk(p, n,
								"Wow this is incredible, i didn't expect anyone to understand me again");
						playerTalk(p, n, "Yes, yes I can understand you",
								"But have you any idea why you're doomed to be a ghost?");
						npcTalk(p, n, "I'm not sure");
						playerTalk(
								p,
								n,
								"I've been told a certain task may need to be completed",
								"So you can rest in peace");
						npcTalk(p, n, "I should think it is probably because ",
								"a warlock has come along and stolen my skull",
								"If you look inside my coffin there",
								"you'll find my corpse without a head on it");
						playerTalk(p, n,
								"Do you know where this warlock might be now?");
						npcTalk(p,
								n,
								"I think it was one of the warlocks who lives in the big tower",
								"In the sea southwest from here");
						playerTalk(p, n,
								"Ok I will try and get the skull back for you, so you can rest in peace.");
						npcTalk(p,
								n,
								"Ooh thank you. That would be such a great relief",
								"It is so dull being a ghost");
						p.updateQuestStage(Constants.Quests.THE_RESTLESS_GHOST, 3);
					} else if (choice == 1) {
						npcTalk(p, n,
								"Oh that's a pity. You got my hopes up there");
						playerTalk(p, n, "Yeah, it is pity. Sorry");
						npcTalk(p, n, "Hang on a second. You can understand me");
						int choice2 = showMenu(p, n, new String[] {
								"No I can't", "Yep clever aren't I" });
						if (choice2 == 0) {
							npcTalk(p, n,
									"I don't know, the first person I can speak to in ages is a moron");
						} else if (choice2 == 1) {
							npcTalk(p, n, "I'm impressed",
									"You must be very powerful",
									"I don't suppose you can stop me being a ghost?");
							int choice3 = showMenu(p, n, new String[] {
									"Yes, Ok. Do you know why you're a ghost?",
									"No, you're scary" });
							if (choice3 == 0) {
								ghostDialogue(p, n, Ghost.WHY);
							} else if (choice3 == 1) {
								ghostDialogue(p, n, Ghost.SCARY);
							}
						}
					} else if (choice == 2) {
						npcTalk(p,
								n,
								"Oh its your amulet that's doing it. I did wonder",
								"I don't suppose you can help me? I don't like being a ghost");
						int choice3 = showMenu(p, n, new String[] {
								"Yes, Ok. Do you know why you're a ghost?",
								"No, you're scary" });
						if (choice3 == 0) {
							ghostDialogue(p, n, Ghost.WHY);
						} else if (choice3 == 1) {
							ghostDialogue(p, n, Ghost.SCARY);
						}
					}
				}
				return;// try now, son. :D
			}
			switch (cID) {
			case Ghost.DONTSPEAK:
				npcTalk(p, n, "Woo woo?");
				playerTalk(p, n, "Nope still don't understand you");
				npcTalk(p, n, "Wooooooo");
				playerTalk(p, n, "Never mind");
				break;
			case Ghost.GOODBYE:
				npcTalk(p, n, "Wooo wooo");
				break;
			case Ghost.NOTSURE:
				npcTalk(p, n, "Wooo woo");
				playerTalk(p, n, "Well if you insist");
				npcTalk(p, n, "Wooooooooo");
				playerTalk(p, n, "Ah well, better be off now");
				npcTalk(p, n, "Woo");
				playerTalk(p, n, "Bye");
				break;
			case Ghost.WHY:
				npcTalk(p, n,
						"No, I just know I can't do anything much like this");
				playerTalk(
						p,
						n,
						"I've been told a certain task may need to be completed",
						"So you can rest in peace");
				npcTalk(p, n, "I should think it is probably because ",
						"a warlock has come along and stolen my skull",
						"If you look inside my coffin there",
						"you'll find my corpse without a head on it");
				playerTalk(p, n, "Do you know where this warlock might be now?");
				npcTalk(p,
						n,
						"I think it was one of the warlocks who lives in the big tower",
						"In the sea southwest from here");
				playerTalk(p, n,
						"Ok I will try and get the skull back for you, so you can rest in peace.");
				npcTalk(p, n,
						"Ooh thank you. That would be such a great relief",
						"It is so dull being a ghost");
				p.updateQuestStage(Constants.Quests.THE_RESTLESS_GHOST, 3);
				break;
			case Ghost.SCARY:
				break;
			}
		}
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 15) {
			ghostDialogue(p, n, -1);
		}
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if(command.equals("open") && obj.getID() == 39) {
			player.message("You open the coffin");
			replaceObject(obj, new GameObject(obj.getLocation(), 40, obj.getDirection(), obj.getType()));
			return;
		}
		if(command.equals("close") && obj.getID() == 40) {
			player.message("You close the coffin");
			replaceObject(obj, new GameObject(obj.getLocation(), 39, obj.getDirection(), obj.getType()));
			return;
		}
		if (command.equals("search") && obj.getID() == 40) {
			if (player.getQuestStage(this) > 0) {
				player.message("Theres a skeleton without a skull in here");
			}
			else if(player.getQuestStage(this) == -1) {
				player.message("Theres a nice and complete skeleton in here");
			} else {
				player.message("You search the coffin and find some human remains");
			}
			return;
		}
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		if (obj.getID() == 40 && player.getQuestStage(this) == 3
				&& item.getID() == 27) {
			spawnNpc(15, 102, 675, 30);
			message(player, "You put the skull in the coffin");
			removeItem(player, 27, 1);			
			Npc npc = getNearestNpc(player, 15, 8);
			if (npc != null) {
				npc.remove();
			}
			message(player, "The ghost has vanished",
					"You think you hear a faint voice in the air", "Thank you");
			player.sendQuestComplete(Constants.Quests.THE_RESTLESS_GHOST);
			return;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 15) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		if (item.getID() == 27 && obj.getID() == 40) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 40 && (command.equals("search") || command.equals("close"))) {
			return true;
		}
		if (obj.getID() == 39 && command.equals("open")) {
			return true;
		}
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("You have completed the restless ghost quest");
		player.incQuestExp(5, player.getSkills().getMaxStat(5) * 250 + 2000);
		player.incQuestPoints(1);
		player.message("@gre@You haved gained 1 quest point!");

	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if (i.getX() == 218 && i.getY() == 3521 && i.getID() == 27) {
			return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		Npc skeleton = getNearestNpc(p, 50, 3);
		if (i.getX() == 218 && i.getY() == 3521 && i.getID() == 27) {			
			if(p.getQuestStage(Quests.THE_RESTLESS_GHOST) != 3 || p.getQuestStage(Quests.THE_RESTLESS_GHOST) == -1 ) {
				playerTalk(p, null, "That skull is scary", "I've got no reason to take it", "I think I'll leave it alone");
				return ;
			}
			if (skeleton != null) {
				spawnNpc(50, 217, 3520, 100);
				p.message("The skeleton is guarding the skull, you need to get rid of him first");
				skeleton.setChasing(p);
				return;
			}
			Npc skeleton1 = getNearestNpc(p, 50, 20);
			skeleton1.remove();
			World.getWorld().unregisterItem(i);
			addItem(p, 27, 1);
		}
	}
}
