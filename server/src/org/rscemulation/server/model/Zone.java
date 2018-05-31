package org.rscemulation.server.model;

import java.util.ArrayList;
import java.util.List;

public class Zone {
	private List<Player> players = new ArrayList<Player>();
	private List<Npc> npcs = new ArrayList<Npc>();
	private List<GameObject> objects = new ArrayList<GameObject>(), doors = new ArrayList<GameObject>();
	private List<Item> items = new ArrayList<Item>();

	public Item getItemAt(int x, int y) {
		synchronized (items) {
			for (Item i : items) {
				if (i.getX() != x || i.getY() != y)
					continue;
				return i;
			}
			return null;
		}
	}

	public Item getSpecificItemVisibleTo(short x, short y, short id, Player player) {
		synchronized(items) {
			for (Item i : items) {
				if (i.getID() != id || i.getX() != x || i.getY() != y || !i.visibleTo(player))
					continue;
				return i;
			}
			return null;
		}
	}

	public GameObject getObjectAt(int x, int y) {
		synchronized (objects) {
			for (GameObject o : objects) {
				if (o.getX() != x || o.getY() != y)
					continue;
				return o;
			}
			return null;
		}
	}
	
	public GameObject getDoorAt(int x, int y) {
		synchronized(doors) {
			for (GameObject o : doors) {
				if (o.getX() != x || o.getY() != y)
					continue;
				return o;
			}
			return null;
		}
	}

	public Npc getNpcAt(int x, int y) {
		synchronized (npcs) {
			for (Npc n : npcs) {
				if (n.getX() != x || n.getY() != y)
					continue;
				return n;
			}
			return null;
		}
	}

	public Player getPlayerAt(int x, int y) {
		synchronized (players) {
			for (Player p : players) {
				if (p.getX() != x || p.getY() != y)
					continue;
				return p;
			}
			return null;
		}
	}

	public boolean entityExists(Entity e) {
		if (e instanceof Player)
			return playerExists((Player)e);
		else if(e instanceof Npc)
			return npcExists((Npc)e);
		else if(e instanceof GameObject) {
			GameObject o = ((GameObject)e);
			if (o.getType() == 0)
				return objectExists(o);
			return doorExists(o);
		} else if (e instanceof Item)
			return itemExists((Item)e);
		else
			return false;
	}

	private boolean itemExists(Item i) {
		synchronized(items) {
			for (Item item : items) {
				if (i.getID() != item.getID() || i.getAmount() != item.getAmount() || i.getX() != item.getX() || i.getY() != item.getY())
					continue;
				return true;
			}
			return false;
		}
	}

	private boolean objectExists(GameObject o) {
		synchronized(objects) {
			for (GameObject object : objects) {
				if (o.getID() != object.getID() || o.getX() != object.getX() || o.getY() != object.getY())
					continue;
				return true;
			}
			return false;
		}
	}

	private boolean doorExists(GameObject d) {
		synchronized(doors) {
			for (GameObject door : doors) {
				if (d.getID() != door.getID() || d.getX() != door.getX() || d.getY() != door.getY())
					continue;
				return true;
			}
			return false;
		}
	}

	private boolean npcExists(Npc n) {
		synchronized(npcs) {
			for (Npc npc : npcs) {
				if (n.getID() != npc.getID() || n.getX() != npc.getX() || n.getY() != npc.getY())
					continue;
				return true;
			}
			return false;
		}
	}
	
	private boolean playerExists(Player p) {
		synchronized(players) {
			for (Player player : players) {
				if (!player.equals(p) || player.getX() != p.getX() || player.getY() != p.getY())
					continue;
				return true;
			}
			return false;
		}
	}

	public void add(Entity entity) {
		if (entity instanceof Player) {
			synchronized (players) {
				players.add((Player)entity);
			}
		} else if (entity instanceof Npc) {
			synchronized(npcs) {
				npcs.add((Npc)entity);
			}
		} else if (entity instanceof Item) {
			synchronized (items) {
				items.add((Item)entity);
			}
		} else if (entity instanceof GameObject && ((GameObject)entity).getType() == 0) {
			synchronized (objects) {
				objects.add((GameObject)entity);
			}
		} else if (entity instanceof GameObject && ((GameObject)entity).getType() == 1) {
			synchronized (doors) {
				doors.add((GameObject)entity);
			}
		}
	}

	public void remove(Entity entity) {
		if (entity instanceof Player) {
			synchronized (players) {
				players.remove(entity);
			}
		} else if (entity instanceof Npc) {
			synchronized (npcs) {
				npcs.remove(entity);
			}
		} else if (entity instanceof Item) {
			synchronized (items) {
				items.remove(entity);
			}
		} else if (entity instanceof GameObject && ((GameObject)entity).getType() == 0) {
			synchronized (objects) {
				objects.remove(entity);
			}
		} else if (entity instanceof GameObject && ((GameObject)entity).getType() == 1) {
			synchronized (doors) {
				doors.remove(entity);
			}
		}
	}

	private int top, bottom, left, right;

	public Zone(int x, int y) {
		this.top = y;
		this.right = x;
		this.bottom = y + 48;
		this.left = x + 48;
	}
	
	public int getX() {
		return right;
	}

	public int getY() {
		return top;
	}
	
	public boolean withinZone(int x, int y) {
		return x < left && x >= right && y < bottom && y >= top;
	}

	public List<Player> getPlayers() {
		synchronized(players) {
			return players;
		}
	}	

	public List<Npc> getNpcs() {
		return npcs;
	}

	public List<Item> getItems() {
		return items;
	}

	public List<GameObject> getObjects() {
		return objects;
	}

	public List<GameObject> getDoors() {
		return doors;
	}

	public List<Entity> getEntitiesAt(int x, int y) {
		List<Entity> ret = new ArrayList<Entity>();
		ret.addAll(getItemsAt(x, y));
		ret.addAll(getPlayersAt(x, y));
		ret.addAll(getNpcsAt(x, y));
		ret.addAll(getObjectsAt(x, y));
		ret.addAll(getDoorsAt(x, y));
		return ret;
	}

	public List<Item> getItemsAt(int x, int y) {
		List<Item> ret = new ArrayList<Item>();
		synchronized (items) {
			for (Item i : items) {
				if (i.getX() != x || i.getY() != y)
					continue;
				ret.add(i);
			}
		}
		return ret;
	}

	public List<Npc> getNpcsAt(int x, int y) {
		List<Npc> ret = new ArrayList<Npc>();
		synchronized(npcs) {
			for (Npc n : npcs) {
				if (n.getX() != x || n.getY() != y)
					continue;
				ret.add(n);
			}
		}
		return ret;
	}

	public List<Player> getPlayersAt(int x, int y) {
		List<Player> ret = new ArrayList<Player>();
		synchronized (players) {
			for (Player p : players) {
				if (p.getX() != x || p.getY() != y)
					continue;
				ret.add(p);
			}
		}
		return ret;
	}

	public List<GameObject> getObjectsAt(int x, int y) {
		List<GameObject> ret = new ArrayList<GameObject>();
		synchronized (objects) {
			for (GameObject o : objects) {
				if (o.getX() != x || o.getY() != y)
					continue;
				ret.add(o);
			}
		}
		return ret;
	}

	public List<GameObject> getDoorsAt(int x, int y) {
		List<GameObject> ret = new ArrayList<GameObject>();
		synchronized (doors) {
			for (GameObject d : doors) {
				if (d.getX() != x || d.getY() != y)
					continue;
				ret.add(d);
			}
		}
		return ret;
	}
}