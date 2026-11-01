package com.notheartyt.vexoro.hud;

import com.notheartyt.vexoro.FeatureRegistry;
import com.notheartyt.vexoro.VexoroConfig;
import com.notheartyt.vexoro.feature.Feature;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/** Draws every enabled module: text modules are stacked left/right, others draw themselves. */
public final class HudRenderer {
    private HudRenderer() {}

    public static void init() {
        HudRenderCallback.EVENT.register(HudRenderer::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        VexoroConfig config = VexoroConfig.get();
        int accent = config.accentArgb();
        int text = config.textArgb();
        int leftY = 4;
        int rightY = 4;
        int width = context.getScaledWindowWidth();

        for (Feature feature : FeatureRegistry.all()) {
            if (!feature.isEnabled()) continue;
            feature.update(client);
            feature.render(context, client);

            List<String> lines = feature.lines(client);
            for (String line : lines) {
                if (feature.rightSide()) {
                    int x = width - client.textRenderer.getWidth(line) - 6;
                    context.fill(x - 4, rightY - 1, x - 3, rightY + 9, accent);
                    context.drawText(client.textRenderer, line, x, rightY, text, true);
                    rightY += 12;
                } else {
                    context.fill(4, leftY - 1, 5, leftY + 9, accent);
                    context.drawText(client.textRenderer, line, 9, leftY, text, true);
                    leftY += 12;
                }
            }
        }
    }
}
