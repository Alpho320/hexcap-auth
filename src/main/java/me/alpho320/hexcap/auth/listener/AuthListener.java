package me.alpho320.hexcap.auth.listener;

import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.RegisterEvent;
import me.alpho320.hexcap.auth.HexCapAuthPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class AuthListener implements Listener {

    private final @NotNull HexCapAuthPlugin plugin;

    public AuthListener(@NotNull HexCapAuthPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRegister(RegisterEvent event) {
        Player player = event.getPlayer();
        plugin.handler().update(player, plugin.dataSource().getPassword(player.getName()).getHash());
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();
        plugin.handler().update(player, plugin.dataSource().getPassword(player.getName()).getHash());
    }

}