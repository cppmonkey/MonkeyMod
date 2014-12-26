package me.cppmonkey.monkeymod.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.IThreadCallback;
import me.cppmonkey.monkeymod.utils.Parm;

import org.bukkit.command.CommandSender;

/**
 *
 * @author CppMonkey
 */
@Deprecated
public class HttpRequestThread extends Thread {

    public final static String name = "Http Request Thread";
    public final static String version = "1.4.1";
    private CommandSender m_ThreadOwner;
    private URL m_url;
    private boolean m_debug;
    // New for Bukkit
    private IThreadCallback m_callback = null;

    public HttpRequestThread(String id, CommandSender player, String url, Parm[] parms) {

        m_ThreadOwner = player;
        m_debug = false;

        try {
            m_url = new URL(url + parseUrlParms(parms));
        } catch (MalformedURLException e) {
            message("HttpRequestThread() Exception");
            message(e.getMessage());
        }
    }

    public HttpRequestThread(String id, CommandSender player, String url, Parm[] parms, IThreadCallback callback) {
        m_callback = callback;

        m_ThreadOwner = player;
        m_debug = false;

        try {
            m_url = new URL(url + parseUrlParms(parms));
        } catch (MalformedURLException e) {
            message("HttpRequestThread() Exception");
            message(e.getMessage());
        }
    }

    public HttpRequestThread(String id, CommandSender player, String url, Parm[] parms, Boolean debug) {

        m_ThreadOwner = player;
        m_debug = debug;

        try {
            m_url = new URL(url + parseUrlParms(parms));
        } catch (MalformedURLException e) {
            message("HttpRequestThread() Exception");
            message(e.getMessage());
        }

        if (m_debug) {
            message(getName());
            message("url: " + url);
            message("parms: " + parseUrlParms(parms));
        }

    }

    public HttpRequestThread(String id, String url, Parm parms[], boolean debug) {
        super(id);

        m_ThreadOwner = null;
        m_debug = debug;

        if (m_debug) {
            message(getName());
            message("url: " + url);
            message("parms: " + parseUrlParms(parms));
        }

        try {
            m_url = new URL(url + parseUrlParms(parms));
        } catch (MalformedURLException e) {
            message("HttpRequestThread() Exception");
            message(e.getMessage());
        }

    }

    private void message(String msg) {
        if (m_ThreadOwner != null) {
            m_ThreadOwner.sendMessage(msg);
        } else {
            MonkeyMod.log.info(msg);
        }
    }

    public void run() {
        HttpURLConnection urlConn = null;
        try {
            while (urlConn == null || urlConn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            urlConn = (HttpURLConnection) m_url.openConnection();
            urlConn.setDoOutput(true);

                // Output to stream must occur before establishing connection
                OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream()); // TODO close stream properly try, catch, finally
                wr.write("this=test");
                wr.flush();

                HttpURLConnection.setFollowRedirects(true);

                urlConn.connect();

                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    m_url = new URL(urlConn.getHeaderField("Location"));
                    urlConn.disconnect();
                }
            }

            BufferedReader in = null;

            try {

                if (m_callback != null) {
                    in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                    if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK || urlConn.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        // debug output
                        MonkeyMod.log.finest(inputLine);

                        m_callback.processLine(inputLine);
                    }
                    in.close();
                    m_callback.complete();
                    } else {
                        MonkeyMod.log.severe("Http request failed (" + urlConn.getURL() + ")");
                        MonkeyMod.log.severe("Server response to request - " + urlConn.getResponseCode());
                    }

                } else {
                    // Basic call
                    urlConn.getInputStream();
                }

            } catch (IOException e) {
                MonkeyMod.reportException(name + " Unable to get InputStream: ", e);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException e) {
            MonkeyMod.reportException("HttpRequestThread.run() Exception", e);
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
            m_ThreadOwner = null;
            m_url = null;
        }
    }

    private String parseUrlParms(Parm parms[]) {
        StringBuilder parsedParms = new StringBuilder("?");
        parsedParms.append(parms[0].name).append("=").append(parms[0].value);

        for (int i = 1; i < parms.length; i++) {
            parsedParms.append("&").append(parms[i].name).append("=").append(parms[i].value);
        }

        return parsedParms.toString();
    }
}
