package com.notheartyt.vexoro.feature;

import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;

public final class PositionFeature extends Feature {
    public PositionFeature(BooleanSupplier getter, Consumer<Boolean> setter) {
        super("position", "Position HUD", getter, setter);
    }

    @Override
    public List<String> lines(MinecraftClient client) {
        if (client.player == null) return List.of();
        return List.of(String.format(Locale.ROOT, "XYZ: %.1f / %.1f / %.1f",
                client.player.getX(), client.player.getY(), client.player.getZ()));
    }
}
