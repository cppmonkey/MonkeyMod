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
public class TeleCommand implements CommandExecutor {

    private MonkeyMod m_plugin;
    private final Configuration m_settings;

    public TeleCommand(MonkeyMod instance) {
        m_plugin = instance;
        m_settings = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.TELE);
    }

    private boolean home(CommandSender sender, String[] args) {
        if (args.length > 0) {
            // is the sender a player?
            if ((sender instanceof Player)) {

                Player player = (Player) sender;

                // Permission check.
                if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use teleport commands");
                    return true;
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("set")) {
                        if (args[1].matches("[1-5]")) {
                            m_settings.setProperty((player.getName().toLowerCase() + "-" + args[1].toString()), (player.getWorld().getName().toString() + ":" + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ()));
                            m_settings.save();
                            return true;
                        }
                    }
                } else if (args.length == 1) {
                    if (args[0].matches("[1-5]")) {
                        try {
                            String Pos = m_settings.getString((player.getName().toLowerCase() + "-" + args[0].toString()), "empty");
                            player.sendMessage(ChatColor.GREEN + "POS " + Pos);
                            if (Pos.equals("empty")) {
                                player.sendMessage(ChatColor.RED + "Input error. Are you sure you have saved a home warp here?");
                                return false;
                            } else {
                                String Details[] = Pos.split(":");
                                if (Details[0].equalsIgnoreCase(player.getWorld().getName().toString())) {
                                    // csv xyz = [1]
                                    player.sendMessage(ChatColor.GREEN + "teleporting");
                                    String Coords[] = Details[1].split(",");
                                    double X = Double.parseDouble(Coords[0]);
                                    double Y = Double.parseDouble(Coords[1]);
                                    double Z = Double.parseDouble(Coords[2]);
                                    Location newLocation = new Location(player.getWorld(), X, Y, Z);
                                    player.teleport(newLocation);
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
                player.teleport(m_plugin.getServer().getWorld(player.getWorld().getName().toString()).getSpawnLocation());
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
                if(args.length > 0){
                    Player onPlayers[] = m_plugin.getServer().getOnlinePlayers();
                    int playerNum = -1;
                    for( int i = 0; i < onPlayers.length;i++)
                    {
                        if(onPlayers[i].getName().toString().equals(args[0])){
                            playerNum =i;
                        }
                    }
                    if(playerNum != -1)
                    {
                            player.teleport(onPlayers[playerNum].getLocation());
                    }
                    player.teleport(m_plugin.getServer().getWorld(player.getWorld().getName().toString()).getSpawnLocation());
                return true; 
                }
            }
        }
        return false;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().toString().equals("home")){
            return home(sender,args);
        } else if(command.getName().toString().equals("spawn")){
            return spawn(sender);
        } else if(command.getName().toString().equals("tele")){
            return tele(sender,args);
        }
        return false;
    }
}