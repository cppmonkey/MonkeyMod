package me.cppmonkey.monkeymod;

import java.util.HashMap;
import java.util.Locale;
import java.util.Stack;
import java.util.logging.Logger;

import me.cppmonkey.monkeymod.commands.BoxyCommand;
import me.cppmonkey.monkeymod.commands.ChestCommand;
import me.cppmonkey.monkeymod.commands.InspectionCommand;
import me.cppmonkey.monkeymod.commands.ItemCommand;
import me.cppmonkey.monkeymod.commands.MessageCommand;
import me.cppmonkey.monkeymod.commands.MonkeyCommand;
import me.cppmonkey.monkeymod.commands.PluginCommand;
import me.cppmonkey.monkeymod.commands.TeleCommand;
import me.cppmonkey.monkeymod.interfaces.IThread;
import me.cppmonkey.monkeymod.listeners.MonkeyModBlockListener;
import me.cppmonkey.monkeymod.listeners.MonkeyModChestBlockListener;
import me.cppmonkey.monkeymod.listeners.MonkeyModChestPlayerListener;
import me.cppmonkey.monkeymod.listeners.MonkeyModExplosionListener;
import me.cppmonkey.monkeymod.listeners.MonkeyModPlayerDeathListener;
import me.cppmonkey.monkeymod.listeners.MonkeyModPlayerListener;
import me.cppmonkey.monkeymod.threads.HttpRequestThread;
import me.cppmonkey.monkeymod.threads.UpdateThread;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MonkeyMod extends JavaPlugin {

    // Plugin Details
	private Integer m_build = 138;
    private PluginDescriptionFile m_pluginDescFile;
    // Array Storage for Configs
    //  private Config[] m_configs;
    // Thread holder
    private Stack<IThread> m_announceThreads = new Stack<IThread>();
    // Private members containing listeners
    private MonkeyModPlayerListener m_PlayerListener;
    private MonkeyModBlockListener m_BlockListener;
    private MonkeyModChestBlockListener m_ChestBlockListener;
    private MonkeyModChestPlayerListener m_ChestPlayerListener;
    private MonkeyModPlayerDeathListener m_EntityListener;
    private MonkeyModExplosionListener m_ExplosionListener;

    public static final Logger log = Logger.getLogger("Minecraft");

    // Temp
    public final HashMap<Player,Boolean> canBuild = new HashMap<Player,Boolean>();
    public final HashMap<Player,Boolean> canIgnite = new HashMap<Player,Boolean>();
	public final HashMap<Player,Boolean> isVip = new HashMap<Player,Boolean>();
	public final HashMap<Player,Boolean> isAdmin = new HashMap<Player,Boolean>();


    public void onDisable() {

        this.saveConfig();
        MonkeyMod.log.info("Shutting down MonkeyMod Threads");
        while (!m_announceThreads.isEmpty()) {
            IThread temp = m_announceThreads.pop();
            temp.halt();
        }

        MonkeyMod.log.info(m_pluginDescFile.getFullName() + "(" + m_build
                + ") is disabled!");

        //      for (EConfig config : EConfig.values()) {
        //          try {
        //              if (config == EConfig.PLUGIN) {
        //                  this.saveConfig();
        //              } else {
        //                  // TODO Save other configs
        //              }
        //          } catch (YAMLException e) {
        //              MonkeyMod.log.severe("saving " + config.name() + ".yml");
        //              String msg = e.getMessage();
        //              if (msg != null) {
        //                  MonkeyMod.log.severe(msg);
        //              }
        //          }
        //      }

        // destroy Listeners
        m_PlayerListener = null;
        m_BlockListener = null;
        m_ChestBlockListener = null;
        m_ChestPlayerListener = null;
        m_EntityListener = null;
        m_ExplosionListener = null;
    }

    public void onEnable() {

        setNaggable(true);
        m_pluginDescFile = this.getDescription();

        PluginManager pm = getServer().getPluginManager();

        /*// Load configs
        m_configs = new Config[EConfig.values().length];

        for (EConfig config : EConfig.values()) {
            try {
                if (config == EConfig.PLUGIN) {
                    m_configs[config.ordinal()].config = this.getConfig();
                } else {
                    m_configs[config.ordinal()].file = new File(
                            getDataFolder(), config.name().toLowerCase(
                                    Locale.ENGLISH)
                                    + ".yml");
                    m_configs[config.ordinal()].config = YamlConfiguration
                            .loadConfiguration(m_configs[config.ordinal()].file);
                }
            } catch (YAMLException e) {
                Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE,
                        "Could not load config to " + e);
                log.info(getDataFolder()
                        + config.name().toLowerCase(Locale.ENGLISH) + ".yml");
            } catch (NullPointerException e) {
                Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE,
                        "Could not load config to " + e);
                log.info(getDataFolder()
                        + config.name().toLowerCase(Locale.ENGLISH) + ".yml");
            }
        }
         */
        m_PlayerListener = new MonkeyModPlayerListener(this);
        m_BlockListener = new MonkeyModBlockListener(this);
        m_ChestBlockListener = new MonkeyModChestBlockListener(this);
        m_ChestPlayerListener = new MonkeyModChestPlayerListener(this);
        m_EntityListener = new MonkeyModPlayerDeathListener(this);
        m_ExplosionListener = new MonkeyModExplosionListener(this);


        // TODO Server verification before setting up hooks
        /*
         * if
         * (!m_configs[EConfig.PLUGIN.ordinal()].getBoolean("server.registered",
         * false) &&
         * !m_configs[EConfig.PLUGIN.ordinal()].getBoolean("override.nag",
         * false)) { log.info("Creating nag thread");
         *
         * AnnounceThread announcement = new AnnounceThread(this);
         * announcement.setPriority(AnnounceThread.MIN_PRIORITY); // FIXME
         * Announcement thread, need to verify registration /// WARNING HIGH
         * CPU! DO NOT USE! //// announcement.start();
         * m_announceThreads.add(announcement); }
         */

        MonkeyMod.log.info(m_pluginDescFile.getFullName() + "(" + m_build
                + ") is enabled!");

        /*
         * Enable various logger hooks if
         * (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.enabled",
         * true)) { // Register hooks to process events if
         * (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.connect",
         * true)) { pm.registerEvents(m_PlayerListener, this); } if
         * (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.disconnect",
         * true)) { pm.registerEvents(m_PlayerListener, this); } if
         * (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("logger.chat", true))
         * { pm.registerEvents(m_PlayerListener, this); } } // END Logging
         *
         * if
         * (m_configs[EConfig.PLUGIN.ordinal()].getBoolean("protection.grief",
         * true)) { // Stop the burning!! pm.registerEvents(m_BlockListener,
         * this); }
         */

        if (this.getConfig().getBoolean(
                "server.protection.enabled")) {
            pm.registerEvents(m_ExplosionListener, this);
            pm.registerEvents(m_BlockListener, this);
            pm.registerEvents(m_ChestBlockListener, this);
            pm.registerEvents(m_ChestPlayerListener, this);
        }

        pm.registerEvents(m_EntityListener, this);
        pm.registerEvents(m_PlayerListener, this);

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
        getCommand("back").setExecutor(Telelistener);
        getCommand("compass").setExecutor(Telelistener);
        getCommand("mode").setExecutor(Telelistener);
        InspectionCommand Inspectionlistener = new InspectionCommand(this);
        getCommand("list").setExecutor(Inspectionlistener);
        getCommand("inventory").setExecutor(Inspectionlistener);
        getCommand("message").setExecutor(new MessageCommand(this));

        // Notify CppMonkey.NET of the new server
        // Notify server about new server

        Parm[] parms = {
            new Parm("action", "update"),
            new Parm("package", this.getName()),
            new Parm("version", this.getVersion()),
            new Parm("build", this.getBuild()),
                new Parm("rcon-port",
                        Integer.toString(getServer().getPort() + 10)) };

        HttpRequestThread notification = new HttpRequestThread(

        "Notification thread: Plugin initialized", getServer()
                .getConsoleSender(), getLoggerUrl(), parms);
        notification.setPriority(Thread.MIN_PRIORITY);
        notification.start();

        if (this.getConfig().getBoolean(
                "plugin.update.auto")) {
            /*
             * Check for updates from server
             */
            ConsoleCommandSender sender = getServer().getConsoleSender();
            getServer().dispatchCommand(sender, "monkey uptodate");
        }
        // getCommand("debug");
    }

    public void selfUpdate(CommandSender sender) {
        // sender needs to be an OP to carry out this action
        if (sender.isOp()) { // Alter to .isAdmin
            try {
                sender.sendMessage(ChatColor.GREEN
                        + "Trying to update MonkeyMod");
            } catch (Exception e) {
                MonkeyMod.log.info("Unable to message sender");
            }

            UpdateThread updateThread = new UpdateThread("Update", sender,
                    this.getName(), "http://cppmonkey.net/minecraft/", this /*
                                                                             * ,
                                                                             * new
                                                                             * CSelfUpdateCallback
                                                                             * (
                                                                             * this
                                                                             * )
                                                                             */);
            updateThread.setPriority(Thread.MIN_PRIORITY);
            updateThread.start();
        } else {
            sender.sendMessage(ChatColor.RED
                    + "You dont have permission to update");
        }
    }

    public enum EConfig {

        PLUGIN, PERMISSIONS, VIP, BOXY, CHESTS, TELE
    };

    /*
     * Retrieve configuration file for plugin
     */
    @Deprecated
    public FileConfiguration getPluginConfiguration(EConfig config) {
        // OMG so much nicer
        return this.getConfig();
        }

    /*
     *
     */
    public Boolean getPermition(Player player, String path) {
        // query permissions file
        // player.sendMessage(player.getName().toLowerCase(Locale.ENGLISH) +
        // path);

		if( path.matches(".isVip") && this.isVip.containsKey(player) ) {
			return this.isVip.get(player);
		}else if( path.matches(".isAdmin") && this.isAdmin.containsKey(player) ) {
			return this.isAdmin.get(player);
		}else if( path.matches(".canBuild") && this.canBuild.containsKey(player) ) {
                return this.canBuild.get(player);
		}else if( path.matches(".canIgnite") && this.canIgnite.containsKey(player)) {
                return this.canIgnite.get(player);
            }

		// if not found return false
		return false;
    }

    @Deprecated
    public Object isKnownUser(Player player) {
        // FIXME need to be seeing if there username exists. not, can they
        // build!
        // Couldn't figure it out at the time
        return this.getConfig().getString(player
                .getName().toLowerCase(Locale.ENGLISH) + ".canBuild");
    }

    public String getLoggerUrl() {
        return this.getConfig().getString(
                "logger.url", "http://cppmonkey.net/minecraft/update.php");
    }

    public String getVersion() {
        return m_pluginDescFile.getVersion();
    }

    public String getBuild() {
        return m_build.toString();
    }

    public String[] getStatus() {
        return new String[] {
                "logger.enabled "
                        + this.getConfig().getBoolean("logger.enabled"),
                "logger.connect "
                        + this.getConfig().getBoolean("logger.connect"),
                "logger.disconnect "
                        + this.getConfig().getBoolean("logger.disconnect"),
                "logger.chat "
                        + this.getConfig().getBoolean("logger.chat"),
                "protection.grief "
                        + this.getConfig().getBoolean("protection.grief"),
                "plugin.update.auto "
                        + this.getConfig().getBoolean("plugin.update.auto"),
                "server.protection.enabled "
                        + this.getConfig().getBoolean("server.protection.enabled") }; // TODO
                                                                        // global
                                                                        // list
                                                                        // required
                                                                        // to
                                                                        // ensure
                                                                        // ALL
                                                                        // properties
                                                                        // are
                                                                        // listed
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
