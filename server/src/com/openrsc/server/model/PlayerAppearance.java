package com.openrsc.server.model;

import com.mysql.cj.xdevapi.Client;
import com.openrsc.server.external.NPCDef;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ClientLimitations;
import com.openrsc.server.util.rsc.DataConversions;

public class PlayerAppearance {

	private boolean hideTrousers;
	private int body;
	private byte hairColour;
	private int head;
	private byte skinColour;
	private byte topColour;
	private byte trouserColour;

	private boolean impersonatingNpc;
	private NPCDef npcAppearance;

	private final int[] bodySprites = { 2, 5 };
	private final int[] headSprites = { 1, 4, 6, 7, 8 };

	private final int[] playerSkinColors = new int[]{
		// original player skin colours
		0xECDED0, 0xCCB366, 0xB38C40, 0x997326, 0x906020,

		// authentic npc skin colours (with previously used colours removed)
		0x000000, 0x000004, 0x0066FF, 0x009000, 0x3CB371,
		0x55BFEE, 0x55CFFF, 0x604020, 0x663300, 0x6F5737,
		0x705010, 0x804000, 0x996633, 0x999999, 0xAC9E90,
		0xDCC399, 0xDCCEA0, 0xDCFFD0, 0xDD3040, 0xEADED2,
		0xECEED0, 0xECFED0, 0xECFFD0, 0xFCEEE0, 0xFF3333,
		0xFF9F55, 0xFFDED2, 0xFFFEF0, 0xFFFFFF,

		0x00A0A0, // teal
		0xFFFF00, // yellow
		0xFF69B4, // hot pink
		0x0180A2, // rsc zombie
		0x86668e, // evequill purple
		0x663399, // rebecca purple
		0xB5FF1D, // easter ogre
		0xA0C0C0, // silver man
		0x608080, // coal woman
	};

	public PlayerAppearance(int hairColour, int topColour, int trouserColour,
							int skinColour, int head, int body) {
		this.hairColour = (byte) hairColour;
		this.topColour = (byte) topColour;
		this.trouserColour = (byte) trouserColour;
		this.skinColour = (byte) skinColour;
		this.setHead(head);
		this.setBody(body);
	}

	public byte getHairColour() {
		if (impersonatingNpc)
			return getNearestHairColour(npcAppearance.getHairColour());
		return hairColour;
	}

	public byte getHairColourSave() {
		return hairColour;
	}

	private byte getNearestHairColour(int hairColour) {
		int[] authenticHairColours = new int[] { 0xffc030, 0xffa040, 0x805030, 0x604020, 0x303030, 0xff6020, 0xff4000, 0xffffff, 0x00ff00, 0x00ffff };
		return getNearestColour(hairColour, authenticHairColours);
	}

	public byte getSkinColour() {
		return getSkinColour(4);
	}

	public byte getSkinColour(int limit) {
		if (impersonatingNpc)
			return getNearestSkinColour(npcAppearance.getSkinColour(), limit);
		return skinColour;
	}

	public byte getSkinColourSave() {
		return skinColour;
	}

	private byte getNearestSkinColour(int skinColour, int supportedColours) {
		return getNearestColour(skinColour, playerSkinColors, supportedColours);
	}

	public int getSprite(int pos) {
		switch (pos) {
			case 0:
				return getHead();
			case 1:
				return getBody();
			case 2:
				if (hideTrousers) {
					return 0;
				}
				return 3;
			default:
				return 0;
		}
	}

	// was originally implemented for use with some minigame pre-RSCR era
	public void hideTrousers(boolean b) {
		hideTrousers = b;
	}

	public int[] getSprites() {
		return new int[]{getHead(), getBody(), 3, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	}

	public byte getTopColour() {
		if (impersonatingNpc)
			return getNearestClothingColour(npcAppearance.getTopColour());
		return topColour;
	}

	public byte getTopColourSave() {
		return topColour;
	}

	public byte getTrouserColour() {
		if (impersonatingNpc)
			return getNearestClothingColour(npcAppearance.getBottomColour());
		return trouserColour;
	}

	public byte getTrouserColourSave() {
		return trouserColour;
	}

	private byte getNearestClothingColour(int clothingColour) {
		int[] authenticClothingColours = new int[] { 0xff0000, 0xff8000, 0xffe000, 0xa0e000, 0x00e000, 0x008000, 0x00a080, 0x00b0ff, 0x0080ff, 0x0030f0, 0xe000e0, 0x303030, 0x604000, 0x805000, 0xffffff };
		return getNearestColour(clothingColour, authenticClothingColours);
	}

	public boolean isValid(Player player) {
		if (!DataConversions.inArray(headSprites, getHead())
			|| !DataConversions.inArray(bodySprites, getBody())) {
			return false;
		}
		if (hairColour < 0 || topColour < 0 || trouserColour < 0
			|| skinColour < 0) {
			return false;
		}
		if (skinColour > 4) {
			if (skinColour >= playerSkinColors.length) {
				return false;
			}
			// player cache hasn't been loaded yet, will allow a skin within bounds
			if (!player.isLoggedIn()) {
				return player.supportsPlayerUnlockedAppearancesPacket();
			}

			return player.getUnlockedSkinColours()[skinColour];
		}
		return hairColour <= 9 && topColour <= 14 && trouserColour <= 14;
	}

	public int getHead() {
		return head;
	}

	public void setHead(int head) {
		this.head = head;
	}

	public int getBody() {
		return body;
	}

	public void setBody(int body) {
		this.body = body;
	}

	public boolean isImpersonatingNpc() {
		return impersonatingNpc;
	}

	public void setNpcAppearance(NPCDef n) {
		npcAppearance = n;
		impersonatingNpc = true;
	}

	public void restorePlayerAppearance() {
		impersonatingNpc = false;
	}


	private int distanceBetweenColours (int color1, int color2) {
		int red1 = (color1 & 0xFF0000) >> 16;
		int red2 = (color2 & 0xFF0000) >> 16;
		int green1 = (color1 & 0xFF00) >> 8;
		int green2 = (color2 & 0xFF00) >> 8;
		int blue1 = color1 & 0xFF;
		int blue2 = color2 & 0xFF;

		int redDiff = red1 > red2 ? red1 - red2 : red2 - red1;
		int greenDiff = green1 > green2 ? green1 - green2 : green2 - green1;
		int blueDiff = blue1 > blue2 ? blue1 - blue2 : blue2 - blue1;

		return redDiff + greenDiff + blueDiff;
	}

	private byte getNearestColour(int colour, int[] availableColours) {
		return getNearestColour(colour, availableColours, Integer.MAX_VALUE);
	}

	private byte getNearestColour(int colour, int[] availableColours, int limit) {
		// override for black -> a much lighter shade of gray which looks black
		// otherwise, a dark green is actually the closest to black.
		if (colour <= 5) {
			for (int i = 0; i < availableColours.length && i <= limit; i++) {
				if (availableColours[i] == 0x303030) {
					return (byte)(i & 0xFF);
				}
			}
		}

		// normal distance formula
		int[] distances = new int[availableColours.length];
		for (int i = 0; i < availableColours.length && i <= limit; i++) {
			distances[i] = distanceBetweenColours(colour, availableColours[i]);
		}

		int smallestIndex = 0;
		for (int i = 1; i < distances.length && i <= limit; i++) {
			if (distances[i] < distances[smallestIndex]) {
				smallestIndex = i;
			}
		}

		return (byte)(smallestIndex & 0xFF);
	}

}
