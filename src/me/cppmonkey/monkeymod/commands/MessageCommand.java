/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.commands;
import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.cppmonkey.monkeymod.Parm;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;

/**
 *
 * @author caboose89
 */
public class MessageCommand implements CommandExecutor {
    public final static String command = "messages";
    private final MonkeyMod m_plugin;
    
    public MessageCommand(MonkeyMod instance) {
        m_plugin = instance;
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if ("post-send".equalsIgnoreCase(command.getName())) {
                if(args.length == 1){
                    String Details[] = args[0].split(":");
                    if(Details.length == 2){
                        // escape the inputs and post
                        Parm[] parms = {
                            new Parm("action", "offline"),
                            new Parm("player", player.getName()),
                            new Parm("data", args[0])
                            //TODO: add location + owner
                        };
                        HttpRequestThread notification = new HttpRequestThread(
                                "Connection Notification Thread:" + player.getName(),
                                player,
                                m_plugin.getLoggerUrl(),
                                parms,
                                false);
                        notification.setPriority(Thread.MIN_PRIORITY);
                        notification.start();
                    }
                }
            }
        }
        return false;
    }
    
}
