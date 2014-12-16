package me.cppmonkey.monkeymod.listeners;

import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.config.Configuration;

public class MonkeyModBlockListener extends BlockListener {

    private final MonkeyMod m_plugin;
    private final Configuration m_chestPermissions;

    public MonkeyModBlockListener(MonkeyMod instance) {
        m_plugin = instance;
        m_chestPermissions = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.CHESTS);
    }

    @Override
    public void onBlockIgnite(BlockIgniteEvent event) {
        //TODO Add exceptions to burning

        Player player = event.getPlayer();

        // is player?
        if (player != null) {
            if (m_plugin.getPermition(player, ".canIgnite")) {
                //CAN BURN!!!
                return; // nothing else to do here
            } else {
                //Not allowed to burn
                player.sendMessage(ChatColor.RED + "You don't have permission to do that.");

                event.setCancelled(true);
                return;
            }
        } else if (m_plugin.getConfiguration().getBoolean("protection.fire", false)) // Is environment caused burning allowed? Cant be first otherwise players wouldn't be able to place fire at all
        {
            // cancel environment based fire
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player != null) {
            if (m_plugin.getPermition(player, ".canBuild")) {

                // Is the item being place a chest?
                if (event.getBlockPlaced().getType() == Material.CHEST) {
                    //
                    player.sendMessage("You placed a chest");
                    String chestLocation = event.getBlockPlaced().getLocation().getX() + "," + event.getBlockPlaced().getLocation().getY()  + "," + event.getBlockPlaced().getLocation().getZ();

                    m_chestPermissions.setProperty(chestLocation + ".owner", player);
                    return;
                }

                // nothing to do
                return;
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to do that");
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();

        //return is not a player
        if (player == null) {
            return;
        }
            //player.sendMessage(ChatColor.YELLOW + "onBlockDamage");

        // Can the player build?
        if (!m_plugin.getPermition(player, ".canBuild")){
            player.sendMessage(ChatColor.RED + "You don't have permission to do that");
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

        Player player = event.getPlayer();

        if (player != null) {
            //player.sendMessage(ChatColor.YELLOW + "onBlockDamage");
        }
    }
}
