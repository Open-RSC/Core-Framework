package com.openrsc.server.plugins.authentic.quests.members.digsite;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteExpert implements QuestInterface, TalkNpcTrigger, UseNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.DIGSITE;
	}

	@Override
	public String getQuestName() {
		return "Digsite (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.DIGSITE.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Congratulations, you have finished the digsite quest");
		final QuestReward reward = Quest.DIGSITE.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		player.getCache().remove("winch_rope_2");
		player.getCache().remove("winch_rope_1");
		player.getCache().remove("digsite_winshaft");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.ARCHAEOLOGICAL_EXPERT.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.ARCHAEOLOGICAL_EXPERT.id()) {
			switch (player.getQuestStage(Quests.DIGSITE)) {
				case -1:
					npcsay(player, n, "Hello again",
						"I am now studying this mysterious altar and its inhabitants",
						"The markings are strange, but it refers to a god I have never",
						"heard of before named Zaros. It must be some pagan superstition.",
						"That was a great find, who knows what other secrets",
						"Lie buried beneath the surface of our land...");
					break;
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
					say(player, n, "Hello, who are you ?");
					npcsay(player, n, "Good day to you",
						"My name is Terry balando",
						"I am an expert on digsite finds",
						"I am employed by the museum in varrock",
						"To oversee all finds in this digsite",
						"Anything you find must be reported to me");
					say(player, n, "Oh, okay if I find anything of interest I will bring it here");
					npcsay(player, n, "Very good",
						"Can I help you at all ?");
					int menu = multi(player, n, false, //do not send over
						"I have something I need checking out",
						"No thanks",
						"Can you tell me anything about the digsite?");
					if (menu == 0) {
						say(player, n, "I have something I need checking out");
						npcsay(player, n, "Okay, give it to me and I'll have a look for you");
					} else if (menu == 1) {
						say(player, n, "No thanks");
						npcsay(player, n, "Good, let me know if you find anything unusual");
					} else if (menu == 2) {
						say(player, n, "Can you tell me anything about the digsite ?");
						npcsay(player, n, "Yes indeed, I am currently studying the lives of the settlers",
							"During the end of the third age, this used to be a great city",
							"It's inhabitants were humans, supporters of the god Saradomin",
							"It's not recorded what happened to the community here",
							"I suspect nobody has lived here for over a millenium!");
					}
					break;
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc n, Item i) {
		return n.getID() == NpcId.ARCHAEOLOGICAL_EXPERT.id();
	}

	@Override
	public void onUseNpc(Player player, Npc n, Item i) {
		if (n.getID() == NpcId.ARCHAEOLOGICAL_EXPERT.id()) {
			switch (ItemId.getById(i.getCatalogId())) {
				case GOLD_NUGGETS:
					say(player, n, "I have these gold nuggets");
					if (ifheld(player, ItemId.GOLD_NUGGETS.id(), 3)) {
						player.message("You give the nuggets to the expert");
						player.getCarriedItems().remove(new Item(ItemId.GOLD_NUGGETS.id(), 3));
						give(player, ItemId.GOLD.id(), 1);
						npcsay(player, n, "Good, that's 3, I can exchange them for normal gold now",
							"You can get this refined and make a profit!");
						say(player, n, "Excellent!");
					} else {
						npcsay(player, n, "I can't do much with these nuggets yet",
							"Come back when you have 3, and I will exchange them for you");
						say(player, n, "Okay I will, thanks");
					}
					break;
				case PANNING_TRAY_FULL:
					player.message("You give the panning tray to the expert");
					npcsay(player, n, "Have you searched this tray yet ?");
					say(player, n, "Not that I remember");
					npcsay(player, n, "It may contain something, I don't want to get my hands dirty");
					player.playerServerMessage(MessageType.QUEST, "The expert hands the tray back to you");
					break;
				case PANNING_TRAY_GOLD_NUGGET:
					player.message("You give the panning tray to the expert");
					npcsay(player, n, "Did you realize there is something in this tray ?");
					say(player, n, "Err, not really");
					npcsay(player, n, "Check it out thoroughly first");
					player.playerServerMessage(MessageType.QUEST, "The expert hands you back the tray");
					break;
				case PANNING_TRAY:
					player.message("You give the panning tray to the expert");
					npcsay(player, n, "I have no need for panning trays");
					break;
				case CRACKED_ROCK_SAMPLE:
					say(player, n, "I found this rock...");
					npcsay(player, n, "What a shame it's cracked, this looks like it would have been a good sample");
					break;
				case TALISMAN_OF_ZAROS:
					say(player, n, "What about this ?");
					npcsay(player, n, "Unusual...",
						"This object doesn't appear right...",
						"Hmmmm.....");
					delay(3);
					npcsay(player, n, "I wonder...Let me check my guide...",
						"Could it be ? surely not...",
						"From the markings on it it seems to be",
						"a ceremonial ornament to a god named...",
						"Zaros? I have never heard of him before",
						"This is a great discovery, we know very little",
						"of the pagan gods that people worshipped",
						"in the olden days. There is some strange writing",
						"embossed upon it - it says",
						"'Zaros will return and wreak his vengeance",
						"upon Zamorak the pretender' - I wonder what",
						"it means by that? Some silly superstition probably.",
						"Still, I wonder what this is doing around here...",
						"I'll tell you what, as you have found this",
						"I will allow you to use the private dig shaft",
						"You obviously have a keen eye...",
						"Take this letter and give it to one of the workmen",
						"And they will allow you to use it");
					player.message("The expert hands you a letter");
					player.getCarriedItems().remove(new Item(ItemId.TALISMAN_OF_ZAROS.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.DIGSITE_SCROLL.id()));
					break;
				case UNIDENTIFIED_LIQUID:
					say(player, n, "Do you know what this is ?");
					npcsay(player, n, "Where did you get this ?");
					say(player, n, "From one of the barrels at the digsite");
					npcsay(player, n, "This is a dangerous liquid called nitroglycerin",
						"Be careful how you handle it");
					player.getCarriedItems().remove(new Item(ItemId.UNIDENTIFIED_LIQUID.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.NITROGLYCERIN.id()));
					break;
				case NITROGLYCERIN:
					say(player, n, "Can you tell me any more about this ?");
					npcsay(player, n, "nitroglycerin...this is a dangerous substance",
						"This is normally mixed with other chemicals",
						"To produce a potent compound...",
						"Be sure not to drop it!",
						"That stuff is highly volatile...");
					break;
				case UNIDENTIFIED_POWDER:
					say(player, n, "Do you know what this powder is ?");
					npcsay(player, n, "Really you do find the most unusual items",
						"I know what this is...",
						"It's called ammonium nitrate - A strong chemical",
						"Why you want this i'll never know...");
					player.getCarriedItems().remove(new Item(ItemId.UNIDENTIFIED_POWDER.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.AMMONIUM_NITRATE.id()));
					break;
				case MIXED_CHEMICALS_1:
					say(player, n, "Hey, look at this");
					npcsay(player, n, "Hmmm, that looks dangerous...",
						"Handle it carefully and don't drop it!");
					break;
				case EXPLOSIVE_COMPOUND:
					say(player, n, "What do you think about this ?");
					npcsay(player, n, "What have you concocted now ?",
						"Just be careful when playing with chemicals...");
					break;
				case MIXED_CHEMICALS_2:
					say(player, n, "See what I have done with the compound now");
					npcsay(player, n, "Seriously, I think you have a death wish!",
						"What on earth are you going to do with that stuff ?");
					say(player, n, "I'll find a use for it");
					break;
				case STONE_TABLET: // QUEST COMPLETION!!
					if (player.getQuestStage(Quests.DIGSITE) == -1) {
						npcsay(player, n, "I don't need another tablet",
							"One is enough thank you!");
						return;
					}
					if (player.getQuestStage(Quests.DIGSITE) == 6) {
						say(player, n, "I found this in a hidden cavern beneath the digsite");
						player.getCarriedItems().remove(new Item(ItemId.STONE_TABLET.id()));
						npcsay(player, n, "Incredible!");
						say(player, n, "There is an altar down there",
							"The place is crawling with skeletons!");
						npcsay(player, n, "Yuck!",
							"This is an amazing discovery!",
							"All this while we were convinced...",
							"That no other race had lived here",
							"It seems the followers of Saradomin",
							"Have tried to cover up the evidence of the zaros altar",
							"This whole city must have been built over it!",
							"Thanks for your help",
							"Your sharp eyes have spotted what many have missed...",
							"Here, take this as your reward");
						player.message("The expert gives you 2 gold bars as payment");
						give(player, ItemId.GOLD_BAR.id(), 2);
						player.sendQuestComplete(Quests.DIGSITE);
					}
					break;
				case BELT_BUCKLE:
					say(player, n, "Have a look at this unusual item");
					npcsay(player, n, "Let me see..",
						"This is a belt buckle",
						"I should imagine it came from a guard");
					break;
				case BONES:
					say(player, n, "Have a look at these bones");
					npcsay(player, n, "Ah yes, a fine bone example",
						"No noticeable fractures, and in good condition",
						"There are common cow bones however",
						"They have no archaeological value");
					break;
				case BROKEN_ARROW:
					say(player, n, "Have a look at this arrow");
					npcsay(player, n, "No doubt this arrow was shot by a strong warrior",
						"It's split in half!",
						"It is not a valuable object though...");
					break;
				case BROKEN_GLASS_DIGSITE_LVL_2:
					say(player, n, "Have a look at this glass");
					npcsay(player, n, "Hey you should be careful of that",
						"It might cut your fingers, throw it away!");
					break;
				case BROKEN_STAFF:
					say(player, n, "Have a look at this staff");
					npcsay(player, n, "Look at this...interesting",
						"This appers to belong to a cleric of some kind",
						"Certainly not a follower of saradomin however...",
						"I wonder if there was another civilization before the saradominists ?");
					break;
				case BUTTONS:
					say(player, n, "I found these buttons");
					npcsay(player, n, "Let's have a look",
						"Ah, I think these are from the nobility",
						"Perhaps a royal servant ?",
						"Not valuable but an unusual find for this area");
					break;
				case CERAMIC_REMAINS:
					say(player, n, "I found some potery pieces");
					npcsay(player, n, "Yes many parts are discovered",
						"The inhabitants of these parts were great potters...");
					say(player, n, "You mean they were good at using potions ?");
					npcsay(player, n, "No no silly - they are were known for their skill with clay");
					break;
				case DAMAGED_ARMOUR_1:
					say(player, n, "I found some old armour");
					npcsay(player, n, "Hmm...unusual",
						"This armour dosen't seem to match with the other finds",
						"Keep looking, this could be evidence of an older civilization!");
					break;
				case DAMAGED_ARMOUR_2:
					say(player, n, "I found some armour");
					npcsay(player, n, "It looks like the wearer of this fought a mighty battle");
					break;
				case NEEDLE:
					say(player, n, "I found a needle");
					npcsay(player, n, "Hmm yes, I wondered why this race were so well dressed!",
						"It looks like they had a mastery of needlework");
					break;
				case OLD_BOOT:
					say(player, n, "Have a look at this");
					npcsay(player, n, "Ah yes, an old boot",
						"Not really an ancient artifact is it?");
					break;
				case OLD_TOOTH:
					say(player, n, "Hey look at this");
					npcsay(player, n, "Oh, an old tooth",
						"..It looks like it has come from a mighty being");
					break;
				case DIGSITE_SCROLL:
					npcsay(player, n, "There's no point in giving me this back!");
					break;
				case ROCK_SAMPLE_GREEN:
				case ROCK_SAMPLE_ORANGE:
				case ROCK_SAMPLE_PURPLE: //rock samples
					say(player, n, "Have a look at this rock");
					if (i.getCatalogId() == ItemId.ROCK_SAMPLE_ORANGE.id()) {
						npcsay(player, n, "This rock has been picked at",
							"It looks like it could belong to someone...");
					} else if (i.getCatalogId() == ItemId.ROCK_SAMPLE_GREEN.id()) {
						npcsay(player, n, "This has been partly prepared",
							"It looks like it may belong to someone...");
					} else if (i.getCatalogId() == ItemId.ROCK_SAMPLE_PURPLE.id()) {
						npcsay(player, n, "This rock is not naturally formed",
							"It looks like it might belong to someone...");
					}
					break;
				case ROTTEN_APPLES:
					say(player, n, "I found these...");
					npcsay(player, n, "Ew! throw them away this instant!");
					break;
				case RUSTY_SWORD:
					say(player, n, "I found an old sword");
					npcsay(player, n, "Oh, its very rusty isn't it ?",
						"I'm not sure this sword belongs here",
						"It looks very out of place...");
					break;
				case VASE:
					say(player, n, "I found a vase");
					npcsay(player, n, "Ah yes these are commonly found in these parts",
						"Not a valuable item");
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
	}
}
