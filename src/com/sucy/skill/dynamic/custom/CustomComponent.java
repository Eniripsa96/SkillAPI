package com.sucy.skill.dynamic.custom;

import com.sucy.skill.dynamic.ComponentType;

import java.util.List;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.custom.CustomComponent
 */
public interface CustomComponent {
    String getKey();
    ComponentType getType();
    String getDescription();
    List<EditorOption> getOptions();

    default String getDisplayName() {
        return getKey();
    }

    default boolean isContainer() {
        return getType() != ComponentType.MECHANIC;
    }
}
