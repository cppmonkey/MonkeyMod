package me.cppmonkey.monkeymod;

import me.cppmonkey.monkeymod.threads.UpdateThread;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.listeners.MonkeyModPlayerListener;
import me.cppmonkey.monkeymod.listeners.MonkeyModBlockListener;

import java.io.File;
import java.util.Stack;
import java.util.logging.Logger;

import me.cppmonkey.monkeymod.commands.BoxyCommand;
import me.cppmonkey.monkeymod.commands.ItemCommand;
import me.cppmonkey.monkeymod.commands.MonkeyCommand;
import me.cppmonkey.monkeymod.commands.PluginCommand;
import me.cppmonkey.monkeymod.interfaces.MonkeyModThread;
import me.cppmonkey.monkeymod.listeners.MonkeyModEntityListener;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class MonkeyMod extends JavaPlugin {

    //Plugin Details
    private Integer m_build = 56;
    private PluginDescriptionFile m_pluginDescFile;
    private Configuration m_pluginConfig;
    private Configuration m_pluginPermissions;
    private Configuration m_pluginVips;
    private Configuration m_pluginBoxy;
    private Stack<MonkeyModThread> m_announceThreads = new Stack<MonkeyModThread>();
    //Private members containing listeners
    private final MonkeyModPlayerListener m_PlayerListener = new MonkeyModPlayerListener(this);
    private final MonkeyModBlockListener m_BlockListener = new MonkeyModBlockListener(this);
    private final MonkeyModEntityListener m_EntityListener = new MonkeyModEntityListener(this);
    private static final Logger log = Logger.getLogger("Minecraft");

    public void onDisable() {
        System.out.println("Shutting down MonkeyMod Threads");
        while (!m_announceThreads.isEmpty()) {
            MonkeyModThread temp = m_announceThreads.pop();
            temp.Halt();
        }

        log.info(m_pluginDescFile.getFullName() + "(" + m_build + ") is disabled!");
    }

    public void onEnable() {

        setNaggable(true);
        m_pluginDescFile = this.getDescription();

        PluginManager pm = getServer().getPluginManager();

        m_pluginConfig = getConfiguration();

        m_pluginPermissions = new Configuration(new File(getDataFolder(), "permissions.yml"));
        m_pluginPermissions.load();

        m_pluginVips = new Configuration(new File(getDataFolder(), "vips.yml"));
        m_pluginVips.load();

        m_pluginBoxy = new Configuration(new File(getDataFolder(),"boxy.yml"));
        m_pluginBoxy.load();

        //Options available to general, just place holders at the moment

        /*
        m_pluginConfig.setProperty("server.registered", false);
        m_pluginConfig.setProperty("plugin.update.auto", false);
        m_pluginConfig.setProperty("plugin.update.url", "http://cppmonkey.net/minecraft/");
        m_pluginConfig.setProperty("protection.grief", true);
        m_pluginConfig.setProperty("protection.tower", true);
        m_pluginConfig.setProperty("protection.tower.threshold", 40);
        m_pluginConfig.setProperty("logger.url", "http://cppmonkey.net/minecraft/update.php");
        m_pluginConfig.setProperty("logger.enable", true);
        m_pluginConfig.setProperty("logger.connect", true);
        m_pluginConfig.setProperty("logger.disconnect", true);
        m_pluginConfig.setProperty("logger.chat", true);

        m_pluginConfig.setProperty("override.nag", true);
         */


        // TODO Server verification before setting up hooks
        /*
        if (!m_pluginConfig.getBoolean("server.registered", false) && !m_pluginConfig.getBoolean("override.nag", false)) {
            log.info("Creating nag thread");

            AnnounceThread announcement = new AnnounceThread(this);
            announcement.setPriority(AnnounceThread.MIN_PRIORITY);
            // FIXME Annoucment thread, need to varify registration
         * /// WARNING HIGH CPU! DO NOT USE!
          //// announcement.start();
            m_announceThreads.add(announcement);
        }
         *
         */

        log.info(m_pluginDescFile.getFullName() + "(" + m_build + ") is enabled!");

        // Enable various logger hooks
        if (m_pluginConfig.getBoolean("logger.enabled", true)) {
            //Register hooks to process events
            if (m_pluginConfig.getBoolean("logger.connect", true)) {
                pm.registerEvent(Event.Type.PLAYER_JOIN, m_PlayerListener, Priority.Monitor, this);
            }
            if (m_pluginConfig.getBoolean("logger.disconnect", true)) {
                pm.registerEvent(Event.Type.PLAYER_QUIT, m_PlayerListener, Priority.Monitor, this);
            }
            if (m_pluginConfig.getBoolean("logger.chat", true)) {
                pm.registerEvent(Event.Type.PLAYER_CHAT, m_PlayerListener, Priority.Monitor, this);
            }
        } //END Logging


        if (m_pluginConfig.getBoolean("protection.grief", true)) {
            //Stop the burning!!
            pm.registerEvent(Event.Type.BLOCK_IGNITE, m_BlockListener, Priority.Normal, this);
        }

        pm.registerEvent(Event.Type.BLOCK_PLACE, m_BlockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE, m_BlockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, m_BlockListener, Priority.Normal, this);

        pm.registerEvent(Event.Type.ENTITY_DEATH, m_EntityListener, Priority.Normal, this);

        pm.registerEvent(Event.Type.INVENTORY_OPEN, m_PlayerListener, Priority.Normal, this);

        // Process commands, these a partial commands!!
        getCommand("monkey").setExecutor(new MonkeyCommand(this));
        getCommand("item").setExecutor(new ItemCommand(this));
        getCommand("boxy").setExecutor(new BoxyCommand(this));
        getCommand("plugin").setExecutor(new PluginCommand(this));


        //Notify CppMonkey.NET of the new server
        // Notify server about new server

        String[] parms = {
            "action=update",
            "package=" + this.getName(),
            "version=" + this.getVersion(),
            "build=" + this.getBuild(),
            "rcon-port=" + (getServer().getPort() + 10)
        };
        HttpRequestThread notification = new HttpRequestThread(
                "Notification thread: Plugin initialized", new ConsoleCommandSender(getServer()),
                getLoggerUrl(),
                parms);
        notification.setPriority(Thread.MIN_PRIORITY);
        notification.start();

        if (m_pluginConfig.getBoolean("plugin.update.auto", false)) {
            /*
             * Check for updates from server
             */
            ConsoleCommandSender sender = new ConsoleCommandSender(getServer());
            getServer().dispatchCommand(sender, "monkey uptodate");
        }
        //getCommand("debug");
    }

    public void selfUpdate(CommandSender sender) {
        // sender needs to be an OP to carry out this action
        if (sender.isOp()) { // Alter to .isAdmin
            try {
                sender.sendMessage(ChatColor.GREEN + "Trying to update MonkeyMod");
            } catch (Exception e) {
                log.info("Unable to message sender");
            }

            UpdateThread updateThread = new UpdateThread("Update", sender, this.getName(), "http://cppmonkey.net/minecraft/", this);
            updateThread.setPriority(Thread.MIN_PRIORITY);
            updateThread.start();
        } else {
            sender.sendMessage(ChatColor.RED + "You dont have permission to do that");
        }
    }

    @Deprecated
    public Configuration getPluginConfiguration() {
        return m_pluginConfig;
    }

    public enum EConfig {

        PLUGIN,
        PERMISSIONS,
        VIP,
        BOXY
    };

    /*
     * Retrieve configuration file for plugin
     */
    public Configuration getPluginConfiguration(EConfig config) {
        switch (config) {
            case PLUGIN:
                return m_pluginConfig;
            case PERMISSIONS:
                return m_pluginPermissions;
            case VIP:
                return m_pluginVips;
            case BOXY:
                return m_pluginBoxy;
        }

        // returns pluginConfig by default
        return m_pluginConfig;
    }


    /*
     *
     */
    public Boolean getPermition(Player player, String path) {
        // query permissions file
        // player.sendMessage(player.getName().toLowerCase() + path);
        return m_pluginPermissions.getBoolean(player.getName().toLowerCase() + path, false);
    }

    public Object isKnownUser(Player player) {
        //FIXME need to be seeing if there username exists. not, can they build!
        // Couldn't figure it out at the time
        return m_pluginPermissions.getProperty(player.getName().toLowerCase() + ".canBuild");
    }

    public String getLoggerUrl() {
        return m_pluginConfig.getString("logger.url", "http://cppmonkey.net/minecraft/update.php");
    }

    public String getName() {
        return m_pluginDescFile.getName();
    }

    public String getVersion() {
        return m_pluginDescFile.getVersion();
    }

    public String getBuild() {
        return m_build.toString();
    }

    public String[] getStatus(){
        return new String[]{
                    "logger.enabled "+m_pluginConfig.getBoolean("logger.enabled", true),
                    "logger.connect "+m_pluginConfig.getBoolean("logger.connect", true),
                    "logger.disconnect "+m_pluginConfig.getBoolean("logger.disconnect", true),
                    "logger.chat "+m_pluginConfig.getBoolean("logger.chat", true),
                    "protection.grief "+m_pluginConfig.getBoolean("protection.grief", true),
                    "plugin.update.auto "+m_pluginConfig.getBoolean("plugin.update.auto", false)
                };
    }

    public String[] getUsers(){

        return new String[]{""};
    }

}
