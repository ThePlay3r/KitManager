package me.pljr.kitmanager.commands;

import me.pljr.kitmanager.KitManager;
import me.pljr.kitmanager.config.Lang;
import me.pljr.kitmanager.objects.CoreKit;
import me.pljr.pljrapispigot.utils.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class AKitCommand extends CommandUtil {

    public AKitCommand(){
        super("akit", "kitmanager.admin.use");
    }

    @Override
    public void onPlayerCommand(Player player, String[] args){
        if (args.length == 1){
            // /akit help
            if (args[0].equalsIgnoreCase("help")){
                if (!checkPerm(player, "kitmanager.admin.help")) return;
                sendMessage(player, Lang.ADMIN_HELP);
                return;
            }

            // /akit <kit>
            if (!checkPerm(player, "kitmanager.admin.kit")) return;
            if (!KitManager.getCoreKitManager().getKitNames().contains(args[0])){
                sendMessage(player, Lang.KIT_FAILURE_NO_KIT.get().replace("{kit}", args[0]));
                return;
            }
            CoreKit coreKit = KitManager.getCoreKitManager().get(args[0]);
            KitManager.getCoreKitManager().give(coreKit, player);
            sendMessage(player, Lang.KIT_SUCCESS.get().replace("{kit}", args[0]));
            return;
        }

        else if (args.length == 2){
            // /akit remove <name>
            if (args[0].equalsIgnoreCase("remove")){
                if (!checkPerm(player, "kitmanager.admin.remove")) return;
                if (!KitManager.getCoreKitManager().getKitNames().contains(args[1])){
                    sendMessage(player, Lang.KIT_FAILURE_NO_KIT.get().replace("{kit}", args[0]));
                    return;
                }
                KitManager.getCoreKitManager().remove(args[1]);
                sendMessage(player, Lang.AKIT_REMOVE_SUCCESS.get().replace("{kit}", args[1]));
                return;
            }
        }

        else if (args.length == 3){
            // /akit give <player> <name>
            if (args[0].equalsIgnoreCase("give")){
                if (!checkPerm(player, "kitmanager.admin.give")) return;
                if (!checkPlayer(player, args[1])) return;
                if (!KitManager.getCoreKitManager().getKitNames().contains(args[2])){
                    sendMessage(player, Lang.KIT_FAILURE_NO_KIT.get().replace("{kit}", args[0]));
                    return;
                }
                Player requestedPlayer = Bukkit.getPlayer(args[1]);
                CoreKit coreKit = KitManager.getCoreKitManager().get(args[2]);
                KitManager.getCoreKitManager().give(coreKit, requestedPlayer);
                sendMessage(player, Lang.AKIT_GIVE_SUCCESS.get().replace("{player}", args[1]).replace("{kit}", args[2]));
                sendMessage(requestedPlayer, Lang.AKIT_GIVE_SUCCESS_NOTIFY.get().replace("{player}", player.getName()).replace("{kit}", args[2]));
                return;
            }

            // /akit create <name> <cooldown>
            if (args[0].equalsIgnoreCase("create")){
                if (!checkPerm(player, "kitmanager.admin.create")) return;
                if (!checkInt(player, args[2])) return;
                String kitName = args[1];
                int kitCooldown = Integer.parseInt(args[2]);
                List<ItemStack> items = new ArrayList<>();
                PlayerInventory pInv = player.getInventory();
                for (int i = 0; i < pInv.getSize(); i++){
                    items.add(pInv.getItem(i));
                }
                CoreKit coreKit = new CoreKit(kitName, kitCooldown, items);
                KitManager.getCoreKitManager().create(coreKit);
                sendMessage(player, Lang.AKIT_CREATE_SUCCESS.get().replace("{kit}", kitName).replace("{cooldown}", args[2]));
                return;
            }
        }

        if (checkPerm(player, "kitmanager.admin.help")) sendMessage(player, Lang.ADMIN_HELP);
    }

    @Override
    public void onConsoleCommand(ConsoleCommandSender sender, String[] args){
        if (args.length == 1){
            // /akit help
            if (args[0].equalsIgnoreCase("help")){
                sendMessage(sender, Lang.ADMIN_HELP);
                return;
            }
        }

        else if (args.length == 2){
            // /akit remove <name>
            if (args[0].equalsIgnoreCase("remove")){
                if (!KitManager.getCoreKitManager().getKitNames().contains(args[1])){
                    sendMessage(sender, Lang.KIT_FAILURE_NO_KIT.get().replace("{kit}", args[0]));
                    return;
                }
                KitManager.getCoreKitManager().remove(args[1]);
                sendMessage(sender, Lang.AKIT_REMOVE_SUCCESS.get().replace("{kit}", args[1]));
                return;
            }
        }

        else if (args.length == 3){
            // /akit give <player> <name>
            if (args[0].equalsIgnoreCase("give")){
                if (!checkPlayer(sender, args[1])) return;
                if (!KitManager.getCoreKitManager().getKitNames().contains(args[2])){
                    sendMessage(sender, Lang.KIT_FAILURE_NO_KIT.get().replace("{kit}", args[0]));
                    return;
                }
                Player requestedPlayer = Bukkit.getPlayer(args[1]);
                CoreKit coreKit = KitManager.getCoreKitManager().get(args[2]);
                KitManager.getCoreKitManager().give(coreKit, requestedPlayer);
                sendMessage(sender, Lang.AKIT_GIVE_SUCCESS.get().replace("{player}", args[1]).replace("{kit}", args[2]));
                sendMessage(requestedPlayer, Lang.AKIT_GIVE_SUCCESS_NOTIFY.get().replace("{player}", sender.getName()).replace("{kit}", args[2]));
                return;
            }
        }

        sendMessage(sender, Lang.ADMIN_HELP);
    }
}
