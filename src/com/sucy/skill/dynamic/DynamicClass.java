package com.sucy.skill.dynamic;

import com.rit.sucy.config.Config;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * <p>A basic implementation of RPGClass that loads from the dynamic
 * folder instead of the normal one. There's not really a difference
 * between normal classes, just this is used repeatedly and is added
 * based off configs versus coded ones which need to be manually
 * registered.</p>
 * <p>You should not use this class as it is meant for dynamic usage
 * and any other usage would only be an extra layer.</p>
 */
public class DynamicClass extends RPGClass
{
    /**
     * Constructs a new dynamic class
     *
     * @param api API reference
     * @param key key for the class
     */
    public DynamicClass(SkillAPI api, String key)
    {
        super(key, new ItemStack(Material.APPLE), 20);

        load(new Config(api, "dynamic/skill/" + key).getConfig());
    }
}
