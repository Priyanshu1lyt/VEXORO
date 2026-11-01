package com.notheartyt.vexoro;

import com.notheartyt.vexoro.feature.Feature;
import com.notheartyt.vexoro.hud.HudRenderer;
import com.notheartyt.vexoro.screen.VexoroScreen;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** VEXORO client entry point. */
public final class VexoroClient implements ClientModInitializer {
    public static final String MOD_ID = "vexoro";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding openMenuKey;
    private static final Map<Feature, KeyBinding> TOGGLE_KEYS = new LinkedHashMap<>();

    private static KeyBinding register(String translationKey, int defaultKey) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                translationKey, InputUtil.Type.KEYSYM, defaultKey, KeyBinding.Category.MISC));
    }

    @Override
    public void onInitializeClient() {
        VexoroConfig.load();
        FeatureRegistry.registerDefaults();
        HudRenderer.init();

        // Open menu: default O. Every key can be changed in Options > Controls > Key Binds (Misc).
        openMenuKey = register("key.vexoro.open", GLFW.GLFW_KEY_O);
        // One toggle key per module, unbound by default so you can pick your own.
        for (Feature feature : FeatureRegistry.all()) {
            TOGGLE_KEYS.put(feature, register("key.vexoro.toggle." + feature.id(), GLFW.GLFW_KEY_UNKNOWN));
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new VexoroScreen(null));
            }
            for (Map.Entry<Feature, KeyBinding> entry : TOGGLE_KEYS.entrySet()) {
                while (entry.getValue().wasPressed()) {
                    entry.getKey().setEnabled(!entry.getKey().isEnabled());
                    VexoroConfig.save();
                }
            }
        });

        LOGGER.info("VEXORO initialized. Author: NotHeartYT");
    }
}
