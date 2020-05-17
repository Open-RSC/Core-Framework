package com.openrsc.server.plugins.quests.members.legendsquest.mechanism;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestMapJungle implements OpInvTrigger {

	/**
	 * MAPPING THE JUNGLE!!
	 * <p>
	 * WEST 432 -> 477
	 * MIDDLE 431 < 384
	 * EAST 383 > 338
	 * <p>
	 * From real runescape classic using the map on multiple X coordinate tiles for accurate mapping.
	 * <p>
	 * SIDE NOTE: There are 5 entrances (Very Deep east & Deep east, Very Deep west & Deep west, One Centered in middle).
	 */

	private boolean JUNGLE_WEST_AREA(Player player) {
		return player.getLocation().inBounds(432, 872, 477, 909);
	}

	private boolean JUNGLE_MIDDLE_AREA(Player player) {
		return player.getLocation().inBounds(384, 874, 431, 909);
	}

	private boolean JUNGLE_EAST_AREA(Player player) {
		return player.getLocation().inBounds(338, 875, 383, 909);
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.RADIMUS_SCROLLS_COMPLETE.id()) {
			player.message("The map of Kharazi Jungle is complete, Sir Radimus will be pleased.");
			int menu = multi(player, "Read Mission Briefing", "Close");
			if (menu == 0) {
				missionBreifing(player);
			} else if (menu == 1) {
				player.message("You put the scrolls away.");
			}
		}
		else if (item.getCatalogId() == ItemId.RADIMUS_SCROLLS.id()) {
			boolean canMap = true;
			player.message("You open and start to read the scrolls that Radimus gave you.");
			int menu = multi(player,
				"Read Mission Briefing",
				"Start Mapping Kharazi Jungle.");
			if (menu == 0) {
				missionBreifing(player);
			} else if (menu == 1) {
				if (!JUNGLE_WEST_AREA(player) && !JUNGLE_MIDDLE_AREA(player) && !JUNGLE_EAST_AREA(player)) {
					int rnd = DataConversions.random(0, 1);
					if (rnd == 0) {
						mes(player, config().GAME_TICK * 2, "You're not even in the Kharazi Jungle yet.");
						mes(player, config().GAME_TICK * 2, "You need to get to the Southern end of Karamja ");
						mes(player, config().GAME_TICK * 2, "before you can start mapping.");
					}
					else {
						mes(player, config().GAME_TICK * 3, "You prepare to start mapping this area...");
						mes(player, config().GAME_TICK * 2, "This doesn't look like the Kharazi Jungle! ");
						mes(player, config().GAME_TICK * 2, "You need to go to the very southern end of the Island of Karamja !");
					}
					return;
				}
				mes(player, config().GAME_TICK * 3, "You prepare to start mapping this area...");
				if (player.getCache().hasKey("JUNGLE_EAST") && JUNGLE_EAST_AREA(player)) {
					mes(player, config().GAME_TICK * 2, "You have already completed this part of the map.");
					checkMapComplete(player);
					return;
				}
				if (player.getCache().hasKey("JUNGLE_MIDDLE") && JUNGLE_MIDDLE_AREA(player)) {
					mes(player, config().GAME_TICK * 2, "You have already completed this part of the map.");
					checkMapComplete(player);
					return;
				}
				if (player.getCache().hasKey("JUNGLE_WEST") && JUNGLE_WEST_AREA(player)) {
					mes(player, config().GAME_TICK * 2, "You have already completed this part of the map.");
					checkMapComplete(player);
					return;
				}
				if (!player.getCarriedItems().hasCatalogID(ItemId.PAPYRUS.id(), Optional.of(false))
					&& !player.getCarriedItems().hasCatalogID(ItemId.A_LUMP_OF_CHARCOAL.id(), Optional.of(false))) { // no charcoal or papyrus
					mes(player, config().GAME_TICK * 2, "You'll need some papyrus and charcoal to complete this map.");
					canMap = false;
				} else if (player.getCarriedItems().hasCatalogID(ItemId.PAPYRUS.id(), Optional.of(false))
					&& !player.getCarriedItems().hasCatalogID(ItemId.A_LUMP_OF_CHARCOAL.id(), Optional.of(false))) { // has papyrus but no charcoal
					mes(player, config().GAME_TICK * 2, "You'll need some charcoal to complete this map.");
					canMap = false;
				} else if (!player.getCarriedItems().hasCatalogID(ItemId.PAPYRUS.id(), Optional.of(false))
					&& player.getCarriedItems().hasCatalogID(ItemId.A_LUMP_OF_CHARCOAL.id(), Optional.of(false))) { // has charcoal but no papyrus
					mes(player, config().GAME_TICK * 2, "You'll need some additional Papyrus to complete this map.");
					canMap = false;
				}
				//potentially this check was done earlier?
				if (getCurrentLevel(player, Skills.CRAFTING) < 50) {
					player.message("You need a crafting level of 50 to perform this task.");
					return;
				}
				if (canMap) {
					mapArea(player);
				}
			}
		}
	}

	private void missionBreifing(Player player) {
		ActionSender.sendBox(player, "* Legends Guild Quest * % % % %"
			+ "1 : Map the Kharazi Jungle (Southern end of Karamja), there are three main areas that need to be mapped.% %"
			+ "2 : Try to meet up with the local friendly natives, some are not so friendly so be careful.% %"
			+ "3 : See if you can get a trophy or native jungle item from the natives to display in the Legends Guild. You may be given a task or test to earn this.% % %"
			+ "* Note - You may need to get help from other people near the jungle, for example, the local woodsmen may have some knowledge of the Jungle area.", true);
	}

	private void checkMapComplete(Player player) {
		if (!player.getCache().hasKey("JUNGLE_EAST")) {
			mes(player, config().GAME_TICK * 2, "@red@You have yet to map the eastern part of the Kharazi Jungle");
		} else {
			mes(player, config().GAME_TICK * 2, "@gre@Eastern area of the Kharazi Jungle - *** Completed ***");
		}
		if (!player.getCache().hasKey("JUNGLE_MIDDLE")) {
			mes(player, config().GAME_TICK * 2, "@red@You have yet to map the mid - part of the Kharazi Jungle.");
		} else {
			mes(player, config().GAME_TICK * 2, "@gre@Middle area of the Kharazi Jungle- *** Completed ***");
		}
		if (!player.getCache().hasKey("JUNGLE_WEST")) {
			mes(player, config().GAME_TICK * 2, "@red@You have yet to map the Western part of the Kharazi Jungle.");
		} else {
			mes(player, config().GAME_TICK * 2, "@gre@Western part of the Kharazi Jungle- *** Completed ***");
		}
	}

	private void mapArea(Player player) {
		int random = DataConversions.random(0, 100);
		if (random <= 29) { // 30% succeed.
			player.getCarriedItems().remove(new Item(ItemId.PAPYRUS.id()));
			mes(player, config().GAME_TICK * 2, "You neatly add a new section to your map.");
			if (JUNGLE_WEST_AREA(player)) {
				if (!player.getCache().hasKey("JUNGLE_WEST")) {
					player.getCache().store("JUNGLE_WEST", true);
				}
			}
			if (JUNGLE_MIDDLE_AREA(player)) {
				if (!player.getCache().hasKey("JUNGLE_MIDDLE")) {
					player.getCache().store("JUNGLE_MIDDLE", true);
				}
			}
			if (JUNGLE_EAST_AREA(player)) {
				if (!player.getCache().hasKey("JUNGLE_EAST")) {
					player.getCache().store("JUNGLE_EAST", true);
				}
			}
			if (player.getCache().hasKey("JUNGLE_EAST") && player.getCache().hasKey("JUNGLE_MIDDLE") && player.getCache().hasKey("JUNGLE_WEST")) {
				mes(player, config().GAME_TICK * 2, "Well done !",
					"You have completed mapping the Kharazai jungle on the southern end of Karamja,");
				mes(player, config().GAME_TICK * 3, "Grand Vizier Erkle will be pleased.");
				player.getCarriedItems().remove(new Item(ItemId.RADIMUS_SCROLLS.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.RADIMUS_SCROLLS_COMPLETE.id())); // switch map to complete map.
				checkMapComplete(player);
				player.getCache().remove("JUNGLE_EAST");
				player.getCache().remove("JUNGLE_MIDDLE");
				player.getCache().remove("JUNGLE_WEST");
			} else {
				mes(player, config().GAME_TICK * 3, "You still have some sections of the map to complete.");
				checkMapComplete(player);
			}
		} else if (random <= 50) { // 20 % fail both.
			player.message("You fall over, landing on your charcoal and papyrus, destroying them both.");
			player.getCarriedItems().remove(new Item(ItemId.PAPYRUS.id()));
			player.getCarriedItems().remove(new Item(ItemId.A_LUMP_OF_CHARCOAL.id()));
		} else if (random <= 70) { // 20% to fail papyrus
			player.message("You make a mess of the map, the paper is totally ruined.");
			player.getCarriedItems().remove(new Item(ItemId.PAPYRUS.id()));
		} else if (random <= 90) { // 20% to fail charcoal
			player.message("You snap your stick of charcoal.");
			player.getCarriedItems().remove(new Item(ItemId.A_LUMP_OF_CHARCOAL.id()));
		} else if (random <= 100) { // 10% to fail and save papyrus
			player.message("You make a mess of the map, but are able to rescue the paper.");
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.RADIMUS_SCROLLS.id() || item.getCatalogId() == ItemId.RADIMUS_SCROLLS_COMPLETE.id();
	}
}
