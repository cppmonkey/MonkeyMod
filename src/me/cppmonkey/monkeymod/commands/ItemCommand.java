package me.cppmonkey.monkeymod.commands;

import java.util.HashMap;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.utils.Parm;

public class ItemCommand implements CommandExecutor {

    public final static String command = "item";
    private MonkeyMod m_plugin;

    public ItemCommand(MonkeyMod instance) {
        m_plugin = instance;

        // TODO Should acquire restricted items from web service.

        m_plugin.canSpawn.put(Material.BEDROCK, true);
        m_plugin.canSpawn.put(Material.WATER, true);
        m_plugin.canSpawn.put(Material.STATIONARY_WATER, true);
        m_plugin.canSpawn.put(Material.LAVA, true);
        m_plugin.canSpawn.put(Material.STATIONARY_LAVA, true);
        m_plugin.canSpawn.put(Material.TNT, true);
        m_plugin.canSpawn.put(Material.FIRE, true);
        m_plugin.canSpawn.put(Material.MOB_SPAWNER, true);
        m_plugin.canSpawn.put(Material.FLINT_AND_STEEL, true);
        m_plugin.canSpawn.put(Material.LAVA_BUCKET, true);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Has the sender provided arguments?
        if (args.length > 0) {

            // is the sender a player?
            if (sender instanceof Player) {

                Player player = (Player) sender;

                // Permission check.
                if (!m_plugin.getPermition(player, ".isVip") && !m_plugin.getPermition(player, ".isAdmin")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to spawn items");
                    MonkeyMod.log.info(player.getName() + " isVip " + m_plugin.getPermition(player, ".isVip"));
                    MonkeyMod.log.info(player.getName() + " isAdmin " + m_plugin.getPermition(player, ".isAdmin"));
                    return true;
                }

                try {

                    // Get Item from Name
                    String itemDetails[] = args[0].split(":");
                    Material itemMaterial = Material.matchMaterial(itemDetails[0]);

                    if (itemRestricted(itemMaterial)) {
                        // Item is restricted
                        // Allow exceptions to rule
                        if (m_plugin.getPermition(player, ".isAdmin")) {
                            return false;
                        }
                        player.sendMessage(ChatColor.RED + "This item is restricted");
                        Parm[] parms = {
                            new Parm("action", "restricted-item-attempt"),
                            new Parm("player_id", m_plugin.getPlayerUID(player)),
                            new Parm("server_uid", m_plugin.getServerUID()),
                            new Parm("data",itemMaterial.name())
                        };
                        HttpRequestThread notification = new HttpRequestThread(
                            "Connection Notification Thread:" + player.getName(),
                            m_plugin.getLoggerUrl(),
                            parms);
                        notification.setPriority(Thread.MIN_PRIORITY);
                        notification.start();
                        return true;
                    }

                    // Material wasn't found trying again
                    if (itemMaterial == null) {
                        player.sendMessage(ChatColor.RED + "Item not found");
                        return true;
                    }

                    //Create stack for item. Size is 0 to begin with!

                    short durability = 0;
                    if (itemDetails.length == 2) {
                        durability = (short) Integer.parseInt(itemDetails[1]);
                    }

                    int quantity = 1;
                    if (args.length == 2) // Parse quantity if specified else set it to 1
                    {
                        quantity = Integer.parseInt(args[1]);
                        // If invalid it will be handles further down. no item will be produced
                    }

                    // Collected all variables require
                    ItemStack item = new ItemStack(itemMaterial, quantity, durability);


                    player.getInventory().addItem(item);
                    player.sendMessage(ChatColor.GREEN + "There you go " + player.getName());
                    return true;
                } catch (NumberFormatException ex) {
                    // Report an error if text entered is invalid
                    sender.sendMessage(ChatColor.RED + "Invalid argument value");
                    sender.sendMessage(ex.getMessage());
                    return false;
                } catch (NullPointerException e) {
                    MonkeyMod.reportException(e.getMessage(),e);
                }catch (RuntimeException rex){
                    MonkeyMod.reportException("RuntimeExcption within ItemCommand.onCommand()", rex);
                } catch (Exception ex) {
                    MonkeyMod.reportException("Excption within ItemCommand.onCommand()", ex);
                }
            } /*END /item (player) */ else {
                //TODO Process Console /item Commands. Will require player name!
                sender.sendMessage("Error: the console cant do that yet!");
            }
        }

        return false;
    }

    private boolean itemRestricted(Material item) {
        if(m_plugin.canSpawn.containsKey(item)){
            return m_plugin.canSpawn.get(item);
        }
        return false;
    }
}
