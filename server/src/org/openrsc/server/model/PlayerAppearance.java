package org.openrsc.server.model;

import org.openrsc.server.util.Formulae;
import org.openrsc.server.util.DataConversions;

public class PlayerAppearance {
	private byte hairColour, topColour, trouserColour, skinColour;
	private int head, body;
	
	public PlayerAppearance clone() {
		return new PlayerAppearance(hairColour, topColour, trouserColour, skinColour, head, body);
	}
	
	public PlayerAppearance(int hairColour, int topColour, int trouserColour, int skinColour, int head, int body) {
		this.hairColour = (byte)hairColour;
		this.topColour = (byte)topColour;
		this.trouserColour = (byte)trouserColour;
		this.skinColour = (byte)skinColour;
		this.head = head;
		this.body = body;
	}
	
	public int getSprite(int pos) {
		switch (pos) {
			case 0:
				return head;
			case 1:
				return body;
			case 2:
				return 3;
			default:
				return 0;
		}
	}
	
	public int[] getSprites() {
		return new int[]{head, body, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	}
	
	public byte getHairColour() {
		return hairColour;
	}
	
	public byte getTopColour() {
		return topColour;
	}
	
	public byte getTrouserColour() {
		return trouserColour;
	}
	
	public byte getSkinColour() {
		return skinColour;
	}
	
	public boolean isValid() {
		if (!DataConversions.inArray(Formulae.headSprites, head) || !DataConversions.inArray(Formulae.bodySprites, body))
			return false;
		if (hairColour < 0 || topColour < 0 || trouserColour < 0 || skinColour < 0)
			return false;
		if (hairColour > 9 || topColour > 14 || trouserColour > 14 || skinColour > 4)
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("Playerappearance: ");
		sb.append("hair:").append((int)hairColour).append(", top:").append((int)topColour).append(", trouser: ").append((int)trouserColour).append(", skin:").append((int)skinColour);
		return sb.toString();
	}
}