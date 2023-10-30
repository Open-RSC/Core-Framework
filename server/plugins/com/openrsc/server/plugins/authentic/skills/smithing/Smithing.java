package com.openrsc.server.plugins.authentic.skills.smithing;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.external.ItemSmithingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.custom.minigames.ABoneToPick;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MathUtil;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Smithing implements UseLocTrigger {

	private final int DORICS_ANVIL = 177;
	private final int ANVIL = 50;
	private final int LAVA_ANVIL = 1285;

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == ANVIL
			|| obj.getID() == DORICS_ANVIL
			|| obj.getID() == LAVA_ANVIL;
	}

	@Override
	public void onUseLoc(final Player player, GameObject obj, final Item item) {
		if ((obj.getID() == DORICS_ANVIL || obj.getID() == ANVIL)
			&& item.getCatalogId() == ItemId.ALUMINIUM_BAR.id()) {
			ABoneToPick.makeAluminiumCog(player);
			return;
		}

		if (obj.getID() == LAVA_ANVIL) {
			if (player.getCache().hasKey("miniquest_dwarf_youth_rescue")
			&& player.getCache().getInt("miniquest_dwarf_youth_rescue") == 2) {
				if (item.getCatalogId() == ItemId.DRAGON_BAR.id()) {
					if (!player.getCarriedItems().getInventory().hasInInventory(ItemId.HAMMER.id()))
					{
						player.message("You need a hammer to do that");
						return;
					}
					if (getCurrentLevel(player, Skill.SMITHING.id()) < 90) {
						player.message("You need 90 smithing to work dragon metal");
						return;
					}
					if (player.getCarriedItems().remove(new Item(ItemId.DRAGON_BAR.id())) > -1) {
						give(player, ItemId.DRAGON_METAL_CHAIN.id(), 50);
						player.incExp(Skill.SMITHING.id(), 1000, true);
					}
				} else
					player.message("Nothing interesting happens");
			}
			return;
		}
		// Doric's Anvil
		if (obj.getID() == DORICS_ANVIL && !allowDorics(player)) return;

		if (!smithingChecks(obj, item, player)) return;

		beginSmithing(item, player);
	}

	private boolean allowDorics(Player player) {
		if (player.getQuestStage(Quests.DORICS_QUEST) > -1) {
			Npc doric = ifnearvisnpc(player, NpcId.DORIC.id(), 20);
			if (doric != null) {
				npcsay(player, doric, "Heh who said you could use that?");
			}
			//message likely not given out, see https://classic.runescape.wiki/w/Transcript:Doric?diff=79647&oldid=79230
			//player.message("You need to finish Doric's quest to use this anvil");
			return false;
		}
		return true;
	}

	private boolean smithingChecks(final GameObject obj, final Item item, final Player player) {

		// Not an anvil or Doric's Anvil...
		if (!(obj.getID() == 50 || obj.getID() == 177)) return false;

		if (!player.withinRange(obj, 1)) {
			return false;
		}

		// Using hammer with anvil.
		if (item.getCatalogId() == ItemId.HAMMER.id()) {
			player.message("To forge items use the metal you wish to work with the anvil");
			return false;
		}

		int minSmithingLevel = minSmithingLevel(item.getCatalogId());

		// Special Dragon Square Shield Case
		if (minSmithingLevel < 0 && item.getCatalogId() != ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id() && item.getCatalogId() != ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id()) {
			player.message("Nothing interesting happens");
			return false;
		}

		int maxItemId = player.getConfig().RESTRICT_ITEM_ID;
		boolean worldSupportsGoldSmithing = !player.getConfig().LACKS_GOLD_SMITHING && MathUtil.maxUnsigned(maxItemId, ItemId.GOLDEN_BOWL.id()) == maxItemId;
		if (item.getCatalogId() == ItemId.GOLD_BAR.id() && !worldSupportsGoldSmithing) {
			player.message("Nothing interesting happens");
			return false;
		}

		if (player.getSkills().getLevel(Skill.SMITHING.id()) < minSmithingLevel) {
			if (item.getCatalogId() != ItemId.GOLD_BAR.id()) {
				player.message("You need at least level "
					+ minSmithingLevel + " smithing to work with "
					+ item.getDef(player.getWorld()).getName().toLowerCase().replaceAll("bar", ""));
			} else {
				// not entirely sure should give message here or this one is once Legends started
				// on OSRS the advice is to try to use furnace
				// Logg tested before legends but with level past 50 and was "You're not quite sure what to make from the gold.."
				player.message("You need at least level 50 smithing to work gold...");
			}
			return false;
		}

		if (player.getCarriedItems().getInventory().countId(ItemId.HAMMER.id(), Optional.of(false)) < 1) {
			player.playerServerMessage(MessageType.QUEST, "You need a hammer to work the metal with.");
			return false;
		}

		return true;
	}

	private void beginSmithing(final Item item, final Player player) {

		// Combining Dragon Square Shield Halves
		if (item.getCatalogId() == ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id() || item.getCatalogId() == ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id()) {
			attemptDragonSquareCombine(item, player);
			return;
		}

		int maxItemId = player.getConfig().RESTRICT_ITEM_ID;
		boolean worldSupportsGoldSmithing = !player.getConfig().LACKS_GOLD_SMITHING && MathUtil.maxUnsigned(maxItemId, ItemId.GOLDEN_BOWL.id()) == maxItemId;
		// Failure to make a gold bowl without Legend's Quest.
		if (item.getCatalogId() == ItemId.GOLD_BAR.id() && worldSupportsGoldSmithing && player.getQuestStage(Quests.LEGENDS_QUEST) >= 0 && player.getQuestStage(Quests.LEGENDS_QUEST) <= 2) {
			player.message("You're not quite sure what to make from the gold..");
			return;
		}

		// Gold
		if (item.getCatalogId() == ItemId.GOLD_BAR.id()) {
			if (worldSupportsGoldSmithing) {
				player.message("What would you like to make?");
				handleGoldSmithing(player);
			} else {
				player.message("Nothing interesting happens");
			}
			return;
		}

		player.message("What would you like to make?");

		handleSmithing(item, player);

	}

	private void attemptDragonSquareCombine(Item item, Player player) {
		if (player.getSkills().getLevel(Skill.SMITHING.id()) < 60) {
			player.message("You need a smithing ability of at least 60 to complete this task.");
		}
		// non-kosher this message
		else if (player.getCarriedItems().getInventory().countId(ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id(), Optional.of(false)) < 1
				|| player.getCarriedItems().getInventory().countId(ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id(), Optional.of(false)) < 1) {
			player.message("You need the two shield halves to repair the shield.");
		} else {
			mes("You set to work trying to fix the ancient shield.");
			delay(2);
			mes("You hammer long and hard and use all of your skill.");
			delay(2);
			mes("Eventually, it is ready...");
			delay(2);
			mes("You have repaired the Dragon Square Shield.");
			delay(2);
			if (player.getCarriedItems().remove(new Item(ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id()),
				new Item(ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id()))) {
				player.getCarriedItems().getInventory().add(new Item(ItemId.DRAGON_SQUARE_SHIELD.id()));
				player.incExp(Skill.SMITHING.id(), 300, true);
			}
		}
	}

	private void handleGoldSmithing(Player player) {
		int goldOption = multi(player, "Golden bowl.", "Cancel");

		if (goldOption == 1) return;

		if (!config().MEMBER_WORLD) {
			player.message("This feature is members only");
			return;
		}

		if (!canReceive(player, new Item(ItemId.GOLDEN_BOWL.id()))) {
			player.message("Your client does not support the desired object");
			return;
		}

		/*if (player.isBusy()) {
			return;
		}*/
		if (goldOption == 0) {
			if (player.getCarriedItems().getInventory().countId(ItemId.GOLD_BAR.id(), Optional.of(false)) < 2) {
				player.message("You need two bars of gold to make this item.");
				return;
			}

			final int toMake = config().BATCH_PROGRESSION ?
				(player.getCarriedItems().getInventory().countId(ItemId.GOLD_BAR.id(), Optional.of(false)) / 2) : 1;
			startbatch(toMake);
			batchGoldSmithing(player);
		}
	}

	private void batchGoldSmithing(final Player player) {
		if (player.getCarriedItems().getInventory().countId(ItemId.GOLD_BAR.id(), Optional.of(false)) < 2) {
			player.message("You need two bars of gold to make this item.");
			return;
		}

		mes("You hammer the metal...");
		delay(3);
		if (!Formulae.breakGoldenItem(50, player.getSkills().getLevel(Skill.SMITHING.id()))) {
			for (int x = 0; x < 2; x++) {
				player.getCarriedItems().remove(new Item(ItemId.GOLD_BAR.id()));
			}
			player.message("You forge a beautiful bowl made out of solid gold.");
			player.getCarriedItems().getInventory().add(new Item(ItemId.GOLDEN_BOWL.id(), 1));
			player.incExp(Skill.SMITHING.id(), 120, true);
		} else {
			player.message("You make a mistake forging the bowl..");
			delay(3);
			player.message("You pour molten gold all over the floor..");
			player.getCarriedItems().remove(new Item(ItemId.GOLD_BAR.id()));
			player.incExp(Skill.SMITHING.id(), 4, true);
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchGoldSmithing(player);
		}
	}

	private void handleSmithing(final Item item, final Player player) {

		// First Smithing Menu
		int firstType = firstMenu(item, player);
		if (firstType < 0) return;

		/*if (player.isBusy()) {
			return;
		}*/

		// Second Smithing Menu
		int secondType = secondMenu(item, player, firstType);
		if (secondType < 0) return;

		// Distribute to the correct function to make our final choice
		int toMake = chooseItem(player, secondType);

		if (toMake == -1) {
			return;
		}

		final ItemSmithingDef def = player.getWorld().getServer().getEntityHandler().getSmithingDef((getBarType(item.getCatalogId()) * 24) + toMake);

		if (def == null) {
			// No definition found
			player.message("Nothing interesting happens");
			return;
		}

		int makeCount = getCount(def, item, player);

		if (makeCount == -1) return;

		startbatch(makeCount);
		batchSmithing(player, item, def);
	}

	private void batchSmithing(Player player, Item item, ItemSmithingDef def) {
		if (!canReceive(player, new Item(def.getItemID()))) {
			player.message("Your client does not support the desired object");
			return;
		}
		if (player.getSkills().getLevel(Skill.SMITHING.id()) < def.getRequiredLevel()) {
			player.message("You need to be at least level "
				+ def.getRequiredLevel() + " smithing to do that");
			return;
		}
		if (player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false)) < def.getRequiredBars()) {
			player.message("You need " + def.getRequiredBars() + " bars of metal to make this item");
			return;
		}
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to smith");
				return;
			}
		}
		player.playSound("anvil");
		for (int x = 0; x < def.getRequiredBars(); x++) {
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
		}

		thinkbubble(item);
		if (player.getWorld().getServer().getEntityHandler().getItemDef(def.getItemID()).isStackable()) {
			player.playerServerMessage(MessageType.QUEST, "You hammer the metal and make " + def.getAmount() + " "
				+ player.getWorld().getServer().getEntityHandler().getItemDef(def.getItemID()).getName().toLowerCase());
			player.getCarriedItems().getInventory().add(
				new Item(def.getItemID(), def.getAmount()));
		} else {
			player.playerServerMessage(MessageType.QUEST, "You hammer the metal and make a "
				+ player.getWorld().getServer().getEntityHandler().getItemDef(def.getItemID()).getName().toLowerCase());
			for (int x = 0; x < def.getAmount(); x++) {
				player.getCarriedItems().getInventory().add(new Item(def.getItemID(), 1));
			}
		}
		player.incExp(Skill.SMITHING.id(), getSmithingExp(item.getCatalogId(), def.getRequiredBars()), true);
		delay();

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchSmithing(player, item, def);
		}
	}

	private int firstMenu(Item item, Player player) {
		int option;
		ArrayList<String> options = new ArrayList<>();
		int maxItemId = player.getConfig().RESTRICT_ITEM_ID;

		// Steel Bar
		if (item.getCatalogId() == ItemId.STEEL_BAR.id()) {
			options.addAll(Arrays.asList(
				"Make Weapon",
				"Make Armour"
			));
			if (player.getConfig().CAN_FEATURE_MEMBS) {
				options.add("Make Missile Heads");
			}
			if (MathUtil.maxUnsigned(maxItemId, ItemId.NAILS.id()) == maxItemId) {
				options.add("Make Nails");
			}
			options.add("Cancel");
			String[] finalOptions = new String[options.size()];
			option = multi(player, options.toArray(finalOptions));

			// Cancel
			if (option == finalOptions.length - 1) return -1;

			// Missile Heads or Nails
			if (option > 1) {
				if (option == 2 && !options.contains("Make Missile Heads")) {
					// set as nails
					option = 3;
				}
			}

			return option;
		}

		// Bronze Bar
		if (item.getCatalogId() == ItemId.BRONZE_BAR.id()) {
			options.addAll(Arrays.asList(
				"Make Weapon",
				"Make Armour"
			));
			if (player.getConfig().CAN_FEATURE_MEMBS) {
				options.add("Make Missile Heads");
			}
			if (MathUtil.maxUnsigned(maxItemId, ItemId.BRONZE_WIRE.id()) == maxItemId) {
				options.add("Make Craft Item");
			}
			options.add("Cancel");
			String[] finalOptions = new String[options.size()];
			option = multi(player, options.toArray(finalOptions));

			// Cancel
			if (option == finalOptions.length - 1) return -1;

			// Missile Heads or Craft Item
			if (option > 1) {
				if (option == 2 && !options.contains("Make Missile Heads")) {
					// set as nails
					option = 3;
				}
			}

			return option;
		}

		// Any other bar.
		options.addAll(Arrays.asList(
			"Make Weapon",
			"Make Armour"
		));
		if (player.getConfig().CAN_FEATURE_MEMBS) {
			options.add("Make Missile Heads");
		}
		options.add("Cancel");
		String[] finalOptions = new String[options.size()];
		option = multi(player, options.toArray(finalOptions));

		if (option == finalOptions.length - 1) return -1;

		return option;
	}

	private int secondMenu(Item item, Player player, int firstType) {

		int offset = 0;

		ArrayList<String> options = new ArrayList<>();

		// Weapon
		if (firstType == 0) {
			player.message("Choose a type of weapon to make");
			options.add("Dagger");
			if (player.getConfig().CAN_FEATURE_MEMBS) {
				options.add("Throwing Knife");
			}
			options.addAll(Arrays.asList(
				"Sword",
				"Axe",
				"Mace"
			));
			String[] finalOptions = new String[options.size()];
			int option = multi(player, options.toArray(finalOptions));

			if (option > 0 && !options.contains("Throwing Knife")) {
				++option;
			}
			return option;
		}

		offset += 5;

		// Armour
		if (firstType == 1) {
			player.message("Choose a type of armour to make");
			int option = multi(player, "Helmet", "Shield", "Armour");
			// Cancel
			if (option < 0) return -1;

			return offset + option;
		}

		offset += 3;

		if (firstType == 2) {
			if (!config().MEMBER_WORLD) {
				player.message("This feature is members only");
				return -1;
			}

			int option;

			// During or after Tourist Trap
			if (player.getQuestStage(Quests.TOURIST_TRAP) >= 8
				|| player.getQuestStage(Quests.TOURIST_TRAP) == -1) {
				option = multi(player, "Make Arrow Heads.", "Forge Dart Tips.", "Cancel.");

				// Cancel
				if (option == 2) return -1;
			}

			// Before Tourist Trap
			else {
				option = multi(player, "Make Arrow Heads.", "Cancel.");

				// Cancel
				if (option == 2) return -1;
			}

			// Cancel
			if (option < 0) return -1;

			return offset + option;
		}

		if (firstType == 3) {

			// Nails
			if (item.getCatalogId() == ItemId.STEEL_BAR.id()) {
				makeNails(item, player);
			}

			// Bronze Wire
			else if (item.getCatalogId() == ItemId.BRONZE_BAR.id()) {
				makeWire(item, player);
			}
		}

		return -1;
	}

	private void makeNails(Item item, Player player) {
		if (!canReceive(player, new Item(ItemId.NAILS.id()))) {
			player.message("Your client does not support the desired object");
			return;
		}

		if (player.getSkills().getLevel(Skill.SMITHING.id()) < 34) {
			player.message("You need to be at least level 34 smithing to do that");
			return;
		}
		if (player.getCarriedItems().getInventory().countId(ItemId.STEEL_BAR.id(), Optional.of(false)) < 1) {
			player.playerServerMessage(MessageType.QUEST, "You need 1 bar of metal to make this item");
			return;
		}
		thinkbubble(item);
		player.getCarriedItems().remove(new Item(ItemId.STEEL_BAR.id()));
		player.playerServerMessage(MessageType.QUEST, "You hammer the metal and make some nails");
		player.getCarriedItems().getInventory().add(new Item(ItemId.NAILS.id(), 2));
		player.incExp(Skill.SMITHING.id(), 150, true);
	}

	private void makeWire(Item item, Player player) {
		player.message("What sort of craft item do you want to make?");
		int bronzeWireOption = multi(player, "Bronze Wire(1 bar)", "Cancel");

		if (bronzeWireOption == 1) return;

		if (!config().MEMBER_WORLD) {
			player.message("This feature is members only");
			return;
		}

		if (!canReceive(player, new Item(ItemId.BRONZE_WIRE.id()))) {
			player.message("Your client does not support the desired object");
			return;
		}

		/*if (player.isBusy()) {
			return;
		}*/
		if (player.getSkills().getLevel(Skill.SMITHING.id()) < 4) {
			player.message("You need to be at least level 4 smithing to do that");
			return;
		}
		if (player.getCarriedItems().getInventory().countId(ItemId.BRONZE_BAR.id(), Optional.of(false)) < 1) {
			player.playerServerMessage(MessageType.QUEST, "You need 1 bar of metal to make this item");
			return;
		}
		if (bronzeWireOption == 0) {
			thinkbubble(item);
			player.getCarriedItems().remove(new Item(ItemId.BRONZE_BAR.id()));
			player.playerServerMessage(MessageType.QUEST, "You hammer the Bronze Bar and make some bronze wire");
			player.getCarriedItems().getInventory().add(new Item(ItemId.BRONZE_WIRE.id(), 1));
			player.incExp(Skill.SMITHING.id(), 50, true);
		}
	}

	private int chooseItem(Player player, int secondType) {
		// Dagger
		if (secondType == 0) return 0;

			// Throwing Knife
		else if (secondType == 1) {
			if (!config().MEMBER_WORLD) {
				player.message("This feature is members only");
				return -1;
			}
			return 1;
		}

		// Sword
		else if (secondType == 2) return swordChoice(player);

			// Axe
		else if (secondType == 3) return axeChoice(player);

			// Mace
		else if (secondType == 4) return 9;

			// Helmet
		else if (secondType == 5) return helmetChoice(player);

			// Shield
		else if (secondType == 6) return shieldChoice(player);

			// Armour
		else if (secondType == 7) return armourChoice(player);

			// Arrowheads
		else if (secondType == 8) return 18;

		// Dart tips
		else if (secondType == 9) {
			if (player.getQuestStage(Quests.TOURIST_TRAP) >= 8 || player.getQuestStage(Quests.TOURIST_TRAP) == -1) {
				return 20;
			}
		}

		return -1;
	}

	private int swordChoice(Player player) {
		player.message("What sort of sword do you want to make?");
		int option = multi(player, "Short sword",
			"Long sword (2 bars)", "Scimitar (2 bars)",
			"2-handed sword (3 bars)");
		if (option == 0) return 2; // Short Sword
		else if (option == 1) return 3; // Long Sword
		else if (option == 2) return 4; // Scimitar
		else if (option == 3) return 5; // 2-handed Sword
		return -1;
	}

	private int axeChoice(Player player) {
		player.message("What sort of axe do you want to make?");
		int option = multi(player, "Hatchet", "Battle Axe (3 bars)");
		if (option == 0) return 6; // Hatchet
		else if (option == 1) return 8; // Battle Axe
		return -1;
	}

	private int helmetChoice(Player player) {
		player.message("What sort of helmet do you want to make?");
		int option = multi(player, "Medium Helmet",
			"Large Helmet (2 bars)");
		if (option == 0) return 10; // Medium Helmet
		else if (option == 1) return 11; // Large Helmet
		return -1;
	}

	private int shieldChoice(Player player) {
		player.message("What sort of shield do you want to make?");
		int option = multi(player, "Square Shield (2 bars)",
			"Kite Shield (3 bars)");
		if (option == 0) return 12; // Square Shield
		else if (option == 1) return 13; // Kite Shield
		return -1;
	}

	private int armourChoice(Player player) {
		player.message("What sort of armour do you want to make?");
		final boolean chainLegs = config().WANT_CHAIN_LEGS;
		ArrayList<String> options = new ArrayList<>();
		if (chainLegs) {
			options.add("Chain mail legs (2 bars)");
		}
		options.add("Chain mail body (3 bars)");
		options.add("Plate mail body (5 bars)");
		options.add("Plate mail legs (3 bars)");
		options.add("Plated Skirt (3 bars)");
		if (config().WANT_CUSTOM_SPRITES) {
			options.add("Chain mail top (3 bars)");
			options.add("Plate mail top (5 bars)");
		}

		String[] finalOptions = new String[options.size()];

		int option = multi(player, options.toArray(finalOptions));

		if (chainLegs && option >=0) {
			if (option == 0) return 21; // Chain Mail Legs
			else option -= 1;
		}

		if (option == 0) return 14; // Chain Mail Body
		else if (option == 1) return 15; // Plate Mail Body
		else if (option == 2) return 16; // Plate Mail Legs
		else if (option == 3) return 17; // Plated Skirt
		else if (config().WANT_CUSTOM_SPRITES) {
			if (option == 4) {
				return 22; // Chain mail top
			} else if (option == 5) {
				return 23; // Plate mail top
			}
		}
		return -1;
	}

	private int getCount(ItemSmithingDef def, Item item, Player player) {
		int count = 1;
		if (config().BATCH_PROGRESSION) {
			String[] options = {
				"Make 1",
				"Make 5",
				"Make 10",
				"Make All"
			};

			count = multi(player, options);

			if (count == -1) {
				return -1;
			}

			int maximumMakeCount = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false)) / def.getRequiredBars();

			return count != 3
				? Integer.parseInt(options[count].replaceAll("Make ", ""))
				: maximumMakeCount;
		}

		return count;
	}

	/**
	 * Gets the smithing exp for the given amount of the right bars
	 */
	public int getSmithingExp(int barID, int barCount) {
		int[] exps = {50, 100, 150, 200, 250, 300};
		int type = getBarType(barID);
		if (type < 0) {
			return 0;
		}
		return (exps[type] * barCount);
	}

	/**
	 * Gets the min level required to smith a bar
	 */
	public int minSmithingLevel(int barID) {
		int[] levels = {1, 15, 30, 50, 70, 85};
		int type = getBarType(barID);
		if (type < 0) {
			return -1;
		}
		return levels[type];
	}

	/**
	 * Gets the type of bar we have
	 */
	public int getBarType(int barID) {
		switch (ItemId.getById(barID)) {
			case BRONZE_BAR:
				return 0;
			case IRON_BAR:
				return 1;
			case STEEL_BAR:
				return 2;
			case GOLD_BAR:
			case MITHRIL_BAR:
				return 3;
			case ADAMANTITE_BAR:
				return 4;
			case RUNITE_BAR:
				return 5;
			default:
				break;
		}
		return -1;
	}
}
