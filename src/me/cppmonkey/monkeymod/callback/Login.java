package me.cppmonkey.monkeymod.callback;

import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.IThreadCallback;

public class Login implements IThreadCallback {

    private MonkeyMod m_plugin;
    CommandSender m_owner;

    public Login(MonkeyMod instance, CommandSender owner) {
        m_plugin = instance;
        m_owner = owner;
    }

    public void processLine(String result) {
        try {
            if (m_owner instanceof Player && result != null && !result.isEmpty()) {
                Player player = (Player) m_owner;

                String booleanValues[] = {
                        "canBuild", "isVip", "canIgnite", "isAdmin"
                };

                result = result.trim();
                String split[] = result.split(":");

                if (split.length == 2) {

                    if ("isOp".equalsIgnoreCase(split[0])) {
                        m_owner.setOp(split[1].equalsIgnoreCase("true"));
                        return;
                    }

                    if (split[0].equalsIgnoreCase("canBuild")) {
                        m_plugin.canBuild.put((Player) m_owner, split[1].equalsIgnoreCase("true"));
                        return;
                    }

                    if (split[0].equalsIgnoreCase("canIgnite")) {
                        m_plugin.canIgnite.put((Player) m_owner, split[1].equalsIgnoreCase("true"));
                        return;
                    }

                    if (split[0].equalsIgnoreCase("isVip")) {
                        m_plugin.isVip.put((Player) m_owner, split[1].equalsIgnoreCase("true"));
                        return;
                    }

                    if (split[0].equalsIgnoreCase("isAdmin")) {
                        m_plugin.isAdmin.put((Player) m_owner, split[1].equalsIgnoreCase("true"));
                        return;
                    }
                    
                    if (split[0].equalsIgnoreCase("playerUID")) {
                        m_plugin.playerUIDs.put(player, Integer.parseInt(split[1]));
                        return;
                    }

                    for (int i = 0; i < booleanValues.length; i++) {
                        if (split[0].equalsIgnoreCase(booleanValues[i])) {
                            m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + "." + booleanValues[i], split[1].equalsIgnoreCase("true"));
                            return;
                        }
                    }
                }

                message(result);
            }
        } catch (Exception e) {
            message("Excetion within LoginCallback.java");
        }
    }

    public void complete() {
        message(ChatColor.GREEN + "Login Complete");
    }

    private void message(String msg) {
        m_owner.sendMessage(msg);
    }
}
