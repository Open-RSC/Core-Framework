package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
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
		if (obj.getID() == 177) { // Doric's Anvil
			if (checkDorics(player)) return;
		}

		if (!smithingChecks(obj, item, player)) return;

		beginSmithing(item, player);
	}

	private boolean checkDorics(Player player) {
		if (player.getQuestStage(3) > -1) {
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

		if (obj.getID() != 50) return false;

		// Using hammer with anvil.
		if(item.getID() == 168) {
			player.message("To forge items use the metal you wish to work with the anvil");
			return false;
		}

		int minSmithingLevel = Formulae.minSmithingLevel(item.getID());

		// Special Dragon Square Shield Case
		if (minSmithingLevel < 0 && item.getID() != 1276 && item.getID() != 1277) {
			player.message("Nothing interesting happens");
			return false;
		}

		if (player.getSkills().getLevel(13) < minSmithingLevel) {
			player.message("You need at least level "
				+ minSmithingLevel + " smithing to work with "
				+ item.getDef().getName().toLowerCase().replaceAll("bar", ""));
			return false;
		}

		if (player.getInventory().countId(168) < 1) {
			player.message("You need a hammer to work the metal with.");
			return false;
		}

		return true;
	}

	private void beginSmithing(final Item item, final Player player) {

		// Combining Dragon Square Shield Halves
		if(item.getID() == 1276 || item.getID() == 1277) {
			attemptDragonSquareCombine(item, player);
			return;
		}

		// Failure to make a gold bowl without Legend's Quest.
		if(item.getID() == 172 && player.getQuestStage(Quests.LEGENDS_QUEST) >= 0 && player.getQuestStage(Quests.LEGENDS_QUEST) <= 2) {
			player.message("You're not quite sure what to make from the gold..");
			return;
		}

		player.message("What would you like to make?");

		// Gold
		if(item.getID() == 172) {
			handleGoldSmithing(player);
			return;
		}

		handleSmithing(item, player);

	}

	private void attemptDragonSquareCombine(Item item, Player player) {
		if(player.getSkills().getLevel(13) < 60) {
			player.message("You need a smithing ability of at least 60 to complete this task.");
		}
		// non-kosher this message
		else if(player.getInventory().countId(1276) < 1 || player.getInventory().countId(1277) < 1) {
			player.message("You need the two shield halves to repair the shield.");
		}
		else {
			player.message("You set to work trying to fix the ancient shield.");
			sleep(1200);
			player.message("You hammer long and hard and use all of your skill.");
			sleep(1200);
			player.message("Eventually, it is ready...");
			sleep(1200);
			player.message("You have repaired the Dragon Square Shield.");
			player.getInventory().remove(1276, 1);
			player.getInventory().remove(1277, 1);
			player.getInventory().add(new Item(1278, 1));
			player.incExp(13, 300, true);
		}
	}

	private void handleGoldSmithing(Player player) {
		int goldOption = showMenu(player, "Golden bowl.", "Cancel");
		if(player.isBusy()) {
			return;
		}
		if (goldOption == 0) {
			message(player, "You hammer the metal...");
			if(player.getInventory().countId(172) < 2) {
				player.message("You need two bars of gold to make this item.");
			} else {
				if(Formulae.failCalculation(player, Skills.SMITHING, 50)) {
					for (int x = 0; x < 2; x++) {
						player.getInventory().remove(172, 1);
					}
					player.message("You forge a beautiful bowl made out of solid gold.");
					player.getInventory().add(new Item(1188, 1));
					player.incExp(13, 120, true);
				} else {
					player.message("You make a mistake forging the bowl..");
					player.message("You pour molten gold all over the floor..");
					player.getInventory().remove(172, 1);
					player.incExp(13, 4, true);
				}
			}
		}
	}

	private void handleSmithing(final Item item, final Player player) {

		// First Smithing Menu
		int firstType = firstMenu(item, player);
		if (firstType < 0) return;

		if(player.isBusy()) {
			return;
		}

		// Second Smithing Menu
		int secondType = secondMenu(item, player, firstType);
		if (secondType < 0) return;

		// Distribute to the correct function to make our final choice
		int toMake = chooseItem(player, secondType);

		if(player.isBusy() || toMake == -1) {
			return;
		}

		final ItemSmithingDef def = EntityHandler.getSmithingDef(
			(Formulae.getBarType(item.getID()) * 21) + toMake);

		if (def == null) {
			// No definition found
			player.message("Nothing interesting happens");
			return;
		}

		if (player.getSkills().getLevel(13) < def.getRequiredLevel()) {
			player.message("You need to be at least level "
				+ def.getRequiredLevel() + " smithing to do that");
			return;
		}

		int makeCount = getCount(def, item, player);

		if (makeCount == -1) return;

		player.setBatchEvent(new BatchEvent(player, 600, makeCount) {
			@Override
			public void action() {
				if (player.getInventory().countId(item.getID()) < def.getRequiredBars()) {
					player.message("You need " + def.getRequiredBars() + " bars of metal to make this item");
					interrupt();
					return;
				}
				if (player.getFatigue() >= player.MAX_FATIGUE) {
					player.message("You are too tired to smith");
					interrupt();
					return;
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
				player.incExp(13,
					Formulae.getSmithingExp(item.getID(), def.getRequiredBars()), true);
			}
		});
	}

	private int firstMenu(Item item, Player player) {
		int option;

		// Steel Bar
		if (item.getID() == 171) {
			option = showMenu(player, "Make Weapon", "Make Armour",
				"Make Missile Heads", "Make Steel Nails", "Cancel");

			// Cancel
			if (option == 4) return -1;

			return option;
		}

		// Bronze Bar
		if(item.getID() == 169) {
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
			return offset + showMenu(player,  "Helmet", "Shield", "Armour");
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
			if(item.getID() == 171) {
				makeNails(item, player);
			}

			// Bronze Wire
			else if(item.getID() == 169) {
				makeWire(item, player);
			}
		}

		return -1;
	}

	private void makeNails(Item item, Player player) {
		if (player.getSkills().getLevel(13) < 34) {
			player.message("You need to be at least level 34 smithing to do that");
			return;
		}
		if (player.getInventory().countId(171) < 1) {
			player.message("You need 1 bar of metal to make this item");
			return;
		}
		showBubble(player, item);
		player.getInventory().remove(171, 1);
		player.message("You hammer the metal and make some nails");
		player.getInventory().add(new Item(419, 2));
		player.incExp(13, 70, true);
	}

	private void makeWire(Item item, Player player) {
		player.message("What sort of craft item do you want to make?");
		int bronzeWireOption = showMenu(player, "Bronze Wire(1 bar)", "Cancel");
		if(player.isBusy()) {
			return;
		}
		if (player.getInventory().countId(169) < 1) {
			player.message("You need 1 bar of metal to make this item");
			return;
		}
		if (bronzeWireOption == 0) {
			showBubble(player, item);
			player.getInventory().remove(169, 1);
			player.message("You hammer the Bronze Bar and make some bronze wire");
			player.getInventory().add(new Item(979, 1));
			player.incExp(13, 50, true);
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
		int option = showMenu(player,  "Square Shield (2 bars)",
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

			if(count == -1) {
				return -1;
			}

			int maximumMakeCount = player.getInventory().countId(item.getID()) / def.getRequiredBars();

			return count != 3
				? Integer.parseInt(options[count].replaceAll("Make ", ""))
				: maximumMakeCount;
		}

		return count;
	}
}
