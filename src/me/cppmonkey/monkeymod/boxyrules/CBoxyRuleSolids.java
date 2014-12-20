/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.boxyrules;

import java.util.EnumMap;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.interfaces.IBoxyReplacer;
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

    public CBoxyRuleSolids( Material toMaterial) {
        m_toMaterial = toMaterial;
    }

    public boolean exclude(Block block) {
        if (m_exclusionsEnabled) {
            return m_exclusionList.containsKey(block.getType()) || !isSolid(block);
        }
        return !isSolid(block);
    }

    // FIXME Needs improvement. Materials are suppose to have isSolid()
    private boolean isSolid(Block block) {
        switch (block.getTypeId()) {
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

    public void setFrom(int descFromId) {
        // nothing to do
    }

    public void process(Block block) {
        block.setType(m_toMaterial);
    }
}
