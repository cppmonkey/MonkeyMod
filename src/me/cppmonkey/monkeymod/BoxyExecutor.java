package me.cppmonkey.monkeymod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.block.Block;
import org.bukkit.World;
import org.bukkit.Location;

/**
 *
 * @author Alex
 */
public class BoxyExecutor {
    private final MonkeyMod m_plugin;
    private final Configuration m_settings;

    public BoxyExecutor(MonkeyMod instance) {
        m_plugin = instance;
        m_settings = m_plugin.getPluginConfiguration(MonkeyMod.EConfig.BOXY);
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
            m_settings.setProperty(player.getName().toLowerCase() + ".startWorld", block.getWorld().getName().toString());
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
        //private EnumMap<Material, Boolean> m_exclusionList = new EnumMap<Material, Boolean>(Material.class);
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
            CPosition position = new CPosition(0, 0, 0);
            for (position.setY((int) start.getY()); position.getY() <= end.getY(); position.setY(position.getY() + step)) {
                current.setY(position.getY());
                for (position.setZ((int) start.getZ()); position.getZ() <= end.getZ(); position.setZ(position.getZ() + step)) {
                    current.setZ(position.getZ());
                    for (position.setX((int) start.getX()); position.getX() <= end.getX(); position.setX(position.getX() + step)) {
                        current.setX(position.getX());
                        for (int e = 0; e < exclusions.length; e++) {
                            if (world.getBlockTypeIdAt(current) == exclusionsI[e]) {
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
            CPosition position = new CPosition(0, 0, 0);
            for (position.setY((int) start.getY()); position.getY() <= end.getY(); position.setY(position.getY() + step)) {
                current.setY(position.getY());
                for (position.setZ((int) start.getZ()); position.getZ() <= end.getZ(); position.setZ(position.getZ() + step)) {
                    current.setZ(position.getZ());
                    for (position.setX((int) start.getX()); position.getX() <= end.getX(); position.setX(position.getX() + step)) {
                        current.setX(position.getX());
                        for (int e = 0; e < exclusions.length; e++) {
                            if ((world.getBlockTypeIdAt(current) == exclusionsI[e]) || (!Solids(world.getBlockTypeIdAt(current)))) {
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
            CPosition position = new CPosition(0, 0, 0);
            for (position.setY((int) start.getY()); position.getY() <= end.getY(); position.setY(position.getY() + step)) {
                current.setY(position.getY());
                for (position.setZ((int) start.getZ()); position.getZ() <= end.getZ(); position.setZ(position.getZ() + step)) {
                    current.setZ(position.getZ());
                    for (position.setX((int) start.getX()); position.getX() <= end.getX(); position.setX(position.getX() + step)) {
                        current.setX(position.getX());
                        for (int e = 0; e < exclusions.length; e++) {
                            if ((world.getBlockTypeIdAt(current) == exclusionsI[e]) || ((world.getBlockTypeIdAt(current) != fromMaterial.getId()))) {
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
}
