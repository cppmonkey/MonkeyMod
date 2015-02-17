package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.player.PlayerDetails;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCommand implements CommandExecutor {

    public final static String command = "item";
    private MonkeyMod m_plugin;

    public ItemCommand(MonkeyMod instance) {
        m_plugin = instance;

        // TODO Should acquire restricted items from web service.
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Has the sender provided arguments?
        if (args.length > 0) {

            // is the sender a player?
            if (sender instanceof Player) {

                Player player = (Player) sender;
                PlayerDetails playerDetails = m_plugin.getPlayerDetails(player);

                // Permission check.
                if (!playerDetails.isVip() && !playerDetails.isAdmin()) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to spawn items");
                    MonkeyMod.log.info(player.getName() + " isVip " + playerDetails.isVip());
                    MonkeyMod.log.info(player.getName() + " isAdmin " + playerDetails.isAdmin());
                    return true;
                }

                try {

                    // Get Item from Name
                    String itemDetails[] = args[0].split(":");
                    Material itemMaterial = Material.matchMaterial(itemDetails[0]);

                    // Material wasn't found trying again
                    if (itemMaterial == null) {
                        player.sendMessage(ChatColor.RED + "Item not found");
                        return true;
                    }

                    if (playerDetails.cantPlace(itemMaterial) && !playerDetails.isAdmin()) {
                        // Item is restricted
                        // Allow exceptions to rule
                        player.sendMessage(ChatColor.RED + "This item is restricted");
                        return true;
                    }

                    // Create stack for item. Size is 0 to begin with!
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
                    MonkeyMod.reportException(e.getMessage(), e);
                } catch (RuntimeException rex) {
                    MonkeyMod.reportException("RuntimeExcption within ItemCommand.onCommand()", rex);
                } catch (Exception ex) {
                    MonkeyMod.reportException("Exception within ItemCommand.onCommand()", ex);
                }
            } /* END /item (player) */else {
                // TODO Process Console /item Commands. Will require player name!
                sender.sendMessage("Error: the console cant do that yet!");
            }
        }
        return false;
    }
}
