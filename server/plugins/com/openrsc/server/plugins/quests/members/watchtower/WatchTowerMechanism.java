package com.openrsc.server.plugins.quests.members.watchtower;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;
/**
 * 
 * @author Imposter/Fate
 *
 */
public class WatchTowerMechanism implements InvUseOnObjectListener, InvUseOnObjectExecutiveListener, InvUseOnItemListener, InvUseOnItemExecutiveListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener, PickupListener, PickupExecutiveListener {

	public static int TOBAN_CHEST = 978;
	public static int KEY = 1047;
	public static int STOLEN_GOLD = 1040;

	public static final int OGRE_RELIC_PART_1 = 1050;
	public static final int OGRE_RELIC_PART_2 = 1049;
	public static final int OGRE_RELIC_PART_3 = 1048;
	public static final int OGRE_RELIC_COMPLETE = 1044;
	public static final int POWERING_CRYSTAL1 = 1037;
	public static final int POWERING_CRYSTAL2 = 1152;
	public static final int POWERING_CRYSTAL3 = 1153;
	public static final int POWERING_CRYSTAL4 = 1154;
	public static final int VIAL_OF_WATER = 464;
	public static final int DEATH_RUNE = 38;
	public static final int SKAVID_MAP = 1045;
	public static final int NIGHTSHADE = 1086;
	public static final int SHAMAN_ROBE = 1087;
	public static final int OGRE_POTION = 1053;
	public static final int MAGIC_OGRE_POTION = 1054;


