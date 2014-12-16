package me.cppmonkey.monkeymod.callback;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.IThreadCallback;

public class LoginCallback implements IThreadCallback {
	
	private MonkeyMod m_plugin;
	CommandSender m_owner;
	Player m_player = null;
	boolean configDirty = false;
	
	public LoginCallback( MonkeyMod instance, CommandSender owner ){
		m_plugin = instance;
		m_owner = owner;
		if( owner instanceof Player )
			m_player = (Player)owner;
	}

	public void processLine(String result) {
		
		
		// TODO Auto-generated method stub
		if(!result.equals(null) || !result.isEmpty()){
			
			String booleanValues[] = {
				"isAdmin",
				"isVip",
				"canBuild",
				"canIgnite"
			};
			
			result = result.trim();
			String split[] = result.split(":");
			
			if (split.length == 2){
				for ( int i = 0; i < booleanValues.length; i++ ){
					if (split[0].equalsIgnoreCase(booleanValues[i])) {
						m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PERMISSIONS).setProperty(
								m_player.getName().toLowerCase()+"."+booleanValues[i],
								split[1].equalsIgnoreCase("true"));
						configDirty = true;
						return;
					}
				}
			}
			
			Message(result);
		}
	}

	public void complete() {
		if (configDirty)
			m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PERMISSIONS).save();
		
		Message(ChatColor.GREEN + "Login Complete");
	}
	

	private void Message(String msg) {
        m_owner.sendMessage(msg);
    }

}
