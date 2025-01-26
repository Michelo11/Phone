package me.michelemanna.phone.hooks;

import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import me.michelemanna.phone.managers.CallManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VoiceCallManager extends CallManager implements VoicechatPlugin {
    private VoicechatServerApi api;

    public void register() {
        BukkitVoicechatService service = Bukkit.getServicesManager().load(BukkitVoicechatService.class);

        if (service != null) {
            service.registerPlugin(this);
        }
    }

    @Override
    public void call(Player player, Player target) {
        super.call(player, target);

        Group group = api.groupBuilder()
                .setPersistent(false)
                .setName("Phone Call")
                .setType(Group.Type.ISOLATED)
                .setHidden(true)
                .build();

        setGroup(group, player);
        setGroup(group, target);
    }


    @Override
    public void endCall(Player player) {
        final Player target = getCall(player);

        super.endCall(player);

        if (target == null) return;

        setGroup(null, player);
        setGroup(null, target);
    }

    private void setGroup(Group group, Player player) {
        VoicechatConnection connection = api.getConnectionOf(player.getUniqueId());
        if (connection == null) return;

        connection.setGroup(group);
    }

    @Override
    public String getPluginId() {
        return "Phone";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        this.api = event.getVoicechat();
    }
}
