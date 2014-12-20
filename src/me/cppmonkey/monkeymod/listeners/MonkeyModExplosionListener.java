package me.cppmonkey.monkeymod.listeners;

import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.ListIterator;
import org.bukkit.entity.Entity;

/**
 *
 * @author alex
 */
public class MonkeyModExplosionListener extends EntityListener{
    MonkeyMod m_plugin;

    public MonkeyModExplosionListener(MonkeyMod instance) {
        m_plugin = instance;
    }
     public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntity().toString() == "CraftCreeper"){
            Creeper killer = (Creeper)event.getEntity();
            List<Entity> damageees = event.getEntity().getNearbyEntities(5, 5, 5);
            ListIterator itr = damageees.listIterator();

            while(itr.hasNext()) {
                Entity entity = (Entity)itr.next();
                if(entity.getClass().getSimpleName().equalsIgnoreCase("CraftChicken")){Chicken newEntity = (Chicken)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftCow"))){Cow newEntity = (Cow)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftCreeper"))){Creeper newEntity = (Creeper)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftGhast"))){Ghast newEntity = (Ghast)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftGiant"))){Giant newEntity = (Giant)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftPig"))){Pig newEntity = (Pig)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftPigZombie"))){PigZombie newEntity = (PigZombie)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftSheep"))){Sheep newEntity = (Sheep)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftSkeleton"))){Skeleton newEntity = (Skeleton)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftSlime"))){Slime newEntity = (Slime)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftSpider"))){Spider newEntity = (Spider)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftSquid"))){Squid newEntity = (Squid)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftZombie"))){Zombie newEntity = (Zombie)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftWolf"))){Wolf newEntity = (Wolf)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftPlayer"))){Player newEntity = (Player)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftCaveSpider"))){CaveSpider newEntity = (CaveSpider)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftEnderman"))){Enderman newEntity = (Enderman)entity;newEntity.damage(5, killer);}
                if((entity.getClass().getSimpleName().equalsIgnoreCase("CraftSilverfish"))){Silverfish newEntity = (Silverfish)entity;newEntity.damage(5, killer);}
                //TODO: Add new mobs to list!!!
            }
            event.getEntity().remove();
            event.setCancelled(true); //NO MORE EXPLOSIONS :D
        }
    }
}