package com.openrsc.server.event.rsc.impl.projectile;

public enum ProjectileFailureReason {
    HANDLED_BY_PLUGIN(null),
    OUT_OF_AMMO("I've run out of ammo!"),
    BOLTS_TOO_POWERFUL("Your bolts are too powerful for your crossbow."),
    ARROWS_TOO_POWERFUL("Your arrows are too powerful for your Bow."),
    NOT_ENOUGH_AMMO_BOLTS("You don't have enough ammo in your bolt holder"),
    NOT_ENOUGH_AMMO_ARROWS("You don't have enough ammo in your quiver"),
    BOLTS_WONT_FIT_DRAGON_CROSSBOW("Your bolts will not fit in the dragon crossbow."),
    ARROWS_WONT_FIT_DRAGON_LONGBOW("Your arrows will not fit the dragon longbow."),
    CANT_FIRE_ARROWS_WITH_CROSSBOW("You can't fire arrows with a crossbow"),
    CANT_FIRE_BOLTS_WITH_BOW("You can't fire bolts with a bow"),
    CANT_GET_CLOSE_ENOUGH("I can't get close enough"),
    CANT_GET_CLEAR_SHOT("I can't get a clear shot from here"),
    NO_AMMO_EQUIPPED("you don't have any ammo equipped");

    private final String text;

    ProjectileFailureReason(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
