package me.pljr.kitmanager.listeners;

import lombok.AllArgsConstructor;
import me.pljr.kitmanager.KitManager;
import me.pljr.kitmanager.managers.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@AllArgsConstructor
public class AsyncPlayerPreLoginListener implements Listener {

    private final PlayerManager playerManager;

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event){
        playerManager.getPlayer(event.getUniqueId(), ignored -> KitManager.log.info("Loaded " + event.getName()));
    }
}
