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

/**
 *
 * @author CppMonkey
 */
@Deprecated
public class HttpRequestThread extends Thread {

    public final static String name = "Http Request Thread";
    public final static String version = "1.5";
    private URL m_url;

    private IThreadCallback m_callback = null;

    public HttpRequestThread(String id, String url, Parm[] parms) {
        buildUrl(url, parms);
    }

    public HttpRequestThread(String id, String url, Parm[] parms, IThreadCallback callback) {
        m_callback = callback;
        buildUrl(url, parms);
        }

    private void buildUrl(String url, Parm parms[]) {
        try {
            m_url = new URL(url + parseUrlParms(parms));
        } catch (RuntimeException rex){
            MonkeyMod.reportException("RuntimeExcption within HttpRequestThread.run()", rex);
        } catch (MalformedURLException e) {
            MonkeyMod.reportException("HttpRequestThread() Exception", e);
        }
    }

    public void run() {
        HttpURLConnection urlConn = null;
        try {
            while (urlConn == null || urlConn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                urlConn = (HttpURLConnection) m_url.openConnection();
                urlConn.setDoOutput(true);

                // Output to stream must occur before establishing connection
                OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
                try {
                    wr.write("this=test");
                    wr.flush();
                } catch (IOException e) {
                    MonkeyMod.reportException(name + " Unable to get InputStream: ", e);
                } finally {
                    wr.close();
                }

                HttpURLConnection.setFollowRedirects(true);

                urlConn.connect();

                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    m_url = new URL(urlConn.getHeaderField("Location"));
                    urlConn.disconnect();
                }
            }

            BufferedReader in = null;

            if (m_callback != null) {
                try {
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
                } catch (RuntimeException rex){
                    MonkeyMod.reportException("RuntimeExcption within HttpRequestThread.run()", rex);
            } catch (IOException e) {
                MonkeyMod.reportException(name + " Unable to get InputStream: ", e);
            } finally {
                if (in != null) {
                    in.close();
                }
            }

            } else {
                // Basic call
                urlConn.getInputStream();
            }



        } catch (RuntimeException rex){
            MonkeyMod.reportException("RuntimeExcption within HttpRequestThread.run()", rex);
        } catch (IOException e) {
            MonkeyMod.reportException("HttpRequestThread.run() Exception", e);
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
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
