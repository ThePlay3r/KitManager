package me.pljr.kitmanager.managers;

import lombok.AllArgsConstructor;
import me.pljr.kitmanager.KitManager;
import me.pljr.kitmanager.objects.KitMPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public class PlayerManager {
    private final static int AUTOSAVE = 12000;

    private final HashMap<UUID, KitMPlayer> players = new HashMap<>();
    private final JavaPlugin plugin;
    private final QueryManager queryManager;
    private final boolean cachePlayers;

    public void getPlayer(UUID uuid, Consumer<KitMPlayer> consumer) {
        if (!players.containsKey(uuid)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                KitMPlayer player = queryManager.loadPlayer(uuid);
                setPlayer(uuid, player);
                consumer.accept(player);
            });
        } else {
            consumer.accept(players.get(uuid));
        }
    }

    public KitMPlayer getPlayer(UUID uuid){
        if (!players.containsKey(uuid)){
            KitMPlayer player = queryManager.loadPlayer(uuid);
            setPlayer(uuid, player);
            return player;
        }else{
            return players.get(uuid);
        }
    }

    public void setPlayer(UUID uuid, KitMPlayer player){
        players.put(uuid, player);
    }

    public void savePlayer(UUID uuid){
        if (!cachePlayers) players.remove(uuid);
        queryManager.savePlayer(players.get(uuid));
    }

    public void initAutoSave(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            KitManager.log.info("Saving players..");
            for (Map.Entry<UUID, KitMPlayer> entry : players.entrySet()){
                savePlayer(entry.getKey());
            }
            KitManager.log.info("All players were saved.");
        }, AUTOSAVE, AUTOSAVE);
    }
}
