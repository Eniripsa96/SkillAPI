package com.sucy.skill.example.warrior;

import com.sucy.skill.api.ClassAttribute;
import com.sucy.skill.example.ExampleClass;
import com.sucy.skill.example.warrior.active.*;
import com.sucy.skill.example.warrior.passive.Berserk;
import com.sucy.skill.example.warrior.passive.Recovery;
import com.sucy.skill.example.warrior.passive.Toughness;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Warrior class
 *
 * Proficient at taking damage and staying in the front line
 */
public class Warrior extends ExampleClass {

    public static final String NAME = "Warrior";

    /**
     * Constructor for the class
     */
    public Warrior() {
        super(NAME, null, ChatColor.DARK_RED + NAME, 0, 40);

        setAttribute(ClassAttribute.HEALTH, 30.75, 0.75);
        setAttribute(ClassAttribute.MANA, 100, 0);

        addSkills(

                // Actives
                GroundPound.NAME,
                Hatchet.NAME,
                HeadSmash.NAME,
                PowerfulBlow.NAME,
                Taunt.NAME,

                // Passives
                Berserk.NAME,
                Recovery.NAME,
                Toughness.NAME
        );

        setDamage(Material.WOOD_SWORD, 3);
        setDamage(Material.STONE_SWORD, 3);
        setDamage(Material.IRON_SWORD, 4);
        setDamage(Material.GOLD_SWORD, 4);
        setDamage(Material.DIAMOND_SWORD, 5);

        setDamage(Material.WOOD_AXE, 2);
        setDamage(Material.STONE_AXE, 2);
        setDamage(Material.IRON_AXE, 3);
        setDamage(Material.GOLD_AXE, 3);
        setDamage(Material.DIAMOND_AXE, 4);
    }
}
