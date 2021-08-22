package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkillBar;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CmdToggleBar implements IFunction {

    private static final String TOGGLE_ON   = "toggle-on";
    private static final String TOGGLE_OFF  = "toggle-off";

    @Override
    public void execute(final ConfigurableCommand cmd, final Plugin plugin, final CommandSender sender, final String[] args) {
        SkillAPI api = (SkillAPI) plugin;
        boolean enabled = api.isSkillBarsEnabled();
        api.setSkillBarsEnabled(!enabled);

        String key = enabled ? TOGGLE_OFF : TOGGLE_ON;
        cmd.sendMessage(sender, key, "Toggled");

        // If true means we are disabling it, thus disabling all online players on enabled worlds
        if (enabled) {
            for (World world : Bukkit.getWorlds()) {
                if (!SkillAPI.getSettings().isWorldEnabled(world))
                    continue;
                for (Player online : world.getPlayers()) {
                    PlayerData data = SkillAPI.getPlayerData(online);
                    if (!data.hasClass())
                        continue;
                    PlayerSkillBar bar = data.getSkillBar();
                    if (bar.isEnabled())
                        bar.toggleEnabled();
                }
            }
        }
    }
}