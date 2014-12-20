package me.cppmonkey.monkeymod.commands;

import java.util.Locale;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BackCommand implements CommandExecutor {

    private MonkeyMod m_plugin;
    public final static String command = "back";
    
    public BackCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            String path = player.getName().toLowerCase(Locale.ENGLISH)+"."+player.getWorld().getName()+".last";
            Vector pos = m_plugin.getConfig().getVector(path);
                if (pos == null) {
                    player.sendMessage(ChatColor.RED + "Input error. Last tele location not found?");
                } else {
                    player.teleport(pos.toLocation(player.getWorld()));
                }
                return true;
            }
        return false;
    }

}
