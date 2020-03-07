package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassIban implements UseLocTrigger {

	private static int PIT_OF_THE_DAMNED = 913;

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return obj.getID() == PIT_OF_THE_DAMNED && item.getCatalogId() == ItemId.A_DOLL_OF_IBAN.id();
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == PIT_OF_THE_DAMNED) {
			//iban has been killed (+ doesn't matter status of doll)
			if(atQuestStages(p, Quests.UNDERGROUND_PASS, 8, -1)) {
				p.message("iban is already dead");
				return;
			}
			boolean defeated = false;
			if (p.getCache().hasKey("poison_on_doll")
				&& p.getCache().hasKey("cons_on_doll")
				&& p.getCache().hasKey("ash_on_doll")
				&& p.getCache().hasKey("shadow_on_doll")) {
				Npc iban = ifnearvisnpc(p, NpcId.IBAN.id(), 10);
				if (iban == null) {
					p.message("iban is still not here");
					return;
				}
				else {
					Functions.mes(p, "you throw the doll of iban into the pit");
					remove(p, new Item(ItemId.A_DOLL_OF_IBAN.id(), 1));
					defeated = true;
					p.setAttribute("iban_bubble_show", true);
					npcsay(p, iban, "what's happening?, it's dark here...so dark",
						"im falling into the dark, what have you done?");
					Functions.mes(p, "iban falls to his knees clutching his throat");
					npcsay(p, iban, "noooooooo!");
					p.message("iban slumps motionless to the floor");
					iban.remove();
				}
				if (defeated) {
					Functions.mes(p, "a roar comes from the pit of the damned",
							"the infamous iban has finally gone to rest");
						p.message("amongst ibans remains you find his staff..");
						Functions.mes(p, "...and some runes");
						p.message("suddenly around you rocks crash to the floor..");
						Functions.mes(p, "...as the ground begins to shake",
							"the temple walls begin to collapse in",
							"and you're thrown from the temple platform");
						give(p, ItemId.STAFF_OF_IBAN.id(), 1);
						give(p, ItemId.DEATH_RUNE.id(), 15);
						give(p, ItemId.FIRE_RUNE.id(), 30);
						p.teleport(687, 3485);

						/*player may teleport out after defeating iban
						 * without talking to koftik (very likely and logic)*/
						p.updateQuestStage(Quests.UNDERGROUND_PASS, 8);
						//REMOVE CACHES
						p.getCache().remove("orb_of_light1");
						p.getCache().remove("orb_of_light2");
						p.getCache().remove("orb_of_light3");
						p.getCache().remove("orb_of_light4");
						if (p.getCache().hasKey("stalagmite")) {
							p.getCache().remove("stalagmite");
						}
						if (p.getCache().hasKey("crate_food")) {
							p.getCache().remove("crate_food");
						}
						if (p.getCache().hasKey("paladin_food")) {
							p.getCache().remove("paladin_food");
						}
						if (p.getCache().hasKey("brew_on_tomb")) {
							p.getCache().remove("brew_on_tomb");
						}
						p.getCache().remove("rope_wall_grill");
						p.getCache().remove("flames_of_zamorak1");
						p.getCache().remove("flames_of_zamorak2");
						p.getCache().remove("flames_of_zamorak3");
						p.getCache().remove("doll_of_iban");
						p.getCache().remove("kardia_cat");
						p.getCache().remove("poison_on_doll");
						p.getCache().remove("cons_on_doll");
						p.getCache().remove("ash_on_doll");
						p.getCache().remove("shadow_on_doll");
						//reset flag on last map koftik npc
						p.getCache().store("advised_koftik", false);
						/* end the show! */
				}
			} else {
				p.message("the doll is still incomplete");
			}
		}
	}
}
