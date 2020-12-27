package me.pljr.kitmanager.managers;

import me.pljr.kitmanager.KitManager;
import me.pljr.kitmanager.objects.CoreKit;
import me.pljr.pljrapispigot.managers.ConfigManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoreKitManager {
    private final HashMap<String, CoreKit> kits;
    private final List<String> kitNames;
    private final ConfigManager manager;

    public CoreKitManager(ConfigManager manager){
        this.kits = new HashMap<>();
        this.kitNames = new ArrayList<>();
        this.manager = manager;

        ConfigurationSection cs = manager.getConfigurationSection("");
        for (String kit : cs.getKeys(false)){
            ConfigurationSection itemsCs = cs.getConfigurationSection(kit);
            List<ItemStack> items = new ArrayList<>();
            int cooldown = manager.getInt(kit+".cooldown");
            for (String item : itemsCs.getKeys(false)){
                if (item.equals("cooldown")) continue;
                items.add(manager.getItemStack(kit+"."+item));
            }
            kits.put(kit, new CoreKit(kit, cooldown, items));
            if (!kitNames.contains(kit)){
                kitNames.add(kit);
            }
        }
    }

    public void create(CoreKit coreKit){
        kits.put(coreKit.getName(), coreKit);
        kitNames.add(coreKit.getName());
        FileConfiguration config = manager.getConfig();
        int i = 1;
        for (ItemStack item : coreKit.getItems()){
            config.set(coreKit.getName()+"."+i, item);
            config.set(coreKit.getName()+".cooldown", coreKit.getCooldown());
            i++;
        }
        manager.save();
    }

    public void remove(String name){
        FileConfiguration config = manager.getConfig();
        config.set(name, null);
        manager.save();
        kits.remove(name);
        kitNames.remove(name);
    }

    public CoreKit get(String name){
        if (kits.containsKey(name)){
            return kits.get(name);
        }
        return null;
    }

    public void give(CoreKit coreKit, Player player){
        for (ItemStack item : coreKit.getItems()){
            if (item == null || item.getType() == Material.AIR) continue;
            if (player.getInventory().firstEmpty() == -1){
                player.getWorld().dropItem(player.getLocation(), item);
                continue;
            }
            player.getInventory().addItem(item);
            player.updateInventory();
        }
    }

    public List<String> getKitNames() {
        return kitNames;
    }
}
