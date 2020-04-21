package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassDungeonFloor implements OpLocTrigger, OpBoundTrigger, UseLocTrigger {

	/**
	 * OBJECT IDs
	 **/
	public static int SPIDER_NEST_RAILING = 171;
	public static int LADDER = 920;
	public static int TOMB_OF_IBAN = 878;
	public static int DWARF_BARREL = 880;
	public static int PILE_OF_MUD = 890;

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return obj.getID() == LADDER || obj.getID() == TOMB_OF_IBAN || obj.getID() == DWARF_BARREL || obj.getID() == PILE_OF_MUD;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == LADDER) {
			mes(p, "you climb the ladder");
			p.message("it leads to some stairs, you walk up...");
			p.teleport(782, 3549);
		}
		else if (obj.getID() == TOMB_OF_IBAN) {
			mes(p, "you try to open the door of the tomb");
			p.message("but the door refuses to open");
			mes(p, "you hear a noise from below");
			p.message("@red@leave me be");
			GameObject claws_of_iban = new GameObject(p.getWorld(), Point.location(p.getX(), p.getY()), 879, 0, 0);
			addloc(claws_of_iban);
			p.damage(((int) getCurrentLevel(p, Skills.HITS) / 5) + 5);
			say(p, null, "aaarrgghhh");
			delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
			delloc(claws_of_iban);
		}
		else if (obj.getID() == DWARF_BARREL) {
			if (!p.getCarriedItems().hasCatalogID(ItemId.BUCKET.id(), Optional.of(false))) {
				p.message("you need a bucket first");
			} else {
				p.message("you poor some of the strong brew into your bucket");
				p.getCarriedItems().getInventory().replace(ItemId.BUCKET.id(), ItemId.DWARF_BREW.id());
			}
		}
		else if (obj.getID() == PILE_OF_MUD) {
			mes(p, "you climb the pile of mud");
			p.message("it leads to an old stair way");
			p.teleport(773, 3417);
		}
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return obj.getID() == TOMB_OF_IBAN && (item.getCatalogId() == ItemId.DWARF_BREW.id() || item.getCatalogId() == ItemId.TINDERBOX.id());
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == TOMB_OF_IBAN && item.getCatalogId() == ItemId.DWARF_BREW.id()) {
			if (p.getCache().hasKey("doll_of_iban") && p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				p.message("you pour the strong alcohol over the tomb");
				if (!p.getCache().hasKey("brew_on_tomb") && !p.getCache().hasKey("ash_on_doll")) {
					p.getCache().store("brew_on_tomb", true);
				}
				p.getCarriedItems().getInventory().replace(ItemId.DWARF_BREW.id(), ItemId.BUCKET.id());
			} else {
				mes(p, "you consider pouring the brew over the grave");
				p.message("but it seems such a waste");
			}
		}
		else if (obj.getID() == TOMB_OF_IBAN && item.getCatalogId() == ItemId.TINDERBOX.id()) {
			mes(p, "you try to set alight to the tomb");
			if (p.getCache().hasKey("brew_on_tomb") && !p.getCache().hasKey("ash_on_doll")) {
				mes(p, "it bursts into flames");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), 97, obj.getDirection(), obj
					.getType()));
				addloc(obj.getWorld(), obj.getLoc(), 10000);
				mes(p, "you search through the remains");
				if (!p.getCarriedItems().hasCatalogID(ItemId.IBANS_ASHES.id(), Optional.of(false))) {
					p.message("and find the ashes of ibans corpse");
					addobject(ItemId.IBANS_ASHES.id(), 1, 726, 654, p);
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
	public boolean blockOpBound(GameObject obj, Integer click, Player player) {
		return obj.getID() == SPIDER_NEST_RAILING;
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player p) {
		if (obj.getID() == SPIDER_NEST_RAILING) {
			mes(p, "you search the bars");
			if (p.getCache().hasKey("doll_of_iban") || p.getQuestStage(Quests.UNDERGROUND_PASS) >= 7 || p.getQuestStage(Quests.UNDERGROUND_PASS) == -1) {
				mes(p, "there's a gap big enough to squeeze through");
				p.message("would you like to try");
				int menu = multi(p,
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
