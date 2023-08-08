package com.openrsc.server.plugins.custom.skills.runecraft;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.external.ObjectRunecraftDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
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

	int[] TALISMANS = {
		ItemId.AIR_TALISMAN.id(),
		ItemId.MIND_TALISMAN.id(),
		ItemId.WATER_TALISMAN.id(),
		ItemId.EARTH_TALISMAN.id(),
		ItemId.FIRE_TALISMAN.id(),
		ItemId.BODY_TALISMAN.id(),
		ItemId.COSMIC_TALISMAN.id(),
		ItemId.CHAOS_TALISMAN.id(),
		ItemId.NATURE_TALISMAN.id(),
		ItemId.LAW_TALISMAN.id(),
		ItemId.DEATH_TALISMAN.id(),
		ItemId.BLOOD_TALISMAN.id(),
	};

	int[] CURSED_TALISMANS = {
		ItemId.CURSED_AIR_TALISMAN.id(),
		ItemId.CURSED_MIND_TALISMAN.id(),
		ItemId.CURSED_WATER_TALISMAN.id(),
		ItemId.CURSED_EARTH_TALISMAN.id(),
		ItemId.CURSED_FIRE_TALISMAN.id(),
		ItemId.CURSED_BODY_TALISMAN.id(),
		ItemId.CURSED_COSMIC_TALISMAN.id(),
		ItemId.CURSED_CHAOS_TALISMAN.id(),
		ItemId.CURSED_NATURE_TALISMAN.id(),
		ItemId.CURSED_LAW_TALISMAN.id(),
		ItemId.CURSED_DEATH_TALISMAN.id(),
		ItemId.CURSED_BLOOD_TALISMAN.id(),
	};

	int[] ENFEEBLED_TALISMANS = {
		ItemId.ENFEEBLED_AIR_TALISMAN.id(),
		ItemId.ENFEEBLED_MIND_TALISMAN.id(),
		ItemId.ENFEEBLED_WATER_TALISMAN.id(),
		ItemId.ENFEEBLED_EARTH_TALISMAN.id(),
		ItemId.ENFEEBLED_FIRE_TALISMAN.id(),
		ItemId.ENFEEBLED_BODY_TALISMAN.id(),
		ItemId.ENFEEBLED_COSMIC_TALISMAN.id(),
		ItemId.ENFEEBLED_CHAOS_TALISMAN.id(),
		ItemId.ENFEEBLED_NATURE_TALISMAN.id(),
		ItemId.ENFEEBLED_LAW_TALISMAN.id(),
		ItemId.ENFEEBLED_DEATH_TALISMAN.id(),
		ItemId.ENFEEBLED_BLOOD_TALISMAN.id(),
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

	final int AIR_ALTAR = 1190;
	final int MIND_ALTAR = 1192;
	final int WATER_ALTAR = 1194;
	final int EARTH_ALTAR = 1196;
	final int FIRE_ALTAR = 1198;
	final int BODY_ALTAR = 1200;
	final int COSMIC_ALTAR = 1202;
	final int CHAOS_ALTAR = 1204;
	final int NATURE_ALTAR = 1206;
	final int LAW_ALTAR = 1208;
	final int DEATH_ALTAR = 1210;
	final int BLOOD_ALTAR = 1212;
	HashMap<Integer, int[]> altarTalismans = new HashMap<Integer, int[]>() {{
		put(AIR_ALTAR, new int[]{
			ItemId.AIR_TALISMAN.id(),
			ItemId.CURSED_AIR_TALISMAN.id(),
			ItemId.ENFEEBLED_AIR_TALISMAN.id()});
		put(MIND_ALTAR, new int[]{
			ItemId.MIND_TALISMAN.id(),
			ItemId.CURSED_MIND_TALISMAN.id(),
			ItemId.ENFEEBLED_MIND_TALISMAN.id()});
		put(WATER_ALTAR, new int[]{
			ItemId.WATER_TALISMAN.id(),
			ItemId.CURSED_WATER_TALISMAN.id(),
			ItemId.ENFEEBLED_WATER_TALISMAN.id()});
		put(EARTH_ALTAR, new int[]{
			ItemId.EARTH_TALISMAN.id(),
			ItemId.CURSED_EARTH_TALISMAN.id(),
			ItemId.ENFEEBLED_EARTH_TALISMAN.id()});
		put(FIRE_ALTAR, new int[]{
			ItemId.FIRE_TALISMAN.id(),
			ItemId.CURSED_FIRE_TALISMAN.id(),
			ItemId.ENFEEBLED_FIRE_TALISMAN.id()});
		put(BODY_ALTAR, new int[]{
			ItemId.BODY_TALISMAN.id(),
			ItemId.CURSED_BODY_TALISMAN.id(),
			ItemId.ENFEEBLED_BODY_TALISMAN.id()});
		put(COSMIC_ALTAR, new int[]{
			ItemId.COSMIC_TALISMAN.id(),
			ItemId.CURSED_COSMIC_TALISMAN.id(),
			ItemId.ENFEEBLED_COSMIC_TALISMAN.id()});
		put(CHAOS_ALTAR, new int[]{
			ItemId.CHAOS_TALISMAN.id(),
			ItemId.CURSED_CHAOS_TALISMAN.id(),
			ItemId.ENFEEBLED_CHAOS_TALISMAN.id()});
		put(NATURE_ALTAR, new int[]{
			ItemId.NATURE_TALISMAN.id(),
			ItemId.CURSED_NATURE_TALISMAN.id(),
			ItemId.ENFEEBLED_NATURE_TALISMAN.id()});
		put(LAW_ALTAR, new int[]{
			ItemId.LAW_TALISMAN.id(),
			ItemId.CURSED_LAW_TALISMAN.id(),
			ItemId.ENFEEBLED_LAW_TALISMAN.id()});
		put(DEATH_ALTAR, new int[]{
			ItemId.DEATH_TALISMAN.id(),
			ItemId.CURSED_DEATH_TALISMAN.id(),
			ItemId.ENFEEBLED_DEATH_TALISMAN.id()});
		put(BLOOD_ALTAR, new int[]{
			ItemId.BLOOD_TALISMAN.id(),
			ItemId.CURSED_BLOOD_TALISMAN.id(),
			ItemId.ENFEEBLED_BLOOD_TALISMAN.id()});
	}};

	int[] BIND_ALTARS = {
		1191, // Air
		1193, // Mind
		1195, // Water
		1197, // Earth
		1199, // Fire
		1201, // Body
		1203, // Cosmic
		1205, // Chaos
		1207, // Nature
		1209, // Law
		1211, // Death
		1213, // Blood
	};

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
			int heldTalisman = -1;

			if (altarTalismans.get(obj.getID()) == null) return;

			for (int talismanId : altarTalismans.get(obj.getID())) {
				if (player.getCarriedItems().hasCatalogID(talismanId, Optional.of(false))) {
					heldTalisman = talismanId;
					break;
				}
			}

			if (heldTalisman != -1) {
				this.onUseLoc(player, obj, new Item(heldTalisman, 1));
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
				player.message("You have no rune stones to bind.");
				return;
			}

			// Get the proper talisman type for the altar
			int talismanIndex = -1;
			for (int i = 0; i < BIND_ALTARS.length; i++) {
				if (obj.getID() == BIND_ALTARS[i]) {
					talismanIndex = i;
					break;
				}
			}

			// Check to see if the player has one of the fancy talismans
			int multiplier = 1;
			int levelAdd = 0;
			boolean cursed = false;
			boolean enfeebled = false;
			if (player.getCarriedItems().hasCatalogID(ENFEEBLED_TALISMANS[talismanIndex], Optional.of(false))) {
				multiplier = 5;
				levelAdd = 14;
				enfeebled = true;
			} else if (player.getCarriedItems().hasCatalogID(CURSED_TALISMANS[talismanIndex], Optional.of(false))) {
				multiplier = 2;
				levelAdd = 7;
				cursed = true;
			}

			// Check to see if they have a talisman
			// cursed == enfeebled would mean both are false,
			// thus they are not carrying either special type.
			if (cursed == enfeebled &&
				player.getCarriedItems().getInventory().countId(TALISMANS[talismanIndex], Optional.of(false)) <= 0) {

				player.message("You need a talisman to use the power of this altar");
				return;
			}

			if (player.getSkills().getLevel(Skill.RUNECRAFT.id()) < def.getRequiredLvl()) {
				player.message("You require more skill to use this altar.");
				return;
			}

			if (cursed || enfeebled) {
				if (player.getSkills().getLevel(Skill.RUNECRAFT.id()) < def.getRequiredLvl() + levelAdd) {
					player.message("You require more skill to use this talisman with this altar.");
					return;
				}
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.CROWN_OF_THE_ARTISAN.id())) {
					player.message("As you attempt to bind the temple's power into " + def.getRuneName() + " runes");
					delay(3);
					player.message("You feel a conflict between the magic of your crown and talisman");
					return;
				}
			}

			player.message("You bind the temple's power into " + def.getRuneName() + " runes.");

			int successCount = 0;
			int repeatTimes = player.getCarriedItems().getInventory().countId(ItemId.RUNE_STONE.id(), Optional.of(false));
			for (int loop = 0; loop < repeatTimes; ++loop) {
				Item i = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(ItemId.RUNE_STONE.id(), Optional.of(false)));
				if (i == null) break;
				player.getCarriedItems().remove(i);

				// Don't get any runes if they have an enfeebled talisman
				if (!enfeebled) {
					player.getCarriedItems().getInventory().add(new Item(def.getRuneId(), getRuneMultiplier(player, def.getRuneId())));
				}
				++successCount;
			}

			if (cursed) {
				Item talisman = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(CURSED_TALISMANS[talismanIndex], Optional.of(false))
				);

				// If they try to pull a fast one and not have the talisman,
				// we'll just put the multiplier back to 1.
				if (talisman == null) {
					multiplier = 1;
				} else {
					// Cursed talisman gets destroyed.
					player.getCarriedItems().remove(talisman);
					player.message("Your talisman crumbles to dust");
				}
			} else if (enfeebled) {
				Item talisman = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(ENFEEBLED_TALISMANS[talismanIndex], Optional.of(false))
				);

				// If they try to pull a fast one and not have the talisman,
				// we'll just put the multiplier back to 1.
				if (talisman == null) {
					multiplier = 1;
				} else {
					// Enfeebled talisman gets destroyed.
					// Player's Runecraft level is lowered by 15%.
					player.getCarriedItems().remove(talisman);
					player.message("The runes crumble to dust");
					delay(3);
					player.message("And your talisman explodes!");
					say(player, "ouch");
					player.message("You feel strange");

					int subtractLevel = (int)Math.round(player.getSkills().getLevel(Skill.RUNECRAFT.id()) * 0.15D);
					boolean sendUpdate = player.getClientLimitations().supportsSkillUpdate;
					player.getSkills().setLevel(Skill.RUNECRAFT.id(),
						(player.getSkills().getLevel(Skill.RUNECRAFT.id()) - subtractLevel), sendUpdate);
					player.damage(3);
					if (!sendUpdate) {
						player.getSkills().sendUpdateAll();
					}
				}
			}

			player.incExp(Skill.RUNECRAFT.id(), def.getExp() * successCount * multiplier, true);
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		if (altarTalismans.get(obj.getID()) == null) return false;

		for (int talismanId : altarTalismans.get(obj.getID())) {
			if (item.getCatalogId() == talismanId) return true;
		}
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
		delay();

		switch(obj.getID())
		{
			case AIR_ALTAR:
				player.teleport(985,19,false);
				break;
			case MIND_ALTAR:
				player.teleport(934,14,false);
				break;
			case WATER_ALTAR:
				player.teleport(986,63,false);
				break;
			case EARTH_ALTAR:
				player.teleport(934,70,false);
				break;
			case FIRE_ALTAR:
				player.teleport(887,26,false);
				break;
			case BODY_ALTAR:
				player.teleport(893,71,false);
				break;
			case COSMIC_ALTAR:
				player.teleport(839,26,false);
				break;
			case CHAOS_ALTAR:
				player.teleport(826,90,false);
				break;
			case NATURE_ALTAR:
				player.teleport(787,29,false);
				break;
			case LAW_ALTAR:
				player.teleport(790,69,false);
				break;
			case DEATH_ALTAR:
				player.teleport(934,14,false);
				break;
			case BLOOD_ALTAR:
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
		delay();
		player.message("You chisel the rune stone into a talisman.");
		player.getCarriedItems().getInventory().add(new Item(ItemId.UNCHARGED_TALISMAN.id()));
		player.incExp(Skill.CRAFTING.id(), 20, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
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

		if (player.getLevel(Skill.RUNECRAFT.id()) < talismanInformation.get(talismanId)[LEVEL_INDEX]) {
			mes("You must be at least level " + talismanInformation.get(talismanId)[LEVEL_INDEX] + " to imbue that");
			delay(3);
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
			delay(3);
			return;
		}

		Item imbued = new Item(talismanId);

		thinkbubble(talisman);
		player.getCarriedItems().remove(talisman);
		player.getCarriedItems().remove(new Item(rune.getCatalogId(), 10));
		delay();
		player.getCarriedItems().getInventory().add(imbued);
		player.incExp(Skill.RUNECRAFT.id(), talismanInformation.get(talismanId)[EXP_INDEX], true);
		player.message("You imbue the uncharged talisman and create a " + imbued.getDef(player.getWorld()).getName());

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
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
				retVal =  (int)Math.floor(getCurrentLevel(player, Skill.RUNECRAFT.id())/11.0)+1;
				if (retVal > 10)
					retVal = 10;
				break;
			case MIND_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skill.RUNECRAFT.id())/14.0)+1;
				if (retVal > 8)
					retVal = 8;
				break;
			case WATER_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skill.RUNECRAFT.id())/19.0)+1;
				if (retVal > 6)
					retVal = 6;
				break;
			case EARTH_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skill.RUNECRAFT.id())/26.0)+1;
				if (retVal > 4)
					retVal = 4;
				break;
			case FIRE_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skill.RUNECRAFT.id())/35.0)+1;
				if (retVal > 3)
					retVal = 3;
				break;
			case BODY_RUNE:
				retVal = (int)Math.floor(getCurrentLevel(player, Skill.RUNECRAFT.id())/46.0)+1;
				if (retVal > 3)
					retVal = 3;
				break;
			case COSMIC_RUNE:
				retVal = getCurrentLevel(player,Skill.RUNECRAFT.id()) >= 59 ? 2 : 1;
				break;
			case CHAOS_RUNE:
				retVal = getCurrentLevel(player,Skill.RUNECRAFT.id()) >= 74 ? 2 : 1;
				break;
			case NATURE_RUNE:
				retVal = getCurrentLevel(player, Skill.RUNECRAFT.id()) >= 91 ? 2 : 1;
				break;
		}

		return retVal;
	}
}
