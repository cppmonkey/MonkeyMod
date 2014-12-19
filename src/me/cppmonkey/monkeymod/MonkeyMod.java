package me.cppmonkey.monkeymod;

import java.io.File;
import java.util.Stack;

import me.cppmonkey.monkeymod.commands.BoxyCommand;
import me.cppmonkey.monkeymod.commands.ChestCommand;
import me.cppmonkey.monkeymod.commands.ItemCommand;
import me.cppmonkey.monkeymod.commands.MonkeyCommand;
import me.cppmonkey.monkeymod.commands.PluginCommand;
import me.cppmonkey.monkeymod.commands.TeleCommand;
import me.cppmonkey.monkeymod.commands.InspectionCommand;
import me.cppmonkey.monkeymod.interfaces.IThread;
import me.cppmonkey.monkeymod.listeners.MonkeyModBlockListener;
import me.cppmonkey.monkeymod.listeners.MonkeyModChestBlockListener;
import me.cppmonkey.monkeymod.listeners.MonkeyModChestPlayerListener;
import me.cppmonkey.monkeymod.listeners.PlayerDeathListener;
import me.cppmonkey.monkeymod.listeners.MonkeyModPlayerListener;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.threads.UpdateThread;

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
import org.yaml.snakeyaml.error.YAMLException;

public class MonkeyMod extends JavaPlugin {

    // Plugin Details
    private Integer m_build = 107;
    private PluginDescriptionFile m_pluginDescFile;
    // Array Storage for Configs
    private Configuration[] m_configs;
    // Thread holder
    private Stack<IThread> m_announceThreads = new Stack<IThread>();
    // Private members containing listeners
    private MonkeyModPlayerListener m_PlayerListener;
    private MonkeyModBlockListener m_BlockListener;
    private MonkeyModChestBlockListener m_ChestBlockListener;
    private MonkeyModChestPlayerListener m_ChestPlayerListener;
    private PlayerDeathListener m_EntityListener;

    public void onDisable() {
        System.out.println("Shutting down MonkeyMod Threads");
        while (!m_announceThreads.isEmpty()) {
            IThread temp = m_announceThreads.pop();
            temp.halt();
        }

        System.out.println(m_pluginDescFile.getFullName() + "(" + m_build + ") is disabled!");

        for (EConfig config : EConfig.values()) {
            try {
                m_configs[config.ordinal()].save();
            } catch (YAMLException e) {
                System.out.println("[ERROR] saving " + config.name() + ".yml");
                String msg = e.getMessage();
                if (msg != null) {
                    System.out.println("[ERROR] " + msg);
                }
            }
        }

        // destroy Listeners
        m_PlayerListener = null;
        m_BlockListener = null;
        m_ChestBlockListener = null;
        m_ChestPlayerListener = null;
        m_EntityListener = null;
    }

    public void onEnable() {

        setNaggable(true);
        m_pluginDescFile = this.getDescription();

        PluginManager pm = getServer().getPluginManager();

        // Load configs
        m_configs = new Configuration[EConfig.values().length];

        for (EConfig config : EConfig.values()) {
            if (config == EConfig.PLUGIN) {
                m_configs[config.ordinal()] = getConfiguration();
            } else {
                m_configs[config.ordinal()] = new Configuration(new File(
                        getDataFolder(), config.name().toLowerCase() + ".yml"));
                m_configs[config.ordinal()].load();
            }
        }

        m_PlayerListener = new MonkeyModPlayerListener(this);
        m_BlockListener = new MonkeyModBlockListener(this);
        m_ChestBlockListener = new MonkeyModChestBlockListener(this);
        m_ChestPlayerListener = new MonkeyModChestPlayerListener(this);
        m_EntityListener = new PlayerDeathListener(this);


        // TODO Server verification before setting up hooks
        /*
        if (!m_configs[EConfig.PLUGIN.ordinal()].getBoolean("server.registered", false) && !m_configs[EConfig.PLUGIN.ordinal()].getBoolean("override.nag", false)) {
            log.info("Creating nag thread");

            AnnounceThread announcement = new AnnounceThread(this);
            announcement.setPriority(AnnounceThread.MIN_PRIORITY);
        // FIXME Announcement thread, need to verify registration
         * /// WARNING HIGH CPU! DO NOT USE!
          //// announcement.start();
            m_announceThreads.add(announcement);
        }
         *
         */

        System.out.println(m_pluginDescFile.getFullName() + "(" + m_build + ") is enabled!");

        // Enable various logger hooks
        if (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.enabled", true)) {
            // Register hooks to process events
            if (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.connect", true)) {
                pm.registerEvent(Event.Type.PLAYER_JOIN, m_PlayerListener, Priority.Monitor, this);
            }
            if (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.disconnect", true)) {
                pm.registerEvent(Event.Type.PLAYER_QUIT, m_PlayerListener, Priority.Monitor, this);
            }
            if (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.chat", true)) {
                pm.registerEvent(Event.Type.PLAYER_CHAT, m_PlayerListener, Priority.Monitor, this);
            }
        } // END Logging

        if (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("protection.grief", true)) {
            // Stop the burning!!
            pm.registerEvent(Event.Type.BLOCK_IGNITE, m_BlockListener, Priority.Normal, this);
        }

        if (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("server.protection.enabled", false)) {
        pm.registerEvent(Event.Type.BLOCK_PLACE, m_BlockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE, m_BlockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, m_BlockListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.BLOCK_PLACE, m_ChestBlockListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.BLOCK_DAMAGE, m_ChestBlockListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.BLOCK_BREAK, m_ChestBlockListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.PLAYER_INTERACT, m_ChestPlayerListener, Priority.Normal, this);
        }

        pm.registerEvent(Event.Type.ENTITY_DEATH, m_EntityListener, Priority.Low, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, m_EntityListener, Priority.Low, this);

        pm.registerEvent(Event.Type.INVENTORY_OPEN, m_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, m_PlayerListener, Priority.Normal, this);

        // Process commands, these a partial commands!!
        getCommand("monkey").setExecutor(new MonkeyCommand(this));
        getCommand("item").setExecutor(new ItemCommand(this));
        getCommand("boxy").setExecutor(new BoxyCommand(this));
        getCommand("plugin").setExecutor(new PluginCommand(this));
        getCommand("chest").setExecutor(new ChestCommand(this));
        TeleCommand Telelistener = new TeleCommand(this);
        getCommand("spawn").setExecutor(Telelistener);
        getCommand("home").setExecutor(Telelistener);
        getCommand("tele").setExecutor(Telelistener);
        InspectionCommand Inspectionlistener = new InspectionCommand(this);
        getCommand("list").setExecutor(Inspectionlistener);
        getCommand("inventory").setExecutor(Inspectionlistener);


        // Notify CppMonkey.NET of the new server
        // Notify server about new server

        Parm[] parms = {
            new Parm("action", "update"),
            new Parm("package", this.getName()),
            new Parm("version", this.getVersion()),
            new Parm("build", this.getBuild()),
            new Parm("rcon-port", Integer.toString(getServer().getPort() + 10))
        };
        HttpRequestThread notification = new HttpRequestThread(
                "Notification thread: Plugin initialized", new ConsoleCommandSender(getServer()),
                getLoggerUrl(),
                parms);
        notification.setPriority(Thread.MIN_PRIORITY);
        notification.start();

        if (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("plugin.update.auto", false)) {
            /*
             * Check for updates from server
             */
            ConsoleCommandSender sender = new ConsoleCommandSender(getServer());
            getServer().dispatchCommand(sender, "monkey uptodate");
        }
        // getCommand("debug");
    }

