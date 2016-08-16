/**
 * SkillAPI
 * com.sucy.skill.example.psykin.Rupture
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public final class Rupture extends Skill implements SkillShot
{
    public static final String NAME = "Rupture";

    private static final String SPIKE = "rupture";

    private static final String DAMAGE = "damage";
    private static final String RADIUS = "radius";
    private static final String INTERVAL = "interval";
    private static final String RANGE = "range";

    private EffectPlayer player;

    public Rupture()
    {
        super(NAME, "Delayed AOE", makeIcon(), 10);

        settings.set(SkillAttribute.COST, 1, 0);
        settings.set(SkillAttribute.LEVEL, 1, 0);
        settings.set(SkillAttribute.COOLDOWN, 7, -0.5);
        settings.set(SkillAttribute.MANA, 8, 0);

        settings.set(DAMAGE, 6, 2);
        settings.set(RADIUS, 2, 0);
        settings.set(INTERVAL, 5, 0);
        settings.set(RANGE, 6, 1);

        settings.set(SPIKE + EffectPlayer.SHAPE, "one-circle");
        settings.set(SPIKE + EffectPlayer.SHAPE_DIR, "XZ");
        settings.set(SPIKE + EffectPlayer.SHAPE_SIZE, "sq(1-p*2)");
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
        final double radius = settings.getAttr(RADIUS, level);
        final double damage = settings.getAttr(DAMAGE, level);
        final int interval = (int)(20 * settings.getAttr(INTERVAL, level));

        final Location target = user.getLocation();
        final Vector direction = target.getDirection();
        direction.setY(0);
        direction.normalize().multiply(radius);
        SkillAPI.schedule(
            new BukkitRunnable()
            {
                int spikes = (int)Math.ceil(settings.getAttr(RANGE, level) / radius);
                @Override
                public void run()
                {
                    spikes--;
                    target.add(direction);
                    player.start(new FixedTarget(target), SPIKE, 10, level);
                    final List<LivingEntity> targets = Nearby.getLivingNearby(target, radius);
                    for (LivingEntity entity : targets)
                        if (SkillAPI.getSettings().canAttack(user, entity))
                            Rupture.this.damage(entity, damage, user);
                    if (spikes <= 0)
                        cancel();
                }
            }, 0, interval
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
        lore.add(" &2Range: {attr:range}");
        lore.add("");
        lore.add(" Releases a wave of destructive ");
        lore.add(" energy in front of you, damaging ");
        lore.add(" enemies along the way.");
        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }
}
