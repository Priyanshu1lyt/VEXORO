package com.notheartyt.vexoro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/** Small JSON config. Stored in the game's config folder as vexoro.json. */
public final class VexoroConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean armorHud = true;
    public boolean inventoryHud = false;
    public boolean effectsHud = true;
    public boolean customCrosshair = true;
    public boolean hitboxDisplay = false;
    public boolean reachIndicator = true;
    public boolean positionHud = true;
    public boolean cpsCounter = true;
    public String accentColor = "#A855F7";
    public float hudScale = 1.0f;
    public float hudOpacity = 1.0f;

    // Potion HUD (potionX = -1 means "top right")
    public int potionX = -1;
    public int potionY = 4;
    public boolean potionHorizontal = false;
    public boolean potionMini = false;
    public int potionGap = 2;
    public float potionBgOpacity = 0.4f;

    // Armor HUD
    public int armorX = 4;
    public int armorY = 60;
    public float armorScale = 1.0f;
    public int armorDamageMode = 0; // 0 = bar, 1 = number, 2 = percent
    public boolean armorShowHelmet = true;
    public boolean armorShowChest = true;
    public boolean armorShowLegs = true;
    public boolean armorShowBoots = true;
    public boolean armorShowMainHand = false;
    public boolean armorShowOffHand = false;
    public boolean armorShowFreeSlots = true;
    public boolean armorShowArrows = true;

    private static VexoroConfig instance = new VexoroConfig();

    private VexoroConfig() {}

    public static VexoroConfig get() {
        return instance;
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("vexoro.json");
    }

    public static void load() {
        try {
            Path file = file();
            Files.createDirectories(file.getParent());
            if (Files.exists(file)) {
                VexoroConfig loaded = GSON.fromJson(Files.readString(file), VexoroConfig.class);
                if (loaded != null) {
                    loaded.sanitize();
                    instance = loaded;
                }
            } else {
                save();
            }
        } catch (IOException | RuntimeException exception) {
            VexoroClient.LOGGER.error("Could not load VEXORO config; using defaults.", exception);
        }
    }

    public static void save() {
        try {
            Path file = file();
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(instance));
        } catch (IOException exception) {
            VexoroClient.LOGGER.error("Could not save VEXORO config.", exception);
        }
    }

    private void sanitize() {
        if (accentColor == null) accentColor = "#A855F7";
        hudScale = Math.max(0.5f, Math.min(2.0f, hudScale));
        hudOpacity = Math.max(0.1f, Math.min(1.0f, hudOpacity));
        potionGap = Math.max(0, Math.min(8, potionGap));
        potionBgOpacity = Math.max(0f, Math.min(1f, potionBgOpacity));
        armorScale = Math.max(0.5f, Math.min(2.5f, armorScale));
        armorDamageMode = Math.max(0, Math.min(2, armorDamageMode));
    }

    /** Accent color as ARGB with the configured HUD opacity applied. */
    public int accentArgb() {
        int rgb = 0xA855F7;
        try {
            rgb = Integer.parseInt(accentColor.replace("#", ""), 16) & 0xFFFFFF;
        } catch (RuntimeException ignored) {
            // keep default
        }
        return (alpha() << 24) | rgb;
    }

    /** White text color with the configured HUD opacity applied. */
    public int textArgb() {
        return (alpha() << 24) | 0xFFFFFF;
    }

    private int alpha() {
        return Math.max(8, Math.min(255, Math.round(hudOpacity * 255f)));
    }
            }
