package me.michelemanna.phone.gui.items;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.conversations.PlayerMessageConversation;
import me.michelemanna.phone.data.Contact;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.builder.SkullBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.Collections;

public class ContactItem extends AbstractItem {
    private final Contact contact;

    public ContactItem(Contact contact) {
        this.contact = contact;
    }

    @Override
    public ItemProvider getItemProvider() {
        try {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(contact.getOwner());
            SkullBuilder builder;

            if (owner.getName() == null) {
                builder = new SkullBuilder(owner.getUniqueId());
            } else {
                builder = new SkullBuilder(owner.getName());
            }

            return builder
                    .setDisplayName(contact.getName())
                    .setLegacyLore(Collections.singletonList(contact.getNumber() + ""));
        } catch (Exception | Error e) {
            return new ItemBuilder(Material.PLAYER_HEAD)
                    .setDisplayName(contact.getName())
                    .setLegacyLore(Collections.singletonList(contact.getNumber() + ""));
        }
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        player.closeInventory();

        PhonePlugin.getInstance().getDatabase().getSim(player.getUniqueId()).thenAccept(sim -> {

            if (sim == null) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("gui.phone-not-renewed"));
                return;
            }

            if (System.currentTimeMillis() - sim.lastRenew() > 1000*60*60*24*PhonePlugin.getInstance().getConfig().getLong("carriers." + sim.carrier() + ".duration")) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("gui.phone-expired"));
                return;
            }

            if (PhonePlugin.getInstance().getConfig().getBoolean("repeater-enabled", true) &&
                    !PhonePlugin.getInstance().getRepeaterManager().isNear(player.getLocation(), sim.carrier())) {
                player.sendMessage(PhonePlugin.getInstance().getMessage("gui.no-signal"));
                return;
            }

            switch (clickType) {
                case LEFT:
                    if (sim.messagesSent() >= PhonePlugin.getInstance().getConfig().getLong("carriers." + sim.carrier() + ".messages")) {
                        player.sendMessage(PhonePlugin.getInstance().getMessage("gui.no-messages"));
                        return;
                    }

                    PlayerMessageConversation conversation = new PlayerMessageConversation(contact.getOwner());

                    new ConversationFactory(PhonePlugin.getInstance())
                            .withEscapeSequence("cancel")
                            .withFirstPrompt(conversation)
                            .withModality(false)
                            .withLocalEcho(false)
                            .buildConversation(player)
                            .begin();
                    break;
                case RIGHT:
                    Player target = Bukkit.getPlayer(contact.getOwner());

                    if (target == null) {
                        player.sendMessage(PhonePlugin.getInstance().getMessage("gui.player-not-found"));
                        return;
                    }

                    PhonePlugin.getInstance().getCallManager().getPendingCalls().put(target, player);

                    player.sendMessage(PhonePlugin.getInstance().getMessage("gui.calling").replace("%player%", target.getName()));
                    target.sendMessage(PhonePlugin.getInstance().getMessage("gui.calling-other").replace("%player%", player.getName()));
                    break;
                case DROP:
                    PhonePlugin.getInstance().getDatabase().deleteContact(player.getUniqueId(), contact.getName());
                    break;
            }
        });
    }
}
