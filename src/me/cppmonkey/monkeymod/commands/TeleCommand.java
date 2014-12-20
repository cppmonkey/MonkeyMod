/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.commands;

import java.util.Locale;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.GameMode;

/**
 *
 * @author Alex
 */
public class TeleCommand implements CommandExecutor {

    public final static String command = "tele";
    private MonkeyMod m_plugin;
    private final Configuration m_settings;

    public TeleCommand(MonkeyMod instance) {
        m_plugin = instance;
        m_settings = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.TELE);
    }

    private boolean home(CommandSender sender, String[] args) {
        if (args.length > 0) {
            // is the sender a player?
            if (sender instanceof Player) {

                Player player = (Player) sender;

                // Permission check.
                if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use teleport commands");
                    return true;
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("set")) {
                        if (args[1].matches("[1-5]")) {
                            m_settings.setProperty(player.getName().toLowerCase(Locale.ENGLISH) + "-" + args[1], player.getWorld().getName() + ":" + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ());
                            player.sendMessage(ChatColor.GREEN + "Saved");
                            return true;
                        }
                    }
                } else if (args.length == 1) {
                    if (args[0].matches("[1-5]")) {
                        try {
                            String Pos = m_settings.getString(player.getName().toLowerCase(Locale.ENGLISH) + "-" + args[0], "empty");
                            if (Pos.equals("empty")) {
                                player.sendMessage(ChatColor.RED + "Input error. Are you sure you have saved a home warp here?");
                                return false;
                            } else {
                                String Details[] = Pos.split(":");
                                if (Details[0].equalsIgnoreCase(player.getWorld().getName())) {
                                    // csv xyz = [1]
                                    String Coords[] = Details[1].split(",");
                                    double X = Double.parseDouble(Coords[0]);
                                    double Y = Double.parseDouble(Coords[1]);
                                    double Z = Double.parseDouble(Coords[2]);
                                    Location newLocation = new Location(player.getWorld(), X, Y, Z);
                                    player.setCompassTarget(newLocation);
                                    return true;
                                }
                            }
                        } catch (Exception e) {
                            player.sendMessage(ChatColor.RED + "error occoured: " + e.getMessage());
                            return false;
                        }
                        player.sendMessage(ChatColor.RED + "You cannot teleport from world to world. Please go to the appropriate world first");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean spawn(CommandSender sender) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use teleport commands");
                return true;
            } else {
                player.teleport(m_plugin.getServer().getWorld(player.getWorld().getName()).getSpawnLocation());
                return true;
            }
        }
        return false;
    }

    private boolean back(CommandSender sender) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
             if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use teleport commands");
                return true;
            } else {
                try {
                    String Pos = m_settings.getString(player.getName().toLowerCase(Locale.ENGLISH) + "-LAST", "empty");
                    if (Pos.equals("empty")) {
                        player.sendMessage(ChatColor.RED + "Input error. Last tele location not found?");
                        return false;
                    } else {
                        String Details[] = Pos.split(":");
                        if (Details[0].equalsIgnoreCase(player.getWorld().getName())) {
                            // csv xyz = [1]
                            String Coords[] = Details[1].split(",");
                            double X = Double.parseDouble(Coords[0]);
                            double Y = Double.parseDouble(Coords[1]);
                            double Z = Double.parseDouble(Coords[2]);
                            Location newLocation = new Location(player.getWorld(), X, Y, Z);
                            player.setCompassTarget(newLocation);
                            return true;
                        }
                    }
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "error occoured: " + e.getMessage());
                    return false;
                }
                player.sendMessage(ChatColor.RED + "You cannot teleport from world to world. Please go to the appropriate world first");
                return true;
            }
        }
        return false;
    }

    private boolean tele(CommandSender sender, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use teleport commands");
                return true;
            } else {
                if (args.length > 0) {
                    Player onPlayers[] = m_plugin.getServer().getOnlinePlayers();
                    int playerNum = -1;
                    for (int i = 0; i < onPlayers.length; i++) {
                        player.sendMessage(ChatColor.GREEN + "player: " + onPlayers[i]);
                        if (onPlayers[i].getName().contains(args[0])) {
                            playerNum = i;
                        }
                    }
                    if (playerNum != -1) {
                        if (player.getWorld().getName().equals(onPlayers[playerNum].getWorld().getName())) {
                            player.teleport(onPlayers[playerNum].getLocation());
                            m_settings.setProperty(player.getName().toLowerCase(Locale.ENGLISH) + "-LAST", player.getWorld().getName() + ":" + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ());
                        } else {
                            player.sendMessage(ChatColor.GOLD + "Target player is in another world. Please go to the right world first.");
                        }
                    } else {
                        player.sendMessage(ChatColor.GOLD + "Player not found.");
                    }
                return true; 
                }
            }
        }
        return false;
    }
    private boolean compass(CommandSender sender) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            Material itemMaterial = Material.matchMaterial("345");
            short durability = (short) 0;
            ItemStack item = new ItemStack(itemMaterial, 1, durability);
            player.getInventory().addItem(item);
            return true;
        }
        return false;
    }
    private boolean mode(CommandSender sender) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use mode commands");
                return true;
            }else{
                if(player.getGameMode() == GameMode.CREATIVE){
                    player.setGameMode(GameMode.SURVIVAL);
                    return true;
                }else{
                    player.setGameMode(GameMode.CREATIVE);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("home")) {
            return home(sender, args);
        } else if (command.getName().equals("spawn")) {
            return spawn(sender);
        } else if (command.getName().equals("tele")) {
            return tele(sender, args);
        } else if (command.getName().equals("back")) {
            return back(sender);
        } else if (command.getName().equals("compass")){
            return compass(sender);
        } else if (command.getName().equals("mode")){
            return mode(sender);
        }
        return false;
    }
}