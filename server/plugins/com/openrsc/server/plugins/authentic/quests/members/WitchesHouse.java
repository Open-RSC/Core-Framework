package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class WitchesHouse implements QuestInterface, TalkNpcTrigger,
	OpBoundTrigger,
	OpLocTrigger,
	DropObjTrigger,
	UseNpcTrigger,
	KillNpcTrigger,
	TakeObjTrigger,
	AttackNpcTrigger {

	/**
	 * INFORMATION Rat appears on coords: 356, 494 Dropping cheese in the whole
	 * room and rat appears on the same coord Rat is never removed until you
	 * use magnet room inbounds : MIN X: 356 MAX X: 357 MIN Y: 494 MAX Y: 496
	 */

	private static final int WITCHES_HOUSE_CUPBOARD_OPEN = 259;
	private static final int WITCHES_HOUSE_CUPBOARD_CLOSED = 258;

	@Override
	public int getQuestId() {
		return Quests.WITCHS_HOUSE;
	}

	@Override
	public String getQuestName() {
		return "Witch's house (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.WITCHS_HOUSE.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the Witches house quest");
		final QuestReward reward = Quest.WITCHS_HOUSE.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		player.getCache().remove("witch_gone");
		player.getCache().remove("shapeshifter");
		player.getCache().remove("found_magnet");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BOY.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.BOY.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "Hello young man");
					mes("The boy sobs");
					delay(3);
					int first = multi(player, n, "What's the matter?",
						"Well if you're not going to answer, I'll go");
					if (first == 0) {
						npcsay(player, n,
							"I've kicked my ball over that wall, into that garden",
							"The old lady who lives there is scary",
							"She's locked the ball in her wooden shed",
							"Can you get my ball back for me please");
						int second = multi(player, n, false, //do not send over
							"Ok, I'll see what I can do",
							"Get it back yourself");
						if (second == 0) {
							say(player, n, "Ok I'll see what I can do");
							npcsay(player, n, "Thankyou");
							player.updateQuestStage(getQuestId(), 1);
						} else if (second == 1) {
							// NOTHING
							say(player, n, "Get it back yourself");
						}
					} else if (first == 1) {
						mes("The boy sniffs slightly");
						delay(3);
					}
					break;
				case 1:
				case 2:
				case 3:
					if (player.getCarriedItems().hasCatalogID(ItemId.BALL.id(), Optional.of(false))) {
						say(player, n, "Hi I have got your ball back",
							"It was harder than I thought it would be");
						npcsay(player, n, "Thankyou very much");
						player.getCarriedItems().remove(new Item(ItemId.BALL.id()));
						if (player.getQuestStage(Quests.WITCHS_HOUSE) == 3) {
							player.sendQuestComplete(Quests.WITCHS_HOUSE);
						}
					} else {
						npcsay(player, n, "Have you got my ball back yet?");
						say(player, n, "Not yet");
						npcsay(player, n, "Well it's in the shed in that garden");
					}
					break;
				case -1:
					npcsay(player, n, "Thankyou for getting my ball back");
					break;
			}
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == 69 || (obj.getID() == 70 && obj.getX() == 358) || (obj.getID() == 71 && obj.getY() == 495)
				|| (obj.getID() == 73 && obj.getX() == 351) || (obj.getID() == 72 && obj.getX() == 356);
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 69) {
			player.message("The door is locked");
		}
		else if (obj.getID() == 70 && obj.getX() == 358) {
			doDoor(obj, player);
		}
		else if (obj.getID() == 71 && obj.getY() == 495) {
			if (player.getCache().hasKey("witch_spawned") && player.getQuestStage(getQuestId()) == 2) {
				// inside house for some reason
				if (player.getX() > 355) {
					doDoor(obj, player);
				} else {
					Npc witch = ifnearvisnpc(player, NpcId.NORA_T_HAG.id(), 5);
					if (witch != null) {
						witch.teleport(355, 494);

						npcsay(player, witch, "Oi what are you doing in my garden?");
						npcsay(player, witch, "Get out you pesky intruder");
						mes("Nora begins to cast a spell");
						delay(3);

						player.teleport(347, 616, true);
						delnpc(witch, false);

						player.getCache().remove("witch_spawned");
						player.updateQuestStage(this, 1);
						player.getCache().remove("found_magnet");
					} else {
						doDoor(obj, player);
						player.getCache().remove("witch_spawned");
					}
				}
				return;
			}
			if (player.getQuestStage(this) > 1 || player.getQuestStage(getQuestId()) == -1 || player.getX() == 355) {
				doDoor(obj, player);
			} else {
				player.message("The door won't open");
			}
		}

		else if (obj.getID() == 73 && obj.getX() == 351) {
			if (player.getQuestStage(this) == 3 || player.getQuestStage(getQuestId()) == -1) {
				doDoor(obj, player);
				return;
			} else if (player.getQuestStage(this) < 2) {
				mes("The shed door is locked");
				delay(3);
				return;
			}
			Npc witch = ifnearvisnpc(player, NpcId.NORA_T_HAG.id(), 10);
			// first time or witch went away
			if (!player.getCache().hasKey("witch_spawned") || witch == null) {
				mes("As you reach out to open the door you hear footsteps inside the house");
				delay(3);
				mes("The footsteps approach the back door");
				delay(3);
				addnpc(player.getWorld(), NpcId.NORA_T_HAG.id(), 356, 495, 60000);
				if (!player.getCache().hasKey("witch_spawned")) {
					player.getCache().store("witch_spawned", true);
				}
			} else {
				mes("The shed door is locked");
				delay(3);
				witch.teleport(355, 494);
				npcsay(player, witch, "Oi what are you doing in my garden?");
				npcsay(player, witch, "Get out you pesky intruder");
				mes("Nora begins to cast a spell");
				delay(3);

				player.teleport(347, 616, true);
				delnpc(witch, false);
				player.updateQuestStage(this, 1);
				player.getCache().remove("found_magnet");
			}
		}
		else if (obj.getID() == 72 && obj.getX() == 356) {
			boolean fromGarden = player.getX() <= 355;
			doDoor(obj, player);
			Npc witch = ifnearvisnpc(player, NpcId.NORA_T_HAG.id(), 5);
			if (fromGarden && player.getCache().hasKey("witch_spawned") && witch != null) {
				//witch.setBusy(true);
				delay(3);
				player.message("Through a crack in the door, you see a witch enter the garden");
				witch.teleport(353, 492);
				delay(4);
				witch.teleport(351, 491);
				player.message("The witch disappears into the shed");
				npcsay(player, witch, "How are you tonight my pretty?",
					"Would you like some food?",
					"Just wait there while I get some");
				witch.teleport(353, 492);
				witch.setLocation(Point.location(353, 492), true);
				mes("The witch passes  back through the garden again");
				delay(3);
				mes("Leaving the shed door unlocked");
				delay(3);

				delnpc(witch, false);
				player.getCache().remove("witch_spawned");

				player.updateQuestStage(this, 3);
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 255 || (obj.getID() == 256 && obj.getX() == 363)
				|| ((obj.getID() == WITCHES_HOUSE_CUPBOARD_OPEN || obj.getID() == WITCHES_HOUSE_CUPBOARD_CLOSED) && obj.getY() == 3328);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 255) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.FRONT_DOOR_KEY.id(), Optional.of(false))) {
				player.message("You find a key under the mat");
				give(player, ItemId.FRONT_DOOR_KEY.id(), 1);
			} else {
				player.message("You find nothing interesting");
			}
		}
		else if (obj.getID() == 256 && obj.getX() == 363) {
			boolean shouldShock = false;
			if (wearingMetalArmour(player)) {
				player.message("As your metal armour touches the gate you feel a shock");
				shouldShock = true;
			} else if (!wearingInsulatingGloves(player)) {
				player.message("As your bare hands touch the gate you feel a shock");
				shouldShock = true;
			}
			if (shouldShock) {
				int damage;
				if (player.getSkills().getLevel(Skill.HITS.id()) < 20) {
					damage = DataConversions.getRandom().nextInt(9) + 1;
				} else {
					damage = DataConversions.getRandom().nextInt(14) + 1;
				}
				player.damage(damage);
			} else {
				doGate(player, obj);
			}
		}
		else if ((obj.getID() == WITCHES_HOUSE_CUPBOARD_OPEN || obj.getID() == WITCHES_HOUSE_CUPBOARD_CLOSED) && obj.getY() == 3328) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, player, WITCHES_HOUSE_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, player, WITCHES_HOUSE_CUPBOARD_CLOSED);
			} else {
				if (!player.getCarriedItems().hasCatalogID(ItemId.MAGNET.id(), Optional.of(false))) {
					player.message("You find a magnet in the cupboard");
					give(player, ItemId.MAGNET.id(), 1);
					if (player.getQuestStage(this) > 0) {
						player.getCache().store("found_magnet", true);
					}
				} else {
					player.message("You search the cupboard, but find nothing");
				}
			}
		}

	}

	@Override
	public boolean blockDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		return item.getCatalogId() == ItemId.CHEESE.id() && player.getLocation().inBounds(356, 357, 494, 496);
	}

	// room inbounds : MIN X: 356 MAX X: 357 MIN Y: 494 MAX Y: 496
	@Override
	public void onDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		if (item.getCatalogId() == ItemId.CHEESE.id() && player.getLocation().inBounds(356, 357, 494, 496)) {
			if (player.getQuestStage(this) == -1) {
				say(player, null, "I would rather eat it to be honest");
				return;
			}
			player.getCarriedItems().remove(new Item(ItemId.CHEESE.id()));
			mes("A rat appears from a hole and eats the cheese");
			delay(3);
			//if there's already a rat, it despawns them in 19 secs
			final Npc oldRat = ifnearvisnpc(player, NpcId.RAT_WITCHES_HOUSE.id(), 5);
			if (oldRat != null) {
				player.getWorld().getServer().getGameEventHandler().add(
					new SingleEvent(player.getWorld(), null, config().GAME_TICK * 30, "Witches House Rat Delay") {
						public void action() {
							oldRat.remove();
						}
					});
			}

			addnpc(player.getWorld(), NpcId.RAT_WITCHES_HOUSE.id(), 356, 494);
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return item.getCatalogId() == ItemId.MAGNET.id() && npc.getID() == NpcId.RAT_WITCHES_HOUSE.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (item.getCatalogId() == ItemId.MAGNET.id() && npc.getID() == NpcId.RAT_WITCHES_HOUSE.id()) {
			if (player.getQuestStage(this) == -1) {
				return;
			}
			if (!player.getCache().hasKey("found_magnet")) {
				player.message("You need to get the magnet yourself to do this quest");
			} else {
				player.message("You put the magnet on the rat");
				//Npc rat = ifnearvisnpc(player, NpcId.RAT_WITCHES_HOUSE.id(), 2);
				delnpc(npc, false);
				mes("The rat runs back into his hole");
				delay(3);
				mes("You hear a click and whirr");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.MAGNET.id()));
				player.updateQuestStage(getQuestId(), 2);
			}
		}

	}

	@Override
	public boolean blockKillNpc(Player player, Npc npc) {
		return DataConversions.inArray(new int[] {NpcId.SHAPESHIFTER_HUMAN.id(), NpcId.SHAPESHIFTER_SPIDER.id(),
				NpcId.SHAPESHIFTER_BEAR.id(), NpcId.SHAPESHIFTER_WOLF.id()}, npc.getID());
	}

	@Override
	public void onKillNpc(Player player, Npc npc) {
		npc.resetCombatEvent();
		if (npc.getID() >= NpcId.SHAPESHIFTER_WOLF.id()) {
			player.message("You finally kill the shapeshifter once and for all");
			if (!player.getCache().hasKey("shapeshifter")) {
				player.getCache().store("shapeshifter", true);
			}
			return;
		}
		Npc nextShape = addnpc(player.getWorld(), npc.getID() + 1, npc.getX(), npc.getY(), 300000);

		player.message("The shapeshifer turns into a "
			+ npcMessage(nextShape.getID()) + "!");
		nextShape.startCombat(player);
	}

	private String npcMessage(int id) {
		if (id == NpcId.SHAPESHIFTER_SPIDER.id()) {
			return "spider";
		} else if (id == NpcId.SHAPESHIFTER_BEAR.id()) {
			return "bear";
		} else if (id == NpcId.SHAPESHIFTER_WOLF.id()) {
			return "wolf";
		}
		return "";
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.SHAPESHIFTER_HUMAN.id() && player.getQuestStage(getQuestId()) == -1) {
			player.message("I have already done that quest");
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SHAPESHIFTER_HUMAN.id() && player.getQuestStage(getQuestId()) == -1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.BALL.id() && i.getX() == 351 && i.getY() == 491) {
			if (player.getQuestStage(getQuestId()) == -1) {
				return true;
			}
			if (!player.getCache().hasKey("shapeshifter")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (!player.getCache().hasKey("shapeshifter")) {
			Npc shapeshifter = ifnearvisnpc(player, NpcId.SHAPESHIFTER_HUMAN.id(), 20);
			if (shapeshifter != null) {
				shapeshifter.startCombat(player);
				if (DataConversions.random(0, 3) == 0) weakenPlayer(player);
			}
		} else if (player.getQuestStage(getQuestId()) == -1) {
			say(player, null, "I'd better not take it, its not mine");
		}

	}

	private void weakenPlayer(Player player) {
		player.message("The shapeshifter glares at you");
		//delay of about 2 ticks
		player.message("You feel slightly weakened");
		int[] stats = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.STRENGTH.id()};
		boolean sendUpdate = player.getClientLimitations().supportsSkillUpdate;
		for(int affectedStat : stats) {
			/* How much to lower the stat */
			int lowerBy = (int) Math.ceil(((player.getSkills().getMaxStat(affectedStat) - 4) / 15.0));
			/* New current level */
			final int newStat = Math.max(0, player.getSkills().getLevel(affectedStat) - lowerBy);
			player.getSkills().setLevel(affectedStat, newStat, sendUpdate);
		}
		if (!sendUpdate) {
			player.getSkills().sendUpdateAll();
		}
	}

	private boolean wearingInsulatingGloves(Player player) {
		return player.getCarriedItems().getEquipment().hasEquipped(ItemId.LEATHER_GLOVES.id()) || player.getCarriedItems().getEquipment().hasEquipped(ItemId.ICE_GLOVES.id());
	}

	//considerations: sq/kite shields, med/large helms, plate-bodies/plate-tops/chains, legs/skirts
	public static final int[] METAL_ARMOURS = {
		//plate bodies
		ItemId.BRONZE_PLATE_MAIL_BODY.id(), ItemId.IRON_PLATE_MAIL_BODY.id(), ItemId.STEEL_PLATE_MAIL_BODY.id(), ItemId.MITHRIL_PLATE_MAIL_BODY.id(), ItemId.ADAMANTITE_PLATE_MAIL_BODY.id(), ItemId.BLACK_PLATE_MAIL_BODY.id(), ItemId.RUNE_PLATE_MAIL_BODY.id(),
		//plate tops
		ItemId.BRONZE_PLATE_MAIL_TOP.id(), ItemId.IRON_PLATE_MAIL_TOP.id(), ItemId.STEEL_PLATE_MAIL_TOP.id(), ItemId.MITHRIL_PLATE_MAIL_TOP.id(), ItemId.ADAMANTITE_PLATE_MAIL_TOP.id(), ItemId.BLACK_PLATE_MAIL_TOP.id(), ItemId.RUNE_PLATE_MAIL_TOP.id(),
		//chain bodies
		ItemId.BRONZE_CHAIN_MAIL_BODY.id(), ItemId.IRON_CHAIN_MAIL_BODY.id(), ItemId.STEEL_CHAIN_MAIL_BODY.id(), ItemId.MITHRIL_CHAIN_MAIL_BODY.id(), ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id(), ItemId.BLACK_CHAIN_MAIL_BODY.id(), ItemId.RUNE_CHAIN_MAIL_BODY.id(), ItemId.DRAGON_SCALE_MAIL.id(),
		//chain legs
		ItemId.BRONZE_CHAIN_MAIL_LEGS.id(), ItemId.IRON_CHAIN_MAIL_LEGS.id(), ItemId.STEEL_CHAIN_MAIL_LEGS.id(), ItemId.MITHRIL_CHAIN_MAIL_LEGS.id(), ItemId.ADAMANTITE_CHAIN_MAIL_LEGS.id(), ItemId.BLACK_CHAIN_MAIL_LEGS.id(), ItemId.RUNE_CHAIN_MAIL_LEGS.id(),
		//plate legs
		ItemId.BRONZE_PLATE_MAIL_LEGS.id(), ItemId.IRON_PLATE_MAIL_LEGS.id(), ItemId.STEEL_PLATE_MAIL_LEGS.id(), ItemId.MITHRIL_PLATE_MAIL_LEGS.id(), ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id(), ItemId.BLACK_PLATE_MAIL_LEGS.id(), ItemId.RUNE_PLATE_MAIL_LEGS.id(),
		//plate skirts
		ItemId.BRONZE_PLATED_SKIRT.id(), ItemId.IRON_PLATED_SKIRT.id(), ItemId.STEEL_PLATED_SKIRT.id(), ItemId.MITHRIL_PLATED_SKIRT.id(), ItemId.ADAMANTITE_PLATED_SKIRT.id(), ItemId.BLACK_PLATED_SKIRT.id(), ItemId.RUNE_SKIRT.id(),
		//medium helmets
		ItemId.MEDIUM_BRONZE_HELMET.id(), ItemId.MEDIUM_IRON_HELMET.id(), ItemId.MEDIUM_STEEL_HELMET.id(), ItemId.MEDIUM_MITHRIL_HELMET.id(), ItemId.MEDIUM_ADAMANTITE_HELMET.id(), ItemId.MEDIUM_BLACK_HELMET.id(), ItemId.MEDIUM_RUNE_HELMET.id(), ItemId.DRAGON_MEDIUM_HELMET.id(),
		//large helmets
		ItemId.LARGE_BRONZE_HELMET.id(), ItemId.LARGE_IRON_HELMET.id(), ItemId.LARGE_STEEL_HELMET.id(), ItemId.LARGE_MITHRIL_HELMET.id(), ItemId.LARGE_ADAMANTITE_HELMET.id(), ItemId.LARGE_BLACK_HELMET.id(), ItemId.LARGE_RUNE_HELMET.id(),
		//square shields
		ItemId.BRONZE_SQUARE_SHIELD.id(), ItemId.IRON_SQUARE_SHIELD.id(), ItemId.STEEL_SQUARE_SHIELD.id(), ItemId.MITHRIL_SQUARE_SHIELD.id(), ItemId.ADAMANTITE_SQUARE_SHIELD.id(), ItemId.BLACK_SQUARE_SHIELD.id(), ItemId.RUNE_SQUARE_SHIELD.id(), ItemId.DRAGON_SQUARE_SHIELD.id(),
		//kite shields
		ItemId.BRONZE_KITE_SHIELD.id(), ItemId.IRON_KITE_SHIELD.id(), ItemId.STEEL_KITE_SHIELD.id(), ItemId.MITHRIL_KITE_SHIELD.id(), ItemId.ADAMANTITE_KITE_SHIELD.id(), ItemId.BLACK_KITE_SHIELD.id(), ItemId.RUNE_KITE_SHIELD.id()
	};

	private boolean wearingMetalArmour(Player player) {
		if (wearingInsulatingGloves(player)) {
			return false;
		}
		boolean isWearingMetal = false;

		for (int itemId : METAL_ARMOURS) {
			isWearingMetal |= player.getCarriedItems().getEquipment().hasEquipped(itemId);
			if (isWearingMetal) break;
		}

		return isWearingMetal;
	}
}
