package org.rscemulation.client.entityhandling.defs;

	public class NPCDef extends EntityDef {
	public String command;
	public int[] sprites;
	public int hairColour;
	public int topColour;
	public int bottomColour;
	public int skinColour;
	public int camera1, camera2;
	public int walkModel, combatModel, combatSprite;
	public int hits;
	public int attack;
	public int defense;
	public int strength;
	public boolean attackable;

	public NPCDef(String name, String description, String command, int attack, int strength, int hits, int defense, boolean attackable, int[] sprites, int hairColour, int topColour, int bottomColour, int skinColour, int camera1, int camera2, int walkModel, int combatModel, int combatSprite, int id) {
		super(name, description, id);
		this.command = command;
		this.attack = attack;
		this.strength = strength;
		this.hits = hits;
		this.defense = defense;
		this.attackable = attackable;
		this.sprites = sprites;
		this.hairColour = hairColour;
		this.topColour = topColour;
		this.bottomColour = bottomColour;
		this.skinColour = skinColour;
		this.camera1 = camera1;
		this.camera2 = camera2;
		this.walkModel = walkModel;
		this.combatModel = combatModel;
		this.combatSprite = combatSprite;
	}

	public String getCommand() {
		return command;
	}
	public int getSprite(int index) {
		return sprites[index];
	}
	public int getHairColour() {
		return hairColour;
	}
	public int getTopColour() {
		return topColour;
	}
	public int getBottomColour() {
		return bottomColour;
	}
	public int getSkinColour() {
		return skinColour;
	}
	public int getCamera1() {
		return camera1;
	}
	public int getCamera2() {
		return camera2;
	}
	public int getWalkModel() {
		return walkModel;
	}
	public int getCombatModel() {
		return combatModel;
	}
	public int getCombatSprite() {
		return combatSprite;
	}
	public int getHits() {
		return hits;
	}
	public int getAtt() {
		return attack;
	}
	public int getDef() {
		return defense;
	}
	public int getStr() {
		return strength;
	}
	public int[] getStats() {
		return new int[]{attack, defense, strength};
	}
	public boolean isAttackable() {
		return attackable;
	}
}
