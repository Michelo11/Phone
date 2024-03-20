package me.michelemanna.phone.commands.subcommands;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class GiveCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.create")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("no-permission"));
            return;
        }

        if (args.length != 2) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("give-usage"));
            return;
        }

        ItemStack phone = new ItemBuilder(Material.PRISMARINE_SHARD)
                .setDisplayName("§bPhone")
                .setCustomModelData(1)
                .get();

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("player-not-found"));
            return;
        }

        NBTItem nbtItem = new NBTItem(phone);
        nbtItem.setString("phone_owner", target.getUniqueId().toString());

        target.getInventory().addItem(nbtItem.getItem());
        player.sendMessage(PhonePlugin.getInstance().getMessage("phone-given").replace("%player%", target.getName()));
        target.sendMessage(PhonePlugin.getInstance().getMessage("phone-received").replace("%player%", player.getName()));

        PhonePlugin.getInstance().getDatabase().getPhoneNumber(target.getUniqueId()).thenAccept(number -> {
            if (number == null) {
                number = (long) (Math.random() * 10000000000L);

                PhonePlugin.getInstance().getDatabase().createPhoneNumber(target.getUniqueId(), number);
            }

            target.sendMessage(PhonePlugin.getInstance().getMessage("phone-number").replace("%number%", String.valueOf(number)));
        });
    }
}