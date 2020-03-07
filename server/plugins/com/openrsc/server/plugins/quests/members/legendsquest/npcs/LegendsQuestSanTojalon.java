package com.openrsc.server.plugins.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.*;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestSanTojalon implements AttackNpcTrigger, KillNpcTrigger, SpellNpcTrigger, PlayerRangeNpcTrigger,
	EscapeNpcTrigger {

	@Override
	public boolean blockAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && !p.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !p.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onAttackNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !p.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !p.getCache().hasKey("cavernous_opening")) {
			attackMessage(p, n);
		}
	}

	private void attackMessage(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !p.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !p.getCache().hasKey("cavernous_opening")) {
			npcsay(p, n, "You have entered the Viyeldi caves and  your bravery must be tested.");
			n.setChasing(p);
			npcsay(p, n, "Prepare yourself...San Tojalon will test your mettle.");
		}
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return (n.getID() == NpcId.SAN_TOJALON.id() && !p.getCache().hasKey("cavernous_opening"))
				|| (n.getID() == NpcId.SAN_TOJALON.id() && p.getQuestStage(Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions"));
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && p.getQuestStage(Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions")) {
			n.remove();
			if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 1) {
				p.getCache().set("viyeldi_companions", 2);
			}
			mes(p, 1300, "A nerve tingling scream echoes around you as you slay the dead Hero.",
				"@yel@San Tojalon: Ahhhggggh",
				"@yel@San Tojalon: Forever must I live in this torment till this beast is slain...");
			delay(650);
			LegendsQuestNezikchened.demonFight(p);
		}
		if (n.getID() == NpcId.SAN_TOJALON.id() && !p.getCache().hasKey("cavernous_opening")) {
			if (p.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false))
				|| p.getCarriedItems().hasCatalogID(ItemId.A_RED_CRYSTAL.id(), Optional.of(false))
				|| p.getCarriedItems().hasCatalogID(ItemId.A_GLOWING_RED_CRYSTAL.id(), Optional.of(false))) {
				npcsay(p, n, "A fearsome foe you are, and bettered me once have you done already.");
				p.message("Your opponent is retreating");
				n.remove();
			} else {
				npcsay(p, n, "You have proved yourself of the honour..");
				p.resetCombatEvent();
				n.resetCombatEvent();
				p.message("Your opponent is retreating");
				npcsay(p, n, "");
				n.remove();
				mes(p, 1300, "A piece of crystal forms in midair and falls to the floor.",
					"You place the crystal in your inventory.");
				give(p, ItemId.A_CHUNK_OF_CRYSTAL.id(), 1);
			}
		}
	}

	@Override
	public boolean blockSpellNpc(Player p, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && !p.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !p.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onSpellNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !p.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !p.getCache().hasKey("cavernous_opening")) {
			attackMessage(p, n);
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && !p.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !p.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !p.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !p.getCache().hasKey("cavernous_opening")) {
			attackMessage(p, n);
		}
	}

	@Override
	public boolean blockEscapeNpc(Player p, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && p.getQuestStage(Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions");
	}

	@Override
	public void onEscapeNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && p.getQuestStage(Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions")) {
			n.remove();
			mes(p, 1300, "As you try to make your escape,",
				"the Viyeldi fighter is recalled by the demon...",
				"@yel@Nezikchened : Ha, ha ha!",
				"@yel@Nezikchened : Run then fetid worm...and never touch my totem again...");
		}

	}
}
