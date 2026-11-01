package com.notheartyt.vexoro.feature;

import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;

/** Shows the vanilla distance to the entity under the crosshair. It never changes attack reach. */
public final class ReachFeature extends Feature {
    public ReachFeature(BooleanSupplier getter, Consumer<Boolean> setter) {
        super("reach", "Reach Indicator", getter, setter);
    }

    @Override
    public List<String> lines(MinecraftClient client) {
        if (client.player == null) return List.of();
        HitResult hit = client.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
            double distance = client.player.getEyePos().distanceTo(hit.getPos());
            return List.of(String.format(Locale.ROOT, "Reach: %.2f", distance));
        }
        return List.of("Reach: --");
    }
}
