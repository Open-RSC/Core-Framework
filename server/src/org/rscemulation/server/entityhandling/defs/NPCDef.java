package org.rscemulation.server.entityhandling.defs;

import org.rscemulation.server.entityhandling.defs.extras.ItemDropDef;
import java.util.ArrayList;

public class NPCDef extends EntityDef {

	public int retreatHits;
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
	public int respawnTime;
	public boolean aggressive, blocks, retreats, follows, undead, dragon, armoured;
	public ArrayList<ItemDropDef> drops = new ArrayList<ItemDropDef>();
	
	public NPCDef(String name) {
		this.name = name;
	}
	
	public boolean blocks() {
		return blocks;
	}
	
	public boolean follows() {
		return follows;
	}
	
	public boolean isUndead() {
		return undead;
	}
	
	public boolean isDragon() {
		return dragon;
	}
	
	public void addDrop(ItemDropDef drop) {
		drops.add(drop);
	}
	
	public ArrayList<ItemDropDef> getDrops() {
		return drops;
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
	
	public int respawnTime() {
		return respawnTime;
	}
	
	public boolean isAggressive() {
		return attackable && aggressive;
	}

	public boolean isArmoured() {
		return armoured;
	}
}
