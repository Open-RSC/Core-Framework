package com.openrsc.server.plugins.quests.free;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TeleportExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;


public class PiratesTreasure implements QuestInterface,InvActionListener,
InvActionExecutiveListener, TalkToNpcListener, ObjectActionListener,
ObjectActionExecutiveListener, TalkToNpcExecutiveListener,
InvUseOnObjectListener, InvUseOnObjectExecutiveListener, TeleportExecutiveListener {

	class Frank {
		public static final int TREASURE = 0;
	}

	class Luthas {
		public static final int ANNOYING = 0;

	}

	@Override
	public int getQuestId() {
		return Constants.Quests.PIRATES_TREASURE;
	}

	@Override
	public String getQuestName() {
		return "Pirate's treasure";
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player p) {
		// 2 Quest Points
		// 450 coins
		// Gold ring
		// Emerald
		addItem(p, 283, 1);
		addItem(p, 163, 1);
		addItem(p, 10, 450);
		p.incQuestPoints(2);
		p.message("Well done you have completed the pirate treasure quest");
		p.message("@gre@You haved gained 2 quest points!");
		p.updateQuestStage(this, -1);
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		if (item.getID() == 249 && obj.getID() == 182 || item.getID() == 318
				&& obj.getID() == 182 || obj.getID() == 187
				&& item.getID() == 382) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (item.getID() == 249 && obj.getID() == 182 && obj.getY() == 711) {
			if (p.getCache().hasKey("bananas")) {
				if (p.getCache().getInt("bananas") >= 10) {
					p.message(
							"The crate is full of bananas");
					return;
				}
				if (p.getInventory().remove(item) > -1) {
					p.message(
							"You put a banana in the crate");

					p.getCache().set("bananas",
							p.getCache().getInt("bananas") + 1);
				}
			} else {
				p.message("I have no reason to do that.");
			}
		} else if (item.getID() == 318 && obj.getID() == 182
				&& p.getQuestStage(this) > 0) {
			if (p.getCache().hasKey("bananas")) {
				if (p.getInventory().remove(item) > -1) {
					p.message(
							"You stash the rum in the crate");
					if(!p.getCache().hasKey("rum_in_crate")) {
						p.getCache().store("rum_in_crate", true);
					}
				}
			}
		} else if (obj.getID() == 187 && item.getID() == 382) {
			p.message("You unlock the chest");
			World.getWorld().replaceGameObject(obj, 
					new GameObject(obj.getLocation(), 186, obj.getDirection(),
							obj.getType()));
			World.getWorld().delayedSpawnObject(obj.getLoc(), 3000);
			removeItem(p, 382, 1);
			message(p, "All that is in the chest is a message");
			message(p, "You take the message from the chest");
			message(p, "It says dig just behind the south bench in the park");
			p.updateQuestStage(this, 2);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 128) {
			return true;
		}
		if (n.getID() == 164) {
			return true;
		}
		return false;
	}

	public void frankDialogue(Player p, Npc n, int cID) {
		switch (p.getQuestStage(this)) {
		case 0:
			npcTalk(p, n, "Arrrh Matey");
			int choice = showMenu(p, n, new String[] {
					"I'm in search of treasure", "Arrrh",
					"Do you want to trade?" });
			if (choice == 0) {
				npcTalk(p, n, "Arrrh treasure you be after eh?",
						"Well I might be able to tell you where to find some.",
						"For a price");
				playerTalk(p, n, "What sort of price?");

				npcTalk(p, n,
						"Well for example if you can get me a bottle of rum",
						"Not jus any rum mind",
						"I'd like some rum brewed on Karamja island",
						"There's no rum like Karamja rum");
				p.updateQuestStage(this, 1);
			} else if (choice == 1) {
				npcTalk(p, n, "Arrrh");
			} else if (choice == 2) {
				npcTalk(p, n, "No I've got nothing to trade");
			} 
			break;
		case 1:
			npcTalk(p, n, "Arrrh Matey",
					"Have Ye brought some rum for yer old mate Frank");
			if (!p.getInventory().hasItemId(318)) {
				playerTalk(p, n, "No not yet");
				return;
			}
			playerTalk(p, n, "Yes I've got some");
			p.getInventory().remove(318, 1);

			message(p, "Frank happily takes the rum");
			npcTalk(p,
					n,
					"Now a deals a deal, I'll tell ye about the treasure",
					"I used to serve under a pirate captain called One Eyed Hector",
					"Hector was a very successful pirate and became very rich",
					"but about a year ago we were boarded by the Royal Asgarnian Navy",
					"Hector was killed along with many of the crew",
					"I was one of the few to escape", "And I escaped with this");
			message(p, "Frank hands you a key");
			addItem(p, 382, 1);
			npcTalk(p, n, "This is Hector's key",
					"I believe it opens his chest",
					"In his old room in the blue moon inn in Varrock",
					"With any luck his treasure will be in there");
			int menu = showMenu(p, n, new String[] {
					"Ok thanks, I'll go and get it",
			"So why didn't you ever get it?" });
			if (menu == 1) {
				npcTalk(p, n, "I'm not allowed in the blue moon inn",
						"Apparently I'm a drunken trouble maker");
			}
			break;
		case -1:
		case 2:
			npcTalk(p, n, "Arrrh Matey");
			int menu1 = showMenu(p, n, new String[] { "Arrrh",
					"Do you want to trade?"});
			if (menu1 == 0) {
				npcTalk(p, n, "Arrrh");
			} else if (menu1 == 1) {
				npcTalk(p, n, "No I've got nothing to trade");
			}
			break;
		}
	}

	public void luthasDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			if (!p.getCache().hasKey("bananas")) {
				npcTalk(p, n,
						"Hello I'm Luthas, I run the banana plantation here");
				int choice = showMenu(p, n, new String[] {
						"Could you offer me employment on your plantation?",
				"That customs officer is annoying isn't she?" });
				if (choice == 0) {
					npcTalk(p,
							n,
							"Yes, I can sort something out",
							"Yes there's a crate outside ready for loading up on the ship",
							"If you could fill it up with bananas",
							"I'll pay you 30 gp");
					p.getCache().set("bananas", 0);
				} else if (choice == 1) {
					luthasDialogue(p, n, Luthas.ANNOYING);
				}
			} else {
				if (p.getCache().getInt("bananas") >= 10) {
					playerTalk(p, n, "I've filled a crate with bananas");
					npcTalk(p, n, "Well done here is your payment");
					p.message("Luthas hands you 30 coins");
					p.getInventory().add(new Item(10, 30));
					if(p.getCache().hasKey("bananas")) {
						p.getCache().remove("bananas");
					}
					if (p.getCache().hasKey("rum_in_crate")) {
						p.getCache().remove("rum_in_crate"); // this erhm wait
					}
					if (!p.getCache().hasKey("rum_delivered")) {
						p.getCache().store("rum_delivered", true);
					}
					int choice = showMenu(
							p,
							n,
							new String[] {
									"Will you pay me for another crate full?",
									"Thankyou, I'll be on my way",
									"So where are these bananas going to be delivered to?",
							"That custom officer is annoying isn't she?" });
					if (choice == 0) {
						p.getCache().set("bananas", 0);// I was intending to
						// remove it if u
						// dont wanna fill
						// it again. xD
						npcTalk(p,
								n,
								"Yes certainly",
								"If you go outside you should see the old crate has been loaded on to the ship",
								"and there is another empty crate in it's place");
					} else if (choice == 2) {
						npcTalk(p, n,
								"I sell them to Wydin who runs a grocery store in Port Sarim");
					} else if (choice == 3) {
						luthasDialogue(p, n, Luthas.ANNOYING);
					}
					return;
				}
				npcTalk(p, n, "Have you completed your task yet?");
				int choice = showMenu(p, n, new String[] {
						"What did I have to do again?",
				"No, the crate isn't full yet" });
				if (choice == 0) {
					npcTalk(p,
							n,
							"There's a crate outside ready for loading up on the ship",
							"If you could fill it up with bananas",
							"I'll pay you 30 gp");
				} else if (choice == 1) {
					npcTalk(p, n, "Well come back when it is");
				}
			}

		}
		switch (cID) {
		case Luthas.ANNOYING:
			npcTalk(p, n, "Well I know her pretty well",
					"She doesn't cause me any trouble any more",
					"She doesn't search my export crates any more",
					"She knows they only contain bananas");
			break;
		}
	}

	// need to do wydin. ye do we really need todo him again, since he only
	// opens door? cuz hes already
	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 164) {
			luthasDialogue(p, n, -1);
		}
		if (n.getID() == 128) {
			frankDialogue(p, n, -1);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		return obj.getID() == 182 || obj.getID() == 185;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		switch (obj.getID()) {
		case 182:
			String s = "";
			if (p.getCache().hasKey("bananas")) {
				int b = p.getCache().getInt("bananas");
				if (b == 0)
					s = "The crate is completely empty";
				else if (b < 10)
					s = "the crate is partially full of bananas";
				else
					s = "the crate is full of bananas";

			} else {
				s = "The crate is completely empty";
			}
			p.message(s);
			break;
		case 185:
			if (p.getCache().hasKey("rum_delivered")) {
				if (p.getCache().getBoolean("rum_delivered")) {
					message(p, "There are a lot of bananas in this crate",
							"You find your bottle of rum amoungst the bananas");
					p.getInventory().add(new Item(318));
					//
					p.getCache().remove("rum_delivered");
				}
			} else {
				int wantabanana = showMenu(p, "Yes", "No");
				if (wantabanana == 0) {
					p.getInventory().add(new Item(249));

				}
			}
			break;
		}

	}
	
	@Override
	public boolean blockInvAction(Item item, Player p) {
		return (p.getY() == 548 && p.getX() > 287 && p.getX() < 291)
				&& item.getID() == 211;
	}
	
	@Override	
	public boolean blockTeleport(Player p) {
		if (p.getInventory().hasItemId(318) && (p.getLocation().inKaramja())) {		
			p.getInventory().remove(318);										
			}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if (p.getQuestStage(this) != 2)
			return;
		if (p.isBusy())
			return;
		if ((p.getY() == 548 && p.getX() >= 287 && p.getX() <= 291)
				&& item.getID() == 211) {
			if (p.getX() == 290 || p.getX() == 289) {
				Npc wysin = getNearestNpc(p, 116, 20);
				boolean dig = false;
				if (wysin != null) {
					wysin.getUpdateFlags().setChatMessage(new ChatMessage(wysin, "Hey leave off my flowers", p));

					sleep(1000);
					wysin.setChasing(p);
					long start = System.currentTimeMillis();
					while (!p.inCombat()) {
						if (System.currentTimeMillis() - start > 2000) {
							dig = true;
							break;
						}
						sleep(50);
					}
				} else {
					dig = true;
				}
				if (dig) {
					message(p, "You dig a hole in the ground",
							"You find a little bag of treasure");
					p.sendQuestComplete(this.getQuestId());
				}
			}
		}
	}
}
