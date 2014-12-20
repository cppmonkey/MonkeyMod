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
import java.net.URLEncoder;
import java.util.Locale;

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
        try{
            if (sender instanceof Player) {
            Player player = (Player) sender;
                boolean compacted = false;
                    String Details[] = args[0].split(":");
                if(args.length > 1){
                    for(int i = 1;i<args.length;i++){
                        Details[1] += " " + args[i];
                    }
                    compacted = true;
                }
                if((args.length == 1)||(compacted)){
                    if(Details.length == 2){
                        // this assumes we wont have a player called: ";-- DROP users;
                        String to = URLEncoder.encode(Details[0], "UTF-8");
                        String message = URLEncoder.encode(Details[1], "UTF-8");
                        Parm[] parms = {
                            new Parm("action", "offline"),
                            new Parm("player", URLEncoder.encode(player.getName(), "UTF-8")),
                            new Parm("recipient",to),
                            new Parm("message", message)
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
                        sender.sendMessage("Message sent! Message will be transmitted on next logon.");
                        return true;
                    }
                }
            }
        }catch(Exception e){
            sender.sendMessage("Message sending failed:");
            sender.sendMessage(e.getMessage());
        }
        return false;
    }
    
}
