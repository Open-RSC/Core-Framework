package com.openrsc.server.plugins.authentic.quests.members.legendsquest.mechanism;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestBullRoarer implements OpInvTrigger {
	private static final Logger LOGGER = LogManager.getLogger(LegendsQuestBullRoarer.class);
	private boolean inKharaziJungle(Player player) {
		return player.getLocation().inBounds(338, 869, 477, 908);
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.BULL_ROARER.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.BULL_ROARER.id()) {
			mes("You start to swing the bullroarer above your head.");
			delay(2);
			mes("You feel a bit silly at first, but soon it makes an interesting sound.");
			delay(2);
			if (inKharaziJungle(player)) {
				mes("You see some movement in the trees...");
				delay(2);
				attractNatives(player);
			} else {
				mes("Nothing much seems to happen though.");
				delay(2);
				Npc forester = ifnearvisnpc(player, NpcId.JUNGLE_FORESTER.id(), 10);
				if (forester != null) {
					npcsay(player, forester, "You might like to use that when you get into the ",
						"Kharazi jungle, it might attract more natives...");
				}
			}
		}
	}

	private void attractNatives(Player player) {
		int controlRandom = DataConversions.getRandom().nextInt(4);
		if (controlRandom == 0) {
			mes("...but nothing else much seems to happen.");
			delay(2);
		} else if (controlRandom >= 1 && controlRandom <= 2) {
			mes("...and a tall, dark, charismatic looking native approaches you.");
			delay(2);
			Npc gujuo = ifnearvisnpc(player, NpcId.GUJUO.id(), 15);
			if (gujuo == null) {
				gujuo = addnpc(player.getWorld(), NpcId.GUJUO.id(), player.getX(), player.getY());
				delayedRemoveGujuo(player, gujuo);
			}
			if (gujuo != null) {
				gujuo.resetPath();
				gujuo.teleport(player.getX(), player.getY());
				gujuo.initializeTalkScript(player);
				delay();
				npcWalkFromPlayer(player, gujuo);
			}
		} else if (controlRandom == 3) {
			Npc nativeNpc = ifnearvisnpc(player, 5, NpcId.OOMLIE_BIRD.id(), NpcId.KARAMJA_WOLF.id(), NpcId.JUNGLE_SPIDER.id(), NpcId.JUNGLE_SAVAGE.id());
			if (nativeNpc != null) {
				mes("...and a nearby " + (nativeNpc.getDef().getName().contains("bird") ? nativeNpc.getDef().getName() : "Kharazi " + nativeNpc.getDef().getName().toLowerCase()) + " takes a sudden dislike to you.");
				delay(2);
				nativeNpc.setChasing(player);
				mes("And attacks...");
				delay();
			} else {
				attractNatives(player);
			}
		}
	}

	private void delayedRemoveGujuo(Player player, Npc n) {
		try {
			player.getWorld().getServer().getGameEventHandler().add(new DelayedEvent(player.getWorld(), null, (int)TimeUnit.SECONDS.toMillis(150), "Delayed Remove Gujuo") {
				@Override
				public void run() {
					if (!player.isLoggedIn() || player.isRemoved()) {
						n.remove();
						stop();
						return;
					}
					if (n.isRemoved()) {
						stop();
						return;
					}
					if (!inKharaziJungle(player)) {
						n.remove();
						stop();
						return;
					}
					int yell = DataConversions.random(0, 3);
					if (yell == 0) {
						npcsay(player, n, "I am tired Bwana, I must go and rest...");
					}
					if (yell == 1) {
						npcsay(player, n, "I must visit my people now...");
					} else if (yell == 2) {
						npcsay(player, n, "I must go and hunt now Bwana..");
					} else if (yell == 3) {
						npcsay(player, n, "I have to collect herbs now Bwana...");
					} else {
						npcsay(player, n, "I have work to do Bwana, I may see you again...");
					}
					getWorld().getServer().getGameEventHandler().add(new SingleEvent(player.getWorld(), null, n.getConfig().GAME_TICK * 3, "Legends Quest Gujuo Disappears") {
						public void action() {
							if (player != null)
								player.message("Gujuo disapears into the Kharazi jungle as swiftly as he appeared...");
							if(n != null)
								n.remove();
						}
					});
					stop();
				}
			});
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}
}
