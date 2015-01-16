package com.sucy.skill.api.util;

import com.rit.sucy.reflect.Particle;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.enums.Direction;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Random;

/**
 * Helper class for playing particles via config strings in various ways.
 */
public class ParticleHelper
{
    /**
     * Settings key for the arrangement type of particles
     */
    public static final String ARRANGEMENT_KEY = "arrangement";

    /**
     * Settings key for the type of particle
     */
    public static final String PARTICLE_KEY = "particle";

    /**
     * Settings key for the radius of the particle arrangement
     */
    public static final String RADIUS_KEY = "radius";

    /**
     * Settings key for the amount of particles to play
     */
    public static final String AMOUNT_KEY = "amount";

    /**
     * Settings key for the particle arrangement direction (circles only)
     */
    public static final String DIRECTION_KEY = "direction";

    /**
     * Settings key for the Bukkit effects' data (default 0)
     */
    public static final String DATA_KEY = "data";

    /**
     * Settings key for the reflection particles' visible radius (default 25)
     */
    public static final String VISIBLE_RADIUS_KEY = "visible-radius";

    /**
     * Settings key for the reflection particles' X-offset (default 0)
     */
    public static final String DX_KEY = "dx";

    /**
     * Settings key for the reflection particles' Y-offset (default 0)
     */
    public static final String DY_KEY = "dx";

    /**
     * Settings key for the reflection particles' Z-offset (default 0)
     */
    public static final String DZ_KEY = "dx";

    /**
     * Settings key for the reflection particles' "speed" value (default 1)
     */
    public static final String SPEED_KEY = "speed";

    private static final Random random = new Random();

    /**
     * Plays an entity effect at the given location
     *
     * @param loc    location to play the effect
     * @param effect entity effect to play
     */
    public static void play(Location loc, EntityEffect effect)
    {
        Entity wolf = loc.getWorld().spawnEntity(loc, EntityType.WOLF);
        wolf.playEffect(effect);
        wolf.remove();
    }

    /**
     * Plays particles about the given location using the given settings
     *
     * @param loc      location to center the effect around
     * @param settings data to play the particles with
     */
    public static void play(Location loc, Settings settings)
    {
        String particle = settings.getString(PARTICLE_KEY, "invalid");
        if (settings.has(ARRANGEMENT_KEY))
        {
            double radius =  settings.get(RADIUS_KEY, 3.0);
            int amount = settings.getInt(AMOUNT_KEY, 10);

            String arrangement = settings.getString(ARRANGEMENT_KEY).toLowerCase();
            if (arrangement.equals("circle"))
            {
                Direction dir = null;
                if (settings.has(DIRECTION_KEY))
                {
                    try
                    {
                        dir = Direction.valueOf(settings.getString(DIRECTION_KEY));
                    }
                    catch (Exception ex)
                    { /* Use default value */ }
                }
                if (dir == null)
                {
                    dir = Direction.XZ;
                }

                fillCircle(loc, particle, settings, radius, amount, dir);
            }
            else if (arrangement.equals("sphere"))
            {
                fillSphere(loc, particle, settings, radius, amount);
            }
            else if (arrangement.equals("hemisphere"))
            {
                fillHemisphere(loc, particle, settings, radius, amount);
            }
        }
        else
        {
            play(loc, particle, settings);
        }
    }

    /**
     * Plays a particle at the given location based on the string
     *
     * @param loc      location to play the effect
     * @param particle particle to play
     * @param settings data to play the particle with
     */
    public static void play(Location loc, String particle, Settings settings)
    {
        particle = particle.toLowerCase().replace("_", " ");

        // Normal bukkit effects
        if (BUKKIT_EFFECTS.containsKey(particle))
        {
            loc.getWorld().playEffect(loc, BUKKIT_EFFECTS.get(particle), settings.getInt(DATA_KEY, 0));
        }

        // Entity effects
        else if (ENTITY_EFFECTS.containsKey(particle))
        {
            play(loc, ENTITY_EFFECTS.get(particle));
        }

        // Reflection particles
        else if (REFLECT_PARTICLES.containsKey(particle))
        {
            Particle.play(REFLECT_PARTICLES.get(particle), loc, settings.getInt(VISIBLE_RADIUS_KEY, 25), (float) settings.get(DX_KEY, 0.0), (float) settings.get(DY_KEY, 0.0), (float) settings.get(DZ_KEY, 0.0), (float) settings.get(SPEED_KEY, 1.0), 1);
        }
    }

