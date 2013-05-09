package me.cppmonkey.monkeymod;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

public class MonkeyModPlayerListener extends PlayerListener {
	
	private final MonkeyMod m_plugin;
	
	public MonkeyModPlayerListener(MonkeyMod instance)
	{
		m_plugin = instance;
	}
	
	@Override
	public void onPlayerJoin(PlayerEvent event){
		//TODO Add :- reporting to cppmonkey.net
	}
	
	@Override
	public void onPlayerQuit(PlayerEvent event){
		//Todo add :- reporting to cppmonkey.net
	}
}
