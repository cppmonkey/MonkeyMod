package me.cppmonkey.monkeymod.listeners;

import java.net.URLEncoder;
import java.util.Locale;

import me.cppmonkey.monkeymod.BoxyExecutor;
import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.Parm;
import me.cppmonkey.monkeymod.callback.Login;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MonkeyModPlayerListener implements Listener {

    private MonkeyMod m_plugin;

    public MonkeyModPlayerListener(MonkeyMod instance) {
        m_plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {

            // Welcome player
            event.getPlayer().sendMessage(m_plugin.getConfig().getString("welcome"));

            // reporting to cppmonkey.net
            // setting up parms for http request
            Parm[] parms = {
                new Parm("action", "connect"),
                new Parm("player", player.getName()),
                new Parm("ip", player.getAddress().getAddress().toString())
            };

            HttpRequestThread notification = new HttpRequestThread("Connection Notification Thread:" + player.getName(), player, m_plugin.getLoggerUrl(), parms, new Login(m_plugin, player));

            notification.setPriority(Thread.MIN_PRIORITY);
            notification.start();
        } catch (Throwable ex) {
           MonkeyMod.log.info("Exception within onPlayerJoin()");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        // reporting to cppmonkey.net
        Parm[] parms = {
            new Parm("action", "disconnect"),
                new Parm("player_id", m_plugin.playerUIDs.get(player).toString())
        };
        HttpRequestThread notification = new HttpRequestThread("Connection Notification Thread:" + player.getName(), player, m_plugin.getLoggerUrl(), parms, false);
        notification.setPriority(Thread.MIN_PRIORITY);
        notification.start();

        // Clean up permissions
        m_plugin.isAdmin.remove(player);
        m_plugin.isVip.remove(player);
        m_plugin.canBuild.remove(player);
        m_plugin.canIgnite.remove(player);
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        try {
            Parm parms[] = {
                new Parm("action", "message"),
                    new Parm("player_id", m_plugin.playerUIDs.get(player).toString()),
                new Parm("message", URLEncoder.encode(message, "UTF-8"))
            };
            HttpRequestThread notification = new HttpRequestThread("Disconnection Notification Thread:" + player.getName(), player, m_plugin.getLoggerUrl(), parms);
            notification.setPriority(Thread.MIN_PRIORITY);
            notification.start();
        } catch (Exception e) {
           MonkeyMod.log.info(e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player != null) {
            // player interaction sent from player
            Action click = event.getAction();
            if (click.equals(Action.RIGHT_CLICK_BLOCK) && m_plugin.getConfig().getBoolean(player.getName().toLowerCase(Locale.ENGLISH) + ".enabled", false) && m_plugin.getConfig().getInt("boxy.tool") == player.getItemInHand().getTypeId()) {

                if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use Boxy");
                } else {

                            Block block = event.getClickedBlock();

                            try {

                        /*
                         * /* //the switch compensates coords for the side of
                         * the block clicked switch (event.getBlockFace()) {
                         * case UP: Y++; break; case DOWN: Y--; break; case
                         * NORTH: X++; break; case SOUTH: X--; break; case EAST:
                         * Z++; break; case WEST: Z--; break; default: break; }
                        */

                                BoxyExecutor BoxyRunner = new BoxyExecutor(m_plugin);
                                BoxyRunner.playerBoxyClickEvent(player, block);
                            } catch (NullPointerException e) {
                                player.sendMessage(ChatColor.RED + "This is NOT a valid Boxy position or block type!");
                                m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] BOXY OPERATION FAILED!");
                    }
                }
            }
        }
    }
}
