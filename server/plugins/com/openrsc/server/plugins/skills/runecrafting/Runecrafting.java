package com.openrsc.server.plugins.skills.runecrafting;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.external.ObjectRunecraftingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Runecrafting implements OpLocTrigger, UseLocTrigger {


	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() < 1190 || obj.getID() > 1213)
			return false;

		final ObjectRunecraftingDef def = player.getWorld().getServer().getEntityHandler().getObjectRunecraftingDef(obj.getID());

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

		final ObjectRunecraftingDef def = player.getWorld().getServer().getEntityHandler().getObjectRunecraftingDef(obj.getID());

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

			if (!player.getCarriedItems().hasCatalogID(ItemId.RUNE_ESSENCE.id(), Optional.of(false))){
				player.message("You have no rune essence to bind.");
				return;
			}
			else {
				if (player.getSkills().getLevel(Skills.RUNECRAFTING) < def.getRequiredLvl()) {
					player.message("You require more skill to use this altar.");
					return;
				}
				player.message("You bind the temple's power into " + def.getRuneName() + " runes.");
			}
			int successCount = 0;
			int repeatTimes = player.getCarriedItems().getInventory().countId(ItemId.RUNE_ESSENCE.id(), Optional.of(false));
			for (int loop = 0; loop < repeatTimes; ++loop) {
				Item i = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(ItemId.RUNE_ESSENCE.id(), Optional.of(false)));
				if (i == null) break;
				player.getCarriedItems().remove(i);
				player.getCarriedItems().getInventory().add(new Item(def.getRuneId(), getRuneMultiplier(player, def.getRuneId())));
				++successCount;
			}
			System.out.println(def.getExp() + " " + successCount);
			player.incExp(Skills.RUNECRAFTING, def.getExp() * successCount, true);
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

	public int getRuneMultiplier(Player player, int runeId) {
		int retVal = 1;

		switch(ItemId.getById(runeId)) {
			case AIR_RUNE:
				retVal =  (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFTING)/11.0)+1;
				if (retVal > 10)
					retVal = 10;
				break;
			case MIND_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFTING)/14.0)+1;
				if (retVal > 8)
					retVal = 8;
				break;
			case WATER_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFTING)/19.0)+1;
				if (retVal > 6)
					retVal = 6;
				break;
			case EARTH_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFTING)/26.0)+1;
				if (retVal > 4)
					retVal = 4;
				break;
			case FIRE_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFTING)/35.0)+1;
				if (retVal > 3)
					retVal = 3;
				break;
			case BODY_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skills.RUNECRAFTING)/46.0)+1;
				if (retVal > 2)
					retVal = 2;
				break;
			case COSMIC_RUNE:
				retVal = getCurrentLevel(player,Skills.RUNECRAFTING) >= 59 ? 2 : 1;
				break;
			case CHAOS_RUNE:
				retVal = getCurrentLevel(player,Skills.RUNECRAFTING) >= 74 ? 2 : 1;
				break;
			case NATURE_RUNE:
				retVal = getCurrentLevel(player, Skills.RUNECRAFTING) >= 91 ? 2 : 1;
				break;
		}

		return retVal;
	}
}
