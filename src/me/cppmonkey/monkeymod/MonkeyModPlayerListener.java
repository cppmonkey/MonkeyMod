package me.cppmonkey.monkeymod;

import org.bukkit.event.player.PlayerListener;

public class MonkeyModPlayerListener extends PlayerListener {
	
	private final MonkeyMod m_plugin;
	
	public MonkeyModPlayerListener(MonkeyMod instance)
	{
		m_plugin = instance;
	}
}
