package me.cppmonkey.monkeymod;

import java.util.Locale;

import me.cppmonkey.monkeymod.utils.CPosition;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author Alex
 */
public class BoxyExecutor {

    private final MonkeyMod m_plugin;

    public BoxyExecutor(MonkeyMod instance) {
        m_plugin = instance;
    }

    @Deprecated
    // user Material.isSolid()
    private boolean solids(int value) {
        switch (value) {
            case 0:
            case 8:
            case 9:
            case 10:
            case 11:
            case 51:
                return false;
            default:
                return true;
        }
    }

    // private EnumMap<Material, Boolean> m_exclusionList = new
    // EnumMap<Material, Boolean>(Material.class);

    public boolean playerBoxyClickEvent(Player player, Block block) {
        if (!m_plugin.getConfig().getBoolean(player.getName().toLowerCase(Locale.ENGLISH) + ".hasStart", false)) {
            // Start point selected
            m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasStart", true);
            m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".startLocation", block.getLocation().toVector());
            m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".startWorld", block.getWorld().getName());
            player.sendMessage(ChatColor.GREEN + "Boxy start point confirmed");
            return true;
        } else if (!m_plugin.getConfig().getBoolean(player.getName().toLowerCase(Locale.ENGLISH) + ".hasEnd", false)) {
            // end point selected
                m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasEnd", true);
            m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".endLocation", block.getLocation().toVector());
                m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".endWorld", block.getWorld().getName());
                player.sendMessage(ChatColor.GREEN + "Boxy end point confirmed");
                if (!m_plugin.getConfig().getString(player.getName().toLowerCase(Locale.ENGLISH) + ".startWorld", "FAIL").matches(block.getWorld().getName())) {
                    player.sendMessage(ChatColor.RED + "Cannot perform boxy across worlds! Aborted operation.");
                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasStart", false);
                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasEnd", false);
                    return true;
                }

                player.sendMessage(ChatColor.GOLD + "Caution! You are about to commit a Boxy alteration!");
                player.sendMessage(ChatColor.RED + "RIGHT CLICK TO COMMIT! right click elsewhere to cancel!");
                return true;
        } else if (m_plugin.getConfig().getVector(player.getName().toLowerCase(Locale.ENGLISH) + ".endLocation") == block.getLocation().toVector() /* TODO compare vectors */) {
            // end point confirmed and boxy committed
                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasStart", false);
                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasEnd", false);
                    boxyOperation(player, block);
                    return true;
                } else {
                    // boxy aborted
                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasStart", false);
                    m_plugin.getConfig().set(player.getName().toLowerCase(Locale.ENGLISH) + ".hasEnd", false);
                    player.sendMessage(ChatColor.GREEN + "Boxy alteration aborted");
                    return true;
                }

    }

    private boolean boxyOperation(Player player, Block block) {
        m_plugin.getServer().broadcastMessage(ChatColor.RED + "[SERVER] WARNING! BOXY OPERATION IN PROGRES!");
        m_plugin.getServer().broadcastMessage(ChatColor.RED + "[SERVER] PLEASE STOP WHAT YOU ARE DOING, AND EXPECT LAG!");
        m_plugin.getServer().broadcastMessage(ChatColor.GOLD + "[SERVER] Processing...");
        int toMaterial;
        int fromMaterial;
        toMaterial = m_plugin.getConfig().getInt(player.getName().toLowerCase(Locale.ENGLISH) + ".toId", 51);
        fromMaterial = m_plugin.getConfig().getInt(player.getName().toLowerCase(Locale.ENGLISH) + ".fromId", 51);
        if (toMaterial == Material.FIRE.getId() || fromMaterial == Material.FIRE.getId()) {
            player.sendMessage(ChatColor.RED + "Boxy settings fault. Please review your settings!");
            m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] OPERATION COMPLETE!");
            return false;
        }

        World world = player.getWorld();
        Location end = block.getLocation();
        Location start;
        Vector startLoc = m_plugin.getConfig().getVector(player.getName().toLowerCase(Locale.ENGLISH) + ".startLocation");

        start = startLoc.toLocation(world);
        start.setY(start.getY() + 1);

        int step = m_plugin.getConfig().getInt(player.getName().toLowerCase(Locale.ENGLISH) + ".step", 1);
        if (step <= 0) {
            player.sendMessage(ChatColor.RED + "Boxy settings fault. Please review your settings! (Step fault)");
            m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] OPERATION COMPLETE!");
            return false;
        }
        boolean everything = false;
        boolean solids = false;
        if (m_plugin.getConfig().getInt(player.getName().toLowerCase(Locale.ENGLISH) + ".toId", -5) < 0) {
            player.sendMessage(ChatColor.RED + "Boxy settings fault. Please review your settings! (Type fault)");
            m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] OPERATION COMPLETE!");
            return false;
        } else if (m_plugin.getConfig().getInt(player.getName().toLowerCase(Locale.ENGLISH) + ".fromId", -5) == -1) {
            everything = true;
        } else if (m_plugin.getConfig().getInt(player.getName().toLowerCase(Locale.ENGLISH) + ".fromId", -5) == -2) {
            solids = true;
        } else if (m_plugin.getConfig().getInt(player.getName().toLowerCase(Locale.ENGLISH) + ".fromId", -5) < 0) {
            player.sendMessage(ChatColor.RED + "Boxy settings fault. Please review your settings! (Type fault)");
            m_plugin.getServer().broadcastMessage(ChatColor.GREEN + "[SERVER] OPERATION COMPLETE!");
            return false;
        }
        String exclusions[] = m_plugin.getConfig().getString(player.getName().toLowerCase(Locale.ENGLISH) + ".exclude").split(",");

        // FIXME Very inflexible Look at EnumMap!
        // HINT private EnumMap<Material, Boolean> m_exclusionList = new
        // EnumMap<Material, Boolean>(Material.class);
        int exclusionsI[] = new int[512];
        exclusionsI[0] = 0;
        for (int i = 0; i < exclusions.length; i++) {
            exclusionsI[i] = Integer.parseInt(exclusions[i]);
        }
        if (start.getX() > end.getX()) {
            // force transition from south to north
            double tempDir = end.getX();
            end.setX(start.getX());
            start.setX(tempDir);
        }
        if (start.getZ() > end.getZ()) {
            // force transition from west to east
            double tempDir = end.getZ();
            end.setZ(start.getZ());
            start.setZ(tempDir);
        }
        start.setY(start.getY() + (m_plugin.getConfig().getInt(player.getName().toLowerCase(Locale.ENGLISH) + ".height", 0)));
        if (start.getY() > end.getY()) {
            // force transition from bottom to top
            double tempDir = end.getY();
            end.setY(start.getY());
            start.setY(tempDir);
        }
        Location current = start.clone();
        boolean Excluded = false;
        if (everything) {
            player.sendMessage(ChatColor.BLUE + "everything");
            CPosition position = new CPosition(0, 0, 0);
            for (position.setY( start.getBlockY()); position.getY() <= end.getY(); position.setY(position.getY() + step)) {
                current.setY(position.getY());
                for (position.setZ( start.getBlockZ()); position.getZ() <= end.getZ(); position.setZ(position.getZ() + step)) {
                    current.setZ(position.getZ());
                    for (position.setX( start.getBlockX()); position.getX() <= end.getX(); position.setX(position.getX() + step)) {
                        current.setX(position.getX());
                        for (int e = 0; e < exclusions.length; e++) {
                            if (world.getBlockTypeIdAt(current) == exclusionsI[e]) {
                                Excluded = true;
                                // player.sendMessage(ChatColor.BLUE +
                                // "excluded");
                            }
                        }
                        if (!Excluded) {
                            // player.sendMessage(ChatColor.BLUE +
                            // "not excluded: replacing");
                            world.getBlockAt(current).setTypeId(toMaterial);
                        }
                        Excluded = false;
                    }
                }
            }
        } else if (solids) {
            player.sendMessage(ChatColor.BLUE + "solids");
            CPosition position = new CPosition(0, 0, 0);
            for (position.setY( start.getBlockY()); position.getY() <= end.getY(); position.setY(position.getY() + step)) {
                current.setY(position.getY());
                for (position.setZ( start.getBlockZ()); position.getZ() <= end.getZ(); position.setZ(position.getZ() + step)) {
                    current.setZ(position.getZ());
                    for (position.setX( start.getBlockX()); position.getX() <= end.getX(); position.setX(position.getX() + step)) {
                        current.setX(position.getX());
                        for (int e = 0; e < exclusions.length; e++) {
                            if (world.getBlockTypeIdAt(current) == exclusionsI[e] || !solids(world.getBlockTypeIdAt(current))) {
                                Excluded = true;
                                // player.sendMessage(ChatColor.BLUE +
                                // "excluded");
                            }
                        }
                        if (!Excluded) {
                            // player.sendMessage(ChatColor.BLUE +
                            // "not excluded: replacing");
                            world.getBlockAt(current).setTypeId(toMaterial);
                        }
                        Excluded = false;
                    }
                }
            }
        } else {
            player.sendMessage(ChatColor.BLUE + "select group");
            CPosition position = new CPosition(0, 0, 0);
            for (position.setY( start.getBlockY()); position.getY() <= end.getY(); position.setY(position.getY() + step)) {
                current.setY(position.getY());
                for (position.setZ( start.getBlockZ()); position.getZ() <= end.getZ(); position.setZ(position.getZ() + step)) {
                    current.setZ(position.getZ());
                    for (position.setX( start.getBlockX()); position.getX() <= end.getX(); position.setX(position.getX() + step)) {
                        current.setX(position.getX());
                        for (int e = 0; e < exclusions.length; e++) {
                            if (world.getBlockTypeIdAt(current) == exclusionsI[e] || world.getBlockTypeIdAt(current) != fromMaterial) {
                                Excluded = true;
                                // player.sendMessage(ChatColor.BLUE +
                                // "excluded");
                            }
                        }
                        if (!Excluded) {
                            // player.sendMessage(ChatColor.BLUE +
                            // "not excluded: replacing");
                            world.getBlockAt(current).setTypeId(toMaterial);
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
