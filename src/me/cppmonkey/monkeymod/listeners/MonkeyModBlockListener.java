package me.cppmonkey.monkeymod.listeners;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.commands.ChestCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.config.Configuration;

public class MonkeyModBlockListener extends BlockListener {

    private final MonkeyMod m_plugin;
    private Configuration m_chestPermissions;
    // FIXME I am a member variable, show that I am!
    private final ChestCommand ChestExec;

    public MonkeyModBlockListener(MonkeyMod instance) {
        m_plugin = instance;
        /* I'll be honest with you, I don't understand why you create a new CommandListener JUST to access functions
         * that otherwise wouldn't need to be needed within the ChestCommand. Wouldn't it be a better idea to create an
         * entirely new listener to capture the chest listen functions? Then we'd have encapsulation just like your
         * trying to achive in a rather bizare manner
         * 
         * If you REALLY need access to the Command add it to the perramiter list, and pass it in rather than creating a new one here
         */

        ChestExec = new ChestCommand(m_plugin);
    }

    public void onEnable() {
        m_chestPermissions = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.CHESTS);
    }

    public void onBlockIgnite(BlockIgniteEvent event) {
        //TODO Add exceptions to burning

        Player player = event.getPlayer();

        // is player?
        if (player != null) {
            if (m_plugin.getPermition(player, ".canIgnite")) {
                //CAN BURN!!!
                return; // nothing else to do here
            } else {
                //Not allowed to burn
                player.sendMessage(ChatColor.RED + "You dont have permission to ignite");

                event.setCancelled(true);
                return;
            }
        } else if (m_plugin.getConfiguration().getBoolean("protection.fire", false)) // Is environment caused burning allowed? Can't be first otherwise players wouldn't be able to place fire at all
        {
            // cancel environment based fire
            event.setCancelled(true);
            return;
        }
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            if (m_plugin.getPermition(player, ".canBuild")) {

                // Is the item being place a chest?
                if (event.getBlockPlaced().getType() == Material.CHEST) {
                    ChestExec.ChestPlaced(event.getBlock(), event.getPlayer());
                    player.sendMessage("You placed a chest");
                    String chestLocation = event.getBlockPlaced().getLocation().getX() + "," + event.getBlockPlaced().getLocation().getY() + "," + event.getBlockPlaced().getLocation().getZ();

                    m_chestPermissions.setProperty(chestLocation + ".owner", player);

                    return;
                }
                // nothing to do
                return;
            } else {
                player.sendMessage(ChatColor.RED + "You don't have pemission to build");
                event.setCancelled(true);
                return;
            }
        }
    }

    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();

        //return is not a player
        if (player == null) {
            return;
        }
            //player.sendMessage(ChatColor.YELLOW + "onBlockDamage");

        // Can the player build?
        if (!m_plugin.getPermition(player, ".canBuild")) {
            player.sendMessage(ChatColor.RED + "You don't have pemission to destroy");
            event.setCancelled(true);
            return;
        } else if (event.getBlock().getType() == Material.CHEST) {
            if (ChestExec.ChestDamage(event.getBlock(), event.getPlayer()) == false) {
                    player.sendMessage(ChatColor.RED + "You don't have pemission to destroy");
                    event.setCancelled(true);
                    return;
                }
            }
    }

    public void onBlockCanBuild(BlockCanBuildEvent event) {
        System.out.println("onBlockCanBuild");
    }

    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (player != null) {
            //player.sendMessage(ChatColor.YELLOW + "onBlockDamage");
        }
    }
}
