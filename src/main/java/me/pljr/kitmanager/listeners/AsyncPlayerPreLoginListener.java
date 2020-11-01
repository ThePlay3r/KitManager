package me.pljr.kitmanager.listeners;

import me.pljr.kitmanager.KitManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class AsyncPlayerPreLoginListener implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event){
        KitManager.getQueryManager().loadPlayerSync(event.getUniqueId());
    }
}
