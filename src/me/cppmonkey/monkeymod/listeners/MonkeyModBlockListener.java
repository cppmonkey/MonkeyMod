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

    @SuppressWarnings("unused")
    private final MonkeyMod m_plugin;

    public MonkeyModBlockListener(MonkeyMod instance) {
        m_plugin = instance;
    }

    @Override
    public void onBlockIgnite(BlockIgniteEvent event) {
        //TODO, Allow or disallow action

        Player player = (Player) event.getPlayer();

        // is player?
        if (player != null) {
            if (m_plugin.getPermition(player, ".canIgnite")) {
                //CAN BURN!!!
                return; // nothing else to do here
            } else {
                //Not allowed to burn
                player.sendMessage(ChatColor.RED + "You dont have permission to do that.");

                event.setCancelled(true);
                return;
            }
        } else if (m_plugin.getConfiguration().getBoolean("protection.fire", false)) // Is enviroment caused burning allowed? Cant be first otherwise players wouldnt be able to place fire at all
        {
            // cancel eviroment based fire
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = (Player) event.getPlayer();

        if (player != null) {
            if (m_plugin.getPermition(player, ".canBuild")) {
                // nothing to do
                return;
            } else {
                player.sendMessage(ChatColor.RED + "You don't have pemission to do that");
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void onBlockDamage(BlockDamageEvent event) {
        System.out.println( ChatColor.YELLOW + "onBlockDamage");
        Player player = (Player) event.getPlayer();

        //return is not a player
        if (player == null)
            return;
            //player.sendMessage(ChatColor.YELLOW + "onBlockDamage");

        // Can the player build?
        if (!m_plugin.getPermition(player, ".canBuild")){
            player.sendMessage(ChatColor.RED + "You don't have pemission to do that");
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onBlockCanBuild(BlockCanBuildEvent event) {
        System.out.println( "onBlockCanBuild");
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        System.out.println( "onBlockBreak");

        Player player = (Player) event.getPlayer();

        if (player != null) {
            //player.sendMessage(ChatColor.YELLOW + "onBlockDamage");
        }
    }
}
