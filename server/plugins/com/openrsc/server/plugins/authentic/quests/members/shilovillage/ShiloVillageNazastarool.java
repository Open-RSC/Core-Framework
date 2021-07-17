package com.openrsc.server.plugins.authentic.quests.members.shilovillage;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.EscapeNpcTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.SpellNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class ShiloVillageNazastarool implements OpLocTrigger,
	KillNpcTrigger, EscapeNpcTrigger,
	SpellNpcTrigger {

	private static final int TOMB_DOLMEN_Nazastarool = 724;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == TOMB_DOLMEN_Nazastarool;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == TOMB_DOLMEN_Nazastarool) {
			if (player.getCache().hasKey("dolmen_zombie")
				&& player.getCache().hasKey("dolmen_skeleton")
				&& player.getCache().hasKey("dolmen_ghost")) {
				if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.BEADS_OF_THE_DEAD.id())) {
					choke(player);
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.RASHILIYA_CORPSE.id(), Optional.of(false))) {
					player.message("You find nothing new on the Dolmen.");
					return;
				}
				mes("You search the Dolmen...");
				delay(3);
				mes("and find the mumified remains of a human female corpse.");
				delay(3);
				player.message("Do you want to take the corpse?");
				int menu = multi(player, "Yes, I'll take the remains.", "No, I'll leave them where they are.");
				if (menu == 0) {
					player.message("You carefully place the remains in your inventory.");
					give(player, ItemId.RASHILIYA_CORPSE.id(), 1);
					if (!player.getCache().hasKey("rashiliya_corpse")) {
						player.getCache().store("rashiliya_corpse", true);
					}
				} else if (menu == 1) {
					player.message("You decide to leave the remains where they are.");
				}
				return;
			}
			if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.BEADS_OF_THE_DEAD.id())) {
				choke(player);
			}
			player.message("You touch the Dolmen, and the ground starts to shake.");
			delay(2);
			player.message("You hear an unearthly voice booming and ");
			delay(2);
			player.message("you step away from the Dolmen in anticipation...");
			delay(2);
			player.teleport(380, 3625);
			if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.BEADS_OF_THE_DEAD.id())) {
				choke(player);
			}
			if (!player.getCache().hasKey("dolmen_zombie")) {
				spawnAndMoveAway(player, NpcId.NAZASTAROOL_ZOMBIE.id());
				return;
			}
			if (!player.getCache().hasKey("dolmen_skeleton")) {
				spawnAndMoveAway(player, NpcId.NAZASTAROOL_SKELETON.id());
				return;
			}
			if (!player.getCache().hasKey("dolmen_ghost")) {
				spawnAndMoveAway(player, NpcId.NAZASTAROOL_GHOST.id());
				return;
			}
		}
	}

	private void choke(Player player) {
		mes("@red@You feel invisible hands starting to choke you...");
		delay(3);
		player.damage(getCurrentLevel(player, Skill.HITS.id()) / 2);
	}

	private void runFromNazastarool(Player player, Npc n) {
		player.teleport(379, 3626);
		n.teleport(378, 3622);
		if (n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id()) {
			npcsay(player, n, "Leave then, and let Rashiliyia rest in peace!",
				"Do not return here or your life will be forfeit!");
		} else if (n.getID() == NpcId.NAZASTAROOL_SKELETON.id()) {
			npcsay(player, n, "Leave now mortal, sweet Rashiliyia will rest!",
				"Your life will be forfeit if you return!");
		} else if (n.getID() == NpcId.NAZASTAROOL_GHOST.id()) {
			npcsay(player, n, "Run infidel and never polute the tomb of Rashiliyia again!",
				"A grisly death is what you will meet should you return.");
		}
		n.remove();
	}

	// run away coords 379, 3626
	private void spawnAndMoveAway(Player player, int npcID) {
		Npc npc = addnpc(player.getWorld(), npcID, 380, 3625, 60000 * 5);
		delay(2);
		npc.teleport(381, 3625);
		if (npc.getID() == NpcId.NAZASTAROOL_ZOMBIE.id()) {
			zombieShout(player, npc);
		} else if (npc.getID() == NpcId.NAZASTAROOL_SKELETON.id()) {
			skeletonShout(player, npc);
		} else if (npc.getID() == NpcId.NAZASTAROOL_GHOST.id()) {
			ghostShout(player, npc);
		}
		npc.startCombat(player);
	}

	private void zombieShout(Player player, Npc n) {
		npcsay(player, n, "Who dares disturb Rashiliyias' rest?",
			"I am Nazastarool!",
			"Prepare to die!");
	}

	private void skeletonShout(Player player, Npc n) {
		npcsay(player, n, "Quake in fear, for I am reborn!",
			"Your death will be swift.");
	}

	private void ghostShout(Player player, Npc n) {
		npcsay(player, n, "Nazastarool returns with vengeance!",
			"Soon you will serve Rashiliyia!");
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id() || n.getID() == NpcId.NAZASTAROOL_SKELETON.id() || n.getID() == NpcId.NAZASTAROOL_GHOST.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() ==  NpcId.NAZASTAROOL_ZOMBIE.id()) {
			n.remove();
			if (!player.getCache().hasKey("dolmen_zombie")) {
				player.getCache().store("dolmen_zombie", true);
			}
			player.message("You defeat Nazastarool and the corpse falls to  ");
			delay(2);
			player.message("the ground. The bones start to move again and   ");
			delay(2);
			player.message("soon they reform into a grisly giant skeleton.  ");
			delay(2);
			spawnAndMoveAway(player, NpcId.NAZASTAROOL_SKELETON.id());
		}
		if (n.getID() == NpcId.NAZASTAROOL_SKELETON.id()) {
			n.remove();
			if (!player.getCache().hasKey("dolmen_skeleton")) {
				player.getCache().store("dolmen_skeleton", true);
			}
			player.message("You defeat the Nazastarool Skeleton as the corpse falls to ");
			delay(2);
			player.message("the ground. An ethereal form starts taking shape above the ");
			delay(2);
			player.message("bones and you soon face the vengeful ghost of Nazastarool ");
			delay(2);
			spawnAndMoveAway(player, NpcId.NAZASTAROOL_GHOST.id());
		}
		if (n.getID() == NpcId.NAZASTAROOL_GHOST.id()) {
			n.remove();
			if (!player.getCache().hasKey("dolmen_ghost")) {
				player.getCache().store("dolmen_ghost", true);
			}
			player.message("@yel@Nazastarool: May you perish in the fires of Zamoraks furnace!");
			delay(2);
			player.message("@yel@Nazastarool: May Rashiliyias Curse be upon you!");
			delay(2);
			player.message("You see something appear on the Dolmen");
		}
	}

	@Override
	public boolean blockEscapeNpc(Player player, Npc n) {
		return n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id() || n.getID() == NpcId.NAZASTAROOL_SKELETON.id() || n.getID() == NpcId.NAZASTAROOL_GHOST.id();
	}

	@Override
	public void onEscapeNpc(Player player, Npc n) {
		if (n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id() || n.getID() == NpcId.NAZASTAROOL_SKELETON.id() || n.getID() == NpcId.NAZASTAROOL_GHOST.id()) {
			runFromNazastarool(player, n);
		}
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id() || n.getID() == NpcId.NAZASTAROOL_SKELETON.id() || n.getID() == NpcId.NAZASTAROOL_GHOST.id();
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		if (n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id() || n.getID() == NpcId.NAZASTAROOL_SKELETON.id() || n.getID() == NpcId.NAZASTAROOL_GHOST.id()) {
			if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.BEADS_OF_THE_DEAD.id())) {
				choke(player);
			}
			n.getSkills().setLevel(Skill.HITS.id(), n.getSkills().getMaxStat(Skill.HITS.id()));
		}
	}
}
