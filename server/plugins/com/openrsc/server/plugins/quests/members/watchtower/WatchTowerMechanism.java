package com.openrsc.server.plugins.quests.members.watchtower;

import com.openrsc.server.constants.*;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class WatchTowerMechanism implements UseLocTrigger, UseInvTrigger, UseNpcTrigger, TakeObjTrigger {

	private static final int TOBAN_CHEST_OPEN = 979;
	private static final int TOBAN_CHEST_CLOSED = 978;

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return obj.getID() == TOBAN_CHEST_CLOSED && item.getCatalogId() == ItemId.KEY.id();
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == TOBAN_CHEST_CLOSED && item.getCatalogId() == ItemId.KEY.id()) {
			openChest(obj, 2000, TOBAN_CHEST_OPEN);
			if (p.getCarriedItems().hasCatalogID(ItemId.STOLEN_GOLD.id(), Optional.empty())) {
				Functions.mes(p, "You have already got the stolen gold");
			} else {
				p.message("You find a stash of gold inside");
				Functions.mes(p, "You take the gold");
				give(p, ItemId.STOLEN_GOLD.id(), 1);
			}
			p.message("The chest springs shut");
		}
	}

	@Override
	public boolean blockUseInv(Player p, Item item1, Item item2) {
		return ((item1.getCatalogId() == ItemId.OGRE_RELIC_PART_BODY.id() || item1.getCatalogId() == ItemId.OGRE_RELIC_PART_BASE.id() || item1.getCatalogId() == ItemId.OGRE_RELIC_PART_HEAD.id()) &&
				(item2.getCatalogId() == ItemId.OGRE_RELIC_PART_BODY.id() || item2.getCatalogId() == ItemId.OGRE_RELIC_PART_BASE.id() || item2.getCatalogId() == ItemId.OGRE_RELIC_PART_HEAD.id())) ||
			Functions.compareItemsIds(item1, item2, ItemId.BAT_BONES.id(), ItemId.VIAL.id());
	}

	@Override
	public void onUseInv(Player p, Item item1, Item item2) {
		if ((item1.getCatalogId() == ItemId.OGRE_RELIC_PART_BODY.id() || item1.getCatalogId() == ItemId.OGRE_RELIC_PART_BASE.id() || item1.getCatalogId() == ItemId.OGRE_RELIC_PART_HEAD.id()) &&
				(item2.getCatalogId() == ItemId.OGRE_RELIC_PART_BODY.id() || item2.getCatalogId() == ItemId.OGRE_RELIC_PART_BASE.id() || item2.getCatalogId() == ItemId.OGRE_RELIC_PART_HEAD.id())) {
			p.message("I think these fit together, but I can't seem to make it fit");
			p.message("I am going to need someone with more experience to help me with this");
		} else if (Functions.compareItemsIds(item1, item2, ItemId.BAT_BONES.id(), ItemId.VIAL.id())) {
			p.message("The bat bones are to bulky to fit in the vial");
		}
	}

	@Override
	public boolean blockUseNpc(Player p, Npc npc, Item item) {
		return npc.getID() == NpcId.WATCHTOWER_WIZARD.id() || (npc.getID() == NpcId.CITY_GUARD.id() && item.getCatalogId() == ItemId.DEATH_RUNE.id())
				|| (npc.getID() == NpcId.OGRE_GUARD_CAVE_ENTRANCE.id() && item.getCatalogId() == ItemId.NIGHTSHADE.id());
	}

	private void lastCrystalChat(Player p, Npc n) {
		say(p, n, "This is the last one");
		npcsay(p, n, "Magnificent!",
			"At last you've brought all the crystals",
			"Now the shield generator can be activated again",
			"And once again Yanille will be safe",
			"From the threat of the ogres",
			"Throw the lever to activate the system...");
		if (p.getQuestStage(Quests.WATCHTOWER) == 9) {
			p.updateQuestStage(Quests.WATCHTOWER, 10);
		}
	}

	@Override
	public void onUseNpc(Player p, Npc npc, Item item) {
		if (npc.getID() == NpcId.WATCHTOWER_WIZARD.id()) {
			if (p.getQuestStage(Quests.WATCHTOWER) == -1) {
				p.message("The wizard has no need for more evidence");
				return;
			}
			switch (ItemId.getById(item.getCatalogId())) {
				case POWERING_CRYSTAL1:
					if (p.getQuestStage(Quests.WATCHTOWER) == 10) {
						npcsay(p, npc, "More crystals ?",
							"I don't need any more now...");
						return;
					}
					say(p, npc, "Wizard, look what I have found");
					npcsay(p, npc, "Well done! well done!",
						"That's a crystal found!",
						"You are clever",
						"Hold onto it until you have all four...");
					if (p.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL4.id(), Optional.of(false))) {
						lastCrystalChat(p, npc);
					} else {
						npcsay(p, npc, "Keep searching for the others",
							"If you've dropped any...",
							"Then you will need to go back to where you got it from");
					}
					break;
				case POWERING_CRYSTAL2:
					if (p.getQuestStage(Quests.WATCHTOWER) == 10) {
						npcsay(p, npc, "More crystals ?",
							"I don't need any more now...");
						return;
					}
					say(p, npc, "Wizard, I have another crystal");
					npcsay(p, npc, "Superb!",
						"Keep up the good work",
						"Hold onto it until you have all four...");
					if (p.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL4.id(), Optional.of(false))) {
						lastCrystalChat(p, npc);
					} else {
						npcsay(p, npc, "Keep searching for the others",
							"If you've dropped any...",
							"Then you will need to go back to where you got it from");
					}
					break;
				case POWERING_CRYSTAL3:
					if (p.getQuestStage(Quests.WATCHTOWER) == 10) {
						npcsay(p, npc, "More crystals ?",
							"I don't need any more now...");
						return;
					}
					say(p, npc, "Wizard, here is another crystal");
					npcsay(p, npc, "I must say i'm impressed",
						"May Saradomin speed you in finding them all",
						"Hold onto it until you have all four...");
					if (p.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL4.id(), Optional.of(false))) {
						lastCrystalChat(p, npc);
					} else {
						npcsay(p, npc, "Keep searching for the others",
							"If you've dropped any...",
							"Then you will need to go back to where you got it from");
					}
					break;
				case POWERING_CRYSTAL4:
					if (p.getQuestStage(Quests.WATCHTOWER) == 10) {
						npcsay(p, npc, "More crystals ?",
							"I don't need any more now...");
						return;
					}
					lastCrystalChat(p, npc);
					break;
				case OGRE_RELIC_PART_BODY:
					say(p, npc, "I had this given to me");
					if (!p.getCache().hasKey("wizard_relic_part_1")) {
						p.getCarriedItems().remove(new Item(ItemId.OGRE_RELIC_PART_BODY.id()));
						npcsay(p, npc, "It's part of an ogre relic");
						p.getCache().store("wizard_relic_part_1", true);
						relicParts(p, npc);
					} else {
						npcsay(p, npc, "I already have that part...");
					}
					break;
				case OGRE_RELIC_PART_BASE:
					say(p, npc, "I got given this by an ogre");
					if (!p.getCache().hasKey("wizard_relic_part_2")) {
						p.getCarriedItems().remove(new Item(ItemId.OGRE_RELIC_PART_BASE.id()));
						npcsay(p, npc, "Good good,a part of an ogre relic");
						p.getCache().store("wizard_relic_part_2", true);
						relicParts(p, npc);
					} else {
						npcsay(p, npc, "I already have that part...");
					}
					break;
				case OGRE_RELIC_PART_HEAD:
					say(p, npc, "An ogre gave me this");
					if (!p.getCache().hasKey("wizard_relic_part_3")) {
						p.getCarriedItems().remove(new Item(ItemId.OGRE_RELIC_PART_HEAD.id()));
						npcsay(p, npc, "Ah, it's part of an old ogre statue");
						p.getCache().store("wizard_relic_part_3", true);
						relicParts(p, npc);
					} else {
						npcsay(p, npc, "I already have that part...");
					}
					break;
				case OGRE_RELIC:
					say(p, npc, "What is this ?");
					npcsay(p, npc, "It is the ogre statue I finished for you...");
					break;
				case VIAL:
					npcsay(p, npc, "Oh lovely, fresh water...thanks!");
					p.getCarriedItems().getInventory().replace(ItemId.VIAL.id(), ItemId.EMPTY_VIAL.id());
					break;
				case SKAVID_MAP:
					p.message("You give the map to the wizard");
					npcsay(p, npc, "Well well! a map!",
						"Indeed this shows the paths into the skavid caves",
						"I suggest you search these now...");
					break;
				case OGRE_POTION:
					say(p, npc, "Yes I have made the potion");
					npcsay(p, npc, "That's great news, let me infuse it with magic...");
					p.message("The wizard mutters strange words over the liquid");
					p.getCarriedItems().remove(new Item(ItemId.OGRE_POTION.id()));
					give(p, ItemId.MAGIC_OGRE_POTION.id(), 1);
					npcsay(p, npc, "Here it is, a dangerous substance",
						"I must remind you that this potion can only be used",
						"If your magic ability is high enough");
					if (p.getQuestStage(Quests.WATCHTOWER) == 7) {
						p.updateQuestStage(Quests.WATCHTOWER, 8);
					}
					break;
				case MAGIC_OGRE_POTION:
					npcsay(p, npc, "Yes that is the potion I enchanted for you",
						"Go and use it now...");
					break;
				default:
					p.message("Nothing interesting happens");
					break;
			}
		}
		else if (npc.getID() == NpcId.CITY_GUARD.id() && item.getCatalogId() == ItemId.DEATH_RUNE.id()) {
			if (p.getCache().hasKey("city_guard_riddle") || p.getQuestStage(Quests.WATCHTOWER) == -1) {
				p.message("The guard is not listening to you");
			} else {
				p.getCarriedItems().remove(new Item(ItemId.DEATH_RUNE.id()));
				give(p, ItemId.SKAVID_MAP.id(), 1);
				if (p.getQuestStage(Quests.WATCHTOWER) == 3) {
					p.updateQuestStage(Quests.WATCHTOWER, 4);
				}
				p.getCache().store("city_guard_riddle", true);
				say(p, npc, "I worked it out!");
				npcsay(p, npc, "Well well.. the imp has done it!",
					"Thanks for the rune",
					"This is what you be needing...");
				p.message("The guard gives you a map");
			}
		}
		else if (npc.getID() == NpcId.OGRE_GUARD_CAVE_ENTRANCE.id() && item.getCatalogId() == ItemId.NIGHTSHADE.id()) {
			if ((p.getQuestStage(Quests.WATCHTOWER) >= 0 && p.getQuestStage(Quests.WATCHTOWER) < 5) ||
				(p.getQuestStage(Quests.WATCHTOWER) == -1 &&
				!p.getWorld().getServer().getConfig().LOCKED_POST_QUEST_REGIONS_ACCESSIBLE)) {
				p.message("The guard is occupied at the moment");
			} else {
				p.playerServerMessage(MessageType.QUEST, "You give the guard some nightshade");
				p.getCarriedItems().remove(new Item(ItemId.NIGHTSHADE.id()));
				npcsay(p, npc, "What is this!!!",
					"Arrrrgh! I cannot stand this plant!",
					"Ahhh, it burns! it burns!!!");
				p.message("You run past the guard while he's busy...");
				p.teleport(647, 3644);
			}
		}
	}

	private void relicParts(Player p, Npc n) {
		if (p.getCache().hasKey("wizard_relic_part_1") && p.getCache().hasKey("wizard_relic_part_2") && p.getCache().hasKey("wizard_relic_part_3")) {
			npcsay(p, n, "Excellent! that seems to be all the pieces",
				"Now I can assemble it...",
				"Hmm, yes it is as I thought...",
				"A statue symbolising an ogre warrior of old",
				"Well, if you ever wanted to make friends with an ogre",
				"Then this is the item to have!");
			p.message("The wizard gives you a complete statue");
			give(p, ItemId.OGRE_RELIC.id(), 1);
			if (p.getQuestStage(Quests.WATCHTOWER) == 2) {
				p.updateQuestStage(Quests.WATCHTOWER, 3);
			}
		} else {
			npcsay(p, n, "There may be more parts to find...",
				"I'll keep this for later");
		}

	}

	@Override
	public boolean blockTakeObj(Player p, GroundItem i) {
		return inArray(i.getID(), ItemId.SHAMAN_ROBE.id(), ItemId.POWERING_CRYSTAL1.id(), ItemId.POWERING_CRYSTAL2.id(),
				ItemId.POWERING_CRYSTAL3.id(), ItemId.POWERING_CRYSTAL4.id());
	}

	@Override
	public void onTakeObj(Player p, GroundItem i) {
		if (i.getID() == ItemId.SHAMAN_ROBE.id()) {
			p.message("You take the robe");
			give(p, ItemId.SHAMAN_ROBE.id(), 1);
			i.remove();
			if (p.getQuestStage(Quests.WATCHTOWER) == 5) {
				p.updateQuestStage(Quests.WATCHTOWER, 6);
			}
		}
		else if (i.getID() == ItemId.POWERING_CRYSTAL1.id() || i.getID() == ItemId.POWERING_CRYSTAL2.id()
				|| i.getID() == ItemId.POWERING_CRYSTAL3.id() || i.getID() == ItemId.POWERING_CRYSTAL4.id()) {
			if (p.getQuestStage(Quests.WATCHTOWER) == -1) {
				Functions.mes(p, "You try and take the crystal but its stuck solid!",
					"You feel magic power coursing through the crystal...",
					"The force renews your magic level");
				int maxMagic = getMaxLevel(p, Skills.MAGIC);
				if (getCurrentLevel(p, Skills.MAGIC) < maxMagic) {
					p.getSkills().setLevel(Skills.MAGIC, maxMagic);
				}
			} else {
				p.message("You take the crystal");
				give(p, i.getID(), 1);
				i.remove();
			}
		}
	}
}
