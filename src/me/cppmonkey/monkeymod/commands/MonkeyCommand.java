package me.cppmonkey.monkeymod.commands;

import java.util.Iterator;
import java.util.List;
import me.cppmonkey.monkeymod.callback.CSelfUpdateCallback;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class MonkeyCommand implements CommandExecutor {

    private MonkeyMod m_plugin;
    private Configuration m_permissions;

    public MonkeyCommand(MonkeyMod instance) {
        m_plugin = instance;
        m_permissions = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PERMISSIONS);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Priority to /monkey update!
        if (args.length > 0) {
            if ("update".equalsIgnoreCase(args[0])) {
                //Let the user know what's going on
                m_plugin.selfUpdate(sender);
                return true;
            } //END /monkey update

            // force update check
            if ("uptodate".equalsIgnoreCase(args[0])) {
                // Setting up parms for http update query
                String[] parms = {
                    "name=" + m_plugin.getName(),
                    "version=" + m_plugin.getVersion(),
                    "build=" + m_plugin.getBuild()
                };

                // Create http request thread
                HttpRequestThread updateQuery = new HttpRequestThread("uptodate", sender, "http://cppmonkey.net/monkeymod/ajax.php", parms, m_plugin, new CSelfUpdateCallback(m_plugin,sender));
                // Start the thread
                updateQuery.start();

                return true;
            }  //END /monkey uptodate

            // process /monkey version
            if ("version".equalsIgnoreCase(args[0])) {
                sender.sendMessage(ChatColor.RED + m_plugin.getName() + " " + m_plugin.getVersion() + "(" + m_plugin.getBuild() + ")");
                return true;
            } //END /monkey version

            if ("status".equalsIgnoreCase(args[0])) {
                // process status commands
                String status[] = m_plugin.getStatus();

                for( int i = 0; i < status.length; i++){
                    sender.sendMessage(ChatColor.RED+status[i]);
                }
                sender.sendMessage(ChatColor.RED + "Action still to do!");
                return true;
            } //END /monkey status

            if ("setvar".equalsIgnoreCase(args[0])) {
                // process setvar commands
                String[] stringOptions = {
                    "plugin.update.url",
                    "logger.url"
                };

                if( args.length == 3){
                    for(int i = 0; i < stringOptions.length;i++){
                        // Option found
                        if(stringOptions[i].equalsIgnoreCase(args[1])){
                            Configuration config = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PLUGIN);
                            config.setProperty(
                                    stringOptions[i],
                                    args[2]);
                            config.save();
                            sender.sendMessage(ChatColor.GREEN+stringOptions[i]+" has been altered");
                            sender.sendMessage(ChatColor.GREEN+"A restart maybe required to apply changes");
                            return true;
                        }
                    }
                }

                String[] intOptions = {
                    "protection.tower.threshold"
                };

                if( args.length == 3){
                    for(int i = 0; i < intOptions.length;i++){
                        // Option found
                        if(intOptions[i].equalsIgnoreCase(args[1])){
                            Configuration config = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PLUGIN);
                            config.setProperty(
                                    intOptions[i],
                                    Integer.parseInt(args[2]));
                            config.save();
                            sender.sendMessage(ChatColor.GREEN+intOptions[i]+" has been altered");
                            sender.sendMessage(ChatColor.GREEN+"A restart maybe required to apply changes");
                            return true;
                        }
                    }
                }
                sender.sendMessage(ChatColor.RED + "Action still to do!");
                return true;
            } //END /monkey setvar


            if ("enable".equalsIgnoreCase(args[0]) || "disable".equalsIgnoreCase(args[0])) {
                // List all all the boolean options
                String[] boolOptions = {
                  "server.protection.enabled",
                  "plugin.update.auto",
                  "plugin.silent", // TODO Make plugin silent by default ;-)
                  "protection.grief",
                  "protection.fire",
                  "protection.tower.strict",
                  "protection.tower.enable",
                  "logger.enable",
                  "logger.connect",
                  "logger.disconnect",
                  "logger.chat",
                  "override.nag"
                };

                // Correct number of arguments?
                if( args.length == 2){
                    for(int i = 0; i < boolOptions.length;i++){
                        // Option found
                        if(boolOptions[i].equalsIgnoreCase(args[1])){
                            Configuration config = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PLUGIN);
                            config.setProperty(
                                    boolOptions[i],
                                    "enable".equalsIgnoreCase(args[0]));
                            config.save();
                            sender.sendMessage(ChatColor.GREEN+boolOptions[i]+" has been altered");
                            sender.sendMessage(ChatColor.GREEN+"A restart maybe required to apply changes");
                            return true;
                        }
                    }
                }



                //return false, the command wasn't found
                return false;
            } //END /monkey [enable/disable]
            if (args.length == 3){
                // Must be admin to add users
                if( sender instanceof Player ){
                    Player player = (Player)sender;
                    if (!m_permissions.getBoolean(player.getName().toLowerCase()+".isAdmin", false))
                    	sender.sendMessage("You do not have permission to do that");
                        return false;
                }


                /*
                 * Was just a quick implementation to get it working
                 */
                if ("add".equalsIgnoreCase(args[0])) {
                    // Get permission configs

                    // Username
                    String playerName = args[2].toLowerCase();

                    if("user".equalsIgnoreCase(args[1])){
                        m_permissions.setProperty(playerName+".canBuild", true);
                        m_permissions.setProperty(playerName+".canIgnite", false);
                        m_permissions.save();
                        sender.sendMessage("New player '"+ playerName + "' added");
                        return true;
                    }
                    if("vip".equalsIgnoreCase(args[1])){
                        m_permissions.setProperty(playerName+".canBuild", true);
                        m_permissions.setProperty(playerName+".canIgnite", false);
                        m_permissions.setProperty(playerName+".isVip", true);
                        m_permissions.save();
                        
                        sender.sendMessage("Vip player '"+ playerName + "' added");
                        return true;
                    }
                    if("admin".equalsIgnoreCase(args[1])){
                        m_permissions.setProperty(playerName+".canBuild", true);
                        m_permissions.setProperty(playerName+".canIgnite", true);
                        m_permissions.setProperty(playerName+".isAdmin", true);
                        m_permissions.save();
                        sender.sendMessage("Admin player '"+ playerName + "' added");
                        return true;
                    }
                }// END add
                if ("remove".equalsIgnoreCase(args[0]) || "rm".equalsIgnoreCase(args[0])) {
                	// Username
                    String playerName = args[2].toLowerCase();
                    
                    if("user".equalsIgnoreCase(args[1])){
                    	m_permissions.removeProperty(playerName);
                        m_permissions.save();
                        sender.sendMessage("player '"+ playerName + "' removed");
                        return true;
                    }
                    if("vip".equalsIgnoreCase(args[1])){
                        m_permissions.setProperty(playerName+".isVip", false);
                        m_permissions.save();
                        
                        sender.sendMessage("Vip player '"+ playerName + "' removed");
                        return true;
                    }
                    if("admin".equalsIgnoreCase(args[1])){
                        m_permissions.setProperty(playerName+".canIgnite", false);
                        m_permissions.setProperty(playerName+".isAdmin", false);
                        m_permissions.save();
                        sender.sendMessage("Admin player '"+ playerName + "' removed");
                        return true;
	                }
	            }
            } // END args == 4

            if ("user".equalsIgnoreCase(args[0])) {
                sender.sendMessage( ChatColor.DARK_RED + "WARNING CASE SENSITIVE");
                sender.sendMessage( args[1]+".canBuild: " + m_permissions.getBoolean(args[1]+".canBuild", false));
                sender.sendMessage( args[1]+".canIgnite: " + m_permissions.getBoolean(args[1]+".canIgnite", false));
                sender.sendMessage( args[1]+".isAdmin: " + m_permissions.getBoolean(args[1]+".isAdmin", false));
                sender.sendMessage( args[1]+".isVip: " + m_permissions.getBoolean(args[1]+".isVip", false));
                return true;
            }

            if ("world".equalsIgnoreCase(args[0])) {
                List<World> worlds = m_plugin.getServer().getWorlds();

                Iterator<World> world_iterator = worlds.iterator();

                while( world_iterator.hasNext() ) {
                    World world = world_iterator.next();
                    sender.sendMessage( world.getName() + " : " + world.getSeed() );
                }

                return true;
            }

        }// END args > 0

        sender.sendMessage("No matching commands found");
        return false;
    }
}
