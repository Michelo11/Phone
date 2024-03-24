package me.michelemanna.phone.conversations;

import me.michelemanna.phone.PhonePlugin;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerNumberConversation extends StringPrompt {
    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext context) {
        return PhonePlugin.getInstance().getMessage("conversations.enter-number");
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
                    context.getForWhom().sendRawMessage(PhonePlugin.getInstance().getMessage("conversations.number-not-found"));
                    return;
                }

                PhonePlugin.getInstance().getDatabase().addContact(owner, ((Player) context.getForWhom()).getUniqueId(), name, Long.parseLong(input));
                context.getForWhom().sendRawMessage(PhonePlugin.getInstance().getMessage("conversations.contact-added"));
            } catch (NumberFormatException e) {
                context.getForWhom().sendRawMessage(PhonePlugin.getInstance().getMessage("conversations.invalid-number"));
            }
        });

        return null;
    }
}
