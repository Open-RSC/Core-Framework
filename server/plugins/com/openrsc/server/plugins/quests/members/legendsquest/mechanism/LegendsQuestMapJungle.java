package com.openrsc.server.plugins.quests.members.legendsquest.mechanism;

import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.external.ItemId;

public class LegendsQuestMapJungle implements InvActionListener, InvActionExecutiveListener {

	/**
	 * MAPPING THE JUNGLE!!
	 * <p>
	 * WEST 432 -> 477
	 * MIDDLE 431 < 384
	 * EAST 383 > 338
	 * <p>
	 * Coords for each area calculated by: Imposter.
	 * From real runescape classic using the map on multiple X coordinate tiles for accurate mapping.
	 * <p>
	 * SIDE NOTE: There are 5 entrances (Very Deep east & Deep east, Very Deep west & Deep west, One Centered in middle).
	 */

	private boolean JUNGLE_WEST_AREA(Player p) {
		return p.getLocation().inBounds(432, 872, 477, 909);
	}

	private boolean JUNGLE_MIDDLE_AREA(Player p) {
		return p.getLocation().inBounds(384, 874, 431, 909);
	}

	private boolean JUNGLE_EAST_AREA(Player p) {
		return p.getLocation().inBounds(338, 875, 383, 909);
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if (item.getID() == ItemId.RADIMUS_SCROLLS_COMPLETE.id()) {
			p.message("The map of Kharazi Jungle is complete, Sir Radimus will be pleased.");
			int menu = showMenu(p, "Read Mission Briefing", "Close");
			if (menu == 0) {
				missionBreifing(p);
			} else if (menu == 1) {
				p.message("You put the scrolls away.");
			}
		}
		else if (item.getID() == ItemId.RADIMUS_SCROLLS.id()) {
			boolean canMap = true;
			p.message("You open and start to read the scrolls that Radimus gave you.");
			int menu = showMenu(p,
				"Read Mission Briefing",
				"Start Mapping Kharazi Jungle.");
			if (menu == 0) {
				missionBreifing(p);
			} else if (menu == 1) {
				if (!JUNGLE_WEST_AREA(p) && !JUNGLE_MIDDLE_AREA(p) && !JUNGLE_EAST_AREA(p)) {
					int rnd = DataConversions.random(0, 1);
					if (rnd == 0) {
						message(p, 1200, "You're not even in the Kharazi Jungle yet.");
						message(p, 1200, "You need to get to the Southern end of Karamja ");
						message(p, 1200, "before you can start mapping.");
					}
					else {
						message(p, 1900, "You prepare to start mapping this area...");
						message(p, 1200, "This doesn't look like the Kharazi Jungle! ");
						message(p, 1200, "You need to go to the very southern end of the Island of Karamja !");
					}
					return;
				}
				message(p, 1900, "You prepare to start mapping this area...");
				if (p.getCache().hasKey("JUNGLE_EAST") && JUNGLE_EAST_AREA(p)) {
					message(p, 1200, "You have already completed this part of the map.");
					checkMapComplete(p);
					return;
				}
				if (p.getCache().hasKey("JUNGLE_MIDDLE") && JUNGLE_MIDDLE_AREA(p)) {
					message(p, 1200, "You have already completed this part of the map.");
					checkMapComplete(p);
					return;
				}
				if (p.getCache().hasKey("JUNGLE_WEST") && JUNGLE_WEST_AREA(p)) {
					message(p, 1200, "You have already completed this part of the map.");
					checkMapComplete(p);
					return;
				}
				if (!hasItem(p, ItemId.PAPYRUS.id()) && !hasItem(p, ItemId.A_LUMP_OF_CHARCOAL.id())) { // no charcoal or papyrus
					message(p, 1200, "You'll need some papyrus and charcoal to complete this map.");
					canMap = false;
				} else if (hasItem(p, ItemId.PAPYRUS.id()) && !hasItem(p, ItemId.A_LUMP_OF_CHARCOAL.id())) { // has papyrus but no charcoal
					message(p, 1200, "You'll need some charcoal to complete this map.");
					canMap = false;
				} else if (!hasItem(p, ItemId.PAPYRUS.id()) && hasItem(p, ItemId.A_LUMP_OF_CHARCOAL.id())) { // has charcoal but no papyrus
					message(p, 1200, "You'll need some additional Papyrus to complete this map.");
					canMap = false;
				}
				//potentially this check was done earlier?
				if (getCurrentLevel(p, SKILLS.CRAFTING.id()) < 50) {
					p.message("You need a crafting level of 50 to perform this task.");
					return;
				}
				if (canMap) {
					mapArea(p);
				}
			}
		}
	}

