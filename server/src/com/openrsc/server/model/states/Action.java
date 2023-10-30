package com.openrsc.server.model.states;

public enum Action {

	idle(EntityType.NONE, "idle"),
	takeobj(EntityType.GROUND_ITEM, "takeobj"),
	dropobj(EntityType.INVENTORY_ITEM,"dropobj"),
	useobj(EntityType.GROUND_ITEM,"useobj"),
	wearobj(EntityType.INVENTORY_ITEM,"wearobj"),
	removeobj(EntityType.INVENTORY_ITEM,"removeobj"),
	spellobj(EntityType.GROUND_ITEM,"spellobj"),
	talknpc(EntityType.NPC,"talknpc"),
	attacknpc(EntityType.NPC,"attacknpc"),
	usenpc(EntityType.NPC,"usenpc"),
	killnpc(EntityType.NPC,"killnpc"),
	escapenpc(EntityType.NPC,"escapenpc"),
	attackbynpc(EntityType.NPC,"attackbynpc"),
	spellnpc(EntityType.NPC,"spellnpc"),
	oploc(EntityType.LOCATION,"oploc"),
	useloc(EntityType.LOCATION,"useloc"),
	spellloc(EntityType.LOCATION,"spellloc"),
	opinv(EntityType.INVENTORY_ITEM,"opinv"),
	useinv(EntityType.INVENTORY_ITEM,"useinv"),
	spellinv(EntityType.INVENTORY_ITEM,"spellinv"),
	opbound(EntityType.BOUNDARY,"opbound"),
	usebound(EntityType.BOUNDARY,"usebound"),
	spellbound(EntityType.BOUNDARY,"spellbound"),
	attackplayer(EntityType.PLAYER,"attackplayer"),
	useplayer(EntityType.PLAYER,"useplayer"),
	spellplayer(EntityType.PLAYER,"spellplayer"),
	spellself(EntityType.NONE,"spellself"),
	spellground(EntityType.COORDINATE,"spellground"),
	// Not fully authentic Actions here
	catgrowth(EntityType.NONE, "catgrowth"),
	wineferment(EntityType.NONE, "wineferment"),
	command(EntityType.NONE, "command"),
	playerdeath(EntityType.NONE, "playerdeath"),
	playerkilledplayer(EntityType.PLAYER, "playerkilledplayer"),
	playerlogin(EntityType.NONE, "playerlogin"),
	playerlogout(EntityType.NONE, "playerlogout"),
	playerrangenpc(EntityType.NPC, "playerrangenpc"),
	playerrangeplayer(EntityType.PLAYER, "playerrangeplayer"),
	startup(EntityType.NONE, "startup"),
	trade(EntityType.NONE, "opnpc"),
	timedevent(EntityType.NONE, "timedevent");

	public static Action getActionFromPlugin(final String pluginInterface) {
		for (Action action : Action.values()) {
			if (action.getDescription().equalsIgnoreCase(pluginInterface)) {
				return action;
			}
		}

		return null;
	}

	private final String description;
	private final EntityType defaultEntityType;

	Action(EntityType defaultEntityType, String description) {
		this.defaultEntityType = defaultEntityType;
		this.description = description;
	}

	public String toString() {
		return description;
	}

	public EntityType getDefaultEntityType() {
		return defaultEntityType;
	}

	public String getDescription() { return description; }
}
