package me.cppmonkey.monkeymod.commands;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.Parm;
import me.cppmonkey.monkeymod.callback.CSelfUpdateCallback;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;

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
        // Priority to /monkey update!
        if (args.length > 0) {
            if ("update".equalsIgnoreCase(args[0])) {
                // Let the user know what's going on
                m_plugin.selfUpdate(sender);
                return true;
            } // END /monkey update

            // force update check
            if ("uptodate".equalsIgnoreCase(args[0])) {
                // Setting up parms for http update query
                Parm[] parms = {
                    new Parm("name", m_plugin.getName()),
                    new Parm("version", m_plugin.getVersion()),
                    new Parm("build", m_plugin.getBuild())
                };

                // Create http request thread
                HttpRequestThread updateQuery = new HttpRequestThread("uptodate", sender, "http://cppmonkey.net/monkeymod/ajax.php", parms, new CSelfUpdateCallback(m_plugin, sender));
                // Start the thread
                updateQuery.start();

                return true;
            } // END /monkey uptodate

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
                String[] stringOptions = {
                        "plugin.update.url", "logger.url"
                };

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

                String[] intOptions = {
                    "protection.tower.threshold"
                };

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
                String[] boolOptions = {
                    "server.protection.enabled",
                    "plugin.update.auto",
                    "plugin.silent", // TODO Make plugin silent by default
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
                    "override.nag"
                };

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
                if (sender instanceof Player && !m_plugin.getPermition((Player) sender,".isAdmin")) {
                        sender.sendMessage("You do not have permission to do that");
                    return true;
                }

                /*
                 * Was just a quick implementation to get it working
                 */
                if ("add".equalsIgnoreCase(args[0])) {

                    Player player = m_plugin.getServer().getPlayer(args[2]);

                    if( player == null ) {
                        sender.sendMessage("Unable to find player called "+ args[2]);
                        return true;
                    }

                    Parm admin = new Parm("admin", sender.getName());


                    Parm permission;

                    if ("user".equalsIgnoreCase(args[1])) {
                        if (!m_plugin.canBuild.containsKey(player)) {
                            m_plugin.canBuild.put(player, true);
                        }
                        permission = new Parm("add", "user");
                        sender.sendMessage("User player '" + player.getName() + "' added");
                    } else if ("vip".equalsIgnoreCase(args[1])) {
                        m_plugin.isVip.put(player, true);
                        m_plugin.canBuild.put(player, true);
                        permission = new Parm("add", "vip");
                        sender.sendMessage("Vip player '" + player.getName() + "' added");
                    } else if ("admin".equalsIgnoreCase(args[1])) {
                        m_plugin.isAdmin.put(player, true);
                        m_plugin.canBuild.put(player, true);
                        permission = new Parm("add", "admin");
                        sender.sendMessage("Admin player '" + player.getName() + "' added");
                    } else {
                        return false;
                    }

                    Parm[] parms = {
                        new Parm("action", "modify"),
                        new Parm("player", player.getName()),
                        admin,
                        permission
                    };

                    HttpRequestThread notification = new HttpRequestThread("Permission Notification Thread:" + player.getName(), sender, m_plugin.getLoggerUrl(), parms);
                    notification.setPriority(Thread.MIN_PRIORITY);
                    notification.start();
                    return true;

                }/* END add */else if ("remove".equalsIgnoreCase(args[0]) || "rm".equalsIgnoreCase(args[0])) {
                    // Username
                    String playerName = args[2].toLowerCase(Locale.ENGLISH);
                    Parm permission;

                    Player player = m_plugin.getServer().getPlayer(playerName);

                    if( player == null ) {
                        sender.sendMessage("Unable to find player called "+ args[2]);
                        return true;
                    }

                    if ("user".equalsIgnoreCase(args[1])) {
                        if (player != null && m_plugin.canBuild.containsKey(player)) {
                            m_plugin.canBuild.remove(player);
                        }
                        permission = new Parm("remove", "user");
                        sender.sendMessage("player '" + playerName + "' removed");
                    } else if ("vip".equalsIgnoreCase(args[1])) {
                        if (player != null && m_plugin.isVip.containsKey(player)) {
                            m_plugin.isVip.remove(player);
                        }
                        permission = new Parm("remove", "vip");
                        sender.sendMessage("Vip player '" + playerName + "' removed");
                    } else if ("admin".equalsIgnoreCase(args[1])) {
                        if (player != null && m_plugin.isAdmin.containsKey(player)) {
                            m_plugin.isAdmin.remove(player);
                        }
                        permission = new Parm("remove", "admin");
                        sender.sendMessage("Admin player '" + playerName + "' removed");
                    } else {
                        return false;
                    }

                    Parm[] parms = {
                        new Parm("action", "modify"),
                        new Parm("player", playerName),
                        permission
                    };

                    HttpRequestThread notification = new HttpRequestThread("Permission Notification Thread:" + playerName, sender, m_plugin.getLoggerUrl(), parms
                    /* , new LoginCallback(m_plugin,player) */);
                    notification.setPriority(Thread.MIN_PRIORITY);
                    notification.start();

                } else {
                    // Command not found;
                    return false;
                }
                return true;
            } /* END args == 4 */else if ("user".equalsIgnoreCase(args[0]) && args.length == 2) {
                // FIXME Update to new variables
                Player player = m_plugin.getServer().getPlayer(args[1]);
                if (player != null) {
                    sender.sendMessage(player.getName() + ".canBuild: " + m_plugin.getPermition(player,".canBuild"));
                    sender.sendMessage(player.getName() + ".canIgnite: " + m_plugin.getPermition(player,".canIgnite"));
                    sender.sendMessage(player.getName() + ".isAdmin: " + m_plugin.getPermition(player,".isAdmin"));
                    sender.sendMessage(player.getName() + ".isVip: " + m_plugin.getPermition(player,".isVip"));
                    sender.sendMessage(player.getName() + ".UID: " + m_plugin.playerUIDs.get(player));
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

        sender.sendMessage("No matching commands found");
        return false;
    }
}
