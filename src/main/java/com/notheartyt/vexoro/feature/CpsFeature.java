package com.notheartyt.vexoro.feature;

import java.util.ArrayDeque;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Counts left clicks in the last second by polling the mouse button every frame. */
public final class CpsFeature extends Feature {
    private final ArrayDeque<Long> clicks = new ArrayDeque<>();
    private boolean wasDown;

    public CpsFeature(BooleanSupplier getter, Consumer<Boolean> setter) {
        super("cps", "CPS Counter", getter, setter);
    }

    @Override
    public void update(MinecraftClient client) {
        boolean down = client.currentScreen == null
                && GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        long now = System.currentTimeMillis();
        if (down && !wasDown) clicks.addLast(now);
        wasDown = down;
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000L) clicks.removeFirst();
    }

    @Override
    public List<String> lines(MinecraftClient client) {
        return List.of("CPS: " + clicks.size());
    }
}
