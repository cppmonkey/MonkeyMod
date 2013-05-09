package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BoxyCommand implements CommandExecutor {
	@SuppressWarnings("unused")
	private final MonkeyMod m_plugin;
	
	public BoxyCommand(MonkeyMod instance){
		m_plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		// TODO Auto-generated method stub
		return false;
	}

}
