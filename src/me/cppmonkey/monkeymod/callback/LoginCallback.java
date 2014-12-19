package me.cppmonkey.monkeymod.callback;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.IThreadCallback;

public class LoginCallback implements IThreadCallback {
	
	private MonkeyMod m_plugin;
	CommandSender m_owner;
	
    public LoginCallback(MonkeyMod instance, CommandSender owner) {
		m_plugin = instance;
		m_owner = owner;
	}

	public void processLine(String result) {
		
        if (m_owner instanceof Player && result != null && !result.isEmpty()) {
            Player player = (Player) m_owner;
			
            String booleanValues[] = {"canBuild", "isVip", "canIgnite", "isAdmin"};
			
			result = result.trim();
			String split[] = result.split(":");
			
            if (split.length == 2) {
            	
                if ("isOp".equalsIgnoreCase(split[0])) {
                    // TODO make user Op
            		return;
            	}
            	
                for (int i = 0; i < booleanValues.length; i++) {
					if (split[0].equalsIgnoreCase(booleanValues[i])) {
                        m_plugin.getPluginConfiguration(MonkeyMod.EConfig.PERMISSIONS).setProperty(player.getName().toLowerCase() + "." + booleanValues[i], split[1].equalsIgnoreCase("true"));
						return;
					}
				}
			}
			
            message(result);
		}
	}

	public void complete() {
        message(ChatColor.GREEN + "Login Complete");
    }

    private void message(String msg) {
        m_owner.sendMessage(msg);
    }
}
