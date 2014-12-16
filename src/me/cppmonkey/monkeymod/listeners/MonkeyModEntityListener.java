/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cppmonkey.monkeymod.listeners;

import me.cppmonkey.monkeymod.MonkeyMod;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

/**
 *
 * @author CppMonkey
 */
public class MonkeyModEntityListener extends EntityListener {

    MonkeyMod m_plugin;

    public MonkeyModEntityListener(MonkeyMod instance) {
        m_plugin = instance;
    }

    private String DeathDescription(EntityDeathEvent event) {
        double randomNum = Math.random() * 10;
        String cause = event.getEntity().getLastDamageCause().getCause().toString();
        String output;
        if (cause.equalsIgnoreCase("contact")) {
            switch ((int) randomNum) {
                case 0:
                    output = " hugged a cactus.";
                    break;
                case 1:
                    output = " was pricked to death.";
                    break;
                case 2:
                    output = " fought a cactus... and didnt win.";
                    break;
                case 3:
                    output = " shoved a cactus... but the cactus shoved back.";
                    break;
                case 4:
                    output = " thought a cactus was something less dangerous...";
                    break;
                case 5:
                    output = " hugged a cactus";
                    break;
                case 6:
                    output = " was pricked to death.";
                    break;
                case 7:
                    output = " fought a cactus... and didnt win";
                    break;
                case 8:
                    output = " shoved a cactus... but the cactus shoved back.";
                    break;
                case 9:
                    output = " thought a cactus was something less dangerous...";
                    break;
                default:
                    output = " hugged a cactus";
                    break;

            }
            return output;
        }
        if (cause.equalsIgnoreCase("entity_attack")) {
            // get the entity details
            //TODO: Get the entity type of the killer
            output = " was killed by a creature, of some kind";
            return output;
        }
        if (cause.equalsIgnoreCase("fall")) {
            switch ((int) randomNum) {
                case 0:
                    output = " went bungie jumping... without the bungie.";
                    break;
                case 1:
                    output = " has poor depth perception.";
                    break;
                case 2:
                    output = " wasn't wearning spring shoes.";
                    break;
                case 3:
                    output = " belived they could fly... but they couldn't";
                    break;
                case 4:
                    output = " didnt watch where they were going.";
                    break;
                case 5:
                    output = " bellived that gravity is optional.";
                    break;
                case 6:
                    output = " lost their footing.";
                    break;
                case 7:
                    output = " got owned by Newton.";
                    break;
                case 8:
                    output = " should have used a ladder.";
                    break;
                case 9:
                    output = " saw something shiny... a long way down";
                    break;
                default:
                    output = " fell to their death";
                    break;

        }
            return output;
        }
        if (cause.equalsIgnoreCase("fire")) {
            switch ((int) randomNum) {
                case 0:
                    output = " was on fire... literaly.";
                    break;
                case 1:
                    output = " played with matches.";
                    break;
                case 2:
                    output = " is now crispy.";
                    break;
                case 3:
                    output = " put their head in a furnace.";
                    break;
                case 4:
                    output = " smells like cooked pork.";
                    break;
                case 5:
                    output = " cooked themselves.";
                    break;
                case 6:
                    output = " stood in a fire.";
                    break;
                case 7:
                    output = " thought they were a marshmellow.";
                    break;
                case 8:
                    output = " is medium-rare.";
                    break;
                case 9:
                    output = " thought they were the Human Torch.";
                    break;
                default:
                    output = " died in a fire";
                    break;
            }
            return output;
        }

        if (cause.equalsIgnoreCase("fire_tick")) {
            switch ((int) randomNum) {
                case 0:
                    output = " should have had a water-bucket.";
                    break;
                case 1:
                    output = " didnt find water fast enough.";
                    break;
                case 2:
                    output = " should have worn less-flamable clothing.";
                    break;
                case 3:
                    output = " could have used some rain.";
                    break;
                case 4:
                    output = " played with matches.";
                    break;
                case 5:
                    output = " cooked themselves... slowly.";
                    break;
                case 6:
                    output = " realised far too late that fire is bad.";
                    break;
                case 7:
                    output = " slow-cooked themselves.";
                    break;
                case 8:
                    output = " knows how the skellingtons feel now.";
                    break;
                case 9:
                    output = " needed water.";
                    break;
                default:
                    output = " died while on fire";
                    break;
            }
            return output;
        }

        if (cause.equalsIgnoreCase("lava")) {
            switch ((int) randomNum) {
                case 0:
                    output = " took a VERY hot bath.";
                    break;
                case 1:
                    output = " didnt realise how hot lava was.";
                    break;
                case 2:
                    output = " liked the look of lava.";
                    break;
                case 3:
                    output = " didn't realise what the hot red stuff was.";
                    break;
                case 4:
                    output = " went swimming in lava.";
                    break;
                case 5:
                    output = " did the burning backstroke.";
                    break;
                case 6:
                    output = " needed less 10 and more 8.";
                    break;
                case 7:
                    output = " is now encased in molton rock.";
                    break;
                case 8:
                    output = " thought a boat would be fine on lava.";
                    break;
                case 9:
                    output = " wanted obscidian... but did it wrong.";
                    break;
                default:
                    output = " burnt in lava";
                    break;
            }
            return output;
        }
        if (cause.equalsIgnoreCase("drowning")) {
            switch ((int) randomNum) {
                case 0:
                    output = " is sleeping with the fishes.";
                    break;
                case 1:
                    output = " couldnt hold their breath.";
                    break;
                case 2:
                    output = " needed a life-jacket.";
                    break;
                case 3:
                    output = "; the RNLI didnt get to them in time.";
                    break;
                case 4:
                    output = " wasn't a strong swimmer.";
                    break;
                case 5:
                    output = " forgot their snorkel.";
                    break;
                case 6:
                    output = " thought they had gills.";
                    break;
                case 7:
                    output = " got taken by Davy Jones.";
                    break;
                case 8:
                    output = " should have used a boat.";
                    break;
                case 9:
                    output = " thought they were a fish.";
                    break;
                default:
                    output = " drowned";
                    break;
            }
            return output;
        }
        if (cause.equalsIgnoreCase("block_explosion")) {
            switch ((int) randomNum) {
                case 0:
                    output = " didnt realise what the hissing sound was.";
                    break;
                case 1:
                    output = " is in several pieces.";
                    break;
                case 2:
                    output = " sould stayed further away from gunpowder.";
                    break;
                case 3:
                    output = " heard 'HHSSsssss'.";
                    break;
                case 4:
                    output = " went boom.";
                    break;
                case 5:
                    output = " was asploded.";
                    break;
                case 6:
                    output = " is now in orbit.";
                    break;
                case 7:
                    output = " didnt understand the term 'blast-radius' means.";
                    break;
                case 8:
                    output = " should have stayed further away from the big explosion.";
                    break;
                case 9:
                    output = " got blown up.";
                    break;
                default:
                    output = " got blown up";
                    break;
            }
        }
        if (cause.equalsIgnoreCase("void")) {
            switch ((int) randomNum) {
                case 0:
                    output = " fell into nothingness.";
                    break;
                case 1:
                    output = " made it past bedrock; it wasnt worth it.";
                    break;
                case 2:
                    output = " should have stayed above bedrock.";
                    break;
                case 3:
                    output = " didn't get on with the Void.";
                    break;
                case 4:
                    output = " is annother victim of the Void.";
                    break;
                case 5:
                    output = " realised why people avoid the Void.";
                    break;
                case 6:
                    output = " fell into nothingness.";
                    break;
                case 7:
                    output = " went looking for the nether, but found nothing.";
                    break;
                case 8:
                    output = " got Voided.";
                    break;
                case 9:
                    output = " thought bedrock was there for show.";
                    break;
                default:
                    output = " fell into the void";
                    break;
            }
            return output;
        }
        if (cause.equalsIgnoreCase("custom")) {
            switch ((int) randomNum) {
                case 0:
                    output = " encountered MISSINGNO.";
                    break;
                case 1:
                    output = " died in mysterious circumstances.";
                    break;
                case 2:
                    output = " was killd by something unusual.";
                    break;
                default:
                    output = " died at the hands of an unknow source";
                    break;
            }
            return output;
        }
        return " died!";
    }

    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            m_plugin.getServer().broadcastMessage(ChatColor.GOLD + ((Player) event.getEntity()).getName().toString() + DeathDescription(event));
        } else {
            //Animal death
        }
            
    }
}
