package me.cppmonkey.monkeymod.commands;

import java.util.Iterator;
import java.util.List;
import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.player.PlayerDetails;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MonkeyCommand implements CommandExecutor {

    public final static String command = "monkey";
    private MonkeyMod m_plugin;

    public MonkeyCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            // Priority to /monkey update!
            if (args.length > 0) {
                if ("update".equalsIgnoreCase(args[0])) {
                    // Let the user know what's going on
                    m_plugin.selfUpdate(sender);
                    return true;
                } // END /monkey update

                // process /monkey version
                if ("version".equalsIgnoreCase(args[0])) {
                    sender.sendMessage(ChatColor.RED + m_plugin.getName() + " " + m_plugin.getVersion() + "(" + m_plugin.getBuild() + ")");
                    return true;
                } // END /monkey version

                if ("status".equalsIgnoreCase(args[0])) {
                    // process status commands
                    String status[] = m_plugin.getStatus();

                    for (int i = 0; i < status.length; i++) {
                        sender.sendMessage(ChatColor.RED + status[i]);
                    }
                    sender.sendMessage(ChatColor.RED + "Action still to do!");
                    return true;
                } // END /monkey status

                if ("setvar".equalsIgnoreCase(args[0])) {
                    // process setvar commands
                    String[] stringOptions = { "plugin.update.url", "logger.url" };

                    if (args.length == 3) {
                        for (int i = 0; i < stringOptions.length; i++) {
                            // Option found
                            if (stringOptions[i].equalsIgnoreCase(args[1])) {
                                m_plugin.getConfig().set(stringOptions[i], args[2]);
                                sender.sendMessage(ChatColor.GREEN + stringOptions[i] + " has been altered");
                                sender.sendMessage(ChatColor.GREEN + "A restart maybe required to apply changes");
                                return true;
                            }
                        }
                    }

                    String[] intOptions = { "protection.tower.threshold" };

                    if (args.length == 3) {
                        for (int i = 0; i < intOptions.length; i++) {
                            // Option found
                            if (intOptions[i].equalsIgnoreCase(args[1])) {
                                m_plugin.getConfig().set(intOptions[i], Integer.parseInt(args[2]));
                                sender.sendMessage(ChatColor.GREEN + intOptions[i] + " has been altered");
                                sender.sendMessage(ChatColor.GREEN + "A restart maybe required to apply changes");
                                return true;
                            }
                        }
                    }
                    sender.sendMessage(ChatColor.RED + "Action still to do!");
                    return true;
                } // END /monkey setvar

                if ("enable".equalsIgnoreCase(args[0]) || "disable".equalsIgnoreCase(args[0])) {
                    // List all all the boolean options
                    String[] boolOptions = { "server.protection.enabled", "plugin.update.auto", "plugin.silent", // TODO
                                                                                                                 // Make
                                                                                                                 // plugin
                                                                                                                 // silent
                                                                                                                 // by
                                                                                                                 // default
                        "protection.grief",
                        "protection.fire",
                        "protection.tower.strict",
                        "protection.tower.enable",
                        "logger.enable",
                        "logger.connect",
                        "logger.disconnect",
                        "logger.chat",
                        "override.nag",
                        "boxy.enabled",
                        "boxy.verbose",
                        "override.nag" };

                    // Correct number of arguments?
                    if (args.length == 2) {
                        for (int i = 0; i < boolOptions.length; i++) {
                            // Option found
                            if (boolOptions[i].equalsIgnoreCase(args[1])) {
                                m_plugin.getConfig().set(boolOptions[i], "enable".equalsIgnoreCase(args[0]));
                                sender.sendMessage(ChatColor.GREEN + boolOptions[i] + " has been altered");
                                sender.sendMessage(ChatColor.GREEN + "A restart maybe required to apply changes");
                                return true;
                            }
                        }
                    }

                    // return false, the command wasn't found
                    return false;
                } // END /monkey [enable/disable]
                if (args.length == 3) {
                    // Must be admin to add users
                    if (sender instanceof Player && !m_plugin.getPlayerDetails((Player) sender).isAdmin()) {
                        sender.sendMessage("You do not have permission to do that");
                        return true;
                    }

                    /*
                     * Was just a quick implementation to get it working
                     */
                    if ("add".equalsIgnoreCase(args[0]) || "remove".equalsIgnoreCase(args[0]) || "rm".equalsIgnoreCase(args[0])) {

                        Player grantToPlayer = m_plugin.getServer().getPlayer(m_plugin.getPlayer(args[2]));
                        if (grantToPlayer == null) {
                            sender.sendMessage("Unable to find player called " + args[2]);
                            return true;
                        } else if ("user".equalsIgnoreCase(args[1])) {
                            /*
                             * m_plugin.getPlayerDetails(grantToPlayer).setIsUser ("add".equalsIgnoreCase(args[0]));
                             */// TODO sort out adding users
                        } else if ("vip".equalsIgnoreCase(args[1])) {
                            m_plugin.getPlayerDetails(grantToPlayer).setIsVip("add".equalsIgnoreCase(args[0]));
                        } else if ("admin".equalsIgnoreCase(args[1])) {
                            m_plugin.getPlayerDetails(grantToPlayer).setIsAdmin("add".equalsIgnoreCase(args[0]));
                            grantToPlayer.sendMessage("isAdmin = " + m_plugin.getPlayerDetails(grantToPlayer).isAdmin());
                        }

                        return true;

                    } else {
                        // Command not found;
                        return false;
                    }
                } /* END args == 4 */else if ("user".equalsIgnoreCase(args[0]) && args.length == 2) {
                    // FIXME Update to new variables
                    Player player = m_plugin.getServer().getPlayer(m_plugin.getPlayer(args[1]));
                    if (player != null) {
                        PlayerDetails playerDetails = m_plugin.getPlayerDetails(player);
                        sender.sendMessage(player.getName() + ".canBuild: " + playerDetails.canBuild());
                        sender.sendMessage(player.getName() + ".canIgnite: " + playerDetails.canIgnite());
                        sender.sendMessage(player.getName() + ".isAdmin: " + playerDetails.isAdmin());
                        sender.sendMessage(player.getName() + ".isVip: " + playerDetails.isVip());
                        sender.sendMessage(player.getName() + ".UID: " + playerDetails.playerUID());
                    } else {
                        sender.sendMessage("User is offline");
                    }
                    return true;
                } else if ("world".equalsIgnoreCase(args[0])) {
                    List<World> worlds = m_plugin.getServer().getWorlds();

                    Iterator<World> world_iterator = worlds.iterator();

                    while (world_iterator.hasNext()) {
                        World world = world_iterator.next();
                        sender.sendMessage(world.getName() + " : " + world.getSeed());
                    }

                    return true;
                }

            }// END args > 0
        } catch (Exception ex) {
            MonkeyMod.reportException("Exception within MonkeyCommand.java", ex);
        }

        sender.sendMessage("No matching commands found");
        return false;
    }
}
