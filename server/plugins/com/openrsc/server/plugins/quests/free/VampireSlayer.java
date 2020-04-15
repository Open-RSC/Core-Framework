package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.AttackNpcTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class VampireSlayer implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	KillNpcTrigger,
	AttackNpcTrigger {

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
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the vampire slayer quest");
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.VAMPIRE_SLAYER), true);
		player.message("@gre@You haved gained 3 quest points!");

	}

	private void morganDialogue(Player p, Npc n) {
		switch (p.getQuestStage(this)) {
			case 0:
				npcsay(p, n, "Please please help us, bold hero");
				say(p, n, "What's the problem?");
				npcsay(p,
					n,
					"Our little village has been dreadfully ravaged by an evil vampire",
					"There's hardly any of us left",
					"We need someone to get rid of him once and for good");
				int choice = multi(p, n,
					"No. vampires are scary", "Ok I'm up for an adventure",
					"I tried fighting him. He wouldn't die");
				if (choice == 0) {
					npcsay(p, n, "I don't blame you");
				} else if (choice == 1) {
					npcsay(p,
						n,
						"I think first you should seek help",
						"I have a friend who is a retired vampire hunter",
						"Called Dr Harlow",
						"He may be able to give you some tips",
						"He can normally be found in the Jolly boar inn these days",
						"He's a bit of an old soak",
						"Mention his old friend Morgan",
						"I'm sure he wouldn't want me to be killed by a vampire");
					say(p, n, "I'll look him up then");
					p.updateQuestStage(getQuestId(), 1);
				} else if (choice == 2) {
					npcsay(p,
						n,
						"Maybe you're not going about it right",
						"I think first you should seek help",
						"I have a friend who is a retired vampire hunter",
						"Called Dr Harlow",
						"He may be able to give you some tips",
						"He can normally be found in the Jolly boar inn these days",
						"He's a bit of an old soak",
						"Mention his old friend Morgan",
						"I'm sure he wouldn't want me to be killed by a vampire");
					say(p, n, "I'll look him up then");
					p.updateQuestStage(getQuestId(), 1);
				}
				break;
			case 1:
			case 2:
				npcsay(p, n, "How are you doing with your quest?");
				say(p, n, "I'm working on it still");
				npcsay(p, n, "Please hurry", "Every day we live in fear of lives",
					"That we will be the vampires next victim");
				break;
			case -1:
				npcsay(p, n, "How are you doing with your quest?");
				say(p, n, "I have slain the foul creature");
				npcsay(p, n, "Thank you, thank you",
					"You will always be a hero in our village");
				break;
		}
	}

	private void harlowDialogue(Player p, Npc n) {
		switch (p.getQuestStage(this)) {
			case -1:
			case 0:
			case 1:
			case 2:
				String[] options;
				npcsay(p, n, "Buy me a drrink pleassh");
				if (!p.getCarriedItems().hasCatalogID(ItemId.STAKE.id(), Optional.empty())
					&& p.getQuestStage(Quests.VAMPIRE_SLAYER) != -1) {
					options = new String[]{"No you've had enough", "Ok mate",
						"Morgan needs your help"};
				} else {
					options = new String[]{"No you've had enough", "Ok mate"};
				}
				int choice = multi(p, n, options);
				if (choice == 0) {
				} else if (choice == 1) {
					if (p.getCarriedItems().hasCatalogID(ItemId.BEER.id())) {
						p.message("You give a beer to Dr Harlow");
						p.getCarriedItems().remove(new Item(ItemId.BEER.id()));
						npcsay(p, n, "Cheersh matey");
					} else {
						say(p, n, "I'll just go and buy one");
					}
				} else if (choice == 2) {
					npcsay(p, n, "Morgan you shhay?");
					say(p, n,
						"His village is being terrorised by a vampire",
						"He wanted me to ask you how i should go about stopping it");
					npcsay(p, n,
						"Buy me a beer then i'll teash you what you need to know");
					int choice2 = multi(p, n, "Ok mate",
						"But this is your friend Morgan we're talking about");
					if (choice2 == 0) {
						if (p.getCarriedItems().hasCatalogID(ItemId.BEER.id())) {
							p.message("You give a beer to Dr Harlow");
							npcsay(p, n, "Cheersh matey");
							p.getCarriedItems().remove(new Item(ItemId.BEER.id()));
							say(p, n, "So tell me how to kill vampires then");
							npcsay(p, n,
								"Yesh yesh vampires I was very good at killing em once");
							p.message("Dr Harlow appears to sober up slightly");
							npcsay(p,
								n,
								"Well you're gonna to kill it with a stake",
								"Otherwishe he'll just regenerate",
								"Yes your killing blow must be done with a stake",
								"I jusht happen to have one on me");
							p.message("Dr Harlow hands you a stake");
							p.getCarriedItems().getInventory().add(new Item(ItemId.STAKE.id()));
							npcsay(p,
								n,
								"You'll need a hammer to hand to drive it in properly as well",
								"One last thing",
								"It's wise to carry garlic with you",
								"Vampires are weakened somewhat if they can smell garlic",
								"Dunno where you'd find that though",
								"Remember even then a vampire is a dangeroush foe");
							say(p, n, "Thank you very much");
							p.updateQuestStage(getQuestId(), 2);
						} else {
							say(p, n, "I'll just go and buy one");
						}
					} else if (choice2 == 1) {
						npcsay(p, n, "Buy ush a drink anyway");
					}
				}
				break;
		}
	}

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.MORGAN.id()) {
			morganDialogue(p, n);
		} else if (n.getID() == NpcId.DR_HARLOW.id()) {
			harlowDialogue(p, n);
		}
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {
		if ((obj.getID() == COUNT_DRAYNOR_COFFIN_OPEN || obj.getID() == COUNT_DRAYNOR_COFFIN_CLOSED) && obj.getY() == 3380) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, player, COUNT_DRAYNOR_COFFIN_OPEN, "You open the coffin");
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, player, COUNT_DRAYNOR_COFFIN_CLOSED, "You close the coffin");
			} else {
				if (player.getQuestStage(this) == -1) {
					player.message("There's a pillow in here");
					return;
				} else {
					for (Npc npc : player.getRegion().getNpcs()) {
						if (npc.getID() == NpcId.COUNT_DRAYNOR.id() && npc.getAttribute("spawnedFor", null).equals(player)) {
							player.message("There's nothing there.");
							return;
						}
					}

					final Npc n = addnpc(NpcId.COUNT_DRAYNOR.id(), 206, 3381, 1000 * 60 * 5, player);
					n.setShouldRespawn(false);
					player.message("A vampire jumps out of the coffin");
					return;
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

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.MORGAN.id() || n.getID() == NpcId.DR_HARLOW.id();
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command,
							  Player player) {
		return (obj.getID() == COUNT_DRAYNOR_COFFIN_OPEN || obj.getID() == COUNT_DRAYNOR_COFFIN_CLOSED) && obj.getY() == 3380
				|| (obj.getID() == GARLIC_CUPBOARD_OPEN || obj.getID() == GARLIC_CUPBOARD_CLOSED) && obj.getY() == 1562;
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return n.getID() == NpcId.COUNT_DRAYNOR.id();
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (n.getID() == NpcId.COUNT_DRAYNOR.id()) {
			if (p.getCarriedItems().getEquipment().hasEquipped(ItemId.STAKE.id()) && p.getCarriedItems().hasCatalogID(ItemId.HAMMER.id())) {
				Item item = p.getCarriedItems().getInventory().get(
					p.getCarriedItems().getInventory().getLastIndexById(ItemId.STAKE.id()));
				if (item.getItemStatus().getNoted()) return;
				p.getCarriedItems().remove(item);
				p.message("You hammer the stake in to the vampires chest!");
				n.killedBy(p);
				n.remove();
				// Completed Vampire Slayer Quest.
				if (p.getQuestStage(this) == 2) {
					p.sendQuestComplete(Quests.VAMPIRE_SLAYER);
				}
			} else {
				n.getSkills().setLevel(Skills.HITS, 35);
				p.message("The vampire seems to regenerate");
			}
		}
	}

	@Override
	public void onAttackNpc(Player p, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.COUNT_DRAYNOR.id()) {
			if (p.getCarriedItems().hasCatalogID(ItemId.GARLIC.id())) {
				p.message("The vampire appears to weaken");
				//if a better approx is found, replace
				for (int i = 0; i < 3; i++) {
					int maxStat = affectedmob.getSkills().getMaxStat(i);
					int newStat = maxStat - (int) (maxStat * 0.1);
					affectedmob.getSkills().setLevel(i, newStat);
				}
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player p, Npc n) {
		return false;
	}
}
