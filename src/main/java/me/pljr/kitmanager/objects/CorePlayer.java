package me.pljr.kitmanager.objects;

import java.util.HashMap;

public class CorePlayer {
    private final HashMap<String, Long> cooldowns;

    public CorePlayer(HashMap<String, Long> cooldowns){
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
