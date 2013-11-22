package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Mechanic for damaging targets based on missing mana
 */
public class SoundMechanic implements IMechanic {

    private static final String
        SOUND = "Sound",
        VOLUME = "Volume",
        PITCH = "Pitch";

    /**
     * Damages targets based on missing mana
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

        // Requires a target
        if (targets.size() == 0) return false;

        // Sound
        Sound sound;
        int soundIndex = skill.getValue(SOUND);
        if (SOUNDS.containsKey(soundIndex)) sound = SOUNDS.get(soundIndex);
        else sound = SOUNDS.get(0);

        // Volume
        float volume = skill.getValue(SOUND) / 100.0f;
        volume = Math.max(Math.min(volume, 1.0f), 0.0f);

        // Pitch
        float pitch = (3 * skill.getValue(PITCH) + 500) / 400.0f;
        pitch = Math.min(Math.max(pitch, 0.5f), 2.0f);

        // Play the sound at each target
        for (LivingEntity entity : targets) {
            entity.getWorld().playSound(entity.getLocation(), sound, volume, pitch);
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
    public void applyDefaults(DynamicSkill skill, String prefix) {
        if (!skill.isSet(SOUND)) skill.setValue(SOUND, 0);
        if (!skill.isSet(VOLUME)) skill.setValue(VOLUME, 100);
        if (!skill.isSet(PITCH)) skill.setValue(PITCH, 0);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }

    private static final HashMap<Integer, Sound> SOUNDS = new HashMap<Integer, Sound>() {{
        put(0, Sound.AMBIENCE_CAVE);
        put(1, Sound.AMBIENCE_RAIN);
        put(2, Sound.AMBIENCE_THUNDER);
        put(3, Sound.ANVIL_BREAK);
        put(4, Sound.ANVIL_LAND);
        put(5, Sound.ANVIL_USE);
        put(6, Sound.ARROW_HIT);
        put(7, Sound.BAT_DEATH);
        put(8, Sound.BAT_HURT);
        put(9, Sound.BAT_IDLE);
        put(10, Sound.BAT_LOOP);
        put(11, Sound.BAT_TAKEOFF);
        put(12, Sound.BLAZE_BREATH);
        put(13, Sound.BLAZE_DEATH);
        put(14, Sound.BLAZE_HIT);
        put(15, Sound.BREATH);
        put(16, Sound.BURP);
        put(17, Sound.CAT_HISS);
        put(18, Sound.CAT_HIT);
        put(19, Sound.CAT_MEOW);
        put(20, Sound.CAT_PURR);
        put(21, Sound.CAT_PURREOW);
        put(22, Sound.CHEST_CLOSE);
        put(23, Sound.CHEST_OPEN);
        put(24, Sound.CHICKEN_EGG_POP);
        put(25, Sound.CHICKEN_HURT);
        put(26, Sound.CHICKEN_IDLE);
        put(27, Sound.CHICKEN_WALK);
        put(28, Sound.CLICK);
        put(29, Sound.COW_HURT);
        put(30, Sound.COW_IDLE);
        put(31, Sound.COW_WALK);
        put(32, Sound.CREEPER_DEATH);
        put(33, Sound.CREEPER_HISS);
        put(34, Sound.DIG_GRASS);
        put(35, Sound.DIG_GRAVEL);
        put(36, Sound.DIG_SAND);
        put(37, Sound.DIG_SNOW);
        put(38, Sound.DIG_STONE);
        put(39, Sound.DIG_WOOD);
        put(40, Sound.DIG_WOOL);
        put(41, Sound.DONKEY_ANGRY);
        put(42, Sound.DONKEY_DEATH);
        put(43, Sound.DONKEY_HIT);
        put(44, Sound.DONKEY_IDLE);
        put(45, Sound.DOOR_CLOSE);
        put(46, Sound.DOOR_OPEN);
        put(47, Sound.DRINK);
        put(48, Sound.EAT);
        put(49, Sound.ENDERDRAGON_DEATH);
        put(50, Sound.ENDERDRAGON_GROWL);
        put(51, Sound.ENDERDRAGON_HIT);
        put(52, Sound.ENDERDRAGON_WINGS);
        put(53, Sound.ENDERMAN_DEATH);
        put(54, Sound.ENDERMAN_HIT);
        put(55, Sound.ENDERMAN_IDLE);
        put(56, Sound.ENDERMAN_SCREAM);
        put(57, Sound.ENDERMAN_STARE);
        put(58, Sound.ENDERMAN_TELEPORT);
        put(59, Sound.EXPLODE);
        put(60, Sound.FALL_BIG);
        put(61, Sound.FALL_SMALL);
        put(62, Sound.FIRE);
        put(63, Sound.FIRE_IGNITE);
        put(64, Sound.FIREWORK_BLAST);
        put(65, Sound.FIREWORK_BLAST2);
        put(66, Sound.FIREWORK_LARGE_BLAST);
        put(67, Sound.FIREWORK_LARGE_BLAST2);
        put(68, Sound.FIREWORK_LAUNCH);
        put(69, Sound.FIREWORK_TWINKLE);
        put(70, Sound.FIREWORK_TWINKLE2);
        put(71, Sound.FIZZ);
        put(72, Sound.FUSE);
        put(73, Sound.GHAST_CHARGE);
        put(74, Sound.GHAST_DEATH);
        put(75, Sound.GHAST_FIREBALL);
        put(76, Sound.GHAST_MOAN);
        put(77, Sound.GHAST_SCREAM);
        put(78, Sound.GHAST_SCREAM2);
        put(79, Sound.GLASS);
        put(80, Sound.HORSE_ANGRY);
        put(81, Sound.HORSE_ARMOR);
        put(82, Sound.HORSE_BREATHE);
        put(83, Sound.HORSE_GALLOP);
        put(84, Sound.HORSE_HIT);
        put(85, Sound.HORSE_IDLE);
        put(86, Sound.HORSE_JUMP);
        put(87, Sound.HORSE_LAND);
        put(88, Sound.HORSE_SADDLE);
        put(89, Sound.HORSE_SKELETON_DEATH);
        put(90, Sound.HORSE_SKELETON_IDLE);
        put(91, Sound.HORSE_SOFT);
        put(92, Sound.HORSE_WOOD);
        put(93, Sound.HORSE_ZOMBIE_DEATH);
        put(94, Sound.HORSE_ZOMBIE_HIT);
        put(95, Sound.HORSE_ZOMBIE_IDLE);
        put(96, Sound.HURT);
        put(97, Sound.HURT_FLESH);
        put(98, Sound.IRONGOLEM_DEATH);
        put(99, Sound.IRONGOLEM_HIT);
        put(100, Sound.IRONGOLEM_THROW);
        put(101, Sound.IRONGOLEM_WALK);
        put(102, Sound.ITEM_BREAK);
        put(103, Sound.ITEM_PICKUP);
        put(104, Sound.LAVA);
        put(105, Sound.LAVA_POP);
        put(106, Sound.LEVEL_UP);
        put(107, Sound.MAGMACUBE_JUMP);
        put(108, Sound.MAGMACUBE_WALK);
        put(109, Sound.MAGMACUBE_WALK2);
        put(110, Sound.MINECART_BASE);
        put(111, Sound.MINECART_INSIDE);
        put(112, Sound.NOTE_BASS);
        put(113, Sound.NOTE_BASS_DRUM);
        put(114, Sound.NOTE_BASS_GUITAR);
        put(115, Sound.NOTE_PIANO);
        put(116, Sound.NOTE_PLING);
        put(117, Sound.NOTE_SNARE_DRUM);
        put(118, Sound.NOTE_STICKS);
        put(119, Sound.ORB_PICKUP);
        put(120, Sound.PIG_DEATH);
        put(121, Sound.PIG_IDLE);
        put(122, Sound.PIG_WALK);
        put(123, Sound.PISTON_EXTEND);
        put(124, Sound.PISTON_RETRACT);
        put(125, Sound.PORTAL);
        put(126, Sound.PORTAL_TRAVEL);
        put(127, Sound.PORTAL_TRIGGER);
        put(128, Sound.SHEEP_IDLE);
        put(129, Sound.SHEEP_SHEAR);
        put(130, Sound.SHEEP_WALK);
        put(131, Sound.SHOOT_ARROW);
        put(132, Sound.SILVERFISH_HIT);
        put(133, Sound.SILVERFISH_IDLE);
        put(134, Sound.SILVERFISH_KILL);
        put(135, Sound.SILVERFISH_WALK);
        put(136, Sound.SKELETON_DEATH);
        put(137, Sound.SKELETON_HURT);
        put(138, Sound.SKELETON_IDLE);
        put(139, Sound.SKELETON_WALK);
        put(140, Sound.SLIME_ATTACK);
        put(141, Sound.SLIME_WALK);
        put(142, Sound.SLIME_WALK2);
        put(143, Sound.SPIDER_DEATH);
        put(144, Sound.SPIDER_IDLE);
        put(145, Sound.SPIDER_WALK);
        put(146, Sound.SPLASH);
        put(147, Sound.SPLASH2);
        put(148, Sound.STEP_GRASS);
        put(149, Sound.STEP_GRAVEL);
        put(150, Sound.STEP_LADDER);
        put(151, Sound.STEP_SAND);
        put(152, Sound.STEP_SNOW);
        put(153, Sound.STEP_STONE);
        put(154, Sound.STEP_WOOD);
        put(155, Sound.STEP_WOOL);
        put(156, Sound.SUCCESSFUL_HIT);
        put(157, Sound.SWIM);
        put(158, Sound.VILLAGER_DEATH);
        put(159, Sound.VILLAGER_HAGGLE);
        put(160, Sound.VILLAGER_HIT);
        put(161, Sound.VILLAGER_IDLE);
        put(162, Sound.VILLAGER_NO);
        put(163, Sound.VILLAGER_YES);
        put(164, Sound.WATER);
        put(165, Sound.WITHER_DEATH);
        put(166, Sound.WITHER_HURT);
        put(167, Sound.WITHER_IDLE);
        put(168, Sound.WITHER_SHOOT);
        put(169, Sound.WITHER_SPAWN);
        put(170, Sound.WOLF_BARK);
        put(171, Sound.WOLF_DEATH);
        put(172, Sound.WOLF_GROWL);
        put(173, Sound.WOLF_HOWL);
        put(174, Sound.WOLF_HURT);
        put(175, Sound.WOLF_PANT);
        put(176, Sound.WOLF_SHAKE);
        put(177, Sound.WOLF_WALK);
        put(178, Sound.WOLF_WHINE);
        put(179, Sound.WOOD_CLICK);
        put(180, Sound.ZOMBIE_DEATH);
        put(181, Sound.ZOMBIE_HURT);
        put(182, Sound.ZOMBIE_IDLE);
        put(183, Sound.ZOMBIE_INFECT);
        put(184, Sound.ZOMBIE_METAL);
        put(185, Sound.ZOMBIE_PIG_ANGRY);
        put(186, Sound.ZOMBIE_PIG_DEATH);
        put(187, Sound.ZOMBIE_PIG_HURT);
        put(188, Sound.ZOMBIE_PIG_IDLE);
        put(189, Sound.ZOMBIE_REMEDY);
        put(190, Sound.ZOMBIE_UNFECT);
        put(191, Sound.ZOMBIE_WOOD);
        put(192, Sound.ZOMBIE_WOODBREAK);
    }};
}
