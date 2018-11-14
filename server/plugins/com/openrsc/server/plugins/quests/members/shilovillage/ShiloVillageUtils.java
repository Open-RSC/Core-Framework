package com.openrsc.server.plugins.quests.members.shilovillage;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class ShiloVillageUtils implements DropListener, DropExecutiveListener, InvActionListener, InvActionExecutiveListener, InvUseOnItemListener, InvUseOnItemExecutiveListener, PickupListener, PickupExecutiveListener {

	public static void BUMPY_DIRT_HOLDER(Player p) {
		message(p, "that you might just be able to crawl through.");
		p.message("Do you want to try to crawl through the fissure?");
		int menu = showMenu(p,
				"Yes, I'll give it a go!",
				"No thanks, it looks a bit dark!");
		if(menu == 0) {
			p.message("You start to contort your body...");
			message(p, "through the small crack in the rock.",
					"With some dificulty you manage to push your body",
					"As you squeeze out of the hole...");
			p.message("you realise that there is a huge drop underneath you");
			p.message("You begin falling....");
			p.teleport(380, 3692);
			sleep(500);
			playerTalk(p, null, "Ahhhhh!");
			p.damage(1);
			p.message("Your body is battered as you hit the cavern walls.");
			playerTalk(p, null, "Ooooff!");
			p.damage(1);
			sleep(500);
			p.teleport(352, 3650);
			p.damage((int) (getCurrentLevel(p, HITS) * 0.2 + 10)); 
			message(p, "You hit the floor and it knocks the wind out of you!");
			playerTalk(p, null, "Ugghhhh!!");
			p.updateQuestStage(Constants.Quests.SHILO_VILLAGE, 3);
		} else if(menu == 1) {
			p.message("You think better of attempting to squeeze your body into the fissure.");
			playerTalk(p, null, "It looked very dangerous, and dark...",
					"scarey!");
		}
	}

	public static boolean succeed(Player player, int req) {
		int level_difference = getCurrentLevel(player, AGILITY) - req;
		int percent = random(1, 100);

		if(level_difference < 0)
			return true;
		if(level_difference >= 15)
			level_difference = 80;
		if(level_difference >= 20)
			level_difference = 90;
		else
			level_difference = 30 + level_difference;

		return percent <= level_difference;
	}

	// Zadimus: 589
	private void dropZadimusCorpse(Player p) {
		message(p, "You feel an uneartly compunction to bury this corpse!");
		if(p.getLocation().inBounds(445, 749, 449, 753)) {
			message(p, "You hear an unearthly moaning sound as you see",
					"an apparition materialises right in front of you.");
			Npc zadimus = spawnNpc(589, p.getX(), p.getY(), 60000);
			sleep(500);
			if(zadimus != null) {
				npcTalk(p, zadimus, "You have released me from my torture, and now I shall aid you");
				sleep(500);
				npcTalk(p, zadimus, "You seek to dispell the one who tortured and killed me");
				sleep(500);
				npcTalk(p, zadimus, "Remember this...");
				sleep(500);
				npcTalk(p, zadimus, "'I am the key, but only kin may approach her.'");
				message(p, "The apparition disapears into the ground where you buried the corpse.");
				zadimus.remove();
				message(p, "You see the ground in front of you shake ",
						"as a shard of bone forces its way to the surface.");
				p.message("You take the bone shard and place it in your inventory.");
				p.getInventory().replace(ShiloVillageObjects.ZADIMUS_CORPSE, 974);
				p.setQuestStage(Constants.Quests.SHILO_VILLAGE, 4);
			}
		} else {
			message(p, "You hear a ghostly wailing sound coming from the corpse",
					"and a whispering voice says,");
			p.message("'@yel@Zadimus: Let me rest in a sacred place and assist you I will'");
		}
	}

	@Override
	public boolean blockDrop(Player p, Item i) {
		if(i.getID() == ShiloVillageObjects.CRUMPLED_SCROLL) {
			return true;
		}
		if(i.getID() == ShiloVillageObjects.ZADIMUS_CORPSE) {
			return true;
		}
		if(i.getID() == ShiloVillageObjects.TATTERED_SCROLL) {
			return true;
		}
		if(i.getID() == 974) { // Bone shard
			return true;
		}
		if(i.getID() == 973) { // sword pommel
			return true;
		}
		if(i.getID() == 972) { // locating crystal
			return true;
		}
		if(i.getID() == 961) { // tomb notes
			return true;
		}
		if(i.getID() == 976) { // bone beads
			return true;
		}
		if(i.getID() == 852) { // beads of the dead - amulet
			return true;
		}
		if(i.getID() == 977) { // rashiliyia corpse
			return true;
		}
		return false;
	}
	//533
	@Override
	public void onDrop(Player p, Item i) {
		if(i.getID() == 977) { // rashiliyia corpse
			message(p, "The remains of Rashiliyia look quite delicate.",
					"You sense that a spirit needs to be put to rest.");
			p.message("Are you sure that you want to drop the remains ?");
			int menu = showMenu(p,
					"Yes, I am sure.",
					"No, I'll keep hold of the remains.");
			if(menu == 0) {
				if(p.getCache().hasKey("dolmen_zombie")) {
					p.getCache().remove("dolmen_zombie");
				}
				if(p.getCache().hasKey("dolmen_skeleton")) {
					p.getCache().remove("dolmen_skeleton");
				}
				if(p.getCache().hasKey("dolmen_ghost")) {
					p.getCache().remove("dolmen_ghost");
				}
				removeItem(p, 977, 1);
				message(p, "You drop Rashiliyias remains on the ground.",
						"The bones turn to dust and forms into the shape of a human figure.");
				Npc rash = spawnNpc(533, p.getX(), p.getY(), 30000);
				message(p,"The figure turns to you and you hear a cackling, croaky voice on the air.");
				if(rash != null) {
					npcTalk(p, rash, "Many thanks for releasing me!",
							"Please excuse me, I must attend to my plans!");
					rash.remove();
				}
				p.message("The figure turns and soars away quickly disapearing into the distance.");
			} else if(menu == 1) {
				p.message("You decide to keep hold of Rashiliyias remains.");
			}
		}
		if(i.getID() == 852) { // beads of the dead - amulet
			message(p, "Are you sure you want to drop the Beads of the Dead?");
			p.message("It looks very rare and unique.");
			int menu = showMenu(p,
					"Yes, I'm sure.",
					"Nope, I've had second thoughts.");
			if(menu == 0) {
				message(p, "As the necklace hits the floor, it disintigrates",
						"into a puff of white powder.");
				p.message("and you start to wonder if it ever really existed?");
				removeItem(p, 852, 1);
			} else if(menu == 1) {
				p.message("You decide not to drop the Beads of the Dead.");
			}
		}
		if(i.getID() == 976) { // bone beads
			message(p, "As the beads hit the floor, they disintegrate into");
			p.message("puffs of white powder.");
			removeItem(p, 976, 1);
		}
		if(i.getID() == 961) { // tomb notes
			p.message("As you drop the delicate scrolls onto the floor, they");
			p.message("disintegrate immediately.");
			if(!p.getCache().hasKey("dropped_writing")) {
				p.getCache().store("dropped_writing", true);
			}
			removeItem(p, 961, 1);
		}
		if(i.getID() == 972) { // locating crystal
			p.message("Are you sure you want to drop this crystal?");
			p.message("It looks very delicate and it may break.");
			int menu = showMenu(p,
					"Yes, I am sure.",
					"No, I've reconsidered, I'll keep it!");
			if(menu == 0) {
				message(p, "As you drop the cystal, it hits a rock and explodes.");
				p.message("You are lascerated by shards of glass.");
				p.damage(10);
				removeItem(p, 972, 1);
			} else if(menu == 1) {
				p.message("You decide to keep the Locating Crystal ");
				p.message("tucked into your inventory safe and sound.");
			}
		}
		if(i.getID() == 973) { // sword pommel
			message(p, "You drop the sword pommel on the floor.");
			p.message("It turns to dust as soon as it hits the ground.");
			removeItem(p, 973, 1);
		}
		if(i.getID() == 974) { // Bone shard
			p.message("You cannot bring yourself to drop this item.");
			p.message("You remember the words that Zadimus said when he appeared");
			p.message("in front of you.");
			p.message("@yel@'I am the key, but only kin may approach her.");
		}
		if(i.getID() == ShiloVillageObjects.CRUMPLED_SCROLL) {
			p.message("This looks quite important, are you sure you want to drop it?");
			int menu = showMenu(p,
					"Yes, I'm sure.",
					"No, I think I'll keep it.");
			if(menu == 0) {
				p.message("As you drop the item, it gets carried off by the wind.");
				p.message("never to be seen again.");
				removeItem(p, ShiloVillageObjects.CRUMPLED_SCROLL, 1);
			} else if(menu == 1) {
				p.message("You decide against throwing the item away.");
			}
		}
		if(i.getID() == ShiloVillageObjects.ZADIMUS_CORPSE) {
			dropZadimusCorpse(p);
		}
		if(i.getID() == ShiloVillageObjects.TATTERED_SCROLL) {
			p.message("This looks quite important, are you sure you want to drop it?");
			int menu = showMenu(p,
					"Yes, I'm sure.",
					"No, I think I'll keep it.");
			if(menu == 0) {
				p.message("You decide to throw the item away.");
				p.message("As you drop the item, it falls down a narrow crevice.");
				p.message("never to be seen again.");
				removeItem(p, ShiloVillageObjects.TATTERED_SCROLL, 1);
			} else if(menu == 1) {
				p.message("You decide against throwing the item away.");
			}
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player p) {
		if(item.getID() == ShiloVillageObjects.ZADIMUS_CORPSE) {
			return true;
		}
		if(item.getID() == ShiloVillageObjects.CRUMPLED_SCROLL) {
			return true;
		}
		if(item.getID() == ShiloVillageObjects.TATTERED_SCROLL) {
			return true;
		}
		if(item.getID() == 974) { // Bone shard
			return true;
		}
		if(item.getID() == 961) { // read tomb notes
			return true;
		}
		if(item.getID() == 972) { // activate crystal
			return true;
		}
		if(item.getID() == 835) { // bone key
			return true;
		}
		if(item.getID() == 977) { // rash corpse
			return true;
		}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if(item.getID() == 977) {
			p.message("Nothing interesting happens");
		}
		if(item.getID() == 835) { // bone key
			p.message("The key is intricately carved out of bone.");
		}
		if(item.getID() == 972) { // activate crystal
			message(p, "You feel the crystal trying to draw upon your spiritual energy.");
			p.message("Do you want to let it.");
			int menu = showMenu(p,
					"Yes, that seems fine.",
					"No, it sounds a bit dangerous.");
			if(menu == 0) {
				if(getCurrentLevel(p, PRAYER) < 10) {
					p.message("You have no spiritual energy that the crystal can draw from.");
					sleep(1200);
					p.message("You need to have at least 10 prayer points for it to work.");
					return;
				}
				int objectX = 351;
				if(objectX - p.getX() <= 5 && objectX - p.getX() >= -5) {
					p.message("The crystal blazes brilliantly.");
					p.getSkills().subtractLevel(PRAYER, 1);
				} else if(objectX - p.getX() <= 10 && objectX - p.getX() >= -10) {
					p.message("@red@The crystal glows brightly");
					p.getSkills().subtractLevel(PRAYER, 1);
				} else if(objectX - p.getX() <= 20 && objectX - p.getX() >= -20) {
					p.message("The crystal glows feintly");
					p.getSkills().subtractLevel(PRAYER, 1);
				} else {
					p.message("Nothing seems different about the Crystal.");
					p.getSkills().subtractLevel(PRAYER, 2);
				}
			} else if(menu == 1) {
				p.message("You decide not to allow the crystal to draw spiritual energy from your body.");
			}
		}
		if(item.getID() == 961) { // read tomb notes
			p.setBusy(true);
			p.message("This scroll is a collection of writings..");
			sleep(1200);
			p.message("Some of them are just scraps of papyrus with what looks like random scribblings.");
			sleep(1200);
			p.message("Which would you like to read?");
			sleep(1200);
			p.setBusy(false);
			int menu = showMenu(p,
					"Tattered Yellow papyrus",
					"Decayed White papyrus",
					"Crusty Orange papyrus");
			if(menu == 0) {
				ActionSender.sendBox(p, "...and rest like your mother who is silent in the peace of her "
						+ "tomb far to the North of Ah Za Rhoon. Near the sea, and under "
						+ "the hills deep in the underground to watch all of nature from the "
						+ "darkness of her final resting place.", false);
			} else if(menu == 1) {
				ActionSender.sendBox(p, "...Rashiliyia did so love objects of beauty. Her tomb was "
						+ "adnorned with crystals that glowed brightly when near to each other.", false);
			} else if(menu == 2) {
				ActionSender.sendBox(p, "...the sphere is activated when power of a spiritual nature is "
						+ "expended upon it, this can be very draining on the body...", false);
			}
		}
		if(item.getID() == 974) { // Bone shard
			p.message("The words of Zadimus come back to you.");
			p.message("@yel@'I am the key, but only kin may approach her.'");
		}
		if(item.getID() == ShiloVillageObjects.ZADIMUS_CORPSE) {
			dropZadimusCorpse(p);
		}
		if(item.getID() == ShiloVillageObjects.CRUMPLED_SCROLL) {
			message(p, "This looks like part of a scroll about Rashiliyia",
					"Would you like to read it?");
			int menu = showMenu(p,
					"Yes please!",
					"No thanks.");
			if(menu == 0) {
				ActionSender.sendBox(p, "Rashiliyia's rage went unchecked.% %"
						+ "She killed without mercy for revenge of her sons life.% %"
						+ "Like a spectre through the night she entered houses and one "
						+ "by one quietly strangled life from the occupants.% %"
						+ "It is said that only a handful survived, protected by necklace wards to keep the Witch Queen at bay.", true);
			} else if(menu == 1) {
				p.message("You decide to leave the scroll well alone.");
			}
		}
		if(item.getID() == ShiloVillageObjects.TATTERED_SCROLL) {
			message(p, "This looks like part of a scroll about someone called Berverius..");
			p.message("Would you like to read it?");
			int menu = showMenu(p,
					"Yes please.",
					"No thanks.");
			if(menu == 0) {
				ActionSender.sendBox(p, "Bervirius, song of King Danthalas, was killed in battle.% %"
						+ "His devout Mother Rashiliyia was so heartbroken that she% swore fealty to Zamorak "
						+ "if he would return her son to her.% %"
						+ "Bervirius returned as an undead creature and terrorized the "
						+ "King and Queen. Many guards died fighting the Undead "
						+ "Berverious, eventually the undead Bervirius was set on fire and "
						+ "soon only the bones remained.% %"
						+ "His remains were taken far to the South, and then towards the "
						+ "setting sun to a tomb that is surrounded by and level with the "
						+ "sea. The only remedy for containing the spirits of witches and undead.", true);
			} else if(menu == 1) {
				p.message("You decide not to open the scroll but instead put it carefully back into your inventory.");
			}
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
		//chisel and plommen sword
		if(item1.getID() == 167 && item2.getID() == 973 || item1.getID() == 973 && item2.getID() == 167) {
			return true;
		}
		//bone beads and bronze wire TODO
		if(item1.getID() == 976 && item2.getID() == 979 || item1.getID() == 979 && item2.getID() == 976) {
			return true;
		}
		//chisel and bone shard
		if(item1.getID() == 167 && item2.getID() == 974 || item1.getID() == 974 && item2.getID() == 167) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if(item1.getID() == 976 && item2.getID() == 979 || item1.getID() == 979 && item2.getID() == 976) {
			if(getCurrentLevel(p, CRAFTING) < 20) {
				p.message("You need a level of 20 Crafting to craft this.");
				return;
			}
			message(p, "You successfully craft the beads and Bronze Wire ");
			p.message("into a necklace which you name, 'Beads of the dead'");
			removeItem(p, 979, 1);
			p.getInventory().replace(976, 852);
		}
		if(item1.getID() == 167 && item2.getID() == 974 || item1.getID() == 974 && item2.getID() == 167) {
			if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == -1) {
				p.message("You're not quite sure what to make with this.");
				return;
			}
			if(p.getCache().hasKey("can_chisel_bone")) {
				if(getCurrentLevel(p, CRAFTING) < 20) {
					p.message("You need a level of 20 Crafting to craft this.");
					return;
				}
				message(p, "Remembering Zadimus' words and the strange bone lock,",
						"you start to craft the bone.");
				p.message("You succesfully make a key out of the bone shard.");
				p.getInventory().replace(974, 835);
				p.incExp(CRAFTING, 32, true);
			} else {
				message(p, "You're not quite sure what to make with this.");
				p.message("Perhaps it will come to you as you discover more about Rashiliyia?");
			}
		}
		if(item1.getID() == 167 && item2.getID() == 973 || item1.getID() == 973 && item2.getID() == 167) {
			if(getCurrentLevel(p, CRAFTING) < 20) {
				p.message("You need a level of 20 Crafting to craft this.");
				return;
			}
			message(p, "You prepare the ivory pommel and the chisel to start crafting...",
					"You successfully craft some of the ivory into beads.");
			p.message("They may look good as part of a necklace.");
			p.incExp(CRAFTING, 35, true);
			p.getInventory().replace(973, 976);
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if(i.getID() == 10 && i.getX() == 358 && i.getY() == 3626) {
			return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if(i.getID() == 10 && i.getX() == 358 && i.getY() == 3626) {
			if(p.getCache().hasKey("coins_shilo_cave")) {
				i.remove();
				addItem(p, 10, 10);
				p.message("The coins turn to dust in your hand...");
			} else {
				message(p, "As soon as you touch the coins...",
						"You hear the grinding sound of bones");
				p.message("against stone as you see skeletons and ");
				sleep(1000);
				p.message("Zombies rising up out of the ground.");
				spawnNpc(40, p.getX() - 1, p.getY() + 1, 60000);
				spawnNpc(40, p.getX() - 1, p.getY() - 1, 60000);

				spawnNpc(542, p.getX() + 2, p.getY() + 1, 60000);
				spawnNpc(542, p.getX() + 1, p.getY() - 1, 60000);
				p.message("The coins turn to dust in your hands.");
				p.getCache().store("coins_shilo_cave", true);
			}
		}
	}
}
