package me.michelemanna.phone.conversations;

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
        return "Send a message to the player:";
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (input == null) return this;

        Player player = Bukkit.getPlayer(owner);

        if (player == null) {
            context.getForWhom().sendRawMessage("§cThe player is not online.");
            return END_OF_CONVERSATION;
        }

        player.sendMessage("§7[§a" + ((Player) context.getForWhom()).getName() + "§7] §f" + input);
        context.getForWhom().sendRawMessage("§7[§aYou§7] §f" + input);
        return END_OF_CONVERSATION;
    }
}
