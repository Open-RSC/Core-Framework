package com.openrsc.server.plugins.misc;


import com.mysql.jdbc.Util;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import javax.swing.text.Utilities;
import java.util.ArrayList;

public class ExitPortal implements ObjectActionListener, ObjectActionExecutiveListener {
	ArrayList<Point> level1Portals;
	ArrayList<Point> level2Portals;
	ArrayList<Point> level3Portals;

	public ExitPortal() {
		this.initPortals();
	}
	private void initPortals() {
		this.level1Portals = new ArrayList<>();
		this.level2Portals = new ArrayList<>();
		this.level3Portals = new ArrayList<>();
		this.level1Portals.add(new Point(825, 67));
		this.level1Portals.add(new Point(836, 76));
		this.level1Portals.add(new Point(843, 79));
		this.level1Portals.add(new Point(849, 83));
		this.level1Portals.add(new Point(859, 79));
		this.level2Portals.addAll(level1Portals);
		this.level2Portals.add(new Point(835, 71));
		this.level2Portals.add(new Point(839, 75));
		this.level2Portals.add(new Point(851, 60));
		this.level3Portals.addAll(level2Portals);
		this.level3Portals.add(new Point(842, 55));
		this.level3Portals.add(new Point(830, 57));
		this.level3Portals.add(new Point(829, 64));
		this.level3Portals.add(new Point(823, 51));

	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getGameObjectDef().getObjectModel().equalsIgnoreCase("portal");
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {

		if (command.equalsIgnoreCase("exit")) {
			switch (obj.getID()) {
				case 1214: //air altar
					player.teleport(305, 594, false);
					break;
				case 1215: //mind altar
					player.teleport(298, 441, false);
					break;
				case 1216://water altar
					player.teleport(148, 683, false);
					break;
				case 1217://earth altar
					player.teleport(63, 467, false);
					break;
				case 1218://fire altar
					player.teleport(53, 634, false);
					break;
				case 1219://body altar
					player.teleport(260, 502, false);
					break;
				case 1220://cosmic altar
					player.teleport(104, 3566, false);
					break;
				case 1221://chaos altar
					player.teleport(236, 376, false);
					break;
				case 1222://nature altar
					player.teleport(393, 803, false);
					break;
				case 1223://law altar
					player.teleport(410, 537, false);
					break;
				case 1224://death altar
					player.teleport(0, 0, false);
					break;
				case 1225://blood altar
					player.teleport(0, 0, false);
					break;
				case 1226://rune essence mine
				{
					if (player.getCache().hasKey("essence_entrance")) {
						if (player.getCache().getInt("essence_entrance") == 0) {
							player.teleport(101, 523, false);
						} else {
							player.teleport(222, 3517, false);
						}
						player.getCache().remove("essence_entrance");
					} else //shouldn't happen unless a gm teleports to essence
						player.teleport(101, 523, false);
				}
				break;
			}
		} else if (command.equalsIgnoreCase("take")) {
			int rand = 0;
			switch (obj.getID()) {
				case 1228: //chaos altar maze begin
					player.teleport(835, 71, false);
					break;
				case 1229: //chaos altar maze begin
					player.teleport(825, 76, false);
					break;
				case 1230: //chaos altar maze begin
					player.teleport(842, 55, false);
					break;
				case 1231: //chaos altar maze begin
					player.teleport(819, 51, false);
					break;
				case 1232: //chaos altar maze begin
					player.teleport(859, 51, false);
					break;
				case 1233: //level1 random wrong portal
					rand = DataConversions.random(1,level1Portals.size()) - 1;
					player.teleport(level1Portals.get(rand).getX(),level1Portals.get(rand).getY());
					break;
				case 1234: //level2 random wrong portal
					rand = DataConversions.random(1,level2Portals.size()) - 1;
					player.teleport(level2Portals.get(rand).getX(),level2Portals.get(rand).getY());
					break;
				case 1235: //level3 random wrong portal
					rand = DataConversions.random(1,level3Portals.size()) - 1;
					player.teleport(level3Portals.get(rand).getX(),level3Portals.get(rand).getY());
					break;
				case 1236: //exit portal at the altar
					break;
			}
		}
	}
}
