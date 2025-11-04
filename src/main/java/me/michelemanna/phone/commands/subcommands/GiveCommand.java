package me.michelemanna.phone.commands.subcommands;

import de.tr7zw.changeme.nbtapi.NBT;
import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class GiveCommand implements SubCommand {
    @Override
    public void execute(CommandSender player, String[] args) {
        if (!player.hasPermission("phone.create")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length < 3 || args.length > 4) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.give-usage"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-not-found"));
            return;
        }

        String type = args[2];

        int customModelData = PhonePlugin.getInstance().getConfig().getInt("phone-type." + type, -1);
        if (customModelData == -1) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.give-usage"));
            return;
        }

        Long customNumber = null;
        if (args.length == 4) {
            try {
                customNumber = Long.parseLong(args[3]);
                if (customNumber < 0) {
                    player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-number"));
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-number"));
                return;
            }
        }

        ItemStack phone = new ItemBuilder(Material.PRISMARINE_SHARD)
                .setDisplayName("§bPhone")
                .setCustomModelData(customModelData)
                .get();

        NBT.modify(phone, nbt -> {
            nbt.setString("phone_owner", target.getUniqueId().toString());
        });

        target.getInventory().addItem(phone);

        player.sendMessage(PhonePlugin.getInstance().getMessage("commands.phone-given").replace("%player%", target.getName()));

        if (!target.equals(player)) {
            target.sendMessage(PhonePlugin.getInstance().getMessage("commands.phone-received").replace("%player%", player.getName()));
        }

        Long finalCustomNumber = customNumber;
        PhonePlugin.getInstance().getDatabase().getPhoneNumber(target.getUniqueId()).thenAccept(number -> {
            if (number == null) {
                number = finalCustomNumber != null ? finalCustomNumber : (long) (Math.random() * 10000000000L);

                PhonePlugin.getInstance().getDatabase().createPhoneNumber(target.getUniqueId(), number);

                PhonePlugin.getInstance().getDatabase().getNumbersCache().put(target.getUniqueId(), number);
            }

            target.sendMessage(PhonePlugin.getInstance().getMessage("commands.phone-number").replace("%number%", String.valueOf(number)));
        });
    }
}