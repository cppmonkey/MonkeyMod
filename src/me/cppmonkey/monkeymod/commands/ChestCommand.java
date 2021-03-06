package me.cppmonkey.monkeymod.commands;

import java.util.Locale;
import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.player.PlayerDetails;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// @author Alex
/*
 * version 1.01
 *  removed gamemode change
 *  made unlock available to non vips
 */
public class ChestCommand implements CommandExecutor {

    public final static String command = "chest";
    private MonkeyMod m_plugin;

    public ChestCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerDetails playerDetails = m_plugin.getPlayerDetails(player);
            if (args.length > 0) {
                // Must be a player to use these commands
                if (args.length == 1) {
                    if ((playerDetails.isVip() || playerDetails.isAdmin()) && "LOCK".equalsIgnoreCase(args[0])) {
                        m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".key", "LOCK");
                        player.sendMessage(ChatColor.GREEN + "Your skellington key is set to LOCK");
                        return true;
                    } else if ("UNLOCK".equalsIgnoreCase(args[0])) {
                        m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".key", "UNLOCK");
                        player.sendMessage(ChatColor.GREEN + "Your skellington key is set to UNLOCK");
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "Advanced chest functions are for VIPs only!");
                        return true;
                    }
                }
            } else {
                // No args supplied
                player.sendMessage(ChatColor.RED + "Chest Commands called without any args");
                return false;
            }
        }

        // Could be an entity... Creeper that destroyed it?
        MonkeyMod.log.warning("Console cant use Chest Commands");
        return false;
    }
}
