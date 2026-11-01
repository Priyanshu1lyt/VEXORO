package com.notheartyt.vexoro.screen;

import com.notheartyt.vexoro.FeatureRegistry;
import com.notheartyt.vexoro.VexoroConfig;
import com.notheartyt.vexoro.feature.Feature;
import com.notheartyt.vexoro.hud.HudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** Drag and drop HUD editor. Hold the left mouse button on a HUD and move it. Esc saves. */
public final class HudEditScreen extends Screen {
    private final Screen parent;
    private Feature dragging;
    private int offsetX;
    private int offsetY;
    private boolean wasDown;

    public HudEditScreen(Screen parent) {
        super(Text.literal("Edit HUD Positions"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        HudRenderer.suppressed = true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        long window = this.client.getWindow().getHandle();
        boolean down = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (down && !wasDown) {
            dragging = null;
            for (Feature feature : FeatureRegistry.all()) {
                if (!feature.draggable() || !feature.isEnabled()) continue;
                int x = feature.hudX();
                int y = feature.hudY();
                if (mouseX >= x && mouseX <= x + feature.hudW() && mouseY >= y && mouseY <= y + feature.hudH()) {
                    dragging = feature;
                    offsetX = mouseX - x;
                    offsetY = mouseY - y;
                }
            }
        }
        if (down && dragging != null) {
            dragging.setHudPos(mouseX - offsetX, mouseY - offsetY);
        }
        if (!down) dragging = null;
        wasDown = down;

        int accent = VexoroConfig.get().accentArgb();
        for (Feature feature : FeatureRegistry.all()) {
            if (!feature.draggable() || !feature.isEnabled()) continue;
            feature.render(context, this.client, true);
            int x = feature.hudX();
            int y = feature.hudY();
            int w = feature.hudW();
            int h = feature.hudH();
            context.fill(x - 1, y - 1, x + w + 1, y, accent);
            context.fill(x - 1, y + h, x + w + 1, y + h + 1, accent);
            context.fill(x - 1, y, x, y + h, accent);
            context.fill(x + w, y, x + w + 1, y + h, accent);
        }

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Drag the HUDs. Esc = save and exit."), this.width / 2, 8, 0xFFFFFFFF);
    }

    @Override
    public void removed() {
        HudRenderer.suppressed = false;
    }

    @Override
    public void close() {
        HudRenderer.suppressed = false;
        VexoroConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }
}
