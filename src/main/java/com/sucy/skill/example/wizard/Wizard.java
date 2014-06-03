package com.sucy.skill.example.wizard;

import com.sucy.skill.api.ClassAttribute;
import com.sucy.skill.example.ExampleClass;
import com.sucy.skill.example.wizard.active.*;
import com.sucy.skill.example.wizard.passive.QuickCasting;
import org.bukkit.ChatColor;

public class Wizard extends ExampleClass {

    public static final String NAME = "Wizard";

    public Wizard() {
        super(NAME, null, ChatColor.AQUA + NAME, 0, 40);

        setAttribute(ClassAttribute.HEALTH, 25.375, 0.375);
        setAttribute(ClassAttribute.MANA, 100, 0);

        addSkills(

                // Actives
                Blink.NAME,
                ChainLightning.NAME,
                Funnel.NAME,
                MagicMissile.NAME,
                MindShock.NAME,

                // Passives
                QuickCasting.NAME
        );
    }
}
