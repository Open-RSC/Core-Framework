package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class RomeoAndJuliet implements QuestInterface, TalkNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.ROMEO_N_JULIET;
	}

	@Override
	public String getQuestName() {
		return "Romeo & Juliet";
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("You have completed the quest of Romeo and Juliet");
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.ROMEO_N_JULIET), true);
		player.message("@gre@You haved gained 5 quest points!");
	}

	private void romeoDialogue(Player p, Npc n) {
		switch (p.getQuestStage(this)) {
			case 0:
				npcsay(p, n, "Juliet, Juliet, Juliet!  Wherefore Art thou?",
					"Kind friend, Have you seen Juliet?",
					"Her and her Father seem to have disappeared");
				int choice = multi(p, n, "Yes, I have seen her",
					"No, but that's girls for you",
					"Can I help find her for you?");
				if (choice == 0) {
					say(p, n, "I think it was her. Blond, stressed");
					npcsay(p, n, "Yes, that sounds like her",
						"Please tell her I long to be with her");
					int choice2 = multi(p, n, false, //do not send over
						"Yes, I will tell her",
						"Sorry, I am too busy. Maybe later?");
					if (choice2 == 0) {
						say(p, n, "Yes, I will tell her how you feel");
						npcsay(p, n, "You are the saviour of my heart, thank you.");
						say(p, n, "err, yes. Ok. Thats.... nice.");
						p.updateQuestStage(this, 1);
					} else if (choice2 == 1) {
						say(p, n, "Sorry, I am too busy. Maybe later?");
						npcsay(p, n,
							"Well if you do find her, I would be most grateful");
					}
				} else if (choice == 1) {
					npcsay(p, n, "Not my dear Juliet. She is different",
						"Could you find her for me?",
						"Please tell her I long to be with her");
					int choice3 = multi(p, n,
						"Yes, I will tell her how you feel",
						"I can't, it sounds like work to me");
					if (choice3 == 0) {
						npcsay(p, n, "You are the saviour of my heart, thank you.");
						say(p, n, "err, yes. Ok. Thats.... nice.");
						p.updateQuestStage(this, 1);
					} else if (choice3 == 1) {
						npcsay(p, n,
							"Well, I guess you are not the romantic type",
							"Goodbye");
					}
				} else if (choice == 2) {
					npcsay(p, n, "Oh would you? That would be wonderful!",
						"Please tell her I long to be with her");
					say(p, n, "Yes, I will tell her how you feel");
					npcsay(p, n, "You are the saviour of my heart, thank you.");
					say(p, n, "err, yes. Ok. Thats.... nice.");
					p.updateQuestStage(this, 1);
				}
				break;
			case 1:
				npcsay(p, n, "Please find my Juliet. I am so, so sad");
				break;
			case 2:
				int count = messageCount(p);
				if (count < 2) {
					say(p, n, "Romeo, I have a message from Juliet");
				} else if (count < 3) {
					say(p, n, "Romeo, I have a message from Juliet",
						"Except that I seem to have lost it");
				} else {
					npcsay(p, n, "Ah, it seems that you can deliver a message after all",
						"My faith in you is restored!");
				}
				p.message("You pass Juliet's message to Romeo");
				p.getCarriedItems().remove(ItemId.MESSAGE.id(), 1);
				npcsay(p, n, "Tragic news. Her father is opposing our marriage",
					"If her father sees me, he will kill me",
					"I dare not go near his lands",
					"She says Father Lawrence can help us",
					"Please find him for me. Tell him of our plight");
				p.updateQuestStage(getQuestId(), 3);
				p.getCache().remove("romeo_juliet_msgs");
				break;
			case 3:
				npcsay(p, n, "Please friend, how goes our quest?",
					"Father Lawrence must be told. only he can help");
				break;
			case 4:
				npcsay(p, n, "Did you find the Father? What did he suggest?");
				int menu = multi(p, n, "He sent me to the apothecary",
					"He seems keen for you to marry Juliet");
				if (menu == 0) {
					npcsay(p, n, "I know him. He lives near the town square",
						"the small house behind the sloped building",
						"Good luck");
				} else if (menu == 1) {
					npcsay(p, n, "I think he wants some peace. He was our messenger",
						"before you were kind enough to help us");
				}
				break;
			case 5:
				npcsay(p, n, "I hope the potion is near ready",
					"It is the last step for the great plan",
					"I hope I will be with my dear one soon");
				break;
			case 6:
				npcsay(p, n, "Ah, you have the potion. I was told what to do by the good Father",
					"Better get it to Juliet. She knows what is happening");
				break;
			case 7:
				say(p, n, "Romeo, its all set. Juliet has the potion");
				npcsay(p, n, "Ah right", "What potion would that be then?");
				say(p, n, "The one to get her to the crypt.");
				npcsay(p, n, "Ah right", "So she is dead then. Ah thats a shame",
					"Thanks for your help anyway.");
				p.sendQuestComplete(Quests.ROMEO_N_JULIET);
				break;
			case -1:
				npcsay(p, n, "I heard Juliet had died. Terrible business",
					"Her cousin and I are getting on well though",
					"Thanks for your help");
				break;
		}
	}

	private void julietDialogue(Player p, Npc n) {
		switch (p.getQuestStage(this)) {
			case 0:
				npcsay(p, n, "Romeo, Romeo, wherefore art thou Romeo?",
					"Bold adventurer, have you seen Romeo on your travels?",
					"Skinny guy, a bit wishy washy, head full of poetry");
				int choice = multi(p, n, false, //do not send over
					"Yes I have met him",
					"No, I think I would have remembered if I had",
					"I guess I could find him", "I think you could do better");
				if (choice == 0) {
					say(p, n, "I did see Romeo somewhere.",
						"He seemed a bit depressed.");
					npcsay(p, n, "Yes, that would be him.",
						"Could you please deliver a messge to him?");
					int sub_choice = multi(p, n, false, //do not send over
						"Certainly, I will do so straight away",
						"No, he was a little too weird for me");
					if (sub_choice == 0) {
						say(p, n, "Certinly, I will deliver your message straight away");
						npcsay(p, n, "It may be our only hope");
						p.message("Juliet gives you a message");
						p.getCarriedItems().getInventory().add(new Item(ItemId.MESSAGE.id()));
						p.getCache().set("romeo_juliet_msgs", 1);
						p.updateQuestStage(getQuestId(), 2);
					} else if (sub_choice == 1) {
						say(p, n, "No");
						npcsay(p, n, "Oh dear, that will be the ruin of our love",
							"Well, I will just stay here and worry",
							"You unromantic soul.");
					}
				} else if (choice == 1) {
					say(p, n, "No, I think I would have remembered");
					npcsay(p, n, "Could you please deliver a message to him?");
					int sub_choice = multi(p, n, false, //do not send over
						"Certinly, I will do so straight away",
						"No, I have better things to do");
					if (sub_choice == 0) {
						say(p, n, "Certinly, I will deliver your message straight away");
						npcsay(p, n, "It may be our only hope");
						p.message("Juliet gives you a message");
						p.getCarriedItems().getInventory().add(new Item(ItemId.MESSAGE.id()));
						p.getCache().set("romeo_juliet_msgs", 1);
						p.updateQuestStage(getQuestId(), 2);
					} else if (sub_choice == 1) {
						say(p, n, "No, I have better things to do");
						npcsay(p, n, "I will not keep you from them. Goodbye");
					}
				} else if (choice == 2) {
					say(p, n, "I guess I could find him");
					npcsay(p, n, "That is most kind of you",
						"Could you please deliver a message to him?");
					say(p, n,
						"Certinly, I will deliver your message straight away");
					npcsay(p, n, "It may be our only hope");
					p.message("Juliet gives you a message");
					p.getCarriedItems().getInventory().add(new Item(ItemId.MESSAGE.id()));
					p.getCache().set("romeo_juliet_msgs", 1);
					p.updateQuestStage(getQuestId(), 2);
				} else if (choice == 3) {
					say(p, n, "I think you could do better");
					npcsay(p, n, "He has his good points",
						"He doesn't spend all day on the internet, at least");
				}
				break;
			case 1:
				say(p, n, "Juliet, I come from Romeo",
					"He begs me tell you he cares still");
				npcsay(p, n, "Please, Take this message to him");
				say(p, n,
					"Certinly, I will deliver your message straight away");
				npcsay(p, n, "It may be our only hope");
				p.message("Juliet gives you a message");
				p.getCarriedItems().getInventory().add(new Item(ItemId.MESSAGE.id()));
				p.getCache().set("romeo_juliet_msgs", 1);
				p.updateQuestStage(getQuestId(), 2);
				break;
			case 2:
				int count = messageCount(p);
				if (count <= 2 && p.getCarriedItems().hasCatalogID(ItemId.MESSAGE.id(), Optional.of(false)))
					npcsay(p, n, "Please, deliver the message to Romeo with all speed");
				else {
					if (count < 2) {
						npcsay(p, n, "How could you lose this most important message?",
							"Please, take this message to him, and please don't lose it");
						p.message("Juliet gives you another message");
						p.getCarriedItems().getInventory().add(new Item(ItemId.MESSAGE.id()));
						p.getCache().set("romeo_juliet_msgs", 2);
					} else if (count < 3) {
						npcsay(p, n, "It seems I cannot trust you with a simple message",
							"I am sorry, I need a more reliable messenger");
						//doesn't give another msg, just for dialogue purposes
						p.getCache().set("romeo_juliet_msgs", 3);
					} else {
						npcsay(p, n, "I am sorry, I do need a more reliable messenger",
							"Can you send any friends my way?",
							"Preferably tall, dark and handsome");
					}
				}
				break;
			case 3:
				say(p, n, "I have passed on your message",
					"Now I go to Father Lawrence for help");
				npcsay(p, n, "Yes, he knows many things that can be done",
					"I hope you find him soon");
				break;
			case 4:
				say(p, n, "I found the Father. Now I seek the apothecary");
				npcsay(p, n,
					"I do not know where he lives",
					"but please, make haste. My father is close");
				break;
			case 5:
				say(p, n, "I have to get a potion made for you",
					"Not done that bit yet though. Still trying");
				npcsay(p, n, "Fair luck to you, the end is close");
				break;
			case 6:
				say(p, n, "I have a potion from Father Lawrence",
					"it should make you seem dead, and get you away from this place");
				p.message("You pass the potion to Juliet");
				p.getCarriedItems().remove(ItemId.CADAVA.id(), 1);
				npcsay(p, n,
					"Wonderful. I just hope Romeo can remember to get me from the Crypt",
					"Many thanks kind friend",
					"Please go to Romeo, make sure he understands",
					"He can be a bit dense sometimes");
				p.updateQuestStage(getQuestId(), 7);
				break;
			case 7:
				npcsay(p, n,
					"Have you seen Romeo? He will reward you for your help",
					"He is the wealth in this story", "I am just the glamour");
				break;
			case -1:
				npcsay(p,
					n,
					"I sat in that cold crypt for ages waiting for Romeo",
					"That useless fool never showed up",
					"And all I got was indigestion. I am done with men like him",
					"Now go away before I call my father!");
				break;
		}
	}

	public int messageCount(Player p) {
		if (!p.getCache().hasKey("romeo_juliet_msgs")) {
			return 0;
		} else {
			return p.getCache().getInt("romeo_juliet_msgs");
		}
	}

	private void lawrenceDialogue(Player p, Npc n) {
		switch (p.getQuestStage(this)) {
			case 0:
			case 1:
			case 2:
				int count = messageCount(p);
				if (p.getQuestStage(this) < 2 || count < 3) {
					npcsay(p, n, "Hello adventurer, do you seek a quest?");
					int choice = multi(p, n,
						"I am always looking for a quest",
						"No, I prefer just to kill things",
						"Can you recommend a good bar?");
					if (choice == 0) {
						npcsay(p,
							n,
							"Well, I see poor Romeo wandering around the square. I think he may need help",
							"I was helping him and Juliet to meet, but it became impossible",
							"I am sure he can use some help");
					} else if (choice == 1) {
						npcsay(p, n, "That's a fine career in these lands",
							"There is more that needs killing every day");
					} else if (choice == 2) {
						npcsay(p, n, "Drinking will be the death of you",
							"But the Blue Moon in the city is cheap enough",
							"And providing you buy one drink an hour they let you stay all night");
					}
				} else {
					npcsay(p, n, "Oh to be a father in the times of whiskey",
						"I sing and I drink and I wake up in gutters",
						"To err is human, to forgive, quite difficult");
				}
				break;
			case 3:
				say(p, n, "Romeo sent me. He says you can help");
				npcsay(p, n,
					"Ah Romeo, yes. A fine lad, but a little bit confused");
				say(p, n, "Juliet must be rescued from her fathers control");
				npcsay(p, n,
					"I know just the thing. A potion to make her appear dead",
					"Then Romeo can collect her from the crypt",
					"Go to the Apothecary, tell him I sent you",
					"You need some Cadava Potion");
				p.updateQuestStage(getQuestId(), 4);
				break;
			case 4:
				npcsay(p, n, "Ah, have you found the Apothecary yet?",
					"Remember, Cadava potion, for Father Lawrence");
				break;
			case 5:
				if (p.getCarriedItems().hasCatalogID(ItemId.MESSAGE.id(), Optional.of(false))) {
					npcsay(p, n, "Did you find the Apothecary?");
					say(p, n, "I am on my way back to him with the ingredients");
					npcsay(p, n, "Good work. Get the potion to Juliet when you have it",
						"I will tell Romeo to be ready");
				} else {
					npcsay(p, n, "Did you find the Apothecary?");
					say(p, n, "Yes, I must find some berries");
					npcsay(p, n, "Well, take care. They are poisonous to the touch",
						"You will need gloves");
				}
				break;
			case 6:
				npcsay(p, n, "Did you find the Apothecary?");
				say(p, n, "Yes, I must find some berries");
				npcsay(p, n, "Well, take care. They are poisonous to the touch",
					"You will need gloves");
				say(p, n, "I am on my way back to him with the ingredients");
				npcsay(p, n, "Good work. Get the potion to Juliet when you have it",
					"I will tell Romeo to be ready");
				break;
			case 7:
				npcsay(p, n, "Oh to be a father in the times of whiskey",
					"I sing and I drink and I wake up in gutters",
					"Top of the morning to you",
					"To err is human, to forgive, quite difficult");
				break;
			case -1:
				npcsay(p, n, "Oh to be a father in the times of whiskey",
					"I sing and I drink and I wake up in gutters",
					"I need a think I drink");
				break;
		}
	}

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.ROMEO.id()) {
			romeoDialogue(p, n);
		} else if (n.getID() == NpcId.JULIET.id()) {
			julietDialogue(p, n);
		} else if (n.getID() == NpcId.FATHER_LAWRENCE.id()) {
			lawrenceDialogue(p, n);
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.ROMEO.id() || n.getID() == NpcId.JULIET.id() || n.getID() == NpcId.FATHER_LAWRENCE.id();
	}
}
