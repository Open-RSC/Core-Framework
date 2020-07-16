package com.openrsc.server.plugins.authentic.minigames.fishingtrawler;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.content.minigame.fishingtrawler.FishingTrawler;
import com.openrsc.server.content.minigame.fishingtrawler.FishingTrawler.TrawlerBoat;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public class Murphy implements MiniGameInterface, TalkNpcTrigger {

	/**
	 * IMPORTANT NOTES:
	 * <p>
	 * NPC: 734
	 * START EAST: 272, 741 START WEST: 320, 741 GO
	 * UNDER EAST: 248, 729 UNDER WEST: 296, 729
	 * FAIL - AFTER GO UNDER EAST: 254, 759
	 * (SHARED) FAIL - AFTER GO UNDER WEST AND/OR QUIT MINI-GAME: 302, 759 GO
	 * BACK FROM FAIL LOCATION: 550, 711
	 */

	@Override
	public int getMiniGameId() {
		return Minigames.FISHING_TRAWLER;
	}

	@Override
	public String getMiniGameName() {
		return "Fishing Trawler (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		//mini-game complete handled already
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.MURPHY_LAND.id() || n.getID() == NpcId.MURPHY_BOAT.id() || n.getID() == NpcId.MURPHY_UNRELEASED.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.MURPHY_LAND.id()) { // Murphy on land
			if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
				|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id())) {
				player.message("As an Iron Man, you cannot use the Trawler.");
				return;
			}
			if (!player.getCache().hasKey("fishingtrawler")) {
				say(player, n, "good day to you sir");
				npcsay(player, n, "well hello my brave adventurer");
				say(player, n, "what are you up to?");
				npcsay(player, n, "getting ready to go fishing of course",
						"there's no time to waste",
						"i've got all the supplies i need from the shop at the end of the pier",
						"they sell good rope, although their bailing buckets aren't too effective");
				showStartOption(player, n, true, true, true);
			} else {
				say(player, n, "hello again murphy");
				npcsay(player, n, "good day to you land lover");
				if (player.getCache().hasKey("fishing_trawler_reward")) {
					npcsay(player, n, "It looks like your net is full from last trip");
					return;
				}
				npcsay(player, n, "fancy hitting the high seas again?");
				int option = multi(player, n, "no thanks, i still feel ill from last time", "yes, lets do it");
				if (option == 0) {
					npcsay(player, n, "hah..softy");
				} else if (option == 1) {
					letsGo(player, n);
				}
			}
		} else if (n.getID() == NpcId.MURPHY_BOAT.id()) { // Murphy on the boat.
			onship(n, player);
		} else if (n.getID() == NpcId.MURPHY_UNRELEASED.id()) { // Another murphy potentially non existent
		}
	}

	private void showStartOption(Player player, Npc n, boolean showOptionFish, boolean showOptionNotSafe, boolean showOptionHelp) {
		ArrayList<String> options = new ArrayList<>();
		if (showOptionFish) {
			options.add("what fish do you catch?");
		}
		if (showOptionNotSafe) {
			options.add("your boat doesn't look too safe");
		}
		if (showOptionHelp) {
			options.add("could i help?");
		}

		String[] finalOptions = new String[options.size()];
		int option = multi(player, n, options.toArray(finalOptions));

		if (option == 2) {
			if (showOptionHelp) {
				chatOptionHelp(player, n);
			}
		}

		else if (option == 1) {
			if (showOptionFish) {
				if (showOptionNotSafe) {
					chatOptionNotSafe(player, n);
				}
				else if (showOptionHelp) {
					chatOptionHelp(player, n);
				}
			}
			else if (showOptionNotSafe) {
				if (showOptionHelp) {
					chatOptionHelp(player, n);
				}
			}
		}

		else if (option == 0) {
			if (showOptionFish) {
				chatOptionFish(player, n);
			}
			else if (showOptionNotSafe) {
				chatOptionNotSafe(player, n);
			}
			else if (showOptionHelp) {
				chatOptionHelp(player, n);
			}
		}
	}

	private void chatOptionFish(Player player, Npc npc) {
		npcsay(player, npc, "i get all sorts, anything that lies on the sea bed",
			"you never know what you're going to get until...",
			"...you pull up the net");
		showStartOption(player, npc, false, true, true);
	}

	private void chatOptionNotSafe(Player player, Npc npc) {
		npcsay(player, npc, "that's because it's not, the dawn thing's full of holes");
		say(player, npc, "oh, so i suppose you can't go out for a while");
		npcsay(player, npc, "oh no, i don't let a few holes stop an experienced sailor like me",
			"i could sail these seas in a barrel",
			"i'll be going out soon enough");
		showStartOption(player, npc, true, false, true);
	}

	private void chatOptionHelp(Player player, Npc npc) {
		npcsay(player, npc, "well of course you can",
			"i'll warn you though, the seas are merciless",
			"and with out fishing experience you won't catch much");
		mes("you need a fishing level of 15 or above to catch any fish on the trawler");
		delay(3);
		npcsay(player, npc, "on occasions the net rip's, so you'll need some rope to repair it");
		say(player, npc, "rope...ok");
		npcsay(player, npc, "there's also a slight problem with leaks");
		say(player, npc, "leaks!");
		npcsay(player, npc, "nothing some swamp paste won't fix");
		say(player, npc, "swamp paste?");
		npcsay(player, npc, "oh, and one more thing...",
			"..i hope you're a good swimmer");
		int gooption = multi(player, npc, "actually, i think i'll leave it", "i'll be fine, lets go",
			"what's swamp paste?");
		switch (gooption) {
			case 0:
				npcsay(player, npc, "bloomin' land lover's");
				break;
			case 1:
				letsGo(player, npc);
				break;
			case 2:
				npcsay(player, npc, "swamp tar mixed with flour...",
					"...which is then heated over a fire");
				say(player, npc, "where can i find swamp tar?");
				npcsay(player, npc, "unfortunately the only supply of swamp tar is in the swamps below lumbridge");
				break;
		}
	}

	private void letsGo(Player player, Npc n) {
		npcsay(player, n, "would you like to sail east or west?");
		int choice = multi(player, n, false, //do not send over
				"east please", "west please");
		FishingTrawler instance = null;
		if (choice == 0 || choice == 1) {
			if (choice == 0) {
				instance = player.getWorld().getFishingTrawler(TrawlerBoat.EAST);
			} else if (choice == 1) {
				instance = player.getWorld().getFishingTrawler(TrawlerBoat.WEST);
			}
			if (instance != null && instance.isAvailable()) {
				npcsay(player, n, "good stuff, jump aboard",
						"ok m hearty, keep your eys pealed",
						"i need you to clog up those holes quick time");
				say(player, n, "i'm ready and waiting");
				if (!player.getCache().hasKey("fishingtrawler")) {
					player.getCache().store("fishingtrawler", true);
				}
				instance.addPlayer(player);
			} else {
				npcsay(player, n, "sorry m hearty it appeears the boat is in the middle of a game");
				player.message("The boat should be available in a couple of minutes");
			}
		}
	}

	private void onship(Npc n, Player player) {
		npcsay(player, n, "whoooahh sailor");
		int option = multi(player, n, "i've had enough,  take me back", "how you doing murphy?");
		if (option == 0) {
			npcsay(player, n, "haa .. the soft land lovers lost there see legs have they?");
			say(player, n, "something like that");
			npcsay(player, n, "we're too far out now, it'd be dangerous");
			option = multi(player, n, false, //do not send over
					"I insist murphy, take me back", "Ok then murphy, just keep us afloat");
			if (option == 0) {
				say(player, n, "i insist murphy, take me back");
				npcsay(player, n, "ok, ok, i'll try, but don't say i didn't warn you");
				mes("murphy sharply turns the large ship");
				delay(3);
				mes("the boats gone under");
				delay(3);
				mes("you're lost at sea!");
				delay(3);
				if (player.getWorld().getFishingTrawler(player) != null) {
					player.getWorld().getFishingTrawler(player).quitPlayer(player);
				}
				else {
					player.teleport(302, 759, false);
					ActionSender.hideFishingTrawlerInterface(player);
				}
			} else if (option == 1) {
				say(player, n, "ok then murphy, just keep us afloat");
				npcsay(player, n, "that's the attitude sailor");
			}
		}
		if (option == 1) {
			int rnd = DataConversions.random(0,2);
			if (rnd == 0) {
				npcsay(player, n, "don't bail..it's a waste of time",
						"just fill those holes");
			} else if (rnd == 1) {
				npcsay(player, n, "it's a fierce sea today traveller",
						"you best hold on tight");
			} else if (rnd == 2) {
				npcsay(player, n, "get those fishey's");
			}
		}
	}
}
