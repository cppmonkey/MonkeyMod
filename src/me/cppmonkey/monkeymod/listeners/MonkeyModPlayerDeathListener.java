/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import me.cppmonkey.monkeymod.MonkeyMod;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

/**
 *
 * @author CppMonkey
 */
@Deprecated // Please use inbuilt enum for this purpose, even though I have added more error reporting
enum e_monsters {

    CraftZombie, CraftCreeper, CraftSpider, CraftPigZombie, CraftSlime, CraftGiant, CraftGhast, CraftSkeleton, CraftFireball, CraftCaveSpider, CraftEnderman, CraftSilverfish, CraftBlaze, CraftEnderDragon, CraftMagmaCube, novalue;

    public static e_monsters fromString(String Str) {
        try {
            return valueOf(Str);
        } catch (RuntimeException rex){
            MonkeyMod.reportException("RuntimeExcption within e_monsters.fromString()", rex);
        } catch (Exception ex) {
            MonkeyMod.reportException("Exception within e_monsters.fromString()", ex);
        }
        return novalue;
    }
};

public class MonkeyModPlayerDeathListener implements Listener {
    HashMap<String, String> playerMap;

    public MonkeyModPlayerDeathListener(MonkeyMod instance) {
        playerMap = new HashMap<String, String>();
    }

