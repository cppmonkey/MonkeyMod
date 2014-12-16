package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

// @author Alex
public class ChestCommand implements CommandExecutor {

    private MonkeyMod m_plugin;
    private Configuration m_chestPermissions;

    public ChestCommand(MonkeyMod instance) {
        m_plugin = instance;
        m_chestPermissions = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.CHESTS);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            // Must be a player to use these commands
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (m_plugin.getPermition(player, ".isVip") || m_plugin.getPermition(player, ".isAdmin")) {
                    if (args.length == 1) {
                        String arguments = args[0];
                        if (arguments.matches("UNLOCK") || arguments.matches("unlock")) {
                            m_chestPermissions.setProperty(player.getName().toLowerCase() + ".key", "UNLOCK");
                            m_chestPermissions.save();
                            player.sendMessage(ChatColor.GREEN + "Your skellington key is set to UNLOCK");
                        return true;
                        } else if (arguments.matches("LOCK") || arguments.matches("lock")) {
                            m_chestPermissions.setProperty(player.getName().toLowerCase() + ".key", "LOCK");
                            m_chestPermissions.save();
                            player.sendMessage(ChatColor.GREEN + "Your skellington key is set to LOCK");
                            return true;
                }
            }
                } else {
                    player.sendMessage(ChatColor.RED + "Advanced chest functions are for VIPs only!");
        return true;
    }
            }
        return false;
    }
                return true;
    }
}
