package me.pljr.kitmanager.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class KitsFile {
    private static File file;
    private static FileConfiguration customFile;

    public static void setupKitsFile(Plugin plugin){
        file = new File(plugin.getDataFolder(), "kits.yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                //
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getKitsFile(){
        return customFile;
    }

    public static void saveKitsFile(){
        try{
            customFile.save(file);
        }catch (IOException e){
            System.out.println("KitManager: Couldn't save Kits File!");
        }
    }

    public static void removeKitsFile(){
        if (file.delete()){
            System.out.println("KitManager: Kits File deleted.");
        }else{
            System.out.println("KitManager: Couldn't delete Kits File!");
        }
    }

    public static void reloadKitsFile(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }
}
