package com.openrsc.server.model.entity.npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.npc.Npc;

public enum NpcInteraction {
	NPC_ATTACK,
	NPC_TALK_TO,
	NPC_OP,
	NPC_USE_ITEM,
	NPC_KILL,
	NPC_CAST_SPELL,
	NPC_GNOMEBALL_OP;

	public static void setInteractions(Npc npc, Player player, NpcInteraction interaction) {
		npc.setInteractingPlayer(player);
		player.setInteractingNpc(npc);
		npc.setNpcInteraction(interaction);
		player.setNpcInteraction(interaction);
	}
}
