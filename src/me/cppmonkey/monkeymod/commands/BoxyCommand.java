package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.block.Block;
import org.bukkit.World;
import org.bukkit.Location;

public class BoxyCommand implements CommandExecutor {

	private final MonkeyMod m_plugin;
    private final Configuration m_settings;
	
    public BoxyCommand(MonkeyMod instance) {
		m_plugin = instance;
        m_settings = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.BOXY);
	}

    private int setDefaultSettings(String PlayerName) {
        //set default settings on error occurring
        m_settings.setProperty(PlayerName.toLowerCase() + ".fromId", 0);
        m_settings.setProperty(PlayerName.toLowerCase() + ".toId", 0);
        m_settings.setProperty(PlayerName.toLowerCase() + ".step", 1);
        m_settings.setProperty(PlayerName.toLowerCase() + ".height", 1);
        return 0;
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            // Must be a player to use these commands
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (m_plugin.getPermition(player, ".isVip") || m_plugin.getPermition(player, ".isAdmin")) {
                    if (args.length == 4) {
                        try {
                            m_settings.setProperty(player.getName().toLowerCase() + ".fromId", Integer.parseInt(args[0]));
                            m_settings.setProperty(player.getName().toLowerCase() + ".toId", Integer.parseInt(args[1]));
                            m_settings.setProperty(player.getName().toLowerCase() + ".step", Integer.parseInt(args[3]));
                            m_settings.setProperty(player.getName().toLowerCase() + ".height", Integer.parseInt(args[2]));
                            if (m_settings.getInt(player.getName().toLowerCase() + ".step", -1) < 0) {
                                sender.sendMessage(ChatColor.RED + "Invalid argument value");
                                setDefaultSettings(player.getName());
                                m_settings.save();
                            }
                        player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                        m_settings.save();
                        return true;
                        } catch (NumberFormatException ex) {
                    // Report an error if text entered is invalid
                    sender.sendMessage(ChatColor.RED + "Invalid argument value");
                    sender.sendMessage(ex.getMessage());
                    setDefaultSettings(player.getName());
                    m_settings.save();
                    return false;
                    }
                    } else {
                        try {
                        String boxyDetails[] = args[0].split(":");
                            if (boxyDetails[0].equals("h")) {
                                m_settings.setProperty(player.getName().toLowerCase() + ".height", Integer.parseInt(boxyDetails[1]));
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                            m_settings.save();
                                return true;
                        }
                            if (boxyDetails[0].equals("s")) {
                                m_settings.setProperty(player.getName().toLowerCase() + ".step", Integer.parseInt(boxyDetails[1]));
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                            m_settings.save();
                                return true;
                        }
                            if (boxyDetails[0].equals("e")) {
                                m_settings.setProperty(player.getName().toLowerCase() + ".exclude", boxyDetails[1]);
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                            m_settings.save();
                                return true;
                            }
                            if (boxyDetails[0].equals("help")) {
                                player.sendMessage(ChatColor.GREEN + "Boxy help:");
                                player.sendMessage(ChatColor.GREEN + "/boxy [from id/name] [to id/name] [height offset] [step]");
                                player.sendMessage(ChatColor.GREEN + "/boxy h:[height offset");
                                player.sendMessage(ChatColor.GREEN + "/boxy s:[stepping]");
                                player.sendMessage(ChatColor.GREEN + "/boxy e:[exclude block id (seperate many with commas)]");
                                player.sendMessage(ChatColor.GREEN + "/boxy (this enables / disables boxy)");
                                return true;
                            }
                            if (boxyDetails[0].equals("enable")) {
                                if (m_settings.getBoolean(player.getName().toLowerCase() + ".enabled", false)) {
                                    m_settings.setProperty(player.getName().toLowerCase() + ".enabled", false);
                                    player.sendMessage(ChatColor.GREEN + "Boxy disabled");
                                    m_settings.setProperty(player.getName().toLowerCase() + ".hasStart", false);
                                    m_settings.setProperty(player.getName().toLowerCase() + ".hasEnd", false);
                                    m_settings.save();
                                    return true;
                                } else {
                                    m_settings.setProperty(player.getName().toLowerCase() + ".enabled", true);
                                    player.sendMessage(ChatColor.GREEN + "Boxy enabled");
                                    m_settings.setProperty(player.getName().toLowerCase() + ".hasStart", false);
                                    m_settings.setProperty(player.getName().toLowerCase() + ".hasEnd", false);
                                    m_settings.setProperty(player.getName().toLowerCase() + ".exclude", "7");
                                    m_settings.save();
                                    return true;
                        }
                    }
                        } catch (NumberFormatException ex) {
                    // Report an error if text entered is invalid
                    sender.sendMessage(ChatColor.RED + "Invalid argument value");
                    sender.sendMessage(ex.getMessage());
                    setDefaultSettings(player.getName());
                    m_settings.save();
                    return false;
                    }
        }
                    player.sendMessage(ChatColor.RED + "Invalid Boxy command! Type Help for usage!");
                    return false;

                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use Boxy");
                    return true;
                }
            } else {
        //TODO Undo capabilities for Console?
		sender.sendMessage(ChatColor.RED + "Not implimented yet");
                return false;
            }
        }
		return false;
	}
}
