package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
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

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class VampireSlayer implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	KillNpcTrigger,
	AttackNpcTrigger{

	private static final int COUNT_DRAYNOR_COFFIN_OPEN = 136;
	private static final int COUNT_DRAYNOR_COFFIN_CLOSED = 135;
	private static final int GARLIC_CUPBOARD_OPEN = 141;
	private static final int GARLIC_CUPBOARD_CLOSED = 140;

	@Override
	public int getQuestId() {
		return Quests.VAMPIRE_SLAYER;
	}

	@Override
	public String getQuestName() {
		return "Vampire slayer";
	}

	@Override
	public int getQuestPoints() {
		return Quest.VAMPIRE_SLAYER.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(final Player player) {
		player.message("Well done you have completed the vampire slayer quest");
		final QuestReward reward = Quest.VAMPIRE_SLAYER.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	private void morganDialogue(final Player player, final Npc npc) {
		switch (player.getQuestStage(this)) {
			case 0:
				npcsay(player, npc, "Please please help us, bold hero");
				say(player, npc, "What's the problem?");
				npcsay(player,
					npc,
					"Our little village has been dreadfully ravaged by an evil vampire",
					"There's hardly any of us left",
					"We need someone to get rid of him once and for good");
				int choice = multi(player, npc,
					"No. vampires are scary", "Ok I'm up for an adventure",
					"I tried fighting him. He wouldn't die");
				if (choice == 0) {
					npcsay(player, npc, "I don't blame you");
				} else if (choice == 1) {
					npcsay(player,
						npc,
						"I think first you should seek help",
						"I have a friend who is a retired vampire hunter",
						"Called Dr Harlow",
						"He may be able to give you some tips",
						"He can normally be found in the Jolly boar inn these days",
						"He's a bit of an old soak",
						"Mention his old friend Morgan",
						"I'm sure he wouldn't want me to be killed by a vampire");
					say(player, npc, "I'll look him up then");
					player.updateQuestStage(getQuestId(), 1);
				} else if (choice == 2) {
					npcsay(player,
						npc,
						"Maybe you're not going about it right",
						"I think first you should seek help",
						"I have a friend who is a retired vampire hunter",
						"Called Dr Harlow",
						"He may be able to give you some tips",
						"He can normally be found in the Jolly boar inn these days",
						"He's a bit of an old soak",
						"Mention his old friend Morgan",
						"I'm sure he wouldn't want me to be killed by a vampire");
					say(player, npc, "I'll look him up then");
					player.updateQuestStage(getQuestId(), 1);
				}
				break;
			case 1:
			case 2:
				npcsay(player, npc, "How are you doing with your quest?");
				say(player, npc, "I'm working on it still");
				npcsay(player, npc, "Please hurry", "Every day we live in fear of lives",
					"That we will be the vampires next victim");
				break;
			case -1:
				npcsay(player, npc, "How are you doing with your quest?");
				say(player, npc, "I have slain the foul creature");
				npcsay(player, npc, "Thank you, thank you",
					"You will always be a hero in our village");
				break;
		}
	}

	private void harlowDialogue(final Player player, final Npc npc) {
		switch (player.getQuestStage(this)) {
			case -1:
			case 0:
			case 1:
			case 2:
				String[] options;
				npcsay(player, npc, "Buy me a drrink pleassh");
				if (!player.getCarriedItems().hasCatalogID(ItemId.STAKE.id(), Optional.empty())
					&& player.getQuestStage(Quests.VAMPIRE_SLAYER) != -1) {
					options = new String[]{"No you've had enough", "Ok mate",
						"Morgan needs your help"};
				} else {
					options = new String[]{"No you've had enough", "Ok mate"};
				}
				int choice = multi(player, npc, options);
				if (choice == 0) {
				} else if (choice == 1) {
					if (player.getCarriedItems().hasCatalogID(ItemId.BEER.id())) {
						player.message("You give a beer to Dr Harlow");
						player.getCarriedItems().remove(new Item(ItemId.BEER.id()));
						npcsay(player, npc, "Cheersh matey");
					} else {
						say(player, npc, "I'll just go and buy one");
					}
				} else if (choice == 2) {
					npcsay(player, npc, "Morgan you shhay?");
					say(player, npc,
						"His village is being terrorised by a vampire",
						"He wanted me to ask you how i should go about stopping it");
					npcsay(player, npc,
						"Buy me a beer then i'll teash you what you need to know");
					int choice2 = multi(player, npc, "Ok mate",
						"But this is your friend Morgan we're talking about");
					if (choice2 == 0) {
						if (player.getCarriedItems().hasCatalogID(ItemId.BEER.id())) {
							player.message("You give a beer to Dr Harlow");
							npcsay(player, npc, "Cheersh matey");
							player.getCarriedItems().remove(new Item(ItemId.BEER.id()));
							say(player, npc, "So tell me how to kill vampires then");
							npcsay(player, npc,
								"Yesh yesh vampires I was very good at killing em once");
							player.message("Dr Harlow appears to sober up slightly");
							npcsay(player,
								npc,
								"Well you're gonna to kill it with a stake",
								"Otherwishe he'll just regenerate",
								"Yes your killing blow must be done with a stake",
								"I jusht happen to have one on me");
							player.message("Dr Harlow hands you a stake");
							player.getCarriedItems().getInventory().add(new Item(ItemId.STAKE.id()));
							npcsay(player,
								npc,
								"You'll need a hammer to hand to drive it in properly as well",
								"One last thing",
								"It's wise to carry garlic with you",
								"Vampires are weakened somewhat if they can smell garlic",
								"Dunno where you'd find that though",
								"Remember even then a vampire is a dangeroush foe");
							say(player, npc, "Thank you very much");
							player.updateQuestStage(getQuestId(), 2);
						} else {
							say(player, npc, "I'll just go and buy one");
						}
					} else if (choice2 == 1) {
						npcsay(player, npc, "Buy ush a drink anyway");
					}
				}
				break;
		}
	}

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		if (npc.getID() == NpcId.MORGAN.id()) {
			morganDialogue(player, npc);
		} else if (npc.getID() == NpcId.DR_HARLOW.id()) {
			harlowDialogue(player, npc);
		}
	}

	@Override
	public void onOpLoc(final Player player, final GameObject obj, final String command) {
		if ((obj.getID() == COUNT_DRAYNOR_COFFIN_OPEN || obj.getID() == COUNT_DRAYNOR_COFFIN_CLOSED) && obj.getY() == 3380) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, player, COUNT_DRAYNOR_COFFIN_OPEN, "You open the coffin");
				// if quest is not complete spawns also vampire if none present
				if (player.getQuestStage(this) >= 0) {
					spawnVampire(player);
				}
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, player, COUNT_DRAYNOR_COFFIN_CLOSED, "You close the coffin");
			} else {
				if (player.getQuestStage(this) == -1) {
					player.message("There's a pillow in here");
					return;
				} else {
					spawnVampire(player);
				}
			}
		} else if ((obj.getID() == GARLIC_CUPBOARD_OPEN || obj.getID() == GARLIC_CUPBOARD_CLOSED) && obj.getY() == 1562) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, player, GARLIC_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, player, GARLIC_CUPBOARD_CLOSED);
			} else {
				player.message("You search the cupboard");
				if (!player.getCarriedItems().hasCatalogID(ItemId.GARLIC.id())) {
					player.message("You find a clove of garlic that you take");
					player.getCarriedItems().getInventory().add(new Item(ItemId.GARLIC.id()));
				} else {
					player.message("The cupboard is empty");
				}
			}
			return;
		}
	}

	private void spawnVampire(final Player player) {
		Npc spawnedVampire = ifnearvisnpc(player, NpcId.COUNT_DRAYNOR.id(), 15);

		if (spawnedVampire != null) {
			//nothing to do
			return;
		}

		final Npc vampire = addnpc(player.getWorld(), NpcId.COUNT_DRAYNOR.id(), 205, 3382);
		if (vampire == null) {
			return;
		}
		player.message("A vampire jumps out of the coffin");
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.MORGAN.id() || npc.getID() == NpcId.DR_HARLOW.id();
	}

	@Override
	public boolean blockOpLoc(final Player player, final GameObject obj, final String command) {
		return (obj.getID() == COUNT_DRAYNOR_COFFIN_OPEN || obj.getID() == COUNT_DRAYNOR_COFFIN_CLOSED) && obj.getY() == 3380
				|| (obj.getID() == GARLIC_CUPBOARD_OPEN || obj.getID() == GARLIC_CUPBOARD_CLOSED) && obj.getY() == 1562;
	}

	@Override
	public boolean blockKillNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.COUNT_DRAYNOR.id();
	}

	@Override
	public void onKillNpc(final Player player, final Npc npc) {
		if (npc.getID() == NpcId.COUNT_DRAYNOR.id()) {
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.STAKE.id()) && player.getCarriedItems().hasCatalogID(ItemId.HAMMER.id())) {
				Item item = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(ItemId.STAKE.id(), Optional.of(false)));
				if (item == null) {
					item = player.getCarriedItems().getEquipment().get(
						player.getCarriedItems().getEquipment().searchEquipmentForItem(ItemId.STAKE.id())
					);
				}
				if (item == null) {
					npc.killed = false;
					return;
				}
				player.getCarriedItems().remove(item);
				player.message("You hammer the stake in to the vampires chest!");
				player.getWorld().registerItem(
					new GroundItem(player.getWorld(), ItemId.BONES.id(), npc.getX(), npc.getY(), 1, player));
				npc.remove();

				// Completed Vampire Slayer Quest.
				if (player.getQuestStage(this) == 2) {
					player.sendQuestComplete(Quests.VAMPIRE_SLAYER);
				}
			} else {
				npc.getSkills().setLevel(Skill.HITS.id(), 20);
				player.message("The vampire seems to regenerate");
				npc.killed = false;
			}
		}
	}

	@Override
	public void onAttackNpc(final Player player, final Npc affectedMob) {
		if (affectedMob.getID() == NpcId.COUNT_DRAYNOR.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.GARLIC.id())) {
				player.message("The vampire appears to weaken");
				//if a better approx is found, replace
				int[] affectedStats = {Skill.ATTACK.id(), Skill.STRENGTH.id(), Skill.DEFENSE.id()};
				double factor = 0.1;
				for (int stat : affectedStats) {
					int maxStat = affectedMob.getSkills().getMaxStat(stat);
					// We don't want to keep draining the vampire if he's already been drained.
					if (affectedMob.getSkills().getLevel(stat) == maxStat) {
						if (stat == Skill.DEFENSE.id()) factor = 0.4;
						int newStat = maxStat - (int) (maxStat * factor);
						affectedMob.getSkills().setLevel(stat, newStat);
					}
				}
				if (affectedMob.getSkills().getLevel(Skill.HITS.id()) >= 34) {
					affectedMob.damage(10);
				}
			}

			player.startCombat(affectedMob);
		}
	}

	@Override
	public boolean blockAttackNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.COUNT_DRAYNOR.id();
	}
}
