package me.cppmonkey.monkeymod.threads;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

/**
 *
 * @author caboose89
 */
public class LoggingThread extends Thread {

    public final static String name = "Logging Thread";
    public final static String version = "1.3.1";
    private CommandSender m_ThreadOwner = null;
    private String m_PackageName;
    private String m_PostUrl;
    private Boolean m_debug = false;
    private Boolean m_isPlugin = true;
    private MonkeyMod m_plugin;

    public LoggingThread(String id, CommandSender player, String packageName, String postUrl, Boolean isPlugin, MonkeyMod plugin) {
        super(id);
        m_plugin = plugin;
        m_ThreadOwner = player;
        m_PackageName = packageName;
        m_PostUrl = postUrl;
        m_isPlugin = isPlugin;
    }

    private void message(String msg) {
        if (m_ThreadOwner != null) {
            m_ThreadOwner.sendMessage(msg);
        } else {
            MonkeyMod.log.info(msg);
        }
    }

    public void run() {
        String msg = "";

        try {

            msg = "Attempting to POST ... ";

            message(ChatColor.GREEN + msg);

            URL url = new URL(m_PostUrl);
            if (m_debug) {
                String hostAddr = InetAddress.getByName(url.getHost()).getHostAddress();
                message(hostAddr);
            }

            if (!HttpURLConnection.getFollowRedirects()) {
                MonkeyMod.log.warning("HTTP Redirections are not allowed");
            }

            HttpURLConnection urlConn = null;

            while (urlConn == null || urlConn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {

                urlConn = (HttpURLConnection) url.openConnection();

                urlConn.setRequestMethod("SET");

                urlConn.connect();

                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    url = new URL(urlConn.getHeaderField("Location"));
                    urlConn.disconnect();
                }
            }

            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_NOT_IMPLEMENTED) /* Used as a unique code to say that it is the right location. Could do to be changed to check page content instead */ {
                try {
                    
                } catch (Exception e) {
                    MonkeyMod.log.severe("Http post failed (" + urlConn.getURL() + ")");
                    MonkeyMod.log.severe("Server response to request - " + urlConn.getResponseCode());
                }
                
                
                /*

                try {
                    if (m_isPlugin) {
                        m_plugin.getServer().reload();
                    } else {
                        m_plugin.getServer().dispatchCommand(m_ThreadOwner, "stop");
                    }
                    message(ChatColor.GREEN + "Update complete!");
                } catch (CommandException e) {
                    message(ChatColor.RED + "Something went wrong whilst updaing");
                    message(ChatColor.RED + e.getMessage());
                }*/
            } else {
                MonkeyMod.log.severe("Http request failed (" + urlConn.getURL() + ")");
                MonkeyMod.log.severe("Server response to request - " + urlConn.getResponseCode());
            }

        } catch (IOException e) {
            msg = "Sorry master something went wrong!";
            message(ChatColor.RED + msg);

            msg = e.getMessage();
            message(ChatColor.RED + msg);
        }
    }
}
