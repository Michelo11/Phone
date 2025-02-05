package me.michelemanna.phone.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class InventoryUtils {
    public static void clearInventory(Player player) {
        WrapperPlayServerWindowItems inventoryPacket = new WrapperPlayServerWindowItems(0, 0, new ArrayList<>(), null);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, inventoryPacket);
    }

    public static void setItem(Player player, int slot, ItemStack item) {
        WrapperPlayServerSetSlot slotPacket = new WrapperPlayServerSetSlot(0, 0, slot, SpigotConversionUtil.fromBukkitItemStack(item.clone()));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, slotPacket);
    }

    public static void restoreInventory(Player player) {
        final ItemStack[] realInventory = player.getInventory().getContents();

        WrapperPlayServerWindowItems inventoryPacket = new WrapperPlayServerWindowItems(0, 0,
                Arrays.stream(realInventory)
                        .map(item -> {
                            if (item != null)
                                return SpigotConversionUtil.fromBukkitItemStack(item.clone());

                            return com.github.retrooper.packetevents.protocol.item.ItemStack.EMPTY;
                        })
                        .collect(Collectors.toList()),
                null
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, inventoryPacket);

        player.updateInventory();
    }
}