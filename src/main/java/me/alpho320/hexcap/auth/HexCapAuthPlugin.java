package me.alpho320.hexcap.auth;

import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.datasource.DataSource;
import me.alpho320.fabulous.core.bukkit.BukkitCore;
import me.alpho320.fabulous.core.bukkit.util.BukkitConfiguration;
import me.alpho320.fabulous.core.bukkit.util.debugger.Debug;
import me.alpho320.hexcap.auth.handler.AuthHandler;
import me.alpho320.hexcap.auth.listener.AuthListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class HexCapAuthPlugin extends JavaPlugin {

    private HexCapAuthPlugin instance;
    private AuthMe authMe;
    private DataSource dataSource;

    private BukkitConfiguration config;
    private AuthHandler handler;

    @Override
    public void onEnable() {
        if (instance != null) throw new IllegalStateException("HexCapAuth cannot be started twice");
        this.instance = this;

        if (!getServer().getPluginManager().isPluginEnabled("AuthMe"))
            throw new IllegalStateException("AuthMe disabled!");

        new BukkitCore(this).init("HexCapAuth");
        this.authMe = (AuthMe) getServer().getPluginManager().getPlugin("AuthMe");

        try {
            Field field = AuthMeApi.getInstance().getClass().getDeclaredField("dataSource");
            field.setAccessible(true);
            this.dataSource = (DataSource) field.get(AuthMeApi.getInstance());
            Debug.debug(0, " | DataSource: " + dataSource);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("An error accorded while hooking into AuthMe!");
        }

        this.config = new BukkitConfiguration("config", this);
        this.handler = new AuthHandler(this).init();

        getServer().getPluginManager().registerEvents(new AuthListener(this), this);

        Debug.debug(0, "");
        Debug.debug(0, "============ HexCapAuthPlugin ============");
        Debug.debug(0, "");
        Debug.debug(0, "HexCapAuthPlugin Active!");
        Debug.debug(0, "Version: " + getDescription().getVersion());
        Debug.debug(0, "Developer: Alpho320#9202");
        Debug.debug(0, "");
        Debug.debug(0, "============ HexCapAuthPlugin ============");
        Debug.debug(0, "");
    }

    @Override
    public void onDisable() {
        handler.close();

        Debug.debug(0, "");
        Debug.debug(0, "============ HexCapAuthPlugin ============");
        Debug.debug(0, "");
        Debug.debug(0, "HexCapAuthPlugin Deactive!");
        Debug.debug(0, "Version: " + getDescription().getVersion());
        Debug.debug(0, "Developer: Alpho320#9202");
        Debug.debug(0, "");
        Debug.debug(0, "============ HexCapAuthPlugin ============");
        Debug.debug(0, "");
    }

    @Override
    public @NotNull BukkitConfiguration getConfig() {
        return this.config;
    }

    public @NotNull AuthHandler handler() {
        return this.handler;
    }


    public @NotNull AuthMe authMe() {
        return this.authMe;
    }

    public @NotNull DataSource dataSource() {
        return this.dataSource;
    }

}