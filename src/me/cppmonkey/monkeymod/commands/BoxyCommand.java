package me.cppmonkey.monkeymod.commands;

import java.util.Locale;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.player.PlayerDetails;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BoxyCommand implements CommandExecutor {
    public final static String command = "boxy";

    private final MonkeyMod m_plugin;


    public BoxyCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

    private void setDefaultSettings(String PlayerName) {
        //set default settings on error occurring
        m_plugin.getConfig().set(PlayerName.toLowerCase(Locale.ENGLISH) + ".fromId", 0);
        m_plugin.getConfig().set(PlayerName.toLowerCase(Locale.ENGLISH) + ".toId", 0);
        m_plugin.getConfig().set(PlayerName.toLowerCase(Locale.ENGLISH) + ".step", 1);
        m_plugin.getConfig().set(PlayerName.toLowerCase(Locale.ENGLISH) + ".height", 1);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            // Must be a player to use these commands
            if (sender instanceof Player) {
                Player player = (Player) sender;
				PlayerDetails playerDetails = m_plugin.getPlayerDetails(player);

                if (playerDetails.isVip() || playerDetails.isAdmin()) {
                    if (args.length == 4) {
                        try {
                            m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".fromId", Integer.parseInt(args[0]));
                            m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".toId", Integer.parseInt(args[1]));
                            m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".step", Integer.parseInt(args[3]));
                            m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".height", Integer.parseInt(args[2]));
                            if (m_plugin.getConfig().getInt(player.getName().toLowerCase(Locale.ENGLISH) + ".step", -1) < 0) {
                                sender.sendMessage(ChatColor.RED + "Invalid argument value");
                                setDefaultSettings(player.getName());
                            }
                        player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                        return true;
                        } catch (NumberFormatException ex) {
                    // Report an error if text entered is invalid
                    sender.sendMessage(ChatColor.RED + "Invalid argument value");
                    sender.sendMessage(ex.getMessage());
                    setDefaultSettings(player.getName());
                    return false;
                    }
                    } else {
                        try {
                        String boxyDetails[] = args[0].split(":");
                            if (boxyDetails[0].equals("h")) {
                                m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".height", Integer.parseInt(boxyDetails[1]));
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                                return true;
                        }
                            if (boxyDetails[0].equals("s")) {
                                m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".step", Integer.parseInt(boxyDetails[1]));
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                                return true;
                        }
                            if (boxyDetails[0].equals("e")) {
                                m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".exclude", boxyDetails[1]);
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
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
                                if (m_plugin.getConfig().getBoolean(player.getName().toLowerCase(Locale.ENGLISH) + ".enabled", false)) {
                                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".enabled", false);
                                    player.sendMessage(ChatColor.GREEN + "Boxy disabled");
                                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasStart", false);
                                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasEnd", false);
                                    return true;
                                } else {
                                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".enabled", true);
                                    player.sendMessage(ChatColor.GREEN + "Boxy enabled");
                                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasStart", false);
                                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasEnd", false);
                                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".exclude", "7");
                                    return true;
                        }
                    }
                        } catch (NumberFormatException ex) {
                    // Report an error if text entered is invalid
                    sender.sendMessage(ChatColor.RED + "Invalid argument value");
                    sender.sendMessage(ex.getMessage());
                    setDefaultSettings(player.getName());
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
