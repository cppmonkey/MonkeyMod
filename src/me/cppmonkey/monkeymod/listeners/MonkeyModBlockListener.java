package me.cppmonkey.monkeymod.listeners;

import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import me.cppmonkey.monkeymod.Parm;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;

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
            if (m_plugin.getPermition(player, ".canIgnite")) {
                // CAN BURN!!!
            } else {
                //Not allowed to burn
                player.sendMessage(ChatColor.RED + "You dont have permission to ignite");

                event.setCancelled(true);
                Parm[] parms = {
                    new Parm("action", "ignite-attempt"),
                    new Parm("player", player.getName()),
                    new Parm("data", event.getBlock().getX() + "," + event.getBlock().getY() + "," + event.getBlock().getZ())
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
            /* Is environment caused burning allowed? Can't be first otherwise players wouldn't be able to place fire at all */
        } else if (m_plugin.getConfig().getBoolean("protection.fire", false)) {
            // cancel environment based fire
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null && !m_plugin.getPermition(player, ".canBuild")) {
            player.sendMessage(ChatColor.RED + "You don't have pemission to build");
            event.setCancelled(true);
            Parm[] parms = {
                new Parm("action", "build-attempt"),
                    new Parm("player", player.getName()),
                    new Parm("data", event.getBlock().getX() + "," + event.getBlock().getY() + "," + event.getBlock().getZ())
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
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();

        //return is not a player
        if (player != null && !m_plugin.getPermition(player, ".canBuild")) {
            player.sendMessage(ChatColor.RED + "You don't have pemission to destroy");
            event.setCancelled(true);
                    Parm[] parms = {
                    new Parm("action", "block-break-attempt"),
                    new Parm("player", player.getName()),
                    new Parm("data",event.getBlock().getX() + "," + event.getBlock().getY() + "," + event.getBlock().getZ())
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
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
            // THIS MUST BE HERE!!! Otherwise people can wipe the text from signs
        if (player != null && !m_plugin.getPermition(player, ".canBuild")) {
            player.sendMessage(ChatColor.RED + "You don't have pemission to destroy");
            event.setCancelled(true);
            Parm[] parms = {
                    new Parm("action", "block-break-attempt"),
                    new Parm("player", player.getName()),
                    new Parm("data", event.getBlock().getX() + "," + event.getBlock().getY() + "," + event.getBlock().getZ())
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
    }
}
