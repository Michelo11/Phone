package me.michelemanna.phone.gui;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.data.Contact;
import me.michelemanna.phone.data.Repeater;
import me.michelemanna.phone.gui.items.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.*;

public class PhoneMenu implements InventoryHolder {
    private static final Map<Player, ItemStack[]> INVENTORIES = new HashMap<>();
    private final List<Contact> contacts = new ArrayList<>();
    private final String career;
    private final Map<Integer, AbstractItem> items = new HashMap<>();
    private int page = 0;
    private Inventory inventory;

    public PhoneMenu(List<Contact> contacts, String career) {
        this.contacts.addAll(contacts);
        this.career = career;
    }

    public void open(Player player) {
        Bukkit.getScheduler().runTask(PhonePlugin.getInstance(), () -> {
            INVENTORIES.put(player, player.getInventory().getContents());

            redraw(player);

            player.openInventory(inventory);
        });
    }

    public void redraw(Player player) {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, 54, PhonePlugin.getInstance().getMessage("gui.title"));
        }

        player.getInventory().clear();
        inventory.clear();

        for (int i = 0; i < 14; i++) {
            if (i + page * 14 < contacts.size()) {
                Contact contact = contacts.get(i + page * 14);

                int slot = i + 19;
                if (i >= 7) {
                    slot += 2;
                }

                items.put(slot, new ContactItem(contact));
            } else {
                break;
            }
        }

        items.put(47, new ChangePageItem(false, this));

        items.put(51, new ChangePageItem(true, this));

        inventory.setItem(49, getSignalItem(player));

        PlayerInventory playerInventory = player.getInventory();

        items.put(119, new AcceptItem());

        for (int i = 21; i < 24; i++) {
            items.put(100 + i, new AddItem());
        }

        items.put(125, new CloseItem(player));

        Bukkit.getScheduler().runTask(PhonePlugin.getInstance(), () -> {
            items.forEach((slot, item) -> {
                Inventory inv = inventory;

                if (slot >= 100) {
                    inv = playerInventory;
                    slot -= 100;
                }

                inv.setItem(slot, item.getItemProvider().get());
            });
        });
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getSignalItem(Player player) {
        if (!PhonePlugin.getInstance().getConfig().getBoolean("repeater-enabled", true)) {
            return new ItemBuilder(Material.MAP)
                    .setDisplayName(PhonePlugin.getInstance().getMessage("gui.signal.full"))
                    .setCustomModelData(1013)
                    .get();
        }

        Repeater nearest = PhonePlugin.getInstance()
                .getRepeaterManager()
                .getNearest(player.getLocation(), career);

        if (nearest == null) {
            return new ItemBuilder(Material.MAP)
                    .setDisplayName(PhonePlugin.getInstance().getMessage("gui.signal.none"))
                    .setCustomModelData(1011)
                    .get();
        }

        double distance = nearest.location().distance(player.getLocation());

        if (distance <= nearest.range() * 0.3) {
            return new ItemBuilder(Material.MAP)
                    .setDisplayName(PhonePlugin.getInstance().getMessage("gui.signal.full"))
                    .setCustomModelData(1013)
                    .get();
        } else if (distance <= nearest.range()) {
            return new ItemBuilder(Material.MAP)
                    .setDisplayName(PhonePlugin.getInstance().getMessage("gui.signal.half"))
                    .setCustomModelData(1012)
                    .get();
        }

        return new ItemBuilder(Material.MAP)
                .setDisplayName(PhonePlugin.getInstance().getMessage("gui.signal.none"))
                .setCustomModelData(1011)
                .get();
    }

    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if (event.getClickedInventory() instanceof PlayerInventory) {
            slot += 100;
        }

        if (items.containsKey(slot)) {
            items.get(slot).handleClick(event.getClick(), (Player) event.getWhoClicked(), event);
        }
    }

    public static ItemStack[] getPlayerContent(Player player) {
        return INVENTORIES.remove(player);
    }

    public static void closeAll() {
        INVENTORIES.forEach((player, content) -> {
            player.closeInventory();
            player.getInventory().clear();
            player.getInventory().setContents(content);
        });
    }

    public int getCurrentPage() {
        return page;
    }

    public int getPageAmount() {
        return (int) Math.ceil(contacts.size() / 14.0);
    }

    public void next(Player player) {
        if (page + 1 < getPageAmount()) {
            page++;
            this.redraw(player);
        }
    }

    public void previous(Player player) {
        if (page > 0) {
            page--;
            this.redraw(player);
        }
    }
}
