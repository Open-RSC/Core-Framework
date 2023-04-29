package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.AttackNpcTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.SpellNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class CombatInstructor implements TalkNpcTrigger, KillNpcTrigger, AttackNpcTrigger, SpellNpcTrigger {
	/**
	 * Tutorial island combat instructor
	 * Level-7 rat not the regular rat!!!!!!!
	 * YOUTUBE: NO XP GIVEN IN ANY COMBAT STAT BY KILLING THE RAT
	 */

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (!player.getCarriedItems().hasCatalogID(ItemId.WOODEN_SHIELD.id(), Optional.of(false))
			&& (!player.getCarriedItems().hasCatalogID(ItemId.BRONZE_LONG_SWORD.id(), Optional.of(false))) && player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 15) {
			npcsay(player, n, "Aha a new recruit",
				"I'm here to teach you the basics of fighting",
				"First of all you need weapons");
			give(player, ItemId.WOODEN_SHIELD.id(), 1); // Add wooden shield to the players inventory
			give(player, ItemId.BRONZE_LONG_SWORD.id(), 1); // Add bronze long sword to the players inventory
			mes("The instructor gives you a sword and shield");
			delay(3);
			npcsay(player, n, "look after these well",
				"These items will now have appeared in your inventory",
				"You can access them by selecting the bag icon in the menu bar",
				"which can be found in the top right hand corner of the screen",
				"To wield your weapon and shield left click on them within your inventory");
			if (config().WANT_EQUIPMENT_TAB) {
				npcsay(player, n, "they will then be added to your equipment tab",
					"which you can view by clicking the Equipment button at the bottom of the inventory");
			} else {
				npcsay(player, n, "their box will go red to show you are wearing them");
			}
			player.message("When you have done this speak to the combat instructor again");
			player.getCache().set("tutorial", 16);
		} else if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 16) {
			if ((!player.getCarriedItems().hasCatalogID(ItemId.WOODEN_SHIELD.id()) || player.getCarriedItems().getEquipment().hasEquipped(ItemId.WOODEN_SHIELD.id())) &&
				(!player.getCarriedItems().hasCatalogID(ItemId.BRONZE_LONG_SWORD.id()) || player.getCarriedItems().getEquipment().hasEquipped(ItemId.BRONZE_LONG_SWORD.id()))) {
				npcsay(player, n, "Today we're going to be killing giant rats");
				Npc rat = ifnearvisnpc(player, NpcId.RAT_TUTORIAL.id(), 10);
				if (rat == null) {
					// if no rat around releases one, see: https://youtu.be/33YvXfbHfnc?t=425
					npcsay(player, n, "I'll just let out some rats for you");
					player.message("The combat instructor releases a rat");
					rat = addnpc(player.getWorld(), NpcId.RAT_TUTORIAL.id(), 231, 735);
				} else {
					npcsay(player, rat, "squeek");
					n = ifnearvisnpc(player, n.getID(), 10);
					npcsay(player, n, "move your mouse over a rat you will see it is level 7",
						"You will see that it's level is written in green",
						"If it is green this means you have a strong chance of killing it",
						"creatures with their name in red should probably be avoided",
						"As this indicates they are tougher than you",
						"left click on the rat to attack it");
				}
			} else {
				npcsay(player, n, "You need to wield your equipment",
						"You can access it by selecting the bag icon",
						"which can be found in the top right hand corner of the screen",
						"To wield your weapon and shield left click on them");
				if (config().WANT_EQUIPMENT_TAB) {
					npcsay(player, n, "they will then be added to your equipment tab",
						"which you can view by clicking the Equipment button at the bottom of the inventory");
				} else {
					npcsay(player, n, "their boxs will go red to show you are wearing them");
				}
				player.message("When you have done this speak to the combat instructor again");
			}
		} else if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") >= 20) {
			npcsay(player, n, "Well done you're a born fighter",
				"As you kill things",
				"Your combat experience will go up",
				"this expereince will slowly cause you to get tougher",
				"eventually you will be able to take on stronger enemies",
				"Such as those found in dungeons",
				"Now contine to the building to the northeast");
			if(player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") < 25)
				player.getCache().set("tutorial", 25);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.COMBAT_INSTRUCTOR.id();
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.RAT_TUTORIAL.id();
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		if (!(
			(!player.getCache().hasKey("tutorial") || !player.getLocation().aroundTutorialRatZone()) ||
				(affectedmob.getID() == NpcId.CHICKEN.id()) ||
				(affectedmob.getID() == NpcId.RAT_TUTORIAL.id() && player.getCache().getInt("tutorial") == 16)
		)) {
			if (player.getCache().getInt("tutorial") < 16) {
				mes("Speak to the combat instructor before killing rats");
				delay(3);
			}
			else {
				mes("That's enough rat killing for now");
				delay(3);
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		if (
			(!player.getCache().hasKey("tutorial") || !player.getLocation().aroundTutorialRatZone()) ||
				(n.getID() == NpcId.CHICKEN.id()) ||
				(n.getID() == NpcId.RAT_TUTORIAL.id() && player.getCache().getInt("tutorial") == 16)
		) {
			return false;
		}

		return true;
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		onAttackNpc(player, n);
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return blockAttackNpc(player, n);
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.RAT_TUTORIAL.id()) {
			// GIVE NO XP ACCORDING TO YOUTUBE VIDEOS FOR COMBAT SINCE IT WAS HEAVILY ABUSED IN REAL RSC TO TRAIN ON THOSE RATS.
			if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 16) {
				mes("Well done you've killed the rat");
				delay(3);
				mes("Now speak to the combat instructor again");
				delay(3);
				player.getCache().set("tutorial", 20);
			}
		}
	}
}
