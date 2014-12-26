/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod;

/**
 *
 * @author CppMonkey
 */
public class Parm {

    public Parm(String name, String value) {
        this.name = name;
        this.value = value;
    }
    public Parm(String name, Integer serverUID) {
    this.name = name;
        this.value = Integer.toString(serverUID);
    }
    public String name;
    public String value;
    public void setValue(String string) {
        this.value = string;
    }
}
