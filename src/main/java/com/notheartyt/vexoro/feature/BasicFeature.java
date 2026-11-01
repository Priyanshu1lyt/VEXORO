package com.notheartyt.vexoro.feature;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Placeholder for modules not implemented yet (Inventory HUD, Crosshair, Hitbox). */
public final class BasicFeature extends Feature {
    public BasicFeature(String id, String displayName, BooleanSupplier getter, Consumer<Boolean> setter) {
        super(id, displayName, getter, setter);
    }
}
