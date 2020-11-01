package me.pljr.kitmanager;

import me.pljr.kitmanager.commands.AKitCommand;
import me.pljr.kitmanager.commands.KitCommand;
import me.pljr.kitmanager.config.CfgLang;
import me.pljr.kitmanager.files.KitsFile;
import me.pljr.kitmanager.listeners.AsyncPlayerPreLoginListener;
import me.pljr.kitmanager.listeners.PlayerQuitListener;
import me.pljr.kitmanager.managers.CoreKitManager;
import me.pljr.kitmanager.managers.PlayerManager;
import me.pljr.kitmanager.managers.QueryManager;
import me.pljr.pljrapi.PLJRApi;
import me.pljr.pljrapi.database.DataSource;
import me.pljr.pljrapi.managers.ConfigManager;
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
        if (!setupPLJRApi()) return;
        instance = this;
        setupConfig();
        setupManagers();
        setupDatabase();
        setupListeners();
        setupCommands();
    }

    private boolean setupPLJRApi(){
        PLJRApi api = (PLJRApi) Bukkit.getServer().getPluginManager().getPlugin("PLJRApi");
        if (api == null){
            Bukkit.getConsoleSender().sendMessage("§cKitManager: PLJRApi not found, disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }else{
            Bukkit.getConsoleSender().sendMessage("§aKitManager: Hooked into PLJRApi!");
            return true;
        }
    }

    private void setupConfig(){
        saveDefaultConfig();
        configManager = new ConfigManager(getConfig(), "§cKitManager:", "config.yml");
        KitsFile.setupKitsFile(this);
        kitConfigManager = new ConfigManager(KitsFile.getKitsFile(), "§cKitManager:", "kits.yml");
        CfgLang.load(configManager);
    }

    private void setupManagers(){
        playerManager = new PlayerManager();
        coreKitManager = new CoreKitManager();
        coreKitManager.load(kitConfigManager);
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
        getCommand("kit").setExecutor(new KitCommand());
        getCommand("akit").setExecutor(new AKitCommand());
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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player player : Bukkit.getOnlinePlayers()){
            queryManager.savePlayerSync(player.getUniqueId());
        }
    }
}
