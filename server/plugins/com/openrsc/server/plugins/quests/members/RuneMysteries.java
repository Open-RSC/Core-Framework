package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class RuneMysteries implements QuestInterface,
	WallObjectActionListener, WallObjectActionExecutiveListener, InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener {

	@Override
	public int getQuestId() {
		return Quests.RUNE_MYSTERIES;
	}

	@Override
	public String getQuestName() {
		return "Rune mysteries (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	public static void dukeDialog(int questState, Player p, Npc n) {
		switch (questState) {
			case -1:
				npcTalk(p, n, "All is well for me",
					"Thanks again for your help with the talisman");
				break;
			case 0:
				npcTalk(p, n, "Well, it's not really a quest, but",
					"I recently discovered this strange talisman.",
					"It's not like anything I have seen before",
					"Would you take it to the head wizard",
					"in the basement of the Wizards' Tower for me?",
					"It should not take you very long at all",
					"and I would be awfully grateful");

				String[] menu = new String[]{ // Dragon Slayer
					"Sure, I have some spare time",
					"No thanks"
				};
				int choice = showMenu(p, n, false, menu);
				if (choice == 0) {
					playerTalk(p, n, "Sure, I have some spare time");
					npcTalk(p, n, "Thank you very much, stranger",
						"I am sure the head wizard will reward you",
						"for such an interesting find");
					if (p.getInventory().getFreeSlots() > 0) {
						message(p, "The Duke hands you a talisman.");
						addItem(p, ItemId.AIR_TALISMAN.id(), 1);
						p.setQuestStage(Quests.RUNE_MYSTERIES, 1);
					} else {
						npcTalk(p, n, "Make some room in your inventory",
							"and see me again");
					}
				}
				break;
			case 1:
				playerTalk(p, n, "What was I supposed to do again?");
				npcTalk(p, n, "Take the air talisman to the head wizard",
					"in the basement of Wizards' Tower");
				break;
			default:
				break;
		}
	}

	public static void sedridorDialog(Player p, Npc n) {
		switch (p.getQuestStage(Quests.RUNE_MYSTERIES)) {
			case 1:
				npcTalk(p, n, "That's me, but why would you be doing that?");
				playerTalk(p, n, "The Duke of Lumbridge sent me to find...er, you..",
					"I have a weird talisman that the Duke found.",
					"He said the head wizard would be interested in it.");
				npcTalk(p, n, "Did he now? Well, that IS interesting.",
					"Hand it over, then, adventurer - let me see what",
					"all the hubbub is about. Just some crudge amulet, I'll wager.");
				playerTalk(p, n, "Okay, here you go.");

				if (!p.getInventory().hasItemId(ItemId.AIR_TALISMAN.id())) {
					playerTalk(p, n, "Oh, I seem to have lost the talisman.");
					npcTalk(p, n, "Pity. If you happen to find it, bring it to me.");
					return;
				}
				message(p, "You give the talisman to the wizard.");
				removeItem(p,ItemId.AIR_TALISMAN.id(),1);
				npcTalk(p, n, "Wow! This is incredible! Th-this talisman you brought me...",
					"it is the last piece of the puzzle. Finally! the legacy of our",
					"ancestors will return to us once more! I need time to",
					"study this, " + p.getUsername() + ", can you please perform",
					"one task while I study this talisman? In the mighty city",
					"of Varrock, located north-east of here, there is",
					"a certain shop that sells magical runes. I have, in this",
					"package, all of the research I have done relating to runes",
					"but I require somebody to take them to a shopkeeper who",
					"can offer me his insights. Do this thing for me,",
					"bring back what he gives you, and if my suspicions are",
					"correct, I will let you in on one of the greatest",
					"secrets this world has ever known. It is a secret",
					"so powerful that it destroyed the original Wizards' Tower",
					"many centuries ago! Do this thing for me, " + p.getUsername(),
					"and you will be rewarded.");

				int choice = showMenu(p,"Yes, certainly","No, I'm busy.");
				if (choice == 0)
				{
					npcTalk(p,n,"Take this package to Varrock, the large city",
						"north of Lumbrdige. Aubury's rune shop is in the",
						"south-east quarter. He will give you a special item -",
						"bring it back to me and I will show you the mystery of runes.");

					if (p.getInventory().getFreeSlots() > 0)
					{
						message(p, "The head wizard gives you a research package.");
						npcTalk(p,n,"Best of luck with your quest, " + p.getUsername());
						addItem(p, ItemId.RESEARCH_PACKAGE.id(),1);
						p.setQuestStage(Quests.RUNE_MYSTERIES,2);
					} else {
						message(p,"The head wizard tried to give you a research package, but your inventory was full.");
					}

				} else if (choice == 1)
				{
					message(p,"The head wizard returns your talisman.");
					addItem(p,ItemId.AIR_TALISMAN.id(),1);
				}
				break;
			case 2:
				if (!p.getInventory().hasItemId(ItemId.RESEARCH_PACKAGE.id()))
				{
					playerTalk(p,n,"I lost the research package");
					npcTalk(p,n,"My my... I think I can pack up another one.");

					if (p.getInventory().getFreeSlots() > 0 )
					{
						message(p,"The head wizard gives you a research package.");
						npcTalk(p,n,"Be more careful this time");
						addItem(p, ItemId.RESEARCH_PACKAGE.id(),1);
					} else {
						message(p,"The head wizard tried to give you a research package, but your inventory was full.");
					}
				} else {
					playerTalk(p,n,"What was I supposed to do with this package again?");
					npcTalk(p,n,"Deliver it to Aubury's rune shop in Varrock please.");
				}
				break;
			case 3:
				npcTalk(p,n,"Ah, " + p.getUsername() + ". How goes your quest?",
					"Have you delivered the research package to my friend yet?");
				playerTalk(p,n,"Yes, I have. He gave me some research notes to pass on to you.");
				npcTalk(p,n,"May I have them?");

				if (!p.getInventory().hasItemId(ItemId.RESEARCH_NOTES.id()))
				{
					playerTalk(p,n,"Er... I seem to have lost them.");
					npcTalk(p,n,"Sigh... go find them and return to me.");
					return;
				}

				playerTalk(p,n,"Sure. I have them here.");
				npcTalk(p,n,"You have been nothing but helpful, adventurer.",
					"In return, I can let you in on the secret of our research.",
					"Many centuries ago, the wizards of the Wizards' Tower",
					"learn the secret of creating runes, which allowed them",
					"to cast magic very easily. But, when the tower was burnt",
					"down, the secret of creating runes was lost with it...",
					"Or so I thought. Some months ago, while searching these",
					"ruins for information, I came upon a scroll that",
					"made reference to a magical rock, deep in the ice fields",
					"of the north. this rock was called the 'rune essence'",
					"by those magicians who studied its powers.",
					"Apparently, by simply breaking a chunk from it,",
					"a rune could be fashioned and taken to certain",
					"magical altars that were scattered across the land.",
				"Now, this is an interesting little piece of history",
				"but not much use to us since we do not have",
				"access to this rune essence or these altars.",
				"This is where you and Aubury come in.",
				"A little while ago, Aubury discovered a parchment",
				"detailing a teleportation spell that he had never",
				"come across before. When cast, it took him",
				"to a strange rock, yet it felt strangely",
				"familiar. As I'm sure you have guessed, he had",
				"discovered a spell to the mythical rune essence.",
				"As soon as he told me of this, I saw the importance",
				"of the find. For if we could find the altars",
				"spoken of in the ancient texts, we would once",
				"more be able to create runes as",
				"our ancestors had done.");
				playerTalk(p,n,"I'm still not sure how I fit into",
					"this little story of yours.");
				npcTalk(p,n,"You haven't guessed? The talisman you brought me",
					"is a key to the elemental altar of air! When you hold",
					"it, it directs you to the entrance of the long",
					"forgotten Air Altar. By bringing pieces of the",
					"rune essence to the Air Altar, you will be able",
					"to fashion your own air runes. That's not all!",
					"By finding other talismans similiar to this one,",
					"you will eventually be able to craft every rune",
					"that is available in this world, just as our",
					"ancestors did. I cannot stress enough what a",
					"find this is! Now, due to the risks involved",
					"in letting this mighty power fall into the",
					"wrong hands, I will try to keep the teleport",
					"spell to the rune essence a closely gaurded",
					"secret. This means that, if any evil power",
					"should discover the talismans required",
					"to enter the elemental temples, we should be",
					"able to prevent their access to the runes",
					"essence. I know not where the altars are",
				"located, nor do I know where the talismans",
				"have been scattered, but I now return your",
				"air talisman. Find the Air Altar and you will be able",
				"to craft your blank runes into air runes at will.",
					"Any time you wish to visit the rune essence,",
				"speak to me or Aubury and we will open a",
				"portal to that mystical place.");
				playerTalk(p,n,"So, only you and Aubury know the teleport",
					"spell to the rune essence?");
				npcTalk(p,n,"No, there are others. When you speak",
					"to them, they will know you and grant you access to",
					"that place when asked. Use the air talisman to",
					"locate the Air Altar and use any further talismans",
					"you find to locate the other altars.",
					"Now, my research notes, please?");
				message(p, "You hand Sedridor the research notes.");
				removeItem(p, ItemId.RESEARCH_NOTES.id(),1);
				addItem(p, ItemId.AIR_TALISMAN.id(),1);
				p.sendQuestComplete(Quests.RUNE_MYSTERIES);
				break;
		}
	}

	public static void auburyDialog(Player p, Npc n) {
		if (p.getQuestStage(Quests.RUNE_MYSTERIES) == 2)
		{
			if (p.getInventory().hasItemId(ItemId.RESEARCH_PACKAGE.id()))
			{
				playerTalk(p,n, "I've been sent here with a package for you.",
					"It's from Sedridor, the head wizard at the Wizards' Tower.");
				npcTalk(p,n,"Really? Surely he can't have...",
					"Please... let me have it.");
				message(p, "You have Aubury the research package.");
				npcTalk(p,n,"My gratitude, adventurer, for bringing me this research package.",
					"Combined with the information I have already collated",
					"regarding rune essence, I think we have finally",
					"unlocked the power to... No..",
					"I'm getting ahead of myself. Take this summary",
					"of my research back to Sedridor in the basement of the",
					"Wizards' Tower. He will know whether or not",
					"to let you in on our little secret.");
				removeItem(p, ItemId.RESEARCH_PACKAGE.id(),1);
				addItem(p, ItemId.RESEARCH_NOTES.id(), 1);
				p.setQuestStage(Quests.RUNE_MYSTERIES, 3);
				message(p, "Aubury gives you his research notes.");
				npcTalk(p,n,"Now, I'm sure I can spare a couple of runes for",
					"such a worth cause as these notes.",
					"Do you want me to teleport you back?");
				int choice = showMenu(p, "Yes, please.", "No, thank you.");
				if (choice == 0)
				{
					p.teleport(217,685,true);
				}

			} else {
				playerTalk(p,n,"I had a package for you... But I lost it");
				npcTalk(p,n,"See if you can find another one and return to me");
			}
		}
		else if (p.getQuestStage(Quests.RUNE_MYSTERIES) == 3)
		{
			if (!p.getInventory().hasItemId(ItemId.RESEARCH_NOTES.id()))
			{
				playerTalk(p,n,"I lost your research notes...");
				npcTalk(p,n,"I see. Here, I have another copy.");

				if (p.getInventory().getFreeSlots() > 0)
				{
					message(p,"Aubury hands you his research notes.");
					addItem(p,ItemId.RESEARCH_NOTES.id(),1);
				} else
				{
					message(p, "Aubury tried to give you notes, but your inventory is full.");
				}
			} else {
				playerTalk(p,n,"What am I to do with these notes?");
				npcTalk(p,n,"Take them to Sedridor in the Wizards' Tower.");
			}
		}

	}


	@Override
	public void handleReward(Player p) {
		p.message("Well done you have completed the rune mysteries quest");
		p.message("@gre@You haved gained 1 quest point!");
		p.message("You now have access to the Runecrafting skill!");
		incQuestReward(p, Quests.questData.get(Quests.RUNE_MYSTERIES), true);
	}


	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
										 Player player) {
		return (obj.getID() == 63 && obj.getY() == 3332) || (obj.getID() == 64 && (obj.getY() == 3336 || obj.getY() == 3332));
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 63 && obj.getY() == 3332) {
			Npc suit = World.getWorld().getNpc(NpcId.SUIT_OF_ARMOUR.id(), 374, 374, 3330, 3334);
			if (suit != null && !(p.getX() <= 373)) {
				p.message("Suddenly the suit of armour comes to life!");
				suit.setChasing(p);
			} else {
				doDoor(obj, p);
			}
		} else if (obj.getID() == 64 && (obj.getY() == 3336 || obj.getY() == 3332)) {
			doDoor(obj, p);
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
									   Player player) {
		return obj.getID() == 236 &&
			(item.getID() == ItemId.RAW_CHICKEN.id() || item.getID() == ItemId.RAW_RAT_MEAT.id()
				|| item.getID() == ItemId.RAW_BEEF.id() || item.getID() == ItemId.RAW_BEAR_MEAT.id());
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 236 &&
			(item.getID() == ItemId.RAW_CHICKEN.id() || item.getID() == ItemId.RAW_RAT_MEAT.id()
				|| item.getID() == ItemId.RAW_BEEF.id() || item.getID() == ItemId.RAW_BEAR_MEAT.id())) {
			if (item.getID() == ItemId.RAW_CHICKEN.id()) {
				message(p, "You dip the chicken in the cauldron");
				p.getInventory().remove(ItemId.RAW_CHICKEN.id(), 1);

				addItem(p, ItemId.ENCHANTED_CHICKEN_MEAT.id(), 1);
			} else if (item.getID() == ItemId.RAW_BEAR_MEAT.id()) {
				message(p, "You dip the bear meat in the cauldron");
				p.getInventory().remove(ItemId.RAW_BEAR_MEAT.id(), 1);

				addItem(p, ItemId.ENCHANTED_BEAR_MEAT.id(), 1);
			} else if (item.getID() == ItemId.RAW_RAT_MEAT.id()) {
				message(p, "You dip the rat meat in the cauldron");
				p.getInventory().remove(ItemId.RAW_RAT_MEAT.id(), 1);

				addItem(p, ItemId.ENCHANTED_RAT_MEAT.id(), 1);
			} else if (item.getID() == ItemId.RAW_BEEF.id()) {
				message(p, "You dip the beef in the cauldron");
				p.getInventory().remove(ItemId.RAW_BEEF.id(), 1);

				addItem(p, ItemId.ENCHANTED_BEEF.id(), 1);
			}
		}
	}

	class kaqemeex {
		public static final int STONE_CIRCLE = 0;
		public static final int ON_MY_WAY_NOW = 1;
		public static final int SEARCH_OF_QUEST = 2;
	}
}
