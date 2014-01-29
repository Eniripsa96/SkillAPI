package com.sucy.skill.mechanic;

import com.sucy.skill.BukkitHelper;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.Status;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.EmbedData;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.util.TargetHelper;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Mechanic for applying embedded effects under a condition
 */
public class ConditionMechanic implements IMechanic {

    private static final String
            CONDITION = "Condition";

    private static final int
        CONDITIONS = 256, // 2 ^ 8
        OPERATORS = 16;   // 2 ^ 4

    private static final int
        STUN = 0,
        ROOT = 1,
        INVINCIBLE = 2,
        ABSORB = 3,
        SILENCE = 4,
        DISARM = 5,
        CURSE = 6,
        FIRE = 7,
        SPEED = 8,
        SLOWNESS = 9,
        HASTE = 10,
        FATIGUE = 11,
        STRENGTH = 12,
        JUMP = 13,
        NAUSEA = 14,
        REGENERATION = 15,
        RESISTANCE = 16,
        FIRE_RESISTANCE = 17,
        WATER_BREATHING = 18,
        INVISIBILITY = 19,
        BLINDNESS = 20,
        NIGHT_VISION = 21,
        HUNGER = 22,
        WEAKNESS = 23,
        POISON = 24,
        WITHER = 25,
        HEALTH = 26,
        ABSORPTION = 27,
        SATURATION = 28,
        POTION = 29,
        STATUS = 30,
        BIOME_BEACH = 31,
        BIOME_DESERT = 32,
        BIOME_DESERT_HILLS = 33,
        BIOME_EXTREME_HILLS = 34,
        BIOME_FOREST = 35,
        BIOME_FOREST_HILLS = 36,
        BIOME_FROZEN_OCEAN = 37,
        BIOME_FROZEN_RIVER = 38,
        BIOME_HELL = 39,
        BIOME_ICE_MOUNTAINS = 40,
        BIOME_ICE_PLAINS = 41,
        BIOME_JUNGLE = 42,
        BIOME_JUNGLE_HILLS = 43,
        BIOME_MUSHROOM_ISLAND = 44,
        BIOME_MUSHROOM_SHORE = 45,
        BIOME_OCEAN = 46,
        BIOME_PLAINS = 47,
        BIOME_RIVER = 48,
        BIOME_SKY = 49,
        BIOME_SMALL_MOUNTAINS = 50,
        BIOME_SWAMPLAND = 51,
        BIOME_TAIGA = 52,
        BIOME_TAIGA_HILLS = 53,
        TIME_DAY = 54,
        TIME_NIGHT = 55,
        IN_WATER = 56,
        OUT_OF_WATER = 57,
        ITEM_AXE = 58,
        ITEM_HOE = 59,
        ITEM_PICKAXE = 60,
        ITEM_SHOVEL = 61,
        ITEM_SWORD = 62,
        ITEM_WOOD = 63,
        ITEM_STONE = 64,
        ITEM_IRON = 65,
        ITEM_GOLD = 66,
        ITEM_DIAMOND = 67,
        TARGET_FRONT = 68,
        TARGET_BACK = 69;

    private static final int
        STOP = 0,
        AND = 1,
        OR = 2,
        NAND = 3,
        NOR = 4,
        XOR = 5,
        XNOR = 6;

