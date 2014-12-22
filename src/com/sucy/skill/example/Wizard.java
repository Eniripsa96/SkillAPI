package com.sucy.skill.example;

import com.sucy.skill.api.classes.ClassAttribute;
import com.sucy.skill.api.classes.RPGClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Wizard extends RPGClass
{
    public static final String NAME = "Wizard";

    public Wizard()
    {
        super(NAME, new ItemStack(Material.BOOK), 40);

        attributes.set(ClassAttribute.HEALTH, 25.375, 0.375);
        attributes.set(ClassAttribute.MANA, 100, 0);

        addSkills(

        );
    }
}
