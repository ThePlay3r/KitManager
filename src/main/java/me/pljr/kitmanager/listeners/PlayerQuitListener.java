package me.pljr.kitmanager.listeners;

import me.pljr.kitmanager.KitManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        KitManager.getPlayerManager().savePlayer(event.getPlayer().getUniqueId());
    }
}
