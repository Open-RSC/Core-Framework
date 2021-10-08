package com.openrsc.server.plugins.authentic.quests.members.digsite;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteWinch implements OpLocTrigger, UseLocTrigger {

	private static final int[] WINCH = {1095, 1053};

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), WINCH);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (inArray(obj.getID(), WINCH)) {
			switch (player.getQuestStage(Quests.DIGSITE)) {
				case -1:
					player.playerServerMessage(MessageType.QUEST, "You find yourself in a cavern...");
					player.teleport(19, 3385);
					break;
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
					if (obj.getID() == WINCH[0]) {
						if (!player.getCache().hasKey("digsite_winshaft")) {
							this.handleCantUseWinch(player, obj);
						} else if (!player.getCache().hasKey("winch_rope_1")) {
							player.playerServerMessage(MessageType.QUEST, "You operate the winch");
							player.message("The bucket descends, but does not reach the bottom");
							say(player, null, "Hey I think I could fit down here...", "I need something to help me get all the way down");
						} else {
							if (getCurrentLevel(player, Skill.AGILITY.id()) < 10) {
								player.message("You need an agility level of 10 to do this");
								return;
							}
							mes("You try to climb down the rope");
							delay(3);
							mes("You lower yourself into the shaft");
							delay(3);
							player.incExp(Skill.AGILITY.id(), 20, true);
							player.teleport(26, 3346);
							player.playerServerMessage(MessageType.QUEST, "You find yourself in a cavern...");
						}

					}

					else if (obj.getID() == WINCH[1]) {
						if (!player.getCache().hasKey("digsite_winshaft")) {
							this.handleCantUseWinch(player, obj);
						} else if (!player.getCache().hasKey("winch_rope_2")) {
							player.playerServerMessage(MessageType.QUEST, "You operate the winch");
							player.message("The bucket descends, but does not reach the bottom");
							say(player, null, "Hey I think I could fit down here...", "I need something to help me get all the way down");
						} else {
							if (getCurrentLevel(player, Skill.AGILITY.id()) < 10) {
								player.message("You need an agility level of 10 to do this");
								return;
							}
							mes("You try to climb down the rope");
							delay(3);
							mes("You lower yourself into the shaft");
							delay(3);
							player.incExp(Skill.AGILITY.id(), 20, true);
							if (player.getQuestStage(Quests.DIGSITE) >= 6) {
								player.teleport(19, 3385);
							} else {
								player.teleport(19, 3337);
							}
							player.playerServerMessage(MessageType.QUEST, "You find yourself in a cavern...");
						}
					}
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return inArray(obj.getID(), WINCH) && item.getCatalogId() == ItemId.ROPE.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (inArray(obj.getID(), WINCH) && item.getCatalogId() == ItemId.ROPE.id()) {
			if (obj.getID() == WINCH[0]) {
				if (player.getCache().hasKey("digsite_winshaft")) {
					if (!player.getCache().hasKey("winch_rope_1")) {
						player.message("You tie the rope to the bucket");
						player.getCache().store("winch_rope_1", true);
						player.getCarriedItems().remove(new Item(ItemId.ROPE.id()));
					} else {
						player.message("There is already a rope tied to this bucket");
					}
				} else {
					say(player, null, "Err... I have no idea why I am doing this !");
				}
			}
			else if (obj.getID() == WINCH[1]) {
				if (player.getCache().hasKey("digsite_winshaft")) {
					if (!player.getCache().hasKey("winch_rope_2")) {
						player.message("You tie the rope to the bucket");
						player.getCache().store("winch_rope_2", true);
						player.getCarriedItems().remove(new Item(ItemId.ROPE.id()));
					} else {
						player.message("There is already a rope tied to this bucket");
					}
				} else {
					say(player, null, "Err... I have no idea why I am doing this !");
				}
			}
		}
	}

	private void handleCantUseWinch(Player player, GameObject obj) {
		boolean workmanWasSpawned;

		Npc workman = ifnearvisnpc(player, NpcId.WORKMAN.id(), 5);
		if (workman == null) {
			workman = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 60000);
			workmanWasSpawned = true;
		} else {
			workmanWasSpawned = false;
			workman.resetPath();
			workman.teleport(player.getX(), player.getY());
		}
		npcsay(player, workman, "Sorry, this area is private");
		workman.teleport(player.getX() + (obj.getID() == WINCH[0] ? +1 : -1), player.getY());
		npcsay(player, workman, "The only way you'll get to use these",
			"Is by impressing the expert",
			"Up at the centre",
			"Find something worthwhile...",
			"And he might let you use the winches",
			"Until then, get lost !");
		if (workmanWasSpawned) {
			workman.remove();
		}
	}
}
