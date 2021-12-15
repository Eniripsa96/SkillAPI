package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.ParticleSettings;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.util.ParticleHelper;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

public class ParticleLineMechanic extends MechanicComponent {

    private static final String POINTS = "points";
    private static final String UPWARD = "upward";

    @Override
    public String getKey() {
        return "particle line";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, double critChance) {
        if (targets.size() == 0) {
            return false;
        }

        double points = settings.getDouble(POINTS, 20);
        double upward = settings.getDouble(UPWARD, 0);

        Vector offset = new Vector(0, upward, 0);

        final Settings copy = new Settings(settings);
        copy.set(ParticleHelper.PARTICLES_KEY, parseValues(caster, ParticleHelper.PARTICLES_KEY, level, 1, 0), 0);
        copy.set(ParticleHelper.RADIUS_KEY, parseValues(caster, ParticleHelper.RADIUS_KEY, level, 0, 0), 0);
        copy.set("level", level);

        LivingEntity target = targets.get(0);

        Location startLocation = caster.getEyeLocation();
        Location endLocation = target.getEyeLocation();

        ParticleSettings particleSettings = new ParticleSettings(caster, settings);

        Vector line = endLocation.clone().toVector().subtract(startLocation.toVector());
        double length = line.length();
        double steps = length / points;
        for (double i = steps; i < length; i += steps) {
            line.multiply(i);
            startLocation.add(line);
            ParticleHelper.play(particleSettings, startLocation.clone().add(offset));
            startLocation.subtract(line);
            line.normalize();
        }

        return targets.size() > 0;
    }

    private void rotate(Vector vec, double cos, double sin) {
        double x = vec.getX() * cos - vec.getZ() * sin;
        vec.setZ(vec.getX() * sin + vec.getZ() * cos);
        vec.setX(x);
    }

}
