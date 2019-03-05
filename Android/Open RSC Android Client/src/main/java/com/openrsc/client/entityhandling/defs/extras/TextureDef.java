package com.openrsc.client.entityhandling.defs.extras;

public class TextureDef {
    private String dataName;
    private String animationName;

    public TextureDef(String dataName, String animationName) {
        this.dataName = dataName;
        this.animationName = animationName;
    }

    public String getDataName() {
        return dataName;
    }

    public String getAnimationName() {
        return animationName;
    }
}