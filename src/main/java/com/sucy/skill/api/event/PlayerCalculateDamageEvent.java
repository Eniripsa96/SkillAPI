/**
 * SkillAPI
 * com.sucy.skill.api.event
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
package com.sucy.skill.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a flag is applied to an entity
 */
public class PlayerCalculateDamageEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private LivingEntity caster;
	private LivingEntity target;
	private String[] types;
	private double damage;
	private double posmult;
	private double negmult;
	private double flat;


	/**
	 * Constructor
	 *
	 * @param entity the entity the flag was on
	 * @param flag   the flag that is to be applied
	 */
	public PlayerCalculateDamageEvent(LivingEntity caster, LivingEntity target, double damage, double posmult, double negmult, double flat, String[] types) {
		this.caster = caster;
		this.target = target;
		this.damage = damage;
		this.posmult = posmult;
		this.negmult = negmult;
		this.flat = flat;
		this.types = types;
	}
	public String[] getTypes() {
		return types;
	}
	
	public LivingEntity getTarget() {
		return target;
	}

	public void setTarget(LivingEntity target) {
		this.target = target;
	}
	
	public double getDamage() {
		return damage;
	}

	public double getPosmult() {
		return posmult;
	}
	public void setPosmult(double posmult) {
		this.posmult = posmult;
	}
	public double getNegmult() {
		return negmult;
	}
	public void setNegmult(double negmult) {
		this.negmult = negmult;
	}
	public double getFlat() {
		return flat;
	}
	public void setFlat(double flat) {
		this.flat = flat;
	}
	public void setTypes(String[] types) {
		this.types = types;
	}
	public LivingEntity getCaster() {
		return caster;
	}

	/**
	 * @return gets the handlers for the event
	 */
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * @return gets the handlers for the event
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}
	public void setCaster(LivingEntity caster) {
		this.caster = caster;
	}
	
	
}
