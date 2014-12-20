package me.cppmonkey.monkeymod.listeners;

import java.util.Locale;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.Configuration;
//TODO: change to new configuration format as this is getting depricated next version: http://wiki.bukkit.org/Introduction_to_the_New_Configuration
import me.cppmonkey.monkeymod.Parm;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;

public class MonkeyModChestPlayerListener extends PlayerListener {

    private MonkeyMod m_plugin;
    private Configuration m_chestPermissions;

    public MonkeyModChestPlayerListener(MonkeyMod instance) {
        m_plugin = instance;
        m_chestPermissions = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.CHESTS);
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
            Action click = event.getAction();
        if (player != null && click.equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType() == Material.CHEST) {
            String chestLocation = event.getClickedBlock().getWorld().getName() + ":" + event.getClickedBlock().getX() + "," + event.getClickedBlock().getY() + "," + event.getClickedBlock().getZ();
            String chestOwner = m_chestPermissions.getString(chestLocation + ".owner", "PUBLIC");
            String chestLock = m_chestPermissions.getString(chestLocation + ".lock", "OPEN");
            if (player.getItemInHand().getType() == Material.BONE) {
                if (chestOwner.equalsIgnoreCase(player.getName())) {
                    String keySetting = m_chestPermissions.getString(player.getName().toLowerCase(Locale.ENGLISH) + ".key", "NONE");
                    if (keySetting.matches("NONE")) {
                        player.sendMessage(ChatColor.GOLD + "You have not set your chest lock settings!");
                        player.sendMessage(ChatColor.GOLD + "Use '/chest lock' or '/chest unlock'");
                    } else if (keySetting.matches("LOCK")) {
                        player.sendMessage(ChatColor.GOLD + "You have locked this chest");
                        m_chestPermissions.setProperty(chestLocation + ".lock", "CLOSED");
                    } else if (keySetting.matches("UNLOCK")) {
                        player.sendMessage(ChatColor.GOLD + "You have un-locked this chest");
                        player.sendMessage(ChatColor.GOLD + "Anybody can now access its contents!");
                        m_chestPermissions.setProperty(chestLocation + ".lock", "OPEN");
                    }
                    // Cancel the appearance of the inventory
                    event.setCancelled(true);
                } else {
                    player.sendMessage(ChatColor.RED + "This is not your chest!");
                    player.sendMessage(ChatColor.RED + "You cannot lock / unlock it");
                    event.setCancelled(true);
                    //reporting to cppmonkey.net
                    Parm[] parms = {
                        new Parm("action", "attempt_to_unlock_chest"),
                        new Parm("player", player.getName()),
                        new Parm("data", chestOwner + ":" + event.getClickedBlock().getX() + "," + event.getClickedBlock().getY() + "," + event.getClickedBlock().getZ())
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
            } else if (!chestOwner.equalsIgnoreCase(player.getName())
                    && !chestOwner.matches("PUBLIC")
                    && !m_plugin.getPermition(player, ".isAdmin")
                    && !chestLock.matches("OPEN")) {
                player.sendMessage(ChatColor.RED + "You cannot access this chest.");
                player.sendMessage(ChatColor.RED + "It belongs to " + chestOwner + ", and is locked.");
                event.setCancelled(true);
                //reporting to cppmonkey.net
                Parm[] parms = {
                    new Parm("action", "attempt_to_open_chest"),
                    new Parm("player", player.getName()),
                    new Parm("data", chestOwner + ":" + event.getClickedBlock().getX() + "," + event.getClickedBlock().getY() + "," + event.getClickedBlock().getZ())
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
}
