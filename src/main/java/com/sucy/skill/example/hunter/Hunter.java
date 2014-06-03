package com.sucy.skill.example.hunter;

import com.sucy.skill.api.ClassAttribute;
import com.sucy.skill.example.ExampleClass;
import com.sucy.skill.example.hunter.active.*;
import com.sucy.skill.example.hunter.passive.WildHunt;
import org.bukkit.ChatColor;

public class Hunter extends ExampleClass {

    public static final String NAME = "Hunter";

    public Hunter() {
        super(NAME, null, ChatColor.DARK_GREEN + NAME, 0, 40);

        setAttribute(ClassAttribute.HEALTH, 25.375, 0.375);
        setAttribute(ClassAttribute.MANA, 100, 0);

        addSkills(

                // Actives
                BlindingDart.NAME,
                Bolas.NAME,
                Grapple.NAME,
                VenomousStrike.NAME,
                Whistle.NAME,

                // Passives
                WildHunt.NAME
        );
    }
}
