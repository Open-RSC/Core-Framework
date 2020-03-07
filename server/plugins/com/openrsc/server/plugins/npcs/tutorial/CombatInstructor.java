package com.openrsc.server.plugins.npcs.tutorial;

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
	 * @author Davve
	 * Tutorial island combat instructor
	 * Level-7 rat not the regular rat!!!!!!!
	 * YOUTUBE: NO XP GIVEN IN ANY COMBAT STAT BY KILLING THE RAT
	 */

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (!p.getCarriedItems().hasCatalogID(ItemId.WOODEN_SHIELD.id(), Optional.of(false))
			&& (!p.getCarriedItems().hasCatalogID(ItemId.BRONZE_LONG_SWORD.id(), Optional.of(false))) && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 15) {
			npcTalk(p, n, "Aha a new recruit",
				"I'm here to teach you the basics of fighting",
				"First of all you need weapons");
			addItem(p, ItemId.WOODEN_SHIELD.id(), 1); // Add wooden shield to the players inventory
			addItem(p, ItemId.BRONZE_LONG_SWORD.id(), 1); // Add bronze long sword to the players inventory
			message(p, "The instructor gives you a sword and shield");
			npcTalk(p, n, "look after these well",
				"These items will now have appeared in your inventory",
				"You can access them by selecting the bag icon in the menu bar",
				"which can be found in the top right hand corner of the screen",
				"To wield your weapon and shield left click on them within your inventory",
				"their box will go red to show you are wearing them");
			p.message("When you have done this speak to the combat instructor again");
			p.getCache().set("tutorial", 16);
		} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 16) {
			if ((!p.getCarriedItems().hasCatalogID(ItemId.WOODEN_SHIELD.id()) || p.getCarriedItems().getEquipment().hasEquipped(ItemId.WOODEN_SHIELD.id())) &&
				(!p.getCarriedItems().hasCatalogID(ItemId.BRONZE_LONG_SWORD.id()) || p.getCarriedItems().getEquipment().hasEquipped(ItemId.BRONZE_LONG_SWORD.id()))) {
				npcTalk(p, n, "Today we're going to be killing giant rats");
				Npc rat = getNearestNpc(p, NpcId.RAT_TUTORIAL.id(), 10);
				if (rat != null) {
					npcTalk(p, rat, "squeek");
				}
				npcTalk(p, n, "move your mouse over a rat you will see it is level 7",
					"You will see that it's level is written in green",
					"If it is green this means you have a strong chance of killing it",
					"creatures with their name in red should probably be avoided",
					"As this indicates they are tougher than you",
					"left click on the rat to attack it");
			} else {
				npcTalk(p, n, "You need to wield your equipment",
						"You can access it by selecting the bag icon",
						"which can be found in the top right hand corner of the screen",
						"To wield your weapon and shield left click on them",
						"their boxs will go red to show you are wearing them");
				p.message("When you have done this speak to the combat instructor again");
			}
		} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") >= 20) {
			npcTalk(p, n, "Well done you're a born fighter",
				"As you kill things",
				"Your combat experience will go up",
				"this expereince will slowly cause you to get tougher",
				"eventually you will be able to take on stronger enemies",
				"Such as those found in dungeons",
				"Now contine to the building to the northeast");
			if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") < 25)
				p.getCache().set("tutorial", 25);
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.COMBAT_INSTRUCTOR.id();
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return n.getID() == NpcId.RAT_TUTORIAL.id();
	}

	@Override
	public void onAttackNpc(Player p, Npc affectedmob) {
		if (!(
			(!p.getCache().hasKey("tutorial") || !p.getLocation().aroundTutorialRatZone()) ||
				(affectedmob.getID() == NpcId.CHICKEN.id()) ||
				(affectedmob.getID() == NpcId.RAT_TUTORIAL.id() && p.getCache().getInt("tutorial") == 16)
		)) {
			if (p.getCache().getInt("tutorial") < 16)
				message(p, "Speak to the combat instructor before killing rats");
			else
				message(p, "That's enough rat killing for now");
		}
	}

	@Override
	public boolean blockAttackNpc(Player p, Npc n) {
		if (
			(!p.getCache().hasKey("tutorial") || !p.getLocation().aroundTutorialRatZone()) ||
				(n.getID() == NpcId.CHICKEN.id()) ||
				(n.getID() == NpcId.RAT_TUTORIAL.id() && p.getCache().getInt("tutorial") == 16)
		) {
			return false;
		}

		return true;
	}

	@Override
	public void onSpellNpc(Player p, Npc n) {
		onAttackNpc(p, n);
	}

	@Override
	public boolean blockSpellNpc(Player p, Npc n) {
		return blockAttackNpc(p, n);
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (n.getID() == NpcId.RAT_TUTORIAL.id()) {
			n.resetCombatEvent();
			n.remove();
			// GIVE NO XP ACCORDING TO YOUTUBE VIDEOS FOR COMBAT SINCE IT WAS HEAVILY ABUSED IN REAL RSC TO TRAIN ON THOSE RATS.
			if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 16) {
				message(p, "Well done you've killed the rat",
					"Now speak to the combat instructor again");
				p.getCache().set("tutorial", 20);
			}
		}
	}
}
