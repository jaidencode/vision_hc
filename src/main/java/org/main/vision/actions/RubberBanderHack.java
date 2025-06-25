package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

/**
 * Delays and reorders outgoing PvP packets to create mild desync effects.
 */
public class RubberBanderHack extends ActionBase {
    private static final int DELAY_TICKS = 2;

    private static class Queued {
        final IPacket<?> packet;
        final int sendTick;
        Queued(IPacket<?> p, int t) { this.packet = p; this.sendTick = t; }
    }

    private final Queue<Queued> queue = new LinkedList<>();

    public static boolean handleSend(IPacket<?> packet) {
        RubberBanderHack hack = org.main.vision.VisionClient.getRubberBanderHack();
        if (hack.isEnabled() && hack.shouldQueue(packet)) {
            Minecraft mc = Minecraft.getInstance();
            ClientWorld world = mc.level;
            if (world != null) {
                int tick = (int) world.getGameTime() + DELAY_TICKS;
                hack.queue.add(new Queued(packet, tick));
                return true;
            }
        }
        return false;
    }

    private boolean shouldQueue(IPacket<?> packet) {
        if (!(packet instanceof CPlayerPacket || packet instanceof CUseEntityPacket)) return false;
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        ClientWorld world = mc.level;
        if (player == null || world == null) return false;
        for (PlayerEntity p : world.players()) {
            if (p != player && p.distanceToSqr(player) < 9.0D) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDisable() {
        flushAll();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        flushReady();
    }

    private void flushReady() {
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        if (world == null || mc.getConnection() == null) return;
        int tick = (int) world.getGameTime();
        if (queue.isEmpty()) return;
        List<IPacket<?>> send = new ArrayList<>();
        while (!queue.isEmpty() && queue.peek().sendTick <= tick) {
            send.add(queue.poll().packet);
        }
        if (send.isEmpty()) return;
        Collections.reverse(send);
        NetworkManager nm = mc.getConnection().getConnection();
        for (IPacket<?> p : send) {
            nm.send(p);
        }
    }

    private void flushAll() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) return;
        NetworkManager nm = mc.getConnection().getConnection();
        while (!queue.isEmpty()) {
            nm.send(queue.poll().packet);
        }
    }
}
