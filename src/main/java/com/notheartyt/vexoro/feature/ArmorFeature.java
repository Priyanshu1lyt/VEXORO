package com.notheartyt.vexoro.feature;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public final class ArmorFeature extends Feature {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public ArmorFeature(BooleanSupplier getter, Consumer<Boolean> setter) {
        super("armor_hud", "Armor HUD", getter, setter);
    }

    @Override
    public void render(DrawContext context, MinecraftClient client) {
        if (client.player == null) return;
        int x = 4;
        int y = context.getScaledWindowHeight() / 2 - 36;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = client.player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                context.drawItem(stack, x, y);
                context.drawStackOverlay(client.textRenderer, stack, x, y);
            }
            y += 18;
        }
    }
}
