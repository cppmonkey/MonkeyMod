package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class BoxyCommand implements CommandExecutor {

	private final MonkeyMod m_plugin;
    @Deprecated
    private final Configuration m_settings;
	
    public BoxyCommand(MonkeyMod instance) {
		m_plugin = instance;
        m_settings = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.BOXY);
	}

    private int setDefaultSettings(String PlayerName){
        m_settings.setProperty(PlayerName.toLowerCase()+".fromId", 0);
        m_settings.setProperty(PlayerName.toLowerCase()+".toId", 0);
        m_settings.setProperty(PlayerName.toLowerCase()+".step", 1);
        m_settings.setProperty(PlayerName.toLowerCase()+".height", 1);
        return 0;
    }

    public boolean playerListenerEvent(Player player, int X, int Y, int Z){
        if(!(m_settings.getBoolean(player.getName().toLowerCase() + ".hasStart", false))){
            m_settings.setProperty(player.getName().toLowerCase()+".hasStart",true);
            m_settings.setProperty(player.getName().toLowerCase()+".startLocation",(X+","+Y+","+Z));
            player.sendMessage(ChatColor.GREEN + "Boxy start point confirmed");
            m_settings.save();
        }
        else{
            if(!(m_settings.getBoolean(player.getName().toLowerCase() + ".hasEnd", false))){
                m_settings.setProperty(player.getName().toLowerCase()+".hasEnd",true);
                m_settings.setProperty(player.getName().toLowerCase()+".endLocation",(X+","+Y+","+Z));
                player.sendMessage(ChatColor.GREEN + "Boxy end point confirmed");
                player.sendMessage(ChatColor.GOLD + "Caution! You are about to commit a Boxy alteration!");
                player.sendMessage(ChatColor.GOLD + "Converting block type " + m_settings.getProperty(player.getName().toLowerCase()+".fromId") + " to " + (m_settings.getProperty(player.getName().toLowerCase()+".toId")));
                player.sendMessage(ChatColor.GOLD + "Excludeing block types: " + m_settings.getProperty(player.getName().toLowerCase()+".exclude"));
                player.sendMessage(ChatColor.GOLD + "With a height of " + m_settings.getProperty(player.getName().toLowerCase()+".height") + " and a step of " + m_settings.getProperty(player.getName().toLowerCase()+".step"));
                player.sendMessage(ChatColor.GOLD + "Start point (X,Y,Z): " + m_settings.getProperty(player.getName().toLowerCase()+".startLocation"));
                player.sendMessage(ChatColor.GOLD + "End point (X,Y,Z): " + m_settings.getProperty(player.getName().toLowerCase()+".endLocation"));
                player.sendMessage(ChatColor.RED + "RIGHT CLICK TO COMMIT! right click elsewhere to cancel!");
                m_settings.save();
            }
            else{
                if(m_settings.getProperty(player.getName().toLowerCase()+".endLocation").toString().matches((X+","+Y+","+Z))){
                    player.sendMessage(ChatColor.RED + "BOXY COMMITTED! ARGH!");
                    m_settings.setProperty(player.getName().toLowerCase()+".hasStart",false);
                    m_settings.setProperty(player.getName().toLowerCase()+".hasEnd",false);
                }
                else
                {
                    m_settings.setProperty(player.getName().toLowerCase()+".hasStart",false);
                    m_settings.setProperty(player.getName().toLowerCase()+".hasEnd",false);
                    player.sendMessage(ChatColor.GREEN + "Boxy alteration aborted");
                }
            }
        }
        return true;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        /* TODO process commands
		 * 	/boxy [id/name] [id/name] [height] [step]
		 *  /boxy h:[height]
		 *  /boxy s:[stepping]
		 *  /boxy e:[exclude csv of id/name]
		 *  /boxy undo (even possible?) 
		 */
        if (args.length > 0){
        // Must be a player to use these commands
        if (sender instanceof Player){
            Player player = (Player)sender;

                if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use Boxy");
                    System.out.println(player.getName() + " isVip " + m_plugin.getPermition(player, ".isVip"));
                    System.out.println(player.getName() + " isAdmin " + m_plugin.getPermition(player, ".isAdmin"));
                    return true;
                    }
                if(args.length == 4)
                {
                    try{
                        m_settings.setProperty(player.getName().toLowerCase()+".fromId", Integer.parseInt(args[0]));
                        m_settings.setProperty(player.getName().toLowerCase()+".toId", Integer.parseInt(args[1]));
                        m_settings.setProperty(player.getName().toLowerCase()+".step", Integer.parseInt(args[3]));
                        m_settings.setProperty(player.getName().toLowerCase()+".height", Integer.parseInt(args[2]));
                        player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                        m_settings.save();
                        return true;
                    }
                    catch(NumberFormatException ex) {
                    // Report an error if text entered is invalid
                    sender.sendMessage(ChatColor.RED + "Invalid argument value");
                    sender.sendMessage(ex.getMessage());
                    setDefaultSettings(player.getName());
                    m_settings.save();
                    return false;
                    }
                }
                else{
                    try{
                        String boxyDetails[] = args[0].split(":");
                        if(boxyDetails[0].equals("h")){
                            m_settings.setProperty(player.getName().toLowerCase()+".height", Integer.parseInt(boxyDetails[1]));
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                            m_settings.save();
                        }
                        if(boxyDetails[0].equals("s")){
                            m_settings.setProperty(player.getName().toLowerCase()+".step", Integer.parseInt(boxyDetails[1]));
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                            m_settings.save();
                        }
                        if(boxyDetails[0].equals("e")){
                            /*String excludes[] = boxyDetails[1].split(",");
                            for(int i = 0;i < excludes.length;i++){*/
                            m_settings.setProperty(player.getName().toLowerCase()+".exclude", boxyDetails[1]);
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                            m_settings.save();
                        }
                    }
                    catch(NumberFormatException ex) {
                    // Report an error if text entered is invalid
                    sender.sendMessage(ChatColor.RED + "Invalid argument value");
                    sender.sendMessage(ex.getMessage());
                    setDefaultSettings(player.getName());
                    m_settings.save();
                    return false;
                    }
                    return true;
                }
        }

        //TODO Undo capabilities for Console?

		sender.sendMessage(ChatColor.RED + "Not implimented yet");
        }
		return false;
	}
}
