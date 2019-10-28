package com.openrsc.server.plugins.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestIrvigSenay implements PlayerAttackNpcListener, PlayerAttackNpcExecutiveListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener, PlayerMageNpcListener, PlayerMageNpcExecutiveListener, PlayerRangeNpcListener, PlayerRangeNpcExecutiveListener,
	PlayerNpcRunListener, PlayerNpcRunExecutiveListener {

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.IRVIG_SENAY.id() && !hasItem(p, ItemId.A_LUMP_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onPlayerAttackNpc(Player p, Npc n) {
		if (n.getID() == NpcId.IRVIG_SENAY.id() && !hasItem(p, ItemId.A_LUMP_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening")) {
			attackMessage(p, n);
		}
	}

	private void attackMessage(Player p, Npc n) {
		if (n.getID() == NpcId.IRVIG_SENAY.id() && !hasItem(p, ItemId.A_LUMP_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening")) {
			npcTalk(p, n, "Greetings Brave warrior, destiny is upon you...");
			n.setChasing(p);
			npcTalk(p, n, "Ready your weapon and defend yourself.");
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return (n.getID() == NpcId.IRVIG_SENAY.id() && !p.getCache().hasKey("cavernous_opening"))
				|| (n.getID() == NpcId.IRVIG_SENAY.id() && p.getQuestStage(Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions"));
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == NpcId.IRVIG_SENAY.id() && p.getQuestStage(Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions")) {
			n.remove();
			if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 2) {
				p.getCache().set("viyeldi_companions", 3);
			}
			message(p, 1300, "A nerve tingling scream echoes around you as you slay the dead Hero.",
				"@yel@Irvig Senay: Ahhhggggh",
				"@yel@Irvig Senay: Forever must I live in this torment till this beast is slain...");
			sleep(650);
			LegendsQuestNezikchened.demonFight(p);
		}
		if (n.getID() == NpcId.IRVIG_SENAY.id() && !p.getCache().hasKey("cavernous_opening")) {
			if (hasItem(p, ItemId.A_LUMP_OF_CRYSTAL.id()) || hasItem(p, ItemId.A_RED_CRYSTAL.id()) || hasItem(p, ItemId.A_GLOWING_RED_CRYSTAL.id())) {
				npcTalk(p, n, "A fearsome foe you are, and bettered me once have you done already.");
				p.message("Your opponent is retreating");
				n.remove();
			} else {
				npcTalk(p, n, "You have proved yourself of the honour..");
				p.resetCombatEvent();
				n.resetCombatEvent();
				p.message("Your opponent is retreating");
				npcTalk(p, n, "");
				n.remove();
				message(p, 1300, "A piece of crystal forms in midair and falls to the floor.",
					"You place the crystal in your inventory.");
				addItem(p, ItemId.A_LUMP_OF_CRYSTAL.id(), 1);
			}
		}
	}

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		return n.getID() == NpcId.IRVIG_SENAY.id() && !hasItem(p, ItemId.A_LUMP_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc n) {
		if (n.getID() == NpcId.IRVIG_SENAY.id() && !hasItem(p, ItemId.A_LUMP_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening")) {
			attackMessage(p, n);
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		return n.getID() == NpcId.IRVIG_SENAY.id() && !hasItem(p, ItemId.A_LUMP_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening");
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if (n.getID() == NpcId.IRVIG_SENAY.id() && !hasItem(p, ItemId.A_LUMP_OF_CRYSTAL.id()) && !p.getCache().hasKey("cavernous_opening")) {
			attackMessage(p, n);
		}
	}

	@Override
	public boolean blockPlayerNpcRun(Player p, Npc n) {
		return n.getID() == NpcId.IRVIG_SENAY.id() && p.getQuestStage(Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions");
	}

	@Override
	public void onPlayerNpcRun(Player p, Npc n) {
		if (n.getID() == NpcId.IRVIG_SENAY.id() && p.getQuestStage(Quests.LEGENDS_QUEST) == 8 && p.getCache().hasKey("viyeldi_companions")) {
			n.remove();
			message(p, 1300, "As you try to make your escape,",
				"the Viyeldi fighter is recalled by the demon...",
				"@yel@Nezikchened : Ha, ha ha!",
				"@yel@Nezikchened : Run then fetid worm...and never touch my totem again...");
		}

	}
}

