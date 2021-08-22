package com.sucy.skill.api.util;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.TestUtils;
import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * SkillAPI © 2017
 * com.sucy.skill.api.util.BuffDataTest
 */
@RunWith(MockitoJUnitRunner.class)
public class BuffDataTest {
    private static final double DELTA = 1e-10;

    private static final String KEY = "buff";

    @Mock
    private BukkitScheduler scheduler;

    private BuffData     subject;
    private LivingEntity entity;
    private Server       server;
    private SkillAPI     plugin;

    @Before
    public void setUp() throws Exception {
        plugin = TestUtils.getMockPlugin();
        entity = mock(LivingEntity.class);
        subject = new BuffData(entity);
        server = TestUtils.getMockServer();
        when(server.getScheduler()).thenReturn(scheduler);
    }

    @Test
    public void addBuff() {
        subject.addBuff(BuffType.DAMAGE, new Buff(KEY, 1, false), 5);
        assertEquals(4, subject.apply(BuffType.DAMAGE, 3), DELTA);
    }
}