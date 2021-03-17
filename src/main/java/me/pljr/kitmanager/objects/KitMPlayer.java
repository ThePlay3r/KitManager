package me.pljr.kitmanager.objects;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

public class KitMPlayer {
    @Getter private final UUID uniqueId;
    private final HashMap<String, Long> cooldowns;

    public KitMPlayer(UUID uniqueId){
        this.uniqueId = uniqueId;
        this.cooldowns = new HashMap<>();
    }

    public KitMPlayer(UUID uniqueId, HashMap<String, Long> cooldowns){
        this.uniqueId = uniqueId;
        this.cooldowns = cooldowns;
    }

    public HashMap<String, Long> getCooldowns() {
        return cooldowns;
    }

    public long getCooldown(String name){
        if (cooldowns.containsKey(name)){
            return cooldowns.get(name);
        }
        return 0;
    }

    public void setCooldown(String name, long cooldown){
        cooldowns.put(name, cooldown);
    }
}
