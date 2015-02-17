/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.boxy.rules;

import java.util.EnumMap;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.boxy.interfaces.IBoxyReplacer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * 
 * @author CppMonkey
 */
public class CBoxyRuleSolids implements IBoxyReplacer {
    private Material m_toMaterial;
    private boolean m_exclusionsEnabled = false;
    private EnumMap<Material, Boolean> m_exclusionList = new EnumMap<Material, Boolean>(Material.class);

    public CBoxyRuleSolids(Material toMaterial) {
        m_toMaterial = toMaterial;
    }

    public boolean exclude(Block block) {
        if (m_exclusionsEnabled) {
            return m_exclusionList.containsKey(block.getType()) || !isSolid(block);
        }
        return !isSolid(block);
    }

    // FIXME Needs improvement. Materials are suppose to have isSolid() or !isLiquid()
    private boolean isSolid(Block block) {
        switch (block.getType()) {
        case WATER: // Formally block ID 8
        case STATIONARY_WATER: // Formally block ID 9
        case LAVA: // Formally block ID 10
        case STATIONARY_LAVA: // Formally block ID 11
        case FIRE: // Formally block ID 51
            return false;
        default:
            return true;
        }
    }

    public String message() {
        return ChatColor.BLUE + "solids";
    }

    public void setExclusions(String[] exclusions) {
        if (exclusions.length > 0) {
            m_exclusionsEnabled = true;

            // populate exclusions map
            for (String excl : exclusions) {
                try {
                    m_exclusionList.put(Material.matchMaterial(excl), true);
                } catch (NullPointerException e) {
                    MonkeyMod.log.info("[EXCEPTION] " + this.getClass().getName());
                    MonkeyMod.log.info(e.getMessage());
                }
            }
        }
    }

    public void setFrom(String descFromId) {
        // nothing to do
    }

    public void process(Block block) {
        block.setType(m_toMaterial);
    }
}
