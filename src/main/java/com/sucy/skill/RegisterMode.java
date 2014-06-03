package com.sucy.skill;

/**
 * States of the API when registering skills and classes to help make sure nothing gets registered out of place
 */
public enum RegisterMode {

    /**
     * When the API is registering skills
     */
    SKILL,

    /**
     * When the API is registering classes
     */
    CLASS,

    /**
     * When the API is not registering anything
     */
    DONE
}
