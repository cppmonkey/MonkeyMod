package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModeCommand implements CommandExecutor {

    private MonkeyMod m_plugin;
    public final static String command = "mode";

    public ModeCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (args.length == 1) {
            // For console use and administrators
            if (sender instanceof Player && !m_plugin.getPlayerDetails((Player) sender).isAdmin()) {
                // Doesn't have access to do this
                sender.sendMessage(ChatColor.RED + "You do not have permission to use " + ModeCommand.command + " commands");
                return true;
            }
            player = m_plugin.getServer().getPlayer(m_plugin.getPlayer(args[0]));
        } else if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (player != null) {
            if (player.getGameMode() == GameMode.CREATIVE) {
                    player.setGameMode(GameMode.SURVIVAL);
            } else {
                    player.setGameMode(GameMode.CREATIVE);
                }
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Unable to find player");
            return true;
        }
    }
}
