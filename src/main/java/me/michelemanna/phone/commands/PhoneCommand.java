package me.michelemanna.phone.commands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PhoneCommand implements CommandExecutor {
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public PhoneCommand(PhonePlugin plugin) {
        this.subCommands.put("give", new GiveCommand());
        this.subCommands.put("number", new NumberCommand());
        this.subCommands.put("regen", new RegenCommand());
        this.subCommands.put("whois", new WhoisCommand());
        this.subCommands.put("renew", new RenewCommand());
        this.subCommands.put("repeater", new RepeaterCommand());
        this.subCommands.put("message", new MessageCommand());
        this.subCommands.put("call", new CallCommand());
        this.subCommands.put("accept", new AcceptCommand());
        this.subCommands.put("close", new CloseCommand());
        this.subCommands.put("help", new HelpCommand());
        this.subCommands.put("add", new AddCommand());
        this.subCommands.put("remove", new RemoveCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PhonePlugin.getInstance().getMessage("commands.player-only"));
            return true;
        }

        if (args.length == 0) {
            subCommands.get("help").execute(player, args);
            return true;
        }

        if (subCommands.containsKey(args[0].toLowerCase())) {
            subCommands.get(args[0].toLowerCase()).execute(player, args);
            return true;
        }

        return false;
    }
}
