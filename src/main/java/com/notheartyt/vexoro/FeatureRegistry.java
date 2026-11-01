package com.notheartyt.vexoro;

import com.notheartyt.vexoro.feature.ArmorFeature;
import com.notheartyt.vexoro.feature.BasicFeature;
import com.notheartyt.vexoro.feature.CpsFeature;
import com.notheartyt.vexoro.feature.EffectsFeature;
import com.notheartyt.vexoro.feature.Feature;
import com.notheartyt.vexoro.feature.PositionFeature;
import com.notheartyt.vexoro.feature.ReachFeature;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/** Central registry for all VEXORO modules. */
public final class FeatureRegistry {
    private static final Map<String, Feature> FEATURES = new LinkedHashMap<>();

    private FeatureRegistry() {}

    public static void registerDefaults() {
        FEATURES.clear();
        register(new ArmorFeature(() -> VexoroConfig.get().armorHud, v -> VexoroConfig.get().armorHud = v));
        register(new BasicFeature("inventory_hud", "Inventory HUD", () -> VexoroConfig.get().inventoryHud, v -> VexoroConfig.get().inventoryHud = v));
        register(new EffectsFeature(() -> VexoroConfig.get().effectsHud, v -> VexoroConfig.get().effectsHud = v));
        register(new BasicFeature("crosshair", "Custom Crosshair", () -> VexoroConfig.get().customCrosshair, v -> VexoroConfig.get().customCrosshair = v));
        register(new BasicFeature("hitbox", "Player Hitbox", () -> VexoroConfig.get().hitboxDisplay, v -> VexoroConfig.get().hitboxDisplay = v));
        register(new ReachFeature(() -> VexoroConfig.get().reachIndicator, v -> VexoroConfig.get().reachIndicator = v));
        register(new PositionFeature(() -> VexoroConfig.get().positionHud, v -> VexoroConfig.get().positionHud = v));
        register(new CpsFeature(() -> VexoroConfig.get().cpsCounter, v -> VexoroConfig.get().cpsCounter = v));
    }

    public static void register(Feature feature) {
        FEATURES.put(feature.id(), feature);
    }

    public static Feature get(String id) {
        return FEATURES.get(id);
    }

    public static Collection<Feature> all() {
        return FEATURES.values();
    }
}
