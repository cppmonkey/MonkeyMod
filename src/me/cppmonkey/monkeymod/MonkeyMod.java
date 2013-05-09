package me.cppmonkey.monkeymod;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MonkeyMod extends JavaPlugin{
	private final MonkeyModPlayerListener m_PlayerListener = new MonkeyModPlayerListener(this);
	private final MonkeyModBlockListener m_BlockListener = new MonkeyModBlockListener(this);
	
	private PluginManager m_pm = getServer().getPluginManager();

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		
		//Register hooks to process events
		m_pm.registerEvent(Event.Type.PLAYER_LOGIN, m_PlayerListener, Priority.Low, this);
		m_pm.registerEvent(Event.Type.PLAYER_QUIT, m_PlayerListener, Priority.Low, this);
		m_pm.registerEvent(Event.Type.PLAYER_CHAT, m_PlayerListener, Priority.Low, this);
		
		//Stop the burning!! 
		m_pm.registerEvent(Event.Type.BLOCK_IGNITE, m_BlockListener, Priority.Low, this);

		//TODO Process commands, these a partial commands!!
		MonkeyCommand monkeyCommand = new MonkeyCommand(this);
		
		getCommand("monkey").setExecutor(monkeyCommand);
		getCommand("boxie").setExecutor(monkeyCommand);
		
		getCommand("debug");
	}

}
