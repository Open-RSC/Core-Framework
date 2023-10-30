package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.triggers.*;

import static com.openrsc.server.plugins.Functions.*;

public class Zamorak implements TalkNpcTrigger, TakeObjTrigger, AttackNpcTrigger, PlayerRangeNpcTrigger, SpellNpcTrigger {

	@Override
	public void onTakeObj(Player owner, GroundItem item) {
		Npc zam = ifnearvisnpc(owner, 7, NpcId.MONK_OF_ZAMORAK.id(), NpcId.MONK_OF_ZAMORAK_MACE.id());
		if (zam != null && !zam.inCombat()) {
			NpcInteraction interaction = NpcInteraction.NPC_TALK_TO;
			NpcInteraction.setInteractions(zam, owner, interaction);
			applyCurse(owner, zam);
		}
		else {
			owner.groundItemTake(item);
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem item) {
		return (item.getID() == ItemId.WINE_OF_ZAMORAK.id()
			|| (item.getID() == ItemId.HALF_FULL_WINE_JUG.id()&& player.getConfig().BASED_MAP_DATA <= 28))
			&& item.getX() == 333 && item.getY() == 434;
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		return n.getID() == NpcId.MONK_OF_ZAMORAK.id() || n.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id();
	}

	@Override
	public void onAttackNpc(Player player, Npc zamorak) {
		if (zamorak.getID() == NpcId.MONK_OF_ZAMORAK.id() || zamorak.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id()) {
			applyCurse(player, zamorak);
		}
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return n.getID() == NpcId.MONK_OF_ZAMORAK.id() || n.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id();
	}

	@Override
	public void onSpellNpc(Player player, Npc zamorak) {
		if (zamorak.getID() == NpcId.MONK_OF_ZAMORAK.id() || zamorak.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id()) {
			applyCurse(player, zamorak);
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player player, Npc n) {
		return n.getID() == NpcId.MONK_OF_ZAMORAK.id() || n.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id();
	}

	@Override
	public void onPlayerRangeNpc(Player player, Npc zamorak) {
		if (zamorak.getID() == NpcId.MONK_OF_ZAMORAK.id() || zamorak.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id()) {
			applyCurse(player, zamorak);
		}
	}

	private void applyCurse(Player player, Npc zam) {
		zam.getUpdateFlags().setChatMessage(new ChatMessage(zam, "A curse be upon you", player));
		delay(4);
		player.message("You feel slightly weakened");
		int dmg = (int) Math.ceil(((player.getSkills().getMaxStat(Skill.HITS.id()) + 20) * 0.05));
		player.damage(dmg);
		int[] stats = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.STRENGTH.id()};
		boolean sendUpdate = player.getClientLimitations().supportsSkillUpdate;
		for(int affectedStat : stats) {
			/* How much to lower the stat */
			int lowerBy = (int) Math.ceil(((player.getSkills().getMaxStat(affectedStat) + 20) * 0.05));
			/* New current level */
			final int newStat = Math.max(0, player.getSkills().getLevel(affectedStat) - lowerBy);
			player.getSkills().setLevel(affectedStat, newStat, sendUpdate);
		}
		if (!sendUpdate) {
			player.getSkills().sendUpdateAll();
		}
		delay();
		zam.setChasing(player);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.MONK_OF_ZAMORAK.id() || n.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.MONK_OF_ZAMORAK.id() || n.getID() == NpcId.MONK_OF_ZAMORAK_MACE.id()) {
			if (n.getID() == NpcId.MONK_OF_ZAMORAK.id()) {
				npcsay(player, n, "Save your speech for the altar");
			} else {
				npcsay(player, n, "Who are you to dare speak to the servants of Zamorak ?");
			}
			n.setChasing(player);
		}
	}
}
