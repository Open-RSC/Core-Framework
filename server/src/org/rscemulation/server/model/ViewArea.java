package org.rscemulation.server.model;

import java.util.ArrayList;
import java.util.List;

import org.rscemulation.server.model.GameObject;
import org.rscemulation.server.model.Item;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;

public class ViewArea {
	
	private Entity entity;

	public ViewArea(Entity entity) {
		this.entity = entity;
	}
	
	public List<Player> getPlayersInView() {
		List<Player> players = new ArrayList<Player>();
		if (entity.getUpdateZone() != null) {
			for (Zone zone : entity.getUpdateZone()) {
				for (Player p : zone.getPlayers()) {
					if (p.withinRange(entity, 15))
						players.add(p);
				}	
			}
		}
		return players;
	}
	
	public List<Item> getItemsInView() {
		List<Item> items = new ArrayList<Item>();
		if (entity.getUpdateZone() != null) {
			for (Zone zone : entity.getUpdateZone()) {
				for (Item i : zone.getItems()) {
					if (i.withinRange(entity, 15))
						items.add(i);
				}
			}
		}
		return items;
	}

	public List<GameObject> getGameObjectsInView() {
		List<GameObject> objects = new ArrayList<GameObject>();
		if (entity.getUpdateZone() != null) {
			for (Zone zone : entity.getUpdateZone()) {
				for (GameObject o : zone.getObjects()) {
					if(o.withinRange(entity, 15))
						objects.add(o);
				}
				for(GameObject d : zone.getDoors()) {
					if (d.withinRange(entity, 15))
						objects.add(d);
				}
			}
		}
		return objects;
	}

	public List<Npc> getNpcsInView() {
		List<Npc> npcs = new ArrayList<Npc>();
		if (entity.getUpdateZone() != null) {
			for (Zone zone : entity.getUpdateZone()) {
				for (Npc n : zone.getNpcs()) {
					if (n.withinRange(entity, 15))
						npcs.add(n);
				}
			}
		}
		return npcs;
	}
}