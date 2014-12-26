package me.cppmonkey.monkeymod.listeners;
import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;


/**
 *
 * @author alex
 */
public class MonkeyModExplosionListener implements Listener {
    MonkeyMod m_plugin;

    public MonkeyModExplosionListener(MonkeyMod instance) {
        m_plugin = instance;
    }

    @EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Creeper) {
            event.blockList().clear(); // NO MORE EXPLOSIONS :D
        }
    }
}