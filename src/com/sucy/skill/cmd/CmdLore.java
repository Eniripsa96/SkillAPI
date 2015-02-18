package com.sucy.skill.cmd;

import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.config.Filter;
import com.rit.sucy.text.TextFormatter;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.language.RPGFilter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * A command that gives a player class experience
 */
public class CmdLore implements IFunction
{
    private static final String NOT_PLAYER    = "not-player";
    private static final String NO_ITEM       = "no-item";
    private static final String LORE_ADDED    = "lore-added";
    private static final String RECEIVED_MANA = "received-mana";

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
        // Must be a player with an argument
        if (args.length >= 1 && sender instanceof Player)
        {
            Player player = (Player)sender;
            ItemStack held = player.getInventory().getItemInHand();

            // No held item
            if (held == null) {
                cmd.sendMessage(sender, NO_ITEM, ChatColor.RED + "You are not holding an item");
                return;
            }

            ItemMeta meta = held.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<String>();
            lore.add(TextFormatter.colorString(StringUtils.join(args, " ")));
            meta.setLore(lore);
            held.setItemMeta(meta);

            // Messages
            cmd.sendMessage(sender, LORE_ADDED, ChatColor.DARK_GREEN + "The lore has been added to your item");
        }

        // Not a player
        else if (!(sender instanceof Player)) {
            cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "Only players can use that command");
        }

        // Not enough arguments
        else
        {
            CommandManager.displayUsage(cmd, sender);
        }
    }
}
