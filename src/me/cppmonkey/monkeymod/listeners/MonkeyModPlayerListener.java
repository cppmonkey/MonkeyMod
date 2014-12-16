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
import org.bukkit.util.config.Configuration;

public class MonkeyModPlayerListener extends PlayerListener {

    private MonkeyMod m_plugin;
    @Deprecated
    private Configuration m_permissions;

    public MonkeyModPlayerListener(MonkeyMod instance) {
        m_plugin = instance;
        m_permissions = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PERMISSIONS);
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
            try{
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

            //TODO start thread with appropriate callback function to amend any user abilities
        }
        else
        {
            player.sendMessage( ChatColor.GREEN+"Welcome back "+ player.getName() +", lovely to see you again =).");

                try{

                    if (m_plugin.getPermition(player, ".isAdmin")) {
                        player.setDisplayName(ChatColor.RED + (String)player.getName()+ChatColor.WHITE);
                    } else if (m_plugin.getPermition(player, ".isVip")) {
                        player.setDisplayName(ChatColor.GREEN + (String)player.getName()+ChatColor.WHITE);
            }
                }catch(Throwable ex){
                    player.sendMessage(ChatColor.RED + "EXCEPTION");
                }
        }

        notification.setPriority(Thread.MIN_PRIORITY);
        notification.start();
        }catch(Throwable ex){
            System.out.println("Exception within onPlayerJoin()");
        }
    }

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

    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
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
    public void onInventoryOpen(PlayerInventoryEvent event) {
        Player player = event.getPlayer();

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

    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (player != null){
            if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use Boxy");
            }
            else{
                Configuration m_pluginBoxy = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.BOXY);
                if(Integer.parseInt(m_pluginBoxy.getProperty("boxyToolID").toString()) == player.getItemInHand().getTypeId()){
                    int X=0;
                    int Y=0;
                    int Z=0;
                    try{
                        X = event.getClickedBlock().getLocation().getBlockX();
                        Y = event.getClickedBlock().getLocation().getBlockY();
                        Z = event.getClickedBlock().getLocation().getBlockZ();
                        BoxyCommand BoxyExec = new BoxyCommand(m_plugin);
                        BoxyExec.playerListenerEvent(player, X, Y, Z);
                    }
                    catch(NullPointerException e){
                        player.sendMessage(ChatColor.RED + "This is NOT a valid Boxy position");
                    }
                }
            }
        }
    }
}
