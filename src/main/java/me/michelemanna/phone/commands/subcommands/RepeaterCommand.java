package me.michelemanna.phone.commands.subcommands;

import de.tr7zw.changeme.nbtapi.NBT;
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

        if (args.length != 3) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.repeater-usage"));
            return;
        }

        int speed, range;

        try {
            speed = Integer.parseInt(args[1]);
            range = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-arguments"));
            return;
        }

        ItemStack repeater = new ItemBuilder(Material.IRON_BLOCK)
                .setDisplayName("§bRepeater")
                .setCustomModelData(1)
                .get();


        NBT.modify(repeater, nbt -> {
            nbt.setBoolean("repeater", true);
            nbt.setInteger("speed", speed);
            nbt.setInteger("range", range);
        });

        player.getInventory().addItem(repeater);

        player.sendMessage(PhonePlugin.getInstance().getMessage("commands.repeater-given"));
    }
}