    /**
     * Grants a temporary damage bonus to the targets
     *
     * @param player  player using the skill
     * @param data    data of the player using the skill
     * @param skill   skill being used
     * @param target  target type of the skill
     * @param targets targets for the effects
     * @return        true if was able to use
     */
    @Override
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, Target target, List<LivingEntity> targets) {

        if (targets.isEmpty()) return false;

        // Get attributes
        int statement = skill.getValue(CONDITION);

        // Prepare the embed data in case it needs to be used
        EmbedData embedData = new EmbedData(player, data, skill);
        skill.beginUsage();

        // Loop through each target
        boolean worked = false;
        for (LivingEntity t : targets) {

            // Initial values
            int targetCondition = statement;
            int operator = OR;
            boolean success = false;

            // Loop through each part of the statement
            do {

                // Grab the next condition
                int condition = targetCondition % CONDITIONS;
                targetCondition /= CONDITIONS;

                // Check the condition
                boolean passed = false;
                Biome biome = player.getLocation().getBlock().getBiome();
                ItemType type = ItemType.getType(player.getItemInHand());
                ItemMat mat = ItemMat.getMat(type == ItemType.NULL ? null : player.getItemInHand());
                if (condition == STUN && data.getAPI().getStatusHolder(t).hasStatus(Status.STUN)) passed = true;
                else if (condition == ROOT && data.getAPI().getStatusHolder(t).hasStatus(Status.ROOT)) passed = true;
                else if (condition == INVINCIBLE && data.getAPI().getStatusHolder(t).hasStatus(Status.INVINCIBLE)) passed = true;
                else if (condition == ABSORB && data.getAPI().getStatusHolder(t).hasStatus(Status.ABSORB)) passed = true;
                else if (condition == SILENCE && data.getAPI().getStatusHolder(t).hasStatus(Status.SILENCE)) passed = true;
                else if (condition == DISARM && data.getAPI().getStatusHolder(t).hasStatus(Status.DISARM)) passed = true;
                else if (condition == CURSE && data.getAPI().getStatusHolder(t).hasStatus(Status.CURSE)) passed = true;
                else if (condition == FIRE && t.getFireTicks() > 0) passed = true;
                else if (condition == SPEED && t.hasPotionEffect(PotionEffectType.SPEED)) passed = true;
                else if (condition == SLOWNESS && t.hasPotionEffect(PotionEffectType.SLOW)) passed = true;
                else if (condition == HASTE && t.hasPotionEffect(PotionEffectType.FAST_DIGGING)) passed = true;
                else if (condition == FATIGUE && t.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) passed = true;
                else if (condition == STRENGTH && t.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) passed = true;
                else if (condition == JUMP && t.hasPotionEffect(PotionEffectType.JUMP)) passed = true;
                else if (condition == NAUSEA && t.hasPotionEffect(PotionEffectType.CONFUSION)) passed = true;
                else if (condition == REGENERATION && t.hasPotionEffect(PotionEffectType.REGENERATION)) passed = true;
                else if (condition == RESISTANCE && t.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) passed = true;
                else if (condition == FIRE_RESISTANCE && t.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) passed = true;
                else if (condition == WATER_BREATHING && t.hasPotionEffect(PotionEffectType.WATER_BREATHING)) passed = true;
                else if (condition == INVISIBILITY && t.hasPotionEffect(PotionEffectType.INVISIBILITY)) passed = true;
                else if (condition == BLINDNESS && t.hasPotionEffect(PotionEffectType.BLINDNESS)) passed = true;
                else if (condition == NIGHT_VISION && t.hasPotionEffect(PotionEffectType.NIGHT_VISION)) passed = true;
                else if (condition == HUNGER && t.hasPotionEffect(PotionEffectType.HUNGER)) passed = true;
                else if (condition == WEAKNESS && t.hasPotionEffect(PotionEffectType.WEAKNESS)) passed = true;
                else if (condition == POISON && t.hasPotionEffect(PotionEffectType.POISON)) passed = true;
                else if (condition == WITHER && t.hasPotionEffect(PotionEffectType.WITHER)) passed = true;
                else if (BukkitHelper.isVersionAtLeast(BukkitHelper.MC_1_6_2_MIN) && condition == HEALTH && t.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) passed = true;
                else if (BukkitHelper.isVersionAtLeast(BukkitHelper.MC_1_6_2_MIN) && condition == ABSORPTION && t.hasPotionEffect(PotionEffectType.ABSORPTION)) passed = true;
                else if (BukkitHelper.isVersionAtLeast(BukkitHelper.MC_1_6_2_MIN) && condition == SATURATION && t.hasPotionEffect(PotionEffectType.SATURATION)) passed = true;
                else if (condition == POTION && t.getActivePotionEffects().size() > 0) passed = true;
                else if (condition == STATUS && data.getAPI().getStatusHolder(t).hasStatuses()) passed = true;
                else if (condition == BIOME_BEACH && biome == Biome.BEACH) passed = true;
                else if (condition == BIOME_DESERT && biome == Biome.DESERT) passed = true;
                else if (condition == BIOME_DESERT_HILLS && biome == Biome.DESERT_HILLS) passed = true;
                else if (condition == BIOME_EXTREME_HILLS && biome == Biome.EXTREME_HILLS) passed = true;
                else if (condition == BIOME_FOREST && biome == Biome.FOREST) passed = true;
                else if (condition == BIOME_FOREST_HILLS && biome == Biome.FOREST_HILLS) passed = true;
                else if (condition == BIOME_FROZEN_OCEAN && biome == Biome.FROZEN_OCEAN) passed = true;
                else if (condition == BIOME_FROZEN_RIVER && biome == Biome.FROZEN_RIVER) passed = true;
                else if (condition == BIOME_HELL && biome == Biome.HELL) passed = true;
                else if (condition == BIOME_ICE_MOUNTAINS && biome == Biome.ICE_MOUNTAINS) passed = true;
                else if (condition == BIOME_ICE_PLAINS && biome == Biome.ICE_PLAINS) passed = true;
                else if (condition == BIOME_JUNGLE && biome == Biome.JUNGLE) passed = true;
                else if (condition == BIOME_JUNGLE_HILLS && biome == Biome.JUNGLE_HILLS) passed = true;
                else if (condition == BIOME_MUSHROOM_ISLAND && biome == Biome.MUSHROOM_ISLAND) passed = true;
                else if (condition == BIOME_MUSHROOM_SHORE && biome == Biome.MUSHROOM_SHORE) passed = true;
                else if (condition == BIOME_OCEAN && biome == Biome.OCEAN) passed = true;
                else if (condition == BIOME_PLAINS && biome == Biome.PLAINS) passed = true;
                else if (condition == BIOME_RIVER && biome == Biome.RIVER) passed = true;
                else if (condition == BIOME_SKY && biome == Biome.SKY) passed = true;
                else if (condition == BIOME_SMALL_MOUNTAINS && biome == Biome.SMALL_MOUNTAINS) passed = true;
                else if (condition == BIOME_SWAMPLAND && biome == Biome.SWAMPLAND) passed = true;
                else if (condition == BIOME_TAIGA && biome == Biome.TAIGA) passed = true;
                else if (condition == BIOME_TAIGA_HILLS && biome == Biome.TAIGA_HILLS) passed = true;
                else if (condition == TIME_DAY && (player.getWorld().getTime() < 12300 || player.getWorld().getTime() > 23850)) passed = true;
                else if (condition == TIME_NIGHT && (player.getWorld().getTime() >= 12300 && player.getWorld().getTime() <= 23850)) passed = true;
                else if (condition == IN_WATER && player.getLocation().getBlock().getType() == Material.WATER) passed = true;
                else if (condition == OUT_OF_WATER && player.getLocation().getBlock().getType() != Material.WATER) passed = true;
                else if (condition == ITEM_AXE && type == ItemType.AXE) passed = true;
                else if (condition == ITEM_HOE && type == ItemType.HOE) passed = true;
                else if (condition == ITEM_PICKAXE && type == ItemType.PICKAXE) passed = true;
                else if (condition == ITEM_SHOVEL && type == ItemType.SHOVEL) passed = true;
                else if (condition == ITEM_SWORD && type == ItemType.SWORD) passed = true;
                else if (condition == ITEM_WOOD && mat == ItemMat.WOOD) passed = true;
                else if (condition == ITEM_STONE && mat == ItemMat.STONE) passed = true;
                else if (condition == ITEM_IRON && mat == ItemMat.IRON) passed = true;
                else if (condition == ITEM_GOLD && mat == ItemMat.GOLD) passed = true;
                else if (condition == ITEM_DIAMOND && mat == ItemMat.DIAMOND) passed = true;
                else if (condition == TARGET_FRONT && TargetHelper.isInFront(t, player)) passed = true;
                else if (condition == TARGET_BACK && !TargetHelper.isInFront(t, player)) passed = true;

                // Operators
                if (operator == AND) success = success && passed;
                else if (operator == OR) success = success || passed;
                else if (operator == NAND) success = !(success && passed);
                else if (operator == NOR) success = !(success || passed);
                else if (operator == XOR) success = success != passed;
                else if (operator == XNOR) success = success == passed;

                // Grab the next operator
                operator = targetCondition % OPERATORS;
                targetCondition /= OPERATORS;
            }

            // Stop when the next operator is a "Stop" operator
            while (operator != STOP);

            // Apply the embedded effects if the condition passed
            if (success) {
                embedData.resolveNonTarget(t.getLocation());
                embedData.resolveTarget(t);
                worked = true;
            }
        }
        skill.stopUsage();

        return worked;
    }

    /**
     * Applies default values for the mechanic attributes
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the attribute
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        if (!skill.isSet(CONDITION)) skill.setValue(CONDITION, 0);
    }

    /**
     * @return names of the attributes used by the mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }

    /**
     * Tool type enum for items
     */
    public enum ItemType {

        AXE,
        HOE,
        PICKAXE,
        SHOVEL,
        SWORD,
        NULL
        ;

        /**
         * Retrieves the tool type of the item
         *
         * @param item item to retrieve for
         * @return     tool type of the item
         */
        public static ItemType getType(ItemStack item) {
            if (item == null) return NULL;
            String name = item.getType().name();
            if (name.contains("PICKAXE")) return PICKAXE;
            if (name.contains("AXE")) return AXE;
            if (name.contains("HOE")) return HOE;
            if (name.contains("SPADE")) return SHOVEL;
            if (name.contains("SWORD")) return SWORD;
            return null;
        }
    }

    /**
     * Material type enum for tools
     */
    public enum ItemMat {

        WOOD,
        STONE,
        IRON,
        GOLD,
        DIAMOND,
        NULL
        ;

        /**
         * Retrieves the item material of the item
         *
         * @param item item to retrieve for
         * @return     material type of the item
         */
        public static ItemMat getMat(ItemStack item) {
            if (item == null) return NULL;
            String name = item.getType().name();
            if (name.contains("WOOD")) return WOOD;
            if (name.contains("STONE")) return STONE;
            if (name.contains("IRON")) return IRON;
            if (name.contains("GOLD")) return GOLD;
            if (name.contains("DIAMOND")) return DIAMOND;
            return NULL;
        }
    }
}