	public static int WATCHTOWER_WIZARD = 672;
	public static int OGRE_GUARD = 684;

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == TOBAN_CHEST && item.getID() == KEY) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == TOBAN_CHEST && item.getID() == KEY) {
			openChest(obj, 2000, 979);
			if(hasItem(p, STOLEN_GOLD)) {
				message(p, "You have already got the stolen gold");
			} else {
				p.message("You find a stash of gold inside");
				message(p, "You take the gold");
				addItem(p, STOLEN_GOLD, 1);
			}
			p.message("The chest springs shut");
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
		if((item1.getID() == OGRE_RELIC_PART_1 || item1.getID() == OGRE_RELIC_PART_2 || item1.getID() == OGRE_RELIC_PART_3) && (item2.getID() == OGRE_RELIC_PART_1 || item2.getID() == OGRE_RELIC_PART_2 || item2.getID() == OGRE_RELIC_PART_3)) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if((item1.getID() == OGRE_RELIC_PART_1 || item1.getID() == OGRE_RELIC_PART_2 || item1.getID() == OGRE_RELIC_PART_3) && (item2.getID() == OGRE_RELIC_PART_1 || item2.getID() == OGRE_RELIC_PART_2 || item2.getID() == OGRE_RELIC_PART_3)) {
			p.message("I think these fit together, but I can't seem to make it fit");
			p.message("I am going to need someone with more experience to help me with this");
		}
	}

	@Override
	public boolean blockInvUseOnNpc(Player p, Npc npc, Item item) {
		if(npc.getID() == WATCHTOWER_WIZARD) {
			return true;
		}
		if(npc.getID() == WatchTowerDialogues.CITY_GUARD && item.getID() == DEATH_RUNE) {
			return true;
		}
		if(npc.getID() == OGRE_GUARD && item.getID() == NIGHTSHADE) {
			return true;
		}
		return false;
	}
	private void lastCrystalChat(Player p, Npc n) {
		playerTalk(p, n, "This is the last one");
		npcTalk(p, n, "Magnificent!",
				"At last you've brought all the crystals",
				"Now the shield generator can be activated again",
				"And once again Yanille will be safe",
				"From the threat of the ogres",
				"Throw the lever to activate the system...");
		if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 9) {
			p.updateQuestStage(Constants.Quests.WATCHTOWER, 10);
		}
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc npc, Item item) {
		if(npc.getID() == WATCHTOWER_WIZARD) {
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
				p.message("The wizard has no need for more evidence");
				return;
			}
			switch(item.getID()) {
			case POWERING_CRYSTAL1:
				if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 10) {
					npcTalk(p, npc, "More crystals ?",
							"I don't need any more now...");
					return;
				}
				playerTalk(p,npc, "Wizard, look what I have found");
				npcTalk(p,npc, "Well done! well done!",
						"That's a crystal found!",
						"You are clever",
						"Hold onto it until you have all four...");
				if(hasItem(p, 1154)) {
					lastCrystalChat(p, npc);
				} else {
					npcTalk(p, npc, "Keep searching for the others",
							"If you've dropped any...",
							"Then you will need to go back to where you got it from");
				}
				break;
			case POWERING_CRYSTAL2:
				if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 10) {
					npcTalk(p, npc, "More crystals ?",
							"I don't need any more now...");
					return;
				}
				playerTalk(p, npc, "Wizard, I have another crystal");
				npcTalk(p, npc, "Superb!",
						"Keep up the good work",
						"Hold onto it until you have all four...");
				if(hasItem(p, 1154)) {
					lastCrystalChat(p, npc);
				} else {
					npcTalk(p, npc, "Keep searching for the others",
							"If you've dropped any...",
							"Then you will need to go back to where you got it from");
				}
				break;
			case POWERING_CRYSTAL3:
				if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 10) {
					npcTalk(p, npc, "More crystals ?",
							"I don't need any more now...");
					return;
				}
				playerTalk(p, npc, "Wizard, here is another crystal");
				npcTalk(p, npc, "I must say i'm impressed",
						"May Saradomin speed you in finding them all",
						"Hold onto it until you have all four...");
				if(hasItem(p, 1154)) {
					lastCrystalChat(p, npc);
				} else {
					npcTalk(p, npc, "Keep searching for the others",
							"If you've dropped any...",
							"Then you will need to go back to where you got it from");
				}
				break;
			case POWERING_CRYSTAL4:
				if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 10) {
					npcTalk(p, npc, "More crystals ?",
							"I don't need any more now...");
					return;
				}
				lastCrystalChat(p, npc);
				break;
			case OGRE_RELIC_PART_1:
				playerTalk(p,npc, "I had this given to me");
				if(!p.getCache().hasKey("wizard_relic_part_1")) {
					removeItem(p, OGRE_RELIC_PART_1, 1);
					npcTalk(p,npc, "It's part of an ogre relic");
					p.getCache().store("wizard_relic_part_1", true);
					relicParts(p, npc);
				} else {
					npcTalk(p,npc, "I already have that part...");
				}
				break;
			case OGRE_RELIC_PART_2:
				playerTalk(p,npc, "I got given this by an ogre");
				if(!p.getCache().hasKey("wizard_relic_part_2")) {
					removeItem(p, OGRE_RELIC_PART_2, 1);
					npcTalk(p,npc, "Good good,a part of an ogre relic");
					p.getCache().store("wizard_relic_part_2", true);
					relicParts(p, npc);
				} else {
					npcTalk(p,npc, "I already have that part...");
				}
				break;
			case OGRE_RELIC_PART_3:
				playerTalk(p,npc, "An ogre gave me this");
				if(!p.getCache().hasKey("wizard_relic_part_3")) {
					removeItem(p, OGRE_RELIC_PART_3, 1);
					npcTalk(p,npc, "Ah, it's part of an old ogre statue");
					p.getCache().store("wizard_relic_part_3", true);
					relicParts(p, npc);
				} else {
					npcTalk(p,npc, "I already have that part...");
				}
				break;
			case OGRE_RELIC_COMPLETE:
				playerTalk(p,npc, "What is this ?");
				npcTalk(p,npc, "It is the ogre statue I finished for you...");
				break;
			case VIAL_OF_WATER:
				npcTalk(p,npc, "Oh lovely, fresh water...thanks!");
				p.getInventory().replace(VIAL_OF_WATER, 465);
				break;
			case SKAVID_MAP:
				p.message("You give the map to the wizard");
				npcTalk(p,npc, "Well well! a map!",
						"Indeed this shows the paths into the skavid caves",
						"I suggest you search these now...");
				break;
			case OGRE_POTION:
				playerTalk(p, npc, "Yes I have made the potion");
				npcTalk(p, npc, "That's great news, let me infuse it with magic...");
				p.message("The wizard mutters strange words over the liquid");
				removeItem(p, 1053, 1);
				addItem(p, 1054, 1);
				npcTalk(p, npc, "Here it is, a dangerous substance",
						"I must remind you that this potion can only be used",
						"If your magic ability is high enough");
				if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 7) {
					p.updateQuestStage(Constants.Quests.WATCHTOWER, 8);
				}
				break;
			case MAGIC_OGRE_POTION:
				npcTalk(p, npc, "Yes that is the potion I enchanted for you",
						"Go and use it now...");
				break;
			default:
				p.message("Nothing interesting happens");
				break;
			}
		}
		if(npc.getID() == WatchTowerDialogues.CITY_GUARD && item.getID() == DEATH_RUNE) {
			if(p.getCache().hasKey("city_guard_riddle") || p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
				p.message("The guard is not listening to you");
			} else {
				removeItem(p, 38, 1);
				addItem(p, WatchTowerDialogues.SKAVID_MAP, 1);
				if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 3) {
					p.updateQuestStage(Constants.Quests.WATCHTOWER, 4);
				}
				p.getCache().store("city_guard_riddle", true);
				playerTalk(p, npc, "I worked it out!");
				npcTalk(p, npc, "Well well.. the imp has done it!",
						"Thanks for the rune",
						"This is what you be needing...");
				p.message("The guard gives you a map");
			}
		}
		if(npc.getID() == OGRE_GUARD && item.getID() == NIGHTSHADE) {
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) < 5) {
				p.message("The guard is occupied at the moment");
			} else {
				p.playerServerMessage(MessageType.QUEST, "You give the guard some nightshade");
				removeItem(p, NIGHTSHADE, 1);
				npcTalk(p, npc, "What is this!!!",
						"Arrrrgh! I cannot stand this plant!",
						"Ahhh, it burns! it burns!!!");
				p.message("You run past the guard while he's busy...");
				p.teleport(647, 3644);
			}
		}
	}
	private void relicParts(Player p, Npc n) {
		if(p.getCache().hasKey("wizard_relic_part_1") && p.getCache().hasKey("wizard_relic_part_2") && p.getCache().hasKey("wizard_relic_part_3")) {
			npcTalk(p,n, "Excellent! that seems to be all the pieces",
					"Now I can assemble it...",
					"Hmm, yes it is as I thought...",
					"A statue symbolising an ogre warrior of old",
					"Well, if you ever wanted to make friends with an ogre",
					"Then this is the item to have!");
			p.message("The wizard gives you a complete statue");
			addItem(p, OGRE_RELIC_COMPLETE, 1);
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 2) {
				p.updateQuestStage(Constants.Quests.WATCHTOWER, 3);
			}
		} else {
			npcTalk(p, n, "There may be more parts to find...",
					"I'll keep this for later");
		}

	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if(i.getID() == SHAMAN_ROBE) {
			return true;
		}
		if(i.getID() == 1037 || i.getID() == 1152 || i.getID() == 1154 || i.getID() == 1153) {
			return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if(i.getID() == SHAMAN_ROBE) {
			p.message("You take the robe");
			addItem(p, SHAMAN_ROBE, 1);
			i.remove();
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 5) {
				p.updateQuestStage(Constants.Quests.WATCHTOWER, 6);
			}
		}
		if(i.getID() == 1037 || i.getID() == 1152 || i.getID() == 1154 || i.getID() == 1153) {
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
				message(p, "You try and take the crystal but its stuck solid!",
						"You feel magic power coursing through the crystal...",
						"The force renews your magic level");
				int maxMagic = p.getSkills().getMaxStat(6);
				if (p.getSkills().getLevel(6) < maxMagic) {
					p.getSkills().setLevel(6, maxMagic);
				}
			} else {
				p.message("You take the crystal");
				addItem(p, i.getID(), 1);
				i.remove();
			}
		}
	}
}
