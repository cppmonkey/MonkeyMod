package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCommand implements CommandExecutor{
	@SuppressWarnings("unused")
	private final MonkeyMod m_plugin;
	
	public ItemCommand(MonkeyMod instance){
		m_plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Has the sender provided arguments?
		if (args.length > 0) {
			
			// is the sender a player?
			if ((sender instanceof Player)){
			
				Player player = (Player)sender;
				
				// TODO Permission check. Op only command for now
				if (!player.isOp()) {
					player.sendMessage(ChatColor.RED + "You do not have permission to do that");
					return true;
				}
					
				
				try{
					// Create stack of items from Supplied ID. Maybe unhandled exception from invalid IDs
					ItemStack item = new ItemStack( Integer.parseInt(args[0]));
					
					if (args.length == 2)
						// Parse quantity if specified else set it to 1
						item.setAmount( Integer.parseInt(args[1]));
					else
						item.setAmount(1);
		
					player.getInventory().addItem(item);
					player.sendMessage(ChatColor.GREEN + "There you go " + player.getName());
					return true;
				}catch(NumberFormatException ex){
					// Report an error if text entered is invalid
					sender.sendMessage(ChatColor.RED + "Invalid argument value");
					sender.sendMessage( ex.getMessage());
					return false;
				}
			} /*END /item (player) */ else {
				//TODO Process Console /item Commands. Will require player name! 
				sender.sendMessage("Error: the console cant do that yet!");
			}
		}
		
		return false;
	}

}
