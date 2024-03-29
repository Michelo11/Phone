package me.michelemanna.phone.gui;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.gui.items.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.Window;

import java.util.UUID;
import java.util.stream.Collectors;


public class PhoneMenu {
    public static void openPhone(Player player, UUID owner) {
         PhonePlugin.getInstance().getDatabase().getContacts(owner).thenAccept(contacts -> {
             if (contacts == null) {
                 player.sendMessage(PhonePlugin.getInstance().getMessage("gui.error"));
                 return;
             }

             PagedGui.Builder<Item> itemBuilder = PagedGui
                     .items()
                     .setStructure(
                             "# # # # s # # # #",
                             "# . . . . . . . #",
                             "# . . . . . . . #",
                             "# . . . . . . . #",
                             "# # < c a d > # #"
                     )
                     .addIngredient('#', new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" "))
                     .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                     .addIngredient('a', new AddItem())
                     .addIngredient('c', new AcceptItem())
                     .addIngredient('d', new CloseItem(player))
                     .addIngredient('s', new SignalItem(player))
                     .addIngredient('<', new ChangePageItem(false))
                     .addIngredient('>', new ChangePageItem(true));

             if (!contacts.isEmpty()) {
                 itemBuilder.setContent(contacts.stream().map(ContactItem::new).collect(Collectors.toList()));
             }

             PagedGui<Item> gui = itemBuilder.build();


             Window window = Window.single()
                     .setViewer(player)
                     .setTitle(PhonePlugin.getInstance().getMessage("gui.title"))
                     .setGui(gui)
                     .build();

             Bukkit.getScheduler().runTask(PhonePlugin.getInstance(), window::open);
         });
    }
}
