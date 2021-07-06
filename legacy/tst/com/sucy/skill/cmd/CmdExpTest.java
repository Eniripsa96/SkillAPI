package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.config.Filter;
import com.sucy.skill.TestUtils;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.language.RPGFilter;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SkillAPI © 2018
 * com.sucy.skill.cmd.CmdExpTest
 */
@RunWith(MockitoJUnitRunner.class)
public class CmdExpTest {

    private static final CmdExp SUBJECT = new CmdExp();

    @Mock
    private ConfigurableCommand  cmd;
    @Mock
    private ConsoleCommandSender console;

    @Mock
    private PlayerClass playerClass;

    private Player     player;
    private Plugin     plugin;
    private PlayerData playerData;

    @Before
    public void setUp() throws Exception {
        plugin = TestUtils.getMockPlugin();
        playerData = TestUtils.mockPlayerData();
        player = TestUtils.mockPlayer();

        when(player.isOnline()).thenReturn(true);
        when(player.getPlayer()).thenReturn(player);
        when(TestUtils.getMockSettings().isWorldEnabled(any(World.class))).thenReturn(true);
    }

    @Test
    public void execute_simplePlayerUsage() {
        SUBJECT.execute(cmd, plugin, player, "3");

        verify(playerData).giveExp(3, ExpSource.COMMAND, true);
        verify(cmd).sendMessage(
                player,
                "received-exp",
                "§2You have received §6{exp} experience §2from §6{player}",
                Filter.PLAYER,
                RPGFilter.EXP);
    }

    @Test
    public void execute_playerWithGroupNotFound() {
        SUBJECT.execute(cmd, plugin, player, "3", "race");
        verify(playerData).getClass("race");
    }

    @Test
    public void execute_playerWithGroupFound() {
        when(playerData.getClass("race")).thenReturn(playerClass);
        SUBJECT.execute(cmd, plugin, player, "3", "race");

        verify(playerClass).giveExp(3, ExpSource.COMMAND, true);
    }
}