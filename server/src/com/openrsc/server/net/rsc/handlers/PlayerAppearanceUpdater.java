package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.PlayerAppearance;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.PlayerAppearanceStruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.openrsc.server.constants.Skills.*;

public class PlayerAppearanceUpdater implements PayloadProcessor<PlayerAppearanceStruct, OpcodeIn> {

	public void process(PlayerAppearanceStruct payload, Player player) throws Exception {

		if (!player.isChangingAppearance()) {
			player.setSuspiciousPlayer(true, "player appearance packet without changing appearance");
			return;
		}
		player.setChangingAppearance(false);
		byte headRestrictions = payload.headRestrictions;
		byte headType = payload.headType;
		byte bodyType = payload.bodyType;

		// This value is always "2" and is not very useful.
		// I looked in the  v40 client deob, and the 4th byte is also always 2 there.
		// I looked in the v127 client deob, and the 4th byte is also always 2 there.
		// I looked in the v204 client deob, and the 4th byte is also always 2 there.
		// I looked in the v233 client deob, and the 4th byte is also always 2 there.
		byte mustEqual2 = payload.mustEqual2;
		if (mustEqual2 != 2) {
			player.setSuspiciousPlayer(true, "4th byte of player appearance packet wasn't equal to 2");
			return;
		}

		int hairColour = payload.hairColour;
		int topColour = payload.topColour;
		int trouserColour = payload.trouserColour;
		int skinColour = payload.skinColour;
		int ironmanMode = payload.ironmanMode; // custom protocol
		int isOneXp = payload.isOneXp; // custom protocol

		int headSprite = headType + 1;
		int bodySprite = bodyType + 1;

		PlayerAppearance appearance = new PlayerAppearance(hairColour,
			topColour, trouserColour, skinColour, headSprite, bodySprite);
		if (!appearance.isValid()) {
			player.setSuspiciousPlayer(true, "player invalid appearance");
			return;
		}

		player.setMale(headRestrictions == 1); // TODO: expand gender preferences

		if (player.isMale()) {
			if (player.getConfig().WANT_EQUIPMENT_TAB) {
				Item top = player.getCarriedItems().getEquipment().get(1);
				if (top != null && top.getDef(player.getWorld()).isFemaleOnly()) {
					if(!player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, top, UnequipRequest.RequestType.FROM_EQUIPMENT, false))) {
						player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, top, UnequipRequest.RequestType.FROM_BANK, false));
					}
					ActionSender.sendEquipmentStats(player, 1);
				}
			} else {
				Inventory inv = player.getCarriedItems().getInventory();
				for (int slot = 0; slot < inv.size(); slot++) {
					Item i = inv.get(slot);
					if (i.isWieldable(player.getWorld()) && i.getDef(player.getWorld()).getWieldPosition() == 1
						&& i.isWielded() && i.getDef(player.getWorld()).isFemaleOnly()) {
						if(!player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, i, UnequipRequest.RequestType.FROM_EQUIPMENT, false))) {
							player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, i, UnequipRequest.RequestType.FROM_BANK, false));
						}
						ActionSender.sendInventoryUpdateItem(player, slot);
						break;
					}
				}
			}
		}
		int[] oldWorn = player.getWornItems();
		int[] oldAppearance = player.getSettings().getAppearance().getSprites();
		player.getSettings().setAppearance(appearance);
		int[] newAppearance = player.getSettings().getAppearance().getSprites();
		for (int i = 0; i < 12; i++) {
			if (oldWorn[i] == oldAppearance[i]) {
				player.updateWornItems(i, newAppearance[i]);
			}
		}

		if (player.getLastLogin() == 0L) {
			if (player.getConfig().USES_CLASSES) {
				List<Item> starterItems = new ArrayList<>();
				switch (payload.chosenClass) {
					// assumption: 100 xp for each lvl
					case ADVENTURER:
						// 2 Attack, Strength, Defense, 2 Ranged,
						// 2 PrayGood / PrayEvil / Prayer,
						// 2 GoodMagic / EvilMagic / Magic,
						// 11 Hits (original)
						// items: tinderbox, bronze axe, empty jug, pot
						player.getSkills().setExperienceAndLevel(Skill.of(ATTACK).id(), 400, 2, false);
						player.getSkills().setExperienceAndLevel(Skill.of(STRENGTH).id(), 400, 2, false);
						player.getSkills().setExperienceAndLevel(Skill.of(DEFENSE).id(), 400, 2, false);
						player.getSkills().setExperienceAndLevel(Skill.of(RANGED).id(), 400, 2, false);
						if (player.getConfig().DIVIDED_GOOD_EVIL) {
							player.getSkills().setExperienceAndLevel(Skill.of(PRAYGOOD).id(), 400, 2, false);
							player.getSkills().setExperienceAndLevel(Skill.of(PRAYEVIL).id(), 400, 2, false);
							player.getSkills().setExperienceAndLevel(Skill.of(GOODMAGIC).id(), 400, 2, false);
							player.getSkills().setExperienceAndLevel(Skill.of(EVILMAGIC).id(), 400, 2, false);
						} else {
							player.getSkills().setExperienceAndLevel(Skill.of(PRAYER).id(), 400, 2, false);
							player.getSkills().setExperienceAndLevel(Skill.of(MAGIC).id(), 400, 2, false);
						}
						player.getSkills().setExperienceAndLevel(Skill.of(HITS).id(), 4000, 11, false);
						starterItems.addAll(Arrays.asList(new Item(ItemId.TINDERBOX.id()), new Item(ItemId.BRONZE_AXE.id()), new Item(ItemId.JUG.id()), new Item(ItemId.POT.id())));
						break;
					case WARRIOR:
						// 3 Attack, Strength, Defense
						// 12 Hits
						// items: bronze short sword, wooden shield
						player.getSkills().setExperienceAndLevel(Skill.of(ATTACK).id(), 800, 3, false);
						player.getSkills().setExperienceAndLevel(Skill.of(STRENGTH).id(), 800, 3, false);
						player.getSkills().setExperienceAndLevel(Skill.of(DEFENSE).id(), 800, 3, false);
						player.getSkills().setExperienceAndLevel(Skill.of(HITS).id(), 4400, 12, false);
						starterItems.addAll(Arrays.asList(new Item(ItemId.BRONZE_SHORT_SWORD.id()), new Item(ItemId.WOODEN_SHIELD.id())));
						break;
					case WIZARD:
						// 7 GoodMagic / Magic
						// 10 Hits
						// items: blue wizardhat, regular staff
						if (player.getConfig().DIVIDED_GOOD_EVIL) {
							player.getSkills().setExperienceAndLevel(Skill.of(GOODMAGIC).id(), 2400, 7, false);
						} else {
							player.getSkills().setExperienceAndLevel(Skill.of(MAGIC).id(), 2400, 7, false);
						}
						player.getSkills().setExperienceAndLevel(Skill.of(HITS).id(), 3600, 10, false);
						starterItems.addAll(Arrays.asList(new Item(ItemId.BLUE_WIZARDSHAT.id()), new Item(ItemId.STAFF.id())));
						break;
					case NECROMANCER:
						// 7 EvilMagic / Magic
						// 10 Hits
						// items: black wizardhat, regular staff
						if (player.getConfig().DIVIDED_GOOD_EVIL) {
							player.getSkills().setExperienceAndLevel(Skill.of(EVILMAGIC).id(), 2400, 7, false);
						} else {
							player.getSkills().setExperienceAndLevel(Skill.of(MAGIC).id(), 2400, 7, false);
						}
						player.getSkills().setExperienceAndLevel(Skill.of(HITS).id(), 3600, 10, false);
						starterItems.addAll(Arrays.asList(new Item(ItemId.BLACK_WIZARDSHAT.id()), new Item(ItemId.STAFF.id())));
						break;
					case RANGER:
						// 6 Ranged
						// 12 Hits
						// items: shortbow, 10 (bronze) arrows
						player.getSkills().setExperienceAndLevel(Skill.of(RANGED).id(), 2000, 6, false);
						player.getSkills().setExperienceAndLevel(Skill.of(HITS).id(), 4400, 12, false);
						starterItems.addAll(Arrays.asList(new Item(ItemId.SHORTBOW.id()), new Item(ItemId.BRONZE_ARROWS.id(), 10)));
						break;
					case MINER:
						// 7 Mining
						// 10 Hits
						// items: (bronze) pickaxe
						player.getSkills().setExperienceAndLevel(Skill.of(MINING).id(), 2400, 7, false);
						player.getSkills().setExperienceAndLevel(Skill.of(HITS).id(), 3600, 10, false);
						starterItems.addAll(Arrays.asList(new Item(ItemId.BRONZE_PICKAXE.id())));
						break;
				}
				ActionSender.sendStats(player);
				for (Item item : starterItems) {
					player.getCarriedItems().getInventory().add(item, false);
				}
				ActionSender.sendInventory(player);
			}

			if (player.getConfig().USES_PK_MODE) {
				player.setPkMode(payload.pkMode);
				player.setPkChanges(2);
				ActionSender.sendGameSettings(player);
			}

			if (player.getConfig().CHARACTER_CREATION_MODE == 1) {
				player.setIronMan(ironmanMode);
				player.setOneXp(isOneXp == 1);
			}
		}
	}
}
