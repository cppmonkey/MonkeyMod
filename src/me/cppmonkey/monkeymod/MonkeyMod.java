package me.cppmonkey.monkeymod;

import java.util.logging.Logger;

import me.cppmonkey.monkeymod.commands.MonkeyCommand;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MonkeyMod extends JavaPlugin{
	private final MonkeyModPlayerListener m_PlayerListener = new MonkeyModPlayerListener(this);
	private final MonkeyModBlockListener m_BlockListener = new MonkeyModBlockListener(this);
	
	private static final Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		log.info("Disabling MonkeyMod");
	}

	@Override
	public void onEnable() {
		
		PluginManager pm = getServer().getPluginManager();
		
		// TODO Auto-generated method stub
		log.info("Enabling MonkeyMod");
		
		//Register hooks to process events
		pm.registerEvent(Event.Type.PLAYER_JOIN, m_PlayerListener, Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, m_PlayerListener, Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, m_PlayerListener, Priority.Low, this);
		
		//Stop the burning!! 
		pm.registerEvent(Event.Type.BLOCK_IGNITE, m_BlockListener, Priority.Low, this);

		//TODO Process commands, these a partial commands!!
		
		getCommand("monkey").setExecutor(new MonkeyCommand(this));
		//getCommand("boxie").setExecutor(new MonkeyCommand(this));
		
		
		//Notify CppMonkey.NET of the new server
		// Notify server about new server
		
		String[] parms = {
	            "action=update",
	            "package=" + "monkeymod",
	            "version=" + "0.1",
	            "build=" + "1",
	            "rcon-port=" + "27075"
	        };
        HttpRequestThread notification = new HttpRequestThread(
                "Notification thread: Plugin initialized",
                "http://cppmonkey.net/minecraft/" + "update.php",
                parms, false);
        
        notification.start();
		
		//getCommand("debug");
	}

}
