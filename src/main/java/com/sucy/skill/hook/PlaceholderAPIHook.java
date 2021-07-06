package com.sucy.skill.hook;

import com.google.common.collect.ImmutableMap;
import com.rit.sucy.mobs.MobManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.dynamic.DynamicSkill;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * SkillAPI © 2018
 * com.sucy.skill.hook.PlaceholderAPIHook
 */
public class PlaceholderAPIHook {

    public static String format(final String message, final Player player) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }

    public static void registerPlaceholder(SkillAPI skillAPI) {
        new SkillAPIPlaceholders().register();
    }

    public static class SkillAPIPlaceholders extends PlaceholderExpansion {

        @NotNull
        public String getIdentifier() {
            return "sapi";
        }

        @Override
        public boolean persist(){
            return true;
        }

        @Override
        public boolean canRegister(){
            return true;
        }

        @NotNull
        public String getAuthor() {
            return "Eniripsa96";
        }

        @NotNull
        public String getVersion() {
            return "1.0";
        }

        private final Map<String, BiFunction<PlayerData, String, String>> PLACEHOLDERS = getMap();

        @Override
        public String onPlaceholderRequest(Player player, String id) {
            String key, param;
            int paramIndex = id.indexOf(':') + 1;

            if (paramIndex > 0) {
                param = id.substring(paramIndex);
                key = id.substring(0, paramIndex);
            } else {

                key = id;
                param = null;
            }

            BiFunction<PlayerData, String, String> placeholder = PLACEHOLDERS.get(key);
            return (placeholder == null) ? "" : placeholder.apply(SkillAPI.getPlayerData(player), param);
        }


        private String getExp(PlayerClass playerClass) {
            return Integer.toString((int) ((playerClass == null) ? 0.0D : playerClass.getExp()));
        }


        private String getTotalExp(PlayerClass playerClass) {
            return Integer.toString((int) ((playerClass == null) ? 0.0D : playerClass.getTotalExp()));
        }


        private String getRequiredExp(PlayerClass playerClass) {
            return Integer.toString((playerClass == null) ? SkillAPI.getSettings().getRequiredExp(1) : playerClass.getRequiredExp());
        }


        private String getRemainingExp(PlayerClass playerClass) {
            return Integer.toString((int) ((playerClass == null) ? SkillAPI.getSettings().getRequiredExp(1) : (playerClass.getRequiredExp() - playerClass.getExp())));
        }


        private String getLevel(PlayerClass playerClass) {
            return Integer.toString((playerClass == null) ? 0 : playerClass.getLevel());
        }


        private String getSkillPoints(PlayerClass playerClass) {
            return Integer.toString((playerClass == null) ? 0 : playerClass.getPoints());
        }


        private String getPrefix(PlayerClass playerClass) {
            return (playerClass == null) ? "" : playerClass.getData().getPrefix();
        }


        private String getManaName(PlayerClass playerClass) {
            return (playerClass == null) ? "" : playerClass.getData().getManaName();
        }


        private String format(Object value) {
            if (value instanceof Player)
                return ((Player) value).getName();
            if (value instanceof LivingEntity) {
                if (((LivingEntity) value).isCustomNameVisible()) {
                    return ((LivingEntity) value).getCustomName();
                }
                return MobManager.getName((LivingEntity) value);
            }

            if (value instanceof Collection) {
                return ((Collection<?>) value).stream()
                        .map(this::format)
                        .reduce((a, b) -> a + ", " + b)
                        .toString();
            }
            return value.toString();
        }

        private Map<String, BiFunction<PlayerData, String, String>> getMap() {
            ImmutableMap.Builder<String, BiFunction<PlayerData, String, String>> builder = ImmutableMap.builder();
            builder.put("attrib_points", (p, u) -> Integer.toString(p.getAttributePoints()))
                    .put("attrib_spent:", (p, attribute) -> Integer.toString(p.getInvestedAttribute(attribute)))
                    .put("attrib_total:", (p, attribute) -> Integer.toString(p.getAttribute(attribute)))
                    .put("combo", (p, u) -> p.getComboData().getCurrentComboString())
                    .put("exp", (p, u) -> getExp(p.getMainClass()))
                    .put("exp:", (p, group) -> getExp(p.getClass(group)))
                    .put("exp_total", (p, u) -> getTotalExp(p.getMainClass()))
                    .put("exp_total:", (p, group) -> getTotalExp(p.getClass(group)))
                    .put("exp_left", (p, u) -> getRemainingExp(p.getMainClass()))
                    .put("exp_left:", (p, group) -> getRemainingExp(p.getClass(group)))
                    .put("exp_req", (p, u) -> getRequiredExp(p.getMainClass()))
                    .put("exp_req:", (p, group) -> getRequiredExp(p.getClass(group)))
                    .put("level", (p, u) -> getLevel(p.getMainClass()))
                    .put("level:", (p, group) -> getLevel(p.getClass(group)))
                    .put("mana", (p, u) -> Integer.toString((int) p.getMana()))
                    .put("mana_max", (p, u) -> Integer.toString((int) p.getMaxMana()))
                    .put("mana_name", (p, u) -> getManaName(p.getMainClass()))
                    .put("mana_name:", (p, group) -> getManaName(p.getClass(group)))
                    .put("prefix", (p, u) -> getPrefix(p.getMainClass()))
                    .put("prefix:", (p, className) -> getPrefix(p.getClass(className)))
                    .put("skill_level:", (p, skill) -> Integer.toString(p.getSkill(skill).getLevel()))
                    .put("skill_points", (p, u) -> getSkillPoints(p.getMainClass()))
                    .put("skill_points:", (p, group) -> getSkillPoints(p.getClass(group)))
                    .put("value:", (p, key) -> format(DynamicSkill.getCastData(p.getPlayer()))).build();
            return builder.build();
        }

    }

}
