package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.handlers.ItemUseOnItem;
import com.openrsc.server.plugins.authentic.commands.RegularPlayer;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.UsePlayerTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

// Fluffs is an unobtainable item, so it's safe to use them as a moderator tool.
// Even if someone got Fluffs, they don't do anything very powerful.
// All dialogue and behaviours in this file are inauthentic.
public class Fluffs implements UsePlayerTrigger, OpInvTrigger, UseNpcTrigger, UseInvTrigger {
	@Override
	public void onUsePlayer(Player player, Player otherPlayer, Item item) {
		if (item.getCatalogId() == ItemId.GERTRUDES_CAT.id()) {
			if (player.hasElevatedPriveledges()) {
				player.face(otherPlayer);

				thinkbubble(item);
				player.playerServerMessage(MessageType.QUEST, "You release Fluffs upon " + otherPlayer.getUsername());
				otherPlayer.playerServerMessage(MessageType.QUEST, "fluffs looks at you with great anger...");
				delay(3);
				thinkbubble(item);
				player.playerServerMessage(MessageType.QUEST, "@yel@Fluffs: @ran@MRRROAOAOUWW!! @dre@ARE YOU BOTTING?? ARE YOU A BOTTER???");
				otherPlayer.playerServerMessage(MessageType.QUEST, "@yel@Fluffs: @ran@MRRROAOAOUWW!! @dre@ARE YOU BOTTING?? ARE YOU A BOTTER???");
				delay(3);
				thinkbubble(item);
				player.playerServerMessage(MessageType.QUEST, "@yel@Fluffs: if u are not a botter myeow, then please answers this question nya");
				otherPlayer.playerServerMessage(MessageType.QUEST, "@yel@Fluffs: if u are not a botter myeow, then please answers this question nya");
				delay(3);
				thinkbubble(item);
				player.playerServerMessage(MessageType.QUEST, "@yel@Fluffs: what food do i think is most delicious? :3");
				otherPlayer.playerServerMessage(MessageType.QUEST, "@yel@Fluffs: what food do i think is most delicious? :3");
				delay(3);
				thinkbubble(item);
			}
		}
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		player.playerServerMessage(MessageType.QUEST, "Fluffs looks kind of annoyed, but tolerates your gentle caress");
		if (player.hasElevatedPriveledges()) {
			if (player.getWorld().getPlayers().size() > 1) {
				player.playerServerMessage(MessageType.QUEST, "Eventually, Fluffs meows and you think you can hear the names of all players online?");
				RegularPlayer.queryOnlinePlayers(player, true, false);
			} else {
				// fluffs' pronouns are she/her according to Gertrude's transcript
				player.playerServerMessage(MessageType.QUEST, "Eventually, Fluffs meows and you think you can hear her say something?");
				delay(3);
				player.playerServerMessage(MessageType.QUEST, "@yel@Fluffs: @dre@... It's just you and me, kid.");
				delay(2);
				player.playerServerMessage(MessageType.QUEST, "@yel@Fluffs: Could we go visit Gertrude some time?");
			}
		}
	}

	@Override
	public boolean blockUsePlayer(Player player, Player otherPlayer, Item item) {
		return item.getCatalogId() == ItemId.GERTRUDES_CAT.id();
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.GERTRUDES_CAT.id();
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.GERTRUDE.id() && item.getCatalogId() == ItemId.GERTRUDES_CAT.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		NpcInteraction interaction = NpcInteraction.NPC_TALK_TO;
		NpcInteraction.setInteractions(npc, player, interaction);
		npcsay(player, npc, "Oh Fluffs!! It's so good to see you.");
		mes("Fluffs jumps out of your sack and rubs against Gertrude's legs, purring");
		if (player.getCarriedItems().getInventory().hasInInventory(item.getCatalogId())) {
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
		}
		delay(4);
		npcsay(player, npc, "I trust you've been taking good care of Fluffs?");
		say(player, npc, "Of course. She's very precious and useful.");
		say(player, npc, "I can tell why you love her so much.");
		delay();
		mes("Fluffs seems to be very much at home and doesn't want to leave.");
		delay(5);
		say(player, npc, "... I can let you have her for a bit if you'd like");
		delay();
		npcsay(player, npc, "That would be wonderful, thankyou.");
		npcsay(player, npc, "I understand she is a very important kitty these days");
		npcsay(player, npc, "with a very important job.");
		npcsay(player, npc, "If you need her again, you know how to get her.");
		say(player, npc, "1093.");
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		// TODO: make fluffs do some amazing moderator tool thing when using ball of wool on her
		mes("Fluffs plays with the ball of wool and purrs");
		delay(2);
		mes("@whi@" + player.getUsername() + ": It'd be really cool if you did something useful with that!");
		delay(2);
		mes("@yel@Fluffs: Are you not entertained?");
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.GERTRUDES_CAT.id(), ItemId.BALL_OF_WOOL.id());
	}

}
