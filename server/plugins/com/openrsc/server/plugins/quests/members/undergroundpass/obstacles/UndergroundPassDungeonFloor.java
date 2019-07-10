package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassDungeonFloor implements ObjectActionListener, ObjectActionExecutiveListener, WallObjectActionListener, WallObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	/**
	 * OBJECT IDs
	 **/
	public static int SPIDER_NEST_RAILING = 171;
	public static int LADDER = 920;
	public static int TOMB_OF_IBAN = 878;
	public static int DWARF_BARREL = 880;
	public static int PILE_OF_MUD = 890;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == LADDER || obj.getID() == TOMB_OF_IBAN || obj.getID() == DWARF_BARREL || obj.getID() == PILE_OF_MUD;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == LADDER) {
			message(p, "you climb the ladder");
			p.message("it leads to some stairs, you walk up...");
			p.teleport(782, 3549);
		}
		else if (obj.getID() == TOMB_OF_IBAN) {
			message(p, "you try to open the door of the tomb");
			p.message("but the door refuses to open");
			message(p, "you hear a noise from below");
			p.message("@red@leave me be");
			GameObject claws_of_iban = new GameObject(Point.location(p.getX(), p.getY()), 879, 0, 0);
			registerObject(claws_of_iban);
			p.damage(((int) getCurrentLevel(p, SKILLS.HITS.id()) / 5) + 5);
			playerTalk(p, null, "aaarrgghhh");
			sleep(1000);
			removeObject(claws_of_iban);
		}
		else if (obj.getID() == DWARF_BARREL) {
			if (!hasItem(p, ItemId.BUCKET.id())) {
				p.message("you need a bucket first");
			} else {
				p.message("you poor some of the strong brew into your bucket");
				p.getInventory().replace(ItemId.BUCKET.id(), ItemId.DWARF_BREW.id());
			}
		}
		else if (obj.getID() == PILE_OF_MUD) {
			message(p, "you climb the pile of mud");
			p.message("it leads to an old stair way");
			p.teleport(773, 3417);
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		return obj.getID() == TOMB_OF_IBAN && (item.getID() == ItemId.DWARF_BREW.id() || item.getID() == ItemId.TINDERBOX.id());
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == TOMB_OF_IBAN && item.getID() == ItemId.DWARF_BREW.id()) {
			if (p.getCache().hasKey("doll_of_iban") && p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 6) {
				p.message("you pour the strong alcohol over the tomb");
				if (!p.getCache().hasKey("brew_on_tomb") && !p.getCache().hasKey("ash_on_doll")) {
					p.getCache().store("brew_on_tomb", true);
				}
				p.getInventory().replace(ItemId.DWARF_BREW.id(), ItemId.BUCKET.id());
			} else {
				message(p, "you consider pouring the brew over the grave");
				p.message("but it seems such a waste");
			}
		}
		else if (obj.getID() == TOMB_OF_IBAN && item.getID() == ItemId.TINDERBOX.id()) {
			message(p, "you try to set alight to the tomb");
			if (p.getCache().hasKey("brew_on_tomb") && !p.getCache().hasKey("ash_on_doll")) {
				message(p, "it bursts into flames");
				replaceObject(obj, new GameObject(obj.getLocation(), 97, obj.getDirection(), obj
					.getType()));
				delayedSpawnObject(obj.getLoc(), 10000);
				message(p, "you search through the remains");
				if (!hasItem(p, ItemId.IBANS_ASHES.id())) {
					p.message("and find the ashes of ibans corpse");
					createGroundItem(ItemId.IBANS_ASHES.id(), 1, 726, 654, p);
				} else {
					p.message("but find nothing");
				}
				p.getCache().remove("brew_on_tomb");
			} else {
				p.message("but it will not light");
			}
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		return obj.getID() == SPIDER_NEST_RAILING;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == SPIDER_NEST_RAILING) {
			message(p, "you search the bars");
			if (p.getCache().hasKey("doll_of_iban") || p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) >= 7 || p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == -1) {
				message(p, "there's a gap big enough to squeeze through");
				p.message("would you like to try");
				int menu = showMenu(p,
					"nope",
					"yes, lets do it");
				if (menu == 1) {
					p.message("you squeeze through the old railings");
					if (obj.getDirection() == 0) {
						if (obj.getY() == p.getY())
							p.teleport(obj.getX(), obj.getY() - 1);
						else
							p.teleport(obj.getX(), obj.getY());
					}
				}
			} else {
				p.message("but you can't quite squeeze through");
			}
		}
	}
}
