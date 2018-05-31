package org.rscemulation.client.entityhandling.defs.extras;

public class AnimationDef {
    public String name;
    public int charColour;
    public int genderModel;
    public boolean hasA;
    public boolean hasF;
    public int number;

	public AnimationDef(String name, int charColour, int genderModel, boolean hasA, boolean hasF, int number) {
		this.name = name;
		this.charColour = charColour;
		this.genderModel = genderModel;
		this.hasA = hasA;
		this.hasF = hasF;
		this.number = number;
	}
	
    public String getName() {
        return name;
    }

    public int getCharColour() {
        return charColour;
    }

    public int getGenderModel() {
        return genderModel;
    }

    public boolean hasA() {
        return hasA;
    }

    public boolean hasF() {
        return hasF;
    }

    public int getNumber() {
        return number;
    }
}
