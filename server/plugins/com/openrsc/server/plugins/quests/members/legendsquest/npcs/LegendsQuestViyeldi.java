package com.openrsc.server.plugins.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.*;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestViyeldi implements TalkNpcTrigger, TakeObjTrigger, AttackNpcTrigger, SpellNpcTrigger, PlayerRangeNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.VIYELDI.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.VIYELDI.id()) {
			switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
				case 7:
					mes(player, n, player.getWorld().getServer().getConfig().GAME_TICK * 2, "The headless, spirit of Viyeldi animates and walks towards you.");
					if (!player.getCache().hasKey("killed_viyeldi")) {
						mes(player, n, player.getWorld().getServer().getConfig().GAME_TICK * 2, "And starts talking to you in a shrill, excited voice...");
						npcsay(player, n, "Beware adventurer, lest thee loses they head in search of source.",
							"Bravery has thee been tested and not found wanting..");
						mes(player, n, player.getWorld().getServer().getConfig().GAME_TICK * 2, "The spirit wavers slightly and then stands proud...");
						npcsay(player, n, "But perilous danger waits for thee,",
							"Tojalon, Senay and Devere makes three,",
							"None hold malice but will test your might,",
							"Pray that you do not lose this fight,",
							"If however, you win this day,",
							"Take heart that see the source you may,",
							"Through dragons eye will you gain new heart,",
							"To see the source and then depart.");
					} else {
						player.message("Viyeldi falls silent...");
						delay(7000);
						player.message("...and the clothes slump to the floor.");
						if (n != null)
							n.remove();
					}
					break;
			}
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.A_BLUE_WIZARDS_HAT.id() && i.getX() == 426 && i.getY() == 3708;
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.A_BLUE_WIZARDS_HAT.id() && i.getX() == 426 && i.getY() == 3708) {
			player.teleport(i.getX(), i.getY());
			mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "Your hand passes through the hat as if it wasn't there.");
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 8) {
				return;
			}
			player.teleport(i.getX(), i.getY() - 1);
			mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 2, "Instantly the clothes begin to animate and then walk towards you.");
			Npc n = ifnearvisnpc(player, NpcId.VIYELDI.id(), 3);
			if (n == null)
				n = addnpc(player.getWorld(), NpcId.VIYELDI.id(), i.getX(), i.getY(), 60000);
			if (n != null) {
				n.initializeTalkScript(player);
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		return n.getID() == NpcId.VIYELDI.id();
	}

	@Override
	public void onAttackNpc(Player player, Npc n) {
		if (n.getID() == NpcId.VIYELDI.id()) {
			attackViyeldi(player, n);
		}
	}

	private void attackViyeldi(Player player, Npc n) {
		if (n.getID() == NpcId.VIYELDI.id()) {
			if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.DARK_DAGGER.id())) {
				mes(player, n, player.getWorld().getServer().getConfig().GAME_TICK * 2, "Your attack passes straight through Viyeldi.");
				npcsay(player, n, "Take challenge with me is useless for I am impervious to your attack",
					"Take your fight to someone else, and maybe then get back on track.");
			} else {
				player.getCarriedItems().remove(new Item(ItemId.DARK_DAGGER.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.GLOWING_DARK_DAGGER.id()));
				mes(player, n, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You thrust the Dark Dagger at Viyeldi...");
				npcsay(player, n, "So, you have fallen for the foul one's trick...");
				mes(player, n, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You hit Viyeldi squarely with the Dagger .");
				npcsay(player, n, "AhhhhhhhhHH! The Pain!");
				mes(player, n, player.getWorld().getServer().getConfig().GAME_TICK * 2, "You see a flash as something travels from Viyeldi into the dagger.");
				mes(player, n, 0, "The dagger seems to glow as Viyeldi crumpels to the floor.");
				if (n != null) {
					n.remove();
				}
				if (!player.getCache().hasKey("killed_viyeldi")) {
					player.getCache().store("killed_viyeldi", true);
				}
			}
		}
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return n.getID() == NpcId.VIYELDI.id();
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		if (n.getID() == NpcId.VIYELDI.id()) {
			attackViyeldi(player, n);
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player player, Npc n) {
		return n.getID() == NpcId.VIYELDI.id();
	}

	@Override
	public void onPlayerRangeNpc(Player player, Npc n) {
		if (n.getID() == NpcId.VIYELDI.id()) {
			attackViyeldi(player, n);
		}
	}
}
