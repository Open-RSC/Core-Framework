package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.MenuOptionListener;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

public class InvAction extends Functions implements InvActionListener, InvActionExecutiveListener {

	@Override
	public boolean blockInvAction(Item item, Player player) {
		return inArray(item.getID(),
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
			ItemId.OYSTER.id(), ItemId.SCRUMPLED_PIECE_OF_PAPER.id());
	}

	@Override
	public void onInvAction(Item item, Player player) {
		int id = item.getID();
		if (id == ItemId.OYSTER.id()) {
			handleOyster(player, id);
		}

		else if (id == ItemId.SCRUMPLED_PIECE_OF_PAPER.id())
			handleScrumpledPieceOfPaper(player);

		else if (id == ItemId.ASTROLOGY_BOOK.id())
			handleAstrologyBook(player);

		else if (id == ItemId.BARCRAWL_CARD.id())
			handleBarcrawlCard(player);

		else if (id == ItemId.INSTRUCTION_MANUAL.id())
			handleInstructionManual(player);

		else if (id == ItemId.TREE_GNOME_TRANSLATION.id())
			handleTreeGnomeTranslation(player);

		else if (id == ItemId.GLOUGHS_JOURNAL.id())
			handleGloughsJournal(player);

		else if (id == ItemId.INVOICE.id())
			handleInvoice(player);

		else if (id == ItemId.GLOUGHS_NOTES.id())
			handleGloughsNotes(player);

		else if (id == ItemId.WAR_SHIP.id())
			handleWarShip(player);

		else if (id == ItemId.DIARY.id())
			handleDiary(player);

		else if (id == ItemId.DRY_STICKS.id())
			handleDrySticks(player);

		else if (id == ItemId.SCRUFFY_NOTE.id())
			handleScruffyNote(player);

		else if (id == ItemId.MAGIC_SCROLL.id())
			handleMagicScroll(player);

		else if (id == ItemId.SPELL_SCROLL.id())
			handleSpellScroll(player);

		else if (id == ItemId.TOURIST_GUIDE.id())
			handleTouristGuide(player);

		else if (id == ItemId.MESSENGER_PIGEONS.id())
			handleMessengerPigeons(player);

		else if (id == ItemId.JANGERBERRIES.id())
			handleJangerberries(player);

		else if (id == ItemId.A_FREE_SHANTAY_DISCLAIMER.id())
			handleShantayDisclaimer(player);

		else if (id == ItemId.TECHNICAL_PLANS.id())
			handleTechnicalPlans(player);

		else if (id == ItemId.ANA_IN_A_BARREL.id())
			handleAnaInABarrel(player);

		else if (id == ItemId.RANDASS_JOURNAL.id())
			handleRandassJournal(player);

		else if (id == ItemId.A_DOLL_OF_IBAN.id())
			handleADollOfIban(player);

		else if (id == ItemId.STAFF_OF_IBAN_BROKEN.id())
			handleStaffOfIban(player);

		else if (id == ItemId.NIGHTSHADE.id())
			handleNightshade(player);

		else if (id == ItemId.SHAMAN_ROBE.id())
			handleShamanRobe(player);

		else if (id == ItemId.BOOK_OF_EXPERIMENTAL_CHEMISTRY.id())
			handleBookOfExperimentalChemistry(player);

		else if (id == ItemId.LEVEL_1_CERTIFICATE.id())
			handleLevelOneCertificate(player);

		else if (id == ItemId.LEVEL_2_CERTIFICATE.id())
			handleLevelTwoCertificate(player);

		else if (id == ItemId.LEVEL_3_CERTIFICATE.id())
			handleLevelThreeCertificate(player);

		else if (id == ItemId.DIGSITE_SCROLL.id())
			handleDigsiteScroll(player);

		else if (id == ItemId.STONE_TABLET.id())
			handleStoneTablet(player);
	}

