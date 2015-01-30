package com.sucy.skill.data.io;

import com.rit.sucy.config.Config;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.*;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigIO extends IOManager
{

    private static final String
            LIMIT          = "limit",
            ACTIVE         = "active",
            ACCOUNTS       = "accounts",
            ACCOUNT_PREFIX = "acc",
            CLASSES        = "classes",
            SKILLS         = "skills",
            BINDS          = "binds",
            LEVEL          = "level",
            TOTAL_EXP      = "total-exp",
            POINTS         = "points",
            SKILL_BAR      = "bar",
            ENABLED        = "enabled",
            SLOTS          = "slots",
            UNASSIGNED     = "e";

    private final Config config;

    public ConfigIO(SkillAPI plugin)
    {
        super(plugin);
        config = new Config(plugin, "players");
    }

    @Override
    public PlayerAccounts loadData(OfflinePlayer player)
    {
        PlayerAccounts data = new PlayerAccounts(player);
        String playerKey = new VersionPlayer(player).getIdString();
        if (!playerKey.equals(player.getName()) && config.getConfig().contains(player.getName()))
        {
            config.getConfig().set(playerKey, config.getConfig().getConfigurationSection(player.getName()));
            config.getConfig().set(player.getName(), null);
        }
        if (config.getConfig().contains(playerKey))
        {
            ConfigurationSection file = config.getConfig().getConfigurationSection(playerKey);

            ConfigurationSection accounts = file.getConfigurationSection(ACCOUNTS);
            for (String accountKey : accounts.getKeys(false))
            {
                ConfigurationSection account = accounts.getConfigurationSection(accountKey);
                PlayerData acc = data.getData(Integer.parseInt(accountKey.replace(ACCOUNT_PREFIX, "")));

                // Load classes
                ConfigurationSection classes = account.getConfigurationSection(CLASSES);
                for (String classKey : classes.getKeys(false))
                {
                    RPGClass rpgClass = SkillAPI.getClass(classKey);
                    if (rpgClass != null)
                    {
                        PlayerClass c = acc.setClass(rpgClass);
                        ConfigurationSection classData = classes.getConfigurationSection(classKey);
                        int levels = classData.getInt(LEVEL) - 1;
                        if (levels > 0)
                        {
                            c.giveLevels(levels);
                        }
                        c.setPoints(classData.getInt(POINTS));
                        c.setTotalExp(classData.getDouble(TOTAL_EXP));
                    }
                }

                // Load skills
                ConfigurationSection skills = account.getConfigurationSection(SKILLS);
                for (String skillKey : skills.getKeys(false))
                {
                    ConfigurationSection skill = skills.getConfigurationSection(skillKey);
                    PlayerSkill skillData = acc.getSkill(skillKey);
                    if (skillData != null)
                    {
                        skillData.addLevels(skill.getInt(LEVEL));
                        skillData.addPoints(skill.getInt(POINTS));
                    }
                }

                // Load binds
                ConfigurationSection binds = account.getConfigurationSection(BINDS);
                for (String bindKey : binds.getKeys(false))
                {
                    acc.bind(Material.valueOf(bindKey), acc.getSkill(binds.getString(bindKey)));
                }

                // Load skill bar
                ConfigurationSection skillBar = account.getConfigurationSection(SKILL_BAR);
                PlayerSkillBar bar = acc.getSkillBar();
                if (skillBar != null && bar != null)
                {
                    for (String key : skillBar.getKeys(false))
                    {
                        if (key.equals(ENABLED))
                        {
                            if (bar.isEnabled() != skillBar.getBoolean(key))
                            {
                                bar.toggleEnabled();
                            }
                        }
                        else if (key.equals(SLOTS))
                        {
                            List<Integer> slots = skillBar.getIntegerList(SLOTS);
                            for (int i : slots)
                            {
                                bar.getData().put(i, UNASSIGNED);
                            }
                        }
                        else if (SkillAPI.getSkill(key) != null)
                        {
                            bar.getData().put(skillBar.getInt(key), key);
                        }
                    }
                    bar.applySettings();
                }
            }

            data.setAccount(file.getInt(ACTIVE, data.getActiveId()));
        }
        return data;
    }

    @Override
    public void saveData(PlayerAccounts data)
    {
        ConfigurationSection file = config.getConfig().createSection(data.getUUID().toString());
        file.set(LIMIT, data.getAccountLimit());
        file.set(ACTIVE, data.getActiveId());
        ConfigurationSection accounts = file.createSection(ACCOUNTS);
        for (Map.Entry<Integer, PlayerData> entry : data.getAllData().entrySet())
        {
            ConfigurationSection account = accounts.createSection(ACCOUNT_PREFIX + entry.getKey());
            PlayerData acc = entry.getValue();

            // Save classes
            ConfigurationSection classes = account.createSection(CLASSES);
            for (PlayerClass c : acc.getClasses())
            {
                ConfigurationSection classSection = classes.createSection(c.getData().getName());
                classSection.set(LEVEL, c.getLevel());
                classSection.set(POINTS, c.getPoints());
                classSection.set(TOTAL_EXP, c.getTotalExp());
            }

            // Save skills
            ConfigurationSection skills = account.createSection(SKILLS);
            for (PlayerSkill skill : acc.getSkills())
            {
                ConfigurationSection skillSection = skills.createSection(skill.getData().getName());
                skillSection.set(LEVEL, skill.getLevel());
                skillSection.set(POINTS, skill.getPoints());
            }

            // Save binds
            ConfigurationSection binds = account.createSection(BINDS);
            for (Map.Entry<Material, PlayerSkill> bind : acc.getBinds().entrySet())
            {
                binds.set(bind.getKey().name(), bind.getValue().getData().getName());
            }

            // Save skill bar
            if (acc.getSkillBar() != null)
            {
                ConfigurationSection skillBar = account.createSection(SKILL_BAR);
                PlayerSkillBar bar = acc.getSkillBar();
                skillBar.set(ENABLED, bar.isEnabled());
                skillBar.set(SLOTS, new ArrayList<Integer>(bar.getData().keySet()));
                for (Map.Entry<Integer, String> slotEntry : bar.getData().entrySet())
                {
                    if (slotEntry.getValue().equals(UNASSIGNED))
                    {
                        continue;
                    }
                    skillBar.set(slotEntry.getValue(), slotEntry.getKey());
                }
            }
        }
    }

    @Override
    public void saveAll()
    {
        for (Map.Entry<String, PlayerAccounts> entry : SkillAPI.getPlayerAccountData().entrySet())
        {
            saveData(entry.getValue());
        }
        config.saveConfig();
    }
}
