package me.cppmonkey.monkeymod.callback;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.IThreadCallback;

import org.bukkit.command.CommandSender;

public class CSelfUpdateCallback  implements IThreadCallback {

	private MonkeyMod m_plugin;
	CommandSender m_owner = null;
	private boolean m_outOfDate = false;
	
    public CSelfUpdateCallback(MonkeyMod instance, CommandSender owner) {
		m_plugin = instance;
		m_owner = owner;
	}

	public void processLine(String result) {
		
        if ("false".equalsIgnoreCase(result)) {
			// Needs updating
			m_outOfDate = true;
		}
		
	}
	
    private void message(String msg) {
        m_owner.sendMessage(msg);
    }

	public void complete() {

    	if (m_outOfDate) {
            message("Update found!");
			// Update found, now attempt to update using callers permissions
            m_plugin.selfUpdate(m_owner);
        } else {
            message("You have the latest version!");
        }
	}
}
