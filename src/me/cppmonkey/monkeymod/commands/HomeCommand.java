/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.commands;

import java.util.Locale;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * 
 * @author CppMonkey
 */
public class HomeCommand implements CommandExecutor {
    public final static String command = "home";

    private MonkeyMod m_plugin;

    public HomeCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] cmndargs) {
        if (cmndargs.length > 0 && cs instanceof Player) {

            Player player = (Player) cs;

            // Permission check.
            // TODO Comments :-/
            if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use teleport commands");
            } else if (cmndargs.length == 2 && cmndargs[0].equalsIgnoreCase("set") && cmndargs[1].matches("[1-5]")) {
                String path = player.getName().toLowerCase(Locale.ENGLISH) + "." + player.getWorld().getName() + "." + cmndargs[1];
                m_plugin.getConfig().set(path, player.getLocation().toVector());
                player.sendMessage(ChatColor.GREEN + "Saved");
            } else if (cmndargs.length == 1 && cmndargs[0].matches("[1-5]")) {
                String path = player.getName().toLowerCase(Locale.ENGLISH) + "." + player.getWorld().getName() + "." + cmndargs[0];
                Vector Pos = m_plugin.getConfig().getVector(path);
                if (Pos == null) {
                    player.sendMessage(ChatColor.RED + "Input error. Are you sure you have saved a home warp here?");
                } else {
                    player.teleport(Pos.toLocation(player.getWorld()));
                }
            } else {
                return false;
            }
            return true;
        }
        return false;
    }
}
