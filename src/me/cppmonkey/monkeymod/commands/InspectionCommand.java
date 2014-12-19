/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

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
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if ("list".equalsIgnoreCase(command.getName())) {
                Player onPlayers[] = m_plugin.getServer().getOnlinePlayers();
                player.sendMessage(ChatColor.GREEN + "players: ");
                for (int i = 0; i < onPlayers.length; i++) {
                    player.sendMessage(ChatColor.GREEN + onPlayers[i].getDisplayName() + ",");
                }
                return true;
            } else if ("inventory".equalsIgnoreCase(command.getName())) {
                switch (args.length) {
                    case 1: {
                if (!m_plugin.getPermition(player, ".isAdmin")) {
                    player.sendMessage(ChatColor.RED + "Admin access only!");
                    return true;
                } else {
                    Player onPlayers[] = m_plugin.getServer().getOnlinePlayers();
                    int playerNum = -1;
                    for (int i = 0; i < onPlayers.length; i++) {
                        if (onPlayers[i].getName().toString().contains(args[0])) {
                            playerNum = i;
                        }
                    }
                    if (playerNum != -1) {
                                ItemStack item[] = onPlayers[playerNum].getInventory().getContents();
                                for (int i = 0; i < 40; i++) {
                                    try {
                                        if (item[i] != null) {
                                            player.sendMessage(ChatColor.GOLD + "item slot " + (i + 1) + ": " + item[i].getType().toString() + " X " + (item[i].getAmount()));
                                        }
                                    } catch (Exception e) {
                                        //no item found
                                    }
                        }
                    } else {
                        player.sendMessage(ChatColor.GOLD + "Player not found.");
                    }
                    return true;   
                        }
                    }
                    case 2: {
                        if (args[1].toLowerCase().equalsIgnoreCase("risk")) {
                            String dangerItems = "";
                            Player onPlayers[] = m_plugin.getServer().getOnlinePlayers();
                            int playerNum = -1;
                            for (int i = 0; i < onPlayers.length; i++) {
                                if (onPlayers[i].getName().toString().contains(args[0])) {
                                    playerNum = i;
                                }
                            }
                            if (playerNum == -1) {
                                ItemStack item[] = onPlayers[playerNum].getInventory().getContents();
                                for (int i = 0; i < 40; i++) {
                                    try {
                                        if (item[i] != null) {
                                            player.sendMessage(ChatColor.GOLD + "item slot " + (i + 1) + ": " + item[i].getType().toString() + " X " + (item[i].getAmount()));
                                        }
                                    } catch (Exception e) {
                                        //no item found
                                    }
                                }
                                player.sendMessage(ChatColor.GOLD + "Player not found.");
                                return true;
                            } else {
                                ItemStack item[] = onPlayers[playerNum].getInventory().getContents();
                                for (int i = 0; i < 40; i++) {
                                    try {
                                        if (item[i] != null) {
                                            switch (item[i].getTypeId()) {
                                                case 7:
                                                    dangerItems += "BEDROCK,";
                                                    break;
                                                case 8:
                                                    dangerItems += "WATER,";
                                                    break;
                                                case 9:
                                                    dangerItems += "WATER,";
                                                    break;
                                                case 10:
                                                    dangerItems += "LAVA,";
                                                    break;
                                                case 11:
                                                    dangerItems += "LAVA,";
                                                    break;
                                                case 30:
                                                    dangerItems += "WEB,";
                                                    break;
                                                case 46:
                                                    dangerItems += "TNT,";
                                                    break;
                                                case 49:
                                                    dangerItems += "OBSCIDAIN,";
                                                    break;
                                                case 51:
                                                    dangerItems += "FIRE,";
                                                    break;
                                                case 79:
                                                    dangerItems += "ICE,";
                                                    break;
                                                case 90:
                                                    dangerItems += "PORTAL,";
                                                    break;
                                                case 259:
                                                    dangerItems += "FLINT+STEEL,";
                                                    break;
                                                case 326:
                                                    dangerItems += "WATER BUCKET,";
                                                    break;
                                                case 327:
                                                    dangerItems += "LAVA BUCKET,";
                                                    break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        //no item found
                                    }
                                }
                                if (dangerItems.length() > 1) {
                                    player.sendMessage(ChatColor.GOLD + "Player is carrying dangerous items!!!");
                                    player.sendMessage(ChatColor.GOLD + dangerItems);
                                    return true;
                                } else {
                                    player.sendMessage(ChatColor.GOLD + "Player is not carrying anything dangerous");
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
