package me.michelemanna.phone.gui;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.gui.items.AddItem;
import me.michelemanna.phone.gui.items.ChangePageItem;
import me.michelemanna.phone.gui.items.ContactItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.Window;

import java.util.stream.Collectors;


public class PhoneMenu {
    public static void openPhone(Player player) {
         PhonePlugin.getInstance().getDatabase().getContacts(player.getUniqueId()).thenAccept(contacts -> {
             if (contacts == null) {
                 player.sendMessage("§cAn error occurred while loading your contacts.");
                 return;
             }

             PagedGui.Builder<Item> itemBuilder = PagedGui
                     .items()
                     .setStructure(
                             "# # # # # # # # #",
                             "# . . . . . . . #",
                             "# . . . . . . . #",
                             "# . . . . . . . #",
                             "# # < c a d > # #"
                     )
                     .addIngredient('#', new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" "))
                     .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                     .addIngredient('a', new AddItem())
                     .addIngredient('<', new ChangePageItem(false))
                     .addIngredient('>', new ChangePageItem(true));

             if (!contacts.isEmpty()) {
                 itemBuilder.setContent(contacts.stream().map(ContactItem::new).collect(Collectors.toList()));
             }

             PagedGui<Item> gui = itemBuilder.build();


             Window window = Window.single()
                     .setViewer(player)
                     .setTitle("Phone Menu")
                     .setGui(gui)
                     .build();

             Bukkit.getScheduler().runTask(PhonePlugin.getInstance(), window::open);
         });
    }
}
