package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

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
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == LADDER || obj.getID() == TOMB_OF_IBAN || obj.getID() == DWARF_BARREL || obj.getID() == PILE_OF_MUD;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == LADDER) {
			mes("you climb the ladder");
			delay(3);
			player.message("it leads to some stairs, you walk up...");
			player.teleport(782, 3549);
		}
		else if (obj.getID() == TOMB_OF_IBAN) {
			mes("you try to open the door of the tomb");
			delay(3);
			player.message("but the door refuses to open");
			mes("you hear a noise from below");
			delay(3);
			player.message("@red@leave me be");
			GameObject claws_of_iban = new GameObject(player.getWorld(), Point.location(player.getX(), player.getY()), 879, 0, 0);
			addloc(claws_of_iban);
			player.damage(((int) getCurrentLevel(player, Skill.HITS.id()) / 5) + 5);
			say(player, null, "aaarrgghhh");
			delay(2);
			delloc(claws_of_iban);
		}
		else if (obj.getID() == DWARF_BARREL) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.BUCKET.id(), Optional.of(false))) {
				player.message("you need a bucket first");
			} else {
				player.message("you poor some of the strong brew into your bucket");
				player.getCarriedItems().remove(new Item(ItemId.BUCKET.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_BREW.id()));
			}
		}
		else if (obj.getID() == PILE_OF_MUD) {
			mes("you climb the pile of mud");
			delay(3);
			player.message("it leads to an old stair way");
			player.teleport(773, 3417);
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == TOMB_OF_IBAN && (item.getCatalogId() == ItemId.DWARF_BREW.id() || item.getCatalogId() == ItemId.TINDERBOX.id());
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == TOMB_OF_IBAN && item.getCatalogId() == ItemId.DWARF_BREW.id()) {
			if (player.getCache().hasKey("doll_of_iban") && player.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				player.message("you pour the strong alcohol over the tomb");
				if (!player.getCache().hasKey("brew_on_tomb") && !player.getCache().hasKey("ash_on_doll")) {
					player.getCache().store("brew_on_tomb", true);
				}
				player.getCarriedItems().remove(new Item(ItemId.DWARF_BREW.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.BUCKET.id()));
			} else {
				mes("you consider pouring the brew over the grave");
				delay(3);
				player.message("but it seems such a waste");
			}
		}
		else if (obj.getID() == TOMB_OF_IBAN && item.getCatalogId() == ItemId.TINDERBOX.id()) {
			mes("you try to set alight to the tomb");
			delay(3);
			if (player.getCache().hasKey("brew_on_tomb") && !player.getCache().hasKey("ash_on_doll")) {
				mes("it bursts into flames");
				delay(3);
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), 97, obj.getDirection(), obj
					.getType()));
				addloc(obj.getWorld(), obj.getLoc(), 10000);
				mes("you search through the remains");
				delay(3);
				if (!player.getCarriedItems().hasCatalogID(ItemId.IBANS_ASHES.id(), Optional.of(false))) {
					player.message("and find the ashes of ibans corpse");
					addobject(ItemId.IBANS_ASHES.id(), 1, 726, 654, player);
				} else {
					player.message("but find nothing");
				}
				player.getCache().remove("brew_on_tomb");
			} else {
				player.message("but it will not light");
			}
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == SPIDER_NEST_RAILING;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == SPIDER_NEST_RAILING) {
			mes("you search the bars");
			delay(3);
			if (player.getCache().hasKey("doll_of_iban") || player.getQuestStage(Quests.UNDERGROUND_PASS) >= 7 || player.getQuestStage(Quests.UNDERGROUND_PASS) == -1) {
				mes("there's a gap big enough to squeeze through");
				delay(3);
				player.message("would you like to try");
				int menu = multi(player,
					"nope",
					"yes, lets do it");
				if (menu == 1) {
					player.message("you squeeze through the old railings");
					if (obj.getDirection() == 0) {
						if (obj.getY() == player.getY())
							player.teleport(obj.getX(), obj.getY() - 1);
						else
							player.teleport(obj.getX(), obj.getY());
					}
				}
			} else {
				player.message("but you can't quite squeeze through");
			}
		}
	}
}
