package me.michelemanna.phone.commands;

import org.bukkit.command.CommandSender;

public interface SubCommand {
    void execute(CommandSender player, String[] args);
}