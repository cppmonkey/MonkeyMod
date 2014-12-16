package me.cppmonkey.monkeymod.callback;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.IThreadCallback;

import org.bukkit.command.CommandSender;

public class CSelfUpdateCallback  implements IThreadCallback {
	private MonkeyMod m_plugin;
	CommandSender m_owner = null;
	
	private boolean m_outOfDate = false;
	
	public CSelfUpdateCallback( MonkeyMod instance, CommandSender owner ){
		m_plugin = instance;
		m_owner = owner;
	}

	public void processLine(String result) {
		// TODO Auto-generated method stub
		
		if( "false".equalsIgnoreCase(result)){
			//Needs updating
			m_outOfDate = true;
		}
		
	}
	
	private void Message(String msg) {
        m_owner.sendMessage(msg);
    }

	public void complete() {
		// TODO Auto-generated method stub
		

    	if (m_outOfDate) {
    		Message( "Update found!" );
    		//Update found, now attempt to update using callers permissions
        	m_plugin.selfUpdate( m_owner );
        }else{
        	Message( "You have the latest version!" );
        }
	}

}