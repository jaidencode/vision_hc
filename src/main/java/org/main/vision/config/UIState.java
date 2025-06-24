package org.main.vision.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraftforge.fml.loading.FMLPaths;

/** Simple UI state persistence for Vision menu. */
public class UIState {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("vision_ui.json");

    public int miscBarX = 10;
    public int miscBarY = 10;
    public boolean hacksExpanded = false;

    // Additional bars for categorizing hacks
    public int renderBarX = 120;
    public int renderBarY = 10;
    public boolean renderExpanded = false;

    public int utilBarX = 230;
    public int utilBarY = 10;
    public boolean utilExpanded = false;

    /** Load state from disk. */
    public static UIState load() {
        if (Files.exists(FILE)) {
            try {
                return GSON.fromJson(Files.newBufferedReader(FILE), UIState.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new UIState();
    }

    /** Save the state to disk. */
    public void save() {
        try {
            Files.write(FILE, GSON.toJson(this).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
