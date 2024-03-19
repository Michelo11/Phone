package me.michelemanna.phone.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerAddConversation extends StringPrompt {
    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext context) {
        return "Enter the name of the contact you want to add:";
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (input == null) return this;

        context.setSessionData("name", input);

        return new PlayerNumberConversation();
    }
}
