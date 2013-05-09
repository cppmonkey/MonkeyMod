package me.cppmonkey.monkeymod;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MonkeyCommand implements CommandExecutor {
	private final MonkeyMod m_plugin;
	
	
	public MonkeyCommand(MonkeyMod instance){
		m_plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String lable, String[] args){
		if (!(sender instanceof Player)){
			return false;
		}
		//Todo process commands
		// Priority to /monkey update!
		return false;
	}
}
