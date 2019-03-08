package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.showBubble;

public class SpinningWheel implements InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener {

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return obj.getID() == 121;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, final Item item, Player player) {
		int produceID = -1;
		int requiredLevel = -1;
		int experience = -1;

		if (item.getID() == ItemId.WOOL.id()) {
			produceID = ItemId.BALL_OF_WOOL.id();
			requiredLevel = 1;
			experience = 10;
		}

		else if (item.getID() == ItemId.FLAX.id()) {
			produceID = ItemId.BOW_STRING.id();
			requiredLevel = 10;
			experience = 60;
		}

		else {
			player.message("Nothing interesting happens");
			return;
		}

		final int produce = produceID;
		final int requirement = requiredLevel;
		final int exp = experience;
		if (produce == -1 || requirement == -1 || exp == -1) {
			return;
		}
		player.setBatchEvent(new BatchEvent(player, 600, Formulae
			.getRepeatTimes(player, Skills.CRAFTING)) {
			@Override
			public void action() {
				if (owner.getSkills().getLevel(Skills.CRAFTING) < requirement) {
					message(owner, "You need a crafting level of "
						+ requirement + " to spin "
						+ item.getDef().getName().toLowerCase() + "!");
					interrupt();
					return;
				}
				if (owner.getInventory().remove(item.getID(), 1) > -1) {
					showBubble(owner, item);
					owner.playSound("mechanical");
					owner.message("You make the "
						+ item.getDef().getName()
						+ " into a "
						+ EntityHandler.getItemDef(produce).getName()
						.toLowerCase() + "");
					owner.getInventory().add(new Item(produce, 1));
					owner.incExp(Skills.CRAFTING, exp, true);
				} else {
					interrupt();
				}
			}
		});

	}
}
