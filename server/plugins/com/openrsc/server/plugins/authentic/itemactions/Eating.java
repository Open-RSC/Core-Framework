package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.HpUpdate;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class Eating implements OpInvTrigger {

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.isEdible(player.getWorld())
			|| item.getCatalogId() == ItemId.ROTTEN_APPLES.id()
			|| item.getCatalogId() == ItemId.FISH_OIL.id()
			|| item.getCatalogId() == ItemId.SWEETENED_SLICES.id()
			|| item.getCatalogId() == ItemId.SWEETENED_CHUNKS.id();
	}

	@Override
	public void onOpInv(final Player player, Integer invIndex, final Item item, final String command) {
		if (item.isEdible(player.getWorld()) || item.getCatalogId() == ItemId.ROTTEN_APPLES.id()) {

			if (item.getItemStatus().getNoted()) {
				return;
			}

			if (player.getCarriedItems().remove(item) == -1) {
				return;
			}

			ActionSender.sendSound(player, "eat");

			int id = item.getCatalogId();
			boolean isKebabVariant = false;
			boolean gaveMessage = false;
			if (id == ItemId.SPECIAL_DEFENSE_CABBAGE.id() || id == ItemId.CABBAGE.id() || id == ItemId.RED_CABBAGE.id()) {
				if (id == ItemId.SPECIAL_DEFENSE_CABBAGE.id()) {
					player.playerServerMessage(MessageType.QUEST, "You eat the " + item.getDef(player.getWorld()).getName().toLowerCase());
					player.playerServerMessage(MessageType.QUEST, "It seems to taste nicer than normal");
					int lv = player.getSkills().getMaxStat(Skills.DEFENSE);
					int newStat = player.getSkills().getLevel(Skills.DEFENSE) + 1;
					if (newStat <= (lv + 1))
						player.getSkills().setLevel(Skills.DEFENSE, newStat);
				} else {
					player.playerServerMessage(MessageType.QUEST, "You eat the " + item.getDef(player.getWorld()).getName().toLowerCase()
						+ ". Yuck!");
					player.playerServerMessage(MessageType.QUEST, "It heals some health anyway");
				}
				gaveMessage = true;
			} else if (id == ItemId.KEBAB.id()) {
				isKebabVariant = true;
				handleKebab(player, item);
			} else if (id == ItemId.TASTY_UGTHANKI_KEBAB.id()) {
				isKebabVariant = true;
				handleTastyKebab(player, item);
			} else if (id == ItemId.COOKED_OOMLIE_MEAT_PARCEL.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the prepared Oomlie meat in Palm leaf parcel");
				player.message("It tastes very gamey !");
				gaveMessage = true;
			} else if (id == ItemId.SPINACH_ROLL.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the spinach roll");
				player.playerServerMessage(MessageType.QUEST, "It tastes a bit weird, but fills you up");
			} else if (id == ItemId.CHOCOLATE_BOMB.id() || id == ItemId.GNOME_WAITER_CHOCOLATE_BOMB.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the choc bomb");
				player.message("it tastes great");
			} else if (id == ItemId.VEGBALL.id() || id == ItemId.GNOME_WAITER_VEGBALL.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the veg ball");
				player.message("it tastes quite good");
			} else if (id == ItemId.WORM_HOLE.id() || id == ItemId.GNOME_WAITER_WORM_HOLE.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the " + item.getDef(player.getWorld()).getName().toLowerCase());
				say(player, null, "yuck");
				player.message("that was awful");
			} else if (id == ItemId.TANGLED_TOADS_LEGS.id() || id == ItemId.GNOME_WAITER_TANGLED_TOADS_LEGS.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the tangled toads legs");
				player.message("it tastes.....slimey");
			} else if (id == ItemId.ROCK_CAKE.id()) {
				// authentic does not send message to quest tab
				mes("You eat the " + item.getDef(player.getWorld()).getName().toLowerCase());
				say(player, null, "Ow! I nearly broke a tooth!");
				player.message("You feel strangely heavier and more tired");
			} else if (id == ItemId.EQUA_LEAVES.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the leaves..chewy but tasty");

			else if (id == ItemId.DWELLBERRIES.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the berrys..quite tasty");

			else if (id == ItemId.LEMON.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the lemon ..it's very sour");

			else if (id == ItemId.LEMON_SLICES.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the lemon slices ..they're very sour");

			else if (id == ItemId.DICED_LEMON.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the lemon cubes ..they're very sour");

			else if (id == ItemId.LIME.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the lime ..it's quite sour");

			else if (id == ItemId.LIME_SLICES.id() || id == ItemId.LIME_CHUNKS.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the " + item.getDef(player.getWorld()).getName().toLowerCase() + "..they're quite sour");

			else if (id == ItemId.GRAPEFRUIT.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the grapefruit ...it's somewhat bitter");

			else if (id == ItemId.GRAPEFRUIT_SLICES.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the grapefruit slices ...they're somewhat bitter");

			else if (id == ItemId.DICED_GRAPEFRUIT.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the grapefruit cubes ...they're somewhat bitter");

			else if (id == ItemId.ORANGE.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the orange ...yum");

			else if (id == ItemId.ORANGE_SLICES.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the orange slices ...yum");

			else if (id == ItemId.DICED_ORANGE.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the orange cubes ...yum");

			else if (id == ItemId.FRESH_PINEAPPLE.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the pineapple ...yum");

			else if (id == ItemId.PINEAPPLE_CHUNKS.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the pineapple chunks ..yum");

			else if (id == ItemId.CREAM.id())
				player.playerServerMessage(MessageType.QUEST, "You eat the cream..you get some on your nose");

			else if (id == ItemId.GNOMEBOWL.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the gnome bowl");
				player.message("it's pretty tastless");
				resetGnomeCooking(player);
			} else if (id == ItemId.GNOMECRUNCHIE.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the gnome crunchies");
				resetGnomeCooking(player);
			} else if (id == ItemId.CHEESE_AND_TOMATO_BATTA.id()
				|| id == ItemId.GNOME_WAITER_CHEESE_AND_TOMATO_BATTA.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the cheese and tomato batta");
				player.message("it's quite tasty");
			} else if (id == ItemId.TOAD_BATTA.id() || id == ItemId.GNOME_WAITER_TOAD_BATTA.id()
				|| id == ItemId.WORM_BATTA.id() || id == ItemId.GNOME_WAITER_WORM_BATTA.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the " + item.getDef(player.getWorld()).getName().toLowerCase());
				player.message("it's a bit chewy");
			} else if (id == ItemId.FRUIT_BATTA.id() || id == ItemId.GNOME_WAITER_FRUIT_BATTA.id()
				|| id == ItemId.VEG_BATTA.id() || id == ItemId.GNOME_WAITER_VEG_BATTA.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the " + item.getDef(player.getWorld()).getName().toLowerCase());
				player.message("it's tastes pretty good");
			} else if (id == ItemId.CHOC_CRUNCHIES.id() || id == ItemId.GNOME_WAITER_CHOC_CRUNCHIES.id()
				|| id == ItemId.SPICE_CRUNCHIES.id() || id == ItemId.GNOME_WAITER_SPICE_CRUNCHIES.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the " + item.getDef(player.getWorld()).getName().toLowerCase());
				player.message("they're very tasty");
			} else if (id == ItemId.WORM_CRUNCHIES.id() || id == ItemId.GNOME_WAITER_WORM_CRUNCHIES.id()
				|| id == ItemId.TOAD_CRUNCHIES.id() || id == ItemId.GNOME_WAITER_TOAD_CRUNCHIES.id()) {
				player.playerServerMessage(MessageType.QUEST, "You eat the " + item.getDef(player.getWorld()).getName().toLowerCase());
				player.message("they're a bit chewy");
			} else if (eatenByParts(player, item)) {
				String itemName = item.getDef(player.getWorld()).getName().toLowerCase();
				String message = "";
				String needleSt = "half a";
				String origName;
				if (itemName.contains("pie")) {
					if (itemName.contains(needleSt + " ")) {
						origName = itemName.substring(7); // "half a "
					} else if (itemName.contains(needleSt + "n ")) {
						origName = itemName.substring(8); // "half an "
					} else { // complete pies
						origName = itemName;
					}

					message = "You eat half of a" + (startsWithVowel(origName) ? "n " : " ") + origName;
				} else if (itemName.contains("pizza")) {
					message = "You eat half of the pizza";
				}
				// cakes
				else {
					boolean isChocolate = itemName.contains("chocolate");
					if (itemName.contains("slice")) {
						message = "You eat the " + (isChocolate ? "chocolate slice" : "slice of cake");
					} else if (itemName.contains("partial")) {
						message = "You eat some more of the " + (isChocolate ? "chocolate " : " ") + "cake";
					} else if (itemName.contains("cake")) {
						message = "You eat part of the " + (isChocolate ? "chocolate " : " ") + "cake";
					} else { // shouldn't happen
						message = "You eat the " + itemName;
					}
				}
				player.playerServerMessage(MessageType.QUEST, message);
			} else if (id == ItemId.ROTTEN_APPLES.id()) {
				// authentic does not give message to quest tab
				mes("you eat an apple");
				delay(3);
				say(player, null, "yuck");
				player.message("it's rotten, you spit it out");
			} else
				player.playerServerMessage(MessageType.QUEST, "You eat the " + item.getDef(player.getWorld()).getName().toLowerCase());

			final boolean heals = player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS);
			if (heals) {
				int newHp = player.getSkills().getLevel(Skills.HITS) + item.eatingHeals(player.getWorld());
				if (newHp > player.getSkills().getMaxStat(Skills.HITS)) {
					newHp = player.getSkills().getMaxStat(Skills.HITS);
				}
				player.getSkills().setLevel(Skills.HITS, newHp);
			}
			if (heals && !isKebabVariant && !gaveMessage) {
				player.playerServerMessage(MessageType.QUEST, "It heals some health");
				if (config().WANT_PARTIES) {
					if (player.getParty() != null) {
						player.getUpdateFlags().setHpUpdate(new HpUpdate(player, 0));
						player.getParty().sendParty();
					}
				}
			}

			addFoodResult(player, id);

		} else if (item.getCatalogId() == ItemId.FISH_OIL.id()) {
			ActionSender.sendSound(player, "eat");
			int id = item.getCatalogId();

			player.playerServerMessage(MessageType.QUEST, "You eat the fish oil");

			// Heal
			if (player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
				// See if it heals
				if (DataConversions.random(1, 2) == 1) {
					int newHp = player.getSkills().getLevel(Skills.HITS) + 1;
					if (newHp > player.getSkills().getMaxStat(Skills.HITS)) {
						newHp = player.getSkills().getMaxStat(Skills.HITS);
					}
					player.getSkills().setLevel(Skills.HITS, newHp);
					player.playerServerMessage(MessageType.QUEST, "It heals some health");
				} else {
					player.playerServerMessage(MessageType.QUEST, "You don't feel a difference");
				}
			}
			// Remove
			player.getCarriedItems().remove(new Item(id, 1));
		} else if (item.getCatalogId() == ItemId.SWEETENED_SLICES.id() || item.getCatalogId() == ItemId.SWEETENED_CHUNKS.id()) {
			ActionSender.sendSound(player, "eat");
			int id = item.getCatalogId();

			player.playerServerMessage(MessageType.QUEST, "You eat the sweetened fruit");

			// Heal
			if (player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
				int newHp = player.getSkills().getLevel(Skills.HITS) + DataConversions.random(1, 2);
				if (newHp > player.getSkills().getMaxStat(Skills.HITS)) {
					newHp = player.getSkills().getMaxStat(Skills.HITS);
				}
				player.getSkills().setLevel(Skills.HITS, newHp);
				player.playerServerMessage(MessageType.QUEST, "It heals some health");
			}
			// Remove
			player.getCarriedItems().remove(new Item(id, 1));
		}
	}

	private void handleKebab(Player player, Item item) {
		int rand = DataConversions.random(0, 31);
		player.playerServerMessage(MessageType.QUEST, "You eat the Kebab");
		int hpRestored = 0;
		if (rand == 0) { // 1/32 or 3% chance chance of 2-4 damage (can never kill)
			player.playerServerMessage(MessageType.QUEST, "That tasted a bit dodgy");
			player.message("You feel a bit ill");
			if (player.getSkills().getLevel(Skills.HITS) > 2) {
				int dmg = DataConversions.random(2, 4);
				int newHp = Math.max(player.getSkills().getLevel(Skills.HITS) - dmg, 1);
				player.getSkills().setLevel(Skills.HITS, newHp);
			}
		} else if (rand <= 1) { // 1/32 or 3% chance to heal 30 hits and gaining 1-3 levels att, str, def
			player.playerServerMessage(MessageType.QUEST, "Wow that was an amazing kebab!");
			player.message("You feel slightly invigorated");
			int boost = DataConversions.random(1, 3);
			int[] skills = {Skills.ATTACK, Skills.STRENGTH, Skills.DEFENSE};
			for (int skill : skills) {
				if (player.getSkills().getLevel(skill) <= player.getSkills().getMaxStat(skill)) {
					player.getSkills().setLevel(skill, player.getSkills().getLevel(skill) + boost);
				}
			}
			hpRestored = 30;
		} else if (rand <= 8) { // 7/32 or 21% chance of healing 10-20 hits
			player.playerServerMessage(MessageType.QUEST, "That was a good kebab");
			player.message("You feel a lot better");
			hpRestored = DataConversions.random(10, 20);
		} else if (rand <= 28) { // 20/32 or 62% chance of healing 10% max hits
			player.playerServerMessage(MessageType.QUEST, "It heals some health");
			hpRestored = player.getSkills().getMaxStat(Skills.HITS) * 10 / 100;
		} else { // 3/32 or 9% that does nothing
			player.playerServerMessage(MessageType.QUEST, "The kebab didn't seem to do a lot");
			hpRestored = 0;
		}
		if (hpRestored > 0 && player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
			int newStat = player.getSkills().getLevel(Skills.HITS) + hpRestored;
			if (newStat > player.getSkills().getMaxStat(Skills.HITS)) {
				newStat = player.getSkills().getMaxStat(Skills.HITS);
			}
			player.getSkills().setLevel(Skills.HITS, newStat);
		}
	}

	private void handleTastyKebab(Player player, Item item) {
		thinkbubble(item);
		player.playerServerMessage(MessageType.QUEST, "You eat the " + item.getDef(player.getWorld()).getName());
		player.playerServerMessage(MessageType.QUEST, "It heals some health");
		// restores up to 19
		int newStat = player.getSkills().getLevel(Skills.HITS) + 19;
		if (newStat > player.getSkills().getMaxStat(Skills.HITS)) {
			newStat = player.getSkills().getMaxStat(Skills.HITS);
		}
		player.getSkills().setLevel(Skills.HITS, newStat);
		switch(DataConversions.random(0,2)) {
			case 0:
				say(player, null, "Yummmmm!");
				break;
			case 1:
				say(player, null, "Oh, so nice!!!");
				break;
			case 2:
				say(player, null, "Lovely!");
				break;
		}
	}

	// cakes, pies and pizzas (except plain pizza) are eaten partially
	private boolean eatenByParts(Player player, Item item) {
		int eatenByParts[] = {
			// Cakes
			ItemId.CAKE.id(),
			ItemId.PARTIAL_CAKE.id(),
			ItemId.CHOCOLATE_CAKE.id(),
			ItemId.PARTIAL_CHOCOLATE_CAKE.id(),
			ItemId.SLICE_OF_CAKE.id(),
			ItemId.ROCK_CAKE.id(),

			// Pies
			ItemId.APPLE_PIE.id(),
			ItemId.HALF_AN_APPLE_PIE.id(),
			ItemId.REDBERRY_PIE.id(),
			ItemId.HALF_A_REDBERRY_PIE.id(),
			ItemId.MEAT_PIE.id(),
			ItemId.HALF_A_MEAT_PIE.id(),

			// Pizzas
			ItemId.MEAT_PIZZA.id(),
			ItemId.HALF_MEAT_PIZZA.id(),
			ItemId.ANCHOVIE_PIZZA.id(),
			ItemId.HALF_ANCHOVIE_PIZZA.id(),
			ItemId.PINEAPPLE_PIZZA.id(),
			ItemId.HALF_PINEAPPLE_PIZZA.id()
		};
		return DataConversions.inArray(eatenByParts, item.getCatalogId());
	}

	private void addFoodResult(Player player, int id) {

		if (id == ItemId.MEAT_PIZZA.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.HALF_MEAT_PIZZA.id()));

		else if (id == ItemId.ANCHOVIE_PIZZA.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.HALF_ANCHOVIE_PIZZA.id()));

		else if (id == ItemId.PINEAPPLE_PIZZA.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.HALF_PINEAPPLE_PIZZA.id()));

		else if (id == ItemId.CAKE.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.PARTIAL_CAKE.id()));

		else if (id == ItemId.PARTIAL_CAKE.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.SLICE_OF_CAKE.id()));

		else if (id == ItemId.CHOCOLATE_CAKE.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.PARTIAL_CHOCOLATE_CAKE.id()));

		else if (id == ItemId.PARTIAL_CHOCOLATE_CAKE.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.CHOCOLATE_SLICE.id()));

		else if (id == ItemId.APPLE_PIE.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.HALF_AN_APPLE_PIE.id()));

		else if (id == ItemId.HALF_AN_APPLE_PIE.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.PIE_DISH.id()));

		else if (id == ItemId.REDBERRY_PIE.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.HALF_A_REDBERRY_PIE.id()));

		else if (id == ItemId.HALF_A_REDBERRY_PIE.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.PIE_DISH.id()));

		else if (id == ItemId.MEAT_PIE.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.HALF_A_MEAT_PIE.id()));

		else if (id == ItemId.HALF_A_MEAT_PIE.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.PIE_DISH.id()));

		else if (id == ItemId.STEW.id() || id == ItemId.CURRY.id() || id == ItemId.SPECIAL_CURRY.id()
			|| id == ItemId.SEAWEED_SOUP.id())
			player.getCarriedItems().getInventory().add(new Item(ItemId.BOWL.id()));
	}
}
