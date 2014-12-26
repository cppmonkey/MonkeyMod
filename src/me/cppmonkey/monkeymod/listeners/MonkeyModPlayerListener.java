package me.cppmonkey.monkeymod.listeners;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;

import me.cppmonkey.monkeymod.BoxyExecutor;
import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.boxy.BoxyThread;
import me.cppmonkey.monkeymod.http.callbacks.OnPlayerLogin;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.utils.Parm;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

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
                new Parm("server_uid", m_plugin.getServerUID())
            };

            HttpRequestThread notification = new HttpRequestThread("Connection Notification Thread:" + player.getName(), m_plugin.getLoggerUrl(), parms, new OnPlayerLogin(m_plugin, player));

            notification.setPriority(Thread.MIN_PRIORITY);
            notification.start();

        } catch (RuntimeException rex){
            // Shouldn't really cause any exceptions
            MonkeyMod.reportException("RuntimeExcption within onPlayerJoin()", rex);
        }catch (Exception ex) {
            // Shouldn't really cause any exceptions
            MonkeyMod.reportException("Exception within onPlayerJoin()", ex);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        // reporting to cppmonkey.net
        Parm[] parms = {
            new Parm("action", "disconnect"),
            new Parm("server_uid", m_plugin.getServerUID()),
            new Parm("player_id", m_plugin.getPlayerUID(player).toString())
        };
        HttpRequestThread notification = new HttpRequestThread("Connection Notification Thread:" + player.getName(), m_plugin.getLoggerUrl(), parms);
        notification.setPriority(Thread.MIN_PRIORITY);
        notification.start();

        // Clean up permissions
        m_plugin.isAdmin.remove(player);
        m_plugin.isVip.remove(player);
        m_plugin.canBuild.remove(player);
        m_plugin.canIgnite.remove(player);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        try {
            Parm parms[] = {
                new Parm("action", "message"),
                new Parm("player_id", m_plugin.getPlayerUID(player).toString()),
                new Parm("server_uid", m_plugin.getServerUID()),
                new Parm("message", URLEncoder.encode(message, "UTF-8"))
            };
            HttpRequestThread notification = new HttpRequestThread("Disconnection Notification Thread:" + player.getName(), m_plugin.getLoggerUrl(), parms);
            notification.setPriority(Thread.MIN_PRIORITY);
            notification.start();
        } catch (RuntimeException rex){
            MonkeyMod.reportException("RuntimeExcption within onPlayerChat()", rex);
        } catch (Exception ex) {
            MonkeyMod.reportException("Exception within onPlayerChat()",ex);
        }
    }

    private HashMap<Player,Vector> mp_playerStartVector = new HashMap<Player,Vector>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && m_plugin.getConfig().getBoolean(player.getName().toLowerCase(Locale.ENGLISH) + ".enabled", false) && m_plugin.getConfig().getInt("boxy.tool") == player.getItemInHand().getTypeId()) {
            MonkeyMod.log.info(event.getPlayer().getName() + " is trying to use boxy");
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
                    if ( !m_plugin.getConfig().getBoolean("boxy.experimental")){
                    /* FIXME shouldn't create a new BoxyExecutor just to update start block
                     * by all means make it a static function within your class to update the start location
                     * but a temp variable would be adequate to keep the start location. Means less file access
                     * Does mean that reloading the plugin resets players start location >_<
                     */
                    BoxyExecutor BoxyRunner = new BoxyExecutor(m_plugin);
                    BoxyRunner.playerBoxyClickEvent(player, block);
                    } else {
                        // New scheduled task
                        if (!mp_playerStartVector.containsKey(event.getPlayer())) {
                            mp_playerStartVector.put(event.getPlayer(), event.getClickedBlock().getLocation().toVector());
                        } else {
                            // Player is selecting end block
                            Vector minVector = Vector.getMinimum(event.getClickedBlock().getLocation().toVector(), mp_playerStartVector.get(event.getPlayer()));
                            event.getPlayer().sendMessage("Min " + minVector.toString());
                            Vector maxVector = Vector.getMaximum(event.getClickedBlock().getLocation().toVector(), mp_playerStartVector.get(event.getPlayer()));
                            event.getPlayer().sendMessage("Max " + maxVector.toString());
                            Bukkit.getScheduler().scheduleAsyncDelayedTask(m_plugin, new BoxyThread(m_plugin, event.getPlayer().getWorld(), minVector, maxVector));
                            mp_playerStartVector.remove(event.getPlayer());
                        }
                    }
                } catch (NullPointerException e) {
                    player.sendMessage(ChatColor.RED + "This is NOT a valid Boxy position or block type!");
                }
            }
        }
    } // END onPlayerInteract()
}
