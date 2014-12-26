package me.cppmonkey.monkeymod.http.callbacks;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.IThreadCallback;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.utils.Parm;

public class OnPluginStart implements IThreadCallback {
    private MonkeyMod m_plugin;
    
    public OnPluginStart(MonkeyMod plugin) {
        m_plugin = plugin;
    }

    public static void (){
        Parm[] parms = {
            new Parm("action", "update"),
            new Parm("package", m_plugin.getName()),
            new Parm("version", m_plugin.getVersion()),
            new Parm("build", m_plugin.getBuild()),
            new Parm("port", Integer.toString(m_plugin.getServer().getPort()))
        };

        HttpRequestThread notification = new HttpRequestThread("Notification thread: Plugin initialized", m_plugin.getServer().getConsoleSender(), m_plugin.getLoggerUrl(), parms);
        notification.setPriority(Thread.MIN_PRIORITY);
        notification.start();
    }

    public void processLine(String result) {
        // TODO Auto-generated method stub
        if (result != null && !result.isEmpty()) {
            
            result = result.trim();
            String split[] = result.split(":");
            
            if (split.length == 2) {
                if( "serverUID".equalsIgnoreCase(split[0]) ) {
                    m_plugin.serverUID = Integer.parseInt(split[1]);
                }
            }
        }
    }

    public void complete() {

    }

}
