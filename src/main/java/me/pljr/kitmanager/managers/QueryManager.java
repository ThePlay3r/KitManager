package me.pljr.kitmanager.managers;

import me.pljr.kitmanager.KitManager;
import me.pljr.kitmanager.objects.CorePlayer;
import me.pljr.pljrapi.database.DataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QueryManager {
    private final DataSource dataSource;
    private final Plugin plugin;

    public QueryManager(DataSource dataSource, Plugin plugin){
        this.dataSource = dataSource;
        this.plugin = plugin;
    }

    public void loadPlayer(UUID uuid){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()->{
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM kitmanager_players WHERE uuid=?"
                );
                preparedStatement.setString(1, uuid.toString());
                ResultSet results = preparedStatement.executeQuery();
                HashMap<String, Long> cooldowns = new HashMap<>();
                while (results.next()){
                    cooldowns.put(results.getString("kit"), results.getLong("time"));
                }
                KitManager.getPlayerManager().setCorePlayer(uuid, new CorePlayer(cooldowns));
                dataSource.close(connection, preparedStatement, results);
            }catch (SQLException e){
                e.printStackTrace();
            }
        });
    }

    public void loadPlayerSync(UUID uuid){
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM kitmanager_players WHERE uuid=?"
            );
            preparedStatement.setString(1, uuid.toString());
            ResultSet results = preparedStatement.executeQuery();
            HashMap<String, Long> cooldowns = new HashMap<>();
            while (results.next()){
                cooldowns.put(results.getString("kit"), results.getLong("time"));
            }
            KitManager.getPlayerManager().setCorePlayer(uuid, new CorePlayer(cooldowns));
            dataSource.close(connection, preparedStatement, results);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void savePlayer(UUID uuid){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()->{
            try {
                CorePlayer corePlayer = KitManager.getPlayerManager().getCorePlayer(uuid);
                HashMap<String, Long> cooldowns = corePlayer.getCooldowns();
                for (Map.Entry<String, Long> entry : cooldowns.entrySet()){
                    Connection remove = dataSource.getConnection();
                    PreparedStatement removeStatement = remove.prepareStatement(
                            "DELETE FROM kitmanager_players WHERE uuid=? AND kit=?"
                    );
                    removeStatement.setString(1, uuid.toString());
                    removeStatement.setString(2, entry.getKey());
                    removeStatement.executeUpdate();
                    dataSource.close(remove, removeStatement, null);

                    Connection replace = dataSource.getConnection();
                    PreparedStatement replaceStatement = replace.prepareStatement(
                            "REPLACE INTO kitmanager_players VALUES (?,?,?)"
                    );
                    replaceStatement.setString(1, uuid.toString());
                    replaceStatement.setString(2, entry.getKey());
                    replaceStatement.setLong(3, entry.getValue());
                    replaceStatement.executeUpdate();
                    dataSource.close(replace, replaceStatement, null);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        });
    }

    public void savePlayerSync(UUID uuid){
        try {
            CorePlayer corePlayer = KitManager.getPlayerManager().getCorePlayer(uuid);
            HashMap<String, Long> cooldowns = corePlayer.getCooldowns();
            for (Map.Entry<String, Long> entry : cooldowns.entrySet()){
                Connection remove = dataSource.getConnection();
                PreparedStatement removeStatement = remove.prepareStatement(
                        "DELETE FROM kitmanager_players WHERE uuid=? AND kit=?"
                );
                removeStatement.setString(1, uuid.toString());
                removeStatement.setString(2, entry.getKey());
                removeStatement.executeUpdate();
                dataSource.close(remove, removeStatement, null);

                Connection replace = dataSource.getConnection();
                PreparedStatement replaceStatement = replace.prepareStatement(
                        "REPLACE INTO kitmanager_players VALUES (?,?,?)"
                );
                replaceStatement.setString(1, uuid.toString());
                replaceStatement.setString(2, entry.getKey());
                replaceStatement.setLong(3, entry.getValue());
                replaceStatement.executeUpdate();
                dataSource.close(replace, replaceStatement, null);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void setupTables(){
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS kitmanager_players (" +
                            "uuid char(36) NOT NULL," +
                            "kit varchar(255) NOT NULL," +
                            "time bigint(13) NOT NULL); "
            );
            preparedStatement.executeUpdate();
            dataSource.close(connection, preparedStatement, null);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
