package me.cppmonkey.monkeymod.listeners;

import java.net.URLEncoder;

import me.cppmonkey.monkeymod.callback.LoginCallback;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.commands.BoxyCommand;
import org.bukkit.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.config.Configuration;
import org.bukkit.event.block.Action;
import org.bukkit.block.Block;

public class MonkeyModPlayerListener extends PlayerListener {

    private MonkeyMod m_plugin;
    @Deprecated
    private Configuration m_permissions;

    public MonkeyModPlayerListener(MonkeyMod instance) {
        m_plugin = instance;
        m_permissions = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PERMISSIONS);
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
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
                m_plugin,
                    new LoginCallback(m_plugin, player));

        // FIXME - improve method of checking to see if the player is known
            if (m_plugin.isKnownUser(player) == null) {
                player.sendMessage(ChatColor.GREEN + "Welcome " + player.getName() + ", you apear to be new around here");
                player.sendMessage(ChatColor.GREEN + "Please wait one moment. Checking permissions with CppMonkey.NET");

            //TODO start thread with appropriate callback function to amend any user abilities
            } else {
                player.sendMessage(ChatColor.GREEN + "Welcome back " + player.getName() + ", lovely to see you again =).");

                try{

                    if (m_plugin.getPermition(player, ".isAdmin")) {
                        player.setDisplayName(ChatColor.RED + player.getName() + ChatColor.WHITE);
                    } else if (m_plugin.getPermition(player, ".isVip")) {
                        player.setDisplayName(ChatColor.GREEN + player.getName() + ChatColor.WHITE);
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
            if (click.compareTo(Action.RIGHT_CLICK_BLOCK) == 0) {
                Configuration m_pluginBoxy = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.BOXY);
                if (m_pluginBoxy.getBoolean(player.getName().toLowerCase() + ".enabled", false)) {
                    //user has boxy enabled
                    if (m_plugin.getPermition(player, ".isVip") || m_plugin.getPermition(player, ".isAdmin")) {
                        // FIXME allowed to use boxy, shouldn't this be the FIRST thing you check?
                        if (m_pluginBoxy.getInt("boxyToolID", -1) == player.getItemInHand().getTypeId()) {
                            Block block = event.getClickedBlock();
                            int X = 0;
                            int Y = 0;
                            int Z = 0;
                            try {
                                X = block.getLocation().getBlockX();
                                Y = block.getLocation().getBlockY();
                                Z = block.getLocation().getBlockZ();
                        BoxyCommand BoxyExec = new BoxyCommand(m_plugin);
                                //the switch compensates coords for the side of the block clicked
                                // FIXME WARNING! ALL MISSING BREAK! Always check compiler output
                                switch (event.getBlockFace()) {
                                    case UP:
                                        Y++;
                                    case DOWN:
                                        Y--;
                                    case NORTH:
                                        X++;
                                    case SOUTH:
                                        X--;
                                    case EAST:
                                        Z++;
                                    case WEST:
                                        Z--;
                                }
                                    
                                //FIXME Don't like the way this is called. Why create a new listener only just to make a single call? just move the code here?
                                playerListenerEvent(player, block, clicked);
                                return;
                            } catch (NullPointerException e) {
                                player.sendMessage(ChatColor.RED + "This is NOT a valid Boxy position or block type!");
                                m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] BOXY OPERATION FAILED!");
                                return;
                    }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have permission to use Boxy");
                        return;
                    }
                }
            }
        }
    }
}
