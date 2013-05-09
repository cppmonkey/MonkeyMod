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
		// TODO Auto-generated method stub
		if (args.length > 0) {
			
			if (!(sender instanceof Player)){
			
				Player player = (Player)sender;
				
				try{
					ItemStack item = new ItemStack( Integer.parseInt(args[0]));
					if (args.length == 2)
						item.setAmount( Integer.parseInt(args[1]));
					else
						item.setAmount(1);
		
					player.getInventory().addItem(item);
					player.sendMessage(ChatColor.GREEN + "There you go " + player.getName());
					return true;
				}catch(NumberFormatException ex){
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
