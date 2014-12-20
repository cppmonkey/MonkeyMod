package me.cppmonkey.monkeymod.listeners;

import java.net.URLEncoder;
import java.util.Locale;

import me.cppmonkey.monkeymod.BoxyExecutor;
import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.Parm;
import me.cppmonkey.monkeymod.callback.LoginCallback;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.config.Configuration;

public class MonkeyModPlayerListener extends PlayerListener {

    private MonkeyMod m_plugin;
    private Configuration m_permissions;
    private Configuration m_boxy;

    public MonkeyModPlayerListener(MonkeyMod instance) {
        m_plugin = instance;
        m_permissions = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PERMISSIONS);
        m_boxy = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.BOXY);
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            // reporting to cppmonkey.net
            Player player = event.getPlayer();

            // setting up parms for http request
            Parm[] parms = {
                new Parm("action", "connect"),
                new Parm("player", player.getName()),
                new Parm("vip", Boolean.toString(m_permissions.getBoolean(player.getName().toLowerCase(Locale.ENGLISH) + ".isVip", false))),
                new Parm("admin", Boolean.toString(m_permissions.getBoolean(player.getName().toLowerCase(Locale.ENGLISH) + ".isAdmin", false))),
                new Parm("ip", player.getAddress().getAddress().getHostAddress().toString())
            };
            HttpRequestThread notification = new HttpRequestThread(
                    "Connection Notification Thread:" + player.getName(),
                    player,
                    m_plugin.getLoggerUrl(),
                    parms,
                        new LoginCallback(m_plugin, player));

                // FIXME - improve method of checking to see if the player is known
                if (m_plugin.isKnownUser(player) == null) {
                    player.sendMessage(ChatColor.GREEN + "Welcome " + player.getName() + ", you apear to be new around here");
                    player.sendMessage(ChatColor.GREEN + "Please wait one moment. Checking permissions with CppMonkey.NET");
                } else {
                    player.sendMessage(ChatColor.GREEN + "Welcome back " + player.getName() + ", lovely to see you again =).");

                    try {

                        if (m_plugin.getPermition(player, ".isAdmin")) {
                            player.setDisplayName(ChatColor.RED + player.getName() + ChatColor.WHITE);
                        } else if (m_plugin.getPermition(player, ".isVip")) {
                            player.setDisplayName(ChatColor.GREEN + player.getName() + ChatColor.WHITE);
                        }
                    } catch (Throwable ex) {
                        player.sendMessage(ChatColor.RED + "EXCEPTION");
                    }
                }

            notification.setPriority(Thread.MIN_PRIORITY);
            notification.start();
        } catch (Throwable ex) {
           MonkeyMod.log.info("Excption within onPlayerJoin()");
        }
    }

    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        //reporting to cppmonkey.net
        Parm[] parms = {
            new Parm("action", "disconnect"),
            new Parm("player", player.getName())
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
            Parm parms[] = {
                new Parm("action", "message"),
                new Parm("player", URLEncoder.encode(player.getName(), "UTF-8")),
                new Parm("message", URLEncoder.encode(message, "UTF-8"))
            };
            HttpRequestThread notification = new HttpRequestThread(
                    "Disconnection Notification Thread:" + player.getName(),
                    player,
                    m_plugin.getLoggerUrl(),
                    parms);
            notification.setPriority(Thread.MIN_PRIORITY);
            notification.start();
        } catch (Exception e) {
            //FIXME Do something with exception?
           MonkeyMod.log.info(e.getMessage());
        }
    }

    // Something not right here.... Removed to see if there is a warning generated
    public void onInventoryOpen(PlayerInventoryEvent event) {
        Player player = event.getPlayer();

        if (player != null) {
            player.sendMessage(ChatColor.YELLOW + "onInventoryOpen");
            if (m_plugin.getPermition(player, "")) {
                return;
            } else {
                player.sendMessage(ChatColor.RED + "You cant do that");
            }
        }
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player != null) {
            // player interaction sent from player
            Action click = event.getAction();
            if (click.equals(Action.RIGHT_CLICK_BLOCK)
                    && m_boxy.getBoolean(player.getName().toLowerCase(Locale.ENGLISH) + ".enabled", false)
                    && m_boxy.getInt("boxyToolID", -1) == player.getItemInHand().getTypeId()) {

                if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use Boxy");
                } else {

                            Block block = event.getClickedBlock();
                            int X = 0;
                            int Y = 0;
                            int Z = 0;
                            try {
                                X = block.getLocation().getBlockX();
                                Y = block.getLocation().getBlockY();
                                Z = block.getLocation().getBlockZ();
                                //the switch compensates coords for the side of the block clicked
                                switch (event.getBlockFace()) {
                                    case UP:
                                        Y++;
                                        break;
                                    case DOWN:
                                        Y--;
                                        break;
                                    case NORTH:
                                        X++;
                                        break;
                                    case SOUTH:
                                        X--;
                                        break;
                                    case EAST:
                                        Z++;
                                        break;
                                    case WEST:
                                        Z--;
                                        break;
                                    default:
                                        break;
                                }

                                BoxyExecutor BoxyRunner = new BoxyExecutor(m_plugin);
                                BoxyRunner.playerBoxyClickEvent(player, block, X, Y, Z);
                            } catch (NullPointerException e) {
                                player.sendMessage(ChatColor.RED + "This is NOT a valid Boxy position or block type!");
                                m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] BOXY OPERATION FAILED!");
                    }
                }
            }
        }
    }
}
