/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.DamageMechanic
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

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerCriticalEvent;
import com.sucy.skill.api.player.PlayerData;

import java.util.List;
import java.util.Random;

/**
 * Deals damage to each target
 */
public class DamageMechanic extends MechanicComponent {
	private static final String TYPE = "type";
	private static final String DAMAGE = "value";
	private static final String CRITCHANCE = "critchance";
	private static final String TRUE = "true";
	private static final String CLASSIFIER = "classifier";
	private static Random gen = new Random();

	@Override
	public String getKey() {
		return "damage";
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
	public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean isCrit) {
		String pString = settings.getString(TYPE, "damage").toLowerCase();
		boolean percent = pString.equals("multiplier") || pString.equals("percent");
		boolean missing = pString.equals("percent missing");
		boolean left = pString.equals("percent left");
		boolean trueDmg = settings.getBool(TRUE, false);
		double critchance = parseValues(caster, CRITCHANCE, level, 0.0);
		double damage = parseValues(caster, DAMAGE, level, 1.0);
		String classification = settings.getString(CLASSIFIER, "default");
		if (damage < 0) {
			return false;
		}
		for (LivingEntity target : targets) {
			if (target.isDead()) {
				continue;
			}

			double amount = damage;
			if (percent) {
				amount = damage * target.getMaxHealth() / 100;
			}
			else if (missing) {
				amount = damage * (target.getMaxHealth() - target.getHealth()) / 100;
			}
			else if (left) {
				amount = damage * target.getHealth() / 100;
			}
			
			if (gen.nextDouble() < critchance) {
		        // Apply global modifiers
				double critscale = 1.5;
		        if (SkillAPI.getSettings().isAttributesEnabled() && caster instanceof Player) {
		            PlayerData data = SkillAPI.getPlayerData((Player) caster);
		            critscale += data.getAttribute("dexterity") * SkillAPI.getSettings().getDexCrit();
		        }
				amount *= critscale;
				PlayerCriticalEvent e = new PlayerCriticalEvent((Player) caster, amount, targets);
				Bukkit.getPluginManager().callEvent(e);
				if (e.isCancelled()) {
					return false;
				}
				amount = e.getDamage();
			}
			
			
			if (trueDmg) {
				skill.trueDamage(target, amount, caster);
			}
			else {
				skill.damage(target, amount, caster, classification);
			}
		}
		return targets.size() > 0;
	}
}
