package me.cppmonkey.monkeymod.listeners;

import java.util.HashMap;
import java.util.Locale;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.boxy.BoxyThread;
import me.cppmonkey.monkeymod.player.PlayerDetails;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class MonkeyModPlayerListener implements Listener {

    private MonkeyMod m_plugin;

    public MonkeyModPlayerListener(MonkeyMod instance) {
        m_plugin = instance;
    }

    private HashMap<Player,Vector> mp_playerStartVector = new HashMap<Player,Vector>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerDetails playerDetails = m_plugin.getPlayerDetails(player);
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && m_plugin.getConfig().getBoolean(player.getName().toLowerCase(Locale.ENGLISH) + ".enabled", false) && m_plugin.getConfig().getInt("boxy.tool") == player.getItemInHand().getTypeId()) {
            MonkeyMod.log.info(event.getPlayer().getName() + " is trying to use boxy");
            if (!playerDetails.isVip() && !playerDetails.isAdmin()) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use Boxy");
            } else if (!mp_playerStartVector.containsKey(event.getPlayer())) {
                mp_playerStartVector.put(event.getPlayer(), event.getClickedBlock().getLocation().toVector());
            } else {
                // Player is selecting end block
                Vector minVector = Vector.getMinimum(event.getClickedBlock().getLocation().toVector(), mp_playerStartVector.get(event.getPlayer()));
                event.getPlayer().sendMessage("Min " + minVector.toString());
                Vector maxVector = Vector.getMaximum(event.getClickedBlock().getLocation().toVector(), mp_playerStartVector.get(event.getPlayer()));
                event.getPlayer().sendMessage("Max " + maxVector.toString());
                Bukkit.getScheduler().scheduleSyncDelayedTask(m_plugin, new BoxyThread(m_plugin, event.getPlayer().getWorld(), minVector, maxVector));
                mp_playerStartVector.remove(event.getPlayer());
            }
        }
    } // END onPlayerInteract()
}
