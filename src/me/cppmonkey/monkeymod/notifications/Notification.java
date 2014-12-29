package me.cppmonkey.monkeymod.notifications;

public abstract class Notification {
    private long m_timeStamp;// Timestamp

    public Notification(){
        m_timeStamp = 0; // TODO - Generate timestamp
    }
   
}
