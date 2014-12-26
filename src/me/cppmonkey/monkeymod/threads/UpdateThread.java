package me.cppmonkey.monkeymod.threads;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author CppMonkey
 */
public class UpdateThread extends Thread {

    public final static String name = "Update Thread";
    public final static String version = "1.3.1";
    private CommandSender m_ThreadOwner = null;
    private String m_PackageName;
    private String m_ReposUrl;
    private Boolean m_debug = false;
    private Boolean m_isPlugin = true;
    private MonkeyMod m_plugin;

    /**
     * @param args
     *            the command line arguments
     */
    public UpdateThread(String id, CommandSender player, String packageName, String respoUrl, MonkeyMod plugin) {
        super(id);
        m_plugin = plugin;
        m_ThreadOwner = player;
        m_PackageName = packageName;
        m_ReposUrl = respoUrl;
    }

    public UpdateThread(String id, CommandSender player, String packageName, String respoUrl, Boolean isPlugin, MonkeyMod plugin) {
        super(id);
        m_plugin = plugin;
        m_ThreadOwner = player;
        m_PackageName = packageName;
        m_ReposUrl = respoUrl;
        m_isPlugin = isPlugin;
    }

    @Deprecated
    public UpdateThread(String id, String packageName, String respoUrl) {
        super(id);

        m_PackageName = packageName;
        m_ReposUrl = respoUrl;
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

            msg = "Attempting to download " + m_ReposUrl + m_PackageName + ".jar";

            message(ChatColor.GREEN + msg);
                
            URL url = new URL(m_ReposUrl + m_PackageName + ".jar");
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

                urlConn.setRequestMethod("GET");

                urlConn.connect();

                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    url = new URL(urlConn.getHeaderField("Location"));
                    urlConn.disconnect();
                }
            }

            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED || urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream is = urlConn.getInputStream();
                OutputStream os = null;
                try {
                    if (m_isPlugin) {
                        os = new FileOutputStream("plugins//" + m_PackageName + ".jar");
                    } else {
                        os = new FileOutputStream(m_PackageName + ".jar");
                    }
                    
                    int data = is.read();
                    
                    while (data != -1) {
                       os.write(data);
                       data = is.read();
                    }
                } catch (FileNotFoundException e) {
                    MonkeyMod.reportException("FileNotFoundException within UpdateThread.java",e);
                } finally {
                    if (os != null) {
                        os.close();
                    }
                }
                is.close();
                
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
                }
            } else {
                MonkeyMod.log.severe("Http request failed (" + urlConn.getURL() + ")");
                MonkeyMod.log.severe("Server response to request - " + urlConn.getResponseCode());
            }

        } catch (IOException e) {
            msg = "Sorry master, something went wrong!";
            message(ChatColor.RED + msg);

            msg = e.getMessage();
            message(ChatColor.RED + msg);
        }
    }
}
