/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.WolfMechanic
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.listener.MechanicListener;
import com.sucy.skill.task.RemoveTask;
import org.bukkit.DyeColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Applies a flag to each target
 */
public class WolfMechanic extends MechanicComponent {
    public static final  String SKILL_META = "sapi_wolf_skills";
    public static final  String LEVEL      = "sapi_wolf_level";
    private static final String COLOR      = "color";
    private static final String HEALTH     = "health";
    private static final String SECONDS    = "seconds";
    private static final String NAME       = "name";
    private static final String DAMAGE     = "damage";
    private static final String SKILLS     = "skills";
    private static final String AMOUNT     = "amount";
    private static final String SITTING    = "sitting";

    private final Map<Integer, RemoveTask> tasks = new HashMap<>();

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
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
        if (!(caster instanceof Player)) {
            return false;
        }

        cleanUp(caster);

        final Player player = (Player) caster;

        boolean isSelf = targets.size() == 1 && targets.get(0) == player;
        String color = settings.getString(COLOR);
        double health = parseValues(player, HEALTH, level, 10.0);
        String name = TextFormatter.colorString(settings.getString(NAME, "").replace("{player}", player.getName()));
        double damage = parseValues(player, DAMAGE, level, 3.0);
        double amount = parseValues(player, AMOUNT, level, 1.0);
        boolean sitting = settings.getString(SITTING, "false").equalsIgnoreCase("true");
        List<String> skills = settings.getStringList(SKILLS);

        DyeColor dye = null;
        if (color != null) {
            try {
                dye = DyeColor.valueOf(color);
            } catch (Exception ex) { /* Invalid color */ }
        }

        double seconds = parseValues(player, SECONDS, level, 10.0);
        int ticks = (int) (seconds * 20);
        List<LivingEntity> wolves = new ArrayList<>();
        for (LivingEntity target : targets) {
            for (int i = 0; i < amount; i++) {
                Wolf wolf = target.getWorld().spawn(target.getLocation(), Wolf.class);
                wolf.setOwner(player);
                wolf.setMaxHealth(health);
                wolf.setHealth(health);
                wolf.setSitting(sitting);
                SkillAPI.setMeta(wolf, MechanicListener.SUMMON_DAMAGE, damage);

                List<LivingEntity> owner = new ArrayList<>(1);
                owner.add(player);
                DynamicSkill.getCastData(wolf).put("api-owner", owner);

                if (dye != null) {
                    wolf.setCollarColor(dye);
                }
                if (name.length() > 0) {
                    wolf.setCustomName(name);
                    wolf.setCustomNameVisible(true);
                }

                // Setup skills
                for (String skillName : skills) {
                    Skill skill = SkillAPI.getSkill(skillName);
                    if (skill instanceof PassiveSkill) {
                        ((PassiveSkill) skill).initialize(wolf, level);
                    }
                }
                SkillAPI.setMeta(wolf, SKILL_META, skills);
                SkillAPI.setMeta(wolf, LEVEL, level);

                wolves.add(wolf);
            }
        }

        final RemoveTask task = new RemoveTask(wolves, ticks);
        tasks.put(caster.getEntityId(), task);

        // Apply children to the wolves
        if (wolves.size() > 0) {
            executeChildren(player, level, wolves);
            return true;
        }
        return false;
    }

    @Override
    public String getKey() {
        return "wolf";
    }

    @Override
    public void cleanUp(final LivingEntity caster) {
        final RemoveTask task = tasks.remove(caster.getEntityId());
        if (task != null) {
            task.cancel();
            task.run();
        }
    }
}
