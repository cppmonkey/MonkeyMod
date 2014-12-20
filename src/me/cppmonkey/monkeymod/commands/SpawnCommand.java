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

/**
 * 
 * @author CppMonkey
 */
public class SpawnCommand implements CommandExecutor {

    public final static String command = "spawn";

    public SpawnCommand(MonkeyMod instance) {
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cs instanceof Player) {
            Player player = (Player) cs;
            player.teleport(player.getWorld().getHighestBlockAt(player.getWorld().getSpawnLocation()).getLocation());
        } else {
            cs.sendMessage("[ERROR] Console can't use spawn");
        }
        return true;
    }
}
