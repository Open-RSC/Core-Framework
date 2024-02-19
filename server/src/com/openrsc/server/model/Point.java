package com.openrsc.server.model;

import com.openrsc.server.model.entity.WildernessLocation;
import com.openrsc.server.model.entity.WildernessLocation.WildState;
import com.openrsc.server.model.world.Area;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import java.util.ArrayList;

public class Point {

	private static ArrayList<WildernessLocation> wildernessLocations = new ArrayList<WildernessLocation>();

	static {
		/* Edgeville dungeon wilderness, always members wild */
		wildernessLocations.add(new WildernessLocation(WildState.MEMBERS_WILD, 195, 3206, 234, 3258));
		/* Red Dragons, always P2P */
		wildernessLocations.add(new WildernessLocation(WildState.MEMBERS_WILD, 129, 180, 163, 219));
		/* Underground Lava maze, always P2P */
		wildernessLocations.add(new WildernessLocation(WildState.MEMBERS_WILD, 243, 2988, 283, 3020));
	}

	protected short x, y;

	protected Point() {
	}

	public Point(int x, int y) {
		this((short)x, (short)y);
	}

	public Point(short x, short y) {
		this.x = x;
		this.y = y;
	}

	public static Point location(int x, int y) {
		return location((short)x, (short)y);
	}
	public static Point location(short x, short y) {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException(
				"Point may not contain non negative values x:" + x + " y:"
					+ y);
		}
		return new Point(x, y);
	}

	public String returnLocationName() {
		if (inHeroQuestRangeRoom()) {
			return "Hero's Quest Range Room";
		}

		else if (inTutorialLanding()) {
			return "Tutorial Landing";
		}

		else if (aroundTutorialRatZone()) {
			return "Tutorial Rats";
		}

		else if (onTutorialIsland()) {
			return "Tutorial Island";
		}

		else if (onBlackHole()) {
			return "Black Hole";
		}

		else if (inModRoom()) {
			return "Mod Room";
		}

		else if (inFreeWild()) {
			return "F2P Wilderness";
		}

		else if (inWilderness()) {
			return "Wilderness";
		}

		else if (inVarrock()) {
			return "Varrock";
		}

		else if (inEdgeville()) {
			return "Edgeville";
		}

		else if (inBarbVillage()) {
			return "Barbarian Village";
		}

		else if (inDraynor()) {
			return "Draynor";
		}

		else if (inLumbridge()) {
			return "Lumbridge";
		}

		else if (inAlKharid()) {
			return "Al Kharid";
		}

		else if (inFalador()) {
			return "Falador";
		}

		else if (inPortSarim()) {
			return "Port Sarim";
		}

		else if (inTaverly()) {
			return "Taverly";
		}

		else if (inEntrana()) {
			return "Entrana";
		}

		else if (inCatherby()) {
			return "Catherby";
		}

		else if (isInSeersPartyHall()) {
			return "Seers Party Hall";
		}

		else if (inSeers()) {
			return "Seers";
		}

		else if (inGnomeStronghold()) {
			return "Gnome Stronghold";
		}

		else if (inArdougne()) {
			return "Ardougne";
		}

		else if (inYanille()) {
			return "Yanille";
		}

		else if (inBrimhaven()) {
			return "Brimhaven";
		}

		else if (inShiloVillage()) {
			return "Shilo Village";
		}

		else if (inKaramja()) {
			return "Karamja";
		}

		else if (isInFisherKingRealm()) {
			return "Fisher King Realm";
		}

		else if (isInsideGrandTreeGround()) {
			return "Grand Tree";
		}

		return getX() + "," + getY();
	}

	public static boolean inWilderness(int x, int y) {
		int wild = 2203 - (y + (1776 - (944 * (int) (y / 944))));
		if (x + 2304 >= 2640) {
			wild = -50;
		}
		if (wild > 0) {
			return (1 + wild / 6) >= 1;
		}
		return false;
	}

	public boolean isMembersWild() {
		if (inWilderness()) {
			for (WildernessLocation location : wildernessLocations) {
				if (x >= location.getMinX() && y >= location.getMinY() && x <= location.getMaxX() && y <= location.getMaxY()) {
					if (location.getWildState() == WildState.MEMBERS_WILD) {
						return true;
					} else if (location.getWildState() == WildState.FREE_WILD) {
						return false;
					}
				}
			}
			/* If its allowed in these wild levels */
			return wildernessLevel() >= 48 && wildernessLevel() <= 56;
			/* It is F2P */
		}
		/* Not in wild, its P2P */
		return true;
	}

	public WildernessLocation getWildernessLocation() {
		for (WildernessLocation location : wildernessLocations) {
			if (x > location.getMinX() && y > location.getMinY() && x < location.getMaxX() && y < location.getMaxY()) {
				return location;
			}
		}
		return null;
	}

	public final boolean withinRange(Point point, int radius) {
		/*int xDiff = this.x - p.x;
		int yDiff = this.y - p.y;

		return xDiff <= radius && xDiff >= -radius && yDiff <= radius
			&& yDiff >= -radius;*/
		return getDistancePythagoras(point) <= radius;
	}

	public final boolean withinGridRange(Point point, int gridSize) {
		// Snap coordinates to an 8x8 grid
		// radius is compared in multiples of 8
		final int xDiff = (this.x >> 3) - (point.x >> 3);
		final int yDiff = (this.y >> 3) - (point.y >> 3);
		return xDiff <= gridSize && xDiff >= -gridSize && yDiff <= gridSize && yDiff >= -gridSize;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return (x << 16) | y;
	}

	public boolean inBounds(int x1, int y1, int x2, int y2) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}

	public boolean inHeroQuestRangeRoom() {
		return inBounds(459, 672, 460, 673);
	}

	public boolean onTutorialIsland() {
		return inBounds(190, 720, 240, 770);
	}

	public boolean onBlackHole() {
		return inBounds(303, 3298, 307, 3302);
	}

	public boolean inTutorialLanding() {
		return inBounds(214, 739, 221, 747);
	}

	public boolean aroundTutorialRatZone() {
		return inBounds(226, 728, 234, 738);
	}

	public boolean inModRoom() {
		return inBounds(64, 1639, 80, 1643);
	}

	public boolean inWilderness() {
		return wildernessLevel() > 0;
	}

	public boolean inFreeWild() {
		return (wildernessLevel() >= 1 && wildernessLevel() <= 48);
	}

	public boolean inVarrock() {
		return inBounds(78, 490, 175, 537) || inBounds(92, 444, 150, 490);
	}

	public boolean inEdgeville() {
		return inBounds(198, 427, 229, 450) || inBounds(208, 451, 227, 472);
	}

	public boolean inBarbVillage() {
		return inBounds(209, 491, 247, 529);
	}

	public boolean inDraynor() {
		return inBounds(210, 608, 233, 659);
	}

	public boolean inLumbridge() {
		return inBounds(108, 620, 147, 670);
	}

	public boolean inAlKharid() {
		return inBounds(48, 659, 96, 703);
	}

	public boolean inFalador() {
		return inBounds(245, 531, 341, 583);
	}

	public boolean inPortSarim() {
		return inBounds(246, 621, 286, 670);
	}

	public boolean inTaverly() {
		return inBounds(343, 454, 389, 512);
	}

	public boolean inEntrana() {
		return inBounds(395, 525, 441, 573);
	}

	public boolean inCatherby() {
		return inBounds(415, 475, 456, 508);
	}

	public boolean inSeers() {
		return inBounds(486, 438, 534, 482);
	}

	public boolean inGnomeStronghold() {
		return inBounds(673, 432, 751, 537);
	}

	public boolean inArdougne() {
		return inBounds(500, 537, 600, 708);
	}

	public boolean inYanille() {
		return inBounds(577, 741, 647, 767);
	}

	public boolean inBrimhaven() {
		return inBounds(435, 644, 477, 709);
	}

	public boolean inKaramja() {
		return inBounds(323, 644, 679, 908);
	}

	public boolean inShiloVillage() {
		return inBounds(384, 817, 430, 860);
	}

	public boolean isInSeersPartyHall() {
		return isInSeersPartyHallUpstairs() || isInSeersPartyHallDownstairs();
	}

	public boolean isInSeersPartyHallUpstairs() {
		return inBounds(490, 1408, 500, 1415);
	}

	public boolean isInSeersPartyHallDownstairs() {
		return inBounds(490, 464, 500, 471);
	}

	public boolean isInFisherKingRealm() {
		return inBounds(388, 4, 427, 40) || inBounds(484, 4, 523, 40)
				|| inBounds(411, 976, 519, 984)
				|| inBounds(411, 1920, 518, 1925)
				|| inBounds(511, 976, 519, 984)
				|| inBounds(511, 1920, 518, 1925);
	}

	public boolean isInsideGrandTreeGround() {
		return inBounds(410, 158, 422, 170);
	}

	public int wildernessLevel() {
		int wild = 2203 - (y + (1776 - (944 * Formulae.getHeight(this))));
		if (x + 2304 >= 2640) {
			wild = -50;
		}
		if (wild > 0) {
			return 1 + wild / 6;
		}
		return 0;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Point) || o == null) {
			return false;
		}

		Point point = (Point) o;
		return x == point.x && y == point.y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public boolean isWithin1Tile(Point o2) {
		int xDiff = Math.abs(getX() - o2.getX());
		int yDiff = Math.abs(getY() - o2.getY());
		return xDiff <= 1 && yDiff <= 1;
	}

	public int getDistancePythagoras(Point o2) {
		return (int)Math.sqrt(Math.pow(getX() - o2.getX(), 2) + Math.pow(getY() - o2.getY(), 2));
	}

	public boolean inDwarfArea() {
		return inBounds(240, 432, 309, 527);
	}

	public boolean inPlatformArea() {
		return inBounds(492, 614, 498, 620);
	}

	// This is the bounds of the Mage Arena which are actually as far as it needs to be for Mage Arena spell-giving to work correctly.
	// inMageArenaLogOutZone() might authentically be the only check that exists.
	public boolean inMageArena() {
		return inBounds(217, 119, 239, 141);
	}

	// Attempting to emulate the "Anywhere along the water" described by zephyr in a replay.
	// The replays don't thoroughly test every single point near the water to determine the real bounds
	// however, enough tests were done to say that the bounding box for this is NOT rectangular
	// and that "anywhere along the water" could be accurate.
	// See the series of replays between [08-05-2018 13.25.12 drop disk of returning, something cool happened, go to wildy]
	// and [08-05-2018 13.53.26 more pvp mechanics slash bugs with zephyr]
	//
	// Hard to believe this is how it was actually implemented, but not sure how else it would have been done.
	public boolean inMageArenaLogOutZone() {
		int bottom = 141; // standing against member's gate
		// follow the river... yup, this is definitely how jagex wrote it...
		return inBounds(221, 113, 235, bottom) // top flat
			|| inBounds(220, 114, 220, bottom) // little slivers to the right 1
			|| inBounds(218, 116, 219, bottom) // 2
			|| inBounds(217, 117, 217, bottom) // 3
			|| inBounds(215, 118, 216, bottom) // 4
			|| inBounds(214, 119, 214, bottom) // 5
			|| inBounds(213, 120, 213, bottom) // 6, this extends all the way down to the nook where Zephyr & Beast Fable show off one of the known weird logout locations
			|| inBounds(236, 114, 236, bottom) // little slivers to the left 1
			|| inBounds(237, 115, 239, bottom) // 2
			|| inBounds(240, 117, 240, bottom) // 3
			|| inBounds(241, 118, 241, bottom) // 4
			|| inBounds(242, 120, 242, bottom) // 5
			|| inBounds(243, 122, 243, bottom) // 6
			|| inBounds(244, 123, 244, bottom) // 7
			|| inBounds(245, 127, 241, 130) // 8
			|| inBounds(245, 137, 241, bottom) // 9
			;
	}

	public boolean inIbansChamberLogOutZone() {
		return inBounds(794, 3463, 807, 3475);
	}

	public boolean inTouristTrapCave() {
		return inBounds(49, 3600, 95, 3647);
	}

	public boolean inTouristTrapCave1() {
		return inBounds(79, 3614, 95, 3647);
	}

	public boolean inTouristTrapCave2() {
		return inBounds(48, 3633, 78, 3647);
	}

	public boolean inTouristTrapCave3() {
		return inBounds(49, 3600, 95, 3647);
	}

	public boolean isInsideFlameWall() {
		return inBounds(450, 3704, 455, 3711);
	}

	public boolean isAroundBoulderRock() {
		return inBounds(404, 3730, 418, 3744)
				|| inBounds(407, 3718, 421, 3732)
				|| inBounds(417, 3716, 431, 3730);
	}

	public boolean isAroundTotemPole() {
		return inBounds(360, 881, 374, 895)
				|| inBounds(388, 889, 402, 903)
				|| inBounds(456, 882, 470, 896);
	}

	public boolean isInWatchtowerPedestal() {
		return (x == 490 && y == 3520) || (x == 495 && y == 3520)
			|| (x == 490 && y == 3525) || (x == 495 && y == 3525);
	}

	// note: every bank in the game is conveniently perfectly rectangular except for zanaris
	public boolean isInBank(int mapData) {
		return (inBounds(87, 689, 93, 700) && mapData >= 29) // Al Kharid
			|| inBounds(437, 491, 443, 496) // Catherby
			|| (inBounds(216, 634, 223, 638) && mapData >= 24) // Draynor
			|| inBounds(577, 572, 585, 576) // East Ardougne North
			|| inBounds(551, 609, 554, 616) // East Ardougne South
			|| (inBounds(212, 448, 220, 453) && mapData >= 23) // Edgeville
			|| (inBounds(280, 564, 286, 573) && mapData >= 27) // Falador East
			|| inBounds(328, 549, 334, 557) // Falador West
			|| inBounds(714, 1399, 718, 1403) // Grand Tree Second Floor
			|| inBounds(508, 2421, 517, 2423) // Legend's Guild
			|| inBounds(451, 3376, 457, 3380) // Mage Arena
			|| inBounds(498, 447, 504, 453) // Seer's Village
			|| inBounds(59, 731, 59, 731) // Shantay Pass (1 square)
			|| inBounds(399, 848, 404, 854) // Shilo Village
			|| inBounds(196, 746, 203, 754) // Tutorial Island
			|| inBounds(712, 1440, 716, 1464) // Tree Gnome Stronghold (south, near spinning wheel)
			|| (inBounds(98, 510, 106, 515) && mapData >= 24) // Varrock East
			|| inBounds(147, 498, 153, 506) // Varrock West
			|| inBounds(585, 750, 590, 758) // Yanille
			|| inBounds(172, 3521, 176, 3528) // Zanaris Box 1
			|| inBounds(172, 3529, 174, 3529) // Zanaris Box 2
			|| inBounds(170, 3521, 171, 3525) // Zanaris Box 3
		;
	}

	public boolean isInSaradominMonksPlace() {
		return inBounds(249, 452, 265, 468);
	}
	public boolean isInZamorakMonksPlace() {
		return inBounds(679, 634, 704, 659);
	}

	public boolean isInLumbridgeStartingChunk() {
		return inBounds(96, 625, 142, 671) // Ground Floor
			|| inBounds(96, 1569, 142, 1615) // 1st Floor
			|| inBounds(96, 2513, 142, 2559); // 2nd Floor
	}

	public boolean fromHopper() {
		return this.equals(new Point(166, 599)) // lumbridge chute
			|| this.equals(new Point(179, 481)) // cooks guild chute
			|| this.equals(new Point(565, 532)) // ardougne chute
			|| this.equals(new Point(162, 3533)); // zanaris chute
	}

	public boolean inArea(Area area) {
		return area.inBounds(this);
	}

	public Point furthestWalkableTile(World world, int radius) {
		Point candidatePoint = new Point(this.x, this.y);
		int xOffset = radius;
		int yOffset = radius;

		// 1st check if we can go directly adjacent at the requested radius. (this is considered friendly)
		// 2nd check if we can go to one of the corners.
		// 3rd, reduce radius & try again.
		// no need to check every single square in the radius. If these aren't possible, probably it is not possible.
		int initialDir = DataConversions.random(0, 3);
		for (; radius > 0; radius--) {
			for (int attempt = 0; attempt < 8; attempt++) {
				switch ((initialDir + attempt) % 4) {
					case 0:
						if (attempt < 4) {
							xOffset = radius;
							yOffset = 0;
						} else {
							xOffset = radius;
							yOffset = radius;
						}
						break;
					case 1:
						if (attempt < 4) {
							xOffset = -radius;
							yOffset = 0;
						} else {
							xOffset = -radius;
							yOffset = radius;
						}
						break;
					case 2:
						if (attempt < 4) {
							xOffset = 0;
							yOffset = radius;
						} else {
							xOffset = radius;
							yOffset = -radius;
						}
						break;
					case 3:
						if (attempt < 4) {
							xOffset = 0;
							yOffset = -radius;
						} else {
							xOffset = -radius;
							yOffset = -radius;
						}
						break;
					default:
						// not possible to reach default
						xOffset = radius;
						yOffset = radius;
						break;
				}
				candidatePoint = new Point(this.x + xOffset, this.y + yOffset);

				if (PathValidation.checkPath(world, candidatePoint, this) && PathValidation.checkPoint(world, candidatePoint)) {
					return candidatePoint;
				}
			}
		}

		// Finally, no possible tile is walkable. We will agree to teleport directly at the player
		return this;
	}

	@SuppressWarnings("DefaultLocale")
	public String pointToJagexPoint() {
		int height;
		int sectorX;
		int sectorY;
		int offsetX;
		int offsetY;

		height = y / 944;
		int yCalc = y - (height * 944);
		sectorX = (x / 48) + 48;
		sectorY = (yCalc / 48) + 37;
		offsetX = x % 48;
		offsetY = yCalc % 48;
		if (offsetX == 24 && offsetY == 24) {
			// According to Rab, the spot that a new player spawns at is where /rtele 05050 went to
			// It is the center of the chunk.
			return String.format("%d%02d%02d", height, sectorX, sectorY);
		}
		return String.format("%d%02d%02d %02d%02d", height, sectorX, sectorY, offsetX, offsetY);
	}

	public static final int UNABLE_TO_CONVERT = -10000;
	public static final int BAD_COORDINATE_LENGTH = 0;
	public static final int NOT_A_NUMBER = 1;
	public static Point jagexPointToPoint(String jagexPoint) {
		int x = 0;
		int y = 0;

		int height = 0;
		int sectorX = 0;
		int sectorY = 0;
		int offsetX = 0;
		int offsetY = 0;
		try {
			switch (jagexPoint.length()) {
				case 10:
					offsetX = Integer.parseInt(jagexPoint.substring(6, 8));
					offsetY = Integer.parseInt(jagexPoint.substring(8, 10));
				case 5:
					height = Integer.parseInt(jagexPoint.substring(0, 1));
					sectorX = Integer.parseInt(jagexPoint.substring(1, 3));
					sectorY = Integer.parseInt(jagexPoint.substring(3, 5));
					break;
				default:
					return new Point(UNABLE_TO_CONVERT, BAD_COORDINATE_LENGTH);
			}
		} catch (NumberFormatException ex) {
			return new Point(UNABLE_TO_CONVERT, NOT_A_NUMBER);
		}
		if (jagexPoint.length() == 5) {
			offsetX = 24;
			offsetY = 24;
		}

		while (offsetX > 47) {
			sectorX += 1;
			offsetX -= 48;
		}
		while (offsetY > 47) {
			sectorY += 1;
			offsetY -= 48;
		}

		x = ((sectorX - 48) * 48) + offsetX;
		y = (height * 944) + ((sectorY - 37) * 48) + offsetY;
		return new Point(x, y);
	}
}
