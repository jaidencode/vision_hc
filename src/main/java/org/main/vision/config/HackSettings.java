package org.main.vision.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Stores tweakable values for each hack and persists them to disk. */
public class HackSettings {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("vision_hacks.json");

    /** Movement speed multiplier for SpeedHack. */
    public float speedMultiplier = 1.5f;
    /** Jump velocity for JumpHack. */
    public double jumpVelocity = 1.2D;
    /** Horizontal speed for FlyHack. */
    public double flyHorizontalSpeed = 0.75D;
    /** Vertical speed for FlyHack. */
    public double flyVerticalSpeed = 0.5D;
    /** Hover velocity for FlyHack. */
    public double flyHoverVelocity = 0.0D;
    /** Buoyancy applied when walking on water for JesusHack. */
    public double jesusBuoyancy = 0.0D;
    /** Fall distance threshold before NoFall activates. */
    public double noFallThreshold = 2.0D;
    /** List of block ids highlighted by XRayHack. */
    public java.util.List<String> xrayBlocks = new java.util.ArrayList<>();

    /** Alias used by the SpoofName hack. */
    public String spoofName = "";
    /** Whether incoming chat should use the alias. */
    public boolean spoofIncoming = true;
    /** Whether outgoing chat should use the alias. */
    public boolean spoofOutgoing = true;


    /** Helper to check if a block should be highlighted. */
    public boolean isXrayTarget(net.minecraft.block.Block block) {
        if (block == null) return false;
        String id = block.getRegistryName().toString();
        return xrayBlocks.contains(id);
    }

    /** Load settings from disk. */
    public static HackSettings load() {
        if (Files.exists(FILE)) {
            try {
                return GSON.fromJson(Files.newBufferedReader(FILE), HackSettings.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HackSettings();
    }

    /** Save the settings to disk. */
    public void save() {
        try {
            Files.write(FILE, GSON.toJson(this).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
