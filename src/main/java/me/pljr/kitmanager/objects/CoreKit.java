package me.pljr.kitmanager.objects;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CoreKit {
    private final String name;
    private final long cooldown;
    private final List<ItemStack> items;

    public CoreKit(String name, long cooldown, List<ItemStack> items){
        this.name = name;
        this.cooldown = cooldown;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public long getCooldown() {
        return cooldown;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
