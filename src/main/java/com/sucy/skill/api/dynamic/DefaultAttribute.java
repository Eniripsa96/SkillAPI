package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.Attributed;

/**
 * Default attribute for dynamic skills
 */
public class DefaultAttribute {

    private String name;
    private int base;
    private int bonus;

    /**
     * Constructor
     *
     * @param name  attribute name
     * @param base  attribute base
     * @param bonus attribute bonus
     */
    public DefaultAttribute(String name, int base, int bonus) {
        this.name = name;
        this.base = base;
        this.bonus = bonus;
    }

    /**
     * <p>Applies the default to the attributed object</p>
     * <p>Does not do anything if the attribute is already set</p>
     *
     * @param attributed attributed object to apply to
     */
    public void apply(Attributed attributed) {
        if (!attributed.hasAttribute(name)) {
            attributed.setAttribute(name, base, bonus);
        }
    }
}
