package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.CustomClass;
import com.sucy.skill.config.ClassValues;
import org.bukkit.configuration.ConfigurationSection;

import javax.swing.tree.TreeNode;

/**
 * A class that can be defined via config
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
}
