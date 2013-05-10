package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.config.Configuration;

public class MonkeyCommand implements CommandExecutor {

    private final MonkeyMod m_plugin;

    public MonkeyCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //TODO process commands
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
                HttpRequestThread updateQuery = new HttpRequestThread("uptodate", sender, "http://cppmonkey.net/monkeymod/ajax.php", parms, m_plugin);
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
                // TODO process status commands
                sender.sendMessage(ChatColor.RED + "Action still to do!");
                return true;
            } //END /monkey status

            if ("setvar".equalsIgnoreCase(args[0])) {
                // TODO process setvar commands
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
                  "plugin.update.auto",  
                  "protection.grief",
                  "protection.fire",
                  "protection.tower.stict",
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
                                    Boolean.parseBoolean(args[0]));
                            config.save();
                            sender.sendMessage(ChatColor.GREEN+boolOptions[i]+" has been altered");
                            sender.sendMessage(ChatColor.GREEN+"A restart maybe required to apply changes");
                            return true;
                        }
                    }
                }
                //return false, the command wasnt found
                return false;
            } //END /monkey [enable/disable]
        }// END args > 0
        return false;
    }
}
