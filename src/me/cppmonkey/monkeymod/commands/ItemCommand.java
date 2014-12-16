package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

public class ItemCommand implements CommandExecutor {

    private final MonkeyMod m_plugin;
    @Deprecated
    private final Configuration m_settings;

    public ItemCommand(MonkeyMod instance) {
        m_plugin = instance;
        m_settings = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PERMISSIONS);
    }

    /*
     * I Honserly don't know why you cast the int back to the string
     * It will need to be re-parsed...
     * re-use of variables good idea, just poor execution
     */
    @SuppressWarnings({ "static-access", "null", "unused" })
    private String itemName(String Item) {
        //returns the ID of an item / block from a given string
        //eg: input = CHEST
        //return = 54
        Material tempMaterial = null;
        tempMaterial.matchMaterial(Item);
        return Integer.toString(tempMaterial.getId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Has the sender provided arguments?
        if (args.length > 0) {

            // is the sender a player?
            if ((sender instanceof Player)) {

                Player player = (Player) sender;

                // Permission check.
                if (!m_plugin.getPermition(player, ".isVip") || !m_plugin.getPermition(player, ".isAdmin")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to do that");
                    return true;
                }


                try {

                    // Get Item from Name
                    String itemDetails[] = args[0].split(":");
                    Material itemMaterial = Material.matchMaterial(itemDetails[0]);

                    if (ItemRestricted(itemMaterial)) {
                        //Item is restricted
                        //TODO Allow exceptions to rule
                        player.sendMessage(ChatColor.RED + "This item is restricted");
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
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } /*END /item (player) */ else {
                //TODO Process Console /item Commands. Will require player name!
                sender.sendMessage("Error: the console cant do that yet!");
            }
        }

        return false;
    }

    private boolean ItemRestricted(Material item) {
        switch (item) {

            case BEDROCK:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
            case TNT:
            case FIRE:
            case MOB_SPAWNER:
            case FLINT_AND_STEEL:
                return true;
        }
        return false;
    }
}
