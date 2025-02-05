package me.michelemanna.phone.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.michelemanna.phone.PhonePlugin;
import me.michelemanna.phone.data.Repeater;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhonePlaceholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "phone";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Michelo11";
    }

    @Override
    public @NotNull String getVersion() {
        return PhonePlugin.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        switch (params) {
            case "number":
                return PhonePlugin.getInstance().getDatabase().getNumbersCache().getOrDefault(player.getUniqueId(), 0L).toString();
            case "distance": {
                Repeater nearest = PhonePlugin.getInstance().getRepeaterManager().getNearest(player.getLocation());
                return nearest == null ? "N/A" : String.valueOf(Math.round(nearest.location().distance(player.getLocation())));
            }
            case "career":
                String career = PhonePlugin.getInstance().getDatabase().getCareersCache().get(player.getUniqueId());
                return career == null ? "N/A" : career;
            case "connection": {
                Repeater nearest = PhonePlugin.getInstance()
                        .getRepeaterManager()
                        .getNearest(player.getLocation());

                if (nearest == null) {
                    return "No connection";
                }

                double distance = nearest.location().distance(player.getLocation());

                if (distance <= nearest.range() * 0.3) {
                    return "Full connection";
                } else if (distance <= nearest.range()) {
                    return "Half connection";
                }

                return "No connection";
            }
        }

        return null;
    }
}
