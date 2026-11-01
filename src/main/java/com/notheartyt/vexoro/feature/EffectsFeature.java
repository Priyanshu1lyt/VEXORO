package com.notheartyt.vexoro.feature;

import com.notheartyt.vexoro.VexoroConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

/** Potion HUD: effects with timer, optional mini mode with duration bar, horizontal mode. */
public final class EffectsFeature extends Feature {
    private record Entry(String label, String time, int ticks, int max, int color) {}

    private final Map<String, Integer> maxTicks = new HashMap<>();
    private int lastX;
    private int lastY;
    private int lastW;
    private int lastH;

    public EffectsFeature(BooleanSupplier getter, Consumer<Boolean> setter) {
        super("effects_hud", "Potion HUD", getter, setter);
    }

    @Override public boolean draggable() { return true; }
    @Override public int hudX() { return lastX; }
    @Override public int hudY() { return lastY; }
    @Override public int hudW() { return lastW; }
    @Override public int hudH() { return lastH; }

    @Override
    public void setHudPos(int x, int y) {
        VexoroConfig.get().potionX = x;
        VexoroConfig.get().potionY = y;
    }

    private List<Entry> collect(MinecraftClient client, boolean preview) {
        List<Entry> list = new ArrayList<>();
        if (preview) {
            list.add(new Entry("Speed 2", "1:30", 1800, 3600, 0x7CAFC6));
            list.add(new Entry("Strength 1", "0:45", 900, 3600, 0x932423));
            list.add(new Entry("Fire Resistance 1", "inf", -1, -1, 0xE49A3A));
            return list;
        }
        if (client.player == null) return list;
        Set<String> seen = new HashSet<>();
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            String key = effect.getEffectType().value().getName().getString() + " " + (effect.getAmplifier() + 1);
            int color = effect.getEffectType().value().getColor();
            seen.add(key);
            if (effect.isInfinite()) {
                list.add(new Entry(key, "inf", -1, -1, color));
            } else {
                int ticks = effect.getDuration();
                int max = maxTicks.merge(key, ticks, Math::max);
                int seconds = ticks / 20;
                String time = String.format(Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60);
                list.add(new Entry(key, time, ticks, max, color));
            }
        }
        maxTicks.keySet().retainAll(seen);
        return list;
    }

    @Override
    public void render(DrawContext ctx, MinecraftClient client, boolean preview) {
        VexoroConfig cfg = VexoroConfig.get();
        List<Entry> entries = collect(client, preview);
        if (entries.isEmpty()) {
            lastW = 0;
            lastH = 0;
            return;
        }
        TextRenderer tr = client.textRenderer;
        boolean horizontal = cfg.potionHorizontal;
        boolean mini = cfg.potionMini;
        int gap = cfg.potionGap;
        int rowH = mini ? 14 : 12;
        int bg = (Math.round(cfg.potionBgOpacity * 255f) & 0xFF) << 24;

        int n = entries.size();
        int[] widths = new int[n];
        int total = 0;
        int widest = 0;
        for (int i = 0; i < n; i++) {
            Entry e = entries.get(i);
            String text = mini ? e.label() : e.label() + "  " + e.time();
            widths[i] = tr.getWidth(text) + 10;
            total += widths[i];
            widest = Math.max(widest, widths[i]);
        }
        int w = horizontal ? total + gap * (n - 1) : widest;
        int h = horizontal ? rowH : n * rowH + gap * (n - 1);

        int sw = ctx.getScaledWindowWidth();
        int sh = ctx.getScaledWindowHeight();
        int x = cfg.potionX < 0 ? sw - w - 4 : cfg.potionX;
        int y = cfg.potionY;
        x = Math.max(0, Math.min(x, sw - w));
        y = Math.max(0, Math.min(y, sh - h));
        lastX = x;
        lastY = y;
        lastW = w;
        lastH = h;

        int cx = x;
        int cy = y;
        for (int i = 0; i < n; i++) {
            Entry e = entries.get(i);
            int ew = horizontal ? widths[i] : w;
            int color = 0xFF000000 | e.color();
            if ((bg >>> 24) != 0) ctx.fill(cx, cy, cx + ew, cy + rowH, bg);
            ctx.fill(cx, cy, cx + 2, cy + rowH, color);
            String text = mini ? e.label() : e.label() + "  " + e.time();
            ctx.drawText(tr, text, cx + 6, cy + 2, cfg.textArgb(), true);
            if (mini) {
                int bx = cx + 6;
                int bw = ew - 9;
                ctx.fill(bx, cy + rowH - 3, bx + bw, cy + rowH - 1, 0x66FFFFFF);
                float frac = e.ticks() < 0 ? 1f : Math.max(0f, Math.min(1f, (float) e.ticks() / Math.max(1, e.max())));
                ctx.fill(bx, cy + rowH - 3, bx + Math.round(bw * frac), cy + rowH - 1, color);
            }
            if (horizontal) {
                cx += ew + gap;
            } else {
                cy += rowH + gap;
            }
        }
    }
}
