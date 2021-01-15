package me.pljr.kitmanager.config;

import me.pljr.pljrapispigot.managers.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;

public enum Lang {
    HELP("" +
            "\n§a§lKitManager Help" +
            "\n" +
            "\n§e/kit help §8» §fDisplays this message." +
            "\n§e/kit <name> §8» §fGives the desired kit." +
            "\n§e/kit list §8» §fShows all kits, that you can get." +
            "\n"),

    ADMIN_HELP("" +
            "\n§a§lKitManager Admin-Help" +
            "\n" +
            "\n§e/akit help §8» §fDisplays this message." +
            "\n§e/akit <name> §8» §fGives desired kit, ignores permission and cooldown." +
            "\n§e/akit give <player> <name> §8» §fGives desired kit to player, ignoring his permissions and cooldowns." +
            "\n§e/akit create <name> <cooldown> §8» §fCreates kit from items in you inventory." +
            "\n§e/akit remove <name> §8» §fRemoves desired kit." +
            "\n"),

    KIT_SUCCESS("§aKitManager §8» §fYou have received kit §b{kit}§f."),
    KIT_FAILURE_NO_KIT("§aKitManager §8» §b{kit} §fcould not be found."),
    KIT_FAILURE_COOLDOWN("§aKitManager §8» §fYou must wait %time §fto get this kit!"),
    KITS_SUCCESS_TITLE("§aKitManager §8» §fYour kits:"),
    KITS_SUCCESS_FORMAT_AVAILABLE("§7- §f{name} <hover:show_text:'§eClick to get'><click:run_command:/kit {name}>§e#</click></hover> <hover:show_text:'§cClick to remove'><click:run_command:/akit remove {name}>§c✕</click></hover>"),
    KITS_SUCCESS_FORMAT_UNAVAILABLE("§7- §7{name} <hover:show_text:'§cClick to remove'><click:run_command:/akit remove {name}>§c✕</click></hover>"),
    AKIT_GIVE_SUCCESS("§aKitManager §8» §fYou have successfully given §b{player} §fkit §b{kit}§f."),
    AKIT_GIVE_SUCCESS_NOTIFY("§aKitManager §8» §fYou have received kit §b{kit} §ffrom §b{player}§f."),
    AKIT_CREATE_SUCCESS("§aKitManager §8» §fSuccessfully created kit §b{kit} §fwith cooldown §b{cooldown}§f."),
    AKIT_REMOVE_SUCCESS("§aKitManager §8» §fSuccessfully removed kit §b{kit}§f.");

    private static HashMap<Lang, String> lang;
    private final String defaultValue;

    Lang(String defaultValue){
        this.defaultValue = defaultValue;
    }

    public static void load(ConfigManager config){
        lang = new HashMap<>();
        FileConfiguration fileConfig = config.getConfig();
        for (Lang lang : values()){
            if (!fileConfig.isSet(lang.toString())){
                fileConfig.set(lang.toString(), lang.getDefault());
            }
            Lang.lang.put(lang, config.getString(lang.toString()));
        }
    }

    public String get(){
        return lang.get(this);
    }

    public String getDefault(){
        return this.defaultValue;
    }
}
