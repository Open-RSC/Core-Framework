package com.openrsc.server.plugins.authentic.quests.members.watchtower;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class WatchTowerMechanism implements UseLocTrigger, UseInvTrigger, UseNpcTrigger, TakeObjTrigger {

	private static final int TOBAN_CHEST_OPEN = 979;
	private static final int TOBAN_CHEST_CLOSED = 978;

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == TOBAN_CHEST_CLOSED && item.getCatalogId() == ItemId.KEY.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == TOBAN_CHEST_CLOSED && item.getCatalogId() == ItemId.KEY.id()) {
			openChest(obj, 2000, TOBAN_CHEST_OPEN);
			if (player.getCarriedItems().hasCatalogID(ItemId.STOLEN_GOLD.id(), Optional.empty())) {
				mes("You have already got the stolen gold");
				delay(3);
			} else {
				player.message("You find a stash of gold inside");
				mes("You take the gold");
				delay(3);
				give(player, ItemId.STOLEN_GOLD.id(), 1);
			}
			player.message("The chest springs shut");
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return ((item1.getCatalogId() == ItemId.OGRE_RELIC_PART_BODY.id() || item1.getCatalogId() == ItemId.OGRE_RELIC_PART_BASE.id() || item1.getCatalogId() == ItemId.OGRE_RELIC_PART_HEAD.id()) &&
				(item2.getCatalogId() == ItemId.OGRE_RELIC_PART_BODY.id() || item2.getCatalogId() == ItemId.OGRE_RELIC_PART_BASE.id() || item2.getCatalogId() == ItemId.OGRE_RELIC_PART_HEAD.id())) ||
			compareItemsIds(item1, item2, ItemId.BAT_BONES.id(), ItemId.VIAL.id());
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if ((item1.getCatalogId() == ItemId.OGRE_RELIC_PART_BODY.id() || item1.getCatalogId() == ItemId.OGRE_RELIC_PART_BASE.id() || item1.getCatalogId() == ItemId.OGRE_RELIC_PART_HEAD.id()) &&
				(item2.getCatalogId() == ItemId.OGRE_RELIC_PART_BODY.id() || item2.getCatalogId() == ItemId.OGRE_RELIC_PART_BASE.id() || item2.getCatalogId() == ItemId.OGRE_RELIC_PART_HEAD.id())) {
			player.message("I think these fit together, but I can't seem to make it fit");
			player.message("I am going to need someone with more experience to help me with this");
		} else if (compareItemsIds(item1, item2, ItemId.BAT_BONES.id(), ItemId.VIAL.id())) {
			player.message("The bat bones are to bulky to fit in the vial");
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return (npc.getID() == NpcId.WATCHTOWER_WIZARD.id() && item.getCatalogId() != ItemId.FINGERNAILS.id()) || (npc.getID() == NpcId.CITY_GUARD.id() && item.getCatalogId() == ItemId.DEATH_RUNE.id())
				|| (npc.getID() == NpcId.OGRE_GUARD_CAVE_ENTRANCE.id() && item.getCatalogId() == ItemId.NIGHTSHADE.id());
	}

	private boolean hasAllCrystals(Player player) {
		return player.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL1.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL2.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL3.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL4.id(), Optional.of(false));
	}

	private void lastCrystalChat(Player player, Npc n) {
		say(player, n, "This is the last one");
		npcsay(player, n, "Magnificent!",
			"At last you've brought all the crystals",
			"Now the shield generator can be activated again",
			"And once again Yanille will be safe",
			"From the threat of the ogres",
			"Throw the lever to activate the system...");
		if (player.getQuestStage(Quests.WATCHTOWER) == 9) {
			player.updateQuestStage(Quests.WATCHTOWER, 10);
		}
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == NpcId.WATCHTOWER_WIZARD.id()) {
			switch (ItemId.getById(item.getCatalogId())) {
				case POWERING_CRYSTAL1:
					if (player.getQuestStage(Quests.WATCHTOWER) == 10
						|| player.getQuestStage(Quests.WATCHTOWER) == -1) {
						npcsay(player, npc, "More crystals ?",
							"I don't need any more now...");
						return;
					}
					say(player, npc, "Wizard, look what I have found");
					npcsay(player, npc, "Well done! well done!",
						"That's a crystal found!",
						"You are clever",
						"Hold onto it until you have all four...");
					if (hasAllCrystals(player)) {
						lastCrystalChat(player, npc);
					} else {
						npcsay(player, npc, "Keep searching for the others",
							"If you've dropped any...",
							"Then you will need to go back to where you got it from");
					}
					break;
				case POWERING_CRYSTAL2:
					if (player.getQuestStage(Quests.WATCHTOWER) == 10
						|| player.getQuestStage(Quests.WATCHTOWER) == -1) {
						npcsay(player, npc, "More crystals ?",
							"I don't need any more now...");
						return;
					}
					say(player, npc, "Wizard, I have another crystal");
					npcsay(player, npc, "Superb!",
						"Keep up the good work",
						"Hold onto it until you have all four...");
					if (hasAllCrystals(player)) {
						lastCrystalChat(player, npc);
					} else {
						npcsay(player, npc, "Keep searching for the others",
							"If you've dropped any...",
							"Then you will need to go back to where you got it from");
					}
					break;
				case POWERING_CRYSTAL3:
					if (player.getQuestStage(Quests.WATCHTOWER) == 10
						|| player.getQuestStage(Quests.WATCHTOWER) == -1) {
						npcsay(player, npc, "More crystals ?",
							"I don't need any more now...");
						return;
					}
					say(player, npc, "Wizard, here is another crystal");
					npcsay(player, npc, "I must say i'm impressed",
						"May Saradomin speed you in finding them all",
						"Hold onto it until you have all four...");
					if (hasAllCrystals(player)) {
						lastCrystalChat(player, npc);
					} else {
						npcsay(player, npc, "Keep searching for the others",
							"If you've dropped any...",
							"Then you will need to go back to where you got it from");
					}
					break;
				case POWERING_CRYSTAL4:
					if (player.getQuestStage(Quests.WATCHTOWER) == 10
						|| player.getQuestStage(Quests.WATCHTOWER) == -1) {
						npcsay(player, npc, "More crystals ?",
							"I don't need any more now...");
						return;
					}
					npcsay(player, npc, "Well done! Well done!");
					if (hasAllCrystals(player)) {
						lastCrystalChat(player, npc);
					} else {
						npcsay(player, npc, "Keep searching for the others",
							"If you've dropped any...",
							"Then you will need to go back to where you got it from");
						// authentic only first time showing him without the other crystals triggered new dialogue
						// on talk
						if (!player.getCache().hasKey("crystal_rock")) {
							player.getCache().store("crystal_rock", true);
						}
					}
					break;
				case OGRE_RELIC_PART_BODY:
					say(player, npc, "I had this given to me");
					if (player.getCache().hasKey("wizard_relic_part_1")
						|| player.getQuestStage(Quests.WATCHTOWER) == 10
						|| player.getQuestStage(Quests.WATCHTOWER) == -1) {
						npcsay(player, npc, "I already have that part...");
					} else {
						player.getCarriedItems().remove(new Item(ItemId.OGRE_RELIC_PART_BODY.id()));
						npcsay(player, npc, "It's part of an ogre relic");
						player.getCache().store("wizard_relic_part_1", true);
						relicParts(player, npc);
					}
					break;
				case OGRE_RELIC_PART_BASE:
					say(player, npc, "I got given this by an ogre");
					if (player.getCache().hasKey("wizard_relic_part_2")
						|| player.getQuestStage(Quests.WATCHTOWER) == 10
						|| player.getQuestStage(Quests.WATCHTOWER) == -1) {
						npcsay(player, npc, "I already have that part...");
					} else {
						player.getCarriedItems().remove(new Item(ItemId.OGRE_RELIC_PART_BASE.id()));
						npcsay(player, npc, "Good good,a part of an ogre relic");
						player.getCache().store("wizard_relic_part_2", true);
						relicParts(player, npc);
					}
					break;
				case OGRE_RELIC_PART_HEAD:
					say(player, npc, "An ogre gave me this");
					if (player.getCache().hasKey("wizard_relic_part_3")
						|| player.getQuestStage(Quests.WATCHTOWER) == 10
						|| player.getQuestStage(Quests.WATCHTOWER) == -1) {
						npcsay(player, npc, "I already have that part...");
					} else {
						player.getCarriedItems().remove(new Item(ItemId.OGRE_RELIC_PART_HEAD.id()));
						npcsay(player, npc, "Ah, it's part of an old ogre statue");
						player.getCache().store("wizard_relic_part_3", true);
						relicParts(player, npc);
					}
					break;
				case OGRE_RELIC:
					say(player, npc, "What is this ?");
					npcsay(player, npc, "It is the ogre statue I finished for you...");
					break;
				case VIAL:
					npcsay(player, npc, "Oh lovely, fresh water...thanks!");
					player.getCarriedItems().remove(new Item(ItemId.VIAL.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.EMPTY_VIAL.id()));
					break;
				case SKAVID_MAP:
					player.message("You give the map to the wizard");
					npcsay(player, npc, "Well well! a map!",
						"Indeed this shows the paths into the skavid caves",
						"I suggest you search these now...");
					break;
				case UNFINISHED_OGRE_POTION:
					npcsay(player, npc, "No no, the potion is not complete yet...");
					break;
				case OGRE_POTION:
					if (player.getQuestStage(Quests.WATCHTOWER) == -1) {
						npcsay(player, npc, "Another potion ?",
							"Ooo no, I don't think so...",
							"I can't let you use this anymore, it is just too dangerous",
							"I'd better take it from you before you injure yourself");
						player.getCarriedItems().remove(new Item(ItemId.OGRE_POTION.id()));
						return;
					}
					say(player, npc, "Yes I have made the potion");
					npcsay(player, npc, "That's great news, let me infuse it with magic...");
					player.message("The wizard mutters strange words over the liquid");
					player.getCarriedItems().remove(new Item(ItemId.OGRE_POTION.id()));
					give(player, ItemId.MAGIC_OGRE_POTION.id(), 1);
					npcsay(player, npc, "Here it is, a dangerous substance",
						"I must remind you that this potion can only be used",
						"If your magic ability is high enough");
					if (player.getQuestStage(Quests.WATCHTOWER) == 7) {
						player.updateQuestStage(Quests.WATCHTOWER, 8);
					}
					break;
				case MAGIC_OGRE_POTION:
					npcsay(player, npc, "Yes that is the potion I enchanted for you",
						"Go and use it now...");
					break;
				case ARMOUR:
				case WATCH_TOWER_EYE_PATCH:
				case ROBE:
				case DAGGER:
				case GOBLIN_ARMOUR:
				case EYE_PATCH:
				case IRON_DAGGER:
				case WIZARDS_ROBE:
					if (player.getQuestStage(Quests.WATCHTOWER) != 1) {
						player.message("The wizard has no need for more evidence");
						return;
					}
					if (item.getCatalogId() == ItemId.EYE_PATCH.id()) {
						say(player, npc, "I found this eye patch");
					} else if (item.getCatalogId() == ItemId.GOBLIN_ARMOUR.id()) {
						say(player, npc, "Have a look at this goblin armour");
					} else if (item.getCatalogId() == ItemId.IRON_DAGGER.id()) {
						say(player, npc, "I found a dagger");
					} else if (item.getCatalogId() == ItemId.WIZARDS_ROBE.id()) {
						say(player, npc, "I have this robe");
					}
					npcsay(player, npc, "Let me see...",
						"No, sorry this is not evidence",
						"You need to keep searching im afraid");
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
		else if (npc.getID() == NpcId.CITY_GUARD.id() && item.getCatalogId() == ItemId.DEATH_RUNE.id()) {
			if ((player.getCache().hasKey("city_guard_riddle") && player.getCache().getBoolean("city_guard_riddle"))
				|| player.getQuestStage(Quests.WATCHTOWER) == -1) {
				player.message("The guard is not listening to you");
			} else {
				player.getCarriedItems().remove(new Item(ItemId.DEATH_RUNE.id()));
				give(player, ItemId.SKAVID_MAP.id(), 1);
				if (player.getQuestStage(Quests.WATCHTOWER) == 3) {
					player.updateQuestStage(Quests.WATCHTOWER, 4);
				}
				// player solved the riddle
				player.getCache().store("city_guard_riddle", true);
				say(player, npc, "I worked it out!");
				npcsay(player, npc, "Well well.. the imp has done it!",
					"Thanks for the rune",
					"This is what you be needing...");
				player.message("The guard gives you a map");
			}
		}
		else if (npc.getID() == NpcId.OGRE_GUARD_CAVE_ENTRANCE.id() && item.getCatalogId() == ItemId.NIGHTSHADE.id()) {
			if ((player.getQuestStage(Quests.WATCHTOWER) >= 0 && player.getQuestStage(Quests.WATCHTOWER) < 5) ||
				(player.getQuestStage(Quests.WATCHTOWER) == -1 &&
				!config().LOCKED_POST_QUEST_REGIONS_ACCESSIBLE)) {
				player.message("The guard is occupied at the moment");
			} else {
				player.playerServerMessage(MessageType.QUEST, "You give the guard some nightshade");
				player.getCarriedItems().remove(new Item(ItemId.NIGHTSHADE.id()));
				npcsay(player, npc, "What is this!!!",
					"Arrrrgh! I cannot stand this plant!",
					"Ahhh, it burns! it burns!!!");
				player.message("You run past the guard while he's busy...");
				player.teleport(647, 3644);
			}
		}
	}

	private void relicParts(Player player, Npc n) {
		if (player.getCache().hasKey("wizard_relic_part_1") && player.getCache().hasKey("wizard_relic_part_2") && player.getCache().hasKey("wizard_relic_part_3")) {
			npcsay(player, n, "Excellent! that seems to be all the pieces",
				"Now I can assemble it...",
				"Hmm, yes it is as I thought...",
				"A statue symbolising an ogre warrior of old",
				"Well, if you ever wanted to make friends with an ogre",
				"Then this is the item to have!");
			player.message("The wizard gives you a complete statue");
			give(player, ItemId.OGRE_RELIC.id(), 1);
			if (player.getQuestStage(Quests.WATCHTOWER) == 2) {
				player.updateQuestStage(Quests.WATCHTOWER, 3);
			}
		} else {
			npcsay(player, n, "There may be more parts to find...",
				"I'll keep this for later");
		}

	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return inArray(i.getID(), ItemId.SHAMAN_ROBE.id(), ItemId.POWERING_CRYSTAL1.id(), ItemId.POWERING_CRYSTAL2.id(),
				ItemId.POWERING_CRYSTAL3.id(), ItemId.POWERING_CRYSTAL4.id());
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.SHAMAN_ROBE.id()) {
			player.message("You take the robe");
			give(player, ItemId.SHAMAN_ROBE.id(), 1);
			i.remove();
			if (player.getQuestStage(Quests.WATCHTOWER) == 5) {
				player.updateQuestStage(Quests.WATCHTOWER, 6);
			}
		}
		else if (i.getID() == ItemId.POWERING_CRYSTAL1.id() || i.getID() == ItemId.POWERING_CRYSTAL2.id()
				|| i.getID() == ItemId.POWERING_CRYSTAL3.id() || i.getID() == ItemId.POWERING_CRYSTAL4.id()) {
			if (player.getQuestStage(Quests.WATCHTOWER) == -1 || i.getLocation().isInWatchtowerPedestal()) {
				mes("You try and take the crystal but its stuck solid!");
				delay(3);
				mes("You feel magic power coursing through the crystal...");
				delay(3);
				mes("The force renews your magic level");
				delay(3);
				int maxMagic = getMaxLevel(player, Skill.MAGIC.id());
				boolean sendUpdate = player.getClientLimitations().supportsSkillUpdate;
				if (getCurrentLevel(player, Skill.MAGIC.id()) < maxMagic) {
					player.getSkills().setLevel(Skill.MAGIC.id(), maxMagic, sendUpdate);
					if (!sendUpdate) {
						player.getSkills().sendUpdateAll();
					}
				}
			} else {
				player.message("You take the crystal");
				give(player, i.getID(), 1);
				i.remove();
			}
		}
	}
}
