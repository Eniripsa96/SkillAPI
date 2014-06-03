package com.sucy.skill.example.alchemist;

import com.sucy.skill.api.ClassAttribute;
import com.sucy.skill.example.ExampleClass;
import com.sucy.skill.example.alchemist.active.*;
import com.sucy.skill.example.alchemist.passive.Immunity;
import org.bukkit.ChatColor;

public class Alchemist extends ExampleClass {

    public static final String NAME = "Alchemist";

    public Alchemist() {
        super(NAME, null, ChatColor.GOLD + NAME, 0, 40);

        setAttribute(ClassAttribute.HEALTH, 25.375, 0.375);
        setAttribute(ClassAttribute.MANA, 100, 0);

        addSkills(

                // Actives
                ExplosionPotion.NAME,
                FlashBang.NAME,
                GooeyAdhesive.NAME,
                HealthPotion.NAME,
                PotionOfSickness.NAME,

                // Passives
                Immunity.NAME
        );
    }
}
