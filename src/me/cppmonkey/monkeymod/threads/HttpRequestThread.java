package me.cppmonkey.monkeymod.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.IThreadCallback;

import org.bukkit.command.CommandSender;

/**
 *
 * @author CppMonkey
 */
public class HttpRequestThread extends Thread {

    protected static final Logger log = Logger.getLogger("Minecraft");
    public static String name = "Http Request Thread";
    public static String version = "1.4.1";
    private CommandSender m_ThreadOwner;
    private URL m_url;
    private Boolean m_debug;
    
    //New for Bukkit
    private IThreadCallback m_callback = null;
    private MonkeyMod m_plugin = null;

    public HttpRequestThread(String id, CommandSender player, String url, String[] parms) {

        m_ThreadOwner = player;
        m_debug = false;

        try {
            m_url = new URL(url + "?" + ParseUrlParms(parms));
        } catch (MalformedURLException e) {
            Message("HttpRequestThread() Exception");
            Message(e.getMessage());
        }
    }
    
    public HttpRequestThread(String id, CommandSender player, String url, String[] parms, MonkeyMod plugin) {

    	m_plugin = plugin;
    	
        m_ThreadOwner = player;
        m_debug = false;

        try {
            m_url = new URL(url + "?" + ParseUrlParms(parms));
        } catch (MalformedURLException e) {
            Message("HttpRequestThread() Exception");
            Message(e.getMessage());
        }
    }

    public HttpRequestThread(String id, CommandSender player, String url, String[] parms, MonkeyMod plugin, IThreadCallback callback) {
    	
    	m_plugin = plugin;
    	m_callback = callback;
    	
        m_ThreadOwner = player;
        m_debug = false;

        try {
            m_url = new URL(url + "?" + ParseUrlParms(parms));
        } catch (MalformedURLException e) {
            Message("HttpRequestThread() Exception");
            Message(e.getMessage());
        }
    }

    public HttpRequestThread(String id, CommandSender player, String url, String[] parms, Boolean debug) {

        m_ThreadOwner = player;
        m_debug = debug;

        try {
            m_url = new URL(url + "?" + ParseUrlParms(parms));
        } catch (MalformedURLException e) {
            Message("HttpRequestThread() Exception");
            Message(e.getMessage());
        }

        if (m_debug) {
            Message(getName());
            Message("url: " + url);
            Message("parms: " + ParseUrlParms(parms));
        }

    }

    // Legacy support
    @Deprecated
    public HttpRequestThread(String id, CommandSender player, String url, String parms, Boolean debug) {
        super(id);

        m_ThreadOwner = player;
        m_debug = debug;

        if (m_debug) {
            Message(getName());
            Message("url: " + url);
            Message("parms: " + parms);
            Message(url + "?" + parms);
        }

        try {
            m_url = new URL(url + "?" + parms);
        } catch (MalformedURLException e) {
            Message("HttpRequestThread() Exception");
            Message(e.getMessage());
        }
    }

    @Deprecated
    public HttpRequestThread(String id, String url, String parms, Boolean debug) {
        super(id);

        m_ThreadOwner = null;
        m_debug = debug;

        if (m_debug) {
            Message(getName());
            Message("url: " + url);
            Message("parms: " + parms);
        }

        try {
            m_url = new URL(url + "?" + parms);
        } catch (MalformedURLException e) {
            Message("HttpRequestThread() Exception");
            Message(e.getMessage());
        }

    }

    public HttpRequestThread(String id, String url, String parms[], Boolean debug) {
        super(id);

        m_ThreadOwner = null;
        m_debug = debug;

        if (m_debug) {
            Message(getName());
            Message("url: " + url);
            Message("parms: " + ParseUrlParms(parms));
        }

        try {
            m_url = new URL(url + "?" + ParseUrlParms(parms));
        } catch (MalformedURLException e) {
            Message("HttpRequestThread() Exception");
            Message(e.getMessage());
        }

    }

    private void Message(String msg) {
        if (m_ThreadOwner != null) {
            m_ThreadOwner.sendMessage(msg);
        } else {
            log.info(msg);
        }
    }

    public void run() {
        HttpURLConnection urlConn = null;
        try {

            urlConn = (HttpURLConnection) m_url.openConnection();

            urlConn.setRequestMethod("GET");
            urlConn.setAllowUserInteraction(false);
            urlConn.setDoOutput(true);
            urlConn.addRequestProperty("Content-type", "text/xml");

            try {
            	
            	if (m_callback != null) {
            		BufferedReader in = new BufferedReader(
							new InputStreamReader(
									urlConn.getInputStream()));

	            	String inputLine;
	            	
	            	while ((inputLine = in.readLine() )!= null){
	            		// debug output
	            		if(m_debug)
	            			System.out.println(inputLine);
	            		
	            		m_callback.processLine( inputLine );
	            	}
	            	in.close();
	            	m_callback.complete();
	            	
                }else{
                	//Basic call
                	urlConn.getInputStream();
                }
                	
                
            } catch (IOException e) {
                Message(name + " Unable to get InputStream");
            }


        } catch (IOException e) {
            Message("HttpRequestThread.run() Exception");
            for (int i = 0; i < e.getMessage().length() / 50; ++i) {
                Message(e.getMessage().substring(i * 50, (i + 1) * 50));
            }
        } finally {
            urlConn.disconnect();
            m_ThreadOwner = null;
            m_url = null;
        }
    }

    private String ParseUrlParms(String parms[]) {
        String parsedParms = "";

        for (int i = 0; i < parms.length; i++) {
            parsedParms += parms[i];

            if (i < parms.length - 1) {
                parsedParms += "&";
            }
        }
        return parsedParms;
    }
}
