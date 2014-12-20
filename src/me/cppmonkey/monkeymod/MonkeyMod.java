package me.cppmonkey.monkeymod;

import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;

import me.cppmonkey.monkeymod.callback.LoginCallback;
import me.cppmonkey.monkeymod.commands.BackCommand;
import me.cppmonkey.monkeymod.commands.BoxyCommand;
import me.cppmonkey.monkeymod.commands.ChestCommand;
import me.cppmonkey.monkeymod.commands.CompassCommand;
import me.cppmonkey.monkeymod.commands.HomeCommand;
import me.cppmonkey.monkeymod.commands.InventoryCommand;
import me.cppmonkey.monkeymod.commands.ItemCommand;
import me.cppmonkey.monkeymod.commands.MessageCommand;
import me.cppmonkey.monkeymod.commands.ModeCommand;
import me.cppmonkey.monkeymod.commands.MonkeyCommand;
import me.cppmonkey.monkeymod.commands.SpawnCommand;
import me.cppmonkey.monkeymod.commands.TeleCommand;
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
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MonkeyMod extends JavaPlugin {

    // Plugin Details
    private Integer m_build = 143;
    private PluginDescriptionFile m_pluginDescFile;

    // Private members containing listeners
    private MonkeyModPlayerListener m_PlayerListener;
    private MonkeyModBlockListener m_BlockListener;
    private MonkeyModChestBlockListener m_ChestBlockListener;
    private MonkeyModChestPlayerListener m_ChestPlayerListener;
    private MonkeyModPlayerDeathListener m_EntityListener;
    private MonkeyModExplosionListener m_ExplosionListener;

    public static final Logger log = Logger.getLogger("Minecraft");

    // Temp
    public final HashMap<Player, Boolean> canBuild = new HashMap<Player, Boolean>();
    public final HashMap<Player, Boolean> canIgnite = new HashMap<Player, Boolean>();
    public final HashMap<Player, Boolean> isVip = new HashMap<Player, Boolean>();
    public final HashMap<Player, Boolean> isAdmin = new HashMap<Player, Boolean>();

    public void onDisable() {

        this.saveConfig();

        MonkeyMod.log.info(m_pluginDescFile.getFullName() + "(" + m_build + ") is disabled!");

        // destroy Listeners
        m_PlayerListener = null;
        m_BlockListener = null;
        m_ChestBlockListener = null;
        m_ChestPlayerListener = null;
        m_EntityListener = null;
        m_ExplosionListener = null;
    }

    public void onEnable() {

        getServer().broadcastMessage("Reloading MonkeyMod");
        getServer().broadcastMessage("There maybe a slight delay whilst permissions rights are re-aquired");
        
        for(Player player : getServer().getOnlinePlayers()){
            Parm[] parms = {
                    new Parm("action", "connect"),
                    new Parm("player", player.getName()),
                    new Parm("ip", player.getAddress().getAddress().toString()),
                    new Parm("record", "false")
            };

            HttpRequestThread notification = new HttpRequestThread(
                    "Connection Notification Thread:" + player.getName(),
                    player,
                    this.getLoggerUrl(),
                    parms,
                    new LoginCallback(this, player));
            notification.start();
        }

        setNaggable(true);
        m_pluginDescFile = this.getDescription();

        PluginManager pm = getServer().getPluginManager();

        m_PlayerListener = new MonkeyModPlayerListener(this);
        m_BlockListener = new MonkeyModBlockListener(this);
        m_ChestBlockListener = new MonkeyModChestBlockListener(this);
        m_ChestPlayerListener = new MonkeyModChestPlayerListener(this);
        m_EntityListener = new MonkeyModPlayerDeathListener(this);
        m_ExplosionListener = new MonkeyModExplosionListener(this);

        MonkeyMod.log.info(m_pluginDescFile.getFullName() + "(" + m_build + ") is enabled!");

        if (this.getConfig().getBoolean("server.protection.enabled")) {
            pm.registerEvents(m_ExplosionListener, this);
            pm.registerEvents(m_BlockListener, this);
            pm.registerEvents(m_ChestBlockListener, this);
            pm.registerEvents(m_ChestPlayerListener, this);
        }

        pm.registerEvents(m_EntityListener, this);
        pm.registerEvents(m_PlayerListener, this);

        try {
        // Process commands, these a partial commands!!
            getCommand(MonkeyCommand.command).setExecutor(new MonkeyCommand(this));
            getCommand(ItemCommand.command).setExecutor(new ItemCommand(this));
            getCommand(BoxyCommand.command).setExecutor(new BoxyCommand(this));
            getCommand(ChestCommand.command).setExecutor(new ChestCommand(this));
            getCommand(TeleCommand.command).setExecutor(new TeleCommand(this));
            getCommand(SpawnCommand.command).setExecutor(new SpawnCommand(this));
            getCommand(HomeCommand.command).setExecutor(new HomeCommand(this));
            getCommand(BackCommand.command).setExecutor(new BackCommand(this));
            getCommand(CompassCommand.command).setExecutor(new CompassCommand(this));
            getCommand(ModeCommand.command).setExecutor(new ModeCommand(this));
            getCommand(InventoryCommand.command).setExecutor(new InventoryCommand(this));
            getCommand(MessageCommand.command).setExecutor(new MessageCommand(this));
        } catch (Exception ex) {
            MonkeyMod.log.severe("[Error] Unable to register command");
        }

        // Notify CppMonkey.NET of the new server
        // Notify server about new server

        Parm[] parms = {
            new Parm("action", "update"),
            new Parm("package", this.getName()),
            new Parm("version", this.getVersion()),
            new Parm("build", this.getBuild()),
                new Parm("rcon-port", Integer.toString(getServer().getPort() + 10))
        };

        HttpRequestThread notification = new HttpRequestThread("Notification thread: Plugin initialized", getServer().getConsoleSender(), getLoggerUrl(), parms);
        notification.setPriority(Thread.MIN_PRIORITY);
        notification.start();

        if (this.getConfig().getBoolean("plugin.update.auto")) {
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
        if (sender.isOp()) {
            try {
                sender.sendMessage(ChatColor.GREEN + "Trying to update MonkeyMod");
            } catch (Exception e) {
                MonkeyMod.log.info("Unable to message sender");
            }

            UpdateThread updateThread = new UpdateThread("Update", sender, this.getName(), "http://cppmonkey.net/minecraft/", this);
            updateThread.setPriority(Thread.MIN_PRIORITY);
            updateThread.start();
        } else {
            sender.sendMessage(ChatColor.RED + "You dont have permission to update");
            sender.sendMessage(ChatColor.RED + "You have to be Op!");
        }
    }

    /*
     *
     */
    public Boolean getPermition(Player player, String path) {
        if (path.matches(".isVip") && this.isVip.containsKey(player)) {
			return this.isVip.get(player);
        } else if (path.matches(".isAdmin") && this.isAdmin.containsKey(player)) {
			return this.isAdmin.get(player);
        } else if (path.matches(".canBuild") && this.canBuild.containsKey(player)) {
                return this.canBuild.get(player);
        } else if (path.matches(".canIgnite") && this.canIgnite.containsKey(player)) {
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
        return this.getConfig().getString(player.getName().toLowerCase(Locale.ENGLISH) + ".canBuild");
    }

    public String getLoggerUrl() {
        return this.getConfig().getString("logger.url", "http://cppmonkey.net/minecraft/update.php");
    }

    public String getVersion() {
        return m_pluginDescFile.getVersion();
    }

    public String getBuild() {
        return m_build.toString();
    }

    public String[] getStatus() {
        return new String[] {
                "logger.enabled " + this.getConfig().getBoolean("logger.enabled"),
                "logger.connect " + this.getConfig().getBoolean("logger.connect"),
                "logger.disconnect " + this.getConfig().getBoolean("logger.disconnect"),
                "logger.chat " + this.getConfig().getBoolean("logger.chat"),
                "protection.grief " + this.getConfig().getBoolean("protection.grief"),
                "plugin.update.auto " + this.getConfig().getBoolean("plugin.update.auto"),
                "server.protection.enabled " + this.getConfig().getBoolean("server.protection.enabled")
        };
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
