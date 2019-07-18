package com.openrsc.server.plugins.quests.members.shilovillage;

import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerNpcRunListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerNpcRunExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class ShiloVillageNazastarool implements ObjectActionListener, ObjectActionExecutiveListener,
	PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener, PlayerNpcRunListener, PlayerNpcRunExecutiveListener,
	PlayerMageNpcListener, PlayerMageNpcExecutiveListener {

	private static final int TOMB_DOLMEN_Nazastarool = 724;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == TOMB_DOLMEN_Nazastarool;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == TOMB_DOLMEN_Nazastarool) {
			if (p.getCache().hasKey("dolmen_zombie")
				&& p.getCache().hasKey("dolmen_skeleton")
				&& p.getCache().hasKey("dolmen_ghost")) {
				if (!p.getInventory().wielding(ItemId.BEADS_OF_THE_DEAD.id())) {
					choke(p);
				}
				if (hasItem(p, ItemId.RASHILIYA_CORPSE.id())) {
					p.message("You find nothing new on the Dolmen.");
					return;
				}
				message(p, "You search the Dolmen...",
					"and find the mumified remains of a human female corpse.");
				p.message("Do you want to take the corpse?");
				int menu = showMenu(p, "Yes, I'll take the remains.", "No, I'll leave them where they are.");
				if (menu == 0) {
					p.message("You carefully place the remains in your inventory.");
					addItem(p, ItemId.RASHILIYA_CORPSE.id(), 1);
				} else if (menu == 1) {
					p.message("You decide to leave the remains where they are.");
				}
				return;
			}
			p.setBusy(true);
			if (!p.getInventory().wielding(ItemId.BEADS_OF_THE_DEAD.id())) {
				choke(p);
			}
			p.message("You touch the Dolmen, and the ground starts to shake.");
			sleep(1200);
			p.message("You hear an unearthly voice booming and ");
			sleep(1200);
			p.message("you step away from the Dolmen in anticipation...");
			sleep(1000);
			p.teleport(380, 3625);
			if (!p.getInventory().wielding(ItemId.BEADS_OF_THE_DEAD.id())) {
				choke(p);
			}
			if (!p.getCache().hasKey("dolmen_zombie")) {
				spawnAndMoveAway(p, NpcId.NAZASTAROOL_ZOMBIE.id());
				p.setBusy(false);
				return;
			}
			if (!p.getCache().hasKey("dolmen_skeleton")) {
				spawnAndMoveAway(p, NpcId.NAZASTAROOL_SKELETON.id());
				p.setBusy(false);
				return;
			}
			if (!p.getCache().hasKey("dolmen_ghost")) {
				spawnAndMoveAway(p, NpcId.NAZASTAROOL_GHOST.id());
				p.setBusy(false);
				return;
			}
		}
	}

	private void choke(Player p) {
		message(p, "@red@You feel invisible hands starting to choke you...");
		p.damage(getCurrentLevel(p, SKILLS.HITS.id()) / 2);
	}

	private void runFromNazastarool(Player p, Npc n) {
		p.setBusy(true);
		p.teleport(379, 3626);
		n.teleport(378, 3622);
		if (n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id()) {
			npcTalk(p, n, "Leave then, and let Rashiliyia rest in peace!",
				"Do not return here or your life will be forfeit!");
		} else if (n.getID() == NpcId.NAZASTAROOL_SKELETON.id()) {
			npcTalk(p, n, "Leave now mortal, sweet Rashiliyia will rest!",
				"Your life will be forfeit if you return!");
		} else if (n.getID() == NpcId.NAZASTAROOL_GHOST.id()) {
			npcTalk(p, n, "Run infidel and never polute the tomb of Rashiliyia again!",
				"A grisly death is what you will meet should you return.");
		}
		n.remove();
		p.setBusy(false);
	}

	// run away coords 379, 3626
	private void spawnAndMoveAway(Player p, int npcID) {
		Npc npc = spawnNpc(npcID, 380, 3625, 60000 * 5);
		sleep(1000);
		npc.teleport(381, 3625);
		if (npc.getID() == NpcId.NAZASTAROOL_ZOMBIE.id()) {
			zombieShout(p, npc);
		} else if (npc.getID() == NpcId.NAZASTAROOL_SKELETON.id()) {
			skeletonShout(p, npc);
		} else if (npc.getID() == NpcId.NAZASTAROOL_GHOST.id()) {
			ghostShout(p, npc);
		}
		npc.startCombat(p);
	}

	private void zombieShout(Player p, Npc n) {
		npcTalk(p, n, "Who dares disturb Rashiliyias' rest?",
			"I am Nazastarool!",
			"Prepare to die!");
	}

	private void skeletonShout(Player p, Npc n) {
		npcTalk(p, n, "Quake in fear, for I am reborn!",
			"Your death will be swift.");
	}

	private void ghostShout(Player p, Npc n) {
		npcTalk(p, n, "Nazastarool returns with vengeance!",
			"Soon you will serve Rashiliyia!");
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id() || n.getID() == NpcId.NAZASTAROOL_SKELETON.id() || n.getID() == NpcId.NAZASTAROOL_GHOST.id();
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() ==  NpcId.NAZASTAROOL_ZOMBIE.id()) {
			n.remove();
			p.setBusy(true);
			if (!p.getCache().hasKey("dolmen_zombie")) {
				p.getCache().store("dolmen_zombie", true);
			}
			p.message("You defeat Nazastarool and the corpse falls to  ");
			sleep(1200);
			p.message("the ground. The bones start to move again and   ");
			sleep(1200);
			p.message("soon they reform into a grisly giant skeleton.  ");
			sleep(1000);
			spawnAndMoveAway(p, NpcId.NAZASTAROOL_SKELETON.id());
			p.setBusy(false);
		}
		if (n.getID() == NpcId.NAZASTAROOL_SKELETON.id()) {
			n.remove();
			p.setBusy(true);
			if (!p.getCache().hasKey("dolmen_skeleton")) {
				p.getCache().store("dolmen_skeleton", true);
			}
			p.message("You defeat the Nazastarool Skeleton as the corpse falls to ");
			sleep(1200);
			p.message("the ground. An ethereal form starts taking shape above the ");
			sleep(1200);
			p.message("bones and you soon face the vengeful ghost of Nazastarool ");
			sleep(1000);
			spawnAndMoveAway(p, NpcId.NAZASTAROOL_GHOST.id());
			p.setBusy(false);
		}
		if (n.getID() == NpcId.NAZASTAROOL_GHOST.id()) {
			n.remove();
			p.setBusy(true);
			if (!p.getCache().hasKey("dolmen_ghost")) {
				p.getCache().store("dolmen_ghost", true);
			}
			p.message("@yel@Nazastarool: May you perish in the fires of Zamoraks furnace!");
			sleep(1200);
			p.message("@yel@Nazastarool: May Rashiliyias Curse be upon you!");
			sleep(1200);
			p.message("You see something appear on the Dolmen");
			p.setBusy(false);
		}
	}

	@Override
	public boolean blockPlayerNpcRun(Player p, Npc n) {
		return n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id() || n.getID() == NpcId.NAZASTAROOL_SKELETON.id() || n.getID() == NpcId.NAZASTAROOL_GHOST.id();
	}

	@Override
	public void onPlayerNpcRun(Player p, Npc n) {
		if (n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id() || n.getID() == NpcId.NAZASTAROOL_SKELETON.id() || n.getID() == NpcId.NAZASTAROOL_GHOST.id()) {
			runFromNazastarool(p, n);
		}
	}

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		return n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id() || n.getID() == NpcId.NAZASTAROOL_SKELETON.id() || n.getID() == NpcId.NAZASTAROOL_GHOST.id();
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc n) {
		if (n.getID() == NpcId.NAZASTAROOL_ZOMBIE.id() || n.getID() == NpcId.NAZASTAROOL_SKELETON.id() || n.getID() == NpcId.NAZASTAROOL_GHOST.id()) {
			if (!p.getInventory().wielding(ItemId.BEADS_OF_THE_DEAD.id())) {
				choke(p);
			}
			n.getSkills().setLevel(SKILLS.HITS.id(), n.getSkills().getMaxStat(SKILLS.HITS.id()));
		}
	}
}
