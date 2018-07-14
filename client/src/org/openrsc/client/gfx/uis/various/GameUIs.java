package org.openrsc.client.gfx.uis.various;

import java.util.ArrayList;
import java.util.List;

import org.openrsc.client.mudclient;
import org.openrsc.client.gfx.GraphicalOverlay;
import org.openrsc.client.gfx.uis.AuctionHouse;

public class GameUIs {
	public static void reload() {
		overlays.clear();
		overlays.add(new AuctionHouse(mudclient.getInstance()));
	}

	public static List<GraphicalOverlay> overlays = new ArrayList<GraphicalOverlay>();

	static {
		reload();
	}

}
