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

/**
 * 
 * @author Alex
 */
public class InventoryCommand implements CommandExecutor {

    public final static String command = "inventory";
    private MonkeyMod m_plugin;

    public InventoryCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !m_plugin.getPermition((Player) sender, ".isAdmin")) {
            sender.sendMessage(ChatColor.RED + "Admin access only!");
        } else {
            Player player = m_plugin.getServer().getOfflinePlayer(args[0]);
            
            if (player != null) {
                int numOfItems = player.getInventory().getContents().length;
                for (int itemIndex = 0; itemIndex < numOfItems; itemIndex++) {
                    // TODO Does this getInventory().toString() even work?
                    sender.sendMessage(ChatColor.GOLD + onPlayers[playerIndex].getInventory().toString());
                }
            } else {
                sender.sendMessage(ChatColor.GOLD + "Player not found.");
            }
        }

        return true;
    }
}
