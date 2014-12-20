package me.cppmonkey.monkeymod.commands;

import java.util.Locale;
import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

// @author Alex
public class ChestCommand implements CommandExecutor {

    public final static String command = "chest";
    private MonkeyMod m_plugin;
    private Configuration m_chestPermissions;

    public ChestCommand(MonkeyMod instance) {
        m_plugin = instance;
        m_chestPermissions = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.CHESTS);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.setGameMode(GameMode.CREATIVE);
                if (args.length > 0) {
                // Must be a player to use these commands

                if (m_plugin.getPermition(player, ".isVip") || m_plugin.getPermition(player, ".isAdmin")) {
                    if (args.length == 1) {
                        if ("UNLOCK".equalsIgnoreCase(args[0])) {
                            m_chestPermissions.setProperty(player.getName().toLowerCase(Locale.ENGLISH) + ".key", "UNLOCK");
                            player.sendMessage(ChatColor.GREEN + "Your skellington key is set to UNLOCK");
                            return true;
                        } else if ("LOCK".equalsIgnoreCase(args[0])) {
                            m_chestPermissions.setProperty(player.getName().toLowerCase(Locale.ENGLISH) + ".key", "LOCK");
                            player.sendMessage(ChatColor.GREEN + "Your skellington key is set to LOCK");
                            return true;
                        }
                    } // END length 1
                } else {
                    player.sendMessage(ChatColor.RED + "Advanced chest functions are for VIPs only!");
                    return true;
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
