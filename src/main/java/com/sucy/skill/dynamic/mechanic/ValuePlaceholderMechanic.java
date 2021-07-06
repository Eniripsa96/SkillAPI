package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.hook.PlaceholderAPIHook;
import com.sucy.skill.hook.PluginChecker;
import com.sucy.skill.log.Logger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.mechanic.ValuePlaceholderMechanic
 */
public class ValuePlaceholderMechanic extends MechanicComponent
{
    private static final String KEY  = "key";
    private static final String TYPE = "type";
    private static final String PLACEHOLDER = "placeholder";

    @Override
    public String getKey() {
        return "value placeholder";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        if (!PluginChecker.isPlaceholderAPIActive()) {
            return false;
        }

        if (targets.get(0) instanceof Player)
        {
            final String key = settings.getString(KEY);
            final String placeholder = settings.getString(PLACEHOLDER);
            final String type = settings.getString(TYPE).toUpperCase();

            final String value = PlaceholderAPIHook.format(placeholder, (Player)targets.get(0));

            switch (type.charAt(0)) {
                case 'S': // STRING
                    DynamicSkill.getCastData(caster).put(key, value);
                    break;
                default: // NUMBER
                    try {
                        DynamicSkill.getCastData(caster).put(key, Double.parseDouble(value));
                    } catch (final Exception ex) {
                        Logger.invalid(placeholder + " is not a valid numeric placeholder - PlaceholderAPI returned " + value);
                        return false;
                    }
            }
            return true;
        }
        return false;
    }
}
