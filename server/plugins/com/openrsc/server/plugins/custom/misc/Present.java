package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.content.DropTable;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UsePlayerTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public class Present implements UsePlayerTrigger, OpInvTrigger {

	private static DropTable presentDrops;

	static {
		presentDrops = new DropTable();
		DropTable holidayTable = new DropTable();
		DropTable junkTable = new DropTable();
		DropTable bronzeTable = new DropTable();
		DropTable ironTable = new DropTable();
		DropTable steelTable = new DropTable();
		DropTable blackTable = new DropTable();
		DropTable mithTable = new DropTable();
		DropTable addyTable = new DropTable();
		DropTable runeTable = new DropTable();
		DropTable ultraRareTable = new DropTable();

		/**
		 * Holiday Items Table
		 * Authentic holiday items are meant to be more common
		 */
		holidayTable.addItemDrop(ItemId.CHRISTMAS_CRACKER.id(), 1, 64, false);
		holidayTable.addItemDrop(ItemId.SANTAS_HAT.id(), 1, 64, false);
		holidayTable.addItemDrop(ItemId.CHRISTMAS_CAPE.id(), 1, 9, false);
		holidayTable.addItemDrop(ItemId.SANTAS_HAT_BEARD.id(), 1, 9, false);
		holidayTable.addItemDrop(ItemId.CHRISTMAS_APRON.id(), 1, 9, false);
		holidayTable.addItemDrop(ItemId.SANTAS_GLOVES.id(), 1, 9, false);
		holidayTable.addItemDrop(ItemId.SANTAS_MITTENS.id(), 1, 9, false);
		holidayTable.addItemDrop(ItemId.SANTAS_SUIT_TOP.id(), 1, 9, false);
		holidayTable.addItemDrop(ItemId.SANTAS_SUIT_BOTTOM.id(), 1, 9, false);
		holidayTable.addItemDrop(ItemId.GREEN_SANTAS_HAT.id(), 1, 9, false);
		holidayTable.addItemDrop(ItemId.RUDOLPHS_ANTLERS.id(), 1, 8, false);
		holidayTable.addItemDrop(ItemId.GLASS_MILK.id(), 1, 12, false);
		holidayTable.addItemDrop(ItemId.CANE_COOKIE.id(), 1, 12, false);
		holidayTable.addItemDrop(ItemId.STAR_COOKIE.id(), 1, 12, false);
		holidayTable.addItemDrop(ItemId.TREE_COOKIE.id(), 1, 12, false);

		/**
		 * Junk Items
		 */
		junkTable.addItemDrop(ItemId.COAL.id(), 1, 128, false);
		junkTable.addItemDrop(ItemId.CHOCOLATE_BAR.id(), 1, 16, false);
		junkTable.addItemDrop(ItemId.BEER.id(), 1, 16, false);
		junkTable.addItemDrop(ItemId.TINDERBOX.id(), 1, 16, false);
		junkTable.addItemDrop(ItemId.MILK.id(), 1, 16, false);
		junkTable.addItemDrop(ItemId.CHOCOLATY_MILK.id(), 1, 16, false);
		junkTable.addItemDrop(ItemId.LOGS.id(), 1, 16, false);
		junkTable.addItemDrop(ItemId.CAKE.id(), 1, 16, false);
		junkTable.addItemDrop(ItemId.CAKE_TIN.id(), 1, 16, false);

		/**
		 * Bronze Items
		 */
		bronzeTable.addItemDrop(ItemId.BRONZE_ARROWS.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_PICKAXE.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_AXE.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_LONG_SWORD.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_PLATE_MAIL_BODY.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_PLATE_MAIL_LEGS.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_PLATE_MAIL_TOP.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_PLATED_SKIRT.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_2_HANDED_SWORD.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_CHAIN_MAIL_BODY.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_SPEAR.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_THROWING_DART.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_THROWING_KNIFE.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_KITE_SHIELD.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_SQUARE_SHIELD.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_BATTLE_AXE.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_DAGGER.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_SCIMITAR.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_SHORT_SWORD.id(), 1, 1, false);
		bronzeTable.addItemDrop(ItemId.BRONZE_MACE.id(), 1, 1, false);

		/**
		 * Iron Items
		 */
		ironTable.addItemDrop(ItemId.IRON_ARROWS.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_PICKAXE.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_AXE.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_LONG_SWORD.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_PLATE_MAIL_BODY.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_PLATE_MAIL_LEGS.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_PLATE_MAIL_TOP.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_PLATED_SKIRT.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_2_HANDED_SWORD.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_CHAIN_MAIL_BODY.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_SPEAR.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_THROWING_DART.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_THROWING_KNIFE.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_KITE_SHIELD.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_SQUARE_SHIELD.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_BATTLE_AXE.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_DAGGER.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_SCIMITAR.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_SHORT_SWORD.id(), 1, 1, false);
		ironTable.addItemDrop(ItemId.IRON_MACE.id(), 1, 1, false);

		/**
		 * Steel Items
		 */
		steelTable.addItemDrop(ItemId.STEEL_ARROWS.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_PICKAXE.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_AXE.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_LONG_SWORD.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_PLATE_MAIL_BODY.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_PLATE_MAIL_LEGS.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_PLATE_MAIL_TOP.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_PLATED_SKIRT.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_2_HANDED_SWORD.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_CHAIN_MAIL_BODY.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_SPEAR.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_THROWING_DART.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_THROWING_KNIFE.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_KITE_SHIELD.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_SQUARE_SHIELD.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_BATTLE_AXE.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_DAGGER.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_SCIMITAR.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_SHORT_SWORD.id(), 1, 1, false);
		steelTable.addItemDrop(ItemId.STEEL_MACE.id(), 1, 1, false);

		/**
		 * Black Items
		 */
		blackTable.addItemDrop(ItemId.BLACK_AXE.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_LONG_SWORD.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_PLATE_MAIL_BODY.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_PLATE_MAIL_LEGS.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_PLATE_MAIL_TOP.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_PLATED_SKIRT.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_2_HANDED_SWORD.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_CHAIN_MAIL_BODY.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_THROWING_KNIFE.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_KITE_SHIELD.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_SQUARE_SHIELD.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_BATTLE_AXE.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_DAGGER.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_SCIMITAR.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_SHORT_SWORD.id(), 1, 1, false);
		blackTable.addItemDrop(ItemId.BLACK_MACE.id(), 1, 1, false);

		/**
		 * Mith Items
		 */
		mithTable.addItemDrop(ItemId.MITHRIL_ARROWS.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_PICKAXE.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_AXE.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_LONG_SWORD.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_PLATE_MAIL_BODY.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_PLATE_MAIL_LEGS.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_PLATE_MAIL_TOP.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_PLATED_SKIRT.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_2_HANDED_SWORD.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_CHAIN_MAIL_BODY.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_SPEAR.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_THROWING_DART.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_THROWING_KNIFE.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_KITE_SHIELD.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_SQUARE_SHIELD.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_BATTLE_AXE.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_DAGGER.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_SCIMITAR.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_SHORT_SWORD.id(), 1, 1, false);
		mithTable.addItemDrop(ItemId.MITHRIL_MACE.id(), 1, 1, false);

		/**
		 * Addy Items
		 */
		addyTable.addItemDrop(ItemId.ADAMANTITE_ARROWS.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_PICKAXE.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_AXE.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_LONG_SWORD.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_PLATE_MAIL_BODY.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_PLATE_MAIL_TOP.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_PLATED_SKIRT.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_2_HANDED_SWORD.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_SPEAR.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_THROWING_DART.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_THROWING_KNIFE.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_KITE_SHIELD.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_SQUARE_SHIELD.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_BATTLE_AXE.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_DAGGER.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_SCIMITAR.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_SHORT_SWORD.id(), 1, 1, false);
		addyTable.addItemDrop(ItemId.ADAMANTITE_MACE.id(), 1, 1, false);

		/**
		 * Rune Items
		 */
		runeTable.addItemDrop(ItemId.RUNE_ARROWS.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_PICKAXE.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_AXE.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_LONG_SWORD.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_PLATE_MAIL_BODY.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_PLATE_MAIL_LEGS.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_PLATE_MAIL_TOP.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_SKIRT.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_2_HANDED_SWORD.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_CHAIN_MAIL_BODY.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_SPEAR.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_THROWING_DART.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_THROWING_KNIFE.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_KITE_SHIELD.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_SQUARE_SHIELD.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_BATTLE_AXE.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_DAGGER.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_SCIMITAR.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_SHORT_SWORD.id(), 1, 1, false);
		runeTable.addItemDrop(ItemId.RUNE_MACE.id(), 1, 1, false);

		/**
		 * Ultra Rare Items
		 */
		ultraRareTable.addItemDrop(ItemId.LOOP_KEY_HALF.id(), 1, 38, false);
		ultraRareTable.addItemDrop(ItemId.TOOTH_KEY_HALF.id(), 1, 36, false);
		ultraRareTable.addItemDrop(ItemId.DRAGONSTONE.id(), 1, 19, false);
		ultraRareTable.addItemDrop(ItemId.DRAGONSTONE.id(), 2, 3, false);
		ultraRareTable.addItemDrop(ItemId.DRAGON_SWORD.id(), 1, 14, false);
		ultraRareTable.addItemDrop(ItemId.DRAGON_AXE.id(), 1, 10, false);
		ultraRareTable.addItemDrop(ItemId.DRAGON_MEDIUM_HELMET.id(), 1, 2, false);
		ultraRareTable.addItemDrop(ItemId.RIGHT_HALF_DRAGON_SQUARE_SHIELD.id(), 1, 4, false);
		ultraRareTable.addItemDrop(ItemId.LEFT_HALF_DRAGON_SQUARE_SHIELD.id(), 1, 2, false);

		/**
		 * Bring all the tables together
		 */
		presentDrops.addTableDrop(holidayTable, 128);
		presentDrops.addTableDrop(junkTable, 48);
		presentDrops.addTableDrop(bronzeTable, 14);
		presentDrops.addTableDrop(ironTable, 13);
		presentDrops.addTableDrop(steelTable, 12);
		presentDrops.addTableDrop(blackTable, 12);
		presentDrops.addTableDrop(mithTable, 11);
		presentDrops.addTableDrop(addyTable, 10);
		presentDrops.addTableDrop(runeTable, 6);
		presentDrops.addTableDrop(ultraRareTable, 2);
	}

	@Override
	public void onUsePlayer(Player player, Player otherPlayer, Item item) {
		if (item.getCatalogId() == ItemId.PRESENT.id()) {
			if (otherPlayer.isIronMan(IronmanMode.Ironman.id()) || otherPlayer.isIronMan(IronmanMode.Ultimate.id())
				|| otherPlayer.isIronMan(IronmanMode.Hardcore.id()) || otherPlayer.isIronMan(IronmanMode.Transfer.id())) {
				player.message(otherPlayer.getUsername() + " is an Iron Man. " + (otherPlayer.isMale() ? "He" : "She") + " stands alone.");
				return;
			}

			if(!config().CAN_USE_CRACKER_ON_SELF && !player.isAdmin() && player.getCurrentIP().equalsIgnoreCase(otherPlayer.getCurrentIP())) {
				player.message(otherPlayer.getUsername() + " does not want your present...");
				return;
			}

			player.face(otherPlayer);
			otherPlayer.face(player);

			thinkbubble(item);
			player.message("You give a present to " + otherPlayer.getUsername());
			otherPlayer.message(player.getUsername() + " handed you a present...");
			delay();
			otherPlayer.message("You unwrap the present and reach your hand inside...");
			delay();

			ArrayList<Item> prizeList = presentDrops.rollItem(false, otherPlayer);
			if (prizeList.size() <= 0) return;
			Item prize = prizeList.get(0);
			String prizeName = prize.getDef(player.getWorld()).getName().toLowerCase();

			player.message(otherPlayer.getUsername() + " got a " + prizeName + " from your present!");
			otherPlayer.message("You take out a " + prizeName + ".");
			delay();

			String playerDialogue;

			if(prize.getCatalogId() == ItemId.COAL.id()) {
				switch(DataConversions.random(0, 8)) {
					default:
					case 0:
						playerDialogue = "No presents for you!";
						break;
					case 1:
						playerDialogue = "Naughty boys and girls get coal for christmas";
						break;
					case 2:
						playerDialogue = "Oh, behave!";
						break;
					case 3:
						playerDialogue = "I can get you off the Naughty List, for a price...";
						break;
					case 4:
						playerDialogue = "Darn! Almost had it.";
						break;
					case 5:
						playerDialogue = "a glitch for a grinch, a pile of coal for you!";
						break;
					case 6:
						playerDialogue = "For not believing in Christmas you get a lump of coal";
						break;
					case 7:
						playerDialogue = "for behavior so cold, you are getting a lump of coal";
						break;
					case 8:
						playerDialogue = "I know what you did last summer";
						break;
				}
			} else {
				playerDialogue = "Happy holidays";
			}

			player.getUpdateFlags().setChatMessage(new ChatMessage(player, playerDialogue, null));

			otherPlayer.getCarriedItems().getInventory().add(prize);
			player.getCarriedItems().remove(item);
		}
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		player.message("It would be selfish to keep this for myself");
		player.message("I should give it to someone else");
	}

	@Override
	public boolean blockUsePlayer(Player player, Player otherPlayer, Item item) {
		return item.getCatalogId() == ItemId.PRESENT.id();
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.PRESENT.id();
	}
}
