package me.cppmonkey.monkeymod.listeners;

import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class MonkeyModBlockListener extends BlockListener {

    private final MonkeyMod m_plugin;

    public MonkeyModBlockListener(MonkeyMod instance) {
        m_plugin = instance;
    }

    public void onBlockIgnite(BlockIgniteEvent event) {
        //TODO Add exceptions to burning

        Player player = event.getPlayer();

        // is player?
        if (player != null) {
            if (m_plugin.getPermition(player, ".canIgnite")) {
                // CAN BURN!!!
            } else {
                //Not allowed to burn
                player.sendMessage(ChatColor.RED + "You dont have permission to ignite");

                event.setCancelled(true);
            }
            /* Is environment caused burning allowed? Can't be first otherwise players wouldn't be able to place fire at all */
        } else if (m_plugin.getConfiguration().getBoolean("protection.fire", false)) {
            // cancel environment based fire
            event.setCancelled(true);
        }
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null && !m_plugin.getPermition(player, ".canBuild")) {
                player.sendMessage(ChatColor.RED + "You don't have pemission to build");
                event.setCancelled(true);
        }
    }

    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();

        //return is not a player
        if (player != null && !m_plugin.getPermition(player, ".canBuild")) {
            player.sendMessage(ChatColor.RED + "You don't have pemission to destroy");
            event.setCancelled(true);
        }
    }

    public void onBlockCanBuild(BlockCanBuildEvent event) {
       MonkeyMod.log.info("onBlockCanBuild");
    }

    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
            // THIS MUST BE HERE!!! Otherwise people can wipe the text from signs
        if (player != null && !m_plugin.getPermition(player, ".canBuild")) {
            player.sendMessage(ChatColor.RED + "You don't have pemission to destroy");
            event.setCancelled(true);
        }
    }
}
