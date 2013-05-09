package me.cppmonkey.monkeymod;

import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;

public class MonkeyModBlockListener extends BlockListener {

	@SuppressWarnings("unused")
	private final MonkeyMod m_plugin;

	public MonkeyModBlockListener( MonkeyMod instance ){
		m_plugin = instance;
	}
	
	public void onBlockIgnite(BlockIgniteEvent event){
		//Todo, Allow or disallow action
	}
}
