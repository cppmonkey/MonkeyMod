/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 *
 * @author Alex
 */
public class InspectionCommand implements CommandExecutor {

    private MonkeyMod m_plugin;
    private final Configuration m_settings;

    public InspectionCommand(MonkeyMod instance) {
        m_plugin = instance;
        m_settings = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.TELE);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().toString().equals("list")) {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                Player onPlayers[] = m_plugin.getServer().getOnlinePlayers();
                player.sendMessage(ChatColor.GREEN + "players: ");
                for (int i = 0; i < onPlayers.length; i++) {
                    player.sendMessage(ChatColor.GREEN + onPlayers[i].getDisplayName() + ",");
                }
            }
        } else if (command.getName().toString().equals("inventory")) {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;





                if (!m_plugin.getPermition(player, ".isAdmin")) {
                    player.sendMessage(ChatColor.RED + "Admin access only!");
                    return true;
                } else {
                    Player onPlayers[] = m_plugin.getServer().getOnlinePlayers();
                    int playerNum = -1;
                    for (int i = 0; i < onPlayers.length; i++) {
                        if (onPlayers[i].getName().toString().contains(args[0].toString())) {
                            playerNum = i;
                        }
                    }
                    if (playerNum != -1) {
                        int numOfItems = onPlayers[playerNum].getInventory().getContents().length;
                        for(int i = 0; i < numOfItems;i++){
                            player.sendMessage(ChatColor.GOLD + onPlayers[playerNum].getInventory().toString());
                        }
                    } else {
                        player.sendMessage(ChatColor.GOLD + "Player not found.");
                    }
                    return true;









                
                
                
                
                
                
                
                
            }
        }
        return false;
    }
}
