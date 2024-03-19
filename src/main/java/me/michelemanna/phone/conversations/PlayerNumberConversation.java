package me.michelemanna.phone.conversations;

import me.michelemanna.phone.PhonePlugin;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerNumberConversation extends StringPrompt {
    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext context) {
        return "Enter the number of the contact you want to add:";
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (input == null) return this;

        String name = (String) context.getSessionData("name");

        Bukkit.getScheduler().runTaskAsynchronously(PhonePlugin.getInstance(), () -> {
            try {
                UUID owner = PhonePlugin.getInstance().getDatabase().getOwner(Long.parseLong(input)).join();

                if (owner == null) {
                    context.getForWhom().sendRawMessage("§cThis number is not associated with any player.");
                    return;
                }

                PhonePlugin.getInstance().getDatabase().addContact(owner, name, Long.parseLong(input));
                context.getForWhom().sendRawMessage("§aContact added successfully.");
            } catch (NumberFormatException e) {
                context.getForWhom().sendRawMessage("§cPlease enter a valid number.");
            }
        });

        return null;
    }
}
