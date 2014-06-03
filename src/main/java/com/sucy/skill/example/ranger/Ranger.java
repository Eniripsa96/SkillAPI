package com.sucy.skill.example.ranger;

import com.sucy.skill.api.ClassAttribute;
import com.sucy.skill.example.ExampleClass;
import com.sucy.skill.example.ranger.active.*;
import com.sucy.skill.example.ranger.passive.LightFeet;
import com.sucy.skill.example.ranger.passive.Precision;
import org.bukkit.ChatColor;

/**
 * Warrior class
 *
 * Proficient at taking damage and staying in the front line
 */
public class Ranger extends ExampleClass {

    public static final String NAME = "Ranger";

    /**
     * Constructor for the class
     */
    public Ranger() {
        super(NAME, null, ChatColor.GREEN + NAME, 0, 40);

        setAttribute(ClassAttribute.HEALTH, 25.375, 0.375);
        setAttribute(ClassAttribute.MANA, 100, 0);

        addSkills(

                // Actives
                FireArrow.NAME,
                Fletching.NAME,
                FrostArrow.NAME,
                SlipAway.NAME,
                SpreadShot.NAME,

                // Passives
                LightFeet.NAME,
                Precision.NAME
        );
    }
}