    private String deathDescription(EntityDeathEvent event) {
        try{
            DamageCause cause = event.getEntity().getLastDamageCause().getCause();
            if (cause == DamageCause.CONTACT) {
                String m_cactusDeath[] = {
                    " hugged a cactus.",
                    " was pricked to death.",
                    " fought a cactus... and didn't win.",
                    " shoved a cactus... but the cactus shoved back.",
                    " thought a cactus was something less dangerous..."
                };
                int randomNum =  (int) Math.round(Math.random() * m_cactusDeath.length + 0.5f); // Includes rounding up/down
                return m_cactusDeath[randomNum];
            }else if (cause == DamageCause.ENTITY_ATTACK) {
                Player player = (Player) event.getEntity();

                //Entity killer = player.getLastDamageCause().getEntity();
                String killer = playerMap.get(player.getName());
                String killerDetails[] = killer.split(":");
                if (killerDetails.length > 1) {
                    if("WOLF".equalsIgnoreCase(killerDetails[0])) {
                        return " was mauled to  death by " + killerDetails[1] + "'s wolf.";
                    }
                    if("SHOT".equalsIgnoreCase(killerDetails[0])) {
                        return " was shot by " + killerDetails[2] + "'s " + killerDetails[1];
                    }
                    if("PVP".equalsIgnoreCase(killerDetails[0])) {
                        return " was killed by " + killerDetails[2] + ", using a " + killerDetails[1];
                    }
                } else {
                    String output = "";
                    switch (e_monsters.fromString(killer)) {
                        case CraftZombie:
                            output = " got their brain eaten.";
                            break;
                        case CraftCreeper:
                            output = " saw a sad, green face... Briefly.";
                            break;
                        case CraftSpider:
                            output = " wasn't carrying spider repellent.";
                            break;
                        case CraftPigZombie:
                            output = " upset a Pig Zombie.";
                            break;
                        case CraftSlime:
                            output = " got slimed.";
                            break;
                        case CraftGiant:
                            output = " had their bones ground into bread.";
                            break;
                        case CraftGhast:
                            output = " was spotted by a ghast.";
                            break;
                        case CraftSkeleton:
                            output = " was assassinated by a skeletal archer.";
                            break;
                        case CraftFireball:
                            output = " should have called ghastbusters.";
                            break;
                        case CraftCaveSpider:
                            output = " thought the little blue spiders were passive.";
                            break;
                        case CraftEnderman:
                            output = " got picked up, and taken to the End for supper.";
                            break;
                        case CraftSilverfish:
                            output = " had a silver-fish burrow into them.";
                            break;
                        case CraftBlaze:
                            output = " got blazed.";
                            break;
                        case CraftEnderDragon:
                            output = " should have trained their dragon better.";
                            break;
                        case CraftMagmaCube:
                            output = " was jumped on by animated lava.";
                            break;
                        default:
                            output = " was killed by an angry mob.";
                            break;
                    }
                    return output;
                }
            }else if (cause == DamageCause.FALL) {
                String m_fallDeath[] = {
                    " went bungee jumping... without the bungee.",
                    " has poor depth perception.",
                    " wasn't wearing spring shoes.",
                    " believed they could fly... but they couldn't",
                    " didn't watch where they were going.",
                    " believed that gravity is optional.",
                    " lost their footing.",
                    " got owned by Newton.",
                    " should have used a ladder.",
                    " saw something shiny... a long way down",
                    " fell to their death"
                };
                int randomNum =  (int) Math.round(Math.random() * m_fallDeath.length + 0.5f); // Includes rounding up/down
                return m_fallDeath[randomNum];
            }else if (cause == DamageCause.FIRE) {
                String m_fireDeath[] = {
                    " was on fire... literally.",
                    " played with matches.",
                    " is now crispy.",
                    " put their head in a furnace.",
                    " smells like cooked pork.",
                    " cooked themselves.",
                    " stood in a fire.",
                    " thought they were a marshmallow.",
                    " is medium-rare.",
                    " thought they were the Human Torch.",
                    " died in a fire"
                };
                    int randomNum =  (int) Math.round(Math.random() * (m_fireDeath.length - 1)); // Includes rounding up/down
                return m_fireDeath[randomNum];
            }else if (cause == DamageCause.FIRE_TICK) {
                String m_fireTickDeath[] = {
                    " should have had a water-bucket.",
                    " didn't find water fast enough.",
                    " should have worn less-flammable clothing.",
                    " could have used some rain.",
                    " played with matches.",
                    " cooked themselves... slowly.",
                    " realised far too late that fire is bad.",
                    " slow-cooked themselves.",
                    " knows how the skellingtons feel now.",
                    " needed water.",
                    " died while on fire."
                };
                    int randomNum =  (int) Math.round(Math.random() * (m_fireTickDeath.length - 1)); // Includes rounding up/down
                return m_fireTickDeath[randomNum];
            }else if (cause == DamageCause.LAVA) {
                String m_lavaDeath[] = {
                    " took a VERY hot bath.",
                    " didn't realise how hot lava was.",
                    " liked the look of lava.",
                    " didn't realise what the hot red stuff was.",
                    " went swimming in lava.",
                    " did the burning backstroke.",
                    " needed less 10 and more 8.",
                    " is now encased in molten rock.",
                    " thought a boat would be fine on lava.",
                    " wanted obsidian... but did it wrong.",
                    " burnt in lava."
                };
                    int randomNum =  (int) Math.round(Math.random() * (m_lavaDeath.length - 1)); // Includes rounding up/down
                return m_lavaDeath[randomNum];
            }else if (cause == DamageCause.DROWNING) {
                String m_drowningDeath[] = {
                    " is sleeping with the fishes.",
                    " couldn't hold their breath.",
                    " needed a life-jacket.",
                    " the RNLI didn't get to them in time.",
                    " wasn't a strong swimmer.",
                    " forgot their snorkel.",
                    " thought they had gills.",
                    " got taken by Davy Jones.",
                    " should have used a boat.",
                    " thought they were a fish.",
                    " drowned"
                };
                    int randomNum =  (int) Math.round(Math.random() * (m_drowningDeath.length - 1)); // Includes rounding up/down
                return m_drowningDeath[randomNum];
            }else if (cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION) {
                String m_explosionDeath[] = {
                    " didn't realise what the hissing sound was.",
                    " is in several pieces.",
                    " should have stayed further away from gunpowder.",
                    " heard 'HHSSsssss'.",
                    " went boom.",
                    " was exploded.",
                    " is now in orbit.",
                    " didn't understand the term 'blast-radius' means.",
                    " should have stayed further away from the big explosion.",
                    " got blown up."
                };
                    int randomNum =  (int) Math.round(Math.random() * (m_explosionDeath.length - 1)); // Includes rounding up/down
                return m_explosionDeath[randomNum];
            }else if (cause == DamageCause.VOID) {
                String m_voidDeath[] = {
                    " fell into nothingness.",
                    " made it past bedrock; it wasn't worth it.",
                    " should have stayed above bedrock.",
                    " didn't get on with the Void.",
                    " is another victim of the Void.",
                    " realised why people avoid the Void.",
                    " fell into nothingness.",
                    " went looking for the nether, but found nothing.",
                    " got Voided.",
                    " thought bedrock was there for show.",
                    " fell into the void."
                };
                    int randomNum =  (int) Math.round(Math.random() * (m_voidDeath.length - 1)); // Includes rounding up/down
                return m_voidDeath[randomNum];
            }else if (cause == DamageCause.CUSTOM) {
                String m_customDeath[] = {
                    " encountered MISSINGNO.",
                    " died in mysterious circumstances.",
                    " was killed by something unusual.",
                    " died at the hands of an unknown source"
                };
                    int randomNum =  (int) Math.round(Math.random() * (m_customDeath.length - 1)); // Includes rounding up/down
                return m_customDeath[randomNum];
            }
        }catch (RuntimeException rex){
            MonkeyMod.reportException("RuntimeException within MonkeyModPlayerDeathListener()", rex);
        }catch (Exception ex){
            MonkeyMod.reportException("Exception within MonkeyModPlayerDeathListener()", ex);
        }

        return " died!";
    }

