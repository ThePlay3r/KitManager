package me.pljr.kitmanager;

import me.pljr.kitmanager.commands.AKitCommand;
import me.pljr.kitmanager.commands.KitCommand;
import me.pljr.kitmanager.config.Lang;
import me.pljr.kitmanager.listeners.AsyncPlayerPreLoginListener;
import me.pljr.kitmanager.listeners.PlayerQuitListener;
import me.pljr.kitmanager.managers.CoreKitManager;
import me.pljr.kitmanager.managers.PlayerManager;
import me.pljr.kitmanager.managers.QueryManager;
import me.pljr.pljrapispigot.PLJRApiSpigot;
import me.pljr.pljrapispigot.database.DataSource;
import me.pljr.pljrapispigot.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class KitManager extends JavaPlugin {

    public static Logger log;

    private PLJRApiSpigot pljrApiSpigot;

    private ConfigManager configManager;
    private ConfigManager kitConfigManager;

    private CoreKitManager coreKitManager;
    private PlayerManager playerManager;
    private QueryManager queryManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        log = getLogger();
        if (!setupPLJRApi()) return;
        setupConfig();
        setupDatabase();
        setupManagers();
        setupListeners();
        setupCommands();
        setupPapi();
    }

    public boolean setupPLJRApi(){
        if (PLJRApiSpigot.get() == null){
            getLogger().warning("PLJRApi-Spigot is not enabled!");
            return false;
        }
        pljrApiSpigot = PLJRApiSpigot.get();
        return true;
    }

    private void setupConfig(){
        saveDefaultConfig();
        configManager = new ConfigManager(this, "config.yml");
        kitConfigManager = new ConfigManager(this, "kits.yml");
        Lang.load(new ConfigManager(this, "lang.yml"));
    }

    private void setupDatabase(){
        DataSource dataSource = pljrApiSpigot.getDataSource(configManager);
        queryManager = new QueryManager(dataSource, this);
        queryManager.setupTables();
        for (Player player : Bukkit.getOnlinePlayers()){
            queryManager.loadPlayer(player.getUniqueId());
        }
    }

    private void setupManagers(){
        playerManager = new PlayerManager(this, queryManager, true);
        playerManager.initAutoSave();
        coreKitManager = new CoreKitManager(kitConfigManager);
    }

    private void setupListeners(){
        getServer().getPluginManager().registerEvents(new AsyncPlayerPreLoginListener(playerManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(playerManager), this);
    }

    private void setupCommands(){
        new KitCommand(playerManager, coreKitManager).registerCommand(this);
        new AKitCommand(coreKitManager).registerCommand(this);
    }

    private void setupPapi(){
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new PapiExpansion(this, coreKitManager, playerManager).register();
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> playerManager.getPlayer(player.getUniqueId(), queryManager::savePlayer));
    }
}
