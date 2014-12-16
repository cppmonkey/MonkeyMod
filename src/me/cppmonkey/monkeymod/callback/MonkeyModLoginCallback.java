package me.cppmonkey.monkeymod.callback;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.MonkeyModThreadCallback;

public class MonkeyModLoginCallback implements MonkeyModThreadCallback {
	
	private MonkeyMod m_plugin;
	
	public MonkeyModLoginCallback( MonkeyMod instance ){
		m_plugin = instance;
	}

	public void process(String result) {
		// TODO Auto-generated method stub
		
	}

}
