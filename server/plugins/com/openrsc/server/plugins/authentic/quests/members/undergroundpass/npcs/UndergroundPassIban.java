package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassIban implements UseLocTrigger {

	private static int PIT_OF_THE_DAMNED = 913;

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == PIT_OF_THE_DAMNED && item.getCatalogId() == ItemId.A_DOLL_OF_IBAN.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == PIT_OF_THE_DAMNED) {
			//iban has been killed (+ doesn't matter status of doll)
			if(atQuestStages(player, Quests.UNDERGROUND_PASS, 8, -1)) {
				player.message("iban is already dead");
				return;
			}
			boolean defeated = false;
			if (player.getCache().hasKey("poison_on_doll")
				&& player.getCache().hasKey("cons_on_doll")
				&& player.getCache().hasKey("ash_on_doll")
				&& player.getCache().hasKey("shadow_on_doll")) {
				Npc iban = ifnearvisnpc(player, NpcId.IBAN.id(), 10);
				if (iban == null) {
					player.message("iban is still not here");
					return;
				}
				else {
					mes("you throw the doll of iban into the pit");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.A_DOLL_OF_IBAN.id()));
					defeated = true;
					player.setAttribute("iban_bubble_show", true);
					npcsay(player, iban, "what's happening?, it's dark here...so dark",
						"im falling into the dark, what have you done?");
					mes("iban falls to his knees clutching his throat");
					delay(3);
					npcsay(player, iban, "noooooooo!");
					player.message("iban slumps motionless to the floor");
					iban.remove();
				}
				if (defeated) {
					mes("a roar comes from the pit of the damned");
					delay(3);
					mes("the infamous iban has finally gone to rest");
					delay(3);
					player.message("amongst ibans remains you find his staff..");
					mes("...and some runes");
					delay(3);
					player.message("suddenly around you rocks crash to the floor..");
					mes("...as the ground begins to shake");
					delay(3);
					mes("the temple walls begin to collapse in");
					delay(3);
					mes("and you're thrown from the temple platform");
					delay(3);
					give(player, ItemId.STAFF_OF_IBAN.id(), 1);
					give(player, ItemId.DEATH_RUNE.id(), 15);
					give(player, ItemId.FIRE_RUNE.id(), 30);
					player.teleport(687, 3485);

					/*player may teleport out after defeating iban
					 * without talking to koftik (very likely and logic)*/
					player.updateQuestStage(Quests.UNDERGROUND_PASS, 8);
					//REMOVE CACHES
					player.getCache().remove("orb_of_light1");
					player.getCache().remove("orb_of_light2");
					player.getCache().remove("orb_of_light3");
					player.getCache().remove("orb_of_light4");
					if (player.getCache().hasKey("stalagmite")) {
						player.getCache().remove("stalagmite");
					}
					if (player.getCache().hasKey("crate_food")) {
						player.getCache().remove("crate_food");
					}
					if (player.getCache().hasKey("paladin_food")) {
						player.getCache().remove("paladin_food");
					}
					if (player.getCache().hasKey("brew_on_tomb")) {
						player.getCache().remove("brew_on_tomb");
					}
					player.getCache().remove("rope_wall_grill");
					player.getCache().remove("flames_of_zamorak1");
					player.getCache().remove("flames_of_zamorak2");
					player.getCache().remove("flames_of_zamorak3");
					player.getCache().remove("doll_of_iban");
					player.getCache().remove("kardia_cat");
					player.getCache().remove("poison_on_doll");
					player.getCache().remove("cons_on_doll");
					player.getCache().remove("ash_on_doll");
					player.getCache().remove("shadow_on_doll");
					//reset flag on last map koftik npc
					player.getCache().store("advised_koftik", false);
					/* end the show! */
				}
			} else {
				player.message("the doll is still incomplete");
			}
		}
	}
}
