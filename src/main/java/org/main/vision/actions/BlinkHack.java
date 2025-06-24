package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.IPacket;
import java.util.LinkedList;
import java.util.Queue;

/** Buffers outgoing packets until disabled. */
public class BlinkHack extends ActionBase {
    private final Queue<IPacket<?>> queue = new LinkedList<>();

    public static boolean handleSend(IPacket<?> packet) {
        BlinkHack hack = org.main.vision.VisionClient.getBlinkHack();
        if (hack.isEnabled()) {
            hack.queue.add(packet);
            return true;
        }
        return false;
    }

    @Override
    protected void onDisable() {
        NetworkManager nm = Minecraft.getInstance().getConnection().getConnection();
        while (!queue.isEmpty()) {
            nm.send(queue.poll());
        }
    }
}
