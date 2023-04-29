package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.authentic.skills.firemaking.Firemaking;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class InvAction implements OpInvTrigger {

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return inArray(item.getCatalogId(),
			ItemId.BARCRAWL_CARD.id(), ItemId.INSTRUCTION_MANUAL.id(), ItemId.DIARY.id(),
			ItemId.DRY_STICKS.id(), ItemId.SCRUFFY_NOTE.id(), ItemId.MAGIC_SCROLL.id(),
			ItemId.TOURIST_GUIDE.id(), ItemId.TREE_GNOME_TRANSLATION.id(), ItemId.WAR_SHIP.id(),
			ItemId.GLOUGHS_JOURNAL.id(), ItemId.INVOICE.id(), ItemId.GLOUGHS_NOTES.id(),
			ItemId.MESSENGER_PIGEONS.id(), ItemId.JANGERBERRIES.id(), ItemId.A_FREE_SHANTAY_DISCLAIMER.id(),
			ItemId.TECHNICAL_PLANS.id(), ItemId.ANA_IN_A_BARREL.id(), ItemId.RANDASS_JOURNAL.id(),
			ItemId.A_DOLL_OF_IBAN.id(), ItemId.STAFF_OF_IBAN_BROKEN.id(), ItemId.NIGHTSHADE.id(),
			ItemId.SHAMAN_ROBE.id(), ItemId.SPELL_SCROLL.id(), ItemId.BOOK_OF_EXPERIMENTAL_CHEMISTRY.id(),
			ItemId.LEVEL_1_CERTIFICATE.id(), ItemId.LEVEL_2_CERTIFICATE.id(), ItemId.LEVEL_3_CERTIFICATE.id(),
			ItemId.DIGSITE_SCROLL.id(), ItemId.ASTROLOGY_BOOK.id(), ItemId.STONE_TABLET.id(),
			ItemId.OYSTER.id(), ItemId.SCRUMPLED_PIECE_OF_PAPER.id(), ItemId.NULODIONS_NOTES.id(), ItemId.OLD_JOURNAL.id(),
			ItemId.BURNTPIE.id(), ItemId.BURNT_STEW.id(), ItemId.BURNT_CURRY.id(),
			ItemId.BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER.id(), ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id(), ItemId.SPADE.id());
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		int id = item.getCatalogId();

		if (id == ItemId.OYSTER.id()) {
			handleOyster(player, id);
		}
		else if (id == ItemId.SCRUMPLED_PIECE_OF_PAPER.id()) {
			handleScrumpledPieceOfPaper(player);
		}
		else if (id == ItemId.ASTROLOGY_BOOK.id()) {
			handleAstrologyBook(player);
		}
		else if (id == ItemId.BARCRAWL_CARD.id()) {
			handleBarcrawlCard(player);
		}
		else if (id == ItemId.INSTRUCTION_MANUAL.id()) {
			handleInstructionManual(player);
		}
		else if (id == ItemId.TREE_GNOME_TRANSLATION.id()) {
			handleTreeGnomeTranslation(player);
		}
		else if (id == ItemId.GLOUGHS_JOURNAL.id()) {
			handleGloughsJournal(player);
		}
		else if (id == ItemId.INVOICE.id()) {
			handleInvoice(player);
		}
		else if (id == ItemId.GLOUGHS_NOTES.id()) {
			handleGloughsNotes(player);
		}
		else if (id == ItemId.WAR_SHIP.id()) {
			handleWarShip(player);
		}
		else if (id == ItemId.DIARY.id()) {
			handleDiary(player);
		}
		else if (id == ItemId.DRY_STICKS.id()) {
			handleDrySticks(player);
		}
		else if (id == ItemId.SCRUFFY_NOTE.id()) {
			handleScruffyNote(player);
	    }
		else if (id == ItemId.MAGIC_SCROLL.id()) {
			handleMagicScroll(player);
		}
		else if (id == ItemId.SPELL_SCROLL.id()) {
			handleSpellScroll(player);
		}
		else if (id == ItemId.TOURIST_GUIDE.id()) {
			handleTouristGuide(player);
		}
		else if (id == ItemId.MESSENGER_PIGEONS.id()) {
			handleMessengerPigeons(player);
		}
		else if (id == ItemId.JANGERBERRIES.id()) {
			handleJangerberries(player);
		}
		else if (id == ItemId.A_FREE_SHANTAY_DISCLAIMER.id()) {
			handleShantayDisclaimer(player);
		}
		else if (id == ItemId.TECHNICAL_PLANS.id()) {
			handleTechnicalPlans(player);
		}
		else if (id == ItemId.ANA_IN_A_BARREL.id()) {
			handleAnaInABarrel(player);
		}
		else if (id == ItemId.RANDASS_JOURNAL.id()) {
			handleRandassJournal(player);
		}
		else if (id == ItemId.A_DOLL_OF_IBAN.id()) {
			handleADollOfIban(player);
		}
		else if (id == ItemId.STAFF_OF_IBAN_BROKEN.id()) {
			handleStaffOfIban(player);
		}
		else if (id == ItemId.NIGHTSHADE.id()) {
			handleNightshade(player);
		}
		else if (id == ItemId.SHAMAN_ROBE.id()) {
			handleShamanRobe(player);
		}
		else if (id == ItemId.BOOK_OF_EXPERIMENTAL_CHEMISTRY.id()) {
			handleBookOfExperimentalChemistry(player);
		}
		else if (id == ItemId.LEVEL_1_CERTIFICATE.id()) {
			handleLevelOneCertificate(player);
		}
		else if (id == ItemId.LEVEL_2_CERTIFICATE.id()) {
			handleLevelTwoCertificate(player);
		}
		else if (id == ItemId.LEVEL_3_CERTIFICATE.id()) {
			handleLevelThreeCertificate(player);
		}
		else if (id == ItemId.DIGSITE_SCROLL.id()) {
			handleDigsiteScroll(player);
		}
		else if (id == ItemId.STONE_TABLET.id()) {
			handleStoneTablet(player);
		}
		else if (id == ItemId.NULODIONS_NOTES.id()) {
			handleNulodionsNotes(player);
		}
		else if (id == ItemId.OLD_JOURNAL.id()) {
			handleOldJournal(player);
		}
		else if (id == ItemId.BURNTPIE.id() && command.equalsIgnoreCase("empty dish")) {
			if (player.getCarriedItems().remove(new Item(item.getCatalogId())) == -1) return;
			player.message("you remove the burnt pie from the pie dish");
			player.getCarriedItems().getInventory().add(new Item(ItemId.PIE_DISH.id()));
		}
		else if (id == ItemId.BURNT_STEW.id() && command.equalsIgnoreCase("empty")) {
			if (player.getCarriedItems().remove(new Item(item.getCatalogId())) == -1) return;
			player.message("you remove the burnt stew from the bowl");
			player.getCarriedItems().getInventory().add(new Item(ItemId.BOWL.id()));
		}
		else if (id == ItemId.BURNT_CURRY.id() && command.equalsIgnoreCase("empty")) {
			if (player.getCarriedItems().remove(new Item(item.getCatalogId())) == -1) return;
			player.message("you remove the burnt curry from the bowl");
			player.getCarriedItems().getInventory().add(new Item(ItemId.BOWL.id()));
		}
		else if (id == ItemId.BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER.id() && command.equalsIgnoreCase("empty")) {
			if (player.getCarriedItems().remove(new Item(item.getCatalogId())) == -1) return;
			player.message("You empty the plain water out of the Blessed Golden Bowl.");
			player.getCarriedItems().getInventory().add(new Item(ItemId.BLESSED_GOLDEN_BOWL.id()));
		}
		else if (id == ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id() && command.equalsIgnoreCase("empty")) {
			if (player.getCarriedItems().remove(new Item(item.getCatalogId())) == -1) return;
			player.message("You empty the plain water out of the Golden Bowl.");
			player.getCarriedItems().getInventory().add(new Item(ItemId.GOLDEN_BOWL.id()));
		}
		else if (id == ItemId.SPADE.id()) {
			// nothing - no action/message was triggered with spade's dig option
		}
	}

	private void handleOyster(Player player, int oyster) {
		player.message("you open the oyster shell");
		if (DataConversions.random(0, 10) == 1) {
			if (player.getCarriedItems().remove(new Item(oyster)) == -1) return;
			player.getCarriedItems().getInventory().add(new Item(ItemId.OYSTER_PEARLS.id()));
		} else {
			if (player.getCarriedItems().remove(new Item(oyster)) == -1) return;
			player.getCarriedItems().getInventory().add(new Item(ItemId.EMPTY_OYSTER.id()));
		}
	}

	private void handleScrumpledPieceOfPaper(Player player) {
		ActionSender.sendBox(player,
		"@gre@*** Delicious Ugthanki Kebab *** % %"
			+ "Ingredients : Cooked Ugthanki meat %"
			+ "Flour %Water %Onion %Tomato % %"
			+ "@yel@The Ugthanki meat should be nicely grilled. %"
			+ "@yel@Next take the flour and water and make some Pitta Bread. %"
			+ "@yel@You'll need a range to do this. % %"
			+ "@yel@Take an onion and chop it into a bowl. %"
			+ "@yel@Take a tomato and chop it into the onion mixture. %"
			+ "@yel@Chop the meat into the Onion and Tomato mixture. %"
			+ "@yel@Finally fill the pitta bread with the Ugthanki, Onion and %"
			+ "@yel@Tomato mixture to make your delicious Ugthanki Kebab.", true);
	}

	private void handleAstrologyBook(Player player) {
		ActionSender.sendBox(player, "THE TALE OF SCORPIUS: %"
			+ "A HISTORY OF ASTROLOGY IN RUNESCAPE. %"
			+ "At the start of the Fourth age, "
			+ "A learned man by the name of Scorpius, "
			+ "known well for his powers of vision and magic, "
			+ "sought communion with the gods of the world. "
			+ "After many years of study he developed a machine infused with magical power, "
			+ "that had the ability to pierce into the very heavens itself, "
			+ "a huge eye that gave the user sight like never before. "
			+ "As Time passed Scorpius grew adept at his skill, "
			+ "and followed the star movements themselves which he mapped and named, "
			+ "and are still used to this very day. "
			+ "Before long Scorpius used his knowledge for predicting the future and gaining dark knowledge. "
			+ "Years after his death, "
			+ "the plans of Scorpius were uncovered at an ancient Zamorakian worship site, "
			+ "and the heavenly eye was again constructed. "
			+ "Since then many have learned the ways of the Astrologer. "
			+ "Some claim his ghost still wanders ever seeking his master Zamorak, "
			+ "and will grant those adept in the arts of the Astrologer a blessing of power. "
			+ "Here ends the tale of how Astrology entered the known world.", true);
	}

	private void handleBarcrawlCard(Player player) {
		if (player.getCache().hasKey("barone") &&
			player.getCache().hasKey("bartwo") &&
			player.getCache().hasKey("barthree") &&
			player.getCache().hasKey("barfour") &&
			player.getCache().hasKey("barfive") &&
			player.getCache().hasKey("barsix")) {
			player.message("You are to drunk to be able to read the barcrawl card");
			return;

		}
		mes("The official Alfred Grimhand barcrawl");
		delay(3);
		player.message(!player.getCache().hasKey("barone") ?
			"The jolly boar inn - not completed" : "The jolly boar inn - completed");
		delay(2);
		player.message(!player.getCache().hasKey("bartwo") ?
			"The blue moon inn - not completed" : "The blue moon inn - completed");
		delay(2);
		player.message(!player.getCache().hasKey("barthree") ?
			"The rising sun - not completed" : "The rising sun - completed");
		delay(2);
		player.message(!player.getCache().hasKey("barfour") ?
			"The dead man's chest - not completed" : "The dead man's chest - completed");
		delay(2);
		player.message(!player.getCache().hasKey("barfive") ?
			"The forester's arms - not completed" : "The forester's arms - completed");
		delay(2);
		player.message(!player.getCache().hasKey("barsix") ?
			"The rusty anchor - not completed" : "The rusty anchor - completed");
	}

	private void handleInstructionManual(Player player) {
		String[] options;
		options = new String[]{"Constructing the cannon", "Making ammo", "firing the cannon", "warrenty"};
		mes("the manual has four pages");
		delay(3);
		int option = multi(player, options);

		/*if (player.isBusy()) {
			return;
		}*/

		if (option == 0) {
			ActionSender.sendBox(player, "Constructing the cannon% %"
				+ "To construct the cannon, firstly set down Dwarf cannon base on the ground.% %"
				+ "Next add the Dwarf cannon stand to the Dwarf cannon base.% %"
				+ "Then add the Dwarf cannon barrels (this can be tiring work).% %"
				+ "Last of all add the Dwarf cannon furnace which powers the cannon.% %"
				+ "You should now have a fully set up dwarf multi cannon ready to go splat some nasty creatures.% % % %"
				+ "@red@WARNING: You should be well rested before attempting to @red@lift the heavy cannon", true);

		}

		else if (option == 1) {
			ActionSender.sendBox(player, "Making ammo% %"
				+ "The ammo for the cannon is made from steel bars.% %"
				+ "Firstly you must heat up a steel bar in a furnace% %"
				+ "Then pour the molten steel into a cannon ammo mould% %"
				+ "You should now have a ready to fire multi cannon ball% %", true);
		}

		else if (option == 2) {
			ActionSender.sendBox(player, "Firing the cannon% %"
				+ "The cannon will only fire when monsters are available to target.% %"
				+ "If you are carrying enough ammo the multi cannon will fire up to 20 rounds before stopping.% %"
				+ "The cannon will automatically target non friendly creatures.% %"
				+ "@red@Warning - firing the cannon is exhausting work and can @red@leave adventurers too fatigued to carry the cannon, so @red@rest well before using", true);
		}

		else if (option == 3) {
			ActionSender.sendBox(player, "@red@Dwarf cannon warrenty% %"
				+ "If your cannon is stolen or lost, after or during being set up, the dwarf engineer will happily replace the parts% %"
				+ "However cannon parts that were given away or dropped will not be replaced for free% %"
				+ "It is only possible to operate one cannon at a time% % % % % %"
				+ "by order of the dwarwven black guard", true);
		}
	}

	private void handleTreeGnomeTranslation(Player player) {
		mes("the book contains the alphabet...");
		delay(3);
		mes("translated into the old gnome tounge");
		delay(3);
		// http://i.imgur.com/XmSmukw.png
		ActionSender.sendBox(player,
		"@yel@A = @red@:v  @yel@B = @red@x:   @yel@C = @red@za% %"
			+ "@yel@D = @red@qe  @yel@E = @red@:::   @yel@F = @red@hb% %"
			+ "@yel@G = @red@qa  @yel@H = @red@x   @yel@I = @red@xa% %"
			+ "@yel@J = @red@ve  @yel@K = @red@vo   @yel@L = @red@va% %"
			+ "@yel@M = @red@ql  @yel@N = @red@ha   @yel@O = @red@ho% %"
			+ "@yel@P = @red@ni  @yel@Q = @red@na   @yel@R = @red@qi% %"
			+ "@yel@S = @red@sol  @yel@T = @red@lat   @yel@U = @red@z% %"
			+ "@yel@V = @red@::  @yel@W = @red@h:   @yel@X = @red@:i:% %"
			+ "@yel@Y = @red@im  @yel@Z = @red@dim% %", true);
	}

	private void handleGloughsJournal(Player player) {
		player.message("the book contains several hurried notes");
		int menu = multi(player, "the migration failed", "they must be stopped", "gaining support");
		if (menu == 0) {
			ActionSender.sendBox(player, "@red@The migration failed" + " %" + " %"
				+ "After spending half a century hiding underground you would think that the great migration would have improved life on runescape for tree gnomes. However, rather than the great liberation promised to us by king Healthorg at the end of the last age, we have been forced to live in hiding ,up trees or in the gnome maze, laughed at and mocked by man. Living in constant fear of human aggression, we are in a no better situation now then when we lived in the caves% %"
				+ "Change must come soon", true);
		} else if (menu == 1) {
			ActionSender.sendBox(player, "@red@They must be stopped" + " %" + " %"
				+ "Today I heard of three more gnomes slain by Khazard's human troops for fun, I cannot control my anger" + " %" + " %"
				+ "Humanity seems to have aquired a level of arrogance comparable to that of zamorak, killing and pillaging at will. We are small and at heart not warriors, but something must be done, we will pickup arms and go forth into the human world. We will defend ourselves and we will pursue justice for all gnomes who fell at the hands of humans", true);
		} else if (menu == 2) {
			ActionSender.sendBox(player, "@red@gaining support" + " %" + " %"
				+ "Some of the local gnomes seem strangly deluded about humans, many actually believe that humans are not all naturally evil but instead vary from person to person" + " %" + " %"
				+ "This sort of talk could be the end for the tree gnomes and i must continue to convince my fellow gnome folk the cold truth about these human creatures, how they will not stop until all gnome life is destroyed - unless  we can destroy them first", true);
		}
	}

	private void handleInvoice(Player player) {
		mes("you open the invoice");
		delay(3);
		ActionSender.sendBox(player,
			"@red@Order"
				+ " %" + " %" +
				"30 karamja battleships to be constructed in karamja"
				+ " %" + " %" +
				"Timber needed - 2000 tons"
				+ " %" + " %" +
				"Troops to be carried - 300"
			, true);
	}

	private void handleGloughsNotes(Player player) {
		mes("the notes contain sketched maps and diagrams");
		delay(3);
		mes("the text reads");
		delay(3);
		ActionSender.sendBox(player,
			"@red@invasion"
				+ " %" + " %" +
				"Troops board three fleets at karamja"
				+ " %" + " %" +
				"Fleet one attacks misthalin from south"
				+ " %" + " %" +
				"Fleet two groups at crandor and attacks Asgarnia from west coast"
				+ " %" + " %" +
				"Fleet three sails north attack Kandarin from south rienforced by gnome foot soldiers leaving gnome stronghold"
				+ " %" + " %" +
				"All prisoners to be slain"
			, true);
	}

	private void handleWarShip(Player player) {
		mes("you pretend to sail the ship across the floor");
		delay(3);
		mes("you soon become very bored");
		delay(3);
		mes("and realise you look quite silly");
		delay(3);
	}

	private void handleDiary(Player player) {
		mes("Pentember the 3rd");
		delay(3);
		mes("The experiment is going well - moved it to the wooden shed in the garden");
		delay(3);
		mes("It does too much damage in the house");
		delay(3);
		mes("Pentember the 6th");
		delay(3);
		mes("Don't want people getting in back garden to see the experiment");
		delay(3);
		mes("A guy called Professer Odenstein is fitting me a new security system");
		delay(3);
		mes("Pentember the 8th");
		delay(3);
		mes("The security system is done - by zamorak is it contrived!");
		delay(3);
		mes("Now to open my own back door");
		delay(3);
		mes("I lure a rat out of a hole in the back porch");
		delay(3);
		mes("I fit a magic curved piece of metal to its back");
		delay(3);
		mes("The rat goes back in the hole, and the door unlocks");
		delay(3);
		mes("The prof tells me that this is cutting edge technology!");
		delay(3);
	}

	private void handleDrySticks(Player player) {
		player.message("you rub together the dry sticks");
		if (getCurrentLevel(player, Skill.FIREMAKING.id()) < 30) {
			player.message("you need a firemaking level of 30 or above");
			player.message("the sticks smoke momentarily then die out");
			return;
		}
		mes("The sticks catch alight");
		delay(3);
		if (player.getCarriedItems().remove(new Item(ItemId.UNLIT_TORCH.id())) != -1) {
			player.message("you place the smouldering twigs to your torch");
			player.message("your torch lights");
			player.getCarriedItems().getInventory().add(new Item(ItemId.LIT_TORCH.id()));
			player.incExp(Skill.FIREMAKING.id(), Firemaking.getExp(player.getSkills().getMaxStat(Skill.FIREMAKING.id()), 25), true);
			if (player.getQuestStage(Quests.SEA_SLUG) == 5 && !player.getCache().hasKey("lit_torch")) {
				player.getCache().store("lit_torch", true);
			}
		} else {
			player.message("the sticks smoke momentarily then die out");
		}
	}

	private void handleScruffyNote(Player player) {
		mes("The handwriting on this note is very scruffy");
		delay(3);
		mes("as far as you can make out it says");
		delay(3);
		mes("Got a bncket of nnlk");
		delay(3);
		mes("Tlen qrind sorne lhoculate");
		delay(3);
		mes("vnith a pestal and rnortar");
		delay(3);
		mes("ald the grourd dlocolate to tho milt");
		delay(3);
		mes("fnales add 5cme snape gras5");
		delay(3);
		mes("you guess it really says something slightly different");
		delay(3);
	}

	private void handleMagicScroll(Player player) {
		if (player.getCache().hasKey("ardougne_scroll") && player.getQuestStage(Quests.PLAGUE_CITY) == -1) {
			mes("The scroll crumbles to dust");
			delay(3);
		} else {
			mes("You memorise what is written on the scroll");
			delay(3);
			mes("You can now cast the Ardougne teleport spell");
			delay(3);
			mes("Provided you have the required runes and magic level");
			delay(3);
			mes("The scroll crumbles to dust");
			delay(3);
		}
		player.getCarriedItems().remove(new Item(ItemId.MAGIC_SCROLL.id()));
		if (!player.getCache().hasKey("ardougne_scroll")) {
			player.getCache().store("ardougne_scroll", true);
		}
	}

	private void handleSpellScroll(Player player) {
		if (player.getCache().hasKey("watchtower_scroll") && player.getQuestStage(Quests.WATCHTOWER) == -1) {
			mes("The scroll crumbles to dust");
			delay(3);
		} else {
			mes("You memorise what is written on the scroll");
			delay(3);
			mes("You can now cast the Watchtower teleport spell");
			delay(3);
			mes("Provided you have the required runes and magic level");
			delay(3);
			mes("The scroll crumbles to dust");
			delay(3);
		}
		player.getCarriedItems().remove(new Item(ItemId.SPELL_SCROLL.id()));
		if (!player.getCache().hasKey("watchtower_scroll")) {
			player.getCache().store("watchtower_scroll", true);
		}
	}

	private void handleTouristGuide(Player player) {
		mes("You read the guide");
		delay(3);
		say(player, null, "This book is your guide to the vibrant city of Ardougne",
			"Ardougne is an exciting modern city",
			"Located on the sunny south coast of Kandarin");
		mes("Pick a chapter to read");
		delay(3);
		int chapter = multi(player,
			"Ardougne city of shopping",
			"Ardougne city of history",
			"Ardougne city of fun",
			"The area surrounding Ardougne",
			"I don't want to read this rubbish");
		if (chapter == 0) {
			mes("Ardougne city of shopping");
			delay(3);
			mes("Come sample the delights of the Ardougne market");
			delay(3);
			mes("The biggest in the known world");
			delay(3);
			mes("From spices to silk");
			delay(3);
			mes("There is something here for everyone");
			delay(3);
			mes("Other popular shops in the area include Zeneshas the armourer");
			delay(3);
			mes("And the adventurers supply store");
			delay(3);
		} else if (chapter == 1) {
			mes("Ardougne, city of history");
			delay(3);
			mes("Ardougne is an important historical city");
			delay(3);
			mes("One historic building is the magnificent Handelmort mansion");
			delay(3);
			mes("Currently owned by Lord Franis Bradley Handelmort");
			delay(3);
			mes("Ardougne castle in the east side of the city");
			delay(3);
			mes("Is now open to the public");
			delay(3);
			mes("and members of the holy order of ardougne paladins");
			delay(3);
			mes("Still wander the streets");
			delay(3);
		} else if (chapter == 2) {
			mes("Ardougne city of fun");
			delay(3);
			mes("If you're looking for entertainment in Ardougne");
			delay(3);
			mes("Why not pay a visit to Ardougne city zoo");
			delay(3);
			mes("Or relax for a drink in the flying horse inn");
			delay(3);
			mes("Or slaughter rats in Ardougne sewers");
			delay(3);
		} else if (chapter == 3) {
			mes("The area surrounding Ardougne");
			delay(3);
			mes("If you want to go further afield");
			delay(3);
			mes("Why not have a look at the pillars of Zanash");
			delay(3);
			mes("The mysterious marble pillars west of the city");
			delay(3);
			mes("Or the town of Brimhaven, on exotic Karamja");
			delay(3);
			mes("Is only a short boat ride away");
			delay(3);
			mes("Ships leaving regularily from Ardougne harbour");
			delay(3);
		} else if (chapter == 4) {
			say(player, null, "I don't want to read this rubbish");
		}
	}

	private void handleMessengerPigeons(Player player) {
		/* Biohazard quest */
		player.message("you open the cage");
		if ((player.getCache().hasKey("bird_feed")
			|| player.getQuestStage(Quests.BIOHAZARD) == 3)
				&& player.getLocation().inBounds(617, 582, 622, 590)) {
			mes("the pigeons fly towards the watch tower");
			delay(3);
			mes("they begin pecking at the bird feed");
			delay(3);
			mes("the mourners are frantically trying to scare the pigeons away");
			delay(3);
			if (player.getQuestStage(Quests.BIOHAZARD) == 2) {
				player.updateQuestStage(Quests.BIOHAZARD, 3);
			}
			if (player.getCache().hasKey("bird_feed")) {
				player.getCache().remove("bird_feed");
			}
			player.getCarriedItems().remove(new Item(ItemId.MESSENGER_PIGEONS.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.PIGEON_CAGE.id()));
		} else {
			player.message("the pigeons don't want to leave");
		}
	}

	private void handleJangerberries(Player player) {
		mes("You eat the Jangerberries");
		delay(3);
		if (player.getCarriedItems().remove(new Item(ItemId.JANGERBERRIES.id())) == -1) return;
		addstat(player, Skill.ATTACK.id(), 2, 0);
		substat(player, Skill.DEFENSE.id(), 1, 0);
		addstat(player, Skill.STRENGTH.id(), 1, 0);
		healstat(player, Skill.HITS.id(), 2, 0);
		healstat(player, Skill.PRAYER.id(), 1, 0);
		player.message("They taste very bitter");
	}

	private void handleShantayDisclaimer(Player player) {
		ActionSender.sendBox(player,
			"@red@*** Shantay Disclaimer***% %"
			+ "@gre@The Desert is a VERY Dangerous place.% %"
			+ "@red@Do not enter if you're scared of dying.% %"
			+ "@gre@Beware of high temperatures, sand storms, and slavers% %"
			+ "@red@No responsibility is taken by Shantay% %"
			+ "@gre@If anything bad happens to you under any circumstances.", true);
	}

	private void handleTechnicalPlans(Player player) {
		mes("The plans look very technical!");
		delay(3);
		mes("But you can see that this item will require ");
		delay(3);
		mes("a bronze bar and at least 10 feathers.");
		delay(3);
	}

	private void handleAnaInABarrel(Player player) {
		mes("Ana looks pretty angry, she starts shouting at you.");
		delay(3);
		mes("@gre@Ana: Get me out of here!");
		delay(3);
		mes("@gre@Ana: Do you hear me!");
		delay(3);
		mes("@gre@Ana: Get me out of here I say!");
		delay(3);
	}

	private void handleRandassJournal(Player player) {
		mes("the journal is old and worn");
		delay(3);
		player.message("it reads...");
		ActionSender.sendBox(player,
			"@red@I came to cleanse these mountain passes of the dark forces%"
			+ "@red@that dwell here, I knew my journey would be treacherous.% %"
			+ "@red@I have deposited Spheres of Light in some of the tunnels%"
			+ "@red@These spheres are a beacon of safety for all who come.%"
			+ "@red@I still feel iban relentlessly tugging at my weak soul.% %"
			+ "@red@The spheres were created by Saradominist mages.%"
			+ "@red@When held they boost faith and courage%"
			+ "@red@bringing out any innate goodness to ones heart%"
			+ "@red@illuminating the dark caverns with the light of saradomin%"
			+ "@red@bringing fear and pain to all who embrace the dark side.% %"
			+ "@red@My men are still repelled by 'ibans well', it seems as if%"
			+ "@red@there pure hearts bar them from entering%"
			+ "@red@ibans realm%"
			+ "@red@my turn has come, I dare not admit it to my loyal men%"
			+ "@red@But I fear for my soul", true);
	}

	private void handleADollOfIban(Player player) {
		mes("you carefully search the doll");
		delay(3);
		if (player.getCache().hasKey("poison_on_doll")) {
			mes("Blood has been smeared onto the doll");
			delay(3);
		}
		if (player.getCache().hasKey("cons_on_doll")) {
			mes("Crushed bones have been smeared onto the doll");
			delay(3);
		}
		if (player.getCache().hasKey("ash_on_doll")) {
			mes("Burnt ash has been smeared onto the doll");
			delay(3);
		}
		if (player.getCache().hasKey("shadow_on_doll")) {
			mes("A dark liquid has been poured over the doll");
			delay(3);
		}
		player.message("the doll is made from old wood and cloth");
		delay(4);
	}

	private void handleStaffOfIban(Player player) {
		mes("the staff is broken");
		delay(3);
		mes("you must have a dark mage repair it");
		delay(3);
		player.message("before it can be used");
		delay(4);
	}

	private void handleNightshade(Player player) {
		if (player.getCarriedItems().remove(new Item(ItemId.NIGHTSHADE.id())) == -1) return;
		player.message("You eat the nightshade...");
		say(player, null, "Ahhhh! what have I done !");
		player.damage((int) ((getCurrentLevel(player, Skill.HITS.id()) * 0.166666666D) + 14));
		player.message("The nightshade was highly poisonous");
	}

	private void handleShamanRobe(Player player) {
		if (player.getQuestStage(Quests.WATCHTOWER) == 8 || player.getQuestStage(Quests.WATCHTOWER) == 9) {
			player.message("You search the robe");
			if (player.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL3.id(), Optional.empty())) {
				mes("You find nothing");
				delay(3);
			} else if (player.getBank().hasItemId(ItemId.POWERING_CRYSTAL3.id())) {
				say(player, null, "I already have this in my bank");
			} else {
				mes("You find a crystal wrapped in the folds of the material");
				delay(3);
				give(player, ItemId.POWERING_CRYSTAL3.id(), 1);
			}
		} else {
			mes("You search the robe");
			delay(3);
			mes("You find nothing");
			delay(3);
		}
	}

	private void handleBookOfExperimentalChemistry(Player player) {
		ActionSender.sendBox(player,
		"Volatile chemicals - Notes on experimental chemistry. % %"
			+ "In order to ease the mining Process, my colleagues and I "
			+ "decided we needed something stronger than picks to delve "
			+ "under the digsite. As I already had an intermediate knowledge of "
			+ "herblaw, I experimented on certain chemicals, and invented a "
			+ "compound of tremendous power, which, if subjected to a spark "
			+ "would literally explode. We used vials of this compound with great results, "
			+ "as it enabled us to reach further than ever before. "
			+ "Here is what I have left of the compound's recipe: % %"
			+ "1 measure of ammonium nitrate powder,% "
			+ "1 measure of nitroglycerin,% "
			+ "1 measure of ground charcoal.% "
			+ "1 measure of ?% "
			+ "Unfortunately the last ingredient was not written down, but we "
			+ "understand that a certain root grows around these parts that was used to very good effect...", true);
	}

	private void handleLevelOneCertificate(Player player) {
		say(player, null, "It says:",
			"The holder of this certificate has passed the level 1 exam in earth sciences");
	}

	private void handleLevelTwoCertificate(Player player) {
		say(player, null, "It says:",
			"The holder of this certificate has passed the level 2 exam in earth sciences");
	}

	private void handleLevelThreeCertificate(Player player) {
		say(player, null, "It says:",
			"The holder of this certificate has passed the level 3 exam in earth sciences");
	}

	private void handleDigsiteScroll(Player player) {
		say(player, null, "It says 'I give permission for the bearer to use the mineshafts on site",
			"Signed Terrance Balando, Archaeological expert, City of Varrock");
	}

	private void handleStoneTablet(Player player) {
		say(player, null, "It says:",
			"Tremble mortal, before the altar of our dread lord zaros");
	}

	private void handleNulodionsNotes(Player player) {
		player.message("the note reads....");
		ActionSender.sendBox(player, "Ammo for the dwarf multi cannon must be made from steel bars% %"
			+ "The bars must be heated in a furnace and used with the cannon ball mould.% %"
			+ "Due to the cannon ball's extreame weight only so many can be carried before one must rest", true);
	}
	private void handleDwarfSmithyNote(Player player) {
		player.message("the note reads....");
		ActionSender.sendBox(player, "How to obtain the Dragon Scale Mail% %"
			+ "Required Items:% %"
			+ "500 Dragon Metal Chains% "
			+ "150 Chipped Dragon Scales% %"
			+ "Dragon metal chains can be smithed(req 90) from dragon metal bars (one bar -> 50 chains)% "
			+ "Speak to the dwarven smithy for details to obtain dragon bars% %"
			+ "Chipped dragon scales are crafted(req 90, chisel) from King Black Dragon scales% %"
			+ "One all items are prepared, seek out Wayne in Falador", true);
	}
	private void handleOldJournal(Player player) {
		mes("the journal is old and covered in dust");
		delay(3);
		mes("inside are several chapters...");
		delay(3);
		int chapter = multi(player, "intro", "iban", "the ressurection", "the four elements");
		int offset;
		if (chapter == 0) {
			readJournalChapter(player, 0);
		} else if (chapter == 1) {
			readJournalChapter(player, 1);
		} else if (chapter == 2) {
			readJournalChapter(player, 2);
		} else if (chapter == 3) {
			mes("you turn to the page titled 'the four elements'");
			delay(3);
			mes("there are four more chapters");
			delay(3);
			offset = multi(player, "flesh", "blood", "shadow", "conscience");
			if (offset >= 0 && offset <= 3) {
				readJournalChapter(player, offset + 3);
			}
		}
	}

	private void readJournalChapter(Player player, int chapter) {
		if (chapter == 0) {
			player.message("you turn to the page titled 'intro'");
			ActionSender.sendBox(player, "% %@red@Gather round, all ye followers of the dark arts.% %" +
				"@red@Read carefully the words that I hereby inscribe,% %" +
				"@red@as I detail the heady the brew that is responsible for my @red@% %" +
				"@red@greatest creation yet. @red@I am Kardia, the most wretched % %" +
				"@red@witch in all the land,scorned by beauty and the world.% %" +
				"@red@See what I have created: @red@the most powerful force% %" +
				"@red@of darkness ever to be seen in human form!" , true);
		} else if (chapter == 1) {
			player.message("you turn to the page titled 'iban'");
			ActionSender.sendBox(player, "@red@Iban was a Black Knight who had learned to fight under the%" +
				"@red@great Darkquerius himself. Together they had taken on the @red@might of the White Knights, and the blood of a hundred @red@soldiers had been wiped from Iban's sword.% %" +
				"@red@In many respects Iban was not so different from the White @red@Knights that he so mercilessly slaughtered: noble and @red@educated@red@ with a taste for the finer things in life. But there @red@was something that made him different: ambition. No, not @red@the simple desire to succeed or lead one's fellow @red@man. @red@This was an ambition that hungered for something beyond @red@the @red@mortal @red@realm", true);
			int cont = multi(player, "continue reading", "close book");
			if (cont == 0) {
				ActionSender.sendBox(player, "%@red@..that was almost godlike in its insatiability.% %" +
					"@red@But therein lay the essence of his darkness. @red@At its most @red@base level, Iban's fundamental impulse@red@ was a desire to @red@control the hearts and minds of his fellow man.@red@ To take @red@them beyond the @red@pale of mere allegiance,@red@ and corrupt @red@them into a pure force for evil.% %" +
					"@red@ This was the fantasy that chased him in his dreams. A @red@whole legion of soul-less beings, their minds demented @red@from the sheer power that he had channelled through to @red@them.%" +
					"@red@But dreams was all they ever were.@red@ As a mere mortal- @red@heroic though he was- this @red@was an ambition that Iban was @red@unable to achieve. Meeting his demise in the White @red@Knights' now @red@famous Dawn Ascent, Iban died with the @red@bitter taste of @red@failure in his mouth. Little did he know that @red@his death was only the beginning.", true);
			}
		} else if (chapter == 2) {
			player.message("you turn to the page titled 'the ressurection'");
			ActionSender.sendBox(player, "% %@red@I knew of Iban's life, though of course we had never met.% %" +
				"@red@And @red@using the power of my dark arts, I vowed @red@to % %" +
				"@red@resurrect this @red@once great warrior. I would raise @red@him again,% %" +
				"@red@to fulfill @red@the @red@promise of his human life: to be a% %" +
				"@red@Master of the Undead.", true);
		} else if (chapter == 3) {
			player.message("you turn to the page titled 'flesh'");
			ActionSender.sendBox(player, "@red@Ibans Flesh% %" +
				"@red@Taking a small doll to represent Iban, I smeared my effigy % %" +
				"@red@with the four crucial elements that constitute a life.% %" +
				"@red@Rooting around the desolate battlefield, I had been able to% %" +
				"@red@steal a piece of Iban's cold flesh.% %" +
				"@red@Now clasping some in my own hand, I smeared it over% %" +
				"@red@my miniature idol, all the while chanting Iban's name.", true);
		} else if (chapter == 4) {
			player.message("you turn to the page titled 'blood'");
			ActionSender.sendBox(player, "@red@Ibans Blood% %" +
				"@red@I also needed some blood. By now, Iban's body was just a% %" +
				"@red@hardened vessel-his life blood had literally drained from % %" +
				"@red@him. But these caverns are home to the giant spider,% %" +
				"@red@a venomous creature that is known to feed on human % %" +
				"@red@blood.  Killing one of these spiders, I wiped my carved doll % %" +
				"@red@in its blood.", true);
		} else if (chapter == 5) {
			player.message("you turn to the page titled 'shadow'");
			ActionSender.sendBox(player, "@red@Ibans Shadow% %" +
				"@red@Then came the hard part: recreating the parts of a man @red@that cannot be seen or touched: those intangible things @red@that are life itself. Using all the mystical force that I could @red@muster, I performed the ancient ritual of Incantia, a spell @red@so powerful that it nearly stole the life from my frail and @red@withered body. Opening my eyes again, I saw the three @red@demons that had been summoned. Standing in a triangle, @red@their energy @red@was focused on the doll. These demons @red@would be the keepers of @red@Iban's shadow. Black as night, @red@their shared spirit @red@would follow his undead body like an @red@angel of death.", true);
		} else if (chapter == 6) {
			player.message("you turn to the page titled 'conscience'");
			ActionSender.sendBox(player, "@red@Ibans conscience% %" +
				"@red@Finally, I had to construct that most unique thing, the one @red@element which seperates man from every other beast- his @red@conscience. A zombie does not need a mind: his is a @red@mindless destruction, borne of simple bloodlust. But for all @red@of Iban's life, he himself choose to take the evil path- @red@driven by such a monstrous ambition. This is what gave @red@him such potential- potential that I would now harness to @red@the fullest.", true);
		}

		if (chapter >= 3 && chapter <= 6) {
			mes("there are four more chapters");
			delay(3);
			int chapterOpt = multi(player, "flesh", "blood", "shadow", "conscience");
			if (chapterOpt >= 0)
				readJournalChapter(player, chapterOpt + 3);
		}
	}
}
