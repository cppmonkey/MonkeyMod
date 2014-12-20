package me.cppmonkey.monkeymod.threads;
/*
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URLEncoder;
import java.net.URLConnection;
import java.security.MessageDigest;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author caboose89
 ADD COMMENT CLOSE HERE
public class LoggingThread extends Thread {

    public final static String name = "Logging Thread";
    public final static String version = "1.3.1";
    private CommandSender m_ThreadOwner = null;
    private String m_PackageName;
    private String m_PostUrl;
    private Boolean m_debug = false;
    private Boolean m_isPlugin = true;
    private MonkeyMod m_plugin;

    public LoggingThread( CommandSender event, MonkeyMod plugin) {
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
        String postData = "";
        String finalHash = "";

        try{
            // BEGIN HASHING
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest("ADDPOSTFIELDSHERE".getBytes());
            StringBuffer sb = new StringBuffer();
            for(byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            finalHash = sb.toString();
            //END HASHING
        }
        catch(Exception e){
            MonkeyMod.log.severe("Http post failed: Generating hash");
        }

        try {

            msg = "Attempting to POST ... ";

            message(ChatColor.GREEN + msg);

            URL url = new URL(m_PostUrl);
            //URL url = new URL("http://hostname:80/cgi");
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

            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) Used as a unique code to say that it is the right location. Could do to be changed to check page content instead  {
                 XML FORMAT:
                <monkey>
                         <event>
                             <type>Tnt Placed</type>
                             <player>
                                <username>caboose89</username>
                                <last_ip>127.0.6.1</last_ip>
                             </player>
                             <position>world1 56 293 35</position>
                             <server>192.168.1.1</server>
                         </event>
                </monkey>
                try {
                    // Construct data
                    String data = URLEncoder.encode("XML", "UTF-8") + "=" + URLEncoder.encode(postData, "UTF-8");
                    data += "&" + URLEncoder.encode("hash", "UTF-8") + "=" + URLEncoder.encode(finalHash, "UTF-8");

                    // Send data
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    // Get the response
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        // Process line...
                    }
                    wr.close();
                    rd.close();
                    
                } catch (Exception e) {
                    MonkeyMod.log.severe("Http post failed (" + urlConn.getURL() + ")");
                    MonkeyMod.log.severe("Server response to request - " + urlConn.getResponseCode());
                }
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
*/