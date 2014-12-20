package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModeCommand implements CommandExecutor {

    private MonkeyMod m_plugin;
    public final static String command = "mode";

    public ModeCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use mode commands");
                return true;
            }else{
                if(player.getGameMode() == GameMode.CREATIVE){
                    player.setGameMode(GameMode.SURVIVAL);
                    return true;
                }else{
                    player.setGameMode(GameMode.CREATIVE);
                    return true;
                }
            }
        }
        return false;
    }

}
