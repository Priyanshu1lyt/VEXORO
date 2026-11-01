package com.notheartyt.vexoro.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;

public final class EffectsFeature extends Feature {
    public EffectsFeature(BooleanSupplier getter, Consumer<Boolean> setter) {
        super("effects_hud", "Status Effects", getter, setter);
    }

    @Override
    public boolean rightSide() { return true; }

    @Override
    public List<String> lines(MinecraftClient client) {
        List<String> lines = new ArrayList<>();
        if (client.player == null) return lines;
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            String name = effect.getEffectType().value().getName().getString();
            String time;
            if (effect.isInfinite()) {
                time = "inf";
            } else {
                int seconds = effect.getDuration() / 20;
                time = String.format(Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60);
            }
            lines.add(name + " " + (effect.getAmplifier() + 1) + " (" + time + ")");
        }
        return lines;
    }
}
