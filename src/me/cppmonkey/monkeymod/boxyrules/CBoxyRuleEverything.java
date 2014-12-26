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
public class CBoxyRuleEverything implements IBoxyReplacer {

    private Material m_toMaterial;
    public CBoxyRuleEverything( Material blockTo ){
        m_toMaterial = blockTo;
    }

    private boolean m_exclusionsEnabled = false;
    private EnumMap<Material, Boolean> m_exclusionList = new EnumMap<Material, Boolean>(Material.class);

    public boolean exclude(Block block) {
        // This is for everything, nothing is to be excluded
        if (m_exclusionsEnabled) {
            return m_exclusionList.containsKey(block.getType());
        }
        return false;
    }

    public String message() {
        return ChatColor.BLUE + "everything";
    }

    public void setExclusions(String[] exclusions) {
        if (exclusions.length > 0) {
            m_exclusionsEnabled = true;

            // populate exclusions map
            for (String excl : exclusions) {
                try {
                    m_exclusionList.put(Material.matchMaterial(excl), true);
                } catch (NullPointerException e) {
                    MonkeyMod.reportException("[EXCEPTION] " + this.getClass().getName(), e);
                }
            }
        }
    }

    public void setFrom(int descFromId) {
        // Nothing to do
    }

    public void process(Block block) {
        block.setType(m_toMaterial);
    }
}
