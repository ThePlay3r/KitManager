package me.pljr.kitmanager;

import me.pljr.kitmanager.commands.AKitCommand;
import me.pljr.kitmanager.commands.KitCommand;
import me.pljr.kitmanager.config.Lang;
import me.pljr.kitmanager.listeners.AsyncPlayerPreLoginListener;
import me.pljr.kitmanager.listeners.PlayerQuitListener;
import me.pljr.kitmanager.managers.CoreKitManager;
import me.pljr.kitmanager.managers.PlayerManager;
import me.pljr.kitmanager.managers.QueryManager;
import me.pljr.pljrapispigot.database.DataSource;
import me.pljr.pljrapispigot.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class KitManager extends JavaPlugin {
    private static KitManager instance;
    private static ConfigManager configManager;
    private static ConfigManager kitConfigManager;
    private static CoreKitManager coreKitManager;
    private static PlayerManager playerManager;
    private static QueryManager queryManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        setupConfig();
        setupManagers();
        setupDatabase();
        setupListeners();
        setupCommands();
    }

    private void setupConfig(){
        saveDefaultConfig();
        configManager = new ConfigManager(this, "config.yml");
        kitConfigManager = new ConfigManager(this, "kits.yml");
        Lang.load(configManager);
    }

    private void setupManagers(){
        playerManager = new PlayerManager();
        coreKitManager = new CoreKitManager(kitConfigManager);
    }

    private void setupDatabase(){
        DataSource dataSource = DataSource.getFromConfig(configManager);
        queryManager = new QueryManager(dataSource, this);
        queryManager.setupTables();
        for (Player player : Bukkit.getOnlinePlayers()){
            queryManager.loadPlayer(player.getUniqueId());
        }
    }

    private void setupListeners(){
        getServer().getPluginManager().registerEvents(new AsyncPlayerPreLoginListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
    }

    private void setupCommands(){
        new KitCommand().registerCommand(this);
        new AKitCommand().registerCommand(this);
    }

    public static KitManager getInstance() {
        return instance;
    }
    public static PlayerManager getPlayerManager() {
        return playerManager;
    }
    public static QueryManager getQueryManager() {
        return queryManager;
    }
    public static CoreKitManager getCoreKitManager() {
        return coreKitManager;
    }
    public static ConfigManager getKitConfigManager() {
        return kitConfigManager;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player player : Bukkit.getOnlinePlayers()){
            queryManager.savePlayerSync(player.getUniqueId());
        }
    }
}
