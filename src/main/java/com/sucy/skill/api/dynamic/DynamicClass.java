package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.ClassAttribute;
import com.sucy.skill.api.CustomClass;
import org.bukkit.configuration.ConfigurationSection;

/**
 * <p>A class that can be defined via config</p>
 * <p>You should not use this class as it is for dynamic usage only</p>
 */
public class DynamicClass extends CustomClass {

    /**
     * Initial Constructor
     *
     * @param name class name
     */
    public DynamicClass(String name) {
        super(name, null, name, 20, 20);
    }

    /**
     * Apply default attribute values for dynamic classes
     *
     * @param config configuration to update from
     */
    @Override
    public void update(ConfigurationSection config) {
        super.update(config);
        checkDefault(ClassAttribute.HEALTH, 20, 0);
        checkDefault(ClassAttribute.MANA, 100, 0);
    }
}
