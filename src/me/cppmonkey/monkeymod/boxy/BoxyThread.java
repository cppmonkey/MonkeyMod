package me.cppmonkey.monkeymod.boxy;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class BoxyThread implements Runnable {
    private JavaPlugin m_plugin;
    private World m_world;
    private Vector m_start, m_current, m_end;
    private int m_totalProcessed = 0;

    public int g_maxBlocks = 16;

    public BoxyThread(JavaPlugin plugin, World world, Vector start, Vector end) {
        m_world = world;
        m_plugin = plugin;
        m_start = start;
        m_current = start.clone(); // Has to be cloned or it wont work correctly
        m_end = end;
    }

    public BoxyThread(JavaPlugin plugin, World world, Vector start, Vector current, Vector end) {
        m_world = world;
        m_plugin = plugin;
        m_start = start;
        m_current = current;
        m_end = end;
    }

    public BoxyThread(JavaPlugin plugin, World world, Vector start, Vector current, Vector end, int processed) {
        m_world = world;
        m_plugin = plugin;
        m_current = current;
        m_start = start;
        m_end = end;
        m_totalProcessed = processed;
    }

    public void run() {
        int blocksProcessed = 0;

        while (blocksProcessed < g_maxBlocks) {
            Block current = m_current.toLocation(m_world).getBlock();
            if (current != null) {
                current.breakNaturally();
            }
            blocksProcessed++;

            if (m_current.equals(m_end)) {
                break;
            }

            if (m_current.getBlockZ() != m_end.getBlockZ()) {
                m_current.setZ(m_current.getZ() + 1);
            } else if (m_current.getBlockY() != m_end.getBlockY()) {
                m_current.setY(m_current.getY() + 1);
                m_current.setZ(m_start.getZ());
            } else if (m_current.getBlockX() != m_end.getBlockX()) {
                m_current.setX(m_current.getX() + 1);
                m_current.setY(m_start.getY());
                m_current.setZ(m_start.getZ());
            }

        }

        m_totalProcessed += blocksProcessed;

        if (blocksProcessed == g_maxBlocks) {
            // create new process to continue the task next tick, prevents stalling
            // Bukkit.broadcastMessage("process too large, rescheduling");
            Bukkit.getScheduler().scheduleSyncDelayedTask(m_plugin, new BoxyThread(m_plugin, m_world, m_start, m_current, m_end, m_totalProcessed));
        } else {
            // Process finished
            Bukkit.broadcastMessage("Blocks processed in total = " + Integer.toString(m_totalProcessed));
        }

    }

}
