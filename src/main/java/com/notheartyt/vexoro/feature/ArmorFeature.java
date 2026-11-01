package com.notheartyt.vexoro.feature;

import com.notheartyt.vexoro.VexoroConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Armor HUD: durability of armor/hands, free inventory slots and arrow count. */
public final class ArmorFeature extends Feature {
    private record Row(ItemStack stack, String text, int color, boolean overlay) {}

    private int lastX;
    private int lastY;
    private int lastW;
    private int lastH;

    public ArmorFeature(BooleanSupplier getter, Consumer<Boolean> setter) {
        super("armor_hud", "Armor HUD", getter, setter);
    }

    @Override public boolean draggable() { return true; }
    @Override public int hudX() { return lastX; }
    @Override public int hudY() { return lastY; }
    @Override public int hudW() { return lastW; }
    @Override public int hudH() { return lastH; }

    @Override
    public void setHudPos(int x, int y) {
        VexoroConfig.get().armorX = x;
        VexoroConfig.get().armorY = y;
    }

    private static ItemStack damaged(Item item, int damage) {
        ItemStack stack = new ItemStack(item);
        stack.setDamage(damage);
        return stack;
    }

    private void addSlot(List<Row> rows, boolean show, MinecraftClient client, boolean preview,
                         EquipmentSlot slot, ItemStack sample) {
        if (!show) return;
        ItemStack stack = preview ? sample : client.player.getEquippedStack(slot);
        if (stack.isEmpty()) return;
        int mode = VexoroConfig.get().armorDamageMode;
        String text = "";
        int color = 0xFFFFFFFF;
        if (stack.isDamageable()) {
            int max = stack.getMaxDamage();
            int left = max - stack.getDamage();
            float frac = max <= 0 ? 1f : (float) left / max;
            color = frac >= 0.6f ? 0xFF55FF55 : frac >= 0.3f ? 0xFFFFFF55 : 0xFFFF5555;
            if (mode == 1) text = left + "/" + max;
            else if (mode == 2) text = Math.round(frac * 100f) + "%";
        }
        rows.add(new Row(stack, text, color, mode == 0));
    }

    private List<Row> rows(MinecraftClient client, boolean preview) {
        List<Row> rows = new ArrayList<>();
        if (!preview && client.player == null) return rows;
        VexoroConfig c = VexoroConfig.get();
        addSlot(rows, c.armorShowHelmet, client, preview, EquipmentSlot.HEAD, damaged(Items.DIAMOND_HELMET, 120));
        addSlot(rows, c.armorShowChest, client, preview, EquipmentSlot.CHEST, damaged(Items.DIAMOND_CHESTPLATE, 40));
        addSlot(rows, c.armorShowLegs, client, preview, EquipmentSlot.LEGS, damaged(Items.DIAMOND_LEGGINGS, 200));
        addSlot(rows, c.armorShowBoots, client, preview, EquipmentSlot.FEET, damaged(Items.DIAMOND_BOOTS, 10));
        addSlot(rows, c.armorShowMainHand, client, preview, EquipmentSlot.MAINHAND, damaged(Items.DIAMOND_SWORD, 300));
        addSlot(rows, c.armorShowOffHand, client, preview, EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
        if (c.armorShowFreeSlots) {
            int free = preview ? 12 : countFree(client);
            rows.add(new Row(new ItemStack(Items.CHEST), free + " free", 0xFFFFFFFF, false));
        }
        if (c.armorShowArrows) {
            int arrows = preview ? 64 : countArrows(client);
            rows.add(new Row(new ItemStack(Items.ARROW), String.valueOf(arrows), 0xFFFFFFFF, false));
        }
        return rows;
    }

    private static int countFree(MinecraftClient client) {
        PlayerInventory inv = client.player.getInventory();
        int free = 0;
        for (int i = 0; i < 36; i++) {
            if (inv.getStack(i).isEmpty()) free++;
        }
        return free;
    }

    private static int countArrows(MinecraftClient client) {
        PlayerInventory inv = client.player.getInventory();
        int total = 0;
        int size = Math.min(inv.size(), 41);
        for (int i = 0; i < size; i++) {
            ItemStack s = inv.getStack(i);
            if (s.isOf(Items.ARROW) || s.isOf(Items.SPECTRAL_ARROW) || s.isOf(Items.TIPPED_ARROW)) {
                total += s.getCount();
            }
        }
        return total;
    }

    @Override
    public void render(DrawContext ctx, MinecraftClient client, boolean preview) {
        List<Row> rows = rows(client, preview);
        if (rows.isEmpty()) {
            lastW = 0;
            lastH = 0;
            return;
        }
        VexoroConfig c = VexoroConfig.get();
        TextRenderer tr = client.textRenderer;
        float scale = c.armorScale;

        int textW = 0;
        for (Row r : rows) textW = Math.max(textW, tr.getWidth(r.text()));
        int w = Math.round((20 + textW) * scale);
        int h = Math.round(rows.size() * 18 * scale);

        int sw = ctx.getScaledWindowWidth();
        int sh = ctx.getScaledWindowHeight();
        int x = Math.max(0, Math.min(c.armorX, sw - w));
        int y = Math.max(0, Math.min(c.armorY, sh - h));
        lastX = x;
        lastY = y;
        lastW = w;
        lastH = h;

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate((float) x, (float) y);
        ctx.getMatrices().scale(scale, scale);
        int ry = 0;
        for (Row r : rows) {
            ctx.drawItem(r.stack(), 0, ry);
            if (r.overlay()) ctx.drawStackOverlay(tr, r.stack(), 0, ry);
            if (!r.text().isEmpty()) ctx.drawText(tr, r.text(), 20, ry + 5, r.color(), true);
            ry += 18;
        }
        ctx.getMatrices().popMatrix();
    }
            }
