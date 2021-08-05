package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.UseBoundTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassKardiaTheWitch implements OpLocTrigger, OpBoundTrigger, TakeObjTrigger, UseBoundTrigger {

	/**
	 * OBJECT IDs
	 **/
	private static int WITCH_RAILING = 172;
	private static int WITCH_DOOR = 173;
	private static int WITCH_CHEST = 885;

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == WITCH_RAILING || obj.getID() == WITCH_DOOR;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == WITCH_RAILING) {
			mes("inside you see Kardia the witch");
			delay(3);
			player.message("her appearence make's you feel quite ill");
		}
		else if (obj.getID() == WITCH_DOOR) {
			if (click == 0) {
				if (player.getCache().hasKey("kardia_cat")) {
					player.message("you open the door");
					doDoor(obj, player);
					mes("and walk through");
					delay(3);
					player.message("the witch is busy talking to the cat");
				} else {
					Npc witch = ifnearvisnpc(player, NpcId.KARDIA_THE_WITCH.id(), 5);
					player.message("you reach to open the door");
					if (witch != null) {
						npcsay(player, witch, "get away...far away from here");
						delay(2);
						player.message("the witch raises her hands above her");
						displayTeleportBubble(player, player.getX(), player.getY(), true);
						player.damage(((int) getCurrentLevel(player, Skill.HITS.id()) / 5) + 5); // 6 lowest, 25 max.
						npcsay(player, witch, "haa haa.. die mortal");
					} else {
						// TODO: find if something happens here authentically
						player.message("but nothing seems to happen");
					}
				}
			} else if (click == 1) {
				if (player.getCarriedItems().hasCatalogID(ItemId.KARDIA_CAT.id(), Optional.of(false)) && !player.getCache().hasKey("kardia_cat")) {
					mes("you place the cat by the door");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.KARDIA_CAT.id()));
					player.teleport(776, 3535);
					mes("you knock on the door and hide around the corner");
					delay(3);
					player.message("the witch takes the cat inside");
					if (!player.getCache().hasKey("kardia_cat")) {
						player.getCache().store("kardia_cat", true);
					}
				} else if (player.getCache().hasKey("kardia_cat")) {
					mes("there is no reply");
					delay(3);
					player.message("inside you can hear the witch talking to her cat");
				} else {
					mes("you knock on the door");
					delay(3);
					player.message("there is no reply");
				}
			}
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.KARDIA_CAT.id() && player.getCarriedItems().hasCatalogID(ItemId.KARDIA_CAT.id(), Optional.of(false));
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.KARDIA_CAT.id() && player.getCarriedItems().hasCatalogID(ItemId.KARDIA_CAT.id(), Optional.of(false))) {
			mes("it's not very nice to squeeze one cat into a satchel");
			delay(3);
			player.message("...two's just plain cruel!");
		}
	}

	@Override
	public boolean blockUseBound(Player player, GameObject obj, Item item) {
		return obj.getID() == WITCH_DOOR && item.getCatalogId() == ItemId.KARDIA_CAT.id();
	}

	@Override
	public void onUseBound(Player player, GameObject obj, Item item) {
		if (obj.getID() == WITCH_DOOR && item.getCatalogId() == ItemId.KARDIA_CAT.id()) {
			if (!player.getCache().hasKey("kardia_cat")) {
				mes("you place the cat by the door");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.KARDIA_CAT.id()));
				player.teleport(776, 3535);
				mes("you knock on the door and hide around the corner");
				delay(3);
				player.message("the witch takes the cat inside");
				if (!player.getCache().hasKey("kardia_cat")) {
					player.getCache().store("kardia_cat", true);
				}
			} else {
				mes("the witch is busy playing...");
				delay(3);
				player.message("with her other cat");
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == WITCH_CHEST;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == WITCH_CHEST) {
			mes("you search the chest");
			delay(3);
			if (player.getQuestStage(Quests.UNDERGROUND_PASS) == 6 && !player.getCache().hasKey("doll_of_iban")) {
				player.message("..inside you find a book a wooden doll..");
				player.message("...and two potions");
				give(player, ItemId.A_DOLL_OF_IBAN.id(), 1);
				give(player, ItemId.OLD_JOURNAL.id(), 1);
				give(player, ItemId.FULL_SUPER_ATTACK_POTION.id(), 1);
				give(player, ItemId.FULL_STAT_RESTORATION_POTION.id(), 1);
				if (!player.getCache().hasKey("doll_of_iban")) {
					player.getCache().store("doll_of_iban", true);
				}
			} else {
				player.message("but you find nothing of interest");
			}
		}
	}
}
