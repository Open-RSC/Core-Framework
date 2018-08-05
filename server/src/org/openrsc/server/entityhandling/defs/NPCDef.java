package org.openrsc.server.entityhandling.defs;

import com.runescape.entity.attribute.DropItemAttr;
import org.openrsc.server.entityhandling.defs.extras.ItemDropDef;
import java.util.ArrayList;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Point;
import org.openrsc.server.model.World;
import org.openrsc.server.states.CombatState;
import org.openrsc.server.util.DataConversions;

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
    
    public static void spawnEventNpcs(int npcID, int npcAmt, int item_id, int item_amount, Point location, int npc_timeout)
    {
        InvItem item = new InvItem(item_id, item_amount);
        int random = DataConversions.random(0, npcAmt);
        int x = 0;
        int y = 0;
        int baseX = location.getX();
        int baseY = location.getY();
        int nextX = 0;
        int nextY = 0;
        int dX = 0;
        int dY = 0;
        int minX = 0;
        int minY = 0;
        int maxX = 0;
        int maxY = 0;
        int scanned = -1;
        while (scanned < npcAmt) {
            scanned++;
            if (dX < 0) {
                x -= 1;
                if (x == minX) {
                    dX = 0;
                    dY = nextY;
                    if (dY < 0)
                        minY -= 1;
                    else
                        maxY += 1;
                    nextX = 1;
                }
            } else if (dX > 0) {
                x += 1;
                if (x == maxX) {
                    dX = 0;
                    dY = nextY;
                    if (dY < 0)
                        minY -=1;
                    else
                        maxY += 1;
                    nextX = -1;
                }
            } else {
                if (dY < 0) {
                    y -= 1;
                    if (y == minY) {
                        dY = 0;
                        dX = nextX;
                        if (dX < 0)
                            minX -= 1;
                        else
                            maxX += 1;
                        nextY = 1;
                    }
                } else if (dY > 0) {
                    y += 1;
                    if (y == maxY) {
                        dY = 0;
                        dX = nextX;
                        if (dX < 0)
                            minX -= 1;
                        else
                            maxX += 1;
                        nextY = -1;
                    }
                } else {
                    minY -= 1;
                    dY = -1;
                    nextX = 1;
                }
            }
            if (!((baseX + x) < 0 || (baseY + y) < 0 || ((baseX + x) >= World.MAX_WIDTH) || ((baseY + y) >= World.MAX_HEIGHT))) {
                if ((World.mapValues[baseX + x][baseY + y] & 64) == 0) {
                    final Npc n = new Npc(npcID, baseX + x, baseY + y, baseX + x - 20, baseX + x + 20, baseY + y - 20, baseY + y + 20);

                    if (scanned == random) {
                        DropItemAttr attr = new DropItemAttr(n, item);
                        n.addAttr(attr);
                    }

                    n.setRespawn(false);
                    World.registerEntity(n);
                    World.getDelayedEventHandler().add(new SingleEvent(null, npc_timeout /* 2 minutes */) {
                        public void action() {
                            Mob opponent = n.getOpponent();
                            if (opponent != null)
                                opponent.resetCombat(CombatState.ERROR);
                            n.resetCombat(CombatState.ERROR);
                            n.remove();
                        }
                    });
                }
            }
        }
    }
}
