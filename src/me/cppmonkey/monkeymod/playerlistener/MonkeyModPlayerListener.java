package me.cppmonkey.monkeymod.playerlistener;

import java.net.URLEncoder;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MonkeyModPlayerListener extends PlayerListener {
	
	@SuppressWarnings("unused")
	private final MonkeyMod m_plugin;
	
	public MonkeyModPlayerListener(MonkeyMod instance)
	{
		m_plugin = instance;
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event){
		//TODO Add :- reporting to cppmonkey.net
		Player player = event.getPlayer();
		
		// setting up parms for http request
		String[] parms = {
                "action=connect",
                "player=" + player.getName(),
                "vip=false", /* TODO process VIP status */
                "admin=" + player.isOp() /* TODO process Op/Admin status*/
            };
        HttpRequestThread notification = new HttpRequestThread(
                "Connection Notification Thread:" + player.getName(),
                player,
                m_plugin.getLoggerUrl(),
                parms,
                false);
        
        notification.start();
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event){
		
		Player player = event.getPlayer();
		//reporting to cppmonkey.net
		String[] parms = {
                "action=disconnect",
                "player=" + player.getName()
            };
        HttpRequestThread notification = new HttpRequestThread(
                "Connection Notification Thread:" + player.getName(),
                player,
                m_plugin.getLoggerUrl(),
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
	            HttpRequestThread notification = new HttpRequestThread(
	            		"Disconnection Notification Thread:" + player.getName(),
	            		player,
	            		m_plugin.getLoggerUrl(),
	            		parms);
	            
	            notification.start();
		}catch(Exception e){
			
		}
	}
}
