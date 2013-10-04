package com.sucy.skill.command.basic;

import com.sucy.skill.PermissionNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skill.*;
import com.sucy.skill.api.util.Protection;
import com.sucy.skill.api.util.TargetHelper;
import com.sucy.skill.command.CommandHandler;
import com.sucy.skill.command.ICommand;
import com.sucy.skill.command.SenderType;
import com.sucy.skill.language.CommandNodes;
import com.sucy.skill.language.OtherNodes;
import com.sucy.skill.skills.PlayerSkills;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to bind a skill to an item
 */
public class CmdCast implements ICommand {

    /**
     * Executes the command
     *
     * @param handler handler for the command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        SkillAPI api = (SkillAPI)plugin;
        PlayerSkills player = api.getPlayer(sender.getName());

        // Requires at least one argument
        if (args.length >= 1) {

            // Get the skill name
            String skill = args[0];
            for (int i = 1; i < args.length; i++) skill += " " + args[i];

            // Invalid skill
            if (!api.isSkillRegistered(skill)) {
                String error = api.getMessage(CommandNodes.NOT_A_SKILL, true);
                error = error.replace("{skill}", skill);
                sender.sendMessage(error);
            }

            // Player doesn't have the skill
            else if (!player.hasSkill(skill) || player.getSkillLevel(skill) == 0) {
                String error = api.getMessage(CommandNodes.SKILL_NOT_OWNED, true);
                error = error.replace("{skill}", skill);
                sender.sendMessage(error);
            }

            // Cast the skill
            else {
                ClassSkill classSkill = api.getRegisteredSkill(skill);

                // Cannot cast a passive ability
                if (classSkill instanceof PassiveSkill) {
                    String error = api.getMessage(CommandNodes.CANNOT_BE_CAST, true);
                    error = error.replace("{skill}", classSkill.getName());
                    sender.sendMessage(error);
                    return;
                }

                SkillStatus status = classSkill.checkStatus(player, api.isManaEnabled());
                int level = player.getSkillLevel(classSkill.getName());

                // Skill is on cooldown
                if (status == SkillStatus.ON_COOLDOWN) {
                    List<String> messages = api.getMessages(OtherNodes.ON_COOLDOWN, true);
                    for (String message : messages) {
                        message = message.replace("{cooldown}", classSkill.getCooldown(player) + "")
                                .replace("{skill}", classSkill.getName());

                        plugin.getServer().getPlayer(player.getName()).sendMessage(message);
                    }
                }

                // Skill requires more mana
                else if (status == SkillStatus.MISSING_MANA) {
                    List<String> messages = api.getMessages(OtherNodes.NO_MANA, true);
                    int cost = classSkill.getAttribute(SkillAttribute.MANA, level);
                    for (String message : messages) {
                        message = message.replace("{missing}", (cost - player.getMana()) + "")
                                .replace("{mana}", player.getMana() + "")
                                .replace("{cost}", cost + "")
                                .replace("{skill}", classSkill.getName());

                        plugin.getServer().getPlayer(player.getName()).sendMessage(message);
                    }
                }

                // Cast a target skill
                if (classSkill instanceof TargetSkill) {
                    Player p = plugin.getServer().getPlayer(player.getName());
                    LivingEntity target = TargetHelper.getLivingTarget(p, classSkill.getAttribute(SkillAttribute.RANGE, level));

                    // No target
                    if (target == null) {
                        String error = api.getMessage(CommandNodes.NO_TARGET, true);
                        sender.sendMessage(error);
                    }

                    // Cast on the target
                    else {
                        if (((TargetSkill) classSkill).cast(p, target, level, Protection.isAlly(p, target))) {
                            classSkill.startCooldown(player);
                            player.useMana(classSkill.getAttribute(SkillAttribute.MANA, level));

                            List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.CAST, true);
                            for (String message : messages) {
                                message = message.replace("{skill}", classSkill.getName())
                                        .replace("{mana}", classSkill.getAttribute(SkillAttribute.MANA, player.getSkillLevel(classSkill.getName())) + "")
                                        .replace("{cooldown}", classSkill.getAttribute(SkillAttribute.COOLDOWN, player.getSkillLevel(classSkill.getName())) + "");
                                sender.sendMessage(message);
                            }
                        }
                    }
                }

                // Cast the skill shot
                else {
                    if (((SkillShot) classSkill).cast(plugin.getServer().getPlayer(sender.getName()), level)) {
                        classSkill.startCooldown(player);
                        player.useMana(classSkill.getAttribute(SkillAttribute.MANA, level));

                        List<String> messages = api.getMessages(CommandNodes.COMPLETE + CommandNodes.CAST, true);
                        for (String message : messages) {
                            message = message.replace("{skill}", classSkill.getName())
                                             .replace("{mana}", classSkill.getAttribute(SkillAttribute.MANA, player.getSkillLevel(classSkill.getName())) + "")
                                             .replace("{cooldown}", classSkill.getAttribute(SkillAttribute.COOLDOWN, player.getSkillLevel(classSkill.getName())) + "");
                            sender.sendMessage(message);
                        }
                    }
                }
            }
        }

        // Invalid arguments
        else handler.displayUsage(sender);
    }

    /**
     * @return permission required for this command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.BASIC;
    }

    /**
     * @return arguments used by this command
     */
    @Override
    public String getArgsString(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.ARGUMENTS + CommandNodes.CAST, true);
    }

    /**
     * @return the description of this command
     */
    @Override
    public String getDescription(Plugin plugin) {
        return ((SkillAPI)plugin).getMessage(CommandNodes.DESCRIPTION + CommandNodes.CAST, true);
    }

    /**
     * @return required sender type for this command
     */
    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER_ONLY;
    }
}
