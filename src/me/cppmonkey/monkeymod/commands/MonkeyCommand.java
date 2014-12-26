package me.cppmonkey.monkeymod.commands;

import java.util.Iterator;
import java.util.List;
import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.callback.CSelfUpdateCallback;
import me.cppmonkey.monkeymod.player.PlayerDetails;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.utils.Parm;

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
        try{
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
                HttpRequestThread updateQuery = new HttpRequestThread("uptodate", "http://cppmonkey.net/monkeymod/ajax.php", parms, new CSelfUpdateCallback(m_plugin, sender));
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
                if (sender instanceof Player && !m_plugin.getPlayerDetails((Player) sender).isAdmin()) {
                    sender.sendMessage("You do not have permission to do that");
                    return true;
                }

                /*
                 * Was just a quick implementation to get it working
                 */
                    if ("add".equalsIgnoreCase(args[0]) || "remove".equalsIgnoreCase(args[0]) || "rm".equalsIgnoreCase(args[0])) {

                        Player grantToPlayer = m_plugin.getServer().getPlayer(args[2]);
                        if( grantToPlayer == null ) {
                            sender.sendMessage("Unable to find player called "+ args[2]);
                            return true;
                        }

                        PlayerDetails playerDetails = m_plugin.getPlayerDetails(grantToPlayer);

                        Parm player_id = new Parm("player_id", sender instanceof Player ? playerDetails.getPlayerUID():-1);
                        Boolean grant = "add".equalsIgnoreCase(args[0]);


                        Parm permission = new Parm("add".equalsIgnoreCase(args[2])?"add":"remove","user");
                    if ("user".equalsIgnoreCase(args[1])) {
                        if (!playerDetails.canBuild()) {
                             playerDetails.setCanBuild(grant);
                        }
                        sender.sendMessage("User player '" + grantToPlayer.getName() + "' added");
                    } else if ("vip".equalsIgnoreCase(args[1])) {
                        playerDetails.setIsVip(grant);
                        playerDetails.setCanBuild(grant);
                        permission.setValue("vip");
                        sender.sendMessage("Vip player '" + grantToPlayer.getName() + "' added");
                    } else if ("admin".equalsIgnoreCase(args[1])) {
                        playerDetails.setIsAdmin(grant);
                        playerDetails.setCanBuild(grant);
                        permission.setValue("admin");
                        sender.sendMessage("Admin player '" + grantToPlayer.getName() + "' added");
                    } else {
                        return false;
                    }

                    Parm[] parms = {
                        new Parm("action", "modify"),
                        new Parm("grant_to_id", playerDetails.getPlayerUID()), // This is the Granted By ID
                        new Parm("server_uid", m_plugin.getServerUID()),
                        player_id,
                        permission
                    };

                    HttpRequestThread notification = new HttpRequestThread("Permission Notification Thread:" + grantToPlayer.getName(), m_plugin.getLoggerUrl(), parms);
                    notification.setPriority(Thread.MIN_PRIORITY);
                    notification.start();
                    return true;

                } else {
                    // Command not found;
                    return false;
                }
            } /* END args == 4 */else if ("user".equalsIgnoreCase(args[0]) && args.length == 2) {
                // FIXME Update to new variables
                Player player = m_plugin.getServer().getPlayer(args[1]);
                if (player != null) {
                    PlayerDetails playerDetails = m_plugin.getPlayerDetails(player);
                    sender.sendMessage(player.getName() + ".canBuild: " + playerDetails.canBuild());
                    sender.sendMessage(player.getName() + ".canIgnite: " + playerDetails.canIgnite());
                    sender.sendMessage(player.getName() + ".isAdmin: " + playerDetails.isAdmin());
                    sender.sendMessage(player.getName() + ".isVip: " + playerDetails.isVip());
                    sender.sendMessage(player.getName() + ".UID: " + playerDetails.getPlayerUID());
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
        }catch(Exception ex){
            MonkeyMod.reportException("Exception within MonkeyCommand.java", ex);
        }

        sender.sendMessage("No matching commands found");
        return false;
    }
}
