package me.cppmonkey.monkeymod.listeners;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.player.PlayerDetails;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class MonkeyModBlockListener implements Listener {

    private final MonkeyMod m_plugin;

    public MonkeyModBlockListener(MonkeyMod instance) {
        m_plugin = instance;
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        //TODO Add exceptions to burning. caboose89 -> such as?

        Player player = event.getPlayer();

        // is player?
        if (player != null) {
            PlayerDetails playerDetails = m_plugin.getPlayerDetails(player);
            if (!playerDetails.canIgnite()) {
                //Not allowed to burn
                player.sendMessage(ChatColor.RED + "You don't have permission to ignite");
                playerDetails.incrementDeniedBlocksIgnited();
                event.setCancelled(true);
            } else {
                playerDetails.incrementBlocksIgnited();
            }
            /* Is environment caused burning allowed? Can't be first otherwise players wouldn't be able to place fire at all */
        } else if (m_plugin.getConfig().getBoolean("protection.fire", false)) {
            // cancel environment based fire
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null){
            PlayerDetails playerDetails = m_plugin.getPlayerDetails(player);

            if (playerDetails.canBuild()) {
                playerDetails.incrementBlocksPlaced();
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to build");
                playerDetails.incrementDeniedBlocksPlaced();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent event){
        if(event.getEntityType() == EntityType.ENDERMAN){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();

        //return is not a player
            PlayerDetails playerDetails = m_plugin.getPlayerDetails(player);
        if (player != null && !playerDetails.canBuild()) {
            event.setCancelled(true);
            playerDetails.incrementDeniedBlocksDamaged();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // THIS MUST BE HERE!!! Otherwise people can wipe the text from signs
        if (player != null) {
            PlayerDetails playerDetails = m_plugin.getPlayerDetails(player);
            if(playerDetails.canBuild()) {
                playerDetails.incrementBlocksDestroyed();
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to destroy");
                playerDetails.incrementDeniedBlocksDestroyed();
                event.setCancelled(true);
            }
        }
    }
}
