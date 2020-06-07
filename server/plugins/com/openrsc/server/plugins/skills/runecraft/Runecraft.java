package com.openrsc.server.plugins.skills.runecraft;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.external.ObjectRunecraftDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.HashMap;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Runecraft implements OpLocTrigger, UseLocTrigger, UseInvTrigger {

	int[] RUNES = new int[] {
		ItemId.FIRE_RUNE.id(),
		ItemId.WATER_RUNE.id(),
		ItemId.AIR_RUNE.id(),
		ItemId.EARTH_RUNE.id(),
		ItemId.MIND_RUNE.id(),
		ItemId.BODY_RUNE.id(),
		ItemId.CHAOS_RUNE.id(),
		ItemId.COSMIC_RUNE.id(),
		ItemId.NATURE_RUNE.id(),
		// TODO
		// ItemId.LAW_RUNE.id(),
		// ItemId.DEATH_RUNE.id(),
		// ItemId.BLOOD_RUNE.id()
	};

	HashMap<Integer, Integer> talismanIds = new HashMap<Integer, Integer>() {{
		put(ItemId.FIRE_RUNE.id(), ItemId.FIRE_TALISMAN.id());
		put(ItemId.WATER_RUNE.id(), ItemId.WATER_TALISMAN.id());
		put(ItemId.AIR_RUNE.id(), ItemId.AIR_TALISMAN.id());
		put(ItemId.EARTH_RUNE.id(), ItemId.EARTH_TALISMAN.id());
		put(ItemId.MIND_RUNE.id(), ItemId.MIND_TALISMAN.id());
		put(ItemId.BODY_RUNE.id(), ItemId.BODY_TALISMAN.id());
		put(ItemId.COSMIC_RUNE.id(), ItemId.COSMIC_TALISMAN.id());
		put(ItemId.CHAOS_RUNE.id(), ItemId.CHAOS_TALISMAN.id());
		put(ItemId.NATURE_RUNE.id(), ItemId.NATURE_TALISMAN.id());
		// TODO
		// put(ItemId.LAW_RUNE.id(), ItemId.LAW_TALISMAN.id());
		// put(ItemId.DEATH_RUNE.id(), ItemId.DEATH_TALISMAN.id());
		// put(ItemId.BLOOD_RUNE.id(), ItemId.BLOOD_TALISMAN.id());
	}};

	int LEVEL_INDEX = 0;
	int EXP_INDEX = 1;
	HashMap<Integer, int[]> talismanInformation = new HashMap<Integer, int[]>() {{
		put(ItemId.AIR_TALISMAN.id(), new int[]{1, 16});
		put(ItemId.MIND_TALISMAN.id(), new int[]{2, 18});
		put(ItemId.WATER_TALISMAN.id(), new int[]{5, 20});
		put(ItemId.EARTH_TALISMAN.id(), new int[]{9, 22});
		put(ItemId.FIRE_TALISMAN.id(), new int[]{14, 24});
		put(ItemId.BODY_TALISMAN.id(), new int[]{20, 26});
		put(ItemId.COSMIC_TALISMAN.id(), new int[]{27, 28});
		put(ItemId.CHAOS_TALISMAN.id(), new int[]{35, 30});
		put(ItemId.NATURE_TALISMAN.id(), new int[]{44, 32});
		// TODO
		// put(ItemId.LAW_TALISMAN.id(), new int[]{54, 34});
		// put(ItemId.DEATH_TALISMAN.id(), new int[]{65, 36});
		// put(ItemId.BLOOD_TALISMAN.id(), new int[]{77, 38});
	}};

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() < 1190 || obj.getID() > 1213)
			return false;

		final ObjectRunecraftDef def = player.getWorld().getServer().getEntityHandler().getObjectRunecraftDef(obj.getID());

		if (def != null) {
			switch (ItemId.getById(def.getRuneId()))
			{
				case AIR_RUNE:
				case MIND_RUNE:
				case WATER_RUNE:
				case EARTH_RUNE:
				case FIRE_RUNE:
				case BODY_RUNE:
				case COSMIC_RUNE:
				case CHAOS_RUNE:
				case NATURE_RUNE:
				case LAW_RUNE:
				case DEATH_RUNE:
				case BLOOD_RUNE:
					return true;
				default:
					return false;
			}
		} else {
			if (command.equalsIgnoreCase("enter"))
				return true;
		}
		return false;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {

		final ObjectRunecraftDef def = player.getWorld().getServer().getEntityHandler().getObjectRunecraftDef(obj.getID());

		if (command.equalsIgnoreCase("enter")) {
			int requiredTalisman = -1;
			switch (obj.getID()) {
				case 1190:
					requiredTalisman = ItemId.AIR_TALISMAN.id();
					break;
				case 1192:
					requiredTalisman = ItemId.MIND_TALISMAN.id();
					break;
				case 1194:
					requiredTalisman = ItemId.WATER_TALISMAN.id();
					break;
				case 1196:
					requiredTalisman = ItemId.EARTH_TALISMAN.id();
					break;
				case 1198:
					requiredTalisman = ItemId.FIRE_TALISMAN.id();
					break;
				case 1200:
					requiredTalisman = ItemId.BODY_TALISMAN.id();
					break;
				case 1202:
					requiredTalisman = ItemId.COSMIC_TALISMAN.id();
					break;
				case 1204:
					requiredTalisman = ItemId.CHAOS_TALISMAN.id();
					break;
				case 1206:
					requiredTalisman = ItemId.NATURE_TALISMAN.id();
					break;
				case 1208:
					requiredTalisman = ItemId.LAW_TALISMAN.id();
					break;
				case 1210:
					requiredTalisman = ItemId.DEATH_TALISMAN.id();
					break;
				case 1212:
					requiredTalisman = ItemId.BLOOD_TALISMAN.id();
					break;
			}

			if (player.getCarriedItems().hasCatalogID(requiredTalisman, Optional.of(false))) {
				this.onUseLoc(player, obj, new Item(requiredTalisman, 1));
			} else {
				player.message("You can't enter this place");
			}
		} else if (def != null) {
			if (player.getQuestStage(Quests.RUNE_MYSTERIES) != -1)
			{
				player.message("You need to complete Rune Mysteries first. How did you get here?");
				return;
			}

			if (!player.getCarriedItems().hasCatalogID(ItemId.RUNE_STONE.id(), Optional.of(false))){
				player.message("You have no rune essence to bind.");
				return;
			}
			else {
				if (player.getSkills().getLevel(Skills.RUNECRAFT) < def.getRequiredLvl()) {
					player.message("You require more skill to use this altar.");
					return;
				}
				player.message("You bind the temple's power into " + def.getRuneName() + " runes.");
			}
			int successCount = 0;
			int repeatTimes = player.getCarriedItems().getInventory().countId(ItemId.RUNE_STONE.id(), Optional.of(false));
			for (int loop = 0; loop < repeatTimes; ++loop) {
				Item i = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(ItemId.RUNE_STONE.id(), Optional.of(false)));
				if (i == null) break;
				player.getCarriedItems().remove(i);
				player.getCarriedItems().getInventory().add(new Item(def.getRuneId(), getRuneMultiplier(player, def.getRuneId())));
				++successCount;
			}
			player.incExp(Skills.RUNECRAFT, def.getExp() * successCount, true);
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {

		if (item.getCatalogId() == ItemId.AIR_TALISMAN.id() && obj.getID() == 1190)
			return true;
		else if (item.getCatalogId() == ItemId.MIND_TALISMAN.id() && obj.getID() == 1192)
			return true;
		else if (item.getCatalogId() == ItemId.WATER_TALISMAN.id() && obj.getID() == 1194)
			return true;
		else if (item.getCatalogId() == ItemId.EARTH_TALISMAN.id() && obj.getID() == 1196)
			return true;
		else if (item.getCatalogId() == ItemId.FIRE_TALISMAN.id() && obj.getID() == 1198)
			return true;
		else if (item.getCatalogId() == ItemId.BODY_TALISMAN.id() && obj.getID() == 1200)
			return true;
		else if (item.getCatalogId() == ItemId.COSMIC_TALISMAN.id() && obj.getID() == 1202)
			return true;
		else if (item.getCatalogId() == ItemId.CHAOS_TALISMAN.id() && obj.getID() == 1204)
			return true;
		else if (item.getCatalogId() == ItemId.NATURE_TALISMAN.id() && obj.getID() == 1206)
			return true;
		else if (item.getCatalogId() == ItemId.LAW_TALISMAN.id() && obj.getID() == 1208)
			return true;
		else if (item.getCatalogId() == ItemId.DEATH_TALISMAN.id() && obj.getID() == 1210)
			return true;
		else if (item.getCatalogId() == ItemId.BLOOD_TALISMAN.id() && obj.getID() == 1212)
			return true;
		return false;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {

		if (player.getQuestStage(Quests.RUNE_MYSTERIES) != -1)
		{
			player.message("You need to complete Rune Mysteries first.");
			return;
		}
		player.message("You feel a powerful force take hold of you...");
		delay(config().GAME_TICK);

		switch(ItemId.getById(item.getCatalogId()))
		{
			case AIR_TALISMAN:
				player.teleport(985,19,false);
				break;
			case MIND_TALISMAN:
				player.teleport(934,14,false);
				break;
			case WATER_TALISMAN:
				player.teleport(986,63,false);
				break;
			case EARTH_TALISMAN:
				player.teleport(934,70,false);
				break;
			case FIRE_TALISMAN:
				player.teleport(887,26,false);
				break;
			case BODY_TALISMAN:
				player.teleport(893,71,false);
				break;
			case COSMIC_TALISMAN:
				player.teleport(839,26,false);
				break;
			case CHAOS_TALISMAN:
				player.teleport(826,90,false);
				break;
			case NATURE_TALISMAN:
				player.teleport(787,29,false);
				break;
			case LAW_TALISMAN:
				player.teleport(790,69,false);
				break;
			case DEATH_TALISMAN:
				player.teleport(934,14,false);
				break;
			case BLOOD_TALISMAN:
				player.teleport(743,22,false);
				break;
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		boolean chisel = item1.getCatalogId() == ItemId.CHISEL.id() || item2.getCatalogId() == ItemId.CHISEL.id();
		boolean runestone = item1.getCatalogId() == ItemId.RUNE_STONE.id() || item2.getCatalogId() == ItemId.RUNE_STONE.id();
		boolean rune = DataConversions.inArray(RUNES, item1.getCatalogId()) || DataConversions.inArray(RUNES, item2.getCatalogId());
		boolean talisman = item1.getCatalogId() == ItemId.UNCHARGED_TALISMAN.id() || item2.getCatalogId() == ItemId.UNCHARGED_TALISMAN.id();
		return (chisel && runestone) || (rune && talisman);
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		boolean chisel = item1.getCatalogId() == ItemId.CHISEL.id() || item2.getCatalogId() == ItemId.CHISEL.id();
		boolean runestone = item1.getCatalogId() == ItemId.RUNE_STONE.id() || item2.getCatalogId() == ItemId.RUNE_STONE.id();
		if (chisel && runestone) {
			chiselTalisman(player);
			return;
		}

		boolean rune = DataConversions.inArray(RUNES, item1.getCatalogId()) || DataConversions.inArray(RUNES, item2.getCatalogId());
		boolean talisman = item1.getCatalogId() == ItemId.UNCHARGED_TALISMAN.id() || item2.getCatalogId() == ItemId.UNCHARGED_TALISMAN.id();
		if (rune && talisman) {
			if (item1.getCatalogId() == ItemId.UNCHARGED_TALISMAN.id()) {
				imbueTalisman(player, item2);
			}
			else {
				imbueTalisman(player, item1);
			}
		}
	}

	private void chiselTalisman(Player player) {
		Item chisel = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(ItemId.CHISEL.id(), Optional.of(false))
		);
		if (chisel == null) return;

		int repeat = player.getCarriedItems().getInventory().countId(ItemId.RUNE_STONE.id(), Optional.of(false));
		if (repeat <= 0) return;
		startbatch(repeat);
		batchChisel(player, chisel);
	}

	private void batchChisel(Player player, Item chisel) {
		if (checkFatigued(player)) return;

		Item runestone = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(ItemId.RUNE_STONE.id(), Optional.of(false))
		);
		if (runestone == null) return;

		thinkbubble(chisel);
		player.getCarriedItems().remove(runestone);
		delay(config().GAME_TICK);
		player.message("You chisel the rune stone into a talisman.");
		player.getCarriedItems().getInventory().add(new Item(ItemId.UNCHARGED_TALISMAN.id()));
		player.incExp(Skills.CRAFTING, 20, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !ifbatchcompleted()) {
			delay(config().GAME_TICK);
			batchChisel(player, chisel);
		}
	}

	private void imbueTalisman(Player player, Item rune) {
		int repeat = player.getCarriedItems().getInventory().countId(ItemId.UNCHARGED_TALISMAN.id(), Optional.of(false));
		if (repeat <= 0) return;
		startbatch(repeat);
		batchImbue(player, rune);
	}

	private void batchImbue(Player player, Item rune) {
		if (checkFatigued(player)) return;

		int talismanId = talismanIds.getOrDefault(rune.getCatalogId(), -1);
		if (talismanId <= 0) return;

		if (player.getLevel(Skills.RUNECRAFT) < talismanInformation.get(talismanId)[LEVEL_INDEX]) {
			mes("You must be at least level " + talismanInformation.get(talismanId)[LEVEL_INDEX] + " to imbue that");
			return;
		}

		Item talisman = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(ItemId.UNCHARGED_TALISMAN.id(), Optional.of(false))
		);
		rune = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(rune.getCatalogId(), Optional.of(false))
		);
		if (talisman == null || rune == null) return;

		if (rune.getAmount() < 10) { // 10 runes to imbue a talisman.
			mes("You do not have enough runes to imbue that talisman!");
			return;
		}

		Item imbued = new Item(talismanId);

		thinkbubble(talisman);
		player.getCarriedItems().remove(talisman);
		player.getCarriedItems().remove(new Item(rune.getCatalogId(), 10));
		delay(config().GAME_TICK);
		player.getCarriedItems().getInventory().add(imbued);
		player.incExp(Skills.RUNECRAFT, talismanInformation.get(talismanId)[EXP_INDEX], true);
		player.message("You imbue the uncharged talisman and create a " + imbued.getDef(player.getWorld()).getName());

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !ifbatchcompleted()) {
			delay(config().GAME_TICK);
			batchImbue(player, rune);
		}
	}

	private boolean checkFatigued(Player player) {
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too fatigued to do that.");
				return true;
			}
		}
		return false;
	}

	public int getRuneMultiplier(Player player, int runeId) {
		int retVal = 1;

		switch(ItemId.getById(runeId)) {
			case AIR_RUNE:
				retVal =  (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFT)/11.0)+1;
				if (retVal > 10)
					retVal = 10;
				break;
			case MIND_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFT)/14.0)+1;
				if (retVal > 8)
					retVal = 8;
				break;
			case WATER_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFT)/19.0)+1;
				if (retVal > 6)
					retVal = 6;
				break;
			case EARTH_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFT)/26.0)+1;
				if (retVal > 4)
					retVal = 4;
				break;
			case FIRE_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFT)/35.0)+1;
				if (retVal > 3)
					retVal = 3;
				break;
			case BODY_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFT)/46.0)+1;
				if (retVal > 2)
					retVal = 2;
				break;
			case COSMIC_RUNE:
				retVal = getCurrentLevel(player,Skills.RUNECRAFT) >= 59 ? 2 : 1;
				break;
			case CHAOS_RUNE:
				retVal = getCurrentLevel(player,Skills.RUNECRAFT) >= 74 ? 2 : 1;
				break;
			case NATURE_RUNE:
				retVal = getCurrentLevel(player, Skills.RUNECRAFT) >= 91 ? 2 : 1;
				break;
		}

		return retVal;
	}
}
