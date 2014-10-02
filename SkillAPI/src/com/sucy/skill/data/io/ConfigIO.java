package com.sucy.skill.data.io;

import com.rit.sucy.config.Config;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

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
            POINTS         = "points";

    private final Config config;

    public ConfigIO(SkillAPI plugin)
    {
        super(plugin);
        config = new Config(plugin, "players");
    }

    @Override
    public PlayerAccounts loadData(VersionPlayer player)
    {
        PlayerAccounts data = new PlayerAccounts(player);
        if (config.getConfig().contains(player.getIdString()))
        {
            ConfigurationSection file = config.getConfig().getConfigurationSection(player.getIdString());

            ConfigurationSection accounts = file.getConfigurationSection(ACCOUNTS);
            for (String accountKey : accounts.getKeys(false))
            {
                ConfigurationSection account = accounts.getConfigurationSection(accountKey);
                PlayerData acc = data.getData(accountKey);

                // Load classes
                ConfigurationSection classes = account.getConfigurationSection(CLASSES);
                for (String classKey : classes.getKeys(false))
                {
                    PlayerClass c = new PlayerClass(acc, api.getClass(classKey));
                    ConfigurationSection classData = classes.getConfigurationSection(classKey);
                    c.giveLevels(classData.getInt(LEVEL));
                    c.setPoints(classData.getInt(POINTS));
                    c.setTotalExp(classData.getInt(TOTAL_EXP));
                }

                // Load skills
                ConfigurationSection skills = account.getConfigurationSection(SKILLS);
                for (String skillKey : skills.getKeys(false))
                {
                    ConfigurationSection skill = skills.getConfigurationSection(skillKey);
                    PlayerSkill skillData = acc.getSkill(skillKey);
                    skillData.addLevels(skill.getInt(LEVEL));
                    skillData.addPoints(skill.getInt(POINTS));
                }

                // Load binds
                ConfigurationSection binds = account.getConfigurationSection(BINDS);
                for (String bindKey : binds.getKeys(false))
                {
                    acc.bind(Material.valueOf(bindKey), acc.getSkill(binds.getString(bindKey)));
                }
            }

            data.setAccount(file.getString(ACTIVE, data.getActiveId()));
        }
        return data;
    }

    @Override
    public void saveData(PlayerAccounts data)
    {
        ConfigurationSection file = config.getConfig().createSection(data.getVersionPlayer().getIdString());
        file.set(LIMIT, data.getAccountLimit());
        file.set(ACTIVE, data.getActiveId());
        ConfigurationSection accounts = file.createSection(ACCOUNTS);
        for (Map.Entry<String, PlayerData> entry : data.getAllData().entrySet())
        {
            ConfigurationSection account = accounts.createSection(entry.getKey());
            PlayerData acc = entry.getValue();

            // Save classes
            ConfigurationSection classes = account.createSection(CLASSES);
            for (PlayerClass c : acc.getClasses())
            {
                ConfigurationSection classSection = classes.createSection(c.getData().getName());
                classSection.set(LEVEL, c.getLevel());
                classSection.set(POINTS, c.getPoints());
                classes.set(TOTAL_EXP, c.getTotalExp());
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
        }
    }

    @Override
    public void saveAll()
    {

    }
}
