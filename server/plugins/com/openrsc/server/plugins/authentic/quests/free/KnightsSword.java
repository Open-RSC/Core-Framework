package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.RuneScript;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class KnightsSword implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger {
	private static final int VYVINS_CUPBOARD_OPEN = 175;
	private static final int VYVINS_CUPBOARD_CLOSED = 174;
	private static final int CUPBOARD_Y = 2454;

	// Thrugo coords: 290 716

	@Override
	public int getQuestId() {
		return Quests.THE_KNIGHTS_SWORD;
	}

	@Override
	public String getQuestName() {
		return "The Knight's sword";
	}

	@Override
	public int getQuestPoints() {
		return Quest.THE_KNIGHTS_SWORD.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		if (player.getConfig().INFLUENCE_INSTEAD_QP) {
			player.message("Well done you have completed the sword quest");
		} else {
			player.message("Well done you have completed the knight's sword quest");
		}
		final QuestReward reward = Quest.THE_KNIGHTS_SWORD.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.THURGO.id() || n.getID() == NpcId.SQUIRE.id()
			|| n.getID() == NpcId.SIR_VYVIN.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.THURGO.id()) {
			dwarfDialogue(player, n);
		} else if (n.getID() == NpcId.SQUIRE.id()) {
			squireDialogue(player, n, -1);
		} else if (n.getID() == NpcId.SIR_VYVIN.id()) {
			vyvinDialogue(player, n);
		}
	}

	private void vyvinDialogue(final Player player, final Npc n) {
		say(player, n, "Hello");
		npcsay(player, n, "Greetings traveller");
		ArrayList<String> options = new ArrayList<String>();
		options.add("Do you have anything to trade?");
		options.add("Why are there so many knights in this city?");
		if (config().WANT_CUSTOM_SPRITES) {
			options.add("I wanted to ask you about your cape");
		}
		int option = multi(player, n, options.toArray(new String[0]));
		if (option == 0) {
			npcsay(player, n, "No I'm sorry");
		} else if (option == 1) {
			npcsay(player, n, "We are the White Knights of Falador",
				"We are the most powerfull order of knights in the land",
				"We are helping the king Vallance rule the kingdom",
				"As he is getting old and tired");
		} else if (config().WANT_CUSTOM_SPRITES && option == 2) {
			npcsay(player, n, "This is the cape of defense",
				"Given to knights who are exceptional at surviving");
			if (player.getSkills().getMaxStat(Skill.DEFENSE.id()) >= 99) {
				npcsay(player, n, "You look like someone who is quite formidable",
					"I can sell you your own defense cape for 99,000 gold coins");

				int choice = multi(player, n, "Yes please", "no thankyou");
				if (choice == 0) {
					if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
						mes("You hand the gold to Sir Vyvin");
						delay(3);
						if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
							mes("He presents you with your own Defense cape");
							delay(3);
							give(player, ItemId.DEFENSE_CAPE.id(), 1);
							npcsay(player, n, "May this cape protect you against the fiercest foes");
						}
					} else {
						npcsay(player, n, "Apologies, friend", "It looks like you aren't carrying enough coins");
					}
				} else if (choice == -1) return;
			}
			// This will play at the end of all dialog trees
			if (player.getQuestStage(Quest.THE_KNIGHTS_SWORD.id()) == -1) {
				npcsay(player, n, "Now if you'll excuse me",
					"I must get back to looking for my father's portrait",
					"I swear I left it in this cupboard...");
			}
		}
	}

	public void dwarfDialogue(final Player player, final Npc n) {
		ArrayList<String> choices = new ArrayList<>();
		String goodbyeNiceDay = "Have a nice day";
		String goodbyeNevermind = "Nevermind";
		switch (player.getQuestStage(this)) {
			case -1:
				say(player, n, "Thanks for your help in getting the sword for me");
				npcsay(player, n, "No worries mate");
				if (canBuyCape(player)) skillcape(player, n, goodbyeNiceDay);
				break;
			case 0:
				if (!canBuyCape(player)) {
					player.message("Thurgo doesn't appear to be interested in talking");
				} else {
					npcsay(player, n, "Eh?");
					skillcape(player, n, goodbyeNevermind);
				}
				break;
			case 1:
				say(player, n, "Hello are you are an Imcando Dwarf?");
				npcsay(player, n, "Yeah what about it?");
				if (canBuyCape(player)) skillcape(player, n, goodbyeNevermind);
				break;
			case 2:
				if (!player.getCarriedItems().hasCatalogID(ItemId.REDBERRY_PIE.id(), Optional.of(false))) {
					say(player, n, "Hello are you are an Imcando Dwarf?");
					npcsay(player, n, "Yeah what about it?");
					if (canBuyCape(player)) skillcape(player, n, goodbyeNevermind);
				} else {
					choices.add("Hello are you an Imcando Dwarf?");
					choices.add("Would you like some redberry pie?");
					if (canBuyCape(player)) choices.add("What's that cape you've got on?");
					int option = multi(player, n, false, //do not send over
						choices.toArray(new String[choices.size()]));
					if (option == 0) {
						say(player, n, "Hello are you an Imcando Dwarf?");
						npcsay(player, n, "Yeah what about it?");
						option = multi(player, n, false, //do not send over
							"Would you like some redberry  Pie?",
							"Can you make me a special sword?");
						if (option == 0) {
							say(player, n, "Would you like some redberry Pie?");
							givePie(player, n);
						} else if (option == 1) {
							say(player, n, "Can you make me a special sword?");
							npcsay(player, n, "no I don't do that anymore",
								"I'm getting old");
						}
					} else if (option == 1) {
						say(player, n, "Would you like some redberry Pie?");
						givePie(player, n);
					} else if (option == 2) {
						say(player, n, "What's that cape you've got on?");
						skillcape(player, n, "yes");
					}
				}
				break;
			case 3:
				say(player, n, "Can you make me a special sword?");
				npcsay(player, n, "Well after you've brought me such a great pie",
					"I guess I should give it a go",
					"What sort of sword is it?");
				say(
					player,
					n,
					"I need you to make a sword for one of Falador's knights",
					"He had one which was passed down through five generations",
					"But his squire has lost it",
					"So we need an identical one to replace it");
				npcsay(player,
					n,
					"A Knight's sword eh?",
					"Well I'd need to know exactly how it looked",
					"Before I could make a new one",
					"All the Faladian knights used to have swords with different designs",
					"could you bring me a picture or something?");
				say(player, n, "I'll see if I can find one",
					"I'll go and ask his squire");
				player.updateQuestStage(this, 4);
				break;
			case 4:
				if (player.getCarriedItems().hasCatalogID(ItemId.PORTRAIT.id(), Optional.of(false))) {
					say(player, n,
						"I have found a picture of the sword I would like you to make");
					player.message("You give the portrait to Thurgo");
					player.getCarriedItems().remove(new Item(ItemId.PORTRAIT.id()));
					mes("Thurgo studies the portrait");
					delay(3);
					player.updateQuestStage(this, 5);
					npcsay(player,
						n,
						"Ok you'll need to get me some stuff for me to make this",
						"I'll need two Iron bars to make the sword to start with",
						"I'll also need an ore called blurite",
						"It's useless for making actual weapons for fighting with",
						"But I'll need some as decoration for the hilt",
						"It is a fairly rare sort of ore",
						"The only place I know where to get it",
						"Is under this cliff here",
						"But it is guarded by a very powerful ice giant",
						"Most the rocks in that clif are pretty useless",
						"Don't contain much of anything",
						"But there's definitly some blurite in there",
						"You'll need a little bit of mining experience",
						"TO be able to find it");
					say(player, n, "Ok I'll go and find them");
				} else {
					npcsay(player, n, "Have you got a picture of the sword for me yet?");
					say(player, n, "Sorry not yet");
					if (canBuyCape(player)) skillcape(player, n, "I'll go get it");
				}
				break;
			case 5:
			case 6:
				if (player.getCarriedItems().hasCatalogID(ItemId.FALADIAN_KNIGHTS_SWORD.id(), Optional.of(false))) {
					say(player, n,
						"Thanks for your help in getting the sword for me");
					npcsay(player, n, "No worries mate");
					if (canBuyCape(player)) skillcape(player, n, goodbyeNiceDay);
					return;
				}
				if (ifheld(player, ItemId.IRON_BAR.id(), 2) && player.getCarriedItems().hasCatalogID(ItemId.BLURITE_ORE.id(), Optional.of(false))) {
					npcsay(player, n, "How are you doing finding sword materials?");
					say(player, n, "I have them all");
					mes("You give some blurite ore and two iron bars to Thurgo");
					delay(3);

					player.getCarriedItems().remove(new Item(ItemId.IRON_BAR.id()));
					player.getCarriedItems().remove(new Item(ItemId.IRON_BAR.id()));
					player.getCarriedItems().remove(new Item(ItemId.BLURITE_ORE.id()));
					mes("Thurgo starts making a sword");
					delay(3);
					mes("Thurgo hammers away");
					delay(3);
					mes("Thurgo hammers some more");
					delay(3);
					mes("Thurgo hands you a sword");
					delay(3);

					give(player, ItemId.FALADIAN_KNIGHTS_SWORD.id(), 1);
					say(player, n, "Thank you very much");
					npcsay(player, n, "Just remember to call in with more pie some time");
					player.updateQuestStage(this, 6);
				} else {
					npcsay(player, n, "How are you doing finding sword materials?");
					say(player, n, "I haven't found everything yet");
					npcsay(player, n, "Well come back when you do",
						"Remember I need blurite ore and two iron bars");
					if (canBuyCape(player)) skillcape(player, n, "Alright, I'll be back");
				}
				break;
		}
	}

	private boolean canBuyCape(Player player) {
		if (config().WANT_CUSTOM_SPRITES
			&& getMaxLevel(player, Skill.SMITHING.id()) >= 99) { return true; }
		return false;
	}

	private void skillcape(Player player, Npc n, String goodbye) {
		if (goodbye.equalsIgnoreCase("yes")
			|| multi(player, n, "What's that cape you've got on?", goodbye) == 0) {

			npcsay(player, n, "This is the Smithing cape",
				"Trusted only with masters of metalworking",
				"Like me",
				"I see that you have also mastered smithing",
				"I will sell you a Smithing cape for 99,000 coins");
			if (multi(player, n, "Sounds fair", "No way") == 0) {
				if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
					mes("Thurgo takes your coins");
					delay(3);
					if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
						mes("And hands you a Smithing cape");
						delay(3);
						give(player, ItemId.SMITHING_CAPE.id(), 1);
						npcsay(player, n, "Don't lose this",
							"This cape will allow you save some coal while smelting");
					}
				} else {
					npcsay(player, n, "Sorry mate, you don't have enough coins");
				}
			}
		}
	}

	private void givePie(Player player, Npc n) {
		mes("Thurgo's eyes light up");
		delay(3);
		npcsay(player, n, "I'd never say no to a redberry pie");
		npcsay(player, n, "It's great stuff");
		if (!player.getCarriedItems().hasCatalogID(ItemId.REDBERRY_PIE.id(), Optional.of(false))) { //should not happen here
			say(player, n, "Well that's too bad, because I don't have any");
			mes("Thurgo does not look impressed");
			delay(3);
		} else {
			mes("You hand over the pie");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.REDBERRY_PIE.id()));
			player.updateQuestStage(this, 3);
			mes("Thurgo eats the pie");
			delay(3);
			mes("Thurgo pats his stomach");
			delay(3);
			npcsay(player, n, "By Guthix that was good pie",
				"Anyone who makes pie like that has gotta be alright");
		}
	}

	public void squireDialogue(final Player player, final Npc n, int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case -1:
					npcsay(player, n, "Hello friend", "thanks for your help before",
						"Vyvin never even realised it was a different sword");
					break;
				case 0:
					npcsay(player, n, "Hello I am the squire to Sir Vyvin");
					int option = multi(player, n, "And how is life as a squire?",
						"Wouldn't you prefer to be a squire for me?");
					if (option == 0) {
						npcsay(player, n, "Well Sir Vyvin is a good guy to work for",
							"However I'm in a spot of trouble today",
							"I've gone and lost Sir Vyvin's sword");
						option = multi(player, n, "Do you know where you lost it?",
							"I can make a new sword if you like",
							"Is he angry?");
						if (option == 0) {
							npcsay(player, n, "Well now if I knew that",
								"It wouldn't be lost,now would it?");

							squireDialogue(player, n, Squire.MAIN);
						} else if (option == 1) {
							squireDialogue(player, n, Squire.NEW_SWORD);
						} else if (option == 2) {
							squireDialogue(player, n, Squire.ANGRY);
						}
					} else if (option == 1) {
						npcsay(player, n, "No, sorry I'm loyal to Vyvin");
					}
					break;
				case 1:
				case 2:
				case 3:
					npcsay(player, n, "So how are you doing getting a sword?");
					say(player, n, "I'm still looking for Imcando dwarves");
					break;
				case 4:
					npcsay(player, n, "So how are you doing getting a sword?");
					say(player, n, "I've found an Imcando dwarf",
						"But he needs a picture of the sword before he can make it");
					npcsay(player,
						n,
						"A picture eh?",
						"The only one I can think of is in a small portrait of Sir Vyvin's father",
						"Sir Vyvin keeps it in a cupboard in his room I think");
					break;
				case 5:
				case 6:
					if (player.getCarriedItems().hasCatalogID(ItemId.FALADIAN_KNIGHTS_SWORD.id(), Optional.of(false))) {
						say(player, n, "I have retrieved your sword for you");
						npcsay(player, n, "Thankyou, Thankyou",
							"I was seriously worried I'd have to own up to Sir Vyvin");
						player.message("You give the sword to the squire");
						player.getCarriedItems().remove(new Item(ItemId.FALADIAN_KNIGHTS_SWORD.id()));
						player.sendQuestComplete(getQuestId());
					} else {
						npcsay(player, n, "So how are you doing getting a sword?");
						say(player, n, "I've found a dwarf who will make the sword",
							"I've just got to find the materials for it now");
					}
					break;
			}
		}
		switch (cID) {
			case Squire.MAIN:
				int option = multi(player, n, false, //do not send over
					"Well do you know the vague area you lost it?",
					"I can make a new sword if you like",
					"Well the kingdom is fairly abundant with swords",
					"Is he angry?");
				if (option == 0) {
					say(player, n, "Well do you know the vague area you lost it in?");
					squireDialogue(player, n, Squire.LOST_IT);
				} else if (option == 1) {
					say(player, n, "I can make a new sword if you like");
					squireDialogue(player, n, Squire.NEW_SWORD);
				} else if (option == 2) {
					say(player, n, "Well the kingdom is fairly abundant with swords");
					squireDialogue(player, n, Squire.ABUNDANT);
				} else if (option == 3) {
					say(player, n, "Is he angry?");
					squireDialogue(player, n, Squire.ANGRY);
				}
				break;
			case Squire.LOST_IT:
				npcsay(player,
					n,
					"No I was carrying it for him all the way from where he had it stored in Lumbridge",
					"It must have slipped from my pack during the trip",
					"And you know what people are like these days",
					"Someone will have just picked it up and kept it for themselves");
				int option1 = multi(player, n, "I can make a new sword if you like",
					"Well the kingdom is fairly abundant with swords",
					"Well I hope you find it soon");
				if (option1 == 0) {
					squireDialogue(player, n, Squire.NEW_SWORD);
				} else if (option1 == 1) {
					squireDialogue(player, n, Squire.ABUNDANT);
				} else if (option1 == 2) {
					squireDialogue(player, n, Squire.FIND_IT);
				}
				break;
			case Squire.NEW_SWORD:
				npcsay(player, n, "Thanks for the offer",
					"I'd be surprised if you could though");
				squireDialogue(player, n, Squire.DWARF_CHAT);
				break;
			case Squire.ABUNDANT:
				npcsay(player, n, "Yes you can get bronze swords anywhere",
					"But this isn't any old sword");
				squireDialogue(player, n, Squire.DWARF_CHAT);
				break;
			case Squire.ANGRY:
				npcsay(player, n, "He doesn't know yet",
					"I was hoping I could think of something to do",
					"Before he does find out",
					"But I find myself at a loss");
				squireDialogue(player, n, Squire.MAIN);
				break;
			case Squire.FIND_IT:
				npcsay(player, n, "Yes me too",
					"I'm not looking forward to telling Vyvin I've lost it",
					"He's going to want it for the parade next week as well");
				break;
			case Squire.DWARF_CHAT:
				npcsay(player,
					n,
					"The thing is,this sword is a family heirloom",
					"It has been passed down through Vyvin's family for five generations",
					"It was originally made by the Imcando Dwarves",
					"Who were a particularly skilled tribe of dwarven smiths",
					"I doubt anyone could make it in the style they do");

				int option11 = multi(player, n,
					"So would these dwarves make another one?",
					"Well I hope you find it soon");
				if (option11 == 0) {
					npcsay(player,
						n,
						"I'm not a hundred percent sure the Imcando tribe exists anymore",
						"I should think Reldo the palace librarian in Varrock will know",
						"He has done a lot of research on the races of Runescape",
						"I don't suppose you could try and track down the Imcando dwarves for me?",
						"I've got so much work to do");

					int option2 = multi(player, n, "Ok I'll give it a go",
						"No I've got lots of mining work to do");
					if (option2 == 0) {
						npcsay(player, n, "Thankyou very much",
							"As I say the best place to start should be with Reldo");
						player.updateQuestStage(this, 1);
					}
				} else if (option11 == 1) {
					squireDialogue(player, n, Squire.FIND_IT);
				}
				break;
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return (obj.getID() == VYVINS_CUPBOARD_OPEN || obj.getID() == VYVINS_CUPBOARD_CLOSED) && obj.getY() == CUPBOARD_Y
				&& obj.getX() == 318;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		final Npc n = player.getWorld().getNpc(NpcId.SIR_VYVIN.id(), 316, 320, 2454, 2459);
		if ((obj.getID() == VYVINS_CUPBOARD_OPEN || obj.getID() == VYVINS_CUPBOARD_CLOSED) && obj.getY() == CUPBOARD_Y
			&& obj.getX() == 318) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, player, VYVINS_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, player, VYVINS_CUPBOARD_CLOSED);
			} else {
				if (n != null) {
					if (!n.isBusy()) {
						npcsay(player, n, "Hey what are you doing?",
							"That's my cupboard");
						mes("Maybe you need to get someone to distract Sir Vyvin for you");
						delay(3);
					} else {
						if (player.getCarriedItems().hasCatalogID(ItemId.PORTRAIT.id(), Optional.of(false)) || player.getQuestStage(this) < 4) {
							player.message("There is just a load of junk in here");
							return;
						}
						player.message("You find a small portrait in here which you take");
						give(player, ItemId.PORTRAIT.id(), 1);
					}
				}
			}
		}
	}

	class Squire {
		public static final int ANGRY = 6;
		public static final int DWARF_CHAT = 5;
		public static final int ABUNDANT = 4;
		public static final int FIND_IT = 3;
		public static final int NEW_SWORD = 2;
		public static final int LOST_IT = 1;
		public static final int MAIN = 0;

	}

}
