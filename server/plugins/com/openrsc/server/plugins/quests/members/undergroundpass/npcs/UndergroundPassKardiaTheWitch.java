package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnWallObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassKardiaTheWitch implements ObjectActionListener, ObjectActionExecutiveListener, WallObjectActionListener, WallObjectActionExecutiveListener, PickupExecutiveListener, PickupListener, InvUseOnWallObjectListener, InvUseOnWallObjectExecutiveListener  {

	/** OBJECT IDs **/
	public static int WITCH_RAILING = 172;
	public static int WITCH_DOOR = 173;
	public static int WITCH_CHEST = 885;

	/** ITEM IDs **/
	public static int CAT = 1003;

	/** NPC IDs **/
	public static int KARDIA_THE_WITCH = 643;

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		if(obj.getID() == WITCH_RAILING) {
			return true;
		}
		if(obj.getID() == WITCH_DOOR) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if(obj.getID() == WITCH_RAILING) {
			message(p, "inside you see Kardia the witch");
			p.message("her appearence make's you feel quite ill");
		}
		if(obj.getID() == WITCH_DOOR) {
			if(click == 0) {
				if(p.getCache().hasKey("kardia_cat")) {
					p.message("you open the door");
					doDoor(obj, p);
					message(p, "and walk through");
					p.message("the witch is busy talking to the cat");
				} else {
					Npc witch = getNearestNpc(p, KARDIA_THE_WITCH, 5);
					p.message("you reach to open the door");
					npcTalk(p,witch, "get away...far away from here");
					sleep(1000);
					p.message("the witch raises her hands above her");
					displayTeleportBubble(p, p.getX(), p.getY(), true);
					p.damage(((int) getCurrentLevel(p, HITS) / 5) + 5); // 6 lowest, 25 max. 
					npcTalk(p,witch, "haa haa.. die mortal");
				}
			} else if(click == 1) {
				if(hasItem(p, CAT) && !p.getCache().hasKey("kardia_cat")) {
					message(p, "you place the cat by the door");
					removeItem(p, CAT, 1);
					p.teleport(776, 3535);
					message(p,"you knock on the door and hide around the corner");
					p.message("the witch takes the cat inside");
					if(!p.getCache().hasKey("kardia_cat")) {
						p.getCache().store("kardia_cat", true);
					}
				} else if(p.getCache().hasKey("kardia_cat")) {
					message(p, "there is no reply");
					p.message("inside you can hear the witch talking to her cat");
				} else {
					message(p, "you knock on the door");
					p.message("there is no reply");
				}
			}
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if(i.getID() == CAT && hasItem(p, CAT)) {
			return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if(i.getID() == CAT && hasItem(p, CAT)) {
			message(p, "it's not very nice to squeeze one cat into a satchel");
			p.message("...two's just plain cruel!");
		}
	}

	@Override
	public boolean blockInvUseOnWallObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == WITCH_DOOR && item.getID() == CAT) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnWallObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == WITCH_DOOR && item.getID() == CAT) {
			if(!p.getCache().hasKey("kardia_cat")) {
				message(p, "you place the cat by the door");
				removeItem(p, CAT, 1);
				p.teleport(776, 3535);
				message(p,"you knock on the door and hide around the corner");
				p.message("the witch takes the cat inside");
				if(!p.getCache().hasKey("kardia_cat")) {
					p.getCache().store("kardia_cat", true);
				}
			} else {
				message(p, "the witch is busy playing...");
				p.message("with her other cat");
			}
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == WITCH_CHEST) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == WITCH_CHEST) {
			message(p, "you search the chest");
			if(p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 6 && !p.getCache().hasKey("doll_of_iban")) {
				p.message("..inside you find a book a wooden doll..");
				p.message("...and two potions");
				addItem(p, 1004, 1);
				addItem(p, 1005, 1);
				addItem(p, 486, 1);
				addItem(p, 466, 1);
				if(!p.getCache().hasKey("doll_of_iban")) {
					p.getCache().store("doll_of_iban", true);
				}
			} else {
				p.message("but you find nothing of interest");
			}
		}
	}
}
