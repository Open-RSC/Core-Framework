package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class Smelting implements InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener {

	public static final int FURNACE = 118;

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == FURNACE && !DataConversions.inArray(new int[]{ItemId.GOLD_BAR.id(), ItemId.SILVER_BAR.id(), ItemId.SAND.id(), ItemId.GOLD_BAR_FAMILYCREST.id()}, item.getID())) {
			if (item.getID() == ItemId.STEEL_BAR.id()) {
				if (p.getInventory().hasItemId(ItemId.CANNON_AMMO_MOULD.id())) {
					if (getCurrentLevel(p, SKILLS.SMITHING.id()) < 30) {
						p.message("You need at least level 30 smithing to make cannon balls");
						return;
					}
					if (p.getQuestStage(Constants.Quests.DWARF_CANNON) != -1) {
						p.message("You need to complete the dwarf cannon quest");
						return;
					}
					showBubble(p, item);
					message(p, 1200, "you heat the steel bar into a liquid state",
						"and pour it into your cannon ball mould",
						"you then leave it to cool for a short while");
					p.setBatchEvent(new BatchEvent(p, 1800, Formulae.getRepeatTimes(p, SKILLS.SMITHING.id()), false) {
						public void action() {
							p.incExp(SKILLS.SMITHING.id(), 100, true);
							p.getInventory().replace(ItemId.STEEL_BAR.id(), ItemId.MULTI_CANNON_BALL.id());
							addItem(p, ItemId.MULTI_CANNON_BALL.id(), 1);
							ActionSender.sendInventory(p);
							sleep(1800);
							p.message("it's very heavy");

							if (!isCompleted()) {
								showBubble(p, item);
							}
							if (Constants.GameServer.WANT_FATIGUE) {
								if (p.getFatigue() >= p.MAX_FATIGUE) {
									p.message("You are too tired to smelt cannon ball");
									interrupt();
									return;
								}
							}
							if (p.getInventory().countId(ItemId.STEEL_BAR.id()) < 1) {
								p.message("You have no steel bars left");
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
		String formattedName = item.getDef().getName().toUpperCase().replaceAll(" ", "_");
		Smelt smelt;
		if (item.getID() == Smelt.IRON_ORE.getID() && getCurrentLevel(p, SKILLS.SMITHING.id()) >= 30 && p.getInventory().countId(Smelt.COAL.getID()) >= 2) {
			String coalChange = EntityHandler.getItemDef(Smelt.COAL.getID()).getName().toUpperCase();
			smelt = Smelt.valueOf(coalChange);
		} else {
			smelt = Smelt.valueOf(formattedName);
		}

		showBubble(p, item);
		if (!p.getInventory().contains(item)) {
			return;
		}
		if (!p.withinRange(obj, 2)) {
			return;
		}
		if (Constants.GameServer.WANT_FATIGUE) {
			if (p.getFatigue() >= p.MAX_FATIGUE) {
				p.message("You are too tired to smelt this ore");
				return;
			}
		}
		if (getCurrentLevel(p, SKILLS.SMITHING.id()) < smelt.getRequiredLevel()) {
			p.message("You need to be at least level-" + smelt.getRequiredLevel() + " smithing to " + (smelt.getSmeltBarId() == ItemId.SILVER_BAR.id() || smelt.getSmeltBarId() == ItemId.GOLD_BAR.id() || smelt.getSmeltBarId() == ItemId.GOLD_BAR_FAMILYCREST.id() ? "work " : "smelt ") + EntityHandler.getItemDef(smelt.getSmeltBarId()).getName().toLowerCase().replaceAll("bar", ""));
			if (smelt.getSmeltBarId() == ItemId.IRON_BAR.id())
				p.message("Practice your smithing using tin and copper to make bronze");
			return;
		}
		if (p.getInventory().countId(smelt.getReqOreId()) < smelt.getReqOreAmount() || (p.getInventory().countId(smelt.getID()) < smelt.getOreAmount() && smelt.getReqOreAmount() != -1)) {
			if (smelt.getID() == Smelt.TIN_ORE.getID() || item.getID() == Smelt.COPPER_ORE.getID()) {
				p.message("You also need some " + (item.getID() == Smelt.TIN_ORE.getID() ? "copper" : "tin") + " to make bronze");
				return;
			}
			if (smelt.getID() == Smelt.COAL.getID() && (p.getInventory().countId(Smelt.IRON_ORE.getID()) < 1 || p.getInventory().countId(Smelt.COAL.getID()) <= 1)) {
				p.message("You need 1 iron-ore and 2 coal to make steel");
				return;
			} else {
				p.message("You need " + smelt.getReqOreAmount() + " heaps of " + EntityHandler.getItemDef(smelt.getReqOreId()).getName().toLowerCase()
					+ " to smelt "
					+ item.getDef().getName().toLowerCase().replaceAll("ore", ""));
				return;
			}
		}

		p.message(smeltString(smelt, item));
		p.setBatchEvent(new BatchEvent(p, 1800, Formulae.getRepeatTimes(p, SKILLS.SMITHING.id()), false) {
			public void action() {
				if (Constants.GameServer.WANT_FATIGUE) {
					if (p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to smelt this ore");
						interrupt();
						return;
					}
				}
				if (p.getInventory().countId(smelt.getReqOreId()) < smelt.getReqOreAmount() || (p.getInventory().countId(smelt.getID()) < smelt.getOreAmount() && smelt.getReqOreAmount() != -1)) {
					if (smelt.getID() == Smelt.COAL.getID() && (p.getInventory().countId(Smelt.IRON_ORE.getID()) < 1 || p.getInventory().countId(Smelt.COAL.getID()) <= 1)) {
						p.message("You need 1 iron-ore and 2 coal to make steel");
						interrupt();
						return;
					}
					if (smelt.getID() == Smelt.TIN_ORE.getID() || item.getID() == Smelt.COPPER_ORE.getID()) {
						p.message("You also need some " + (item.getID() == Smelt.TIN_ORE.getID() ? "copper" : "tin") + " to make bronze");
						interrupt();
						return;
					} else {
						p.message("You need " + smelt.getReqOreAmount() + " heaps of " + EntityHandler.getItemDef(smelt.getReqOreId()).getName().toLowerCase()
							+ " to smelt "
							+ item.getDef().getName().toLowerCase().replaceAll("ore", ""));
						interrupt();
						return;
					}
				}
				showBubble(p, item);
				if (p.getInventory().countId(item.getID()) > 0) {
					if (item.getID() == ItemId.GOLD_FAMILYCREST.id())
						removeItem(p, ItemId.GOLD_FAMILYCREST.id(), 1);
					else
						p.getInventory().remove(smelt.getID(), smelt.getOreAmount());

					if (smelt.getReqOreAmount() > 0)
						p.getInventory().remove(smelt.getReqOreId(), smelt.getReqOreAmount());

					if (smelt.getID() == Smelt.IRON_ORE.getID() && DataConversions.random(0, 1) == 1) {
						p.message("The ore is too impure and you fail to refine it");
					} else {
						if (item.getID() == ItemId.GOLD_FAMILYCREST.id())
							addItem(p, ItemId.GOLD_BAR_FAMILYCREST.id(), 1);
						else
							addItem(p, smelt.getSmeltBarId(), 1);

						p.message("You retrieve a bar of " + new Item(smelt.getSmeltBarId()).getDef().getName().toLowerCase().replaceAll("bar", ""));

						/** Gauntlets of Goldsmithing provide an additional 23 experience when smelting gold ores **/
						if (p.getInventory().wielding(ItemId.GAUNTLETS_OF_GOLDSMITHING.id()) && new Item(smelt.getSmeltBarId()).getID() == ItemId.GOLD_BAR.id()) {
							p.incExp(SKILLS.SMITHING.id(), smelt.getXp() + 45, true);
						} else {
							p.incExp(SKILLS.SMITHING.id(), smelt.getXp(), true);
						}
					}
				} else {
					interrupt();
				}
			}
		});
	}

	private String smeltString(Smelt smelt, Item item) {
		String message = null;
		if (smelt.getSmeltBarId() == ItemId.BRONZE_BAR.id()) {
			message = "You smelt the copper and tin together in the furnace";
		} else if (smelt.getSmeltBarId() == ItemId.MITHRIL_BAR.id() || smelt.getSmeltBarId() == ItemId.ADAMANTITE_BAR.id()|| smelt.getSmeltBarId() == ItemId.RUNITE_BAR.id()) {
			message = "You place the " + item.getDef().getName().toLowerCase().replaceAll(" ore", "") + " and " + smelt.getReqOreAmount() + " heaps of " + EntityHandler.getItemDef(smelt.getReqOreId()).getName().toLowerCase() + " into the furnace";
		} else if (smelt.getSmeltBarId() == ItemId.STEEL_BAR.id()) {
			message = "You place the iron and 2 heaps of coal into the furnace";
		} else if (smelt.getSmeltBarId() == ItemId.IRON_BAR.id()) {
			message = "You smelt the " + item.getDef().getName().toLowerCase().replaceAll(" ore", "") + " in the furnace";
		} else if (smelt.getSmeltBarId() == ItemId.SILVER_BAR.id() || smelt.getSmeltBarId() == ItemId.GOLD_BAR.id() || smelt.getSmeltBarId() == ItemId.GOLD_BAR_FAMILYCREST.id()) {
			message = "You place a lump of " + item.getDef().getName().toLowerCase().replaceAll(" ore", "") + " in the furnace";
		}
		return message;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		if (obj.getID() == FURNACE && !DataConversions.inArray(new int[]{ItemId.GOLD_BAR.id(), ItemId.SILVER_BAR.id(), ItemId.SODA_ASH.id(), ItemId.SAND.id(), ItemId.GOLD_BAR_FAMILYCREST.id()}, item.getID())) {
			return true;
		}
		return false;
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
