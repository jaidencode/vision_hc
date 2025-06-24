package org.main.vision;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

/**
 * Keeps the game window title fixed to "vision - v(version)".
 */
@Mod.EventBusSubscriber(modid = "vision", value = Dist.CLIENT)
public class WindowTitleHandler {
    private static final String TITLE;

    static {
        String version = ModList.get().getModContainerById("vision")
                .map(c -> c.getModInfo().getVersion().toString())
                .orElse("unknown");
        TITLE = "vision - v" + version;
    }

    /** Set the title immediately when initialized. */
    static void init() {
        Minecraft.getInstance().getWindow().setTitle(TITLE);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft.getInstance().getWindow().setTitle(TITLE);
        }
    }
}
