/**
 * SkillAPI
 * com.sucy.skill.example.psykin.Retribution
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.sucy.skill.example.psykin;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.particle.EffectPlayer;
import com.sucy.skill.api.particle.target.FixedTarget;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillAttribute;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.api.util.Nearby;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Retribution extends Skill implements SkillShot
{
    public static final String NAME = "Retribution";

    private static final String CHARGE = "retribution-charge";
    private static final String SPIKE  = "retribution-spike";

    private static final String DAMAGE = "damage";
    private static final String RADIUS = "radius";
    private static final String DELAY  = "delay";

    private EffectPlayer player;

    public Retribution()
    {
        super(NAME, "Delayed AOE", makeIcon(), 10);

        settings.set(SkillAttribute.COST, 1, 0);
        settings.set(SkillAttribute.LEVEL, 1, 0);
        settings.set(SkillAttribute.COOLDOWN, 7, -0.5);
        settings.set(SkillAttribute.MANA, 8, 0);

        settings.set(DAMAGE, 4, 1);
        settings.set(RADIUS, 1, 0.1);
        settings.set(DELAY, 2, -0.1);

        settings.set(CHARGE + EffectPlayer.SHAPE, "three-point");
        settings.set(CHARGE + EffectPlayer.SHAPE_DIR, "XZ");
        settings.set(CHARGE + EffectPlayer.SHAPE_SIZE, "1-p");
        settings.set(CHARGE + EffectPlayer.ANIMATION, "linear");
        settings.set(CHARGE + EffectPlayer.ANIM_DIR, "YZ");
        settings.set(CHARGE + EffectPlayer.ANIM_SIZE, "1-p");
        settings.set(CHARGE + EffectPlayer.INTERVAL, 2);
        settings.set(CHARGE + EffectPlayer.VIEW_RANGE, 25);

        settings.set(CHARGE + EffectPlayer.P_TYPE, "SPELL");
        settings.set(CHARGE + EffectPlayer.AMOUNT, 1);
        settings.set(CHARGE + EffectPlayer.DX, 0);
        settings.set(CHARGE + EffectPlayer.DY, 0);
        settings.set(CHARGE + EffectPlayer.DZ, 0);
        settings.set(CHARGE + EffectPlayer.SPEED, 0);

        settings.set(SPIKE + EffectPlayer.SHAPE, "one-circle");
        settings.set(SPIKE + EffectPlayer.SHAPE_DIR, "XZ");
        settings.set(SPIKE + EffectPlayer.SHAPE_SIZE, "sq(1-p*(v*0.1+0.9))");
        settings.set(SPIKE + EffectPlayer.ANIMATION, "linear-quick");
        settings.set(SPIKE + EffectPlayer.ANIM_DIR, "YZ");
        settings.set(SPIKE + EffectPlayer.ANIM_SIZE, "4*p");
        settings.set(SPIKE + EffectPlayer.INTERVAL, 1);
        settings.set(SPIKE + EffectPlayer.VIEW_RANGE, 25);

        settings.set(SPIKE + EffectPlayer.P_TYPE, "CRIT");
        settings.set(SPIKE + EffectPlayer.AMOUNT, 1);
        settings.set(SPIKE + EffectPlayer.DX, 0);
        settings.set(SPIKE + EffectPlayer.DY, 0);
        settings.set(SPIKE + EffectPlayer.DZ, 0);
        settings.set(SPIKE + EffectPlayer.SPEED, 0);

        player = new EffectPlayer(settings);
    }

    @Override
    public boolean cast(final LivingEntity user, final int level)
    {
        Object offender = DynamicSkill.getCastData(user).get(Psykin.OFFENDER);
        if (offender == null)
            return false;

        int ticks = (int) (20 * settings.getAttr(DELAY, level));

        final Location target = ((LivingEntity) offender).getLocation();
        player.start(new FixedTarget(target), CHARGE, ticks, level);
        SkillAPI.schedule(
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    double damage = settings.getAttr(DAMAGE, level);
                    double radius = settings.getAttr(RADIUS, level);
                    player.start(new FixedTarget(target), SPIKE, 10, level);
                    List<LivingEntity> targets = Nearby.getLivingNearby(target, radius);
                    for (LivingEntity entity : targets)
                        if (SkillAPI.getSettings().canAttack(user, entity))
                            Retribution.this.damage(entity, damage, user);
                }
            }, ticks
        );

        return true;
    }

    private static ItemStack makeIcon()
    {
        ItemStack icon = new ItemStack(Material.DIAMOND);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName("Ͽ=-------=V=-------=Ͼ");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(" &d{name} &7[{level} / {max}] ");
        lore.add("");
        lore.add(" {req:level}Level: {attr:level}");
        lore.add(" {req:cost}Cost: {attr:cost}");
        lore.add("");
        lore.add(" &2Capacity: {attr:mana}");
        lore.add(" &2Cooldown: {attr:cooldown}");
        lore.add(" &2Damage: {attr:damage}");
        lore.add(" &2Radius: {attr:radius}");
        lore.add(" &2Delay: {attr:delay}");
        lore.add("");
        lore.add(" Builds up energy around the ");
        lore.add(" last foe that attacked you, ");
        lore.add(" exploding after a short time. ");
        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }
}
