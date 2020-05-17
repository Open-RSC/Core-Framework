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
	public boolean blockAttackNpc(Player player, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && !player.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !player.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onAttackNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !player.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !player.getCache().hasKey("cavernous_opening")) {
			attackMessage(player, n);
		}
	}

	private void attackMessage(Player player, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !player.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !player.getCache().hasKey("cavernous_opening")) {
			npcsay(player, n, "You have entered the Viyeldi caves and  your bravery must be tested.");
			n.setChasing(player);
			npcsay(player, n, "Prepare yourself...San Tojalon will test your mettle.");
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return (n.getID() == NpcId.SAN_TOJALON.id() && !player.getCache().hasKey("cavernous_opening"))
				|| (n.getID() == NpcId.SAN_TOJALON.id() && player.getQuestStage(Quests.LEGENDS_QUEST) == 8 && player.getCache().hasKey("viyeldi_companions"));
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && player.getQuestStage(Quests.LEGENDS_QUEST) == 8 && player.getCache().hasKey("viyeldi_companions")) {
			n.remove();
			if (player.getCache().hasKey("viyeldi_companions") && player.getCache().getInt("viyeldi_companions") == 1) {
				player.getCache().set("viyeldi_companions", 2);
			}
			mes(player, config().GAME_TICK * 2, "A nerve tingling scream echoes around you as you slay the dead Hero.",
				"@yel@San Tojalon: Ahhhggggh",
				"@yel@San Tojalon: Forever must I live in this torment till this beast is slain...");
			delay(config().GAME_TICK);
			LegendsQuestNezikchened.demonFight(player);
		}
		if (n.getID() == NpcId.SAN_TOJALON.id() && !player.getCache().hasKey("cavernous_opening")) {
			if (player.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false))
				|| player.getCarriedItems().hasCatalogID(ItemId.A_RED_CRYSTAL.id(), Optional.of(false))
				|| player.getCarriedItems().hasCatalogID(ItemId.A_GLOWING_RED_CRYSTAL.id(), Optional.of(false))) {
				npcsay(player, n, "A fearsome foe you are, and bettered me once have you done already.");
				player.message("Your opponent is retreating");
				n.remove();
			} else {
				npcsay(player, n, "You have proved yourself of the honour..");
				player.resetCombatEvent();
				n.resetCombatEvent();
				player.message("Your opponent is retreating");
				npcsay(player, n, "");
				n.remove();
				mes(player, config().GAME_TICK * 2, "A piece of crystal forms in midair and falls to the floor.",
					"You place the crystal in your inventory.");
				give(player, ItemId.A_CHUNK_OF_CRYSTAL.id(), 1);
			}
		}
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && !player.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !player.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !player.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !player.getCache().hasKey("cavernous_opening")) {
			attackMessage(player, n);
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player player, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && !player.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !player.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onPlayerRangeNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && !player.getCarriedItems().hasCatalogID(ItemId.A_CHUNK_OF_CRYSTAL.id(), Optional.of(false)) && !player.getCache().hasKey("cavernous_opening")) {
			attackMessage(player, n);
		}
	}

	@Override
	public boolean blockEscapeNpc(Player player, Npc n) {
		return n.getID() == NpcId.SAN_TOJALON.id() && player.getQuestStage(Quests.LEGENDS_QUEST) == 8 && player.getCache().hasKey("viyeldi_companions");
	}

	@Override
	public void onEscapeNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SAN_TOJALON.id() && player.getQuestStage(Quests.LEGENDS_QUEST) == 8 && player.getCache().hasKey("viyeldi_companions")) {
			n.remove();
			mes(player, config().GAME_TICK * 2, "As you try to make your escape,",
				"the Viyeldi fighter is recalled by the demon...",
				"@yel@Nezikchened : Ha, ha ha!",
				"@yel@Nezikchened : Run then fetid worm...and never touch my totem again...");
		}

	}
}
