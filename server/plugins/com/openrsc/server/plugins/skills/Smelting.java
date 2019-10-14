package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class Smelting implements InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener {

	public static final int FURNACE = 118;

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == FURNACE && !DataConversions.inArray(new int[]{ItemId.GOLD_BAR.id(), ItemId.SILVER_BAR.id(), ItemId.SAND.id(), ItemId.GOLD_BAR_FAMILYCREST.id()}, item.getID())) {
			if (item.getID() == ItemId.STEEL_BAR.id()) {
				if (p.getInventory().hasItemId(ItemId.CANNON_AMMO_MOULD.id())) {
					if (getCurrentLevel(p, Skills.SMITHING) < 30) {
						p.message("You need at least level 30 smithing to make cannon balls");
						return;
					}
					if (p.getQuestStage(Quests.DWARF_CANNON) != -1) {
						p.message("You need to complete the dwarf cannon quest");
						return;
					}
					showBubble(p, new Item(ItemId.MULTI_CANNON_BALL.id(), 1));
					int messagedelay = p.getWorld().getServer().getConfig().BATCH_PROGRESSION ? 200 : 1700;
					int delay = p.getWorld().getServer().getConfig().BATCH_PROGRESSION ? 7200: 2100;
					message(p, messagedelay, "you heat the steel bar into a liquid state",
						"and pour it into your cannon ball mould",
						"you then leave it to cool for a short while");

					p.setBatchEvent(new BatchEvent(p.getWorld(), p, delay, "Smelting", p.getInventory().countId(item.getID()), false) {
						@Override
						public void action() {
							getOwner().incExp(Skills.SMITHING, 100, true);
							getOwner().getInventory().replace(ItemId.STEEL_BAR.id(), ItemId.MULTI_CANNON_BALL.id(),false);
							if (Functions.isWielding(getOwner(), ItemId.DWARVEN_RING.id())) {
								getOwner().getInventory().add(new Item(ItemId.MULTI_CANNON_BALL.id(), getWorld().getServer().getConfig().DWARVEN_RING_BONUS),false);
								int charges;
								if (getOwner().getCache().hasKey("dwarvenring")) {
									charges = getOwner().getCache().getInt("dwarvenring") + 1;
									if (charges >= getWorld().getServer().getConfig().DWARVEN_RING_USES) {
										getOwner().getCache().remove("dwarvenring");
										getOwner().getInventory().shatter(ItemId.DWARVEN_RING.id());
									} else
										getOwner().getCache().put("dwarvenring", charges);
								}
								else
									getOwner().getCache().put("dwarvenring", 1);

							}
							ActionSender.sendInventory(getOwner());
							getOwner().message("it's very heavy");

							if (!isCompleted()) {
								getOwner().message("you repeat the process");
								showBubble(getOwner(), new Item(ItemId.MULTI_CANNON_BALL.id(), 1));
							}
							if (getCurrentLevel(getOwner(), Skills.SMITHING) < 30) {
								getOwner().message("You need at least level 30 smithing to make cannon balls");
								interrupt();
								return;
							}
							if (getOwner().getQuestStage(Quests.DWARF_CANNON) != -1) {
								getOwner().message("You need to complete the dwarf cannon quest");
								interrupt();
								return;
							}
							if (getWorld().getServer().getConfig().WANT_FATIGUE) {
								if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
									&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
									getOwner().message("You are too tired to smelt cannon ball");
									interrupt();
									return;
								}
							}
							if (getOwner().getInventory().countId(ItemId.STEEL_BAR.id()) < 1) {
								getOwner().message("You have no steel bars left");
								interrupt();
								return;
							}
						}
					});
				} else { // No mould
					p.message("You heat the steel bar");
				}
			} else {
				handleRegularSmelting(item, p, obj);
			}
		}
	}

	private void handleRegularSmelting(final Item item, Player p, final GameObject obj) {
		if (!inArray(item.getID(), Smelt.ADAMANTITE_ORE.getID(), Smelt.COAL.getID(), Smelt.COPPER_ORE.getID(), Smelt.IRON_ORE.getID(), Smelt.GOLD.getID(), Smelt.MITHRIL_ORE.getID(), Smelt.RUNITE_ORE.getID(), Smelt.SILVER.getID(), Smelt.TIN_ORE.getID(), ItemId.GOLD_FAMILYCREST.id())) {
			p.message("Nothing interesting happens");
			return;
		}
		String formattedName = item.getDef(p.getWorld()).getName().toUpperCase().replaceAll(" ", "_");
		Smelt smelt;
		if (item.getID() == Smelt.IRON_ORE.getID() && getCurrentLevel(p, Skills.SMITHING) >= 30 && p.getInventory().countId(Smelt.COAL.getID()) >= 2) {
			String coalChange = p.getWorld().getServer().getEntityHandler().getItemDef(Smelt.COAL.getID()).getName().toUpperCase();
			smelt = Smelt.valueOf(coalChange);
		} else {
			smelt = Smelt.valueOf(formattedName);
		}

		if (!p.getInventory().contains(item)) {
			return;
		}

		if (obj.getLocation().equals(Point.location(399, 840))) {
			// furnace in shilo village
			if ((p.getLocation().getY() == 841 && !p.withinRange(obj, 2)) && !p.withinRange90Deg(obj, 2)) {
				return;
			}
		} else {
			// some furnaces the player is 2 spaces away
			if (!p.withinRange(obj, 1) && !p.withinRange90Deg(obj, 2)) {
				return;
			}
		}

		showBubble(p, item);
		if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (p.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
				&& p.getFatigue() >= p.MAX_FATIGUE) {
				p.message("You are too tired to smelt this ore");
				return;
			}
		}
		if (getCurrentLevel(p, Skills.SMITHING) < smelt.getRequiredLevel()) {
			p.playerServerMessage(MessageType.QUEST, "You need to be at least level-" + smelt.getRequiredLevel() + " smithing to " + (smelt.getSmeltBarId() == ItemId.SILVER_BAR.id() || smelt.getSmeltBarId() == ItemId.GOLD_BAR.id() || smelt.getSmeltBarId() == ItemId.GOLD_BAR_FAMILYCREST.id() ? "work " : "smelt ") + p.getWorld().getServer().getEntityHandler().getItemDef(smelt.getSmeltBarId()).getName().toLowerCase().replaceAll("bar", ""));
			if (smelt.getSmeltBarId() == ItemId.IRON_BAR.id())
				p.playerServerMessage(MessageType.QUEST, "Practice your smithing using tin and copper to make bronze");
			return;
		}
		if (p.getInventory().countId(smelt.getReqOreId()) < smelt.getReqOreAmount() || (p.getInventory().countId(smelt.getID()) < smelt.getOreAmount() && smelt.getReqOreAmount() != -1)) {
			if (smelt.getID() == Smelt.TIN_ORE.getID() || item.getID() == Smelt.COPPER_ORE.getID()) {
				p.playerServerMessage(MessageType.QUEST, "You also need some " + (item.getID() == Smelt.TIN_ORE.getID() ? "copper" : "tin") + " to make bronze");
				return;
			}
			if (smelt.getID() == Smelt.COAL.getID() && (p.getInventory().countId(Smelt.IRON_ORE.getID()) < 1 || p.getInventory().countId(Smelt.COAL.getID()) <= 1)) {
				p.playerServerMessage(MessageType.QUEST, "You need 1 iron-ore and 2 coal to make steel");
				return;
			} else {
				p.playerServerMessage(MessageType.QUEST, "You need " + smelt.getReqOreAmount() + " heaps of " + p.getWorld().getServer().getEntityHandler().getItemDef(smelt.getReqOreId()).getName().toLowerCase()
					+ " to smelt "
					+ item.getDef(p.getWorld()).getName().toLowerCase().replaceAll("ore", ""));
				return;
			}
		}

		p.playerServerMessage(MessageType.QUEST, smeltString(p.getWorld(), smelt, item));
		p.setBatchEvent(new BatchEvent(p.getWorld(), p, 1800, "Smelt", Formulae.getRepeatTimes(p, Skills.SMITHING), false) {
			@Override
			public void action() {
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().message("You are too tired to smelt this ore");
						interrupt();
						return;
					}
				}
				if (getCurrentLevel(getOwner(), Skills.SMITHING) < smelt.getRequiredLevel()) {
					getOwner().playerServerMessage(MessageType.QUEST, "You need to be at least level-" + smelt.getRequiredLevel() + " smithing to " + (smelt.getSmeltBarId() == ItemId.SILVER_BAR.id() || smelt.getSmeltBarId() == ItemId.GOLD_BAR.id() || smelt.getSmeltBarId() == ItemId.GOLD_BAR_FAMILYCREST.id() ? "work " : "smelt ") + getWorld().getServer().getEntityHandler().getItemDef(smelt.getSmeltBarId()).getName().toLowerCase().replaceAll("bar", ""));
					if (smelt.getSmeltBarId() == ItemId.IRON_BAR.id())
						getOwner().playerServerMessage(MessageType.QUEST, "Practice your smithing using tin and copper to make bronze");
					interrupt();
					return;
				}
				if (getOwner().getInventory().countId(smelt.getReqOreId()) < smelt.getReqOreAmount() || (getOwner().getInventory().countId(smelt.getID()) < smelt.getOreAmount() && smelt.getReqOreAmount() != -1)) {
					if (smelt.getID() == Smelt.COAL.getID() && (getOwner().getInventory().countId(Smelt.IRON_ORE.getID()) < 1 || getOwner().getInventory().countId(Smelt.COAL.getID()) <= 1)) {
						getOwner().playerServerMessage(MessageType.QUEST, "You need 1 iron-ore and 2 coal to make steel");
						interrupt();
						return;
					}
					if (smelt.getID() == Smelt.TIN_ORE.getID() || item.getID() == Smelt.COPPER_ORE.getID()) {
						getOwner().playerServerMessage(MessageType.QUEST, "You also need some " + (item.getID() == Smelt.TIN_ORE.getID() ? "copper" : "tin") + " to make bronze");
						interrupt();
						return;
					} else {
						getOwner().playerServerMessage(MessageType.QUEST, "You need " + smelt.getReqOreAmount() + " heaps of " + getWorld().getServer().getEntityHandler().getItemDef(smelt.getReqOreId()).getName().toLowerCase()
							+ " to smelt "
							+ item.getDef(getWorld()).getName().toLowerCase().replaceAll("ore", ""));
						interrupt();
						return;
					}
				}
				showBubble(getOwner(), item);
				if (getOwner().getInventory().countId(item.getID()) > 0) {
					if (item.getID() == ItemId.GOLD_FAMILYCREST.id())
						removeItem(getOwner(), ItemId.GOLD_FAMILYCREST.id(), 1);
					else
						getOwner().getInventory().remove(smelt.getID(), smelt.getOreAmount());

					if (smelt.getReqOreAmount() > 0)
						getOwner().getInventory().remove(smelt.getReqOreId(), smelt.getReqOreAmount());

					if (smelt.getID() == Smelt.IRON_ORE.getID() && DataConversions.random(0, 1) == 1) {
						if (Functions.isWielding(getOwner(), ItemId.RING_OF_FORGING.id())) {
							getOwner().message("Your ring of forging shines brightly");
							addItem(getOwner(), smelt.getSmeltBarId(), 1);
							if (getOwner().getCache().hasKey("ringofforging")) {
								int ringCheck = getOwner().getCache().getInt("ringofforging");
								if (ringCheck + 1 == getWorld().getServer().getConfig().RING_OF_FORGING_USES) {
									getOwner().getCache().remove("ringofforging");
									getOwner().getInventory().shatter(ItemId.RING_OF_FORGING.id());
								} else {
									getOwner().getCache().set("ringofforging", ringCheck + 1);
								}
							} else {
								getOwner().getCache().put("ringofforging", 1);
								getOwner().message("You start a new ring of forging");
							}
						} else {
							getOwner().message("The ore is too impure and you fail to refine it");
						}
					} else {
						if (item.getID() == ItemId.GOLD_FAMILYCREST.id())
							addItem(getOwner(), ItemId.GOLD_BAR_FAMILYCREST.id(), 1);
						else
							addItem(getOwner(), smelt.getSmeltBarId(), 1);

						getOwner().playerServerMessage(MessageType.QUEST, "You retrieve a bar of " + new Item(smelt.getSmeltBarId()).getDef(getWorld()).getName().toLowerCase().replaceAll("bar", ""));

						/** Gauntlets of Goldsmithing provide an additional 23 experience when smelting gold ores **/
						if (getOwner().getInventory().wielding(ItemId.GAUNTLETS_OF_GOLDSMITHING.id()) && new Item(smelt.getSmeltBarId()).getID() == ItemId.GOLD_BAR.id()) {
							getOwner().incExp(Skills.SMITHING, smelt.getXp() + 45, true);
						} else {
							getOwner().incExp(Skills.SMITHING, smelt.getXp(), true);
						}
					}
				} else {
					interrupt();
				}
			}
		});
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
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return obj.getID() == FURNACE && !DataConversions.inArray(new int[]{ItemId.GOLD_BAR.id(), ItemId.SILVER_BAR.id(), ItemId.SODA_ASH.id(), ItemId.SAND.id(), ItemId.GOLD_BAR_FAMILYCREST.id()}, item.getID());
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
