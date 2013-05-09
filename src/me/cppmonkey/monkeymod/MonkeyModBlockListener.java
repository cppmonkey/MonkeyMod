package me.cppmonkey.monkeymod;

import org.bukkit.entity.Player;
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
		
		Player player = (Player)event.getPlayer();
		
		player.sendMessage("This action currently isn't allowed on this server.");
		
		event.setCancelled(true);
	}
}
