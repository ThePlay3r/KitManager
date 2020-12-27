package me.pljr.kitmanager.config;

import me.pljr.pljrapispigot.managers.ConfigManager;

import java.util.HashMap;
import java.util.List;

public enum Lang {
    NO_CONSOLE,
    KIT_SUCCESS,
    KIT_FAILURE_NO_KIT,
    KIT_FAILURE_COOLDOWN,
    KITS_SUCCESS_TITLE,
    KITS_SUCCESS_FORMAT_AVAILABLE,
    KITS_SUCCESS_FORMAT_UNAVAILABLE,
    AKIT_GIVE_SUCCESS,
    AKIT_GIVE_SUCCESS_NOTIFY,
    AKIT_CREATE_SUCCESS,
    AKIT_REMOVE_SUCCESS;
    public static List<String> HELP;
    public static List<String> ADMIN_HELP;

    private static HashMap<Lang, String> lang;

    public static void load(ConfigManager config){
        HELP = config.getStringList("help");
        ADMIN_HELP = config.getStringList("admin-help");

        lang = new HashMap<>();
        for (Lang lang : values()){
            Lang.lang.put(lang, config.getString("lang."+lang));
        }
    }

    public String get(){
        return lang.get(this);
    }
}
