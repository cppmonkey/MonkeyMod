package me.cppmonkey.monkeymod.boxy.rules;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

public class CBoxyRuleNormalWithData extends CBoxyRuleNormal {

    private MaterialData m_fromMaterialData;
    private MaterialData m_toMaterialData;

    public CBoxyRuleNormalWithData(Material fromMaterial, MaterialData fromData, Material toMaterial, MaterialData toData) {
        super(fromMaterial, toMaterial);
        m_fromMaterialData = fromData;
        m_toMaterialData = toData;
    }

    public boolean exclude(Block block) {
        // FIXME determine how to use MaterialData
        if (block.getData() == m_fromMaterialData.getData() && !super.exclude(block)) {
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
        block.setData(m_toMaterialData.getData());
    }
}
