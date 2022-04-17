package com.openrsc.server.plugins.custom.misc.PeelingTheOnionItems;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.custom.quests.free.PeelingTheOnion;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

import static com.openrsc.server.plugins.RuneScript.*;

public class MakeoverWaiver implements OpInvTrigger {
	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		ActionSender.sendBox(player, "@lre@Make Over Mage Liability Waiver%" +
			"@whi@You agree that you are undergoing an experimental and unproven procedure. " +
			"While we will attempt to ensure the safety of all participants, we cannot guarantee it. " +
			"Please note that your clothes may become permanently stained. By signing this waiver you agree " +
			"not to hold the Make Over Mage (henceforth referred to as \"The Mage\") responsible for any intended or unintended consequences of the procedure. " +
			"You agree to bring The Mage a cooked chicken at christmas. You further agree to forfeit " +
			"your rights to claim damages in a civil suit. You will not hold any other person or entity " +
			"responsible under the same terms as written both above and below. " +
			"This procedure could lead to symptoms including but not limited to: insomnia, increased risk of harmful gallstones, high " +
			"blood pressure, emphysema, mesothelioma, monotheism, incontinence, and dry eye. Rarely: loss of limb, blindness, sudden inability to speak, and death have occurred." +
			"% @yel@And no complaining either!" +
			"% % Sign here:   " + getSignature(player)
			, true);

		if (player.getQuestStage(Quests.PEELING_THE_ONION) == PeelingTheOnion.STATE_MAKE_OVER_MAGE_GAVE_WAIVER) {
			mes("Would you like to sign the waiver?");
			int sign = -1;
			while (sign == -1) {
				sign = multi("Sign the waiver", "Do not sign the waiver");
			}
			if (sign == 0) {
				player.setQuestStage(Quests.PEELING_THE_ONION, PeelingTheOnion.STATE_SIGNED_WAIVER);
				mes("great job signing the waiver"); // lmao idiot
			} else {
				mes("Let's not");
			}
		}
	}

	private String getSignature(Player player) {
		int queststage = player.getQuestStage(Quests.PEELING_THE_ONION);
		final String emptyLine = "__________________________________";
		if (queststage >= PeelingTheOnion.STATE_SIGNED_WAIVER || queststage == PeelingTheOnion.STATE_COMPLETE) {
			StringBuilder sb = new StringBuilder("__");
			sb.append("@cya@ ");
			sb.append(player.getUsername());
			sb.append(" @yel@");
			for (int i = 4 + player.getUsername().length(); i < emptyLine.length(); i++) {
				sb.append("_");
			}
			return sb.toString();
		} else {
			return emptyLine;
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.MAKEOVER_WAIVER.id();
	}
}
