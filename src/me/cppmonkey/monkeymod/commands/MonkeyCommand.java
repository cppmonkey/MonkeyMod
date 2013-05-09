package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MonkeyCommand implements CommandExecutor {
	private final MonkeyMod m_plugin;
	
	
	public MonkeyCommand(MonkeyMod instance){
		m_plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		//TODO process commands
		// Priority to /monkey update!
		if (args.length > 0) {
			if ("update".equalsIgnoreCase( args[0])) {
				//Let the user know what's going on		
				m_plugin.selfUpdate(sender);            
				return true;
			} //END /monkey update
			
			// force update check
			if ("uptodate".equalsIgnoreCase(args[0])){
				// Setting up parms for http update query
				String[] parms = {
						"name=" + m_plugin.getName(),
						"version=" + m_plugin.getVersion(),
						"build=" + m_plugin.getBuild()
				};
				
				// Create http request thread 
				HttpRequestThread updateQuery = new HttpRequestThread("uptodate", sender, "http://cppmonkey.net/monkeymod/ajax.php", parms, m_plugin );
				// Start the thread
				updateQuery.start();
				
				return true;
			}  //END /monkey uptodate
			
			// process /monkey version
			if ("version".equalsIgnoreCase(args[0])){
				//TODO process action
				sender.sendMessage(ChatColor.RED + "Action still to do!");
				return true;
			} //END /monkey version
			
			if ("status".equalsIgnoreCase(args[0])){
				// TODO process status commands
				sender.sendMessage(ChatColor.RED + "Action still to do!");
				return true;
			} //END /monkey status
			
			if ("setvar".equalsIgnoreCase(args[0])){
				// TODO process setvar commands
				sender.sendMessage(ChatColor.RED + "Action still to do!");
				return true;
			} //END /monkey setvar
			
			
			if ("enable".equalsIgnoreCase(args[0])||"disable".equalsIgnoreCase(args[0])){
				// TODO process enable disable commands
				sender.sendMessage(ChatColor.RED + "Action still to do!");
				return true;
			} //END /monkey [enable/disable]
		}// END args > 0
		return false;
	}
}
