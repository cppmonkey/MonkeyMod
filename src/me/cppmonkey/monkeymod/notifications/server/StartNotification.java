package me.cppmonkey.monkeymod.notifications.server;

import org.bukkit.Bukkit;

import me.cppmonkey.monkeymod.notifications.Notification;

public class StartNotification extends Notification {
    public StartNotification() {
        Bukkit.getDefaultGameMode(); // TODO Must query this value
    }
}
