package com.openrsc.server.plugins.authentic.skills.smithing;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.CarriedItems;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Smelting implements UseLocTrigger {

	public static final int FURNACE = 118;
	public static final int LAVA_FURNACE = 1284;

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == FURNACE && !DataConversions.inArray(new int[]{ItemId.GOLD_BAR.id(), ItemId.SILVER_BAR.id(), ItemId.SAND.id(), ItemId.GOLD_BAR_FAMILYCREST.id()}, item.getCatalogId())) {
			if (item.getCatalogId() == ItemId.STEEL_BAR.id()) {
				if (player.getCarriedItems().hasCatalogID(ItemId.CANNON_AMMO_MOULD.id())) {
					int repeat = 1;
					if (config().BATCH_PROGRESSION) {
						repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId());
					}

					startbatch(repeat);
					handleCannonBallSmelting(player);
				} else { // No mould
					player.message("you heat the steel bar");
				}
			} else {
				handleRegularSmelting(item, player, obj);
			}
		} else if (obj.getID() == LAVA_FURNACE) {
			int stage = player.getCache().hasKey("miniquest_dwarf_youth_rescue") ? player.getCache().getInt("miniquest_dwarf_youth_rescue") : -1;
			if (stage != 2) {
				player.message("You don't have permission to use this");
				return;
			}
			int amount = 0;
			if (item.getCatalogId() == ItemId.DRAGON_SWORD.id())
				amount = 1;
			else if (item.getCatalogId() == ItemId.DRAGON_AXE.id())
				amount = 2;
			else {
				player.message("Nothing interesting happens");
				return;
			}
			if (getCurrentLevel(player, Skill.SMITHING.id()) < 90) {
				player.message("90 smithing is required to use this forge");
				return;
			}
			if (player.getCarriedItems().remove(new Item(item.getCatalogId())) > -1) {
				player.message("You smelt the " + item.getDef(player.getWorld()).getName() + "...");
				delay(5);
				player.message("And retrieve " + amount + " dragon bar" + (amount > 1? "s":""));
				give(player, ItemId.DRAGON_BAR.id(), amount);
			}
		}
	}

	private void handleCannonBallSmelting(Player player) {
		if (getCurrentLevel(player, Skill.SMITHING.id()) < 30) {
			player.message("You need at least level 30 smithing to make cannon balls");
			return;
		}
		if (player.getQuestStage(Quests.DWARF_CANNON) != -1) {
			player.message("You need to complete the dwarf cannon quest");
			return;
		}
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to smelt a cannon ball");
				return;
			}
		}
		if (player.getCarriedItems().getInventory().countId(ItemId.STEEL_BAR.id()) < 1) {
			player.message("You have no steel bars left");
			return;
		}

		thinkbubble(new Item(ItemId.MULTI_CANNON_BALL.id(), 1));
		int messagedelay = config().BATCH_PROGRESSION ? 1 : 2;
		mes("you heat the steel bar into a liquid state");
		delay(messagedelay);
		mes("and pour it into your cannon ball mould");
		delay(messagedelay);
		mes("you then leave it to cool for a short while");
		delay(messagedelay);

		player.getCarriedItems().remove(new Item(ItemId.STEEL_BAR.id()));
		// If you are fatigued, you should still make the cannonball, it just
		// falls to the floor.
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED == 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("you are too tired to lift the ammo");
				player.getWorld().registerItem(new GroundItem(
					player.getWorld(),
					ItemId.MULTI_CANNON_BALL.id(),
					player.getX(),
					player.getY(),
					1,
					player
				));
				return;
			}
		}
		player.incExp(Skill.SMITHING.id(), 100, true);
		player.getCarriedItems().getInventory().add(new Item(ItemId.MULTI_CANNON_BALL.id()));
		if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.DWARVEN_RING.id())) {
			player.getCarriedItems().getInventory().add(new Item(ItemId.MULTI_CANNON_BALL.id(), config().DWARVEN_RING_BONUS));
			int charges;
			if (player.getCache().hasKey("dwarvenring")) {
				charges = player.getCache().getInt("dwarvenring") + 1;
				if (charges >= config().DWARVEN_RING_USES) {
					player.getCache().remove("dwarvenring");
					player.getCarriedItems().shatter(new Item(ItemId.DWARVEN_RING.id()));
				} else
					player.getCache().put("dwarvenring", charges);
			}
			else
				player.getCache().put("dwarvenring", 1);

		}
		player.message("it's very heavy");

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			player.message("you repeat the process");
			delay();
			handleCannonBallSmelting(player);
		}
	}

	private void handleRegularSmelting(final Item item, Player player, final GameObject obj) {
		if (!inArray(item.getCatalogId(),
				Smelt.ADAMANTITE_ORE.getID(), Smelt.COAL.getID(), Smelt.COPPER_ORE.getID(),
				Smelt.IRON_ORE.getID(), Smelt.GOLD.getID(), Smelt.MITHRIL_ORE.getID(),
				Smelt.RUNITE_ORE.getID(), Smelt.SILVER.getID(), Smelt.TIN_ORE.getID(),
				ItemId.GOLD_FAMILYCREST.id())) {
			player.message("Nothing interesting happens");
			return;
		}
		String formattedName = item.getDef(player.getWorld()).getName().toUpperCase().replaceAll(" ", "_");
		Smelt smelt;
		CarriedItems ci = player.getCarriedItems();
		if (item.getCatalogId() == Smelt.IRON_ORE.getID()
				&& getCurrentLevel(player, Skill.SMITHING.id()) >= 30
				&& ci.getInventory().countId(Smelt.COAL.getID()) >= 2) {
			String coalChange = player.getWorld().getServer().getEntityHandler().getItemDef(Smelt.COAL.getID()).getName().toUpperCase();
			smelt = Smelt.valueOf(coalChange);
		} else {
			smelt = Smelt.valueOf(formattedName);
		}

		if (!ci.getInventory().contains(item)) {
			return;
		}

		if (obj.getLocation().equals(Point.location(399, 840))) {
			// furnace in shilo village
			if ((player.getLocation().getY() == 841 && !player.withinRange(obj, 2)) && !player.withinRange90Deg(obj, 2)) {
				return;
			}
		} else {
			// some furnaces the player is 2 spaces away
			if (!player.withinRange(obj, 1) && !player.withinRange90Deg(obj, 2)) {
				return;
			}
		}

		thinkbubble(item);
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to smelt this ore");
				return;
			}
		}
		if (getCurrentLevel(player, Skill.SMITHING.id()) < smelt.getRequiredLevel()) {
			String smeltOrWork = smelt.getSmeltBarId() == ItemId.SILVER_BAR.id()
				|| smelt.getSmeltBarId() == ItemId.GOLD_BAR.id()
				|| smelt.getSmeltBarId() == ItemId.GOLD_BAR_FAMILYCREST.id() ? "work " : "smelt ";
			ItemDefinition barDef = player.getWorld().getServer().getEntityHandler().getItemDef(smelt.getSmeltBarId());
			player.playerServerMessage(MessageType.QUEST,
				String.format("You need to be at least level-%d smithing to %s %s",
				smelt.getRequiredLevel(), smeltOrWork, barDef.getName().toLowerCase().replaceAll("bar", "")));
			if (smelt.getSmeltBarId() == ItemId.IRON_BAR.id()) {
				player.playerServerMessage(MessageType.QUEST, "Practice your smithing using tin and copper to make bronze");
			}
			return;
		}
		if (ci.getInventory().countId(smelt.getReqOreId()) < smelt.getReqOreAmount()
				|| (ci.getInventory().countId(smelt.getID()) < smelt.getOreAmount() && smelt.getReqOreAmount() != -1)) {
			if (smelt.getID() == Smelt.TIN_ORE.getID() || item.getCatalogId() == Smelt.COPPER_ORE.getID()) {
				player.playerServerMessage(MessageType.QUEST, "You also need some "
					+ (item.getCatalogId() == Smelt.TIN_ORE.getID() ? "copper" : "tin") + " to make bronze");
			}
			else if (smelt.getID() == Smelt.COAL.getID() && (ci.getInventory().countId(Smelt.IRON_ORE.getID()) < 1 || ci.getInventory().countId(Smelt.COAL.getID()) <= 1)) {
				player.playerServerMessage(MessageType.QUEST, "You need 1 iron-ore and 2 coal to make steel");
			}
			else {
				player.playerServerMessage(MessageType.QUEST,
					String.format("You need %d heaps of %s to smelt %s", smelt.getReqOreAmount(),
					player.getWorld().getServer().getEntityHandler().getItemDef(smelt.getReqOreId()).getName().toLowerCase(),
					item.getDef(player.getWorld()).getName().toLowerCase().replaceAll("ore", "")));
			}
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			// repeat = Formulae.getRepeatTimes(player, Skill.SMITHING.id();
			int carriedOre = player.getCarriedItems().getInventory().countId(
				smelt.getID(), Optional.of(false));
			if (smelt.getReqOreId() == -1) {
				repeat = carriedOre;
			} else {
				repeat = Math.min(
					carriedOre,
					player.getCarriedItems().getInventory().countId(
						smelt.getReqOreId(), Optional.of(false)) / smelt.requestedOreAmount
				);
			}
		}

		startbatch(repeat);
		batchSmelt(player, item, smelt);
	}

	private void batchSmelt(Player player, Item item, Smelt smelt) {
		CarriedItems ci = player.getCarriedItems();
		item = ci.getInventory().get(
			ci.getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (item == null) return;

		String barName = player.getWorld().getServer().getEntityHandler().getItemDef(
			smelt.getSmeltBarId()).getName().toLowerCase().replaceAll("bar", "");
		player.playerServerMessage(MessageType.QUEST, smeltString(player.getWorld(), smelt, item));
		delay(3);

		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to smelt this ore");
				return;
			}
		}
		if (getCurrentLevel(player, Skill.SMITHING.id()) < smelt.getRequiredLevel()) {
			String smeltOrWork = smelt.getSmeltBarId() == ItemId.SILVER_BAR.id() || smelt.getSmeltBarId() == ItemId.GOLD_BAR.id()
				|| smelt.getSmeltBarId() == ItemId.GOLD_BAR_FAMILYCREST.id() ? "work " : "smelt ";
			player.playerServerMessage(MessageType.QUEST,
				String.format("You need to be at least level-%d smithing to %s %s",
				smelt.getRequiredLevel(), smeltOrWork, barName));
			if (smelt.getSmeltBarId() == ItemId.IRON_BAR.id())
				player.playerServerMessage(MessageType.QUEST, "Practice your smithing using tin and copper to make bronze");
			return;
		}
		if (ci.getInventory().countId(smelt.getReqOreId()) < smelt.getReqOreAmount()
				|| (ci.getInventory().countId(smelt.getID()) < smelt.getOreAmount() && smelt.getReqOreAmount() != -1)) {
			if (smelt.getID() == Smelt.COAL.getID() && (ci.getInventory().countId(Smelt.IRON_ORE.getID()) < 1
					|| ci.getInventory().countId(Smelt.COAL.getID()) <= 1)) {
				player.playerServerMessage(MessageType.QUEST, "You need 1 iron-ore and 2 coal to make steel");
				return;
			}
			if (smelt.getID() == Smelt.TIN_ORE.getID() || item.getCatalogId() == Smelt.COPPER_ORE.getID()) {
				player.playerServerMessage(MessageType.QUEST, "You also need some "
					+ (item.getCatalogId() == Smelt.TIN_ORE.getID() ? "copper" : "tin") + " to make bronze");
				return;
			} else {
				player.playerServerMessage(MessageType.QUEST,
					String.format("You need %d heaps of %s to smelt %s",
					smelt.getReqOreAmount(),
					player.getWorld().getServer().getEntityHandler().getItemDef(smelt.getReqOreId()).getName().toLowerCase(),
					item.getDef(player.getWorld()).getName().toLowerCase().replaceAll("ore", "")));
				return;
			}
		}
		thinkbubble(item);
		if (ci.getInventory().countId(item.getCatalogId()) > 0) {
			boolean skillcape = false;
			if ((smelt.getID() == ItemId.COAL.id()
				|| smelt.getReqOreId() == ItemId.COAL.id())
				&& SkillCapes.shouldActivate(player, ItemId.SMITHING_CAPE)) {

				skillcape = true;
				player.message("You heat the furnace using half the usual amount of coal");
			}

			if (item.getCatalogId() == ItemId.GOLD_FAMILYCREST.id()) {
				ci.remove(new Item(ItemId.GOLD_FAMILYCREST.id()));
			}
			else {
				int toUse = smelt.getOreAmount();
				if (skillcape && smelt.getID() == ItemId.COAL.id()) {
					toUse = smelt.getOreAmount() / 2;
				}
				for (int i = 0; i < toUse; i++) {
					ci.remove(new Item(smelt.getID(), 1));
				}
			}

			if (smelt.getReqOreAmount() > 0) {
				int toUse = smelt.getReqOreAmount();
				if (skillcape && smelt.getReqOreId() == ItemId.COAL.id()) {
					toUse = smelt.getReqOreAmount() / 2;
				}
				for (int i = 0; i < toUse; i++) {
					ci.remove(new Item(smelt.getReqOreId(), 1));
				}
			}

			if (smelt.getID() == Smelt.IRON_ORE.getID() && DataConversions.random(0, 1) == 1) {
				if (ci.getEquipment().hasEquipped(ItemId.RING_OF_FORGING.id())) {
					player.message("@or1@Your ring of forging shines brightly");
					give(player, smelt.getSmeltBarId(), 1);
					if (player.getCache().hasKey("ringofforging")) {
						int ringCheck = player.getCache().getInt("ringofforging");
						if (ringCheck + 1 == config().RING_OF_FORGING_USES) {
							player.getCache().remove("ringofforging");
							ci.shatter(new Item(ItemId.RING_OF_FORGING.id()));
						} else {
							player.getCache().set("ringofforging", ringCheck + 1);
						}
					} else {
						player.getCache().put("ringofforging", 1);
						player.message("@or1@You start a new ring of forging");
					}
				} else {
					player.message("The ore is too impure and you fail to refine it");
				}
			} else {
				if (item.getCatalogId() == ItemId.GOLD_FAMILYCREST.id()) {
					give(player, ItemId.GOLD_BAR_FAMILYCREST.id(), 1);
				}
				else {
					give(player, smelt.getSmeltBarId(), 1);
				}

				player.playerServerMessage(MessageType.QUEST, "You retrieve a bar of " + barName);

				/** Gauntlets of Goldsmithing provide an additional 23 experience when smelting gold ores **/
				if (ci.getEquipment().hasEquipped(ItemId.GAUNTLETS_OF_GOLDSMITHING.id()) && new Item(smelt.getSmeltBarId()).getCatalogId() == ItemId.GOLD_BAR.id()) {
					player.incExp(Skill.SMITHING.id(), smelt.getXp() + 45, true);
				} else {
					player.incExp(Skill.SMITHING.id(), smelt.getXp(), true);
				}
			}

			// Repeat
			updatebatch();
			if (!ifinterrupted() && !isbatchcomplete()) {
				if (item.getCatalogId() == Smelt.IRON_ORE.getID()
					&& getCurrentLevel(player, Skill.SMITHING.id()) >= 30
					&& ci.getInventory().countId(Smelt.COAL.getID()) >= 2) {
					String coalChange = player.getWorld().getServer().getEntityHandler().getItemDef(Smelt.COAL.getID()).getName().toUpperCase();
					smelt = Smelt.valueOf(coalChange);
				} else {
					String formattedName = item.getDef(player.getWorld()).getName().toUpperCase().replaceAll(" ", "_");
					smelt = Smelt.valueOf(formattedName);
				}
				batchSmelt(player, item, smelt);
			}
		}
	}

	private String smeltString(World world, Smelt smelt, Item item) {
		String message = null;
		if (smelt.getSmeltBarId() == ItemId.BRONZE_BAR.id()) {
			message = "You smelt the copper and tin together in the furnace";
		} else if (smelt.getSmeltBarId() == ItemId.MITHRIL_BAR.id() || smelt.getSmeltBarId() == ItemId.ADAMANTITE_BAR.id()|| smelt.getSmeltBarId() == ItemId.RUNITE_BAR.id()) {
			message = "You place the " + item.getDef(world).getName().toLowerCase().replaceAll(" ore", "") + " and " + smelt.getReqOreAmount() + " heaps of " + world.getServer().getEntityHandler().getItemDef(smelt.getReqOreId()).getName().toLowerCase() + " into the furnace";
		} else if (smelt.getSmeltBarId() == ItemId.STEEL_BAR.id()) {
			message = "You place the iron and 2 heaps of coal into the furnace";
		} else if (smelt.getSmeltBarId() == ItemId.IRON_BAR.id()) {
			message = "You smelt the " + item.getDef(world).getName().toLowerCase().replaceAll(" ore", "") + " in the furnace";
		} else if (smelt.getSmeltBarId() == ItemId.SILVER_BAR.id() || smelt.getSmeltBarId() == ItemId.GOLD_BAR.id() || smelt.getSmeltBarId() == ItemId.GOLD_BAR_FAMILYCREST.id()) {
			message = "You place a lump of " + item.getDef(world).getName().toLowerCase().replaceAll(" ore", "") + " in the furnace";
		}
		return message;
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (obj.getID() == FURNACE && !DataConversions.inArray(new int[]{ItemId.GOLD_BAR.id(), ItemId.SILVER_BAR.id(), ItemId.SODA_ASH.id(), ItemId.SAND.id(), ItemId.GOLD_BAR_FAMILYCREST.id()}, item.getCatalogId()))
			|| obj.getID() == LAVA_FURNACE;
	}

	enum Smelt {
		COPPER_ORE(ItemId.COPPER_ORE.id(), 25, 1, 1, 169, 202, 1),
		TIN_ORE(ItemId.TIN_ORE.id(), 25, 1, 1, 169, 150, 1),
		IRON_ORE(ItemId.IRON_ORE.id(), 50, 15, 1, 170, -1, -1),
		SILVER(ItemId.SILVER.id(), 54, 20, 1, 384, -1, -1),
		GOLD(ItemId.GOLD.id(), 90, 40, 1, 172, -1, -1),
		MITHRIL_ORE(ItemId.MITHRIL_ORE.id(), 120, 50, 1, 173, 155, 4),
		ADAMANTITE_ORE(ItemId.ADAMANTITE_ORE.id(), 150, 70, 1, 174, 155, 6),
		COAL(ItemId.COAL.id(), 70, 30, 2, 171, 151, 1),
		RUNITE_ORE(ItemId.RUNITE_ORE.id(), 200, 85, 1, 408, 155, 8);

		private final int id;
		private final int xp;
		private final int requiredLevel;
		private final int oreAmount;
		private final int smeltBarId;
		private final int requestedOreId;
		private final int requestedOreAmount;

		Smelt(int itemId, int exp, int req, int oreAmount, int barId, int reqOreId, int reqOreAmount) {
			this.id = itemId;
			this.xp = exp;
			this.requiredLevel = req;
			this.oreAmount = oreAmount;
			this.smeltBarId = barId;
			this.requestedOreId = reqOreId;
			this.requestedOreAmount = reqOreAmount;
		}

		public int getID() {
			return id;
		}

		public int getXp() {
			return xp;
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public int getOreAmount() {
			return oreAmount;
		}

		public int getSmeltBarId() {
			return smeltBarId;
		}

		public int getReqOreId() {
			return requestedOreId;
		}

		public int getReqOreAmount() {
			return requestedOreAmount;
		}
	}
}
