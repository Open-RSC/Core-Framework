package com.openrsc.server.model;

import com.openrsc.server.external.NPCDef;
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
		if (impersonatingNpc)
			return getNearestSkinColour(npcAppearance.getSkinColour());
		return skinColour;
	}

	public byte getSkinColourSave() {
		return skinColour;
	}

	private byte getNearestSkinColour(int skinColour) {
		int[] authenticSkinColours = new int[] { 0xecded0, 0xccb366, 0xb38c40, 0x997326, 0x906020 };
		return getNearestColour(skinColour, authenticSkinColours);
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

	public boolean isValid() {
		if (!DataConversions.inArray(headSprites, getHead())
			|| !DataConversions.inArray(bodySprites, getBody())) {
			return false;
		}
		if (hairColour < 0 || topColour < 0 || trouserColour < 0
			|| skinColour < 0) {
			return false;
		}
		return hairColour <= 9 && topColour <= 14 && trouserColour <= 14
			&& skinColour <= 4;
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

	private byte getNearestColour(int colour, int[] authenticColours) {
		// override for black -> a much lighter shade of gray which looks black
		// otherwise, a dark green is actually the closest to black.
		if (colour <= 5) {
			for (int i = 0; i < authenticColours.length; i++) {
				if (authenticColours[i] == 0x303030) {
					return (byte)(i & 0xFF);
				}
			}
		}

		// normal distance formula
		int[] distances = new int[authenticColours.length];
		for (int i = 0; i < authenticColours.length; i++) {
			distances[i] = distanceBetweenColours(colour, authenticColours[i]);
		}

		int smallestIndex = 0;
		for (int i = 1; i < distances.length; i++) {
			if (distances[i] < distances[smallestIndex]) {
				smallestIndex = i;
			}
		}

		return (byte)(smallestIndex & 0xFF);
	}

}
