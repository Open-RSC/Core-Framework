package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.ItemSmithingDef;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class Smithing implements InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener {

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
									   Player player) {
		return obj.getID() == 177 || obj.getID() == 50;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, final Item item, final Player player) {

		// Doric's Anvil
		if (obj.getID() == 177 && !allowDorics(player)) return;

		if (!smithingChecks(obj, item, player)) return;

		beginSmithing(item, player);
	}

	private boolean allowDorics(Player player) {
		if (player.getQuestStage(Quests.DORICS_QUEST) > -1) {
			Npc doric = World.getWorld().getNpc(144, 323, 327, 487, 492,
				true);
			doric.getUpdateFlags().setChatMessage(new ChatMessage(doric,
				"Heh who said you could use that?", player));
			player.message("You need to finish Doric's quest to use this anvil");
			return false;
		}
		return true;
	}

	private boolean smithingChecks(final GameObject obj, final Item item, final Player player) {

		// Not an anvil or Doric's Anvil...
		if (!(obj.getID() == 50 || obj.getID() == 177)) return false;

		// Using hammer with anvil.
		if (item.getID() == ItemId.HAMMER.id()) {
			player.message("To forge items use the metal you wish to work with the anvil");
			return false;
		}

		int minSmithingLevel = minSmithingLevel(item.getID());

		// Special Dragon Square Shield Case
		if (minSmithingLevel < 0 && item.getID() != ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id() && item.getID() != ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id()) {
			player.message("Nothing interesting happens");
			return false;
		}

		if (player.getSkills().getLevel(Skills.SMITHING) < minSmithingLevel) {
			player.message("You need at least level "
				+ minSmithingLevel + " smithing to work with "
				+ item.getDef().getName().toLowerCase().replaceAll("bar", ""));
			return false;
		}

		if (player.getInventory().countId(ItemId.HAMMER.id()) < 1) {
			player.message("You need a hammer to work the metal with.");
			return false;
		}

		return true;
	}

	private void beginSmithing(final Item item, final Player player) {

		// Combining Dragon Square Shield Halves
		if (item.getID() == ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id() || item.getID() == ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id()) {
			attemptDragonSquareCombine(item, player);
			return;
		}

		// Failure to make a gold bowl without Legend's Quest.
		if (item.getID() == ItemId.GOLD_BAR.id() && player.getQuestStage(Quests.LEGENDS_QUEST) >= 0 && player.getQuestStage(Quests.LEGENDS_QUEST) <= 2) {
			player.message("You're not quite sure what to make from the gold..");
			return;
		}

		player.message("What would you like to make?");

		// Gold
		if (item.getID() == ItemId.GOLD_BAR.id()) {
			handleGoldSmithing(player);
			return;
		}

		handleSmithing(item, player);

	}

	private void attemptDragonSquareCombine(Item item, Player player) {
		if (player.getSkills().getLevel(Skills.SMITHING) < 60) {
			player.message("You need a smithing ability of at least 60 to complete this task.");
		}
		// non-kosher this message
		else if (player.getInventory().countId(ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id()) < 1
				|| player.getInventory().countId(ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id()) < 1) {
			player.message("You need the two shield halves to repair the shield.");
		} else {
			message(player, 1200, "You set to work trying to fix the ancient shield.",
					"You hammer long and hard and use all of your skill.",
					"Eventually, it is ready...",
					"You have repaired the Dragon Square Shield.");
			player.getInventory().remove(ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id(), 1);
			player.getInventory().remove(ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id(), 1);
			player.getInventory().add(new Item(ItemId.DRAGON_SQUARE_SHIELD.id(), 1));
			player.incExp(Skills.SMITHING, 300, true);
		}
	}

	private void handleGoldSmithing(Player player) {
		int goldOption = showMenu(player, "Golden bowl.", "Cancel");
		if (player.isBusy()) {
			return;
		}
		if (goldOption == 0) {
			message(player, "You hammer the metal...");
			if (player.getInventory().countId(ItemId.GOLD_BAR.id()) < 2) {
				player.message("You need two bars of gold to make this item.");
			} else {
				if (Formulae.failCalculation(player, Skills.SMITHING, 50)) {
					for (int x = 0; x < 2; x++) {
						player.getInventory().remove(ItemId.GOLD_BAR.id(), 1);
					}
					player.message("You forge a beautiful bowl made out of solid gold.");
					player.getInventory().add(new Item(ItemId.GOLDEN_BOWL.id(), 1));
					player.incExp(Skills.SMITHING, 120, true);
				} else {
					player.message("You make a mistake forging the bowl..");
					player.message("You pour molten gold all over the floor..");
					player.getInventory().remove(ItemId.GOLD_BAR.id(), 1);
					player.incExp(Skills.SMITHING, 4, true);
				}
			}
		}
	}

	private void handleSmithing(final Item item, final Player player) {

		// First Smithing Menu
		int firstType = firstMenu(item, player);
		if (firstType < 0) return;

		if (player.isBusy()) {
			return;
		}

		// Second Smithing Menu
		int secondType = secondMenu(item, player, firstType);
		if (secondType < 0) return;

		// Distribute to the correct function to make our final choice
		int toMake = chooseItem(player, secondType);

		if (player.isBusy() || toMake == -1) {
			return;
		}

		final ItemSmithingDef def = EntityHandler.getSmithingDef((getBarType(item.getID()) * 21) + toMake);

		if (def == null) {
			// No definition found
			player.message("Nothing interesting happens");
			return;
		}

		if (player.getSkills().getLevel(Skills.SMITHING) < def.getRequiredLevel()) {
			player.message("You need to be at least level "
				+ def.getRequiredLevel() + " smithing to do that");
			return;
		}

		int makeCount = getCount(def, item, player);

		if (makeCount == -1) return;

		player.setBatchEvent(new BatchEvent(player, 600, "Smithing", makeCount, false) {
			@Override
			public void action() {
				if (player.getInventory().countId(item.getID()) < def.getRequiredBars()) {
					player.message("You need " + def.getRequiredBars() + " bars of metal to make this item");
					interrupt();
					return;
				}
				if (Constants.GameServer.WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to smith");
						interrupt();
						return;
					}
				}
				player.playSound("anvil");
				for (int x = 0; x < def.getRequiredBars(); x++) {
					player.getInventory().remove(new Item(item.getID(), 1));
				}

				showBubble(player, item);
				if (EntityHandler.getItemDef(def.getItemID()).isStackable()) {
					player.message("You hammer the metal and make " + def.getAmount() + " "
						+ EntityHandler.getItemDef(def.getItemID()).getName().toLowerCase());
					player.getInventory().add(
						new Item(def.getItemID(), def.getAmount()));
				} else {
					player.message("You hammer the metal and make a "
						+ EntityHandler.getItemDef(def.getItemID()).getName().toLowerCase());
					for (int x = 0; x < def.getAmount(); x++) {
						player.getInventory().add(new Item(def.getItemID(), 1));
					}
				}
				player.incExp(Skills.SMITHING, getSmithingExp(item.getID(), def.getRequiredBars()), true);
			}
		});
	}

	private int firstMenu(Item item, Player player) {
		int option;

		// Steel Bar
		if (item.getID() == ItemId.STEEL_BAR.id()) {
			option = showMenu(player, "Make Weapon", "Make Armour",
				"Make Missile Heads", "Make Steel Nails", "Cancel");

			// Cancel
			if (option == 4) return -1;

			return option;
		}

		// Bronze Bar
		if (item.getID() == ItemId.BRONZE_BAR.id()) {
			option = showMenu(player, "Make Weapon", "Make Armour",
				"Make Missile Heads", "Make Craft Item", "Cancel");

			// Cancel
			if (option == 4) return -1;

			return option;
		}

		// Any other bar.
		option = showMenu(player, "Make Weapon", "Make Armour",
			"Make Missile Heads", "Cancel");

		if (option == 3) return -1;

		return option;
	}

	private int secondMenu(Item item, Player player, int firstType) {

		int offset = 0;

		// Weapon
		if (firstType == 0) {
			player.message("Choose a type of weapon to make");
			return showMenu(player, "Dagger", "Throwing Knife", "Sword", "Axe", "Mace");
		}

		offset += 5;

		// Armour
		if (firstType == 1) {
			player.message("Choose a type of armour to make");
			int option = showMenu(player, "Helmet", "Shield", "Armour");
			// Cancel
			if (option < 0) return -1;
			
			return offset + option;
		}

		offset += 3;

		if (firstType == 2) {
			if (!Constants.GameServer.MEMBER_WORLD) {
				player.message("This feature is members only");
				return -1;
			}

			int option;

			// During or after Tourist Trap
			if (player.getQuestStage(Constants.Quests.TOURIST_TRAP) >= 8
				|| player.getQuestStage(Constants.Quests.TOURIST_TRAP) == -1) {
				option = showMenu(player, "Make Arrow Heads.", "Forge Dart Tips.", "Cancel.");

				// Cancel
				if (option == 2) return -1;
			}

			// Before Tourist Trap
			else {
				option = showMenu(player, "Make Arrow Heads.", "Cancel.");

				// Cancel
				if (option == 2) return -1;
			}

			// Cancel
			if (option < 0) return -1;

			return offset + option;
		}

		if (firstType == 3) {

			// Nails
			if (item.getID() == ItemId.STEEL_BAR.id()) {
				makeNails(item, player);
			}

			// Bronze Wire
			else if (item.getID() == ItemId.BRONZE_BAR.id()) {
				makeWire(item, player);
			}
		}

		return -1;
	}

	private void makeNails(Item item, Player player) {
		if (player.getSkills().getLevel(Skills.SMITHING) < 34) {
			player.message("You need to be at least level 34 smithing to do that");
			return;
		}
		if (player.getInventory().countId(ItemId.STEEL_BAR.id()) < 1) {
			player.message("You need 1 bar of metal to make this item");
			return;
		}
		showBubble(player, item);
		player.getInventory().remove(ItemId.STEEL_BAR.id(), 1);
		player.message("You hammer the metal and make some nails");
		player.getInventory().add(new Item(ItemId.NAILS.id(), 2));
		player.incExp(Skills.SMITHING, 70, true);
	}

	private void makeWire(Item item, Player player) {
		player.message("What sort of craft item do you want to make?");
		int bronzeWireOption = showMenu(player, "Bronze Wire(1 bar)", "Cancel");
		if (player.isBusy()) {
			return;
		}
		if (player.getInventory().countId(ItemId.BRONZE_BAR.id()) < 1) {
			player.message("You need 1 bar of metal to make this item");
			return;
		}
		if (bronzeWireOption == 0) {
			showBubble(player, item);
			player.getInventory().remove(ItemId.BRONZE_BAR.id(), 1);
			player.message("You hammer the Bronze Bar and make some bronze wire");
			player.getInventory().add(new Item(ItemId.BRONZE_WIRE.id(), 1));
			player.incExp(Skills.SMITHING, 50, true);
		}
	}

	private int chooseItem(Player player, int secondType) {
		// Dagger
		if (secondType == 0) return 0;

			// Throwing Knife
		else if (secondType == 1) {
			if (!Constants.GameServer.MEMBER_WORLD) {
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

		else if (secondType == 9) {
			if (player.getQuestStage(Constants.Quests.TOURIST_TRAP) >= 8 || player.getQuestStage(Constants.Quests.TOURIST_TRAP) == -1) {
				return 20;
			}
		}

		return -1;
	}

	private int swordChoice(Player player) {
		player.message("What sort of sword do you want to make?");
		int option = showMenu(player, "Short Sword",
			"Long Sword (2 bars)", "Scimitar (2 bars)",
			"2-handed Sword (3 bars)");
		if (option == 0) return 2; // Short Sword
		else if (option == 1) return 3; // Long Sword
		else if (option == 2) return 4; // Scimitar
		else if (option == 3) return 5; // 2-handed Sword
		return -1;
	}

	private int axeChoice(Player player) {
		player.message("What sort of axe do you want to make?");
		int option = showMenu(player, "Hatchet", "Battle Axe (3 bars)");
		if (option == 0) return 6; // Hatchet
		else if (option == 1) return 8; // Battle Axe
		return -1;
	}

	private int helmetChoice(Player player) {
		player.message("What sort of helmet do you want to make?");
		int option = showMenu(player, "Medium Helmet",
			"Large Helmet (2 bars)");
		if (option == 0) return 10; // Medium Helmet
		else if (option == 1) return 11; // Large Helmet
		return -1;
	}

	private int shieldChoice(Player player) {
		player.message("What sort of shield do you want to make?");
		int option = showMenu(player, "Square Shield (2 bars)",
			"Kite Shield (3 bars)");
		if (option == 0) return 12; // Square Shield
		else if (option == 1) return 13; // Kite Shield
		return -1;
	}

	private int armourChoice(Player player) {
		player.message("What sort of armour do you want to make?");
		int option = showMenu(player, "Chain Mail Body (3 bars)",
			"Plate Mail Body (5 bars)",
			"Plate Mail Legs (3 bars)", "Plated Skirt (3 bars)");
		if (option == 0) return 14; // Chain Mail Body
		else if (option == 1) return 15; // Plate Mail Body
		else if (option == 2) return 16; // Plate Mail Legs
		else if (option == 3) return 17; // Plated Skirt
		return -1;
	}

	private int getCount(ItemSmithingDef def, Item item, Player player) {
		int count = 1;
		if (Constants.GameServer.BATCH_PROGRESSION) {
			String[] options = {
				"Make 1",
				"Make 5",
				"Make 10",
				"Make All"
			};

			count = showMenu(player, options);

			if (count == -1) {
				return -1;
			}

			int maximumMakeCount = player.getInventory().countId(item.getID()) / def.getRequiredBars();

			return count != 3
				? Integer.parseInt(options[count].replaceAll("Make ", ""))
				: maximumMakeCount;
		}

		return count;
	}

	/**
	 * Gets the smithing exp for the given amount of the right bars
	 */
	public static int getSmithingExp(int barID, int barCount) {
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
	public static int minSmithingLevel(int barID) {
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
	public static int getBarType(int barID) {
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
