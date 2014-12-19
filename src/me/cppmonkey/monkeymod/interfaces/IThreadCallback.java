package me.cppmonkey.monkeymod.interfaces;

public abstract interface IThreadCallback {

    public abstract void processLine(String result);

	public abstract void complete();
}
