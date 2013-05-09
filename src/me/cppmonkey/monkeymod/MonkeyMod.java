package me.cppmonkey.monkeymod;

import me.cppmonkey.monkeymod.threads.UpdateThread;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.playerlistener.MonkeyModPlayerListener;
import me.cppmonkey.monkeymod.blocklistener.MonkeyModBlockListener;
import java.util.logging.Logger;

import me.cppmonkey.monkeymod.commands.BoxyCommand;
import me.cppmonkey.monkeymod.commands.ItemCommand;
import me.cppmonkey.monkeymod.commands.MonkeyCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MonkeyMod extends JavaPlugin{
	
	//Plugin Details
	private Integer m_build = 16;
	
	private PluginDescriptionFile m_pluginDescFile;
	
	//Private members containing listeners
	private final MonkeyModPlayerListener m_PlayerListener = new MonkeyModPlayerListener(this);
	private final MonkeyModBlockListener m_BlockListener = new MonkeyModBlockListener(this);
	
	private static final Logger log = Logger.getLogger("Minecraft");
	
	
	public void onDisable() {
		// TODO Auto-generated method stub
		log.info( m_pluginDescFile.getFullName() + "(" + m_build + ") is disabled!" );
	}

	public void onEnable() {
		m_pluginDescFile = this.getDescription();
		
		PluginManager pm = getServer().getPluginManager();
		
		// TODO Auto-generated method stub
		
		
		log.info( m_pluginDescFile.getFullName() + "(" + m_build +") is enabled!" );
		
		//Register hooks to process events
		pm.registerEvent(Event.Type.PLAYER_JOIN, m_PlayerListener, Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, m_PlayerListener, Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, m_PlayerListener, Priority.Low, this);
		
		//Stop the burning!! 
		pm.registerEvent(Event.Type.BLOCK_IGNITE, m_BlockListener, Priority.Normal, this);
		
		//TODO Add block destroy and placements rules 

		//TODO Process commands, these a partial commands!!
		getCommand("monkey").setExecutor(new MonkeyCommand(this));
		getCommand("item").setExecutor(new ItemCommand(this));
		getCommand("boxy").setExecutor(new BoxyCommand(this));
		
		
		//Notify CppMonkey.NET of the new server
		// Notify server about new server
		
		String[] parms = {
	            "action=update",
	            "package=" + this.getName(),
	            "version=" + this.getVersion(),
	            "build=" + this.getBuild(),
	            "rcon-port=" + (getServer().getPort()+10)
	        };
        HttpRequestThread notification = new HttpRequestThread(
                "Notification thread: Plugin initialized", null,
                "http://cppmonkey.net/minecraft/" + "update.php",
                parms);
        
        notification.start();
        
        /*
         * Check for updates from server
         */
        ConsoleCommandSender sender = new ConsoleCommandSender(getServer());
        getServer().dispatchCommand(sender, "monkey uptodate");
        		
		//getCommand("debug");
	}
	
	public void selfUpdate( CommandSender sender ){
		// sender needs to be an OP to carry out this action
		if (sender.isOp()){
			try{
				sender.sendMessage(ChatColor.GREEN + "Trying to update MonkeyMod");
			}catch(Exception e){
				log.info( "Unable to message sender");
			}
			
			UpdateThread updateThread = new UpdateThread("Update", sender, this.getName(), "http://cppmonkey.net/minecraft/", this);
	        updateThread.start();
		}else{
			sender.sendMessage(ChatColor.RED + "You dont have permission to do that");
		}
	}
	
	public String getName()
	{
		return m_pluginDescFile.getName();
	}
	
	public String getVersion(){
		return m_pluginDescFile.getVersion();
	}
	
	public String getBuild(){
		return m_build.toString();
	}
}
