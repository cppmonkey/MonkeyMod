package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class BoxyCommand implements CommandExecutor {

	private final MonkeyMod m_plugin;
    @Deprecated
    private final Configuration m_settings;
	
    public BoxyCommand(MonkeyMod instance) {
		m_plugin = instance;
        m_settings = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.BOXY);
	}

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /* TODO process commands
		 * 	/boxy [id/name] [id/name] [height] [step]
		 *  /boxy h:[height]
		 *  /boxy s:[stepping]
		 *  /boxy e:[exclude csv of id/name]
		 *  /boxy undo (even possible?) 
		 */

        // Must be a player to use these commands
        if (sender instanceof Player){
            Player player = (Player)sender;

            m_settings.setProperty(player.getName().toLowerCase()+".fromId", 0);
            m_settings.setProperty(player.getName().toLowerCase()+".toId", 1);
            m_settings.setProperty(player.getName().toLowerCase()+".step", 1);
            m_settings.setProperty(player.getName().toLowerCase()+".height", 1);
        }

        //TODO Undo capabilities for Console?

		sender.sendMessage(ChatColor.RED + "Not implimented yet");
		return false;
	}
}
