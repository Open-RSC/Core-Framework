package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class FightArena implements QuestInterface,TalkToNpcListener,
TalkToNpcExecutiveListener, ObjectActionListener,
ObjectActionExecutiveListener, InvUseOnObjectListener,
InvUseOnObjectExecutiveListener, PlayerKilledNpcListener,
PlayerKilledNpcExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.FIGHT_ARENA;
	}

	@Override
	public String getQuestName() {
		return "Fight Arena (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		message(p, "you have completed the fight arena quest",
				"Lady Servil gives you 1000 gold coins",
				"you gain two quest points");
		addItem(p, 10, 1000);
		p.message("@gre@You haved gained 2 quest points!");
		int[] questData = Quests.questData.get(Quests.FIGHT_ARENA);
		//keep order kosher
		int[] skillIDs = {ATTACK, THIEVING};
		for(int i=0; i<skillIDs.length; i++) {
			questData[Quests.MAPIDX_SKILL] = skillIDs[i];
			incQuestReward(p, questData, i==(skillIDs.length-1));
		}
		p.getCache().remove("freed_servil");
		p.getCache().remove("killed_ogre");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 372) {
			return true;
		}
		if (n.getID() == 381) {
			return true;
		}
		if (n.getID() == 374) {
			return true;
		}
		if (n.getID() == 373) {
			return true;
		}
		if (n.getID() == 376) {
			return true;
		}
		if (n.getID() == 377) {
			return true;
		}
		if (n.getID() == 387) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == 384 || n.getID() == 388 || n.getID() == 383) { // general
			// khazard
			// 383,
			// justin
			// servil
			// 378,
			// 386
			// scorp,
			// 388
			// bouncer
			return true;
		}
		if (n.getID() == 386) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		n.killedBy(p);
		n.remove();
		if (n.getID() == 384) {
			if(!p.getCache().hasKey("killed_ogre")) {
				p.getCache().store("killed_ogre", true);
			}
			p.message("You kill the ogre");
			message(p, "Jeremy's father survives");
			Npc justin = getNearestNpc(p, 378, 15);
			if(justin != null) {
				npcTalk(p, justin, "You saved my life and my son's",
						"I am eternally in your debt brave traveller");
			}
			spawnNpc(383, 613, 708, 60000);
			sleep(1000);
			Npc general =  getNearestNpc(p, 383, 8);
			if (general != null) {
				npcTalk(p,
						general,
						"Haha, well done, well done that was rather entertaining",
						"I'm the great General Khazard",
						"And the two men you just saved are my property");
				playerTalk(p, general, "They belong to no one");
				npcTalk(p, general, "I suppose we could find some arrangement",
						"for their freedom... hmmmm");
				playerTalk(p, general, "What do you mean?");
				npcTalk(p,
						general,
						"I'll let them go but you must stay and fight for me",
						"You'll make me double the gold if you manage to last a few fights",
						"Guards! take him away!");
				p.message("Khazard's men have locked you in a cell");
				p.teleport(609, 715, false);
			}
		}
		if (n.getID() == 386) {
			p.message("You defeat the scorpion");
			spawnNpc(383, 613, 708, 30000);
			sleep(1000);
			Npc generalAgain = getNearestNpc(p, 383, 15);
			if(generalAgain != null) {
				npcTalk(p, generalAgain, "Not bad, not bad at all",
						"I think you need a tougher challenge",
						"Time for my puppy", "Guards, guards bring on bouncer");
			}
			message(p, "From above you hear a voice...",
					"Ladies and gentlemen!", "Todays second round");
			spawnNpc(388, 613, 708, 240000);
			p.message("between the Outsider and bouncer");
			Npc bouncer = World.getWorld().getNpcById(388);
			if(bouncer != null) {
				bouncer.setChasing(p);
			}
		}
		if (n.getID() == 388) {
			p.message("You defeat bouncer");
			spawnNpc(383, 613, 708, 60000 * 2);
			sleep(1000);
			Npc generalAgainAgain = getNearestNpc(p, 383, 15);
			if(generalAgainAgain != null) {
				npcTalk(p, generalAgainAgain, "nooooo! bouncer, how dare you?",
						"you've taken the life of my only friend!");
				p.message("Khazard looks very angry");
				npcTalk(p, generalAgainAgain,
						"now you'll suffer traveller, prepare to meet your maker");
				message(p, "No, he doesn't look happy at all",
						"You might want to run for it",
						"Go back to lady servil to claim your reward");
				generalAgainAgain.setChasing(p);
			}
			p.updateQuestStage(getQuestId(), 3);
		}
		if (n.getID() == 383) {
			p.message("You kill general khazard");
			p.message("but he shall return");
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 387) {
			if (p.getQuestStage(getQuestId()) == 2
					&& p.getCache().hasKey("killed_ogre")) {
				playerTalk(p, n, "Are you ok stranger?");
				npcTalk(p, n, "I'm fine thanks, my name's Hengrad",
						"So khazard got his hands on you too?");
				playerTalk(p, n, "I'm afraid so");
				npcTalk(p, n, "If you're lucky you may last as long as me");
				playerTalk(p, n, "How long have you been here?");
				npcTalk(p,
						n,
						"I've been in khazard's prisons ever since i can remember",
						"I was a child when his men kidnapped me",
						"My whole life has been spent killing and fighting",
						"All in the hope that one day I'll escape");
				playerTalk(p, n, "Don't give up");
				npcTalk(p, n, "Thanks friend..wait..sshh,the guard is coming",
						"He'll be taking one of us to the arena");
				message(p, "A guard approaches the cell");
				npcTalk(p, n, "Looks like it's you,good luck friend");
				message(p, "The guard leads you to the arena",
						"For your battle");
				p.teleport(609, 705, false);
				message(p, "From above you hear a voice...",
						"Ladies and gentlemen!",
						"Todays first fight between the outsider",
						"And everyone's favorite scorpion has begun");
				spawnNpc(386, 613, 708, 120000);
				Npc scorp = World.getWorld().getNpcById(386);
				if(scorp != null) {
					scorp.setChasing(p);
				}
			}
		}
		if (n.getID() == 377) {
			if ((p.getQuestStage(getQuestId()) >= 3)
					|| p.getQuestStage(getQuestId()) == -1) {
				p.message("You need to kill the creatures in the arena");
				return;
			}
			if (p.getQuestStage(getQuestId()) == 2
					&& p.getCache().hasKey("freed_servil")) {
				playerTalk(p, n, "Jeremy where's your father?");
				npcTalk(p, n, "Quick, help him! that beast will kill him",
						"He can't fight! he's too old!");
				message(p, "You see Jeremy's father Justin",
						"Trying to escape an ogre");
				npcTalk(p, n, "Please help him!");
				spawnNpc(384, 613, 708, 60000 * 2);
			}
		}
		if (n.getID() == 376) {
			if (p.getQuestStage(getQuestId()) == 3
					|| p.getQuestStage(getQuestId()) == -1) {
				playerTalk(p, n, "hello");
				npcTalk(p, n, "you're the outsider who killed bouncer",
						"die traitor!");
				n.setChasing(p);
				return;
			}
			playerTalk(p, n, "hello");
			if (p.getInventory().wielding(733)
					&& p.getInventory().wielding(734)) {
				npcTalk(p, n, "can i help you stranger?",
						"oh.. you're a guard as well", "that's ok then",
						"we don't like outsiders around here");
			} else {
				npcTalk(p, n, "i don't know you stranger", "get of our land");
				n.setChasing(p);
			}
		}
		if (n.getID() == 373) {
			if (p.getQuestStage(getQuestId()) >= 2) {
				if (p.getInventory().wielding(733)
						&& p.getInventory().wielding(734)) {
					playerTalk(p, n, "hello");
					npcTalk(p, n, "hello, hope you're keeping busy?");
					playerTalk(p, n, "of course");
				} else {
					npcTalk(p, n, "this area is restricted, leave now",
							"OUT and don't come back!");
					message(p, "the guard has thrown you out");
					p.teleport(602, 717, false);
				}
				return;
			}
			playerTalk(p, n, "long live General Khazard");
			npcTalk(p, n, "erm.. yes.. soldier", "i take it you're new");
			playerTalk(p, n, "you could say that");
			npcTalk(p, n, "Khazard died two hundred years ago",
					"however his dark spirit remains",
					"in the form of the undead maniac...General Khazard",
					"remember he is your master, always watching",
					"you got that, newbie?");
			playerTalk(p, n, "undead, maniac, master, got it - loud and clear");
		}
		if (n.getID() == 374) {
			if (p.getQuestStage(getQuestId()) == 3
					|| p.getQuestStage(getQuestId()) == -1) {
				if (p.getInventory().wielding(733)
						&& p.getInventory().wielding(734)) {
					playerTalk(p, n, "hello");
					npcTalk(p, n, "less chat and more work",
							"i can't stand lazy guards");

				} else {
					npcTalk(p, n, "this area is restricted, leave now",
							"OUT and don't come back!");
					message(p, "the guard has thrown you out");
					p.teleport(621, 698, false);
				}
				return;
			}
			if (p.getCache().hasKey("guard_sleeping")
					|| p.getCache().hasKey("freed_servil")) {
				npcTalk(p, n, "please, let me rest");
				return;
			}
			if (p.getQuestStage(getQuestId()) == 2) {
				playerTalk(p, n, "hello again");
				npcTalk(p,
						n,
						"bored, bored, bored",
						"you would think the slaves would be more entertaining",
						"selfish.. the lot of 'em");
				if (hasItem(p, 735)) {
					playerTalk(p, n, "do you still fancy a drink?");
					npcTalk(p, n,
							"I really shouldn't... ok then, just the one",
							"this stuff looks good");
					removeItem(p, 735, 1);
					message(p, "the guard takes a mouthful of drink");
					npcTalk(p, n, "blimey this stuff is pretty good",
							"it's not too strong is it?");
					playerTalk(p, n, "no, not at all, you'll be fine");
					message(p, "the guard finishes the bottle");
					npcTalk(p, n, "that is some gooood stuff",
							"yeah... woooh... yeah");
					message(p, "the guard seems quite typsy");
					playerTalk(p, n, "are you alright?");
					npcTalk(p, n, "yeesshh, ooohh, 'hiccup'",
							"maybe i should relax for a while....");
					playerTalk(p, n, "good idea, i'll look after the prisoners");
					npcTalk(p, n, "ok then, here, 'hiccup',",
							"take these keys",
							"any trouble you give 'em a good beating");
					playerTalk(p, n, "no problem, i'll keep them in line");
					npcTalk(p, n, "zzzzz zzzzz zzzzz");
					message(p, "the guard is asleep");
					p.getCache().store("guard_sleeping", true);
					addItem(p, 736, 1);
				}
				return;
			}
			playerTalk(p, n, "long live General Khazard");
			npcTalk(p, n, "erm.. yes.. quite right",
					"have you come to laugh at the fight slaves?",
					"i used to really enjoy it",
					"but after a while they become quite boring",
					"now i just want a decent drink",
					"mind you, too much khali brew and i'll fall asleep");
		}
		if (n.getID() == 381) {
			if (p.getQuestStage(getQuestId()) == -1) {
				playerTalk(p, n, "hello");
				npcTalk(p, n, "please, i haven't done anything");
				playerTalk(p, n, "what?");
				npcTalk(p, n, "i love General Khazard, please believe me");
				return;
			}
			if (p.getQuestStage(getQuestId()) == 3) {
				playerTalk(p, n, "hello");
				npcTalk(p, n, "hello stranger",
						"Khazard's got some great fights lined up this week",
						"i can't wait");
				return;
			}
			playerTalk(p, n, "hello");
			npcTalk(p, n, "are you enjoying the arena?",
					"i heard the servil family are fighting soon",
					"should be very entertaining");
		}
		if (n.getID() == 372) {
			switch (p.getQuestStage(this)) {
			case 0:
				playerTalk(p, n, "hi there, looks like you're in some trouble");
				npcTalk(p, n, "oh, i wish this broken cart was my only problem");
				npcTalk(p, n, "sob.. i've got to find my family.. sob");
				int first = showMenu(p, n, "I hope you can, good luck",
						"can i help you?");
				if (first == 0) {
					npcTalk(p, n, " sob..sob");
				} else if (first == 1) {
					npcTalk(p, n, "sob.. would you? please?",
							"i'm Lady Servil, my husband's Sir Servil",
							"we were travelling north with my son",
							"when we were ambushed by general Khazard's men");
					playerTalk(p, n, "general Khazard? i haven't heard of him");
					npcTalk(p, n, "he's been after me ever since i",
							"declined his hand in marriage",
							"now he's kidnapped my husband and son",
							"to fight slaves in his",
							"battle arena, to the south of here",
							"i hate to think what he'll do to them",
							"he's a sick, twisted man");
					playerTalk(p, n, "I'll try my best to return your family");
					npcTalk(p, n, "please do, i'm a wealthy woman",
							"and can reward you handsomely",
							"i'll be waiting for you here");
					p.updateQuestStage(getQuestId(), 1);
				}
				break;
			case 1:
			case 2:
				playerTalk(p, n, "hello Lady Servil");
				npcTalk(p, n, "Brave traveller, please..bring back my family");
				break;
			case 3:
				playerTalk(p, n, "Lady Servil");
				npcTalk(p, n, "you're alive, i thought Khazard's men took you",
						"My son and husband are safe and recovering at home",
						"without you they would certainly be dead",
						"I am truly grateful for your service",
						"all i can offer in return is material wealth",
						"please take these coins and enjoy");
				p.sendQuestComplete(Constants.Quests.FIGHT_ARENA);
				break;
			case -1:
				playerTalk(p, n, "Hello lady Servil");
				npcTalk(p, n, "oh hello my dear",
						"my husband and son are resting",
						"while i wait for the cart fixer");
				playerTalk(p, n, "hope he's not too long");
				npcTalk(p, n, "thanks again for everything");
				break;
			}
		}

	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 382 && obj.getY() == 1623) {
			return true;
		}
		if (obj.getID() == 371 && (obj.getY() == 700 || obj.getY() == 707)) {
			return true;
		}
		if (obj.getID() == 371 && obj.getY() == 716) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 382 && obj.getY() == 1623) {
			if (!hasItem(p, 734) && !hasItem(p, 733)
					&& p.getQuestStage(getQuestId()) >= 1) {
				p.message("You search the cupboard...");
				p.message("You find a khazard helmet");
				p.message("You find a khazard chainmail");
				addItem(p, 734, 1);
				addItem(p, 733, 1);
			}
			else {
				p.message("You search the cupboard, but find nothing");
			}
		}
		if (obj.getID() == 371 && (obj.getY() == 700 || obj.getY() == 707)) {
			Npc joe = getNearestNpc(p, 379, 5);

			if (joe != null) {
				playerTalk(p, joe, "are you ok?");
				npcTalk(p, joe, "spare me your fake pity",
						"I spit on Khazard's grave and all who do his bidding");
			}
			Npc kelvin = getNearestNpc(p, 380, 5);
			if (kelvin != null) {
				playerTalk(p, kelvin, "hello there");
				npcTalk(p, kelvin, "get away, get away",
						"one day i'll have my revenge",
						"and i'll have all your heads!");
			}
		}
		if (obj.getID() == 371 && obj.getY() == 716) {
			if (p.getCache().hasKey("freed_servil")
					|| p.getQuestStage(getQuestId()) == 3
					|| p.getQuestStage(getQuestId()) == -1) {
				p.message("You have already freed jeremy");
				return;
			}
			Npc servil = getNearestNpc(p, 377, 5);
			Npc guard = getNearestNpc(p, 373, 5);
			if (servil != null && guard != null) {
				if (p.getCache().hasKey("guard_sleeping") && hasItem(p, 736)) {
					playerTalk(p, servil, "Jeremy, look, I have the cell keys");
					npcTalk(p, servil, "Wow! Please help me");
					playerTalk(p, servil, "ok, keep quiet");
					npcTalk(p, servil, "Set me free then we can find dad");
					message(p, "You use your key to open the cell door",
							"The gate swings open");
					p.playSound("opendoor");
					World.getWorld().replaceGameObject(obj,
							new GameObject(obj.getLocation(), 181, obj
									.getDirection(), obj.getType()));
					World.getWorld().delayedSpawnObject(obj.getLoc(), 3000);
					servil.teleport(605, 718);
					playerTalk(p, servil,
							"There you go, now we need to find your father");
					npcTalk(p, servil, "I overheard a guard talking",
							"I think they've taken him to the arena");
					playerTalk(p, servil, "OK we'd better hurry");
					npcTalk(p, servil, " I'll run ahead");
					servil.remove();
					p.getCache().store("freed_servil", true);
					p.getCache().remove("guard_sleeping");
					npcTalk(p, guard, "What are you doing?",
							"It's an imposter!");
					sleep(1000);
					guard.setChasing(p);
					return;
				}
				npcTalk(p, servil, "I'm Jeremy Servil",
						"Please sir, don't hurt me");
				playerTalk(p, servil, "I'm here to help",
						"Where do they keep the keys?");
				npcTalk(p, servil, "The guard keeps them.. always");
				p.updateQuestStage(getQuestId(), 2);
			}
		}
	}
	
	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		if (obj.getID() == 371 && obj.getY() == 716 && item.getID() == 736) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 371 && obj.getY() == 716 && item.getID() == 736) {
			p.message("To unlock the gate, left click on it");
		}
	}

}
