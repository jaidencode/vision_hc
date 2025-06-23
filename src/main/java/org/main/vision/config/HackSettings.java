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
