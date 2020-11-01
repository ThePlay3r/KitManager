package me.pljr.kitmanager.managers;

import me.pljr.kitmanager.KitManager;
import me.pljr.kitmanager.files.KitsFile;
import me.pljr.kitmanager.objects.CoreKit;
import me.pljr.pljrapi.managers.ConfigManager;
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

    public CoreKitManager(){
        this.kits = new HashMap<>();
        this.kitNames = new ArrayList<>();
    }

    public void load(ConfigManager configManager){
        ConfigurationSection cs = configManager.getConfigurationSection("");
        for (String kit : cs.getKeys(false)){
            ConfigurationSection itemsCs = cs.getConfigurationSection(kit);
            List<ItemStack> items = new ArrayList<>();
            int cooldown = configManager.getInt(kit+".cooldown");
            for (String item : itemsCs.getKeys(false)){
                if (item.equals("cooldown")) continue;
                items.add(configManager.getItemStack(kit+"."+item));
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
        FileConfiguration file = KitsFile.getKitsFile();
        int i = 1;
        for (ItemStack item : coreKit.getItems()){
            file.set(coreKit.getName()+"."+i, item);
            file.set(coreKit.getName()+".cooldown", coreKit.getCooldown());
            i++;
        }
        KitsFile.saveKitsFile();
    }

    public void remove(String name){
        FileConfiguration file = KitsFile.getKitsFile();
        file.set(name, null);
        KitsFile.saveKitsFile();
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