	private void handleOyster(Player player, int oyster) {
		player.message("you open the oyster shell");
		if (DataConversions.random(0, 10) == 1) {
			player.getInventory().replace(oyster, ItemId.OYSTER_PEARLS.id());
		} else {
			player.getInventory().replace(oyster, ItemId.EMPTY_OYSTER.id());
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
		message(player, "The official Alfred Grimhand barcrawl");
		player.message(!player.getCache().hasKey("barone") ?
			"The jolly boar inn - not completed" : "The jolly boar inn - completed");
		sleep(800);
		player.message(!player.getCache().hasKey("bartwo") ?
			"The blue moon inn - not completed" : "The blue moon inn - completed");
		sleep(800);
		player.message(!player.getCache().hasKey("barthree") ?
			"The rising sun - not completed" : "The rising sun - completed");
		sleep(800);
		player.message(!player.getCache().hasKey("barfour") ?
			"The dead man's chest - not completed" : "The dead man's chest - completed");
		sleep(800);
		player.message(!player.getCache().hasKey("barfive") ?
			"The forester's arms - not completed" : "The forester's arms - completed");
		sleep(800);
		player.message(!player.getCache().hasKey("barsix") ?
			"The rusty anchor - not completed" : "The rusty anchor - completed");
	}

	private void handleInstructionManual(Player player) {
		String[] options;
		options = new String[]{"Constructing the cannon", "Making ammo", "firing the cannon", "warrenty"};
		message(player, "the manual has four pages");
		player.setMenuHandler(new MenuOptionListener(options) {
			public void handleReply(int option, String reply) {
				if (owner.isBusy()) {
					return;
				}
				if (option == 0) {
					ActionSender.sendBox(owner, "Constructing the cannon% %"
						+ "To construct the cannon, firstly set down Dwarf cannon base on the ground.% %"
						+ "Next add the Dwarf cannon stand to the Dwarf cannon base.% %"
						+ "Then add the Dwarf cannon barrels (this can be tiring work).% %"
						+ "Last of all add the Dwarf cannon furnace which powers the cannon.% %"
						+ "You should now have a fully set up dwarf multi cannon ready to go splat some nasty creatures.% % % %"
						+ "@red@WARNING: You should be well rested before attempting to @red@lift the heavy cannon", true);

				}

				else if (option == 1) {
					ActionSender.sendBox(owner, "Making ammo% %"
						+ "The ammo for the cannon is made from steel bars.% %"
						+ "Firstly you must heat up a steel bar in a furnace% %"
						+ "Then pour the molten steel into a cannon ammo mould% %"
						+ "You should now have a ready to fire multi cannon ball% %", true);
				}

				else if (option == 2) {
					ActionSender.sendBox(owner, "Firing the cannon% %"
						+ "The cannon will only fire when monsters are available to target.% %"
						+ "If you are carrying enough ammo the multi cannon will fire up to 20 rounds before stopping.% %"
						+ "The cannon will automatically target non friendly creatures.% %"
						+ "@red@Warning - firing the cannon is exhausting work and can @red@leave adventurers too fatigued to carry the cannon, so @red@rest well before using", true);
				}

				else if (option == 3) {
					ActionSender.sendBox(owner, "@red@Dwarf cannon warrenty% %"
						+ "If your cannon is stolen or lost, after or during being set up, the dwarf engineer will happily replace the parts% %"
						+ "However cannon parts that were given away or dropped will not be replaced for free% %"
						+ "It is only possible to operate one cannon at a time% % % % % %"
						+ "by order of the dwarwven black guard", true);
				}
			}
		});
		ActionSender.sendMenu(player, options);
	}

	private void handleTreeGnomeTranslation(Player player) {
		message(player, "the book contains the alphabet...",
			"translated into the old gnome tounge");
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
		int menu = showMenu(player, "the migration failed", "they must be stopped", "gaining support");
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
		message(player, "you open the invoice");
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
		message(player, "the notes contain sketched maps and diagrams", "the text reads");
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
		message(player, "you pretend to sail the ship across the floor",
			"you soon become very bored",
			"and realise you look quite silly");
	}

	private void handleDiary(Player player) {
		message(player, "Pentember the 3rd",
			"The experiment is going well - moved it to the wooden shed in the garden",
			"It does too much damage in the house",
			"Pentember the 6th",
			"Don't want people getting in back garden to see the experiment",
			"A guy called Professer Odenstein is fitting me a new security system",
			"Pentember the 8th",
			"The security system is done - by zamorak is it contrived!",
			"Now to open my own back door",
			"I lure a rat out of a hole in the back porch",
			"I fit a magic curved piece of metal to its back",
			"The rat goes back in the hole, and the door unlocks",
			"The prof tells me that this is cutting edge technology!");
	}

	private void handleDrySticks(Player player) {
		player.message("you rub together the dry sticks");
		if (getCurrentLevel(player, SKILLS.FIREMAKING.id()) < 30) {
			player.message("you need a firemaking level of 30 or above");
			player.message("the sticks smoke momentarily then die out");
			return;
		}
		message(player, "The sticks catch alight");
		if (removeItem(player, ItemId.UNLIT_TORCH.id(), 1)) {
			player.message("you place the smouldering twigs to your torch");
			player.message("your torch lights");
			player.getInventory().add(new Item(ItemId.LIT_TORCH.id()));
			player.incExp(SKILLS.FIREMAKING.id(), 450, true);
			if (player.getQuestStage(Quests.SEA_SLUG) == 5 && !player.getCache().hasKey("lit_torch")) {
				player.getCache().store("lit_torch", true);
			}
		} else {
			player.message("the sticks smoke momentarily then die out");
		}
	}

	private void handleScruffyNote(Player player) {
		message(player, "The handwriting on this note is very scruffy",
			"as far as you can make out it says",
			"Got a bncket of nnlk",
			"Tlen qrind sorne lhoculate",
			"vnith a pestal and rnortar",
			"ald the grourd dlocolate to tho milt",
			"fnales add 5cme snape gras5",
			"you guess it really says something slightly different");
	}

	private void handleMagicScroll(Player player) {
		if (player.getCache().hasKey("ardougne_scroll") && player.getQuestStage(Constants.Quests.PLAGUE_CITY) == -1) {
			message(player, "The scroll crumbles to dust");
		} else {
			message(player, "You memorise what is written on the scroll",
				"You can now cast the Ardougne teleport spell",
				"Provided you have the required runes and magic level",
				"The scroll crumbles to dust");
		}
		removeItem(player, ItemId.MAGIC_SCROLL.id(), 1);
		if (!player.getCache().hasKey("ardougne_scroll")) {
			player.getCache().store("ardougne_scroll", true);
		}
	}

	private void handleSpellScroll(Player player) {
		if (player.getCache().hasKey("watchtower_scroll") && player.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
			message(player, "The scroll crumbles to dust");
		} else {
			message(player, "You memorise what is written on the scroll",
				"You can now cast the Watchtower teleport spell",
				"Provided you have the required runes and magic level",
				"The scroll crumbles to dust");
		}
		removeItem(player, ItemId.SPELL_SCROLL.id(), 1);
		if (!player.getCache().hasKey("watchtower_scroll")) {
			player.getCache().store("watchtower_scroll", true);
		}
	}

	private void handleTouristGuide(Player player) {
		message(player, "You read the guide");
		playerTalk(player, null, "This book is your guide to the vibrant city of Ardougne",
			"Ardougne is an exciting modern city",
			"Located on the sunny south coast of Kandarin");
		message(player, "Pick a chapter to read");
		int chapter = showMenu(player,
			"Ardougne city of shopping",
			"Ardougne city of history",
			"Ardougne city of fun",
			"The area surrounding Ardougne",
			"I don't want to read this rubbish");
		if (chapter == 0) {
			message(player, "Ardougne city of shopping",
				"Come sample the delights of the Ardougne market",
				"The biggest in the known world",
				"From spices to silk",
				"There is something here for everyone",
				"Other popular shops in the area include Zeneshas the armourer",
				"And the adventurers supply store");
		} else if (chapter == 1) {
			message(player, "Ardougne, city of history",
				"Ardougne is an important historical city",
				"One historic building is the magnificent Handelmort mansion",
				"Currently owned by Lord Franis Bradley Handelmort",
				"Ardougne castle in the east side of the city",
				"Is now open to the public",
				"and members of the holy order of ardougne paladins",
				"Still wander the streets");
		} else if (chapter == 2) {
			message(player, "Ardougne city of fun",
				"If you're looking for entertainment in Ardougne",
				"Why not pay a visit to Ardougne city zoo",
				"Or relax for a drink in the flying horse inn",
				"Or slaughter rats in Ardougne sewers");
		} else if (chapter == 3) {
			message(player, "The area surrounding Ardougne",
				"If you want to go further afield",
				"Why not have a look at the pillars of Zanash",
				"The mysterious marble pillars west of the city",
				"Or the town of Brimhaven, on exotic Karamja",
				"Is only a short boat ride away",
				"Ships leaving regularily from Ardougne harbour");
		} else if (chapter == 4) {
			playerTalk(player, null, "I don't want to read this rubbish");
		}
	}

	private void handleMessengerPigeons(Player player) {
		/* Biohazard quest */
		player.message("you open the cage");
		if ((player.getCache().hasKey("bird_feed")
			|| player.getQuestStage(Constants.Quests.BIOHAZARD) == 3)
				&& player.getLocation().inBounds(617, 582, 622, 590)) {
			message(player, "the pigeons fly towards the watch tower",
				"they begin pecking at the bird feed",
				"the mourners are frantically trying to scare the pigeons away");
			if (player.getQuestStage(Constants.Quests.BIOHAZARD) == 2) {
				player.updateQuestStage(Constants.Quests.BIOHAZARD, 3);
			}
			if (player.getCache().hasKey("bird_feed")) {
				player.getCache().remove("bird_feed");
			}
			player.getInventory().replace(ItemId.MESSENGER_PIGEONS.id(), ItemId.PIGEON_CAGE.id());
		} else {
			player.message("the pigeons don't want to leave");
		}
	}

	private void handleJangerberries(Player player) {
		message(player, "You eat the Jangerberries");
		removeItem(player, ItemId.JANGERBERRIES.id(), 1);
		int Attack = player.getSkills().getMaxStat(SKILLS.ATTACK.id()) + 2;
		int Strength = player.getSkills().getMaxStat(SKILLS.STRENGTH.id()) + 1;
		if (player.getSkills().getLevel(SKILLS.HITS.id()) < player.getSkills().getMaxStat(SKILLS.HITS.id())) {
			player.getSkills().setLevel(SKILLS.HITS.id(), player.getSkills().getLevel(SKILLS.HITS.id()) + 2);
		}
		if (player.getSkills().getLevel(SKILLS.PRAYER.id()) < player.getSkills().getMaxStat(SKILLS.PRAYER.id())) {
			player.getSkills().setLevel(SKILLS.PRAYER.id(), player.getSkills().getLevel(SKILLS.PRAYER.id()) + 1);
		}
		if (player.getSkills().getLevel(SKILLS.DEFENSE.id()) < 1) {
			player.getSkills().setLevel(SKILLS.DEFENSE.id(), 0);
		} else {
			player.getSkills().setLevel(SKILLS.DEFENSE.id(), player.getSkills().getLevel(SKILLS.DEFENSE.id()) - 1);
		}
		if (player.getSkills().getLevel(SKILLS.ATTACK.id()) < Attack) {
			player.getSkills().setLevel(SKILLS.ATTACK.id(), player.getSkills().getLevel(SKILLS.ATTACK.id()) + 1);
		}
		if (player.getSkills().getLevel(SKILLS.STRENGTH.id()) < Strength) {
			player.getSkills().setLevel(SKILLS.STRENGTH.id(), player.getSkills().getLevel(SKILLS.STRENGTH.id()) + 1);
		}
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
		message(player, "The plans look very technical!",
			"But you can see that this item will require ",
			"a bronze bar and at least 10 feathers.");
	}

	private void handleAnaInABarrel(Player player) {
		message(player, "Ana looks pretty angry, she starts shouting at you.",
			"@gre@Ana: Get me out of here!",
			"@gre@Ana: Do you hear me!",
			"@gre@Ana: Get me out of here I say!");
	}

	private void handleRandassJournal(Player player) {
		message(player, "the journal is old and worn");
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
		message(player, "you carefully search the doll");
		if (player.getCache().hasKey("poison_on_doll")) {
			message(player, "Blood has been smeared onto the doll");
		}
		if (player.getCache().hasKey("cons_on_doll")) {
			message(player, "Crushed bones have been smeared onto the doll");
		}
		if (player.getCache().hasKey("ash_on_doll")) {
			message(player, "Burnt ash has been smeared onto the doll");
		}
		if (player.getCache().hasKey("shadow_on_doll")) {
			message(player, "A dark liquid has been poured over the doll");
		}
		player.message("the doll is made from old wood and cloth");
	}

	private void handleStaffOfIban(Player player) {
		message(player, "the staff is broken",
			"you must have a dark mage repair it");
		player.message("before it can be used");
	}

	private void handleNightshade(Player player) {
		player.message("You eat the nightshade...");
		removeItem(player, ItemId.NIGHTSHADE.id(), 1);
		playerTalk(player, null, "Ahhhh! what have I done !");
		player.damage((int) ((getCurrentLevel(player, SKILLS.HITS.id()) * 0.2D) + 10));
		player.message("The nightshade was highly poisonous");
	}

	private void handleShamanRobe(Player player) {
		if (player.getQuestStage(Quests.WATCHTOWER) == 8 || player.getQuestStage(Quests.WATCHTOWER) == 9) {
			player.message("You search the robe");
			if (hasItem(player, ItemId.POWERING_CRYSTAL3.id())) {
				message(player, "You find nothing");
			} else if (player.getBank().hasItemId(ItemId.POWERING_CRYSTAL3.id())) {
				playerTalk(player, null, "I already have this in my bank");
			} else {
				message(player, "You find a crystal wrapped in the folds of the material");
				addItem(player, ItemId.POWERING_CRYSTAL3.id(), 1);
			}
		} else {
			message(player, "You search the robe",
				"You find nothing");
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
		playerTalk(player, null, "It says:",
			"The holder of this certificate has passed the level 1 exam in earth sciences");
	}

	private void handleLevelTwoCertificate(Player player) {
		playerTalk(player, null, "It says:",
			"The holder of this certificate has passed the level 2 exam in earth sciences");
	}

	private void handleLevelThreeCertificate(Player player) {
		playerTalk(player, null, "It says:",
			"The holder of this certificate has passed the level 3 exam in earth sciences");
	}

	private void handleDigsiteScroll(Player player) {
		playerTalk(player, null, "It says 'I give permission for the bearer to use the mineshafts on site",
			"Signed Terrance Balando, Archaeological expert, City of Varrock");
	}

	private void handleStoneTablet(Player player) {
		playerTalk(player, null, "It says:",
			"Tremble mortal, before the altar of our dread lord zaros");
	}
}
