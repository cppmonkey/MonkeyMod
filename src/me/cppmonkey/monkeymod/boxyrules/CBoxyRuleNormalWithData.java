package me.cppmonkey.monkeymod.boxyrules;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CBoxyRuleNormalWithData extends CBoxyRuleNormal {

    private byte m_fromMaterialData;
    private byte m_toMaterialData;

    public CBoxyRuleNormalWithData(int fromId, int fromData, Material toMaterial, int toData) {
        super(fromId, toMaterial);
        m_fromMaterialData = (byte) fromData;
        m_toMaterialData = (byte) toData;
    }

    public boolean exclude(Block block) {
        if (block.getData() == m_fromMaterialData && !super.exclude(block)) {
            return false;
        } else {
            return true;
        }
    }

    public String message() {
        return ChatColor.BLUE + "normal with data";
    }

    public void process(Block block) {
        block.setType(super.m_toMaterial);
        block.setData(m_toMaterialData);
    }
}
