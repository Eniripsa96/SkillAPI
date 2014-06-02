package com.sucy.skill.example.bard;

import com.sucy.skill.api.ClassAttribute;
import com.sucy.skill.example.ExampleClass;
import com.sucy.skill.example.bard.active.*;
import com.sucy.skill.example.bard.passive.Motivation;
import org.bukkit.ChatColor;

public class Bard extends ExampleClass {

    public static final String NAME = "Bard";

    public Bard() {
        super(NAME, null, ChatColor.LIGHT_PURPLE + NAME, 0, 40);

        setAttribute(ClassAttribute.HEALTH, 25.375, 0.375);
        setAttribute(ClassAttribute.MANA, 100, 0);

        addSkills(

                // Actives
                Galvanize.NAME,
                Heal.NAME,
                HorribleCry.NAME,
                Racket.NAME,
                Repulse.NAME,

                // Passives
                Motivation.NAME
        );
    }
}
