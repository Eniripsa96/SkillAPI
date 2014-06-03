package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for making targets dash forward
 */
public class CooldownMechanic implements IMechanic {

    private static final String
            COOLDOWNS = "Cooldowns";

    /**
     * Forces all targets to dash
     *
     * @param player  player using the skill
     * @param data    data of the player using the skill
     * @param skill   skill being used
     * @param target  target type of the skill
     * @param targets targets for the effects
     * @return        true if there were targets, false otherwise
     */
    @Override
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, Target target, List<LivingEntity> targets) {

        // Needs data
        if (!skill.hasString(COOLDOWNS)) return false;

        // Get values
        String string = skill.getString(COOLDOWNS);
        String[] cooldowns;
        if (!string.contains(";")) return false;
        cooldowns = string.split(";");

        // Apply all of the cooldown modifications
        for (int i = 0; i < cooldowns.length / 2; i++) {

            ClassSkill s = skill.getAPI().getSkill(cooldowns[i * 2]);
            if (s == null || !data.hasSkill(s.getName())) continue;
            int j = i * 2 + 1;

            if (cooldowns[j].equals("r")) s.refreshCooldown(data);
            else if (cooldowns[j].equals("s")) s.startCooldown(data);
            else if (cooldowns[j].startsWith("a")) {
                try {
                    double amount = Double.parseDouble(cooldowns[j].substring(1));
                    s.addCooldown(data, amount);
                }
                catch (Exception ex) { /* Do nothing */ }
            }
            else if (cooldowns[j].startsWith("p")) {
                try {
                    double percent = Double.parseDouble(cooldowns[j].substring(1));
                    s.addCooldown(data, percent * skill.getAttribute(SkillAttribute.COOLDOWN, data.getSkillLevel(skill.getName())) / 100);
                }
                catch (Exception ex) { /* Do nothing */ }
            }
        }

        return true;
    }

    /**
     * Sets default attributes for the skill
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the name
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) { }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }
}
