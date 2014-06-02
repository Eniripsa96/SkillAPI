package com.sucy.skill.example;

import com.sucy.skill.api.CustomClass;
import org.bukkit.Material;

public abstract class ExampleClass extends CustomClass {

    public ExampleClass(String name, String parent, String prefix, int professLevel, int maxLevel) {
        super(name, parent, prefix, professLevel, maxLevel);

        setDamage(Material.WOOD_SWORD, 2);
        setDamage(Material.STONE_SWORD, 2);
        setDamage(Material.IRON_SWORD, 3);
        setDamage(Material.GOLD_SWORD, 3);
        setDamage(Material.DIAMOND_SWORD, 4);

        setDamage(Material.WOOD_AXE, 1);
        setDamage(Material.STONE_AXE, 1);
        setDamage(Material.IRON_AXE, 2);
        setDamage(Material.GOLD_AXE, 2);
        setDamage(Material.DIAMOND_AXE, 3);

        setDamage(Material.WOOD_PICKAXE, 1);
        setDamage(Material.STONE_PICKAXE, 1);
        setDamage(Material.IRON_PICKAXE, 2);
        setDamage(Material.GOLD_PICKAXE, 2);
        setDamage(Material.DIAMOND_PICKAXE, 3);

        setDamage(Material.WOOD_SPADE, 1);
        setDamage(Material.STONE_SPADE, 1);
        setDamage(Material.IRON_SPADE, 2);
        setDamage(Material.GOLD_SPADE, 2);
        setDamage(Material.DIAMOND_SPADE, 3);

        setDamage(Material.WOOD_HOE, 1);
        setDamage(Material.STONE_HOE, 1);
        setDamage(Material.IRON_HOE, 2);
        setDamage(Material.GOLD_HOE, 2);
        setDamage(Material.DIAMOND_HOE, 3);
    }
}
