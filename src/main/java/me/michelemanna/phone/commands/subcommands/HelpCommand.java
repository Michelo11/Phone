package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommand implements SubCommand {

    @Override
    public void execute(Player player, String[] args) {
        player.sendMessage(color("&6&m-------&f&m-------&6&m-------&f&m-------&6&m-------&f&m-------"));
        player.sendMessage(color("&r   &fRunning &6Phones&f by &6Michelo11"));
        player.sendMessage(color("&r"));
        player.sendMessage(color("&f/phone give <player> &8»&f Give a phone to a player"));
        player.sendMessage(color("&f/phone menu &8»&f Open the phone menu"));
        player.sendMessage(color("&f/phone number <player> &8»&f Retrieve a player's phone number"));
        player.sendMessage(color("&f/phone regen <player> &8»&f Generate a new phone number"));
        player.sendMessage(color("&f/phone renew <player> <messages> &8»&f Renew the phone subscription"));
        player.sendMessage(color("&f/phone whois <number> &8»&f Check who is the owner of a number"));
        player.sendMessage(color("&f/phone repeater <speed> <range> &8»&f Get a repeater item"));
        player.sendMessage(color("&f/phone message <number> <message> &8»&f Send a message to a player"));
        player.sendMessage(color("&f/phone call <number> &8»&f Call a player"));
        player.sendMessage(color("&f/phone accept &8»&f Accept a call"));
        player.sendMessage(color("&f/phone close &8»&f Close or deny a call"));
        player.sendMessage(color("&f/phone add <number> <name> &8»&f Add a contact"));
        player.sendMessage(color("&f/phone remove <name> &8»&f Remove a contact"));
        player.sendMessage(color("&6&m-------&f&m-------&6&m-------&f&m-------&6&m-------&f&m-------"));
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}