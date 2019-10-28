package com.openrsc.server.model;

import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

public class PlayerAppearance {

	private boolean hideTrousers;
	private int body;
	private byte hairColour;
	private int head;
	private byte skinColour;
	private byte topColour;
	private byte trouserColour;

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
		return hairColour;
	}

	public byte getSkinColour() {
		return skinColour;
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

	public void hideTrousers(boolean b) {
		hideTrousers = b;
	}

	public int[] getSprites() {
		return new int[]{getHead(), getBody(), 3, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	}

	public byte getTopColour() {
		return topColour;
	}

	public byte getTrouserColour() {
		return trouserColour;
	}

	public boolean isValid() {
		if (!DataConversions.inArray(Formulae.headSprites, getHead())
			|| !DataConversions.inArray(Formulae.bodySprites, getBody())) {
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

}
