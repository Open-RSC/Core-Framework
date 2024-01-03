package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.content.DropTable;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage; // TODO: this should likely be removed in favour of more authentic quest-style dialogue
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UsePlayerTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public class Present implements UsePlayerTrigger, OpInvTrigger {

	private static DropTable cabbagePresentDrops;
	private static DropTable openRSCPresentDrops;

	static {
		cabbagePresentDrops = new DropTable();
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
		holidayTable.addItemDrop(ItemId.CHRISTMAS_CRACKER.id(), 1, 32, false);
		holidayTable.addItemDrop(ItemId.SANTAS_HAT.id(), 1, 24, false);
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
		holidayTable.addItemDrop(ItemId.PINK_SANTA_HAT.id(), 1, 9, false);

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
		cabbagePresentDrops.addTableDrop(holidayTable, 170);
		cabbagePresentDrops.addTableDrop(junkTable, 86);
		/*
		cabbagePresentDrops.addTableDrop(bronzeTable, 14);
		cabbagePresentDrops.addTableDrop(ironTable, 13);
		cabbagePresentDrops.addTableDrop(steelTable, 12);
		cabbagePresentDrops.addTableDrop(blackTable, 12);
		cabbagePresentDrops.addTableDrop(mithTable, 11);
		cabbagePresentDrops.addTableDrop(addyTable, 10);
		cabbagePresentDrops.addTableDrop(runeTable, 6);
		cabbagePresentDrops.addTableDrop(ultraRareTable, 2);
		 */

		/**
		 *
		 * Open RSC server, only allows innocuous drops within the authentic item id limit
		 *
		 */

		openRSCPresentDrops = new DropTable();
		DropTable petTable = new DropTable();
		DropTable foodTable = new DropTable();
		DropTable alcoholTable = new DropTable();
		DropTable coolItemsTable = new DropTable();
		DropTable gnomeRobesTable = new DropTable();
		DropTable cuteSocksTable = new DropTable();
		DropTable unobtainableTable = new DropTable();

		/**
		 * Pet table (a bit rude to impose a pet on a friend imo!)
		 */
		petTable.addItemDrop(ItemId.SWAMP_TOAD.id(), 1, 1);
		petTable.addItemDrop(ItemId.KITTEN.id(), 1, 1); // turns into swamp toad if gertrude's cat not complete by otherPlayer

		/**
		 * Food Table (Semi-common gifts IRL)
		 */
		foodTable.addItemDrop(ItemId.CABBAGE.id(), 1, 2); // yum!
		foodTable.addItemDrop(ItemId.CHOC_CRUNCHIES.id(), 1, 3); // this additionally gives Bucket of Milk later
		foodTable.addItemDrop(ItemId.SPICE_CRUNCHIES.id(), 1, 3); // this additionally gives Bucket of Milk later
		foodTable.addItemDrop(ItemId.CHOCOLATE_SLICE.id(), 1, 3); // this additionally gives Bucket of Milk later
		foodTable.addItemDrop(ItemId.UGTHANKI_KEBAB.id(), 1, 1);
		foodTable.addItemDrop(ItemId.TASTY_UGTHANKI_KEBAB.id(), 1, 1);
		foodTable.addItemDrop(ItemId.BREAD_DOUGH.id(), 1, 3); // "Friendship Bread" https://en.wikipedia.org/wiki/Amish_friendship_bread; sometimes happens near xmas

		/**
		 * Alcohol table (a nice gift if they drink and a bad gift if not)
		 */
		alcoholTable.addItemDrop(ItemId.BRANDY.id(), 1, 1);
		alcoholTable.addItemDrop(ItemId.WHISKY.id(), 1, 1);
		alcoholTable.addItemDrop(ItemId.VODKA.id(), 1, 1);
		alcoholTable.addItemDrop(ItemId.GIN.id(), 1, 1);
		alcoholTable.addItemDrop(ItemId.POISON_CHALICE.id(), 1, 1);

		/**
		 * Unique gift ideas!
		 */
		coolItemsTable.addItemDrop(ItemId.OYSTER_PEARL_BOLT_TIPS.id(), 5, 1); // maybe can make these into a necklace lol?
		coolItemsTable.addItemDrop(ItemId.GNOME_BALL.id(), 1, 1);
		coolItemsTable.addItemDrop(ItemId.PARAMAYA_REST_TICKET.id(), 1, 2); // hotel gift card
		coolItemsTable.addItemDrop(ItemId.SHIP_TICKET.id(), 1, 2); // omg a cruise???

		/**
		 * Clothes for christmas
		 */
		// gnome robe bottoms
		gnomeRobesTable.addItemDrop(ItemId.GNOME_ROBE_PINK.id(), 1, 1);
		gnomeRobesTable.addItemDrop(ItemId.GNOME_ROBE_GREEN.id(), 1, 1);
		gnomeRobesTable.addItemDrop(ItemId.GNOME_ROBE_PURPLE.id(), 1, 1); // dark blue, not purple btw
		gnomeRobesTable.addItemDrop(ItemId.GNOME_ROBE_CREAM.id(), 1, 1);
		gnomeRobesTable.addItemDrop(ItemId.GNOME_ROBE_BLUE.id(), 1, 1); // sky blue

		// gnome robe hats
		gnomeRobesTable.addItemDrop(ItemId.GNOMESHAT_PINK.id(), 1, 1);
		gnomeRobesTable.addItemDrop(ItemId.GNOMESHAT_GREEN.id(), 1, 1);
		gnomeRobesTable.addItemDrop(ItemId.GNOMESHAT_PURPLE.id(), 1, 1); // dark blue, not purple btw
		gnomeRobesTable.addItemDrop(ItemId.GNOMESHAT_CREAM.id(), 1, 1);
		gnomeRobesTable.addItemDrop(ItemId.GNOMESHAT_BLUE.id(), 1, 1); // sky blue

		// gnome robe tops
		gnomeRobesTable.addItemDrop(ItemId.GNOME_TOP_PINK.id(), 1, 1);
		gnomeRobesTable.addItemDrop(ItemId.GNOME_TOP_GREEN.id(), 1, 1);
		gnomeRobesTable.addItemDrop(ItemId.GNOME_TOP_PURPLE.id(), 1, 1); // dark blue, not purple btw
		gnomeRobesTable.addItemDrop(ItemId.GNOME_TOP_CREAM.id(), 1, 1);
		gnomeRobesTable.addItemDrop(ItemId.GNOME_TOP_BLUE.id(), 1, 1); // sky blue

		/**
		 * Socks for christmas!
		 */
		// gnome "socks"
		cuteSocksTable.addItemDrop(ItemId.BOOTS_PINK.id(), 1, 1);
		cuteSocksTable.addItemDrop(ItemId.BOOTS_GREEN.id(), 1, 1);
		cuteSocksTable.addItemDrop(ItemId.BOOTS_PURPLE.id(), 1, 1); // dark blue, not purple btw
		cuteSocksTable.addItemDrop(ItemId.BOOTS_CREAM.id(), 1, 1);
		cuteSocksTable.addItemDrop(ItemId.BOOTS_BLUE.id(), 1, 1); // sky blue
		cuteSocksTable.addItemDrop(ItemId.DESERT_BOOTS.id(), 1, 1);

		/**
		 * Items that are normally unobtainable & kind of make sense to give as xmas gifts
		 * These are both food items.
		 */
		unobtainableTable.addItemDrop(ItemId.SPECIAL_CURRY_UNUSED.id(), 1, 2); // unobtainable item
		unobtainableTable.addItemDrop(ItemId.GNOME_BATTA_UNUSED.id(), 1, 1); // unobtainable item

		/**
		 * Bring all the tables together
		 */
		openRSCPresentDrops.addTableDrop(petTable, 1);
		openRSCPresentDrops.addTableDrop(foodTable, 4);
		openRSCPresentDrops.addTableDrop(alcoholTable, 3);
		openRSCPresentDrops.addTableDrop(coolItemsTable, 2);
		openRSCPresentDrops.addTableDrop(gnomeRobesTable, 3);
		openRSCPresentDrops.addTableDrop(cuteSocksTable, 3);
		openRSCPresentDrops.addTableDrop(unobtainableTable, 1);
	}

	@Override
	public void onUsePlayer(Player player, Player otherPlayer, Item item) {
		if (item.getCatalogId() == ItemId.PRESENT.id()) {
			// prevent player from using present on ironmen
			if (otherPlayer.isIronMan(IronmanMode.Ironman.id()) || otherPlayer.isIronMan(IronmanMode.Ultimate.id())
				|| otherPlayer.isIronMan(IronmanMode.Hardcore.id()) || otherPlayer.isIronMan(IronmanMode.Transfer.id())) {
				player.playerServerMessage(MessageType.QUEST, otherPlayer.getUsername() + " is an Ironman. " + (otherPlayer.isMale() ? "He" : "She") + " stands alone.");
				return;
			}

			// prevent player from using present on themselves
			if(!config().CAN_USE_CRACKER_ON_SELF && !player.isAdmin() && player.getCurrentIP().equalsIgnoreCase(otherPlayer.getCurrentIP())) {
				player.playerServerMessage(MessageType.QUEST, otherPlayer.getUsername() + " does not want your present...");
				return;
			}

			// prevent player from using present on QoL opt out accounts, accounts who have purposely avoided inauthentic features
			if (otherPlayer.getQolOptOut()) {
				player.playerServerMessage(MessageType.QUEST, otherPlayer.getUsername() + " does not want your present...");
				player.playerServerMessage(MessageType.QUEST, "They have opted out of new features which are inauthentic.");
				return;
			}

			player.face(otherPlayer);
			otherPlayer.face(player);

			thinkbubble(item);

			player.getCarriedItems().remove(item);

			if (config().WANT_EQUIPMENT_TAB) { // TODO: this is not a very good way to detect Cabbage server config
				cabbageRollAndAwardPresent(player, otherPlayer);
			} else { // NOT cabbage config
				openRSCRollAndAwardPresent(player, otherPlayer);
			}
		}
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
			|| player.isIronMan(IronmanMode.Hardcore.id())) {
			thinkbubble(item);
			player.playerServerMessage(MessageType.QUEST, "You rip open the present and thrust your hand inside...");
			delay(3);
			player.getCarriedItems().remove(item);

			if (config().WANT_EQUIPMENT_TAB) { // TODO: this is not a very good way to detect Cabbage server config
				cabbageRollAndAwardPresent(player);
			} else {
				// this code is usually unreachable, since no other official config has ironman mode enabled
				openRSCRollAndAwardPresent(player);
			}

		} else {
			player.message("It would be selfish to keep this for myself");
			player.message("I should give it to someone else");
		}
	}

	@Override
	public boolean blockUsePlayer(Player player, Player otherPlayer, Item item) {
		return item.getCatalogId() == ItemId.PRESENT.id();
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.PRESENT.id();
	}

	private void cabbageRollAndAwardPresent(Player player, Player otherPlayer, boolean selfUse) {
		if (!selfUse) {
			player.playerServerMessage(MessageType.QUEST, "You give a present to " + otherPlayer.getUsername());
			otherPlayer.playerServerMessage(MessageType.QUEST, player.getUsername() + " handed you a present...");
			delay();
			otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present and reach your hand inside...");
			delay();
		}

		ArrayList<Item> prizeList = cabbagePresentDrops.rollItem(false, otherPlayer);
		if (prizeList.size() <= 0) return;
		Item prize = prizeList.get(0);
		String prizeName = prize.getDef(player.getWorld()).getName().toLowerCase();

		if (!selfUse) {
			player.playerServerMessage(MessageType.QUEST, otherPlayer.getUsername() + " got a " + prizeName + " from your present!");
		}

		otherPlayer.playerServerMessage(MessageType.QUEST, "You take out a " + prizeName + ".");
		delay();

		String playerDialogue;

		if (prize.getCatalogId() == ItemId.COAL.id()) {
			switch (DataConversions.random(0, 8)) {
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

		if (!selfUse) {
			player.getUpdateFlags().setChatMessage(new ChatMessage(player, playerDialogue, null));
		}

		otherPlayer.getCarriedItems().getInventory().add(prize);
	}

	private void cabbageRollAndAwardPresent(Player player, Player otherPlayer) {
		cabbageRollAndAwardPresent(player, otherPlayer, false);
	}

	private void cabbageRollAndAwardPresent(Player player) {
		cabbageRollAndAwardPresent(player, player, true);
	}

	private void openRSCRollAndAwardPresent(Player player, Player otherPlayer, boolean selfUse) {
		ArrayList<Item> prizeList = openRSCPresentDrops.rollItem(false, otherPlayer);
		if (prizeList.size() <= 0) return;
		Item prize = prizeList.get(0);
		String prizeName = prize.getDef(player.getWorld()).getName().toLowerCase();

		int unwrapDelay = 2;
		int readingDelay = 2;

		if (!selfUse) {
			player.playerServerMessage(MessageType.QUEST, "You give a present to " + otherPlayer.getUsername());
			otherPlayer.playerServerMessage(MessageType.QUEST, player.getUsername() + " handed you a present...");
			delay();

			if (prize.getDef(player.getWorld()).getId() == ItemId.KITTEN.id() && otherPlayer.getQuestStage(Quests.GERTRUDES_CAT) != -1) {
				// kitten selected, but otherPlayer ineligible to receive it, swamp toad substituted
				player.playerServerMessage(MessageType.QUEST, "You hope they enjoy the swamp toad you got them!");
			} else if ((prize.getDef(player.getWorld()).getId() >= ItemId.BOOTS_PINK.id() && prize.getDef(player.getWorld()).getId() <= ItemId.BOOTS_BLUE.id())
				|| prize.getDef(player.getWorld()).getId() == ItemId.DESERT_BOOTS.id()) {
				player.playerServerMessage(MessageType.QUEST, "You hope they enjoy the socks you got them!");
			} else {
				player.playerServerMessage(MessageType.QUEST, "You hope they enjoy the " + prizeName + " you got them!");
			}

			switch (ItemId.getById(prize.getDef(player.getWorld()).getId())) {

				/**
				 * Pet table
				 */
				case KITTEN:
					if (otherPlayer.getQuestStage(Quests.GERTRUDES_CAT) == -1) { // has completed quest requirement
						otherPlayer.playerServerMessage(MessageType.QUEST, "As you unwrap the present, you hear a mewing noise...!!");
						delay(unwrapDelay);
						player.playerServerMessage(MessageType.QUEST, "@yel@" + otherPlayer.getUsername() + ": oh my gosh a kitten!!?!");
						otherPlayer.getCarriedItems().getInventory().add(prize);
						say(otherPlayer, "oh my gosh a kitten!!?!");
						otherPlayer.playerServerMessage(MessageType.QUEST, "@yel@" + player.getUsername() + ": yeah, I hope you enjoy your new pet and take good care of them!");
						say(player, "yeah, I hope you enjoy your new pet and take good care of them!");

						break;
					} else {
						prize = new Item(ItemId.SWAMP_TOAD.id());
						// fall through to swamp toad case
					}
				case SWAMP_TOAD:
					otherPlayer.playerServerMessage(MessageType.QUEST, "As you unwrap the present, you hear a croaking noise...!!");
					delay(unwrapDelay);
					player.playerServerMessage(MessageType.QUEST, "@yel@" + otherPlayer.getUsername() + ": oh my gosh a toad!!?!");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					say(otherPlayer, "oh my gosh a toad!!?!");
					otherPlayer.playerServerMessage(MessageType.QUEST, "@yel@" + player.getUsername() + ": yeah, I hope you enjoy your new pet and take good care of them!");
					say(player, "yeah, I hope you enjoy your new pet and take good care of them!");
					player.playerServerMessage(MessageType.QUEST, "@red@Server Message: @whi@please do not viciously dismember your new pet toad");
					otherPlayer.playerServerMessage(MessageType.QUEST, "@red@Server Message: @whi@please do not viciously dismember your new pet toad");
					break;

				/**
				 * Food table
				 */
				case CABBAGE:
					otherPlayer.playerServerMessage(MessageType.QUEST, "As you unwrap the present, you can smell something weird...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "it's a cabbage...!!!");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;

				case CHOC_CRUNCHIES:
				case SPICE_CRUNCHIES:
				case CHOCOLATE_SLICE:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					if (prize.getDef(player.getWorld()).getId() == ItemId.CHOCOLATE_SLICE.id()) {
						otherPlayer.playerServerMessage(MessageType.QUEST, "Awh, it's some really nice homemade chocolate cake!");
					} else {
						otherPlayer.playerServerMessage(MessageType.QUEST, "Awh, it's some really nice homemade " + prizeName + "!");
					}
					otherPlayer.getCarriedItems().getInventory().add(prize);
					delay(readingDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "and it looks like there's also an entire bucket of milk inside!");
					otherPlayer.getCarriedItems().getInventory().add(new Item(ItemId.MILK.id()));
					break;

				case UGTHANKI_KEBAB:
				case TASTY_UGTHANKI_KEBAB:
					otherPlayer.playerServerMessage(MessageType.QUEST, "As you unwrap the present, you can smell something weird...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "it's an ugthanki kebab!!");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;

				case BREAD_DOUGH:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "Ah! it's an Amish Friendship Bread starter...!");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					delay(readingDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "You're supposed to break off a piece to act as a starter yeast, and bake the rest.");
					delay(readingDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "Take the piece you saved and use more flour & water to create volume");
					delay(readingDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "The yeast should grow over time if you feed it sugar,");
					delay(readingDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "and then you can pass it on to a friend :-)");
					break;

				/**
				 * Alcohol table
				 */
				case BRANDY:
				case WHISKY:
				case VODKA:
				case GIN:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "Oh, nice! It's some gnome " + prizeName + "!");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;
				case POISON_CHALICE:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "... it's some kind of strange cocktail of random spirits!");
					// see https://twitter.com/JagexAsh/status/1073671447753711616 for more info on poison chalice
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;

				/**
				 * Unique gift ideas!
				 */
				case OYSTER_PEARL_BOLT_TIPS:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "Ooh! It's some pointed pearls!");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;
				case GNOME_BALL:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "Oh, fun! A gnome ball! I always wanted one of those");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;
				case PARAMAYA_REST_TICKET:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "It's a gift card for a free stay in the Paramaya Inn!");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					if (otherPlayer.getQuestStage(Quests.SHILO_VILLAGE) != -1) {
						// otherPlayer is currently unable to access the inn, located inside shilo village
						delay(readingDelay + 1);
						otherPlayer.playerServerMessage(MessageType.QUEST, "... Wonder where that is?");
					}
					break;
				case SHIP_TICKET:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "WOW!! it's a ticket for @mag@a trip on a cruise ship!!!!");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;

				/**
				 * Clothes table (Just gnome clothing minus boots)
				 */
				case GNOME_ROBE_PINK: // gnome robe skirts
				case GNOME_ROBE_GREEN:
				case GNOME_ROBE_PURPLE:
				case GNOME_ROBE_CREAM:
				case GNOME_ROBE_BLUE:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "it's a very nice pastel dress");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;
				case GNOMESHAT_PINK: // gnome hats
				case GNOMESHAT_GREEN:
				case GNOMESHAT_PURPLE:
				case GNOMESHAT_CREAM:
				case GNOMESHAT_BLUE:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "it's a very nice pastel hat");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;
				case GNOME_TOP_PINK: // gnome robe tops
				case GNOME_TOP_GREEN:
				case GNOME_TOP_PURPLE:
				case GNOME_TOP_CREAM:
				case GNOME_TOP_BLUE:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "it's a very nice pastel shirt");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;

				/**
				 * Socks for Christmas!
				 */
				case BOOTS_PINK: // gnome boots
				case BOOTS_GREEN:
				case BOOTS_PURPLE:
				case BOOTS_CREAM:
				case BOOTS_BLUE:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "oh! it's a pair of cute socks");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;
				case DESERT_BOOTS:
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "oh! it's a pair of socks...!");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;

				/**
				 * Unobtainable items
				 */
				case SPECIAL_CURRY_UNUSED:
					otherPlayer.playerServerMessage(MessageType.QUEST, "As you unwrap the present, you can smell something strange...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "it's a special christmas curry!!!");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					delay(readingDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "I wonder how they made it?"); // reference to it being unobtainable
					break;
				case GNOME_BATTA_UNUSED:
					otherPlayer.playerServerMessage(MessageType.QUEST, "As you unwrap the present, you can smell something weird...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "it's a homemade gnome batta... kind of smells like pants");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					delay(readingDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "I wonder how they made it?"); // reference to it being unobtainable
					break;

				/**
				 * Bug in future code if reach default statement
				 */
				default:
					// should not be reached
					otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
					delay(unwrapDelay);
					otherPlayer.playerServerMessage(MessageType.QUEST, "oh! it's a " + prizeName + "!");
					otherPlayer.getCarriedItems().getInventory().add(prize);
					break;
			}
		} else {
			// TODO: Should port some of the above unique dialogues & behaviour
			// but in 2020, this code is unreachable on any Open RSC hosted server
			otherPlayer.playerServerMessage(MessageType.QUEST, "You unwrap the present...");
			delay(unwrapDelay);
			otherPlayer.playerServerMessage(MessageType.QUEST, "oh! it's a " + prizeName + "!");
			otherPlayer.getCarriedItems().getInventory().add(prize);
		}
	}

	private void openRSCRollAndAwardPresent(Player player, Player otherPlayer) {
		openRSCRollAndAwardPresent(player, otherPlayer, false);
	}

	private void openRSCRollAndAwardPresent(Player player) {
		openRSCRollAndAwardPresent(player, player, true);
	}

}
