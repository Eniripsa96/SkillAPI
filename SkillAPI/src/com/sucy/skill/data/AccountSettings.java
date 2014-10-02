package com.sucy.skill.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AccountSettings implements Iterable<AccountSettingsData>, Iterator<AccountSettingsData>
{
    private static final String NONE = "none";

    private HashMap<String, AccountSettingsData> settings = new HashMap<String, AccountSettingsData>();
    private ArrayList<String> keys;
    private int               iterationIndex;

    public AccountSettings(ConfigurationSection config)
    {
        for (String key : config.getKeys(false))
        {
            if (config.isConfigurationSection(key))
            {
                ConfigurationSection accSection = config.getConfigurationSection(key);
                settings.put(key, new AccountSettingsData(key, get(config, "class"), get(config, "permission")));
            }
        }
    }

    public boolean isValid()
    {
        for (AccountSettingsData data : this)
        {
            if (data.getPermission() == null)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<AccountSettingsData> iterator()
    {
        keys = new ArrayList<String>(settings.keySet());
        iterationIndex = -1;
        return this;
    }

    @Override
    public boolean hasNext()
    {
        return iterationIndex < keys.size() - 1;
    }

    @Override
    public AccountSettingsData next()
    {
        iterationIndex++;
        return settings.get(keys.get(iterationIndex));
    }

    @Override
    public void remove()
    {
        settings.remove(keys.get(iterationIndex));
        keys.remove(iterationIndex);
    }

    private String get(ConfigurationSection config, String key)
    {
        String value = config.getString(key, NONE);
        if (value.equalsIgnoreCase(NONE))
        {
            return null;
        }
        else
        {
            return value;
        }
    }
}
