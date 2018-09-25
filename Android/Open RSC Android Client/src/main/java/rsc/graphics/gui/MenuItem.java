package rsc.graphics.gui;

import rsc.enumerations.MenuItemAction;
import rsc.util.GenUtil;

public final class MenuItem {
	int index_or_x;
	int tile_id;
	int dir;
	int m_l;
	int id_or_z;
	String strB;
	String label = null;
	String actor = null;
	MenuItemAction actionID;

	final void set(String label, int var2, int index_or_x, int id_or_z, int tile_id, String dropped2, int var7,
			MenuItemAction actionID, String actor, String dropped, int dir, String strB) {
		try {
			
			this.label = label;
			this.m_l = var2;
			this.id_or_z = id_or_z;
			if (var7 > 69) {
				this.actionID = actionID;
				this.index_or_x = index_or_x;
				this.actor = actor;
				this.tile_id = tile_id;
				this.dir = dir;
				this.strB = strB;
			}
		} catch (RuntimeException var14) {
			throw GenUtil.makeThrowable(var14,
					"t.B(" + (label != null ? "{...}" : "null") + ',' + var2 + ',' + index_or_x + ',' + id_or_z + ','
							+ tile_id + ',' + (dropped2 != null ? "{...}" : "null") + ',' + var7 + ',' + actionID + ','
							+ (actor != null ? "{...}" : "null") + ',' + (dropped != null ? "{...}" : "null") + ','
							+ dir + ',' + (strB != null ? "{...}" : "null") + ')');
		}
	}
}
