package me.cppmonkey.monkeymod.commands;

import java.util.List;
import java.util.Locale;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author CppMonkey
 */
public class TeleCommand implements CommandExecutor {

    private MonkeyMod m_plugin;
	public final static String command = "tele";

    public TeleCommand(MonkeyMod instance) {
        m_plugin = instance;
    }

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
			List<Player> onPlayers = m_plugin.getServer().matchPlayer(args[0]);

			if(onPlayers.isEmpty()){
				player.sendMessage(ChatColor.GOLD + "Player not found.");
			} else if (player.getWorld().getName().equals(onPlayers.get(0).getWorld().getName())) {
								// Store current location, may be needed later
				String path = player.getName().toLowerCase(Locale.ENGLISH)+"."+player.getWorld().getName()+".last";
				m_plugin.getConfig().set(path,player.getLocation().toVector());

				// Now teleport the player
				player.teleport(onPlayers.get(0).getLocation());
                        } else {
                            player.sendMessage(ChatColor.GOLD + "Target player is in another world. Please go to the right world first.");
                        }
                    return true;
                }
        return false;
    }
}