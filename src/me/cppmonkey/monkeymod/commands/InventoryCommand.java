/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.commands;

import java.util.HashMap;
import java.util.Map.Entry;

import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author Alex & CppMonkey
 */
public class InventoryCommand implements CommandExecutor {

    public final static String command = "inventory";
    private MonkeyMod m_plugin;

    public InventoryCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (args.length == 1) {
            // For console use and administrators
            if (sender instanceof Player && !m_plugin.getPlayerDetails((Player) sender).isAdmin()) {
                // Doesn't have access to do this
                sender.sendMessage(ChatColor.RED + "You do not have permission to use " + command + " commands");
                return true;
            }
            player = m_plugin.getServer().getPlayer(m_plugin.getPlayer(args[0]));
        } else {
            return false;
        }

        if (player != null) {

            HashMap<Material, Integer> playerItems = new HashMap<Material, Integer>();
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null) {
                    if (playerItems.containsKey(itemStack.getType())) {
                        playerItems.put(itemStack.getType(), itemStack.getAmount() + playerItems.get(itemStack.getType()));
                    } else {
                        playerItems.put(itemStack.getType(), itemStack.getAmount());
                    }
                }
            }

            for (Entry<Material, Integer> entry : playerItems.entrySet()) {
                sender.sendMessage(ChatColor.GOLD + entry.getKey().name() + " " + entry.getValue());
            }
        } else {
            sender.sendMessage(ChatColor.GOLD + "Player not found.");
        }

        return true;
    }
}
