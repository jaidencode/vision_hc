package org.main.vision.actions;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Opens chests client-side when clicked. */
public class ChestInteractHack extends ActionBase {
    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!isEnabled()) return;
        TileEntity te = event.getWorld().getBlockEntity(event.getPos());
        if (!(te instanceof ChestTileEntity)) return;
        if (event.getWorld().isClientSide()) {
            ChestTileEntity chest = (ChestTileEntity) te;
            Minecraft mc = Minecraft.getInstance();
            PlayerInventory inv = mc.player.inventory;
            ChestContainer cont = new ChestContainer(ContainerType.GENERIC_9x3, 0, inv, chest, 3);
            mc.player.containerMenu = cont;
            mc.setScreen(new ChestScreen(cont, inv, chest.getDisplayName()));
        }
        event.setCanceled(true);
    }
}
