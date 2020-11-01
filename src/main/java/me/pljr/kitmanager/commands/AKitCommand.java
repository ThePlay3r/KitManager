package me.pljr.kitmanager.commands;

import me.pljr.kitmanager.KitManager;
import me.pljr.kitmanager.config.CfgLang;
import me.pljr.kitmanager.enums.Lang;
import me.pljr.kitmanager.objects.CoreKit;
import me.pljr.pljrapi.utils.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class AKitCommand extends CommandUtil implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!checkPerm(sender, "kitmanager.admin.use")) return false;

        if (args.length == 1){
            // /akit help
            if (args[0].equalsIgnoreCase("help")){
                if (!checkPerm(sender, "kitmanager.admin.help")) return false;
                sendHelp(sender, CfgLang.adminHelp);
                return true;
            }

            // /akit <kit>
            if (!checkPerm(sender, "kitmanager.admin.kit")) return false;
            if (!(sender instanceof Player)){
                sendMessage(sender, CfgLang.lang.get(Lang.NO_CONSOLE));
                return false;
            }
            Player player = (Player) sender;
            if (!KitManager.getCoreKitManager().getKitNames().contains(args[0])){
                sendMessage(player, CfgLang.lang.get(Lang.KIT_FAILURE_NO_KIT).replace("%kit", args[0]));
                return false;
            }
            CoreKit coreKit = KitManager.getCoreKitManager().get(args[0]);
            KitManager.getCoreKitManager().give(coreKit, player);
            sendMessage(player, CfgLang.lang.get(Lang.KIT_SUCCESS).replace("%kit", args[0]));
            return true;
        }

        else if (args.length == 2){
            // /akit remove <name>
            if (args[0].equalsIgnoreCase("remove")){
                if (!checkPerm(sender, "kitmanager.admin.remove")) return false;
                if (!KitManager.getCoreKitManager().getKitNames().contains(args[1])){
                    sendMessage(sender, CfgLang.lang.get(Lang.KIT_FAILURE_NO_KIT).replace("%kit", args[0]));
                    return false;
                }
                KitManager.getCoreKitManager().remove(args[1]);
                sendMessage(sender, CfgLang.lang.get(Lang.AKIT_REMOVE_SUCCESS).replace("%kit", args[1]));
                return true;
            }
        }

        else if (args.length == 3){
            // /akit give <player> <name>
            if (args[0].equalsIgnoreCase("give")){
                if (!checkPerm(sender, "kitmanager.admin.give")) return false;
                if (!checkPlayer(sender, args[1])) return false;
                if (!KitManager.getCoreKitManager().getKitNames().contains(args[2])){
                    sendMessage(sender, CfgLang.lang.get(Lang.KIT_FAILURE_NO_KIT).replace("%kit", args[0]));
                    return false;
                }
                Player requestedPlayer = Bukkit.getPlayer(args[1]);
                CoreKit coreKit = KitManager.getCoreKitManager().get(args[2]);
                KitManager.getCoreKitManager().give(coreKit, requestedPlayer);
                sendMessage(sender, CfgLang.lang.get(Lang.AKIT_GIVE_SUCCESS).replace("%player", args[1]).replace("%kit", args[2]));
                sendMessage(requestedPlayer, CfgLang.lang.get(Lang.AKIT_GIVE_SUCCESS_NOTIFY).replace("%player", sender.getName()).replace("%kit", args[2]));
                return true;
            }

            // /akit create <name> <cooldown>
            if (args[0].equalsIgnoreCase("create")){
                if (!(sender instanceof Player)) {
                    sendMessage(sender, CfgLang.lang.get(Lang.NO_CONSOLE));
                    return false;
                }
                Player player = (Player) sender;
                if (!checkPerm(player, "kitmanager.admin.create")) return false;
                if (!checkInt(player, args[2])) return false;
                String kitName = args[1];
                int kitCooldown = Integer.parseInt(args[2]);
                List<ItemStack> items = new ArrayList<>();
                PlayerInventory pInv = player.getInventory();
                for (int i = 0; i < pInv.getSize(); i++){
                    items.add(pInv.getItem(i));
                }
                CoreKit coreKit = new CoreKit(kitName, kitCooldown, items);
                KitManager.getCoreKitManager().create(coreKit);
                sendMessage(player, CfgLang.lang.get(Lang.AKIT_CREATE_SUCCESS).replace("%kit", kitName).replace("%cooldown", args[2]));
                return true;
            }
        }

        if (checkPerm(sender, "kitmanager.admin.help")) sendHelp(sender, CfgLang.adminHelp);
        return false;
    }
}
