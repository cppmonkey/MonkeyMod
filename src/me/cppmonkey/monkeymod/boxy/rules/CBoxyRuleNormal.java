package me.cppmonkey.monkeymod.boxy.rules;

import java.util.EnumMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import me.cppmonkey.monkeymod.MonkeyMod;
import me.cppmonkey.monkeymod.boxy.interfaces.IBoxyReplacer;

public class CBoxyRuleNormal implements IBoxyReplacer {

    private Material m_fromMaterial;
    protected Material m_toMaterial;
    private boolean m_exclusionsEnabled = false;
    private EnumMap<Material, Boolean> m_exclusionList = new EnumMap<Material, Boolean>(Material.class);

    public CBoxyRuleNormal(int fromId, Material toMaterial) {
        m_fromMaterial = Material.getMaterial(fromId);
        m_toMaterial = toMaterial;
    }
    public boolean exclude(Block block) {
        if (block.getType() == m_fromMaterial && (m_exclusionsEnabled && !m_exclusionList.containsKey(block.getType()))) {
            return false;
        } else {
            return true;
        }
    }

    public String message() {
        return ChatColor.BLUE + "normal";
    }

    public void setExclusions(String[] exclusions) {
        if (exclusions != null && exclusions.length > 0) {
            m_exclusionsEnabled = true;

            // populate exclusions map
            for (String excl : exclusions) {
                try {
                    m_exclusionList.put(Material.matchMaterial(excl), true);
                    System.out.println("put: exclude " + excl + " " + Material.matchMaterial(excl).name());
                }catch (RuntimeException rex){
                    MonkeyMod.reportException("RuntimeExcption within BoxyRuleNormal.setExclusions()", rex);
                }catch (Exception ex) {
                    MonkeyMod.reportException("Exception within BoxyRuleNormal.setExclusions()", ex);
                }
            }
        }
    }

    public void setFrom(int descFromId) {
        m_fromMaterial = Material.getMaterial(descFromId);
        System.out.println(m_fromMaterial.name());
    }

    public void process(Block block) {
        block.setType(m_toMaterial);
    }

}
