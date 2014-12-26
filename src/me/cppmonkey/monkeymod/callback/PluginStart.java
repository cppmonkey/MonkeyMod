package me.cppmonkey.monkeymod.callback;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.IThreadCallback;

public class PluginStart implements IThreadCallback {
    private MonkeyMod m_plugin;
    
    public PluginStart(MonkeyMod plugin) {
        m_plugin = plugin;
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
