package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class RomeoAndJuliet implements QuestInterface,TalkToNpcListener,
		TalkToNpcExecutiveListener {

	/**
	 * TODO: DO THIS ON REAL RSC THERE ARE MISSING INFO FROM JULIET.
	 */
	@Override
	public int getQuestId() {
		return Constants.Quests.ROMEO_N_JULIET;
	}

	@Override
	public String getQuestName() {
		return "Romeo & Juliet";
	}

	private void romeoDialogue(Player p, Npc n) {
		switch (p.getQuestStage(this)) {
		case 0:
			npcTalk(p, n, "Juliet, Juliet, Juliet! Wherefore Art thou?",
					"Kind friend, Have you seen Juliet?",
					"Her and her Father seem to have disappeared");
			int choice = showMenu(p, n, new String[] { "Yes, I have seen her",
					"No, but that's girls for you ",
					"Can I help find her for you?" });
			if (choice == 0) {
				playerTalk(p, n, "I think it was her. Blond, stressed");
				npcTalk(p, n, "Yes, that sounds like her",
						"Please tell her I long to be with her");
				int choice2 = showMenu(p, n, new String[] {
						"Yes, I will tell her",
						"Sorry I am too busy. Maybe later?" });
				if (choice2 == 0) {
					npcTalk(p, n, "You are the saviour of my heart, thank you");
					playerTalk(p,n, "Err, yes. Ok. Thats... nice.");
					p.updateQuestStage(this, 1);
				} else if (choice2 == 1) {
					npcTalk(p, n,
							"Well if you do find her, I would be most grateful");
				}
			} else if (choice == 1) {
				npcTalk(p, n, "Not my dear Juliet.",
						"Could you find her for me?",
						"Please tell her I long to be with her");
				int choice3 = showMenu(p, n, new String[] {
						"Yes, I will tell her how you feel",
						"I can't, it sounds like work for me" });
				if (choice3 == 0) {
					npcTalk(p, n, "You are the saviour of my heard, thank you");
					playerTalk(p,n, "Err, yes. Ok. Thats... nice.");
					p.updateQuestStage(this, 1);
				} else if (choice3 == 1) {
					npcTalk(p, n,
							"Well, I guess you are not the romantic type",
							"Goodbye");
				}
			} else if (choice == 2) {
				npcTalk(p, n, "Oh would you? That would be wonderful!",
						"Please tell her I long to be with her");
				playerTalk(p, n, "Yes, I will tell her how you feel");
				npcTalk(p, n, "You are the saviour of my heard, thank you");
				playerTalk(p,n, "Err, yes. Ok. Thats... nice.");
				p.updateQuestStage(this, 1);
			}
			break;
		case 1:
			npcTalk(p, n, "Please find my Juliet. I am so, so sad");
			break;
		case 2:
			playerTalk(p, n, "Romeo, I have a message from Juliet");
			p.message("You pass Juliet's message to Romeo");
			p.getInventory().remove(p.getInventory().getLastIndexById(56));
			npcTalk(p, n, "Tragic news. Her father is opposing our marriage",
					"If her father sees me, he will kill me",
					"I dare not go near his lands",
					"She says Father Lawrence can help us",
					"Please find him for me. Tell him of our plight");
			p.updateQuestStage(getQuestId(), 3);
			break;
		case 3:
		case 4:
		case 5:
		case 6:
			npcTalk(p, n, "Please find Father Lawrence, he can help us");
			break;
		case 7:
			playerTalk(p, n, "Romeo, its all set. Juliet has the potion");
			npcTalk(p, n, "Ah right", "What potion would that be then");
			playerTalk(p, n, "The one to get her to the crypt");
			npcTalk(p, n, "Ah right", "So she is dead then. Ah thats a shame",
					"Thanks for your help anyway");
			p.sendQuestComplete(Constants.Quests.ROMEO_N_JULIET);
			break;
		case -1:
			npcTalk(p, n, "I heard Juliet had died. Terrible business",
					"Her cousin and i are getting on well though",
					"Thanks for your help");
			break;
		}
	}

	private void julietDialogue(Player p, Npc n) {
		switch (p.getQuestStage(this)) {
		case 0:
			npcTalk(p, n, "Romeo, Romeo, wherefore art thou Romeo",
					"Bold adventurer, have you seen Romeo on your travels?",
					"Skinyy guy, a bit wishy washy, head full of poetry");
			int choice = showMenu(p, n, new String[] { "yes, I have met him",
					"No, I think I would have remembered if i had",
					"I guess i could find him", "I think you could do better" });
			if (choice == 0) {
				npcTalk(p, n, "He has his good points",
						"He dosen't spend all day on the internet, at least");
			} else if (choice == 1) {

			} else if (choice == 2) {
				npcTalk(p, n, "That is most kind of you",
						"Could you please deliver a messsage to him");
				playerTalk(p, n,
						"Certinly, I will deliver your message straight away");
				npcTalk(p, n, "It may be our only hope");
				p.updateQuestStage(getQuestId(), 2);
			} else if (choice == 3) {

			}
			break;
		case 1:
			playerTalk(p, n, "Juliet, I come from Romeo",
					"He begs me tell you he cares still");
			npcTalk(p, n, "Please, Take this message to him");
			playerTalk(p, n,
					"Certinly, I will deliver your message straight away");
			npcTalk(p, n, "It may be our only hope");
			p.message("Juliet gives you a message");
			p.getInventory().add(new Item(56));
			p.updateQuestStage(getQuestId(), 2);
			break;
		case 2:
			npcTalk(p, n, "Please, deliver the message to Romeo with all speed");
			break;
		case 3:
			npcTalk(p, n, "I have heard you have to find Father Lawrence",
					"Please help us brave adventurer");
			break;
		case 4:
			npcTalk(p, n,
					"I think you have some things to talk with The Apothecary",
					"We really need your help");
			break;
		case 6:
			playerTalk(p, n, "I have a potion from Father Lawrence",
					"It should make you seem dead, and get you away from this place");
			p.message("You pass potion to Juliet");
			p.getInventory().remove(p.getInventory().getLastIndexById(57));
			npcTalk(p, n,
					"I just hope Romeo can remember to get me from the Crypt",
					"Many thanks kind friend",
					"Please go to Romeo, make sure he understands",
					"He can be a bit dense sometimes");
			p.updateQuestStage(getQuestId(), 7);
			break;
		case 7:
			npcTalk(p, n,
					"Have you seen Romeo? He will reward you for your help",
					"He is the wealth in this story", "I am just the glamour");
			break;
		case -1:
			npcTalk(p,
					n,
					"I sat in that cold crypt for ages waiting for Romeo",
					"That useless fool never showed up",
					"And all i got was indigestion. I am done with men like him",
					"Now go away before i call my father");
			break;
		}
	}

	private void lawrenceDialogue(Player p, Npc n) {
		switch (p.getQuestStage(this)) {
		case 0:
			npcTalk(p, n, "Hello adventurer, do you seek a quest");
			int choice = showMenu(p, n, new String[] {
					"I'm always looking for a quest",
					"No, I prefer just to kill things",
					"Can you recommend a good bar" });
			if (choice == 0) {
				npcTalk(p,
						n,
						"Well, I see poor Romeo wandering around the square. I think he may need help",
						"I was helping him and Juliet to meet, but it became impossible",
						"I am sure he can use some help");
			} else if (choice == 1) {
				npcTalk(p, n, "That's fine career in these lands",
						"There is more that needs killing everyday");
			} else if (choice == 2) {
				npcTalk(p, n, "Drinking will be the death of you",
						"But the Blue Moon in the city is cheap enough",
						"And providing you but one drink and hour they let you stay all night");
			}
			break;
		case 3:
			playerTalk(p, n, "Romeo sent me. He says you can help");
			npcTalk(p, n,
					"Ah Romeo, yes. A fine lad, but a little bit confused");
			playerTalk(p, n, "Juliet must be rescued from her fathers control");
			npcTalk(p, n,
					"I know just the things. A potion to make her appear dead",
					"Then Romeo can collect her from the crypt",
					"Go to the Apothecary, tell him I sent you",
					"You need some Cadava Potion");
			p.updateQuestStage(getQuestId(), 4);
			break;
		case 4:
			npcTalk(p, n, "Ah, have you found the Apothecary yet?",
					"Remember, Cadava potion, for Father Lawrence");
			break;
		case 5:
			npcTalk(p, n, "Did you find the Apothecary");
			playerTalk(p, n, "Yes, I must find some berries");
			npcTalk(p, n, "Well, take care. They are poisonous so the touch",
					"You will need gloves");
			playerTalk(p, n, "I am on my way back to him with the ingredients");
			npcTalk(p, n,
					"Good work. Get the potion to Juliey whn you have it",
					"I will tell Romeo to be ready");
			break;
		case 6:
			playerTalk(p, n, "Everything I could do is done now,",
					"everything else depends on Romeo and Juliet");
			npcTalk(p, n, "I hope they will find their luck!");
			break;
		case -1:
			npcTalk(p, n, "Oh to be a father in the times of whiskey",
					"I sing and I drink and i wake up in gutters",
					"Top of the morning to you",
					"To err is human, to forgive, quite difficult");
			break;
		}
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 30) {
			romeoDialogue(p, n);
		}
		if (n.getID() == 31) {
			julietDialogue(p, n);
		}
		if (n.getID() == 32) {
			lawrenceDialogue(p, n);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 30 || n.getID() == 31 || n.getID() == 32) {
			return true;
		}
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("You have completed the quest of Romeo and Juliet");
		player.incQuestPoints(5);
		player.message("@gre@You haved gained 5 quest points!");

	}

	@Override
	public boolean isMembers() {
		return false;
	}

}
