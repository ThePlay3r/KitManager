package me.pljr.kitmanager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.pljr.kitmanager.config.Lang;
import me.pljr.kitmanager.managers.CoreKitManager;
import me.pljr.kitmanager.managers.PlayerManager;
import me.pljr.kitmanager.objects.KitMPlayer;
import me.pljr.pljrapispigot.utils.FormatUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion {

    private final JavaPlugin plugin;
    private final CoreKitManager coreKitManager;
    private final PlayerManager playerManager;

    public PapiExpansion(JavaPlugin plugin, CoreKitManager coreKitManager, PlayerManager playerManager){
        this.plugin = plugin;
        this.coreKitManager = coreKitManager;
        this.playerManager = playerManager;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "kitmanager";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] identifierName = params.split(":");
        if (identifierName.length != 2) return "Bad syntax!";
        String identifier = identifierName[0];
        String kitName = identifierName[1];

        if (!coreKitManager.getKitNames().contains(kitName)) return "Unknown kit!";
        KitMPlayer kitMPlayer = playerManager.getPlayer(player.getUniqueId());

        switch (identifier.toUpperCase()){
            case "COOLDOWN": return FormatUtil.formatTime((kitMPlayer.getCooldown(kitName) - System.currentTimeMillis()) / 1000);
            case "CANCOOLDOWN": {
                if (kitMPlayer.getCooldown(kitName) > System.currentTimeMillis()) return Lang.PAPI_CANCOOLDOWN_NO.get();
                return Lang.PAPI_CANCOOLDOWN_YES.get();
            }
            case "CANPERM": {
                if (player.isOnline()){
                    if (player.getPlayer().hasPermission("kitmanager.kit."+kitName)) return Lang.PAPI_CANPERM_YES.get();
                    return Lang.PAPI_CANPERM_NO.get();
                }
                return Lang.PAPI_CANPERM_OFFLINE.get();
            }
        }

        return "ERROR";
    }
}
