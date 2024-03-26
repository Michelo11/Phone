package me.michelemanna.phone.commands.subcommands;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class RepeaterCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.repeater")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        ItemStack repeater = new ItemBuilder(Material.IRON_BLOCK)
                .setDisplayName("§bRepeater")
                .setCustomModelData(1)
                .get();


        NBTItem nbtItem = new NBTItem(repeater);
        nbtItem.setBoolean("repeater", true);

        player.getInventory().addItem(nbtItem.getItem());

        player.sendMessage(PhonePlugin.getInstance().getMessage("commands.repeater-given"));
    }
}
