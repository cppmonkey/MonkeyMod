/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.commands;

import java.io.File;
import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author CppMonkey
 */
public class PluginCommand implements CommandExecutor {
    private final MonkeyMod m_plugin;

    public PluginCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {

        if (args.length == 2) {
            if ("reload".equalsIgnoreCase(args[0])) {
                // reload plugin
                final PluginManager pm = m_plugin.getServer().getPluginManager();
                final Plugin plugin = pm.getPlugin(args[1]);

                if(plugin.isEnabled()){
                    new Thread(new Runnable(){
                       public void run(){
                           synchronized (pm)
                           {
                                pm.disablePlugin(plugin);
                               try{
                               pm.loadPlugin(new File("plugins", args[1] + ".jar"));
                               }catch( Throwable ex){
                                   sender.sendMessage(ChatColor.RED + "Could load plugin " + args[1] + ".jar");
                               }
                            }
                        }
                    }).start();
                    sender.sendMessage(ChatColor.RED + "Reloading plugin");
                    return true;
                }
            }
        }
        // Not processed
        return false;
    }

    // Available command(s)
    @SuppressWarnings("unused")
    private enum ECommands {
        RELOAD
    }
}
