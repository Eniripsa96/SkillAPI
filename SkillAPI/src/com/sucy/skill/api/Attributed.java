package com.sucy.skill.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Attributed {

    private final HashMap<String, Integer> base = new HashMap<String, Integer>();
    private final HashMap<String, Integer> scale = new HashMap<String, Integer>();

    /**
     * <p>Defines a new attribute for the skill</p>
     * <p>Values are overwritten after the configuration
     * is loaded. Using this method is simply to define
     * the default values before configuration changes.</p>
     * <p>You should not use this method after the initial
     * creation of the skill.</p>
     *
     * @param name  attribute name
     * @param base  base value
     * @param scale value scale
     * @throws IllegalArgumentException if attribute is already defined
     */
    protected void setAttribute(String name, int base, int scale) {
        if (this.base.containsKey(name)) throw new IllegalArgumentException("Attribute is already defined: " + name);

        this.base.put(name, base);
        this.scale.put(name, scale);
    }

    /**
     * Gets the value of an attribute at level 1 of the skill
     *
     * @param attribute attribute name
     * @return          base value
     */
    public int getBase(String attribute) {
        return base.get(attribute);
    }

    /**
     * Gets the scale for an attribute
     *
     * @param attribute attribute name
     * @return          change in value per skill level (adding, can be negative value)
     */
    public int getScale(String attribute) {
        return scale.get(attribute);
    }

    /**
     * <p>Sets the base value of an attribute</p>
     * <p>This is used by the API to override the default values.
     * You should not use this method. When defining attributes,
     * use setAttribute(int, int)</p>
     *
     * @param attribute attribute name
     * @param value     new value
     * @throws IllegalArgumentException if attribute is not defined
     */
    public void setBase(String attribute, int value) {
        if (!base.containsKey(attribute)) throw new IllegalArgumentException("Attribute is not defined - " + attribute);

        base.put(attribute, value);
    }

    /**
     * <p>Sets the bonus value of an attribute</p>
     * <p>This is used by the API to override the default values.
     * You should not use this method. When defining attributes,
     * use setAttribute(int, int)</p>
     *
     * @param attribute attribute name
     * @param value     new scale value
     * @throws IllegalArgumentException if attribute is not defined
     */
    public void setScale(String attribute, int value) {
        if (!scale.containsKey(attribute)) throw new IllegalArgumentException("Attribute is not defined - " + attribute);

        scale.put(attribute, value);
    }

    /**
     * <p>Gets the list of the names of all attributes
     * defined for this skill. Normally you should
     * have the names of attributes already and don't
     * need to use this method. This is primarily
     * for generating Skill Tree icons.</p>
     *
     * @return custom attribute names
     */
    public List<String> getAttributeNames() {
        return new ArrayList<String>(base.keySet());
    }
}