	private void missionBreifing(Player p) {
		ActionSender.sendBox(p, "* Legends Guild Quest * % % % %"
			+ "1 : Map the Kharazi Jungle (Southern end of Karamja), there are three main areas that need to be mapped.% %"
			+ "2 : Try to meet up with the local friendly natives, some are not so friendly so be careful.% %"
			+ "3 : See if you can get a trophy or native jungle item from the natives to display in the Legends Guild. You may be given a task or test to earn this.% % %"
			+ "* Note - You may need to get help from other people near the jungle, for example, the local woodsmen may have some knowledge of the Jungle area.", true);
	}

	private void checkMapComplete(Player p) {
		if (!p.getCache().hasKey("JUNGLE_EAST")) {
			message(p, 1200, "@red@You have yet to map the eastern part of the Kharazi Jungle");
		} else {
			message(p, 1200, "@gre@Eastern area of the Kharazi Jungle - *** Completed ***");
		}
		if (!p.getCache().hasKey("JUNGLE_MIDDLE")) {
			message(p, 1200, "@red@You have yet to map the mid - part of the Kharazi Jungle.");
		} else {
			message(p, 1200, "@gre@Middle area of the Kharazi Jungle- *** Completed ***");
		}
		if (!p.getCache().hasKey("JUNGLE_WEST")) {
			message(p, 1200, "@red@You have yet to map the Western part of the Kharazi Jungle.");
		} else {
			message(p, 1200, "@gre@Western part of the Kharazi Jungle- *** Completed ***");
		}
	}

	private void mapArea(Player p) {
		int random = DataConversions.random(0, 100);
		if (random <= 29) { // 30% succeed.
			removeItem(p, ItemId.PAPYRUS.id(), 1);
			message(p, 1200, "You neatly add a new section to your map.");
			if (JUNGLE_WEST_AREA(p)) {
				if (!p.getCache().hasKey("JUNGLE_WEST")) {
					p.getCache().store("JUNGLE_WEST", true);
				}
			}
			if (JUNGLE_MIDDLE_AREA(p)) {
				if (!p.getCache().hasKey("JUNGLE_MIDDLE")) {
					p.getCache().store("JUNGLE_MIDDLE", true);
				}
			}
			if (JUNGLE_EAST_AREA(p)) {
				if (!p.getCache().hasKey("JUNGLE_EAST")) {
					p.getCache().store("JUNGLE_EAST", true);
				}
			}
			if (p.getCache().hasKey("JUNGLE_EAST") && p.getCache().hasKey("JUNGLE_MIDDLE") && p.getCache().hasKey("JUNGLE_WEST")) {
				message(p, 1200, "Well done !",
					"You have completed mapping the Kharazai jungle on the southern end of Karamja,");
				message(p, 1900, "Grand Vizier Erkle will be pleased.");
				p.getInventory().replace(ItemId.RADIMUS_SCROLLS.id(), ItemId.RADIMUS_SCROLLS_COMPLETE.id()); // switch map to complete map.
				checkMapComplete(p);
				p.getCache().remove("JUNGLE_EAST");
				p.getCache().remove("JUNGLE_MIDDLE");
				p.getCache().remove("JUNGLE_WEST");
			} else {
				message(p, 1900, "You still have some sections of the map to complete.");
				checkMapComplete(p);
			}
		} else if (random <= 50) { // 20 % fail both.
			p.message("You fall over, landing on your charcoal and papyrus, destroying them both.");
			removeItem(p, ItemId.PAPYRUS.id(), 1);
			removeItem(p, ItemId.A_LUMP_OF_CHARCOAL.id(), 1);
		} else if (random <= 70) { // 20% to fail papyrus
			p.message("You make a mess of the map, the paper is totally ruined.");
			removeItem(p, ItemId.PAPYRUS.id(), 1);
		} else if (random <= 90) { // 20% to fail charcoal
			p.message("You snap your stick of charcoal.");
			removeItem(p, ItemId.A_LUMP_OF_CHARCOAL.id(), 1);
		} else if (random <= 100) { // 10% to fail and save papyrus
			p.message("You make a mess of the map, but are able to rescue the paper.");
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player p) {
		return item.getID() == ItemId.RADIMUS_SCROLLS.id() || item.getID() == ItemId.RADIMUS_SCROLLS_COMPLETE.id();
	}
}
