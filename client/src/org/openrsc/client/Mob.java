package org.openrsc.client;

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
		return groupID == 3 || isAdmin() || isSuperMod();
	}
	
	public boolean isDev() {
		return groupID == 8 || isAdmin();
	}	
	
	public boolean isEvent()
	{
		return groupID == 9 || isAdmin();
	}
	
	public boolean isAdmin() {
		return groupID == 1;
	}	
    
    public boolean isStaff(){
        return isMod() || isDev() || isEvent();
    }
    
    public boolean isSubscriber() {
        return groupID == 11 || isStaff();
    }
    
	public static final String getNameRankSprite(int rank) {
        dummyMob.groupID = rank;
        
        if (dummyMob.isAdmin())
            return "#adm#";
        else if (dummyMob.isSuperMod())
            return "#mod#";
        else if (dummyMob.isMod())
            return "#mod#";
        else if (dummyMob.isDev())
            return "#dev#";
        else if (dummyMob.isEvent())
            return "#eve#";
        else
            return "";
	}

	public static final String getNameRankColour(int rank) {
        dummyMob.groupID = rank;
        
        if (dummyMob.isAdmin())
            return "@gre@";
        else if (dummyMob.isSuperMod())
            return "@blu@";
        else if (dummyMob.isMod())
            return "@yel@";
        else if (dummyMob.isDev())
            return "@red@";
        else if (dummyMob.isEvent())
            return "@eve@";
        else if (dummyMob.isSubscriber())
            return "@or2@";
        else
            return "@yel@";
	}
    
    public String getStaffName() {
        if (this.isAdmin())
            return "#adm#@gre@" + this.name;
        else if (this.isSuperMod())
            return "#mod#@blu@" + this.name;
        else if (this.isSuperMod())
            return "#mod#@yel@" + this.name;
        else if (this.isDev())
            return "#dev#@red@" + this.name;
        else if (this.isEvent())
            return "#eve#@eve@" + this.name;
        else if (this.isSubscriber())
            return "@or2@" + this.name;
        else
            return "@whi@" + this.name;
    }
}