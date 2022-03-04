package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class SpinningWheel implements UseLocTrigger {

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 121;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, final Item item) {
		int produceID = -1;
		int requiredLevel = -1;
		int experience = -1;
		String verb, consumedItem, producedItem;

		if (item.getCatalogId() == ItemId.WOOL.id()) {
			produceID = ItemId.BALL_OF_WOOL.id();
			requiredLevel = 1;
			experience = !config().OLD_SKILL_DEFS ? 10 : 0;
			verb = "spin";
			consumedItem = "sheeps wool";
			producedItem = "nice ball of wool";
		} else if (item.getCatalogId() == ItemId.FLAX.id()) {
			produceID = ItemId.BOW_STRING.id();
			requiredLevel = 10;
			experience = 60;
			verb = "make";
			consumedItem = "flax";
			producedItem = "bow string";
		} else {
			player.message("Nothing interesting happens");
			return;
		}

		if (produceID == -1) {
			return;
		}
		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}

		String resultString = "You " + verb + " the " + consumedItem + " into a " + producedItem;

		startbatch(repeat);
		batchSpin(player, item, resultString, produceID, requiredLevel, experience);
	}

	private void batchSpin(Player player, Item item, String resultString, int resultCatalogID, int requiredLevel, int experience) {
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < requiredLevel) {
			mes("You need to have a crafting of level "
				+ requiredLevel + " or higher to make a "
				+ new Item(resultCatalogID).getDef(player.getWorld()).getName().toLowerCase());
			return;
		}
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to craft");
				return;
			}
		}

		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (item == null) return;

		player.getCarriedItems().remove(item);
		thinkbubble(item);
		player.playSound("mechanical");
		player.message(resultString);
		player.getCarriedItems().getInventory().add(new Item(resultCatalogID, 1));
		player.incExp(Skill.CRAFTING.id(), experience, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchSpin(player, item, resultString, resultCatalogID, requiredLevel, experience);
		}
	}
}
