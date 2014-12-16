package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.block.Block;

// @author Alex
public class ChestCommand implements CommandExecutor{
    private MonkeyMod m_plugin;
    private Configuration m_chestPermissions;

    public ChestCommand(MonkeyMod instance){
        m_plugin = instance;
        m_chestPermissions = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.CHESTS);
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            // Must be a player to use these commands
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (m_plugin.getPermition(player, ".isVip") || m_plugin.getPermition(player, ".isAdmin")) {
                    if (args.length == 4) {
                        return true;
                    } else {
                        player.sendMessage(ChatColor.GOLD + "You are not a VIP, so your chest is public");
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    public boolean ChestPlaced(Block block, Player player) {
        player.sendMessage("You placed a chest");
        // Why .getLocation().getX() when .getX() will do?
        String chestLocation = block.getLocation().getX() + "," + block.getLocation().getY()  + "," + block.getLocation().getZ();
        /* EXCEPTION
         * setProperty may take an object but 'player' can not be stored as a text file
         * What is the property your actually trying to store? and is it going to be case sensative?
         */
        m_chestPermissions.setProperty(chestLocation + ".owner", player);
        return true;
    }

    public boolean ChestAccessed(Block block, Player player) {
        // Why .getLocation().getX() when .getX() will do?
        String chestLocation = block.getLocation().getX() + "," + block.getLocation().getY()  + "," + block.getLocation().getZ();
        
        
        /* Use appropriate functions
         * getProperty() is all well a good, but when you know its going to be a string
         * Then why not use getString() ideally with a default value
         * Also how are you going to manage public chests here?
         */
        String chestOwner = m_chestPermissions.getProperty(chestLocation + ".owner").toString();
        /* WARNING ln 74 & 85
         * String compares don't work the same in java as they do in C++ the if will never evaluate to true
         * Because every variable within java by-ref. Its like comparing to pointers, you have to de-ref them to compare
         * In java you use a built in function to compare them instead. In this case equalsIgnoreCase() is the best choice
         * And just noticed. Don't use getDisplayName(), use getName() getDisplayName() include things like colour codes too
         * so it wont be consistant as playings gain and loose VIP or become mods...etc.
         */
        if (chestOwner == player.getDisplayName()) {
                return true;
            }
        player.sendMessage(ChatColor.RED + "You do not have permission to access this chest");
        player.sendMessage(ChatColor.RED + "contact " + chestOwner + " if you require access.");
        return false;
    }

    public boolean ChestDamage(Block block, Player player) {
        String chestLocation = block.getLocation().getX() + "," + block.getLocation().getY()  + "," + block.getLocation().getZ();
        String chestOwner = m_chestPermissions.getProperty(chestLocation + ".owner").toString();
            if(chestOwner == player.getDisplayName()){
                return true;
            }
        player.sendMessage(ChatColor.RED + "You do not have permission to destroy this chest");
        return false;
    }
}
