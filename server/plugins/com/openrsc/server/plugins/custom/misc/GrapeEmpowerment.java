package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;

public class GrapeEmpowerment implements UseInvTrigger {

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (item1.getItemStatus().getNoted() || item2.getItemStatus().getNoted()) return;

		if (!config().MEMBER_WORLD || !config().WANT_HARVESTING) {
			player.message("Nothing interesting happens");
			return;
		}

		if (compareItemsIds(item1, item2, ItemId.GRAPES.id(), ItemId.HOLY_SYMBOL_OF_SARADOMIN.id())) {
			if (player.getLocation().isInZamorakMonksPlace()) {
				Npc monk = addnpc(player.getWorld(), NpcId.MONK_OF_ZAMORAK.id(), player.getX(), player.getY(), (int) TimeUnit.SECONDS.toMillis(120));
				if (monk != null) {
					delay();
					npcsay(player, monk, "How dare you go blessing in Saradomins name");
					delay();
					monk.setChasing(player);
					return;
				}
			} else if (!player.getLocation().isInSaradominMonksPlace()) {
				player.message("Nothing seems to occur");
				return;
			}

			if (player.getLevel(Skill.HARVESTING.id()) < 85) {
				player.playerServerMessage(MessageType.QUEST, "Your harvesting level is not high enough");
				player.playerServerMessage(MessageType.QUEST, "to hold blessed grapes and they would just wither");
				delay(2);
				player.playerServerMessage(MessageType.QUEST, "You need a harvesting level of 85 to bless the grapes");
				return;
			}

			if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.MONKS_ROBE_TOP.id())
				&& !player.getCarriedItems().getEquipment().hasEquipped(ItemId.MONKS_ROBE_BOTTOM.id())) {
				player.playerServerMessage(MessageType.QUEST, "Your faith in Saradomin is not strong enough");
				delay(2);
				player.playerServerMessage(MessageType.QUEST, "You need to be wearing the set of monk robes to bless the grapes");
				return;
			}

			int repeat = 1;
			int currentPrayer = player.getSkills().getLevel(Skill.PRAYER.id());
			if (currentPrayer < 1) {
				player.message("You do not feel devout enough to bless the grapes");
				delay(2);
				player.message("Try recharging your prayer points");
				return;
			}

			if (config().BATCH_PROGRESSION) {
				repeat = Math.min(player.getCarriedItems().getInventory().countId(ItemId.GRAPES.id()),
					currentPrayer);
			}

			startbatch(repeat);
			batchPower(player, new Item(ItemId.GRAPES.id()), ItemId.GRAPES_OF_SARADOMIN.id(), "You bless the grapes");

			return;
		} else if (compareItemsIds(item1, item2, ItemId.GRAPES.id(), ItemId.UNHOLY_SYMBOL_OF_ZAMORAK.id())) {
			if (player.getLocation().isInSaradominMonksPlace()) {
				Npc monk = addnpc(player.getWorld(), NpcId.MONK.id(), player.getX(), player.getY(), (int) TimeUnit.SECONDS.toMillis(120));
				if (monk != null) {
					delay();
					npcsay(player, monk, "You better stop cursing around on Zamoraks name");
					delay();
					monk.setChasing(player);
					return;
				}
			} else if (!player.getLocation().isInZamorakMonksPlace()) {
				player.message("Nothing seems to occur");
				return;
			}

			if (player.getLevel(Skill.HARVESTING.id()) < 85) {
				player.playerServerMessage(MessageType.QUEST, "Your harvesting level is not high enough");
				player.playerServerMessage(MessageType.QUEST, "to hold cursed grapes and they would just wither");
				delay(2);
				player.playerServerMessage(MessageType.QUEST, "You need a harvesting level of 85 to curse the grapes");
				return;
			}

			if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.ROBE_OF_ZAMORAK_TOP.id())
				&& !player.getCarriedItems().getEquipment().hasEquipped(ItemId.ROBE_OF_ZAMORAK_BOTTOM.id())) {
				player.playerServerMessage(MessageType.QUEST, "Your faith in Zamorak is not strong enough");
				delay(2);
				player.playerServerMessage(MessageType.QUEST, "You need to be wearing the set of zamorak robes to curse the grapes");
				return;
			}

			int repeat = 1;
			int currentPrayer = player.getSkills().getLevel(Skill.PRAYER.id());
			if (currentPrayer < 1) {
				player.message("You do not feel devout enough to curse the grapes");
				delay(2);
				player.message("Try recharging your prayer points");
				return;
			}

			if (config().BATCH_PROGRESSION) {
				repeat = Math.min(player.getCarriedItems().getInventory().countId(ItemId.GRAPES.id()),
					currentPrayer);
			}

			startbatch(repeat);
			batchPower(player, new Item(ItemId.GRAPES.id()), ItemId.GRAPES_OF_ZAMORAK.id(), "You curse the grapes");

			return;
		} else if (compareItemsIds(item1, item2, ItemId.GRAPES_OF_SARADOMIN.id(), ItemId.JUG_OF_WATER.id())) {
			makePowerfulWine(player, item1, item2, ItemId.WINE_OF_SARADOMIN.id());
		} else if (compareItemsIds(item1, item2, ItemId.GRAPES_OF_ZAMORAK.id(), ItemId.JUG_OF_WATER.id())) {
			makePowerfulWine(player, item1, item2, ItemId.WINE_OF_ZAMORAK.id());
		}
	}

	private void makePowerfulWine(Player player, Item item1, Item item2, int resultWineId) {
		if (player.getSkills().getLevel(Skill.COOKING.id()) < 70) {
			player.message("You need level 70 cooking to do this");
			return;
		}
		if (player.getCarriedItems().getInventory().contains(item1)
			&& player.getCarriedItems().getInventory().contains(item2)) {
			if (player.getSkills().getLevel(Skill.COOKING.id()) < 70) {
				player.playerServerMessage(MessageType.QUEST, "You need level 70 cooking to do this");
				return;
			}

			int repeat = 1;
			if (config().BATCH_PROGRESSION) {
				repeat = Math.min(player.getCarriedItems().getInventory().countId(item1.getCatalogId(), Optional.of(false)),
					player.getCarriedItems().getInventory().countId(item2.getCatalogId(), Optional.of(false)));
			}

			startbatch(repeat);
			batchPowerfulWineMaking(player, item1, item2, resultWineId);
		}
	}

	private void batchPowerfulWineMaking(Player player, Item item1, Item item2, int resultWineId) {
		player.playerServerMessage(MessageType.QUEST, "You squeeze the grapes into the jug");
		player.getCarriedItems().remove(new Item(item1.getCatalogId()));
		player.getCarriedItems().remove(new Item(item2.getCatalogId()));
		delay(5);
		if (Formulae.calcProductionSuccessfulLegacy(70, player.getSkills().getLevel(Skill.COOKING.id()), true, 105)) {
			player.playerServerMessage(MessageType.QUEST, "You make some powerful wine");
			player.getCarriedItems().getInventory().add(new Item(resultWineId));
			player.incExp(Skill.COOKING.id(), 550, true);
		} else {
			player.playerServerMessage(MessageType.QUEST, "You accidentally make some bad wine");
			player.getCarriedItems().getInventory().add(new Item(ItemId.BAD_OR_UNFERMENTED_WINE.id()));
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchPowerfulWineMaking(player, item1, item2, resultWineId);
		}
	}

	private void batchPower(Player player, Item grapes, int poweredGrapesId, String processString) {
		grapes = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(grapes.getCatalogId(), Optional.of(false)));

		if (grapes == null) return;

		player.message(processString);
		player.getCarriedItems().remove(grapes);
		boolean sendUpdate = player.getClientLimitations().supportsSkillUpdate;
		player.getSkills().setLevel(Skill.PRAYER.id(), player.getSkills().getLevel(Skill.PRAYER.id()) - 1, sendUpdate);
		if (!sendUpdate) {
			player.getSkills().sendUpdateAll();
		}
		give(player, poweredGrapesId, 1);
		delay();

		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchPower(player, grapes, poweredGrapesId, processString);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.GRAPES.id(), ItemId.HOLY_SYMBOL_OF_SARADOMIN.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.GRAPES.id(), ItemId.UNHOLY_SYMBOL_OF_ZAMORAK.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.GRAPES_OF_SARADOMIN.id(), ItemId.JUG_OF_WATER.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.GRAPES_OF_ZAMORAK.id(), ItemId.JUG_OF_WATER.id()))
			return true;
		return false;
	}
}
