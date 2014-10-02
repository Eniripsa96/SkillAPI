package com.sucy.skill.data;

public class AccountSettingsData
{
    private String key;
    private String defaultClass;
    private String permission;

    public AccountSettingsData(String key, String defaultClass, String permission)
    {
        this.key = key;
        this.defaultClass = defaultClass;
        this.permission = permission;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultClass() {
        return defaultClass;
    }

    public String getPermission() {
        return permission;
    }
}