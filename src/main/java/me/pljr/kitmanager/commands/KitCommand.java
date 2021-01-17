package me.pljr.kitmanager.commands;

import me.pljr.kitmanager.KitManager;
import me.pljr.kitmanager.config.Lang;
import me.pljr.kitmanager.objects.CoreKit;
import me.pljr.kitmanager.objects.CorePlayer;
import me.pljr.pljrapispigot.utils.CommandUtil;
import me.pljr.pljrapispigot.utils.FormatUtil;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KitCommand extends CommandUtil {

    public KitCommand(){
        super("kit", "kitmanager.use");
    }

    @Override
    public void onPlayerCommand(Player player, String[] args){
        UUID playerId = player.getUniqueId();

        if (args.length == 1){
            // /kit help
            if (args[0].equalsIgnoreCase("help")){
                if (!checkPerm(player, "kitmanager.help")) return;
                sendMessage(player, Lang.HELP.get());
                return;
            }

            // /kit list
            if (args[0].equalsIgnoreCase("list")){
                if (!checkPerm(player, "kitmanager.list")) return;
                sendMessage(player, Lang.KITS_SUCCESS_TITLE.get());
                long currentTime = System.currentTimeMillis();
                for (String kitName : KitManager.getCoreKitManager().getKitNames()){
                    if (!player.hasPermission("kitmanager.kit."+kitName)) continue;
                    if (KitManager.getPlayerManager().getCorePlayer(playerId).getCooldown(kitName) > currentTime){
                        sendMessage(player, Lang.KITS_SUCCESS_FORMAT_UNAVAILABLE.get().replace("{name}", kitName));
                    }else{
                        sendMessage(player, Lang.KITS_SUCCESS_FORMAT_AVAILABLE.get().replace("{name}", kitName));
                    }
                }
                return;
            }

            // /kit <kit>
            if (!checkPerm(player, "kitmanager.kit." + args[0])) return;
            if (!KitManager.getCoreKitManager().getKitNames().contains(args[0])){
                sendMessage(player, Lang.KIT_FAILURE_NO_KIT.get().replace("{kit}", args[0]));
                return;
            }
            CorePlayer corePlayer = KitManager.getPlayerManager().getCorePlayer(playerId);
            long currentTime = System.currentTimeMillis();
            long cooldown = corePlayer.getCooldown(args[0]);
            if (cooldown > currentTime){
                sendMessage(player, Lang.KIT_FAILURE_COOLDOWN.get().replace("{time}", FormatUtil.formatTime((cooldown-currentTime)/1000)));
                return;
            }
            CoreKit coreKit = KitManager.getCoreKitManager().get(args[0]);
            KitManager.getCoreKitManager().give(coreKit, player);
            corePlayer.setCooldown(args[0], currentTime+coreKit.getCooldown()*1000);
            KitManager.getPlayerManager().setCorePlayer(playerId, corePlayer);
            sendMessage(player, Lang.KIT_SUCCESS.get().replace("{kit}", args[0]));
            return;
        }

        if (checkPerm(player, "kitmanager.help")) sendMessage(player, Lang.HELP.get());
    }
}
