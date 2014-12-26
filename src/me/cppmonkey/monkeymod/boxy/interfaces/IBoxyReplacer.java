/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.boxy.interfaces;

import org.bukkit.block.Block;

/**
 *
 * @author CppMonkey
 */
public interface IBoxyReplacer {

    public void setFrom(int descFromId);

    public boolean exclude(Block block);

    public String message();

    public void process(Block block);

    public void setExclusions(String[] exclusions);
}