    @EventHandler
    public void onEntityDeath(PlayerDeathEvent event) {
        PlayerDeathEvent playerevent = (PlayerDeathEvent)event;
        playerevent.setDeathMessage(ChatColor.GOLD + event.getEntity().getName() + deathDescription(event));

        java.util.List<ItemStack> itemStack = playerevent.getDrops();

        Location location = playerevent.getEntity().getLocation();

        Block block = playerevent.getEntity().getWorld().getBlockAt(location);

        block.setType(Material.CHEST);
        Chest chest = (Chest) block.getState();
        Inventory chestInvetory = chest.getInventory();

        Iterator<ItemStack> itemIterator = itemStack.iterator();

        while (itemIterator.hasNext()){
            ItemStack items = itemIterator.next();
            chestInvetory.addItem(items.clone());
        }

        playerevent.getDrops().clear();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            lastDamage(player, event);
        }
    }

    String lastDamage(Player player, EntityDamageEvent event) {
        String lastDamage = "";

        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent mobEvent = (EntityDamageByEntityEvent) event;

            if (mobEvent.getDamager() instanceof Arrow) {
                Arrow projectile = (Arrow) mobEvent.getDamager();

                ProjectileSource projectileSource = projectile.getShooter();

                if (projectileSource instanceof Player) {
                    Player murderer = (Player) projectileSource;
                    String usingitem = murderer.getItemInHand().getType().name();
                    if (usingitem.equalsIgnoreCase("AIR")) {
                        usingitem = "bare hands";
                    }
                    lastDamage = "SHOT:" + usingitem + ":" + murderer.getName();
                }

            } else {
                Entity attacker = mobEvent.getDamager();
                if (attacker instanceof Wolf) {
                    Wolf thisWolf = (Wolf) attacker;
                    lastDamage = "WOLF:" + thisWolf.getOwner();
                } else if (attacker instanceof Player) {
                    Player murderer = (Player) attacker;
                    String usingItem = murderer.getItemInHand().getType().name();
                    if (usingItem.equalsIgnoreCase("AIR")) {
                        usingItem = "bare hands";
                    }
                    usingItem = usingItem.toLowerCase(Locale.ENGLISH);
                    usingItem = usingItem.replace("_", " ");
                    lastDamage = "PVP:" + usingItem + ":" + murderer.getName();
                } else {
                    // Last resort
                    lastDamage = attacker.getClass().getSimpleName();
                }
            }
        }
        if (playerMap.containsKey(player.getName())) {
            playerMap.remove(player.getName());
            playerMap.put(player.getName(), lastDamage);
        } else {
            playerMap.put(player.getName(), lastDamage);
        }
        return lastDamage;
    }
}
