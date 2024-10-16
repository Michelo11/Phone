package me.michelemanna.phone.commands.subcommands;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.commands.SubCommand;
import me.michelemanna.phone.gui.PhoneMenu;
import org.bukkit.entity.Player;

public class MenuCommand implements SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("phone.menu")) {
            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.no-permission"));
            return;
        }

        PhonePlugin.getInstance().getDatabase().getPhoneNumber(player.getUniqueId()).thenAccept(number -> {
            if (number == null) {
                number = (long) (Math.random() * 10000000000L);

                PhonePlugin.getInstance().getDatabase().createPhoneNumber(player.getUniqueId(), number);

                PhonePlugin.getInstance().getDatabase().getNumbersCache().put(player.getUniqueId(), number);
            }

            player.sendMessage(PhonePlugin.getInstance().getMessage("commands.phone-number").replace("%number%", String.valueOf(number)));

            PhonePlugin.getInstance().getDatabase().getContacts(player.getUniqueId()).thenAccept(contacts -> {
                try {
                    if (contacts == null) {
                        player.sendMessage(PhonePlugin.getInstance().getMessage("gui.error"));
                        return;
                    }

                    new PhoneMenu(contacts).open(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}