package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to be in a specified biome
 */
public class BiomeCondition extends EffectComponent
{
    private static final String TYPE  = "type";
    private static final String BIOME = "biome";

    private static final String[] BIOMES = {
            "BEACH",
            "DESERT",
            "FOREST",
            "FROZEN",
            "HELL",
            "HILLS",
            "ICE",
            "JUNGLE",
            "MESA",
            "MOUNTAINS",
            "MUSHROOM",
            "OCEAN",
            "PLAINS",
            "PLATEAU",
            "RIVER",
            "SAVANNA",
            "SHORE",
            "SKY",
            "SWAMPLAND",
            "TAIGA"
    };

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
        int biomes = settings.getInt(BIOME, 0);
        boolean inBiome = !settings.getString(TYPE, "in biome").toLowerCase().equals("not in biome");
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            String biome = target.getLocation().getBlock().getBiome().name();
            boolean any = false;
            for (int i = 0; i < BIOMES.length; i++)
            {
                if ((biomes & (1 << i)) != 0 && biome.contains(BIOMES[i]))
                {
                    any = true;
                    break;
                }
            }
            if (any == inBiome)
            {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);

    }
}
