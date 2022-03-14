package com.openrsc.server.external;

/**
 * The definition wrapper for npcs
 */
public class NPCDef extends EntityDef {
	/**
	 * Whether the npc is aggressive
	 */
	public Boolean aggressive;
	/**
	 * The attack lvl
	 */
	public int attack;
	/**
	 * Whether the npc is attackable
	 */
	public Boolean attackable;
	public Boolean members;
	/**
	 * Colour of our legs
	 */
	public int bottomColour;
	/**
	 * Something to do with the camera
	 */
	public int camera1, camera2;
	/**
	 * The primary command
	 */
	public String command1, command2;
	/**
	 * The def lvl
	 */
	public int defense;
	public int ranged;
	/**
	 * Possible drops
	 */
	public ItemDropDef[] drops;
	/**
	 * Colour of our hair
	 */
	public int hairColour;
	/**
	 * The hit points
	 */
	public int hits;
	/**
	 * How long the npc takes to respawn
	 */
	public int respawnTime;
	/**
	 * Skin colour
	 */
	public int skinColour;
	/**
	 * Sprites used to make up this npc
	 */
	public int[] sprites = new int[12];
	/**
	 * The strength lvl
	 */
	public int strength;

	/**
	 * combat level because why not,
	 * calculation of strength, def, attack and hits -
	 * is wrong compared to npcs combat level on a few monsters due to RSC set stats on mobs.
	 */
	public int combatLevel;
	/**
	 * Colour of our top
	 */
	public int topColour;
	/**
	 * Something to do with models
	 */
	public int walkModel, combatModel, combatSprite;

	/**
	 * Round Mode of xp given from mob (Xp = 2 * RoundMode(cb lvl) + 20)
	 * -1: Floor
	 * 0: Natural round
	 * 1: Ceil
	 * Default: Cast to int without any Math function
	 */
	public int roundMode;

	private int id;

	public NPCDef(NPCDef.NPCDefinitionBuilder builder) {
		this.id = builder.id;
		super.name = builder.name;
		super.description = builder.description;
		this.command1 = builder.command1;
		this.attack = builder.attack;
		this.strength = builder.strength;
		this.hits = builder.hits;
		this.defense = builder.defense;
		this.ranged = builder.ranged;
		this.combatLevel = builder.combatLevel;
		this.members = builder.members;
		this.attackable = builder.attackable;
		this.aggressive = builder.aggressive;
	}

	public NPCDef() { }

	public int getAtt() {
		return attack;
	}

	public int getBottomColour() {
		return bottomColour;
	}

	public int getCamera1() {
		return camera1;
	}

	public int getCamera2() {
		return camera2;
	}

	public int getCombatModel() {
		return combatModel;
	}

	public int getCombatSprite() {
		return combatSprite;
	}

	public String getCommand1() {
		return command1;
	}
	public void setCommand1(String command) {
		command1 = command;
	}

	public String getCommand2() {
		return command2;
	}
	public void setCommand2(String command) {
		command2 = command;
	}

	public int getDef() {
		return defense;
	}
	public int getRanged() {
		return ranged;
	}

	public ItemDropDef[] getDrops() {
		return drops;
	}

	public int getHairColour() {
		return hairColour;
	}

	public int getHits() {
		return hits;
	}

	public int getSkinColour() {
		return skinColour;
	}

	public int getSprite(int index) {
		return sprites[index];
	}

	public int[] getStats() {
		return new int[]{attack, defense, strength};
	}

	public int getStr() {
		return strength;
	}

	public int getTopColour() {
		return topColour;
	}

	public int getWalkModel() {
		return walkModel;
	}

	public boolean isAggressive() {
		return attackable && aggressive;
	}

	public boolean isAttackable() {
		return attackable;
	}

	public int respawnTime() {
		return respawnTime;
	}

	public boolean isMembers() {
		return members;
	}

	public int roundMode() { return roundMode; }

	public static class NPCDefinitionBuilder
	{
		private String command1;
		private String description;
		private String name;
		private int attack;
		private int strength;
		private int hits;
		private int defense;
		private int ranged;
		private int combatLevel;
		private Boolean members;
		private Boolean attackable;
		private Boolean aggressive;
		private int id;

		public NPCDefinitionBuilder(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public NPCDef.NPCDefinitionBuilder description(String description) {
			this.description = description;
			return this;
		}

		public NPCDef.NPCDefinitionBuilder command(String command) {
			this.command1 = command;
			return this;
		}

		public NPCDef.NPCDefinitionBuilder attack(int attack) {
			this.attack = attack;
			return this;
		}

		public NPCDef.NPCDefinitionBuilder strength(int strength) {
			this.strength = strength;
			return this;
		}

		public NPCDef.NPCDefinitionBuilder hits(int hits) {
			this.hits = hits;
			return this;
		}

		public NPCDef.NPCDefinitionBuilder defense(int defense) {
			this.defense = defense;
			return this;
		}

		public NPCDef.NPCDefinitionBuilder ranged(int ranged) {
			this.ranged = ranged;
			return this;
		}

		public NPCDef.NPCDefinitionBuilder combatLevel(int combatLevel) {
			this.combatLevel = combatLevel;
			return this;
		}

		public NPCDef.NPCDefinitionBuilder members(Boolean members) {
			this.members = members;
			return this;
		}

		public NPCDef.NPCDefinitionBuilder attackable(Boolean attackable) {
			this.attackable = attackable;
			return this;
		}

		public NPCDef.NPCDefinitionBuilder aggressive(Boolean aggressive) {
			this.aggressive = aggressive;
			return this;
		}

		public NPCDef build() {
			NPCDef definition =  new NPCDef(this);
			return definition;
		}
	}
}
