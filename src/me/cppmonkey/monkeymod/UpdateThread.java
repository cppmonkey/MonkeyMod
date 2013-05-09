package me.cppmonkey.monkeymod;
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

import org.bukkit.entity.Player;

/**
 *
 * @author CppMonkey
 */
public class UpdateThread extends Thread {
    protected static final Logger log = Logger.getLogger("Minecraft");

    public static String name = "Update Thread";
    public static String version = "1.3.1";
    private Player m_ThreadOwner = null;
    private String m_PackageName;
    private String m_ReposUrl;
    private Boolean m_debug = false;
    

    /**
     * @param args the command line arguments
     */
    public UpdateThread(String id, Player player, String packageName, String respoUrl) {
        super(id);

        m_ThreadOwner = player;
        m_PackageName = packageName;
        m_ReposUrl = respoUrl;
    }

    public UpdateThread(String id, String packageName, String respoUrl) {
        super(id);

        m_PackageName = packageName;
        m_ReposUrl = respoUrl;
    }
    
    private void Message( String msg ){
        if( m_ThreadOwner != null ){
            m_ThreadOwner.sendMessage( msg );
        }else{
            log.info( msg );
        }
    }

    public void run() {
        String msg = "";

        try {

            msg = "Attempting to download " + m_ReposUrl + m_PackageName + ".jar";

            Message( msg );
                
            URL url = new URL(m_ReposUrl + m_PackageName + ".jar");
            if( m_debug ){
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

            msg = "reloadplugin " + m_PackageName;

            if (m_ThreadOwner != null) {
                m_ThreadOwner.performCommand("/" + msg);
            } else if (log != null) {
                log.info(msg);
                //etc.getLoader().reloadPlugin(m_PackageName);
            }

        } catch (Exception e) {
            msg = "Sorry master something went wrong!";
            Message(msg);

            msg = e.getMessage();
            Message(msg);
        }
    }
}
