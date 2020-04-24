package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class SpinningWheel implements UseLocTrigger {

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player player) {
		return obj.getID() == 121;
	}

	@Override
	public void onUseLoc(GameObject obj, final Item item, Player player) {
		int produceID = -1;
		int requiredLevel = -1;
		int experience = -1;
		String verb, consumedItem, producedItem;

		if (item.getCatalogId() == ItemId.WOOL.id()) {
			produceID = ItemId.BALL_OF_WOOL.id();
			requiredLevel = 1;
			experience = 10;
			verb = "spin";
			consumedItem = "sheeps wool";
			producedItem = "nice ball of wool";
		}

		else if (item.getCatalogId() == ItemId.FLAX.id()) {
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
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Spinning Wheel", Formulae
			.getRepeatTimes(player, Skills.CRAFTING), false) {

			@Override
			public void action() {
				if (getOwner().getSkills().getLevel(Skills.CRAFTING) < requirement) {
					mes(getOwner(), "You need to have a crafting of level "
						+ requirement + " or higher to make a "
						+ new Item(produce).getDef(getWorld()).getName().toLowerCase());
					interrupt();
					return;
				}
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to craft");
						interrupt();
						return;
					}
				}
				if (getOwner().getCarriedItems().remove(new Item(item.getCatalogId())) > -1) {
					thinkbubble(getOwner(), item);
					getOwner().playSound("mechanical");
					getOwner().message("You " + verb + " the "
						+ consumedItem + " into a " + producedItem);
					getOwner().getCarriedItems().getInventory().add(new Item(produce, 1));
					getOwner().incExp(Skills.CRAFTING, exp, true);
				} else {
					interrupt();
				}
			}
		});

	}
}
