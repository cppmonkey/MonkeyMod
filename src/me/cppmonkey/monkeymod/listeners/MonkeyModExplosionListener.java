package me.cppmonkey.monkeymod.listeners;

import java.util.List;
import java.util.ListIterator;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

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
     public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof Creeper) {
            Creeper killer = (Creeper) event.getEntity();
            List<Entity> damageees = event.getEntity().getNearbyEntities(5, 5, 5);
            ListIterator<Entity> itr = damageees.listIterator();

            while (itr.hasNext()) {
                Entity entity = (Entity) itr.next();
                if (entity instanceof LivingEntity) {
                    LivingEntity newEntity = (LivingEntity) entity;
                    newEntity.damage(5, killer);
            }
            }

            event.getEntity().remove();
            event.setCancelled(true); // NO MORE EXPLOSIONS :D
        }
    }
}