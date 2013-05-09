package me.cppmonkey.monkeymod.playerlistener;

import java.net.URLEncoder;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
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
            "vip="+Boolean.toString(m_plugin.getPermition(player, ".isVip")), /* TODO process VIP status */
            "admin=" + Boolean.toString(m_plugin.getPermition(player, ".isAdmin")) /* TODO process Op/Admin status*/};
        HttpRequestThread notification = new HttpRequestThread(
                "Connection Notification Thread:" + player.getName(),
                player,
                m_plugin.getLoggerUrl(),
                parms,
                false);

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

            notification.start();
        } catch (Exception e) {
        }
    }

 	/* // Something not right here.... Removed to see if there is a warning generated
    @Override
    public void onInventoryOpen(PlayerInventoryEvent event) {
        Player player = (Player)event.getPlayer();

        //if (player != null){
            if (m_plugin.getPermition(player, "")){
                return;
            }
            else
            {
                player.sendMessage(ChatColor.RED+"You cant do that");
            }
        //}
    }
	*/
}
