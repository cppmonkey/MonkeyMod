package me.cppmonkey.monkeymod.listeners;

import java.util.Locale;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.Parm;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class MonkeyModChestBlockListener implements Listener {

    private final MonkeyMod m_plugin;

    public MonkeyModChestBlockListener(MonkeyMod instance) {
        m_plugin = instance;
    }

    private String nextToChest(BlockPlaceEvent event) {
 
        Block blockLocation = event.getBlock();
        int X = blockLocation.getX();
        int Z = blockLocation.getZ();
        int Y = blockLocation.getY();
        String Location = "";
        String World = event.getBlock().getWorld().getName();
        String Owner = "NONE";
        X++;
        if (event.getPlayer().getWorld().getBlockAt(X, Y, Z).getType() == Material.CHEST ) {
            Location = World + ":" + X + "," + Y + "," + Z;
            Owner = m_plugin.getConfig().getString(Location + ".owner", "NONE");
        }
        X -= 2;
        if (event.getPlayer().getWorld().getBlockAt(X, Y, Z).getType() == Material.CHEST) {
            Location = World + ":" + X + "," + Y + "," + Z;
            if (!Owner.matches(m_plugin.getConfig().getString(Location + ".owner", "NONE"))) {
                Owner = m_plugin.getConfig().getString(Location + ".owner", "NONE");
            }
        }
        X++;
        Z--;
        if (event.getPlayer().getWorld().getBlockAt(X, Y, Z).getType() == Material.CHEST) {
            Location = World + ":" + X + "," + Y + "," + Z;
            if (!Owner.matches(m_plugin.getConfig().getString(Location + ".owner", "NONE"))) {
                Owner = m_plugin.getConfig().getString(Location + ".owner", "NONE");
            }
        }
        Z += 2;
        if (event.getPlayer().getWorld().getBlockAt(X, Y, Z).getType() == Material.CHEST) {
            Location = World + ":" + X + "," + Y + "," + Z;
            if (!Owner.matches(m_plugin.getConfig().getString(Location + ".owner", "NONE"))) {
                Owner = m_plugin.getConfig().getString(Location + ".owner", "NONE");
            }
        }
        return Owner;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null && m_plugin.getPermition(player, ".canBuild")) {

                // Is the item being place a chest?
                if (event.getBlockPlaced().getType() == Material.CHEST) {
                    String nextTo = nextToChest(event);
                if (nextTo.matches("NONE") || nextTo.matches(player.getName().toLowerCase(Locale.ENGLISH))) {
                    player.sendMessage(ChatColor.GREEN + "This chest is now registered to you");
                    String chestLocation = event.getBlock().getWorld().getName() + ":" + event.getBlock().getX() + "," +event.getBlock().getY() + "," + event.getBlock().getZ();
                    m_plugin.getConfig().set(chestLocation + ".owner", player.getName().toLowerCase(Locale.ENGLISH));
                    m_plugin.getConfig().set(chestLocation + ".lock", "CLOSED");
                    } else {
                        player.sendMessage(ChatColor.RED + "You cannot place a chest here.");
                        player.sendMessage(ChatColor.RED + "The adjacent chest does not belong to you");
                        event.setCancelled(true);
                    }
            } 
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        if (player != null && event.getBlock().getType() == Material.CHEST) {
            //player.sendMessage(ChatColor.YELLOW + "onBlockDamage");
            String chestLocation = event.getBlock().getWorld().getName() + ":" + event.getBlock().getX() + "," + event.getBlock().getY() + "," + event.getBlock().getZ();
            String chestOwner = m_plugin.getConfig().getString(chestLocation + ".owner", "PUBLIC").toLowerCase(Locale.ENGLISH);
            
            if (!chestOwner.equalsIgnoreCase(player.getName().toLowerCase(Locale.ENGLISH)) && !m_plugin.getPermition(player, ".isAdmin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to destroy this chest");
                player.sendMessage(ChatColor.RED + "It belongs to " + chestOwner);
                event.setCancelled(true);
                Parm[] parms = {
                    new Parm("action", "chest-break-attempt"),
                    new Parm("player", player.getName()),
                    new Parm("data", chestOwner + ":" + event.getBlock().getX() + "," + event.getBlock().getY() + "," + event.getBlock().getZ())
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
        } else if (event.getBlock().getType() == Material.CHEST) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (player != null && event.getBlock().getType() == Material.CHEST) {
            String chestLocation = event.getBlock().getWorld().getName() + ":" + (int)(event.getBlock().getX()) + "," + (int)(event.getBlock().getY()) + "," + (int)(event.getBlock().getZ());
            String chestOwner = m_plugin.getConfig().getString(chestLocation + ".owner", "PUBLIC");
            if (chestOwner.equalsIgnoreCase(player.getName()) || m_plugin.getPermition(player, ".isAdmin")) {
                m_plugin.getConfig().set(chestLocation + ".owner", null);
                m_plugin.getConfig().set(chestLocation + ".lock", null);
                m_plugin.getConfig().set(chestLocation, null);
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission to destroy this chest");
                player.sendMessage(ChatColor.RED + "contact " + chestOwner + " if you require assistance.");
                event.setCancelled(true);
            }
        } else if (event.getBlock().getType() == Material.CHEST){
            // Chest destroyed by external force eg tnt.
            event.setCancelled(true);
        }
    }
}
