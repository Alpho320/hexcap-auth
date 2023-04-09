package me.alpho320.hexcap.auth.handler;

import me.alpho320.fabulous.core.bukkit.util.BukkitConfiguration;
import me.alpho320.fabulous.core.bukkit.util.debugger.Debug;
import me.alpho320.hexcap.auth.HexCapAuthPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

public class AuthHandler {

    private final @NotNull HexCapAuthPlugin plugin;
    private @Nullable Connection connection;

    public AuthHandler(@NotNull HexCapAuthPlugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull AuthHandler init() {
        try {
            Debug.debug(0, " | Connecting...");
            Class.forName("com.mysql.cj.jdbc.Driver");

            BukkitConfiguration config = plugin.getConfig();
            String connectionUri = "jdbc:mysql://"
                    + config.getString("Data.ip")
                    + ":" + config.getString("Data.port")
                    + "/" + config.getString("Data.database")
                    + "?autoReconnect=true&useSSL=false&useUnicode=true";

            connection = DriverManager.getConnection(connectionUri, config.getString("Data.username"), config.getString("Data.password"));
            connection.setAutoCommit(true);

            String realmsTable = "CREATE TABLE IF NOT EXISTS hexcap_auth_data (" +
                    "id VARCHAR(64) not null, " +
                    "name VARCHAR(64) not null, " +
                    "password LONGTEXT not null, " +
                    "constraint uuid_pk primary key(id))";

            Statement createTable = connection.createStatement();
            createTable.executeUpdate(realmsTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void update(@NotNull Player player, @NotNull String hashedPass) {
        update(player.getUniqueId(), player.getName(), hashedPass);
    }

    public void update(@NotNull UUID id, @NotNull String name, @NotNull String hashedPass) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ResultSet res = connection.createStatement().executeQuery("SELECT * FROM hexcap_auth_data WHERE id='" + id + "'");

                if (res.next()) {
                    PreparedStatement upt = connection.prepareStatement("UPDATE hexcap_auth_data SET" +
                            " password = ?" +
                            " WHERE id='" + id + "'");

                    upt.setString(1, hashedPass);
                    upt.executeUpdate();
                } else {
                    PreparedStatement newUser = connection.prepareStatement("INSERT INTO hexcap_auth_data (" +
                            "id, name, password)" +
                            "VALUES (?,?, ?)");

                    newUser.setString(1, String.valueOf(id));
                    newUser.setString(2, name);
                    newUser.setString(3, hashedPass);

                    newUser.executeUpdate();
                }

                Debug.debug(2, "AuthHandler | Player of " + name + " password updated!");
                res.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
