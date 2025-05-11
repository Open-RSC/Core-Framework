package com.openrsc.server.plugins.authentic.skills.crafting;

import com.google.common.collect.ImmutableMap;
import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.SceneryId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.external.ItemCraftingDef;
import com.openrsc.server.external.ItemGemDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.CarriedItems;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MathUtil;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.openrsc.server.plugins.Functions.*;

public class Crafting implements UseInvTrigger,
	UseLocTrigger {

	/**
	 * World instance
	 */
	private int[] gemsThatFail = new int[]{
		ItemId.UNCUT_RED_TOPAZ.id(),
		ItemId.UNCUT_JADE.id(),
		ItemId.UNCUT_OPAL.id(),
	};
	private int[] itemsFurnance = new int[]{
		ItemId.SILVER_BAR.id(),
		ItemId.GOLD_BAR.id(),
		ItemId.SODA_ASH.id(),
		ItemId.SAND.id(),
		ItemId.GOLD_BAR_FAMILYCREST.id(),
	};
	private int[] itemsOven = new int[]{
		ItemId.UNFIRED_POT.id(),
		ItemId.UNFIRED_PIE_DISH.id(),
		ItemId.UNFIRED_BOWL.id()
	};

	private static final String ring = "ring";
	private static final String Necklace = "Necklace";
	private static final String amulet = "amulet";
	private static final String Crown = "Crown";
	private static final String Gold = "Gold";
	private static final String Sapphire = "Sapphire";
	private static final String Emerald = "Emerald";
	private static final String Ruby = "Ruby";
	private static final String Diamond = "Diamond";
	private static final String Dragonstone = "Dragonstone";
	private static final String Opal = "Opal";
	private static final String dragonstone = "dragonstone";

	private static final Map<String, Mould> goldMoulds = new ImmutableMap.Builder<String, Mould>()
		.put(ring, new Mould("ring", ItemId.RING_MOULD.id(), "You need a ring mould to make a gold ring"))
		.put(Necklace, new Mould("Necklace", ItemId.NECKLACE_MOULD.id(), "You need a necklace mould to make a gold necklace"))
		.put(amulet, new Mould("amulet", ItemId.AMULET_MOULD.id(), "You need an amulet mould to make a gold amulet"))
		.put(Crown,new Mould("Crown", ItemId.CROWN_MOULD.id(), "You need a crown mould to make a gold crown"))
		.build();

	public final static int[] silver_moulds = {
		ItemId.HOLY_SYMBOL_MOULD.id(), // "You need a Holy symbol mould to make a holy symbol!"
		ItemId.UNHOLY_SYMBOL_MOULD.id(),
	};

	public final static int[] gems = {
		ItemId.NOTHING.id(),
		ItemId.SAPPHIRE.id(),
		ItemId.EMERALD.id(),
		ItemId.RUBY.id(),
		ItemId.DIAMOND.id(),
		ItemId.DRAGONSTONE.id(),
		ItemId.OPAL.id(),
	};

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		int item1ID = item1.getCatalogId();
		int item2ID = item2.getCatalogId();
		CarriedItems carriedItems = player.getCarriedItems();
		Inventory inventory = carriedItems.getInventory();
		if (item1ID == ItemId.CHISEL.id()) {
			doCutGem(player, item1, item2);
		} else if (item2ID == ItemId.CHISEL.id()) {
			doCutGem(player, item2, item1);
		} else if (item1ID == ItemId.GLASSBLOWING_PIPE.id()) {
			doGlassBlowing(player, item1, item2);
		} else if (item2ID == ItemId.GLASSBLOWING_PIPE.id()) {
			doGlassBlowing(player, item2, item1);
		} else if (item1ID == ItemId.NEEDLE.id()) {
			if (item2ID == ItemId.TEDDY_HEAD.id() || item2ID == ItemId.TEDDY_BOTTOM.id()) {
				makeTeddy(player, inventory, carriedItems);
			} else {
				makeLeather(player, item1, item2);
			}
		} else if (item2ID == ItemId.NEEDLE.id()) {
			if (item1ID == ItemId.TEDDY_HEAD.id() || item1ID == ItemId.TEDDY_BOTTOM.id()) {
				makeTeddy(player, inventory, carriedItems);
			} else {
				makeLeather(player, item2, item1);
			}
		} else if (item1ID == ItemId.BALL_OF_WOOL.id()) {
			useWool(player, item1, item2);
		} else if (item2ID == ItemId.BALL_OF_WOOL.id()) {
			useWool(player, item2, item1);
		} else if ((item1ID == ItemId.BUCKET_OF_WATER.id() || item1ID == ItemId.JUG_OF_WATER.id()) && item2ID == ItemId.CLAY.id()) {
			useWater(player, item1, item2);
		} else if ((item2ID == ItemId.BUCKET_OF_WATER.id() || item2ID == ItemId.JUG_OF_WATER.id()) && item1ID == ItemId.CLAY.id()) {
			useWater(player, item2, item1);
		} else if (item1ID == ItemId.MOLTEN_GLASS.id() && item2ID == ItemId.LENS_MOULD.id() || item1ID == ItemId.LENS_MOULD.id() && item2ID == ItemId.MOLTEN_GLASS.id()) {
			if (getQuestStage(player, Quests.OBSERVATORY_QUEST) >= 0 && getQuestStage(player, Quests.OBSERVATORY_QUEST) < 5) {
				say(player, null, "Perhaps I should speak to the professor first");
				return;
			}
			if (getCurrentLevel(player, Skill.CRAFTING.id()) < 10) {
				player.message("Sorry, you need a crafting level");
				player.message("Of 10 or above to use this object");
				//Authentically bugged and wouldn't stop the player from actually making the lens.
			}
			if (carriedItems.remove(new Item(ItemId.MOLTEN_GLASS.id())) > -1) {
				player.message("You pour the molten glass into the mould");
				player.message("And clasp it together");
				player.message("It produces a small convex glass disc");
				inventory.add(new Item(ItemId.LENS.id()));
			}
		} else {
			player.message("Nothing interesting happens");
		}
	}

	private void makeTeddy(Player player, Inventory inventory, CarriedItems carriedItems) {
		if (inventory.hasInInventory(ItemId.TEDDY_HEAD.id())
			&& inventory.hasInInventory(ItemId.TEDDY_BOTTOM.id())
			&& inventory.hasInInventory(ItemId.THREAD.id())) {
			if (getCurrentLevel(player, Skill.CRAFTING.id()) < 15) {
				player.message("You need level 15 crafting to fix the teddy");
				return;
			}

			int stage = player.getCache().hasKey("miniquest_dwarf_youth_rescue") ? player.getCache().getInt("miniquest_dwarf_youth_rescue") : -1;
			if (stage < 1) {
				player.message("I'd better get these parts back to the kid");
				return;
			}
			carriedItems.remove(new Item(ItemId.TEDDY_HEAD.id()));
			carriedItems.remove(new Item(ItemId.TEDDY_BOTTOM.id()));
			carriedItems.remove(new Item(ItemId.THREAD.id()));
			carriedItems.getInventory().add(new Item(ItemId.TEDDY.id()));
			player.message("You stitch together the teddy parts");

		} else {
			player.message("You need the two teddy halves and some thread");
		}
	}

	@Override
	public void onUseLoc(final Player player, GameObject obj, final Item item) {
		if (!craftingChecks(obj, item, player)) return;
		beginCrafting(item, player);
	}

	private boolean craftingChecks(final GameObject obj, final Item item, final Player player) {
		// allowed item on crafting game objects
		if (!craftingTypeChecks(obj, item, player)) return false;

		if (item.getItemStatus().getNoted()) return false;

		if (obj.getLocation().equals(Point.location(399, 840))) {
			// furnace in shilo village
			if ((player.getLocation().getY() == 841 && !player.withinRange(obj, 2)) && !player.withinRange90Deg(obj, 2)) {
				return false;
			}
		} else {
			// some furnaces the player is 2 spaces away
			if (!player.withinRange(obj, 1) && !player.withinRange90Deg(obj, 2)) {
				return false;
			}
		}

		if (item.getCatalogId() == ItemId.SODA_ASH.id() || item.getCatalogId() == ItemId.SAND.id()) { // Soda Ash or Sand (Glass)
			if (player.getCarriedItems().getInventory().countId(ItemId.SODA_ASH.id(), Optional.of(false)) < 1) {
				player.playerServerMessage(MessageType.QUEST, "You need some soda ash to make glass");
				return false;
			} else if (player.getCarriedItems().getInventory().countId(ItemId.SAND.id(), Optional.of(false)) < 1) {
				player.playerServerMessage(MessageType.QUEST, "You need some sand to make glass");
				return false;
			}
		}

		return true;
	}

	private boolean craftingTypeChecks(final GameObject obj, final Item item, final Player player) {
		boolean furnace = obj.getID() == SceneryId.FURNACE.id()|| obj.getID() == SceneryId.FURNACE_UNDERGROUND_PASS.id();
		boolean furnaceItem = DataConversions.inArray(itemsFurnance, item.getCatalogId());
		boolean jewelryBar = item.getCatalogId() == ItemId.SILVER_BAR.id() || item.getCatalogId() == ItemId.GOLD_BAR.id();
		boolean potteryOven = obj.getID() == SceneryId.POTTERY_OVEN.id();
		boolean potteryItem = DataConversions.inArray(itemsOven, item.getCatalogId());
		boolean potteryWheel = obj.getID() == SceneryId.POTTERY_WHEEL.id();
		boolean softClay = item.getCatalogId() == ItemId.SOFT_CLAY.id();

		// Checks to make sure you're using the right item with the right object.
		return (furnace && furnaceItem)
			|| (potteryOven && potteryItem)
			|| (potteryWheel && softClay);
	}

	private void beginCrafting(final Item item, final Player player) {
		if (item.getCatalogId() == ItemId.SODA_ASH.id() || item.getCatalogId() == ItemId.SAND.id()) {
			doGlassMaking(item, player);
			return;
		} else if (DataConversions.inArray(itemsOven, item.getCatalogId())) {
			doPotteryFiring(item, player);
			return;
		}

		player.playerServerMessage(MessageType.QUEST,"what would you like to make");

		int maxItemId = player.getConfig().RESTRICT_ITEM_ID;
		if (item.getCatalogId() == ItemId.GOLD_BAR.id() || item.getCatalogId() == ItemId.GOLD_BAR_FAMILYCREST.id()) {
			doGoldJewelry(item, player);
		} else if (item.getCatalogId() == ItemId.SILVER_BAR.id()) {
			doSilverJewelry(item, player);
		} else if (item.getCatalogId() == ItemId.SOFT_CLAY.id() && MathUtil.maxUnsigned(maxItemId, ItemId.UNFIRED_POT.id()) == maxItemId) {
			doPotteryMolding(item, player);
		}
	}

	private void doGoldJewelry(final Item goldBarItem, final Player player) {
		ItemCraftingDef def = null;
		if (player.getConfig().WANT_BETTER_JEWELRY_CRAFTING) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.CROWN_MOULD.id()) &&
				!player.getCarriedItems().hasCatalogID(ItemId.AMULET_MOULD.id()) &&
				!player.getCarriedItems().hasCatalogID(ItemId.NECKLACE_MOULD.id()) &&
				!player.getCarriedItems().hasCatalogID(ItemId.RING_MOULD.id())) {
				player.message("You need a mould to craft jewelry");
				return;
			}
			def = getDesiredGoldCraftingAutoDetection(goldBarItem, player);
			if (def == null) {
				// No definition found
				player.message("Nothing interesting happens");
				return;
			}
		} else {
			def = getDesiredGoldCraftingAuthentic(goldBarItem, player);
			if (def == null) {
				return;
			}
		}

		if (def.itemID == ItemId.NOTHING.id()) {
		    // not an authentic message
			player.message("You have no reason to make that item.");
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			if (goldBarItem.getCatalogId() != ItemId.GOLD_BAR_FAMILYCREST.id()) { // Perfect gold bars shouldn't be batched
				int mostThatCouldBeMade = 0;
				if (def.getReqGem() == ItemId.NOTHING.id()) {
					mostThatCouldBeMade = player.getCarriedItems().getInventory().countId(goldBarItem.getCatalogId(), Optional.of(false));
				} else {
					mostThatCouldBeMade = Math.min(
						player.getCarriedItems().getInventory().countId(def.getReqGem(), Optional.of(false)),
						player.getCarriedItems().getInventory().countId(goldBarItem.getCatalogId(), Optional.of(false))
					);
				}
				if (mostThatCouldBeMade > 1) {
					int howMany = multi(player, "Make all", "Make 1", "Make 3", "Make 5", "Make 10", "Make all but one");
					switch (howMany) {
						case 1:
							repeat = Math.min(1, mostThatCouldBeMade);
							break;
						case 2:
							repeat = Math.min(3, mostThatCouldBeMade);
							break;
						case 3:
							repeat = Math.min(5, mostThatCouldBeMade);
							break;
						case 4:
							repeat = Math.min(10, mostThatCouldBeMade);
							break;
						case 5:
							if (mostThatCouldBeMade > 1) {
								repeat = mostThatCouldBeMade - 1;
							} else {
								player.playerServerMessage(MessageType.QUEST, "Okay, all done making zero of your item.");
								return;
							}
							break;
						case 0:
							repeat = mostThatCouldBeMade;
					}
				} else {
					repeat = Math.min(1, mostThatCouldBeMade);
				}
			}
		}

		startbatch(repeat);
		batchGoldJewelry(player, goldBarItem, def);
	}

	/* determine all possible jewelry that can be crafted by a player
	   and display it to the user in a list ordered such that the highest xp products
	   are at the top
	 */
	private ItemCraftingDef getDesiredGoldCraftingAutoDetection(Item item, Player player) {
		ArrayList<String> options = new ArrayList<>();
		ArrayList<Integer> itemIds = new ArrayList<>();
		if (player.getCarriedItems().hasCatalogID(ItemId.CROWN_MOULD.id())) {
			if (player.getCarriedItems().hasCatalogID(ItemId.DRAGONSTONE.id())) {
				options.add("Dragonstone crown");
				itemIds.add(ItemId.DRAGONSTONE_CROWN.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.DIAMOND.id())) {
				options.add("Diamond crown");
				itemIds.add(ItemId.DIAMOND_CROWN.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.RUBY.id())) {
				options.add("Ruby crown");
				itemIds.add(ItemId.RUBY_CROWN.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.EMERALD.id())) {
				options.add("Emerald crown");
				itemIds.add(ItemId.EMERALD_CROWN.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.SAPPHIRE.id())) {
				options.add("Sapphire crown");
				itemIds.add(ItemId.SAPPHIRE_CROWN.id());
			}
			options.add("Gold crown");
			itemIds.add(ItemId.GOLD_CROWN.id());
		}
		if (player.getCarriedItems().hasCatalogID(ItemId.AMULET_MOULD.id())) {
			if (player.getCarriedItems().hasCatalogID(ItemId.DRAGONSTONE.id())) {
				options.add("Dragonstone amulet");
				itemIds.add(ItemId.UNSTRUNG_DRAGONSTONE_AMULET.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.DIAMOND.id())) {
				options.add("Diamond amulet");
				itemIds.add(ItemId.UNSTRUNG_DIAMOND_AMULET.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.RUBY.id())) {
				options.add("Ruby amulet");
				itemIds.add(ItemId.UNSTRUNG_RUBY_AMULET.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.EMERALD.id())) {
				options.add("Emerald amulet");
				itemIds.add(ItemId.UNSTRUNG_EMERALD_AMULET.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.SAPPHIRE.id())) {
				options.add("Sapphire amulet");
				itemIds.add(ItemId.UNSTRUNG_SAPPHIRE_AMULET.id());
			}
			options.add("Gold amulet");
			itemIds.add(ItemId.UNSTRUNG_GOLD_AMULET.id());
		}
		if (player.getCarriedItems().hasCatalogID(ItemId.NECKLACE_MOULD.id())) {
			if (player.getCarriedItems().hasCatalogID(ItemId.DRAGONSTONE.id())) {
				options.add("Dragonstone necklace");
				itemIds.add(ItemId.DRAGONSTONE_NECKLACE.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.DIAMOND.id())) {
				options.add("Diamond necklace");
				itemIds.add(ItemId.DIAMOND_NECKLACE.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.RUBY.id())) {
				options.add("Ruby necklace");
				itemIds.add(ItemId.RUBY_NECKLACE.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.EMERALD.id())) {
				options.add("Emerald necklace");
				itemIds.add(ItemId.EMERALD_NECKLACE.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.SAPPHIRE.id())) {
				options.add("Sapphire necklace");
				itemIds.add(ItemId.SAPPHIRE_NECKLACE.id());
			}
			options.add("Gold necklace");
			itemIds.add(ItemId.GOLD_NECKLACE.id());
		}
		if (player.getCarriedItems().hasCatalogID(ItemId.RING_MOULD.id())) {
			if (player.getCarriedItems().hasCatalogID(ItemId.DRAGONSTONE.id())) {
				options.add("Dragonstone ring");
				itemIds.add(ItemId.DRAGONSTONE_RING.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.DIAMOND.id())) {
				options.add("Diamond ring");
				itemIds.add(ItemId.DIAMOND_RING.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.RUBY.id())) {
				options.add("Ruby ring");
				itemIds.add(ItemId.RUBY_RING.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.EMERALD.id())) {
				options.add("Emerald ring");
				itemIds.add(ItemId.EMERALD_RING.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.SAPPHIRE.id())) {
				options.add("Sapphire ring");
				itemIds.add(ItemId.SAPPHIRE_RING.id());
			}
			if (player.getCarriedItems().hasCatalogID(ItemId.OPAL.id())) {
				options.add("Opal ring");
				itemIds.add(ItemId.OPAL_RING.id());
			}
			options.add("Gold ring");
			itemIds.add(ItemId.GOLD_RING.id());
		}

		thinkbubble(new Item(ItemId.GOLD_BAR.id())); // bubble will be displayed after menu
		if (options.size() == 0) {
			player.playerServerMessage(MessageType.QUEST, "You do not have any moulds...!");
			return null;
		}
		String[] finalOptions = new String[Math.min(options.size(), player.getClientLimitations().maxDialogueOptions)];
		System.arraycopy(options.toArray(), 0, finalOptions, 0, Math.min(finalOptions.length, options.size()));
		int menu = multi(player, finalOptions);
		if (menu < 0 || menu > finalOptions.length) {
			return null;
		}
		return player.getWorld().getServer().getEntityHandler().getCraftingDef(itemIds.get(menu));
	}

	private ItemCraftingDef getDesiredGoldCraftingAuthentic(Item item, Player player) {
		// select type
		String[] options;
		if (!config().WANT_EQUIPMENT_TAB) { // TODO: this is not a very good way to detect other than Cabbage server config
			options = new String[]{
				ring,
				Necklace,
				amulet
			};
		} else {
			options = new String[]{
				ring,
				Necklace,
				amulet,
				Crown
			};
		}

		thinkbubble(new Item(ItemId.GOLD_BAR.id())); // bubble will be displayed after menu
		int shapeSelection = multi(player, options);
		if (shapeSelection < 0 || shapeSelection > options.length - 1) {
			return null;
		}
		String jewelryShape = options[shapeSelection];

		if (!hasRequiredMould(player, jewelryShape)) return null;

		boolean gemUsed = false;
		if (!config().WANT_EQUIPMENT_TAB) { // TODO: this is not a very good way to detect other than Cabbage server config
			player.playerServerMessage(MessageType.QUEST,
				"Would you like to put a gem in the " + jewelryShape.toLowerCase() + "?");
			options = new String[]{
				"Yes",
				"No"
			};
			int gemUsedOption = multi(player, options);
			if (gemUsedOption == -1) return null;
			gemUsed = gemUsedOption == 0;
		}

		// select gem
		options = new String[]{
			Sapphire,
			Emerald,
			Ruby,
			Diamond
		};
		if (config().MEMBER_WORLD) {
			if (config().WANT_EQUIPMENT_TAB) { // TODO: this is not a very good way to detect Cabbage server config
				options = new String[]{
					Gold,
					Sapphire,
					Emerald,
					Ruby,
					Diamond,
					Dragonstone,
					Opal
				};
			} else {
				options = new String[]{
					Sapphire,
					Emerald,
					Ruby,
					Diamond,
					dragonstone
				};

				// Dragonstone should be capitalized only when making a Necklace
				if (jewelryShape.equals(Necklace)) {
					options[4] = Dragonstone;
				}
			}
		}

		String gem = Gold;
		if (gemUsed) {
			player.playerServerMessage(MessageType.QUEST, "what sort of gem do you want to put in the " + jewelryShape + "?");
			int gemMultiSelection = multi(player, options);
			if (gemMultiSelection < 0 || gemMultiSelection > options.length)
				return null;

			gem = options[gemMultiSelection];
		}

		return getCraftingDefByShapeAndGem(player, jewelryShape, gem);
	}

	private ItemCraftingDef getCraftingDefByShapeAndGem(Player player, String shape, String gem) {
		int craftingProductItemId = -1;
		switch (shape) {
			case ring: {
				switch (gem) {
					case Gold:
						craftingProductItemId = ItemId.GOLD_RING.id();
						break;
					case Sapphire:
						craftingProductItemId = ItemId.SAPPHIRE_RING.id();
						break;
					case Emerald:
						craftingProductItemId = ItemId.EMERALD_RING.id();
						break;
					case Ruby:
						craftingProductItemId = ItemId.RUBY_RING.id();
						break;
					case Diamond:
						craftingProductItemId = ItemId.DIAMOND_RING.id();
						break;
					case Dragonstone:
					case dragonstone:
						craftingProductItemId = ItemId.DRAGONSTONE_RING.id();
						break;
					case Opal:
						craftingProductItemId = ItemId.OPAL_RING.id();
						break;
				}
				break;
			}
			case Necklace: {
				switch (gem) {
					case Gold:
						craftingProductItemId = ItemId.GOLD_NECKLACE.id();
						break;
					case Sapphire:
						craftingProductItemId = ItemId.SAPPHIRE_NECKLACE.id();
						break;
					case Emerald:
						craftingProductItemId = ItemId.EMERALD_NECKLACE.id();
						break;
					case Ruby:
						craftingProductItemId = ItemId.RUBY_NECKLACE.id();
						break;
					case Diamond:
						craftingProductItemId = ItemId.DIAMOND_NECKLACE.id();
						break;
					case Dragonstone:
					case dragonstone:
						craftingProductItemId = ItemId.DRAGONSTONE_NECKLACE.id();
						break;
				}
				break;
			}
			case amulet: {
				switch (gem) {
					case Gold:
						craftingProductItemId = ItemId.UNSTRUNG_GOLD_AMULET.id();
						break;
					case Sapphire:
						craftingProductItemId = ItemId.UNSTRUNG_SAPPHIRE_AMULET.id();
						break;
					case Emerald:
						craftingProductItemId = ItemId.UNSTRUNG_EMERALD_AMULET.id();
						break;
					case Ruby:
						craftingProductItemId = ItemId.UNSTRUNG_RUBY_AMULET.id();
						break;
					case Diamond:
						craftingProductItemId = ItemId.UNSTRUNG_DIAMOND_AMULET.id();
						break;
					case Dragonstone:
					case dragonstone:
						craftingProductItemId = ItemId.UNSTRUNG_DRAGONSTONE_AMULET.id();
						break;
				}
				break;
			}
			case Crown: {
				switch (gem) {
					case Gold:
						craftingProductItemId = ItemId.GOLD_CROWN.id();
						break;
					case Sapphire:
						craftingProductItemId = ItemId.SAPPHIRE_CROWN.id();
						break;
					case Emerald:
						craftingProductItemId = ItemId.EMERALD_CROWN.id();
						break;
					case Ruby:
						craftingProductItemId = ItemId.RUBY_CROWN.id();
						break;
					case Diamond:
						craftingProductItemId = ItemId.DIAMOND_CROWN.id();
						break;
					case Dragonstone:
					case dragonstone:
						craftingProductItemId = ItemId.DRAGONSTONE_CROWN.id();
						break;
				}
				break;
			}
			default:
				return null;
		}

		return player.getWorld().getServer().getEntityHandler().getCraftingDef(craftingProductItemId);

	}

	private boolean hasRequiredMould(Player player, String jewelryShape) {
		if (player.getCarriedItems().getInventory().countId(goldMoulds.get(jewelryShape).itemId, Optional.of(false)) < 1) {
			player.playerServerMessage(MessageType.QUEST, goldMoulds.get(jewelryShape).failString);
			return false;
		}
		return true;
	}

	private void batchGoldJewelry(Player player, Item item, ItemCraftingDef def) {
		if (!canReceive(player, new Item(def.getItemID()))) {
			player.message("Your client does not support the desired object");
			return;
		}
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < def.getReqLevel()) {
			player.playerServerMessage(MessageType.QUEST, "You need a crafting skill of level " + def.getReqLevel() + " to make this");
			return;
		}
		if (checkFatigue(player)) return;

        // Get last gold bar in inventory.
        Item goldBar = player.getCarriedItems().getInventory().get(
            player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
        );
        if (goldBar == null) {
            // this message is inauthentic; authentically can't happen b/c there's no batching
            player.message("You don't have a gold bar.");
            return;
        }

        Item result;
        if (goldBar.getCatalogId() == ItemId.GOLD_BAR_FAMILYCREST.id() && def.getItemID() == ItemId.RUBY_RING.id()) {
            result = new Item(ItemId.RUBY_RING_FAMILYCREST.id(), 1);
        } else if (goldBar.getCatalogId() == ItemId.GOLD_BAR_FAMILYCREST.id() && def.getItemID() == ItemId.RUBY_NECKLACE.id()) {
            result = new Item(ItemId.RUBY_NECKLACE_FAMILYCREST.id(), 1);
        } else {
            result = new Item(def.getItemID(), 1);
        }

        // Get last gem in inventory.
		Item gemItem;
		if (def.getReqGem() != ItemId.NOTHING.id()) {
			gemItem = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(def.getReqGem(), Optional.of(false))
			);
			if (gemItem == null) {
				tellPlayerNoGem(player, def);
				return;
			}
		}

		// Remove items
        delay();
		tellPlayerSuccessfullyProducedCraftingProduct(player, def);
		player.getCarriedItems().remove(goldBar);
		if (def.getReqGem() != ItemId.NOTHING.id()) {
			player.getCarriedItems().remove(new Item(def.getReqGem()));
		}
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skill.CRAFTING.id(), def.getExp(), true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchGoldJewelry(player, item, def);
		}
	}

	private void tellPlayerSuccessfullyProducedCraftingProduct(Player player, ItemCraftingDef def) {
		switch (ItemId.getById(def.getItemID())) {
			case GOLD_RING:
				player.playerServerMessage(MessageType.QUEST, "You make a gold ring");
				return;
			case GOLD_NECKLACE:
				player.playerServerMessage(MessageType.QUEST, "You make a gold necklace");
				return;
			case UNSTRUNG_GOLD_AMULET:
				player.playerServerMessage(MessageType.QUEST, "You make a gold amulet");
				return;
			case GOLD_CROWN:
				player.playerServerMessage(MessageType.QUEST, "You make a gold crown");
				return;
			case SAPPHIRE_RING:
				player.playerServerMessage(MessageType.QUEST, "You make a Sapphire ring");
				return;
			case SAPPHIRE_NECKLACE:
				player.playerServerMessage(MessageType.QUEST, "You make a Sapphire necklace");
				return;
			case UNSTRUNG_SAPPHIRE_AMULET:
				player.playerServerMessage(MessageType.QUEST, "You make a Sapphire amulet");
				return;
			case SAPPHIRE_CROWN:
				player.playerServerMessage(MessageType.QUEST, "You make a Sapphire crown");
				return;
			case EMERALD_RING:
				player.playerServerMessage(MessageType.QUEST, "You make an Emerald ring");
				return;
			case EMERALD_NECKLACE:
				player.playerServerMessage(MessageType.QUEST, "You make an Emerald necklace");
				return;
			case UNSTRUNG_EMERALD_AMULET:
				player.playerServerMessage(MessageType.QUEST, "You make an Emerald amulet");
				return;
			case EMERALD_CROWN:
				player.playerServerMessage(MessageType.QUEST, "You make an Emerald crown");
				return;
			case RUBY_RING:
				player.playerServerMessage(MessageType.QUEST, "You make a ruby ring");
				return;
			case RUBY_NECKLACE:
				player.playerServerMessage(MessageType.QUEST, "You make a ruby necklace");
				return;
			case UNSTRUNG_RUBY_AMULET:
				player.playerServerMessage(MessageType.QUEST, "You make a ruby amulet");
				return;
			case RUBY_CROWN:
				player.playerServerMessage(MessageType.QUEST, "You make a ruby crown");
				return;
			case DIAMOND_RING:
				player.playerServerMessage(MessageType.QUEST, "You make a diamond ring");
				return;
			case DIAMOND_NECKLACE:
				player.playerServerMessage(MessageType.QUEST, "You make a diamond necklace");
				return;
			case UNSTRUNG_DIAMOND_AMULET:
				player.playerServerMessage(MessageType.QUEST, "You make a diamond amulet");
				return;
			case DIAMOND_CROWN:
				player.playerServerMessage(MessageType.QUEST, "You make a diamond crown");
				return;
			case DRAGONSTONE_RING:
				player.playerServerMessage(MessageType.QUEST, "You make a dragonstone ring");
				return;
			case DRAGONSTONE_NECKLACE:
				player.playerServerMessage(MessageType.QUEST, "You make a dragonstone necklace");
				return;
			case UNSTRUNG_DRAGONSTONE_AMULET:
				player.playerServerMessage(MessageType.QUEST, "You make a dragonstone amulet");
				return;
			case DRAGONSTONE_CROWN:
				player.playerServerMessage(MessageType.QUEST, "You make a dragonstone crown");
				return;
			case OPAL_RING:
				player.playerServerMessage(MessageType.QUEST, "You make an opal ring");
				return;
			default:
				player.playerServerMessage(MessageType.QUEST, "Programmer has not defined a message for successfully crafting this product.");
				player.playerServerMessage(MessageType.QUEST, "Please report this.");
		}
	}

	private void tellPlayerNoGem(Player player, ItemCraftingDef def) {
		switch (ItemId.getById(def.getItemID())) {
			case SAPPHIRE_RING:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut sapphire to make a sapphire ring");
				return;
			case SAPPHIRE_NECKLACE:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut sapphire to make a sapphire necklace");
				return;
			case UNSTRUNG_SAPPHIRE_AMULET:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut sapphire to make a sapphire amulet");
				return;
			case SAPPHIRE_CROWN:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut sapphire to make a sapphire crown");
				return;
			case EMERALD_RING:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut Emerald to make a Emerald ring");
				return;
			case EMERALD_NECKLACE:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut Emerald to make a Emerald necklace");
				return;
			case UNSTRUNG_EMERALD_AMULET:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut Emerald to make a Emerald amulet");
				return;
			case EMERALD_CROWN:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut Emerald to make a Emerald crown");
				return;
			case RUBY_RING:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut ruby to make a ruby ring");
				return;
			case RUBY_NECKLACE:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut ruby to make a ruby necklace");
				return;
			case UNSTRUNG_RUBY_AMULET:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut ruby to make a ruby amulet");
				return;
			case RUBY_CROWN:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut ruby to make a ruby crown");
				return;
			case DIAMOND_RING:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut diamond to make a diamond ring");
				return;
			case DIAMOND_NECKLACE:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut diamond to make a diamond necklace");
				return;
			case UNSTRUNG_DIAMOND_AMULET:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut diamond to make a diamond amulet");
				return;
			case DIAMOND_CROWN:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut diamond to make a diamond crown");
				return;
			case DRAGONSTONE_RING:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut dragonstone to make a dragonstone ring");
				return;
			case DRAGONSTONE_NECKLACE:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut dragonstone to make a dragonstone necklace");
				return;
			case UNSTRUNG_DRAGONSTONE_AMULET:
				player.playerServerMessage(MessageType.QUEST, "You do not have a dragonstone to make a dragonstone amulet");
				return;
			case DRAGONSTONE_CROWN:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut dragonstone to make a dragonstone crown");
				return;
			case OPAL_RING:
				player.playerServerMessage(MessageType.QUEST, "You do not have a cut opal to make an opal ring");
				return;
			default:
				player.playerServerMessage(MessageType.QUEST, "Programmer has not defined a message for failing to have the required gem.");
				player.playerServerMessage(MessageType.QUEST, "Please report this.");
				return;
		}
	}

	private void doSilverJewelry(final Item item, final Player player) {
		AtomicReference<String> reply = new AtomicReference<String>();

		// select type
		ArrayList<String> options = new ArrayList<>();
		options.addAll(Arrays.asList(
			"Holy Symbol of Saradomin"
		));
		int maxItemId = player.getConfig().RESTRICT_ITEM_ID;
		int jewelryId = ItemId.UNSTRUNG_UNHOLY_SYMBOL_OF_ZAMORAK.id();
		if (MathUtil.maxUnsigned(maxItemId, jewelryId) == maxItemId) {
			options.add("Unholy symbol of Zamorak");
		}
		String[] finalOptions = new String[options.size()];
		int type = multi(player, options.toArray(finalOptions));
		if (type < 0 || type > finalOptions.length) {
			return;
		}
		reply.set(finalOptions[type]);

		final int[] results = {
			ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(),
			ItemId.UNSTRUNG_UNHOLY_SYMBOL_OF_ZAMORAK.id()
		};
		if (player.getCarriedItems().getInventory().countId(silver_moulds[type], Optional.of(false)) <= 0) {
			player.message("You need a " + player.getWorld().getServer().getEntityHandler().getItemDef(silver_moulds[type]).getName() + " to make a " + reply.get() + "!");
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchSilverJewelry(player, item, results, type, reply);
	}

	private void batchSilverJewelry(Player player, Item item, int[] results, int type, AtomicReference<String> reply) {
		Item result = new Item(results[type]);
		if (!canReceive(player, result)) {
			player.message("Your client does not support the desired object");
			return;
		}
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < 16) {
			player.playerServerMessage(MessageType.QUEST, "You need a crafting skill of level 16 to make this");
			return;
		}
		if (checkFatigue(player)) return;

		Item silverMould = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(silver_moulds[type], Optional.of(false))
		);
		if (silverMould == null) {
			player.message("You need a " + player.getWorld().getServer().getEntityHandler().getItemDef(silver_moulds[type]).getName() + " to make a " + reply.get() + "!");
			return;
		}

		Item silver = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (silver == null) return;

		thinkbubble(silver);
		player.getCarriedItems().remove(silver);
		delay(2);

		player.playerServerMessage(MessageType.QUEST, "You make a " + result.getDef(player.getWorld()).getName());
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skill.CRAFTING.id(), 200, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchSilverJewelry(player, item, results, type, reply);
		}
	}

	private void doPotteryMolding(final Item item, final Player player) {
		ArrayList<String> options = new ArrayList<>();
		options.addAll(Arrays.asList(
			"Pie dish",
			"Pot"
		));
		int maxItemId = player.getConfig().RESTRICT_ITEM_ID;
		int bowlId = ItemId.UNFIRED_BOWL.id();
		if (MathUtil.maxUnsigned(maxItemId, bowlId) == maxItemId) {
			options.add("Bowl");
		}
		String[] finalOptions = new String[options.size()];
		int type = multi(player, options.toArray(finalOptions));
		if (type < 0 || type > finalOptions.length) {
			return;
		}

		int reqLvl, exp;
		Item result;
		AtomicReference<String> msg = new AtomicReference<String>();
		switch (type) {
			case 1:
				result = new Item(ItemId.UNFIRED_POT.id(), 1);
				reqLvl = 1;
				exp = 25;
				// should not use this, as pot is made at level 1
				msg.set("a pot");
				break;
			case 0:
				result = new Item(ItemId.UNFIRED_PIE_DISH.id(), 1);
				reqLvl = !config().OLD_SKILL_DEFS ? 4 : 3;
				exp = !config().OLD_SKILL_DEFS ? 60 : 30;
				msg.set("pie dishes");
				break;
			case 2:
				result = new Item(ItemId.UNFIRED_BOWL.id(), 1);
				reqLvl = !config().OLD_SKILL_DEFS ? 7 : 5;
				exp = !config().OLD_SKILL_DEFS ? 40 : 30;
				msg.set("a bowl");
				break;
			default:
				player.message("Nothing interesting happens");
				return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchPotteryMoulding(player, item, reqLvl, result, msg, exp);
	}

	private void batchPotteryMoulding(Player player, Item item, int reqLvl, Item result, AtomicReference<String> msg, int exp) {
		if (!canReceive(player, result)) {
			player.message("Your client does not support the desired object");
			return;
		}
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < reqLvl) {
			player.playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + msg.get());
			return;
		}
		if (checkFatigue(player)) return;

		Item softClay = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (softClay == null) return;

		delay();
		player.getCarriedItems().remove(softClay);
		thinkbubble(softClay);
		player.playerServerMessage(MessageType.QUEST, "you make the clay into a " + potteryItemName(result.getDef(player.getWorld()).getName()));
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skill.CRAFTING.id(), exp, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchPotteryMoulding(player, item, reqLvl, result, msg, exp);
		}
	}

	private void doPotteryFiring(final Item item, final Player player) {
		int reqLvl, xp;
		Item result;
		AtomicReference<String> msg = new AtomicReference<String>();
		switch (ItemId.getById(item.getCatalogId())) {
			case UNFIRED_POT:
				result = new Item(ItemId.POT.id(), 1);
				reqLvl = 1;
				xp = !config().OLD_SKILL_DEFS ? 25 : 0;
				// should not use this, as pot is made at level 1
				msg.set("a pot");
				break;
			case UNFIRED_PIE_DISH:
				result = new Item(ItemId.PIE_DISH.id(), 1);
				reqLvl = !config().OLD_SKILL_DEFS ? 4 : 3;
				xp = !config().OLD_SKILL_DEFS ? 40 : 30;
				msg.set("pie dishes");
				break;
			case UNFIRED_BOWL:
				result = new Item(ItemId.BOWL.id(), 1);
				reqLvl = !config().OLD_SKILL_DEFS ? 7 : 5;
				xp = !config().OLD_SKILL_DEFS ? 60 : 30;
				msg.set("a bowl");
				break;
			default:
				player.message("Nothing interesting happens");
				return;
		}

		final int exp = xp;

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchPotteryFiring(player, item, reqLvl, result, msg, exp);
	}

	private void batchPotteryFiring(Player player, Item item, int reqLvl, Item result, AtomicReference<String> msg, int exp) {
		if (!canReceive(player, result)) {
			player.message("Your client does not support the desired object");
			return;
		}
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < reqLvl) {
			player.playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + msg.get());
			return;
		}
		if (checkFatigue(player)) return;

		Item unfiredClay = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (unfiredClay == null) return;

		thinkbubble(unfiredClay);
		String potteryItem = potteryItemName(item.getDef(player.getWorld()).getName());
		player.playerServerMessage(MessageType.QUEST, "You put the " + potteryItem + " in the oven");
		player.getCarriedItems().remove(unfiredClay);
		delay(3);

		if (Formulae.crackPot(reqLvl, player.getSkills().getLevel(Skill.CRAFTING.id()))) {
			player.playerServerMessage(MessageType.QUEST, "The " // TODO: Check if is authentic message
				+ potteryItem + " cracks in the oven, you throw it away.");
		} else {
			player.playerServerMessage(MessageType.QUEST, "the "
				+ potteryItem + " hardens in the oven");

			delay(3);

			String finishedPotteryItem = result.getDef(player.getWorld()).getName().toLowerCase();

			// Remap pie dish -> dish
			if (finishedPotteryItem.equals("pie dish")) {
				finishedPotteryItem = "dish";
			}

			player.playerServerMessage(MessageType.QUEST, "You remove a "
				+ finishedPotteryItem
				+ " from the oven");
			player.getCarriedItems().getInventory().add(result);
			player.incExp(Skill.CRAFTING.id(), exp, true);
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchPotteryFiring(player, item, reqLvl, result, msg, exp);
		}
	}

	private void doGlassMaking(final Item item, final Player player) {
		int otherItem = item.getCatalogId() == ItemId.SAND.id() ? ItemId.SODA_ASH.id() : ItemId.SAND.id();
		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
			repeat = Math.min(player.getCarriedItems().getInventory().countId(otherItem, Optional.of(false)), repeat);
		}

		startbatch(repeat);
		batchGlassMaking(player, item, otherItem);
	}

	private void batchGlassMaking(Player player, Item item, int otherItem) {
		if (!canReceive(player, new Item(ItemId.MOLTEN_GLASS.id()))) {
			player.message("Your client does not support the desired object");
			return;
		}
		if (checkFatigue(player)) return;

		Inventory inventory = player.getCarriedItems().getInventory();
		Item item1 = inventory.get(
			inventory.getLastIndexById(otherItem, Optional.of(false))
		);
		Item item2 = inventory.get(
			inventory.getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (item1 == null || item2 == null)	return;

		thinkbubble(item2);
		player.playerServerMessage(MessageType.QUEST, "you heat the sand and soda ash in the furnace to make glass");
		player.getCarriedItems().remove(item1);
		player.getCarriedItems().remove(item2);
		delay();
		inventory.add(new Item(ItemId.MOLTEN_GLASS.id(), 1));
		inventory.add(new Item(ItemId.BUCKET.id(), 1));
		player.incExp(Skill.CRAFTING.id(), 80, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchGlassMaking(player, item, otherItem);
		}
	}

	private void doGlassBlowing(Player player, final Item pipe, final Item glass) {
		if (glass.getCatalogId() != ItemId.MOLTEN_GLASS.id()) {
			return;
		}
		player.message("what would you like to make?");

		String[] options = new String[]{
			"Vial",
			"orb",
			"Beer glass"
		};

		int type = multi(player, options);
		if (type < 0 || type > 2) {
			return;
		}

		Item result;
		int reqLvl, exp;
		String resultGen;
		switch (type) {
			case 0:
				result = new Item(ItemId.EMPTY_VIAL.id(), 1);
				reqLvl = 33;
				exp = 140;
				resultGen = "vials";
				break;
			case 1:
				result = new Item(ItemId.UNPOWERED_ORB.id(), 1);
				reqLvl = 46;
				exp = 210;
				resultGen = "orbs";
				break;
			case 2:
				result = new Item(ItemId.BEER_GLASS.id(), 1);
				reqLvl = 1;
				exp = 70;
				// should not use this, as beer glass is made at level 1
				resultGen = "beer glasses";
				break;
			default:
				return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(glass.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchGlassBlowing(player, glass, result, reqLvl, exp, resultGen);
	}

	private void batchGlassBlowing(Player player, Item glass, Item result, int reqLvl, int exp, String resultGen) {
		if (!canReceive(player, result)) {
			player.message("Your client does not support the desired object");
			return;
		}
		Inventory inventory = player.getCarriedItems().getInventory();
		ServerConfiguration config = config();
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < reqLvl) {
			player.message(
				"You need a crafting level of " + reqLvl + " to make " + resultGen);
			return;
		}
		if (checkFatigue(player)) return;

		glass = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(glass.getCatalogId(), Optional.of(false))
		);
		if (glass == null) return;

		player.getCarriedItems().remove(glass);
		delay();
		String message = "You make a " + result.getDef(player.getWorld()).getName();

		// Special handling for vials
		int amount = 1;
		if (result.getCatalogId() == ItemId.EMPTY_VIAL.id()) {
			if (config.WANT_CUSTOM_QUESTS) {
				double breakChance = 91.66667 - getCurrentLevel(player, Skill.CRAFTING.id()) / 1.32;
				for (int loop = 0; loop < 5; ++loop) {
					double hit = new Random().nextDouble() * 99;
					if (hit > breakChance) {
						amount++;
					}
				}
				message = "You make " + amount + " vial" + (amount != 1 ? "s" : "");
				if (player.getLocation().inBounds(418, 559, 421, 563)) {
					result.getItemStatus().setNoted(true);
				}
			}
		}

		player.playerServerMessage(MessageType.QUEST, message);

		if (result.getNoted()) {
			result.getItemStatus().setAmount(amount);
			inventory.add(result);
		}
		else {
			for (int i = 0; i < amount; i++) {
				inventory.add(result);
			}
		}

		player.incExp(Skill.CRAFTING.id(), exp, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay(2);
			batchGlassBlowing(player, glass, result, reqLvl, exp, resultGen);
		}
	}

	private void doCutGem(Player player, final Item chisel, final Item gem) {
		final ItemGemDef gemDef = player.getWorld().getServer().getEntityHandler().getItemGemDef(gem.getCatalogId());
		if (gemDef == null) {
			if (gem.getCatalogId() == ItemId.KING_BLACK_DRAGON_SCALE.id()) {
				if (getCurrentLevel(player, Skill.CRAFTING.id()) < 90) {
					player.message("You need 90 crafting to split the scales");
					return;
				}
				if (player.getCarriedItems().remove(new Item(ItemId.KING_BLACK_DRAGON_SCALE.id(), 1)) > -1) {
					player.message("You chip the massive scale into 5 pieces");
					give(player, ItemId.CHIPPED_DRAGON_SCALE.id(), 5);
					player.incExp(Skill.CRAFTING.id(), config().GAME_TICK * 2, true);
				}
			} else {
				player.message("Nothing interesting happens");
			}
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(gem.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchGemCutting(player, gem, gemDef);
	}

	private void batchGemCutting(Player player, Item gem, ItemGemDef gemDef) {
		if (!canReceive(player, new Item(gemDef.getGemID()))) {
			player.message("Your client does not support the desired object");
			return;
		}
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < gemDef.getReqLevel()) {
			boolean pluralize = gemDef.getGemID() <= ItemId.UNCUT_DRAGONSTONE.id();
			player.playerServerMessage(MessageType.QUEST,
				"you need a crafting level of " + gemDef.getReqLevel()
					+ " to cut " + (gem.getDef(player.getWorld()).getName().contains("ruby") ? "rubies" : gem.getDef(player.getWorld()).getName().replaceFirst("(?i)uncut ", "") + (pluralize ? "s" : "")));
			return;
		}
		if (checkFatigue(player)) return;

		Item item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(gem.getCatalogId(), Optional.of(false)));
		if (item == null) return;

		player.getCarriedItems().remove(item);
		delay();
		Item cutGem = new Item(gemDef.getGemID(), 1);
		// Jade, Opal and red topaz fail handler - 25% chance to fail

		if (DataConversions.inArray(gemsThatFail, gem.getCatalogId()) &&
			Formulae.smashGem(gem.getCatalogId(), gemDef.getReqLevel(), player.getSkills().getLevel(Skill.CRAFTING.id()))) {
			player.message("You miss hit the chisel and smash the " + cutGem.getDef(player.getWorld()).getName() + " to pieces!");
			player.getCarriedItems().getInventory().add(new Item(ItemId.CRUSHED_GEMSTONE.id()));

			if (gem.getCatalogId() == ItemId.UNCUT_RED_TOPAZ.id()) {
				player.incExp(Skill.CRAFTING.id(), 25, true);
			} else if (gem.getCatalogId() == ItemId.UNCUT_JADE.id()) {
				player.incExp(Skill.CRAFTING.id(), 20, true);
			} else {
				player.incExp(Skill.CRAFTING.id(), 15, true);
			}
		} else {
			player.getCarriedItems().getInventory().add(cutGem, true);
			String gemName = cutGem.getDef(player.getWorld()).getName();
            if (!DataConversions.inArray(gemsThatFail, gem.getCatalogId())) {
                gemName.toLowerCase();
            } else {
                if (gemName.equals("red topaz")) {
                    gemName = "Red Topaz";
                } else {
                    gemName = StringUtils.capitalize(gemName);
                }
            }
			player.message("You cut the " + gemName);
			player.playSound("chisel");
			player.incExp(Skill.CRAFTING.id(), gemDef.getExp(), true);
		}

		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay(2);
			batchGemCutting(player, gem, gemDef);
		}
	}

	private void makeLeather(Player player, final Item needle, final Item leather) {
		if (leather.getCatalogId() != ItemId.LEATHER.id()) {
			player.message("Nothing interesting happens");
			return;
		}

		if (player.getCarriedItems().getInventory().countId(ItemId.THREAD.id(), Optional.of(false)) < 1) {
			player.message("You need some thread to make anything out of leather");
			return;
		}

		final boolean customLeather = config().WANT_CUSTOM_LEATHER;

		String[] options = customLeather ?
			new String[]{
				"Armour",
				"Gloves",
				"Boots",
				"More...",
				"Cancel"
			}
			:
			new String[]{
				"Armour",
				"Gloves",
				"Boots",
				"Cancel"
			};

		int type = multi(player, options);
		if (type < 0 || type > (customLeather ? 4 : 3)) {
			return;
		}

		Item result;
		int reqLvl, exp;
		switch (type) {
			case 0:
				result = new Item(ItemId.LEATHER_ARMOUR.id(), 1);
				reqLvl = !config().OLD_SKILL_DEFS ? 14 : 7;
				exp = 100;
				break;
			case 1:
				result = new Item(ItemId.LEATHER_GLOVES.id(), 1);
				reqLvl = 1;
				exp = 55;
				break;
			case 2:
				result = new Item(ItemId.BOOTS.id(), 1);
				reqLvl = !config().OLD_SKILL_DEFS ? 7 : 2;
				exp = 65;
				break;
			case 3:
				if (!customLeather) {
					return;
				}

				String[] customMenu = new String[]{
					"Chaps",
					"Top",
					"Skirt",
					"Cancel"
				};

				int customType = multi(player, customMenu);
				if (customType < 0 || customType > 3)
					return;

				switch (customType) {
					case 0:
						result = new Item(ItemId.LEATHER_CHAPS.id(), 1);
						reqLvl = !config().OLD_SKILL_DEFS ? 10 : 5;
						exp = 80;
						break;
					case 1:
						result = new Item(ItemId.LEATHER_TOP.id(), 1);
						reqLvl = !config().OLD_SKILL_DEFS ? 14 : 7;
						exp = 100;
						break;
					case 2:
						result = new Item(ItemId.LEATHER_SKIRT.id(), 1);
						reqLvl = !config().OLD_SKILL_DEFS ? 10 : 5;
						exp = 80;
						break;
					default:
						return;
				}
				break;
			default:
				return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(leather.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchLeather(player, leather, result, reqLvl, exp);
	}

	private void batchLeather(Player player, Item leather, Item result, int reqLvl, int exp) {
		if (!canReceive(player, result)) {
			player.message("Your client does not support the desired object");
			return;
		}
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < reqLvl) {
			player.playerServerMessage(MessageType.QUEST, "You need to have a crafting of level " + reqLvl + " or higher to make " + result.getDef(player.getWorld()).getName());
			return;
		}
		if (checkFatigue(player)) return;

		Item item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(leather.getCatalogId(), Optional.of(false))
		);
		if (item == null) return;

		player.getCarriedItems().remove(item);
		delay();
		player.message("You make some " + result.getDef(player.getWorld()).getName());
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skill.CRAFTING.id(), exp, true);
		// A reel of thread accounts for 5 uses
		if (!player.getCache().hasKey("part_reel_thread")) {
			player.getCache().set("part_reel_thread", 1);
		} else {
			int parts = player.getCache().getInt("part_reel_thread");
			if (parts >= 4) {
				player.message("You use up one of your reels of thread");
				player.getCache().remove("part_reel_thread");
				player.getCarriedItems().remove(new Item(ItemId.THREAD.id()));
				if(player.getCarriedItems().getInventory().countId(ItemId.THREAD.id(), Optional.of(false)) <= 0) {
					return;
				}
			} else {
				player.getCache().put("part_reel_thread", parts + 1);
			}
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchLeather(player, leather, result, reqLvl, exp);
		}
	}

	private void useWool(Player player, final Item woolBall, final Item item) {
		int newID;
		switch (ItemId.getById(item.getCatalogId())) {
			case UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN:
				newID = ItemId.UNBLESSED_HOLY_SYMBOL.id();
				break;
			case UNSTRUNG_UNHOLY_SYMBOL_OF_ZAMORAK:
				newID = ItemId.UNBLESSED_UNHOLY_SYMBOL_OF_ZAMORAK.id();
				break;
			case UNSTRUNG_GOLD_AMULET:
				newID = ItemId.GOLD_AMULET.id();
				break;
			case UNSTRUNG_SAPPHIRE_AMULET:
				newID = ItemId.SAPPHIRE_AMULET.id();
				break;
			case UNSTRUNG_EMERALD_AMULET:
				newID = ItemId.EMERALD_AMULET.id();
				break;
			case UNSTRUNG_RUBY_AMULET:
				newID = ItemId.RUBY_AMULET.id();
				break;
			case UNSTRUNG_DIAMOND_AMULET:
				newID = ItemId.DIAMOND_AMULET.id();
				break;
			case UNSTRUNG_DRAGONSTONE_AMULET:
				newID = ItemId.UNENCHANTED_DRAGONSTONE_AMULET.id();
				break;
			default:
				return;
		}
		int woolAmount = player.getCarriedItems().getInventory().countId(woolBall.getCatalogId(), Optional.of(false));
		int amuletAmount = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = Math.min(woolAmount, amuletAmount);
		}

		startbatch(repeat);
		batchString(player, item, woolBall, newID);
	}

	private void batchString(Player player, Item item, Item woolBall, int newID) {
		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		woolBall = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(woolBall.getCatalogId(), Optional.of(false))
		);
		if (item == null || woolBall == null) return;

		player.getCarriedItems().remove(woolBall);
		player.getCarriedItems().remove(item);
		player.message("You put some string on your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().getInventory().add(new Item(newID));
		delay();

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchString(player, item, woolBall, newID);
		}
	}

	private void useWater(Player player, Item water, Item item) {
		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = Math.min(player.getCarriedItems().getInventory().countId(water.getCatalogId(), Optional.of(false)),
				player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false)));
		}

		startbatch(repeat);
		batchWaterClay(player, water, item);
	}

	private void batchWaterClay(Player player, Item water, Item item) {
		int jugID = Formulae.getEmptyJug(water.getCatalogId());
		if (jugID == -1) return;

		water = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(water.getCatalogId(), Optional.of(false))
		);
		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (water == null || item == null) return;
		player.getCarriedItems().remove(water);
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(jugID));
		player.getCarriedItems().getInventory().add(new Item(ItemId.SOFT_CLAY.id()));
		player.message("You mix the clay and water");
		delay(2);
		player.message("You now have some soft workable clay");

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchWaterClay(player, water, item);
		}
	}

	private String potteryItemName(String rawName) {
		String uncapName = rawName.toLowerCase();
		if (uncapName.startsWith("unfired ")) {
			return uncapName.substring(8);
		}
		return uncapName;
	}

	private boolean checkFatigue(Player player) {
		if (config().WANT_FATIGUE
				&& config().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
			player.message("You are too tired to craft");
			return true;
		}
		return false;
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		ItemGemDef gemDef = player.getWorld().getServer().getEntityHandler().getItemGemDef(item1.getCatalogId());
		ItemGemDef gemDef2 = player.getWorld().getServer().getEntityHandler().getItemGemDef(item2.getCatalogId());
		int item1ID = item1.getCatalogId();
		int item2ID = item2.getCatalogId();
		if (item1ID == ItemId.CHISEL.id() && (gemDef != null || gemDef2 != null)) {
			return true;
		} else if (item2ID == ItemId.CHISEL.id() && (gemDef != null || gemDef2 != null)) {
			return true;
		} else if (item1ID == ItemId.CHISEL.id() && item2ID == ItemId.KING_BLACK_DRAGON_SCALE.id()) {
			return true;
		} else if (item2ID == ItemId.CHISEL.id() && item1ID == ItemId.KING_BLACK_DRAGON_SCALE.id()) {
			return true;
		} else if (item1ID == ItemId.GLASSBLOWING_PIPE.id()) {
			return true;
		} else if (item2ID == ItemId.GLASSBLOWING_PIPE.id()) {
			return true;
		} else if (item1ID == ItemId.NEEDLE.id()) {
			return true;
		} else if (item2ID == ItemId.NEEDLE.id()) {
			return true;
		} else if (item1ID == ItemId.BALL_OF_WOOL.id()) {
			return true;
		} else if (item2ID == ItemId.BALL_OF_WOOL.id()) {
			return true;
		} else if ((item1ID == ItemId.BUCKET_OF_WATER.id() || item1ID == ItemId.JUG_OF_WATER.id()) && item2ID == ItemId.CLAY.id()) {
			return true;
		} else if ((item2ID == ItemId.BUCKET_OF_WATER.id() || item2ID == ItemId.JUG_OF_WATER.id()) && item1ID == ItemId.CLAY.id()) {
			return true;
		} else
			return item1ID == ItemId.MOLTEN_GLASS.id() && item2ID == ItemId.LENS_MOULD.id() || item1ID == ItemId.LENS_MOULD.id() && item2ID == ItemId.MOLTEN_GLASS.id();
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return craftingTypeChecks(obj, item, player);
	}
}
