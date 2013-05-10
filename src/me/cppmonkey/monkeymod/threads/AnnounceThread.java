package me.cppmonkey.monkeymod.threads;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.MonkeyModThread;

public class AnnounceThread extends Thread implements MonkeyModThread{

    private Boolean m_halt = false;
    private MonkeyMod m_plugin;
    private long m_interval = 20; // seconds
    private String[] m_msg = {
        ChatColor.RED + "MonkeyMod is not registered on this server",
        ChatColor.RED + "Visit " + ChatColor.BLUE + "http://CppMonkey.NET/monkeymod/" + ChatColor.RED + " and register"};

    public AnnounceThread(MonkeyMod plugin) {
        m_plugin = plugin;
    }

    public AnnounceThread(MonkeyMod plugin, String[] msg) {
        m_plugin = plugin;
        m_msg = msg;
    }

    public AnnounceThread(MonkeyMod plugin, String[] msg, Integer interval) {
        m_plugin = plugin;
        m_msg = msg;
        m_interval = interval;
    }
    /*
     * TODO Validate server registration, if found, dont start server
     */

    public Boolean VerifyRegistration() {

        return true;
    }

    @Override
    public void Halt() {
        m_halt = true;
    }

    @Override
    public void run() {
        m_plugin.getServer().broadcastMessage("Announcement thread started");
        long startTime = System.nanoTime();
        long interval = m_interval * 1000000000;
        //While not told to halt, loop
        while (!m_halt) {
                
            if( System.nanoTime() - startTime > interval ){
                startTime = System.nanoTime();
                // Iterate through the message strings, broadcasting
                for (int i = 0; i < m_msg.length; i++) {
                    m_plugin.getServer().broadcastMessage(m_msg[i]);
                }
            }
            /*try {
                AnnounceThread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AnnounceThread.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        }
    }
}
