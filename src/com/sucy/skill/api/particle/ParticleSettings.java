/**
 * SkillAPI
 * com.sucy.skill.api.particle.ParticleSettings
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
package com.sucy.skill.api.particle;

import com.rit.sucy.config.parse.DataSection;
import org.bukkit.Material;

/**
 * Settings for playing a particle
 */
public class ParticleSettings {
    private static final String
            PARTICLE_KEY = "particle",
            MATERIAL_KEY = "material",
            DATA_KEY     = "type",
            AMOUNT_KEY   = "amount",
            DX_KEY       = "dx",
            DY_KEY       = "dy",
            DZ_KEY       = "dz",
            SPEED_KEY    = "speed";

    // Particle type
    public final ParticleType type;

    // Offset values
    public final float dx, dy, dz;

    // Particle speed
    public final float speed;

    // Particle amount
    public final int amount;

    // Particle extra data
    public final Material material;
    public final int      data;

    /**
     * Sets up a particle that doesn't require material data
     *
     * @param type   particle type
     * @param dx     DX value
     * @param dy     DY value
     * @param dz     DZ value
     * @param speed  particle speed
     * @param amount particle amount
     */
    public ParticleSettings(ParticleType type, float dx, float dy, float dz, float speed, int amount) {
        this.type = type;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.speed = speed;
        this.amount = amount;
        if (type.usesMat()) {
            throw new IllegalArgumentException("Must provide material data for " + type.name());
        } else {
            material = null;
            data = 0;
        }
    }

    /**
     * Sets up a particle that requires material data
     *
     * @param type     particle type
     * @param dx       DX value
     * @param dy       DY value
     * @param dz       DZ value
     * @param speed    particle speed
     * @param amount   particle amount
     * @param material material to use
     * @param data     material data value
     */
    public ParticleSettings(
            ParticleType type,
            float dx,
            float dy,
            float dz,
            float speed,
            int amount,
            Material material,
            int data) {
        this.type = type;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.speed = speed;
        this.amount = amount;
        if (type.usesMat()) {
            this.material = material;
            this.data = data;
        } else {
            this.material = material;
            this.data = data;
        }
    }

    /**
     * Loads a particle setup from config data
     *
     * @param config config data to load from
     */
    public ParticleSettings(DataSection config) {
        String type = config.getString(PARTICLE_KEY);
        this.type = ParticleLookup.find(type);
        this.dx = config.getFloat(DX_KEY, 0);
        this.dy = config.getFloat(DY_KEY, 0);
        this.dz = config.getFloat(DZ_KEY, 0);
        this.speed = config.getFloat(SPEED_KEY, 1);
        this.amount = config.getInt(AMOUNT_KEY, 1);

        if (this.type.usesMat()) {
            Material mat = null;
            int data = 0;
            try {
                mat = Material.valueOf(config.getString(MATERIAL_KEY).toUpperCase().replace(" ", "_"));
                data = config.getInt(DATA_KEY);
            } catch (Exception ex) { /* */ }
            this.material = mat;
            this.data = data;
        } else {
            this.material = null;
            this.data = 0;
        }
    }

    /**
     * Makes a new instance of the particle effect
     *
     * @param x X-axis coordinates
     * @param y Y-axis coordinates
     * @param z Z-axis coordinates
     *
     * @return packet instance
     *
     * @throws Exception
     */
    public Object instance(double x, double y, double z)
            throws Exception {
        return Particle.make(this, x, y, z);
    }
}
