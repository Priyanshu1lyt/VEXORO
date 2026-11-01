package com.notheartyt.vexoro.screen;

import com.notheartyt.vexoro.FeatureRegistry;
import com.notheartyt.vexoro.VexoroConfig;
import com.notheartyt.vexoro.feature.Feature;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Simple settings screen: one toggle button per module. */
public final class VexoroScreen extends Screen {
    private final Screen parent;

    public VexoroScreen(Screen parent) {
        super(Text.literal("VEXORO"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 150;
        int buttonHeight = 20;
        int gap = 4;
        int startX = this.width / 2 - buttonWidth - gap / 2;
        int y = 40;
        int index = 0;
        for (Feature feature : FeatureRegistry.all()) {
            int x = startX + (index % 2) * (buttonWidth + gap);
            this.addDrawableChild(ButtonWidget.builder(label(feature), button -> {
                feature.setEnabled(!feature.isEnabled());
                button.setMessage(label(feature));
            }).dimensions(x, y, buttonWidth, buttonHeight).build());
            if (index % 2 == 1) y += buttonHeight + gap;
            index++;
        }
        if (index % 2 == 1) y += buttonHeight + gap;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
                .dimensions(this.width / 2 - 75, y + 8, 150, buttonHeight).build());
    }

    private static Text label(Feature feature) {
        boolean on = feature.isEnabled();
        return Text.literal(feature.displayName() + ": " + (on ? "ON" : "OFF"))
                .formatted(on ? Formatting.GREEN : Formatting.RED);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16,
                VexoroConfig.get().accentArgb());
    }

    @Override
    public void close() {
        VexoroConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }
}
