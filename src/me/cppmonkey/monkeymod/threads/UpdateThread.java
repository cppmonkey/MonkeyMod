package me.cppmonkey.monkeymod.threads;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.logging.Logger;
import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

/**
 *
 * @author CppMonkey
 */
public class UpdateThread extends Thread {
    protected static final Logger log = Logger.getLogger("Minecraft");

    public static String name = "Update Thread";
    public static String version = "1.3.1";
    private CommandSender m_ThreadOwner = null;
    private String m_PackageName;
    private String m_ReposUrl;
    private Boolean m_debug = false;
    private MonkeyMod m_plugin;

    /**
     * @param args the command line arguments
     */
    public UpdateThread(String id, CommandSender player, String packageName, String respoUrl, MonkeyMod plugin) {
        super(id);
        m_plugin = plugin;
        m_ThreadOwner = player;
        m_PackageName = packageName;
        m_ReposUrl = respoUrl;
    }

    @Deprecated
    public UpdateThread(String id, String packageName, String respoUrl) {
        super(id);

        m_PackageName = packageName;
        m_ReposUrl = respoUrl;
    }
    
    private void Message(String msg) {
        if (m_ThreadOwner != null) {
            m_ThreadOwner.sendMessage(msg);
        } else {
            log.info(msg);
        }
    }

    @Override
    public void run() {
        String msg = "";

        try {

            msg = "Attempting to download " + m_ReposUrl + m_PackageName + ".jar";

            Message(ChatColor.GREEN + msg);
                
            URL url = new URL(m_ReposUrl + m_PackageName + ".jar");
            if (m_debug) {
                String hostAddr = InetAddress.getByName(url.getHost()).getHostAddress();
                Message(hostAddr);
            }
            
            InputStream is = url.openStream();

            OutputStream os = new FileOutputStream("plugins//" + m_PackageName + ".jar");

            int data = is.read();

            while (data != -1) {
                os.write(data);
                data = is.read();
            }
            os.close();
            is.close();
            
            try {
            	// FIXME re-write auto reload, plugin only though, currently not possible
            	m_plugin.getServer().reload();
                Message(ChatColor.GREEN + "Update complete!");
            } catch (CommandException e) {
                Message(ChatColor.RED + "Something went wrong whilst updaing");
                Message(ChatColor.RED + e.getMessage());
            }

        } catch (Exception e) {
            msg = "Sorry master something went wrong!";
            Message(ChatColor.RED + msg);

            msg = e.getMessage();
            Message(ChatColor.RED + msg);
        }
    }
}