    public void selfUpdate(CommandSender sender) {
        // sender needs to be an OP to carry out this action
        if (sender.isOp()) { // Alter to .isAdmin
            try {
                sender.sendMessage(ChatColor.GREEN + "Trying to update MonkeyMod");
            } catch (Exception e) {
                System.out.println("Unable to message sender");
            }

            UpdateThread updateThread = new UpdateThread("Update", sender, this.getName(), "http://cppmonkey.net/minecraft/", this /*, new CSelfUpdateCallback(this)*/);
            updateThread.setPriority(Thread.MIN_PRIORITY);
            updateThread.start();
        } else {
            sender.sendMessage(ChatColor.RED + "You dont have permission to update");
        }
    }

    public enum EConfig {

        PLUGIN,
        PERMISSIONS,
        VIP,
        BOXY,
        CHESTS,
        TELE
    };

    /*
     * Retrieve configuration file for plugin
     */
    public Configuration getPluginConfiguration(EConfig config) {
        // OMG so much nicer
        return m_configs[config.ordinal()];
        }

    /*
     *
     */
    public Boolean getPermition(Player player, String path) {
        // query permissions file
        // player.sendMessage(player.getName().toLowerCase() + path);
        return m_configs[EConfig.PERMISSIONS.ordinal()].getBoolean(player.getName().toLowerCase() + path, false);
    }

    public Object isKnownUser(Player player) {
        // FIXME need to be seeing if there username exists. not, can they build!
        // Couldn't figure it out at the time
        return m_configs[EConfig.PERMISSIONS.ordinal()].getProperty(player.getName().toLowerCase() + ".canBuild");
    }

    public String getLoggerUrl() {
        return m_configs[EConfig.PLUGIN.ordinal()].getString("logger.url", "http://cppmonkey.net/minecraft/update.php");
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

    public String[] getStatus() {
        return new String[]{
                    "logger.enabled " + m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.enabled", true),
                    "logger.connect " + m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.connect", true),
                    "logger.disconnect " + m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.disconnect", true),
                    "logger.chat " + m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.chat", true),
                    "protection.grief " + m_configs[EConfig.PLUGIN.ordinal()].getBoolean("protection.grief", true),
                    "plugin.update.auto " + m_configs[EConfig.PLUGIN.ordinal()].getBoolean("plugin.update.auto", false),
                    "server.protection.enabled " + m_configs[EConfig.PLUGIN.ordinal()].getBoolean("server.protection.enabled", false)
                }; // TODO global list required to ensure ALL properties are listed 
    }

    public void registerVariable(Object variable) {
    }

    public String[] getUsers() {
    	Player players[] = this.getServer().getOnlinePlayers();

    	String names[] = new String[players.length];
    	
        for (int i = 0; i < names.length; i++) {
    		names[i] = players[i].getName();
    	}

        return names;
    }
}
