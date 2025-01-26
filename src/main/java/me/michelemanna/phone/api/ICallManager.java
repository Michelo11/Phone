package me.michelemanna.phone.api;

import org.bukkit.entity.Player;

import java.util.Map;

public interface ICallManager {
    Map<Player, Player> getCalls();
    Map<Player, Player> getPendingCalls();
    boolean isCalling(Player player);
    Player getCall(Player player);
    void sendMessage(Player player, String message);
    void call(Player player, Player target);
    void endCall(Player player);
}