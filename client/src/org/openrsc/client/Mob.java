package org.openrsc.client;

import org.openrsc.group.Group;

public final class Mob {
	public Mob(){}
	/**
	* Appearance Values
	*/
    public int groupID;
    public long nameLong;
    public String name;
    public int serverIndex;
    public int appearanceID;
	public int wornItemsID;

    public int currentX;
    public int currentY;
    public int stepCount;
    public int currentSprite;
    public int nextSprite;
    public int waypointEndSprite;
    public int waypointCurrent;
    public int waypointsX[] = new int[10];
    public int waypointsY[] = new int[10];
    public int animationCount[] = new int[12];
    public String lastMessage;
    public int lastMessageTimeout;
    public int type;	
    public int hitPointsCurrent;
    public int hitPointsBase;
    public int combatTimer;
    public int level = -1;
    public int colourHairType;
    public int colourTopType;
    public int colourBottomType;
    public int colourSkinType;
    public int attackingCameraInt;
    public int attackingMobIndex;
    public int attackingNpcIndex;
    public int anInt162;
    public int anInt163;
    public int anInt164;
    public int anInt176;
    public int skull;
    public long lastMoved = System.currentTimeMillis();
    public boolean isInvisible = false;
    public boolean isInvulnerable = false;
    
    public static Mob dummyMob = new Mob();
    
	public boolean isSuperMod() {
		return groupID == 2 || isAdmin();
	}
    
	public boolean isMod() {
		return groupID == 3 || isSuperMod();
	}
	
	public boolean isDev() {
		return groupID == 8 || isAdmin();
	}	
	
	public boolean isEvent()
	{
		return groupID == 9 || isAdmin();
	}
	
	public boolean isOwner() {
		return groupID == 0;
	}	
    
	public boolean isAdmin() {
		return groupID == 1 || isOwner();
	}	
    
    public boolean isStaff(){
        return isMod() || isDev() || isEvent();
    }
    
    public boolean isSubscriber() {
        return groupID == 11 || isStaff();
    }

    public String getStaffName() {
        return Group.getStaffPrefix(groupID) + name;
    }
}