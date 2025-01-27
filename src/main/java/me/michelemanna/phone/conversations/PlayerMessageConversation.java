package me.michelemanna.phone.conversations;

import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.data.Repeater;
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

        PhonePlugin.getInstance().getDatabase().incrementMessages(((Player) context.getForWhom()).getUniqueId());

        PhonePlugin.getInstance().getDatabase().getSim(owner).thenAccept(sim -> {
            if (sim == null) {
                context.getForWhom().sendRawMessage(PhonePlugin.getInstance().getMessage("conversations.no-own-sim"));
                return;
            }

            Repeater nearestRepeater = PhonePlugin.getInstance().getRepeaterManager().getNearest(player.getLocation(), sim.career());

            if (nearestRepeater == null || nearestRepeater.range() < player.getLocation().distance(nearestRepeater.location())) {
                context.getForWhom().sendRawMessage(PhonePlugin.getInstance().getMessage("conversations.no-signal"));
                return;
            }

            double delay = nearestRepeater.speed() * 0.1 + player.getLocation().distance(nearestRepeater.location());

            Bukkit.getScheduler().runTaskLater(PhonePlugin.getInstance(), () -> {
                player.sendMessage(PhonePlugin.getInstance().getMessage("conversations.message-sent")
                        .replace("%player%", ((Player) context.getForWhom()).getName())
                        .replace("%message%", input));

                context.getForWhom().sendRawMessage(PhonePlugin.getInstance().getMessage("conversations.message-sent-self")
                        .replace("%player%", player.getName())
                        .replace("%message%", input));
            }, (long) (delay));
        });

        return END_OF_CONVERSATION;
    }
}