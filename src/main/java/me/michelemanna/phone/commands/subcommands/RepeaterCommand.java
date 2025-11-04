package me.michelemanna.phone.commands.subcommands;

import de.tr7zw.changeme.nbtapi.NBT;
import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class RepeaterCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-only"));
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("phone.repeater")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        if (args.length != 4) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.repeater-usage"));
            return;
        }

        if (!PhonePlugin.getInstance().getConfig().getBoolean("repeater-enabled")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.repeater-disabled"));
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

        if (PhonePlugin.getInstance().getConfig().getConfigurationSection("carriers." + args[3].toLowerCase()) == null) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.invalid-carrier"));
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
            nbt.setString("carrier", args[3].toLowerCase());
        });

        player.getInventory().addItem(repeater);

        player.sendMessage(PhonePlugin.getInstance().getMessage("commands.repeater-given"));
    }
}
