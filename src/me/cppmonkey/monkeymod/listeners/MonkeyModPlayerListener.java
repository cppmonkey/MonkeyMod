package me.cppmonkey.monkeymod.listeners;

import java.net.URLEncoder;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MonkeyModPlayerListener extends PlayerListener {

    private final MonkeyMod m_plugin;

    public MonkeyModPlayerListener(MonkeyMod instance) {
        m_plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        // reporting to cppmonkey.net
        Player player = event.getPlayer();

        // setting up parms for http request
        String[] parms = {
            "action=connect",
            "player=" + player.getName(),
            "vip="+Boolean.toString(m_plugin.getPermition(player, ".isVip")),
            "admin=" + Boolean.toString(m_plugin.getPermition(player, ".isAdmin"))};
        HttpRequestThread notification = new HttpRequestThread(
                "Connection Notification Thread:" + player.getName(),
                player,
                m_plugin.getLoggerUrl(),
                parms,
                false);

        // FIXME - improve method of checking to see if the player is known
        if (m_plugin.isKnownUser( player ) == null){
            player.sendMessage( ChatColor.GREEN+"Welcome "+ player.getName() +", you apear to be new around here");
            player.sendMessage( ChatColor.GREEN+"Please wait one moment. Checking permissions with CppMonkey.NET");

            //TODO start thread with appropriate callback function to ammend any user abilities
        }
        else
        {
            player.sendMessage( ChatColor.GREEN+"Welcome back "+ player.getName() +", lovely to see you again =).");
            
            if (m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PERMISSIONS).getBoolean(player.getName()+".isVip", false)) {
                player.setDisplayName(ChatColor.GREEN + player.getName()+ChatColor.WHITE);
            }else if (m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PERMISSIONS).getBoolean(player.getName()+".isAdmin", false)) {
                player.setDisplayName(ChatColor.RED + player.getName()+ChatColor.WHITE);
            }
            
            
        }

        notification.setPriority(Thread.MIN_PRIORITY);
        notification.start();
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        //reporting to cppmonkey.net
        String[] parms = {
            "action=disconnect",
            "player=" + player.getName()
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

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = (Player)event.getPlayer();
        String message = event.getMessage();

        try {
            String parms[] = {
                "action=message",
                "player=" + URLEncoder.encode(player.getName(), "UTF-8"),
                "message=" + URLEncoder.encode(message, "UTF-8")
            };
            HttpRequestThread notification = new HttpRequestThread(
                    "Disconnection Notification Thread:" + player.getName(),
                    player,
                    m_plugin.getLoggerUrl(),
                    parms);
            notification.setPriority(Thread.MIN_PRIORITY);

            notification.start();
        } catch (Exception e) {
        }
    }

    // Something not right here.... Removed to see if there is a warning generated
    @Override
    public void onInventoryOpen(PlayerInventoryEvent event) {
        Player player = (Player)event.getPlayer();

        if (player != null){
            player.sendMessage(ChatColor.YELLOW+"onInventoryOpen");
            if (m_plugin.getPermition(player, "")){
                return;
            }
            else
            {
                player.sendMessage(ChatColor.RED+"You cant do that");
            }
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = (Player)event.getPlayer();

        if (player != null){
            player.sendMessage(ChatColor.YELLOW+"onPlayerInteract");
        }
    }
}
