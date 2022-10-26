package orsc;

import com.openrsc.client.entityhandling.defs.SpriteDef;

import orsc.enumerations.ORSCharacterDirection;

public final class ORSCharacter {
	public String accountName;
	public int animationNext;
	public int attackingNpcServerIndex = 0;
	public int attackingPlayerServerIndex = 0;
	public int bubbleItem;
	public int bubbleTimeout = 0;
	public int colourBottom;
	public int colourHair;
	public int colourSkin;
	public int colourTop;
	public int combatTimeout = 0;
	int healTimeout = 0;
	public int currentX;
	public int currentZ;
	public int damageTaken = 0;
	public int skull = 0;
	public int wield = 0;
	public int wield2 = 0;
	int healTaken = 0;
	public String displayName;
	public int[] layerAnimation = new int[12];
	public int healthCurrent = 0;
	public int healthMax = 0;
	public SpriteDef incomingProjectileSprite;
	public int level = -1;
	public String message;
	public int messageTimeout = 0;
	public int waypointIndexNext;
	public int npcId;
	public int projectileRange = 0;
	public int serverIndex;
	public int skullVisible = 0;
	int stepFrame;
	public int waypointIndexCurrent;
	public int[] waypointsX = new int[10];
	public int[] waypointsZ = new int[10];
	public String clanTag;
	public String partyTag;
	public boolean isInvisible = false;
	public boolean isInvulnerable = false;
	int icon = 0;
	public int groupID = Group.DEFAULT_GROUP;
	ORSCharacterDirection direction = ORSCharacterDirection.NORTH;

	// from com.openrsc.server.model.entity.player.Group.java

	private static final int OWNER = 0;
	private static final int ADMIN = 1;
	private static final int SUPER_MOD = 2;
	private static final int MOD = 3;
	private static final int DEV = 5;
	private static final int EVENT = 7;
	private static final int PLAYER_MOD = 8;
	private static final int TESTER = 9;
	private static final int USER = 10;

	public boolean isSuperMod() {
		return groupID == SUPER_MOD || isAdmin();
	}

	public boolean isMod() {
		return groupID == MOD || isSuperMod();
	}

	public boolean isDev() {
		return groupID == TESTER || groupID == DEV || isAdmin();
	}

	private boolean isEvent() {
		return groupID == EVENT || isMod() || isDev();
	}

	public boolean isOwner() {
		return groupID == OWNER;
	}

	public boolean isAdmin() {
		return groupID == ADMIN || isOwner();
	}

	public boolean isStaff() {
		return isEvent();
	}

	public String getStaffName() {
		return Group.getStaffPrefix(groupID) + displayName;
	}
}
