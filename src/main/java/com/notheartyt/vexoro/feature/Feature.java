package com.notheartyt.vexoro.feature;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Base type for independently toggleable VEXORO modules. State lives in the config. */
public abstract class Feature {
    private final String id;
    private final String displayName;
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;

    protected Feature(String id, String displayName, BooleanSupplier getter, Consumer<Boolean> setter) {
        this.id = id;
        this.displayName = displayName;
        this.getter = getter;
        this.setter = setter;
    }

    public String id() { return id; }
    public String displayName() { return displayName; }
    public boolean isEnabled() { return getter.getAsBoolean(); }
    public void setEnabled(boolean enabled) { setter.accept(enabled); }

    /** Called every frame while the module is enabled and a world is loaded. */
    public void update(MinecraftClient client) {}

    /** Text lines for the stacked HUD. Empty means nothing is drawn. */
    public List<String> lines(MinecraftClient client) { return List.of(); }

    /** True to stack the lines on the right side of the screen instead of the left. */
    public boolean rightSide() { return false; }

    /** Custom drawing for modules that are not plain text (e.g. Armor HUD). */
    public void render(DrawContext context, MinecraftClient client) {}
}