    /**
     * Plays several of a particle type randomly within a circle
     *
     * @param loc      center location of the circle
     * @param particle particle to play
     * @param settings data to play the particle with
     * @param radius   radius of the circle
     * @param amount   amount of particles to play
     */
    public static void fillCircle(Location loc, String particle, Settings settings, double radius, int amount, Direction direction)
    {
        Location temp = loc.clone();
        double rSquared = radius * radius;
        double twoRadius = radius * 2;
        int index = 0;

        // Play the particles
        while (index < amount) {
            if (direction == Direction.XY || direction == Direction.XZ)
                temp.setX(loc.getX() + random.nextDouble() * twoRadius - radius);
            if (direction == Direction.XY || direction == Direction.YZ)
                temp.setY(loc.getY() + random.nextDouble() * twoRadius - radius);
            if (direction == Direction.XZ || direction == Direction.YZ)
                temp.setZ(loc.getZ() + random.nextDouble() * twoRadius - radius);

            if (temp.distanceSquared(loc) > rSquared) continue;

            play(temp, particle, settings);
            index++;
        }
    }

    /**
     * Randomly plays particle effects within the sphere
     *
     * @param loc      location to center the effect around
     * @param particle the string value for the particle
     * @param settings data to play the particle with
     * @param radius   radius of the sphere
     * @param amount   amount of particles to use
     */
    public static void fillSphere(Location loc, String particle, Settings settings, double radius, int amount) {
        Location temp = loc.clone();
        double rSquared = radius * radius;
        double twoRadius = radius * 2;
        int index = 0;

        // Play the particles
        while (index < amount) {
            temp.setX(loc.getX() + random.nextDouble() * twoRadius - radius);
            temp.setY(loc.getY() + random.nextDouble() * twoRadius - radius);
            temp.setZ(loc.getZ() + random.nextDouble() * twoRadius - radius);

            if (temp.distanceSquared(loc) > rSquared) continue;

            play(temp, particle, settings);
            index++;
        }
    }

    /**
     * Randomly plays particle effects within the hemisphere
     *
     * @param loc      location to center the effect around
     * @param particle the string value for the particle
     * @param settings data to play the particle with
     * @param radius   radius of the sphere
     * @param amount   amount of particles to use
     */
    public static void fillHemisphere(Location loc, String particle, Settings settings, double radius, int amount) {
        Location temp = loc.clone();
        double rSquared = radius * radius;
        double twoRadius = radius * 2;
        int index = 0;

        // Play the particles
        while (index < amount) {
            temp.setX(loc.getX() + random.nextDouble() * twoRadius - radius);
            temp.setY(loc.getY() + random.nextDouble() * radius);
            temp.setZ(loc.getZ() + random.nextDouble() * twoRadius - radius);

            if (temp.distanceSquared(loc) > rSquared) continue;

            play(temp, particle, settings);
            index++;
        }
    }

    private static final HashMap<String, Effect> BUKKIT_EFFECTS = new HashMap<String, Effect>()
    {{
            put("smoke", Effect.SMOKE);
            put("ender signal", Effect.ENDER_SIGNAL);
            put("mobspawner flames", Effect.MOBSPAWNER_FLAMES);
            put("potion break", Effect.POTION_BREAK);
        }};

    private static final HashMap<String, EntityEffect> ENTITY_EFFECTS = new HashMap<String, EntityEffect>()
    {{
            put("death", EntityEffect.DEATH);
            put("hurt", EntityEffect.HURT);
            put("sheep eat", EntityEffect.SHEEP_EAT);
            put("wolf hearts", EntityEffect.WOLF_HEARTS);
            put("wolf shake", EntityEffect.WOLF_SHAKE);
            put("wolf smoke", EntityEffect.WOLF_SMOKE);
        }};

    private static final HashMap<String, String> REFLECT_PARTICLES = new HashMap<String, String>()
    {{
            put("angryvillager", "angryVillager");
            put("bubble", "bubble");
            put("cloud", "cloud");
            put("crit", "crit");
            put("deathsuspend", "deathSuspend");
            put("driplava", "dripLava");
            put("dripwater", "dripWater");
            put("enchantmenttable", "enchantmenttable");
            put("explode", "explode");
            put("fireworksspark", "fireworksSpark");
            put("flame", "flame");
            put("footstep", "footstep");
            put("happyvillager", "happyVillager");
            put("heart", "heart");
            put("hugeexplosion", "hugeexplosion");
            put("instantspell", "instantSpell");
            put("largeexplode", "largeexplode");
            put("largesmoke", "largesmoke");
            put("lava", "lava");
            put("magiccrit", "magicCrit");
            put("mobspell", "mobSpell");
            put("mobspellambient", "mobSpellAmbient");
            put("note", "note");
            put("portal", "portal");
            put("reddust", "reddust");
            put("slime", "slime");
            put("snowballpoof", "snowballpoof");
            put("snowshovel", "snowshovel");
            put("spell", "spell");
            put("splash", "splash");
            put("suspend", "suspend");
            put("tilecrack", "tilecrack");
            put("townaura", "townaura");
            put("witchmagic", "witchMagic");
        }};
}
