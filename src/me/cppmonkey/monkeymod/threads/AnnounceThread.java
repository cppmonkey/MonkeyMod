package me.cppmonkey.monkeymod.threads;

import org.bukkit.ChatColor;

import me.cppmonkey.monkeymod.MonkeyMod;

public class AnnounceThread extends Thread {

    private Boolean m_halt = false;
    private MonkeyMod m_plugin;
    private Integer m_interval = 10000;
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

    public void Halt() {
        m_halt = true;
    }

    @Override
    public void run() {
        //While not told to halt, loop
        while (!m_halt) {

            // Iterate through the message strings, broadcasting
            for (int i = 0; i < m_msg.length; i++) {
                m_plugin.getServer().broadcastMessage(m_msg[i]);
            }

            try {
                // Pause the thread
                AnnounceThread.sleep(m_interval);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
