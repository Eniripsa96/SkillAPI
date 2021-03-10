package com.sucy.skill.api.particle;

/**
 * SkillAPI © 2018
 * com.sucy.skill.api.particle.SpigotParticles
 */
public class SpigotParticles {
    private static boolean error = true;

//    public static void play(final Location loc, final String particle, final float dx, final float dy, final float dz, final int count, final float speed, final double distance) {
//        play(loc, particle, dx, dy, dz, count, speed, distance, null);
//    }
//
//    public static void playItem(final Location loc, final String particle, final float dx, final float dy, final float dz, final int count, final float speed, final double distance, final Material material) {
//        play(loc, particle, dx, dy, dz, count, speed, distance, material);
//    }
//
//    public static void playBlock(final Location loc, final String particle, final float dx, final float dy, final float dz, final int count, final float speed, final double distance, final Material material) {
//        play(loc, particle, dx, dy, dz, count, speed, distance, material);
//    }

//    private static void play(final Location loc, final String particle, final float dx, final float dy, final float dz, final int count, final float speed, final double distance, final Material material) {
//        final Particle effect = Particle.valueOf(particle.toUpperCase().replace(' ', '_'));
//        if (effect == null) return;
//
//        try {
//            final Object packet = com.sucy.skill.api.particle.Particle.make(
//                    effect.name(), loc.getX(), loc.getY(), loc.getZ(), dx, dy, dz, speed, count, material, 0);
//            com.sucy.skill.api.particle.Particle.send(loc, ImmutableList.of(packet), distance);
//        } catch (final Exception ex) {
//            if (error) {
//                ex.printStackTrace();
//                error = false;
//            }
//        }
//    }

}
