package me.cppmonkey.monkeymod.commands;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.block.Block;
import org.bukkit.World;
import org.bukkit.Location;

public class BoxyCommand implements CommandExecutor {

	private final MonkeyMod m_plugin;
    private final Configuration m_settings;
	
    //
    //TODO: Make boxy World safe ie: considers worlds which start and end block are in...
    //
    public BoxyCommand(MonkeyMod instance) {
		m_plugin = instance;
        m_settings = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.BOXY);
	}

    private int setDefaultSettings(String PlayerName) {
        //set default settings on error occurring
        m_settings.setProperty(PlayerName.toLowerCase() + ".fromId", 0);
        m_settings.setProperty(PlayerName.toLowerCase() + ".toId", 0);
        m_settings.setProperty(PlayerName.toLowerCase() + ".step", 1);
        m_settings.setProperty(PlayerName.toLowerCase() + ".height", 1);
        return 0;
    }

    @Deprecated // user Material.isSolid()
    private boolean Solids(int value) {
        switch (value) {
            case 8:
                return false;
            case 9:
                return false;
            case 10:
                return false;
            case 11:
                return false;
            case 51:
                return false;
            default:
                return true;
        }
    }

    public boolean playerBoxyClickEvent(Player player, Block block, int X, int Y, int Z) {
        if (!(m_settings.getBoolean(player.getName().toLowerCase() + ".hasStart", false))) {
            //Start point selected
            m_settings.setProperty(player.getName().toLowerCase() + ".hasStart", true);
            m_settings.setProperty(player.getName().toLowerCase() + ".startLocation", (X + "," + Y + "," + Z));
            m_settings.setProperty(player.getName().toLowerCase() + ".startWorld", block.getWorld().getName().toString()); // FIXME toString? why it is a string...?
            player.sendMessage(ChatColor.GREEN + "Boxy start point confirmed");
            m_settings.save();
            return true;
        } else {
            if (!(m_settings.getBoolean(player.getName().toLowerCase() + ".hasEnd", false))) {
                //end point selected
                m_settings.setProperty(player.getName().toLowerCase() + ".hasEnd", true);
                m_settings.setProperty(player.getName().toLowerCase() + ".endLocation", (X + "," + Y + "," + Z));
                m_settings.setProperty(player.getName().toLowerCase() + ".endWorld", block.getWorld().getName().toString());
                m_settings.save();
                player.sendMessage(ChatColor.GREEN + "Boxy end point confirmed");
                if (!((m_settings.getString(player.getName().toLowerCase() + ".startWorld", "FAIL")).matches(block.getWorld().getName().toString()))) {
                    player.sendMessage(ChatColor.RED + "Cannot perform boxy across worlds! Aborted operation.");
                    m_settings.setProperty(player.getName().toLowerCase() + ".hasStart", false);
                    m_settings.setProperty(player.getName().toLowerCase() + ".hasEnd", false);
                    m_settings.save();
                    return true;
                }

                player.sendMessage(ChatColor.GOLD + "Caution! You are about to commit a Boxy alteration!");
                player.sendMessage(ChatColor.GOLD + "Converting block type " + m_settings.getProperty(player.getName().toLowerCase() + ".fromId") + " to " + (m_settings.getProperty(player.getName().toLowerCase() + ".toId")));
                player.sendMessage(ChatColor.GOLD + "Excludeing block types: " + m_settings.getProperty(player.getName().toLowerCase() + ".exclude"));
                player.sendMessage(ChatColor.GOLD + "With a height of " + m_settings.getProperty(player.getName().toLowerCase() + ".height") + " and a step of " + m_settings.getProperty(player.getName().toLowerCase() + ".step"));
                player.sendMessage(ChatColor.GOLD + "Start point (X,Y,Z): " + m_settings.getProperty(player.getName().toLowerCase() + ".startLocation"));
                player.sendMessage(ChatColor.GOLD + "End point (X,Y,Z): " + m_settings.getProperty(player.getName().toLowerCase() + ".endLocation"));
                player.sendMessage(ChatColor.RED + "RIGHT CLICK TO COMMIT! right click elsewhere to cancel!");
                return true;
            } else {
                if (m_settings.getProperty(player.getName().toLowerCase() + ".endLocation").toString().matches((X + "," + Y + "," + Z))) {
                    //end point confirmed and boxy committed
                    m_settings.setProperty(player.getName().toLowerCase() + ".hasStart", false);
                    m_settings.setProperty(player.getName().toLowerCase() + ".hasEnd", false);
                    m_settings.save();
                    boxyOperation(player, block);
                    return true;
                } else {
                    //boxy aborted
                    m_settings.setProperty(player.getName().toLowerCase() + ".hasStart", false);
                    m_settings.setProperty(player.getName().toLowerCase() + ".hasEnd", false);
                    player.sendMessage(ChatColor.GREEN + "Boxy alteration aborted");
                    m_settings.save();
                    return true;
                }
            }
        }
    }

    private boolean boxyOperation(Player player, Block block) {
        m_plugin.getServer().broadcastMessage(ChatColor.RED + "[SERVER] WARNING! BOXY OPERATION IN PROGRES!");
        m_plugin.getServer().broadcastMessage(ChatColor.RED + "[SERVER] PLEASE STOP WHAT YOU ARE DOING, AND EXPECT LAG!");
        m_plugin.getServer().broadcastMessage(ChatColor.GOLD + "[SERVER] Processing...");
        Material toMaterial;
        Material fromMaterial;
        toMaterial = Material.getMaterial(m_settings.getInt((player.getName().toLowerCase() + ".toId"), 51));
        fromMaterial = Material.getMaterial(m_settings.getInt((player.getName().toLowerCase() + ".fromId"), 51));
        if ((toMaterial == Material.FIRE) || (fromMaterial == Material.FIRE)) {
            player.sendMessage(ChatColor.RED + "Boxy settings fault. Please review your settings!");
            m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] OPERATION COMPLETE!");
            return false;
        }

        World world = player.getWorld();
        Location end = block.getLocation();
        Location start = block.getLocation();
        String startLoc = m_settings.getProperty(player.getName().toLowerCase() + ".startLocation").toString();
        String temp[] = startLoc.split(",");
        start.setX(Double.parseDouble(temp[0]));
        start.setY(Double.parseDouble(temp[1]));
        start.setY(start.getY() + 1);
        start.setZ(Double.parseDouble(temp[2]));
        int step = m_settings.getInt(player.getName().toLowerCase() + ".step", 1);
        if (step <= 0) {
            player.sendMessage(ChatColor.RED + "Boxy settings fault. Please review your settings! (Step fault)");
            m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] OPERATION COMPLETE!");
            return false;
        }
        boolean everything = false;
        boolean solids = false;
        if (((m_settings.getInt(player.getName().toLowerCase() + ".toId", -5)) < 0)) {
            player.sendMessage(ChatColor.RED + "Boxy settings fault. Please review your settings! (Type fault)");
            m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] OPERATION COMPLETE!");
            return false;
        } else if (m_settings.getInt(player.getName().toLowerCase() + ".fromId", -5) == -1) {
                everything = true;
        } else if (m_settings.getInt(player.getName().toLowerCase() + ".fromId", -5) == -2) {
                    solids = true;
        } else if (((m_settings.getInt(player.getName().toLowerCase() + ".fromId", -5)) < 0)) {
                        player.sendMessage(ChatColor.RED + "Boxy settings fault. Please review your settings! (Type fault)");
                        m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] OPERATION COMPLETE!");
                        return false;
                    }
        String exclusions[] = m_settings.getProperty(player.getName().toLowerCase() + ".exclude").toString().split(",");

        // FIXME Very inflexable Look at EnumMap!
        // HINT private EnumMap<Material, Boolean> m_exclusionList = new EnumMap<Material, Boolean>(Material.class);
        int exclusionsI[] = new int[512];
        exclusionsI[0] = 0;
        for (int i = 0; i < exclusions.length; i++) {
            exclusionsI[i] = Integer.parseInt(exclusions[i]);
        }
        if (start.getX() > end.getX()) {
            //force transition from south to north
            double tempDir = end.getX();
            end.setX(start.getX());
            start.setX(tempDir);
        }
        if (start.getZ() > end.getZ()) {
            //force transition from west to east
            double tempDir = end.getZ();
            end.setZ(start.getZ());
            start.setZ(tempDir);
        }
        start.setY(start.getY() + (m_settings.getInt(player.getName().toLowerCase() + ".height", 0)));
        if (start.getY() > end.getY()) {
            //force transition from bottom to top
            double tempDir = end.getY();
            end.setY(start.getY());
            start.setY(tempDir);
        }
        Location current = start.clone();
        boolean Excluded = false;
        if (everything) {
            player.sendMessage(ChatColor.BLUE + "everything");
            // FIXME double? coord are ints, try using CPosition rather than Location double variables cost alot more CPU time
            // Make sure you alter all of the variations or have a different class process the blocks accordingly
            for (double y = start.getY(); y <= end.getY(); y++) {
                for (double z = start.getZ(); z <= end.getZ(); z++) {
                    for (double x = start.getX(); x <= end.getX(); x++) {
                        current.setX(x);
                        // FIXME Move to appropriate location
                        current.setY(y);
                        // FIXME Move to appropriate location
                        current.setZ(z);
                        for (int e = 0; e < exclusions.length; e++) {
                            if ((world.getBlockTypeIdAt(current) == exclusionsI[e])
                                    || // FIXME Whats this all about? It isn't needed if the for loops are correct
                                    // HINT for (int x = start.getX(); x <= end.getX(); x += step) {
                                    (x % step != 0) || (y % step != 0) || (z % step != 0)) {
                                Excluded = true;
                                //player.sendMessage(ChatColor.BLUE + "excluded");
                            }
                        }
                        if (!Excluded) {
                            //player.sendMessage(ChatColor.BLUE + "not excluded: replaceing");
                            world.getBlockAt(current).setType(toMaterial);
                        }
                        Excluded = false;
                    }
                }
            }
        } else if (solids) {
                player.sendMessage(ChatColor.BLUE + "solids");
                for (double y = start.getY(); y <= end.getY(); y++) {
                    for (double z = start.getZ(); z <= end.getZ(); z++) {
                        for (double x = start.getX(); x <= end.getX(); x++) {
                            current.setX(x);
                            current.setY(y);
                            current.setZ(z);
                            for (int e = 0; e < exclusions.length; e++) {
                            if ((world.getBlockTypeIdAt(current) == exclusionsI[e]) || (!Solids(world.getBlockTypeIdAt(current))) || (x % step != 0) || (y % step != 0) || (z % step != 0)) {
                                    Excluded = true;
                                    //player.sendMessage(ChatColor.BLUE + "excluded");
                                }
                            }
                            if (!Excluded) {
                                //player.sendMessage(ChatColor.BLUE + "not excluded: replaceing");
                                world.getBlockAt(current).setType(toMaterial);
                            }
                            Excluded = false;
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.BLUE + "select group");
                for (double y = start.getY(); y <= end.getY(); y++) {
                    for (double z = start.getZ(); z <= end.getZ(); z++) {
                        for (double x = start.getX(); x <= end.getX(); x++) {
                            current.setX(x);
                            current.setY(y);
                            current.setZ(z);
                            for (int e = 0; e < exclusions.length; e++) {
                            if ((world.getBlockTypeIdAt(current) == exclusionsI[e]) || ((world.getBlockTypeIdAt(current) != fromMaterial.getId())) || (x % step != 0) || (y % step != 0) || (z % step != 0)) {
                                    Excluded = true;
                                    //player.sendMessage(ChatColor.BLUE + "excluded");
                                }
                            }
                            if (!Excluded) {
                                //player.sendMessage(ChatColor.BLUE + "not excluded: replaceing");
                                world.getBlockAt(current).setType(toMaterial);
                            }
                            Excluded = false;
                        }
                    }
                }
            }

        m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] BOXY OPERATION COMPLETE!");
                    return true;
                    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            // Must be a player to use these commands
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (m_plugin.getPermition(player, ".isVip") || m_plugin.getPermition(player, ".isAdmin")) {
                    if (args.length == 4) {
                        try {
                            m_settings.setProperty(player.getName().toLowerCase() + ".fromId", Integer.parseInt(args[0]));
                            m_settings.setProperty(player.getName().toLowerCase() + ".toId", Integer.parseInt(args[1]));
                            m_settings.setProperty(player.getName().toLowerCase() + ".step", Integer.parseInt(args[3]));
                            m_settings.setProperty(player.getName().toLowerCase() + ".height", Integer.parseInt(args[2]));
                            if (m_settings.getInt(player.getName().toLowerCase() + ".step", -1) < 0) {
                                sender.sendMessage(ChatColor.RED + "Invalid argument value");
                                setDefaultSettings(player.getName());
                                m_settings.save();
                            }
                        player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                        m_settings.save();
                        return true;
                        } catch (NumberFormatException ex) {
                    // Report an error if text entered is invalid
                    sender.sendMessage(ChatColor.RED + "Invalid argument value");
                    sender.sendMessage(ex.getMessage());
                    setDefaultSettings(player.getName());
                    m_settings.save();
                    return false;
                    }
                    } else {
                        try {
                        String boxyDetails[] = args[0].split(":");
                            if (boxyDetails[0].equals("h")) {
                                m_settings.setProperty(player.getName().toLowerCase() + ".height", Integer.parseInt(boxyDetails[1]));
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                            m_settings.save();
                                return true;
                        }
                            if (boxyDetails[0].equals("s")) {
                                m_settings.setProperty(player.getName().toLowerCase() + ".step", Integer.parseInt(boxyDetails[1]));
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                            m_settings.save();
                                return true;
                        }
                            if (boxyDetails[0].equals("e")) {
                                m_settings.setProperty(player.getName().toLowerCase() + ".exclude", boxyDetails[1]);
                            player.sendMessage(ChatColor.GREEN + "Boxy settings accepted");
                            m_settings.save();
                                return true;
                            }
                            if (boxyDetails[0].equals("help")) {
                                player.sendMessage(ChatColor.GREEN + "Boxy help:");
                                player.sendMessage(ChatColor.GREEN + "/boxy [from id/name] [to id/name] [height offset] [step]");
                                player.sendMessage(ChatColor.GREEN + "/boxy h:[height offset");
                                player.sendMessage(ChatColor.GREEN + "/boxy s:[stepping]");
                                player.sendMessage(ChatColor.GREEN + "/boxy e:[exclude block id (seperate many with commas)]");
                                player.sendMessage(ChatColor.GREEN + "/boxy (this enables / disables boxy)");
                                return true;
                            }
                            if (boxyDetails[0].equals("enable")) {
                                if (m_settings.getBoolean(player.getName().toLowerCase() + ".enabled", false)) {
                                    m_settings.setProperty(player.getName().toLowerCase() + ".enabled", false);
                                    player.sendMessage(ChatColor.GREEN + "Boxy disabled");
                                    m_settings.setProperty(player.getName().toLowerCase() + ".hasStart", false);
                                    m_settings.setProperty(player.getName().toLowerCase() + ".hasEnd", false);
                                    m_settings.save();
                                    return true;
                                } else {
                                    m_settings.setProperty(player.getName().toLowerCase() + ".enabled", true);
                                    player.sendMessage(ChatColor.GREEN + "Boxy enabled");
                                    m_settings.setProperty(player.getName().toLowerCase() + ".hasStart", false);
                                    m_settings.setProperty(player.getName().toLowerCase() + ".hasEnd", false);
                                    m_settings.setProperty(player.getName().toLowerCase() + ".exclude", "7");
                                    m_settings.save();
                                    return true;
                        }
                    }
                        } catch (NumberFormatException ex) {
                    // Report an error if text entered is invalid
                    sender.sendMessage(ChatColor.RED + "Invalid argument value");
                    sender.sendMessage(ex.getMessage());
                    setDefaultSettings(player.getName());
                    m_settings.save();
                    return false;
                    }
        }
                    player.sendMessage(ChatColor.RED + "Invalid Boxy command! Type Help for usage!");
                    return false;

                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use Boxy");
                    return true;
                }
            } else {
        //TODO Undo capabilities for Console?
		sender.sendMessage(ChatColor.RED + "Not implimented yet");
                return false;
            }
        }
		return false;
	}
}
