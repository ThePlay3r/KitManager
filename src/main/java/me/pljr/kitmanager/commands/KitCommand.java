package me.pljr.kitmanager.commands;

import me.pljr.kitmanager.KitManager;
import me.pljr.kitmanager.config.CfgLang;
import me.pljr.kitmanager.enums.Lang;
import me.pljr.kitmanager.objects.CoreKit;
import me.pljr.kitmanager.objects.CorePlayer;
import me.pljr.pljrapi.utils.ChatUtil;
import me.pljr.pljrapi.utils.CommandUtil;
import me.pljr.pljrapi.utils.FormatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KitCommand extends CommandUtil implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sendMessage(sender, CfgLang.lang.get(Lang.NO_CONSOLE));
            return false;
        }
        if (!checkPerm(sender, "kitmanager.use")) return false;
        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (args.length == 1){
            // /kit help
            if (args[0].equalsIgnoreCase("help")){
                if (!checkPerm(player, "kitmanager.help")) return false;
                sendHelp(player, CfgLang.help);
                return true;
            }

            // /kit list
            if (args[0].equalsIgnoreCase("list")){
                if (!checkPerm(player, "kitmanager.list")) return false;
                sendMessage(player, CfgLang.lang.get(Lang.KITS_SUCCESS_TITLE));
                long currentTime = System.currentTimeMillis();
                for (String kitName : KitManager.getCoreKitManager().getKitNames()){
                    if (!player.hasPermission("kitmanager.kit."+kitName)) continue;
                    if (KitManager.getPlayerManager().getCorePlayer(playerId).getCooldown(kitName) > currentTime){
                        sendMessage(player, CfgLang.lang.get(Lang.KITS_SUCCESS_FORMAT_UNAVAILABLE).replace("%name", kitName));
                    }else{
                        sendMessage(player, CfgLang.lang.get(Lang.KITS_SUCCESS_FORMAT_AVAILABLE).replace("%name", kitName));
                    }
                }
                return true;
            }

            // /kit <kit>
            if (!checkPerm(player, "kitmanager.kit." + args[0])) return false;
            if (!KitManager.getCoreKitManager().getKitNames().contains(args[0])){
                sendMessage(player, CfgLang.lang.get(Lang.KIT_FAILURE_NO_KIT).replace("%kit", args[0]));
                return false;
            }
            CorePlayer corePlayer = KitManager.getPlayerManager().getCorePlayer(playerId);
            long currentTime = System.currentTimeMillis();
            long cooldown = corePlayer.getCooldown(args[0]);
            if (cooldown > currentTime){
                sendMessage(player, CfgLang.lang.get(Lang.KIT_FAILURE_COOLDOWN).replace("%time", FormatUtil.formatTime((cooldown-currentTime)/1000)));
                return false;
            }
            CoreKit coreKit = KitManager.getCoreKitManager().get(args[0]);
            KitManager.getCoreKitManager().give(coreKit, player);
            corePlayer.setCooldown(args[0], currentTime+coreKit.getCooldown()*1000);
            KitManager.getPlayerManager().setCorePlayer(playerId, corePlayer);
            sendMessage(player, CfgLang.lang.get(Lang.KIT_SUCCESS).replace("%kit", args[0]));
            return true;
        }

        if (checkPerm(player, "kitmanager.help")) sendHelp(player, CfgLang.help);
        return false;
    }
}
