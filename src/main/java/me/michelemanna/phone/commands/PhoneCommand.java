package me.michelemanna.phone.commands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.subcommands.GiveCommand;
import me.michelemanna.phone.commands.subcommands.NumberCommand;
import me.michelemanna.phone.commands.subcommands.RegenCommand;
import me.michelemanna.phone.commands.subcommands.WhoisCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PhoneCommand implements CommandExecutor {
    private final PhonePlugin plugin;
    private Map<String, SubCommand> subCommands = new HashMap<>();

    public PhoneCommand(PhonePlugin plugin) {
        this.plugin = plugin;
        this.subCommands.put("give", new GiveCommand());
        this.subCommands.put("number", new NumberCommand());
        this.subCommands.put("regen", new RegenCommand());
        this.subCommands.put("whois", new WhoisCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cYou must specify a subcommand");  // TODO: Add help message
            return true;
        }

        if (subCommands.containsKey(args[0].toLowerCase())) {
            subCommands.get(args[0].toLowerCase()).execute(player, args);
            return true;
        }

        return false;
    }
}
