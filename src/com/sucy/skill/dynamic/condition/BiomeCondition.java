package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.block.Biome;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to be in a specified biome
 */
public class BiomeCondition extends EffectComponent
{
    private static final String TYPE = "type";
    private static final String BIOME = "biome";

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
        Biome biome;
        try
        {
            biome = Biome.valueOf(settings.getString(BIOME).toUpperCase().replace(' ', '_'));
        }
        catch (Exception e)
        {
            return false;
        }

        boolean inBiome = !settings.getString(TYPE, "in biome").toLowerCase().equals("not in biome");
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            if ((target.getLocation().getBlock().getBiome() == biome) == inBiome)
            {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);
    }
}
