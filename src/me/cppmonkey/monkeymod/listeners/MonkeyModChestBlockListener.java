package me.cppmonkey.monkeymod.listeners;

import java.util.Locale;

import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.config.Configuration;
import org.bukkit.Location;
import me.cppmonkey.monkeymod.Parm;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;

public class MonkeyModChestBlockListener extends BlockListener {

    private final MonkeyMod m_plugin;
    private Configuration m_chestPermissions;

    public MonkeyModChestBlockListener(MonkeyMod instance) {
        m_plugin = instance;
        m_chestPermissions = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.CHESTS);
    }

    private String nextToChest(BlockPlaceEvent event) {
 
        Location blockLocation = event.getBlock().getLocation();
        int X = (int) blockLocation.getX();
        int Z = (int) blockLocation.getZ();
        int Y = (int) blockLocation.getY();
        String Location = "";
        String World = event.getBlock().getWorld().getName();
        String Owner = "NONE";
        X++;
        if (event.getPlayer().getWorld().getBlockAt(X, Y, Z).getType() == Material.CHEST ) {
            Location = World + ":" + X + "," + Y + "," + Z;
            Owner = m_chestPermissions.getString(Location + ".owner", "NONE");
        }
        X -= 2;
        if (event.getPlayer().getWorld().getBlockTypeIdAt(X, Y, Z) == 54) {
            Location = World + ":" + X + "," + Y + "," + Z;
            if (!Owner.matches(m_chestPermissions.getString(Location + ".owner", "NONE"))) {
                Owner = m_chestPermissions.getString(Location + ".owner", "NONE");
            }
        }
        X++;
        Z--;
        if (event.getPlayer().getWorld().getBlockTypeIdAt(X, Y, Z) == 54) {
            Location = World + ":" + X + "," + Y + "," + Z;
            if (!Owner.matches(m_chestPermissions.getString(Location + ".owner", "NONE"))) {
                Owner = m_chestPermissions.getString(Location + ".owner", "NONE");
            }
        }
        Z += 2;
        if (event.getPlayer().getWorld().getBlockTypeIdAt(X, Y, Z) == 54) {
            Location = World + ":" + X + "," + Y + "," + Z;
            if (!Owner.matches(m_chestPermissions.getString(Location + ".owner", "NONE"))) {
                Owner = m_chestPermissions.getString(Location + ".owner", "NONE");
            }
        }
        return Owner;
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null && m_plugin.getPermition(player, ".canBuild")) {

                // Is the item being place a chest?
                if (event.getBlockPlaced().getType() == Material.CHEST) {
                    String nextTo = nextToChest(event);
                if (nextTo.matches("NONE") || nextTo.matches(player.getName().toLowerCase(Locale.ENGLISH))) {
                        player.sendMessage(ChatColor.GREEN + "This chest is now registered to you");
                        String chestLocation = event.getBlock().getWorld().getName() + ":" + (int) event.getBlock().getX() + "," + (int) event.getBlock().getY() + "," + (int) event.getBlock().getZ();
                        m_chestPermissions.setProperty(chestLocation + ".owner", player.getName().toLowerCase(Locale.ENGLISH));
                        m_chestPermissions.setProperty(chestLocation + ".lock", "CLOSED");
                    } else {
                        player.sendMessage(ChatColor.RED + "You cannot place a chest here.");
                        player.sendMessage(ChatColor.RED + "The adjacent chest does not belong to you");
                        event.setCancelled(true);
                    }
            } 
        }
    }

    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        if (player != null && event.getBlock().getType() == Material.CHEST) {
            //player.sendMessage(ChatColor.YELLOW + "onBlockDamage");
            String chestLocation = event.getBlock().getWorld().getName() + ":" + (int) event.getBlock().getX() + "," + (int) event.getBlock().getY() + "," + (int) event.getBlock().getZ();
            String chestOwner = m_chestPermissions.getString(chestLocation + ".owner", "PUBLIC").toLowerCase(Locale.ENGLISH);
            
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

    public void onBlockCanBuild(BlockCanBuildEvent event) {
       MonkeyMod.log.info("onBlockCanBuild");
    }

    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (player != null && event.getBlock().getType() == Material.CHEST) {
            String chestLocation = event.getBlock().getWorld().getName() + ":" + event.getBlock().getX() + "," + event.getBlock().getY() + "," + event.getBlock().getZ();
                String chestOwner = m_chestPermissions.getString(chestLocation + ".owner", "PUBLIC");
            if (chestOwner.equalsIgnoreCase(player.getName()) || m_plugin.getPermition(player, ".isAdmin")) {
                    m_chestPermissions.removeProperty(chestLocation + ".owner");
                    m_chestPermissions.removeProperty(chestLocation + ".lock");
                    m_chestPermissions.removeProperty(chestLocation);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to destroy this chest");
                    player.sendMessage(ChatColor.RED + "contact " + chestOwner + " if you require assistance.");
                    event.setCancelled(true);
            }
        } else if (event.getBlock().getType() == Material.CHEST){
            //Chest destroyed by external force eg tnt.
            event.setCancelled(true);
        }
    }
}
