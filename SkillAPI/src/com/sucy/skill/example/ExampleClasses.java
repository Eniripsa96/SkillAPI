package com.sucy.skill.example;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.SkillPlugin;
import com.sucy.skill.example.alchemist.Alchemist;
import com.sucy.skill.example.alchemist.active.*;
import com.sucy.skill.example.alchemist.passive.Immunity;
import com.sucy.skill.example.bard.Bard;
import com.sucy.skill.example.bard.active.*;
import com.sucy.skill.example.bard.passive.Motivation;
import com.sucy.skill.example.hunter.Hunter;
import com.sucy.skill.example.hunter.active.*;
import com.sucy.skill.example.hunter.passive.WildHunt;
import com.sucy.skill.example.ranger.Ranger;
import com.sucy.skill.example.ranger.active.*;
import com.sucy.skill.example.ranger.passive.LightFeet;
import com.sucy.skill.example.ranger.passive.Precision;
import com.sucy.skill.example.warrior.Warrior;
import com.sucy.skill.example.warrior.active.*;
import com.sucy.skill.example.warrior.passive.Berserk;
import com.sucy.skill.example.warrior.passive.Recovery;
import com.sucy.skill.example.warrior.passive.Toughness;
import com.sucy.skill.example.wizard.Wizard;
import com.sucy.skill.example.wizard.active.*;
import com.sucy.skill.example.wizard.passive.QuickCasting;
import org.bukkit.event.HandlerList;

/**
 * Example class/skill pack for SkillAPI
 */
public class ExampleClasses implements SkillPlugin {

    private Whistle whistle;
    private ClassListener listener;

    /**
     * <p>Initializes the example classes/skills.</p>
     *
     * @param api the SkillAPI reference
     */
    public ExampleClasses(SkillAPI api) {
        listener = new ClassListener(api);
    }

    /**
     * Cleanrs up tasks/listeners for the example pack
     */
    public void disable() {
        whistle.removeWolves();
        HandlerList.unregisterAll(listener);
    }

    /**
     * Registers the skills with SkillAPI
     *
     * @param skillAPI SkillAPI reference
     */
    @Override
    public void registerSkills(SkillAPI skillAPI) {

        skillAPI.addSkills(

                // Alchemist
                new ExplosionPotion(),
                new FlashBang(),
                new GooeyAdhesive(),
                new HealthPotion(),
                new PotionOfSickness(),
                new Immunity(),

                // Bard
                new Galvanize(),
                new Heal(),
                new HorribleCry(),
                new Racket(),
                new Repulse(),
                new Motivation(),

                // Hunter
                new BlindingDart(),
                new Bolas(),
                new Grapple(),
                new VenomousStrike(),
                whistle = new Whistle(),
                new WildHunt(),

                // Ranger
                new FireArrow(),
                new Fletching(),
                new FrostArrow(),
                new SlipAway(),
                new SpreadShot(),
                new LightFeet(),
                new Precision(),

                // Warrior
                new GroundPound(),
                new Hatchet(),
                new HeadSmash(),
                new PowerfulBlow(),
                new Taunt(),
                new Berserk(),
                new Recovery(),
                new Toughness(),

                // Wizard
                new Blink(),
                new ChainLightning(),
                new Funnel(),
                new MagicMissile(),
                new MindShock(),
                new QuickCasting()
        );
    }

    /**
     * Registers the classes with SkillAPI
     *
     * @param skillAPI SkillAPI reference
     */
    @Override
    public void registerClasses(SkillAPI skillAPI) {

        skillAPI.addClasses(
                new Alchemist(),
                new Bard(),
                new Hunter(),
                new Ranger(),
                new Warrior(),
                new Wizard()
        );
    }
}
