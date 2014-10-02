package com.sucy.skill.api.enums;

public enum TreeType
{

    BASIC_HORIZONTAL("BasicHorizontal"),
    BASIC_VERTICAL("BasicVertical"),
    LEVEL_HORIZONTAL("LevelHorizontal"),
    LEVEL_VERTICAL("LevelVertical"),
    REQUIREMENT("Requirement"),;

    private String key;

    private TreeType(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }
}
