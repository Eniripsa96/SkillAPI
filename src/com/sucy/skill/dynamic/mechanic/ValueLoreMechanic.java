package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adds to a cast data value
 */
public class ValueLoreMechanic extends EffectComponent
{
    private static final String KEY        = "key";
    private static final String REGEX      = "regex";
    private static final String MULTIPLIER = "multiplier";

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
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets)
    {
        if (targets.size() == 0 || !settings.has(KEY))
        {
            return false;
        }

        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        String key = settings.getString(KEY);
        HashMap<String, Object> data = skill.getCastData(caster);
        double multiplier = attr(caster, MULTIPLIER, level, 1, isSelf);

        String regex = settings.getString(REGEX, "Damage: {value}");
        regex = regex.replace("{value}", "([0-9]+)");
        Pattern pattern = Pattern.compile(regex);

        if (caster.getEquipment() == null || caster.getEquipment().getItemInHand() == null)
        {
            return true;
        }
        ItemStack hand = caster.getEquipment().getItemInHand();
        if (!hand.hasItemMeta() || !hand.getItemMeta().hasLore())
        {
            return true;
        }
        List<String> lore = hand.getItemMeta().getLore();
        for (String line : lore)
        {
            line = ChatColor.stripColor(line);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find())
            {
                String value = matcher.group(1);
                try
                {
                    double base = Double.parseDouble(value);
                    data.put(key, base * multiplier);
                }
                catch (Exception ex)
                {
                    // Not a valid value
                }
            }
        }

        return true;
    }
}
