package com.notheartyt.vexoro.screen;

import com.notheartyt.vexoro.VexoroConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Potion HUD and Armor HUD settings. */
public final class OptionsScreen extends Screen {
    private record Opt(String label, Supplier<String> value, Runnable click) {}

    private static final float[] SCALES = {0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
    private static final String[] MODES = {"Bar", "Number", "Percent"};

    private final Screen parent;

    public OptionsScreen(Screen parent) {
        super(Text.literal("VEXORO Options"));
        this.parent = parent;
    }

    private static String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }

    private List<Opt> options() {
        VexoroConfig c = VexoroConfig.get();
        List<Opt> list = new ArrayList<>();
        list.add(new Opt("Potion: Horizontal", () -> onOff(c.potionHorizontal), () -> c.potionHorizontal = !c.potionHorizontal));
        list.add(new Opt("Potion: Mini Mode", () -> onOff(c.potionMini), () -> c.potionMini = !c.potionMini));
        list.add(new Opt("Potion: Gap", () -> String.valueOf(c.potionGap), () -> c.potionGap = (c.potionGap + 1) % 9));
        list.add(new Opt("Potion: Background", () -> String.format(Locale.ROOT, "%.1f", c.potionBgOpacity),
                () -> c.potionBgOpacity = ((Math.round(c.potionBgOpacity * 10f) + 1) % 11) / 10f));
        list.add(new Opt("Armor: Scale", () -> String.format(Locale.ROOT, "%.2f", c.armorScale), () -> {
            int next = 0;
            for (int i = 0; i < SCALES.length; i++) {
                if (Math.abs(SCALES[i] - c.armorScale) < 0.01f) next = (i + 1) % SCALES.length;
            }
            c.armorScale = SCALES[next];
        }));
        list.add(new Opt("Armor: Damage", () -> MODES[c.armorDamageMode], () -> c.armorDamageMode = (c.armorDamageMode + 1) % 3));
        list.add(new Opt("Armor: Helmet", () -> onOff(c.armorShowHelmet), () -> c.armorShowHelmet = !c.armorShowHelmet));
        list.add(new Opt("Armor: Chestplate", () -> onOff(c.armorShowChest), () -> c.armorShowChest = !c.armorShowChest));
        list.add(new Opt("Armor: Leggings", () -> onOff(c.armorShowLegs), () -> c.armorShowLegs = !c.armorShowLegs));
        list.add(new Opt("Armor: Boots", () -> onOff(c.armorShowBoots), () -> c.armorShowBoots = !c.armorShowBoots));
        list.add(new Opt("Armor: Main Hand", () -> onOff(c.armorShowMainHand), () -> c.armorShowMainHand = !c.armorShowMainHand));
        list.add(new Opt("Armor: Off Hand", () -> onOff(c.armorShowOffHand), () -> c.armorShowOffHand = !c.armorShowOffHand));
        list.add(new Opt("Armor: Free Slots", () -> onOff(c.armorShowFreeSlots), () -> c.armorShowFreeSlots = !c.armorShowFreeSlots));
        list.add(new Opt("Armor: Arrows", () -> onOff(c.armorShowArrows), () -> c.armorShowArrows = !c.armorShowArrows));
        return list;
    }

    private static Text text(Opt opt) {
        String value = opt.value().get();
        Text valueText = Text.literal(value);
        if (value.equals("ON")) valueText = Text.literal(value).formatted(Formatting.GREEN);
        if (value.equals("OFF")) valueText = Text.literal(value).formatted(Formatting.RED);
        return Text.literal(opt.label() + ": ").append(valueText);
    }

    @Override
    protected void init() {
        int bw = 150;
        int bh = 18;
        int gap = 4;
        int startX = this.width / 2 - bw - gap / 2;
        int y = 30;
        int index = 0;
        for (Opt opt : options()) {
            int x = startX + (index % 2) * (bw + gap);
            this.addDrawableChild(ButtonWidget.builder(text(opt), button -> {
                opt.click().run();
                button.setMessage(text(opt));
            }).dimensions(x, y, bw, bh).build());
            if (index % 2 == 1) y += bh + 3;
            index++;
        }
        if (index % 2 == 1) y += bh + 3;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
                .dimensions(this.width / 2 - 75, y + 6, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12,
                VexoroConfig.get().accentArgb());
    }

    @Override
    public void close() {
        VexoroConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }
                         }
