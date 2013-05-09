package me.cppmonkey.monkeymod;

import java.net.URLEncoder;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MonkeyModPlayerListener extends PlayerListener {
	
	private final MonkeyMod m_plugin;
	
	public MonkeyModPlayerListener(MonkeyMod instance)
	{
		m_plugin = instance;
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event){
		//TODO Add :- reporting to cppmonkey.net
		Player player = event.getPlayer();
		
		String[] parms = {
                "action=connect",
                "player=" + player.getName(),
                "vip=false",
                "admin=false"
            };
        HttpRequestThread notification = new HttpRequestThread(
                "Connection Notification Thread:" + player.getName(),
                player,
                "http://cppmonkey.net/minecraft/" + "update.php",
                parms,
                false);
        
        notification.start();
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event){
		//Todo add :- reporting to cppmonkey.net
		Player player = event.getPlayer();
		
		String[] parms = {
                "action=disconnect",
                "player=" + player.getName()
            };
        HttpRequestThread notification = new HttpRequestThread(
                "Connection Notification Thread:" + player.getName(),
                player,
                "http://cppmonkey.net/minecraft/" + "update.php",
                parms,
                false);
        
        notification.start();
		
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event){
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		try{
			String parms[] = {
					"action=message",
	                "player=" + URLEncoder.encode(player.getName(), "UTF-8"),
	                "message=" + URLEncoder.encode(message, "UTF-8")
	            };
	            HttpRequestThread notification = new HttpRequestThread("Disconnection Notification Thread:" + player.getName(), player, "http://cppmonkey.net/minecraft/" + "update.php", parms, false);
	            
	            notification.start();
		}catch(Exception e){
			
		}
	}
}
