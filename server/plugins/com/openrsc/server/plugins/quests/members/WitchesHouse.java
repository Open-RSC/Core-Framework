package com.openrsc.server.plugins.quests.members;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.doDoor;
import static com.openrsc.server.plugins.Functions.doGate;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.removeNpc;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

public class WitchesHouse implements QuestInterface, TalkToNpcListener,
		TalkToNpcExecutiveListener, WallObjectActionListener,
		WallObjectActionExecutiveListener, ObjectActionListener,
		ObjectActionExecutiveListener, DropListener, DropExecutiveListener,
		InvUseOnNpcListener, InvUseOnNpcExecutiveListener,
		PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener,
		PickupListener, PickupExecutiveListener,
		PlayerAttackNpcExecutiveListener {

	/**
	 * INFORMATION Rat appears on coords: 356, 494 Dropping cheese in the whole
	 * room and rat appears on the same coord Rat is never removed untill you
	 * use magnet room inbounds : MIN X: 356 MAX X: 357 MIN Y: 494 MAX Y: 496
	 */
	
	@Override
	public int getQuestId() {
		return Constants.Quests.WITCHS_HOUSE;
	}

	@Override
	public String getQuestName() {
		return "Witch's house (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done you have completed the Witches house quest");
		p.incQuestPoints(4);
		p.message("@gre@You haved gained 4 quest points!");
		p.incQuestExp(3, p.getSkills().getMaxStat(3) * 600 + 1300);
		p.getCache().remove("witch_gone");
		p.getCache().remove("shapeshifter");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 240) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 240) {
			switch (p.getQuestStage(this)) {
			case 0:
				playerTalk(p, n, "Hello young man");
				message(p, "The boy sobs");
				int first = showMenu(p, n, "What's the matter?",
						"Well if you're not going to answer, I'll go");
				if (first == 0) {
					npcTalk(p, n,
							"I've kicked my ball over that wall, into that garden",
							"The old lady who lives there is scary",
							"She's locked the ball in her wooden shed",
							"Can you get my ball back for me please");
					int second = showMenu(p, n, "Ok, I'll see what I can do",
							"Get it back yourself");
					if (second == 0) {
						npcTalk(p, n, "Thankyou");
						p.updateQuestStage(getQuestId(), 1);
					} else if (second == 1) {
						// NOTHING
					}
				} else if (first == 1) {
					message(p, "The boy sniffs slightly");
				}
				break;
			case 1:
			case 2:
			case 3:
				if (hasItem(p, 539)) {
					playerTalk(p, n, "Hi I have got your ball back",
							"It was harder than I thought it would be");
					npcTalk(p, n, "Thankyou very much");
					removeItem(p, 539, 1);
					if(p.getQuestStage(Constants.Quests.WITCHS_HOUSE) == 3) {
						p.sendQuestComplete(Constants.Quests.WITCHS_HOUSE);
					}
				} else {
					npcTalk(p, n, "Have you got my ball back yet?");
					playerTalk(p, n, "Not yet");
					npcTalk(p, n, "Well it's in the shed in that garden");
				}
				break;
			case -1:
				npcTalk(p, n, "Thankyou for getting my ball back");
				break;
			}
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
			Player player) {
		if (obj.getID() == 69) {
			return true;
		}
		if (obj.getID() == 70 && obj.getX() == 358) {
			return true;
		}
		if (obj.getID() == 71 && obj.getY() == 495) {
			return true;
		}
		if (obj.getID() == 73 && obj.getX() == 351) {
			return true;
		}
		if (obj.getID() == 72 && obj.getX() == 356) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 69) {
			p.message("The door is locked");
		}
		if (obj.getID() == 70 && obj.getX() == 358) {
			doDoor(obj, p);
		}
		if (obj.getID() == 71 && obj.getY() == 495) {
			if (p.getCache().hasKey("witch_spawned") && p.getQuestStage(getQuestId()) == 2) {
				Npc witch = World.getWorld().getNpcById(242);
				if(witch != null) {
					witch.teleport(355, 494);
					
					npcTalk(p, witch, "Oi what are you doing in my garden?");
					npcTalk(p, witch, "Get out you pesky intruder");
					message(p, "Nora begins to cast a spell");
					
					p.teleport(347, 616, false);
					removeNpc(witch);
					
					p.getCache().remove("witch_spawned");
					p.updateQuestStage(this, 1);
				}
				return;
			}
			if (p.getQuestStage(this) == 2 || p.getQuestStage(getQuestId()) == -1 || p.getX() == 355) {
				doDoor(obj, p);
			} else {
				p.message("The door won't open");
			}
		}
		
		if (obj.getID() == 73 && obj.getX() == 351) {
			Npc witch = World.getWorld().getNpcById(242);
			if (p.getQuestStage(this) == 3 || p.getQuestStage(getQuestId()) == -1) {
				doDoor(obj, p);
				return;
			}
			if (!p.getCache().hasKey("witch_spawned")) {
				message(p, "As you reach out to open the door you hear footsteps inside the house", "The footsteps approach the back door");
				spawnNpc(242, 356, 494, 60000);
				p.getCache().store("witch_spawned", true);
			} else {
				message(p, "The shed door is locked");
				if (witch == null) {
					return;
				}
				witch.teleport(355, 494);
				npcTalk(p, witch, "Oi what are you doing in my garden?");
				npcTalk(p, witch, "Get out you pesky intruder");
				message(p, "Nora begins to cast a spell");

				p.teleport(347, 616, false);
				removeNpc(witch);
				p.updateQuestStage(this, 1);
			}
		}
		if (obj.getID() == 72 && obj.getX() == 356) {
			if (p.getX() <= 355) {
				doDoor(obj, p);
				if (p.getCache().hasKey("witch_spawned")) {
					Npc witch = World.getWorld().getNpcById(242);
					witch.setBusy(true);
					sleep(2000);
					p.message("Through a crack in the door, you see a witch enter the garden");
					witch.teleport(353, 492);
					sleep(2500);
					witch.teleport(351, 491);
					p.message("The witch disappears into the shed");
					npcTalk(p, witch, "How are you tonight my pretty?",
							"Would you like some food?",
							"Just wait there while i get some");
					witch.teleport(353, 492);
					witch.setLocation(Point.location(353, 492), true);
					message(p,
							"The witch passes back through the garden again",
							"Leaving the shed door unlocked");
					sleep(2500);
					
					removeNpc(witch);
					p.getCache().remove("witch_spawned");
					
					p.updateQuestStage(this, 3);
				}
			} else {
				p.teleport(355, 492, false);
			}
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 255) {
			return true;
		}
		if (obj.getID() == 256 && obj.getX() == 363) {
			return true;
		}
		if (obj.getID() == 259 && obj.getY() == 3328) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 255) {
			if ((p.getQuestStage(getQuestId()) >= 1 || p
					.getQuestStage(getQuestId()) == -1) && !hasItem(p, 538)) {
				p.message("You find a key under the mat");
				addItem(p, 538, 1);
			} else {
				p.message("You find nothing interesting");
			}
		}
		if (obj.getID() == 256 && obj.getX() == 363) {
			if (!p.getInventory().wielding(16)) {
				p.message("As your bare hands touch the gate you feel a shock");
				int damage;
				if (p.getSkills().getLevel(Skills.HITPOINTS) < 20) {
					damage = p.getRandom().nextInt(9) + 1;
				} else {
					damage = p.getRandom().nextInt(14) + 1;
				}
				p.damage(damage);
			} else {
				doGate(p, obj);
			}
		}
		if (obj.getID() == 259 && obj.getY() == 3328) {
			if (!hasItem(p, 540)) {
				p.message("You find a magnet in the cupboard");
				addItem(p, 540, 1);
			} else {
				p.message("You search the cupboard, but find nothing");
			}
		}

	}

	@Override
	public boolean blockDrop(Player p, Item i) {
		if (i.getID() == 319 && p.getLocation().inBounds(356, 357, 494, 496)) {
			return true;
		}
		return false;
	}

	// room inbounds : MIN X: 356 MAX X: 357 MIN Y: 494 MAX Y: 496
	@Override
	public void onDrop(Player p, Item i) {
		if (i.getID() == 319 && p.getLocation().inBounds(356, 357, 494, 496)) {
			if(p.getQuestStage(this) == -1) {
				playerTalk(p, null, "I would rather eat it to be honest");
				return;
			}
			message(p, "A rat appears from a hole and eats the cheese");
			spawnNpc(241, 356, 494, 60000);
		}
	}

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		if (item.getID() == 540 && npc.getID() == 241) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc npc, Item item) {
		if (item.getID() == 540 && npc.getID() == 241) {
			if(p.getQuestStage(this) == -1) {
				return;
			}
			p.message("You put the magnet on the rat");
			Npc rat = World.getWorld().getNpcById(241);
			removeNpc(rat);
			message(p, "The rat runs back into his hole",
					"You hear a click and whirr");
			p.getInventory().remove(540, 1);
			p.updateQuestStage(getQuestId(), 2);
		}

	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == 244 || n.getID() == 245 || n.getID() == 246
				|| n.getID() == 247) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		n.resetCombatEvent();
		if (n.getID() >= 247) {
			n.killedBy(p);
			p.message("You finally kill the shapeshifter once and for all");
			if(!p.getCache().hasKey("shapeshifter")) {
				p.getCache().store("shapeshifter", true);
			}
			return;
		}
		n.killedBy(p);
		Npc nextShape = spawnNpc(n.getID() + 1, n.getX(), n.getY(), 300000);

		p.message("The shapeshifer turns into a "
				+ npcMessage(nextShape.getID()) + "!");
		nextShape.startCombat(p);
	}
	private String npcMessage(int id) {
		if(id == 245) {
			return "spider";
		} else if(id == 246) {
			return "bear";
		} else if(id == 247) {
			return "wolf";
		}
		return "";
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if (n.getID() == 244 && p.getQuestStage(getQuestId()) == -1) {
			p.message("I have already done that quest");
			return true;
		}
		return false;
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if (i.getID() == 539 && i.getX() == 350 && i.getY() == 491) {
			if (p.getQuestStage(getQuestId()) == -1) {
				return true;
			}
			if (!p.getCache().hasKey("shapeshifter")) {

				return true;
			}
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if (!p.getCache().hasKey("shapeshifter")) {
			Npc shapeshifter = getNearestNpc(p, 244, 20);
			if(shapeshifter != null) {
				shapeshifter.startCombat(p);
			}
		} else if (p.getQuestStage(getQuestId()) == -1) {
			playerTalk(p, null, "I'd better not take it, its not mine");
		}

	}
}
