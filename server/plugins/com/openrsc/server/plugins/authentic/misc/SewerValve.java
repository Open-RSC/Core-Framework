package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class SewerValve implements OpLocTrigger {

	private static final int SEWER_VALVE_1 = 412;
	private static final int SEWER_VALVE_2 = 413;
	private static final int SEWER_VALVE_3 = 414;
	private static final int SEWER_VALVE_4 = 415;
	private static final int SEWER_VALVE_5 = 416;
	private static final int LOG_RAFT = 432;
	private static final int LOG_RAFT_BACK = 433;

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == SEWER_VALVE_1 || obj.getID() == SEWER_VALVE_2 || obj.getID() == SEWER_VALVE_3 || obj.getID() == SEWER_VALVE_4 || obj.getID() == SEWER_VALVE_5) {
			if (command.equalsIgnoreCase("turn left")) {
				player.message("you turn the large metal");
				player.message("valve to the left");
				player.message("beneath the soil you can");
				player.message("hear the gushing of water");
				if (obj.getID() == SEWER_VALVE_1 && player.getCache().hasKey("VALVE_1_RIGHT")) {
					player.getCache().remove("VALVE_1_RIGHT");
				}

				if (obj.getID() == SEWER_VALVE_2 && !player.getCache().hasKey("VALVE_2_LEFT")) {
					player.getCache().store("VALVE_2_LEFT", true);
				}

				if (obj.getID() == SEWER_VALVE_3 && player.getCache().hasKey("VALVE_3_RIGHT")) {
					player.getCache().remove("VALVE_3_RIGHT");
				}

				if (obj.getID() == SEWER_VALVE_4 && player.getCache().hasKey("VALVE_4_RIGHT")) {
					player.getCache().remove("VALVE_4_RIGHT");
				}

				if (obj.getID() == SEWER_VALVE_5 && !player.getCache().hasKey("VALVE_5_LEFT")) {
					player.getCache().store("VALVE_5_LEFT", true);
				}

			} else if (command.equalsIgnoreCase("turn right")) {
				player.message("you turn the large metal");
				player.message("valve to the right");
				player.message("beneath the soil you can");
				player.message("hear the gushing of water");
				if (obj.getID() == SEWER_VALVE_1 && !player.getCache().hasKey("VALVE_1_RIGHT")) {
					player.getCache().store("VALVE_1_RIGHT", true);
				}
				if (obj.getID() == SEWER_VALVE_2 && player.getCache().hasKey("VALVE_2_LEFT")) {
					player.getCache().remove("VALVE_2_LEFT");
				}
				if (obj.getID() == SEWER_VALVE_3 && !player.getCache().hasKey("VALVE_3_RIGHT")) {
					player.getCache().store("VALVE_3_RIGHT", true);
				}
				if (obj.getID() == SEWER_VALVE_4 && !player.getCache().hasKey("VALVE_4_RIGHT")) {
					player.getCache().store("VALVE_4_RIGHT", true);
				}
				if (obj.getID() == SEWER_VALVE_5 && player.getCache().hasKey("VALVE_5_LEFT")) {
					player.getCache().remove("VALVE_5_LEFT");
				}
			}
		}
		if (obj.getID() == LOG_RAFT) {
			mes("you carefully board the small raft");
			delay(3);
			if (player.getCache().hasKey("VALVE_1_RIGHT") && player.getCache().hasKey("VALVE_2_LEFT") && player.getCache().hasKey("VALVE_3_RIGHT") && player.getCache().hasKey("VALVE_4_RIGHT") && player.getCache().hasKey("VALVE_5_LEFT")) {
				player.teleport(587, 3411);
				player.message("the raft washes up the sewer, the sewer passages end here");
			}
			else if (player.getCache().hasKey("VALVE_1_RIGHT") && player.getCache().hasKey("VALVE_2_LEFT") && player.getCache().hasKey("VALVE_3_RIGHT") && player.getCache().hasKey("VALVE_4_RIGHT")) {
				player.teleport(600, 3409);
				player.message("the raft washes up the sewer, and stops at the fifth island");
				player.message("You need to find the right combination");
				player.message("of the 5 sewer valves above to get further");
			}
			else if (player.getCache().hasKey("VALVE_1_RIGHT") && player.getCache().hasKey("VALVE_2_LEFT") && player.getCache().hasKey("VALVE_3_RIGHT")) {
				player.teleport(622, 3410);
				player.message("the raft washes up the sewer, and stops at the fourth island");
				player.message("You need to find the right combination");
				player.message("of the 5 sewer valves above to get further");
			}
			else if (player.getCache().hasKey("VALVE_1_RIGHT") && player.getCache().hasKey("VALVE_2_LEFT")) {
				player.teleport(622, 3422);
				player.message("the raft washes up the sewer, and stops at the third island");
				player.message("You need to find the right combination");
				player.message("of the 5 sewer valves above to get further");
			}
			else if (player.getCache().hasKey("VALVE_1_RIGHT")) {
				player.teleport(622, 3434);
				player.message("the raft washes up the sewer, and stops at the second island");
				player.message("You need to find the right combination");
				player.message("of the 5 sewer valves above to get further");
			}
			else {
				player.teleport(621, 3465);
				player.message("the raft washes up the sewer, and stops at the first island");
				player.message("You need to find the right combination");
				player.message("of the 5 sewer valves above to get further");
			}
		}
		if (obj.getID() == LOG_RAFT_BACK) {
			player.message("the raft floats down the sewers");
			player.message("to the cave entrance");
			player.teleport(620, 3478);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == SEWER_VALVE_1 || obj.getID() == SEWER_VALVE_2 || obj.getID() == SEWER_VALVE_3 || obj.getID() == SEWER_VALVE_4 || obj.getID() == SEWER_VALVE_5 || obj.getID() == LOG_RAFT || obj.getID() == LOG_RAFT_BACK;
	}
}
