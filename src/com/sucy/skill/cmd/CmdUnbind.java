package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Command to bind a skill to an item
 */
public class CmdUnbind implements IFunction
{
    private static final String NOT_PLAYER  = "not-player";
    private static final String NOT_BOUND   = "not-bound";
    private static final String NO_ITEM     = "no-item";
    private static final String SKILL_BOUND = "skill-unbound";
    private static final String DISABLED    = "world-disabled";

    /**
     * Executes the command
     *
     * @param command owning command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(ConfigurableCommand command, Plugin plugin, CommandSender sender, String[] args)
    {
        if (!(sender instanceof Player))
        {
            command.sendMessage(sender, NOT_PLAYER, "&4Only players can use this command");
        }

        // Disabled world
        else if (!SkillAPI.getSettings().isWorldEnabled(((Player) sender).getWorld()))
        {
            command.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        else
        {
            ItemStack item = ((Player) sender).getItemInHand();
            if (item == null || item.getType() == Material.AIR)
            {
                command.sendMessage(sender, NO_ITEM, "&4You are not holding an item");
                return;
            }

            PlayerData player = SkillAPI.getPlayerData((Player) sender);

            if (!player.isBound(item.getType()))
            {
                command.sendMessage(sender, NOT_BOUND, "&4There are no skills bound to the held item");
            }
            else
            {
                PlayerSkill skill = player.getBoundSkill(item.getType());
                player.clearBind(item.getType());
                command.sendMessage(sender, SKILL_BOUND, "&6{skill} &2has been unbound from &6{item}", RPGFilter.SKILL.setReplacement(skill.getData().getName()), RPGFilter.ITEM.setReplacement(TextFormatter.format(item.getType().name())));
            }
        }
    }
}