package com.openrsc.server.plugins.authentic.quests.members.legendsquest.mechanism;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.event.RestartableDelayedEvent;
import com.openrsc.server.event.rsc.impl.projectile.CustomProjectileEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.HashMap;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestHolyWater implements OpInvTrigger, UseInvTrigger {

	private static final HashMap<Player, RestartableDelayedEvent> playerEventMap = new HashMap<Player, RestartableDelayedEvent>();

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.ENCHANTED_VIAL.id())
			|| compareItemsIds(item1, item2, ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.EMPTY_VIAL.id());
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.ENCHANTED_VIAL.id())) {
			// simple random for the moment
			mes("You pour some of the sacred water into the enchanted vial.");
			delay();
			mes("You now have a vial of holy water.");
			delay();
			player.getCarriedItems().remove(new Item(ItemId.ENCHANTED_VIAL.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.HOLY_WATER_VIAL.id()));
			if(!player.getCache().hasKey("remaining_blessed_bowl")) {
				player.getCache().set("remaining_blessed_bowl", DataConversions.random(1, 15));
			} else {
				int remain = player.getCache().getInt("remaining_blessed_bowl");
				if(remain > 1) {
					player.getCache().put("remaining_blessed_bowl", remain - 1);
				}
				// empty the bowl
				else {
					player.message("The pure water in the golden bowl has run out...");
					player.getCarriedItems().remove(new Item(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.BLESSED_GOLDEN_BOWL.id()));
					player.getCache().remove("remaining_blessed_bowl");
				}
			}
		} else if (compareItemsIds(item1, item2, ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.EMPTY_VIAL.id())) {
			// simple random for the moment
			mes("You pour some of the water into the empty vial");
			delay();
			mes("The water seems to loose some of it's effervescence.");
			delay();
			player.getCarriedItems().remove(new Item(ItemId.EMPTY_VIAL.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.VIAL.id()));
			if(!player.getCache().hasKey("remaining_blessed_bowl")) {
				player.getCache().set("remaining_blessed_bowl", DataConversions.random(1, 15));
			} else {
				int remain = player.getCache().getInt("remaining_blessed_bowl");
				if(remain > 1) {
					player.getCache().put("remaining_blessed_bowl", remain - 1);
				}
				// empty the bowl
				else {
					player.message("The pure water in the golden bowl has run out...");
					player.getCarriedItems().remove(new Item(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.BLESSED_GOLDEN_BOWL.id()));
					player.getCache().remove("remaining_blessed_bowl");
				}
			}
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.HOLY_WATER_VIAL.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.HOLY_WATER_VIAL.id())) {
			player.message("You need to equip this item to throw it.");
		}
		else {
			Npc ungadulu = ifnearvisnpc(player, NpcId.UNGADULU.id(), 4);
			if (ungadulu == null || player.getQuestStage(Quests.LEGENDS_QUEST) > 3) {
				player.message("You see no one suitable to throw it at.");
			}
			else {
				player.message("You throw the holy watervial at Ungadulu.");
				player.getCarriedItems().remove(new Item(item.getCatalogId()));
				player.playSound("projectile");
				player.getWorld().getServer().getGameEventHandler().add(new CustomProjectileEvent(player.getWorld(), player, ungadulu, 1) {
					@Override
					public void doSpell() {
					}
				});

				RestartableDelayedEvent playerEvent = playerEventMap.get(player);
				//rethrowing holy water resets the timer
				if (playerEvent == null) {
					playerEvent = new RestartableDelayedEvent(player.getWorld(), player, 1000, "Legends Quest User Holy Water") {
						int timesRan = 0;

						@Override
						public void run() {
							// 5 min of holy water effect tops
							if (timesRan > 300) {
								if (player.getCache().hasKey("holy_water_neiz")) {
									player.getCache().remove("holy_water_neiz");
								}
								stop();
								playerEventMap.remove(player);
							}
							timesRan++;
						}

						@Override
						public void reset() {
							timesRan = 0;
						}
					};
					playerEventMap.put(player, playerEvent);
					if (!player.getCache().hasKey("holy_water_neiz")) {
						player.getCache().store("holy_water_neiz", true);
					}
					player.getWorld().getServer().getGameEventHandler().add(playerEvent);
				} else {
					playerEvent.reset();
				}
				ungadulu = changenpc(ungadulu, NpcId.EVIL_UNGADULU.id(), true);
				npcsay(player, ungadulu, "Vile serpent...you will pay for that...");
				ungadulu = changenpc(ungadulu, NpcId.UNGADULU.id(), true);
				npcsay(player, ungadulu, "What...what happened...why am I all wet?");
			}
		}
	}

}
