package me.cppmonkey.monkeymod.http.callbacks;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.IThreadCallback;
import me.cppmonkey.monkeymod.player.PlayerDetails;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnPlayerLogin implements IThreadCallback {

    private MonkeyMod m_plugin;
    CommandSender m_owner;

    public OnPlayerLogin(MonkeyMod instance, CommandSender owner) {
        m_plugin = instance;
        m_owner = owner;
    }

    /*
     * TODO Create a login notification details to be submitted to web server
     */
    public static Runnable newPlayerLogin() {

        return null;
    }

    public void processLine(String result) {
        try {
            if (m_owner instanceof Player && result != null && result.length() != 0) {
                Player player = (Player) m_owner;

                PlayerDetails playerDetails = m_plugin.getPlayerDetails(player);

                result = result.trim();
                String split[] = result.split(":");

                if (split.length == 2) {

                    if(playerDetails != null){

                    if ("isOp".equalsIgnoreCase(split[0])) {
                        m_owner.setOp(split[1].equalsIgnoreCase("true"));
                        }else if (split[0].equalsIgnoreCase("canBuild")) {
                            playerDetails.setCanBuild(split[1].equalsIgnoreCase("true"));
                        }else if (split[0].equalsIgnoreCase("canIgnite")) {
                            playerDetails.setCanIgnite(split[1].equalsIgnoreCase("true"));
                        }else if (split[0].equalsIgnoreCase("isVip")) {
                            playerDetails.setIsVip( split[1].equalsIgnoreCase("true"));
                        }else if (split[0].equalsIgnoreCase("isAdmin")) {
                            playerDetails.setIsAdmin(split[1].equalsIgnoreCase("true"));
                        }
                    } else if (split[0].equalsIgnoreCase("playerUID")) {
                        playerDetails = new PlayerDetails(Integer.parseInt(split[1]));
                        m_plugin.addPlayerDetails(player, playerDetails);
                    } else {
                        // TODO Report issue
                    }

                    message(result);
                    }
                }

        }catch (RuntimeException rex){
            MonkeyMod.reportException("RuntimeExcption within LoginCallback.processLine()", rex);
        } catch (Exception ex) {
            MonkeyMod.reportException("Exception within LoginCallback.processLine()", ex);
        }
    }

    public void complete() {
        message(ChatColor.GREEN + "Login Complete");
    }

    private void message(String msg) {
        m_owner.sendMessage(msg);
    }
}
