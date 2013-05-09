package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.UpdateThread;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MonkeyCommand implements CommandExecutor {
	@SuppressWarnings("unused")
	private final MonkeyMod m_plugin;
	
	
	public MonkeyCommand(MonkeyMod instance){
		m_plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String lable, String[] args){
			

		//Todo process commands
		// Priority to /monkey update!
		if( "update".equalsIgnoreCase( args[0])){
			//player.sendMessage("Trying to update MonkeyMod");
			
			UpdateThread updateThread = new UpdateThread("Update", MonkeyMod.name, "http://cppmonkey.net/minecraft/");
            updateThread.start();
            
			return true;
		}
		return false;
	}
}
