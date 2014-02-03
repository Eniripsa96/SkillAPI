package com.sucy.skill.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for converting old config values into new ones
 */
public class ConfigConverter {

    /**
     * Converts a configuration
     * from the format for v2.30 and earlier
     * to the one for v2.31 and on
     *
     * @param config configuration to convert
     */
    public static void convert(ConfigurationSection config) {
        for (Map.Entry<String, String> pair : PAIRS.entrySet()) {
            if (config.contains(pair.getKey())) {
                config.set(pair.getValue(), config.get(pair.getKey()));
                config.set(pair.getKey(), null);
            }
        }
    }

    /**
     * Map of old keys to new ones
     */
    private static final HashMap<String, String> PAIRS = new HashMap<String, String>() {{
        put(OldSettingValues.ALLOW_DOWNGRADING_SKILLS.path(), SettingValues.SKILL_ALLOW_DOWNGRADE);
        put(OldSettingValues.BLOCK_CREATIVE_EXP.path(), SettingValues.EXP_BLOCK_CREATIVE);
        put(OldSettingValues.BLOCK_MOB_EGG_EXP.path(), SettingValues.EXP_BLOCK_EGG);
        put(OldSettingValues.BLOCK_MOB_SPAWNER_EXP.path(), SettingValues.EXP_BLOCK_SPAWNER);
        put(OldSettingValues.CLASSLESS_HP.path(), SettingValues.CLASS_HP);
        put(OldSettingValues.DEFAULT_CLASS.path(), SettingValues.CLASS_DEFAULT);
        put(OldSettingValues.DEFAULT_ONE_DAMAGE.path(), SettingValues.ITEM_DEFAULT_ONE_DAMAGE);
        put(OldSettingValues.EXP_FORMULA.path(), SettingValues.EXP_FORMULA);
        put(OldSettingValues.KILLS.path(), SettingValues.EXP_YIELDS);
        put(OldSettingValues.LOAD_LOGGING.path(), SettingValues.LOG_LOAD);
        put(OldSettingValues.LORE_REQUIREMENTS.path(), SettingValues.ITEM_LORE_REQUIREMENTS);
        put(OldSettingValues.MANA_ENABLED.path(), SettingValues.MANA_ENABLED);
        put(OldSettingValues.MANA_GAIN_FREQ.path(), SettingValues.MANA_GAIN_FREQ);
        put(OldSettingValues.MANA_GAIN_AMOUNT.path(), SettingValues.MANA_GAIN_AMOUNT);
        put(OldSettingValues.OLD_HEALTH_BAR.path(), SettingValues.GUI_OLD_HEALTH);
        put(OldSettingValues.PERCENT_EXP_LOST_ON_DEATH.path(), SettingValues.EXP_LOST_ON_DEATH);
        put(OldSettingValues.PLAYERS_PER_CHECK.path(), SettingValues.ITEM_PLAYERS_PER_CHECK);
        put(OldSettingValues.POINTS_PER_LEVEL.path(), SettingValues.CLASS_POINTS_PER_LEVEL);
        put(OldSettingValues.PROFESS_RESET.path(), SettingValues.CLASS_RESET);
        put(OldSettingValues.SCOREBOARD_ENABLED.path(), SettingValues.GUI_SCOREBOARD);
        put(OldSettingValues.SKILL_MESSAGE_RADIUS.path(), SettingValues.SKILL_MESSAGE_RADIUS);
        put(OldSettingValues.SKILL_BAR.path(), SettingValues.SKILL_BAR);
        put(OldSettingValues.STARTING_POINTS.path(), SettingValues.CLASS_STARTING_POINTS);
        put(OldSettingValues.TREE_TYPE.path(), SettingValues.SKILL_TREE_TYPE);
        put(OldSettingValues.USE_CLICK_COMBOS.path(), SettingValues.CAST_CLICK_COMBOS);
        put(OldSettingValues.USE_EXP_ORBS.path(), SettingValues.EXP_USE_ORBS);
        put(OldSettingValues.USE_LEVEL_BAR.path(), SettingValues.GUI_LEVEL_BAR);
        put(OldSettingValues.USE_SKILL_BARS.path(), SettingValues.CAST_SKILL_BARS);
    }};
}
