package me.michelemanna.phone.commands.subcommands;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class GiveCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.create")) {
            player.sendMessage("§cYou don't have permission to use this command");
            return;
        }

        if (args.length != 2) {
            player.sendMessage("§cUsage: /phone give <player>");
            return;
        }

        ItemStack phone = new ItemBuilder(Material.PRISMARINE_SHARD)
                .setDisplayName("§bPhone")
                .setCustomModelData(1)
                .get();

        NBTItem nbtItem = new NBTItem(phone);
        nbtItem.setString("phone_owner", player.getUniqueId().toString());

        player.getInventory().addItem(nbtItem.getItem());
        player.sendMessage("§aYou gave a phone to " + args[1]);

        Player target = PhonePlugin.getInstance().getServer().getPlayer(args[1]);

        if (target == null) {
            player.sendMessage("§cThe player is not online");
            return;
        }

        PhonePlugin.getInstance().getDatabase().getPhoneNumber(target.getUniqueId()).thenAccept(number -> {
            if (number == null) {
                number = (long) (Math.random() * 10000000000L);

                PhonePlugin.getInstance().getDatabase().createPhoneNumber(player.getUniqueId(), number);
            }

            player.sendMessage("§aThe phone number is: " + number);
        });
    }
}