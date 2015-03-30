package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.gui.MapMenuManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.gui.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * A command that allows a player to view their skill tree
 */
public class CmdSkill implements IFunction
{
    private static final String CANNOT_USE = "cannot-use";
    private static final String NO_SKILLS  = "no-skills";
    private static final String DISABLED   = "world-disabled";
    private static final String MAP_GIVEN  = "map-given";
    private static final String MAP_OWNED  = "map-owned";

    /**
     * Runs the command
     *
     * @param cmd    command that was executed
     * @param plugin plugin reference
     * @param sender sender of the command
     * @param args   argument list
     */
    @Override
    public void execute(ConfigurableCommand cmd, Plugin plugin, CommandSender sender, String[] args)
    {
        // Disabled world
        if (sender instanceof Player && !SkillAPI.getSettings().isWorldEnabled(((Player) sender).getWorld()))
        {
            cmd.sendMessage(sender, DISABLED, "&4You cannot use this command in this world");
        }

        // Only plays have skills to view
        else if (sender instanceof Player)
        {
            Player p = (Player) sender;
            if (SkillAPI.getSettings().isMapTreeEnabled())
            {
                ItemStack map = MapMenuManager.getData(Menu.SKILL_TREE).getMapItem();
                for (ItemStack i : p.getInventory().getContents())
                {
                    if (i != null && i.getType() == Material.MAP && i.getDurability() == map.getDurability())
                    {
                        cmd.sendMessage(sender, MAP_OWNED, ChatColor.RED + "You already have the skill tree map");
                        return;
                    }
                }
                cmd.sendMessage(sender, MAP_GIVEN, ChatColor.DARK_GREEN + "You were given the skill tree map. Hold it in your hand to view skills.");
                p.getInventory().addItem(map);
            }
            else
            {
                PlayerData data = SkillAPI.getPlayerData(p);
                if (!data.showSkills(p))
                {
                    cmd.sendMessage(sender, NO_SKILLS, ChatColor.RED + "You have no skills to view");
                }
            }
        }

        // Console doesn't have profession options
        else
        {
            cmd.sendMessage(sender, CANNOT_USE, ChatColor.RED + "This cannot be used by the console");
        }
    }
}
