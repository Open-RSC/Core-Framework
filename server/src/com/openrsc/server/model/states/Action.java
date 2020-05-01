package com.openrsc.server.model.states;

public enum Action {

	/*ATTACKING_MOB("Attacking an NPC or Player"),
	CASTING_GITEM("Casting a Spell on a Ground Item"),
	CASTING_MOB("Casting a Spell on an NPC or Player"),
	DROPPING_GITEM("Dropping an Item"),
	DUELING_PLAYER("Dueling with another player"),
	FIGHTING_MOB("In combat with an NPC or Player"),
	IDLE("Idle or Walking"),
	RANGING_MOB("Ranging an NPC or Player"),
	TAKING_GITEM("Picking up an Item"),
	TALKING_MOB("Talking to an NPC"),
	USING_DOOR("Using a Door"),
	USING_Item_ON_DOOR("Using an Item on a Door"),
	USING_Item_ON_GITEM("Using an Item on a Ground Item"),
	USING_Item_ON_NPC("Using an Item on an NPC"),
	USING_Item_ON_OBJECT("Using an Item on an Object"),
	USING_Item_ON_PLAYER("Using an Item on a Player"),
	USING_OBJECT("Using an Object");*/

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
	command(EntityType.NONE, "command"),
	deposit(EntityType.NONE, "deposit"),
	indirecttalktonpc(EntityType.NONE, "indirecttalktonpc"),
	playerdeath(EntityType.NONE, "playerdeath"),
	playerkilledplayer(EntityType.PLAYER, "playerkilledplayer"),
	playerlogin(EntityType.NONE, "playerlogin"),
	playerlogout(EntityType.NONE, "playerlogout"),
	playerrangenpc(EntityType.NPC, "playerrangenpc"),
	playerrangeplayer(EntityType.PLAYER, "playerrangeplayer"),
	teleport(EntityType.NONE, "teleport"),
	withdraw(EntityType.NONE, "withdraw"),
	startup(EntityType.NONE, "startup"),
	trade(EntityType.NONE, "opnpc");

	public static Action getActionFromPlugin(final String pluginInterface) {
		for (Action action : Action.values()) {
			if (action.getDescription().equalsIgnoreCase(pluginInterface)) {
				return action;
			}
		}

		return null;
	}

	private String description;
	private EntityType entityType;

	Action(final EntityType entityType, final String description) {
		this.entityType = entityType;
		this.description = description;
	}

	public String toString() {
		return description;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public String getDescription() { return description; }
}
