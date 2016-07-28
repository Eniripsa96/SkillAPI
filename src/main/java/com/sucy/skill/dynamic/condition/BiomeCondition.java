/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.BiomeCondition
 * <p/>
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2014 Steven Sucy
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition for dynamic skills that requires the target to be in a specified biome
 */
public class BiomeCondition extends EffectComponent {
    private static final String TYPE = "type";
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
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
        int biomes = settings.getInt(BIOME, 0);
        boolean inBiome = !settings.getString(TYPE, "in biome").toLowerCase().equals("not in biome");
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets) {
            String biome = target.getLocation().getBlock().getBiome().name();
            boolean any = false;
            for (int i = 0; i < BIOMES.length; i++) {
                if ((biomes & (1 << i)) != 0 && biome.contains(BIOMES[i])) {
                    any = true;
                    break;
                }
            }
            if (any == inBiome) {
                list.add(target);
            }
        }
        return list.size() > 0 && executeChildren(caster, level, list);

    }
}
