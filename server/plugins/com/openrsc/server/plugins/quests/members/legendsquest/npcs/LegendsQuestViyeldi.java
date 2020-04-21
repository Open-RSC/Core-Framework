package com.openrsc.server.plugins.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.*;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestViyeldi implements TalkNpcTrigger, TakeObjTrigger, AttackNpcTrigger, SpellNpcTrigger, PlayerRangeNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.VIYELDI.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.VIYELDI.id()) {
			switch (p.getQuestStage(Quests.LEGENDS_QUEST)) {
				case 7:
					mes(p, n, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The headless, spirit of Viyeldi animates and walks towards you.");
					if (!p.getCache().hasKey("killed_viyeldi")) {
						mes(p, n, p.getWorld().getServer().getConfig().GAME_TICK * 2, "And starts talking to you in a shrill, excited voice...");
						npcsay(p, n, "Beware adventurer, lest thee loses they head in search of source.",
							"Bravery has thee been tested and not found wanting..");
						mes(p, n, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The spirit wavers slightly and then stands proud...");
						npcsay(p, n, "But perilous danger waits for thee,",
							"Tojalon, Senay and Devere makes three,",
							"None hold malice but will test your might,",
							"Pray that you do not lose this fight,",
							"If however, you win this day,",
							"Take heart that see the source you may,",
							"Through dragons eye will you gain new heart,",
							"To see the source and then depart.");
					} else {
						p.message("Viyeldi falls silent...");
						delay(7000);
						p.message("...and the clothes slump to the floor.");
						if (n != null)
							n.remove();
					}
					break;
			}
		}
	}

	@Override
	public boolean blockTakeObj(Player p, GroundItem i) {
		return i.getID() == ItemId.A_BLUE_WIZARDS_HAT.id() && i.getX() == 426 && i.getY() == 3708;
	}

	@Override
	public void onTakeObj(Player p, GroundItem i) {
		if (i.getID() == ItemId.A_BLUE_WIZARDS_HAT.id() && i.getX() == 426 && i.getY() == 3708) {
			p.teleport(i.getX(), i.getY());
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "Your hand passes through the hat as if it wasn't there.");
			if (p.getQuestStage(Quests.LEGENDS_QUEST) >= 8) {
				return;
			}
			p.teleport(i.getX(), i.getY() - 1);
			mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "Instantly the clothes begin to animate and then walk towards you.");
			Npc n = ifnearvisnpc(p, NpcId.VIYELDI.id(), 3);
			if (n == null)
				n = addnpc(p.getWorld(), NpcId.VIYELDI.id(), i.getX(), i.getY(), 60000);
			if (n != null) {
				n.initializeTalkScript(p);
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.VIYELDI.id();
	}

	@Override
	public void onAttackNpc(Player p, Npc n) {
		if (n.getID() == NpcId.VIYELDI.id()) {
			attackViyeldi(p, n);
		}
	}

	private void attackViyeldi(Player p, Npc n) {
		if (n.getID() == NpcId.VIYELDI.id()) {
			if (!p.getCarriedItems().getEquipment().hasEquipped(ItemId.DARK_DAGGER.id())) {
				mes(p, n, p.getWorld().getServer().getConfig().GAME_TICK * 2, "Your attack passes straight through Viyeldi.");
				npcsay(p, n, "Take challenge with me is useless for I am impervious to your attack",
					"Take your fight to someone else, and maybe then get back on track.");
			} else {
				p.getCarriedItems().getInventory().replace(ItemId.DARK_DAGGER.id(), ItemId.GLOWING_DARK_DAGGER.id());
				mes(p, n, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You thrust the Dark Dagger at Viyeldi...");
				npcsay(p, n, "So, you have fallen for the foul one's trick...");
				mes(p, n, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You hit Viyeldi squarely with the Dagger .");
				npcsay(p, n, "AhhhhhhhhHH! The Pain!");
				mes(p, n, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You see a flash as something travels from Viyeldi into the dagger.");
				mes(p, n, 0, "The dagger seems to glow as Viyeldi crumpels to the floor.");
				if (n != null) {
					n.remove();
				}
				if (!p.getCache().hasKey("killed_viyeldi")) {
					p.getCache().store("killed_viyeldi", true);
				}
			}
		}
	}

	@Override
	public boolean blockSpellNpc(Player p, Npc n) {
		return n.getID() == NpcId.VIYELDI.id();
	}

	@Override
	public void onSpellNpc(Player p, Npc n) {
		if (n.getID() == NpcId.VIYELDI.id()) {
			attackViyeldi(p, n);
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		return n.getID() == NpcId.VIYELDI.id();
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if (n.getID() == NpcId.VIYELDI.id()) {
			attackViyeldi(p, n);
		}
	}
}
