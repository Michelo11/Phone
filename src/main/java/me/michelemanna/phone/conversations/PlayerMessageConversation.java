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

public class PlayerMessageConversation extends StringPrompt {
    private final UUID owner;

    public PlayerMessageConversation(UUID owner) {
        this.owner = owner;
    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext context) {
        return PhonePlugin.getInstance().getMessage("conversations.message-prompt");
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (input == null) return this;

        Player player = Bukkit.getPlayer(owner);

        if (player == null) {
            context.getForWhom().sendRawMessage(PhonePlugin.getInstance().getMessage("conversations.player-not-online"));
            return END_OF_CONVERSATION;
        }

        PhonePlugin.getInstance().getDatabase().decrementMessages(((Player) context.getForWhom()).getUniqueId());

        player.sendMessage(PhonePlugin.getInstance().getMessage("conversations.message-sent")
                .replace("%player%", ((Player) context.getForWhom()).getName())
                .replace("%message%", input));

        context.getForWhom().sendRawMessage(PhonePlugin.getInstance().getMessage("conversations.message-sent-self")
                .replace("%player%", player.getName())
                .replace("%message%", input));
        return END_OF_CONVERSATION;
    }
}