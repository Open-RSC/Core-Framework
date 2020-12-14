package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.authentic.commands.RegularPlayer;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UsePlayerTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

// Fluffs is an unobtainable item, so it's safe to use them as a moderator tool. Even if someone got Fluffs, they don't do anything very powerful.
public class Fluffs implements UsePlayerTrigger, OpInvTrigger {
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
			player.playerServerMessage(MessageType.QUEST, "Eventually, Fluffs meows and you think you can hear the names of all players online?");
			RegularPlayer.queryOnlinePlayers(player);
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
}
