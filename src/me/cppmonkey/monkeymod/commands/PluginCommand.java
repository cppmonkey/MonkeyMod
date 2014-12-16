/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.commands;

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
                final Server server = m_plugin.getServer();
                final PluginManager pm = server.getPluginManager();
                final Plugin plugin = pm.getPlugin(args[1]);

                if (plugin.isEnabled()) {
                    new Thread(new Runnable() {
                        public void run() {
                            synchronized (pm) {
                                pm.disablePlugin(plugin);

                                pm.enablePlugin(plugin);
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
    private enum ECommands {

        RELOAD
    }
}
