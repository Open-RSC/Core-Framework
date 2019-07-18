package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills.SKILLS;
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
		String verb, consumedItem, producedItem;

		if (item.getID() == ItemId.WOOL.id()) {
			produceID = ItemId.BALL_OF_WOOL.id();
			requiredLevel = 1;
			experience = 10;
			verb = "spin";
			consumedItem = "sheeps wool";
			producedItem = "nice ball of wool";
		}

		else if (item.getID() == ItemId.FLAX.id()) {
			produceID = ItemId.BOW_STRING.id();
			requiredLevel = 10;
			experience = 60;
			verb = "make";
			consumedItem = "flax";
			producedItem = "bow string";
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
		player.setBatchEvent(new BatchEvent(player, 600, "Spinning Wheel", Formulae
			.getRepeatTimes(player, SKILLS.CRAFTING.id()), false) {

			@Override
			public void action() {
				if (owner.getSkills().getLevel(SKILLS.CRAFTING.id()) < requirement) {
					message(owner, "You need to have a crafting of level "
						+ requirement + " or higher to make a "
						+ new Item(produce).getDef().getName().toLowerCase());
					interrupt();
					return;
				}
				if (owner.getInventory().remove(item.getID(), 1) > -1) {
					showBubble(owner, item);
					owner.playSound("mechanical");
					owner.message("You " + verb + " the "
						+ consumedItem + " into a " + producedItem);
					owner.getInventory().add(new Item(produce, 1));
					owner.incExp(SKILLS.CRAFTING.id(), exp, true);
				} else {
					interrupt();
				}
			}
		});

	}
}
