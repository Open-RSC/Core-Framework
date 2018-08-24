package rsc;

import rsc.enumerations.RSCharacterDirection;

public final class RSCharacter {
	public String accountName;
	RSCharacterDirection direction = RSCharacterDirection.NORTH;
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
	public int currentX;
	public int currentZ;
	public int damageTaken = 0;
	public String displayName;
	public int[] layerAnimation = new int[12];
	public int healthCurrent = 0;
	public int healthMax = 0;
	public int incomingProjectileSprite = 0;
	public int level = -1;
	public String message;
	public int messageTimeout = 0;
	public int movingStep;
	public int npcId;
	public int projectileRange = 0;
	public int serverIndex;
	public int skullVisible = 0;
	public int stepFrame;
	public int waypointCurrent;

	public int[] waypointsX = new int[10];

	public int[] waypointsZ = new int[10];
	public String clanTag;
}
