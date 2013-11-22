package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.CustomClass;

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
}
