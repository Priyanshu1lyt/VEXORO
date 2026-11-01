package com.notheartyt.vexoro;

import com.notheartyt.vexoro.hud.HudRenderer;
import com.notheartyt.vexoro.screen.VexoroScreen;
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

    @Override
    public void onInitializeClient() {
        VexoroConfig.load();
        FeatureRegistry.registerDefaults();
        HudRenderer.init();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.vexoro.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KeyBinding.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                client.setScreen(new VexoroScreen(client.currentScreen));
            }
        });

        LOGGER.info("VEXORO initialized. Author: NotHeartYT");
    }
}
