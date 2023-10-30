package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.custom.minigames.ABoneToPick;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import java.util.ArrayList;

import static com.openrsc.server.plugins.RuneScript.*;

public class Lily implements TalkNpcTrigger {

    @Override
	public void onTalkNpc(Player player, Npc npc) {
		npcsay("Hello my lovely!",
            "How can I help you?");

        ArrayList<String> options = new ArrayList<String>();
        options.add("What are you doing here?");
        options.add("Could you tell me about harvesting?");
        if (Functions.config().WANT_CUSTOM_SPRITES) { // This should always be true if harvesting is active
            options.add("I like your cape!");
        }
        if (Functions.config().A_BONE_TO_PICK) {
        	int stage = ABoneToPick.getStage(player);
			if (stage == ABoneToPick.COMPLETED) {
				options.add("The skeletons have been dealt with");
			} else if (stage <= ABoneToPick.HECKLED_THRICE) {
        		options.add("Have two skeletons been bothering you?");
			} else if (stage == ABoneToPick.SPOKE_TO_LILY) {
        		options.add("Where am I supposed to go?");
			} else if (stage == ABoneToPick.HEARD_AMAZING_SONG) {
        		options.add("The skeletons will not leave");
			}
		}

        int option = multi(options.toArray(new String[0]));

        switch (option) {
            case 0:
                npcsay("I'm just out here enjoying nature!",
                    "I love being with all the plants and animals...",
                    "...and smelling the fresh air!",
                    "I also love helping new adventurers learn harvesting!",
					"So if you have any questions, don't hesitate to ask!");
                break;
            case 1:
            	npcsay("Oh harvesting is my favorite skill!",
					"You might get your hands dirty...",
					"...but I just wear gloves for that!",
					"Did you have a specific question in mind?");

            	int subOption = multi("How do I get started?",
					"What do I get from harvesting?",
					"Do I need any tools for harvesting?",
					"Actually nevermind");
            	switch (subOption) {
					case 0:
						npcsay("I'm so excited that you want to get started with harvesting!",
							"You can get started right away!",
							"See these potatoes here?",
							"Just click on it to start harvesting some yummy potatoes!",
							"After a while, you'll get good enough to start harvesting other things!");
						if (Functions.config().WANT_SKILL_MENUS) {
							npcsay("You can check the skill guide by clicking on the Harvesting skill in your skills menu",
								"The skill guide will show you what you can harvest at different levels!");
						}
						npcsay("You also might want to talk to the dear gardeners that hang out around the castles",
							"They are so helpful and are always willing to answer questions!",
							"They can also sell you useful tools to help your harvesting be more efficient!");
						break;
					case 1:
						npcsay("Why a feeling of self-fulfillment of course!",
							"And of course you get to keep whatever you harvest!",
							"You can harvest yummy fruits and vegetables, wonderful herbs, and other assorted plants!",
							"If you get good enough, I might even let you have a cape like mine!");
						mes("Lily smiles and winks at you");
						delay(3);
						break;
					case 2:
						npcsay("Not really",
							"If you don't mind getting your hands dirty, you can harvest almost anything!",
							"However tools will always help your harvest be more bountiful!",
							"If you're looking for tools...",
							"...you can buy them from the gardeners that like to wander around the castles",
							"They're very sweet and are always willing to help out!");
						break;
				}
                break;
            case 2:
            	mes("Lily beams at you");
            	delay(3);
            	npcsay("Aren't you a sweetheart!",
					"This is my Harvesting cape!",
					"I make them for adventurers that have shown a big interest in harvesting!",
					"I'd love to make you one!");
            	if (player.getSkills().getMaxStat(Skill.HARVESTING.id()) >= 99) {
					npcsay("I'd give it to you for free, but the materials can be kind of expensive",
						"So I will have to ask for 99,000 coins in return",
						"Would you like a cape?");
					if (multi("I'd love one!", "No thankyou") == 0) {
						if (ifheld(ItemId.COINS.id(), 99000)) {
							mes("You give 99,000 coins to Lily");
							remove(ItemId.COINS.id(), 99000);
							delay(3);
							mes("Lily gives you a Harvesting cape");
							give(ItemId.HARVESTING_CAPE.id(), 1);
							delay(3);
							npcsay("I made this cape extra special for you!",
								"In fact, if you harvest while wearing this cape...",
								"...you will have a chance of receiving double the yield!",
								"I hope you have a wonderful day!");
							mes("Lily waves goodbye");
							delay(3);
						} else {
							say("But I don't have enough coins right now");
							npcsay("That's okay!",
								"You can come back anytime!",
								"I'll be here!");
						}
					}
				} else {
            		npcsay("Just keep working at Harvesting!",
						"One day you'll get there!");
				}
                break;
			case 3:
				if (!Functions.config().A_BONE_TO_PICK) return;
				ABoneToPick.lilyDialogue(player, npc);
				break;
        }
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.LILY.id();
	}
}
