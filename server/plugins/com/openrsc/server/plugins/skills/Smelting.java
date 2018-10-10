package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
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

	enum Smelt {
		COPPER_ORE(150, 25, 1, 1, 169, 202, 1),
		TIN_ORE(202, 25, 1, 1, 169, 150, 1),
		IRON_ORE(151, 50, 15, 1, 170, -1, -1),
		SILVER(383, 54, 20, 1, 384, -1, -1),
		GOLD(152, 90, 40, 1, 172, -1, -1),
		MITHRIL_ORE(153, 120, 50, 1, 173, 155, 4),
		ADAMANTITE_ORE(154, 150, 70, 1, 174, 155, 6),
		COAL(155, 70, 30, 2, 171, 151, 1),
		RUNITE_ORE(409, 200, 85, 1, 408, 155, 8);

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

	public static final int FURNACE = 118;

	public static final int CANNON_AMMO_MOULD = 1057;
	public static final int MULTI_CANNON_BALL = 1041;

	public static final int BRONZE_BAR = 169;
	public static final int IRON_BAR = 170;
	public static final int STEEL_BAR = 171;
	public static final int MITHRIL_BAR = 173;
	public static final int ADDY_BAR = 174;
	public static final int RUNE_BAR = 408;
	public static final int GOLD_BAR = 172;
	public static final int SILVER_BAR = 384;
	public static final int SAND = 625;
	public static final int GOLD_BAR_FAMILYCREST = 691;
	public static final int GAUNTLETS_OF_GOLDSMITHING = 699;

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == FURNACE && !DataConversions.inArray(new int[] { GOLD_BAR, SILVER_BAR, SAND, GOLD_BAR_FAMILYCREST }, item.getID())) {
			if(item.getID() == STEEL_BAR) {
				if (p.getInventory().hasItemId(CANNON_AMMO_MOULD)) {
					if (getCurrentLevel(p, SMITHING) < 30) {
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
					p.setBatchEvent(new BatchEvent(p, 1800, Formulae.getRepeatTimes(p, SMITHING)) {
						public void action() {
							p.incExp(SMITHING, 100, true);
							p.getInventory().replace(STEEL_BAR, MULTI_CANNON_BALL);
							addItem(p, MULTI_CANNON_BALL, 1);
							ActionSender.sendInventory(p);
							sleep(1800);
							p.message("it's very heavy");

							if(!isCompleted()) {
								showBubble(p, item);
							}
							if (p.getFatigue() >= p.MAX_FATIGUE) {
								p.message("You are too tired to smelt cannon ball");
								interrupt();
								return;
							}
							if (p.getInventory().countId(STEEL_BAR) < 1) {
								p.message("You have no steel bars left");
								interrupt();
								return;
							}
						}
					});
				}
				else { // No mould
					p.message("You heat the steel bar");
				}
			} else {
				handleRegularSmelting(item, p, obj);
			}
		} 
	}

	private void handleRegularSmelting(final Item item, Player p, final GameObject obj) {
		if(!inArray(item.getID(), Smelt.ADAMANTITE_ORE.getID(),Smelt.COAL.getID(), Smelt.COPPER_ORE.getID(), Smelt.IRON_ORE.getID(),Smelt.GOLD.getID(), Smelt.MITHRIL_ORE.getID(),Smelt.RUNITE_ORE.getID(),Smelt.SILVER.getID(),Smelt.TIN_ORE.getID(), 690)) {
			p.message("Nothing interesting happens");
			return;
		}
		String formattedName = item.getDef().getName().toUpperCase().replaceAll(" ", "_");
		Smelt smelt;
		if(item.getID() == Smelt.IRON_ORE.getID() && getCurrentLevel(p, SMITHING) >= 30 && p.getInventory().countId(Smelt.COAL.getID()) >= 2) {
			String coalChange = EntityHandler.getItemDef(Smelt.COAL.getID()).getName().toUpperCase();
			smelt = Smelt.valueOf(coalChange);
		}  else {
			smelt = Smelt.valueOf(formattedName);
		}

		showBubble(p, item);
		if (!p.getInventory().contains(item)) {
			return;
		}
		if (!p.withinRange(obj, 2)) {
			return;
		}
		if (p.getFatigue() >= p.MAX_FATIGUE) {
			p.message("You are too tired to smelt this ore");
			return;
		}
		if (getCurrentLevel(p, SMITHING) < smelt.getRequiredLevel()) {
			p.message("You need to be at least level-" + smelt.getRequiredLevel() + " smithing to " + (smelt.getSmeltBarId() == SILVER_BAR || smelt.getSmeltBarId() == GOLD_BAR || smelt.getSmeltBarId() == GOLD_BAR_FAMILYCREST ? "work " : "smelt ") + EntityHandler.getItemDef(smelt.getSmeltBarId()).getName().toLowerCase().replaceAll("bar", ""));
			if (smelt.getSmeltBarId() == IRON_BAR)
				p.message("Practice your smithing using tin and copper to make bronze");
			return;
		}
		if (p.getInventory().countId(smelt.getReqOreId()) < smelt.getReqOreAmount() || (p.getInventory().countId(smelt.getID()) < smelt.getOreAmount() && smelt.getReqOreAmount() != -1)) {
			if(smelt.getID() == Smelt.TIN_ORE.getID() || item.getID() == Smelt.COPPER_ORE.getID()) {
				p.message("You also need some " + (item.getID() == Smelt.TIN_ORE.getID() ? "copper" : "tin") + " to make bronze");
				return;
			}
			if(smelt.getID() == Smelt.COAL.getID() && (p.getInventory().countId(Smelt.IRON_ORE.getID()) < 1 || p.getInventory().countId(Smelt.COAL.getID()) <= 1)) {
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
		p.setBatchEvent(new BatchEvent(p, 1800, Formulae.getRepeatTimes(p, SMITHING)) {
			public void action() {
				if (p.getFatigue() >= p.MAX_FATIGUE) {
					p.message("You are too tired to smelt this ore");
					interrupt();
					return;
				}
				if (p.getInventory().countId(smelt.getReqOreId()) < smelt.getReqOreAmount() || (p.getInventory().countId(smelt.getID()) < smelt.getOreAmount() && smelt.getReqOreAmount() != -1)) {
					if(smelt.getID() == Smelt.COAL.getID() && (p.getInventory().countId(Smelt.IRON_ORE.getID()) < 1 || p.getInventory().countId(Smelt.COAL.getID()) <= 1)) {
						p.message("You need 1 iron-ore and 2 coal to make steel");
						interrupt();
						return;
					}
					if(smelt.getID() == Smelt.TIN_ORE.getID() || item.getID() == Smelt.COPPER_ORE.getID()) {
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
					if(item.getID() == GOLD_BAR_FAMILYCREST - 1) 
						removeItem(p, GOLD_BAR_FAMILYCREST - 1, 1);
					else 
						removeItem(p, smelt.getID(), smelt.getOreAmount());

					for (int i = 0; i < smelt.getReqOreAmount(); i++) {	
						p.getInventory().remove(new Item(smelt.getReqOreId()));
					}
					if (smelt.getID() == Smelt.IRON_ORE.getID() && DataConversions.random(0, 1) == 1) {
						p.message("The ore is too impure and you fail to refine it");
					} else {
						if(item.getID() == GOLD_BAR_FAMILYCREST - 1) 
							addItem(p, GOLD_BAR_FAMILYCREST, 1);
						else 
							addItem(p, smelt.getSmeltBarId(), 1);

						p.message("You retrieve a bar of " + new Item(smelt.getSmeltBarId()).getDef().getName().toLowerCase().replaceAll("bar", ""));

						/** Gauntlets of Goldsmithing provide an additional 23 experience when smelting gold ores **/
						if(p.getInventory().wielding(GAUNTLETS_OF_GOLDSMITHING) && new Item(smelt.getSmeltBarId()).getID() == GOLD_BAR) {
							p.incExp(SMITHING, smelt.getXp() + 45, true);	
						} else {
							p.incExp(SMITHING, smelt.getXp(), true);	
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
		if(smelt.getSmeltBarId() == BRONZE_BAR) {
			message = "You smelt the copper and tin together in the furnace";
		} else if(smelt.getSmeltBarId() == MITHRIL_BAR || smelt.getSmeltBarId() == ADDY_BAR || smelt.getSmeltBarId() == RUNE_BAR) {
			message = "You place the " + item.getDef().getName().toLowerCase().replaceAll(" ore", "") + " and " + smelt.getReqOreAmount() + " heaps of " + EntityHandler.getItemDef(smelt.getReqOreId()).getName().toLowerCase() + " into the furnace";
		} else if(smelt.getSmeltBarId() == STEEL_BAR) {
			message = "You place the iron and 2 heaps of coal into the furnace";
		} else if(smelt.getSmeltBarId() == IRON_BAR) {
			message = "You smelt the " + item.getDef().getName().toLowerCase().replaceAll(" ore", "") + " in the furnace";
		} else if(smelt.getSmeltBarId() == SILVER_BAR || smelt.getSmeltBarId() == GOLD_BAR || smelt.getSmeltBarId() == GOLD_BAR_FAMILYCREST) {
			message = "You place a lump of " + item.getDef().getName().toLowerCase().replaceAll(" ore", "") + " in the furnace";
		}
		return message;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		if (obj.getID() == FURNACE && !DataConversions.inArray(new int[] { GOLD_BAR, SILVER_BAR, SAND, GOLD_BAR_FAMILYCREST }, item.getID())) {
			return true;
		}
		return false;
	}
}
