package com.notheartyt.vexoro.screen;

import com.notheartyt.vexoro.FeatureRegistry;
import com.notheartyt.vexoro.VexoroConfig;
import com.notheartyt.vexoro.feature.Feature;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/** Main VEXORO menu: logo, module toggles, links to options and the HUD editor. */
public final class VexoroScreen extends Screen {
    private static final Identifier LOGO = Identifier.of("vexoro", "textures/gui/logo.png");

    private final Screen parent;

    public VexoroScreen(Screen parent) {
        super(Text.literal("VEXORO"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int bw = 150;
        int bh = 20;
        int gap = 4;
        int startX = this.width / 2 - bw - gap / 2;
        int y = 82;
        int index = 0;
        for (Feature feature : FeatureRegistry.all()) {
            int x = startX + (index % 2) * (bw + gap);
            this.addDrawableChild(ButtonWidget.builder(label(feature), button -> {
                feature.setEnabled(!feature.isEnabled());
                button.setMessage(label(feature));
            }).dimensions(x, y, bw, bh).build());
            if (index % 2 == 1) y += bh + 2;
            index++;
        }
        if (index % 2 == 1) y += bh + 2;
        y += 4;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("HUD Options"),
                button -> this.client.setScreen(new OptionsScreen(this)))
                .dimensions(startX, y, bw, bh).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Edit HUD Positions"),
                button -> this.client.setScreen(new HudEditScreen(this)))
                .dimensions(startX + bw + gap, y, bw, bh).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
                .dimensions(this.width / 2 - 75, y + bh + 6, 150, bh).build());
    }

    private static Text label(Feature feature) {
        boolean on = feature.isEnabled();
        return Text.literal(feature.displayName() + ": " + (on ? "ON" : "OFF"))
                .formatted(on ? Formatting.GREEN : Formatting.RED);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, LOGO,
                this.width / 2 - 64, 6, 0f, 0f, 128, 72, 512, 288, 512, 288);
    }

    @Override
    public void close() {
        VexoroConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }
}
