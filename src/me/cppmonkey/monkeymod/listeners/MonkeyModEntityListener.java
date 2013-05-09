/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package me.cppmonkey.monkeymod.listeners;

import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

/**
 *
 * @author CppMonkey
 */
public class MonkeyModEntityListener extends EntityListener{

    MonkeyMod m_plugin;

    public MonkeyModEntityListener(MonkeyMod instance) {
        m_plugin = instance;
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        Player player = (Player)event.getEntity();

        if (player != null){
            player.sendMessage(ChatColor.YELLOW+"onEntityDeath");
        }
    }
}
